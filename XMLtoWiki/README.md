# PZ_RadioData_Converter
Converts Project Zomboid's RadioData.xml file into MediaWiki format.

## Installation
Download necessary version from [Releases](https://github.com/KBheid/PZ_RadioData_Converter/releases).

Requires Java 8+.

## Usage
Open the JAR by double clicking or utilizing `java -jar PZ_RadioDataParser.jar` from the commandline.

Some fields may be prepopulated:
- The input file field will be prepopulated if a file named `RadioData.xml` is present in the directory that the JAR is present in.
- If the input file field is unset, the input file field will attempt to populate it with a `RadioData.xml` file based in the following locations based on the user's OS:
  - Windows: `C:/Program Files (x86)/Steam/steamapps/common/ProjectZomboid/media/radio`
  - MacOS: `~/Library/Application Support/Steam/steamapps/common/ProjectZomboid/media/radio` 
  - Other (Linux, likely): `~/.steam/steam/steamapps/common/ProjectZomboid/projectzomboid/media/radio`
- The output directory field will be set if a directory named `wiki` is present in the directory that the JAR is present in. 

When both fields are populated, the Parse button should become available. Selecting the Parse button will transform the input `RadioData.xml` file into directories named by the channel type. The files in said directories will be named based on the channel name followed with a `.wiki` extension.

## Making Changes
`MainGUI.java` contains primarily auto-detection of files and the UI definitions. 
`Main.java` contains the definitions for all XML parsing and the style of output as well as the main method itself.

Changes to output format will likely be made in `Main.java`. Format changes can likely be done simply by modifying the `toString()` method of the `Channel`, `Broadcast`, and `Entry` classes.

`Channel`s contain `Broadcast`s, which are collections of `Entry`s. 

The properties of these objects are as follows:

`Channel`:
- `name` - The name of the channel (eg. PawsTV).
- `channelType` - The type of the channel (eg. television).
- `frequency` - The frequency that the channel broadcasts on ingame (eg. 205).

`Broadcast`:
- `startTime` - The start time of the broadcast. The time unit is currently unknown.
- `endTime` - The end time of the broadcast. Again, the time unit is currently unknown.
- `day` - The day that the broadcast appears. This may be based on an offset defined outside of RadioData.xml (as some with day 0 will happen far after day 1).

`Entry`:
- `hexColor` - The color in hex layout of the entry's text (eg. #ffc000).
- `text` - The text of the entry.

## Contributing
Pull requests are welcome. Features may be requested in the Issues tab.
