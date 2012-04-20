/*******************************************************************************
 * Copyright (c) 2012 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.util;

import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import de.walware.ecommons.FastList;


public class PostSelectionProviderProxy implements IPostSelectionProvider {
	
	
	private final FastList<ISelectionChangedListener> fSelectionListeners = new FastList<ISelectionChangedListener>(ISelectionChangedListener.class);
	private final FastList<ISelectionChangedListener> fPostSelectionListeners = new FastList<ISelectionChangedListener>(ISelectionChangedListener.class);
	
	private final IPostSelectionProvider fSelectionProvider;
	
	
	public PostSelectionProviderProxy(final IPostSelectionProvider selectionProvider) {
		fSelectionProvider = selectionProvider;
		
		fSelectionProvider.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				fireSelectionChanged(new SelectionChangedEvent(PostSelectionProviderProxy.this,
						getSelection(event.getSelection()) ));
			}
		});
		fSelectionProvider.addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				firePostSelectionChanged(new SelectionChangedEvent(PostSelectionProviderProxy.this,
						getSelection(event.getSelection()) ));
			}
		});
	}
	
	
	@Override
	public void setSelection(final ISelection selection) {
		fSelectionProvider.setSelection(selection);
	}
	
	@Override
	public ISelection getSelection() {
		return getSelection(fSelectionProvider.getSelection());
	}
	
	
	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		fSelectionListeners.add(listener);
	}
	
	@Override
	public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
		fSelectionListeners.remove(listener);
	}
	
	
	@Override
	public void addPostSelectionChangedListener(final ISelectionChangedListener listener) {
		fPostSelectionListeners.add(listener);
	}
	
	@Override
	public void removePostSelectionChangedListener(final ISelectionChangedListener listener) {
		fPostSelectionListeners.remove(listener);
	}
	
	
	protected ISelection getSelection(final ISelection originalSelection) {
		return originalSelection;
	}
	
	protected void fireSelectionChanged(final SelectionChangedEvent event) {
		final ISelectionChangedListener[] listeners = fSelectionListeners.toArray();
		for (int i = 0; i < listeners.length; i++) {
			final ISelectionChangedListener l = listeners[i];
			SafeRunnable.run(new SafeRunnable() {
				@Override
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}
	
	protected void firePostSelectionChanged(final SelectionChangedEvent event) {
		final ISelectionChangedListener[] listeners = fPostSelectionListeners.toArray();
		for (int i = 0; i < listeners.length; i++) {
			final ISelectionChangedListener l = listeners[i];
			SafeRunnable.run(new SafeRunnable() {
				@Override
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}
	
}
