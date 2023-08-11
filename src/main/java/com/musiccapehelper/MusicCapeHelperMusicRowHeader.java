package com.musiccapehelper;

import com.musiccapehelper.enums.Music;
import com.musiccapehelper.enums.OrderBy;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.runelite.client.ui.ColorScheme;

public class MusicCapeHelperMusicRowHeader extends JPanel
{
	JLabel rowLabel;
	JLabel spaceLabel;
	JLabel addRemoveAllIcon;

	MusicCapeHelperConfig config;
	Music music;

	public MusicCapeHelperMusicRowHeader(MusicCapeHelperConfig config, Music music)
	{
		this.config = config;
		this.music = music;

		JPanel musicRowHeaderPanel = new JPanel();
		musicRowHeaderPanel.setLayout(new GridLayout(0, 3, 5, 5));
		musicRowHeaderPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		rowLabel = new JLabel();
		spaceLabel = new JLabel("");
		addRemoveAllIcon = new JLabel();

		addRemoveAllIcon.setText("Remove");
		addRemoveAllIcon.setToolTipText("Use this button to remove all map markers");
		//todo add action listener

		if (config.panelSettingOrderBy().equals(OrderBy.REGION))
		{
			rowLabel.setText(music.getRegion().getName());

			//the default is to assume they are all enabled, if there is a match that is not enabled, then set the icon to add all
			/*
			musicRows.stream().filter(m -> m.getMusic().getRegion().equals(music.getRegion())).forEach(o ->
				{
					if (!o.isEnabled())
					{
						addRemoveAllIcon.setText("Add");
						addRemoveAllIcon.setToolTipText("Use this button to add all map markers");
					}
				}
			);
			 */
		}
		else if (config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
		{
			if (music.isRequired())
			{
				rowLabel.setText("Required tracks: ");
			}
			else
			{
				rowLabel.setText("Optional tracks: ");
			}

			/*
			//the default is to assume they are all enabled, if there is a match that is not enabled, then set the icon to add all
			musicRows.stream().filter(m -> m.getMusic().isRequired() == music.isRequired()).forEach(o ->
				{
					if (!o.isEnabled())
					{
						addedRemoveAllIcon.setText("Add");
						addedRemoveAllIcon.setToolTipText("Use this button to add all map markers");
					}
				}
			);

			 */
		}

		musicRowHeaderPanel.add(rowLabel);
		musicRowHeaderPanel.add(spaceLabel);
		musicRowHeaderPanel.add(addRemoveAllIcon);

	}
}
