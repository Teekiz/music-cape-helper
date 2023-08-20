package com.musiccapehelper.enums.settings;

import lombok.Getter;

public enum SettingsOrderBy
{
	AZ("A-Z"), ZA("Z-A"), REGION("Region"), REQUIRED_FIRST("Required First"), OPTIONAL_FIRST("Optional First");
	@Getter
	private String text;
	SettingsOrderBy(String text)
	{
		this.text = text;
	}
}