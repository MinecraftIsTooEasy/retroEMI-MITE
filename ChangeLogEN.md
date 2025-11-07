# Ease Minecraft Is: Too Easy

---

## v1.1.24
* Added compost tab, showing detailed information about insect conversion of food into fertilizer (jeffyjamzhd)
* Optimized crafting tab MITE crafting information display (jeffyjamzhd)
  + Changed to tooltip form
  + Supports more detailed display (crafting progress preview, craftable anywhere, etc.)
  + More accurate crafting time (changes with player level)
  + Removed `Show MITE Crafting Info` config option
* Optimized food tab, improved visual display and supports tooltip for detailed values
* Optimized enchantment tab
* Fixed English translations in info interface (vb1dra)
* Code optimizations

---

## v1.1.23
* Now requires `Rusted Iron Core` as prerequisite mod
* Re-added `Item Index Source` config option (no practical effect since creative inventory sorting matches item registration order)
* Changed crafting tab icon to iron workbench
* Optimized MITE crafting info, moved crafting level to crafting page
* Improved status position related content
  * Note: When rendering at top, MITE special effects code is poor - MITE special effects won't render tooltips
* Partial code optimization
* Improved font-related code (previous implementation was poorly written)
* Fixed font color darkening during rain
* Fixed incorrect hover color in recipe tree interface recipe types
* Fixed incorrect rendering color in chess tab
* Fixed missing icons for enchantment types in recipe book
* Fixed some missing translations
* Fixed duplicate ID errors still reporting in non-development mode

---

## v1.1.22
* Rewrote recipe screenshots
* Changed tab icons to name tags
* Optimized button rendering implementation, now closer to 1.21
* Added `Show MITE Crafting Info` config option to control crafting difficulty and time display
* Slightly optimized config interface
* Fixed double text display in ManyLib settings when loaded with Better Game Setting
* Fixed abnormal recipe screenshot issues
* Fixed abnormal coloring of some buttons
### Synchronized with EMI 1.1.22 updates:
### New Features
* List-type materials can now be serialized (and can be forcibly converted to other materials)
### Adjustments
* Recipe trees now handle catalyst materials more intelligently, no longer requesting redundant tag materials
* Empty/full state search focus no longer triggers with random input
### Bug Fixes
* Fixed crashes when filling maximum stack counts during recipe filling #915

---

## v1.1.21
* Pinyin search now only works when game language is Simplified Chinese
* Fixed abnormal English search results
### Synchronized with EMI 1.1.21 updates:
### Adjustments
* Large item quantities in EMI interface now display with separators for better readability
* Crafting favorite large numbers no longer overlap, using tiny font and approximate values
### Bug Fixes
* Fixed crafting favorite number overlap issue #721
* Fixed crafting favorite counter overlap issue #905
* Fixed recipe filling not respecting item maximum stack counts #906

---

## v1.1.20-patch
* Modified some Chinese translations to align with translations merged into original branch
* Fixed conflicts with Better Game Settings 1.2.0
* Fixed incorrect color of some buttons

---

## v1.1.20
* Now requires fishmodloader v3.4.0
* Mod resources moved to `/assets/emi/`
* Removed duplicate recipes in MITE
* Added partial support for es_es, fi_fi, fr_fr, ja_jp, pt_br, ru_ru, tr_tr languages (json format), with `\n` newline support
* Added partial Turkish language support (lang format, from BTW Community)
* Added tag translations
* Improved recipe screenshots
* Added default recipes for most items (enabling direct use of recipe trees)
* Removed Enable Non-Distracting Recipes config option
* Re-added Efficient Work config preset
* Added copy recipe ID keybind (EMI 1.0.28)
* Significant performance improvement for craftable recipes in rare cases (EMI 1.0.28)
* Code optimization
* Fixed modified numeric config options reverting to defaults after game launch
* Fixed config file comments displaying as key names
* Fixed forward hotkey not working
* Fixed various other miscellaneous issues
### Synchronized with EMI 1.1.20 updates:
### New Content
* Added "Empty" sidebar type
* Added "Low Distraction" config preset, typically using empty sidebar but item index sidebar during search
* Sidebar and tooltip rendering now more error-tolerant, showing error messages instead of crashing
### Adjustments
* More consistent error handling in EMI logging
* EMI avoids loading on non-real screens
* Significant speed improvements for some stack addition routines
### API
* Added experimental API allowing alias registration via code without resource packs

---

## v1.1.19
* Optimized some code
* Added addon config to config interface
* Removed unused material color codes
* Synchronized EMI 1.1.19 changes:
  * Configurable search sidebar targets
  * Significant performance improvements for material structures
  * Improved EMI reload error accuracy
* Ported and built-in Distraction Free Recipes (EMI) mod
* Fixed incorrect title text color in config interface
* Fixed compatibility issues with standard version
* Fixed left-side buttons in config interface not changing color on mouse hover
* Fixed recipe ID conflicts for 2 fish types

---

## v1.1.18
* Added MITE furnaces to smelting workstations
* EMI config button and recipe tree button can now be hidden (from EMI 1.1.18)
* EMI reload now delays some non-essential tasks like recipe baking, speeding up reload and making EMI 10x faster in extreme cases (from EMI 1.0.30)
* Automatically hides mod name when MITE-Better Tips mod is loaded simultaneously
  * Major updates completed, version numbers synchronized

---

## v1.0.8
* Re-added feature showing smelting experience for smelting recipes
* Fixed search abnormalities when PinIn-Lib not installed

---

## v1.0.7
* No longer requires PinIn-Lib as prerequisite
* Re-added world interaction tab
* Anvil repair tab renamed to Repair
* Improved repair tab and added Bottle O' Enchanting recipe
* Fixed GUI and Tooltip darkening due to rain and inventory slots containing blocks
* Fixed enchantment books not showing enchantments in anvil repair tab
* Fixed EMI search triggering creative inventory search simultaneously
* Modified paths for some classes

---

## v1.0.6
* Added ModMenu adaptation
* Modified mod main class
* Modified config file path

---

## v1.0.5
* Added enchantment tab
* Optimized page number display, now consistent with higher version EMI
* Added obsidian workbench to work blocks
* Fixed incorrect shadow when picking up items in inventory

---

## v1.0.4
* Modified software package
* Re-added anvil repair tab (to be improved)
* Adjusted crafting difficulty and time display
* Fixed abnormal smelting time display
* Fixed diamond enchantment table not showing
* Fixed enchantment books not showing attributes
* Fixed crashes when inputting characters while highlighting inventory items with pinyin search enabled

---

## v1.0.3
* Completed localization, liquid capacity now displays correctly in Chinese
* Added info tab, currently showing gravel item drop rates and enchantment book descriptions
* Improved food tab, now showing saturation, vegetable nutrition, protein, and sugar
* Added crafting difficulty and time display
* Fixed missing Comments
* Added another small easter egg

---

## v1.0.2
* In-game config Comments now support Chinese
* Added crafting level display
* Added pinyin search and its config option
* Requires PinIn-Lib 1.6.0 or higher as prerequisite mod
* Fixed invalid subtype for favorited items
* Fixed abnormal scrolling issues
* Fixed search box and button misalignment due to resolution changes

---

## v1.0.1
* Added food tab
* Fixed abnormal search box input issues
* Fixed hidden spawners and chests
* Fixed config file not generating on server
* Fixed EMI being ineffective on server
* Now requires GSON-2.10.1 or higher dependency library

---

## v1.0.0
* Ported from Better Than Wolves Community Edition's retroEMI
  * Original branch from rewindmc's retroEMI
* Added Chinese input support
* Added localization
* Added a small easter egg
* Requires Fast Util dependency library