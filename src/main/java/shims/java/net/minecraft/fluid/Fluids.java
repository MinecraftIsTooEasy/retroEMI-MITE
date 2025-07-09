package shims.java.net.minecraft.fluid;

import dev.emi.emi.Prototype;
import net.minecraft.Block;
import net.minecraft.Item;

public class Fluids {

    public static final Fluid EMPTY = new Fluid(Prototype.EMPTY);

    public static final Fluid WATER = new Fluid(new Prototype(Item.getItem(Block.waterStill)));
    public static final Fluid LAVA = new Fluid(new Prototype(Item.getItem(Block.lavaStill)));

}
