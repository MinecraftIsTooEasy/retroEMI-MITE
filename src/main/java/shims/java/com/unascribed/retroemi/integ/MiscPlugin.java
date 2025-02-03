package shims.java.com.unascribed.retroemi.integ;

import net.minecraft.Block;
import net.minecraft.Item;
import net.minecraft.ResourceLocation;
import shims.java.com.unascribed.retroemi.NamedEmiPlugin;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.config.FluidUnit;
import shims.java.net.minecraft.text.Text;

public class MiscPlugin implements NamedEmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        EmiStack water = EmiStack.of(Block.waterStill, FluidUnit.BUCKET);
        EmiStack lava = EmiStack.of(Block.lavaStill, FluidUnit.BUCKET);
        EmiStack waterCatalyst = water.copy().setRemainder(water);
        EmiStack lavaCatalyst = lava.copy().setRemainder(lava);

        registry.addRecipe(EmiWorldInteractionRecipe.builder()
                .id(new ResourceLocation("emi", "/world/fluid_interaction/minecraft/obsidian_glitch"))
                .leftInput(waterCatalyst)
                .rightInput(EmiStack.of(Item.redstone), false, (sw) -> {
                    sw.appendTooltip(Text.literal(String.valueOf('\u00a7') + "6Build a cobblestone generator, and put redstone dust where the cobblestone would generate."));
                    return sw;
                })
                .rightInput(lavaCatalyst, false)
                .output(EmiStack.of(Block.obsidian))
                .supportsRecipeTree(true)
                .build());
    }

    @Override
    public String getName() {
        return "retro";
    }

}
