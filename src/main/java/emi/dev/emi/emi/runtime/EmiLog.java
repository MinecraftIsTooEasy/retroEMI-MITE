package emi.dev.emi.emi.runtime;

import net.fabricmc.loader.api.FabricLoader;

import java.util.logging.Logger;

public class EmiLog {
	
	public static final Logger logger = Logger.getLogger("EMI");
	
	public static void info(String str) {
		logger.info("EMI: " + str);
	}
	
	public static void warn(String str) {
		logger.warning("EMI: " + str);
	}
	
	public static void error(String str) {
		logger.severe("EMI: " + str);
	}
	
	public static void debug(String str) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			logger.info("EMI: " + str);
		}
	}
}
