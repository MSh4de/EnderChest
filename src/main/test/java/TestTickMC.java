import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestTickMC implements Runnable{

    private static final long SLEEP_PRECISION = TimeUnit.MILLISECONDS.toNanos(2);
    private Logger logger = LoggerFactory.getLogger(TestTickMC.class);
    public TestTickMC() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(this);
    }

    public static void main(String[] args) {
        new TestTickMC();
    }

    @Override
    public void run() {
        long lastTick = System.nanoTime();
        long catchupTime = 0L;
        long tick = 0L;
        while (true) {
            final long curTime = System.nanoTime();
            final long wait = 50000000L - (curTime - lastTick) - catchupTime;
            if (wait > 0L) {
                try {
                    sleepNanos(wait / 1000000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                catchupTime = 0L;
            }
            else {
                catchupTime = Math.min(1000000000L, Math.abs(wait));

                lastTick = curTime;
                if (++tick % 20L == 0L) {
                    logger.info("ticks");
                }
            }
        }
    }
    private void sleepNanos(long nanoDuration) throws InterruptedException {
        final long end = System.nanoTime() + nanoDuration;


        long timeLeft = nanoDuration;
        do {
            if (timeLeft > SLEEP_PRECISION)
                Thread.sleep(1);
            else
                Thread.yield();
            timeLeft = end - System.nanoTime();

            if (Thread.interrupted())
                throw new InterruptedException();

        } while (timeLeft > 0);

    }
}


