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

package de.walware.ecommons.ui.content;

import de.walware.ecommons.ui.components.SearchText;


public class SearchTextBinding implements SearchText.Listener {
	
	
	protected final SearchText fTextControl;
	
	protected final TableFilterController fController;
	
	protected final TextElementFilter fFilter;
	
	
	/**
	 * Create a new instance of the controller.
	 */
	public SearchTextBinding(final SearchText text,
			final TableFilterController controller, final TextElementFilter filter) {
		fTextControl = text;
		fController = controller;
		fFilter = filter;
		
		fTextControl.addListener(this);
	}
	
	
	@Override
	public void textChanged(final boolean user) {
		final String text = getFilterString();
		
		if (fFilter.setText(text)) {
			fController.refresh(user);
		}
	}
	
	@Override
	public void downPressed() {
		okPressed();
		fController.refresh(true);
	}
	
	@Override
	public void okPressed() {
		final String text = getFilterString();
		
		fFilter.setText(text);
		fController.refresh(true);
	}
	
	/**
	 * Convenience method to return the text of the filter control. If the text
	 * widget is not created, then null is returned.
	 * 
	 * @return String in the text, or null if the text does not exist
	 */
	protected String getFilterString() {
		return fTextControl != null ? fTextControl.getText() : null;
	}
	
}
