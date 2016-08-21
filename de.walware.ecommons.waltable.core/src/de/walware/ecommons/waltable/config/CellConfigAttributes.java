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
package de.walware.ecommons.waltable.config;

import de.walware.ecommons.waltable.data.convert.IDisplayConverter;
import de.walware.ecommons.waltable.painter.cell.ICellPainter;
import de.walware.ecommons.waltable.style.ConfigAttribute;
import de.walware.ecommons.waltable.style.IStyle;

public interface CellConfigAttributes {

	ConfigAttribute<ICellPainter> CELL_PAINTER= new ConfigAttribute<>();
	
	ConfigAttribute<IStyle> CELL_STYLE= new ConfigAttribute<>();
	
	ConfigAttribute<IDisplayConverter> DISPLAY_CONVERTER= new ConfigAttribute<>();
	
}
