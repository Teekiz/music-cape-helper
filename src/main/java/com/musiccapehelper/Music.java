package com.musiccapehelper;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

public enum Music
{
	SEVENTH_REALM("7th Realm", false, new WorldPoint(2743, 3154, 0), false, Region.KARAMJA, "Unlocked in the Brimhaven Dungeon", ""),
	ADVENTURE("Adventure", false, new WorldPoint(3234, 3465, 0), false, Region.MITHALIN, "Unlocked in Varrock", ""),
	;

	@Getter
	private final String song_name;
	@Getter
	private final boolean is_required;
	@Getter
	private final WorldPoint song_unlock_point;
	@Getter
	private final boolean is_quest;
	@Getter
	private final Region region;
	@Getter
	private final String description;
	@Getter
	private final String notes; //things like key required or level


	Music(String song_name, boolean is_required, WorldPoint song_unlock_point, boolean is_quest, Region region, String description, String notes)
	{
		this.song_name = song_name;
		this.is_required = is_required;
		this.song_unlock_point = song_unlock_point;
		this.is_quest = is_quest;
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
