package com.musiccapehelper;
import com.musiccapehelper.enums.Music;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.util.ImageUtil;

public class MusicCapeHelperWorldMapPoint extends WorldMapPoint
{
	@Getter
	Music music;
	@Getter
	boolean completed;
	MusicCapeHelperConfig config;

	public MusicCapeHelperWorldMapPoint(Music music, boolean completed, MusicCapeHelperConfig config)
	{
		//ImagePoint code used from "clue scrolls" plugin constructor
		//todo - if the player right clicks, pan and zoom to quest start point
		super(null, null);

		this.music = music;
		this.completed = completed;
		this.config = config;

		this.setName(music.getSongName());
		this.setWorldPoint(music.getSongUnlockPoint());
		this.setTooltip(music.getSongName());
		this.setMapPointImage();
		this.setImagePoint(new Point(getImage().getWidth() / 2, getImage().getHeight()));
		this.setJumpOnClick(true);
	}

	public void setCompleted(Boolean completed)
	{
		this.completed = completed;
	}

	public void setMapPointImage()
	{
		if (config.differentiateQuestMarkers() && getMusic().isQuest())
		{
			if (config.differentiateCompletedMarkers())
			{
				if (isCompleted()) this.setImage(ImageUtil.loadImageResource(getClass(), "/music_arrow_quest_completed.png"));
				else this.setImage(ImageUtil.loadImageResource(getClass(), "/music_arrow_quest_incomplete.png"));
			}
			else
			{
				this.setImage(ImageUtil.loadImageResource(getClass(), "/music_arrow_quest_default.png"));
			}
		}
		else
		{
			if (config.differentiateCompletedMarkers())
			{
				if (isCompleted()) this.setImage(ImageUtil.loadImageResource(getClass(), "/music_arrow_completed.png"));
				else this.setImage(ImageUtil.loadImageResource(getClass(), "/music_arrow_incomplete.png"));
			}
			else
			{
				this.setImage(ImageUtil.loadImageResource(getClass(), "/music_arrow_default.png"));
			}
		}

	}
}
