package com.musiccapehelper.ui.map;
import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.enums.data.MusicData;
import lombok.Getter;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.util.ImageUtil;

public class MusicWorldMapPoint extends WorldMapPoint
{
	@Getter
	private final MusicData musicData;
	@Getter
	private boolean completed;
	private final MusicCapeHelperConfig config;

	public MusicWorldMapPoint(MusicData musicData, boolean completed, MusicCapeHelperConfig config)
	{
		//ImagePoint code used from "clue scrolls" plugin constructor
		super(null, null);

		this.musicData = musicData;
		this.completed = completed;
		this.config = config;

		this.setName(musicData.getSongName());
		this.setWorldPoint(musicData.getSongUnlockPoint());
		this.setTooltip(musicData.getSongName());
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
		if (config.differentiateQuestMarkers() && getMusicData().isQuest())
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
