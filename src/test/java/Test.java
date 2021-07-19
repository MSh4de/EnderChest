import eu.mshade.enderchest.world.noise.PerlinNoiseGenerator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Test {

    private Queue<String> strings = new ConcurrentLinkedQueue<>();
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;
    private static final double FEATURE_SIZE = 24;

    public Test() throws Exception{
        PerlinNoiseGenerator perlinNoiseGenerator = new PerlinNoiseGenerator(new Random(8));
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                //double value = noise.eval(x / FEATURE_SIZE, y / FEATURE_SIZE, 0.0);
                double value = perlinNoiseGenerator.noise(x/FEATURE_SIZE, y/FEATURE_SIZE, 3, 1, 0.9);
                int rgb = 0x010101 * (int)((value + 1) * 127.5);
                image.setRGB(x, y, rgb);
            }
        }
        ImageIO.write(image, "png", new File("noise.png"));



        /*
        System.out.println(Objects.hash(0, 0));

        BinaryTagBufferDriver binaryTagBufferDriver = MWork.get().getBinaryTagBufferDriver();
        WorldManager worldManager = new WorldManager(new DedicatedEnderChest(eventLoopGroup));
        DefaultWorldBuffer defaultWorldBuffer = new DefaultWorldBuffer(worldManager, new WorldLevel("test", 0L, LevelType.DEFAULT, Dimension.OVERWORLD, Difficulty.NORMAL), new File("test"));

        DefaultChunkBuffer defaultChunkBuffer = new DefaultChunkBuffer(0, 0, defaultWorldBuffer);
        WorldBufferIO.writeChunkBuffer(defaultChunkBuffer);

        System.out.println(WorldBufferIO.readChunkBuffer(defaultWorldBuffer, 0, 0));

         */
    }



    public static void main(String[] args) throws Exception {
        new Test();
    }
}
