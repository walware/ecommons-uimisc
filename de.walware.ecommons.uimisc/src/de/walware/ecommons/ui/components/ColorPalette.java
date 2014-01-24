/*=============================================================================#
 # Copyright (c) 2013-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.components;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

import de.walware.ecommons.FastList;
import de.walware.ecommons.graphics.core.ColorDef;
import de.walware.ecommons.graphics.core.ColorRefDef;
import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.util.LayoutUtil;


public class ColorPalette extends Canvas implements IObjValueWidget<ColorDef> {
	
	private static final LineAttributes SELECTION1 = new LineAttributes(1f, SWT.CAP_FLAT, SWT.JOIN_MITER, SWT.LINE_CUSTOM, new float[] { 1f, 1f }, 0f, 10f);
	private static final LineAttributes SELECTION2 = new LineAttributes(1f, SWT.CAP_FLAT, SWT.JOIN_MITER, SWT.LINE_CUSTOM, new float[] { 1f, 1f }, 1f, 10f);
	
	private static final Color G_BACKGROUND = SharedUIResources.getColors().getColor(SharedUIResources.GRAPHICS_BACKGROUND_COLOR_ID);
	private static final ColorDef G_BACKGROUND_DEF = new ColorDef(G_BACKGROUND.getRed(), G_BACKGROUND.getGreen(), G_BACKGROUND.getBlue());
	
	
	private class SWTListener implements Listener, PaintListener {
		
		private boolean fDoubleClick;
		
		@Override
		public void handleEvent(final Event event) {
			int idx;
			switch (event.type) {
			case SWT.Resize:
				updateScroll();
				checkCursor();
				return;
			case SWT.FocusIn:
				fHasFocus = true;
				redraw();
				return;
			case SWT.FocusOut:
				fHasFocus = false;
				redraw();
				return;
			case SWT.Selection:
				redraw();
				return;
			case SWT.MouseHover:
				idx = getColorIdx(event.x, event.y);
				if (idx >= 0) {
					setToolTipText(fColors.get(idx).toString());
				}
				else {
					setToolTipText(""); //$NON-NLS-1$
				}
				return;
			case SWT.MouseDown:
				fDoubleClick = false;
				idx = getColorIdx(event.x, event.y);
				if (idx >= 0) {
					fCursorIdx = idx;
					if (!doSetColor(idx, event.time, 0)) {
						fDoubleClick = true;
						redraw();
					}
				}
				return;
			case SWT.MouseDoubleClick:
				if (fDoubleClick) {
					fDoubleClick = false;
					idx = getColorIdx(event.x, event.y);
					if (idx >= 0 && idx == fSelectionIdx) {
						fCursorIdx = idx;
						if (!doSetColor(idx, event.time, ObjValueEvent.DEFAULT_SELECTION)) {
							redraw();
						}
					}
				}
				return;
			case SWT.KeyDown:
				switch (event.keyCode) {
				case SWT.ARROW_LEFT:
					if (event.stateMask == 0) {
						fCursorIdx--;
						checkCursor();
						redraw();
					}
					return;
				case SWT.ARROW_RIGHT:
					if (event.stateMask == 0) {
						fCursorIdx++;
						checkCursor();
						redraw();
					}
					return;
				case SWT.ARROW_UP:
					if (event.stateMask == 0 && fCursorIdx >= fColumnCount) {
						fCursorIdx -= fColumnCount;
						checkCursor();
						redraw();
					}
					return;
				case SWT.ARROW_DOWN:
					if (event.stateMask == 0 && fCursorIdx < fColors.size() - fColumnCount) {
						fCursorIdx += fColumnCount;
						checkCursor();
						redraw();
					}
					return;
				case SWT.CR:
					doSetColor(fCursorIdx, event.time, ObjValueEvent.DEFAULT_SELECTION);
					return;
				case ' ':
					doSetColor(fCursorIdx, event.time, 0);
					return;
				default:
					return;
				}
			case SWT.Traverse:
				switch (event.detail) {
				case SWT.TRAVERSE_PAGE_NEXT:
				case SWT.TRAVERSE_PAGE_PREVIOUS:
				case SWT.TRAVERSE_ARROW_NEXT:
				case SWT.TRAVERSE_ARROW_PREVIOUS:
				case SWT.TRAVERSE_RETURN:
					event.doit = false;
					return;
				}
				event.doit = true;
			}
		}
		
		@Override
		public void paintControl(final PaintEvent e) {
			final int count = fColors.size();
			if (count == 0 || fColumnCount == 0) {
				return;
			}
			final Rectangle clientArea = getClientArea();
			int idx = getVerticalBar().getSelection() * fColumnCount;
			
			final GC gc = e.gc;
			final Display display = getDisplay();
			int column = 0;
			int x = 1, y = 1;
			while (idx < count) {
				final ColorDef colorDef = fColors.get(idx);
				final Color color = new Color(display, colorDef.getRed(), colorDef.getGreen(), colorDef.getBlue());
				gc.setBackground(color);
				gc.fillRectangle(x, y, fSize - 1, fSize - 1);
				color.dispose();
				
				if (idx == fSelectionIdx) {
					gc.setLineStyle(SWT.LINE_SOLID);
					gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
					gc.drawRectangle(x - 1, y - 1, fSize, fSize);
					gc.setForeground(getBackground());
					gc.drawRectangle(x, y, fSize - 2, fSize - 2);
				}
				else if (idx == count - 1
						&& Math.abs(colorDef.getRed() - G_BACKGROUND_DEF.getRed()) < 8
						&& Math.abs(colorDef.getGreen() - G_BACKGROUND_DEF.getGreen()) < 8
						&& Math.abs(colorDef.getBlue() - G_BACKGROUND_DEF.getBlue()) < 8) {
					gc.setLineStyle(SWT.LINE_SOLID);
					gc.setForeground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
					gc.drawRectangle(x - 1, y - 1, fSize, fSize);
					gc.setForeground(getBackground());
					gc.drawRectangle(x, y, fSize - 2, fSize - 2);
				}
				if (idx == fCursorIdx && fHasFocus) {
					gc.setLineAttributes(SELECTION1);
					gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
					gc.drawRectangle(x, y, fSize - 2, fSize - 2);
					gc.setLineAttributes(SELECTION2);
					gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
					gc.drawRectangle(x, y, fSize - 2, fSize - 2);
				}
				
				idx++;
				column++;
				if (column < fColumnCount) {
					x += fSize;
				}
				else {
					column = 0;
					x = 0;
					y += fSize;
					if (y > clientArea.height) {
						break;
					}
				}
			}
		}
		
	};
	
	
	private final int fSize;
	
	private boolean fHasFocus;
	
	private List<? extends ColorDef> fColors = Collections.emptyList();
	
	private int fSelectionIdx = -1;
	private int fCursorIdx = 0;
	
	private int fColumnCount;
	private int fVisibleRowCount;
	
	private final FastList<IObjValueListener<ColorDef>> fValueListeners = (FastList) new FastList<IObjValueListener>(IObjValueListener.class);
	
	
	public ColorPalette(final Composite parent) {
		super(parent, SWT.V_SCROLL);
		
		fSize = 8 + LayoutUtil.defaultHSpacing() * 2;
		getVerticalBar().setVisible(true);
		
		final SWTListener listener = new SWTListener();
		addPaintListener(listener);
		addListener(SWT.Resize, listener);
		addListener(SWT.FocusIn, listener);
		addListener(SWT.FocusOut, listener);
		getVerticalBar().addListener(SWT.Selection, listener);
		addListener(SWT.MouseVerticalWheel, listener);
		addListener(SWT.MouseDoubleClick, listener);
		addListener(SWT.MouseDown, listener);
		addListener(SWT.MouseHover, listener);
		addListener(SWT.KeyDown, listener);
		addListener(SWT.Traverse, listener);
		updateScroll();
	}
	
	
	public void setColors(final List<? extends ColorDef> colors) {
		fColors = colors;
		updateScroll();
		checkCursor();
	}
	
	
	private void checkCursor() {
		if (fColors.isEmpty()) {
			fCursorIdx = -1;
			return;
		}
		if (fCursorIdx < 0) {
			fCursorIdx = getVerticalBar().getSelection() * fColumnCount;
		}
		if (fCursorIdx >= fColors.size()) {
			fCursorIdx = fColors.size() - 1;
		}
		if (fColumnCount == 0) {
			return;
		}
		final int row = fCursorIdx / fColumnCount;
		final int topRow = getVerticalBar().getSelection();
		if (row < topRow) {
			getVerticalBar().setSelection(row);
		}
		else if (row >= topRow + fVisibleRowCount) {
			getVerticalBar().setSelection(row - fVisibleRowCount + 1);
		}
	}
	
	private void updateScroll() {
		final ScrollBar bar = getVerticalBar();
		final int count = fColors.size();
		final Rectangle clientArea = getClientArea();
		fColumnCount = (clientArea.width - 1) / fSize;
		if (count == 0 || fColumnCount == 0) {
			bar.setEnabled(false);
			bar.setValues(0, 0, 1, 1, 1, 1);
			return;
		}
		final int rows = (count + fColumnCount - 1) / fColumnCount;
		fVisibleRowCount = (clientArea.height - 1) / fSize;
		if (rows <= fVisibleRowCount) {
			bar.setEnabled(false);
			bar.setValues(0, 0, 1, 1, 1, 1);
			return;
		}
		bar.setEnabled(true);
		bar.setValues(0, 0, rows, fVisibleRowCount, 1, fVisibleRowCount);
	}
	
	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		checkWidget();
		final int width = (wHint >= 0) ? wHint : (1 + fSize * 10);
		final int height = (hHint >= 0) ? hHint : (1 + fSize * 9);
		final Rectangle trimmed = computeTrim(0, 0, width, height);
		return new Point(trimmed.width, trimmed.height);
	}
	
	@Override
	public boolean setFocus() {
		return forceFocus();
	}
	
	public int getColorIdx(final int x, final int y) {
		final int count = fColors.size();
		if (count == 0 || fColumnCount == 0) {
			return -1;
		}
		int idx = (getVerticalBar().getSelection() + ((y - 1) / fSize)) * fColumnCount;
		idx += ((x - 1) / fSize);
		return (idx < count) ? idx : -1;
	}
	
	@Override
	public Control getControl() {
		return this;
	}
	
	@Override
	public Class<ColorDef> getValueType() {
		return ColorDef.class;
	}
	
	@Override
	public void addValueListener(final IObjValueListener<ColorDef> listener) {
		fValueListeners.add(listener);
	}
	
	@Override
	public void removeValueListener(final IObjValueListener<ColorDef> listener) {
		fValueListeners.remove(listener);
	}
	
	private boolean doSetColor(final int idx, final int time, final int flags) {
		final ColorDef oldValue = (fSelectionIdx >= 0) ? fColors.get(fSelectionIdx) : null;
		final ColorDef newValue = (idx >= 0) ? fColors.get(idx) : null;
		if (oldValue == newValue && flags == 0) {
			return false;
		}
		final IObjValueListener<ColorDef>[] listeners = fValueListeners.toArray();
		final ObjValueEvent<ColorDef> event = new ObjValueEvent<ColorDef>(this, time, 0,
				oldValue, newValue, flags);
		
		fSelectionIdx = idx;
		for (int i = 0; i < listeners.length; i++) {
			event.newValue = newValue;
			listeners[i].valueChanged(event);
		}
		if (!isDisposed()) {
			redraw();
		}
		return true;
	}
	
	@Override
	public ColorDef getValue(final int idx) {
		if (idx != 0) {
			throw new IllegalArgumentException("idx: " + idx); //$NON-NLS-1$
		}
		return (fSelectionIdx >= 0) ? fColors.get(fSelectionIdx) : null;
	}
	
	@Override
	public void setValue(final int idx, final ColorDef value) {
		if (idx != 0) {
			throw new IllegalArgumentException("idx: " + idx); //$NON-NLS-1$
		}
		if (value != null) {
			for (int i = 0; i < fColors.size(); i++) {
				final ColorDef c = fColors.get(i);
				if (c.equals(value)) {
					setValue(i);
					return;
				}
			}
			if (value instanceof ColorRefDef) {
				final ColorDef ref = ((ColorRefDef) value).getRef();
				for (int i = 0; i < fColors.size(); i++) {
					final ColorDef c = fColors.get(i);
					if (c.equals(ref)) {
						setValue(i);
						return;
					}
				}
			}
			for (int i = 0; i < fColors.size(); i++) {
				final ColorDef c = fColors.get(i);
				if (c.equalsRGB(value)) {
					setValue(i);
					return;
				}
			}
		}
		setValue(-1);
	}
	
	public void setValue(final int idx) {
		fCursorIdx = idx;
		checkCursor();
		if (fSelectionIdx != idx) {
			if (doSetColor(idx, 0, 0)) {
				return;
			}
		}
		redraw();
	}
	
	public void setCursor(final int idx, final ColorDef value) {
		if (idx != 0) {
			throw new IllegalArgumentException("idx: " + idx); //$NON-NLS-1$
		}
		int colorIdx = -1;
		for (int i = 0; i < fColors.size(); i++) {
			final ColorDef c = fColors.get(i);
			if (c.equals(value)) {
				colorIdx = i;
				break;
			}
			if (colorIdx == -1 && c.equalsRGB(value)) {
				colorIdx = i;
			}
		}
		if (colorIdx >= 0) {
			fCursorIdx = colorIdx;
			checkCursor();
			redraw();
		}
	}
	
}