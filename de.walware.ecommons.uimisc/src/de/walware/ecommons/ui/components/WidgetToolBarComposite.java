/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;

import de.walware.ecommons.ui.util.LayoutUtil;


/**
 * Composite adding a toolbar at the top of a widget.
 */
public class WidgetToolBarComposite extends Composite {
	
	
	private final Listener listener;
	
	private ToolBar leftToolBar;
	private ToolBar rightToolBar;
	
	
	public WidgetToolBarComposite(final Composite parent, final int style) {
		super(parent, style);
		
		{	final GridLayout layout= LayoutUtil.createCompositeGrid(2);
			layout.verticalSpacing= 2;
			setLayout(layout);
		}
		
		this.listener= new Listener() {
			@Override
			public void handleEvent(final Event event) {
				switch (event.type) {
				case SWT.Paint:
					onPaint(event);
					break;
				default:
					break;
				}
			}
		};
		
		createToolBars();
	}
	
	
	private void createToolBars() {
		this.leftToolBar= new ToolBar(this, SWT.FLAT);
		this.leftToolBar.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false));
		
		this.rightToolBar= new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		this.rightToolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
		
		addListener(SWT.Paint, this.listener);
	}
	
	
	public ToolBar getLeftToolBar() {
		return this.leftToolBar;
	}
	
	public ToolBar getRightToolBar() {
		return this.rightToolBar;
	}
	
	
	private void onPaint(final Event event) {
		final GC gc= event.gc;
		
		final Rectangle toolBarBounds= this.leftToolBar.getBounds();
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		final int y= toolBarBounds.y + toolBarBounds.height + 1;
		gc.drawLine(event.x, y, event.x + event.width, y);
	}
	
	public GridData getContentLayoutData() {
		return new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
	}
	
}
