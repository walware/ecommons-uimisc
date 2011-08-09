package net.sourceforge.nattable.selection.command;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;


public interface ISelectionCommand {
	
	
	public enum SelectionFlag {
		
		KEEP_SELECTION,
		RANGE_SELECTION;
		
		public static Set<SelectionFlag> NONE = Collections.emptySet();
		
		public static Set<SelectionFlag> newSet() {
			return EnumSet.noneOf(SelectionFlag.class);
		}
		
	}
	
	
	Set<SelectionFlag> getSelectionFlags();
	
}
