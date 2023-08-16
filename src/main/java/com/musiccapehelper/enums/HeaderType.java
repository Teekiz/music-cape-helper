package com.musiccapehelper.enums;

import lombok.Getter;

public enum HeaderType
{
	ASGARNIA(Region.ASGARNIA), FREMENNIK_PROVINCE(Region.FREMENNIK_PROVINCE),
	GREAT_KOUREND(Region.GREAT_KOUREND), KANDARIN(Region.KANDARIN), KARAMJA(Region.KARAMJA),
	KEBOS_LOWLANDS(Region.KEBOS_LOWLANDS), KHARIDIAN_DESERT(Region.KHARIDIAN_DESERT), MITHALIN(Region.MITHALIN),
	MORYTANIA(Region.MORYTANIA), TIRANNWN(Region.MITHALIN), WILDERNESS(Region.WILDERNESS),
	OTHER(Region.OTHER), REQUIRED, OPTIONAL, ERROR;

	@Getter
	private Region region;

	//Enum can refer to either region or required/optional
	HeaderType() {}
	HeaderType(Region region)
	{
		this.region = region;
	}
}
