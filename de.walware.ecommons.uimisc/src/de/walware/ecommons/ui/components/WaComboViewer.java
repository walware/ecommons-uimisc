/*=============================================================================#
 # Copyright (c) 2005-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.components;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;


public class WaComboViewer extends TableViewer {
	
	
	private final WaCombo fCombo;
	
	
	public WaComboViewer(final WaCombo combo) {
		super(combo.getList());
		
		fCombo = combo;
	}
	
	
	@Override
	public void setSelection(final ISelection selection, final boolean reveal) {
		super.setSelection(selection, reveal);
		final Object object = ((IStructuredSelection) getSelection()).getFirstElement();
		fCombo.updateText((TableItem) ((object != null) ? findItem(object) : null));
	}
	
}
