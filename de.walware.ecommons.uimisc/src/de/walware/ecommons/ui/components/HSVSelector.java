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
import org.eclipse.swt.graphics.Device;
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


public class HSVSelector extends Canvas implements IObjValueWidget<ColorDef> {
	
	/* 
	 * s = saturation, v = value
	 * triangle: Psv = (x11, y11), (x01, y01), (x10, y10)
	 * equation of lines: a * x + b * y + c = 0
	 */
	
	
	private static float distance(final float x, final float y) {
		return (float) Math.sqrt(x * x + y * y);
	}
	
	private static float save01(final float value) {
		if (value <= 0) {
			return 0;
		}
		if (value >= 1) {
			return 1;
		}
		return value;
	}
	
	private static final Color G_BACKGROUND = SharedUIResources.getColors().getColor(SharedUIResources.GRAPHICS_BACKGROUND_COLOR_ID);
	
	private static final HSVColorDef DEFAULT_VALUE = new HSVColorDef(0f, 1f, 1f);
	
	private static Color createColor(final Device device, float hue, final float saturation, final float value) {
		float r, g, b;
		if (saturation == 0) {
			r = g = b = value; 
		}
		else {
			if (hue == 1) {
				hue = 0;
			}
			hue *= 6;	
			final int i = (int) hue;
			final float f = hue - i;
			final float p = value * (1 - saturation);
			final float q = value * (1 - saturation * f);
			final float t = value * (1 - saturation * (1 - f));
			switch(i) {
				case 0:
					r = value;
					g = t;
					b = p;
					break;
				case 1:
					r = q;
					g = value;
					b = p;
					break;
				case 2:
					r = p;
					g = value;
					b = t;
					break;
				case 3:
					r = p;
					g = q;
					b = value;
					break;
				case 4:
					r = t;
					g = p;
					b = value;
					break;
				case 5:
				default:
					r = value;
					g = p;
					b = q;
					break;
			}
		}
		return new Color(device, (int) (r * 255 + 0.5), (int) (g * 255 + 0.5), (int) (b * 255 + 0.5));
	}
	
	private static void plotPoint(final GC gc, final int x, final int y, final Color color) {
		if (color != null) {
			gc.setForeground(color);
		}
		gc.drawLine(x - 1, y, x + 1, y);
		gc.drawLine(x, y - 1, x, y + 1);
	}
	
	private static void plotLine(final GC gc, final int xMin, final int xMax, final float a, final float b, final float c, final Color color) {
		if (color != null) {
			gc.setForeground(color);
		}
		final float m = - a / b;
		final float t = - c / b;
		for (int x = xMin; x <= xMax; x++) {
			final float y = m * x + t;
			gc.drawPoint(x, Math.round(y));
		}
	}
	
	
	private final static class Data {
		
		private final static float TRIANGLE_RATIO = 0.78f;
		private final static float TRIANGLE_ALPHA = -1f;
		
		private final int size;
		private final int center;
		private final float outer;
		private final float inner;
		
		private final float h;
		
		private final float xh1;
		private final float yh1;
		
		private final float x11;
		private final float y11;
		private final float x01;
		private final float y01;
		private final float x10;
		private final float y10;
		
		
		public Data(final int size, final float hue) {
			this.size = size;
			center = size / 2;
			outer = center;
			inner = (int) (outer * TRIANGLE_RATIO);
			
			h = hue;
			
			float[] xy1;
			xy1 = hue_xy1(h);
			xh1 = xy1[0];
			yh1 = xy1[1];
			
			x11 = center + inner * xh1;
			y11 = center + inner * yh1;
			xy1 = hue_xy1(h + 1.0/3.0);
			x01 = center + inner * xy1[0];
			y01 = center + inner * xy1[1];
			xy1 = hue_xy1(h - 1.0/3.0);
			x10 = center + inner * xy1[0];
			y10 = center + inner * xy1[1];
		}
		
		
		public final int x0(final int x) {
			return (x - center);
		}
		
		public final int y0(final int y) {
			return (y - center);
		}
		
		public final float xy0_d(final int x0, final int y0) {
			return (float) Math.sqrt((x0 * x0 + y0 * y0));
		}
		
		public final float xy0_hue(final int x0, final int y0) {
			float hue = (float) (Math.atan2(y0, x0) / (2 * Math.PI));
			hue += 0.25f;
			if (hue < 0) {
				hue += 1f;
			}
			return hue;
		}
		
		public final float[] hue_xy1(final double hue) {
			final double a = (hue - 0.25) * (2 * Math.PI);
			return new float[] { (float) Math.cos(a), (float) Math.sin(a) };
		}
		
		public HSVColorDef svColor(final int x, final int y) {
			final float a_01_11 = (y11 - y01);
			final float b_01_11 = -(x11 - x01);
			final float c_01_11 = -(a_01_11 * x11 + b_01_11 * y11);
			
			final float av = (y - y10);
			final float bv = -(x - x10);
			final float cv = -(av * x + bv * y);
			
			final float s;
			final float v;
			if (a_01_11 < -4f || a_01_11 > 4f) {
				final float yq = (av / a_01_11 * c_01_11 - cv) / (bv - (av / a_01_11 * b_01_11));
				
				s = (yq - y01) / a_01_11;
				v = (y - y10) / (yq - y10);
			}
			else {
				final float yq = (a_01_11 / av * cv - c_01_11) / (b_01_11 - (a_01_11 / av * bv));
				final float xq = - (bv * yq + cv) / av;
				
				s = (xq - x01) / (x11 - x01);
				v = (x - x10) / (xq - x10);
			}
			
			return new HSVColorDef(h, save01(s), save01(v));
		}
		
		public int[] sv_xy(final float s, final float v) {
			final float xq = x01 + s * (x11 - x01);
			final float yq = y01 + s * (y11 - y01);
			
			final float x = x10 + v * (xq - x10);
			final float y = y10 + v * (yq - y10);
			
			return new int[] { Math.round(x), Math.round(y) };
		}
		
		private Image createBaseImage(final Display display) {
			final Image image = new Image(display, size, size);
			final GC gc = new GC(image);
			gc.setBackground(G_BACKGROUND);
			gc.fillRectangle(0, 0, size, size);
			gc.setAdvanced(true);
			gc.setAntialias(SWT.OFF);
			int currentAlpha = 255;
			
			final float in = inner + 1;
			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++) {
					final int x0 = x0(x);
					final int y0 = y0(y);
					final float d = xy0_d(x0, y0);
					{	float a = outer - d;
						if (a < 0) {
							continue;
						}
						if (a > 1) {
							a = d - in;
							if (a < 0) {
								continue;
							}
							if (a > 1) {
								a = 1;
							}
						}
						final int alpha = (int) (a * 255);
						if (alpha != currentAlpha) {
							gc.setAlpha(currentAlpha = alpha);
						}
					}
					final Color color = createColor(display, xy0_hue(x0, y0), 1f, 1f);
					gc.setForeground(color);
					gc.drawPoint(x, y);
					color.dispose();
				}
			}
			gc.dispose();
			return image;
		}
		
		private Image createFullImage(final Display display) {
			final Image image = new Image(display, getBaseImage(this, display), SWT.IMAGE_COPY);
			if (h < 0) {
				return image;
			}
			final GC gc = new GC(image);
			gc.setAdvanced(true);
			gc.setAntialias(SWT.OFF);
			int currentAlpha = 255;
			
			final int xMin = Math.min((int) Math.ceil(x11), Math.min((int) Math.ceil(x01), (int) Math.ceil(x10))) - 1;
			final int xMax = Math.max((int) Math.floor(x11), Math.max((int) Math.floor(x01), (int) Math.floor(x10))) + 1;
			final int yMin = Math.min((int) Math.ceil(y11), Math.min((int) Math.ceil(y01), (int) Math.ceil(y10))) - 1;
			final int yMax = Math.max((int) Math.floor(y11), Math.max((int) Math.floor(y01), (int) Math.floor(y10))) + 1;
			
			final float a_01_11, b_01_11, c_01_11;
			{	final float a = (y11 - y01);
				final float b = -(x11 - x01);
				final float d = distance(a, b);
				a_01_11 = a / d;
				b_01_11 = b / d;
				c_01_11 = -(a * x11 + b * y11) / d;
			}
			final float a_10_01, b_10_01, c_10_01;
			{	final float a = (y01 - y10);
				final float b = -(x01 - x10);
				final float d = distance(a, b);
				a_10_01 = a / d;
				b_10_01 = b / d;
				c_10_01 = -(a * x01 + b * y01) / d;
			}
			final float a_11_10, b_11_10, c_11_10;
			{	final float a = (y10 - y11);
				final float b = -(x10 - x11);
				final float d = distance(a, b);
				a_11_10 = a / d;
				b_11_10 = b / d;
				c_11_10 = -(a * x10 + b * y10) / d;
			}
			
			for (int y = yMin; y <= yMax; y++) {
				final float av = (y - y10);
				final float av_as_cs = av / a_01_11 * c_01_11;
				final float av_as_bs = av / a_01_11 * b_01_11;
				for (int x = xMin; x <= xMax; x++) {
					float min = 0f;
					{	final float d = (a_01_11 * x + b_01_11 *y + c_01_11);
						if (d < 0f) {
							if (d < TRIANGLE_ALPHA) {
								continue;
							}
							if (d < min) {
								min = d;
							}
						}
					}
					{	final float d = (a_10_01 * x + b_10_01 *y + c_10_01);
						if (d < 0f) {
							if (d < TRIANGLE_ALPHA) {
								continue;
							}
							if (d < min) {
								min = d;
							}
						}
					}
					{	final float d = (a_11_10 * x + b_11_10 *y + c_11_10);
						if (d < 0f) {
							if (d < TRIANGLE_ALPHA) {
								continue;
							}
							if (d < min) {
								min = d;
							}
						}
					}
					
					final float s;
					final float v;
					{	final float bv = -(x - x10);
						final float cv = -(av * x + bv * y);
						if (a_01_11 < -4f || a_01_11 > 4f) {
							final float yq = (av_as_cs - cv) / (bv - (av_as_bs));
							
							s = (yq - y01) / a_01_11;
							v = (y - y10) / (yq - y10);
						}
						else {
							final float yq = (a_01_11 / av * cv - c_01_11) / (b_01_11 - (a_01_11 / av * bv));
							final float xq = - (bv * yq + cv) / av;
							
							s = (xq - x01) / (x11 - x01);
							v = (x - x10) / (xq - x10);
						}
					}
//					final float xq, yq;
//					{	final float bv = -(x - x10);
//						final float cv = -(av * x + bv * y);
//						
//						yq = (av_as_cs - cv) / (bv - av_as_bs);
//						xq = - (b_01_11 * yq + c_01_11) / a_01_11;
//					}
//					
//					final float s = (xq - x01) / (x11 - x01);
//					final float v = (x - x10) / (xq - x10);
						
					{	final int alpha = (min >= 0f) ? 255 : (int) ((1f+min) * 255);
						if (alpha != currentAlpha) {
							gc.setAlpha(currentAlpha = alpha);
						}
					}
					final Color color = createColor(display, h, save01(s), save01(v));
					gc.setForeground(color);
					gc.drawPoint(x, y);
					color.dispose();
				}
			}
			
			gc.dispose();
			return image;
		}
		
		public void drawTriangle(final GC gc) {
			final int[] xy = new int[6];
			xy[0] = Math.round(x11);
			xy[1] = Math.round(y11);
			xy[2] = Math.round(x01);
			xy[3] = Math.round(y01);
			xy[4] = Math.round(x10);
			xy[5] = Math.round(y10);
			gc.drawPolygon(xy);
		}
		
	}
	
	
	private static final Image[] BASE_CACHE = new Image[10];
	
	private static Image getBaseImage(final Data data, final Display display) {
		for (int i = 0; i < BASE_CACHE.length; i++) {
			if (BASE_CACHE[i] == null) {
				break;
			}
			if (BASE_CACHE[i].getImageData().width == data.size) {
				return BASE_CACHE[i];
			}
		}
		if (BASE_CACHE[BASE_CACHE.length - 1] != null) {
			BASE_CACHE[BASE_CACHE.length - 1].dispose();
		}
		System.arraycopy(BASE_CACHE, 0, BASE_CACHE, 1, BASE_CACHE.length - 1);
		BASE_CACHE[0] = data.createBaseImage(display);
		return BASE_CACHE[0];
	}
	
	
	private class SWTListener implements PaintListener, Listener {
		
		
		private static final int TRACK_HUE = 1;
		private static final int TRACK_SV = 2;
		
		private Data fData;
		
		private Image fImage;
		
		private int fMouseState;
		
		
		@Override
		public void handleEvent(final Event event) {
			final Data data = fData;
			switch (event.type) {
			case SWT.MouseDown:
				if (data != null) {
					final int x0 = data.x0(event.x);
					final int y0 = data.y0(event.y);
					final float d = data.xy0_d(x0, y0);
					if (d > data.inner) {
						doSetValue(new HSVColorDef(data.xy0_hue(x0, y0),
								fValue.getSaturation(), fValue.getValue()),
								event.time, 0 );
						fMouseState = TRACK_HUE;
					}
					else {
						doSetValue(data.svColor(event.x, event.y), event.time, 0);
						fMouseState = TRACK_SV;
					}
				}
				return;
			case SWT.MouseUp:
				fMouseState = 0;
				return;
			case SWT.MouseMove:
				if (data != null) {
					switch (fMouseState) {
					case TRACK_HUE:
						doSetValue(new HSVColorDef(data.xy0_hue(data.x0(event.x), data.y0(event.y)),
								fValue.getSaturation(), fValue.getValue()),
								event.time, 0 );
						break;
					case TRACK_SV:
						doSetValue(data.svColor(event.x, event.y), event.time, 0);
						break;
					}
				}
				return;
			}
		}
		
		@Override
		public void paintControl(final PaintEvent e) {
			final Rectangle clientArea = getClientArea();
			int size = Math.min(clientArea.width, clientArea.height);
			if (fSize > 0 && fSize < size) {
				size = fSize;
			}
			
			if (fData == null || fData.size != size || fData.h != fValue.getHue()) {
				if (fImage != null) {
					fImage.dispose();
					fImage = null;
				}
				fData = new Data(size, fValue.getHue());
			}
			
			final GC gc = e.gc;
			
			gc.setAdvanced(true);
			gc.setAntialias(SWT.OFF);
			final int currentAlpha = 255;
			gc.setAlpha(currentAlpha);
			
			gc.setBackground(G_BACKGROUND);
			gc.fillRectangle(clientArea);
			
			if (fImage == null) {
				fImage = fData.createFullImage(e.display);
			}
			gc.drawImage(fImage, 0, 0);
			
			gc.setLineWidth(1);
			gc.setAntialias(SWT.ON);
			
			gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
			gc.drawLine(Math.round(fData.center + fData.outer * fData.xh1),
					Math.round(fData.center + fData.outer * fData.yh1),
					Math.round(fData.x11),
					Math.round(fData.y11) );
			
			if (fValue.getValue() < 0.50) {
				gc.setForeground(e.display.getSystemColor(SWT.COLOR_WHITE));
			}
			final int[] xy = fData.sv_xy(fValue.getSaturation(), fValue.getValue());
			gc.drawOval(xy[0] - 1, xy[1] - 1, 3, 3);
		}
		
	}
	
	
	private int fSize = 8 + LayoutUtil.defaultHSpacing() * 30;
	
	private HSVColorDef fValue = DEFAULT_VALUE;
	
	private final FastList<IObjValueListener<ColorDef>> fValueListeners = (FastList) new FastList<IObjValueListener>(IObjValueListener.class);
	
	
	public HSVSelector(final Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);
		
		final SWTListener listener = new SWTListener();
		addPaintListener(listener);
		addListener(SWT.MouseDown, listener);
		addListener(SWT.MouseUp, listener);
		addListener(SWT.MouseMove, listener);
	}
	
	
	public void setSize(final int size) {
		fSize = size;
	}
	
	private boolean doSetValue(final HSVColorDef newValue, final int time, final int flags) {
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
		int width = fSize;
		int height = fSize;
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
	public HSVColorDef getValue(final int idx) {
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
		if (value.getType() == "hsv") { //$NON-NLS-1$
			doSetValue((HSVColorDef) value, 0, 0);
		}
		else {
			doSetValue(new HSVColorDef(value), 0, 0);
		}
	}
	
}
