/*=============================================================================#
 # Copyright (c) 2010-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     IBM Corporation - initial API and implementation (TextCellEditor)
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;

import de.walware.ecommons.FastList;
import de.walware.ecommons.ui.internal.AccessibleArrowImage;
import de.walware.ecommons.ui.util.MenuUtil;


public class DropDownButton extends Composite {
	
	
	private Button fMainButton;
	private Button fDownButton;
	
	private Menu fMenu;
	
	private final FastList<MenuListener> fMenuListener = new FastList<MenuListener>(MenuListener.class);
	
	
	public DropDownButton(final Composite parent) {
		this(parent, SWT.NONE);
	}
	
	public DropDownButton(final Composite parent, final int buttonStyle) {
		super(parent, SWT.NONE);
		
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				if (fMenu != null) {
					fMenu.dispose();
					fMenu = null;
				}
			}
		});
		create(buttonStyle);
	}
	
	
	private void create(final int style) {
		fDownButton = new Button(this, SWT.PUSH | style);
		final AccessibleArrowImage image = new AccessibleArrowImage(SWT.DOWN, SWT.DEFAULT,
				fDownButton.getForeground().getRGB(), fDownButton.getBackground().getRGB() );
		fDownButton.setImage(image.createImage());
		fDownButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Menu menu = getDropDownMenu();
				MenuUtil.setPullDownPosition(menu, DropDownButton.this);
				menu.setVisible(true);
			}
		});
		
		fMainButton = new Button(this, SWT.PUSH | style);
		
		setTabList(new Control[] { fMainButton, fDownButton });
		
		addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				final Rectangle clientArea = getClientArea();
				final Point size = fDownButton.computeSize(SWT.DEFAULT, clientArea.height);
				fDownButton.setBounds(clientArea.width - size.x, clientArea.y, size.x, clientArea.height);
				fMainButton.setBounds(clientArea.x, clientArea.y, clientArea.width - size.x + 1, clientArea.height);
			}
		});
	}
	
	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		final Point downSize = fDownButton.computeSize(SWT.DEFAULT, hHint);
		final Point mainSize = (wHint == SWT.DEFAULT) ?
				fMainButton.computeSize(wHint, hHint) :
				fMainButton.computeSize(Math.max(0, wHint - downSize.x), hHint);
		final Rectangle trim = super.computeTrim(0, 0, mainSize.x + downSize.x - 1, Math.max(mainSize.y, downSize.y));
		return new Point (trim.width, trim.height);
	}
	
	
	public void setText(final String string) {
		fMainButton.setText(string);
	}
	
	@Override
	public void setEnabled(final boolean enabled) {
		fMainButton.setEnabled(enabled);
		fDownButton.setEnabled(enabled);
	}
	
	@Override
	public void setToolTipText(final String string) {
		fMainButton.setToolTipText(string);
	}
	
	public void setOptionToolTipText(final String string) {
		fDownButton.setToolTipText(string);
	}
	
	public void addSelectionListener(final SelectionListener listener) {
		fMainButton.addSelectionListener(listener);
	}
	
	public void removeSelectionListener(final SelectionListener listener) {
		fMainButton.removeSelectionListener(listener);
	}
	
	public void addMenuListener(final MenuListener listener) {
		fMenuListener.add(listener);
		if (fMenu != null) {
			fMenuListener.add(listener);
		}
	}
	
	public void removeMenuListener(final MenuListener listener) {
		fMenuListener.remove(listener);
		if (fMenu != null) {
			fMenuListener.remove(listener);
		}
	}
	
	
	public Menu getDropDownMenu() {
		Menu menu = fMenu;
		if (menu == null) {
			menu = createDropDownMenu();
			final Listener listener = new Listener() {
				@Override
				public void handleEvent(final Event event) {
					switch (event.type) {
						
					case SWT.Dispose:
						if (fMenu == event.widget) {
							fMenu = null;
						}
						return;
					
					default:
						return;
					}
				}
			};
			menu.addListener(SWT.Dispose, listener);
			final MenuListener[] listeners = fMenuListener.toArray();
			for (int i = 0; i < listeners.length; i++) {
				menu.addMenuListener(listeners[i]);
			}
			
			fMenu = menu;
		}
		return menu;
	}
	
	protected Menu createDropDownMenu() {
		return new Menu(this);
	}
	
}
