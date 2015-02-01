/*=============================================================================#
 # Copyright (c) 2007-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.util;

import org.eclipse.jface.action.IMenuListener2;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.IMenuService;


public class MenuUtil {
	
	
	public static void setPullDownPosition(final Menu menu, final Control control) {
		Point p = control.getLocation();
		p.y += control.getSize().y;
		p = control.getParent().toDisplay(p);
		
		menu.setLocation(p);
	}
	
	public static void setPullDownPosition(final Menu menu, final ToolItem item) {
		final Rectangle bounds = item.getBounds();
		final Point p = item.getParent().toDisplay(bounds.x, bounds.y + bounds.height);
		
		menu.setLocation(p);
	}
	
	public static void registerOneWayMenu(final MenuManager menuManager, final String id) {
		final IMenuService menuService = (IMenuService) PlatformUI.getWorkbench()
				.getService(IMenuService.class);
		final Menu menu = menuManager.getMenu();
		menuManager.addMenuListener(new IMenuListener2() {
			@Override
			public void menuAboutToShow(final IMenuManager manager) {
				menuService.populateContributionManager(
						menuManager, "menu:" + id); //$NON-NLS-1$
			}
			@Override
			public void menuAboutToHide(final IMenuManager manager) {
				menu.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						menuService.releaseContributions(menuManager);
						menuManager.dispose();
					}
				});
			}
		});
	}
	
}
