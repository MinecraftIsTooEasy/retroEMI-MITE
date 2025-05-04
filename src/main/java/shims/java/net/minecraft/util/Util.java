package shims.java.net.minecraft.util;

import dev.emi.emi.runtime.EmiLog;
import net.xiaoyu233.fml.FishModLoader;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class Util {
    private static final ExecutorService IO_WORKER_EXECUTOR = createIoWorker("IO-Worker-", false);

    public static ExecutorService getIoWorkerExecutor() {
        return IO_WORKER_EXECUTOR;
    }

    private static ExecutorService createIoWorker(String namePrefix, boolean daemon) {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        return Executors.newCachedThreadPool((runnable) -> {
            Thread thread = new Thread(runnable);
            thread.setName(namePrefix + atomicInteger.getAndIncrement());
            thread.setDaemon(daemon);
            thread.setUncaughtExceptionHandler(Util::uncaughtExceptionHandler);
            return thread;
        });
    }

    private static void uncaughtExceptionHandler(Thread thread, Throwable t) {
        throwOrPause(t);
        if (t instanceof CompletionException) {
            t = t.getCause();
        }

        if (t instanceof CrashException crashException) {
            System.out.println(crashException.getReport().getDescription());
            System.exit(-1);
        }

        EmiLog.error(String.format(Locale.ROOT, "Caught exception in thread %s", thread));
        EmiLog.error(t.getMessage());
    }

    public static <T extends Throwable> T throwOrPause(T t) {
        if (FishModLoader.isDevelopmentEnvironment()) {
            EmiLog.error("Trying to throw a fatal exception, pausing in IDE");
            EmiLog.error(t.getMessage());
            pause(t.getMessage());
        }

        return t;
    }

    private static void pause(String message) {
        Instant instant = Instant.now();
        EmiLog.warn("Did you remember to set a breakpoint here?");
        boolean bl = Duration.between(instant, Instant.now()).toMillis() > 500L;
        if (!bl) {
            EmiLog.debug(message);
        }

    }
}
