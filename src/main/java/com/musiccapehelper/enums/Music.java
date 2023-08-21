package com.musiccapehelper.enums;

import com.musiccapehelper.enums.settings.SettingsRegion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.Quest;
import net.runelite.api.coords.WorldPoint;

public enum Music
{
	SEVENTH_REALM("7th Realm", true, new WorldPoint(2743, 3154, 0), false, SettingsRegion.KARAMJA,
		"Unlocked in the Brimhaven Dungeon", Collections.emptyList()),
	ADVENTURE("Adventure", true, new WorldPoint(3245, 3465, 0), false, SettingsRegion.MITHALIN,
		"Unlocked in Varrock", Collections.emptyList()),
	EASTER_JIG("Easter Jig", false, new WorldPoint(2965, 3297, 0), false, SettingsRegion.ASGARNIA,
		"This track unlocks during an Easter event", EventType.EASTER_EVENT),
	ALLS_FAIRY_IN_LOVE_AND_WAR("All's Fairy in Love & War", true, new WorldPoint(3203, 3168, 0), true, SettingsRegion.MITHALIN,
		"Unlocked at the Fairy Resistance Hideout during the Fairytale II - Cure a Queen  quest", Quest.FAIRYTALE_II__CURE_A_QUEEN),
	TEMPEST("Tempest", true, new WorldPoint(3429, 3557, 0), false, SettingsRegion.MORYTANIA,
		"This track unlocks while fighting the Grotesque Guardians", new ArrayList<>(List.of(ItemID.BRITTLE_KEY)))
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
	Music(String songName, boolean isRequired, WorldPoint songUnlockPoint,
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
	Music(String songName, boolean isRequired, WorldPoint songUnlockPoint,
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
	Music(String songName, boolean isRequired, WorldPoint songUnlockPoint,
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
