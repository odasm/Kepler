package org.alexdev.kepler.game.room.managers;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.room.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomItemManager {
    private Room room;
    private Item soundMachine;

    public RoomItemManager(Room room) {
        this.room = room;
    }

    /**
     * Get an item in the room by its item id.
     *
     * @param itemId the item id to retrieve the item instance.
     * @return the new item
     */
    public Item getById(int itemId) {
        for (Item item : this.room.getItems()) {
            if (item.getId() == itemId) {
                return item;
            }
        }

        return null;
    }

    /**
     * Get the list of public items, used for rendering public room items.
     * @return the list of public room items
     */
    public List<Item> getPublicItems() {
        List<Item> items = new ArrayList<>();

        for (Item item : this.room.getItems()) {
            if (!item.hasBehaviour(ItemBehaviour.PUBLIC_SPACE_OBJECT)) {
                continue;
            }

            items.add(item);
        }

        return items;
    }

    /**
     * Get the list of floor items, used for rendering purchased floor items through the catalogue.
     * @return the list of floor items
     */
    public List<Item> getFloorItems() {
        List<Item> items = new ArrayList<>();

        for (Item item : this.room.getItems()) {
            if (item.hasBehaviour(ItemBehaviour.PUBLIC_SPACE_OBJECT)) {
                continue;
            }

            if (item.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
                continue;
            }

            items.add(item);
        }

        return items;
    }

    /**
     * Get the list of wall items, used for rendering purchased wall items through the catalogue.
     * @return the list of wall items
     */
    public List<Item> getWallItems() {
        List<Item> items = new ArrayList<>();

        for (Item item : this.room.getItems()) {
            if (item.hasBehaviour(ItemBehaviour.PUBLIC_SPACE_OBJECT)) {
                continue;
            }

            if (!item.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
                continue;
            }

            items.add(item);
        }

        return items;
    }
    /**
     * Get the rooms' trax machine instance.
     *
     * @return the trax machine
     */
    public Item getSoundMachine() {
        return soundMachine;
    }

    /**
     * Set the rooms' trax instance.
     *
     * @param soundMachine the trax machine instance
     */
    public void setSoundMachine(Item soundMachine) {
        this.soundMachine = soundMachine;
    }
}