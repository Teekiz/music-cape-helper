package com.musiccapehelper.ui.rows;

import com.musiccapehelper.MusicCapeHelperConfig;
import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.enums.HeaderType;
import com.musiccapehelper.enums.Music;
import com.musiccapehelper.enums.OrderBy;
import com.musiccapehelper.ui.panels.MusicCapeHelperPanel;
import java.util.Arrays;
import javax.swing.JLabel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.FontManager;

public class MusicCapeHelperHeader extends MusicCapeHelperRow
{
	@Getter
	private HeaderType headerType;
	@Getter @Setter
	private boolean enabled = false;
	private String rowIconToolTip = "";

	public MusicCapeHelperHeader(Music music, MusicCapeHelperPlugin plugin, MusicCapeHelperConfig config)
	{
		//could get the panel from plugin and use that
		super(music, false, plugin, config);
	}

	@Override
	public void setRowTitle()
	{
		rowTitle.setHorizontalAlignment(JLabel.LEFT);
		rowTitle.setFont(FontManager.getRunescapeSmallFont());

		if (config.panelSettingOrderBy().equals(OrderBy.REGION))
		{
			headerType = Arrays.stream(HeaderType.values()).filter(m -> m.getRegion().equals(music.getRegion())).findFirst().orElse(HeaderType.ERROR);
			rowTitle.setText("Region: " + music.getRegion().getName());
			rowIconToolTip = "tracks from the region " + music.getRegion().getName();
		}
		else if (config.panelSettingOrderBy().equals(OrderBy.REQUIRED_FIRST) || config.panelSettingOrderBy().equals(OrderBy.OPTIONAL_FIRST))
		{
			if (music.isRequired())
			{
				headerType = HeaderType.REQUIRED;
				rowTitle.setText("Required tracks: ");
				rowIconToolTip = "required tracks";
			}
			else
			{
				headerType = HeaderType.OPTIONAL;
				rowTitle.setText("Optional tracks: ");
				rowIconToolTip = "optional tracks";
			}
		}
	}

	@Override
	public void setRowPinIcon()
	{
		if (enabled)
		{
			rowPinIcon.setIcon(plugin.getRemoveIcon());
			rowPinIcon.setToolTipText("Click to unpin all " + rowIconToolTip + " icons from map");
			this.setToolTipText("Click to unpin all " + rowIconToolTip + " icons from map");
		}
		else
		{
			rowPinIcon.setIcon(plugin.getAddIcon());
			rowPinIcon.setToolTipText("Click to pin all icons " + rowIconToolTip + " to the map");
			this.setToolTipText("Click to pin all icons " + rowIconToolTip + " to the map");
		}
	}

	@Override
	public void setEnabled()
	{
		plugin.loggg("LOIGGGG before " + music.getSongName() + "" + enabled);
		MusicCapeHelperPanel panel = plugin.getMusicCapeHelperPanel();
		if (headerType.equals(HeaderType.REQUIRED))
		{
			enabled = panel.getMusicRows()
				.stream()
				.filter(r -> r.getMusic().isRequired())
				.allMatch(MusicCapeHelperRow::isEnabled);
		}
		else if (headerType.equals(HeaderType.OPTIONAL))
		{
			enabled = panel.getMusicRows()
				.stream()
				.filter(r -> !r.getMusic().isRequired())
				.allMatch(MusicCapeHelperRow::isEnabled);
		}
		else
		{
			enabled = panel.getMusicRows()
				.stream()
				.filter(r -> r.getMusic().getRegion().equals(headerType.getRegion()))
				.allMatch(MusicCapeHelperRow::isEnabled);
		}

		plugin.loggg("LOIGGGG after " + music.getSongName() + "" + enabled);
	}

	@Override
	public void updateRow()
	{
		setEnabled();
		setRowPinIcon();
		revalidate();
	}
}
