/*******************************************************************************
 * Copyright (c) 2010 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation (TextCellEditor)
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;

import de.walware.ecommons.ui.util.LayoutUtil;


public class DropDownButton extends Composite {
	
	
	private Button fMainButton;
	private Button fDownButton;
	
	
	public DropDownButton(final Composite parent) {
		super(parent, SWT.NONE);
		
		create();
	}
	
	
	private void create() {
		final GridLayout layout = new GridLayout();
		LayoutUtil.applyCompositeDefaults(layout, 2);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		setLayout(layout);
		fMainButton = new Button(this, SWT.PUSH);
		fMainButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fDownButton = new Button(this, SWT.PUSH);
		fDownButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		fDownButton.setImage(WorkbenchImages.getImage(IWorkbenchGraphicConstants.IMG_LCL_BUTTON_MENU));
		fDownButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				showDropDown();
			}
		});
	}
	
	
	public void setText(final String string) {
		fMainButton.setText(string);
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
	
	
	private void showDropDown() {
		final Menu menu = new Menu(this);
		final Control c = this;
		Point p = c.getLocation();
		p.y = p.y + c.getSize().y;
		p = c.getParent().toDisplay(p);
		
		menu.setLocation(p);
		fillDropDownMenu(menu);
		menu.setVisible(true);
	}
	
	protected void fillDropDownMenu(final Menu menu) {
	}
	
}
