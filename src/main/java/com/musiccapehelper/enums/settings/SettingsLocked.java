package com.musiccapehelper.enums.settings;

import lombok.Getter;

public enum SettingsLocked
{
	ALL("All"), LOCKED("Locked Only"), UNLOCKED("Unlocked Only");
	@Getter
	private final String text;
	SettingsLocked(String text)
	{
		this.text = text;
	}
}
