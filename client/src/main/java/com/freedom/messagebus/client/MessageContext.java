package com.freedom.messagebus.client;

import com.freedom.messagebus.client.message.model.Message;
import com.freedom.messagebus.business.model.Node;
import com.freedom.messagebus.client.core.config.ConfigManager;
import com.freedom.messagebus.client.core.pool.AbstractPool;
import com.freedom.messagebus.client.handler.consume.OriginalReceiver;
import com.freedom.messagebus.client.model.MessageCarryType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * the message context, mostly used in handler chain
 */
public class MessageContext {

    private static final Log logger = LogFactory.getLog(MessageContext.class);

    @NotNull
    public  Connection connection;
    @NotNull
    private String     host;
    private boolean    isAuthorized;
    private boolean    enableTransaction;
    @NotNull
    private String     appId;

    /**
     * for produce
     */
    @NotNull
    private Message[] messages;

    /**
     * for consume
     */
    private Message consumedMsg;
    private String  consumerTag;


    @NotNull
    private MessageCarryType carryType;                 //produce or consume
    @NotNull
    private Node             sourceNode;                //store represent self
    @NotNull
    private Node             targetNode;                 //store represent current carry node

    @NotNull
    private Channel                           channel;
    @NotNull
    private OriginalReceiver.ReceiveEventLoop receiveEventLoop;
    @NotNull
    private IChannelDestroyer                 destroyer;
    @NotNull
    private IMessageReceiveListener           listener;
    @NotNull
    private Map<String, Object> otherParams = new HashMap<String, Object>();

    private AbstractPool<Channel> pool;
    private long                  timeout;
    private boolean               hasTimeout;

    private int           consumeMsgNum;
    private List<Message> consumeMsgs;
    private boolean isSync = false;

    @NotNull
    private String tempQueueName;                       //for response

    private List<String> subQueueNames;

    public MessageContext() {
    }

    @NotNull
    public String getHost() {
        if (this.host == null) {
            this.host = ConfigManager.getInstance().getClientConfigMap().get("messagebus.client.host").getValue();
            return this.host;
        }
        return this.host;
    }

    @NotNull
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(@NotNull Connection connection) {
        this.connection = connection;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
    }

    public boolean isEnableTransaction() {
        return enableTransaction;
    }

    public void setEnableTransaction(boolean enableTransaction) {
        this.enableTransaction = enableTransaction;
    }

    @NotNull
    public Message[] getMessages() {
        return messages;
    }

    public void setMessages(@NotNull Message[] messages) {
        this.messages = messages;
    }

    @NotNull
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(@NotNull Channel channel) {
        this.channel = channel;
    }

    @NotNull
    public OriginalReceiver.ReceiveEventLoop getReceiveEventLoop() {
        return receiveEventLoop;
    }

    public void setReceiveEventLoop(@NotNull OriginalReceiver.ReceiveEventLoop receiveEventLoop) {
        this.receiveEventLoop = receiveEventLoop;
    }

    @NotNull
    public MessageCarryType getCarryType() {
        return carryType;
    }

    public void setCarryType(@NotNull MessageCarryType carryType) {
        this.carryType = carryType;
    }

    @NotNull
    public Node getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(@NotNull Node targetNode) {
        this.targetNode = targetNode;
    }

    @NotNull
    public Node getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(@NotNull Node sourceNode) {
        this.sourceNode = sourceNode;
    }

    @NotNull
    public Map<String, Object> getOtherParams() {
        return otherParams;
    }

    @NotNull
    public String getAppId() {
        return appId;
    }

    public void setAppId(@NotNull String appId) {
        this.appId = appId;
    }

    @NotNull
    public IChannelDestroyer getDestroyer() {
        return destroyer;
    }

    public void setDestroyer(@NotNull IChannelDestroyer destroyer) {
        this.destroyer = destroyer;
    }

    public Message getConsumedMsg() {
        return consumedMsg;
    }

    public void setConsumedMsg(Message consumedMsg) {
        this.consumedMsg = consumedMsg;
    }

    @NotNull
    public IMessageReceiveListener getListener() {
        return listener;
    }

    public void setListener(@NotNull IMessageReceiveListener listener) {
        this.listener = listener;
    }

    public AbstractPool<Channel> getPool() {
        return pool;
    }

    public void setPool(AbstractPool<Channel> pool) {
        this.pool = pool;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isTimeout() {
        return hasTimeout;
    }

    public void setIsTimeout(boolean hasTimeout) {
        this.hasTimeout = hasTimeout;
    }

    @NotNull
    public String getTempQueueName() {
        return tempQueueName;
    }

    public void setTempQueueName(@NotNull String tempQueueName) {
        this.tempQueueName = tempQueueName;
    }

    public int getConsumeMsgNum() {
        return consumeMsgNum;
    }

    public void setConsumeMsgNum(int consumeMsgNum) {
        this.consumeMsgNum = consumeMsgNum;
    }

    public List<Message> getConsumeMsgs() {
        return consumeMsgs;
    }

    public void setConsumeMsgs(List<Message> consumeMsgs) {
        this.consumeMsgs = consumeMsgs;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean isSync) {
        this.isSync = isSync;
    }

    public String getConsumerTag() {
        return consumerTag;
    }

    public void setConsumerTag(String consumerTag) {
        this.consumerTag = consumerTag;
    }

    public List<String> getSubQueueNames() {
        return subQueueNames;
    }

    public void setSubQueueNames(List<String> subQueueNames) {
        this.subQueueNames = subQueueNames;
    }

    @Override
    public String toString() {
        return "MessageContext";
    }
}
