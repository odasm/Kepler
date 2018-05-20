package org.alexdev.icarus.server.netty;

import io.netty.channel.Channel;
import org.alexdev.icarus.encryption.RC4;
import org.alexdev.icarus.messages.types.MessageComposer;
import org.alexdev.icarus.server.netty.codec.EncryptionDecoder;

public class NettyPlayerNetwork {
    private Channel channel;



    private int connectionId;

    public NettyPlayerNetwork(Channel channel, int connectionId) {
        this.channel = channel;
        this.connectionId = connectionId;
    }

    public void close() {
        channel.close();
    }

    public void send(MessageComposer response) {
        channel.writeAndFlush(response);
    }

    public void sendQueued(MessageComposer response) {
        channel.write(response);
    }

    public void flush() {
        channel.flush();
    }

    public int getConnectionId() {
        return connectionId;
    }
}
