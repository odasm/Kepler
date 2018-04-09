#include "walk_task.h"

#include "list.h"
#include "deque.h"

#include "game/player/player.h"

#include "game/pathfinder/coord.h"
#include "game/pathfinder/rotation.h"

#include "game/room/room.h"
#include "game/room/room_user.h"

#include "game/items/item.h"

#include "game/room/mapping/room_model.h"
#include "game/room/mapping/room_map.h"
#include "game/room/mapping/room_tile.h"

#include "communication/messages/outgoing_message.h"

#include "shared.h"

void process_user(player *player);

/**
 * Walk task cyle that is called every 500ms
 *
 * @param room the room handled
 */
void walk_task(room *room) {
    List *users;
    list_copy_shallow(room->users, &users);

    int user_updates = 0;
    outgoing_message *status_update = om_create(34); // "@b"

    for (size_t i = 0; i < list_size(users); i++) {
        player *room_player;
        list_get_at(users, i, (void*)&room_player);

        if (room_player == NULL) {
            continue;
        }

        if (room_player->room_user == NULL) {
            continue;
        }

        process_user(room_player);

        if (room_player->room_user->needs_update) {
            room_player->room_user->needs_update = 0;
            user_updates++;
            append_user_status(status_update, room_player);
        }
    }

    if (user_updates > 0) {
        room_send(room, status_update);
    } else {
        om_cleanup(status_update);
    }

    list_destroy(users);
}

/**
 * Process the user in the walk task cycle
 *
 * @param player the player struct to process
 */
void process_user(player *player) {
    room_user *room_entity = (room_user*)player->room_user;

    if (room_entity->is_walking) {
        if (room_entity->next != NULL) {
            room_entity->current->x = room_entity->next->x;
            room_entity->current->y = room_entity->next->y;
            room_entity->current->z = room_entity->next->z;
            free(room_entity->next);
        }

        if (deque_size(room_entity->walk_list) > 0) {
            coord *next;
            deque_remove_first(room_entity->walk_list, (void*)&next);
            next->z = room_entity->room->room_data->model_data->heights[next->x][next->y];

            char value[30];
            sprintf(value, " %i,%i,%.2f", next->x, next->y, next->z);

            int rotation = calculate(room_entity->current->x, room_entity->current->y, next->x, next->y);
            coord_set_rotation(room_entity->current, rotation, rotation);

            room_user_remove_status(room_entity, "sit");
            room_user_remove_status(room_entity, "lay");

            room_user_add_status(room_entity, "mv", value, -1, "", 0, 0);
            room_entity->next = next;
        } else {
            room_entity->next = NULL;
            room_entity->is_walking = 0;
            stop_walking(room_entity, false);
        }

        player->room_user->needs_update = 1;
    }
}