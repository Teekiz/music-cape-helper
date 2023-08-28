package com.musiccapehelper.enums.data;

import lombok.Getter;

public enum EventType
{
	EASTER_EVENT("Easter Event"),
	HALLOWEEN_EVENT("Halloween Event"),
	CHRISTMAS_EVENT("Christmas Event");

	@Getter
	private final String eventName;

	EventType(String eventName)
	{
		this.eventName = eventName;
	}
}
