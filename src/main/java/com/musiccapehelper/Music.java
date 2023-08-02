package com.musiccapehelper;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

public enum Music
{
	SEVENTH_REALM("7th Realm", true, new WorldPoint(2743, 3154, 0), false, Region.KARAMJA, "Unlocked in the Brimhaven Dungeon", ""),
	ADVENTURE("Adventure", true, new WorldPoint(3234, 3465, 0), false, Region.MITHALIN, "Unlocked in Varrock", ""),
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

enum Region
{
	ASGARNIA("Asgarnia"), FREMENNIK_PROVINCE("Fremennik"), GREAT_KOUREND("Kourend"),
	KANDARIN("Kandarin"), KARAMJA("Karamja"), KEBOS_LOWLANDS("Kebos"),
	KHARIDIAN_DESERT("Desert"), MITHALIN("Mithalin"), MORYTANIA("Morytania"),
	TIRANNWN("Tirannwn"), WILDERNESS("Wilderness"), OTHER("Other");

	@Getter
	private String name;
	Region(String name)
	{
		this.name = name;
	}


}
