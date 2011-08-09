package net.sourceforge.nattable.ui.matcher;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.layer.LabelStack;

import org.eclipse.swt.events.MouseEvent;


public interface IMouseEventMatcher {
	
	
	public static final int NO_BUTTON = 0;
	public static final int LEFT_BUTTON = 1;
	public static final int RIGHT_BUTTON = 3;
	
	
	/**
	 * Figures out if the mouse event occured in the suplied region.
	 * 
	 * @param event  SWT mouse event
	 * @param region Region object indicating a regoin of the NatTable display area. Example: body, header etc.
	 */
	public boolean matches(NatTable natTable, MouseEvent event, LabelStack regionLabels);
	
}
