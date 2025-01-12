package dev.emi.emi.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.input.EmiInput;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.runtime.EmiLog;
import net.xiaoyu233.fml.FishModLoader;
import shims.java.com.unascribed.nil.QDCSS;
import shims.java.com.unascribed.retroemi.RetroEMI;
import shims.java.net.minecraft.client.util.InputUtil;
import shims.java.org.lwjgl.glfw.GLFW;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.Util;

import java.io.File;
import java.io.FileWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EmiConfig {
	private static final Map<Class<?>, Setter> SETTERS = Maps.newHashMap();
	private static final Map<Class<?>, Writer<?>> WRITERS = Maps.newHashMap();
	private static final Map<Class<?>, MultiWriter<?>> MULTI_WRITERS = Maps.newHashMap();
	private static final Map<String, List<String>> unparsed = Maps.newHashMap();
	public static final Map<String, Predicate<?>> FILTERS = Maps.newHashMap();
	public static final String DEFAULT_CONFIG;
	public static boolean useGlobalConfig = false;
	public static String startupConfig;

	// General
	@Comment("emi.config.tooltip.general.enabled")
	@ConfigValue("general.enabled")
	public static boolean enabled = true;
	
	@Comment("emi.config.tooltip.general.cheat-mode")
	@ConfigValue("general.cheat-mode")
	public static boolean cheatMode = false;
	
	@Comment("emi.config.tooltip.general.help-level")
	@ConfigValue("general.help-level")
	public static HelpLevel helpLevel = HelpLevel.NORMAL;

	@ConfigGroup("general.search")
	@Comment("emi.config.tooltip.general.search-sidebar")
	@ConfigValue("general.search-sidebar")
	public static SidebarSide searchSidebar = SidebarSide.RIGHT;

	@Comment("emi.config.tooltip.general.search-tooltip-by-default")
	@ConfigValue("general.search-tooltip-by-default")
	public static boolean searchTooltipByDefault = true;
	
	@Comment("emi.config.tooltip.general.search-mod-name-by-default")
	@ConfigValue("general.search-mod-name-by-default")
	public static boolean searchModNameByDefault = false;
	
	@Comment("emi.config.tooltip.general.search-tags-by-default")
	@ConfigValue("general.search-tags-by-default")
	public static boolean searchTagsByDefault = false;
	
	@Comment("emi.config.tooltip.general.search-id-by-default")
	@ConfigValue("general.search-id-by-default")
	public static boolean searchIdByDefault = true;

	@Comment("emi.config.tooltip.general.search-name-by-pinyin")
	@ConfigValue("general.search-name-by-pinyin")
	@ConfigGroupEnd
	public static boolean searchNameByPinyin = true;

	// UI
	
	@Comment("emi.config.tooltip.ui.effect-location")
	@ConfigValue("ui.effect-location")
	public static EffectLocation effectLocation = EffectLocation.TOP;
	
	@Comment("emi.config.tooltip.ui.show-hover-overlay")
	@ConfigValue("ui.show-hover-overlay")
	public static boolean showHoverOverlay = true;
	
	@ConfigGroup("ui.mod-id")
	@Comment("emi.config.tooltip.ui.append-mod-id")
	@ConfigValue("ui.append-mod-id")
	public static boolean appendModId = true;
	
	@ConfigGroupEnd
	@Comment("emi.config.tooltip.ui.append-item-mod-id")
	@ConfigValue("ui.append-item-mod-id")
	public static boolean appendItemModId = true;
	
	@Comment("emi.config.tooltip.ui.miscraft-prevention")
	@ConfigValue("ui.miscraft-prevention")
	public static boolean miscraftPrevention = true;
	
	@Comment("emi.config.tooltip.ui.fluid-unit")
	@ConfigValue("ui.fluid-unit")
	public static FluidUnit fluidUnit = EmiAgnos.isForge() ? FluidUnit.MILLIBUCKETS : FluidUnit.LITERS;
	
	@Comment("emi.config.tooltip.ui.center-search-bar")
	@ConfigValue("ui.center-search-bar")
	public static boolean centerSearchBar = true;
	
	@ConfigFilter("ui.search-sidebar-focus")
	private static Predicate<SidebarType> searchSidebarFocusFilter = type -> {
		return type != SidebarType.CHESS;
	};
	
	@Comment("emi.config.tooltip.ui.search-sidebar-focus")
	@ConfigValue("ui.search-sidebar-focus")
	public static SidebarType searchSidebarFocus = SidebarType.INDEX;
	
	@ConfigFilter("ui.empty-search-sidebar-focus")
	private static Predicate<SidebarType> emptySearchSidebarFocusFilter = type -> {
		return type != SidebarType.CHESS;
	};
	
	@Comment("emi.config.tooltip.ui.empty-search-sidebar-focus")
	@ConfigValue("ui.empty-search-sidebar-focus")
	public static SidebarType emptySearchSidebarFocus = SidebarType.NONE;

	@Comment("config.emi.tooltip.ui.emi_config_button_visibility")
	@ConfigValue("ui.emi-config-button-visibility")
	public static ButtonVisibility emiConfigButtonVisibility = ButtonVisibility.SHOWN;

	@Comment("config.emi.tooltip.ui.recipe_tree_button_visibility")
	@ConfigValue("ui.recipe-tree-button-visibility")
	public static ButtonVisibility recipeTreeButtonVisibility = ButtonVisibility.AUTO;
	
	@ConfigGroup("ui.recipe-screen")
	@Comment("emi.config.tooltip.ui.vertical-margin")
	@ConfigValue("ui.vertical-margin")
	public static int verticalMargin = 20;
	
	@Comment("emi.config.tooltip.ui.minimum-recipe-screen-width")
	@ConfigValue("ui.minimum-recipe-screen-width")
	public static int minimumRecipeScreenWidth = 176;
	
	@ConfigFilter("ui.workstation-location")
	private static Predicate<SidebarSide> workstationLocationFilter = side -> {
		return side != SidebarSide.TOP;
	};
	@Comment("emi.config.tooltip.ui.workstation-location")
	@ConfigValue("ui.workstation-location")
	public static SidebarSide workstationLocation = SidebarSide.BOTTOM;
	
	@ConfigGroupEnd()
	@Comment("emi.config.tooltip.ui.show-cost-per-batch")
	@ConfigValue("ui.show-cost-per-batch")
	public static boolean showCostPerBatch = true;
	
	@ConfigGroup("ui.recipe-buttons")
	@Comment("emi.config.tooltip.ui.recipe-default-button")
	@ConfigValue("ui.recipe-default-button")
	public static boolean recipeDefaultButton = true;
	
	@Comment("emi.config.tooltip.ui.recipe-tree-button")
	@ConfigValue("ui.recipe-tree-button")
	public static boolean recipeTreeButton = true;
	
	@Comment("emi.config.tooltip.ui.recipe-fill-button")
	@ConfigValue("ui.recipe-fill-button")
	public static boolean recipeFillButton = true;
	
	@Comment("emi.config.tooltip.ui.recipe-screenshot-button")
	@ConfigValue("ui.recipe-screenshot-button")
	public static boolean recipeScreenshotButton = false;
	
	@ConfigGroupEnd
	@Comment("emi.config.tooltip.ui.recipe-screenshot-scale")
	@ConfigValue("ui.recipe-screenshot-scale")
	public static int recipeScreenshotScale = 0;
	
	// Left sidebar
	
	@ConfigGroup("ui.left-sidebar")
	@Comment("emi.config.tooltip.ui.left-sidebar-pages")
	@ConfigValue("ui.left-sidebar-pages")
	public static SidebarPages leftSidebarPages =
			new SidebarPages(Collections.singletonList(new SidebarPages.SidebarPage(SidebarType.FAVORITES)), SidebarSettings.LEFT);
	
	@Comment("emi.config.tooltip.ui.left-sidebar-subpanels")
	@ConfigValue("ui.left-sidebar-subpanels")
	public static SidebarSubpanels leftSidebarSubpanels = new SidebarSubpanels(List.of(), SidebarSettings.LEFT);
	
	@Comment("emi.config.tooltip.ui.left-sidebar-size")
	@ConfigValue("ui.left-sidebar-size")
	public static IntGroup leftSidebarSize = new IntGroup("emi.sidebar.size.", List.of("columns", "rows"), IntList.of(12, 100));
	
	@Comment("emi.config.tooltip.ui.left-sidebar-margins")
	@ConfigValue("ui.left-sidebar-margins")
	public static Margins leftSidebarMargins = new Margins(2, 2, 2, 2);
	
	@Comment("emi.config.tooltip.ui.left-sidebar-align")
	@ConfigValue("ui.left-sidebar-align")
	public static ScreenAlign leftSidebarAlign = new ScreenAlign(ScreenAlign.Horizontal.LEFT, ScreenAlign.Vertical.TOP);
	
	@Comment("emi.config.tooltip.ui.left-sidebar-header")
	@ConfigValue("ui.left-sidebar-header")
	public static HeaderType leftSidebarHeader = HeaderType.VISIBLE;
	
	@ConfigGroupEnd
	@Comment("emi.config.tooltip.ui.left-sidebar-theme")
	@ConfigValue("ui.left-sidebar-theme")
	public static SidebarTheme leftSidebarTheme = SidebarTheme.TRANSPARENT;
	
	
	// Right sidebar
	
	@ConfigGroup("ui.right-sidebar")
	@Comment("emi.config.tooltip.ui.right-sidebar-pages")
	@ConfigValue("ui.right-sidebar-pages")
	public static SidebarPages rightSidebarPages =
			new SidebarPages(List.of(new SidebarPages.SidebarPage(SidebarType.INDEX), new SidebarPages.SidebarPage(SidebarType.CRAFTABLES)),
					SidebarSettings.RIGHT);
	
	@Comment("emi.config.tooltip.ui.right-sidebar-subpanels")
	@ConfigValue("ui.right-sidebar-subpanels")
	public static SidebarSubpanels rightSidebarSubpanels = new SidebarSubpanels(List.of(), SidebarSettings.RIGHT);
	
	@Comment("emi.config.tooltip.ui.right-sidebar-size")
	@ConfigValue("ui.right-sidebar-size")
	public static IntGroup rightSidebarSize = new IntGroup("emi.sidebar.size.", List.of("columns", "rows"), IntList.of(12, 100));
	
	@Comment("emi.config.tooltip.ui.right-sidebar-margins")
	@ConfigValue("ui.right-sidebar-margins")
	public static Margins rightSidebarMargins = new Margins(2, 2, 2, 2);
	
	@Comment("emi.config.tooltip.ui.right-sidebar-align")
	@ConfigValue("ui.right-sidebar-align")
	public static ScreenAlign rightSidebarAlign = new ScreenAlign(ScreenAlign.Horizontal.RIGHT, ScreenAlign.Vertical.TOP);
	
	@Comment("emi.config.tooltip.ui.right-sidebar-header")
	@ConfigValue("ui.right-sidebar-header")
	public static HeaderType rightSidebarHeader = HeaderType.VISIBLE;
	
	@ConfigGroupEnd
	@Comment("emi.config.tooltip.ui.right-sidebar-theme")
	@ConfigValue("ui.right-sidebar-theme")
	public static SidebarTheme rightSidebarTheme = SidebarTheme.TRANSPARENT;
	
	// Top sidebar
	
	@ConfigGroup("ui.top-sidebar")
	@Comment("emi.config.tooltip.ui.top-sidebar-pages")
	@ConfigValue("ui.top-sidebar-pages")
	public static SidebarPages topSidebarPages = new SidebarPages(List.of(), SidebarSettings.TOP);
	
	@Comment("emi.config.tooltip.ui.top-sidebar-subpanels")
	@ConfigValue("ui.top-sidebar-subpanels")
	public static SidebarSubpanels topSidebarSubpanels = new SidebarSubpanels(List.of(), SidebarSettings.TOP);
	
	@Comment("emi.config.tooltip.ui.top-sidebar-size")
	@ConfigValue("ui.top-sidebar-size")
	public static IntGroup topSidebarSize = new IntGroup("emi.sidebar.size.", List.of("columns", "rows"), IntList.of(9, 9));
	
	@Comment("emi.config.tooltip.ui.top-sidebar-margins")
	@ConfigValue("ui.top-sidebar-margins")
	public static Margins topSidebarMargins = new Margins(2, 2, 2, 2);
	
	@Comment("emi.config.tooltip.ui.top-sidebar-align")
	@ConfigValue("ui.top-sidebar-align")
	public static ScreenAlign topSidebarAlign = new ScreenAlign(ScreenAlign.Horizontal.CENTER, ScreenAlign.Vertical.CENTER);
	
	@Comment("emi.config.tooltip.ui.top-sidebar-header")
	@ConfigValue("ui.top-sidebar-header")
	public static HeaderType topSidebarHeader = HeaderType.VISIBLE;
	
	@ConfigGroupEnd
	@Comment("emi.config.tooltip.ui.top-sidebar-theme")
	@ConfigValue("ui.top-sidebar-theme")
	public static SidebarTheme topSidebarTheme = SidebarTheme.TRANSPARENT;
	
	// Bottom sidebar
	
	@ConfigGroup("ui.bottom-sidebar")
	@Comment("emi.config.tooltip.ui.bottom-sidebar-pages")
	@ConfigValue("ui.bottom-sidebar-pages")
	public static SidebarPages bottomSidebarPages = new SidebarPages(List.of(), SidebarSettings.BOTTOM);
	
	@Comment("emi.config.tooltip.ui.bottom-sidebar-subpanels")
	@ConfigValue("ui.bottom-sidebar-subpanels")
	public static SidebarSubpanels bottomSidebarSubpanels = new SidebarSubpanels(List.of(), SidebarSettings.BOTTOM);
	
	@Comment("emi.config.tooltip.ui.bottom-sidebar-size")
	@ConfigValue("ui.bottom-sidebar-size")
	public static IntGroup bottomSidebarSize = new IntGroup("emi.sidebar.size.", List.of("columns", "rows"), IntList.of(9, 9));
	
	@Comment("emi.config.tooltip.ui.bottom-sidebar-margins")
	@ConfigValue("ui.bottom-sidebar-margins")
	public static Margins bottomSidebarMargins = new Margins(2, 2, 2, 2);
	
	@Comment("emi.config.tooltip.ui.bottom-sidebar-align")
	@ConfigValue("ui.bottom-sidebar-align")
	public static ScreenAlign bottomSidebarAlign = new ScreenAlign(ScreenAlign.Horizontal.CENTER, ScreenAlign.Vertical.CENTER);
	
	@Comment("emi.config.tooltip.ui.bottom-sidebar-header")
	@ConfigValue("ui.bottom-sidebar-header")
	public static HeaderType bottomSidebarHeader = HeaderType.VISIBLE;
	
	@ConfigGroupEnd
	@Comment("emi.config.tooltip.ui.bottom-sidebar-theme")
	@ConfigValue("ui.bottom-sidebar-theme")
	public static SidebarTheme bottomSidebarTheme = SidebarTheme.TRANSPARENT;
	
	// Binds
	@Comment("emi.config.tooltip.binds.toggle-visibility")
	@ConfigValue("binds.toggle-visibility")
	public static EmiBind toggleVisibility = new EmiBind("key.emi.toggle_visibility", EmiInput.CONTROL_MASK, GLFW.GLFW_KEY_O);
	
	@Comment("emi.config.tooltip.binds.focus-search")
	@ConfigValue("binds.focus-search")
	public static EmiBind focusSearch = new EmiBind("key.emi.focus_search", EmiInput.CONTROL_MASK, GLFW.GLFW_KEY_F);
	
	@Comment("emi.config.tooltip.binds.clear-search")
	@ConfigValue("binds.clear-search")
	public static EmiBind clearSearch = new EmiBind("key.emi.clear_search", InputUtil.UNKNOWN_KEY.getCode());
	
	@Comment("emi.config.tooltip.binds.view-recipes")
	@ConfigValue("binds.view-recipes")
	public static EmiBind viewRecipes = new EmiBind("key.emi.view_recipes", new EmiBind.ModifiedKey(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_R), 0),
			new EmiBind.ModifiedKey(InputUtil.Type.MOUSE.createFromCode(0), 0));
	
	@Comment("emi.config.tooltip.binds.view-uses")
	@ConfigValue("binds.view-uses")
	public static EmiBind viewUses = new EmiBind("key.emi.view_uses", new EmiBind.ModifiedKey(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_U), 0),
			new EmiBind.ModifiedKey(InputUtil.Type.MOUSE.createFromCode(1), 0));
	
	@Comment("emi.config.tooltip.binds.favorite")
	@ConfigValue("binds.favorite")
	public static EmiBind favorite = new EmiBind("key.emi.favorite", GLFW.GLFW_KEY_A);
	
	@Comment("emi.config.tooltip.binds.default-stack")
	@ConfigValue("binds.default-stack")
	public static EmiBind defaultStack =
			new EmiBind("key.emi.default_stack", new EmiBind.ModifiedKey(InputUtil.Type.MOUSE.createFromCode(0), EmiInput.CONTROL_MASK));
	
	@Comment("emi.config.tooltip.binds.view-stack-tree")
	@ConfigValue("binds.view-stack-tree")
	public static EmiBind viewStackTree = new EmiBind("key.emi.view_stack_tree", InputUtil.UNKNOWN_KEY.getCode());
	
	@Comment("emi.config.tooltip.binds.view-tree")
	@ConfigValue("binds.view-tree")
	public static EmiBind viewTree = new EmiBind("key.emi.view_tree", InputUtil.UNKNOWN_KEY.getCode());
	
	@Comment("emi.config.tooltip.binds.back")
	@ConfigValue("binds.back")
	public static EmiBind back = new EmiBind("key.emi.back", GLFW.GLFW_KEY_BACKSPACE);
	
	@ConfigGroup("binds.crafts")
	@Comment("emi.config.tooltip.binds.craft-one")
	@ConfigValue("binds.craft-one")
	public static EmiBind craftOne = new EmiBind("key.emi.craft_one", new EmiBind.ModifiedKey(InputUtil.Type.MOUSE.createFromCode(0), 0));
	
	@Comment("emi.config.tooltip.binds.craft-all")
	@ConfigValue("binds.craft-all")
	public static EmiBind craftAll = new EmiBind("key.emi.craft_all", new EmiBind.ModifiedKey(InputUtil.Type.MOUSE.createFromCode(0), EmiInput.SHIFT_MASK));
	
	@Comment("emi.config.tooltip.binds.craft-one-to-inventory")
	@ConfigValue("binds.craft-one-to-inventory")
	public static EmiBind craftOneToInventory = new EmiBind("key.emi.craft_one_to_inventory", InputUtil.UNKNOWN_KEY.getCode());
	
	@Comment("emi.config.tooltip.binds.craft-all-to-inventory")
	@ConfigValue("binds.craft-all-to-inventory")
	public static EmiBind craftAllToInventory = new EmiBind("key.emi.craft_all_to_inventory", InputUtil.UNKNOWN_KEY.getCode());
	
	@Comment("emi.config.tooltip.binds.craft-one-to-cursor")
	@ConfigValue("binds.craft-one-to-cursor")
	public static EmiBind craftOneToCursor =
			new EmiBind("key.emi.craft_one_to_cursor", new EmiBind.ModifiedKey(InputUtil.Type.MOUSE.createFromCode(0), EmiInput.CONTROL_MASK));
	
	@ConfigGroupEnd
	@Comment("emi.config.tooltip.binds.show-craft")
	@ConfigValue("binds.show-craft")
	public static EmiBind showCraft = new EmiBind("key.emi.show_craft", GLFW.GLFW_KEY_LEFT_SHIFT);
	
	@ConfigGroup("binds.cheats")
	@Comment("emi.config.tooltip.binds.cheat-one-to-inventory")
	@ConfigValue("binds.cheat-one-to-inventory")
	public static EmiBind cheatOneToInventory =
			new EmiBind("key.emi.cheat_one_to_inventory", new EmiBind.ModifiedKey(InputUtil.Type.MOUSE.createFromCode(1), EmiInput.CONTROL_MASK));
	
	@Comment("emi.config.tooltip.binds.cheat-stack-to-inventory")
	@ConfigValue("binds.cheat-stack-to-inventory")
	public static EmiBind cheatStackToInventory =
			new EmiBind("key.emi.cheat_stack_to_inventory", new EmiBind.ModifiedKey(InputUtil.Type.MOUSE.createFromCode(0), EmiInput.CONTROL_MASK));
	
	@Comment("emi.config.tooltip.binds.cheat-one-to-cursor")
	@ConfigValue("binds.cheat-one-to-cursor")
	public static EmiBind cheatOneToCursor =
			new EmiBind("key.emi.cheat_one_to_cursor", new EmiBind.ModifiedKey(InputUtil.Type.MOUSE.createFromCode(2), EmiInput.CONTROL_MASK));
	
	@Comment("emi.config.tooltip.binds.cheat-stack-to-cursor")
	@ConfigValue("binds.cheat-stack-to-cursor")
	public static EmiBind cheatStackToCursor = new EmiBind("key.emi.cheat_stack_to_cursor", InputUtil.UNKNOWN_KEY.getCode());
	
	@ConfigGroupEnd
	@Comment("emi.config.tooltip.binds.delete-cursor-stack")
	@ConfigValue("binds.delete-cursor-stack")
	public static EmiBind deleteCursorStack = new EmiBind("key.emi.delete_cursor_stack", new EmiBind.ModifiedKey(InputUtil.Type.MOUSE.createFromCode(0), 0));
	
	@Comment("emi.config.tooltip.binds.hide-stack")
	@ConfigValue("binds.hide-stack")
	public static EmiBind hideStack = new EmiBind("key.emi.hide_stack", new EmiBind.ModifiedKey(InputUtil.Type.MOUSE.createFromCode(0), EmiInput.CONTROL_MASK));
	
	@Comment("emi.config.tooltip.binds.hide-stack-by-id")
	@ConfigValue("binds.hide-stack-by-id")
	public static EmiBind hideStackById = new EmiBind("key.emi.hide_stack_by_id",
			new EmiBind.ModifiedKey(InputUtil.Type.MOUSE.createFromCode(0), EmiInput.CONTROL_MASK | EmiInput.SHIFT_MASK));
	
	// Dev
	@Comment("emi.config.tooltip.dev.dev-mode")
	@ConfigValue("dev.dev-mode")
	public static boolean devMode = false;
	
	@Comment("emi.config.tooltip.dev.edit-mode")
	@ConfigValue("dev.edit-mode")
	public static boolean editMode = false;
	
	@Comment("emi.config.tooltip.dev.log-untranslated-tags")
	@ConfigValue("dev.log-untranslated-tags")
	public static boolean logUntranslatedTags = false;
	
	@Comment("emi.config.tooltip.dev.log-non-tag-ingredients")
	@ConfigValue("dev.log-non-tag-ingredients")
	public static boolean logNonTagIngredients = false;
	
	@Comment("emi.config.tooltip.dev.show-recipe-ids")
	@ConfigValue("dev.show-recipe-ids")
	public static boolean showRecipeIds = false;
	
	@Comment("emi.config.tooltip.dev.highlight-defaulted")
	@ConfigValue("dev.highlight-defaulted")
	public static boolean highlightDefaulted = false;
	
	@Comment("emi.config.tooltip.dev.highlight-exclusion-areas")
	@ConfigValue("dev.highlight-exclusion-areas")
	public static boolean highlightExclusionAreas = false;

	//Distraction Free Recipes

	@ConfigGroup("addon.distraction")
	@Comment("emi.config.tooltip.addon.distraction.enable-distraction-free-mode")
	@ConfigValue("addon.distraction.enable-distraction-free-mode")
	public static boolean enableDistractionFreeMode = false;

	@Comment("emi.config.tooltip.addon.distraction.lower_opacity")
	@ConfigValue("addon.distraction.lower_opacity")
	@ConfigGroupEnd()
	public static boolean lowerOpacity = false;
	
	// Persistent (currently empty)
	
	public static void loadConfig() {
		try {
			File global = new File(getGlobalFolder(), "global.css");
			if (global.exists() && global.isFile()) {
				QDCSS css = QDCSS.load(global);
				if (css.containsKey("global.use-global") && css.get("global.use-global").get().equals("true")) {
					useGlobalConfig = true;
				}
			}
			File config = getConfigFile();
			if (config.exists() && config.isFile()) {
				QDCSS css = QDCSS.load(config);
				loadConfig(css);
			}
			if (startupConfig == null) {
				startupConfig = getSavedConfig();
			}
			writeConfig();
		}
		catch (Exception e) {
			EmiLog.error("Error reading config");
			e.printStackTrace();
		}
	}
	
	public static void setGlobalState(boolean useGlobal) {
		try {
			File folder = getGlobalFolder();
			folder.mkdirs();
			File global = new File(folder, "global.css");
			FileWriter writer = new FileWriter(global);
			writer.write("#global {\n\tuse-global: " + useGlobal + ";\n}\n");
			writer.close();
			useGlobalConfig = useGlobal;
			File emi = getConfigFile();
			if (!emi.exists()) {
				emi.createNewFile();
				writeConfig();
			}
			else {
				loadConfig();
			}
		}
		catch (Exception e) {
			EmiLog.error("Error writing global config");
			e.printStackTrace();
		}
	}
	
	public static void loadConfig(QDCSS css) {
		try {
			Set<String> consumed = Sets.newHashSet();
			for (Field field : EmiConfig.class.getFields()) {
				ConfigValue annot = field.getAnnotation(ConfigValue.class);
				if (annot != null) {
					if (css.containsKey(annot.value())) {
						consumed.add(annot.value());
						assignField(css, annot.value(), field);
					}
				}
			}
			for (String key : css.keySet()) {
				if (!consumed.contains(key)) {
					unparsed.put(key, css.getAll(key));
				}
			}
		}
		catch (Exception e) {
			EmiLog.error("Error reading config");
			e.printStackTrace();
		}
	}
	
	public static void writeConfig() {
		try {
			FileWriter writer = new FileWriter(getConfigFile());
			writer.write(getSavedConfig());
			writer.close();
		}
		catch (Exception e) {
			EmiLog.error("Error writing config");
			e.printStackTrace();
		}
	}
	
	public static String getSavedConfig() {
		Map<String, List<String>> unparsed = Maps.newLinkedHashMap();
		for (Field field : EmiConfig.class.getFields()) {
			ConfigValue annot = field.getAnnotation(ConfigValue.class);
			if (annot != null) {
				String[] parts = annot.value().split("\\.");
				String group = parts[0];
				String key = parts[1];
				Comment comment = field.getAnnotation(Comment.class);
				String commentText = "";
				if (comment != null) {
					commentText += "\t/**\n";
					for (String line : RetroEMI.wrapLines(comment.value(), 80)) {
						commentText += "\t * ";
						commentText += line;
						commentText += "\n";
					}
					commentText += "\t */\n";
				}
				String text = commentText;
				try {
					text += writeField(key, field);
				}
				catch (Exception e) {
					EmiLog.error("Error serializing config");
					e.printStackTrace();
				}
				unparsed.computeIfAbsent(group, g -> Lists.newArrayList()).add(text);
			}
		}
		for (Map.Entry<String, List<String>> entry : EmiConfig.unparsed.entrySet()) {
			String[] parts = entry.getKey().split("\\.");
			String group = parts[0];
			String key = parts[1];
			for (String value : entry.getValue()) {
				unparsed.computeIfAbsent(group, g -> Lists.newArrayList()).add("\t/** unparsed */\n\t" + key + ": " + value + ";\n");
			}
		}
		String ret = "";
		ret += "/** EMI Config */\n\n";
		boolean firstCategory = true;
		for (Map.Entry<String, List<String>> category : unparsed.entrySet()) {
			if (!firstCategory) {
				ret += "\n";
			}
			firstCategory = false;
			
			ret += "#" + category.getKey() + " {\n";
			ret += RetroEMI.join(category.getValue(), "\n");
			ret += "}\n";
		}
		return ret;
	}
	
	private static File getConfigFile() {
		String s = System.getProperty("emi.config");
		if (s != null) {
			File f = new File(s);
			if (f.exists() && f.isFile()) {
				return f;
			}
			EmiLog.error("System property 'emi.config' set to '" + s + "' but does not point to real file, using default config.");
		}
		if (useGlobalConfig) {
			return new File(getGlobalFolder(), "emi.css");
		}
		return new File(EmiAgnos.getConfigDirectory().toFile(), "emi.css");
	}
	
	private static File getGlobalFolder() {
		String s = switch (Util.getOSType()) {
			case WINDOWS -> System.getenv("APPDATA") + "/.minecraft";
			case MACOS -> System.getProperty("user.home") + "/Library/Application Support/minecraft";
			default -> System.getProperty("user.home") + "/.minecraft";
		};
		
		return new File(s + "/global/emi");
	}
	
	private static void assignField(QDCSS css, String annot, Field field) throws IllegalAccessException {
		Class<?> type = field.getType();
		Setter setter = SETTERS.get(type);
		if (setter != null) {
			setter.setValue(css, annot, field);
		}
		else if (ConfigEnum.class.isAssignableFrom(type)) {
			SETTERS.get(ConfigEnum.class).setValue(css, annot, field);
		}
		else {
			throw new RuntimeException("[emi] Unknown parsing type: " + type);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static String writeField(String key, Field field) throws IllegalAccessException {
		String text = "";
		Class<?> type = field.getType();
		if (MULTI_WRITERS.containsKey(type)) {
			for (String line : ((MultiWriter<Object>) MULTI_WRITERS.get(type)).writeValue(field.get(null))) {
				text += "\t" + key + ": " + line + ";\n";
			}
		}
		else if (WRITERS.containsKey(type)) {
			text += "\t" + key + ": " + ((Writer<Object>) WRITERS.get(type)).writeValue(field.get(null)) + ";\n";
		}
		else if (ConfigEnum.class.isAssignableFrom(type)) {
			text += "\t" + key + ": " + ((Writer<Object>) WRITERS.get(ConfigEnum.class)).writeValue(field.get(null)) + ";\n";
		}
		return text;
	}
	
	private static void defineType(Class<?> clazz, Setter setter, Writer<?> writer) {
		SETTERS.put(clazz, setter);
		WRITERS.put(clazz, writer);
	}
	
	private static void defineType(Class<?> clazz, Setter setter) {
		defineType(clazz, setter, field -> field.toString());
	}
	
	private static void defineMultiType(Class<?> clazz, Setter setter, MultiWriter<?> writer) {
		SETTERS.put(clazz, setter);
		MULTI_WRITERS.put(clazz, writer);
	}
	
	static {
		defineType(boolean.class, (css, annot, field) -> field.setBoolean(null, css.getBoolean(annot).get()));
		defineType(int.class, (css, annot, field) -> field.setInt(null, css.getInt(annot).get()));
		defineType(double.class, (css, annot, field) -> field.setDouble(null, css.getDouble(annot).get()));
		defineType(String.class, (css, annot, field) -> {
			String s = css.get(annot).get();
			// Nil's QDCSS handles quoted strings on its own
			//				s = s.substring(1, s.length() - 1);
			field.set(null, s);
		}, (String field) -> "\"" + field + "\"");
		defineMultiType(EmiBind.class, (css, annot, field) -> {
			List<String> strings = Lists.newArrayList(css.getAll(annot));
			for (int i = 0; i < strings.size(); i++) {
				String s = strings.get(i);
				strings.set(i, s);
			}
			((EmiBind) field.get(null)).setKey(strings);
		}, (EmiBind field) -> {
			List<String> list = Lists.newArrayList();
			for (EmiBind.ModifiedKey key : field.boundKeys) {
				if (!key.isUnbound() || field.boundKeys.size() == 1) {
					list.add("\"" + key.toName() + "\"");
				}
			}
			return list;
		});
		defineType(ScreenAlign.class, (css, annot, field) -> {
			String[] parts = css.get(annot).get().split(",");
			if (parts.length == 2) {
				((ScreenAlign) field.get(null)).horizontal = ScreenAlign.Horizontal.fromName(parts[0].trim());
				((ScreenAlign) field.get(null)).vertical = ScreenAlign.Vertical.fromName(parts[1].trim());
			}
			else {
				((ScreenAlign) field.get(null)).horizontal = ScreenAlign.Horizontal.CENTER;
				((ScreenAlign) field.get(null)).vertical = ScreenAlign.Vertical.CENTER;
			}
		}, (ScreenAlign field) -> field.horizontal.getName() + ", " + field.vertical.getName());
		defineType(SidebarPages.class, (css, annot, field) -> {
			String[] parts = css.get(annot).get().split(",");
			SidebarPages pages = (SidebarPages) field.get(null);
			pages.pages.clear();
			for (String s : parts) {
				pages.pages.add(new SidebarPages.SidebarPage(SidebarType.fromName(s.trim().toLowerCase())));
			}
			pages.unique();
		}, (SidebarPages field) -> {
			if (field.pages.isEmpty()) {
				return "none";
			}
			else {
				return field.pages.stream().map(p -> p.type.getName()).collect(Collectors.joining(", "));
			}
		});
		defineType(SidebarSubpanels.class, (css, annot, field) -> {
			String[] parts = css.get(annot).get().split(",");
			SidebarSubpanels subpanels = (SidebarSubpanels) field.get(null);
			subpanels.subpanels.clear();
			for (String s : parts) {
				String[] subparts = s.trim().split("\\s+");
				SidebarType type = SidebarType.fromName(subparts[0].toLowerCase());
				int rows = subparts.length > 1 ? Integer.parseInt(subparts[1]) : 1;
				if (rows >= 1) {
					subpanels.subpanels.add(new SidebarSubpanels.Subpanel(type, rows));
				}
			}
			subpanels.unique();
		}, (SidebarSubpanels field) -> {
			if (field.subpanels.isEmpty()) {
				return "none";
			}
			else {
				return field.subpanels.stream().map(p -> p.type.getName() + " " + p.rows).collect(Collectors.joining(", "));
			}
		});
		defineType(IntGroup.class, (css, annot, field) -> {
			((IntGroup) field.get(null)).deserialize(css.get(annot).get());
		}, (IntGroup group) -> group.serialize());
		defineType(Margins.class, (css, annot, field) -> {
			((Margins) field.get(null)).deserialize(css.get(annot).get());
		}, (Margins group) -> group.serialize());
		defineType(ConfigEnum.class, (css, annot, field) -> {
			String name = css.get(annot).get();
			for (ConfigEnum e : (ConfigEnum[]) field.getType().getEnumConstants()) {
				if (e.getName().equals(name)) {
					field.set(null, e);
					break;
				}
			}
		}, (ConfigEnum c) -> c.getName());
		try {
			for (Field field : EmiConfig.class.getDeclaredFields()) {
				ConfigFilter annot = field.getAnnotation(ConfigFilter.class);
				if (annot != null) {
					Predicate<?> predicate = (Predicate<?>) field.get(null);
					FILTERS.put(annot.value(), predicate);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		DEFAULT_CONFIG = getSavedConfig();
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ConfigValue {
		public String value();
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ConfigFilter {
		public String value();
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Comment {
		public String value();
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ConfigGroup {
		public String value();
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ConfigGroupEnd {
	}
	
	private static interface Setter {
		void setValue(QDCSS css, String annot, Field field) throws IllegalAccessException;
	}
	
	private static interface Writer<T> {
		String writeValue(T value);
	}
	
	private static interface MultiWriter<T> {
		List<String> writeValue(T value);
	}
}
