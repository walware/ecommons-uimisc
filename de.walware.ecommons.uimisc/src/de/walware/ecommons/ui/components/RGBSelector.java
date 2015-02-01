/*=============================================================================#
 # Copyright (c) 2013-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.walware.ecommons.FastList;
import de.walware.ecommons.graphics.core.ColorDef;
import de.walware.ecommons.graphics.core.HSVColorDef;
import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.util.LayoutUtil;


public class RGBSelector extends Canvas implements IObjValueWidget<ColorDef> {
	
	
	private static final byte RED = 0x0;
	private static final byte GREEN = 0x1;
	private static final byte BLUE = 0x2;
	
	private static final ColorDef DEFAULT_VALUE = new HSVColorDef(1, 0, 0);
	
	private static final Color G_BACKGROUND = SharedUIResources.getColors().getColor(SharedUIResources.GRAPHICS_BACKGROUND_COLOR_ID);
	
	
	private final static class Data {
		
		private final int size;
		private final float factor;
		
		private final int primX0;
		private final int primX1;
		
		private final int rectX0;
		private final int rectX1;
		
		private final int y0;
		private final int y1;
		
		
		public Data(final int size) {
			this.size = size;
			factor = size - 1;
			
			primX0 = 1;
			primX1 = primX0 + Math.round(size * 0.15f);
			
			rectX0 = primX1 + LayoutUtil.defaultHSpacing();
			rectX1 = rectX0 + size;
			
			y0 = 1;
			y1 = y0 + size;
		}
		
		
		public int prim_y_255(final int y) {
			final int v = 255 - Math.round(((y - y0) / factor) * 255f);
			if (v <= 0) {
				return 0;
			}
			if (v >= 255) {
				return 255;
			}
			return v;
		}
		
		public int rect_x_255(final int x) {
			final int v = Math.round(((x - rectX0) / factor) * 255f);
			if (v <= 0) {
				return 0;
			}
			if (v >= 255) {
				return 255;
			}
			return v;
		}
		
		public int rect_y_255(final int y) {
			final int v = 255 - Math.round(((y - y0) / factor) * 255f);
			if (v <= 0) {
				return 0;
			}
			if (v >= 255) {
				return 255;
			}
			return v;
		}
		
		public int prim_255_y(final int v) {
			return y0 + Math.round(((255 - v) / 255f) * factor);
		}
		
		public int rect_255_x(final int v) {
			return rectX0 + Math.round((v / 255f) * factor);
		}
		
		public int rect_255_y(final int v) {
			return y0 + Math.round(((255 - v) / 255f) * factor);
		}
		
		public Image createImage(final Display display, final byte primColor, final int primValue, final byte xColor, final byte yColor) {
			final Image image = new Image(display, rectX1 + 1, y1 + 1);
			final GC gc = new GC(image);
			gc.setAdvanced(false);
			
			gc.setBackground(G_BACKGROUND);
			gc.fillRectangle(0, 0, image.getImageData().width, image.getImageData().height);
			
			// prim
			final int[] rgb = new int[3];
			{	final int x1 = primX1 - 1;
				for (int y = y0; y < y1; y++) {
					rgb[primColor] = prim_y_255(y);
					final Color color = new Color(display, rgb[0], rgb[1], rgb[2]);
					gc.setForeground(color);
					gc.drawLine(primX0, y, x1, y);
					color.dispose();
				}
			}
			// rect
			rgb[primColor] = primValue;
			for (int y = y0; y < y1; y++) {
				rgb[yColor] = rect_y_255(y);
				for (int x = rectX0; x < rectX1; x++) {
					rgb[xColor] = rect_x_255(x);
					final Color color = new Color(display, rgb[0], rgb[1], rgb[2]);
					gc.setForeground(color);
					gc.drawPoint(x, y);
					color.dispose();
				}
			}
			
			gc.dispose();
			return image;
		}
		
	}
	
	
	private class SWTListener implements PaintListener, Listener {
		
		
		private static final int TRACK_PRIM = 1;
		private static final int TRACK_RECT = 2;
		
		private Data fData;
		
		private Image fImage;
		private int fImagePrim;
		private int fImagePrimValue;
		
		private int fMouseState;
		
		
		@Override
		public void handleEvent(final Event event) {
			final Data data = fData;
			switch (event.type) {
			case SWT.MouseDown:
				if (data != null && event.y >= data.y0 && event.y < data.y1) {
					if (event.x >= data.primX0 && event.x < data.primX1) {
						doSetValue(createColor(fCurrentPrim, data.prim_y_255(event.y)),
								event.time, 0 );
						fMouseState = TRACK_PRIM;
					}
					else if (event.x >= data.rectX0 && event.x < data.rectX1) {
						doSetValue(createColor(fCurrentRectX, data.rect_x_255(event.x),
								fCurrentRectY, data.rect_y_255(event.y)),
								event.time, 0 );
						fMouseState = TRACK_RECT;
					}
				}
				return;
			case SWT.MouseUp:
				fMouseState = 0;
				return;
			case SWT.MouseMove:
				if (data != null) {
					switch (fMouseState) {
					case TRACK_PRIM:
						doSetValue(createColor(fCurrentPrim, data.prim_y_255(event.y)),
								event.time, 0 );
						break;
					case TRACK_RECT:
						doSetValue(createColor(fCurrentRectX, data.rect_x_255(event.x),
								fCurrentRectY, data.rect_y_255(event.y)),
								event.time, 0 );
						break;
					}
				}
				return;
			case SWT.Dispose:
				if (fImage != null) {
					fImage.dispose();
					fImage = null;
				}
			}
		}
		
		private int computeSize(int width, final int height) {
			width -= LayoutUtil.defaultHSpacing();
			width = Math.round(width / 1.15f);
			return Math.min(width - 2, height - 2);
		}
		
		@Override
		public void paintControl(final PaintEvent e) {
			final Rectangle clientArea = getClientArea();
			int size = computeSize(clientArea.width, clientArea.height);
			if (fSize > 0 && fSize < size) {
				size = fSize;
			}
			
			if (fData == null || fData.size != size) {
				fData = new Data(size);
			}
			
			final GC gc = e.gc;
			
			gc.setAdvanced(true);
			gc.setAntialias(SWT.OFF);
			final int currentAlpha = 255;
			gc.setAlpha(currentAlpha);
			
			gc.setBackground(G_BACKGROUND);
			gc.fillRectangle(clientArea);
			
			final int primValue = getComponent(fCurrentPrim);
			if (fImage == null || fImagePrim != fCurrentPrim || fImagePrimValue != primValue) {
				if (fImage != null) {
					fImage.dispose();
				}
				fImage = fData.createImage(e.display, fCurrentPrim, primValue,
						fCurrentRectX, fCurrentRectY );
				fImagePrim = fCurrentPrim;
				fImagePrimValue = primValue;
			}
			gc.drawImage(fImage, 0, 0);
			
			gc.setLineWidth(1);
			gc.setAdvanced(true);
			gc.setAntialias(SWT.ON);
			
			{	final int y = fData.prim_255_y(primValue);
				gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
				gc.drawLine(fData.primX0 - 1, y, fData.primX1, y);
				if (primValue < 127) {
					gc.setForeground(e.display.getSystemColor(SWT.COLOR_WHITE));
					gc.drawLine(fData.primX0, y, fData.primX1 - 1, y);
				}
			}
			{	final int x = fData.rect_255_x(getComponent(fCurrentRectX));
				final int y = fData.rect_255_y(getComponent(fCurrentRectY));
				gc.setForeground(e.display.getSystemColor(
						(Math.max(Math.max(fValue.getRed(), fValue.getGreen()), fValue.getBlue()) < 127) ? SWT.COLOR_WHITE : SWT.COLOR_BLACK ));
				gc.drawOval(x - 1, y - 1, 3, 3);
			}
		}
		
	}
	
	
	private int fSize = 8 + LayoutUtil.defaultHSpacing() * 30;
	
	private ColorDef fValue = DEFAULT_VALUE;
	
	private byte fCurrentPrim;
	private byte fCurrentRectX;
	private byte fCurrentRectY;
	
	private final FastList<IObjValueListener<ColorDef>> fValueListeners = (FastList) new FastList<IObjValueListener>(IObjValueListener.class);
	
	
	public RGBSelector(final Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);
		
		setPrimary(RED);
		
		final SWTListener listener = new SWTListener();
		addPaintListener(listener);
		addListener(SWT.MouseDown, listener);
		addListener(SWT.MouseUp, listener);
		addListener(SWT.MouseMove, listener);
		addListener(SWT.Dispose, listener);
	}
	
	
	public void setSize(final int size) {
		fSize = size;
	}
	
	private int getComponent(final byte c) {
		switch (c) {
		case RED:
			return fValue.getRed();
		case GREEN:
			return fValue.getGreen();
		case BLUE:
			return fValue.getBlue();
		default:
			throw new IllegalArgumentException();
		}
	}
	
	private ColorDef createColor(final byte color, final int value) {
		final int[] rgb = new int[] { fValue.getRed(), fValue.getGreen(), fValue.getBlue() };
		rgb[color] = value;
		return new ColorDef(rgb[0], rgb[1], rgb[2]);
	}
	
	private ColorDef createColor(final byte change1, final int value1, final byte change2, final int value2) {
		final int[] rgb = new int[] { fValue.getRed(), fValue.getGreen(), fValue.getBlue() };
		rgb[change1] = value1;
		rgb[change2] = value2;
		return new ColorDef(rgb[0], rgb[1], rgb[2]);
	}
	
	private boolean doSetValue(final ColorDef newValue, final int time, final int flags) {
		if (fValue.equals(newValue) && flags == 0 && fValue != DEFAULT_VALUE) {
			return false;
		}
		final IObjValueListener<ColorDef>[] listeners = fValueListeners.toArray();
		final ObjValueEvent<ColorDef> event = new ObjValueEvent<ColorDef>(this, time, 0,
				fValue, newValue, flags);
		
		fValue = newValue;
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
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		int width = 2 + Math.round(fSize * 1.15f) + LayoutUtil.defaultHSpacing();
		int height = 2 + fSize;
		final int border = getBorderWidth();
		width += border * 2;
		height += border * 2;
		return new Point(width, height);
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
	
	@Override
	public ColorDef getValue(final int idx) {
		if (idx != 0) {
			throw new IllegalArgumentException("idx: " + idx); //$NON-NLS-1$
		}
		return fValue;
	}
	
	@Override
	public void setValue(final int idx, final ColorDef value) {
		if (idx != 0) {
			throw new IllegalArgumentException("idx: " + idx); //$NON-NLS-1$
		}
		if (value.getType() == "rgb") { //$NON-NLS-1$
			doSetValue(value, 0, 0);
		}
		else {
			doSetValue(new ColorDef(value), 0, 0);
		}
	}
	
	public void setPrimary(final int color) {
		switch (color) {
		case RED:
			fCurrentPrim = RED;
			fCurrentRectX = GREEN;
			fCurrentRectY = BLUE;
			break;
		case GREEN:
			fCurrentPrim = GREEN;
			fCurrentRectX = BLUE;
			fCurrentRectY = RED;
			break;
		case BLUE:
			fCurrentPrim = BLUE;
			fCurrentRectX = RED;
			fCurrentRectY = GREEN;
			break;
		default:
			throw new IllegalArgumentException();
		}
		redraw();
	}
	
}
