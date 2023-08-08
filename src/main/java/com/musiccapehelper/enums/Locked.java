package com.musiccapehelper.enums;

import lombok.Getter;

public enum Locked
{
	ALL("All"), LOCKED("Locked Only"), UNLOCKED("Unlocked Only");
	@Getter
	private String text;
	Locked(String text)
	{
		this.text = text;
	}
}
