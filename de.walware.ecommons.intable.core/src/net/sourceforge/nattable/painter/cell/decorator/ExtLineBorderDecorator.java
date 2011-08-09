package net.sourceforge.nattable.painter.cell.decorator;

import net.sourceforge.nattable.config.IConfigRegistry;
import net.sourceforge.nattable.layer.cell.LayerCell;
import net.sourceforge.nattable.painter.cell.CellPainterWrapper;
import net.sourceforge.nattable.painter.cell.ICellPainter;
import net.sourceforge.nattable.style.BorderStyle;
import net.sourceforge.nattable.style.BorderStyle.LineStyleEnum;
import net.sourceforge.nattable.style.CellStyleAttributes;
import net.sourceforge.nattable.style.CellStyleUtil;
import net.sourceforge.nattable.style.IStyle;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class ExtLineBorderDecorator extends CellPainterWrapper {

	private final BorderStyle defaultBorderStyle;

	public ExtLineBorderDecorator(ICellPainter interiorPainter) {
		this(interiorPainter, null);
	}
	
	public ExtLineBorderDecorator(ICellPainter interiorPainter, BorderStyle defaultBorderStyle) {
		super(interiorPainter);
		this.defaultBorderStyle = defaultBorderStyle;
	}

	public int getPreferredWidth(LayerCell cell, GC gc, IConfigRegistry configRegistry) {
		BorderStyle borderStyle = getBorderStyle(cell, configRegistry);
		int borderThickness = borderStyle != null ? borderStyle.getThickness() : 0;
		
		return super.getPreferredWidth(cell, gc, configRegistry) + (borderThickness * 2);
	}
	
	public int getPreferredHeight(LayerCell cell, GC gc, IConfigRegistry configRegistry) {
		BorderStyle borderStyle = getBorderStyle(cell, configRegistry);
		int borderThickness = borderStyle != null ? borderStyle.getThickness() : 0;
		
		return super.getPreferredHeight(cell, gc, configRegistry) + (borderThickness * 2);
	}

	private BorderStyle getBorderStyle(LayerCell cell, IConfigRegistry configRegistry) {
		IStyle cellStyle = CellStyleUtil.getCellStyle(cell, configRegistry);
		BorderStyle borderStyle = cellStyle.getAttributeValue(CellStyleAttributes.BORDER_STYLE);
		if (borderStyle == null) {
			borderStyle = defaultBorderStyle;
		}
		return borderStyle;
	}

	public void paintCell(LayerCell cell, GC gc, Rectangle rectangle, IConfigRegistry configRegistry) {
		BorderStyle borderStyle = getBorderStyle(cell, configRegistry);
		int borderThickness = borderStyle != null ? borderStyle.getThickness() : 0;
		
		Rectangle interiorBounds =
			new Rectangle(
					rectangle.x + borderThickness,
					rectangle.y + borderThickness,
					rectangle.width - (borderThickness * 2),
					rectangle.height - (borderThickness * 2)
			);
		super.paintCell(cell, gc, interiorBounds, configRegistry);
		
		if (borderStyle == null || borderThickness <= 0) {
			return;
		}
		
		// Save GC settings
		Color originalForeground = gc.getForeground();
		int originalLineWidth = gc.getLineWidth();
		int originalLineStyle = gc.getLineStyle();

		Rectangle borderArea = new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		if (borderThickness >= 1) {
			int shift = 0;
			int areaShift = 0;
			if ((borderThickness % 2) == 0) {
				shift = borderThickness / 2 - 1;
				areaShift = (shift * 2) + 1;
			} else {
				shift = borderThickness / 2;
				areaShift = (shift * 2);
			}
			borderArea.x += shift;
			borderArea.y += shift;
			borderArea.width -= areaShift;
			borderArea.height -= areaShift;
		}

		gc.setLineStyle(LineStyleEnum.toSWT(borderStyle.getLineStyle()));
		gc.setLineWidth(borderThickness+1);
		gc.setForeground(borderStyle.getColor());
		gc.drawRectangle(borderArea);

		// Restore GC settings
		gc.setForeground(originalForeground);
		gc.setLineWidth(originalLineWidth);
		gc.setLineStyle(originalLineStyle);
	}
	
}
