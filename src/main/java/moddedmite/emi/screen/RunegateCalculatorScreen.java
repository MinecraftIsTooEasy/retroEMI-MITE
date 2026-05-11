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

	private static final int PANEL_WIDTH = 500;
	private static final int PANEL_MARGIN = 10;
	private static final int SECTION_GAP = 8;
	private static final int HEADER_HEIGHT = 18;
	private static final int CONTROL_HEIGHT = 20;
	private static final int FIELD_WIDTH = 58;
	private static final int LABEL_COLOR = 0xFFFFE0;
	private static final int MUTED_COLOR = 0xA0A0A0;
	private static final int PREVIEW_COLOR = 0xAAFFAA;
	private static final int RESULT_COLOR = 0xFFFFFF;
	private static final int DANGER = 0xFFFFAA66;

	private static final SavedState LAST_STATE = new SavedState();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File STATE_FILE = new File("emi-runegate.json");
	private static boolean stateLoadedFromDisk;

	private final GuiScreen parent;
	private final List<TextFieldWidget> fields = new ArrayList<>();
	private final List<String> resultLines = new ArrayList<>();
	private boolean resultHasError;

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

	private Layout layout = Layout.empty();
	private int resultScroll;
	private boolean draggingResultScrollbar;
	private double resultScrollbarDragOffset;

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

	private record Rect(int x, int y, int width, int height) {
		private int right() {
			return x + width;
		}

		private int bottom() {
			return y + height;
		}

		private boolean contains(double mouseX, double mouseY) {
			return mouseX >= x && mouseX < right() && mouseY >= y && mouseY < bottom();
		}
	}

	private record Layout(Rect panel, Rect runestones, Rect settings, Rect target, Rect legend, Rect results) {
		private static Layout empty() {
			Rect zero = new Rect(0, 0, 0, 0);
			return new Layout(zero, zero, zero, zero, zero, zero);
		}
	}

	private record ScrollbarMetrics(int trackX, int trackY, int trackHeight, int thumbY, int thumbHeight) {
		private int trackBottom() {
			return trackY + trackHeight;
		}

		private int thumbBottom() {
			return thumbY + thumbHeight;
		}

		private boolean containsTrack(double mouseX, double mouseY) {
			return mouseX >= trackX - 4 && mouseX < trackX + 6 && mouseY >= trackY && mouseY < trackBottom();
		}

		private boolean containsThumb(double mouseX, double mouseY) {
			return mouseX >= trackX - 4 && mouseX < trackX + 6 && mouseY >= thumbY && mouseY < thumbBottom();
		}
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
		layout = createLayout();

		String playerX = getCurrentPlayerBlockXText();
		String playerZ = getCurrentPlayerBlockZText();
		Rect runestones = layout.runestones();
		Rect settings = layout.settings();
		Rect target = layout.target();

		int cornerY = runestones.y() + 32;
		int cornerX = runestones.x() + 10;
		int cornerGap = 8;

		lowerLeftField = addNumberField(cornerX, cornerY, FIELD_WIDTH, stateOrDefault(LAST_STATE.lowerLeft, ""), 2);
		lowerRightField = addNumberField(cornerX + (FIELD_WIDTH + cornerGap), cornerY, FIELD_WIDTH, stateOrDefault(LAST_STATE.lowerRight, ""), 2);
		upperLeftField = addNumberField(cornerX + (FIELD_WIDTH + cornerGap) * 2, cornerY, FIELD_WIDTH, stateOrDefault(LAST_STATE.upperLeft, ""), 2);
		upperRightField = addNumberField(cornerX + (FIELD_WIDTH + cornerGap) * 3, cornerY, FIELD_WIDTH, stateOrDefault(LAST_STATE.upperRight, ""), 2);

		materialButton = this.addDrawableChild(EmiPort.newButton(settings.x() + 146, settings.y() + 27, 92, CONTROL_HEIGHT, EmiPort.literal(""), button -> {
			material = material == RunegateCalculator.RunestoneMaterial.MITHRIL ? RunegateCalculator.RunestoneMaterial.ADAMANTIUM
					: RunegateCalculator.RunestoneMaterial.MITHRIL;
			updateMaterialButtonText();
			radiusField.setText(String.valueOf(resolveDomainRadius()));
			runCurrentMode();
		}));
		updateMaterialButtonText();

		radiusField = addNumberField(settings.x() + 12, settings.y() + 27, 72, stateOrDefault(LAST_STATE.radius, String.valueOf(resolveDomainRadius())), 6);
		targetXField = addNumberField(target.x() + 12, target.y() + 28, 64, stateOrDefault(LAST_STATE.targetX, playerX), 11);
		targetZField = addNumberField(target.x() + 84, target.y() + 28, 64, stateOrDefault(LAST_STATE.targetZ, playerZ), 11);
		maxResultsField = addNumberField(target.x() + 156, target.y() + 28, 48, stateOrDefault(LAST_STATE.maxResults, "24"), 3);
		matchRadiusField = addNumberField(target.x() + 212, target.y() + 28, 64, stateOrDefault(LAST_STATE.matchRadius, "128"), 4);

		this.addDrawableChild(EmiPort.newButton(settings.x() + 88, settings.y() + 27, 48, CONTROL_HEIGHT, EmiPort.literal(t("screen.emi.runegate.reset_radius")), button -> {
			radiusField.setText(String.valueOf(resolveDomainRadius()));
			saveState();
		}));
		modeButton = this.addDrawableChild(EmiPort.newButton(settings.x() + 246, settings.y() + 27, 74, CONTROL_HEIGHT, EmiPort.literal(""), button -> {
			operationMode = operationMode == OperationMode.FORWARD ? OperationMode.REVERSE : OperationMode.FORWARD;
			updateModeButtonText();
			runCurrentMode();
		}));
		updateModeButtonText();

		this.addDrawableChild(EmiPort.newButton(layout.panel().right() - 134, layout.panel().y() + 7, 56, CONTROL_HEIGHT, EmiPort.literal(t("screen.emi.runegate.run")), button -> runCurrentMode()));
		coordinateSpaceButton = this.addDrawableChild(EmiPort.newButton(settings.x() + 328, settings.y() + 27, 70, CONTROL_HEIGHT, EmiPort.literal(""), button -> cycleCoordinateSpace()));
		updateCoordinateSpaceButtonText();

		reverseFilterButton = this.addDrawableChild(EmiPort.newButton(target.x() + 12, target.y() + 54, 130, CONTROL_HEIGHT, EmiPort.literal(""), button -> {
			strictFirstAttemptOnly = !strictFirstAttemptOnly;
			updateReverseFilterButtonText();
			runCurrentMode();
		}));
		updateReverseFilterButtonText();

		this.addDrawableChild(EmiPort.newButton(target.x() + 150, target.y() + 54, 112, CONTROL_HEIGHT, EmiPort.literal(t("screen.emi.runegate.reset_center")), button -> {
			resetTargetToCurrentPlayer();
			runCurrentMode();
		}));
		this.addDrawableChild(EmiPort.newButton(layout.panel().right() - 72, layout.panel().y() + 7, 62, CONTROL_HEIGHT, EmiPort.translatable("gui.done"), button -> close()));

		resultLines.clear();
		if (LAST_STATE.initialized) {
			resultLines.addAll(LAST_STATE.resultLines);
		}
		clampResultScroll();
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
		renderResultBox();
		super.render(raw, mouseX, mouseY, delta);
		renderForeground(raw, mouseX, mouseY);
	}

	private void renderForeground(DrawContext raw, int mouseX, int mouseY) {
		renderScreenTitle();
		renderSectionTitles();
		renderRunestoneLabels();
		renderSettingsLabels();
		renderTargetLabels();
		renderMetaLegend(layout.legend());
		renderResultLines();
	}

	private Layout createLayout() {
		int panelWidth = Math.min(PANEL_WIDTH, Math.max(430, width - 16));
		int panelHeight = Math.min(420, Math.max(320, height - 16));
		int left = width / 2 - panelWidth / 2;
		int top = Math.max(8, height / 2 - panelHeight / 2);
		Rect panel = new Rect(left, top, panelWidth, panelHeight);
		int innerX = panel.x() + PANEL_MARGIN;
		int innerY = panel.y() + 32;
		int innerWidth = panel.width() - PANEL_MARGIN * 2;
		Rect runestones = new Rect(innerX, innerY, innerWidth, 74);
		Rect settings = new Rect(innerX, runestones.bottom() + SECTION_GAP, innerWidth, 58);
		Rect target = new Rect(innerX, settings.bottom() + SECTION_GAP, 284, 80);
		Rect legend = new Rect(target.right() + SECTION_GAP, settings.bottom() + SECTION_GAP, innerWidth - target.width() - SECTION_GAP, 86);
		Rect results = new Rect(innerX, target.bottom() + SECTION_GAP, innerWidth, panel.bottom() - target.bottom() - SECTION_GAP - PANEL_MARGIN);
		if (legend.width() < 112) {
			legend = new Rect(innerX, target.bottom() + SECTION_GAP, innerWidth, 54);
			results = new Rect(innerX, legend.bottom() + SECTION_GAP, innerWidth, panel.bottom() - legend.bottom() - SECTION_GAP - PANEL_MARGIN);
		}
		return new Layout(panel, runestones, settings, target, legend, results);
	}

	private void renderResultBox() {
		Rect rect = resultContentRect();
		drawRect(rect.x() - 1, rect.y() - 1, rect.right() + 1, rect.bottom() + 1, -6250336);
		drawRect(rect.x(), rect.y(), rect.right(), rect.bottom(), -16777216);
		drawScrollbar();
	}

	private void renderScreenTitle() {
		Rect panel = layout.panel();
		drawCenteredString(fontRenderer, t("screen.emi.runegate_calculator"), panel.x() + panel.width() / 2, panel.y() + 11, 0xFFFFFF);
	}

	private void renderSectionTitles() {
		drawSectionTitle(layout.runestones(), t("screen.emi.runegate.corners"));
		drawSectionTitle(layout.settings(), t("screen.emi.runegate.mode"));
		drawSectionTitle(layout.target(), t("screen.emi.runegate.target"));
		drawSectionTitle(layout.legend(), t("screen.emi.runegate.legend"));
		drawSectionTitle(layout.results(), resultHeaderText());
	}

	private void drawSectionTitle(Rect rect, String title) {
		fontRenderer.drawStringWithShadow(title, rect.x(), rect.y(), LABEL_COLOR);
		drawRect(rect.x(), rect.y() + 12, rect.right(), rect.y() + 13, 0xFF555555);
		drawRect(rect.x(), rect.y() + 13, rect.right(), rect.y() + 14, 0xFF1A1A1A);
	}

	private void renderRunestoneLabels() {
		Rect rect = layout.runestones();
		TextFieldWidget[] cornerFields = {lowerLeftField, lowerRightField, upperLeftField, upperRightField};
		String[] labels = {
				t("screen.emi.runegate.corner.ll"),
				t("screen.emi.runegate.corner.lr"),
				t("screen.emi.runegate.corner.ul"),
				t("screen.emi.runegate.corner.ur")
		};
		for (int i = 0; i < cornerFields.length; i++) {
			TextFieldWidget field = cornerFields[i];
			fontRenderer.drawStringWithShadow(labels[i], field.getX() + 2, rect.y() + 20, MUTED_COLOR);
			fontRenderer.drawStringWithShadow(previewCorner(field), field.getX() + 2, field.getY() + 23, previewColor(field));
		}
		int hintX = rect.x() + 270;
		int hintWidth = Math.max(40, rect.right() - hintX - 8);
		fontRenderer.drawStringWithShadow(trimToWidth(t("screen.emi.runegate.hint",
				t("screen.emi.runegate.corner.ll"),
				t("screen.emi.runegate.corner.lr"),
				t("screen.emi.runegate.corner.ul"),
				t("screen.emi.runegate.corner.ur")), hintWidth), hintX, rect.y() + 31, MUTED_COLOR);
		fontRenderer.drawStringWithShadow(trimToWidth(t("screen.emi.runegate.meta_hint"), hintWidth), hintX, rect.y() + 43, MUTED_COLOR);
	}

	private void renderSettingsLabels() {
		Rect rect = layout.settings();
		drawFieldLabel(radiusField, t("screen.emi.runegate.radius"), rect.y() + 17);
		drawButtonLabel(materialButton, t("screen.emi.runegate.material"), rect.y() + 17);
		drawButtonLabel(modeButton, t("screen.emi.runegate.mode"), rect.y() + 17);
		drawButtonLabel(coordinateSpaceButton, t("screen.emi.runegate.coord"), rect.y() + 17);
	}

	private void renderTargetLabels() {
		Rect rect = layout.target();
		drawFieldLabel(targetXField, t("screen.emi.runegate.field.x"), rect.y() + 18);
		drawFieldLabel(targetZField, t("screen.emi.runegate.field.z"), rect.y() + 18);
		drawFieldLabel(maxResultsField, t("screen.emi.runegate.max_results"), rect.y() + 18);
		drawFieldLabel(matchRadiusField, t("screen.emi.runegate.match_radius"), rect.y() + 18);
	}

	private void drawFieldLabel(TextFieldWidget field, String label, int y) {
		fontRenderer.drawStringWithShadow(label, field.getX() + 2, y, MUTED_COLOR);
	}

	private void drawButtonLabel(ButtonWidget button, String label, int y) {
		fontRenderer.drawStringWithShadow(label, button.getX() + 2, y, MUTED_COLOR);
	}

	private void renderMetaLegend(Rect rect) {
		int colWidth = Math.max(52, (rect.width() - 16) / 2);
		int rowHeight = 8;
		int startX = rect.x() + 8;
		int startY = rect.y() + HEADER_HEIGHT + 6;
		for (int i = 0; i < 16; i++) {
			int column = i / 8;
			int row = i % 8;
			int drawX = startX + column * colWidth;
			int drawY = startY + row * rowHeight;
			if (drawY > rect.bottom() - 9) {
				break;
			}
			fontRenderer.drawStringWithShadow(t("screen.emi.runegate.legend.entry", i, RunegateCalculator.magicName(i)), drawX, drawY, 0xCFC7B7);
		}
	}

	private void renderResultLines() {
		Rect rect = resultContentRect();
		int visibleLines = visibleResultLines();
		int y = rect.y() + 4;
		int maxLineWidth = rect.width() - 10 - (needsResultScroll() ? 8 : 0);
		clampResultScroll();
		for (int i = resultScroll; i < resultLines.size() && i < resultScroll + visibleLines; i++) {
			String line = trimToWidth(resultLines.get(i), maxLineWidth);
			fontRenderer.drawStringWithShadow(line, rect.x() + 5, y, resultLineColor(line));
			y += 10;
		}
	}

	private void drawScrollbar() {
		if (!needsResultScroll()) {
			return;
		}
		ScrollbarMetrics metrics = resultScrollbarMetrics();
		int trackX = metrics.trackX();
		int trackY = metrics.trackY();
		int trackHeight = metrics.trackHeight();
		int thumbY = metrics.thumbY();
		int thumbHeight = metrics.thumbHeight();
		drawRect(trackX, trackY, trackX + 2, trackY + trackHeight, 0xFF303030);
		drawRect(trackX - 1, thumbY, trackX + 3, thumbY + thumbHeight, 0xFFA0A0A0);
	}

	private ScrollbarMetrics resultScrollbarMetrics() {
		Rect rect = resultContentRect();
		int trackX = rect.right() - 5;
		int trackY = rect.y() + 3;
		int trackHeight = rect.height() - 6;
		int visibleLines = visibleResultLines();
		int maxScroll = maxResultScroll();
		int thumbHeight = Math.max(12, trackHeight * visibleLines / Math.max(resultLines.size(), 1));
		int thumbY = trackY + (maxScroll == 0 ? 0 : (trackHeight - thumbHeight) * resultScroll / maxScroll);
		return new ScrollbarMetrics(trackX, trackY, trackHeight, thumbY, thumbHeight);
	}

	private String resultHeaderText() {
		String mode = operationMode == OperationMode.FORWARD
				? t("screen.emi.runegate.calculate")
				: t("screen.emi.runegate.reverse");
		return mode + " / " + t(coordinateSpace.translationKey);
	}

	private Rect resultContentRect() {
		Rect results = layout.results();
		return new Rect(results.x() + 5, results.y() + HEADER_HEIGHT + 5, results.width() - 10, results.height() - HEADER_HEIGHT - 10);
	}

	private int visibleResultLines() {
		return Math.max(1, (resultContentRect().height() - 8) / 10);
	}

	private boolean needsResultScroll() {
		return resultLines.size() > visibleResultLines();
	}

	private int maxResultScroll() {
		return Math.max(0, resultLines.size() - visibleResultLines());
	}

	private void clampResultScroll() {
		int maxScroll = maxResultScroll();
		if (resultScroll < 0) {
			resultScroll = 0;
		} else if (resultScroll > maxScroll) {
			resultScroll = maxScroll;
		}
	}

	private void scrollResultToMouse(double mouseY, double dragOffset) {
		if (!needsResultScroll()) {
			resultScroll = 0;
			return;
		}
		ScrollbarMetrics metrics = resultScrollbarMetrics();
		int available = Math.max(1, metrics.trackHeight() - metrics.thumbHeight());
		double raw = (mouseY - dragOffset - metrics.trackY()) / available;
		resultScroll = (int) Math.round(raw * maxResultScroll());
		clampResultScroll();
	}

	private String trimToWidth(String text, int maxWidth) {
		if (fontRenderer.getStringWidth(text) <= maxWidth) {
			return text;
		}
		return fontRenderer.trimStringToWidth(text, Math.max(0, maxWidth - fontRenderer.getStringWidth("..."))) + "...";
	}

	private int resultLineColor(String line) {
		if (resultHasError) {
			return DANGER;
		}
		if (line.startsWith("  ->") || line.startsWith("  -")) {
			return MUTED_COLOR;
		}
		if (line.contains("0x") || line.contains("X=")) {
			return RESULT_COLOR;
		}
		return 0xD7D0C2;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && needsResultScroll()) {
			ScrollbarMetrics metrics = resultScrollbarMetrics();
			if (metrics.containsThumb(mouseX, mouseY)) {
				draggingResultScrollbar = true;
				resultScrollbarDragOffset = mouseY - metrics.thumbY();
				return true;
			}
			if (metrics.containsTrack(mouseX, mouseY)) {
				draggingResultScrollbar = true;
				resultScrollbarDragOffset = metrics.thumbHeight() / 2.0;
				scrollResultToMouse(mouseY, resultScrollbarDragOffset);
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0 && draggingResultScrollbar) {
			draggingResultScrollbar = false;
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (button == 0 && draggingResultScrollbar) {
			scrollResultToMouse(mouseY, resultScrollbarDragOffset);
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
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

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (layout.results().contains(mouseX, mouseY) && needsResultScroll()) {
			resultScroll -= (int) amount;
			clampResultScroll();
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
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
		materialButton.setMessage(EmiPort.literal(materialText()));
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
		resultScroll = 0;
		clampResultScroll();
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
			RunegateCalculator.Destination destination = trace.destination();
			int shownX = fromNative(destination.x(), coordinateSpace);
			int shownZ = fromNative(destination.z(), coordinateSpace);

			resultLines.clear();
			resultHasError = false;
			resultLines.add(t("screen.emi.runegate.result.mode", materialText(), radius));
			resultLines.add(t("screen.emi.runegate.result.coord_space", t(coordinateSpace.translationKey)));
			resultLines.add(t("screen.emi.runegate.result.seed", RunegateCalculator.seedHex(seed), seed));
			resultLines.add(t("screen.emi.runegate.result.destination", shownX, shownZ));
			if (coordinateSpace != CoordinateSpace.CURRENT) {
				resultLines.add(t("screen.emi.runegate.result.destination_native", destination.x(), destination.z()));
			}
			if (seed == 0) {
				resultLines.add(t("screen.emi.runegate.result.seed_zero"));
			} else {
				resultLines.add(t("screen.emi.runegate.result.attempts", destination.attempts(), 4));
				resultLines.add(t("screen.emi.runegate.result.attempt_candidates"));
				for (RunegateCalculator.AttemptPoint attempt : trace.attempts()) {
					int shownAttemptX = fromNative(attempt.x(), coordinateSpace);
					int shownAttemptZ = fromNative(attempt.z(), coordinateSpace);
					String biomeState = t(attempt.ocean()
							? "screen.emi.runegate.result.attempt_ocean"
							: "screen.emi.runegate.result.attempt_land");
					String selectedMark = attempt.attempt() == destination.attempts()
							? t("screen.emi.runegate.result.attempt_selected")
							: "";
					resultLines.add(t("screen.emi.runegate.result.attempt_line",
							attempt.attempt(), shownAttemptX, shownAttemptZ, biomeState, selectedMark));
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
			resultHasError = true;
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
			resultHasError = false;
			resultLines.add(t("screen.emi.runegate.result.reverse_mode", materialText(), radius));
			resultLines.add(t("screen.emi.runegate.result.coord_space", t(coordinateSpace.translationKey)));
			resultLines.add(t("screen.emi.runegate.result.filter", t(strictFirstAttemptOnly
					? "screen.emi.runegate.filter.strict"
					: "screen.emi.runegate.filter.all")));
			resultLines.add(t("screen.emi.runegate.result.target", targetXInput, targetZInput));
			if (coordinateSpace != CoordinateSpace.CURRENT) {
				resultLines.add(t("screen.emi.runegate.result.target_native", targetXNative, targetZNative));
			}
			resultLines.add(t("screen.emi.runegate.result.match_radius", matchRadius));
			if (!analysis.hasInRadiusMatches()) {
				resultLines.add(t("screen.emi.runegate.result.no_match_in_radius"));
				resultLines.add(t("screen.emi.runegate.result.nearest_header", analysis.nearest().size()));
				appendReverseMatches(analysis.nearest(), liveOceanPredicate, radius);
				return;
			}
			resultLines.add(t("screen.emi.runegate.result.matches", analysis.inRadiusTotal(), analysis.isTruncated() ? "+" : ""));
			appendReverseMatches(analysis.inRadius(), liveOceanPredicate, radius);
		} catch (IllegalArgumentException e) {
			resultLines.clear();
			resultHasError = true;
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

	private String materialText() {
		return t(material == RunegateCalculator.RunestoneMaterial.MITHRIL
				? "screen.emi.runegate.material.mithril"
				: "screen.emi.runegate.material.adamantium");
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

	private int previewColor(TextFieldWidget field) {
		String preview = previewCorner(field);
		if ("?".equals(preview)) {
			return DANGER;
		}
		if ("-".equals(preview)) {
			return MUTED_COLOR;
		}
		return PREVIEW_COLOR;
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

	private void appendReverseMatches(List<RunegateCalculator.ReverseMatch> matches, RunegateCalculator.OceanPredicate liveOceanPredicate, int radius) {
		for (RunegateCalculator.ReverseMatch match : matches) {
			resultLines.add(formatReverseMatch(match));
			appendRedirectLineIfNeeded(match, liveOceanPredicate, radius);
		}
	}

	private String formatReverseMatch(RunegateCalculator.ReverseMatch match) {
		RunegateCalculator.Combination combination = match.combination();
		return t("screen.emi.runegate.result.nearest_line",
				RunegateCalculator.seedHex(combination.seed()),
				RunegateCalculator.arrangementCode(combination),
				fromNative(match.destinationX(), coordinateSpace),
				fromNative(match.destinationZ(), coordinateSpace),
				(long) Math.sqrt(match.distanceSq()),
				match.selectedAttempt(),
				RunegateCalculator.describeCorner(combination.lowerLeft()),
				RunegateCalculator.describeCorner(combination.lowerRight()),
				RunegateCalculator.describeCorner(combination.upperLeft()),
				RunegateCalculator.describeCorner(combination.upperRight()));
	}

	private void appendRedirectLineIfNeeded(RunegateCalculator.ReverseMatch match, RunegateCalculator.OceanPredicate liveOceanPredicate, int radius) {
		if (!strictFirstAttemptOnly || match.combination().seed() == 0) {
			return;
		}
		RunegateCalculator.Destination finalDestination = RunegateCalculator.calculateDestination(
				match.combination().seed(), material, radius, liveOceanPredicate);
		if (finalDestination.x() == match.destinationX() && finalDestination.z() == match.destinationZ()) {
			return;
		}
		int shownFinalX = fromNative(finalDestination.x(), coordinateSpace);
		int shownFinalZ = fromNative(finalDestination.z(), coordinateSpace);
		resultLines.add(t("screen.emi.runegate.result.redirect_line", shownFinalX, shownFinalZ, finalDestination.attempts()));
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

	private String stateOrDefault(String stateValue, String defaultValue) {
		if (LAST_STATE.initialized) {
			return stateValue == null ? "" : stateValue;
		}
		return defaultValue == null ? "" : defaultValue;
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
		resultHasError = !resultLines.isEmpty() && resultLines.get(0).startsWith(t("screen.emi.runegate.result.input_error", ""));
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

	private static String getString(JsonObject json, String key, String defaultValue) {
		if (!json.has(key) || json.get(key).isJsonNull()) {
			return defaultValue;
		}
		return json.get(key).getAsString();
	}

	private static boolean getBoolean(JsonObject json, String key, boolean defaultValue) {
		if (!json.has(key) || json.get(key).isJsonNull()) {
			return defaultValue;
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
