package net.sourceforge.nattable.ui.matcher;


import org.eclipse.swt.events.KeyEvent;

public class KeyEventMatcher implements IKeyEventMatcher {

	private int stateMask;
	
	private int keyCode;
	
	public KeyEventMatcher(int keyCode) {
		this(0, keyCode);
	}
	
	public KeyEventMatcher(int stateMask, int keyCode) {
		this.stateMask = stateMask;
		this.keyCode = keyCode;
	}
	
	public int getStateMask() {
		return stateMask;
	}
	
	public int getKeyCode() {
		return keyCode;
	}
	
	public boolean matches(KeyEvent event) {
		boolean stateMaskMatches = stateMask == event.stateMask;
		
		boolean keyCodeMatches = keyCode == event.keyCode;
		
		return stateMaskMatches && keyCodeMatches;
	}
	
	
	public int hashCode() {
		return stateMask + keyCode * 119;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof KeyEventMatcher == false) {
			return false;
		}
		final KeyEventMatcher other = (KeyEventMatcher) obj;
		return (stateMask == other.stateMask
				&& keyCode == other.keyCode);
	}
	
}
