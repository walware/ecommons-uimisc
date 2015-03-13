/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

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

import de.walware.ecommons.debug.core.variables.ResourceVariableResolver;
import de.walware.ecommons.ui.util.UIAccess;


public class ResourceVariableUtil implements ResourceVariableResolver.IResolveContext {
	
	
	/**
	 * Returns the currently selected resource in the active workbench window, or <code>null</code> 
	 * if none.  If an editor is active, the resource adapter associated with the editor is 
	 * returned, if any.
	 * 
	 * @return selected resource or <code>null</code>
	 */
	public static IResource fetchSelectedResource() {
		return fetchSelectedResource(null);
	}
	
	private static IResource fetchSelectedResource(final IWorkbenchPart part) {
		// Compatible with DebugUITools.getSelectedResource
		final Display display= UIAccess.getDisplay();
		if (Thread.currentThread() == display.getThread()) {
			return getSelectedResource0(part);
		}
		else {
			class DisplayRunnable implements Runnable {
				
				IResource resource;
				
				@Override
				public void run() {
					this.resource= getSelectedResource0(part);
				}
				
			}
			final DisplayRunnable runnable= new DisplayRunnable();
			display.syncExec(runnable);
			return runnable.resource;
		}
	}
	
	private static IResource getSelectedResource0(IWorkbenchPart part) {
		IResource resource= null;
		if (part == null) {
			final IWorkbenchPage page= UIAccess.getActiveWorkbenchPage(true);
			if (page != null) {
				part= page.getActivePart();
			}
		}
		if (part != null) {
			if (part instanceof IEditorPart) {
				final IEditorPart epart= (IEditorPart) part;
				resource= (IResource) epart.getEditorInput().getAdapter(IResource.class);
			}
			if (resource == null) {
				final IWorkbenchPartSite site= part.getSite();
				if (site != null) {
					final ISelectionProvider provider= site.getSelectionProvider();
					if (provider != null) {
						final ISelection selection= provider.getSelection();
						if (selection instanceof IStructuredSelection) {
							final IStructuredSelection structuredSelection= (IStructuredSelection) selection;
							if (!structuredSelection.isEmpty()) {
								final Iterator<?> iterator= structuredSelection.iterator();
								final IAdapterManager adapterManager= Platform.getAdapterManager();
								while (resource == null && iterator.hasNext()) {
									final Object element= iterator.next();
									resource= (IResource) adapterManager.getAdapter(element, IResource.class);
								}
							}
						}
					}
				}
			}
		}
		return resource;
	}
	
	
	protected static final byte S_RESOURCE_FETCHED=         0b0_0000_0001;
	
	
	private final IWorkbenchPage workbenchPage;
	
	private final IWorkbenchPart part;
	
	private byte state;
	
	private IResource resource;
	
	
	
	public ResourceVariableUtil() {
		this(UIAccess.getActiveWorkbenchPage(true));
	}
	
	public ResourceVariableUtil(final IWorkbenchPage page) {
		this.workbenchPage= page;
		this.part= this.workbenchPage.getActivePart();
	}
	
	public ResourceVariableUtil(final IWorkbenchPart part) {
		this.workbenchPage= part.getSite().getPage();
		this.part= part;
	}
	
	public ResourceVariableUtil(final IResource resource) {
		this();
		this.resource= resource;
	}
	
	public ResourceVariableUtil(final ResourceVariableUtil location, final IResource resource) {
		this.workbenchPage= location.getWorkbenchPage();
		this.part= location.getWorkbenchPart();
		this.resource= resource;
	}
	
	
	public IWorkbenchPage getWorkbenchPage() {
		return this.workbenchPage;
	}
	
	public IWorkbenchPart getWorkbenchPart() {
		return this.part;
	}
	
	@Override
	public IResource getResource() {
		if ((this.state & S_RESOURCE_FETCHED) == 0) {
			this.state|= S_RESOURCE_FETCHED;
			this.resource= fetchResource();
		}
		return this.resource;
	}
	
	protected void resetResource() {
		this.state&= ~S_RESOURCE_FETCHED;
		this.resource= null;
	}
	
	protected IResource fetchResource() {
		return fetchSelectedResource(getWorkbenchPart());
	}
	
}
