package com.musiccapehelper.enums;

import lombok.Getter;

public enum Optional
{
	ALL("All"), REQUIRED_ONLY("Required Only"), OPTIONAL_ONLY("Optional Only");
	@Getter
	private String text;
	Optional(String text)
	{
		this.text = text;
	}
}
