package moddedmite.emi.screen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.emi.emi.EmiPort;
import moddedmite.emi.util.RunegateCalculator;
import net.minecraft.GuiScreen;
import shims.java.com.unascribed.retroemi.REMIScreen;
import shims.java.net.minecraft.client.gui.DrawContext;
import shims.java.net.minecraft.client.gui.widget.ButtonWidget;
import shims.java.net.minecraft.client.gui.widget.TextFieldWidget;
import shims.java.org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class RunegateCalculatorScreen extends REMIScreen {

	private static final SavedState LAST_STATE = new SavedState();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File STATE_FILE = new File("emi-runegate.json");
	private static boolean stateLoadedFromDisk;

	private final GuiScreen parent;
	private final List<TextFieldWidget> fields = new ArrayList<>();
	private final List<String> resultLines = new ArrayList<>();

	private TextFieldWidget lowerLeftField;
	private TextFieldWidget lowerRightField;
	private TextFieldWidget upperLeftField;
	private TextFieldWidget upperRightField;
	private TextFieldWidget radiusField;
	private TextFieldWidget targetXField;
	private TextFieldWidget targetZField;
	private TextFieldWidget maxResultsField;
	private TextFieldWidget matchRadiusField;
	private ButtonWidget materialButton;
	private ButtonWidget modeButton;
	private ButtonWidget coordinateSpaceButton;
	private ButtonWidget reverseFilterButton;

	private OperationMode operationMode = OperationMode.FORWARD;
	private CoordinateSpace coordinateSpace = CoordinateSpace.CURRENT;
	private RunegateCalculator.RunestoneMaterial material = RunegateCalculator.RunestoneMaterial.MITHRIL;
	private boolean strictFirstAttemptOnly = true;

	private static final class SavedState {
		private boolean initialized;
		private OperationMode operationMode = OperationMode.FORWARD;
		private CoordinateSpace coordinateSpace = CoordinateSpace.CURRENT;
		private RunegateCalculator.RunestoneMaterial material = RunegateCalculator.RunestoneMaterial.MITHRIL;
		private boolean strictFirstAttemptOnly = true;
		private String lowerLeft = "";
		private String lowerRight = "";
		private String upperLeft = "";
		private String upperRight = "";
		private String radius = "";
		private String targetX = "";
		private String targetZ = "";
		private String maxResults = "";
		private String matchRadius = "";
		private List<String> resultLines = new ArrayList<>();
	}

	private enum OperationMode {
		FORWARD,
		REVERSE
	}

	private enum CoordinateSpace {
		CURRENT("screen.emi.runegate.coord.current"),
		OVERWORLD("screen.emi.runegate.coord.overworld"),
		NETHER("screen.emi.runegate.coord.nether");

		private final String translationKey;

		CoordinateSpace(String translationKey) {
			this.translationKey = translationKey;
		}

		private CoordinateSpace next() {
			int nextOrdinal = (this.ordinal() + 1) % values().length;
			return values()[nextOrdinal];
		}
	}

	public RunegateCalculatorScreen(GuiScreen parent) {
		super(EmiPort.translatable("screen.emi.runegate_calculator"));
		this.parent = parent;
		ensureStateLoaded();
	}

	@Override
	protected void init() {
		super.init();
		fields.clear();
		restoreModesFromState();

		int panelWidth = 380;
		int left = width / 2 - panelWidth / 2;
		int row1 = 52;
		int row2 = 90;
		int row3 = 120;
		int row4 = 150;

		String playerX = getCurrentPlayerBlockXText();
		String playerZ = getCurrentPlayerBlockZText();

		lowerLeftField = addNumberField(left + 10, row1, 52, stateOrDefault(LAST_STATE.lowerLeft, ""), 2);
		lowerRightField = addNumberField(left + 68, row1, 52, stateOrDefault(LAST_STATE.lowerRight, ""), 2);
		upperLeftField = addNumberField(left + 126, row1, 52, stateOrDefault(LAST_STATE.upperLeft, ""), 2);
		upperRightField = addNumberField(left + 184, row1, 52, stateOrDefault(LAST_STATE.upperRight, ""), 2);

		materialButton = this.addDrawableChild(EmiPort.newButton(left + 242, row1, 128, 20, EmiPort.literal(""), button -> {
			material = material == RunegateCalculator.RunestoneMaterial.MITHRIL ? RunegateCalculator.RunestoneMaterial.ADAMANTIUM
					: RunegateCalculator.RunestoneMaterial.MITHRIL;
			updateMaterialButtonText();
			radiusField.setText(String.valueOf(resolveDomainRadius()));
			runCurrentMode();
		}));
		updateMaterialButtonText();

		radiusField = addNumberField(left + 60, row2, 72, stateOrDefault(LAST_STATE.radius, String.valueOf(resolveDomainRadius())), 6);
		targetXField = addNumberField(left + 58, row3, 64, stateOrDefault(LAST_STATE.targetX, playerX), 11);
		targetZField = addNumberField(left + 132, row3, 64, stateOrDefault(LAST_STATE.targetZ, playerZ), 11);
		maxResultsField = addNumberField(left + 206, row3, 48, stateOrDefault(LAST_STATE.maxResults, "24"), 3);
		matchRadiusField = addNumberField(left + 58, row4, 64, stateOrDefault(LAST_STATE.matchRadius, "128"), 4);

		this.addDrawableChild(EmiPort.newButton(left + 10, row2, 46, 20, EmiPort.literal(t("screen.emi.runegate.reset_radius")), button -> {
			radiusField.setText(String.valueOf(resolveDomainRadius()));
			saveState();
		}));
		modeButton = this.addDrawableChild(EmiPort.newButton(left + 140, row2, 82, 20, EmiPort.literal(""), button -> {
			operationMode = operationMode == OperationMode.FORWARD ? OperationMode.REVERSE : OperationMode.FORWARD;
			updateModeButtonText();
			runCurrentMode();
		}));
		updateModeButtonText();

		this.addDrawableChild(EmiPort.newButton(left + 226, row2, 54, 20, EmiPort.literal(t("screen.emi.runegate.run")), button -> runCurrentMode()));
		coordinateSpaceButton = this.addDrawableChild(EmiPort.newButton(left + 286, row2, 84, 20, EmiPort.literal(""), button -> cycleCoordinateSpace()));
		updateCoordinateSpaceButtonText();

		reverseFilterButton = this.addDrawableChild(EmiPort.newButton(left + 126, row4, 120, 20, EmiPort.literal(""), button -> {
			strictFirstAttemptOnly = !strictFirstAttemptOnly;
			updateReverseFilterButtonText();
			runCurrentMode();
		}));
		updateReverseFilterButtonText();

		this.addDrawableChild(EmiPort.newButton(left + 250, row4, 120, 20, EmiPort.literal(t("screen.emi.runegate.reset_center")), button -> {
			resetTargetToCurrentPlayer();
			runCurrentMode();
		}));
		this.addDrawableChild(EmiPort.newButton(width / 2 + 130, height - 26, 60, 20, EmiPort.translatable("gui.done"), button -> close()));

		resultLines.clear();
		if (LAST_STATE.initialized) {
			resultLines.addAll(LAST_STATE.resultLines);
		}
		saveState();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		for (TextFieldWidget field : fields) {
			field.tick();
		}
	}

	@Override
	protected void close() {
		saveState();
		client.displayGuiScreen(parent);
	}

	@Override
	protected void render(DrawContext raw, int mouseX, int mouseY, float delta) {
		renderBackground(raw);
		super.render(raw, mouseX, mouseY, delta);

		int panelWidth = 380;
		int left = width / 2 - panelWidth / 2;
		drawCenteredString(fontRenderer, t("screen.emi.runegate_calculator"), width / 2, 14, 0xFFFFFF);
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.corners"), left + 10, 28, 0xE0E0E0);
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.corner.ll"), left + 24, 42, 0xA0A0A0);
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.corner.lr"), left + 82, 42, 0xA0A0A0);
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.corner.ul"), left + 140, 42, 0xA0A0A0);
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.corner.ur"), left + 198, 42, 0xA0A0A0);
		fontRenderer.drawStringWithShadow(previewCorner(lowerLeftField), left + 10, 74, 0x808080);
		fontRenderer.drawStringWithShadow(previewCorner(lowerRightField), left + 68, 74, 0x808080);
		fontRenderer.drawStringWithShadow(previewCorner(upperLeftField), left + 126, 74, 0x808080);
		fontRenderer.drawStringWithShadow(previewCorner(upperRightField), left + 184, 74, 0x808080);

		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.radius"), left + 10, 84, 0xE0E0E0);
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.mode"), left + 140, 84, 0xE0E0E0);
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.coord"), left + 286, 84, 0xE0E0E0);
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.target"), left + 10, 126, 0xE0E0E0);
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.field.x"), left + 58, 112, 0xA0A0A0);
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.field.z"), left + 132, 112, 0xA0A0A0);
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.max_results"), left + 206, 112, 0xA0A0A0);
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.match_radius"), left + 10, 154, 0xE0E0E0);
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.field.match_radius"), left + 58, 140, 0xA0A0A0);
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.filter"), left + 126, 140, 0xA0A0A0);
		int hintLineY = 174;
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.hint",
				t("screen.emi.runegate.corner.ll"),
				t("screen.emi.runegate.corner.lr"),
				t("screen.emi.runegate.corner.ul"),
				t("screen.emi.runegate.corner.ur")), left + 10, hintLineY, 0x808080);
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.meta_hint"), left + 10, hintLineY + 10, 0x808080);
		int legendX = left + panelWidth + 12;
		int legendY = 32;
		boolean legendOutside = legendX + 58 * 2 <= width - 8;
		if (!legendOutside) {
			legendX = left + panelWidth - 122;
			legendY = hintLineY + 20;
		}
		int legendBottom = renderMetaLegend(legendX, legendY);

		int outputY = legendOutside ? hintLineY + 22 : Math.max(hintLineY + 22, legendBottom + 6);
		for (String line : resultLines) {
			if (outputY > height - 34) {
				break;
			}
			fontRenderer.drawStringWithShadow(line, left + 10, outputY, 0xFFFFFF);
			outputY += 10;
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
			runCurrentMode();
			return true;
		}
		if (keyCode == client.gameSettings.keyBindInventory.keyCode) {
			close();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	private TextFieldWidget addNumberField(int x, int y, int width, String value, int maxLength) {
		TextFieldWidget field = new TextFieldWidget(fontRenderer, x, y, width, 20, EmiPort.literal(""));
		field.setDrawsBackground(true);
		field.setMaxLength(maxLength);
		field.setText(value == null ? "" : value);
		fields.add(field);
		this.addDrawable(field);
		this.addSelectableChild(field);
		return field;
	}

	private void updateMaterialButtonText() {
		materialButton.setMessage(EmiPort.literal(t(material == RunegateCalculator.RunestoneMaterial.MITHRIL
				? "screen.emi.runegate.material.mithril"
				: "screen.emi.runegate.material.adamantium")));
	}

	private void updateModeButtonText() {
		modeButton.setMessage(EmiPort.literal(t(operationMode == OperationMode.FORWARD
				? "screen.emi.runegate.calculate"
				: "screen.emi.runegate.reverse")));
	}

	private void updateCoordinateSpaceButtonText() {
		coordinateSpaceButton.setMessage(EmiPort.literal(t(coordinateSpace.translationKey)));
	}

	private void updateReverseFilterButtonText() {
		reverseFilterButton.setMessage(EmiPort.literal(t(strictFirstAttemptOnly
				? "screen.emi.runegate.filter.strict"
				: "screen.emi.runegate.filter.all")));
	}

	private void runCurrentMode() {
		if (operationMode == OperationMode.FORWARD) {
			calculateForward();
		} else {
			reverseDestination();
		}
		saveState();
	}

	private int resolveDomainRadius() {
		return RunegateCalculator.resolveDomainRadius(client.theWorld, material);
	}

	private void calculateForward() {
		try {
			int lowerLeft = parseBounded(lowerLeftField, t("screen.emi.runegate.field.ll"), 0, 15);
			int lowerRight = parseBounded(lowerRightField, t("screen.emi.runegate.field.lr"), 0, 15);
			int upperLeft = parseBounded(upperLeftField, t("screen.emi.runegate.field.ul"), 0, 15);
			int upperRight = parseBounded(upperRightField, t("screen.emi.runegate.field.ur"), 0, 15);
			int radius = parseBounded(radiusField, t("screen.emi.runegate.field.radius"), 1, 200000);

			int seed = RunegateCalculator.composeSeed(lowerLeft, lowerRight, upperLeft, upperRight);
			RunegateCalculator.DestinationTrace trace = RunegateCalculator.traceDestination(
					seed, material, radius, RunegateCalculator.oceanPredicate(client.theWorld));
			RunegateCalculator.Destination destination = trace.destination;
			int shownX = fromNative(destination.x, coordinateSpace);
			int shownZ = fromNative(destination.z, coordinateSpace);

			resultLines.clear();
			resultLines.add(t("screen.emi.runegate.result.mode", t(material == RunegateCalculator.RunestoneMaterial.MITHRIL
					? "screen.emi.runegate.material.mithril"
					: "screen.emi.runegate.material.adamantium"), radius));
			resultLines.add(t("screen.emi.runegate.result.coord_space", t(coordinateSpace.translationKey)));
			resultLines.add(t("screen.emi.runegate.result.seed", RunegateCalculator.seedHex(seed), seed));
			resultLines.add(t("screen.emi.runegate.result.destination", shownX, shownZ));
			if (coordinateSpace != CoordinateSpace.CURRENT) {
				resultLines.add(t("screen.emi.runegate.result.destination_native", destination.x, destination.z));
			}
			if (seed == 0) {
				resultLines.add(t("screen.emi.runegate.result.seed_zero"));
			} else {
				resultLines.add(t("screen.emi.runegate.result.attempts", destination.attempts, 4));
				resultLines.add(t("screen.emi.runegate.result.attempt_candidates"));
				for (RunegateCalculator.AttemptPoint attempt : trace.attempts) {
					int shownAttemptX = fromNative(attempt.x, coordinateSpace);
					int shownAttemptZ = fromNative(attempt.z, coordinateSpace);
					String biomeState = t(attempt.ocean
							? "screen.emi.runegate.result.attempt_ocean"
							: "screen.emi.runegate.result.attempt_land");
					String selectedMark = attempt.attempt == destination.attempts
							? t("screen.emi.runegate.result.attempt_selected")
							: "";
					resultLines.add(t("screen.emi.runegate.result.attempt_line",
							attempt.attempt, shownAttemptX, shownAttemptZ, biomeState, selectedMark));
				}
			}
			resultLines.add(t("screen.emi.runegate.result.lower_corners",
					t("screen.emi.runegate.corner.ll"), RunegateCalculator.describeCorner(lowerLeft),
					t("screen.emi.runegate.corner.lr"), RunegateCalculator.describeCorner(lowerRight)));
			resultLines.add(t("screen.emi.runegate.result.upper_corners",
					t("screen.emi.runegate.corner.ul"), RunegateCalculator.describeCorner(upperLeft),
					t("screen.emi.runegate.corner.ur"), RunegateCalculator.describeCorner(upperRight)));
		} catch (IllegalArgumentException e) {
			resultLines.clear();
			resultLines.add(t("screen.emi.runegate.result.input_error", e.getMessage()));
		}
	}

	private void reverseDestination() {
		try {
			int targetXInput = parseInt(targetXField, t("screen.emi.runegate.field.x"));
			int targetZInput = parseInt(targetZField, t("screen.emi.runegate.field.z"));
			int targetXNative = toNative(targetXInput, coordinateSpace);
			int targetZNative = toNative(targetZInput, coordinateSpace);
			int radius = parseBounded(radiusField, t("screen.emi.runegate.field.radius"), 1, 200000);
			int matchRadius = parseBounded(matchRadiusField, t("screen.emi.runegate.field.match_radius"), 0, 5000);
			int maxResults = parseBounded(maxResultsField, t("screen.emi.runegate.field.max"), 1, 300);
			RunegateCalculator.OceanPredicate oceanPredicate = strictFirstAttemptOnly
					? (x, z) -> false
					: resolveReverseOceanPredicate();
			RunegateCalculator.OceanPredicate liveOceanPredicate = resolveReverseOceanPredicate();
			RunegateCalculator.ReverseAnalysis analysis = RunegateCalculator.reverseAnalyze(
					targetXNative, targetZNative, matchRadius, material, radius, oceanPredicate, maxResults, strictFirstAttemptOnly, 0, 0);

			resultLines.clear();
			resultLines.add(t("screen.emi.runegate.result.reverse_mode", t(material == RunegateCalculator.RunestoneMaterial.MITHRIL
					? "screen.emi.runegate.material.mithril"
					: "screen.emi.runegate.material.adamantium"), radius));
			resultLines.add(t("screen.emi.runegate.result.coord_space", t(coordinateSpace.translationKey)));
			resultLines.add(t("screen.emi.runegate.result.filter", t(strictFirstAttemptOnly
					? "screen.emi.runegate.filter.strict"
					: "screen.emi.runegate.filter.all")));
			resultLines.add(t("screen.emi.runegate.result.target", targetXInput, targetZInput));
			if (coordinateSpace != CoordinateSpace.CURRENT) {
				resultLines.add(t("screen.emi.runegate.result.target_native", targetXNative, targetZNative));
			}
			resultLines.add(t("screen.emi.runegate.result.match_radius", matchRadius));
			if (analysis.inRadiusTotal <= 0) {
				resultLines.add(t("screen.emi.runegate.result.no_match_in_radius"));
				resultLines.add(t("screen.emi.runegate.result.nearest_header", analysis.nearest.size()));
				for (RunegateCalculator.ReverseMatch match : analysis.nearest) {
					int showX = fromNative(match.destinationX, coordinateSpace);
					int showZ = fromNative(match.destinationZ, coordinateSpace);
					resultLines.add(t("screen.emi.runegate.result.nearest_line",
							RunegateCalculator.seedHex(match.combination.seed),
							RunegateCalculator.arrangementCode(match.combination),
							showX,
							showZ,
							(long) Math.sqrt(match.distanceSq),
							match.selectedAttempt,
							RunegateCalculator.describeCorner(match.combination.lowerLeft),
							RunegateCalculator.describeCorner(match.combination.lowerRight),
							RunegateCalculator.describeCorner(match.combination.upperLeft),
							RunegateCalculator.describeCorner(match.combination.upperRight)));
					appendRedirectLineIfNeeded(match, liveOceanPredicate, radius);
				}
				return;
			}
			boolean truncated = analysis.inRadiusTotal > analysis.inRadius.size();
			resultLines.add(t("screen.emi.runegate.result.matches", analysis.inRadiusTotal, truncated ? "+" : ""));
			for (RunegateCalculator.ReverseMatch match : analysis.inRadius) {
				int showX = fromNative(match.destinationX, coordinateSpace);
				int showZ = fromNative(match.destinationZ, coordinateSpace);
				resultLines.add(t("screen.emi.runegate.result.nearest_line",
						RunegateCalculator.seedHex(match.combination.seed),
						RunegateCalculator.arrangementCode(match.combination),
						showX,
						showZ,
						(long) Math.sqrt(match.distanceSq),
						match.selectedAttempt,
						RunegateCalculator.describeCorner(match.combination.lowerLeft),
						RunegateCalculator.describeCorner(match.combination.lowerRight),
						RunegateCalculator.describeCorner(match.combination.upperLeft),
						RunegateCalculator.describeCorner(match.combination.upperRight)));
				appendRedirectLineIfNeeded(match, liveOceanPredicate, radius);
			}
		} catch (IllegalArgumentException e) {
			resultLines.clear();
			resultLines.add(t("screen.emi.runegate.result.input_error", e.getMessage()));
		}
	}

	private int parseInt(TextFieldWidget field, String name) {
		String text = field.getText() == null ? "" : field.getText().trim();
		if (text.isEmpty()) {
			throw new IllegalArgumentException(t("screen.emi.runegate.error.empty", name));
		}
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(t("screen.emi.runegate.error.invalid_int", name));
		}
	}

	private Integer parseIntSafely(TextFieldWidget field) {
		String text = field.getText() == null ? "" : field.getText().trim();
		if (text.isEmpty()) {
			return null;
		}
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException ignored) {
			return null;
		}
	}

	private int parseBounded(TextFieldWidget field, String name, int min, int max) {
		int value = parseInt(field, name);
		if (value < min || value > max) {
			throw new IllegalArgumentException(t("screen.emi.runegate.error.range", name, min, max));
		}
		return value;
	}

	private String t(String key, Object... args) {
		return EmiPort.translatable(key, args).asString();
	}

	private String previewCorner(TextFieldWidget field) {
		String text = field.getText() == null ? "" : field.getText().trim();
		if (text.isEmpty()) {
			return "-";
		}
		try {
			int value = Integer.parseInt(text);
			if (value < 0 || value > 15) {
				return "?";
			}
			return RunegateCalculator.describeCorner(value);
		} catch (NumberFormatException e) {
			return "?";
		}
	}

	private int renderMetaLegend(int x, int y) {
		int colWidth = 58;
		int rowHeight = 9;
		if (x + colWidth * 2 > width - 8) {
			return y;
		}
		fontRenderer.drawStringWithShadow(t("screen.emi.runegate.legend"), x, y, 0xE0E0E0);
		for (int i = 0; i < 16; i++) {
			int column = i / 8;
			int row = i % 8;
			int drawX = x + column * colWidth;
			int drawY = y + 12 + row * rowHeight;
			fontRenderer.drawStringWithShadow(t("screen.emi.runegate.legend.entry", i, RunegateCalculator.magicName(i)), drawX, drawY, 0xC0C0C0);
		}
		return y + 12 + rowHeight * 8;
	}

	private void cycleCoordinateSpace() {
		CoordinateSpace oldSpace = coordinateSpace;
		coordinateSpace = coordinateSpace.next();
		convertFieldSpace(targetXField, oldSpace, coordinateSpace);
		convertFieldSpace(targetZField, oldSpace, coordinateSpace);
		updateCoordinateSpaceButtonText();
		runCurrentMode();
	}

	private RunegateCalculator.OceanPredicate resolveReverseOceanPredicate() {
		return RunegateCalculator.oceanPredicate(client.theWorld);
	}

	private void appendRedirectLineIfNeeded(RunegateCalculator.ReverseMatch match, RunegateCalculator.OceanPredicate liveOceanPredicate, int radius) {
		if (!strictFirstAttemptOnly || match.combination.seed == 0) {
			return;
		}
		RunegateCalculator.Destination finalDestination = RunegateCalculator.calculateDestination(
				match.combination.seed, material, radius, liveOceanPredicate);
		if (finalDestination.x == match.destinationX && finalDestination.z == match.destinationZ) {
			return;
		}
		int shownFinalX = fromNative(finalDestination.x, coordinateSpace);
		int shownFinalZ = fromNative(finalDestination.z, coordinateSpace);
		resultLines.add(t("screen.emi.runegate.result.redirect_line", shownFinalX, shownFinalZ, finalDestination.attempts));
	}

	private void convertFieldSpace(TextFieldWidget field, CoordinateSpace oldSpace, CoordinateSpace newSpace) {
		Integer value = parseIntSafely(field);
		if (value == null) {
			return;
		}
		int nativeValue = toNative(value, oldSpace);
		field.setText(String.valueOf(fromNative(nativeValue, newSpace)));
	}

	private void resetTargetToCurrentPlayer() {
		String playerX = getCurrentPlayerBlockXText();
		String playerZ = getCurrentPlayerBlockZText();
		targetXField.setText(playerX);
		targetZField.setText(playerZ);
		saveState();
	}

	private String getCurrentPlayerBlockXText() {
		if (client == null || client.thePlayer == null) {
			return "";
		}
		return String.valueOf(fromNative((int) Math.floor(client.thePlayer.posX), coordinateSpace));
	}

	private String getCurrentPlayerBlockZText() {
		if (client == null || client.thePlayer == null) {
			return "";
		}
		return String.valueOf(fromNative((int) Math.floor(client.thePlayer.posZ), coordinateSpace));
	}

	private boolean isCurrentWorldNether() {
		return client != null && client.theWorld != null && client.theWorld.isTheNether();
	}

	private int toNative(int value, CoordinateSpace space) {
		if (space == CoordinateSpace.CURRENT) {
			return value;
		}
		if (space == CoordinateSpace.OVERWORLD) {
			return isCurrentWorldNether() ? value / 8 : value;
		}
		return isCurrentWorldNether() ? value : value * 8;
	}

	private int fromNative(int nativeValue, CoordinateSpace space) {
		if (space == CoordinateSpace.CURRENT) {
			return nativeValue;
		}
		if (space == CoordinateSpace.OVERWORLD) {
			return isCurrentWorldNether() ? nativeValue * 8 : nativeValue;
		}
		return isCurrentWorldNether() ? nativeValue : nativeValue / 8;
	}

	private String stateOrDefault(String stateValue, String fallbackValue) {
		if (LAST_STATE.initialized) {
			return stateValue == null ? "" : stateValue;
		}
		return fallbackValue == null ? "" : fallbackValue;
	}

	private void restoreModesFromState() {
		if (!LAST_STATE.initialized) {
			return;
		}
		operationMode = LAST_STATE.operationMode;
		coordinateSpace = LAST_STATE.coordinateSpace;
		material = LAST_STATE.material;
		strictFirstAttemptOnly = LAST_STATE.strictFirstAttemptOnly;
	}

	private void saveState() {
		if (lowerLeftField == null || lowerRightField == null || upperLeftField == null || upperRightField == null ||
				radiusField == null || targetXField == null || targetZField == null || maxResultsField == null || matchRadiusField == null) {
			return;
		}
		LAST_STATE.initialized = true;
		LAST_STATE.operationMode = operationMode;
		LAST_STATE.coordinateSpace = coordinateSpace;
		LAST_STATE.material = material;
		LAST_STATE.strictFirstAttemptOnly = strictFirstAttemptOnly;
		LAST_STATE.lowerLeft = lowerLeftField.getText();
		LAST_STATE.lowerRight = lowerRightField.getText();
		LAST_STATE.upperLeft = upperLeftField.getText();
		LAST_STATE.upperRight = upperRightField.getText();
		LAST_STATE.radius = radiusField.getText();
		LAST_STATE.targetX = targetXField.getText();
		LAST_STATE.targetZ = targetZField.getText();
		LAST_STATE.maxResults = maxResultsField.getText();
		LAST_STATE.matchRadius = matchRadiusField.getText();
		LAST_STATE.resultLines = new ArrayList<>(resultLines);
		saveStateToDisk();
	}

	private static void ensureStateLoaded() {
		if (stateLoadedFromDisk) {
			return;
		}
		stateLoadedFromDisk = true;
		if (!STATE_FILE.exists()) {
			return;
		}
		try (FileReader reader = new FileReader(STATE_FILE)) {
			JsonObject json = GSON.fromJson(reader, JsonObject.class);
			if (json == null) {
				return;
			}
			LAST_STATE.initialized = getBoolean(json, "initialized", false);
			LAST_STATE.operationMode = parseOperationMode(getString(json, "operationMode", OperationMode.FORWARD.name()));
			LAST_STATE.coordinateSpace = parseCoordinateSpace(getString(json, "coordinateSpace", CoordinateSpace.CURRENT.name()));
			LAST_STATE.material = parseMaterial(getString(json, "material", RunegateCalculator.RunestoneMaterial.MITHRIL.name()));
			LAST_STATE.strictFirstAttemptOnly = getBoolean(json, "strictFirstAttemptOnly", true);
			LAST_STATE.lowerLeft = getString(json, "lowerLeft", "");
			LAST_STATE.lowerRight = getString(json, "lowerRight", "");
			LAST_STATE.upperLeft = getString(json, "upperLeft", "");
			LAST_STATE.upperRight = getString(json, "upperRight", "");
			LAST_STATE.radius = getString(json, "radius", "");
			LAST_STATE.targetX = getString(json, "targetX", "");
			LAST_STATE.targetZ = getString(json, "targetZ", "");
			LAST_STATE.maxResults = getString(json, "maxResults", "");
			LAST_STATE.matchRadius = getString(json, "matchRadius", "");
			LAST_STATE.resultLines = new ArrayList<>();
			if (json.has("resultLines") && json.get("resultLines").isJsonArray()) {
				for (var element : json.getAsJsonArray("resultLines")) {
					LAST_STATE.resultLines.add(element.getAsString());
				}
			}
		} catch (Exception ignored) {
		}
	}

	private static void saveStateToDisk() {
		try (FileWriter writer = new FileWriter(STATE_FILE)) {
			JsonObject json = new JsonObject();
			json.addProperty("initialized", LAST_STATE.initialized);
			json.addProperty("operationMode", LAST_STATE.operationMode.name());
			json.addProperty("coordinateSpace", LAST_STATE.coordinateSpace.name());
			json.addProperty("material", LAST_STATE.material.name());
			json.addProperty("strictFirstAttemptOnly", LAST_STATE.strictFirstAttemptOnly);
			json.addProperty("lowerLeft", LAST_STATE.lowerLeft);
			json.addProperty("lowerRight", LAST_STATE.lowerRight);
			json.addProperty("upperLeft", LAST_STATE.upperLeft);
			json.addProperty("upperRight", LAST_STATE.upperRight);
			json.addProperty("radius", LAST_STATE.radius);
			json.addProperty("targetX", LAST_STATE.targetX);
			json.addProperty("targetZ", LAST_STATE.targetZ);
			json.addProperty("maxResults", LAST_STATE.maxResults);
			json.addProperty("matchRadius", LAST_STATE.matchRadius);
			JsonArray resultArray = new JsonArray();
			for (String line : LAST_STATE.resultLines) {
				resultArray.add(line);
			}
			json.add("resultLines", resultArray);
			GSON.toJson(json, writer);
		} catch (Exception ignored) {
		}
	}

	private static String getString(JsonObject json, String key, String fallback) {
		if (!json.has(key) || json.get(key).isJsonNull()) {
			return fallback;
		}
		return json.get(key).getAsString();
	}

	private static boolean getBoolean(JsonObject json, String key, boolean fallback) {
		if (!json.has(key) || json.get(key).isJsonNull()) {
			return fallback;
		}
		return json.get(key).getAsBoolean();
	}

	private static OperationMode parseOperationMode(String raw) {
		try {
			return OperationMode.valueOf(raw);
		} catch (Exception ignored) {
			return OperationMode.FORWARD;
		}
	}

	private static CoordinateSpace parseCoordinateSpace(String raw) {
		try {
			return CoordinateSpace.valueOf(raw);
		} catch (Exception ignored) {
			return CoordinateSpace.CURRENT;
		}
	}

	private static RunegateCalculator.RunestoneMaterial parseMaterial(String raw) {
		try {
			return RunegateCalculator.RunestoneMaterial.valueOf(raw);
		} catch (Exception ignored) {
			return RunegateCalculator.RunestoneMaterial.MITHRIL;
		}
	}
}
