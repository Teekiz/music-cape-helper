package com.musiccapehelper.enums.data;

import com.musiccapehelper.enums.settings.SettingsRegion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.Quest;
import net.runelite.api.coords.WorldPoint;

public enum MusicData
{
	SEVENTH_REALM("7th Realm", true, new WorldPoint(2743, 3154, 0), false, SettingsRegion.KARAMJA,
		"Unlocked in the Brimhaven Dungeon.", Collections.emptyList()),
	ADVENTURE("Adventure", true, new WorldPoint(3245, 3465, 0), false, SettingsRegion.MITHALIN,
		"Unlocked in Varrock.", Collections.emptyList()),
	EASTER_JIG("Easter Jig", false, new WorldPoint(2965, 3297, 0), false, SettingsRegion.ASGARNIA,
		"This track unlocks during an Easter event.", EventType.EASTER_EVENT),
	ALLS_FAIRY_IN_LOVE_AND_WAR("All's Fairy in Love & War", true, new WorldPoint(3203, 3168, 0), true, SettingsRegion.MITHALIN,
		"Unlocked at the Fairy Resistance Hideout during the Fairytale II - Cure a Queen  quest.", Quest.FAIRYTALE_II__CURE_A_QUEEN),
	TEMPEST("Tempest", true, new WorldPoint(3429, 3557, 0), false, SettingsRegion.MORYTANIA,
		"This track unlocks while fighting the Grotesque Guardians.", new ArrayList<>(List.of(ItemID.BRITTLE_KEY))),
	THE_MAD_MOLE("The Mad Mole", true, new WorldPoint(2997, 3376, 0), false, SettingsRegion.ASGARNIA,
	"This track unlocks while fighting the Giant Mole.", new ArrayList<>(List.of(ItemID.SPADE, ItemID.CANDLE, ItemID.TINDERBOX))),
	MUSEUM_MEDLEY("Museum Medley", true, new WorldPoint(3045, 3377, 0), false, SettingsRegion.ASGARNIA,
		"This track unlocks in the Old School Museum.", Collections.emptyList()),
	OUT_AT_THE_MINES("Out at the Mines", true, new WorldPoint(1488, 3865, 0), false, SettingsRegion.GREAT_KOUREND,
		"This track unlocks north west of Lovakengj.", Collections.emptyList()),
	SHARP_END_OF_THE_CRYSTAL("Sharp End of the Crystal", true, new WorldPoint(3224, 6044, 0), false, SettingsRegion.TIRANNWN,
		"This track unlocks in the Iorwerth Slayer Dungeon.", Collections.emptyList()),
	THE_SPURNED_DEMON("The Spurned Demon", true, new WorldPoint(3280, 6059, 0), false, SettingsRegion.TIRANNWN,
		"This track unlocks while fighting Zalcano.", Collections.emptyList()),
	COMPETITION("Competition", true, new WorldPoint(2900, 3565, 0), false, SettingsRegion.ASGARNIA,
		"This track unlocks in the Burthorpe Games Room.", Collections.emptyList()),
	CATCH_ME_IF_YOU_CAN("Catch Me If You Can", true, new WorldPoint(2560, 3320, 0), false, SettingsRegion.KANDARIN,
		"This track unlocks in the Ardougne Rat Pits.", Collections.emptyList()),
	RAT_A_TAT_TAT("Rat a Tat Tat", true, new WorldPoint(3266, 3400, 0), false, SettingsRegion.MITHALIN,
	"This track unlocks at the Varrock Rat Pits.", Collections.emptyList()),
	BEYOND_THE_MEADOW("Beyond the Meadow", true, new WorldPoint(1679, 3550, 0), false, SettingsRegion.GREAT_KOUREND,
		"This track unlocks west of Hosidius.", Collections.emptyList()),
	SORCERESS_GARDEN("Sorceress's Garden", true, new WorldPoint(3320, 3141, 0), false, SettingsRegion.KHARIDIAN_DESERT,
	"This track unlocks in the Sorceress's Garden.", Collections.emptyList()),
	GARDEN_OF_AUTUMN("Garden of Autumn", true, new WorldPoint(3320, 3141, 0), false, SettingsRegion.KHARIDIAN_DESERT,
	"This track unlocks in the Sorceress' Garden minigame.", Collections.emptyList()),
	GARDEN_OF_SPRING("Garden of Spring", true, new WorldPoint(3320, 3141, 0), false, SettingsRegion.KHARIDIAN_DESERT,
		"This track unlocks in the Sorceress' Garden minigame.", Collections.emptyList()),
	GARDEN_OF_SUMMER("Garden of Summer", true, new WorldPoint(3320, 3141, 0), false, SettingsRegion.KHARIDIAN_DESERT,
		"This track unlocks in the Sorceress' Garden minigame.", Collections.emptyList()),
	GARDEN_OF_WINTER("Garden of Winter", true, new WorldPoint(3320, 3141, 0), false, SettingsRegion.KHARIDIAN_DESERT,
		"This track unlocks in the Sorceress' Garden minigame.", Collections.emptyList())
	;

	@Getter
	private final String songName;
	@Getter
	private final boolean isRequired;
	@Getter
	private final WorldPoint songUnlockPoint;
	@Getter
	private final boolean isQuest;
	@Getter
	private final SettingsRegion settingsRegion;
	@Getter
	private final String description;
	@Getter
	private List<Integer> items;
	@Getter
	private Quest quest;
	@Getter
	private EventType eventType;

	//standard tracks
	MusicData(String songName, boolean isRequired, WorldPoint songUnlockPoint,
			  boolean isQuest, SettingsRegion settingsRegion, String description, List<Integer> items)
	{
		this.songName = songName;
		this.isRequired = isRequired;
		this.songUnlockPoint = songUnlockPoint;
		this.isQuest = isQuest;
		this.settingsRegion = settingsRegion;
		this.description = description;
		this.items = items;
	}

	//used for quests only
	MusicData(String songName, boolean isRequired, WorldPoint songUnlockPoint,
			  boolean isQuest, SettingsRegion settingsRegion, String description, Quest quest)
	{
		this.songName = songName;
		this.isRequired = isRequired;
		this.songUnlockPoint = songUnlockPoint;
		this.isQuest = isQuest;
		this.settingsRegion = settingsRegion;
		this.description = description;
		this.quest = quest;
	}
	//used for optional tracks only
	MusicData(String songName, boolean isRequired, WorldPoint songUnlockPoint,
			  boolean isQuest, SettingsRegion settingsRegion, String description, EventType eventType)
	{
		this.songName = songName;
		this.isRequired = isRequired;
		this.songUnlockPoint = songUnlockPoint;
		this.isQuest = isQuest;
		this.settingsRegion = settingsRegion;
		this.description = description;
		this.eventType = eventType;
	}
}
