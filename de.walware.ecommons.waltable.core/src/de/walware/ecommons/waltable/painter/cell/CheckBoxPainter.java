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
package de.walware.ecommons.waltable.painter.cell;

import org.eclipse.swt.graphics.Image;

import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.data.convert.IDisplayConverter;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.util.GUIHelper;


public class CheckBoxPainter extends ImagePainter {

	private final Image checkedImg;
	private final Image uncheckedImg;

	public CheckBoxPainter() {
		this.checkedImg= GUIHelper.getImage("checked"); //$NON-NLS-1$
		this.uncheckedImg= GUIHelper.getImage("unchecked"); //$NON-NLS-1$
	}

	public CheckBoxPainter(final Image checkedImg, final Image uncheckedImg) {
		super();
		this.checkedImg= checkedImg;
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
		return isChecked(cell, configRegistry) ? this.checkedImg : this.uncheckedImg;
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
			convertedValue= (Boolean) displayConverter.canonicalToDisplayValue(cell, configRegistry, cell.getDataValue(0, null));
		}
		if (convertedValue == null) {
			convertedValue= Boolean.FALSE;
		}
		return convertedValue;
	}
}
