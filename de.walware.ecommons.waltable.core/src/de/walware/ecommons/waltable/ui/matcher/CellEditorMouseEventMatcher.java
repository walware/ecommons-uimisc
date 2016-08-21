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
package de.walware.ecommons.waltable.ui.matcher;

import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.edit.EditConfigAttributes;
import de.walware.ecommons.waltable.edit.editor.ICellEditor;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.style.DisplayMode;


/**
 * Implementation of {@link IMouseEventMatcher} that will check if editing should be
 * activated. For this it is possible to specify the region label to react on, the 
 * mouse button that was used to click and if an editor is registered for the cell
 * on which the mouse click was executed. If no region label is specified, only
 * the mouse button and the presence of a cell editor is evaluated. 
 * <p>
 * If not specified, this matcher will react on the left mouse button.
 */
public class CellEditorMouseEventMatcher implements IMouseEventMatcher {

	/**
	 * The label that specifies the region on which this matcher should be attached.
	 * If there is no region label specified, only the button and the presence of 
	 * a configured cell editor will be evaluated for the match.
	 */
	private final String regionLabel;

	/**
	 * The mouse button that need to be pressed or released for this matcher to
	 * react.
	 */
	private final int button;
	
	/**
	 * Will create a new {@link CellEditorMouseEventMatcher} that will only evaluate
	 * the presence of a cell editor and the mouse left click.
	 */
	public CellEditorMouseEventMatcher() {
		this(null, IMouseEventMatcher.LEFT_BUTTON);
	}
	
	/**
	 * Will create a new {@link CellEditorMouseEventMatcher} that will only evaluate
	 * the presence of a cell editor and the specified mouse click.
	 * @param button The mouse button that need to be pressed or released for this 
	 * 			matcher to react.
	 */
	public CellEditorMouseEventMatcher(final int button) {
		this(null, button);
	}
	
	/**
	 * Will create a new {@link CellEditorMouseEventMatcher} for the specified
	 * grid region and the mouse left click.
	 * @param regionLabel the label that specifies the region this matcher should
	 * 			be attached.
	 */
	public CellEditorMouseEventMatcher(final String regionLabel) {
		this(regionLabel, IMouseEventMatcher.LEFT_BUTTON);
	}

	/**
	 * Will create a new {@link CellEditorMouseEventMatcher} for the specified
	 * grid region and mouse button.
	 * @param regionLabel the label that specifies the region this matcher should
	 * 			be attached.
	 * @param button The mouse button that need to be pressed or released for this 
	 * 			matcher to react.
	 */
	public CellEditorMouseEventMatcher(final String regionLabel, final int button) {
		this.regionLabel= regionLabel;
		this.button= button;
	}

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.ui.matcher.IMouseEventMatcher#matches(de.walware.ecommons.waltable.NatTable, org.eclipse.swt.events.MouseEvent, de.walware.ecommons.waltable.layer.LabelStack)
	 */
	@Override
	public boolean matches(final NatTable natTable, final MouseEvent event, final LabelStack regionLabels) {
		if ((this.regionLabel == null || (regionLabels != null && regionLabels.hasLabel(this.regionLabel)))
				&& event.button == this.button) {
			
			final ILayerCell cell= natTable.getCellByPosition(
					natTable.getColumnPositionByX(event.x), 
					natTable.getRowPositionByY(event.y));
			
			//Bug 407598: only perform a check if the click in the body region was performed on a cell
			//cell == null can happen if the viewport is quite large and contains not enough cells to fill it.
			if (cell != null) {
				final ICellEditor cellEditor= natTable.getConfigRegistry().getConfigAttribute(
						EditConfigAttributes.CELL_EDITOR, DisplayMode.EDIT, cell.getConfigLabels().getLabels());
				
				if (cellEditor != null && cellEditor.activateAtAnyPosition()) {
					//if there is a cell editor configured for the cell that was clicked on, the match is found
					return true;
				}
			}
		}
		return false;
	}

}
