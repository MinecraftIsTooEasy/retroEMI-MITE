package moddedmite.emi.mixin.block;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.data.EmiRemoveFromIndex;
import moddedmite.emi.api.EMIBlock;
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
}
