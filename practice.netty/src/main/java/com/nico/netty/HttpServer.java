package nico.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Nio服务器
 * @author Nico
 *
 */
public class HttpServer {

	private final int port;
	
	public HttpServer(int port) {
		super();
		this.port = port;
	}

	public static void main(String[] args) throws Exception{
//		if (args.length != 1) {
//            System.err.println(
//                    "Usage: " + HttpServer.class.getSimpleName() +
//                            " <port>");
//            return;
//        }
        int port = 8089;
        new HttpServer(port).start();
	}
	
	public void start() throws Exception {
		ServerBootstrap bootstrap = new ServerBootstrap();
		NioEventLoopGroup group = new NioEventLoopGroup();
		
		bootstrap.group(group)
		.channel(NioServerSocketChannel.class)
		.childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				System.out.println("initChannel ch:" + ch);
                ch.pipeline()
                        .addLast("decoder", new HttpRequestDecoder())   // 1
                        .addLast("encoder", new HttpResponseEncoder())  // 2
                        .addLast("aggregator", new HttpObjectAggregator(512 * 1024)) // 3
                        .addLast("handler",new HttpHandler());        // 4
			}
		})
		.option(ChannelOption.SO_BACKLOG, 128)
		.childOption(ChannelOption.SO_KEEPALIVE,Boolean.TRUE);
		
		bootstrap.bind(port).sync();
	}
}
