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

package de.walware.ecommons.ui.content;

import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import de.walware.ecommons.ui.util.PostSelectionProviderProxy;
import de.walware.ecommons.ui.util.StructuredSelectionProxy;


public class ElementSourceSelectionProvider extends PostSelectionProviderProxy {
	
	
	protected static class StructuredSelection extends StructuredSelectionProxy
			implements IElementSourceProvider {
		
		
		private final Object fSource;
		
		
		public StructuredSelection(final IStructuredSelection selection, final Object source) {
			super(selection);
			
			fSource = source;
		}
		
		
		@Override
		public Object getElementSource() {
			return fSource;
		}
		
	}
	
	
	private final Object fSource;
	
	
	public ElementSourceSelectionProvider(final IPostSelectionProvider selectionProvider, final Object source) {
		super(selectionProvider);
		
		fSource = source;
	}
	
	
	@Override
	protected ISelection getSelection(final ISelection originalSelection) {
		return new StructuredSelection((IStructuredSelection) originalSelection, fSource);
	}
	
}
