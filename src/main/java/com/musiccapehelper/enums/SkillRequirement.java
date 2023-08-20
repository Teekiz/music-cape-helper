package com.musiccapehelper.enums;

import lombok.Getter;
import net.runelite.api.Skill;

public class SkillRequirement
{
	@Getter
	private Skill skill;
	@Getter
	private int level;

	public SkillRequirement(Skill skill, int level)
	{
		this.skill = skill;
		this.level = level;
	}
}


