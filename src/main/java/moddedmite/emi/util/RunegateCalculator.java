package moddedmite.emi.util;

import net.minecraft.BiomeGenBase;
import net.minecraft.Material;
import net.minecraft.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public final class RunegateCalculator {

	public static final int MAX_SEED = 0xFFFF;
	public static final int MITHRIL_DEFAULT_DOMAIN_RADIUS = 5000;
	public static final int ADAMANTIUM_DEFAULT_DOMAIN_RADIUS = 40000;
	public static final String[] MAGIC_NAMES = new String[] {"Nul", "Quas", "Por", "An", "Nox", "Flam", "Vas", "Des", "Ort", "Tym", "Corp", "Lor", "Mani", "Jux", "Ylem", "Sanct"};
	private static final int RUNE_MASK = 15;
	private static final int MAX_ATTEMPTS = 4;

	private RunegateCalculator() {}

	public enum RunestoneMaterial {
		MITHRIL(Material.mithril, "Mithril", MITHRIL_DEFAULT_DOMAIN_RADIUS),
		ADAMANTIUM(Material.adamantium, "Adamantium", ADAMANTIUM_DEFAULT_DOMAIN_RADIUS);

		private final Material material;
		private final String displayName;
		private final int defaultDomainRadius;

		RunestoneMaterial(Material material, String displayName, int defaultDomainRadius) {
			this.material = material;
			this.displayName = displayName;
			this.defaultDomainRadius = defaultDomainRadius;
		}

		public Material material() {
			return material;
		}

		public String displayName() {
			return displayName;
		}

		public int defaultDomainRadius() {
			return defaultDomainRadius;
		}
	}

	@FunctionalInterface
	public interface OceanPredicate {
		boolean isOcean(int x, int z);
	}

	public record Point(int x, int z) {
	}

	public record Destination(int x, int z, int attempts) {
		public Point point() {
			return new Point(x, z);
		}
	}

	public record AttemptPoint(int attempt, int x, int z, boolean ocean) {
		private AttemptPoint(int attempt, Point point, boolean ocean) {
			this(attempt, point.x(), point.z(), ocean);
		}

		public Point point() {
			return new Point(x, z);
		}
	}

	public record DestinationTrace(Destination destination, List<AttemptPoint> attempts) {
		public DestinationTrace {
			attempts = List.copyOf(attempts);
		}
	}

	public record Combination(int seed, int lowerLeft, int lowerRight, int upperLeft, int upperRight) {
		public Combination {
			seed &= MAX_SEED;
			lowerLeft &= RUNE_MASK;
			lowerRight &= RUNE_MASK;
			upperLeft &= RUNE_MASK;
			upperRight &= RUNE_MASK;
		}
	}

	public record ReverseMatch(Combination combination, Destination destination, long distanceSq) {
		public int destinationX() {
			return destination.x();
		}

		public int destinationZ() {
			return destination.z();
		}

		public int selectedAttempt() {
			return destination.attempts();
		}
	}

	public record ReverseAnalysis(List<ReverseMatch> inRadius, int inRadiusTotal, List<ReverseMatch> nearest) {
		public ReverseAnalysis {
			inRadius = List.copyOf(inRadius);
			nearest = List.copyOf(nearest);
		}

		public boolean hasInRadiusMatches() {
			return inRadiusTotal > 0;
		}

		public boolean isTruncated() {
			return inRadiusTotal > inRadius.size();
		}
	}

	public static int composeSeed(int lowerLeft, int lowerRight, int upperLeft, int upperRight) {
		return (lowerLeft & RUNE_MASK)
				| ((lowerRight & RUNE_MASK) << 4)
				| ((upperLeft & RUNE_MASK) << 8)
				| ((upperRight & RUNE_MASK) << 12);
	}

	public static Combination decodeSeed(int seed) {
		int masked = seed & MAX_SEED;
		return new Combination(masked, masked & RUNE_MASK, (masked >> 4) & RUNE_MASK, (masked >> 8) & RUNE_MASK, (masked >> 12) & RUNE_MASK);
	}

	public static Destination calculateDestination(int seed, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate) {
		return calculateDestination(seed, material, domainRadius, oceanPredicate, 0, 0);
	}

	public static Destination calculateDestination(int seed, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int centerX, int centerZ) {
		return traceDestination(seed, material, domainRadius, oceanPredicate, centerX, centerZ).destination();
	}

	public static DestinationTrace traceDestination(int seed, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate) {
		return traceDestination(seed, material, domainRadius, oceanPredicate, 0, 0);
	}

	public static DestinationTrace traceDestination(int seed, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int centerX, int centerZ) {
		List<AttemptPoint> generated = new ArrayList<>(4);
		int maskedSeed = seed & MAX_SEED;
		if (maskedSeed == 0) {
			return new DestinationTrace(new Destination(centerX, centerZ, 0), generated);
		}

		Point center = new Point(centerX, centerZ);
		Destination destination = new Destination(centerX, centerZ, 0);
		OceanPredicate predicate = safeOceanPredicate(oceanPredicate);
		int radius = sanitizeRadius(domainRadius);
		Random random = new Random(maskedSeed);
		for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
			Point point = nextCandidate(random, material, radius, center);
			boolean ocean = predicate.isOcean(point.x(), point.z());
			generated.add(new AttemptPoint(attempt, point, ocean));
			destination = new Destination(point.x(), point.z(), attempt);
			if (!ocean) {
				break;
			}
		}
		return new DestinationTrace(destination, generated);
	}

	public static List<Combination> reverseDestination(int x, int z, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int maxResults) {
		return reverseDestination(x, z, 0, material, domainRadius, oceanPredicate, maxResults, 0, 0);
	}

	public static List<Combination> reverseDestination(int x, int z, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int maxResults, int centerX, int centerZ) {
		return reverseDestination(x, z, 0, material, domainRadius, oceanPredicate, maxResults, centerX, centerZ);
	}

	public static List<Combination> reverseDestination(int x, int z, int matchRadius, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int maxResults) {
		return reverseDestination(x, z, matchRadius, material, domainRadius, oceanPredicate, maxResults, 0, 0);
	}

	public static List<Combination> reverseDestination(int x, int z, int matchRadius, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int maxResults, int centerX, int centerZ) {
		List<Combination> matches = new ArrayList<>();
		int radius = Math.max(matchRadius, 0);
		long maxDistanceSq = (long) radius * (long) radius;
		for (int seed = 0; seed <= MAX_SEED; seed++) {
			Destination destination = calculateDestination(seed, material, domainRadius, oceanPredicate, centerX, centerZ);
			if (distanceSquared(destination.x(), destination.z(), x, z) <= maxDistanceSq) {
				matches.add(decodeSeed(seed));
				if (maxResults > 0 && matches.size() >= maxResults) {
					break;
				}
			}
		}
		return matches;
	}

	public static List<ReverseMatch> reverseNearest(int x, int z, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int maxResults) {
		return reverseNearest(x, z, material, domainRadius, oceanPredicate, maxResults, 0, 0);
	}

	public static List<ReverseMatch> reverseNearest(int x, int z, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int maxResults, int centerX, int centerZ) {
		return reverseAnalyze(x, z, 0, material, domainRadius, oceanPredicate, maxResults, centerX, centerZ).nearest();
	}

	public static ReverseAnalysis reverseAnalyze(int x, int z, int matchRadius, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int maxResults) {
		return reverseAnalyze(x, z, matchRadius, material, domainRadius, oceanPredicate, maxResults, false, 0, 0);
	}

	public static ReverseAnalysis reverseAnalyze(int x, int z, int matchRadius, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int maxResults, int centerX, int centerZ) {
		return reverseAnalyze(x, z, matchRadius, material, domainRadius, oceanPredicate, maxResults, false, centerX, centerZ);
	}

	public static ReverseAnalysis reverseAnalyze(int x, int z, int matchRadius, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int maxResults, boolean requireSelectedFirstAttempt, int centerX, int centerZ) {
		int limit = Math.max(maxResults, 1);
		long maxDistanceSq = square(Math.max(matchRadius, 0));
		List<ReverseMatch> candidates = new ArrayList<>(MAX_SEED + 1);
		for (int seed = 0; seed <= MAX_SEED; seed++) {
			Destination destination = requireSelectedFirstAttempt
					? firstAttemptDestination(seed, material, domainRadius, centerX, centerZ)
					: calculateDestination(seed, material, domainRadius, oceanPredicate, centerX, centerZ);
			candidates.add(new ReverseMatch(decodeSeed(seed), destination, distanceSquared(destination.x(), destination.z(), x, z)));
		}
		if (candidates.isEmpty()) {
			return new ReverseAnalysis(List.of(), 0, List.of());
		}
		candidates.sort(Comparator
				.comparingLong(ReverseMatch::distanceSq)
				.thenComparingInt(match -> match.combination().seed()));

		List<ReverseMatch> nearest = new ArrayList<>(candidates.subList(0, Math.min(limit, candidates.size())));
		List<ReverseMatch> inRadius = new ArrayList<>();
		int inRadiusTotal = 0;
		for (ReverseMatch match : candidates) {
			if (match.distanceSq() <= maxDistanceSq) {
				inRadiusTotal++;
				if (inRadius.size() < limit) {
					inRadius.add(match);
				}
			} else if (inRadiusTotal > 0) {
				break;
			}
		}
		return new ReverseAnalysis(inRadius, inRadiusTotal, nearest);
	}

	public static int resolveDomainRadius(World world, RunestoneMaterial material) {
		if (world == null) {
			return material.defaultDomainRadius();
		}
		try {
			return world.getRunegateDomainRadius(material.material());
		} catch (Throwable ignored) {
			return material.defaultDomainRadius();
		}
	}

	public static OceanPredicate oceanPredicate(World world) {
		if (world == null) {
			return (x, z) -> false;
		}
		return (x, z) -> world.getBiomeGenForCoords(x, z) == BiomeGenBase.ocean;
	}

	public static String magicName(int metadata) {
		return MAGIC_NAMES[metadata & RUNE_MASK];
	}

	public static String describeCorner(int metadata) {
		int sanitized = metadata & RUNE_MASK;
		return magicName(sanitized) + "(" + sanitized + ")";
	}

	public static String seedHex(int seed) {
		return String.format("0x%04X", seed & MAX_SEED);
	}

	public static String arrangementCode(Combination combination) {
		return String.format("%X%X%X%X",
				combination.lowerLeft(),
				combination.lowerRight(),
				combination.upperLeft(),
				combination.upperRight());
	}

	private static Destination firstAttemptDestination(int seed, RunestoneMaterial material, int domainRadius, int centerX, int centerZ) {
		int maskedSeed = seed & MAX_SEED;
		if (maskedSeed == 0) {
			return new Destination(centerX, centerZ, 0);
		}
		Random random = new Random(maskedSeed);
		Point point = nextCandidate(random, material, sanitizeRadius(domainRadius), new Point(centerX, centerZ));
		return new Destination(point.x(), point.z(), 1);
	}

	private static Point nextCandidate(Random random, RunestoneMaterial material, int radius, Point center) {
		Point point;
		int innerRadius = material == RunestoneMaterial.ADAMANTIUM ? radius / 2 : 0;
		do {
			point = randomPoint(random, radius, center);
			// MITE compares against (radius / 2) after integer division.
		} while (innerRadius > 0 && distanceSquared(point.x(), point.z(), center.x(), center.z()) < square(innerRadius));
		return point;
	}

	private static Point randomPoint(Random random, int radius, Point center) {
		return new Point(
				center.x() + random.nextInt(radius * 2) - radius,
				center.z() + random.nextInt(radius * 2) - radius);
	}

	private static OceanPredicate safeOceanPredicate(OceanPredicate oceanPredicate) {
		return oceanPredicate == null ? (x, z) -> false : oceanPredicate;
	}

	private static int sanitizeRadius(int radius) {
		return Math.max(radius, 1);
	}

	private static long square(int value) {
		return (long) value * (long) value;
	}

	private static long distanceSquared(int x1, int z1, int x2, int z2) {
		long dx = (long) x1 - x2;
		long dz = (long) z1 - z2;
		return dx * dx + dz * dz;
	}
}
