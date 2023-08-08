package com.musiccapehelper;
import com.musiccapehelper.enums.Music;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.util.ImageUtil;

public class MusicCapeHelperWorldMapPoint extends WorldMapPoint
{
	Music music;
	boolean completed;

	public MusicCapeHelperWorldMapPoint(Music music, boolean completed)
	{
		//ImagePoint code used from "clue scrolls" plugin constructor

		//todo - change based on whether it is completed or not and if it is a quest
		super(null, null);

		this.music = music;
		this.completed = completed;

		this.setWorldPoint(music.getSongUnlockPoint());
		this.setTooltip(music.getSongName());
		this.setImage(ImageUtil.loadImageResource(getClass(), "/clue_arrow_d.png"));
		this.setImagePoint(new Point(getImage().getWidth() / 2, getImage().getHeight()));
	}
}
