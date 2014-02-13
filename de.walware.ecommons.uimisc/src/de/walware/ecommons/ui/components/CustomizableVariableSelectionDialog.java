/*=============================================================================#
 # Copyright (c) 2009-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.swt.widgets.Shell;


/**
 * {@link StringVariableSelectionDialog} with option to exclude and add variables.
 */
public class CustomizableVariableSelectionDialog extends StringVariableSelectionDialog {
	
	
	private final List<IStringVariable> fAdditionals = new ArrayList<IStringVariable>();
	
	private boolean fInitialized;
	private Object[] fElements;
	
	
	public CustomizableVariableSelectionDialog(final Shell parent) {
		super(parent);
		
		fInitialized = true;
	}
	
	
	@Override
	public void setElements(final Object[] elements) {
		fElements = elements;
		if (fInitialized) {
			initElements();
		}
	}
	
	private void initElements() {
		final IStringVariable[] orginals = (IStringVariable[]) fElements;
		final List<IStringVariable> list = new ArrayList<IStringVariable>(
				orginals.length + fAdditionals.size());
		list.addAll(fAdditionals);
		list.addAll(Arrays.asList(orginals));
		super.setElements(list.toArray(new IStringVariable[list.size()]));
	}
	
	@Override
	public int open() {
		initElements();
		return super.open();
	}
	
	
	public void setFilters(final List<VariableFilter> filters) {
		super.setFilters(filters.toArray(new VariableFilter[filters.size()]));
	}
	
	public void addAdditional(final IStringVariable variable) {
		fAdditionals.add(variable);
	}
	
	public void setAdditionals(final List<? extends IStringVariable> variables) {
		fAdditionals.addAll(variables);
	}
	
}
