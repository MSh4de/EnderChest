package ua.realpha.enderchest;

import eu.mshade.mwork.MWork;
import sun.misc.Unsafe;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class ChunkLoader {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    public ChunkLoader() throws InterruptedException, IOException {

        int r = 10;
        int rSquared = r * r;
        int cX = 0;
        int cZ = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (int x = cX - r; x <= cX + r; x++) {
            for (int z = cZ - r; z <= cZ + r; z++) {
                if ((cX - x) * (cX - x) + (cZ - z) * (cZ - z) <= rSquared) {
                    stringBuilder.append("*");
                }else stringBuilder.append("O");
            }
            stringBuilder.append("\n");
        }


        System.out.println(stringBuilder.toString());

    }

    public void getChunk(int x, int z, Consumer<String> stringConsumer){
        //lock
        ///
        //unlock
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        new ChunkLoader();
    }

}
