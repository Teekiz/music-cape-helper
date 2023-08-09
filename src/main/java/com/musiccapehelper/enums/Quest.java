package com.musiccapehelper.enums;

import lombok.Getter;

public enum Quest
{
	ALL("All"), QUEST_UNLOCK("Quest Unlocks Only"), NOT_QUEST_UNLOCK("None Quest Unlocks Only");
	@Getter
	private String text;
	Quest(String text)
	{
		this.text = text;
	}
}
