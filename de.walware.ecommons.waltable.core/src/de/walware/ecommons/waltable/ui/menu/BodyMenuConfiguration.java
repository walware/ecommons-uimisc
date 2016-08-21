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
package de.walware.ecommons.waltable.ui.menu;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Menu;

import de.walware.ecommons.waltable.Messages;
import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.config.AbstractUiBindingConfiguration;
import de.walware.ecommons.waltable.grid.GridRegion;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.ui.binding.UiBindingRegistry;
import de.walware.ecommons.waltable.ui.matcher.MouseEventMatcher;

public class BodyMenuConfiguration extends AbstractUiBindingConfiguration {

	private final Menu colHeaderMenu;
	
	public BodyMenuConfiguration(final NatTable natTable, final ILayer bodyLayer) {
		this.colHeaderMenu= new PopupMenuBuilder(natTable)
								.withColumnStyleEditor(Messages.getString("ColumnStyleEditorDialog.shellTitle")) //$NON-NLS-1$
								.build();
		
		natTable.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				BodyMenuConfiguration.this.colHeaderMenu.dispose();
			}
			
		});
	}
	
	@Override
	public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
		uiBindingRegistry.registerMouseDownBinding(
				new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, 3), 
				new PopupMenuAction(this.colHeaderMenu));
	}

}
