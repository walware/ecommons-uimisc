/*******************************************************************************
 * Copyright (c) 2008-2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.breadcrumb;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * Implementors can provide a breadcrumb inside an editor.
 * 
 * <p>Clients should not implement this interface. They should
 * subclass {@link AbstractBreadcrumb} instead if possible</p>
 */
public interface IBreadcrumb {
	
	
	String WITH_BREADCRUMB_CONTEXT_ID = "de.walware.ecommons.breadcrumb.contexts.WithBreadcrumbContext"; //$NON-NLS-1$
	String SHOW_BREADCRUMB_COMMAND_ID = "org.eclipse.jdt.ui.edit.text.java.gotoBreadcrumb"; //$NON-NLS-1$
	
	
	/**
	 * Create breadcrumb content.
	 * 
	 * @param parent the parent of the content
	 * @return the control containing the created content
	 */
	Control createContent(Composite parent);
	
	/**
	 * Returns the selection provider for this breadcrumb.
	 * 
	 * @return the selection provider for this breadcrumb
	 */
	ISelectionProvider getSelectionProvider();
	
	/**
	 * Activates the breadcrumb. This sets the keyboard focus
	 * inside this breadcrumb and retargets the editor
	 * actions.
	 */
	void activate();
	
	/**
	 * A breadcrumb is active if it either has the focus or another workbench part has the focus and
	 * the breadcrumb had the focus before the other workbench part was made active.
	 * 
	 * @return <code>true</code> if this breadcrumb is active
	 */
	boolean isActive();
	
	/**
	 * Set the input of the breadcrumb to the given element
	 * 
	 * @param element the input element can be <code>null</code>
	 */
	void setInput(Object element);
	
	/**
	 * Dispose all resources hold by this breadcrumb.
	 */
	void dispose();
	
}
