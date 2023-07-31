package com.musiccapehelper;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class MusicCapeHelperTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MusicCapeHelperPlugin.class);
		RuneLite.main(args);
	}
}