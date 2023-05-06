import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestTickMC implements Runnable{

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
        while (true) {
            final long curTime = System.nanoTime();
            final long wait = 50000000L - (curTime - lastTick) - catchupTime;
            if (wait > 0L) {
                try {
                    Thread.sleep(wait / 1000000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                catchupTime = 0L;
            }
            else {
                catchupTime = Math.min(1000000000L, Math.abs(wait));

                lastTick = curTime;
                logger.info("ticks");
            }
        }
    }
}
