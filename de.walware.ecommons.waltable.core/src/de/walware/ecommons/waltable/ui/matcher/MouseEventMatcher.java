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
// -depend, +
package de.walware.ecommons.waltable.ui.matcher;

import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.layer.LabelStack;


public class MouseEventMatcher implements IMouseEventMatcher {

	
	/**
	 * Indicating that the statemask is a wildcard allowing all combination of all states
	 * in the specified statemask
	 */
	public static final int WILDCARD_MASK= 1;
	
	
	private final int stateMask;
	private final String regionName;
	private final String regionName2;
	private final int button;
	
	
	public MouseEventMatcher() {
		this(0, null, 0);
	}

	public MouseEventMatcher(final String eventRegionName) {
		this(0, eventRegionName, 0);
	}

	public MouseEventMatcher(final String eventRegion, final int button) {
		this(0, eventRegion, button);
	}

	public MouseEventMatcher(final int stateMask, final String eventRegion) {
		this(stateMask, eventRegion, 0);
	}
	
	/**
	 * Constructor
	 * @param stateMask @see "org.eclipse.swt.events.MouseEvent.stateMask"
	 * @param eventRegion {@linkplain de.walware.ecommons.waltable.grid.GridRegion}
	 * @param button @see org.eclipse.swt.events.MouseEvent#button
	 *  	{@link IMouseEventMatcher#LEFT_BUTTON}, {@link IMouseEventMatcher#RIGHT_BUTTON}
	 *  	can be used for convenience
	 */
	public MouseEventMatcher(final int stateMask, final String eventRegion, final int button) {
		this(stateMask, eventRegion, null, button);
	}
	
	public MouseEventMatcher(final int stateMask, final String eventRegion1, final String eventRegion2, final int button) {
		this.stateMask= stateMask;
		this.regionName= eventRegion1;
		this.regionName2= eventRegion2;
		this.button= button;
	}
	
	
	public int getStateMask() {
		return this.stateMask;
	}
	
	public String getEventRegion() {
		return this.regionName;
	}
	
	public int getButton() {
		return this.button;
	}
	
	@Override
	public boolean matches(final NatTable natTable, final MouseEvent event,
			final LabelStack regionLabels) {
		return ( (((this.stateMask & WILDCARD_MASK) == WILDCARD_MASK) ?
						((event.stateMask | this.stateMask) == this.stateMask) :
						(event.stateMask == this.stateMask) )
				&& event.button == this.button
				&& (this.regionName == null
						|| (regionLabels != null && (regionLabels.hasLabel(this.regionName)
									|| this.regionName2 != null && regionLabels.hasLabel(this.regionName2) ))));
	}
	
	
	@Override
	public int hashCode() {
		return (this.stateMask + ((this.regionName != null) ? this.regionName.hashCode() : -1) * 13) * this.button;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof MouseEventMatcher)) {
			return false;
		}
		final MouseEventMatcher other= (MouseEventMatcher) obj;
		return (this.stateMask == other.stateMask
				&& this.button == other.button
				&& ((this.regionName != null) ? this.regionName.equals(other.regionName) : null == other.regionName)
				&& ((this.regionName2 != null) ? this.regionName2.equals(other.regionName) : null == other.regionName) );
	}


	public static MouseEventMatcher columnHeaderLeftClick(final int mask) {
	    return new MouseEventMatcher(mask, GridRegion.COLUMN_HEADER, LEFT_BUTTON);
	}

	public static MouseEventMatcher columnHeaderRightClick(final int mask) {
		return new MouseEventMatcher(mask, GridRegion.COLUMN_HEADER, RIGHT_BUTTON);
	}

	public static MouseEventMatcher rowHeaderLeftClick(final int mask) {
		return new MouseEventMatcher(mask, GridRegion.ROW_HEADER, LEFT_BUTTON);
	}
	
	public static MouseEventMatcher rowHeaderRightClick(final int mask) {
		return new MouseEventMatcher(mask, GridRegion.ROW_HEADER, RIGHT_BUTTON);
	}

	public static MouseEventMatcher bodyLeftClick(final int mask) {
		return new MouseEventMatcher(mask, GridRegion.BODY, LEFT_BUTTON);
	}
	
	public static MouseEventMatcher bodyRightClick(final int mask) {
		return new MouseEventMatcher(mask, GridRegion.BODY, RIGHT_BUTTON);
	}

	public static MouseEventMatcher columnGroupHeaderLeftClick(final int mask) {
	    return new MouseEventMatcher(mask, GridRegion.COLUMN_GROUP_HEADER, LEFT_BUTTON);
	}
	
	public static MouseEventMatcher columnGroupHeaderRightClick(final int mask) {
	    return new MouseEventMatcher(mask, GridRegion.COLUMN_GROUP_HEADER, RIGHT_BUTTON);
	}
	
	public static MouseEventMatcher rowGroupHeaderLeftClick(final int mask) {
	    return new MouseEventMatcher(mask, GridRegion.ROW_GROUP_HEADER, LEFT_BUTTON);
	}

	public static MouseEventMatcher rowGroupHeaderRightClick(final int mask) {
	    return new MouseEventMatcher(mask, GridRegion.ROW_GROUP_HEADER, RIGHT_BUTTON);
	}
}

