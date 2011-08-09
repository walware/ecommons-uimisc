package net.sourceforge.nattable.ui.matcher;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.grid.GridRegion;
import net.sourceforge.nattable.layer.LabelStack;

import org.eclipse.swt.events.MouseEvent;


public class MouseEventMatcher implements IMouseEventMatcher {
	
	
	/**
	 * Indicating that the statemask is a wildcard allowing all combination of all states
	 * in the specified statemask
	 */
	public static final int WILDCARD_MASK = 1;
	
	
	private final String regionName;
	private final int button;
	private final int stateMask;
	
	
	public MouseEventMatcher() {
		this(null, 0, 0);
	}
	
	public MouseEventMatcher(final String eventRegion) {
		this(eventRegion, 0, 0);
	}
	
	public MouseEventMatcher(final String eventRegion, final int button) {
		this(eventRegion, button, 0);
	}
	
	/**
	 * Constructor
	 * @param eventRegion {@linkplain net.sourceforge.nattable.grid.GridRegionEnum}
	 * @param button @see "org.eclipse.swt.events.MouseEvent.button"<br/>
	 *     {@link MouseEventMatcher#LEFT_BUTTON}, {@link MouseEventMatcher#RIGHT_BUTTON}
	 *     can be used for convenience
	 * @param stateMask @see "org.eclipse.swt.events.MouseEvent.stateMask"
	 */
	public MouseEventMatcher(String eventRegion, int button, int stateMask) {
		this.regionName = eventRegion;
		this.button = button;
		this.stateMask = stateMask;
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
		return ( (((stateMask & WILDCARD_MASK) != 0) ?
						((event.stateMask | stateMask) == stateMask) :
						(event.stateMask == stateMask) )
				&& event.button == button
				&& (regionName == null
						|| (regionLabels != null && regionLabels.hasLabel(regionName)) ));
	}
	
	
	@Override
	public int hashCode() {
		return (stateMask
				+ ((regionName != null) ? regionName.hashCode() : -1) * 13)
				* button;
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
		return (stateMask == other.stateMask
				&& button == other.button
				&& ((regionName != null) ?
						other.regionName.equals(regionName) :
						other.regionName == null ));
	}
	

	public static MouseEventMatcher columnHeaderLeftClick(int mask) {
	    return new MouseEventMatcher(GridRegion.COLUMN_HEADER, LEFT_BUTTON, mask);
	}

	public static MouseEventMatcher rowHeaderLeftClick(int mask) {
		return new MouseEventMatcher(GridRegion.ROW_HEADER, LEFT_BUTTON, mask);
	}

	public static MouseEventMatcher bodyLeftClick(int mask) {
		return new MouseEventMatcher(GridRegion.BODY, LEFT_BUTTON, mask);
	}

	public static MouseEventMatcher columnGroupHeaderLeftClick(int mask) {
	    return new MouseEventMatcher(GridRegion.COLUMN_GROUP_HEADER, LEFT_BUTTON, mask);
	}

}
