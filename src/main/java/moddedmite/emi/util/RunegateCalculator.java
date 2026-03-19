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
	public static final int MITHRIL_FALLBACK_RADIUS = 5000;
	public static final int ADAMANTIUM_FALLBACK_RADIUS = 40000;
	public static final String[] MAGIC_NAMES =
			new String[]{"Nul", "Quas", "Por", "An", "Nox", "Flam", "Vas", "Des", "Ort", "Tym", "Corp", "Lor", "Mani", "Jux", "Ylem", "Sanct"};

	private RunegateCalculator() {}

	public enum RunestoneMaterial {
		MITHRIL(Material.mithril, "Mithril"),
		ADAMANTIUM(Material.adamantium, "Adamantium");

		public final Material material;
		public final String displayName;

		RunestoneMaterial(Material material, String displayName) {
			this.material = material;
			this.displayName = displayName;
		}
	}

	@FunctionalInterface
	public interface OceanPredicate {
		boolean isOcean(int x, int z);
	}

	public static final class Destination {
		public final int x;
		public final int z;
		public final int attempts;

		public Destination(int x, int z, int attempts) {
			this.x = x;
			this.z = z;
			this.attempts = attempts;
		}
	}

	public static final class AttemptPoint {
		public final int attempt;
		public final int x;
		public final int z;
		public final boolean ocean;

		public AttemptPoint(int attempt, int x, int z, boolean ocean) {
			this.attempt = attempt;
			this.x = x;
			this.z = z;
			this.ocean = ocean;
		}
	}

	public static final class DestinationTrace {
		public final Destination destination;
		public final List<AttemptPoint> attempts;

		public DestinationTrace(Destination destination, List<AttemptPoint> attempts) {
			this.destination = destination;
			this.attempts = attempts;
		}
	}

	public static final class Combination {
		public final int seed;
		public final int lowerLeft;
		public final int lowerRight;
		public final int upperLeft;
		public final int upperRight;

		public Combination(int seed, int lowerLeft, int lowerRight, int upperLeft, int upperRight) {
			this.seed = seed & MAX_SEED;
			this.lowerLeft = lowerLeft & 15;
			this.lowerRight = lowerRight & 15;
			this.upperLeft = upperLeft & 15;
			this.upperRight = upperRight & 15;
		}
	}

	public static final class ReverseMatch {
		public final Combination combination;
		public final int destinationX;
		public final int destinationZ;
		public final int selectedAttempt;
		public final long distanceSq;

		public ReverseMatch(Combination combination, int destinationX, int destinationZ, int selectedAttempt, long distanceSq) {
			this.combination = combination;
			this.destinationX = destinationX;
			this.destinationZ = destinationZ;
			this.selectedAttempt = selectedAttempt;
			this.distanceSq = distanceSq;
		}
	}

	public static final class ReverseAnalysis {
		public final List<ReverseMatch> inRadius;
		public final int inRadiusTotal;
		public final List<ReverseMatch> nearest;

		public ReverseAnalysis(List<ReverseMatch> inRadius, int inRadiusTotal, List<ReverseMatch> nearest) {
			this.inRadius = inRadius;
			this.inRadiusTotal = inRadiusTotal;
			this.nearest = nearest;
		}
	}

	public static int composeSeed(int lowerLeft, int lowerRight, int upperLeft, int upperRight) {
		return (lowerLeft & 15) | ((lowerRight & 15) << 4) | ((upperLeft & 15) << 8) | ((upperRight & 15) << 12);
	}

	public static Combination decodeSeed(int seed) {
		int masked = seed & MAX_SEED;
		return new Combination(masked, masked & 15, (masked >> 4) & 15, (masked >> 8) & 15, (masked >> 12) & 15);
	}

	public static Destination calculateDestination(int seed, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate) {
		return calculateDestination(seed, material, domainRadius, oceanPredicate, 0, 0);
	}

	public static Destination calculateDestination(int seed, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int centerX, int centerZ) {
		return traceDestination(seed, material, domainRadius, oceanPredicate, centerX, centerZ).destination;
	}

	public static DestinationTrace traceDestination(int seed, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate) {
		return traceDestination(seed, material, domainRadius, oceanPredicate, 0, 0);
	}

	public static DestinationTrace traceDestination(int seed, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int centerX, int centerZ) {
		int x = 0;
		int z = 0;
		int attempts = 0;
		List<AttemptPoint> generated = new ArrayList<>(4);
		int maskedSeed = seed & MAX_SEED;
		if (maskedSeed == 0) {
			return new DestinationTrace(new Destination(centerX, centerZ, 0), generated);
		}

		int radius = Math.max(domainRadius, 1);
		int adamantiumInnerRadius = radius / 2;
		Random random = new Random(maskedSeed);
		for (int i = 0; i < 4; i++) {
			attempts = i + 1;
			x = centerX + random.nextInt(radius * 2) - radius;
			z = centerZ + random.nextInt(radius * 2) - radius;

			// Match MITE source exactly: compare against (radius / 2) with integer division.
			while (material == RunestoneMaterial.ADAMANTIUM && distanceFromCenter(x, z, centerX, centerZ) < (double) adamantiumInnerRadius) {
				x = centerX + random.nextInt(radius * 2) - radius;
				z = centerZ + random.nextInt(radius * 2) - radius;
			}

			boolean ocean = oceanPredicate.isOcean(x, z);
			generated.add(new AttemptPoint(attempts, x, z, ocean));
			if (!ocean) {
				break;
			}
		}
		return new DestinationTrace(new Destination(x, z, attempts), generated);
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
			if (distanceSquared(destination.x, destination.z, x, z) <= maxDistanceSq) {
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
		return reverseAnalyze(x, z, 0, material, domainRadius, oceanPredicate, maxResults, centerX, centerZ).nearest;
	}

	public static ReverseAnalysis reverseAnalyze(int x, int z, int matchRadius, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int maxResults) {
		return reverseAnalyze(x, z, matchRadius, material, domainRadius, oceanPredicate, maxResults, false, 0, 0);
	}

	public static ReverseAnalysis reverseAnalyze(int x, int z, int matchRadius, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int maxResults, int centerX, int centerZ) {
		return reverseAnalyze(x, z, matchRadius, material, domainRadius, oceanPredicate, maxResults, false, centerX, centerZ);
	}

	public static ReverseAnalysis reverseAnalyze(int x, int z, int matchRadius, RunestoneMaterial material, int domainRadius, OceanPredicate oceanPredicate, int maxResults, boolean requireSelectedFirstAttempt, int centerX, int centerZ) {
		int limit = Math.max(maxResults, 1);
		long maxDistanceSq = (long) Math.max(matchRadius, 0) * Math.max(matchRadius, 0);
		List<ReverseMatch> candidates = new ArrayList<>(MAX_SEED + 1);
		for (int seed = 0; seed <= MAX_SEED; seed++) {
			Destination destination = requireSelectedFirstAttempt
					? firstAttemptDestination(seed, material, domainRadius, centerX, centerZ)
					: traceDestination(seed, material, domainRadius, oceanPredicate, centerX, centerZ).destination;
			candidates.add(new ReverseMatch(
					decodeSeed(seed),
					destination.x,
					destination.z,
					destination.attempts,
					distanceSquared(destination.x, destination.z, x, z)));
		}
		if (candidates.isEmpty()) {
			return new ReverseAnalysis(List.of(), 0, List.of());
		}
		candidates.sort(Comparator
				.comparingLong((ReverseMatch match) -> match.distanceSq)
				.thenComparingInt(match -> match.combination.seed));

		List<ReverseMatch> nearest = new ArrayList<>(candidates.subList(0, Math.min(limit, candidates.size())));
		List<ReverseMatch> inRadius = new ArrayList<>();
		int inRadiusTotal = 0;
		for (ReverseMatch match : candidates) {
			if (match.distanceSq <= maxDistanceSq) {
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
		int fallback = material == RunestoneMaterial.ADAMANTIUM ? ADAMANTIUM_FALLBACK_RADIUS : MITHRIL_FALLBACK_RADIUS;
		if (world == null) {
			return fallback;
		}
		try {
			return world.getRunegateDomainRadius(material.material);
		} catch (Throwable ignored) {
			return fallback;
		}
	}

	public static OceanPredicate oceanPredicate(World world) {
		if (world == null) {
			return (x, z) -> false;
		}
		return (x, z) -> world.getBiomeGenForCoords(x, z) == BiomeGenBase.ocean;
	}

	public static String magicName(int metadata) {
		return MAGIC_NAMES[metadata & 15];
	}

	public static String describeCorner(int metadata) {
		int sanitized = metadata & 15;
		return magicName(sanitized) + "(" + sanitized + ")";
	}

	public static String seedHex(int seed) {
		return String.format("0x%04X", seed & MAX_SEED);
	}

	public static String arrangementCode(Combination combination) {
		return String.format("%X%X%X%X",
				combination.lowerLeft & 15,
				combination.lowerRight & 15,
				combination.upperLeft & 15,
				combination.upperRight & 15);
	}

	private static Destination firstAttemptDestination(int seed, RunestoneMaterial material, int domainRadius, int centerX, int centerZ) {
		int maskedSeed = seed & MAX_SEED;
		if (maskedSeed == 0) {
			return new Destination(centerX, centerZ, 0);
		}
		int radius = Math.max(domainRadius, 1);
		int adamantiumInnerRadius = radius / 2;
		Random random = new Random(maskedSeed);
		int x = centerX + random.nextInt(radius * 2) - radius;
		int z = centerZ + random.nextInt(radius * 2) - radius;
		while (material == RunestoneMaterial.ADAMANTIUM && distanceFromCenter(x, z, centerX, centerZ) < (double) adamantiumInnerRadius) {
			x = centerX + random.nextInt(radius * 2) - radius;
			z = centerZ + random.nextInt(radius * 2) - radius;
		}
		return new Destination(x, z, 1);
	}

	private static double distanceFromCenter(int x, int z, int centerX, int centerZ) {
		int dx = x - centerX;
		int dz = z - centerZ;
		return Math.sqrt((double) dx * dx + (double) dz * dz);
	}

	private static long distanceSquared(int x1, int z1, int x2, int z2) {
		long dx = (long) x1 - x2;
		long dz = (long) z1 - z2;
		return dx * dx + dz * dz;
	}
}
