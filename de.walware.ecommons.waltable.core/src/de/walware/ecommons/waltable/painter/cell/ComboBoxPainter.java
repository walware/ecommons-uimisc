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

import de.walware.ecommons.waltable.painter.cell.decorator.CellPainterDecorator;
import de.walware.ecommons.waltable.ui.util.CellEdgeEnum;
import de.walware.ecommons.waltable.util.GUIHelper;

public class ComboBoxPainter extends CellPainterWrapper {

	/**
	 * Create a new {@link ComboBoxPainter} with the default image.
	 */
	public ComboBoxPainter() {
		this(GUIHelper.getImage("down_2")); //$NON-NLS-1$
	}
	
	/**
	 * Create a new {@link ComboBoxPainter} with the given {@link Image} as the image 
	 * marking the cell as a combo control.
	 * @param comboImage The image marking the cell as a combo control
	 */
	public ComboBoxPainter(final Image comboImage) {
		setWrappedPainter(
				new CellPainterDecorator(
						new TextPainter(), CellEdgeEnum.RIGHT, 
						new ImagePainter(comboImage)));
	}
}
