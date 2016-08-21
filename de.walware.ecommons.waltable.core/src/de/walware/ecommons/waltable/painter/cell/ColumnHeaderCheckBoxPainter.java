/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// -depend
package de.walware.ecommons.waltable.painter.cell;

import org.eclipse.swt.graphics.Image;

import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.data.convert.IDisplayConverter;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.LayerUtil;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.util.GUIHelper;


public class ColumnHeaderCheckBoxPainter extends ImagePainter {
	

	private final Image checkedImg;
	private final Image semicheckedImg;
	private final Image uncheckedImg;
	
	private final ILayer columnDataLayer;

	public ColumnHeaderCheckBoxPainter(final ILayer columnDataLayer) {
		this(
				columnDataLayer,
				GUIHelper.getImage("checked"), //$NON-NLS-1$
				GUIHelper.getImage("semichecked"), //$NON-NLS-1$
				GUIHelper.getImage("unchecked") //$NON-NLS-1$
		);
	}

	public ColumnHeaderCheckBoxPainter(final ILayer columnLayer, final Image checkedImg, final Image semicheckedImage, final Image uncheckedImg) {
		this.columnDataLayer= columnLayer;
		this.checkedImg= checkedImg;
		this.semicheckedImg= semicheckedImage;
		this.uncheckedImg= uncheckedImg;
	}

	public long getPreferredWidth(final boolean checked) {
		return checked ? this.checkedImg.getBounds().width : this.uncheckedImg.getBounds().width;
	}

	public long getPreferredHeight(final boolean checked) {
		return checked ? this.checkedImg.getBounds().height : this.uncheckedImg.getBounds().height;
	}

	@Override
	protected Image getImage(final ILayerCell cell, final IConfigRegistry configRegistry) {
		final long columnPosition= LayerUtil.convertColumnPosition(cell.getLayer(), cell.getColumnPosition(), this.columnDataLayer);
		
		final long checkedCellsCount= getCheckedCellsCount(columnPosition, configRegistry);
		
		if (checkedCellsCount > 0) {
			if (checkedCellsCount == this.columnDataLayer.getRowCount()) {
				return this.checkedImg;
			} else {
				return this.semicheckedImg;
			}
		} else {
			return this.uncheckedImg;
		}
	}

	public long getCheckedCellsCount(final long columnPosition, final IConfigRegistry configRegistry) {
		long checkedCellsCount= 0;
		
		for (long rowPosition= 0; rowPosition < this.columnDataLayer.getRowCount(); rowPosition++) {
			final ILayerCell columnCell= this.columnDataLayer.getCellByPosition(columnPosition, rowPosition);
			if (isChecked(columnCell, configRegistry)) {
				checkedCellsCount++;
			}
		}
		return checkedCellsCount;
	}

	protected boolean isChecked(final ILayerCell cell, final IConfigRegistry configRegistry) {
		return convertDataType(cell, configRegistry).booleanValue();
	}

	protected Boolean convertDataType(final ILayerCell cell, final IConfigRegistry configRegistry) {
		if (cell.getDataValue(0, null) instanceof Boolean) {
			return (Boolean) cell.getDataValue(0, null);
		}
		final IDisplayConverter displayConverter= configRegistry.getConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, cell.getDisplayMode(), cell.getConfigLabels().getLabels());
		Boolean convertedValue= null;
		if (displayConverter != null) {
			try {
				convertedValue= (Boolean) displayConverter.canonicalToDisplayValue(cell, configRegistry, cell.getDataValue(0, null));
			} catch (final Exception e) {
//				log.debug(e);
			}
		}
		if (convertedValue == null) {
			convertedValue= Boolean.FALSE;
		}
		return convertedValue;
	}
	
}
