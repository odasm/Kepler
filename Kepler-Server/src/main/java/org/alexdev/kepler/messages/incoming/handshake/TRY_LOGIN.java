package org.alexdev.kepler.messages.incoming.handshake;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.LOCALISED_ERROR;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class TRY_LOGIN implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        String username = reader.readString();
        String password = reader.readString();

        if (!PlayerDao.login(player, username, password)) {
            player.send(new LOCALISED_ERROR("Login incorrect"));
        } else {
            player.login();
        }
    }
}
