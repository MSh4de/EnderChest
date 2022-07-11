public class Test {


    public static void main(String[] args) throws InterruptedException {

        int index = 4095;
        System.out.println(Long.toBinaryString(index));
        int y = index >> 8;
        int z = y & (index >> 4);
        int x = index & 0x0F;

        System.out.println(y);
        System.out.println(z);
        System.out.println(x);

        System.out.println(Long.toBinaryString(getIndex(15, 15, 15)));
        System.out.println(getIndex(255, 255, 255));


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
