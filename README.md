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
