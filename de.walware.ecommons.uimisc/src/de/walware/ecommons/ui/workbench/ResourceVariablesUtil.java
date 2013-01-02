/*******************************************************************************
 * Copyright (c) 2012-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.workbench;

import java.util.Iterator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;

import de.walware.ecommons.ui.util.UIAccess;


public class ResourceVariablesUtil {
	
	
	/**
	 * Returns the currently selected resource in the active workbench window, or <code>null</code> 
	 * if none.  If an editor is active, the resource adapter associated with the editor is 
	 * returned, if any.
	 * 
	 * @return selected resource or <code>null</code>
	 */
	public static IResource fetchSelectedResource() {
		// Compatible with DebugUITools.getSelectedResource
		final Display display = UIAccess.getDisplay();
		if (display.getThread().equals(Thread.currentThread())) {
			return getSelectedResource0();
		}
		else {
			class DisplayRunnable implements Runnable {
				
				IResource resource;
				
				@Override
				public void run() {
					resource = getSelectedResource0();
				}
				
			}
			final DisplayRunnable runnable = new DisplayRunnable();
			display.syncExec(runnable);
			return runnable.resource;
		}
	}
	
	private static IResource getSelectedResource0() {
		IResource resource = null;
		final IWorkbenchPage page = UIAccess.getActiveWorkbenchPage(true);
		if (page != null) {
			final IWorkbenchPart part = page.getActivePart();
			if (part instanceof IEditorPart) {
				final IEditorPart epart = (IEditorPart) part;
				resource = (IResource) epart.getEditorInput().getAdapter(IResource.class);
			}
			if (resource == null) {
				final IWorkbenchPartSite site = part.getSite();
				if (site != null) {
					final ISelectionProvider provider = site.getSelectionProvider();
					if (provider != null) {
						final ISelection selection = provider.getSelection();
						if (selection instanceof IStructuredSelection) {
							final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
							if (!structuredSelection.isEmpty()) {
								final Iterator<?> iterator = structuredSelection.iterator();
								final IAdapterManager adapterManager = Platform.getAdapterManager();
								while (resource == null && iterator.hasNext()) {
									final Object element = iterator.next();
									resource = (IResource) adapterManager.getAdapter(element, IResource.class);
								}
							}
						}
					}
				}
			}
		}
		return resource;
	}
	
	
	private final IWorkbenchPage fWorkbenchPage;
	
	private IWorkbenchPart fPart;
	
	private IResource fResource;
	
	
	public ResourceVariablesUtil() {
		fWorkbenchPage = UIAccess.getActiveWorkbenchPage(true);
	}
	
	
	public IWorkbenchPage getWorkbenchPage() {
		return fWorkbenchPage;
	}
	
	public IWorkbenchPart getWorkbenchPart() {
		if (fPart == null) {
			fPart = fWorkbenchPage.getActivePart();
		}
		return fPart;
	}
	
	public IResource getSelectedResource() {
		if (fResource == null) {
			fResource = fetchSelectedResource();
		}
		return fResource;
	}
	
}
