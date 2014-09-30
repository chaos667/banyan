package com.freedom.messagebus.client.handler.consume;

import com.freedom.messagebus.client.MessageContext;
import com.freedom.messagebus.client.handler.AbstractHandler;
import com.freedom.messagebus.client.handler.IHandlerChain;
import com.freedom.messagebus.common.message.Message;
import com.freedom.messagebus.common.message.MessageType;
import com.freedom.messagebus.interactor.message.IMessageBodyProcessor;
import com.freedom.messagebus.interactor.message.MessageBodyProcessorFactory;
import com.freedom.messagebus.interactor.message.MessageHeaderProcessor;
import com.freedom.messagebus.interactor.proxy.ProxyConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.QueueingConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SyncConsumer extends AbstractHandler {

    private static final Log logger = LogFactory.getLog(SyncConsumer.class);

    /**
     * the main process method all sub class must implement
     *
     * @param context the message context
     * @param chain   the instance of IHandlerChain
     */
    @Override
    public void handle(@NotNull MessageContext context, @NotNull IHandlerChain chain) {
        if (context.isSync()) {
            List<Message> consumeMsgs = new ArrayList<>(context.getConsumeMsgNum());
            context.setConsumeMsgs(consumeMsgs);
            long everyTimeout = context.getTimeout() / context.getConsumeMsgNum();
            QueueingConsumer consumer;
            try {
                consumer = ProxyConsumer.consume(context.getChannel(),
                                                 context.getQueueNode().getValue());
                int countDown = context.getConsumeMsgNum();
                while (countDown-- > 0) {
                    QueueingConsumer.Delivery delivery= consumer.nextDelivery(everyTimeout);
                    if (delivery == null){
                        continue;
                    }

                    AMQP.BasicProperties properties = delivery.getProperties();
                    byte[] msgBody = delivery.getBody();

                    context.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                    String msgTypeStr = properties.getType();
                    if (msgTypeStr == null || msgTypeStr.isEmpty()) {
                        logger.error("[handle] message type is null or empty");
                        continue;
                    }

                    MessageType msgType = MessageType.lookup(msgTypeStr);
                    Message msg = new Message();
                    initMessage(msg, msgType, properties, msgBody);
                    consumeMsgs.add(msg);
                }
            } catch (IOException e) {
                logger.error("[handle] occurs a IOException " + e.getMessage());
            } catch (InterruptedException e) {
                logger.info("[handle] occurs a InterruptedException " + e.getMessage());
            } finally {
                //destroy channel
                context.getDestroyer().destroy(context.getChannel());
            }
        }

        chain.handle(context);
    }

    private void initMessage(Message msg, MessageType msgType, AMQP.BasicProperties properties, byte[] bodyData) {
        msg.setMessageHeader(MessageHeaderProcessor.unbox(properties, msgType));
        msg.setMessageType(msgType);

        IMessageBodyProcessor msgBodyProcessor = MessageBodyProcessorFactory.createMsgBodyProcessor(msgType);
        msg.setMessageBody(msgBodyProcessor.unbox(bodyData));
    }
}
