package com.musiccapehelper.ui.rows;

import com.musiccapehelper.enums.data.MusicData;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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
import net.runelite.client.util.AsyncBufferedImage;

public class MusicRowItems extends JPanel
{
	private final Color backgroundColour = new Color(85,85,85);
	public MusicRowItems(MusicData musicData, ItemManager itemManager, ClientThread clientThread, Font font)
	{

		if (!musicData.getItems().isEmpty())
		{
			setLayout(new BorderLayout());
			setBorder(new EmptyBorder(5, 0, 5, 0));
			setOpaque(false);

			JLabel itemsPanelLabel = new JLabel();
			itemsPanelLabel.setText("Items Required: ");
			itemsPanelLabel.setHorizontalAlignment(JLabel.LEFT);
			itemsPanelLabel.setFont(font);
			add(itemsPanelLabel, BorderLayout.PAGE_START);

			JPanel itemsPanel = new JPanel();
			itemsPanel.setLayout(new GridLayout(0, 4, 5, 5));
			itemsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
			itemsPanel.setBorder(new LineBorder(ColorScheme.SCROLL_TRACK_COLOR));
			itemsPanel.setBackground(backgroundColour);

			musicData.getItems().forEach(i ->
			{
					SwingUtilities.invokeLater(() ->
					{
						JPanel itemWrapperPanel = new JPanel();
						itemWrapperPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
						itemWrapperPanel.setLayout(new BoxLayout(itemWrapperPanel, BoxLayout.PAGE_AXIS));
						itemWrapperPanel.setBackground(backgroundColour);

						JLabel itemLabel = new JLabel();
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

						clientThread.invokeLater(() ->
						{
							String labelToolTextName = itemManager.getItemComposition(i).getMembersName();
							AsyncBufferedImage itemImage = itemManager.getImage(i);

							itemImage.addTo(itemLabel);
							itemLabel.setToolTipText(labelToolTextName);
						});

						itemWrapperPanel.add(itemLabel);
						itemsPanel.add(itemWrapperPanel);
					});
			});

			add(itemsPanel, BorderLayout.CENTER);
		}
	}
}
