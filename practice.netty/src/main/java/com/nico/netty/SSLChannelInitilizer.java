package nico.netty;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

/**
 * SSL证书就是遵守 SSL协议，由受信任的数字证书颁发机构CA，在验证服务器身份后颁发，具有服务器身份验证和数据传输加密功能。
也就是说，HTTPS相比于HTTP服务，能够防止网络劫持，同时具备一定的安全加密作用。
   netty有提供SSL加密的工具包，只需要通过添加SslHandler，就能快速搭建。基于上面的代码，我们重新定义一个ChannelInitializer
 * @author Nico
 *
 */
public class SSLChannelInitilizer extends ChannelInitializer<SocketChannel>{
	
	private final SslContext sslContext;
	
	public SSLChannelInitilizer() {
		String keyStoreFilePath = "/root/.ssl/test.pkcs12";
        String keyStorePassword = "Password@123";
		try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(keyStoreFilePath), keyStorePassword.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

            sslContext = SslContextBuilder.forServer(keyManagerFactory).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
		
	}


	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
	    SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
	    pipeline.addLast(new SslHandler(sslEngine))
	            .addLast("decoder", new HttpRequestDecoder())
	            .addLast("encoder", new HttpResponseEncoder())
	            .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
	            .addLast("handler", new HttpHandler());
	}
}
