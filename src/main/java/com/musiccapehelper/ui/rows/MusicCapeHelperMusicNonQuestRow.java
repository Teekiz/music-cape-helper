package com.musiccapehelper.ui.rows;

import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.Music;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.inject.Inject;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.AsyncBufferedImage;

public class MusicCapeHelperMusicNonQuestRow extends JPanel
{
	private final Music music;
	private final ItemManager itemManager;
	private final ClientThread clientThread;
	private HashMap<String, AsyncBufferedImage> itemList = new HashMap<>();
	public MusicCapeHelperMusicNonQuestRow(Music music, ItemManager itemManager, ClientThread clientThread)
	{
		this.music = music;
		this.itemManager = itemManager;
		this.clientThread = clientThread;

		if (!music.getItems().isEmpty())
		{
			setBackground(ColorScheme.DARK_GRAY_COLOR);
			setBorder(new EmptyBorder(10, 0, 10, 0));
			music.getItems().forEach(i ->
			{
				clientThread.invokeLater(() ->
				{
					String labelToolTextName = itemManager.getItemComposition(i).getMembersName();
					AsyncBufferedImage itemImage = itemManager.getImage(i);

					SwingUtilities.invokeLater(() ->
					{
						JLabel itemLabel = new JLabel();
						itemLabel.setIcon(new ImageIcon(itemImage));
						itemLabel.setToolTipText(labelToolTextName);
						add(itemLabel);
					});
				});
			});

		}
	}
}
