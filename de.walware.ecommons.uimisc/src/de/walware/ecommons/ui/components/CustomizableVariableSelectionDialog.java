/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.swt.widgets.Shell;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.variables.core.VariableUtils;


/**
 * {@link StringVariableSelectionDialog} with option to exclude and add variables.
 */
public class CustomizableVariableSelectionDialog extends StringVariableSelectionDialog {
	
	
	private final List<IStringVariable> extraVariables= new ArrayList<>();
	
	private final boolean initialized;
	private ImList<IStringVariable> elements;
	
	
	public CustomizableVariableSelectionDialog(final Shell parent) {
		super(parent);
		
		if (this.elements == null) {
			this.elements= ImCollections.emptyList();
		}
		this.initialized= true;
	}
	
	
	@Override
	public void setElements(final Object[] elements) {
		this.elements= ImCollections.newList((IStringVariable[]) elements);
		if (this.initialized) {
			initElements();
		}
	}
	
	private void initElements() {
		final Map<String, IStringVariable> variables= new HashMap<>();
		VariableUtils.add(variables, this.elements);
		VariableUtils.add(variables, this.extraVariables);
		super.setElements(variables.values().toArray(new IStringVariable[variables.size()]));
	}
	
	@Override
	public int open() {
		initElements();
		return super.open();
	}
	
	
	public void setFilters(final List<VariableFilter> filters) {
		super.setFilters(filters.toArray(new VariableFilter[filters.size()]));
	}
	
	public void setAdditionals(final Collection<? extends IStringVariable> variables) {
		this.extraVariables.clear();
		this.extraVariables.addAll(variables);
	}
	
	public void addAdditional(final IStringVariable variable) {
		this.extraVariables.add(variable);
	}
	
}
