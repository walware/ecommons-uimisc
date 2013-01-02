/*******************************************************************************
 * Copyright (c) 2000-2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.components;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;

import de.walware.ecommons.ui.internal.AccessibleArrowImage;
import de.walware.ecommons.ui.internal.UIMiscellanyPlugin;


/**
 * The CCombo class represents a selectable user interface object
 * that combines a text field and a list and issues notification
 * when an item is selected from the list.
 * <p>
 * CCombo was written to work around certain limitations in the native
 * combo box. Specifically, on win32, the height of a CCombo can be set;
 * attempts to set the height of a Combo are ignored. CCombo can be used
 * anywhere that having the increased flexibility is more important than
 * getting native L&F, but the decision should not be taken lightly. 
 * There is no is no strict requirement that CCombo look or behave
 * the same as the native combo box.
 * </p>
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to add children to it, or set a layout on it.
 * </p>
 * <dl>
 * <dt><b>Styles:</b>
 * <dd>BORDER, READ_ONLY, FLAT</dd>
 * <dt><b>Events:</b>
 * <dd>DefaultSelection, Modify, Selection, Verify</dd>
 * </dl>
 *
 * @see <a href="http://www.eclipse.org/swt/snippets/#ccombo">CCombo snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example: CustomControlExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 */
public class WaCombo extends Composite {
	
	
	private static final int [] COMBO_EVENTS = {
			SWT.Dispose, SWT.FocusIn, SWT.Move, SWT.Resize
	};
	
	private static final int[] TEXT_EVENTS = {
			SWT.KeyDown, SWT.KeyUp, SWT.MenuDetect,
			SWT.MouseEnter, SWT.MouseExit, SWT.MouseMove,
			SWT.MouseDown, SWT.MouseUp, SWT.MouseDoubleClick, SWT.MouseHover, SWT.MouseWheel,
			SWT.DragDetect, SWT.Traverse,
			SWT.FocusIn, SWT.Verify
	};
	
	private static final int [] ARROW_EVENTS = {
			SWT.DragDetect,
			SWT.MouseEnter, SWT.MouseExit, SWT.MouseMove,
			SWT.MouseDown,SWT.MouseUp,  SWT.MouseWheel, SWT.MouseHover,
			SWT.Selection,
			SWT.FocusIn,
	};
	
	
	private Label fImage;
	private Label fText;
	private Button fArrow;
	
	private Shell fPopupParent;
	private Shell fPopup;
	
	private boolean fHasFocus;
	private Listener fListener, fFilter;
	
	private Color fForeground, fBackground;
	private Font fFont;
	
	private Table fList;
	private int fItemCount = 0;
	private int fVisibleItemCount = 10;
	
	private int fArrowImageSize;
	
	
	
	private static int checkStyle(final int style) {
		final int mask = SWT.BORDER | SWT.FLAT | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT;
		return SWT.NO_FOCUS | SWT.READ_ONLY | (style & mask);
	}
	
	/**
	 * Constructs a new instance of this class given its parent
	 * and a style value describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in
	 * class <code>SWT</code> which is applicable to instances of this
	 * class, or must be built by <em>bitwise OR</em>'ing together 
	 * (that is, using the <code>int</code> "|" operator) two or more
	 * of those <code>SWT</code> style constants. The class description
	 * lists the style constants that are applicable to the class.
	 * Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a widget which will be the parent of the new instance (cannot be null)
	 * @param style the style of widget to construct
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 * </ul>
	 *
	 * @see SWT#BORDER
	 * @see SWT#READ_ONLY
	 * @see SWT#FLAT
	 * @see Widget#getStyle()
	 */
	public WaCombo(final Composite parent, int style) {
		super(parent, style = checkStyle(style));
		
		setBackgroundMode(SWT.INHERIT_FORCE);
		
		fPopupParent = super.getShell();
		
		fImage = new Label(this, SWT.CENTER);
		
		final int textStyle = SWT.SINGLE;
//		if ((style & SWT.READ_ONLY) != 0) {
//			textStyle |= SWT.READ_ONLY;
//		}
//		if ((style & SWT.FLAT) != 0) {
//			textStyle |= SWT.FLAT;
//		}
		fText = new Label(this, textStyle);
		
		{	final boolean system = !Platform.getWS().equals(Platform.WS_WIN32);
			int arrowStyle = (system) ? (SWT.ARROW | SWT.DOWN) : SWT.PUSH;
			if ((style & SWT.FLAT) != 0) {
				arrowStyle |= SWT.FLAT;
			}
			fArrow = new Button(this, arrowStyle);
			if (!system) {
//				fArrow.setText("▾"); //$NON-NLS-1$
//				fArrow.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(
//						IWorkbenchGraphicConstants.IMG_LCL_BUTTON_MENU ) );
				updateImage(SWT.DEFAULT);
			}
		}
		fListener = new Listener() {
			@Override
			public void handleEvent(final Event event) {
				if (isDisposed()) {
					return;
				}
				if (fPopup == event.widget) {
					popupEvent(event);
					return;
				}
				if (fImage == event.widget || fText == event.widget) {
					textEvent(event);
					return;
				}
				if (fList == event.widget) {
					listEvent(event);
					return;
				}
				if (fArrow == event.widget) {
					arrowEvent(event);
					return;
				}
				if (WaCombo.this == event.widget) {
					comboEvent(event);
					return;
				}
				if (getShell() == event.widget) {
					getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							if (isDisposed()) {
								return;
							}
							handleFocus(SWT.FocusOut);
						}
					});
				}
			}
		};
		fFilter = new Listener() {
			@Override
			public void handleEvent(final Event event) {
				if (isDisposed()) {
					return;
				}
				if (event.type == SWT.Selection) {
					if (event.widget instanceof ScrollBar) {
						handleScroll(event);
					}
					return;
				}
				final Shell shell = ((Control)event.widget).getShell();
				if (shell == WaCombo.this.getShell()) {
					handleFocus(SWT.FocusOut);
				}
			}
		};
		
		for (int i = 0; i < COMBO_EVENTS.length; i++) {
			this.addListener(COMBO_EVENTS[i], fListener);
		}
		
		for (int i = 0; i < TEXT_EVENTS.length; i++) {
			fImage.addListener(TEXT_EVENTS[i], fListener);
		}
		for (int i = 0; i < TEXT_EVENTS.length; i++) {
			fText.addListener(TEXT_EVENTS[i], fListener);
		}
		
		for (int i = 0; i < ARROW_EVENTS.length; i++) {
			fArrow.addListener(ARROW_EVENTS[i], fListener);
		}
		
		createPopup(-1);
		if ((style & SWT.SIMPLE) == 0) {
			final int itemHeight = fList.getItemHeight();
			if (itemHeight != 0) {
				final int maxHeight = getMonitor().getClientArea().height / 3;
				fVisibleItemCount = Math.max(fVisibleItemCount, maxHeight / itemHeight);
			}
		}
		
		initAccessible();
	}
	
	private void checkShell() {
		if (getShell() != fPopup.getParent()) {
			if (fPopup.setParent(getShell())) {
				return;
			}
			final int selectionIndex = fList.getSelectionIndex();
			fList.removeListener(SWT.Dispose, fListener);
			fPopup.dispose();
			fPopup = null;
			fList = null;
			createPopup(selectionIndex);
		}
	}
	
	private void updateImage(int size) {
		if (size == SWT.DEFAULT) {
			size = AccessibleArrowImage.DEFAULT_SIZE;
		}
		fArrowImageSize = size;
		final Image image = UIMiscellanyPlugin.getDefault().getImageDescriptorRegistry().get(
				new AccessibleArrowImage(SWT.DOWN, size,
						fArrow.getForeground().getRGB(), fArrow.getBackground().getRGB() ));
		fArrow.setImage(image);
	}
	
	private void createPopup(final int selectionIndex) {
		// create shell and list
		fPopup = new Shell(getShell(), SWT.NO_TRIM | SWT.ON_TOP);
		final int style = getStyle();
		int listStyle = SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION;
		if ((style & SWT.FLAT) != 0) {
			listStyle |= SWT.FLAT;
		}
		if ((style & SWT.RIGHT_TO_LEFT) != 0) {
			listStyle |= SWT.RIGHT_TO_LEFT;
		}
		if ((style & SWT.LEFT_TO_RIGHT) != 0) {
			listStyle |= SWT.LEFT_TO_RIGHT;
		}
		fList = new Table(fPopup, listStyle);
		if (fFont != null) {
			fList.setFont(fFont);
		}
		if (fForeground != null) {
			fList.setForeground(fForeground);
		}
		if (fBackground != null) {
			fList.setBackground(fBackground);
		}
		
		final int [] popupEvents = {SWT.Close, SWT.Paint};
		for (int i = 0; i < popupEvents.length; i++) {
			fPopup.addListener(popupEvents [i], fListener);
		}
		final int [] listEvents = {SWT.MouseUp, SWT.Selection, SWT.Traverse, SWT.KeyDown, SWT.KeyUp, SWT.FocusIn, SWT.FocusOut, SWT.Dispose};
		for (int i = 0; i < listEvents.length; i++) {
			fList.addListener(listEvents [i], fListener);
		}
		
		fList.setItemCount(fItemCount);
		if (selectionIndex != -1) {
			fList.setSelection(selectionIndex);
		}
	}
	
	void dropDown(final boolean drop) {
		if (drop == isDropped()) {
			return;
		}
		final Display display = getDisplay();
		if (!drop) {
			display.removeFilter(SWT.Selection, fFilter);
			fPopup.setVisible(false);
			if (!isDisposed() && isFocusControl()) {
				fArrow.setFocus();
			}
			return;
		}
		if (!isVisible()) {
			return;
		}
		checkShell();
		
		final Point comboSize = getSize();
		int itemCount = fList.getItemCount();
		itemCount = (itemCount == 0) ? fVisibleItemCount : Math.min(fVisibleItemCount, itemCount);
		final int itemHeight = fList.getItemHeight() * itemCount;
		
		final Point listSize = fList.computeSize(16 + 8 + getMaxListTextWidth(), itemHeight, true);
		final Rectangle displayRect = getMonitor().getClientArea();
		fList.setBounds(1, 1,
				Math.max(comboSize.x - 2, Math.min(listSize.x, displayRect.width - 2)),
				listSize.y );
		
		final int index = fList.getSelectionIndex();
		if (index != -1) {
			fList.setTopIndex(index);
		}
		final Rectangle listRect = fList.getBounds();
		final Rectangle parentRect = display.map(getParent(), null, getBounds());
		final int width = listRect.width + 2;
		int height = listRect.height + 2;
		int x = parentRect.x;
		if (x + width > displayRect.x + displayRect.width) {
			x = displayRect.x + displayRect.width - width;
		}
		int y = parentRect.y + comboSize.y;
		if (y + height > displayRect.y + displayRect.height) {
			final int popUpwardsHeight = (parentRect.y - height < displayRect.y) ? parentRect.y - displayRect.y : height;
			final int popDownwardsHeight = displayRect.y + displayRect.height - y;
			if (popUpwardsHeight > popDownwardsHeight) {
				height = popUpwardsHeight;
				y = parentRect.y - popUpwardsHeight;
			} else {
				height = popDownwardsHeight;
			}
			fList.setSize(listRect.width, height - 2);
		}
		fPopup.setBounds(x, y, width, height);
		
		if (fList.getColumnCount() == 0) {
			new TableColumn(fList, SWT.LEFT);
		}
		fList.getColumn(0).setWidth(fList.getClientArea().width);
		
		fPopup.setVisible(true);
		if (isFocusControl()) {
			fList.setFocus();
		}
		
		/*
		 * Add a filter to listen to scrolling of the parent composite, when the
		 * drop-down is visible. Remove the filter when drop-down is not
		 * visible.
		 */
		display.removeFilter(SWT.Selection, fFilter);
		display.addFilter(SWT.Selection, fFilter);
	}
	
	/**
	 * Sets the layout which is associated with the receiver to be
	 * the argument which may be null.
	 * <p>
	 * Note: No Layout can be set on this Control because it already
	 * manages the size and position of its children.
	 * </p>
	 *
	 * @param layout the receiver's new layout or null
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	@Override
	public void setLayout(final Layout layout) {
		checkWidget();
		return;
	}
	
	@Override
	public Control [] getChildren() {
		checkWidget();
		return new Control[0];
	}
	
	@Override
	public boolean isFocusControl() {
		checkWidget();
		if (fText.isFocusControl () || fArrow.isFocusControl () || fList.isFocusControl () || fPopup.isFocusControl ()) {
			return true;
		} 
		return super.isFocusControl ();
	}
	
	
	/**
	 * Returns the number of items contained in the receiver's list.
	 *
	 * @return the number of items
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public int getItemCount() {
		checkWidget();
		return fItemCount;
	}
	
	public void setItemCount(final int count) {
		fItemCount = count;
		if (fList != null) {
			fList.setItemCount(count);
		}
	}
	
	/**
	 * Returns an array of <code>String</code>s which are the items
	 * in the receiver's list. 
	 * <p>
	 * Note: This is not the actual structure used by the receiver
	 * to maintain its list of items, so modifying the array will
	 * not affect the receiver. 
	 * </p>
	 *
	 * @return the items in the receiver's list
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public Item[] getItems() {
		checkWidget();
		return fList.getItems();
	}
	
	/**
	 * Returns the item at the given, zero-relative index in the
	 * receiver's list. Throws an exception if the index is out
	 * of range.
	 *
	 * @param index the index of the item to return
	 * @return the item at the given index
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public Item getItem(final int index) {
		checkWidget();
		return fList.getItem(index);
	}
	
	
	/**
	 * Returns the height of the area which would be used to
	 * display <em>one</em> of the items in the receiver's list.
	 *
	 * @return the height of one item
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public int getItemHeight() {
		checkWidget();
		return fList.getItemHeight();
	}
	
	@Override
	public Shell getShell() {
		checkWidget();
		final Shell shell = super.getShell();
		if (shell != fPopupParent) {
			if (fPopupParent != null && !fPopupParent.isDisposed()) {
				fPopupParent.removeListener(SWT.Deactivate, fListener);
			}
			fPopupParent = shell;
		}
		return fPopupParent;
	}
	
	@Override
	public int getStyle() {
		int style = super.getStyle();
		style |= SWT.READ_ONLY;
		return style;
	}
	
//	/**
//	 * Returns the height of the receivers's text field.
//	 *
//	 * @return the text height
//	 *
//	 * @exception SWTException <ul>
//	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
//	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
//	 * </ul>
//	 */
//	public int getTextHeight() {
//		checkWidget();
//		return fText.getLineHeight();
//	}
	
	void handleFocus(final int type) {
		switch (type) {
			case SWT.FocusIn: {
				if (fHasFocus) {
					return;
				}
				fHasFocus = true;
				final Shell shell = getShell();
				shell.removeListener(SWT.Deactivate, fListener);
				shell.addListener(SWT.Deactivate, fListener);
				final Display display = getDisplay();
				display.removeFilter(SWT.FocusIn, fFilter);
				display.addFilter(SWT.FocusIn, fFilter);
				final Event e = new Event();
				notifyListeners(SWT.FocusIn, e);
				break;
			}
			case SWT.FocusOut: {
				if (!fHasFocus) {
					return;
				}
				final Control focusControl = getDisplay().getFocusControl();
				if (focusControl == fArrow || focusControl == fList || focusControl == fText) {
					return;
				}
				fHasFocus = false;
				final Shell shell = getShell();
				shell.removeListener(SWT.Deactivate, fListener);
				final Display display = getDisplay();
				display.removeFilter(SWT.FocusIn, fFilter);
				final Event e = new Event();
				notifyListeners(SWT.FocusOut, e);
				break;
			}
		}
	}
	void handleScroll(final Event event) {
		final ScrollBar scrollBar = (ScrollBar)event.widget;
		final Control scrollableParent = scrollBar.getParent();
		if (scrollableParent.equals(fList)) {
			return;
		}
		if (isParentScrolling(scrollableParent)) {
			dropDown(false);
		}
	}
	void initAccessible() {
		final AccessibleAdapter accessibleAdapter = new AccessibleAdapter() {
			@Override
			public void getName(final AccessibleEvent e) {
				String name = null;
				final String text = getAssociatedLabel();
				if (text != null) {
					name = stripMnemonic(text);
				}
				e.result = name;
			}
			@Override
			public void getKeyboardShortcut(final AccessibleEvent e) {
				String shortcut = null;
				final String text = getAssociatedLabel();
				if (text != null) {
					final char mnemonic = findMnemonicChar(text);
					if (mnemonic != '\0') {
						shortcut = "Alt+"+mnemonic; //$NON-NLS-1$
					}
				}
				e.result = shortcut;
			}
			@Override
			public void getHelp(final AccessibleEvent e) {
				e.result = getToolTipText();
			}
		};
		getAccessible().addAccessibleListener(accessibleAdapter);
		fText.getAccessible().addAccessibleListener(accessibleAdapter);
		fList.getAccessible().addAccessibleListener(accessibleAdapter);
		
		fArrow.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			@Override
			public void getName(final AccessibleEvent e) {
				e.result = isDropped() ? SWT.getMessage("SWT_Close") : SWT.getMessage("SWT_Open"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			@Override
			public void getKeyboardShortcut(final AccessibleEvent e) {
				e.result = "Alt+Down Arrow"; //$NON-NLS-1$
			}
			@Override
			public void getHelp(final AccessibleEvent e) {
				e.result = getToolTipText();
			}
		});
		
		getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
			@Override
			public void getChildAtPoint(final AccessibleControlEvent e) {
				final Point testPoint = toControl(e.x, e.y);
				if (getBounds().contains(testPoint)) {
					e.childID = ACC.CHILDID_SELF;
				}
			}
			
			@Override
			public void getLocation(final AccessibleControlEvent e) {
				final Rectangle location = getBounds();
				final Point pt = getParent().toDisplay(location.x, location.y);
				e.x = pt.x;
				e.y = pt.y;
				e.width = location.width;
				e.height = location.height;
			}
			
			@Override
			public void getChildCount(final AccessibleControlEvent e) {
				e.detail = 0;
			}
			
			@Override
			public void getRole(final AccessibleControlEvent e) {
				e.detail = ACC.ROLE_COMBOBOX;
			}
			
			@Override
			public void getState(final AccessibleControlEvent e) {
				e.detail = ACC.STATE_NORMAL;
			}
			
			@Override
			public void getValue(final AccessibleControlEvent e) {
				e.result = fText.getText();
			}
		});
		
		fText.getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
			@Override
			public void getRole(final AccessibleControlEvent e) {
				e.detail = ACC.ROLE_LABEL;
			}
		});
		
		fArrow.getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
			@Override
			public void getDefaultAction(final AccessibleControlEvent e) {
				e.result = isDropped() ? SWT.getMessage("SWT_Close") : SWT.getMessage("SWT_Open"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
	}
	
	boolean isDropped() {
		return fPopup.getVisible();
	}
	
	boolean isParentScrolling(final Control scrollableParent) {
		Control parent = this.getParent();
		while (parent != null) {
			if (parent.equals(scrollableParent)) {
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}
	
	private int getMaxListTextWidth() {
		final TableItem[] items = fList.getItems();
		final GC gc = new GC(fText);
		int textWidth = gc.stringExtent(fText.getText()).x;
		for (int i = 0; i < items.length; i++) {
			textWidth = Math.max(gc.stringExtent(items[i].getText()).x, textWidth);
		}
		gc.dispose();
		return textWidth;
	}
	
	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		checkWidget();
		int width = 0, height = 0;
		final Point imageSize = fImage.computeSize(16, 16, changed);
		imageSize.x += 5;
		imageSize.y += 2;
		final Point textSize = fText.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
		textSize.y += 2;
		final Point arrowSize = fArrow.computeSize(16 + 4, 16, changed);
		final int textWidth = getMaxListTextWidth();
		final int borderWidth = getBorderWidth();
		
		if (imageSize.y > height) {
			height = imageSize.y;
		}
		if (textSize.y > height) {
			height = textSize.y;
		}
//		if (arrowSize.y > height) {
//			height = arrowSize.y;
//		}
		final int spacer = 2;
		width = imageSize.x + textWidth + 2 * spacer + arrowSize.x + 2 * borderWidth;
		if (wHint != SWT.DEFAULT) {
			width = wHint;
		}
		if (hHint != SWT.DEFAULT) {
			height = hHint;
		}
		return new Point(width + 2 * borderWidth, height + 2 * borderWidth);
	}
	
	private void internalLayout(final boolean changed) {
		if (isDropped ()) {
			dropDown (false);
		}
		final Rectangle rect = getClientArea();
		final int width = rect.width;
		final int height = rect.height;
		final Point imageSize = fImage.computeSize(16, 16, changed);
		imageSize.x += 5;
		if (fArrowImageSize > 0) {
			int bestSize = AccessibleArrowImage.DEFAULT_SIZE;
			if (height <= 20) {
				bestSize--;
			}
			else if (height > 30) {
				bestSize++;
			}
			if (fArrowImageSize != bestSize) {
				updateImage(bestSize);
			}
		}
		final Point arrowSize = fArrow.computeSize(19, height, changed);
		fImage.setBounds(0, 0, imageSize.x, height);
		fText.setBounds(imageSize.x, 1, width - imageSize.x - arrowSize.x, height);
		fArrow.setBounds(width - arrowSize.x + 1, -1, arrowSize.x, height + 2);
	}
	
	
	@Override
	public void redraw() {
		super.redraw();
		fText.redraw();
		fArrow.redraw();
		if (fPopup.isVisible()) {
			fList.redraw();
		}
	}
	
	@Override
	public void redraw(final int x, final int y, final int width, final int height, final boolean all) {
		super.redraw(x, y, width, height, true);
	}
	
	
	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the receiver's text is modified, by sending
	 * it one of the messages defined in the <code>ModifyListener</code>
	 * interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see ModifyListener
	 * @see #removeModifyListener
	 */
	public void addModifyListener(final ModifyListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		final TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Modify, typedListener);
	}
	
	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the user changes the receiver's selection, by sending
	 * it one of the messages defined in the <code>SelectionListener</code>
	 * interface.
	 * <p>
	 * <code>widgetSelected</code> is called when the combo's list selection changes.
	 * <code>widgetDefaultSelected</code> is typically called when ENTER is pressed the combo's text area.
	 * </p>
	 *
	 * @param listener the listener which should be notified when the user changes the receiver's selection
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(final SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		final TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection,typedListener);
		addListener(SWT.DefaultSelection,typedListener);
	}
	
	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the receiver's text is verified, by sending
	 * it one of the messages defined in the <code>VerifyListener</code>
	 * interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see VerifyListener
	 * @see #removeVerifyListener
	 * 
	 * @since 3.3
	 */
	public void addVerifyListener(final VerifyListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		final TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Verify,typedListener);
	}
	
	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the receiver's text is modified.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see ModifyListener
	 * @see #addModifyListener
	 */
	public void removeModifyListener(final ModifyListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		removeListener(SWT.Modify, listener);	
	}
	
	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the user changes the receiver's selection.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(final SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		removeListener(SWT.Selection, listener);
		removeListener(SWT.DefaultSelection,listener);	
	}
	
	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the control is verified.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see VerifyListener
	 * @see #addVerifyListener
	 * 
	 * @since 3.3
	 */
	public void removeVerifyListener(final VerifyListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error (SWT.ERROR_NULL_ARGUMENT);
		}
		removeListener(SWT.Verify, listener);
	}
	
	
	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		if (fImage != null) {
			fImage.setEnabled(enabled);
		}
		if (fText != null) {
			fText.setEnabled(enabled);
		}
		if (fArrow != null) {
			fArrow.setEnabled(enabled);
		}
		if (fPopup != null) {
			fPopup.setVisible(false);
		}
	}
	
	@Override
	public boolean setFocus() {
		checkWidget();
		if (!isEnabled() || !getVisible()) {
			return false;
		}
		if (isFocusControl()) {
			return true;
		}
		return fArrow.setFocus();
	}
	
	@Override
	public void setFont(final Font font) {
		super.setFont(font);
		fList.setFont(font);
		internalLayout(true);
	}
	
	@Override
	public void setForeground(final Color color) {
		super.setForeground(color);
		fForeground = color;
		if (fImage != null) {
			fImage.setForeground(color);
		}
		if (fText != null) {
			fText.setForeground(color);
		}
		if (fList != null) {
			fList.setForeground(color);
		}
//		if (fArrow != null) {
//			fArrow.setForeground(color);
//			updateImage(fImageSize);
//		}
	}
	
	@Override
	public void setBackground(final Color color) {
		super.setBackground(color);
		fBackground = color;
//		if (fImage != null) {
//			fImage.setBackground(color);
//		}
//		if (fText != null) {
//			fText.setBackground(color);
//		}
//		if (fList != null) {
//			fList.setBackground(color);
//		}
//		if (fArrow != null) {
//			fArrow.setBackground(color);
//			updateImage(fImageSize);
//		}
	}
	
	@Override
	public void setMenu(final Menu menu) {
		fImage.setMenu(menu);
		fText.setMenu(menu);
	}
	
	@Override
	public Menu getMenu() {
		return fText.getMenu();
	}
	
	@Override
	public void setToolTipText(final String string) {
		checkWidget();
		super.setToolTipText(string);
		fImage.setToolTipText(string);
		fArrow.setToolTipText(string);
		fText.setToolTipText(string);
	}
	
	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		/* 
		 * At this point the widget may have been disposed in a FocusOut event.
		 * If so then do not continue.
		 */
		if (isDisposed()) {
			return;
		}
		// TEMPORARY CODE
		if (fPopup == null || fPopup.isDisposed()) {
			return;
		}
		if (!visible) {
			fPopup.setVisible(false);
		}
	}
	
	/**
	 * Gets the number of items that are visible in the drop
	 * down portion of the receiver's list.
	 *
	 * @return the number of items that are visible
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 3.0
	 */
	public int getVisibleItemCount() {
		checkWidget();
		return fVisibleItemCount;
	}
	
	/**
	 * Sets the number of items that are visible in the drop
	 * down portion of the receiver's list.
	 *
	 * @param count the new number of items to be visible
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 3.0
	 */
	public void setVisibleItemCount(final int count) {
		checkWidget();
		if (count < 0) {
			return;
		}
		fVisibleItemCount = count;
	}
	
	public Table getList() {
		return fList;
	}
	
	/**
	 * Returns <code>true</code> if the receiver's list is visible,
	 * and <code>false</code> otherwise.
	 * <p>
	 * If one of the receiver's ancestors is not visible or some
	 * other condition makes the receiver not visible, this method
	 * may still indicate that it is considered visible even though
	 * it may not actually be showing.
	 * </p>
	 *
	 * @return the receiver's list's visibility state
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public boolean getListVisible() {
		checkWidget();
		return isDropped();
	}
	
	/**
	 * Marks the receiver's list as visible if the argument is <code>true</code>,
	 * and marks it invisible otherwise.
	 * <p>
	 * If one of the receiver's ancestors is not visible or some
	 * other condition makes the receiver not visible, marking
	 * it visible may not actually cause it to be displayed.
	 * </p>
	 *
	 * @param visible the new visibility state
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setListVisible(final boolean visible) {
		checkWidget();
		dropDown(visible);
	}
	
	
	/**
	 * Returns the Label immediately preceding the receiver in the z-order, 
	 * or null if none. 
	 */
	private String getAssociatedLabel() {
		final Control[] siblings = getParent().getChildren();
		for (int i = 0; i < siblings.length; i++) {
			if (siblings [i] == this) {
				if (i > 0) {
					final Control sibling = siblings [i-1];
					if (sibling instanceof Label) {
						return ((Label) sibling).getText();
					}
					if (sibling instanceof CLabel) {
						return ((CLabel) sibling).getText();
					}
				}
				break;
			}
		}
		return null;
	}
	
	/**
	 * Return the lowercase of the first non-'&' character following
	 * an '&' character in the given string. If there are no '&'
	 * characters in the given string, return '\0'.
	 */
	private char findMnemonicChar(final String string) {
		if (string == null) {
			return '\0';
		}
		int index = 0;
		final int length = string.length();
		do {
			while (index < length && string.charAt(index) != '&') {
				index++;
			}
			if (++index >= length) {
				return '\0';
			}
			if (string.charAt(index) != '&') {
				return Character.toLowerCase(string.charAt(index));
			}
			index++;
		} while (index < length);
		return '\0';
	}
	
	private String stripMnemonic(final String string) {
		int index = 0;
		final int length = string.length();
		do {
			while ((index < length) && (string.charAt(index) != '&')) {
				index++;
			}
			if (++index >= length) {
				return string;
			}
			if (string.charAt(index) != '&') {
				return string.substring(0, index-1) + string.substring(index, length);
			}
			index++;
		} while (index < length);
		return string;
	}
	
	
	private void comboEvent(final Event event) {
		switch (event.type) {
			case SWT.Dispose:
				removeListener(SWT.Dispose, fListener);
				notifyListeners(SWT.Dispose, event);
				event.type = SWT.None;
				
				if (fPopup != null && !fPopup.isDisposed()) {
					fList.removeListener(SWT.Dispose, fListener);
					fPopup.dispose();
				}
				final Shell shell = getShell();
				shell.removeListener(SWT.Deactivate, fListener);
				final Display display = getDisplay();
				display.removeFilter(SWT.FocusIn, fFilter);
				fPopup = null;
				fText = null;
				fList = null;
				fArrow = null;
				fPopupParent = null;
				break;
			case SWT.FocusIn:
				final Control focusControl = getDisplay().getFocusControl();
				if (focusControl == fArrow || focusControl == fList) {
					return;
				}
				if (isDropped()) {
					fList.setFocus();
				} else {
					fArrow.setFocus();
				}
				break;
			case SWT.Move:
				dropDown(false);
				break;
			case SWT.Resize:
				internalLayout(false);
				break;
		}
	}
	
	private void arrowEvent(final Event event) {
		switch (event.type) {
			case SWT.FocusIn: {
				handleFocus(SWT.FocusIn);
				break;
			}
			case SWT.DragDetect:
			case SWT.MouseDown:
			case SWT.MouseUp:
			case SWT.MouseMove:
			case SWT.MouseEnter:
			case SWT.MouseExit:
			case SWT.MouseHover: {
				final Point pt = getDisplay().map(fArrow, this, event.x, event.y);
				event.x = pt.x; event.y = pt.y;
				notifyListeners(event.type, event);
				event.type = SWT.None;
				break;
			}
			case SWT.MouseWheel: {
				final Point pt = getDisplay().map(fArrow, this, event.x, event.y);
				event.x = pt.x; event.y = pt.y;
				notifyListeners(SWT.MouseWheel, event);
				event.type = SWT.None;
				if (isDisposed()) {
					break;
				}
				if (!event.doit) {
					break;
				}
				if (event.count != 0) {
					event.doit = false;
					final int oldIndex = getSelectionIndex();
					if (event.count > 0) {
						select(Math.max(oldIndex - 1, 0));
					} else {
						select(Math.min(oldIndex + 1, getItemCount() - 1));
					}
					if (oldIndex != getSelectionIndex()) {
						final Event e = new Event();
						e.time = event.time;
						e.stateMask = event.stateMask;
						notifyListeners(SWT.Selection, e);
					}
					if (isDisposed()) {
						break;
					}
				}
				break;
			}
			case SWT.Selection: {
				fArrow.setFocus();
				dropDown(!isDropped());
				break;
			}
		}
	}
	
	private void textEvent(final Event event) {
		switch (event.type) {
			case SWT.FocusIn: {
				handleFocus(SWT.FocusIn);
				break;
			}
			case SWT.DefaultSelection: {
				dropDown(false);
				final Event e = new Event();
				e.time = event.time;
				e.stateMask = event.stateMask;
				notifyListeners(SWT.DefaultSelection, e);
				break;
			}
			case SWT.DragDetect:
			case SWT.MouseDoubleClick:
			case SWT.MouseMove:
			case SWT.MouseEnter:
			case SWT.MouseExit:
			case SWT.MouseHover: {
				final Point pt = getDisplay().map((Control) event.widget, this, event.x, event.y);
				event.x = pt.x; event.y = pt.y;
				notifyListeners(event.type, event);
				event.type = SWT.None;
				break;
			}
			case SWT.KeyDown: {
				final Event keyEvent = new Event();
				keyEvent.time = event.time;
				keyEvent.character = event.character;
				keyEvent.keyCode = event.keyCode;
				keyEvent.keyLocation = event.keyLocation;
				keyEvent.stateMask = event.stateMask;
				notifyListeners(SWT.KeyDown, keyEvent);
				if (isDisposed ()) {
					break;
				}
				event.doit = keyEvent.doit;
				if (!event.doit) {
					break;
				}
				if (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN) {
					event.doit = false;
					if ((event.stateMask & SWT.ALT) != 0) {
						final boolean dropped = isDropped ();
						if (!dropped) {
							setFocus();
						}
						dropDown (!dropped);
						break;
					}
					
					final int oldIndex = getSelectionIndex ();
					if (event.keyCode == SWT.ARROW_UP) {
						select(Math.max (oldIndex - 1, 0));
					}
					else {
						select(Math.min (oldIndex + 1, getItemCount () - 1));
					}
					if (oldIndex != getSelectionIndex ()) {
						final Event e = new Event();
						e.time = event.time;
						e.stateMask = event.stateMask;
						notifyListeners(SWT.Selection, e);
					}
					if (isDisposed ()) {
						break;
					}
				}
				
				// Further work : Need to add support for incremental search in 
				// pop up list as characters typed in text widget
				break;
			}
			case SWT.KeyUp: {
				final Event e = new Event ();
				e.time = event.time;
				e.character = event.character;
				e.keyCode = event.keyCode;
				e.keyLocation = event.keyLocation;
				e.stateMask = event.stateMask;
				notifyListeners(SWT.KeyUp, e);
				event.doit = e.doit;
				break;
			}
			case SWT.MenuDetect: {
				final Event e = new Event ();
				e.time = event.time;
				e.detail = event.detail;
				e.x = event.x;
				e.y = event.y;
//				if (event.detail == SWT.MENU_KEYBOARD) {
//					Point pt = getDisplay().map(fText, null, fText.getCaretLocation());
//					e.x = pt.x;
//					e.y = pt.y;
//				}
				notifyListeners(SWT.MenuDetect, e);
				event.doit = e.doit;
				event.x = e.x;
				event.y = e.y;
				break;
			}
			case SWT.Modify: {
				fList.deselectAll ();
				final Event e = new Event ();
				e.time = event.time;
				notifyListeners(SWT.Modify, e);
				break;
			}
			case SWT.MouseDown: {
				final Point pt = getDisplay ().map ((Control) event.widget, this, event.x, event.y);
				final Event mouseEvent = new Event ();
				mouseEvent.button = event.button;
				mouseEvent.count = event.count;
				mouseEvent.stateMask = event.stateMask;
				mouseEvent.time = event.time;
				mouseEvent.x = pt.x; mouseEvent.y = pt.y;
				notifyListeners(SWT.MouseDown, mouseEvent);
				if (isDisposed()) {
					break;
				}
				event.doit = mouseEvent.doit;
				if (!event.doit) {
					break;
				}
				if (event.button != 1) {
					return;
				}
				final boolean dropped = isDropped();
				if (!dropped) {
					setFocus();
				}
				dropDown(!dropped);
				break;
			}
			case SWT.MouseUp: {
				final Point pt = getDisplay().map((Control) event.widget, this, event.x, event.y);
				final Event mouseEvent = new Event();
				mouseEvent.button = event.button;
				mouseEvent.count = event.count;
				mouseEvent.stateMask = event.stateMask;
				mouseEvent.time = event.time;
				mouseEvent.x = pt.x; mouseEvent.y = pt.y;
				notifyListeners(SWT.MouseUp, mouseEvent);
				if (isDisposed()) {
					break;
				}
				event.doit = mouseEvent.doit;
				if (!event.doit) {
					break;
				}
				if (event.button != 1) {
					return;
				}
				break;
			}
			case SWT.MouseWheel: {
				notifyListeners(SWT.MouseWheel, event);
				event.type = SWT.None;
				if (isDisposed()) {
					break;
				}
				if (!event.doit) {
					break;
				}
				if (event.count != 0) {
					event.doit = false;
					final int oldIndex = getSelectionIndex();
					if (event.count > 0) {
						select(Math.max(oldIndex - 1, 0));
					} else {
						select(Math.min(oldIndex + 1, getItemCount() - 1));
					}
					if (oldIndex != getSelectionIndex()) {
						final Event e = new Event();
						e.time = event.time;
						e.stateMask = event.stateMask;
						notifyListeners(SWT.Selection, e);
					}
					if (isDisposed()) {
						break;
					}
				}
				break;
			}
			case SWT.Traverse: {		
				switch (event.detail) {
					case SWT.TRAVERSE_ARROW_PREVIOUS:
					case SWT.TRAVERSE_ARROW_NEXT:
						// The enter causes default selection and
						// the arrow keys are used to manipulate the list contents so
						// do not use them for traversal.
						event.doit = false;
						break;
					case SWT.TRAVERSE_TAB_PREVIOUS:
						event.doit = traverse(SWT.TRAVERSE_TAB_PREVIOUS);
						event.detail = SWT.TRAVERSE_NONE;
						return;
				}		
				final Event e = new Event();
				e.time = event.time;
				e.detail = event.detail;
				e.doit = event.doit;
				e.character = event.character;
				e.keyCode = event.keyCode;
				e.keyLocation = event.keyLocation;
				notifyListeners(SWT.Traverse, e);
				event.doit = e.doit;
				event.detail = e.detail;
				break;
			}
			case SWT.Verify: {
				final Event e = new Event();
				e.text = event.text;
				e.start = event.start;
				e.end = event.end;
				e.character = event.character;
				e.keyCode = event.keyCode;
				e.keyLocation = event.keyLocation;
				e.stateMask = event.stateMask;
				notifyListeners(SWT.Verify, e);
				event.text = e.text;
				event.doit = e.doit;
				break;
			}
		}
	}
	
	private void popupEvent(final Event event) {
		switch (event.type) {
			case SWT.Paint:
				// draw black rectangle around list
				final Rectangle listRect = fList.getBounds();
				final Color black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
				event.gc.setForeground(black);
				event.gc.drawRectangle(0, 0, listRect.width + 1, listRect.height + 1);
				break;
			case SWT.Close:
				event.doit = false;
				dropDown (false);
				break;
		}
	}
	
	private void listEvent (final Event event) {
		switch (event.type) {
			case SWT.Dispose:
				checkShell();
				break;
			case SWT.FocusIn: {
				handleFocus (SWT.FocusIn);
				break;
			}
			case SWT.FocusOut: {
				/*
				 * Behavior in Windows, GTK & Cocoa: When the arrow button is pressed
				 * with the popup list visible, the following events are received-
				 * popup control receives a deactivate event, 
				 * list receives focus lost event, and then
				 * arrow button receives a selection event. 
				 * If we hide the popup in the focus out event, the selection event will
				 * show it again. To prevent the popup from showing again, we will detect
				 * this case and let the selection event of the arrow button hide the popup.
				 */
				if (!"carbon".equals(SWT.getPlatform())) {
					final Point point = toControl(getDisplay().getCursorLocation());
					final Point size = getSize();
//					Point point = fArrow.toControl(getDisplay().getCursorLocation());
//					Point size = fArrow.getSize();
					final Rectangle rect = new Rectangle(0, 0, size.x, size.y);
					if (rect.contains(point)) {
						final boolean comboShellActivated = getDisplay().getActiveShell() == getShell();
						if (!comboShellActivated) {
							dropDown(false);
						}
						break;
					}
				}
				dropDown(false);
				break;
			}
			case SWT.MouseUp: {
				if (event.button != 1) {
					return;
				}
				dropDown (false);
				break;
			}
			case SWT.Selection: {
				final int index = fList.getSelectionIndex();
				if (index == -1) {
					return;
				}
				updateText(fList.getItem(index));
				fList.setSelection (index);
				final Event e = new Event ();
				e.time = event.time;
				e.stateMask = event.stateMask;
				e.doit = event.doit;
				notifyListeners (SWT.Selection, e);
				event.doit = e.doit;
				break;
			}
			case SWT.Traverse: {
				switch (event.detail) {
					case SWT.TRAVERSE_RETURN:
					case SWT.TRAVERSE_ESCAPE:
					case SWT.TRAVERSE_ARROW_PREVIOUS:
					case SWT.TRAVERSE_ARROW_NEXT:
						event.doit = false;
						break;
					case SWT.TRAVERSE_TAB_NEXT:
					case SWT.TRAVERSE_TAB_PREVIOUS:
						event.doit = fText.traverse(event.detail);
						event.detail = SWT.TRAVERSE_NONE;
						if (event.doit) {
							dropDown(false);
						}
						return;
				}
				final Event e = new Event ();
				e.time = event.time;
				e.detail = event.detail;
				e.doit = event.doit;
				e.character = event.character;
				e.keyCode = event.keyCode;
				e.keyLocation = event.keyLocation;
				notifyListeners (SWT.Traverse, e);
				event.doit = e.doit;
				event.detail = e.detail;
				break;
			}
			case SWT.KeyUp: {		
				final Event e = new Event ();
				e.time = event.time;
				e.character = event.character;
				e.keyCode = event.keyCode;
				e.keyLocation = event.keyLocation;
				e.stateMask = event.stateMask;
				notifyListeners (SWT.KeyUp, e);
				event.doit = e.doit;
				break;
			}
			case SWT.KeyDown: {
				if (event.character == SWT.ESC) { 
					// Escape key cancels popup list
					dropDown (false);
				}
				if ((event.stateMask & SWT.ALT) != 0 && (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN)) {
					dropDown (false);
				}
				if (event.character == SWT.CR) {
					// Enter causes default selection
					dropDown (false);
					final Event e = new Event ();
					e.time = event.time;
					e.stateMask = event.stateMask;
					notifyListeners (SWT.DefaultSelection, e);
				}
				// At this point the widget may have been disposed.
				// If so, do not continue.
				if (isDisposed ()) {
					break;
				}
				final Event e = new Event();
				e.time = event.time;
				e.character = event.character;
				e.keyCode = event.keyCode;
				e.keyLocation = event.keyLocation;
				e.stateMask = event.stateMask;
				notifyListeners(SWT.KeyDown, e);
				event.doit = e.doit;
				break;
				
			}
		}
	}
	
	void updateText(final TableItem item) {
		if (item == null) {
			fImage.setImage(null);
			fText.setText(""); //$NON-NLS-1$
			
			return;
		}
		
		fImage.setImage(item.getImage());
		fText.setText(item.getText());
		fText.setFont(item.getFont());
	}
	
	
	@Override
	public boolean traverse(final int event){
		/*
		 * When the traverse event is sent to the CCombo, it will create a list of
		 * controls to tab to next. Since the CCombo is a composite, the next control is
		 * the Text field which is a child of the CCombo. It will set focus to the text
		 * field which really is itself. So, call the traverse next events directly on the text.
		 */
		if (event == SWT.TRAVERSE_ARROW_NEXT || event == SWT.TRAVERSE_TAB_NEXT) {
			return fArrow.traverse(event);
		}
		return super.traverse(event);
	}
	
	
	/**
	 * Searches the receiver's list starting at the first item
	 * (index 0) until an item is found that is equal to the 
	 * argument, and returns the index of that item. If no item
	 * is found, returns -1.
	 *
	 * @param item the search item
	 * @return the index of the item
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public int indexOf(final Item item) {
		checkWidget();
		if (item == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		return fList.indexOf((TableItem) item);
	}
	
	/**
	 * Removes the item from the receiver's list at the given
	 * zero-relative index.
	 *
	 * @param index the index for the item
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void remove(final int index) {
		checkWidget();
		fList.remove(index);
		fItemCount = fList.getItemCount();
	}
	
	/**
	 * Removes all of the items from the receiver's list and clear the
	 * contents of receiver's text field.
	 * <p>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void removeAll() {
		checkWidget();
		fText.setText(""); //$NON-NLS-1$
		fList.removeAll();
		fItemCount = fList.getItemCount();
	}
	
	
	/**
	 * Returns the zero-relative index of the item which is currently
	 * selected in the receiver's list, or -1 if no item is selected.
	 *
	 * @return the index of the selected item
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public int getSelectionIndex() {
		checkWidget();
		return fList.getSelectionIndex();
	}
	
	/**
	 * Selects the item at the given zero-relative index in the receiver's 
	 * list.  If the item at the index was already selected, it remains
	 * selected. Indices that are out of range are ignored.
	 *
	 * @param index the index of the item to select
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void select(final int index) {
		checkWidget();
		if (index == -1) {
			fList.deselectAll();
			fText.setText(""); //$NON-NLS-1$
			return;
		}
		if (0 <= index && index < fList.getItemCount()) {
			if (index != getSelectionIndex()) {
				updateText(fList.getItem(index));
				fList.select(index);
				fList.showSelection();
			}
		}
	}
	
	/**
	 * Deselects the item at the given zero-relative index in the receiver's 
	 * list.  If the item at the index was already deselected, it remains
	 * deselected. Indices that are out of range are ignored.
	 *
	 * @param index the index of the item to deselect
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void deselect(final int index) {
		checkWidget();
		if (0 <= index && index < fList.getItemCount() &&
				index == fList.getSelectionIndex()) {
			updateText(null);
			fList.deselect(index);
		}
	}
	
	/**
	 * Deselects all selected items in the receiver's list.
	 * <p>
	 * Note: To clear the selection in the receiver's text field,
	 * use <code>clearSelection()</code>.
	 * </p>
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see #clearSelection
	 */
	public void deselectAll() {
		checkWidget();
		updateText(null);
		fList.deselectAll();
	}
	
}
