package com.musiccapehelper.enums;

import lombok.Getter;

public enum OrderBy
{
	AZ("A-Z"), ZA("Z-A"), REGION("Region"), REQUIRED_FIRST("Required First"), OPTIONAL_FIRST("Optional First");
	@Getter
	private String text;
	OrderBy(String text)
	{
		this.text = text;
	}
}