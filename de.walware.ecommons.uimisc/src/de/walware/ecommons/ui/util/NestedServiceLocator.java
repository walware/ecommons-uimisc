/*******************************************************************************
 * Copyright (c) 2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.util;

import org.eclipse.core.expressions.Expression;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.handlers.NestableHandlerService;
import org.eclipse.ui.internal.services.IServiceLocatorCreator;
import org.eclipse.ui.internal.services.ServiceLocator;
import org.eclipse.ui.services.IDisposable;
import org.eclipse.ui.services.IServiceLocator;


/**
 * Util to create a nested service locator with nested services, e.g. for dialogs.
 */
public class NestedServiceLocator implements Listener {
	
	
	private ServiceLocator fLocator;
	
	
	public NestedServiceLocator(IServiceLocator parent, Expression expression) {
		final IServiceLocatorCreator serviceCreator = (IServiceLocatorCreator) parent.getService(
				IServiceLocatorCreator.class );
		fLocator = (ServiceLocator) serviceCreator.createServiceLocator(parent, null, new IDisposable() {
			@Override
			public void dispose() {
				clear();
			}
		});
		if (expression != null) {
			fLocator.registerService(IHandlerService.class, new NestableHandlerService(
					(IHandlerService) parent.getService(IHandlerService.class), expression ));
		}
	}
	
	
	public void dispose() {
		if (fLocator != null) {
			fLocator.dispose();
			clear();
		}
	}
	
	private void clear() {
		fLocator = null;
	}
	
	
	public IServiceLocator getLocator() {
		return fLocator;
	}
	
	
	public void bindTo(final Control control) {
		control.addListener(SWT.Activate, this);
		control.addListener(SWT.Deactivate, this);
		control.addListener(SWT.Dispose, this);
	}
	
	@Override
	@SuppressWarnings("restriction")
	public void handleEvent(Event event) {
		switch (event.type) {
		case SWT.Activate:
		case SWT.FocusIn:
			if (fLocator != null) {
				fLocator.activate();
			}
			break;
		case SWT.Deactivate:
		case SWT.FocusOut:
			if (fLocator != null) {
				fLocator.deactivate();
			}
			break;
		case SWT.Dispose:
			dispose();
			break;
		default:
			break;
		}
	}
	
}
