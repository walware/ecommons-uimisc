/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.actions;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.services.IServiceLocator;


public abstract class WorkbenchScopingHandler extends AbstractHandler
		implements IExecutableExtension, IWindowListener, IHandlerListener, IElementUpdater {
	
	
	private String commandId;
	
	private Map<Object, AbstractScopeHandler> scopeHandlers= new IdentityHashMap<>();
	
	private AbstractScopeHandler enabledHandler;
	
	
	public WorkbenchScopingHandler() {
		init();
	}
	
	public WorkbenchScopingHandler(final String commandId) {
		init();
		
		this.commandId= commandId;
	}
	
	
	private void init() {
		final IWorkbench workbench= PlatformUI.getWorkbench();
		workbench.addWindowListener(this);
	}
	
	@Override
	public void setInitializationData(final IConfigurationElement config,
			final String propertyName, final Object data) throws CoreException {
		{	final String s= config.getAttribute("commandId"); //$NON-NLS-1$
			if (s != null) {
				this.commandId= s.intern();
			}
		}
	}
	
	@Override
	public void dispose() {
		final IWorkbench workbench= PlatformUI.getWorkbench();
		workbench.removeWindowListener(this);
		
		final Map<Object, AbstractScopeHandler> handlers;
		synchronized (this.scopeHandlers) {
			handlers= this.scopeHandlers;
			this.scopeHandlers= Collections.emptyMap();
		}
		for (final Entry<Object, AbstractScopeHandler> entry : handlers.entrySet()) {
			disposeScope(entry.getKey(), entry.getValue());
		}
		
		super.dispose();
	}
	
	
	@Override
	public void windowOpened(final IWorkbenchWindow window) {
	}
	
	@Override
	public void windowClosed(final IWorkbenchWindow window) {
		removeScope(window);
	}
	
	@Override
	public void windowActivated(final IWorkbenchWindow window) {
	}
	
	@Override
	public void windowDeactivated(final IWorkbenchWindow window) {
	}
	
	
	protected String getCommandId() {
		return this.commandId;
	}
	
	
	protected Object getScope(final IServiceLocator serviceLocator) {
		if (serviceLocator != null) {
			return serviceLocator.getService(IWorkbenchWindow.class);
		}
		return null;
	}
	
	protected Object getScope(final IEvaluationContext context) {
		Object o= context.getVariable(IWorkbenchWindow.class.getName());
		if (o == null) {
			o= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		}
		if (o instanceof IWorkbenchWindow) {
			return o;
		}
		return null;
	}
	
	private AbstractScopeHandler getScopeHandler(final Object scope) {
		AbstractScopeHandler handler= null;
		if (scope != null) {
			synchronized (this.scopeHandlers) {
				handler= this.scopeHandlers.get(scope);
				if (handler == null) {
					handler= createScopeHandler(scope);
					handler.addHandlerListener(this);
					this.scopeHandlers.put(scope, handler);
				}
			}
		}
		return handler;
	}
	
	private void removeScope(final Object scope) {
		final AbstractScopeHandler handler;
		synchronized (this.scopeHandlers) {
			handler= this.scopeHandlers.remove(scope);
		}
		if (handler != null) {
			handler.dispose();
		}
	}
	
	protected AbstractScopeHandler createScopeHandler(final Object scope) {
		return null;
	}
	
	protected void disposeScope(final Object scope, final AbstractScopeHandler handler) {
		handler.dispose();
	}
	
	
	@Override
	public synchronized void setEnabled(final Object appContext) {
		if (appContext instanceof IEvaluationContext) {
			final IEvaluationContext evalContext= (IEvaluationContext) appContext;
			final AbstractScopeHandler handler= getScopeHandler(getScope(evalContext));
			this.enabledHandler= handler;
			if (handler != null) {
				handler.setEnabled(evalContext);
				setBaseEnabled(handler.isEnabled());
				return;
			}
		}
		
		setBaseEnabled(false);
	}
	
	@Override
	public synchronized void handlerChanged(final HandlerEvent handlerEvent) {
		final AbstractScopeHandler handler= (AbstractScopeHandler) handlerEvent.getHandler();
		synchronized (this) {
			if (handler == this.enabledHandler) {
				setBaseEnabled(handler.isEnabled());
				return;
			}
		}
		
		handler.refreshElements();
	}
	
	@Override
	public void updateElement(final UIElement element, final Map parameters) {
		final AbstractScopeHandler handler= getScopeHandler(getScope(element.getServiceLocator()));
		if (handler != null) {
			handler.updateElement(element, parameters);
		}
	}
	
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Object appContext= event.getApplicationContext();
		if (appContext instanceof IEvaluationContext) {
			final IEvaluationContext evalContext= (IEvaluationContext) appContext;
			final AbstractScopeHandler handler= getScopeHandler(getScope(evalContext));
			if (handler != null) {
				synchronized (this) {
					handler.setEnabled(evalContext);
					if (!handler.isEnabled()) {
						return null;
					}
				}
				return handler.execute(event, evalContext);
			}
		}
		return null;
	}
	
}
