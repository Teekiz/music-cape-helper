package com.musiccapehelper.enums.settings;

import lombok.Getter;

public enum SettingsQuest
{
	ALL("All"), QUEST_UNLOCK("Quest Unlocks Only"), NOT_QUEST_UNLOCK("Non-quest Unlocks Only");
	@Getter
	private String text;
	SettingsQuest(String text)
	{
		this.text = text;
	}
}
