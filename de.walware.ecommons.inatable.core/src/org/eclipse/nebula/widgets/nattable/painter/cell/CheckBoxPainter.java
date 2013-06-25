/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.painter.cell;

import org.eclipse.swt.graphics.Image;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;


public class CheckBoxPainter extends ImagePainter {

	private final Image checkedImg;
	private final Image uncheckedImg;

	public CheckBoxPainter() {
		checkedImg = GUIHelper.getImage("checked"); //$NON-NLS-1$
		uncheckedImg = GUIHelper.getImage("unchecked"); //$NON-NLS-1$
	}

	public CheckBoxPainter(Image checkedImg, Image uncheckedImg) {
		super();
		this.checkedImg = checkedImg;
		this.uncheckedImg = uncheckedImg;
	}

	public long getPreferredWidth(boolean checked) {
		return checked ? checkedImg.getBounds().width : uncheckedImg.getBounds().width;
	}

	public long getPreferredHeight(boolean checked) {
		return checked ? checkedImg.getBounds().height : uncheckedImg.getBounds().height;
	}

	@Override
	protected Image getImage(ILayerCell cell, IConfigRegistry configRegistry) {
		return isChecked(cell, configRegistry) ? checkedImg : uncheckedImg;
	}

	protected boolean isChecked(ILayerCell cell, IConfigRegistry configRegistry) {
		return convertDataType(cell, configRegistry).booleanValue();
	}

	protected Boolean convertDataType(ILayerCell cell, IConfigRegistry configRegistry) {
		if (cell.getDataValue() instanceof Boolean) {
			return (Boolean) cell.getDataValue();
		}
		IDisplayConverter displayConverter = configRegistry.getConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, cell.getDisplayMode(), cell.getConfigLabels().getLabels());
		Boolean convertedValue = null;
		if (displayConverter != null) {
			convertedValue = (Boolean) displayConverter.canonicalToDisplayValue(cell, configRegistry, cell.getDataValue());
		}
		if (convertedValue == null) {
			convertedValue = Boolean.FALSE;
		}
		return convertedValue;
	}
}
