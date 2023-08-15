package com.musiccapehelper.enums;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

public enum Music
{
	SEVENTH_REALM("7th Realm", true, new WorldPoint(2743, 3154, 0), false, Region.KARAMJA, "Unlocked in the Brimhaven Dungeon", ""),
	ADVENTURE("Adventure", true, new WorldPoint(3245, 3465, 0), false, Region.MITHALIN, "Unlocked in Varrock", ""),
	EASTER_JIG("Easter Jig", false, new WorldPoint(3245, 3465, 0), false, Region.OTHER, "This track unlocks during an Easter event.", ""),
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
	private final Region region;
	@Getter
	private final String description;
	@Getter
	private final String notes; //things like key required or level


	Music(String songName, boolean isRequired, WorldPoint songUnlockPoint, boolean isQuest, Region region, String description, String notes)
	{
		this.songName = songName;
		this.isRequired = isRequired;
		this.songUnlockPoint = songUnlockPoint;
		this.isQuest = isQuest;
		this.region = region;
		this.description = description;
		this.notes = notes;
	}
}