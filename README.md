# Protect

A lightweight modern alternative to WorldGuard optimized for API usage

## Commands

| Command                              | Description                                       | Permission                            |
|--------------------------------------|---------------------------------------------------|---------------------------------------|
| /area create [name]                  | create new areas                                  | protect.command.area.create           |
| /area delete [area]                  | delete existing areas                             | protect.command.area.delete           |
| /area flag info [flag] (area)        | query information about area flags                | protect.command.area.flag.info        |
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

## Permission Packs

Permission packs streamline server management by providing bundled options
instead of dealing with a multitude of individual permissions.

| Permission                                                     | Description                                                       |
|----------------------------------------------------------------|-------------------------------------------------------------------|
| [protect.admin](#protectadmin)                                 | Allows players to manage all areas and bypass all restrictions    |
| [protect.bypass.admin](#protectbypassadmin)                    | Allows players to bypass any restriction                          |
| [protect.command.area](#protectcommandarea)                    | Allows players to interact with areas in a non-destructive manner |
| [protect.command.area.flag](#protectcommandareaflag)           | Allows players to manage area flags                               |
| [protect.command.area.manage](#protectcommandareamanage)       | Allows players to manage existing areas                           |
| [protect.command.area.schematic](#protectcommandareaschematic) | Allows players to manage area schematics                          |

### protect.admin

    protect.command.area.create
    protect.command.area.delete
    protect.command.area.manage
    protect.bypass.admin

### protect.bypass.admin

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
    protect.bypass.wash-armo

### protect.command.area

    protect.command.area.flag.info
    protect.command.area.info
    protect.command.area.list

### protect.command.area.flag

    protect.command.area.flag.info
    protect.command.area.flag.set
    protect.command.area.flag.unset

### protect.command.area.manage

    protect.command.area
    protect.command.area.flag
    protect.command.area.priority
    protect.command.area.redefine
    protect.command.area.schematic

### protect.command.area.schematic

    protect.command.area.schematic.delete
    protect.command.area.schematic.load
    protect.command.area.schematic.save

