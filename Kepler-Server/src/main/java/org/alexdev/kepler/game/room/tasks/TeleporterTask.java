package org.alexdev.kepler.game.room.tasks;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.items.BROADCAST_TELEPORTER;
import org.alexdev.kepler.messages.outgoing.rooms.user.USER_STATUSES;

import java.util.List;

public class TeleporterTask implements Runnable {
    private final Item item;
    private final Entity entity;
    private final Room room;

    public TeleporterTask(Item linkedTeleporter, Entity entity, Room room) {
        this.item = linkedTeleporter;
        this.entity = entity;
        this.room = room;
    }

    @Override
    public void run() {
        this.entity.getRoomUser().setPosition(this.item.getPosition().copy());

        this.room.send(new USER_STATUSES(List.of(this.entity)));
        this.room.send(new BROADCAST_TELEPORTER(this.item, this.entity.getDetails().getName(), false));

        entity.getRoomUser().setAuthenticateTelporterId(-1);

        // TODO: Properly add and remove entity from the specific tiles instead of regenerating the entire map.
        this.room.getMapping().regenerateCollisionMap();
    }
}
