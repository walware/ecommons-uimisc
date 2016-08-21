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

import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.edit.CheckBoxStateEnum;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.util.GUIHelper;

public abstract class TreeCheckBoxPainter extends ImagePainter {
	
	private final Image checkedImg;
	private final Image semicheckedImg;
	private final Image uncheckedImg;

	public TreeCheckBoxPainter() {
		this(
				GUIHelper.getImage("checked"), //$NON-NLS-1$
				GUIHelper.getImage("semichecked"), //$NON-NLS-1$
				GUIHelper.getImage("unchecked") //$NON-NLS-1$
		);
	}

	public TreeCheckBoxPainter(final Image checkedImg, final Image semicheckedImage, final Image uncheckedImg) {
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
		switch (getCheckBoxState(cell)) {
		case CHECKED:
			return this.checkedImg;
		case SEMICHECKED:
			return this.semicheckedImg;
		default:
			return this.uncheckedImg;
		}
	}
	
	protected abstract CheckBoxStateEnum getCheckBoxState(ILayerCell cell);

}
