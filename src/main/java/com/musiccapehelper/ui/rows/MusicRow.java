package com.musiccapehelper.ui.rows;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.data.IconData;
import com.musiccapehelper.enums.data.MusicData;
import com.musiccapehelper.ui.panels.Panel;
import com.musiccapehelper.ui.rows.addons.MusicRowEvent;
import com.musiccapehelper.ui.rows.addons.MusicRowItems;
import com.musiccapehelper.ui.rows.addons.MusicRowQuest;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import lombok.Getter;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;

public class MusicRow extends Row
{
	@Getter
	protected boolean completed;
	private final JPanel informationPanel = new JPanel();
	private final JLabel hintArrowLabel;

	public MusicRow(MusicData musicData, boolean completed, MusicCapeHelperPlugin plugin,
					MusicCapeHelperConfig config, Panel panel, ItemManager itemManager, ClientThread clientThread)
	{
		super(musicData, plugin, config, panel);
		this.completed = completed;


		//used to standardise all the label fonts (NOTE: description uses a custom version of this because it wouldn't update for some reason)
		Font font = FontManager.getRunescapeSmallFont();
		setTextColour();

		//this is for the inherited GridBagConstraints
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		informationPanel.setOpaque(false);
		informationPanel.setLayout(new GridBagLayout());

		//this is new GridBagConstraints for this panel only
		GridBagConstraints gbcMusicRow = new GridBagConstraints();
		gbcMusicRow.insets = new Insets(2, 2, 2, 2);

		JLabel songRegionLabel = new JLabel("Region: " + musicData.getSettingsRegion().getName(), JLabel.LEFT);
		songRegionLabel.setFont(font);
		songRegionLabel.setHorizontalAlignment(JLabel.LEFT);
		gbcMusicRow.ipadx = 0;
		gbcMusicRow.gridx = 0;
		gbcMusicRow.gridy = 0;
		gbcMusicRow.anchor = GridBagConstraints.SOUTHWEST;
		informationPanel.add(songRegionLabel, gbcMusicRow);

		hintArrowLabel = new JLabel();
		hintArrowLabel.setFont(font);
		hintArrowLabel.setHorizontalAlignment(JLabel.LEFT);
		gbcMusicRow.anchor = GridBagConstraints.SOUTHEAST;
		gbcMusicRow.gridx = 2;
		gbcMusicRow.weighty = 0.0;
		setHintArrowLabel();
		informationPanel.add(hintArrowLabel, gbcMusicRow);
		hintArrowLabel.addMouseListener(this);

		JLabel songIsQuestLabel = new JLabel();
		if (musicData.isQuest())
		{
			songIsQuestLabel.setText("Quest Unlock: Yes");
		}
		else
		{
			songIsQuestLabel.setText("Quest Unlock: No");
		}
		songIsQuestLabel.setFont(font);
		songIsQuestLabel.setHorizontalAlignment(JLabel.LEFT);
		gbcMusicRow.gridy = 1;
		gbcMusicRow.gridx = 0;
		gbcMusicRow.weightx = 1.0;
		gbcMusicRow.anchor = GridBagConstraints.SOUTHWEST;
		informationPanel.add(songIsQuestLabel, gbcMusicRow);

		//spaces add to line up with pin/unpin
		JLabel songIsRequiredLabel = new JLabel();
		if (musicData.isRequired())
		{
			songIsRequiredLabel.setText("Required       ");
		}
		else
		{
			songIsRequiredLabel.setText("Optional       ");
		}
		songIsRequiredLabel.setFont(font);
		songIsRequiredLabel.setHorizontalAlignment(JLabel.LEFT);
		gbcMusicRow.gridx = 2;
		gbcMusicRow.anchor = GridBagConstraints.SOUTHEAST;
		informationPanel.add(songIsRequiredLabel, gbcMusicRow);

		JLabel descriptionTextAreaLabel = new JLabel();
		descriptionTextAreaLabel.setText("Description:");
		descriptionTextAreaLabel.setFont(font);
		descriptionTextAreaLabel.setHorizontalAlignment(JLabel.LEFT);
		gbcMusicRow.gridy = 2;
		gbcMusicRow.gridx = 0;
		gbcMusicRow.weightx = 1.0;
		gbcMusicRow.anchor = GridBagConstraints.SOUTHWEST;
		informationPanel.add(descriptionTextAreaLabel, gbcMusicRow);

		JTextArea descriptionTextArea = new JTextArea();
		descriptionTextArea.setLineWrap(true);
		descriptionTextArea.setWrapStyleWord(true);
		descriptionTextArea.setEditable(false);
		descriptionTextArea.setOpaque(false);
		descriptionTextArea.setFont(new Font(FontManager.getRunescapeSmallFont().getName(),
		FontManager.getRunescapeSmallFont().getStyle(), 16));
		descriptionTextArea.setFocusable(false);
		descriptionTextArea.setForeground(Color.LIGHT_GRAY);
		descriptionTextArea.append(musicData.getDescription());

		gbcMusicRow.gridy = 3;
		gbcMusicRow.weightx = 1.0;
		gbcMusicRow.anchor = GridBagConstraints.SOUTHWEST;
		gbcMusicRow.gridwidth = GridBagConstraints.REMAINDER;
		gbcMusicRow.fill = GridBagConstraints.HORIZONTAL;
		informationPanel.add(descriptionTextArea, gbcMusicRow);

		gbcMusicRow.gridx = 0;
		gbcMusicRow.gridy = 4;
		gbcMusicRow.weightx = 1.0;
		gbcMusicRow.anchor = GridBagConstraints.SOUTH;

		//events
		if (!musicData.isQuest() && !musicData.isRequired())
		{
			informationPanel.add(new MusicRowEvent(musicData, font), gbcMusicRow);
		}
		//quests
		else if (musicData.isQuest() && musicData.isRequired())
		{
			informationPanel.add(new MusicRowQuest(musicData, font), gbcMusicRow);
		}
		//normal
		else if (!musicData.isQuest() && !musicData.getItems().isEmpty())
		{
			informationPanel.add(new MusicRowItems(musicData, itemManager, clientThread, font), gbcMusicRow);
		}

		add(informationPanel, gbc);
		setExpanded();
		setRowTitle();

	}

	public void setHintArrowLabel()
	{
		if (plugin.getHintArrowMusicData() == null || !plugin.getHintArrowMusicData().equals(this.getMusicData()))
		{
			hintArrowLabel.setText("Set Arrow:");
			hintArrowLabel.setIcon(IconData.SHOW_HINT_ARROW.getIcon());
		}
		else
		{
			hintArrowLabel.setText("Unset Arrow:");
			hintArrowLabel.setIcon(IconData.HIDE_HINT_ARROW.getIcon());
		}
		hintArrowLabel.setHorizontalTextPosition(JLabel.LEFT);
		hintArrowLabel.setVerticalTextPosition(JLabel.BOTTOM);
	}

	public void setTextColour()
	{
		if (completed && plugin.isPlayerLoggedIn())
		{
			rowTitle.setForeground(config.panelCompleteTextColour());
		}
		else if (!completed && plugin.isPlayerLoggedIn())
		{
			rowTitle.setForeground(config.panelIncompleteTextColour());
		}
		else
		{
			rowTitle.setForeground(config.panelDefaultTextColour());
		}
	}

	public void setExpanded()
	{
		expanded = plugin.getExpandedRows().getExpandedRows().stream().anyMatch(e -> e.equals(musicData));
		informationPanel.setVisible(expanded);
	}

	@Override
	public void setEnabled()
	{
		enabled = plugin.getMapPoints().stream().anyMatch(m -> m.getMusicData().equals(this.getMusicData()));
	}

	@Override
	public void updateRow()
	{
		setEnabled();
		setExpanded();
		setTextColour();
		setRowTitle();
		setRowPinIcon();
		setHintArrowLabel();
		popupMenu.setText();
		revalidate();
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getComponent().equals(rowPinIcon))
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				plugin.rowPinClicked(this);
			}
			else if (e.getButton() == MouseEvent.BUTTON3)
			{
				popupMenu.setVisible(true);
			}
		}

		//clicking the background
		else if (e.getComponent().equals(this))
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				plugin.getExpandedRows().updateExpandedRows(this);
				panel.updateRow(this);

			}
			else if (e.getButton() == MouseEvent.BUTTON3)
			{
				popupMenu.setVisible(true);
			}
		}

		else if (e.getComponent().equals(hintArrowLabel))
		{
			plugin.setHintArrow(MusicRow.this);
		}
	}
}
