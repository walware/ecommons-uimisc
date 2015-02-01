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
import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.util.LayoutUtil;


public class AlphaSelector extends Canvas implements IObjValueWidget<Float> {
	
	
	private static final Float DEFAULT_VALUE = new Float(1f);
	
	private static final Color G_BACKGROUND = SharedUIResources.getColors().getColor(SharedUIResources.GRAPHICS_BACKGROUND_COLOR_ID);
	
	private static final ColorDef DEFAULT_BASE = new ColorDef(0, 0, 0);
	
	
	private final static class Data {
		
		private final int size;
		private final float factor;
		
		private final ColorDef baseColor;
		
		private final int alphaX0;
		private final int alphaX1;
		
		private final int y0;
		private final int y1;
		
		
		public Data(final int size, final ColorDef baseColor) {
			this.size = size;
			this.baseColor = baseColor;
			factor = size - 1;
			
			alphaX0 = 1;
			alphaX1 = alphaX0 + Math.round(size * 0.15f);
			
			y0 = 1;
			y1 = y0 + size;
		}
		
		
		public int alpha_y_255(final int y) {
			final int v = 255 - Math.round(((y - y0) / factor) * 255f);
			if (v <= 0) {
				return 0;
			}
			if (v >= 255) {
				return 255;
			}
			return v;
		}
		
		public int alpha_255_y(final int v) {
			return y0 + Math.round((v / 255f) * factor);
		}
		
		public float alpha_y_01(final int y) {
			final float v = 1f - ((y - y0) / factor);
			if (v <= 0f) {
				return 0f;
			}
			if (v >= 1f) {
				return 1f;
			}
			return v;
		}
		
		public int alpha_01_y(final float v) {
			return y0 + Math.round(((1f - v) * factor));
		}
		
		public int getBaseMax() {
			return Math.max(baseColor.getRed(), Math.max(baseColor.getGreen(), baseColor.getBlue()));
		}
		
		public Image createImage(final Display display) {
			final Image image = new Image(display, alphaX1 + 1, y1 + 1);
			final GC gc = new GC(image);
			gc.setAdvanced(false);
			
			gc.setBackground(G_BACKGROUND);
			gc.fillRectangle(0, 0, image.getImageData().width, image.getImageData().height);
			
			// prim
			if (baseColor.equalsRGB(DEFAULT_BASE)) {
				final int x1 = alphaX1 - 1;
				for (int y = y0; y < y1; y++) {
					final int alpha255 = 255 - alpha_y_255(y);
					final Color color = new Color(display, alpha255, alpha255, alpha255);
					gc.setForeground(color);
					gc.drawLine(alphaX0, y, x1, y);
					color.dispose();
				}
			}
			else {
				final int x1 = alphaX1 - 1;
				for (int y = y0; y < y1; y++) {
					final int alpha255 = alpha_y_255(y);
					final int white = 255 - alpha255;
					final Color color = new Color(display, 
							white + (baseColor.getRed() * alpha255) / 255,
							white + (baseColor.getGreen() * alpha255) / 255,
							white + (baseColor.getBlue() * alpha255) / 255 );
					gc.setForeground(color);
					gc.drawLine(alphaX0, y, x1, y);
					color.dispose();
				}
			}
			
			gc.dispose();
			return image;
		}
		
	}
	
	
	private class SWTListener implements PaintListener, Listener {
		
		
		private static final int TRACK_ALPHA = 1;
		
		private Data fData;
		
		private Image fImage;
		
		private int fMouseState;
		
		
		@Override
		public void handleEvent(final Event event) {
			final Data data = fData;
			switch (event.type) {
			case SWT.MouseDown:
				if (data != null && event.y >= data.y0 && event.y < data.y1
						&& event.x >= data.alphaX0 && event.x < data.alphaX1) {
					doSetValue(Float.valueOf(data.alpha_y_01(event.y)), event.time, 0);
					fMouseState = TRACK_ALPHA;
				}
				return;
			case SWT.MouseUp:
				fMouseState = 0;
				return;
			case SWT.MouseMove:
				if (data != null) {
					switch (fMouseState) {
					case TRACK_ALPHA:
						doSetValue(Float.valueOf(data.alpha_y_01(event.y)), event.time, 0);
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
			width = Math.round(width / 0.15f);
			return Math.min(width - 2, height - 2);
		}
		
		@Override
		public void paintControl(final PaintEvent e) {
			final Rectangle clientArea = getClientArea();
			int size = computeSize(clientArea.width, clientArea.height);
			if (fSize > 0 && fSize < size) {
				size = fSize;
			}
			
			if (fData == null || fData.size != size || !fData.baseColor.equalsRGB(fBaseColor)) {
				if (fImage != null) {
					fImage.dispose();
					fImage = null;
				}
				fData = new Data(size, fBaseColor);
			}
			
			final GC gc = e.gc;
			
			gc.setAdvanced(false);
			
			gc.setBackground(G_BACKGROUND);
			gc.fillRectangle(clientArea);
			
//			if (fImage == null) {
				if (fImage != null) {
					fImage.dispose();
					fImage = null;
				}
				fImage = fData.createImage(e.display);
//			}
			gc.drawImage(fImage, 0, 0);
			
			gc.setLineWidth(1);
			gc.setAdvanced(true);
			gc.setAntialias(SWT.ON);
			
			{	final float alpha = fValue.floatValue();
				final int y = fData.alpha_01_y(alpha);
				gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
				gc.drawLine(fData.alphaX0 - 1, y, fData.alphaX1, y);
				if (255 * (1f - alpha) + (fData.getBaseMax() * alpha) < 127) {
					gc.setForeground(e.display.getSystemColor(SWT.COLOR_WHITE));
					gc.drawLine(fData.alphaX0, y, fData.alphaX1 - 1, y);
				}
			}
		}
		
	}
	
	
	private int fSize = 8 + LayoutUtil.defaultHSpacing() * 30;
	
	private Float fValue = DEFAULT_VALUE;
	
	private final FastList<IObjValueListener<Float>> fValueListeners = (FastList) new FastList<IObjValueListener>(IObjValueListener.class);
	
	private ColorDef fBaseColor = DEFAULT_BASE;
	
	
	public AlphaSelector(final Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);
		
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
	
	public void setBaseColor(final ColorDef color) {
		fBaseColor = (color != null) ? color : DEFAULT_BASE;
		redraw();
	}
	
	private boolean doSetValue(final Float newValue, final int time, final int flags) {
		if (fValue.equals(newValue) && flags == 0 && fValue != DEFAULT_VALUE) {
			return false;
		}
		final IObjValueListener<Float>[] listeners = fValueListeners.toArray();
		final ObjValueEvent<Float> event = new ObjValueEvent<Float>(this, time, 0,
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
		int width = 2 + Math.round(fSize * 0.15f);
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
	public Class<Float> getValueType() {
		return Float.class;
	}
	
	@Override
	public void addValueListener(final IObjValueListener<Float> listener) {
		fValueListeners.add(listener);
	}
	
	@Override
	public void removeValueListener(final IObjValueListener<Float> listener) {
		fValueListeners.remove(listener);
	}
	
	@Override
	public Float getValue(final int idx) {
		if (idx != 0) {
			throw new IllegalArgumentException("idx: " + idx); //$NON-NLS-1$
		}
		return fValue;
	}
	
	@Override
	public void setValue(final int idx, final Float value) {
		if (idx != 0) {
			throw new IllegalArgumentException("idx: " + idx); //$NON-NLS-1$
		}
		if (value == null) {
			throw new NullPointerException("value"); //$NON-NLS-1$
		}
		doSetValue(value, 0, 0);
	}
	
}
