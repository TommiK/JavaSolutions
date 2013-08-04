package client;

import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class Client {

	public void runClient(int requests) throws Exception {
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap(); 
			b.group(workerGroup); 
			b.channel(NioSocketChannel.class); 
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {

					ch.pipeline().addLast("codec-http", new HttpClientCodec());
					ch.pipeline().addLast("aggregator",
							new HttpObjectAggregator(65536));
					ch.pipeline().addLast("client", new ClientHandler());
				}
			});

			// Start the client.
			Channel ch = b.connect("localhost", 8080).sync().channel(); 
			FullHttpRequest req = new DefaultFullHttpRequest(HTTP_1_1, GET, "/");
			setContentLength(req, req.content().readableBytes());
			if (ch.isActive()) {
				for(int i=0;i<requests;i++){
					ch.writeAndFlush(req);
				}
				//ch.close();
				
			}

			// Wait until the connection is closed.
			ChannelFuture closeFuture = ch.closeFuture().sync();
			
			closeFuture.addListener(new FutureListener<Object>() {

				@Override
				public void operationComplete(Future<Object> arg0) throws Exception {
					System.out.println("close");
				}

			});
		} finally {
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		new Client().runClient(1000);
	}
}
