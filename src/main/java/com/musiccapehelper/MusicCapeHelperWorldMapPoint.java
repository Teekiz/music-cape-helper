package com.musiccapehelper;

import java.awt.image.BufferedImage;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.util.ImageUtil;

public class MusicCapeHelperWorldMapPoint extends WorldMapPoint
{

	final Music music;
	final boolean completed;

	public MusicCapeHelperWorldMapPoint(Music music, boolean completed)
	{
		//todo - change based on whether it is completed or not and if it is a quest
		super(null, null);

		this.music = music;
		this.completed = completed;

		this.setWorldPoint(music.getSongUnlockPoint());
		this.setTooltip(music.getSongName());
		this.setImage(ImageUtil.loadImageResource(getClass(), "/clue_arrow_d.png"));
	}
}
