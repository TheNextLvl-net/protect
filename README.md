# Protect

A lightweight modern alternative to WorldGuard optimized for API usage

* [Commands](#commands)
* [Area flags](#area-flags)
* [Permission Packs](#permission-packs)
* [API](#api)

BStats can be found [here](https://bstats.org/plugin/bukkit/TheNextLvl%2520Protect/21712)

## Commands

| Command                              | Description                                       | Permission                            |
|--------------------------------------|---------------------------------------------------|---------------------------------------|
| /area create [name]                  | create new areas                                  | protect.command.area.create           |
| /area delete [area]                  | delete existing areas                             | protect.command.area.delete           |
| /area flag info [flag] (area)        | query information about area flags                | protect.command.area.flag.info        |
| /area flag list                      | list all existing area flags                      | protect.command.area.flag.list        |
| /area flag set [flag] [value] (area) | change specified flags of areas                   | protect.command.area.flag.set         |
| /area flag unset [flag] (area)       | reset specified flags of areas                    | protect.command.area.flag.unset       |
| /area info (area)                    | query information about specific areas            | protect.command.area.info             |
| /area list                           | list all areas                                    | protect.command.area.list             |
| /area priority [priority] (area)     | change the priority of areas                      | protect.command.area.priority         |
| /area redefine [area]                | redefine the region of an existing area           | protect.command.area.redefine         |
| /area schematic delete [area]        | delete an area's schematic                        | protect.command.area.schematic.delete |
| /area schematic load [area]          | load in an area's schematic                       | protect.command.area.schematic.load   |
| /area schematic save [area]          | save an area's schematic                          | protect.command.area.schematic.save   |
| /area select (area)                  | (worldedit) select the region of an existing area | worldedit.selection.pos               |
| /area teleport [area]                | teleport yourself to an area                      | protect.command.area.teleport         |

## Area flags

| Flag                                  | Description                                                                  | Values           |
|---------------------------------------|------------------------------------------------------------------------------|------------------|
| protect:enter                         | enable/disable entering areas                                                | true / false     |
| protect:leave                         | enable/disable leaving areas                                                 | true / false     |
| protect:armor_stand_manipulate        | enable/disable armor stand manipulation                                      | true / false     |
| protect:armor_washing                 | enable/disable armor washing                                                 | true / false     |
| protect:banner_washing                | enable/disable banner washing                                                | true / false     |
| protect:block_absorb                  | enable/disable block absorption                                              | true / false     |
| protect:block_break                   | enable/disable block breaking                                                | true / false     |
| protect:block_burning                 | enable/disable block burning                                                 | true / false     |
| protect:block_drying                  | enable/disable block drying                                                  | true / false     |
| protect:block_fading                  | enable/disable block fading                                                  | true / false     |
| protect:block_growth                  | enable/disable block growth                                                  | true / false     |
| protect:block_igniting                | enable/disable block ignition                                                | true / false     |
| protect:block_moisturising            | enable/disable block moisturising                                            | true / false     |
| protect:block_place                   | enable/disable block placing                                                 | true / false     |
| protect:block_spread                  | enable/disable block spread                                                  | true / false     |
| protect:cauldron_evaporation          | enable/disable cauldron evaporation                                          | true / false     |
| protect:cauldron_extinguish_entity    | enable/disable entity extinguishing by cauldron                              | true / false     |
| protect:cauldron_level_change_unknown | enable/disable unknown changes to cauldrons                                  | true / false     |
| protect:crop_trample                  | enable/disable crop trampling                                                | true / false     |
| protect:damage                        | enable/disable damage                                                        | true / false     |
| protect:empty_bottle                  | enable/disable emptying bottles                                              | true / false     |
| protect:empty_bucket                  | enable/disable emptying buckets                                              | true / false     |
| protect:entity_attack_entity          | enable/disable entity attacking entities                                     | true / false     |
| protect:entity_attack_player          | enable/disable entity attacking players                                      | true / false     |
| protect:entity_interact               | enable/disable entity interaction                                            | true / false     |
| protect:entity_item_drop              | enable/disable entity drop items                                             | true / false     |
| protect:entity_item_pickup            | enable/disable entity pickup items                                           | true / false     |
| protect:entity_shear                  | enable/disable shearing entities                                             | true / false     |
| protect:explosions                    | enable/disable explosions                                                    | true / false     |
| protect:fill_bottle                   | enable/disable filling bottles                                               | true / false     |
| protect:fill_bucket                   | enable/disable filling buckets                                               | true / false     |
| protect:hanging_break                 | enable/disable breaking hanging entities<br/>(paintings, item frames)        | true / false     |
| protect:hanging_place                 | enable/disable placing hanging entities<br/>(paintings, item frames)         | true / false     |
| protect:hunger                        | enable/disable hunger                                                        | true / false     |
| protect:interact                      | enable/disable block interaction                                             | true / false     |
| protect:leaves_decay                  | enable/disable leaves decaying                                               | true / false     |
| protect:natural_cauldron_fill         | enable/disable cauldron filling by natural causes<br/>(drip stone, downfall) | true / false     |
| protect:natural_entity_spawn          | enable/disable natural entity spawning                                       | true / false     |
| protect:physics                       | enable/disable physics                                                       | true / false     |
| protect:player_attack_entity          | enable/disable player attack entities                                        | true / false     |
| protect:player_attack_player          | enable/disable player attack players                                         | true / false     |
| protect:player_item_drop              | enable/disable player drop items                                             | true / false     |
| protect:redstone                      | enable/disable redstone                                                      | true / false     |
| protect:shoot                         | enable/disable shooting projectile                                           | true / false     |
| protect:shulker_washing               | enable/disable shulker washing                                               | true / false     |
| protect:time                          | change the time inside of an area                                            | long             |
| protect:farewell                      | message that is send to players when leaving an area                         | string           |
| protect:greetings                     | message that is send to players when entering an area                        | string           |
| protect:weather                       | change the weather inside of an area                                         | DOWNFALL / CLEAR |

## Permission Packs

Permission packs streamline server management by providing bundled options
instead of dealing with a multitude of individual permissions.

### protect.admin

Allows players to manage all areas and bypass all restrictions

    protect.command.area.create
    protect.command.area.delete
    protect.command.area.manage
    protect.bypass.admin

### protect.bypass.admin

Allows players to bypass any restriction

    protect.bypass.build
    protect.bypass.break
    protect.bypass.interact
    protect.bypass.entity-interact
    protect.bypass.trample
    protect.bypass.enter
    protect.bypass.leave
    protect.bypass.empty-bucket
    protect.bypass.fill-bucket
    protect.bypass.empty-bottle
    protect.bypass.fill-bottle
    protect.bypass.wash-banner
    protect.bypass.wash-shulker
    protect.bypass.wash-armor

### protect.command.area

Allows players to interact with areas in a non-destructive manner

    protect.command.area.flag.info
    protect.command.area.flag.list
    protect.command.area.info
    protect.command.area.list

### protect.command.area.flag

Allows players to manage area flags

    protect.command.area.flag.info
    protect.command.area.flag.list
    protect.command.area.flag.set
    protect.command.area.flag.unset

### protect.command.area.manage

Allows players to manage existing areas

    protect.command.area
    protect.command.area.flag
    protect.command.area.priority
    protect.command.area.redefine
    protect.command.area.schematic

### protect.command.area.schematic

Allows players to manage area schematics

    protect.command.area.schematic.delete
    protect.command.area.schematic.load
    protect.command.area.schematic.save

## API

https://repo.thenextlvl.net/#/releases/net/thenextlvl/protect/api
