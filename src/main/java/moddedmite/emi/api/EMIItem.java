package moddedmite.emi.api;

import net.minecraft.Item;
import net.minecraft.ItemStack;

public interface EMIItem {
    @Deprecated(since = "1.1.24")
    default Item hideFromEMI() {
        return null;
    }
}
