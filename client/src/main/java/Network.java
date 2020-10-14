import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class Network {
    private SocketChannel channel;

    private static final String HOST = "localhost";
    private static final int PORT = 8023;
    private Controllers controller;
    private static Network ourInstance = new Network();
    private static long userId;

    public Network() {
        Thread t = new Thread(() -> {
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel;
                                socketChannel.pipeline().addLast(
                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        new ObjectEncoder(),
                                        new TelnetClientHandler(ourInstance));
                            }
                        });
                ChannelFuture future = b.connect(HOST, PORT).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        t.start();
    }

    public void close() {
        channel.close();
    }

    public void sendMessage(FilePart str) {
        channel.writeAndFlush(str);
    }

    public void sendMessage(String str) {
        channel.writeAndFlush(str);
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public static Network getInstance() {
        return ourInstance;
    }

    public void setController(Controllers controller) {
        this.controller = controller;
    }

    public void sendAuthInfo(String login, String password) {
        channel.writeAndFlush(commandLibrary.authorizationRequestMessage(login,password));
    }

    public void sendRegInfo(String login, String password) {
        channel.writeAndFlush(commandLibrary.registrationRequestMessage(login, password));
    }

    public Controllers getController() {
        return controller;
    }

    public static void setUserId(long userId) {
        Network.userId = userId;
    }

    public static long getUserId() {
        return userId;
    }
}