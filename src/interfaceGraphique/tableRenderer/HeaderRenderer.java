package interfaceGraphique.tableRenderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class HeaderRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public HeaderRenderer() {
		setHorizontalAlignment(CENTER);
		setHorizontalTextPosition(CENTER);
		setVerticalAlignment(CENTER);
		setOpaque(false);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);
		JTableHeader tableHeader = table.getTableHeader();
		if (tableHeader != null) {
			setForeground(tableHeader.getForeground());
		}

		Border border = new MatteBorder(0, 1, 0, 1, tableHeader.getForeground());
		setBorder(border);
		return this;
	}

}
