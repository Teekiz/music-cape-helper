package com.musiccapehelper.enums;

import lombok.Getter;

public enum Region
{
	ALL("All"), ASGARNIA("Asgarnia"), FREMENNIK_PROVINCE("Fremennik"),
	GREAT_KOUREND("Kourend"), KANDARIN("Kandarin"), KARAMJA("Karamja"),
	KEBOS_LOWLANDS("Kebos"), KHARIDIAN_DESERT("Desert"), MITHALIN("Mithalin"),
	MORYTANIA("Morytania"), TIRANNWN("Tirannwn"), WILDERNESS("Wilderness"),
	OTHER("Other");

	@Getter
	private String name;
	Region(String name)
	{
		this.name = name;
	}
}
