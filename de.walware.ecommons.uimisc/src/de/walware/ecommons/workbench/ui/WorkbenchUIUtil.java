/*=============================================================================#
 # Copyright (c) 2008-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.workbench.ui;

import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.commands.contexts.Context;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.services.IServiceLocator;


/**
 * Util methods for Eclipse workbench
 */
public class WorkbenchUIUtil {
	
	
	public static final boolean IS_E4 = (Platform.getBundle("org.eclipse.e4.ui.workbench") != null); //$NON-NLS-1$
	
	
	public static ISelection getCurrentSelection(final Object context) {
		if (context instanceof IEvaluationContext) {
			final IEvaluationContext evaluationContext = (IEvaluationContext) context;
			Object object = evaluationContext.getVariable(ISources.ACTIVE_SITE_NAME);
			if (object instanceof IWorkbenchSite) {
				final IWorkbenchSite site = (IWorkbenchSite) object;
				final ISelectionProvider selectionProvider = site.getSelectionProvider();
				if (selectionProvider != null) {
					return selectionProvider.getSelection();
				}
				return null;
			}
			else {
				object = evaluationContext.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
				if (object instanceof ISelection) {
					return (ISelection) object;
				}
			}
		}
		return null;
	}
	
	public static IWorkbenchPart getActivePart(final Object context) {
		if (context instanceof IEvaluationContext) {
			final Object object = ((IEvaluationContext) context).getVariable(ISources.ACTIVE_PART_NAME);
			if (object instanceof IWorkbenchPart) {
				return (IWorkbenchPart) object;
			}
		}
		return null;
	}
	
	public static Control getActiveFocusControl(final Object context) {
		if (context instanceof IEvaluationContext) {
			final Object object = ((IEvaluationContext) context).getVariable(ISources.ACTIVE_FOCUS_CONTROL_NAME);
			if (object instanceof Control) {
				return (Control) object;
			}
		}
		return null;
	}
	
	public static KeySequence getBestKeyBinding(final String commandId) {
		final IBindingService bindingSvc = (IBindingService) PlatformUI.getWorkbench().getService(IBindingService.class);
		if (bindingSvc == null) {
			return null;
		}
		{	final TriggerSequence binding = bindingSvc.getBestActiveBindingFor(commandId);
			if (binding instanceof KeySequence) {
				return (KeySequence) binding;
			}
		}
		{	final TriggerSequence[] bindings = bindingSvc.getActiveBindingsFor(commandId);
			for (int i = 0; i < bindings.length; i++) {
				if (bindings[i] instanceof KeySequence) {
					return (KeySequence) bindings[i];
				}
			}
		}
		return null;
	}
	
	public static void activateContext(final IServiceLocator serviceLocator, String contextId) {
		final IContextService contextService = (IContextService) serviceLocator
				.getService(IContextService.class);
		try {
			do {
				final Context context = contextService.getContext(contextId);
				if (context == null || !context.isDefined()) {
					break;
				}
				contextService.activateContext(contextId);
				
				contextId = context.getParentId();
			} while (contextId != null
					&& !contextId.equals(IContextService.CONTEXT_ID_DIALOG)
					&& !contextId.equals(IContextService.CONTEXT_ID_DIALOG_AND_WINDOW)
					&& !contextId.equals(IContextService.CONTEXT_ID_WINDOW) );
		}
		catch (final NotDefinedException e) {}
	}
	
	
	private WorkbenchUIUtil() {}
	
}
