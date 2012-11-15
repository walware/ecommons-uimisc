/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// -depend, +
package org.eclipse.nebula.widgets.nattable.ui.matcher;

import org.eclipse.swt.events.MouseEvent;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;


public class MouseEventMatcher implements IMouseEventMatcher {

	
	/**
	 * Indicating that the statemask is a wildcard allowing all combination of all states
	 * in the specified statemask
	 */
	public static final int WILDCARD_MASK = 1;
	
	
	private final int stateMask;
	private final String regionName;
	private final String regionName2;
	private final int button;
	
	
	public MouseEventMatcher() {
		this(0, null, 0);
	}

	public MouseEventMatcher(String eventRegionName) {
		this(0, eventRegionName, 0);
	}

	public MouseEventMatcher(String eventRegion, int button) {
		this(0, eventRegion, button);
	}

	public MouseEventMatcher(int stateMask, String eventRegion) {
		this(stateMask, eventRegion, 0);
	}
	
	/**
	 * Constructor
	 * @param stateMask @see "org.eclipse.swt.events.MouseEvent.stateMask"
	 * @param eventRegion {@linkplain org.eclipse.nebula.widgets.nattable.grid.GridRegionEnum}
	 * @param button @see "org.eclipse.swt.events.MouseEvent.button"<br/>
	 *  	{@link MouseEventMatcher#LEFT_BUTTON}, {@link MouseEventMatcher#RIGHT_BUTTON}
	 *  	can be used for convenience
	 */
	public MouseEventMatcher(int stateMask, String eventRegion, int button) {
		this(stateMask, eventRegion, null, button);
	}
	
	public MouseEventMatcher(int stateMask, String eventRegion1, String eventRegion2, int button) {
		this.stateMask = stateMask;
		this.regionName = eventRegion1;
		this.regionName2 = eventRegion2;
		this.button = button;
	}
	
	
	public int getStateMask() {
		return stateMask;
	}
	
	public String getEventRegion() {
		return regionName;
	}
	
	public int getButton() {
		return button;
	}
	
	public boolean matches(final NatTable natTable, final MouseEvent event,
			final LabelStack regionLabels) {
		return ( (((stateMask & WILDCARD_MASK) == WILDCARD_MASK) ?
						((event.stateMask | stateMask) == stateMask) :
						(event.stateMask == stateMask) )
				&& event.button == button
				&& (regionName == null
						|| (regionLabels != null && (regionLabels.hasLabel(regionName)
									|| regionName2 != null && regionLabels.hasLabel(regionName2) ))));
	}
	
	
	@Override
	public int hashCode() {
		return (this.stateMask + ((regionName != null) ? regionName.hashCode() : -1) * 13) * button;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof MouseEventMatcher)) {
			return false;
		}
		final MouseEventMatcher other = (MouseEventMatcher) obj;
		return (this.stateMask == other.stateMask
				&& this.button == other.button
				&& ((this.regionName != null) ? this.regionName.equals(other.regionName) : null == other.regionName)
				&& ((this.regionName2 != null) ? this.regionName2.equals(other.regionName) : null == other.regionName) );
	}


	public static MouseEventMatcher columnHeaderLeftClick(int mask) {
	    return new MouseEventMatcher(mask, GridRegion.COLUMN_HEADER, LEFT_BUTTON);
	}

	public static MouseEventMatcher columnHeaderRightClick(int mask) {
		return new MouseEventMatcher(mask, GridRegion.COLUMN_HEADER, RIGHT_BUTTON);
	}

	public static MouseEventMatcher rowHeaderLeftClick(int mask) {
		return new MouseEventMatcher(mask, GridRegion.ROW_HEADER, LEFT_BUTTON);
	}
	
	public static MouseEventMatcher rowHeaderRightClick(int mask) {
		return new MouseEventMatcher(mask, GridRegion.ROW_HEADER, RIGHT_BUTTON);
	}

	public static MouseEventMatcher bodyLeftClick(int mask) {
		return new MouseEventMatcher(mask, GridRegion.BODY, LEFT_BUTTON);
	}
	
	public static MouseEventMatcher bodyRightClick(int mask) {
		return new MouseEventMatcher(mask, GridRegion.BODY, RIGHT_BUTTON);
	}

	public static MouseEventMatcher columnGroupHeaderLeftClick(int mask) {
	    return new MouseEventMatcher(mask, GridRegion.COLUMN_GROUP_HEADER, LEFT_BUTTON);
	}
	
	public static MouseEventMatcher columnGroupHeaderRightClick(int mask) {
	    return new MouseEventMatcher(mask, GridRegion.COLUMN_GROUP_HEADER, RIGHT_BUTTON);
	}
	
	public static MouseEventMatcher rowGroupHeaderLeftClick(int mask) {
	    return new MouseEventMatcher(mask, GridRegion.ROW_GROUP_HEADER, LEFT_BUTTON);
	}

	public static MouseEventMatcher rowGroupHeaderRightClick(int mask) {
	    return new MouseEventMatcher(mask, GridRegion.ROW_GROUP_HEADER, RIGHT_BUTTON);
	}
}

