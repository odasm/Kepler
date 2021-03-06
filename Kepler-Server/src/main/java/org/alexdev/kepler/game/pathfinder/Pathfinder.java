package org.alexdev.kepler.game.pathfinder;

import java.util.Collections;
import java.util.LinkedList;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.mapping.RoomTile;

public class Pathfinder {

    public static final Position[] DIAGONAL_MOVE_POINTS = new Position[]{
            new Position(0, -1, 0),
            new Position(0, 1, 0),
            new Position(1, 0, 0),
            new Position(-1, 0, 0),
            new Position(1, -1, 0),
            new Position(-1, 1, 0),
            new Position(1, 1, 0),
            new Position(-1, -1, 0)
    };

    private static final Position[] MOVE_POINTS = new Position[]{
            new Position(0, -1),
            new Position(1, 0),
            new Position(0, 1),
            new Position(-1, 0)
    };

    /**
     * Method for the pathfinder to check if the tile next to the current tile is a valid step.
     *
     * @param entity the entity walking
     * @param current the current tile
     * @param tmp the temporary tile around the current tile to check
     * @param isFinalMove if the move was final
     * @return true, if a valid step
     */
    public static boolean isValidStep(Room room, Entity entity, Position current, Position tmp, boolean isFinalMove) {
        if (!RoomTile.isValidTile(room, entity, new Position(current.getX(), current.getY()))) {
            return false;
        }

        if (!RoomTile.isValidTile(room, entity, new Position(tmp.getX(), tmp.getY()))) {
            return false;
        }

        RoomTile fromTile = room.getMapping().getTile(current);
        RoomTile toTile = room.getMapping().getTile(tmp);

        double oldHeight = fromTile.getTileHeight();
        double newHeight = toTile.getTileHeight();

        if (oldHeight - 4 >= newHeight) {
            return false;
        }

        if (oldHeight + 1.5 <= newHeight) {
            return false;
        }

        Item fromItem = fromTile.getHighestItem();
        Item toItem = toTile.getHighestItem();

        // Only check these below if the user is in a pool room.
        if (entity.getRoom().getModel().getName().startsWith("pool_")) {

            // Let people to walk to the next tile if they were on a previous tile
            // in the diving deck
            if (fromItem != null && toItem != null) {
                if (entity.getRoom().getModel().getName().equals("pool_b")) {
                    if (fromItem.getDefinition().getSprite().equals("queue_tile2") &&
                            toItem.getDefinition().getSprite().equals("queue_tile2")) {
                        return true;
                    }
                }
            }

            if (toItem != null) {
                // Check if they have swimmers before trying to enter pool
                if (toItem.getDefinition().getSprite().equals("poolEnter") ||
                    toItem.getDefinition().getSprite().equals("poolLeave")) {
                    return entity.getDetails().getPoolFigure().length() > 0;
                }

                // Don't allow to "enter" the pool if they're already swimming
                if (entity.getRoomUser().containsStatus(StatusType.SWIM) &&
                    toItem.getDefinition().getSprite().equals("poolEnter")) {
                    return false;
                }

                // Don't allow to "leave" the pool if they're not swimming
                if (!entity.getRoomUser().containsStatus(StatusType.SWIM) &&
                    toItem.getDefinition().getSprite().equals("poolExit")) {
                    return false;
                }

                // Don't allow people to enter the booth if it's closed, or don't allow
                // if they attempt to use the pool lift without swimmers
                if (toItem.getDefinition().getSprite().equals("poolBooth") ||
                    toItem.getDefinition().getSprite().equals("poolLift")) {

                    if (toItem.getCurrentProgramValue().equals("close")) {
                        return false;
                    } else {
                        return !toItem.getDefinition().getSprite().equals("poolLift") || entity.getDetails().getPoolFigure().length() > 0;
                    }
                }

                // Don't allow people to enter the queue from any coordinate, and don't allow
                // if they don't have a ticket or have swimmers.
                if (entity.getRoom().getModel().getName().equals("pool_b") &&
                    toItem.getDefinition().getSprite().equals("queue_tile2")) {

                    if (toItem.getPosition().getX() == 21 && toItem.getPosition().getY() == 9) {
                        return entity.getDetails().getTickets() > 0 && entity.getDetails().getPoolFigure().length() > 0;
                    } else {
                        return false;
                    }
                }
            }
        }

        // Avoid walking into furniture unless it's their last location
        if (!current.equals(room.getModel().getDoorLocation())) {
            if (toItem != null) {
                if (isFinalMove) {
                    return toItem.isWalkable();
                } else {
                    return toItem.hasBehaviour(ItemBehaviour.CAN_STAND_ON_TOP) || (toItem.hasBehaviour(ItemBehaviour.DOOR) && toItem.getCustomData().equals("O"));
                }
            }
        }

        return true;
    }

    /**
     * Make path.
     *
     * @param entity the entity
     * @return the linked list
     */
    public static LinkedList<Position> makePath(Entity entity) {
        int X = entity.getRoomUser().getGoal().getX();
        int Y = entity.getRoomUser().getGoal().getY();
        return makePath(entity, X, Y);
    }

    /**
     * Make path with specified last coordinates
     *
     * @param entity the entity to move
     * @param x the xcoord to move from
     * @param y the y coord to move from
     * @return the linked list
     */
    private static LinkedList<Position> makePath(Entity entity, int x, int y) {
        if (!RoomTile.isValidTile(entity.getRoom(), entity, new Position(x, y))) {
            return new LinkedList<>();
        }

        LinkedList<Position> squares = new LinkedList<>();
        PathfinderNode nodes = makePathReversed(entity, x, y);

        if (nodes != null) {
            while (nodes.getNextNode() != null) {
                squares.add(new Position(nodes.getPosition().getX(), nodes.getPosition().getY()));
                nodes = nodes.getNextNode();
            }
        }

        Collections.reverse(squares);
        return squares;

    }

    /**
     * Make path reversed.
     *
     * @param entity the entity
     * @return the pathfinder node
     */
    private static PathfinderNode makePathReversed(Entity entity, int X, int Y) {
        LinkedList<PathfinderNode> openList = new LinkedList<>();

        PathfinderNode[][] map = new PathfinderNode[entity.getRoom().getModel().getMapSizeX()][entity.getRoom().getModel().getMapSizeY()];
        PathfinderNode node;
        Position tmp;

        int cost;
        int diff;

        PathfinderNode current = new PathfinderNode(entity.getRoomUser().getPosition());
        current.setCost(0);

        Position end = new Position(X, Y);
        PathfinderNode finish = new PathfinderNode(end);

        map[current.getPosition().getX()][current.getPosition().getY()] = current;
        openList.add(current);

        while (openList.size() > 0) {
            current = openList.pollFirst();
            current.setInClosed(true);

            for (Position POINT : DIAGONAL_MOVE_POINTS) {
                tmp = current.getPosition().add(POINT);

                boolean isFinalMove = (tmp.getX() == end.getX() && tmp.getY() == end.getY());

                if (isValidStep(entity.getRoomUser().getRoom(), entity, new Position(current.getPosition().getX(), current.getPosition().getY(), current.getPosition().getZ()), tmp, isFinalMove)) {
                    if (map[tmp.getX()][tmp.getY()] == null) {
                        node = new PathfinderNode(tmp);
                        map[tmp.getX()][tmp.getY()] = node;
                    } else {
                        node = map[tmp.getX()][tmp.getY()];
                    }

                    if (!node.isInClosed()) {
                        diff = 0;

                        if (current.getPosition().getX() != node.getPosition().getX()) {
                            diff += 1;
                        }

                        if (current.getPosition().getY() != node.getPosition().getY()) {
                            diff += 1;
                        }

                        cost = current.getCost() + diff + node.getPosition().getDistanceSquared(end);

                        if (cost < node.getCost()) {
                            node.setCost(cost);
                            node.setNextNode(current);
                        }

                        if (!node.isInOpen()) {
                            if (node.getPosition().getX() == finish.getPosition().getX() && node.getPosition().getY() == finish.getPosition().getY()) {
                                node.setNextNode(current);
                                return node;
                            }

                            node.setInOpen(true);
                            openList.add(node);
                        }
                    }
                }
            }
        }

        return null;
    }
}