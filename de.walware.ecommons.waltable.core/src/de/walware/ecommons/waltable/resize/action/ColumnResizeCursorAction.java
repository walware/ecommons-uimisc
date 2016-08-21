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
package de.walware.ecommons.waltable.resize.action;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;

import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.ui.action.IMouseAction;

public class ColumnResizeCursorAction implements IMouseAction {

	private Cursor columnResizeCursor;

	@Override
	public void run(final NatTable natTable, final MouseEvent event) {
		if (this.columnResizeCursor == null) {
			this.columnResizeCursor= new Cursor(Display.getDefault(), SWT.CURSOR_SIZEWE);
			
			natTable.addDisposeListener(new DisposeListener() {
				
				@Override
				public void widgetDisposed(final DisposeEvent e) {
					ColumnResizeCursorAction.this.columnResizeCursor.dispose();
				}
				
			});
		}
		
		natTable.setCursor(this.columnResizeCursor);
	}
	
}
