package com.musiccapehelper.enums.settings;

import lombok.Getter;

public enum SettingsOptional
{
	ALL("All"), REQUIRED_ONLY("Required Only"), OPTIONAL_ONLY("Optional Only");
	@Getter
	private String text;
	SettingsOptional(String text)
	{
		this.text = text;
	}
}
