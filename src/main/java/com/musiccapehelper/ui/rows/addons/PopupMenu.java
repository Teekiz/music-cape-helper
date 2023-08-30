package com.musiccapehelper.ui.rows.addons;

import com.musiccapehelper.MusicCapeHelperPlugin;
import com.musiccapehelper.ui.panels.Panel;
import com.musiccapehelper.ui.rows.MusicRow;
import com.musiccapehelper.ui.rows.Row;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.FontManager;

public class PopupMenu extends JPopupMenu implements ActionListener
{
	private final MusicCapeHelperPlugin plugin;
	private final Panel panel;
	private final Row row;
	private final JMenuItem popupMenuIconText;
	private JMenuItem popupMenuBackgroundText;

	public PopupMenu(Row row, MusicCapeHelperPlugin plugin, Panel panel)
	{
		this.plugin = plugin;
		this.panel = panel;
		this.row = row;

		setBorder(new EmptyBorder(5, 5, 5, 5));

		popupMenuIconText = new JMenuItem();
		popupMenuIconText.setFont(FontManager.getRunescapeSmallFont());
		popupMenuIconText.addActionListener(this);
		add(popupMenuIconText);

		if (row instanceof MusicRow)
		{
			popupMenuBackgroundText = new JMenuItem();
			popupMenuBackgroundText.setFont(FontManager.getRunescapeSmallFont());
			popupMenuBackgroundText.addActionListener(this);
			add(popupMenuBackgroundText);
		}

		setText();
	}

	public void setText()
	{
		if (row.isEnabled())
		{
			popupMenuIconText.setText("Unpin map icon");
		}
		else
		{
			popupMenuIconText.setText("Pin map icon");
		}

		if (popupMenuBackgroundText != null)
		{
			if (row.isExpanded())
			{
				popupMenuBackgroundText.setText("Hide music details");
			}
			else
			{
				popupMenuBackgroundText.setText("Show music details");
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(popupMenuIconText))
		{
			plugin.rowPinClicked(row);
			setVisible(false);
		}

		if (e.getSource().equals(popupMenuBackgroundText))
		{
			plugin.getExpandedRows().updateExpandedRows((MusicRow) row);
			panel.updateRow(row);
			setVisible(false);
		}
	}
}
