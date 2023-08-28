package com.musiccapehelper.ui.rows;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.data.HeaderType;
import com.musiccapehelper.enums.data.Icon;
import com.musiccapehelper.enums.data.Music;
import com.musiccapehelper.enums.settings.SettingsOrderBy;
import com.musiccapehelper.ui.panels.MusicCapeHelperPanel;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.JLabel;
import lombok.Getter;
import net.runelite.client.ui.FontManager;

public class MusicCapeHelperHeader extends MusicCapeHelperRow
{
	@Getter
	private HeaderType headerType;

	public MusicCapeHelperHeader(Music music, MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config, MusicCapeHelperPanel panel)
	{
		//could get the panel from plugin and use that
		super(music, plugin, config, panel);
	}

	@Override
	public void setRowTitle()
	{
		rowTitle.setHorizontalAlignment(JLabel.LEFT);
		rowTitle.setFont(FontManager.getRunescapeBoldFont());

		if (config.panelSettingOrderBy().equals(SettingsOrderBy.REGION))
		{
			headerType = Arrays.stream(HeaderType.values()).filter(m -> m.getSettingsRegion().equals(music.getSettingsRegion())).findFirst().orElse(HeaderType.ERROR);
			rowTitle.setText("Region: " + music.getSettingsRegion().getName());
		}
		else if (config.panelSettingOrderBy().equals(SettingsOrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(SettingsOrderBy.OPTIONAL_FIRST))
		{
			if (music.isRequired())
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
			rowPinIcon.setIcon(Icon.REMOVE_ICON.getIcon());
		}
		else
		{
			rowPinIcon.setIcon(Icon.ADD_ICON.getIcon());
		}
	}

	@Override
	public void setEnabled()
	{
		if (panel == null)
		{
			return;
		}

		if (headerType.equals(HeaderType.REQUIRED))
		{
			enabled = panel.getPanelRows()
				.stream()
				.filter(r -> r instanceof MusicCapeHelperMusicRow)
				.filter(r -> r.getMusic().isRequired())
				.allMatch(MusicCapeHelperRow::isEnabled);
		}
		else if (headerType.equals(HeaderType.OPTIONAL))
		{
			enabled = panel.getPanelRows()
				.stream()
				.filter(r -> r instanceof MusicCapeHelperMusicRow)
				.filter(r -> !r.getMusic().isRequired())
				.allMatch(MusicCapeHelperRow::isEnabled);
		}
		else
		{
			enabled = panel.getPanelRows()
				.stream()
				.filter(r -> r instanceof MusicCapeHelperMusicRow)
				.filter(r -> r.getMusic().getSettingsRegion().equals(headerType.getSettingsRegion()))
				.allMatch(MusicCapeHelperRow::isEnabled);
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
