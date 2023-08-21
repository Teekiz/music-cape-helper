package com.musiccapehelper.ui.rows;

import com.musiccapehelper.enums.Music;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.AsyncBufferedImage;

public class MusicCapeHelperMusicItemRow extends JPanel
{
	private final Music music;
	private final ItemManager itemManager;
	private final ClientThread clientThread;
	private final Color backgroundColour = new Color(85,85,85);

	public MusicCapeHelperMusicItemRow(Music music, ItemManager itemManager, ClientThread clientThread)
	{
		this.music = music;
		this.itemManager = itemManager;
		this.clientThread = clientThread;

		if (!music.getItems().isEmpty())
		{
			setLayout(new BorderLayout());
			setBorder(new EmptyBorder(5, 0, 5, 0));
			setOpaque(false);

			JLabel itemsPanelLabel = new JLabel();
			itemsPanelLabel.setText("Items Required: ");
			itemsPanelLabel.setHorizontalAlignment(JLabel.LEFT);
			itemsPanelLabel.setFont(FontManager.getRunescapeSmallFont());
			add(itemsPanelLabel, BorderLayout.PAGE_START);

			JPanel itemsPanel = new JPanel();
			itemsPanel.setLayout(new GridLayout(0, 5, 5, 5));
			itemsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
			itemsPanel.setBorder(new LineBorder(ColorScheme.SCROLL_TRACK_COLOR));
			itemsPanel.setBackground(backgroundColour);

			music.getItems().forEach(i ->
			{
				clientThread.invokeLater(() ->
				{
					String labelToolTextName = itemManager.getItemComposition(i).getMembersName();
					AsyncBufferedImage itemImage = itemManager.getImage(i);

					SwingUtilities.invokeLater(() ->
					{
						JPanel itemWrapperPanel = new JPanel();
						itemWrapperPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
						itemWrapperPanel.setLayout(new BoxLayout(itemWrapperPanel, BoxLayout.PAGE_AXIS));
						itemWrapperPanel.setBackground(backgroundColour);

						JLabel itemLabel = new JLabel();
						itemImage.addTo(itemLabel);
						itemLabel.setToolTipText(labelToolTextName);
						itemLabel.setBackground(backgroundColour);
						itemLabel.setOpaque(true);
						itemLabel.addMouseListener(new MouseAdapter()
						{
							@Override
							public void mouseEntered(MouseEvent e)
							{
								//darker colour of MEDIUM_GRAY_COLOR
								itemLabel.setBackground(new Color(70, 70, 70));
							}

							@Override
							public void mouseExited(MouseEvent e)
							{
								itemLabel.setBackground(backgroundColour);
							}
						});

						itemWrapperPanel.add(itemLabel);
						itemsPanel.add(itemWrapperPanel);
					});
				});
			});

			add(itemsPanel, BorderLayout.CENTER);
		}
	}
}
