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

import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;


public class PostSelectionProviderProxy extends AbstractPostSelectionProvider {
	
	
	private final ISelectionChangedListener fSelectionChangedListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(final SelectionChangedEvent event) {
			fireSelectionChanged(new SelectionChangedEvent(PostSelectionProviderProxy.this,
					getSelection(event.getSelection()) ));
		}
	};
	private final ISelectionChangedListener fPostSelectionChangedListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(final SelectionChangedEvent event) {
			firePostSelectionChanged(new SelectionChangedEvent(PostSelectionProviderProxy.this,
					getSelection(event.getSelection()) ));
		}
	};
	
	protected ISelectionProvider fSelectionProvider;
	
	
	public PostSelectionProviderProxy(final ISelectionProvider selectionProvider) {
		setSelectionProvider(selectionProvider);
	}
	
	public PostSelectionProviderProxy() {
	}
	
	
	protected void setSelectionProvider(final ISelectionProvider selectionProvider) {
		if (fSelectionProvider == selectionProvider) {
			return;
		}
		if (fSelectionProvider != null) {
			fSelectionProvider.removeSelectionChangedListener(fSelectionChangedListener);
			if (fSelectionProvider instanceof IPostSelectionProvider) {
				((IPostSelectionProvider) fSelectionProvider).removePostSelectionChangedListener(fPostSelectionChangedListener);
			}
			else {
				fSelectionProvider.removeSelectionChangedListener(fPostSelectionChangedListener);
			}
		}
		fSelectionProvider = selectionProvider;
		if (fSelectionProvider != null) {
			fSelectionProvider.addSelectionChangedListener(fSelectionChangedListener);
			if (fSelectionProvider instanceof IPostSelectionProvider) {
				((IPostSelectionProvider) fSelectionProvider).addPostSelectionChangedListener(fPostSelectionChangedListener);
			}
			else {
				fSelectionProvider.addSelectionChangedListener(fPostSelectionChangedListener);
			}
		}
		
		{	final SelectionChangedEvent event = new SelectionChangedEvent(
					this, getSelection(getSelection()) );
			
			fireSelectionChanged(event);
			firePostSelectionChanged(event);
		}
	}
	
	protected ISelectionProvider getSelectionProvider() {
		return fSelectionProvider;
	}
	
	
	@Override
	public void setSelection(final ISelection selection) {
		if (fSelectionProvider != null) {
			fSelectionProvider.setSelection(selection);
		}
	}
	
	@Override
	public ISelection getSelection() {
		return getSelection((fSelectionProvider != null) ? fSelectionProvider.getSelection() : null);
	}
	
	protected ISelection getSelection(final ISelection originalSelection) {
		return (originalSelection != null) ? originalSelection : StructuredSelection.EMPTY;
	}
	
}
