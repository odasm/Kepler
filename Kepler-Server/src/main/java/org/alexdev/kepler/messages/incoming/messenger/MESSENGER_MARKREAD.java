package org.alexdev.kepler.messages.incoming.messenger;

import org.alexdev.kepler.dao.mysql.MessengerDao;
import org.alexdev.kepler.game.messenger.Messenger;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class MESSENGER_MARKREAD implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        int messageId = reader.readInt();
        MessengerDao.markMessageRead(messageId);
    }
}
