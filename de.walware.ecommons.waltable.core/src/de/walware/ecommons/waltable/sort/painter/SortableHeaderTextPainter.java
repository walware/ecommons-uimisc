/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// -depend, ~
package de.walware.ecommons.waltable.sort.painter;

import org.eclipse.swt.graphics.Image;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.painter.cell.CellPainterWrapper;
import de.walware.ecommons.waltable.painter.cell.ICellPainter;
import de.walware.ecommons.waltable.painter.cell.ImagePainter;
import de.walware.ecommons.waltable.painter.cell.TextPainter;
import de.walware.ecommons.waltable.painter.cell.decorator.CellPainterDecorator;
import de.walware.ecommons.waltable.sort.config.DefaultSortConfiguration;
import de.walware.ecommons.waltable.ui.util.CellEdgeEnum;
import de.walware.ecommons.waltable.util.GUIHelper;

public class SortableHeaderTextPainter extends CellPainterWrapper {

	/**
	 * Creates the default {@link SortableHeaderTextPainter} that uses a {@link TextPainter}
	 * as base {@link ICellPainter} and decorate it with the {@link SortIconPainter} on the right
	 * edge of the cell.
	 */
	public SortableHeaderTextPainter() {
		this(new TextPainter());
	}

	/**
	 * Creates a {@link SortableHeaderTextPainter} that uses the given {@link ICellPainter}
	 * as base {@link ICellPainter} and decorate it with the {@link SortIconPainter} on the right
	 * edge of the cell.
	 * @param interiorPainter the base {@link ICellPainter} to use
	 */
	public SortableHeaderTextPainter(final ICellPainter interiorPainter) {
		this(interiorPainter, CellEdgeEnum.RIGHT);
	}

	/**
	 * Creates a {@link SortableHeaderTextPainter} that uses the given {@link ICellPainter}
	 * as base {@link ICellPainter} and decorate it with the {@link SortIconPainter} on the specified
	 * edge of the cell.
	 * @param interiorPainter the base {@link ICellPainter} to use
	 * @param cellEdge the edge of the cell on which the sort indicator decoration should be applied
	 */
	public SortableHeaderTextPainter(final ICellPainter interiorPainter, final CellEdgeEnum cellEdge) {
		this(interiorPainter, cellEdge, new SortIconPainter(true));
	}

	/**
	 * Creates a {@link SortableHeaderTextPainter} that uses the given {@link ICellPainter}
	 * as base {@link ICellPainter} and decorate it with the given {@link ICellPainter} to use for sort
	 * related decoration on the specified edge of the cell.
	 * @param interiorPainter the base {@link ICellPainter} to use
	 * @param cellEdge the edge of the cell on which the sort indicator decoration should be applied
	 * @param decoratorPainter the {@link ICellPainter} that should be used to paint the sort related
	 * 			decoration (by default the {@link SortIconPainter} will be used)
	 */
	public SortableHeaderTextPainter(final ICellPainter interiorPainter, final CellEdgeEnum cellEdge, final ICellPainter decoratorPainter) {
		setWrappedPainter(new CellPainterDecorator(interiorPainter, cellEdge, decoratorPainter));
	}

	//the following constructors are intended to configure the CellPainterDecorator that is created as
	//the wrapped painter of this SortableHeaderTextPainter

    /**
     * Creates a {@link SortableHeaderTextPainter} that uses the given {@link ICellPainter} as base
	 * {@link ICellPainter}. It will use the {@link SortIconPainter} as decorator for sort related 
	 * decorations at the specified cell edge, which can be configured to render the background or 
	 * not via method parameter. With the additional parameters, the behaviour of the created 
	 * {@link CellPainterDecorator} can be configured in terms of rendering.
	 * @param interiorPainter the base {@link ICellPainter} to use
	 * @param cellEdge the edge of the cell on which the sort indicator decoration should be applied
	 * @param paintBg flag to configure whether the {@link SortIconPainter} should paint the background or not
     * @param spacing the number of pixels that should be used as spacing between cell edge and decoration
     * @param paintDecorationDependent flag to configure if the base {@link ICellPainter} should render
     * 			decoration dependent or not. If it is set to <code>false</code>, the base painter will
     * 			always paint at the same coordinates, using the whole cell bounds, <code>true</code>
     * 			will cause the bounds of the cell to shrink for the base painter.
     */
    public SortableHeaderTextPainter(final ICellPainter interiorPainter, final CellEdgeEnum cellEdge,
    		final boolean paintBg, final long spacing, final boolean paintDecorationDependent) {
    	
	    final ICellPainter sortPainter= new SortIconPainter(paintBg);
	    final CellPainterDecorator painter= new CellPainterDecorator(interiorPainter, cellEdge, 
	    		spacing, sortPainter, paintDecorationDependent);
        setWrappedPainter(painter);
    }
	
	/**
	 * Creates a {@link SortableHeaderTextPainter} that uses the given {@link ICellPainter} as base
	 * {@link ICellPainter} and decorate it with the {@link SortIconPainter} on the right
	 * edge of the cell. This constructor gives the opportunity to configure the behaviour of the
	 * {@link SortIconPainter} and the {@link CellPainterDecorator} for some attributes.
	 * Remains because of downwards compatibility.
	 * @param interiorPainter the base {@link ICellPainter} to use
	 * @param paintBg flag to configure whether the {@link SortIconPainter} should paint the background or not
	 * @param interiorPainterToSpanFullWidth flag to configure how the bounds of the base painter should be
	 * 			calculated 
	 */
    public SortableHeaderTextPainter(final ICellPainter interiorPainter, final boolean paintBg, final boolean interiorPainterToSpanFullWidth) {
	    final ICellPainter sortPainter= new SortIconPainter(paintBg);
	    final CellPainterDecorator painter= new CellPainterDecorator(interiorPainter, CellEdgeEnum.RIGHT, 0, sortPainter);
	    painter.setPaintDecorationDependent(!interiorPainterToSpanFullWidth);
        setWrappedPainter(painter);
	}
    
    
	/**
	 * Paints the triangular sort icon images.
	 */
	protected static class SortIconPainter extends ImagePainter {
		
		
		public SortIconPainter(final boolean paintBg) {
			super(null, paintBg);
		}
		
		
		@Override
		protected Image getImage(final ILayerCell cell, final IConfigRegistry configRegistry) {
			Image icon= null;
			
			if (isSortedAscending(cell)) {
				icon= selectDownImage(getSortSequence(cell));
			} else if (isSortedDescending(cell)) {
				icon= selectUpImage(getSortSequence(cell));
			}
			
			return icon;
		}
		
		private boolean isSortedAscending(final ILayerCell cell) {
			return cell.getConfigLabels().hasLabel(DefaultSortConfiguration.SORT_UP_CONFIG_TYPE);
		}
		
		private boolean isSortedDescending(final ILayerCell cell) {
			return cell.getConfigLabels().hasLabel(DefaultSortConfiguration.SORT_DOWN_CONFIG_TYPE);
		}
		
		private int getSortSequence(final ILayerCell cell) {
			for (final String configLabel : cell.getConfigLabels().getLabels()) {
				if (configLabel.startsWith(DefaultSortConfiguration.SORT_SEQ_CONFIG_TYPE)) {
					return Integer.parseInt(configLabel.substring(
							DefaultSortConfiguration.SORT_SEQ_CONFIG_TYPE.length() ));
				}
			}
			return 0;
		}
		
		private Image selectUpImage(final int sortSequence) {
			switch (sortSequence) {
			case 0:
				return GUIHelper.getImage("up_0"); //$NON-NLS-1$
			case 1:
				return GUIHelper.getImage("up_1"); //$NON-NLS-1$
			case 2:
				return GUIHelper.getImage("up_2"); //$NON-NLS-1$
			default:
				return GUIHelper.getImage("up_2"); //$NON-NLS-1$
			}
		}
		
		private Image selectDownImage(final int sortSequence) {
			switch (sortSequence) {
			case 0:
				return GUIHelper.getImage("down_0"); //$NON-NLS-1$
			case 1:
				return GUIHelper.getImage("down_1"); //$NON-NLS-1$
			case 2:
				return GUIHelper.getImage("down_2"); //$NON-NLS-1$
			default:
				return GUIHelper.getImage("down_2"); //$NON-NLS-1$
			}
		}
		
	}
	
}
