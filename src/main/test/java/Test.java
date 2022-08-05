
public class Test {


    public static void main(String[] args) throws InterruptedException {

        System.out.println(17 % 2);

        /*
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            logger.info("hey");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, 0, 1, TimeUnit.NANOSECONDS);

         */

    }

    public static int getIndex(int x, int y, int z){
        return ((y & 0xf) << 8) | (z << 4) | x;
    }


}
