package net.sourceforge.nattable.grid.layer;

import net.sourceforge.nattable.layer.ILayer;
import net.sourceforge.nattable.layer.IUniqueIndexLayer;
import net.sourceforge.nattable.layer.cell.LayerCell;
import net.sourceforge.nattable.painter.layer.CellLayerPainter;
import net.sourceforge.nattable.painter.layer.ILayerPainter;

public class CornerLayer extends DimensionallyDependentLayer {

	private final ILayerPainter layerPainter;
	
	
	public CornerLayer(IUniqueIndexLayer baseLayer, ILayer horizontalLayerDependency, ILayer verticalLayerDependency) {
		this(baseLayer, horizontalLayerDependency, verticalLayerDependency,
				new CellLayerPainter());
	}
	
	public CornerLayer(IUniqueIndexLayer baseLayer, ILayer horizontalLayerDependency, ILayer verticalLayerDependency,
			ILayerPainter layerPainter) {
		super(baseLayer, horizontalLayerDependency, verticalLayerDependency);
		
		this.layerPainter = layerPainter;
	}
	
	
	@Override
	public ILayerPainter getLayerPainter() {
		return layerPainter;
	}

	@Override
	public LayerCell getCellByPosition(int columnPosition, int rowPosition) {
		return new LayerCell(this, 0, 0, columnPosition, rowPosition, getHorizontalLayerDependency().getColumnCount(), getVerticalLayerDependency().getRowCount());
	}
	
}
