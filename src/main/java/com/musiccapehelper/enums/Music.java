package com.musiccapehelper.enums;

import com.musiccapehelper.enums.settings.SettingsQuest;
import com.musiccapehelper.enums.settings.SettingsRegion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.Quest;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

public enum Music
{
	SEVENTH_REALM("7th Realm", true, new WorldPoint(2743, 3154, 0), false, SettingsRegion.KARAMJA,
		"Unlocked in the Brimhaven Dungeon", Collections.emptyList(), Collections.emptyList()),
	ADVENTURE("Adventure", true, new WorldPoint(3245, 3465, 0), false, SettingsRegion.MITHALIN,
		"Unlocked in Varrock", Collections.emptyList(), Collections.emptyList()),
	EASTER_JIG("Easter Jig", false, new WorldPoint(2965, 3297, 0), false, SettingsRegion.ASGARNIA,
		"This track unlocks during an Easter event", Collections.emptyList(), Collections.emptyList()),
	ALLS_FAIRY_IN_LOVE_AND_WAR("All's Fairy in Love & War", true, new WorldPoint(3203, 3168, 0), true, SettingsRegion.MITHALIN,
		"Unlocked at the Fairy Resistance Hideout during the Fairytale II - Cure a Queen  quest",
		new ArrayList<>(List.of(new SkillRequirement(Skill.THIEVING, 40), new SkillRequirement(Skill.FARMING, 49), new SkillRequirement(Skill.HERBLORE, 57))), Quest.FAIRYTALE_II__CURE_A_QUEEN),
	TEMPEST("Tempest", true, new WorldPoint(3429, 3557, 0), false, SettingsRegion.MORYTANIA,
		"This track unlocks while fighting the Grotesque Guardians",
		new ArrayList<>(List.of(new SkillRequirement(Skill.SLAYER, 75))), new ArrayList<>(List.of(ItemID.BRITTLE_KEY, ItemID.RUNE_BATTLEAXE, ItemID.DRAGONSTONE, ItemID.CABBAGE, ItemID.DRAGON_PLATEBODY)))
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
	private final List<SkillRequirement> skillsRequired;
	@Getter
	private List<Integer> items;
	@Getter
	private Quest quest;

	//standard tracks
	Music(String songName, boolean isRequired, WorldPoint songUnlockPoint,
		  boolean isQuest, SettingsRegion settingsRegion, String description, List<SkillRequirement> skillsRequired, List<Integer> items)
	{
		this.songName = songName;
		this.isRequired = isRequired;
		this.songUnlockPoint = songUnlockPoint;
		this.isQuest = isQuest;
		this.settingsRegion = settingsRegion;
		this.description = description;
		this.skillsRequired = skillsRequired;
		this.items = items;
	}

	//used for quests only
	Music(String songName, boolean isRequired, WorldPoint songUnlockPoint,
		  boolean isQuest, SettingsRegion settingsRegion, String description, List<SkillRequirement> skillsRequired, Quest quest)
	{
		this.songName = songName;
		this.isRequired = isRequired;
		this.songUnlockPoint = songUnlockPoint;
		this.isQuest = isQuest;
		this.settingsRegion = settingsRegion;
		this.description = description;
		this.skillsRequired = skillsRequired;
		this.quest = quest;
	}
}
