package net.sourceforge.nattable.edit.editor;

import net.sourceforge.nattable.coordinate.IRelative.Direction;
import net.sourceforge.nattable.style.CellStyleAttributes;
import net.sourceforge.nattable.style.HorizontalAlignmentEnum;
import net.sourceforge.nattable.style.IStyle;
import net.sourceforge.nattable.util.GUIHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class TextCellEditor extends AbstractCellEditor {

	private EditorSelectionEnum selectionMode = EditorSelectionEnum.ALL;

	private Text text = null;
	private boolean editable = true;
	protected final ControlDecorationProvider decorationProvider = new ControlDecorationProvider();
	
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public final void setSelectionMode(EditorSelectionEnum selectionMode) {
		this.selectionMode = selectionMode;
	}
	
	public final EditorSelectionEnum getSelectionMode() {
		return selectionMode;
	}

	@Override
	protected Control activateCell(final Composite parent, Object originalCanonicalValue, Character initialEditValue) {
		text = createTextControl(parent);
		
		// If we have an initial value, then 
		if (initialEditValue != null) {
			selectionMode = EditorSelectionEnum.END;
			text.setText(initialEditValue.toString());
			selectText();
		} else {
			setCanonicalValue(originalCanonicalValue);
		}

		if (!isEditable()) {
			text.setEditable(false);
		}

		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent event) {
				if ((event.keyCode == SWT.CR && event.stateMask == 0)
						|| (event.keyCode == SWT.KEYPAD_CR && event.stateMask == 0)) {
					commit(Direction.NONE);
				} 
				else if (event.keyCode == SWT.ESC && event.stateMask == 0){
					close();
				}
			}
		});
		
		text.addTraverseListener(new TraverseListener() {
			
			public void keyTraversed(TraverseEvent event) {
				boolean committed = false;
				if (event.keyCode == SWT.TAB && event.stateMask == SWT.SHIFT) {
					committed = commit(Direction.LEFT);
				} else if (event.keyCode == SWT.TAB && event.stateMask == 0) {
					committed = commit(Direction.RIGHT);
				}
				if (!committed) {
					event.doit = false;
				}
			}
		});

		decorationProvider.createErrorDecorationIfRequired(text);
		
		text.forceFocus(); 
		
		return text;
	}

	private void selectText() {
		int textLength = text.getText().length();
		if (textLength > 0) {
			EditorSelectionEnum selectionMode = getSelectionMode();
			if (selectionMode == EditorSelectionEnum.ALL) {
				text.setSelection(0, textLength);
			} else if (selectionMode == EditorSelectionEnum.END) {
				text.setSelection(textLength, textLength);
			}
		}
	}

	public void setErrorDecorationEnabled(boolean enabled) {
		decorationProvider.setErrorDecorationEnabled(enabled);
	}
	
	public void setErrorDecorationText(String errorText) {
		decorationProvider.setErrorDecorationText(errorText);
	}
	
	/**
	 * Force the error decoration hover to show immediately
	 * @param customErrorText  the text to show in the hover popup
	 */
	public void showErrorDecorationHover(String customErrorText) {
		decorationProvider.showErrorDecorationHover(customErrorText);
	}
	
	public void setDecorationPositionOverride(int decorationPositionOverride) {
		decorationProvider.setDecorationPositionOverride(decorationPositionOverride);
	}
	
	protected Text createTextControl(Composite parent) {
		IStyle cellStyle = getCellStyle();
		final Text textControl = new Text(parent, HorizontalAlignmentEnum.getSWTStyle(cellStyle));
		textControl.setBackground(cellStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR));
		textControl.setForeground(cellStyle.getAttributeValue(CellStyleAttributes.FOREGROUND_COLOR));
		textControl.setFont(cellStyle.getAttributeValue(CellStyleAttributes.FONT));
		
		textControl.addKeyListener(new KeyAdapter() {
			private final Color originalColor = textControl.getForeground();
			@Override
			public void keyReleased(KeyEvent e) {
				if (!validateCanonicalValue()) {
					textControl.setForeground(GUIHelper.COLOR_RED);
					decorationProvider.showDecoration();
				} else {
					textControl.setForeground(originalColor);
					decorationProvider.hideDecoration();
				}
			};
		});
		
		return textControl;
	}

	public Object getCanonicalValue() {
		return getDataTypeConverter().displayToCanonicalValue(text.getText());
	}
	
	public void setCanonicalValue(Object canonicalValue) {
		String displayValue = (String) getDataTypeConverter().canonicalToDisplayValue(canonicalValue);
		text.setText(displayValue != null && displayValue.length() > 0 ? displayValue.toString() : "");
		selectText();
	}
	
	@Override
	public void close() {
		super.close();
		
		if (text != null && !text.isDisposed()) {
			text.dispose();
		}
		
		decorationProvider.dispose();
	}
	
	final protected Text getTextWidget() {
		return text;
	}
}
