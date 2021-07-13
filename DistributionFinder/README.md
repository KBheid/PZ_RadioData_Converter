# PZ_DistributionFinder
Searches Project Zomboid's distribution files and compiles them into a
MediaWiki formatted, copyable window. Also has a human-readable option for
those that who do not wish to use it for the wiki.

Currently supports the following distribution types:
* Buildings (`Distributions.lua`)
* Vehicles (`VehicleDistributions.lua`)

Currently does not support the following distribution types:
* Procedural (ProceduralDistributions.lua)
  * This includes [stashes](https://pzwiki.net/wiki/Containers#Stashes) and the like
    
## Table of Contents
1. [Installation](#installation)
2. [Usage](#usage)
   1. [Selecting a source](#selecting-a-source)
    1. [Finding where items spawn](#finding-where-items-spawn)
    1. [Example output](#example-output)
3. [Making Changes](#making-changes)
4. [Future Features](#future-features)
5. [Contributing](#contributing)

## Installation
Download necessary version from [Releases](https://github.com/KBheid/PZ_RadioData_Converter/tags) (search for tags prefixed with DF).

Requires Java 8+.

## Usage
Open the JAR by double clicking or utilizing `java -jar PZ_CodeToInfobox.jar` from the commandline.

### Selecting a source
The item list is populated via the different distribution files used by Project Zomboid.
The bottom bar of the application display checks for the three relevant files
if they have been found and could be properly parsed.

In order to populate the item list, you must select a source. There are two
ways of sourcing the distributions files: selecting the Project Zomboid directory
or the files individually.

#### Selecting via directory
When sourcing with the Project Zomboid directory, the application will attempt to find
all three files. The resulting files found will be displayed along the bottom of
the application. 

To choose the directory:
1. `File`>`Project Zomboid directory...`
2. Navigate to your Project Zomboid installation.
    1. The application will navigate to the default Steam install directory 
       on the C: drive, if Project Zomboid can be found there.
       
#### Selecting individual files
When sourcing with individual files, the application will prompt the user to select
the respective file. This can be useful if it is desired to view only one distribution
type or to load a distribution file without having Project Zomboid installed.

To choose individual files:
1. `File`>`{Bulding, Procedural, Vehicle} Distribution file...`
2. Navigate to and select the desired file.
    1. The application will always navigate to the application's current directory.
    

### Finding where items spawn
When the items list is available on the right side of the application, they can be
selected to view their spawn locations. Two tabs are available for different viewing:
the Readable tab, which displays the data in easier to read syntax, and the MediaWiki tab, which
displays the data in MediaWiki format.

A search feature is available to filter between items. If the entered string is contained
in the name, the item will display. For example, searching for `dog` would narrow the list down to
'Corndog', 'Dogfood', and 'Hotdog'.

### Example output
The following is example output with both `Distribution.lua` and `VehicleDistribution.lua` files sourced
and the item with ID `VideoGame` selected.

<details>
	<summary>Readable tab output</summary>

```
Location: giftstore
	shelves, rolls: 3
		2.0
	displaycase, rolls: 3
		2.0
Location: changeroom
	locker, rolls: 2
		0.5
Location: toystore
	shelves, rolls: 3
		3.0
```
</details>

<details>
	<summary>MediaWiki tab output</summary>
    Notice the 'EDITOR' notes - these methods do not spawn the item and would
need to be deleted.

```
=== Buildings ===
{| class="pztable" style="text-align:center;"
|-
!Building/Room
!Container
!Rolls
!Chance
|-
|rowspan=2|giftstore
|displaycase
|3
|2.0
|-
|shelves
|3
|2.0
|-
|changeroom
|locker
|2
|0.5
|-
|toystore
|shelves
|3
|3.0
|-
|}
'''''EDITOR! CHECK THE FOLLOWING SECTION TO SEE IF IT CONTAINS ANY ITEMS'''''
=== Containers ===
A list of containers that the item can be found in, not limited to buildings.
{| class="pztable" style="text-align:center;"
|-
!Container
!Rolls
!Chance
|-
|}
'''''EDITOR! CHECK THE FOLLOWING SECTION TO SEE IF IT CONTAINS ANY ITEMS'''''
=== Vehicles ===
{| class="pztable" style="text-align:center;"
|-
!Vehicle Name
!Container
!Rolls
!Chance
|-
|}
```
</details>

## Making Changes
### Parsing
Parsing is accomplished via lua scripts. These scripts can be found under the
`lua` directory. LuaJ can instantiate java objects. The objects that we instantiate
with lua can be found in the `LuaJavaDefines` package. 

### Changing output format
The output format is set in `Main.java`.
* The 'Readable' tab output is defined in `updateReadable()`
* The 'MediaWiki' tab output is defined in `updateMediaWiki()`

## Future Features
### Get all item spawns for a location/vehicle
The ability to select from a list of locations or vehicles and retrieve all items
that can spawn in them, separated by container.
### Distribution creator
An additional sub-tool that allows the creation of custom distribution files
for modders. This will include a graphical interface for selecting spawn locations
and will relay relevant information, such as other items that spawn there and
the number of rolls that the container gets.

## Contributing
Pull requests are welcome. Features may be requested and bugs may be reported 
in the Issues tab with the `DF` tag.
