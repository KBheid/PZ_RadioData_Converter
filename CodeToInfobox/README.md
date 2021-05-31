# PZ_RadioData_Converter
Converts Project Zomboid's item code into a [PZWiki Infobox](https://pzwiki.net/wiki/Category:Infobox). It also reformats the code into wiki usable code with proper spacing (if the input was not already.)

Currently supports the following infobox subtypes:
* Clothing
* Drainable
* Food
* Literature
* Weapon
* Normal

## Table of Contents
1. [Installation](#installation)
2. [Usage](#usage)
    1. [Working example](#working-example)
3. [Making Changes](#making-changes)
    1. [Changing output format](#changing-output-format)
    2. [Adding a new wikiForm](#adding-a-new-wikiform)
    3. [Example wikiForm](#example-wikiform)
4. [Contributing](#contributing)

## Installation
Download necessary version from [Releases](https://github.com/KBheid/PZ_RadioData_Converter/tags) (search for tags prefixed with CTI).

Requires Java 8+.

## Usage
Open the JAR by double clicking or utilizing `java -jar PZ_CodeToInfobox.jar` from the commandline.

Input the code section of a wiki page (or taken from a file under the `media/scripts` folder of PZ.) The details for the input code are as follows:
- Newline characters (`\n`) are required for each key/value pair.
- Whitespace excluding newline characters are ignored and stripped.
- The code must have a line in the format `item [base item name]` before ANY key/value pairs.
- Aside from the above rule, lines that do not contain `=` characters will be ignored.
- `,` characters will be stripped.

The output text may include some values enclosed with square brackets (\[]). These sections should be read, filled in, or removed prior to usage on the wiki.

### Working example
A working example input taken straight from `item_weapons.txt` from PZ Build 41.50: 
<details open>
	<summary>Input</summary>

```
    item BaseballBat
	{
		MaxRange	=	1.25,
		WeaponSprite	=	BaseballBat,
		MinAngle	=	0.75,
		Type	=	Weapon,
		MinimumSwingTime	=	3,
		KnockBackOnNoDeath	=	TRUE,
		SwingAmountBeforeImpact	=	0.02,
		Categories	=	Blunt,
		ConditionLowerChanceOneIn	=	20,
		Weight	=	2,
		SplatNumber	=	1,
		PushBackMod	=	0.5,
		SubCategory	=	Swinging,
		ConditionMax	=	15,
		MaxHitCount	=	2,
		DoorDamage	=	5,
		IdleAnim	=	Idle_Weapon2,
		SwingAnim	=	Bat,
		DisplayName	=	Baseball Bat,
		MinRange	=	0.61,
		SwingTime	=	3,
		HitAngleMod	=	-30,
		KnockdownMod	=	2,
		SplatBloodOnNoDeath	=	FALSE,
		Icon	=	BaseballBat,
		RunAnim	=	Run_Weapon2,
        TwoHandWeapon = TRUE,
        BreakSound  =   BreakWoodItem,
        TreeDamage  =   1,
		CriticalChance	=	40,
		critDmgMultiplier = 2,
		MinDamage	=	0.8,
		MaxDamage	=	1.1,
		BaseSpeed = 1,
		WeaponLength = 0.5,
		AttachmentType = Shovel,
	}
```
	
</details>

The output as of version `CTI_v1.0`:

<details>
	<summary>Infobox</summary>

```
{{Infobox weapon
|display_name=Baseball Bat
|name_colour=Weapon
|name_text_colour=Weapon
|image=[Fill].png
|image_width=[Fill in width or]120px
|alternate_image=[Fill or remove]
|alternate_name=[Fill or remove]
|alternate_link=[Fill or remove]
<!--GENERAL-->
|category=weapon[or tool]
|weight=2
|function=[What the item is used for. Can usually be removed if the item isn't considered a tool by the wiki. (remove if value is empty)]
|equipped=Two-handed
|condition_max=15
|attachment_type=Shovel
<!--CHARACTERISTICS-->
<!--DAMAGE-->
|type=Blunt
|swing_time=3
|min_range=0.61
|max_range=1.25
|min_damage=0.8
|max_damage=1.1
|push_back=0.5
|knockdown=2
|effect_power=[The power of the effect caused by the item. (remove if value is empty)]
|effect_range=[The range of the trap's effect. Defined by either ExplosionRange, FireRange, SmokeRange or NoiseRange. (remove if value is empty)]
|effect_timer=[Time before trap is triggered. Defined by the ExplosionTimer variable. (remove if value is empty)]
|effect_type=[The effect given-off by the trap. Can be either: "explosion", "fire", "smoke" or "noise". Any other value will display as "Effect range". (remove if value is empty)]
|sensor_range=[How close an entity needs to be before the trap is triggered. (remove if value is empty)]
<!--TECHNICAL DETAILS-->
|ingredients=[A list of ingredients required to make this item. Please use the following format: 1 × [[Plank]] and separate ingredients into a list with <br>.]
|class_name=BaseballBat
}}
```
	
</details>
<details>
	<summary>Formatted Code</summary>

```
	item BaseballBat
		MaxRange			= 1.25,
		WeaponSprite			= BaseballBat,
		MinAngle			= 0.75,
		Type				= Weapon,
		MinimumSwingTime		= 3,
		KnockBackOnNoDeath		= TRUE,
		SwingAmountBeforeImpact		= 0.02,
		Categories			= Blunt,
		ConditionLowerChanceOneIn	= 20,
		Weight				= 2,
		SplatNumber			= 1,
		PushBackMod			= 0.5,
		SubCategory			= Swinging,
		ConditionMax			= 15,
		MaxHitCount			= 2,
		DoorDamage			= 5,
		IdleAnim			= Idle_Weapon2,
		SwingAnim			= Bat,
		DisplayName			= Baseball Bat,
		MinRange			= 0.61,
		SwingTime			= 3,
		HitAngleMod			= -30,
		KnockdownMod			= 2,
		SplatBloodOnNoDeath		= FALSE,
		Icon				= BaseballBat,
		RunAnim				= Run_Weapon2,
		TwoHandWeapon			= TRUE,
		BreakSound			= BreakWoodItem,
		TreeDamage			= 1,
		CriticalChance			= 40,
		critDmgMultiplier		= 2,
		MinDamage			= 0.8,
		MaxDamage			= 1.1,
		BaseSpeed			= 1,
		WeaponLength			= 0.5,
		AttachmentType			= Shovel,
```
	
</details>

## Making Changes
`MainGUI.java` contains primarily UI definitions and functionality - specifically the Copy button's functionality.
`Main.java` contains the parsing functionality of both wikiForms as well as input code.
* Input code is parsed within the `Map<String, String> parseCode(String)` method.
* wikiForms definitions are parsed within the `String onGenerateInfobox(String)` method.
* Formatted code output is generated within the `String onGenerateFormattedCode(String)` method.
`wikiForms/` contains the format definitions for each supported infobox subtype.

### Changing output format
Changes to output format will likely be made in files within the `wikiForms/` directory.
The `wikiForms/` format is as follows:
* Lines without any text enclosed in curly brackets ({}) or astrisks (\*\*) will be printed as is.
* Lines with square brackets (\[]) cannot be fully programmatically filled and should be filled in by the end user.
  * Convention:
    * Square bracket sections should have their internal text taken from the infobox's doc page, if applicable.
* Sections in lines enclosed with curly brackets or astrisks will be replaced with keys provided from the code
  * Convention:
    * Keys enclosed in curly brackets are directly from the input code.
    * Keys enclosed in asterisks are special cases and are programatically set.

### Adding a new wikiForm
There are three primary steps to adding a new wikiForm.
1. Create a new file in the `wikiForms/` directory in the form `[infobox_name].txt`
  1. Refer to the prior section (Changing output format) for format specifications.
2. Add your form to the `String getFilenameFromType(String)` method (which switches based on the Type value from the input code)
3. Add special cases within the `Map<String, String> parseCode(String)` method.

### Example wikiForm
The following is an example wikiForm taken from `wikiForm/Normal.txt`, which is based on [Infobox Normal](https://pzwiki.net/wiki/Template:Infobox_normal).
<details open>
	<summary>Example</summary>

```
{{Infobox normal
|display_name={DisplayName}
|name_colour=Normal
|name_text_colour=Normal
|image=[Fill].png
|image_width=[Fill in width or]120px
|alternate_image=[Fill or remove]
|alternate_name=[Fill or remove]
|alternate_link=[Fill or remove]
|weight={Weight}
|primary_use=[The item's primary use. Can usually be removed if the item isn't considered a tool by the wiki. (remove if value is empty)]
|secondary_use=[The item's secondary use if it has one. Can usually be removed if the item isn't considered a tool by the wiki. (remove if value is empty)]
|can_store_water={CanStoreWater}
|rain_factor={RainFactor}
|bandage_power={BandagePower}
|alcohol={Alcoholic}
|wet_cooldown={WetCooldown}
|remote_range={RemoteRange}
|ingredients=[A list of ingredients required to make this item. Please use the following format: 1 × [[Plank]] and separate ingredients into a list with <br>.]
|class_name=*itemName*
}}
```
	
</details>

## Contributing
Pull requests are welcome. Features may be requested and bugs may be reported in the Issues tab.
