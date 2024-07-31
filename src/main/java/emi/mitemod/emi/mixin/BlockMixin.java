package emi.mitemod.emi.mixin;

import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.data.EmiRemoveFromIndex;
import emi.mitemod.emi.api.EMIBlock;
import net.fabricmc.api.EnvType;
import net.minecraft.Block;
import net.minecraft.ItemStack;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin implements EMIBlock {
    @Shadow @Final public int blockID;

    @Override
    public Block hideFromEMI() {
        if (FishModLoader.getEnvironmentType().equals(EnvType.CLIENT)) {
            for (int i = 0; i < 16; i++) {
                EmiRemoveFromIndex.removed.add(EmiStack.of(new ItemStack((Block) ReflectHelper.dyCast(this), 1, i)));
            }
        }
        return ReflectHelper.dyCast(this);
    }

    @Override
    public Block hideFromEMI(int metadata) {
        if (FishModLoader.getEnvironmentType().equals(EnvType.CLIENT)) {
            EmiRemoveFromIndex.removed.add(EmiStack.of(new ItemStack((Block) ReflectHelper.dyCast(this), 1, metadata)));
        }
        return ReflectHelper.dyCast(this);
    }

    @Inject(method = "<clinit>", at = @At(value = "TAIL"))
    private static void addBlockHideFromEMI(CallbackInfo callback) {
//        ((EMIBlock) Block.waterMoving).hideFromEMI();
//        ((EMIBlock) Block.waterStill).hideFromEMI();
//        ((EMIBlock) Block.lavaMoving).hideFromEMI();
//        ((EMIBlock) Block.lavaStill).hideFromEMI();
//        ((EMIBlock) Block.pistonExtension).hideFromEMI();
//        ((EMIBlock) Block.pistonMoving).hideFromEMI();
//        ((EMIBlock) Block.stoneDoubleSlab).hideFromEMI();
//        ((EMIBlock) (Block) Block.bed).hideFromEMI();
//        ((EMIBlock) Block.web).hideFromEMI();
//        ((EMIBlock) Block.mushroomBrown).hideFromEMI();
//        ((EMIBlock) Block.mushroomRed).hideFromEMI();
//        ((EMIBlock) Block.mobSpawner).hideFromEMI();
//        ((EMIBlock) Block.chest).hideFromEMI();
//        ((EMIBlock) (Block) Block.redstoneWire).hideFromEMI();
//        ((EMIBlock) Block.crops).hideFromEMI();
//        ((EMIBlock) Block.tilledField).hideFromEMI();
//        ((EMIBlock) Block.furnaceBurning).hideFromEMI();
//        ((EMIBlock) Block.signPost).hideFromEMI();
//        ((EMIBlock) Block.doorWood).hideFromEMI();
//        ((EMIBlock) Block.signWall).hideFromEMI();
//        ((EMIBlock) Block.doorIron).hideFromEMI();
//        ((EMIBlock) Block.oreRedstoneGlowing).hideFromEMI();
//        ((EMIBlock) Block.torchRedstoneIdle).hideFromEMI();
//        ((EMIBlock) Block.reed).hideFromEMI();
//        ((EMIBlock) Block.cake).hideFromEMI();
//        ((EMIBlock) Block.redstoneRepeaterIdle).hideFromEMI();
//        ((EMIBlock) Block.redstoneRepeaterActive).hideFromEMI();
//        ((EMIBlock) Block.pumpkinStem).hideFromEMI();
//        ((EMIBlock) Block.melonStem).hideFromEMI();
//        ((EMIBlock) Block.mushroomCapBrown).hideFromEMI();
//        ((EMIBlock) Block.mushroomCapRed).hideFromEMI();
//        ((EMIBlock) Block.endPortal).hideFromEMI();
//        ((EMIBlock) Block.enchantmentTable).hideFromEMI();
//        ((EMIBlock) Block.brewingStand).hideFromEMI();
//        ((EMIBlock) Block.cauldron).hideFromEMI();
//        ((EMIBlock) Block.redstoneLampIdle).hideFromEMI();
//        ((EMIBlock) Block.redstoneLampActive).hideFromEMI();
//        ((EMIBlock) Block.woodDoubleSlab).hideFromEMI();
//        ((EMIBlock) Block.carrot).hideFromEMI();
//        ((EMIBlock) Block.potato).hideFromEMI();
//        ((EMIBlock) Block.skull).hideFromEMI();
//        ((EMIBlock) Block.redstoneComparatorActive).hideFromEMI();
//        ((EMIBlock) Block.furnaceClayBurning).hideFromEMI();
//        ((EMIBlock) Block.furnaceSandstoneBurning).hideFromEMI();
//        ((EMIBlock) Block.furnaceObsidianBurning).hideFromEMI();
//        ((EMIBlock) Block.furnaceNetherrackBurning).hideFromEMI();
//        ((EMIBlock) Block.obsidianDoubleSlab).hideFromEMI();
//        ((EMIBlock) Block.onions).hideFromEMI();
//        ((EMIBlock) Block.cropsDead).hideFromEMI();
//        ((EMIBlock) Block.carrotDead).hideFromEMI();
//        ((EMIBlock) Block.potatoDead).hideFromEMI();
//        ((EMIBlock) Block.onionsDead).hideFromEMI();
//        ((EMIBlock) Block.flowerPotMulti).hideFromEMI();
//        ((EMIBlock) (Block) Block.bush).hideFromEMI();
//        ((EMIBlock) Block.furnaceHardenedClayBurning).hideFromEMI();
//        ((EMIBlock) Block.netherStalk).hideFromEMI();
//        ((EMIBlock) Block.tripWire).hideFromEMI();
//        ((EMIBlock) Block.cocoaPlant).hideFromEMI();
//        ((EMIBlock) Block.flowerPot).hideFromEMI();
//        ((EMIBlock) Block.redstoneComparatorIdle).hideFromEMI();
//        ((EMIBlock) Block.doorCopper).hideFromEMI();
//        ((EMIBlock) Block.doorSilver).hideFromEMI();
//        ((EMIBlock) Block.doorGold).hideFromEMI();
//        ((EMIBlock) Block.doorAncientMetal).hideFromEMI();
//        ((EMIBlock) Block.doorMithril).hideFromEMI();
//        ((EMIBlock) Block.doorAdamantium).hideFromEMI();
//        ((EMIBlock) Block.spark).hideFromEMI();
//        ((EMIBlock) Block.portal).hideFromEMI();
//        ((EMIBlock) Block.portal).hideFromEMI();
//        ((EMIBlock) Block.portal).hideFromEMI();

    }
}
