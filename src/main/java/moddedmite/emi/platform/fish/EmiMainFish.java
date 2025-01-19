package moddedmite.emi.platform.fish;

import dev.emi.emi.platform.EmiMain;
import net.fabricmc.api.ModInitializer;
import net.xiaoyu233.fml.ModResourceManager;

public class EmiMainFish implements ModInitializer {

    @Override
    public void onInitialize() {
        EmiMain.init();
        ModResourceManager.addResourcePackDomain("emi");
    }
}