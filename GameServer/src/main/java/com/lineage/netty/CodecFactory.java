package com.lineage.netty;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

/**
 * 處理客戶端消息和各種消息事件的類<br>
 * 類名稱：CodecFactory<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月24日 下午5:34:28<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public final class CodecFactory implements ChannelPipelineFactory {

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = pipeline();

		pipeline.addLast("decoder", new Decoder());
		pipeline.addLast("encoder", new Encoder());
		pipeline.addLast("handler", new ProtocolHandler());

		return pipeline;
	}

}
