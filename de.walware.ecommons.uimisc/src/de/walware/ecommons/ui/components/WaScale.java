/*=============================================================================#
 # Copyright (c) 2012-2014 Stephan Wahlbrink (WalWare.de) and others.
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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import de.walware.ecommons.FastList;
import de.walware.ecommons.collections.ConstArrayList;
import de.walware.ecommons.ui.internal.UIMiscellanyPlugin;


/**
 * Scale slider widget supporting multiple knobs.
 * 
 * By default it has two knobs to adjust an interval (range).
 */
public class WaScale extends Composite implements IIntValueWidget {
	
	
	private static class StyleData {
		
		
		final int minWidth;
		final int defaultWidth;
		final int height;
		
		final int linePadding = 1;
		
		final int knobHeight;
		final int knobWidth;
		final int knobScaleOffset;
		
		final Image knobLine;
		
		
		public StyleData(final Display display, final int style) {
			final Shell shell = new Shell(display, SWT.NO_TRIM);
			GC gc = null;
			try {
				shell.setSize(200, 200);
				shell.setFont(display.getSystemFont());
				gc = new GC(shell);
				int width = gc.getFontMetrics().getAverageCharWidth() * 2;
				if (width % 2 == 0) {
					width++;
				}
				if (width < 11) {
					width = 11;
				}
				
				final Button button = new Button(shell, SWT.PUSH | style);
				final Point size = button.computeSize(width, (int) (width * 1.333));
				width = size.x;
				if (width % 2 == 0) {
					width++;
				}
				knobWidth = width;
				knobHeight = size.y;
				knobScaleOffset = knobWidth / 2;
				height = knobHeight + 9;
				minWidth = 4 * knobWidth + 2 * knobScaleOffset;
				defaultWidth = 100 + 2 * knobScaleOffset;
				
				knobLine = createKnobLine(display, knobHeight / 2);
			}
			finally {
				if (gc != null) {
					gc.dispose();
				}
				shell.dispose();
			}
		}
		
		private Image createKnobLine(final Display display, final int height) {
			final Image image = new Image(display, 3, height);
			
			final GC gc = new GC(image);
			
			gc.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
			gc.drawLine(0, 0, 0, height);
			gc.drawPoint(1, 0);
			gc.drawLine(2, 0, 2, height);
			
			gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			gc.drawLine(1, 1, 1, height);
			
			gc.dispose();
			
			final ImageData imageData = image.getImageData();
			image.dispose();
			
			final byte[] alphaData = new byte[3 * imageData.height];
			byte alpha = 127;
			int line = 0;
			int i = 0;
			while (line++ < 4) {
				alphaData[i++] = alpha;
				alphaData[i++] = alpha;
				alphaData[i++] = alpha;
				alpha += 16;
			}
			while (line++ < imageData.height) {
				alphaData[i++] = alpha;
				alphaData[i++] = alpha;
				alphaData[i++] = alpha;
			}
			imageData.alphaData = alphaData;
			
			return new Image(display, imageData);
		}
		
	}
	
	private static StyleData gDefaultStyleData;
	private static StyleData gFlatStyleData;
	
	private static int checkSWTStyle(int style) {
		style &= ~(SWT.HORIZONTAL | SWT.VERTICAL);
//		style |= SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND;
		return style;
	}
	
	private static int checkThisStyle(int style, final int swtStyle) {
		style &= (SWT.HORIZONTAL | SWT.VERTICAL);
		if ((style & SWT.HORIZONTAL) != 0) {
			style &= ~SWT.VERTICAL;
		}
		style |= (swtStyle & SWT.FLAT);
		return style;
	}
	
	
	protected class Knob {
		
		
		private int fIdx;
		
		private int fValue;
		
		private Button fButton;
		
		private String fToolTipText;
		
		private boolean fSavedEnabled = true;
		
		
		/**
		 * Create a new know
		 * 
		 * @param value The initial value of the knob
		 */
		public Knob(final int value) {
			fValue = value;
		}
		
		
		public void setToolTipText(final String text) {
			fToolTipText = text;
			if (fButton != null) {
				fButton.setToolTipText(text);
			}
		}
		
		/**
		 * Returns the index of the knob in the list of all knobs ({@link WaScale#getKnobs()}).
		 * 
		 * @return The index of the knob
		 */
		public final int getIdx() {
			return fIdx;
		}
		
		/**
		 * Returns the value of the knob.
		 * 
		 * @return The value of the knob
		 */
		public final int getValue() {
			return fValue;
		}
		
		/**
		 * Sets the value of the knob.
		 * 
		 * The method checks if the value is between minimum and maximum of the scale and adjust
		 * it if required. Sub classes can add additional checks by overwriting the method.
		 * 
		 * @param value the new value of the knob
		 */
		protected int checkValue(int value) {
			if (value < fMinimum) {
				value = fMinimum;
			}
			else if (value > fMaximum) {
				value = fMaximum;
			}
			return value;
		}
		
		/**
		 * Sets the value of the knob and notifies all listeners if the value changed.
		 * 
		 * @param time The time stamp of the event
		 * @param value The new value of the knob
		 */
		public boolean setValue(final int time, int value) {
			final int oldValue = fValue;
			value = checkValue(value);
			value = fireAboutToChange(time, fIdx, oldValue, value);
			value = checkValue(value);
			if (oldValue == value) {
				return false;
			}
			fValue = value;
			fireChanged(time, fIdx, oldValue, fValue);
			return true;
		}
		
	}
	
	protected class OrderedKnob extends Knob {
		
		public OrderedKnob(final int value) {
			super(value);
		}
		
		public OrderedKnob(final int value, final String toolTipText) {
			super(value);
			setToolTipText(toolTipText);
		}
		
		protected int getMinDistance() {
			return 1;
		}
		
		@Override
		protected int checkValue(int value) {
			final List<Knob> knobs = getKnobs();
			final int idx = getIdx();
			final int distance = getMinDistance();
			if (idx + 1 < knobs.size()) {
				final int bound = knobs.get(idx + 1).getValue();
				if (bound - value < distance) {
					value = bound - distance;
				}
			}
			if (idx > 0) {
				final int bound = knobs.get(idx - 1).getValue();
				if (value - bound < distance) {
					value = bound + distance;
				}
			}
			return super.checkValue(value);
		}
		
	}
	
	
	private final int fThisStyle;
	private final StyleData fStyleData;
	
	private Color fColor1;
	private Color fColor2;
	private Color fRangeColor;
	private Color fRangeFocusColor;
	private Color fTickColor;
	
	private Rectangle fScaleArea;
	
	private int fMinimum = 1;
	private int fMaximum = 100;
	
	private int fIncrement = 1;
	private int fPageIncrement = 10;
	
	private List<Knob> fKnobs;
	
	private final FastList<IIntValueListener> fSelectionListeners = new FastList<IIntValueListener>(IIntValueListener.class);
	
	private boolean fIsActive;
	
	private int fOpInProgress;
	private int fOpButton;
	private int fOpSavedValue;
	private int fOpOffset;
	
	
	private final FocusListener fButtonFocusListener = new FocusListener() {
		@Override
		public void focusGained(final FocusEvent event) {
			final Control[] children = getChildren();
			if (children[0] == event.widget) {
				return;
			}
			((Button) event.widget).moveAbove(children[0]);
		}
		@Override
		public void focusLost(final FocusEvent event) {
		}
	};
	private final PaintListener fButtonPaintListener = new PaintListener() {
		@Override
		public void paintControl(final PaintEvent event) {
			paintKnob(event);
		}
	};
	private final Listener fButtonKnobMoveListener = new Listener() {
		@Override
		public void handleEvent(final Event event) {
			if (event.type == SWT.Traverse) {
				switch (event.keyCode) {
				case SWT.ARROW_LEFT:
				case SWT.ARROW_DOWN:
				case SWT.ARROW_RIGHT:
				case SWT.ARROW_UP:
					event.doit = false;
					return;
				default:
					return;
				}
			}
			
			moveKnob(event);
		}
	};
	
	
	public WaScale(final Composite parent, final int style) {
		super(parent, checkSWTStyle(style));
		
		fThisStyle = checkThisStyle(style, getStyle());
		fStyleData = createStyle();
		fKnobs = Collections.emptyList();
		
		updateColors();
		
		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(final PaintEvent event) {
				paint(event);
			}
		});
		final Listener listener = new Listener() {
			@Override
			public void handleEvent(final Event event) {
				switch (event.type) {
				case SWT.Activate:
					fIsActive = true;
					redraw();
					return;
				case SWT.Deactivate:
					fIsActive = false;
					redraw();
					return;
				case SWT.Resize:
					updateLayout();
					return;
				case SWT.Dispose:
					onDispose();
					return;
				default:
					return;
				}
			}
		};
		addListener(SWT.Activate, listener);
		addListener(SWT.Deactivate, listener);
		addListener(SWT.Resize, listener);
		addListener(SWT.Dispose, listener);
		
		initKnobs();
	}
	
	
	private StyleData createStyle() {
		if ((fThisStyle & SWT.FLAT) != 0) {
			if (gFlatStyleData == null) {
				gFlatStyleData = new StyleData(getDisplay(), SWT.FLAT);
			}
			return gFlatStyleData;
		}
		else {
			if (gDefaultStyleData == null) {
				gDefaultStyleData = new StyleData(getDisplay(), SWT.NONE);
			}
			return gDefaultStyleData;
		}
	}
	
	@Override
	public Control getControl() {
		return this;
	}
	
	protected void initKnobs() {
//		setKnobs(new ConstList<Knob>(
//				new DefaultKnob(getMinimum(), "Lower Bound"),
//				new DefaultKnob(getMaximum(), "Upper Bound") ));
		setKnobs(new ConstArrayList<Knob>(
				new Knob(getMinimum()),
				new Knob(getMaximum()) ));
	}
	
	protected void setKnobs(List<Knob> knobs) {
		stopOperations(0);
		
		knobs = new ConstArrayList<Knob>(knobs);
		if (fKnobs != null) {
			for (int idx = 0; idx < fKnobs.size(); idx++) {
				
			}
			for (final Knob knob : knobs) {
				if (knob.fButton != null) {
					knob.fButton.dispose();
					knob.fButton = null;
				}
			}
		}
		
		final Control[] tabList = new Control[knobs.size()];
		final boolean enabled = getEnabled();
		for (int idx = 0; idx < knobs.size(); idx++) {
			final Knob knob = knobs.get(idx);
			knobs.get(idx).fIdx = idx;
			knob.fButton = createKnobControl(knob);
			if (!enabled) {
				knob.fButton.setEnabled(false);
			}
			tabList[idx] = knob.fButton;
		}
		fKnobs = knobs;
		setTabList(tabList);
		
		recheckKnobValues();
		updateLayout();
	}
	
	protected List<Knob> getKnobs() {
		return fKnobs;
	}
	
	private void recheckKnobValues() {
		final List<Knob> knobs = fKnobs;
		for (int i = 0; i < knobs.size(); i++) {
			final Knob knob = knobs.get(i);
			knob.setValue(0, knob.getValue());
		}
	}
	
	
	public int getMinimum() {
		checkWidget();
		return fMinimum;
	}
	
	public void setMinimum(final int value) {
		checkWidget();
		fMinimum = value;
	}
	
	public int getMaximum() {
		checkWidget();
		return fMaximum;
	}
	
	public void setMaximum(final int value) {
		checkWidget();
		fMaximum = value;
	}
	
	public int getPageIncrement() {
		checkWidget();
		return fPageIncrement;
	}
	
	public void setPageIncrement(final int increment) {
		checkWidget();
		fPageIncrement = increment;
	}
	
	public int getIncrement() {
		checkWidget();
		return fIncrement;
	}
	
	public void setIncrement(final int increment) {
		checkWidget();
		fIncrement = increment;
	}
	
	@Override
	public int getValue(final int knobIdx) {
		return fKnobs.get(knobIdx).getValue();
	}
	
	@Override
	public void setValue(final int knobIdx, final int value) {
		fKnobs.get(knobIdx).setValue(0, value);
		updateButtonPositions();
		redraw();
	}
	
	@Override
	public void addValueListener(final IIntValueListener listener) {
		fSelectionListeners.add(listener);
	}
	
	@Override
	public void removeValueListener(final IIntValueListener listener) {
		fSelectionListeners.remove(listener);
	}
	
	private int fireAboutToChange(final int time, final int knobIdx, final int oldValue, final int newValue) {
		if (time == 0) {
			return newValue;
		}
		final IIntValueListener[] listeners = fSelectionListeners.toArray();
		final IntValueEvent scaleEvent = new IntValueEvent(this, time, knobIdx, oldValue, newValue);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].valueAboutToChange(scaleEvent);
		}
		return scaleEvent.newValue;
	}
	
	private void fireChanged(final int time, final int knobIdx, final int oldValue, final int newValue) {
		if (time == 0) {
			return;
		}
		final IIntValueListener[] listeners = fSelectionListeners.toArray();
		final IntValueEvent scaleEvent = new IntValueEvent(this, time, knobIdx, oldValue, newValue);
		for (int i = 0; i < listeners.length; i++) {
			scaleEvent.newValue = newValue;
			listeners[i].valueChanged(scaleEvent);
		}
	}
	
	
	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		if (getEnabled() == enabled) {
			return;
		}
		
		final List<Knob> knobs = fKnobs;
		if (!enabled) {
			for (int idx = 0; idx < knobs.size(); idx++) {
				final Knob knob = knobs.get(idx);
				knob.fSavedEnabled = knob.fButton.getEnabled();
				knob.fButton.setEnabled(false);
			}
		}
		else {
			for (int idx = 0; idx < knobs.size(); idx++) {
				final Knob knob = knobs.get(idx);
				knob.fButton.setEnabled(knob.fSavedEnabled);
			}
		}
	}
	
	protected int convertClient2Value(final int coord) {
		final Rectangle scaleArea = fScaleArea;
		double tmp = coord - scaleArea.x;
		tmp /= scaleArea.width;
		tmp *= ((double) fMaximum - fMinimum);
		tmp += fMinimum;
		return (int) tmp;
	}
	
	protected int convertValue2Client(final int value) {
		final Rectangle scaleArea = fScaleArea;
		double tmp = value - fMinimum;
		tmp /= ((double) fMaximum - fMinimum);
		tmp *= scaleArea.width;
		int scale = (int) tmp;
		if (tmp < 0) {
			scale = 0;
		}
		if (tmp > scaleArea.width) {
			scale = scaleArea.width;
		}
		return scale + scaleArea.x;
	}
	
	
	protected final int getCoord(final Event event) {
		return ((fThisStyle & SWT.VERTICAL) != 0) ? event.y : event.x;
	}
	
	protected final int getCoord(final Point location) {
		return ((fThisStyle & SWT.VERTICAL) != 0) ? location.y : location.x;
	}
	
	@Override
	public Point computeSize(int wHint, final int hHint, final boolean changed) {
		if (wHint < 0) {
			wHint = fStyleData.defaultWidth;
		}
		else if (wHint < fStyleData.minWidth) {
			wHint = fStyleData.minWidth;
		}
		final Rectangle trim = computeTrim (0, 0, wHint, fStyleData.height);
		return new Point (trim.width, trim.height);
	}
	
	@Override
	public void setBackground(final Color color) {
		super.setBackground(color);
		updateColors();
	}
	
	protected void updateColors() {
		fColor1 = computeColor1();
		fColor2 = computeColor2();
		fRangeColor = computeRangeColor(false);
		fRangeFocusColor = computeRangeColor(true);
		fTickColor = computeTickColor();
	}
	
	protected Color computeColor1() {
		Color color;
		final Display display = getDisplay();
		color = display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		if (color.equals(getBackground())) {
			color = display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW);
		}
		return color;
	}
	
	protected Color getColor1() {
		return fColor1;
	}
	
	protected Color computeColor2() {
		Color color;
		final Display display = getDisplay();
		color = display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
		if (color.equals(getBackground())) {
			color = display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
		}
		return color;
	}
	
	protected Color getColor2() {
		return fColor2;
	}
	
	protected Color computeRangeColor(final boolean focus) {
		Color color;
		final Display display = getDisplay();
		if (focus) {
			final RGB color1 = getBackground().getRGB();
			final RGB color2 = display.getSystemColor(SWT.COLOR_LIST_SELECTION).getRGB();
			final RGB rgb = new RGB((color1.red + color2.red) / 2,
					(color1.green + color2.green) / 2, (color1.blue + color2.blue) / 2);
			color = UIMiscellanyPlugin.getDefault().getColorManager().getColor(rgb);
		}
		else {
			color = display.getSystemColor(SWT.COLOR_GRAY);
			if (color.equals(getBackground())) {
				color = display.getSystemColor(SWT.COLOR_DARK_GRAY);
			}
		}
		return color;
	}
	
	protected Color getRangeColor(final boolean focus) {
		return (focus) ? fRangeFocusColor : fRangeColor;
	}
	
	protected Color computeTickColor() {
		return computeColor1();
	}
	
	protected Color getTickColor() {
		return fTickColor;
	}
	
	
	
	private Button createKnobControl(final Knob knob) {
		final Button button = new Button(this, SWT.PUSH | (fThisStyle & SWT.FLAT));
		button.setSize(fStyleData.knobWidth, fStyleData.knobHeight);
		button.addFocusListener(fButtonFocusListener);
		button.addPaintListener(fButtonPaintListener);
		button.addListener(SWT.Traverse, fButtonKnobMoveListener);
		button.addListener(SWT.KeyDown, fButtonKnobMoveListener);
		button.addListener(SWT.MouseWheel, fButtonKnobMoveListener);
		button.addListener(SWT.MouseDown, fButtonKnobMoveListener);
		button.addListener(SWT.MouseMove, fButtonKnobMoveListener);
		button.addListener(SWT.MouseUp, fButtonKnobMoveListener);
		button.setData(knob);
		button.setToolTipText(knob.fToolTipText);
		final Region shape = new Region(button.getDisplay());
		shape.add(0, 0, fStyleData.knobWidth, fStyleData.knobHeight - 1);
		button.setRegion(shape);
		return button;
	}
	
	private void updateLayout() {
		final Rectangle scaleArea = getClientArea();
		if ((fThisStyle & SWT.VERTICAL) != 0) {
			scaleArea.x = scaleArea.y;
			scaleArea.width = scaleArea.height;
		}
		scaleArea.x += fStyleData.knobScaleOffset;
		scaleArea.width -= 2* fStyleData.knobScaleOffset;
		fScaleArea = scaleArea;
		
		updateButtonPositions();
	}
	
	private void updateButtonPositions() {
		final List<Knob> knobs = fKnobs;
		if ((fThisStyle & SWT.VERTICAL) != 0) {
			for (int idx = 0; idx < knobs.size(); idx++) {
				final Knob knob = knobs.get(idx);
				final int y = convertValue2Client(knob.getValue()) - fStyleData.knobScaleOffset;
				knob.fButton.setLocation(0, y);
			}
		}
		else {
			for (int idx = 0; idx < knobs.size(); idx++) {
				final Knob knob = knobs.get(idx);
				final int x = convertValue2Client(knob.getValue()) - fStyleData.knobScaleOffset;
				knob.fButton.setLocation(x, 0);
			}
		}
	}
	
	private void paint(final PaintEvent e) {
		final Rectangle clientArea = getClientArea();
		if (clientArea.width == 0 || clientArea.height == 0) {
			return;
		}
		
		final GC gc = e.gc;
		
		gc.setBackground(getBackground());
		gc.fillRectangle(clientArea);
		
		if (clientArea.height < fStyleData.height || clientArea.width < fStyleData.knobWidth) {
			return;
		}
		
		// Line
		gc.setLineWidth(1);
		gc.setForeground(getColor1());
		int x = clientArea.x + fStyleData.linePadding;
		int y = clientArea.y + fStyleData.knobHeight - 1;
		int width = clientArea.width - 2 * fStyleData.linePadding;
		gc.drawLine(x, y, x + width, y);
		y ++;
		gc.drawPoint(x, y);
//		e.gc.setForeground(getBackground());
//		e.gc.drawLine(x + 1, y, x + width - 2, y);
		gc.setForeground(getColor2());
		gc.drawPoint(x + width, y);
		y ++;
		gc.drawLine(x, y, x + width, y);
		y ++;
		
		// Ticks
		final Rectangle scaleArea = fScaleArea;
		width = scaleArea.width;
		y += 3;
		gc.setForeground(getTickColor());
		final int count = 4;
		for (int i = 0; i <= count; i++) {
			x = scaleArea.x + (width * i) / count;
			gc.drawLine(x, y, x, y + 3);
		}
		
		y = clientArea.y + fStyleData.knobHeight;
		paintRanges(gc, y);
		
		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		for (int i = 0; i < fKnobs.size(); i++) {
			x = convertValue2Client(fKnobs.get(i).getValue());
			gc.drawPoint(x, y);
		}
	}
	
	
	protected void stopOperations(final int time) {
		if (fOpInProgress == 0) {
			return;
		}
		fOpInProgress = 0;
		fKnobs.get(fOpButton).setValue(time, fOpSavedValue);
		
		updateButtonPositions();
		redraw();
	}
	
	protected boolean isInOperation() {
		return false;
	}
	
	private void paintKnob(final PaintEvent e) {
		final GC gc = e.gc;
		
		gc.drawImage(fStyleData.knobLine, fStyleData.knobScaleOffset - 1, (1 + fStyleData.knobHeight) / 2);
	}
	
	protected void paintRanges(final GC gc, final int y) {
		if (fKnobs.size() == 2) {
			final Knob knob0 = fKnobs.get(0);
			final Knob knob1 = fKnobs.get(1);
			gc.setForeground(getRangeColor(fIsActive &&
					(knob0.fButton.isFocusControl() || knob1.fButton.isFocusControl())));
			gc.drawLine(convertValue2Client(fKnobs.get(0).getValue()), y,
					convertValue2Client(fKnobs.get(1).getValue()), y);
		}
	}
	
	
	private void moveKnob(final Event event) {
		final Knob knob = (Knob) event.widget.getData();
		
		int newValue;
		
		EVENT: switch (event.type) {
		case SWT.KeyDown:
			stopOperations(event.time);
			
			switch (event.keyCode) {
			case SWT.ARROW_LEFT:
				newValue = knob.getValue() - fIncrement;
				break EVENT;
			case SWT.ARROW_DOWN:
				if ((fThisStyle & SWT.VERTICAL) != 0) {
					newValue = knob.getValue() + fIncrement;
					break EVENT;
				}
				newValue = knob.getValue() - fIncrement;
				break EVENT;
			case SWT.ARROW_RIGHT:
				newValue = knob.getValue() + fIncrement;
				break EVENT;
			case SWT.ARROW_UP:
				if ((fThisStyle & SWT.VERTICAL) != 0) {
					newValue = knob.getValue() - fIncrement;
					break EVENT;
				}
				newValue = knob.getValue() + fIncrement;
				break EVENT;
			case SWT.PAGE_DOWN:
				newValue = knob.getValue() - fPageIncrement;
				break EVENT;
			case SWT.PAGE_UP:
				newValue = knob.getValue() + fPageIncrement;
				break EVENT;
			case SWT.HOME:
				newValue = fMinimum;
				break EVENT;
			case SWT.END:
				newValue = fMaximum;
				break EVENT;
			default:
				return;
			}
		
		case SWT.MouseWheel:
			if (isInOperation()) {
				return;
			}
			
			newValue = knob.getValue() + fIncrement * event.count;
			
			break EVENT;
		
		case SWT.MouseDown:
			stopOperations(event.time);
			
			fOpInProgress = 1;
			fOpButton = knob.getIdx();
			fOpSavedValue = knob.getValue();
			fOpOffset = getCoord(event) - fStyleData.knobScaleOffset;
			
			return;
			
		case SWT.MouseMove:
			if (fOpInProgress != 1) {
				return;
			}
			
			newValue = convertClient2Value(getCoord(knob.fButton.getLocation()) + getCoord(event) - fOpOffset);
			
			break EVENT;
			
		case SWT.MouseUp:
			if (fOpInProgress != 1) {
				return;
			}
			
			fOpInProgress = 0;
			
			return;
			
		default:
			return;
		}
		
		event.doit = false;
		knob.setValue(event.time, newValue);
		
		updateButtonPositions();
		redraw();
	}
	
	protected void onDispose() {
	}
	
}
