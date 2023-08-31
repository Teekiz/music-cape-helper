package com.musiccapehelper.data;

import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.data.MusicData;
import com.musiccapehelper.ui.rows.Row;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import lombok.Getter;
import net.runelite.api.Client;

public class MusicHintArrow
{
	private final MusicCapeHelperPlugin plugin;
	private final Client client;
	private MusicData hintArrowMusicData;
	private final PropertyChangeSupport propertyChangeSupport
		= new PropertyChangeSupport(this);

	public MusicHintArrow(MusicCapeHelperPlugin plugin, Client client)
	{
		this.plugin = plugin;
		this.client = client;
		hintArrowMusicData = null;
	}

	public void clearHintArrow()
	{
		if (client.hasHintArrow())
		{
			hintArrowMusicData = null;
			client.clearHintArrow();
			propertyChanged();
		}
	}

	//clears the hint arrow and either sets it to null or updates the arrow to the new music unlock point
	public void setHintArrow(MusicData music)
	{
		if (plugin.isPlayerLoggedIn())
		{
			client.clearHintArrow();

			//unsets the music
			if (hintArrowMusicData != null && hintArrowMusicData.equals(music))
			{
				hintArrowMusicData = null;
			}
			else
			{
				hintArrowMusicData = music;
				client.setHintArrow(music.getSongUnlockPoint());
			}
			propertyChanged();
		}
	}

	public MusicData getMusicHintArrow()
	{
		return hintArrowMusicData;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void propertyChanged()
	{
		propertyChangeSupport.firePropertyChange("hintArrow", null, null);
	}
}
