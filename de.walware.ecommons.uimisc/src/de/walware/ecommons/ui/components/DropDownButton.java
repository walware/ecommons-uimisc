/*=============================================================================#
 # Copyright (c) 2010-2015 Stephan Wahlbrink (WalWare.de) and others.
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
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;

import de.walware.ecommons.collections.CopyOnWriteIdentityListSet;
import de.walware.ecommons.ui.internal.AccessibleArrowImage;
import de.walware.ecommons.ui.internal.UIMiscellanyPlugin;
import de.walware.ecommons.ui.util.MenuUtil;


public class DropDownButton extends Composite {
	
	
	private Button mainButton;
	private Button downButton;
	
	private Image image;
	private Image disabledImage;
	private Point imageButtonDefaultSize;
	private String arrowText;
	
	private Menu menu;
	
	private final CopyOnWriteIdentityListSet<MenuListener> menuListener= new CopyOnWriteIdentityListSet<>();
	
	
	public DropDownButton(final Composite parent) {
		this(parent, SWT.NONE);
	}
	
	/**
	 * Creates a new drop down button.
	 * 
	 * SWT#SINGLE for a single button (shows always the menu, no default action).
	 * 
	 * @param parent
	 * @param style SWT#SINGLE, other styles for buttons
	 */
	public DropDownButton(final Composite parent, final int style) {
		super(parent, SWT.NONE);
		
		create(style);
	}
	
	
	private void create(final int style) {
		final boolean single= ((style & SWT.SINGLE) != 0);
		final int buttonStyle= (style & ~SWT.SINGLE);
		
		this.downButton= new Button(this, SWT.PUSH | buttonStyle);
		
		final AccessibleArrowImage imageDescriptor= new AccessibleArrowImage(SWT.DOWN, SWT.DEFAULT,
				this.downButton.getForeground().getRGB(), this.downButton.getBackground().getRGB() );
		this.image= UIMiscellanyPlugin.getInstance().getImageDescriptorRegistry().get(imageDescriptor);
		updateSizes();
		
		this.downButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Menu menu= getDropDownMenu();
				MenuUtil.setPullDownPosition(menu, DropDownButton.this);
				menu.setVisible(true);
			}
		});
		
		if (single) {
			setTabList(new Control[] { this.downButton });
		}
		else {
			this.mainButton= new Button(this, SWT.PUSH | buttonStyle);
			
			setTabList(new Control[] { this.mainButton, this.downButton });
		}
		
		final Listener listener= new Listener() {
			@Override
			public void handleEvent(final Event event) {
				switch (event.type) {
				case SWT.Dispose:
					doDispose();
					return;
				case SWT.Resize:
					updateBounds(event);
					return;
				case SWT.Paint:
					paintButton(event);
					return;
				}
			}
		};
		addListener(SWT.Dispose, listener);
		addListener(SWT.Resize, listener);
		this.downButton.addListener(SWT.Paint, listener);
	}
	
	protected void doDispose() {
		if (this.disabledImage != null) {
			this.disabledImage.dispose();
			this.disabledImage= null;
		}
		if (this.menu != null) {
			this.menu.dispose();
			this.menu= null;
		}
	}
	
	private void updateSizes() {
		{	final Button button= new Button(this, SWT.PUSH);
			button.setImage(this.image);
			button.setFont(getFont());
			this.imageButtonDefaultSize= button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			button.dispose();
		}
		
		if (this.mainButton == null) {
			final int imageWidth= this.image.getBounds().width;
			final int requiredWidth= imageWidth 
					+ (this.imageButtonDefaultSize.x - imageWidth + 1) / 2;
			final GC gc= new GC(this);
			String text= " ";
			for (int i= 1; i < 10; i++) {
				if (gc.stringExtent(text).x >= requiredWidth) {
					break;
				}
				text+= " "; //$NON-NLS-1$
			}
			this.arrowText= text;
		}
	}
	
	private void updateBounds(final Event event) {
		final Rectangle clientArea= getClientArea();
		if (this.mainButton != null) {
			final int downButtonWidth= this.imageButtonDefaultSize.x;
			this.mainButton.setBounds(
					clientArea.x,
					clientArea.y,
					clientArea.width - downButtonWidth + 1,
					clientArea.height);
			this.downButton.setBounds(
					clientArea.x + clientArea.width - downButtonWidth,
					clientArea.y,
					downButtonWidth,
					clientArea.height );
		}
		else {
			this.downButton.setBounds(clientArea);
		}
	}
	
	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		final Point downSize= this.downButton.computeSize(SWT.DEFAULT, hHint);
		int width= downSize.x;
		int height= downSize.y;
		if (this.mainButton != null) {
			final Point mainSize= (wHint == SWT.DEFAULT) ?
					this.mainButton.computeSize(SWT.DEFAULT, hHint) :
					this.mainButton.computeSize(Math.max(0, wHint - downSize.x), hHint);
			width+= mainSize.x - 1;
			height= Math.max(mainSize.y, height);
		}
		final Rectangle trim= super.computeTrim(0, 0, width, height);
		return new Point(trim.width, trim.height);
	}
	
	private Image getDisabledImage() {
		if (this.disabledImage == null) {
			this.disabledImage= new Image(this.image.getDevice(), this.image, SWT.IMAGE_DISABLE);
		}
		return this.disabledImage;
	}
	
	private void paintButton(final Event event) {
		final Point buttonSize= this.downButton.getSize();
		final Image image= (this.downButton.isEnabled()) ? this.image : getDisabledImage();
		event.gc.drawImage(image,
				(this.mainButton != null) ?
						(buttonSize.x - image.getBounds().width) / 2 :
						buttonSize.x - this.imageButtonDefaultSize.x
								+ (this.imageButtonDefaultSize.x - image.getBounds().width) / 2,
				(buttonSize.y - image.getBounds().height) / 2 );
	}
	
	
	@Override
	public void setFont(final Font font) {
		super.setFont(font);
		
		updateSizes();
	}
	
	@Override
	public void setEnabled(final boolean enabled) {
		if (this.mainButton != null) {
			this.mainButton.setEnabled(enabled);
		}
		this.downButton.setEnabled(enabled);
	}
	
	public void setText(final String string) {
		if (this.mainButton != null) {
			this.mainButton.setText(string);
		}
		else {
			this.downButton.setText(string + this.arrowText);
		}
	}
	
	@Override
	public void setToolTipText(final String string) {
		if (this.mainButton != null) {
			this.mainButton.setToolTipText(string);
		}
		else {
			this.downButton.setToolTipText(string);
		}
	}
	
	public void setOptionToolTipText(final String string) {
		this.downButton.setToolTipText(string);
	}
	
	public void addSelectionListener(final SelectionListener listener) {
		this.mainButton.addSelectionListener(listener);
	}
	
	public void removeSelectionListener(final SelectionListener listener) {
		this.mainButton.removeSelectionListener(listener);
	}
	
	public void addMenuListener(final MenuListener listener) {
		this.menuListener.add(listener);
		if (this.menu != null) {
			this.menuListener.add(listener);
		}
	}
	
	public void removeMenuListener(final MenuListener listener) {
		this.menuListener.remove(listener);
		if (this.menu != null) {
			this.menuListener.remove(listener);
		}
	}
	
	
	public Menu getDropDownMenu() {
		Menu menu= this.menu;
		if (menu == null) {
			menu= createDropDownMenu();
			final Listener listener= new Listener() {
				@Override
				public void handleEvent(final Event event) {
					switch (event.type) {
						
					case SWT.Dispose:
						if (DropDownButton.this.menu == event.widget) {
							DropDownButton.this.menu= null;
						}
						return;
					
					default:
						return;
					}
				}
			};
			menu.addListener(SWT.Dispose, listener);
			for (final MenuListener menuListener : this.menuListener) {
				menu.addMenuListener(menuListener);
			}
			
			this.menu= menu;
		}
		return menu;
	}
	
	protected Menu createDropDownMenu() {
		return new Menu(this);
	}
	
}
