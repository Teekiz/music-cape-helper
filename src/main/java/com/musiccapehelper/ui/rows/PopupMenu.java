package com.musiccapehelper.ui.rows;

import com.musiccapehelper.data.MusicExpandedRows;
import com.musiccapehelper.data.MusicMapPoints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.FontManager;

public class PopupMenu extends JPopupMenu implements ActionListener
{
	private final Row row;
	private final MusicMapPoints musicMapPoints;
	private final MusicExpandedRows musicExpandedRows;
	private final JMenuItem popupMenuIconText;
	private JMenuItem popupMenuBackgroundText;

	public PopupMenu(Row row, MusicMapPoints musicMapPoints, MusicExpandedRows musicExpandedRows)
	{
		this.row = row;
		this.musicMapPoints = musicMapPoints;
		this.musicExpandedRows = musicExpandedRows;

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
			musicMapPoints.rowPinClicked(row);
			setVisible(false);
		}

		if (e.getSource().equals(popupMenuBackgroundText))
		{
			musicExpandedRows.updateExpandedRows((MusicRow) row);
			setVisible(false);
		}
	}
}
