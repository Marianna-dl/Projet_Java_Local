package interfacegraphique.tablerenderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;

import interfacegraphique.tablemodel.ElementTableModel;

/**
 * Gere le rendu des cases des tableaux de l'IHM. 
 *
 */
public class NormalRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Couleur de fond des cellules.
	 */
	private Color cellBackground;
	/**
	 * Couleur du texte des cellules.
	 */
	private Color cellForeground;
	
	/**
	 * Cree le renderer pour les cellules des tableaux. 
	 * @param cellBackground couleur de fond des cellules
	 * @param cellForeground couleur du texte des cellules
	 */
	public NormalRenderer(Color cellBackground, Color cellForeground) {
		Border border = new MatteBorder(0, 0, 3, 0, Color.WHITE);
		setBorder(border);
		setHorizontalAlignment(JLabel.CENTER);
		setOpaque(true);
		this.cellBackground = cellBackground;
		this.cellForeground = cellForeground;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if (value != null) {
			this.setText(value.toString());
		}
		
		setForeground(cellForeground);
		setBackground(cellBackground);
		
		ElementTableModel<?> model = (ElementTableModel<?>) table.getModel();
		
		if (column == model.getIndexNom()) {
			setBackground(model.getColor(row));
		}
		
		return this;
	}

}
