package com.musiccapehelper.ui.rows;

import com.musiccapehelper.enums.Music;
import javax.swing.JPanel;

public class MusicCapeHelperMusicEventRow extends JPanel
{
	private final Music music;

	//https://oldschool.runescape.wiki/w/File:Holiday_event_icon.png use this Icon
	public MusicCapeHelperMusicEventRow(Music music)
	{
		this.music = music;
	}
}
