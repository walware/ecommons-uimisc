package net.sourceforge.nattable.style;

import net.sourceforge.nattable.persistence.ColorPersistor;
import net.sourceforge.nattable.util.GUIHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * This class defines the visual attributes of a Border.
 */
public class BorderStyle {
	
	
	public enum LineStyleEnum {
		SOLID, DASHED, DOTTED, DASHDOT, DASHDOTDOT;
		
		public static int toSWT(LineStyleEnum line) {
			if (line == null) throw new IllegalArgumentException("null");
			if (line.equals(SOLID)) return SWT.LINE_SOLID;
			else if (line.equals(DASHED)) return SWT.LINE_DASH;
			else if (line.equals(DOTTED)) return SWT.LINE_DOT;
			else if (line.equals(DASHDOT)) return SWT.LINE_DASHDOT;
			else if (line.equals(DASHDOTDOT)) return SWT.LINE_DASHDOTDOT;
			else return SWT.LINE_SOLID;
		}
	}
	
	
	private int thickness = 1;
	private Color color = GUIHelper.COLOR_BLACK;
	private LineStyleEnum lineStyle = LineStyleEnum.SOLID;
	
	
	public BorderStyle() {
	}
	
	public BorderStyle(int thickness, Color color, LineStyleEnum lineStyle) {
		if (color == null || lineStyle == null) {
			throw new NullPointerException();
		}
		this.thickness = thickness;
		this.color = color;
		this.lineStyle = lineStyle;
	}
	
	
	public int getThickness() {
		return thickness;
	}
	
	public Color getColor() {
		return color;
	}
	
	public LineStyleEnum getLineStyle() {
		return lineStyle;
	}
	
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setLineStyle(LineStyleEnum lineStyle) {
		this.lineStyle = lineStyle;
	}
	
	
	@Override
	public int hashCode() {
		return ((thickness
				* 13 + color.hashCode())
				* 17 + lineStyle.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BorderStyle)) {
			return false;
		}
		
		final BorderStyle other = (BorderStyle) obj;
		return (thickness == other.thickness
				&& color.equals(other.color)
				&& lineStyle == other.lineStyle );
	}
	
	/**
	 * @return a human readable representation of the border style.
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + " (" 
				+ "thickness = " + thickness + ", "
				+ "color = " + ColorPersistor.asString(color) + ", "
				+ "lineStyle = " + lineStyle + ")";
	}
	
}
