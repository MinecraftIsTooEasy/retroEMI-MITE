package shims.java.net.minecraft.fluid;
import dev.emi.emi.Prototype;
import net.minecraft.Block;
import net.minecraft.ItemStack;

public class Fluid {

    private final Prototype proto;

    Fluid(Prototype proto) {
        this.proto = proto;
    }

    public int getId() {
        return proto.item().itemID;
    }

    public Prototype getPrototype() {
        return proto;
    }

    public static Fluid of(Prototype proto) {
        int id = proto.item() == null ? 0 : proto.item().itemID;
        if (id == 0) return Fluids.EMPTY;
        if (id == Block.waterStill.blockID) return Fluids.WATER;
        if (id == Block.waterMoving.blockID) return Fluids.WATER;
        if (id == Block.lavaStill.blockID) return Fluids.LAVA;
        if (id == Block.lavaMoving.blockID) return Fluids.LAVA;
        return new Fluid(proto);
    }

    public static Fluid of(ItemStack is) {
        return of(Prototype.of(is));
    }

//    public static Fluid of(LiquidStack ls) {
//        return of(ls.asItemStack());
//    }

    public static Fluid of(Block b) {
        return of(new ItemStack(b));
    }

    @Override
    public int hashCode() {
        return 31*proto.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Fluid that = (Fluid) obj;
        return this.proto.equals(that.proto);
    }

}
