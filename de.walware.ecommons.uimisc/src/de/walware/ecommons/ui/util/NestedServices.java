/*=============================================================================#
 # Copyright (c) 2012-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.util;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.contexts.NestableContextService;
import org.eclipse.ui.internal.handlers.LegacyHandlerService;
import org.eclipse.ui.internal.services.IServiceLocatorCreator;
import org.eclipse.ui.internal.services.IWorkbenchLocationService;
import org.eclipse.ui.internal.services.ServiceLocator;
import org.eclipse.ui.internal.services.WorkbenchLocationService;
import org.eclipse.ui.services.IDisposable;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.ui.services.IServiceScopes;


/**
 * Util to create a nested service locator with nested services, e.g. for dialogs.
 * 
 * Workaround for E-bug #142226
 */
public class NestedServices implements Listener {
	
	
	public static class Dialog extends NestedServices {
		
		public Dialog(final Shell shell) {
			super("Dialog", PlatformUI.getWorkbench());
			
			registerService(IWorkbenchLocationService.class, new WorkbenchLocationService(
					IServiceScopes.DIALOG_SCOPE,
					PlatformUI.getWorkbench(), null, null, null, null, 1 ));
			
			initializeDefaultServices();
			
			final IContextService contextService = (IContextService) getLocator()
					.getService(IContextService.class);
			contextService.registerShell(shell, IContextService.TYPE_DIALOG);
			
			bindTo(shell);
		}
		
	}
	
	
	private final String fName;
	
	private IEclipseContext fContext;
	private ServiceLocator fServiceLocator;
	
	private boolean fIsActivated;
	
	
	public NestedServices(final IServiceLocator parent, final String name) {
		this(name, parent);
		
		initializeDefaultServices();
	}
	
	protected NestedServices(final String name, final IServiceLocator parent) {
		fName = name;
		final IServiceLocatorCreator slc = (IServiceLocatorCreator) parent
				.getService(IServiceLocatorCreator.class);
		final IEclipseContext parentContext = (IEclipseContext) parent
				.getService(IEclipseContext.class);
		fContext = parentContext.createChild(name);
		fServiceLocator = (ServiceLocator) slc.createServiceLocator(parent, null,
				new IDisposable() {
					@Override
					public void dispose() {
						clear();
					}
				}, fContext);
		if (fServiceLocator == null) {
			throw new RuntimeException("Could not create nested service locator.");
		}
	}
	
	
	protected <T> void registerService(final Class<T> api, final T service) {
		fServiceLocator.registerService(api, service);
	}
	
	protected void initializeDefaultServices() {
		registerService(IContextService.class, new NestableContextService(
				fContext.getParent().get(IContextService.class), null ));
		registerService(IHandlerService.class, new LegacyHandlerService(
				fContext, null ));
	}
	
	public void dispose() {
		if (fServiceLocator != null) {
			fServiceLocator.dispose();
			clear();
		}
	}
	
	private void clear() {
		if (fServiceLocator != null) {
			fServiceLocator = null;
			fContext.dispose();
			fContext = null;
		}
	}
	
	
	public IServiceLocator getLocator() {
		return fServiceLocator;
	}
	
	public IEclipseContext getContext() {
		return fContext;
	}
	
	
	public void bindTo(final Control control) {
		control.addListener(SWT.Activate, this);
		control.addListener(SWT.Deactivate, this);
		control.addListener(SWT.Dispose, this);
	}
	
	@Override
	@SuppressWarnings("restriction")
	public void handleEvent(final Event event) {
		switch (event.type) {
		case SWT.Activate:
		case SWT.FocusIn:
			if (fServiceLocator != null && !fIsActivated) {
				activate(event);
			}
			break;
		case SWT.Deactivate:
		case SWT.FocusOut:
			if (fServiceLocator != null && fIsActivated) {
				deactivate(event);
			}
			break;
		case SWT.Dispose:
			dispose();
			break;
		default:
			break;
		}
	}
	
	
	protected void activate(final Event event) {
		fIsActivated = true;
		fContext.activate();
		event.display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (fServiceLocator != null && fIsActivated) {
					fContext.processWaiting();
					fServiceLocator.activate();
				}
			}
		});
	}
	
	protected void deactivate(final Event event) {
		fIsActivated = false;
		fServiceLocator.deactivate();
		fContext.deactivate();
	}
	
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("NestedServices");
		sb.append(" '").append(fName).append("'");
		if (fServiceLocator == null) {
			sb.append(" (disposed)");
		}
		return sb.toString();
	}
	
}
