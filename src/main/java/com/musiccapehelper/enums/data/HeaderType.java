package com.musiccapehelper.enums.data;

import com.musiccapehelper.enums.settings.SettingsRegion;
import lombok.Getter;

public enum HeaderType
{
	ASGARNIA(SettingsRegion.ASGARNIA), FREMENNIK_PROVINCE(SettingsRegion.FREMENNIK_PROVINCE),
	GREAT_KOUREND(SettingsRegion.GREAT_KOUREND), KANDARIN(SettingsRegion.KANDARIN), KARAMJA(SettingsRegion.KARAMJA),
	KEBOS_LOWLANDS(SettingsRegion.KEBOS_LOWLANDS), KHARIDIAN_DESERT(SettingsRegion.KHARIDIAN_DESERT), MITHALIN(SettingsRegion.MITHALIN),
	MORYTANIA(SettingsRegion.MORYTANIA), TIRANNWN(SettingsRegion.MITHALIN), WILDERNESS(SettingsRegion.WILDERNESS),
	OTHER(SettingsRegion.OTHER), REQUIRED, OPTIONAL, ERROR;

	@Getter
	private SettingsRegion settingsRegion;

	//Enum can refer to either region or required/optional
	HeaderType() {}
	HeaderType(SettingsRegion settingsRegion)
	{
		this.settingsRegion = settingsRegion;
	}
}
