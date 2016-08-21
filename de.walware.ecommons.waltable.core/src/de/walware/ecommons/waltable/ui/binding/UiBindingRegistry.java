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
package de.walware.ecommons.waltable.ui.binding;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.ui.action.IDragMode;
import de.walware.ecommons.waltable.ui.action.IKeyAction;
import de.walware.ecommons.waltable.ui.action.IMouseAction;
import de.walware.ecommons.waltable.ui.matcher.IKeyEventMatcher;
import de.walware.ecommons.waltable.ui.matcher.IMouseEventMatcher;


public class UiBindingRegistry implements IUiBindingRegistry {
	
	private final NatTable natTable;
	
	private final LinkedList<KeyBinding> keyBindings= new LinkedList<>();
	
	private final Map<MouseEventTypeEnum, LinkedList<MouseBinding>> mouseBindingsMap= new HashMap<>();
	
	private final LinkedList<DragBinding> dragBindings= new LinkedList<>();
	
	public UiBindingRegistry(final NatTable natTable) {
		this.natTable= natTable;
	}
	
	// Lookup /////////////////////////////////////////////////////////////////
	
	@Override
	public IKeyAction getKeyEventAction(final KeyEvent event) {
		for (final KeyBinding keyBinding : this.keyBindings) {
			if (keyBinding.getKeyEventMatcher().matches(event)) {
				return keyBinding.getAction();
			}
		}
		return null;
	}
	
	@Override
	public IDragMode getDragMode(final MouseEvent event) {
		final LabelStack regionLabels= this.natTable.getRegionLabelsByXY(event.x, event.y);
		
	    for (final DragBinding dragBinding : this.dragBindings) {
	        if (dragBinding.getMouseEventMatcher().matches(this.natTable, event, regionLabels)) {
	            return dragBinding.getDragMode();
	        }
	    }
	    
		return null;
	}
	
	@Override
	public IMouseAction getMouseMoveAction(final MouseEvent event) {
		return getMouseEventAction(MouseEventTypeEnum.MOUSE_MOVE, event);
	}
	
	@Override
	public IMouseAction getMouseDownAction(final MouseEvent event) {
		return getMouseEventAction(MouseEventTypeEnum.MOUSE_DOWN, event);
	}
	
	@Override
	public IMouseAction getSingleClickAction(final MouseEvent event) {
		return getMouseEventAction(MouseEventTypeEnum.MOUSE_SINGLE_CLICK, event);
	}
	
	@Override
	public IMouseAction getDoubleClickAction(final MouseEvent event) {
		return getMouseEventAction(MouseEventTypeEnum.MOUSE_DOUBLE_CLICK, event);
	}
	
	@Override
	public IMouseAction getMouseHoverAction(final MouseEvent event) {
		return getMouseEventAction(MouseEventTypeEnum.MOUSE_HOVER, event);
	}
	
	@Override
	public IMouseAction getMouseEnterAction(final MouseEvent event) {
		return getMouseEventAction(MouseEventTypeEnum.MOUSE_ENTER, event);		
	}
	
	@Override
	public IMouseAction getMouseExitAction(final MouseEvent event) {
		return getMouseEventAction(MouseEventTypeEnum.MOUSE_EXIT, event);
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	private IMouseAction getMouseEventAction(final MouseEventTypeEnum mouseEventType, final MouseEvent event) {

        // TODO: This code can be made more performant by mapping mouse bindings not only to the mouseEventType but 
	    // also to the region that they are interested in. That way, given an area and an event we can narrow down the
	    // list of mouse bindings that need to be searched. -- Azubuko.Obele
	    
		try {
			final LinkedList<MouseBinding> mouseEventBindings= this.mouseBindingsMap.get(mouseEventType);
			if (mouseEventBindings != null) {
				final LabelStack regionLabels= this.natTable.getRegionLabelsByXY(event.x, event.y);
				
			    for (final MouseBinding mouseBinding : mouseEventBindings) {
			        
			        if (mouseBinding.getMouseEventMatcher().matches(this.natTable, event, regionLabels)) {
			            return mouseBinding.getAction();
			        }
			    }
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Registration ///////////////////////////////////////////////////////////

	// Key
	
	public void registerFirstKeyBinding(final IKeyEventMatcher keyMatcher, final IKeyAction action) {
		this.keyBindings.addFirst(new KeyBinding(keyMatcher, action));
	}
	
	public void registerKeyBinding(final IKeyEventMatcher keyMatcher, final IKeyAction action) {
		this.keyBindings.addLast(new KeyBinding(keyMatcher, action));
	}
	
	public void unregisterKeyBinding(final IKeyEventMatcher keyMatcher) {
		for (final KeyBinding keyBinding : this.keyBindings) {
			if (keyBinding.getKeyEventMatcher().equals(keyMatcher)) {
				this.keyBindings.remove(keyBinding);
				return;
			}
		}
	}
	
	// Drag

	public void registerFirstMouseDragMode(final IMouseEventMatcher mouseEventMatcher, final IDragMode dragMode) {
		this.dragBindings.addFirst(new DragBinding(mouseEventMatcher, dragMode));
	}

	public void registerMouseDragMode(final IMouseEventMatcher mouseEventMatcher, final IDragMode dragMode) {
		this.dragBindings.addLast(new DragBinding(mouseEventMatcher, dragMode));
	}
	
	public void unregisterMouseDragMode(final IMouseEventMatcher mouseEventMatcher) {
		for (final DragBinding dragBinding : this.dragBindings) {
			if (dragBinding.getMouseEventMatcher().equals(mouseEventMatcher)) {
				this.dragBindings.remove(dragBinding);
				return;
			}
		}
	}
	
	// Mouse move
	
	public void registerFirstMouseMoveBinding(final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		registerMouseBinding(true, MouseEventTypeEnum.MOUSE_MOVE, mouseEventMatcher, action);
	}
	
	public void registerMouseMoveBinding(final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		registerMouseBinding(false, MouseEventTypeEnum.MOUSE_MOVE, mouseEventMatcher, action);
	}
	
	public void unregisterMouseMoveBinding(final IMouseEventMatcher mouseEventMatcher) {
		unregisterMouseBinding(MouseEventTypeEnum.MOUSE_MOVE, mouseEventMatcher);
	}
	
	// Mouse down
	
	public void registerFirstMouseDownBinding(final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		registerMouseBinding(true, MouseEventTypeEnum.MOUSE_DOWN, mouseEventMatcher, action);
	}
	
	public void registerMouseDownBinding(final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		registerMouseBinding(false, MouseEventTypeEnum.MOUSE_DOWN, mouseEventMatcher, action);
	}
	
	public void unregisterMouseDownBinding(final IMouseEventMatcher mouseEventMatcher) {
		unregisterMouseBinding(MouseEventTypeEnum.MOUSE_DOWN, mouseEventMatcher);
	}
	
	// Single click
	
	public void registerFirstSingleClickBinding(final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		registerMouseBinding(true, MouseEventTypeEnum.MOUSE_SINGLE_CLICK, mouseEventMatcher, action);
	}
	
	public void registerSingleClickBinding(final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		registerMouseBinding(false, MouseEventTypeEnum.MOUSE_SINGLE_CLICK, mouseEventMatcher, action);
	}
	
	public void unregisterSingleClickBinding(final IMouseEventMatcher mouseEventMatcher) {
		unregisterMouseBinding(MouseEventTypeEnum.MOUSE_SINGLE_CLICK, mouseEventMatcher);
	}
	
	// Double click
	
	public void registerFirstDoubleClickBinding(final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		registerMouseBinding(true, MouseEventTypeEnum.MOUSE_DOUBLE_CLICK, mouseEventMatcher, action);
	}
	
	public void registerDoubleClickBinding(final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		registerMouseBinding(false, MouseEventTypeEnum.MOUSE_DOUBLE_CLICK, mouseEventMatcher, action);
	}
	
	public void unregisterDoubleClickBinding(final IMouseEventMatcher mouseEventMatcher) {
		unregisterMouseBinding(MouseEventTypeEnum.MOUSE_DOUBLE_CLICK, mouseEventMatcher);
	}
	
	// Mouse hover
	
	public void registerFirstMouseHoverBinding(final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		registerMouseBinding(true, MouseEventTypeEnum.MOUSE_HOVER, mouseEventMatcher, action);
	}
	
	public void registerMouseHoverBinding(final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		registerMouseBinding(false, MouseEventTypeEnum.MOUSE_HOVER, mouseEventMatcher, action);
	}
	
	public void unregisterMouseHoverBinding(final IMouseEventMatcher mouseEventMatcher) {
		unregisterMouseBinding(MouseEventTypeEnum.MOUSE_HOVER, mouseEventMatcher);
	}
	
	// Mouse enter
	
	public void registerFirstMouseEnterBinding(final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		registerMouseBinding(true, MouseEventTypeEnum.MOUSE_ENTER, mouseEventMatcher, action);
	}
	
	public void registerMouseEnterBinding(final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		registerMouseBinding(false, MouseEventTypeEnum.MOUSE_ENTER, mouseEventMatcher, action);
	}
	
	public void unregisterMouseEnterBinding(final IMouseEventMatcher mouseEventMatcher) {
		unregisterMouseBinding(MouseEventTypeEnum.MOUSE_ENTER, mouseEventMatcher);
	}
	
	// Mouse exit
	
	public void registerFirstMouseExitBinding(final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		registerMouseBinding(true, MouseEventTypeEnum.MOUSE_EXIT, mouseEventMatcher, action);
	}
	
	public void registerMouseExitBinding(final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		registerMouseBinding(false, MouseEventTypeEnum.MOUSE_EXIT, mouseEventMatcher, action);
	}
	
	public void unregisterMouseExitBinding(final IMouseEventMatcher mouseEventMatcher) {
		unregisterMouseBinding(MouseEventTypeEnum.MOUSE_EXIT, mouseEventMatcher);
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	private void registerMouseBinding(final boolean first, final MouseEventTypeEnum mouseEventType, final IMouseEventMatcher mouseEventMatcher, final IMouseAction action) {
		LinkedList<MouseBinding> mouseEventBindings= this.mouseBindingsMap.get(mouseEventType);
		if (mouseEventBindings == null) {
			mouseEventBindings= new LinkedList<>();
			this.mouseBindingsMap.put(mouseEventType, mouseEventBindings);
		}
		if (first) {
			mouseEventBindings.addFirst(new MouseBinding(mouseEventMatcher, action));
		} else {
			mouseEventBindings.addLast(new MouseBinding(mouseEventMatcher, action));
		}
	}
	
	private void unregisterMouseBinding(final MouseEventTypeEnum mouseEventType, final IMouseEventMatcher mouseEventMatcher) {
		final LinkedList<MouseBinding> mouseBindings= this.mouseBindingsMap.get(mouseEventType);
		for (final MouseBinding mouseBinding : mouseBindings) {
			if (mouseBinding.getMouseEventMatcher().equals(mouseEventMatcher)) {
				mouseBindings.remove(mouseBinding);
				return;
			}
		}
	}
	
	private enum MouseEventTypeEnum {

		MOUSE_DOWN,
		MOUSE_MOVE,
		MOUSE_SINGLE_CLICK,
		MOUSE_DOUBLE_CLICK,
		MOUSE_HOVER,
		MOUSE_ENTER,
		MOUSE_EXIT
	}

}
