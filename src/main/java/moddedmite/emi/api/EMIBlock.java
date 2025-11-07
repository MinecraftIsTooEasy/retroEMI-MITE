package moddedmite.emi.api;

import net.minecraft.Block;

public interface EMIBlock {
    @Deprecated(since = "1.1.24")
    default Block hideFromEMI() {
        return null;
    }

    @Deprecated(since = "1.1.24")
    default Block hideFromEMI(int metadata) {
        return null;
    }
}
