/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.actions.CompoundContributionItem;


/**
 * CompoundContributionItem with public access to contribution items
 */
public abstract class ListContributionItem extends CompoundContributionItem {
	
	
	public ListContributionItem() {
	}
	
	public ListContributionItem(final String id) {
		super(id);
	}
	
	
	@Override
	protected IContributionItem[] getContributionItems() {
		final List<IContributionItem> items= new ArrayList<IContributionItem>();
		
		createContributionItems(items);
		
		return items.toArray(new IContributionItem[items.size()]);
	}
	
	protected abstract void createContributionItems(List<IContributionItem> items);
	
	
	/**
	 * Delegates the creation of contribution items to the specified item.
	 */
	protected void createContributionItems(final List<IContributionItem> items,
			final ListContributionItem item) {
		item.createContributionItems(items);
	}
	
}
