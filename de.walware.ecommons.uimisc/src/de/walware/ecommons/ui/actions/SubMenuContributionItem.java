/*******************************************************************************
 * Copyright (c) 2006-2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Wahlbrink - modified for sub menus
 *******************************************************************************/

package de.walware.ecommons.ui.actions;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;


public abstract class SubMenuContributionItem extends ContributionItem implements MenuListener {
	
	
	private MenuItem fMenuItem;
	private Menu fMenu;
	private boolean fIsMenuInitialized;
	
	
	public SubMenuContributionItem() {
		super();
	}
	
	
	@Override
	public void dispose() {
		if (menuExist()) {
			fMenu.dispose();
			fMenu = null;
		}
		
		if (fMenuItem != null) {
			fMenuItem.dispose();
			fMenuItem = null;
		}
	}
	
	/**
	 * Returns whether the menu control is created
	 * and not disposed.
	 * 
	 * @return <code>true</code> if the control is created
	 *     and not disposed, <code>false</code> otherwise
	 */
	private boolean menuExist() {
		return fMenu != null && !fMenu.isDisposed();
	}
	
	@Override
	public void fill(final Menu parent, final int index) {
		if (fMenuItem == null || fMenuItem.isDisposed()) {
			if (index >= 0) {
				fMenuItem = new MenuItem(parent, SWT.CASCADE, index);
			} else {
				fMenuItem = new MenuItem(parent, SWT.CASCADE);
			}
			
			fMenuItem.setText(getLabel());
			fMenuItem.setImage(getImage());
			
			if (!menuExist()) {
				fMenu = new Menu(parent);
				fMenu.addMenuListener(this);
			}
			
			fMenuItem.setMenu(fMenu);
		}
	}
	
	
	protected abstract Image getImage();
	protected abstract String getLabel();
	
	protected abstract void fillMenu(final Menu menu);
	
	
	@Override
	public void menuShown(final MenuEvent e) {
		if (!fIsMenuInitialized) {
			fIsMenuInitialized = true;
			fillMenu(fMenu);
		}
	}
	
	@Override
	public void menuHidden(final MenuEvent e) {
	}
	
}
