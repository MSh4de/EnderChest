package ua.realpha.enderchest;

import sun.misc.Unsafe;

import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class UnsafeArray {

    private final static int BYTE = 1;

    private long size;
    private long address;
    private final Unsafe unsafe;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    public UnsafeArray() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        System.out.println("LOAD");
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        this.unsafe = (Unsafe) f.get(null);
        this.size = 50000000;
        //Thread.sleep(10*1000);
        System.out.println("ALLOCATED");
        AtomicLong atomicLong = new AtomicLong();

        address = unsafe.allocateMemory(size *  Long.SIZE);

        scheduledExecutorService.schedule(() -> {
            System.out.println("FREE");
            unsafe.freeMemory(address);
        }, 5, TimeUnit.SECONDS);

    }


    public void set(long i, long value) {
        this.unsafe.putLong(address + i * Long.SIZE, value);
    }

    public long get(long idx) {
        return this.unsafe.getLong(address + idx * Long.SIZE);
    }

    public long size() {
        return size;
    }

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        new UnsafeArray();
    }

}
