/*******************************************************************************
 * Copyright (c) 2009-2010 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.mpbv;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.CompoundContributionItem;

import de.walware.ecommons.ui.actions.SimpleContributionItem;


public class ShowPageDropdownContribution<S extends ISession> extends CompoundContributionItem {
	
	
	private final ManagedPageBookView<S> fView;
	
	
	public ShowPageDropdownContribution(final ManagedPageBookView<S> view) {
		fView = view;
	}
	
	
	@Override
	protected IContributionItem[] getContributionItems() {
		final List<IContributionItem> list = new ArrayList<IContributionItem>();
		
		final List<S> sessions = fView.getSessions();
		for (int i = 0; i < sessions.size(); i++) {
			list.add(createPageContribution(sessions.get(i), i+1));
		}
		
		return list.toArray(new IContributionItem[list.size()]);
	}
	
	private ContributionItem createPageContribution(final S session, final int num) {
		final ImageDescriptor imageDescriptor = session.getImageDescriptor();
		String label = session.getLabel();
		String mnemonic = null;
		if (num < 10) {
			mnemonic = Integer.toString(num);
			label = mnemonic + " " + label;
		}
		final SimpleContributionItem item = new SimpleContributionItem(imageDescriptor, null, label, mnemonic, SimpleContributionItem.STYLE_RADIO) {
			@Override
			protected void execute() throws ExecutionException {
				fView.showPage(session);
			}
		};
		if (fView.getCurrentSession() == session) {
			item.setChecked(true);
		}
		return item;
	}
	
}
