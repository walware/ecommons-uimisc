/*******************************************************************************
 * Copyright (c) 2009-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.walware.ecommons.FastList;
import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.util.LayoutUtil;


/**
 * Search text custom widget (with clear button)
 */
public class SearchText extends Composite {
	// see org.eclipse.ui.dialogs.FilteredTree
	
	
	public static interface Listener {
		
		void okPressed();
		
		void downPressed();
		
		void textChanged(boolean user);
		
	}
	
	
	private static Boolean useNativeSearchField;
	
	private static boolean useNativeSearchField(final Composite composite) {
		if (useNativeSearchField == null) {
			useNativeSearchField = Boolean.FALSE;
			Text testText = null;
			try {
				testText = new Text(composite, SWT.SEARCH | SWT.ICON_CANCEL);
				useNativeSearchField = new Boolean((testText.getStyle() & SWT.ICON_CANCEL) != 0);
			}
			finally {
				if (testText != null) {
					testText.dispose();
				}
			}
		}
		return useNativeSearchField.booleanValue();
	}
	
	
	private Text fTextControl;
	
	private final FastList<Listener> fListeners = new FastList<Listener>(Listener.class);
	
	private boolean fTypingChange = true;
	
	
	public SearchText(final Composite parent) {
		this(parent, null);
	}
	
	public SearchText(final Composite parent, final String initialText) {
		super(parent, useNativeSearchField(parent) ? SWT.NONE : SWT.BORDER);
		final boolean nativeMode = useNativeSearchField.booleanValue();
		
		setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		final GridLayout layout = LayoutUtil.applyCompositeDefaults(new GridLayout(),
				(nativeMode) ? 1 : 2);
		layout.horizontalSpacing = 0;
		setLayout(layout);
		
		createText(this, nativeMode);
		createClearTextButtonSupport(this, nativeMode);
		
		if (initialText != null) {
			setText(initialText);
			fTextControl.selectAll();
		}
	}
	
	
	public void addListener(final Listener listener) {
		fListeners.add(listener);
	}
	
	public void removeListener(final Listener listener) {
		fListeners.remove(listener);
	}
	
	
	private void textChanged0() {
		final boolean typingChange = fTypingChange;
		fTypingChange = true;
		final Listener[] listeners = fListeners.toArray();
		for (final Listener listener : listeners) {
			listener.textChanged(typingChange);
		}
	}
	
	private void okPressed0() {
		final Listener[] listeners = fListeners.toArray();
		for (final Listener listener : listeners) {
			listener.okPressed();
		}
	}
	
	private void downPressed0() {
		final Listener[] listeners = fListeners.toArray();
		for (final Listener listener : listeners) {
			listener.downPressed();
		}
	}
	
	
	@Override
	public boolean setFocus() {
		return fTextControl.setFocus();
	}
	
	@Override
	public void setToolTipText(final String text) {
		fTextControl.setToolTipText(text);
	}
	
	public void setText(final String text) {
		fTypingChange = false;
		fTextControl.setText((text != null) ? text : "");
	}
	
	public String getText() {
		return fTextControl.getText();
	}
	
	public Text getTextControl() {
		return fTextControl;
	}
	
	public void clearText() {
		setText(null);
		fTextControl.setFocus();
	}
	
	/**
	 * Create the text widget.
	 * 
	 * @param parent parent <code>Composite</code> of toolbar button
	 */
	private void createText(final Composite parent, final boolean nativeMode) {
		fTextControl = new Text(this, (nativeMode) ?
				(SWT.LEFT | SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL) :
				(SWT.LEFT | SWT.SINGLE));
		fTextControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		fTextControl.getAccessible().addAccessibleListener(
				new AccessibleAdapter() {
					@Override
					public void getName(final AccessibleEvent e) {
						e.result = getAccessibleMessage();
					}
				});
		fTextControl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.keyCode == SWT.ARROW_DOWN) {
					downPressed0();
					e.doit = false;
					return;
				}
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					okPressed0();
					e.doit = false;
					return;
				}
			}
		});
		fTextControl.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				textChanged0();
			}
		});
	}
	
	protected String getAccessibleMessage() {
		return fTextControl.getText();
	}
	
	/**
	 * Create the button that clears the text.
	 * 
	 * @param parent parent <code>Composite</code> of toolbar button
	 */
	private void createClearTextButtonSupport(final Composite parent, final boolean nativeMode) {
		fTextControl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.keyCode == SWT.ESC) {
					// allows other top level actions if field was already empty
					final boolean alreadyClear = (fTextControl.getText().isEmpty());
					setText(null);
					e.doit = alreadyClear;
					return;
				}
			}
		});
		if (nativeMode) {
			fTextControl.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetDefaultSelected(final SelectionEvent e) {
					if (e.detail == SWT.ICON_CANCEL) {
						clearText();
					}
				}
			});
		}
		else {
			final Image activeImage = SharedUIResources.getImages().get(SharedUIResources.LOCTOOL_CLEARSEARCH_IMAGE_ID);
			final Image inactiveImage = SharedUIResources.getImages().get(SharedUIResources.LOCTOOLD_CLEARSEARCH_IMAGE_ID);
			final Image pressedImage = new Image(Display.getCurrent(), activeImage, SWT.IMAGE_GRAY);
			
			final Label clearButton = new Label(parent, SWT.NONE);
			clearButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, true));
			clearButton.setImage(inactiveImage);
			clearButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			clearButton.setToolTipText("Clear");
			clearButton.addMouseListener(new MouseAdapter() {
				private MouseMoveListener fMoveListener;
				
				@Override
				public void mouseDown(final MouseEvent e) {
					clearButton.setImage(pressedImage);
					fMoveListener = new MouseMoveListener() {
						private boolean fMouseInButton = true;
						
						@Override
						public void mouseMove(final MouseEvent e) {
							final boolean mouseInButton= isMouseInButton(e);
							if (mouseInButton != fMouseInButton) {
								fMouseInButton = mouseInButton;
								clearButton.setImage(mouseInButton ? pressedImage : inactiveImage);
							}
						}
					};
					clearButton.addMouseMoveListener(fMoveListener);
				}
				
				@Override
				public void mouseUp(final MouseEvent e) {
					if (fMoveListener != null) {
						clearButton.removeMouseMoveListener(fMoveListener);
						fMoveListener = null;
						final boolean mouseInButton= isMouseInButton(e);
						clearButton.setImage(mouseInButton ? activeImage : inactiveImage);
						if (mouseInButton) {
							clearText();
						}
					}
				}
				
				private boolean isMouseInButton(final MouseEvent e) {
					final Point buttonSize = clearButton.getSize();
					return 0 <= e.x && e.x < buttonSize.x && 0 <= e.y && e.y < buttonSize.y;
				}
			});
			clearButton.addMouseTrackListener(new MouseTrackListener() {
				@Override
				public void mouseEnter(final MouseEvent e) {
					clearButton.setImage(activeImage);
				}
				@Override
				public void mouseExit(final MouseEvent e) {
					clearButton.setImage(inactiveImage);
				}
				@Override
				public void mouseHover(final MouseEvent e) {
				}
			});
			clearButton.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(final DisposeEvent e) {
					pressedImage.dispose();
				}
			});
			clearButton.getAccessible().addAccessibleListener(
				new AccessibleAdapter() {
					@Override
					public void getName(final AccessibleEvent e) {
						e.result = "Clear filter field";
					}
			});
			clearButton.getAccessible().addAccessibleControlListener(
				new AccessibleControlAdapter() {
					@Override
					public void getRole(final AccessibleControlEvent e) {
						e.detail = ACC.ROLE_PUSHBUTTON;
					}
			});
		}
	}
	
}
