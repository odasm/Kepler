#include <stdlib.h>

#include "list.h"

#include "game/player/player.h"
#include "game/inventory/inventory.h"
#include "game/items/item.h"

#include "database/queries/item_query.h"

#include "util/stringbuilder.h"

void inventory_clear(List *items);

/**
 * Get an inventory struct.
 *
 * @return the inventory struct
 */
inventory *inventory_create() {
    inventory *inv = malloc(sizeof(inventory));
    inv->items = NULL;
    inv->hand_strip_page_index = 0;
    return inv;
}

/**
 * Initialise the player inventory, will clear and destroy list and query
 * the database for new items.
 *
 * @param player the player to load
 */
void inventory_init(player *player) {
    if (player->inventory->items != NULL) {
        inventory_clear(player->inventory->items);
    }

    player->inventory->hand_strip_page_index = 0;
    player->inventory->items = item_query_get_inventory(player->player_data->id);
}

/**
 * Change the view of the inventory, used for pagination.
 *
 * @param inventory the inventory to change
 * @param strip_view the view type to change to
 */
void inventory_change_view(inventory *inventory, char *strip_view) {
    if (strcmp(strip_view, "new") == 0) {
        inventory->hand_strip_page_index = 0;
    }

    if (strcmp(strip_view, "next") == 0) {
        inventory->hand_strip_page_index++;
    }

    if (strcmp(strip_view, "prew") == 0) {
        if (inventory->hand_strip_page_index > 0) {
            inventory->hand_strip_page_index--;
        }
    }

    if (strcmp(strip_view, "last") == 0) {
        inventory->hand_strip_page_index++;
    }
}

/**
 * Get the inventory casts for opening hand.
 *
 * @param inventory the inventory to get the casts for
 */
char *inventory_get_casts(inventory *inventory, int *count) {
    stringbuilder *sb = sb_create();

    int start_id = 0;
    int end_id = (int) list_size(inventory->items);

    if (inventory->hand_strip_page_index == 255) {
        inventory->hand_strip_page_index = ((end_id - 1) / 9);
    }

    calculate_strip_offset:
    if (end_id > 0) {
        start_id = inventory->hand_strip_page_index * 9;

        if (end_id > (start_id + 9)) {
            end_id = start_id + 9;
        }

        if (start_id >= end_id) {
            inventory->hand_strip_page_index--;
            goto calculate_strip_offset;
        }

        *count = *count + 1;

        for (int strip_slot_id = start_id; strip_slot_id < end_id; strip_slot_id++) {
            item *item;
            list_get_at(inventory->items, (size_t) strip_slot_id, (void*)&item);

            char *strip_string = item_strip_string(item, strip_slot_id);
            sb_add_string(sb, strip_string);
            free(strip_string);
        }
    }

    char *str = strdup(sb->data);
    sb_cleanup(sb);

    return str;
}

/**
 * Clear and destroy inventory list.
 *
 * @param items the list of items to destory
 */
void inventory_clear(List *items) {

}

/**
 * Dispose the inventory.
 *
 * @param inventory the inventory to dispose
 */
void inventory_dispose(inventory *inventory) {
    if (inventory->items != NULL) {
        inventory_clear(inventory->items);
    }

    free(inventory);
}