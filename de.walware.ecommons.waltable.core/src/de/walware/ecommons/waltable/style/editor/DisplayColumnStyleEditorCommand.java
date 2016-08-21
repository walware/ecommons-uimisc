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
package de.walware.ecommons.waltable.style.editor;

import de.walware.ecommons.waltable.command.AbstractContextFreeCommand;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.layer.ILayer;

public class DisplayColumnStyleEditorCommand extends AbstractContextFreeCommand {

	public final long columnPosition;
	public final long rowPosition;
	private final ILayer layer;
	private final IConfigRegistry configRegistry;

	public DisplayColumnStyleEditorCommand(final ILayer natLayer, final IConfigRegistry configRegistry, final long columnPosition, final long rowPosition) {
		this.layer= natLayer;
		this.configRegistry= configRegistry;
		this.columnPosition= columnPosition;
		this.rowPosition= rowPosition;
	}
	
	public ILayer getNattableLayer() {
		return this.layer;
	}

	public IConfigRegistry getConfigRegistry() {
		return this.configRegistry;
	}
}
