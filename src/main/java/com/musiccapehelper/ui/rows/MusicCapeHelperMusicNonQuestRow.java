package com.musiccapehelper.ui.rows;

import com.musiccapehelper.enums.Music;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.AsyncBufferedImage;

public class MusicCapeHelperMusicNonQuestRow extends JPanel
{
	private final Music music;
	private final ItemManager itemManager;
	private final ClientThread clientThread;
	public MusicCapeHelperMusicNonQuestRow(Music music, ItemManager itemManager, ClientThread clientThread)
	{
		this.music = music;
		this.itemManager = itemManager;
		this.clientThread = clientThread;

		if (!music.getItems().isEmpty())
		{
			setBackground(ColorScheme.DARK_GRAY_COLOR);
			setLayout(new BorderLayout());
			setBorder(new EmptyBorder(0, 0, 0, 0));

			JLabel itemsPanelLabel = new JLabel();
			itemsPanelLabel.setText("Items Required: ");
			itemsPanelLabel.setHorizontalAlignment(JLabel.LEFT);
			itemsPanelLabel.setFont(FontManager.getRunescapeFont());
			add(itemsPanelLabel, BorderLayout.PAGE_START);

			JPanel itemsPanel = new JPanel();
			itemsPanel.setLayout(new DynamicGridLayout(0,5, 5, 5));
			itemsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
			itemsPanel.setBorder(new LineBorder(ColorScheme.SCROLL_TRACK_COLOR));
			itemsPanel.setBackground(ColorScheme.LIGHT_GRAY_COLOR);

			music.getItems().forEach(i ->
			{
				clientThread.invokeLater(() ->
				{
					String labelToolTextName = itemManager.getItemComposition(i).getMembersName();
					AsyncBufferedImage itemImage = itemManager.getImage(i);

					SwingUtilities.invokeLater(() ->
					{
						JPanel itemWrapperPanel = new JPanel();
						itemWrapperPanel.setBorder(new EmptyBorder(2,2,2,2));
						itemWrapperPanel.setLayout(new BoxLayout(itemWrapperPanel, BoxLayout.PAGE_AXIS));
						itemWrapperPanel.setBackground(ColorScheme.GRAND_EXCHANGE_LIMIT);
						itemWrapperPanel.setPreferredSize(new Dimension(40, 40));

						JLabel itemLabel = new JLabel();
						itemImage.addTo(itemLabel);
						itemLabel.setToolTipText(labelToolTextName);

						itemWrapperPanel.add(itemLabel);
						itemsPanel.add(itemWrapperPanel);
					});
				});
			});

			add(itemsPanel, BorderLayout.CENTER);
		}
	}
}
