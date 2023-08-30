package com.musiccapehelper.ui.rows;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.data.HeaderType;
import com.musiccapehelper.enums.data.IconData;
import com.musiccapehelper.enums.data.MusicData;
import com.musiccapehelper.enums.settings.SettingsOrderBy;
import com.musiccapehelper.ui.panels.Panel;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.JLabel;
import lombok.Getter;
import net.runelite.client.ui.FontManager;

public class HeaderRow extends Row
{
	@Getter
	private HeaderType headerType;

	public HeaderRow(MusicData musicData, MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config, Panel panel)
	{
		//could get the panel from plugin and use that
		super(musicData, plugin, config, panel);
	}

	@Override
	public void setRowTitle()
	{
		rowTitle.setHorizontalAlignment(JLabel.LEFT);
		rowTitle.setFont(FontManager.getRunescapeBoldFont());

		if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION))
		{
			headerType = Arrays.stream(HeaderType.values()).filter(m -> m.getSettingsRegion().equals(musicData.getSettingsRegion())).findFirst().orElse(HeaderType.ERROR);
			rowTitle.setText("Region: " + musicData.getSettingsRegion().getName());
		}
		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
		{
			if (musicData.isRequired())
			{
				headerType = HeaderType.REQUIRED;
				rowTitle.setText("Required tracks: ");
			}
			else
			{
				headerType = HeaderType.OPTIONAL;
				rowTitle.setText("Optional tracks: ");
			}
		}
	}

	@Override
	public void setRowPinIcon()
	{
		if (enabled)
		{
			rowPinIcon.setIcon(IconData.REMOVE_ICON.getIcon());
		}
		else
		{
			rowPinIcon.setIcon(IconData.ADD_ICON.getIcon());
		}
	}

	@Override
	public void setEnabled()
	{
		//todo - could change to use plugin only by comparing filteredList to mapPoints
		//todo- if this is done, changed helperPanel updateAllRows() to use only one line
		if (panel == null)
		{
			return;
		}

		if (headerType.equals(HeaderType.REQUIRED))
		{
			enabled = panel.getRows()
				.stream()
				.filter(r -> r instanceof MusicRow)
				.filter(r -> r.getMusicData().isRequired())
				.allMatch(Row::isEnabled);
		}
		else if (headerType.equals(HeaderType.OPTIONAL))
		{
			enabled = panel.getRows()
				.stream()
				.filter(r -> r instanceof MusicRow)
				.filter(r -> !r.getMusicData().isRequired())
				.allMatch(Row::isEnabled);
		}
		else
		{
			enabled = panel.getRows()
				.stream()
				.filter(r -> r instanceof MusicRow)
				.filter(r -> r.getMusicData().getSettingsRegion().equals(headerType.getSettingsRegion()))
				.allMatch(Row::isEnabled);
		}
	}

	@Override
	public void updateRow()
	{
		setEnabled();
		setRowPinIcon();
		popupMenu.setText();
		revalidate();
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		//pin icon
		if (e.getComponent().equals(rowPinIcon) || e.getComponent().equals(this))
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
	}
}
