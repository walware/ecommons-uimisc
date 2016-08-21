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

package de.walware.ecommons.waltable.copy;

import de.walware.ecommons.waltable.command.AbstractContextFreeCommand;
import de.walware.ecommons.waltable.config.IConfigRegistry;


public class CopyToClipboardCommand extends AbstractContextFreeCommand {
	
	
	private final String cellDelimeter;
	private final String rowDelimeter;
	private final IConfigRegistry configRegistry;
	
	
	public CopyToClipboardCommand(final String cellDelimeter, final String rowDelimeter, final IConfigRegistry configRegistry) {
		this.cellDelimeter= cellDelimeter;
		this.rowDelimeter= rowDelimeter;
		this.configRegistry= configRegistry;
	}
	
	
	public String getCellDelimeter() {
		return this.cellDelimeter;
	}
	
	public String getRowDelimeter() {
		return this.rowDelimeter;
	}
	
	public IConfigRegistry getConfigRegistry() {
		return this.configRegistry;
	}
	
}
