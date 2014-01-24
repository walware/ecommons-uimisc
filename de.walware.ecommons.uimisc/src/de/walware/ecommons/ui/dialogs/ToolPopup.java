/*=============================================================================#
 # Copyright (c) 2013-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.util.DialogUtil;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.UIAccess;


public class ToolPopup {
	
	
	private static final Color G_BACKGROUND = SharedUIResources.getColors().getColor(SharedUIResources.GRAPHICS_BACKGROUND_COLOR_ID);
	
	
	private class SWTListener implements Listener {
		
		@Override
		public void handleEvent(final Event event) {
			switch (event.type) {
			case SWT.Deactivate:
				if (fIgnoreActivation == 0) {
					close();
				}
				return;
			case SWT.Dispose:
				dispose();
				return;
			case SWT.Selection:
				if (event.widget == fOKButton) {
					onOK();
					close();
					return;
				}
				if (event.widget == fCancelButton) {
					close();
					return;
				}
				if (event.widget == fTabFolder) {
					tabSelected(getTab(fTabFolder.getSelection()));
					return;
				}
			}
		}
		
	}
	
	public static class ToolTab {
		
		
		private final String fKey;
		
		private final ToolPopup fParent;
		private final CTabItem fTabItem;
		private Composite fComposite;
		
		
		public ToolTab(final String key, final ToolPopup parent,
				final String name, final String tooltip) {
			fKey = key;
			fParent = parent;
			fTabItem = new CTabItem(parent.fTabFolder, SWT.NONE);
			fTabItem.setText(name);
			fTabItem.setToolTipText(tooltip);
			parent.fToolTabs.add(this);
		}
		
		
		public ToolPopup getParent() {
			return fParent;
		}
		public CTabItem getTabItem() {
			return fTabItem;
		}
		
		protected Composite create() {
			final Composite composite = new Composite(fParent.getTabFolder(), SWT.NONE);
			fTabItem.setControl(composite);
			composite.setBackground(G_BACKGROUND);
			return composite;
		}
		
		protected void activated() {
		}
		
		protected void performOK() {
			fParent.onOK();
			fParent.close();
		}
		
	}
	
	protected static abstract class PreviewCanvas extends Canvas implements PaintListener {
		
		
		private static final int DEFAULT_WIDTH = 50;
		
		
		public PreviewCanvas(final Composite parent) {
			super(parent, SWT.NONE);
			
			addPaintListener(this);
			setLayoutData(createGD());
		}
		
		@Override
		public void paintControl(final PaintEvent e) {
			final GC gc = e.gc;
			gc.setForeground(e.display.getSystemColor(SWT.COLOR_DARK_GRAY));
			final Rectangle size = getClientArea();
			final int width = Math.min(DEFAULT_WIDTH, size.width / 2);
			final int height = size.height - 7;
			final int x0 = size.x;
			final int y0 = size.y + (size.height - height) / 2;
			
			gc.drawRectangle(x0, y0, width, height);
			gc.drawRectangle(x0 + width, y0, width, height);
			
			drawPreview(gc, 0, x0 + 1, y0 + 1, width - 2, height - 2);
			drawPreview(gc, 1, x0 + width + 1, y0 + 1, width - 2, height - 2);
		}
		
		public GridData createGD() {
			return new GridData(SWT.FILL, SWT.FILL, true, false);
		}
		
		@Override
		public Point computeSize(final int wHint, final int hHint, final boolean changed) {
			int width = 1 + DEFAULT_WIDTH * 2;
			if (wHint != -1 && wHint < width) {
				width = Math.max(width / 2, wHint);
			}
			final int height = (hHint != -1) ? hHint : (4 + LayoutUtil.defaultHSpacing()); 
			
			return new Point(width, height);
		}
		
		protected abstract void drawPreview(GC gc, int idx, int x, int y, int width, int height);
		
	}
	
	
	private Shell fShell;
	
	private CTabFolder fTabFolder;
	
	private Button fOKButton;
	private Button fCancelButton;
	
	private final List<ToolTab> fToolTabs = new ArrayList<ToolTab>();
	
	private int fIgnoreActivation;
	
	
	public ToolPopup() {
	}
	
	
	protected void open(final Shell parent, final Rectangle position) {
		create(parent);
		
		final Point size = fShell.getSize();
		final Display display = fShell.getDisplay();
		final Monitor monitor = DialogUtil.getClosestMonitor(display, position);
		final Rectangle clientArea = monitor.getClientArea();
		
		final Rectangle bounds = new Rectangle(position.x , position.y - size.y, size.x, size.y);
		if (bounds.y < 0) {
			bounds.y = position.y + position.height;
		}
		Geometry.moveInside(bounds, clientArea);
		
		fShell.setBounds(bounds);
		
		selectTab(getBestTab());
		
		fShell.open();
	}
	
	
	public boolean isActive() {
		return (UIAccess.isOkToUse(fShell) && fShell.isVisible());
	}
	
	
	public void close() {
		if (UIAccess.isOkToUse(fShell)) {
			fShell.close();
		}
		dispose();
	}
	
	public void dispose() {
		if (fShell != null) {
			if (!fShell.isDisposed()) {
				fShell.dispose();
			}
			onDispose();
			fShell = null;
		}
	}
	
	private void create(final Shell parent) {
		if (UIAccess.isOkToUse(fShell)) {
			if (fShell.getParent() == parent) {
				return;
			}
			dispose();
		}
		
		fToolTabs.clear();
		
		fShell = new Shell(parent, SWT.ON_TOP | SWT.TOOL); // SWT.RESIZE
		fShell.setText("Color");
		fShell.setFont(JFaceResources.getDialogFont());
		fShell.setSize(320, 300);
		
		{	final GridLayout gl = new GridLayout();
			gl.marginHeight = 0;
			gl.marginWidth = 0;
			gl.horizontalSpacing = 0;
			gl.verticalSpacing = 0;
			fShell.setLayout(gl);
		}
		final SWTListener listener = new SWTListener();
		parent.addListener(SWT.Dispose, listener);
		fShell.addListener(SWT.Deactivate, listener);
		
		fShell.setBackground(fShell.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		fShell.setBackgroundMode(SWT.INHERIT_FORCE);
		
		fTabFolder = new CTabFolder(fShell, SWT.BOTTOM | SWT.FLAT);
		fTabFolder.setSimple(true);
		fTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fTabFolder.setSelectionBackground(G_BACKGROUND);
		
		addTabs(fTabFolder);
		
		final Composite commonBar = new Composite(fShell, SWT.NONE);
		commonBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		commonBar.setLayout(LayoutUtil.createContentGrid(3));
		
//		final Composite status = new Composite(commonBar, SWT.NONE);
//		status.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addStatusControls(commonBar);
		
		fOKButton = new Button(commonBar, SWT.PUSH | SWT.FLAT);
		fOKButton.setText(IDialogConstants.OK_LABEL);
		fOKButton.setFont(fShell.getFont());
		fOKButton.addListener(SWT.Selection, listener);
		
		fCancelButton = new Button(commonBar, SWT.PUSH | SWT.FLAT);
		fCancelButton.setText(IDialogConstants.CANCEL_LABEL);
		fCancelButton.setFont(fShell.getFont());
		fCancelButton.addListener(SWT.Selection, listener);
		
		{	final Point size = fOKButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			size.x = Math.max(size.x, fCancelButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
			{	final GridData gd = new GridData(SWT.FILL, SWT.FILL);
				gd.widthHint = size.x;
				gd.heightHint = size.y - 2;
				fOKButton.setLayoutData(gd);
			}
			{	final GridData gd = new GridData(SWT.FILL, SWT.FILL);
				gd.widthHint = size.x;
				gd.heightHint = size.y - 2;
				fCancelButton.setLayoutData(gd);
			}
		}
		
		fTabFolder.addListener(SWT.Selection, listener);
		fShell.setDefaultButton(fOKButton);
		
		fShell.pack();
	}
	
	public Shell getShell() {
		return fShell;
	}
	
	protected CTabFolder getTabFolder() {
		return fTabFolder;
	}
	
	protected ToolTab getTab(final String key) {
		for (final ToolTab tab : fToolTabs) {
			if (tab.fKey == key) {
				return tab;
			}
		}
		return null;
	}
	
	protected ToolTab getTab(final CTabItem item) {
		for (final ToolTab tab : fToolTabs) {
			if (tab.fTabItem == item) {
				return tab;
			}
		}
		return null;
	}
	
	protected void addStatusControls(final Composite composite) {
	}
	
	protected void addTabs(final CTabFolder tabFolder) {
	}
	
	protected ToolTab getBestTab() {
		return null;
	}
	
	protected void selectTab(final ToolTab tab) {
		if (tab != null) {
			final CTabItem tabItem = tab.getTabItem();
			fTabFolder.setSelection(tabItem);
			tabSelected(tab);
			
			final Display display = fShell.getDisplay();
			final Control focusControl = display.getFocusControl();
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					if (UIAccess.isOkToUse(fTabFolder)
							&& fTabFolder.getSelection() == tabItem
							&& display.getFocusControl() == focusControl) {
						tabItem.getControl().setFocus();
					}
				}
			});
		}
	}
	
	protected void tabSelected(final ToolTab tab) {
		if (tab != null) {
			tab.activated();
			tab.getTabItem().getControl().setFocus();
		}
	}
	
	public void beginIgnoreActivation() {
		fIgnoreActivation++;
	}
	
	public void endIgnoreActivation() {
		fIgnoreActivation--;
	}
	
	protected void onDispose() {
		fTabFolder = null;
		fToolTabs.clear();
	}
	
	protected void onOK() {
	}
	
}
