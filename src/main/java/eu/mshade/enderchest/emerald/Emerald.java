package eu.mshade.enderchest.emerald;

import eu.mshade.enderchest.emerald.listener.ShulkerPacketJoinServiceListener;
import eu.mshade.enderchest.emerald.listener.ShulkerPacketLeaveServiceListener;
import eu.mshade.enderchest.emerald.listener.ShulkerPacketLoginResponseListener;
import eu.mshade.enderchest.emerald.listener.ShulkerPacketSubscribeListener;
import eu.mshade.mwork.MWork;
import eu.mshade.shulker.Shulker;
import eu.mshade.shulker.ShulkerService;
import eu.mshade.shulker.event.ShulkerEventBus;
import eu.mshade.shulker.packet.*;
import eu.mshade.shulker.protocol.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Emerald {

    private static Logger logger = LoggerFactory.getLogger(Emerald.class);
    private EventLoopGroup nioEventLoopGroup;
    private ShulkerServiceRepository shulkerServiceRepository = new ShulkerServiceRepository();
    private TopicRepository topicRepository = new TopicRepository();
    private ShulkerService shulkerService;
    private Channel channel;
    private Shulker shulker;

    public Emerald(EventLoopGroup nioEventLoopGroup) {
        this.nioEventLoopGroup = nioEventLoopGroup;

        MWork mWork = MWork.get();
        this.shulker = new Shulker(mWork);



        ShulkerEventBus shulkerPacketEventBus = shulker.getShulkerPacketEventBus();

        shulkerPacketEventBus.subscribe(ShulkerPacketLoginResponse.class, new ShulkerPacketLoginResponseListener(this));
        shulkerPacketEventBus.subscribe(ShulkerPacketJoinService.class, new ShulkerPacketJoinServiceListener(this));
        shulkerPacketEventBus.subscribe(ShulkerPacketLeaveService.class, new ShulkerPacketLeaveServiceListener(this));
        shulkerPacketEventBus.subscribe(ShulkerPacketSubscribe.class, new ShulkerPacketSubscribeListener(this));

        Bootstrap bootstrap = new Bootstrap()
                .group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new EmeraldChannelInitializer(shulker, mWork));

        String host = (System.getProperty("remote") != null ? System.getProperty("remote") : System.getenv("remote"));

        try {
            channel = bootstrap.connect(host, 4568).addListener(future -> {
                sendPacket(new ShulkerPacketLoginRequest("BFkDDhdjhrc2URsIosOiI9frFMDXJHBq3DJC3XlWhdpyjAxUSyaFrPC7X6TPixEbcGMveCMtsuUL2nD6gDGx/YRqrTFHyvytqLdgJlfY6QA="));
                logger.info(String.valueOf(channel));
            }).channel();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void sendPacket(Topic topic, ShulkerPacket shulkerPacket){
        if (channel != null && !channel.isActive() && !channel.isOpen()) return;
                ShulkerPacketHeader shulkerPacketHeader = new ShulkerPacketHeader(this.shulker.getShulkerPacketRepository(), topic.getId(), 0, 0, topic.getShulkerPacketType());
                sendPacket(new ShulkerPacketContainer<>(shulkerPacketHeader, shulkerPacket));
                //this.channel.writeAndFlush(new ShulkerPacketContainer<>(shulkerPacketHeader, shulkerPacket));


        /*
        this.getShulkerServiceRepository().getSubscriber(topic.getShulkerPacketType()).forEach(shulkerService -> {
            ShulkerPacketHeader shulkerPacketHeader = new ShulkerPacketHeader(this.shulker.getShulkerPacketRepository(), topic.getId(), this.shulkerService.getId(), shulkerService.getId(), topic.getShulkerPacketType());
            this.channel.writeAndFlush(new ShulkerPacketContainer<>(shulkerPacketHeader, shulkerPacket));
        });

         */
    }


    public void sendPacket(ShulkerPacketContainer<ShulkerPacket> shulkerPacketShulkerPacketContainer) {
        this.channel.writeAndFlush(shulkerPacketShulkerPacketContainer, channel.voidPromise());

    }

    public void sendPacket(ShulkerPacket shulkerPacket){
        if (channel != null && !channel.isActive() && !channel.isOpen()) return;
        ShulkerPacketType packetTypeByPacket = this.shulker.getShulkerPacketRepository().getPacketTypeByPacket(shulkerPacket);
        ShulkerPacketHeader shulkerPacketHeader = new ShulkerPacketHeader(this.shulker.getShulkerPacketRepository(), 0, 0, packetTypeByPacket);
        this.channel.writeAndFlush(new ShulkerPacketContainer<>(shulkerPacketHeader, shulkerPacket), channel.voidPromise());
    }

    public ShulkerService getShulkerService() {
        return shulkerService;
    }

    public void setShulkerService(ShulkerService shulkerService) {
        this.shulkerService = shulkerService;
    }

    public ShulkerServiceRepository getShulkerServiceRepository() {
        return shulkerServiceRepository;
    }

    public TopicRepository getTopicRepository() {
        return topicRepository;
    }
}
