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
package de.walware.ecommons.waltable.painter.cell.decorator;

import static de.walware.ecommons.waltable.painter.cell.GraphicsUtils.safe;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.painter.cell.ICellPainter;
import de.walware.ecommons.waltable.style.CellStyleAttributes;
import de.walware.ecommons.waltable.style.CellStyleUtil;
import de.walware.ecommons.waltable.ui.util.CellEdgeEnum;


/**
 * Decorates a cell painter with another cell painter.
 */
public class CellPainterDecorator implements ICellPainter {

	/**
	 * The base {@link ICellPainter} that is decorated.
	 */
	private final ICellPainter baseCellPainter;
	/**
	 * The edge of the cell at which the decoration is applied.
	 */
	private final CellEdgeEnum cellEdge;
	/**
	 * The {@link ICellPainter} that is used to render the decoration.
	 */
	private final ICellPainter decoratorCellPainter;
	/**
	 * The spacing to use between base painter and decoration painter.
	 * Note: If you want to add <b>padding</b> between the decoration and the cell border
	 * 		 you need to add a PaddingDecorator to your painter stack.
	 */
	private final long spacing;
	/**
	 * Flag to specify whether the base painter should render dependent to the decoration painter
	 * or not. This will have effect on the boundary calculation. Setting this flag to <code>true</code>
	 * the bounds of the base painter will be modified regarding the bounds of the decoration painter.
	 * This means that the starting coordinates for the base painter are moving e.g. if the base painter
	 * renders centered the text will move to the left because the decoration consumes space.
	 * If this flag is set to <code>false</code> you can think of the decoration painter painting on
	 * top of the base painter, possibly painting over the base painter. 
	 */
    private boolean paintDecorationDependent;

    /**
     * Will create a {@link CellPainterDecorator} with the default spacing of 2 between base and 
     * decoration painter, where the base painter is rendered dependent to the decoration.
     * @param baseCellPainter The base {@link ICellPainter} that should be decorated
     * @param cellEdge The edge of the cell at which the decoration should be applied
     * @param decoratorCellPainter The {@link ICellPainter} that should be used to render the decoration.
     */
	public CellPainterDecorator(final ICellPainter baseCellPainter, final CellEdgeEnum cellEdge, final ICellPainter decoratorCellPainter) {
		this(baseCellPainter, cellEdge, 2, decoratorCellPainter);
	}

    /**
     * Will create a {@link CellPainterDecorator} with the default spacing of 2 between base and 
     * decoration painter. If paintDecorationDependent is set to <code>false</code>, the spacing will be ignored.
     * @param baseCellPainter The base {@link ICellPainter} that should be decorated
     * @param cellEdge The edge of the cell at which the decoration should be applied
     * @param decoratorCellPainter The {@link ICellPainter} that should be used to render the decoration.
     * @param paintDecorationDependent Flag to specify whether the base painter should render dependent to the 
     * 			decoration painter or not.
     */
	public CellPainterDecorator(final ICellPainter baseCellPainter, final CellEdgeEnum cellEdge, final ICellPainter decoratorCellPainter, final boolean paintDecorationDependent) {
		this(baseCellPainter, cellEdge, 2, decoratorCellPainter, paintDecorationDependent);
	}
	
    /**
     * Will create a {@link CellPainterDecorator} with the given amount of pixels as spacing between base and 
     * decoration painter, where the base painter is rendered dependent to the decoration.
     * @param baseCellPainter The base {@link ICellPainter} that should be decorated
     * @param cellEdge The edge of the cell at which the decoration should be applied
     * @param spacing The amount of pixels that should be used as spacing between decoration and base painter
     * @param decoratorCellPainter The {@link ICellPainter} that should be used to render the decoration.
     */
	public CellPainterDecorator(final ICellPainter baseCellPainter, final CellEdgeEnum cellEdge, final long spacing, final ICellPainter decoratorCellPainter) {
		this(baseCellPainter, cellEdge, spacing, decoratorCellPainter, true);
	}
	
    /**
     * Will create a {@link CellPainterDecorator} with the given amount of pixels as spacing between base and 
     * decoration painter. If paintDecorationDependent is set to <code>false</code>, the spacing will be ignored
     * while the decoration is mainly rendered over the base painter.
     * @param baseCellPainter The base {@link ICellPainter} that should be decorated
     * @param cellEdge The edge of the cell at which the decoration should be applied
     * @param decoratorCellPainter The {@link ICellPainter} that should be used to render the decoration.
     * @param paintDecorationDependent Flag to specify whether the base painter should render dependent to the 
     * 			decoration painter or not.
     */
	public CellPainterDecorator(final ICellPainter baseCellPainter, final CellEdgeEnum cellEdge, final long spacing, final ICellPainter decoratorCellPainter, final boolean paintDecorationDependent) {
		this.baseCellPainter= baseCellPainter;
		this.cellEdge= cellEdge;
		this.spacing= spacing;
		this.decoratorCellPainter= decoratorCellPainter;
		this.paintDecorationDependent= paintDecorationDependent;
	}

	/**
	 * @param paintDecorationDependent <code>true</code> if the base painter should render dependent to 
	 * 			the decoration painter, <code>false</code> if the decoration should be rendered over
	 * 			the base painter.
	 */
	public void setPaintDecorationDependent(final boolean paintDecorationDependent) {
		this.paintDecorationDependent= paintDecorationDependent;
	}
	
    @Override
	public long getPreferredWidth(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		switch (this.cellEdge) {
		case TOP_LEFT:
		case TOP_RIGHT:
		case BOTTOM_LEFT:
		case BOTTOM_RIGHT:
		case BOTTOM:
		case TOP:
		    return this.spacing
				+ Math.max(
						this.baseCellPainter.getPreferredWidth(cell, gc, configRegistry),
						this.decoratorCellPainter.getPreferredWidth(cell, gc, configRegistry)
				);
        default:
            break;
		}

		return this.baseCellPainter.getPreferredWidth(cell, gc, configRegistry)
				+ this.spacing
				+ this.decoratorCellPainter.getPreferredWidth(cell, gc, configRegistry);
	}

    @Override
	public long getPreferredHeight(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
		switch (this.cellEdge) {
		case TOP_LEFT:
		case TOP_RIGHT:
		case BOTTOM_LEFT:
		case BOTTOM_RIGHT:
		case LEFT:
		case RIGHT:
			return this.spacing
				+ Math.max(
						this.baseCellPainter.getPreferredHeight(cell, gc, configRegistry),
						this.decoratorCellPainter.getPreferredHeight(cell, gc, configRegistry)
				);
		default:
		    break;
		}

		return this.baseCellPainter.getPreferredHeight(cell, gc, configRegistry)
				+ this.spacing
				+ this.decoratorCellPainter.getPreferredHeight(cell, gc, configRegistry);
	}

    @Override
	public void paintCell(final ILayerCell cell, final GC gc, final LRectangle adjustedCellBounds, final IConfigRegistry configRegistry) {
		final LRectangle baseCellPainterBounds= this.paintDecorationDependent ? 
				getBaseCellPainterBounds(cell, gc, adjustedCellBounds, configRegistry) : adjustedCellBounds;
		final LRectangle decoratorCellPainterBounds= getDecoratorCellPainterBounds(cell, gc, adjustedCellBounds, configRegistry);
		
		final Color originalBg= gc.getBackground();
		gc.setBackground(CellStyleUtil.getCellStyle(cell, configRegistry).getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR));
		
		gc.fillRectangle(safe(adjustedCellBounds));
		
		gc.setBackground(originalBg);
		
		this.baseCellPainter.paintCell(cell, gc, baseCellPainterBounds, configRegistry);
		this.decoratorCellPainter.paintCell(cell, gc, decoratorCellPainterBounds, configRegistry);
	}

	/**
	 * 
	 * @return The LRectangle which can be used by the base cell painter. 
	 */
	public LRectangle getBaseCellPainterBounds(final ILayerCell cell, final GC gc, final LRectangle adjustedCellBounds, final IConfigRegistry configRegistry) {
		final long preferredDecoratorWidth= this.decoratorCellPainter.getPreferredWidth(cell, gc, configRegistry);
		final long preferredDecoratorHeight= this.decoratorCellPainter.getPreferredHeight(cell, gc, configRegistry);
		
		// grab any extra space:
		final long grabbedPreferredWidth= adjustedCellBounds.width - preferredDecoratorWidth - this.spacing;
		final long grabbedPreferredHeight= adjustedCellBounds.height - preferredDecoratorHeight - this.spacing;
		
		switch (this.cellEdge) {
		case LEFT:
			return new LRectangle(
					adjustedCellBounds.x + preferredDecoratorWidth + this.spacing,
					adjustedCellBounds.y,
					grabbedPreferredWidth,
					adjustedCellBounds.height
					).intersection(adjustedCellBounds);
		case RIGHT:
			return new LRectangle(
					adjustedCellBounds.x,
					adjustedCellBounds.y,
					grabbedPreferredWidth,
					adjustedCellBounds.height
					).intersection(adjustedCellBounds);
		case TOP:
			return new LRectangle(
					adjustedCellBounds.x,
					adjustedCellBounds.y + preferredDecoratorHeight + this.spacing,
					adjustedCellBounds.width,
					grabbedPreferredHeight
					).intersection(adjustedCellBounds);
		case BOTTOM:
			return new LRectangle(
					adjustedCellBounds.x,
					adjustedCellBounds.y,
					adjustedCellBounds.width,
					grabbedPreferredHeight
					).intersection(adjustedCellBounds);
		case TOP_LEFT:
			return new LRectangle(
					adjustedCellBounds.x + preferredDecoratorWidth + this.spacing,
					adjustedCellBounds.y + preferredDecoratorHeight + this.spacing,
					grabbedPreferredWidth,
					grabbedPreferredHeight
					).intersection(adjustedCellBounds);
		case TOP_RIGHT:
			return new LRectangle(
					adjustedCellBounds.x,
					adjustedCellBounds.y + preferredDecoratorHeight + this.spacing,
					grabbedPreferredWidth,
					grabbedPreferredHeight
					).intersection(adjustedCellBounds);
		case BOTTOM_LEFT:
			return new LRectangle(
					adjustedCellBounds.x + preferredDecoratorWidth + this.spacing,
					adjustedCellBounds.y,
					grabbedPreferredWidth,
					grabbedPreferredHeight
					).intersection(adjustedCellBounds);
		case BOTTOM_RIGHT:
			return new LRectangle(
					adjustedCellBounds.x,
					adjustedCellBounds.y,
					grabbedPreferredWidth,
					grabbedPreferredHeight
					).intersection(adjustedCellBounds);
		case NONE:
		    break;
		}
		
		return null;
	}

	/**
	 * @return The LRectangle to paint the decoration.
	 */
	public LRectangle getDecoratorCellPainterBounds(final ILayerCell cell, final GC gc, final LRectangle adjustedCellBounds, final IConfigRegistry configRegistry) {
		final long preferredDecoratorWidth= this.decoratorCellPainter.getPreferredWidth(cell, gc, configRegistry);
		final long preferredDecoratorHeight= this.decoratorCellPainter.getPreferredHeight(cell, gc, configRegistry);
		
		switch (this.cellEdge) {
		case LEFT:
			return new LRectangle(
					adjustedCellBounds.x,
					adjustedCellBounds.y + ((adjustedCellBounds.height - preferredDecoratorHeight) / 2 ),
					preferredDecoratorWidth,
					preferredDecoratorHeight);
		case RIGHT:
			return new LRectangle(
					adjustedCellBounds.x + adjustedCellBounds.width - preferredDecoratorWidth,
					adjustedCellBounds.y + ((adjustedCellBounds.height - preferredDecoratorHeight) / 2 ),
					preferredDecoratorWidth,
					preferredDecoratorHeight);
		case TOP:
			return new LRectangle(
					adjustedCellBounds.x + ((adjustedCellBounds.width - preferredDecoratorWidth) / 2),
					adjustedCellBounds.y,
					preferredDecoratorWidth,
					preferredDecoratorHeight);
		case BOTTOM:
			return new LRectangle(
					adjustedCellBounds.x + ((adjustedCellBounds.width - preferredDecoratorWidth) / 2),
					adjustedCellBounds.y + adjustedCellBounds.height - preferredDecoratorHeight,
					preferredDecoratorWidth,
					preferredDecoratorHeight);
		case TOP_LEFT:
			return new LRectangle(
					adjustedCellBounds.x,
					adjustedCellBounds.y,
					preferredDecoratorWidth,
					preferredDecoratorHeight);
		case TOP_RIGHT:
			return new LRectangle(
					adjustedCellBounds.x + adjustedCellBounds.width - preferredDecoratorWidth,
					adjustedCellBounds.y,
					preferredDecoratorWidth,
					preferredDecoratorHeight);
		case BOTTOM_LEFT:
			return new LRectangle(
					adjustedCellBounds.x,
					adjustedCellBounds.y + adjustedCellBounds.height - preferredDecoratorHeight,
					preferredDecoratorWidth,
					preferredDecoratorHeight);
		case BOTTOM_RIGHT:
			return new LRectangle(
					adjustedCellBounds.x + adjustedCellBounds.width - preferredDecoratorWidth,
					adjustedCellBounds.y + adjustedCellBounds.height - preferredDecoratorHeight,
					preferredDecoratorWidth,
					preferredDecoratorHeight);
		case NONE:
		    break;
		}
		
		return null;
	}
	
	@Override
	public ICellPainter getCellPainterAt(final long x, final long y, final ILayerCell cell, final GC gc, final LRectangle adjustedCellBounds, final IConfigRegistry configRegistry) {
		final LRectangle decoratorCellPainterBounds= getDecoratorCellPainterBounds(cell, gc, adjustedCellBounds, configRegistry);
		if (decoratorCellPainterBounds.contains(x, y)) {
			return this.decoratorCellPainter.getCellPainterAt(x, y, cell, gc, decoratorCellPainterBounds, configRegistry);
		} else {
			final LRectangle baseCellPainterBounds= this.paintDecorationDependent ? 
					getBaseCellPainterBounds(cell, gc, adjustedCellBounds, configRegistry) : adjustedCellBounds;
			if (baseCellPainterBounds.contains(x, y)) {
				return this.baseCellPainter.getCellPainterAt(x, y, cell, gc, baseCellPainterBounds, configRegistry);
			}
		}
		return this;
	}
}
