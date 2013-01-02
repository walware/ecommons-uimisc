/*******************************************************************************
 * Copyright (c) 2008-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.workbench.ui;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;


/**
 * Util methods for Eclipse workbench
 */
public class WorkbenchUIUtil {
	
	
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
	
	
	private WorkbenchUIUtil() {}
	
}
