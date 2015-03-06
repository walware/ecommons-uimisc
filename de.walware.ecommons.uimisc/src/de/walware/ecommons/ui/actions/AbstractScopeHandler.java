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
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.services.IServiceScopes;


/**
 * Handler limited to a specified scope.
 * 
 * @see WorkbenchScopingHandler
 */
public abstract class AbstractScopeHandler extends AbstractHandler {
	
	
	private String commandId;
	
	private Object scope;
	
	private Map<String, Object> filter;
	
	
	public AbstractScopeHandler(final Object scope, final String commandId) {
		init(scope, commandId);
	}
	
	public AbstractScopeHandler() {
	}
	
	void init(final Object scope, final String commandId) {
		if (this.scope != null) {
			throw new IllegalStateException();
		}
		
		this.scope= scope;
		this.commandId= commandId;
		
		if (this.scope instanceof IWorkbenchWindow) {
			this.filter= Collections.singletonMap(IServiceScopes.WINDOW_SCOPE, this.scope);
		}
	}
	
	public Object getScope() {
		return this.scope;
	}
	
	public String getCommandId() {
		return this.commandId;
	}
	
	
	@Override
	public final void setEnabled(final Object appContext) {
		if (appContext instanceof IEvaluationContext) {
			setEnabled(appContext);
		}
	}
	
	public void setEnabled(final IEvaluationContext context) {
	}
	
	protected void refreshElements() {
		final String commandId= this.commandId;
		if (commandId != null) {
			final IWorkbench workbench= PlatformUI.getWorkbench();
			final ICommandService commandService= (ICommandService) workbench.getService(ICommandService.class);
			if (commandService != null) {
				commandService.refreshElements(commandId, this.filter);
			}
		}
	}
	
	public void updateElement(final UIElement element, final Map parameters) {
	}
	
	@Override
	public final Object execute(final ExecutionEvent event) throws ExecutionException {
		final Object appContext= event.getApplicationContext();
		if (appContext instanceof IEvaluationContext) {
			return execute(event, (IEvaluationContext) appContext);
		}
		return null;
	}
	
	public abstract Object execute(ExecutionEvent event, IEvaluationContext evalContext)
			throws ExecutionException;
	
}
