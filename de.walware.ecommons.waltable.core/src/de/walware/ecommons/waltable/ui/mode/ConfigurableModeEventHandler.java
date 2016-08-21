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
package de.walware.ecommons.waltable.ui.mode;


import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.edit.EditUtils;
import de.walware.ecommons.waltable.ui.NatEventData;
import de.walware.ecommons.waltable.ui.action.IDragMode;
import de.walware.ecommons.waltable.ui.action.IKeyAction;
import de.walware.ecommons.waltable.ui.action.IMouseAction;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;

public class ConfigurableModeEventHandler extends AbstractModeEventHandler {

	private final NatTable natTable;
		
	public ConfigurableModeEventHandler(final ModeSupport modeSupport, final NatTable natTable) {
		super(modeSupport);
		
		this.natTable= natTable;
	}
	
	// Event handling /////////////////////////////////////////////////////////
	
	@Override
	public void keyPressed(final KeyEvent event) {
		final IKeyAction keyAction= this.natTable.getUiBindingRegistry().getKeyEventAction(event);
		if (keyAction != null) {
			this.natTable.forceFocus();
			keyAction.run(this.natTable, event);
		}
	}
	
	@Override
	public void mouseDown(final MouseEvent event) {
		if (EditUtils.commitAndCloseActiveEditor()) {
			final IMouseAction mouseDownAction= this.natTable.getUiBindingRegistry().getMouseDownAction(event);
			if (mouseDownAction != null) {
				event.data= NatEventData.createInstanceFromEvent(event);
				mouseDownAction.run(this.natTable, event);
			}
			
			final IMouseAction singleClickAction= getUiBindingRegistry().getSingleClickAction(event);
			final IMouseAction doubleClickAction= getUiBindingRegistry().getDoubleClickAction(event);
			final IDragMode dragMode= this.natTable.getUiBindingRegistry().getDragMode(event);
			
			if (singleClickAction != null || doubleClickAction != null || dragMode != null) {
				switchMode(new MouseModeEventHandler(getModeSupport(), this.natTable, event, singleClickAction, doubleClickAction, dragMode));
			}
		}
	}

	@Override
	public synchronized void mouseMove(final MouseEvent event) {
		if (event.x >= 0 && event.y >= 0) {
			final IMouseAction mouseMoveAction= getUiBindingRegistry().getMouseMoveAction(event);
			if (mouseMoveAction != null) {
				event.data= NatEventData.createInstanceFromEvent(event);
				mouseMoveAction.run(this.natTable, event);
			} else {
				this.natTable.setCursor(null);
			}
		}
	}

	@Override
	public synchronized void mouseHover(final MouseEvent event) {
		if (event.x >= 0 && event.y >= 0) {
			final IMouseAction mouseHoverAction= getUiBindingRegistry().getMouseHoverAction(event);
			if (mouseHoverAction != null) {
				event.data= NatEventData.createInstanceFromEvent(event);
				mouseHoverAction.run(this.natTable, event);
			}
		}
	}

	@Override
	public synchronized void mouseEnter(final MouseEvent event) {
		if (event.x >= 0 && event.y >= 0) {
			final IMouseAction mouseEnterAction= getUiBindingRegistry().getMouseEnterAction(event);
			if (mouseEnterAction != null) {
				event.data= NatEventData.createInstanceFromEvent(event);
				mouseEnterAction.run(this.natTable, event);
			} else {
				this.natTable.setCursor(null);
			}
		}
	}

	@Override
	public synchronized void mouseExit(final MouseEvent event) {
		if (event.x >= 0 && event.y >= 0) {
			final IMouseAction mouseExitAction= getUiBindingRegistry().getMouseExitAction(event);
			if (mouseExitAction != null) {
				event.data= NatEventData.createInstanceFromEvent(event);
				mouseExitAction.run(this.natTable, event);
			} else {
				this.natTable.setCursor(null);
			}
		}
	}
	
	private UiBindingRegistry getUiBindingRegistry() {
		return this.natTable.getUiBindingRegistry();
	}

}
