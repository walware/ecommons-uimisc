package net.sourceforge.nattable.sort.painter;

import net.sourceforge.nattable.config.IConfigRegistry;
import net.sourceforge.nattable.layer.cell.LayerCell;
import net.sourceforge.nattable.painter.cell.CellPainterWrapper;
import net.sourceforge.nattable.painter.cell.ICellPainter;
import net.sourceforge.nattable.painter.cell.ImagePainter;
import net.sourceforge.nattable.painter.cell.TextPainter;
import net.sourceforge.nattable.painter.cell.decorator.CellPainterDecorator;
import net.sourceforge.nattable.sort.SortHeaderLayer;
import net.sourceforge.nattable.ui.util.CellEdgeEnum;
import net.sourceforge.nattable.util.GUIHelper;

import org.eclipse.swt.graphics.Image;

public class SortableHeaderTextPainter extends CellPainterWrapper {

	/**
	 * Default setup, uses the {@link TextPainter} as its companion painter
	 */
	public SortableHeaderTextPainter() {
		setWrappedPainter(new CellPainterDecorator(new TextPainter(), CellEdgeEnum.RIGHT, new SortIconPainter(true)));
	}

	public SortableHeaderTextPainter(ICellPainter integriorPainter, boolean paintBg) {
		setWrappedPainter(new CellPainterDecorator(integriorPainter, CellEdgeEnum.RIGHT, new SortIconPainter(paintBg)));
	}

	/**
	 * Paints the triangular sort icon images.
	 */
	protected static class SortIconPainter extends ImagePainter {

		public SortIconPainter(boolean paintBg) {
			super(null, paintBg);
		}

		@Override
		protected Image getImage(LayerCell cell, IConfigRegistry configRegistry) {
			Image icon = null;

			if (isSortedAscending(cell)) {
				icon = selectDownImage(getSortSequence(cell));
			} else if (isSortedDescending(cell)) {
				icon = selectUpImage(getSortSequence(cell));
			}

			return icon;
		}

		private boolean isSortedAscending(LayerCell cell) {
			return cell.getConfigLabels().hasLabel(SortHeaderLayer.SORT_UP_CONFIG_TYPE);
		}

		private boolean isSortedDescending(LayerCell cell) {
			return cell.getConfigLabels().hasLabel(SortHeaderLayer.SORT_DOWN_CONFIG_TYPE);
		}

		private int getSortSequence(LayerCell cell) {
			int sortSeq = 0;

			for (String configLabel : cell.getConfigLabels().getLabels()) {
				if (configLabel.startsWith(SortHeaderLayer.SORT_SEQ_CONFIG_TYPE)) {
					sortSeq = Integer.parseInt(configLabel.substring(
							SortHeaderLayer.SORT_SEQ_CONFIG_TYPE.length() ));
				}
			}
			return sortSeq;
		}

		private Image selectUpImage(int sortSequence) {
			switch (sortSequence) {
			case 0:
				return GUIHelper.getImage("up_0");
			case 1:
				return GUIHelper.getImage("up_1");
			case 2:
				return GUIHelper.getImage("up_2");
			default:
				return GUIHelper.getImage("up_2");
			}
		}

		private Image selectDownImage(int sortSequence) {
			switch (sortSequence) {
			case 0:
				return GUIHelper.getImage("down_0");
			case 1:
				return GUIHelper.getImage("down_1");
			case 2:
				return GUIHelper.getImage("down_2");
			default:
				return GUIHelper.getImage("down_2");
			}
		}

	}

}
