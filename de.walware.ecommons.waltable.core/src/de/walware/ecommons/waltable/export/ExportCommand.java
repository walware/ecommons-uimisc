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
package de.walware.ecommons.waltable.export;


import org.eclipse.swt.widgets.Shell;

import de.walware.ecommons.waltable.command.AbstractContextFreeCommand;
import de.walware.ecommons.waltable.config.IConfigRegistry;

public class ExportCommand extends AbstractContextFreeCommand {

	private final IConfigRegistry configRegistry;
	private final Shell shell;

	public ExportCommand(final IConfigRegistry configRegistry, final Shell shell) {
		this.configRegistry= configRegistry;
		this.shell= shell;
	}

	public IConfigRegistry getConfigRegistry() {
		return this.configRegistry;
	}

	public Shell getShell() {
		return this.shell;
	}
}
