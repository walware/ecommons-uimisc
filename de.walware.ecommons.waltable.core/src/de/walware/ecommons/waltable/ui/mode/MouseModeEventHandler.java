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

import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.edit.EditUtils;
import de.walware.ecommons.waltable.ui.NatEventData;
import de.walware.ecommons.waltable.ui.action.DragModeEventHandler;
import de.walware.ecommons.waltable.ui.action.IDragMode;
import de.walware.ecommons.waltable.ui.action.IMouseAction;
import de.walware.ecommons.waltable.ui.action.IMouseClickAction;

public class MouseModeEventHandler extends AbstractModeEventHandler {
	
	private final NatTable natTable;
	
	private final MouseEvent initialMouseDownEvent;
	
	private final IMouseAction singleClickAction;
	
	private final IMouseAction doubleClickAction;
	
	private boolean mouseDown;
	
	private final IDragMode dragMode;
	
	private boolean doubleClick;
	
	public MouseModeEventHandler(final ModeSupport modeSupport, final NatTable natTable, final MouseEvent initialMouseDownEvent, final IMouseAction singleClickAction, final IMouseAction doubleClickAction, final IDragMode dragMode) {
		super(modeSupport);
		
		this.natTable= natTable;
		
		this.mouseDown= true;
		
		this.initialMouseDownEvent= initialMouseDownEvent;
		
		this.singleClickAction= singleClickAction;
		this.doubleClickAction= doubleClickAction;
		this.dragMode= dragMode;
	}
	
	@Override
	public void mouseUp(final MouseEvent event) {
		this.mouseDown= false;
		this.doubleClick= false;
		
		if (this.singleClickAction != null) {
			//convert/validate/commit/close possible open editor
			//needed in case of conversion/validation errors to cancel any action
			if (EditUtils.commitAndCloseActiveEditor()) {
				if (this.doubleClickAction != null &&
						(isActionExclusive(this.singleClickAction) || isActionExclusive(this.doubleClickAction))) {
					//If a doubleClick action is registered and either the single click or the double
					//click action is exclusive, wait to see if this mouseUp is part of a doubleClick or not.
					event.display.timerExec(event.display.getDoubleClickTime(), new Runnable() {
						@Override
						public void run() {
							if (!MouseModeEventHandler.this.doubleClick) {
								executeClickAction(MouseModeEventHandler.this.singleClickAction, event);
							}
						}
					});
				} else {
					executeClickAction(this.singleClickAction, event);
				}
			}
		} 
		else if (this.doubleClickAction == null) {
			//No single or double click action registered when mouseUp detected. Switch back to normal mode.
			switchMode(Mode.NORMAL_MODE);
		}
	}
	
	@Override
	public void mouseDoubleClick(final MouseEvent event) {
		//double click event is fired after second mouse up event, so it needs to be set to true here
		//this way the exclusive single click action knows that it should not execute as a double click was performed
		this.doubleClick= true;
		
		executeClickAction(this.doubleClickAction, event);
	}
	
	@Override
	public synchronized void mouseMove(final MouseEvent event) {
		if (this.mouseDown && this.dragMode != null) {
			if (EditUtils.commitAndCloseActiveEditor()) {
				this.dragMode.mouseDown(this.natTable, this.initialMouseDownEvent);
				switchMode(new DragModeEventHandler(getModeSupport(), this.natTable, this.dragMode));
			}
			else {
				switchMode(Mode.NORMAL_MODE);
			}
		} else {
			// No drag mode registered when mouseMove detected. Switch back to normal mode.
			switchMode(Mode.NORMAL_MODE);
		}
	}

	/**
	 * Executes the given IMouseAction and switches the DisplayMode back to normal.
	 * @param action The IMouseAction that should be executed.
	 * @param event The MouseEvent that triggers the action
	 */
	private void executeClickAction(final IMouseAction action, final MouseEvent event) {
		//convert/validate/commit/close possible open editor
		//needed in case of conversion/validation errors to cancel any action
		if (EditUtils.commitAndCloseActiveEditor()) {
			if (action != null && event != null) {
				event.data= NatEventData.createInstanceFromEvent(event);
				action.run(this.natTable, event);
				// Single click action complete. Switch back to normal mode.
				switchMode(Mode.NORMAL_MODE);
			}
		}
	}
	
	/**
	 * Checks whether the given IMouseAction should be performed exclusive or not.
	 * If there is a single and a double click action configured, by default both
	 * the single and the double click will be performed. This behaviour can be
	 * modified if the given action is of type IMouseClickAction and configured to
	 * be exclusive. In this case the single or the double click action will
	 * be performed.
	 * @param action The IMouseAction to check
	 * @return <code>true</code> if the given IMouseAction should be called exclusively, 
	 * 			<code>false</code> if not.
	 */
	private boolean isActionExclusive(final IMouseAction action) {
		if (action instanceof IMouseClickAction) {
			return ((IMouseClickAction)action).isExclusive();
		}
		return false;
	}
	
}
