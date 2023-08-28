package com.musiccapehelper.enums.data;

import com.musiccapehelper.MusicCapeHelperPlugin;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import net.runelite.client.util.ImageUtil;

public enum Icon
{
	PLUGIN_ICON("/pluginicon.png"),
	ADD_ICON("/addicon.png"),
	REMOVE_ICON("/removeicon.png"),
	UP_ICON("/up_icon.png"),
	DOWN_ICON("/down_icon.png"),
	SHOW_HINT_ARROW("/arrow_show.png"),
	HIDE_HINT_ARROW("/arrow_hide.png");

	private final String path;
	Icon(String path)
	{
		this.path = path;
	}

	public BufferedImage getImage()
	{
		return ImageUtil.loadImageResource(MusicCapeHelperPlugin.class, path);
	}

	public ImageIcon getIcon()
	{
		return new ImageIcon(ImageUtil.loadImageResource(MusicCapeHelperPlugin.class, path));
	}
}
