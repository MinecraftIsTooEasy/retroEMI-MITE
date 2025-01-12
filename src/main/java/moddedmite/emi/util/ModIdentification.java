package moddedmite.emi.util;

import net.minecraft.ItemStack;
import net.xiaoyu233.fml.api.block.IBlock;
import net.xiaoyu233.fml.api.item.IItem;

public class ModIdentification {
    public static String getMod(ItemStack itemStack) {
        int id = itemStack.itemID;
        if (itemStack.isBlock()) {
            if (isMITEBlock(id)) {
                return "§9§oMITE";
            } else if (isMinecraftBlock(id)) {
                return "§9§oMinecraft";
            }
            return "§9§o" + (((IBlock) itemStack.getItemAsBlock().getBlock())).getNamespace();
        } else {
            if (isMITEItem(id)) {
                return "§9§oMITE";
            } else if (isMinecraftItem(id)) {
                return "§9§oMinecraft";
            } else {
                return "§9§o" + ((IItem) itemStack.getItem()).getNamespace();
            }
        }
    }

    private static boolean isMITEBlock(int id) {
        if (id >= 198 && id < 256 || id == 95)
            return true;
        return id >= 164 && id < 170;
    }

    private static boolean isMinecraftBlock(int id) {
        if (id >= 170 && id <= 174)
            return true;
        return id <= 163;
    }

    private static boolean isMITEItem(int id) {
        if (id > 955 && id < 1283) {
            if (!(id >= 1058 && id <= 1066))
                return true;
            if (!(id >= 1135 && id <= 1141))
                return true;
            if (!(id >= 1168 && id <= 1171))
                return true;
            if (!(id >= 1265 && id <= 1275))
                return true;
            if (id != 1116 && id != 1026 && id != 1027)
                return true;
        }
        return id >= 2276 && id <= 2279;
    }

    private static boolean isMinecraftItem(int id) {
        if (id > 256 && id <= 422) {
            if (!(id > 269 && id < 280))
                return true;
            if (!(id > 309 && id < 314))
                return true;
            if (!(id > 408 && id < 417))
                return true;
            if (id != 262 && id != 268 && id != 290 && id != 291 && id != 293 && id != 419)
                return true;
        }
        return id >= 2256 && id <= 2267;
    }
}