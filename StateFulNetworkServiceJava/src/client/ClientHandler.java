package client;

import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;

public class ClientHandler extends SimpleChannelInboundHandler<Object> {
	//int sendMessages = 0;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof FullHttpResponse) {
			FullHttpResponse res = (FullHttpResponse) msg;
			handleHttpResponse(ctx, res);
		}else{
			WebSocketFrame frame = (WebSocketFrame) msg;
	        if (frame instanceof CloseWebSocketFrame) {
	            System.out.println("WebSocket Client received closing");
	            ctx.channel().close();
	        }
		}
		
		
		
		
		

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	private void handleHttpResponse(ChannelHandlerContext ctx,
			FullHttpResponse res) {
		// Handle a bad request.
		if (!res.getDecoderResult().isSuccess()) {
			closeConnection(ctx);
			return;
		}

		//read content
		System.out.println(res.content().toString(0,res.content().readableBytes(),CharsetUtil.UTF_8));


	}

	private void closeConnection(ChannelHandlerContext ctx) {
		ctx.close();

	}

	private void sendHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
		// Generate an error page if response getStatus code is not OK (200).
		ctx.writeAndFlush(req);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

}
