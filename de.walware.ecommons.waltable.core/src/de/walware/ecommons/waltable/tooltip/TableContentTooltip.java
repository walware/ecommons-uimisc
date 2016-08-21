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

package de.walware.ecommons.waltable.tooltip;

import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.widgets.Event;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.coordinate.LPoint;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.layer.cell.CellDisplayConversionUtils;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.painter.cell.CellPainterWrapper;
import de.walware.ecommons.waltable.painter.cell.ICellPainter;
import de.walware.ecommons.waltable.painter.cell.PasswordTextPainter;
import de.walware.ecommons.waltable.style.DisplayMode;


/**
 * {@link ToolTip} implementation for the {@link NatTable} which will show the display
 * value of the cell of which the tooltip is requested.
 * <p>It is possible to configure for which regions the tooltips should be activated.
 * If none are configured, the tooltips are active for every region of the {@link NatTable}.
 * 
 * @version 1.0.0
 */
public class TableContentTooltip extends DefaultToolTip {
	
	/**
	 * The {@link NatTable} instance for which this {@link ToolTip} is used. 
	 */
	protected NatTable natTable;
	/**
	 * The regions of the {@link NatTable} for which this {@link ToolTip} is
	 * active.
	 */
	protected String[] tooltipRegions;
	
	
	/**
	 * Creates a new {@link ToolTip} object, attaches it to the given {@link NatTable}
	 * instance and configures and activates it.
	 * @param natTable The {@link NatTable} instance for which this {@link ToolTip} is used.
	 * @param tooltipRegions The regions of the {@link NatTable} for which this {@link ToolTip} is
	 * 			active. If none are given, the tooltip will be active for all regions.
	 */
	public TableContentTooltip(final NatTable natTable, final String... tooltipRegions) {
		super(natTable, ToolTip.NO_RECREATE, false);
		setPopupDelay(500);
		setShift(new org.eclipse.swt.graphics.Point(10, 10));
		activate();
		this.natTable= natTable;
		this.tooltipRegions= tooltipRegions;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Implementation here means the tooltip is not redrawn unless mouse hover moves outside of the
	 * current cell (the combination of ToolTip.NO_RECREATE style and override of this method).
	 */
	@Override
	protected Object getToolTipArea(final Event event) {
		final long col= this.natTable.getColumnPositionByX(event.x);
		final long row= this.natTable.getRowPositionByY(event.y);
		
		return new LPoint(col, row);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Evaluates the cell for which the tooltip should be rendered and checks the
	 * display value. If the display value is empty <code>null</code> will be returned
	 * which will result in not showing a tooltip.
	 */
	@Override
	protected String getText(final Event event) {
		final long col= this.natTable.getColumnPositionByX(event.x);
		final long row= this.natTable.getRowPositionByY(event.y);
		
		final ILayerCell cell= this.natTable.getCellByPosition(col, row);
		if (cell != null) {
			//if the registered cell painter is the PasswordCellPainter, there will be no tooltip
			final ICellPainter painter= this.natTable.getConfigRegistry().getConfigAttribute(
					CellConfigAttributes.CELL_PAINTER, DisplayMode.NORMAL, cell.getConfigLabels().getLabels());
			if (isVisibleContentPainter(painter)) {
				final String tooltipValue= CellDisplayConversionUtils.convertDataType(
						cell, 
						this.natTable.getConfigRegistry());
				
				if (tooltipValue.length() > 0) {
					return tooltipValue;
				}
			}
		}
		return null;
	}
	
	/**
	 * Checks if the given {@link ICellPainter} is showing the content directly or if it is
	 * anonymized by using the {@link PasswordTextPainter}
	 * @param painter The {@link ICellPainter} to check.
	 * @return <code>true</code> if the painter is not a {@link PasswordTextPainter}
	 */
	protected boolean isVisibleContentPainter(final ICellPainter painter) {
		if (painter instanceof PasswordTextPainter) {
			return false;
		} else if (painter instanceof CellPainterWrapper) {
			return isVisibleContentPainter(((CellPainterWrapper) painter).getWrappedPainter());
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Will only display a tooltip if the value of the cell for which the tooltip 
	 * should be rendered is not empty.
	 * <p>If there are regions configured for which the tooltip should be visible, it
	 * is also checked if the the region for which the tooltip should be rendered is 
	 * in one of the configured tooltip regions.
	 */
	@Override
	protected boolean shouldCreateToolTip(final Event event) {
		//check the region?
		boolean regionCheckPassed= false;
		if (this.tooltipRegions.length > 0) {
			final LabelStack regionLabels= this.natTable.getRegionLabelsByXY(event.x, event.y);
			if (regionLabels != null) {
				for (final String label : this.tooltipRegions) {
					if (regionLabels.hasLabel(label)) {
						regionCheckPassed= true;
						break;
					}
				}
			}
		}
		else {
			regionCheckPassed= true;
		}
		
		if (regionCheckPassed && getText(event) != null) {
			return super.shouldCreateToolTip(event);
		}
		
		return false;
	}
	
}
