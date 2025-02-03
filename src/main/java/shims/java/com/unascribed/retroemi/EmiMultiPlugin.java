package shims.java.com.unascribed.retroemi;

import java.util.stream.Stream;

import dev.emi.emi.api.EmiPlugin;

public interface EmiMultiPlugin {

    Stream<EmiPlugin> getChildPlugins();

}
