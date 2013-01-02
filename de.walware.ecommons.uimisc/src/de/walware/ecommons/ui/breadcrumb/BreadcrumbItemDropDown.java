/*******************************************************************************
 * Copyright (c) 2008-2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.breadcrumb;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.Geometry;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import de.walware.ecommons.ui.internal.AccessibleArrowImage;


/**
 * The part of the breadcrumb item with the drop down menu.
 */
class BreadcrumbItemDropDown {
	
	/**
	 * Tells whether this class is in debug mode.
	 */
	private static boolean DEBUG= "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jdt.ui/debug/BreadcrumbItemDropDown")); //$NON-NLS-1$//$NON-NLS-2$
	
	private static final boolean IS_MAC_WORKAROUND= "carbon".equals(SWT.getPlatform()); //$NON-NLS-1$
	
	private static final int DROP_DOWN_HIGHT= 300;
	private static final int DROP_DOWN_WIDTH= 500;
	
	private final BreadcrumbItem fParent;
	private final Composite fParentComposite;
	private final ToolBar fToolBar;
	
	private boolean fMenuIsShown;
	private boolean fEnabled;
	private TreeViewer fDropDownViewer;
	private Shell fShell;
	
	private MenuManager fDropDownMenuManager;
	
	
	public BreadcrumbItemDropDown(final BreadcrumbItem parent, final Composite composite) {
		fParent= parent;
		fParentComposite= composite;
		fMenuIsShown= false;
		fEnabled= true;
		
		fToolBar= new ToolBar(composite, SWT.FLAT);
		fToolBar.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		fToolBar.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			@Override
			public void getName(final AccessibleEvent e) {
				e.result= BreadcrumbMessages.BreadcrumbItemDropDown_showDropDownMenu_action_tooltip;
			}
		});
		final ToolBarManager manager= new ToolBarManager(fToolBar);
		
		final Action showDropDownMenuAction= new Action(null, SWT.NONE) {
			@Override
			public void run() {
				Shell shell= fParent.getDropDownShell();
				if (shell != null) {
					return;
				}
				
				shell= fParent.getViewer().getDropDownShell();
				if (shell != null) {
					shell.close();
				}
				
				showMenu();
				
				fShell.setFocus();
			}
		};
		
		showDropDownMenuAction.setImageDescriptor(new AccessibleArrowImage(
				isLTR() ? SWT.RIGHT : SWT.LEFT, AccessibleArrowImage.DEFAULT_SIZE, 
				composite.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND).getRGB(),
				composite.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND).getRGB() ));
		showDropDownMenuAction.setToolTipText(BreadcrumbMessages.BreadcrumbItemDropDown_showDropDownMenu_action_tooltip);
		manager.add(showDropDownMenuAction);
		
		manager.update(true);
		if (IS_MAC_WORKAROUND) {
			manager.getControl().addMouseListener(new MouseAdapter() {
				// see also BreadcrumbItemDetails#addElementListener(Control)
				@Override
				public void mouseDown(final MouseEvent e) {
					showDropDownMenuAction.run();
				}
			});
		}
		
		fDropDownMenuManager = new MenuManager();
		fDropDownMenuManager.setRemoveAllWhenShown(true);
		fDropDownMenuManager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(final IMenuManager manager) {
				final IStructuredSelection selection = (IStructuredSelection) fDropDownViewer.getSelection();
				if (!selection.isEmpty()) {
					fParent.getViewer().fillDropDownContextMenu(manager, selection.getFirstElement());
				}
			}
		});
	}
	
	
	/**
	 * Return the width of this element.
	 * 
	 * @return the width of this element
	 */
	public int getWidth() {
		return fToolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
	}
	
	/**
	 * Set whether the drop down menu is available.
	 * 
	 * @param enabled true if available
	 */
	public void setEnabled(final boolean enabled) {
		fEnabled= enabled;
		
		fToolBar.setVisible(enabled);
	}
	
	/**
	 * Tells whether the menu is shown.
	 *
	 * @return true if the menu is open
	 */
	public boolean isMenuShown() {
		return fMenuIsShown;
	}
	
	/**
	 * Returns the shell used for the drop down menu if it is shown.
	 * 
	 * @return the drop down shell or <code>null</code>
	 */
	public Shell getDropDownShell() {
		if (!isMenuShown()) {
			return null;
		}
		
		return fShell;
	}
	
	/**
	 * Returns the drop down selection provider.
	 * 
	 * @return the selection provider of the drop down if {@link #isMenuShown()}, <code>null</code>
	 *         otherwise
	 */
	public ISelectionProvider getDropDownSelectionProvider() {
		if (!fMenuIsShown) {
			return null;
		}
		
		return fDropDownViewer;
	}
	
	/**
	 * Opens the drop down menu.
	 */
	public void showMenu() {
		if (DEBUG) {
			System.out.println("BreadcrumbItemDropDown.showMenu()"); //$NON-NLS-1$
		}
		
		if (!fEnabled || fMenuIsShown) {
			return;
		}
		
		fShell= new Shell(fToolBar.getShell(), SWT.RESIZE | SWT.TOOL | SWT.ON_TOP);
		if (DEBUG) {
			System.out.println("	creating new shell"); //$NON-NLS-1$
		}
		
		try {
			fMenuIsShown= true;
			
			final GridLayout layout= new GridLayout(1, false);
			layout.marginHeight= 0;
			layout.marginWidth= 0;
			fShell.setLayout(layout);
			
			final Composite composite= new Composite(fShell, SWT.NONE);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			final GridLayout gridLayout= new GridLayout(1, false);
			gridLayout.marginHeight= 0;
			gridLayout.marginWidth= 0;
			composite.setLayout(gridLayout);
			
			fDropDownViewer= createViewer(composite);
			fDropDownViewer.setUseHashlookup(true);
			
			final Tree tree= (Tree) fDropDownViewer.getControl();
			tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
			final Object input= fParent.getData();
			fParent.getViewer().configureDropDownViewer(fDropDownViewer, input);
			fDropDownViewer.setInput(input);
			
			setShellBounds(fShell);
			
			fDropDownViewer.addOpenListener(new IOpenListener() {
				
				@Override
				public void open(final OpenEvent event) {
					if (DEBUG) {
						System.out.println("BreadcrumbItemDropDown.showMenu()$treeViewer>open"); //$NON-NLS-1$
					}
					
					final ISelection selection= event.getSelection();
					if (!(selection instanceof IStructuredSelection)) {
						return;
					}
					
					final Object element= ((IStructuredSelection) selection).getFirstElement();
					if (element == null) {
						return;
					}
					
					openElement(element);
				}
				
			});
			
			tree.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseUp(final MouseEvent e) {
					if (DEBUG) {
						System.out.println("BreadcrumbItemDropDown.showMenu()$treeViewer>mouseUp"); //$NON-NLS-1$
					}
					
					if (e.button != 1) {
						return;
					}
					
					if ((OpenStrategy.getOpenMethod() & OpenStrategy.SINGLE_CLICK) != 0) {
						return;
					}
					
					final Item item= tree.getItem(new Point(e.x, e.y));
					if (item == null) {
						return;
					}
					
					openElement(item.getData());
				}
				
				@Override
				public void mouseDown(final MouseEvent e) {
				}
				
				@Override
				public void mouseDoubleClick(final MouseEvent e) {
				}
				
			});
			
			tree.addMouseMoveListener(new MouseMoveListener() {
				TreeItem fLastItem= null;
				
				@Override
				public void mouseMove(final MouseEvent e) {
					if (tree.equals(e.getSource())) {
						final Object o= tree.getItem(new Point(e.x, e.y));
						if (o instanceof TreeItem) {
							final Rectangle clientArea = tree.getClientArea();
							final TreeItem currentItem= (TreeItem) o;
							if (!o.equals(fLastItem)) {
								fLastItem= (TreeItem) o;
								tree.setSelection(new TreeItem[] { fLastItem });
							} else if (e.y - clientArea.y < tree.getItemHeight() / 4) {
								// Scroll up
								if (currentItem.getParentItem() == null) {
									final int index= tree.indexOf((TreeItem) o);
									if (index < 1) {
										return;
									}
									
									fLastItem= tree.getItem(index - 1);
									tree.setSelection(new TreeItem[] { fLastItem });
								} else {
									final Point p= tree.toDisplay(e.x, e.y);
									final Item item= fDropDownViewer.scrollUp(p.x, p.y);
									if (item instanceof TreeItem) {
										fLastItem= (TreeItem) item;
										tree.setSelection(new TreeItem[] { fLastItem });
									}
								}
							} else if (clientArea.y + clientArea.height - e.y < tree.getItemHeight() / 4) {
								// Scroll down
								if (currentItem.getParentItem() == null) {
									final int index= tree.indexOf((TreeItem) o);
									if (index >= tree.getItemCount() - 1) {
										return;
									}
									
									fLastItem= tree.getItem(index + 1);
									tree.setSelection(new TreeItem[] { fLastItem });
								} else {
									final Point p= tree.toDisplay(e.x, e.y);
									final Item item= fDropDownViewer.scrollDown(p.x, p.y);
									if (item instanceof TreeItem) {
										fLastItem= (TreeItem) item;
										tree.setSelection(new TreeItem[] { fLastItem });
									}
								}
							}
						}
					}
				}
				
			});
			
			tree.addKeyListener(new KeyListener() {
				
				@Override
				public void keyPressed(final KeyEvent e) {
					if (e.keyCode == SWT.ARROW_UP) {
						final TreeItem[] selection= tree.getSelection();
						if (selection.length != 1) {
							return;
						}
						
						final int selectionIndex= tree.indexOf(selection[0]);
						if (selectionIndex != 0) {
							return;
						}
						
						fShell.close();
					}
				}
				
				@Override
				public void keyReleased(final KeyEvent e) {
				}
				
			});
			
			tree.setMenu(fDropDownMenuManager.createContextMenu(tree));
			
			fDropDownViewer.addTreeListener(new ITreeViewerListener() {
				
				@Override
				public void treeCollapsed(final TreeExpansionEvent event) {
				}
				
				@Override
				public void treeExpanded(final TreeExpansionEvent event) {
					tree.setRedraw(false);
					fShell.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							if (fShell.isDisposed()) {
								return;
							}
							
							try {
								resizeShell(fShell);
							} finally {
								tree.setRedraw(true);
							}
						}
					});
				}
				
			});
			
			final int index= fParent.getViewer().getIndexOfItem(fParent);
			if (index < fParent.getViewer().getItemCount() - 1) {
				final BreadcrumbItem childItem= fParent.getViewer().getItem(index + 1);
				final Object child= childItem.getData();
				
				fDropDownViewer.setSelection(new StructuredSelection(child), true);
				
				final TreeItem[] selection= tree.getSelection();
				if (selection.length > 0) {
					tree.setTopItem(selection[0]);
				}
			}
			
			fShell.setVisible(true);
			installCloser(fShell);
		}
		catch (final RuntimeException e) {
			fMenuIsShown = false;
			fShell.close();
			throw e;
		}
	}
	
	protected TreeViewer createViewer(final Composite composite) {
		return new TreeViewer(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
	}
	
	private void openElement(final Object data) {
		if (data == null) {
			return;
		}
		
		// This might or might not open an editor
		fParent.getViewer().fireMenuSelection(data);
		
		final boolean treeHasFocus= !fShell.isDisposed() && fDropDownViewer.getTree().isFocusControl();
		
		if (DEBUG) {
			System.out.println("	isDisposed: " + fShell.isDisposed()); //$NON-NLS-1$
			System.out.println("	shell hasFocus: " + (!fShell.isDisposed() && fShell.isFocusControl())); //$NON-NLS-1$
			System.out.println("	tree hasFocus: " + treeHasFocus); //$NON-NLS-1$
		}
		
		if (fShell.isDisposed()) {
			return;
		}
		
		if (!treeHasFocus) {
			fShell.close();
			return;
		}
		
		toggleExpansionState(data);
	}
	
	private void toggleExpansionState(final Object element) {
		final Tree tree= fDropDownViewer.getTree();
		if (fDropDownViewer.getExpandedState(element)) {
			fDropDownViewer.collapseToLevel(element, 1);
		} else {
			tree.setRedraw(false);
			try {
				fDropDownViewer.expandToLevel(element, 1);
				resizeShell(fShell);
			} finally {
				tree.setRedraw(true);
			}
		}
	}
	
	/**
	 * The closer closes the given shell when the focus is lost.
	 * 
	 * @param shell the shell to install the closer to
	 */
	private void installCloser(final Shell shell) {
		final Listener focusListener= new Listener() {
			@Override
			public void handleEvent(final Event event) {
				final Widget focusElement= event.widget;
				final boolean isFocusBreadcrumbTreeFocusWidget= focusElement == shell || focusElement instanceof Tree && ((Tree)focusElement).getShell() == shell;
				final boolean isFocusWidgetParentShell= focusElement instanceof Control && ((Control)focusElement).getShell().getParent() == shell;
				
				switch (event.type) {
					case SWT.FocusIn:
						if (DEBUG) {
							System.out.println("focusIn - is breadcrumb tree: " + isFocusBreadcrumbTreeFocusWidget); //$NON-NLS-1$
						}
						
						if (!isFocusBreadcrumbTreeFocusWidget && !isFocusWidgetParentShell) {
							if (DEBUG) {
								System.out.println("==> closing shell since focus in other widget"); //$NON-NLS-1$
							}
							shell.close();
						}
						break;
						
					case SWT.FocusOut:
						if (DEBUG) {
							System.out.println("focusOut - is breadcrumb tree: " + isFocusBreadcrumbTreeFocusWidget); //$NON-NLS-1$
						}
						
						if (event.display.getActiveShell() == null) {
							if (DEBUG) {
								System.out.println("==> closing shell since event.display.getActiveShell() == null"); //$NON-NLS-1$
							}
							shell.close();
						}
						break;
						
					default:
						Assert.isTrue(false);
				}
			}
		};
		
		final Display display= shell.getDisplay();
		display.addFilter(SWT.FocusIn, focusListener);
		display.addFilter(SWT.FocusOut, focusListener);
		
		final ControlListener controlListener= new ControlListener() {
			
			@Override
			public void controlMoved(final ControlEvent e) {
				shell.close();
			}
			
			@Override
			public void controlResized(final ControlEvent e) {
				shell.close();
			}
			
		};
		fToolBar.getShell().addControlListener(controlListener);
		
		shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				if (DEBUG) {
					System.out.println("==> shell disposed"); //$NON-NLS-1$
				}
				
				display.removeFilter(SWT.FocusIn, focusListener);
				display.removeFilter(SWT.FocusOut, focusListener);
				
				if (!fToolBar.isDisposed()) {
					fToolBar.getShell().removeControlListener(controlListener);
				}
			}
			
		});
		shell.addShellListener(new ShellListener() {
			
			@Override
			public void shellActivated(final ShellEvent e) {
			}
			
			@Override
			public void shellClosed(final ShellEvent e) {
				if (DEBUG) {
					System.out.println("==> shellClosed"); //$NON-NLS-1$
				}
				
				if (!fMenuIsShown) {
					return;
				}
				
				fMenuIsShown= false;
				fDropDownViewer= null;
			}
			
			@Override
			public void shellDeactivated(final ShellEvent e) {
			}
			
			@Override
			public void shellDeiconified(final ShellEvent e) {
			}
			
			@Override
			public void shellIconified(final ShellEvent e) {
			}
			
		});
	}
	
	/**
	 * Calculates a useful size for the given shell.
	 * 
	 * @param shell the shell to calculate the size for.
	 */
	private void setShellBounds(final Shell shell) {
		final Rectangle rect= fParentComposite.getBounds();
		final Rectangle toolbarBounds= fToolBar.getBounds();
		
		shell.pack();
		final Point size= shell.getSize();
		final int height= Math.min(size.y, DROP_DOWN_HIGHT);
		final int width= Math.max(Math.min(size.x, DROP_DOWN_WIDTH), 250);
		
		int imageBoundsX= 0;
		if (fDropDownViewer.getTree().getItemCount() > 0) {
			final TreeItem item= fDropDownViewer.getTree().getItem(0);
			imageBoundsX= item.getImageBounds(0).x;
		}
		
		final Rectangle trim= fShell.computeTrim(0, 0, width, height);
		int x= toolbarBounds.x + toolbarBounds.width + 2 + trim.x - imageBoundsX;
		if (!isLTR()) {
			x+= width;
		}
		
		Point pt= new Point(x, rect.y + rect.height);
		pt= fParentComposite.toDisplay(pt);
		
		final Rectangle monitor= getClosestMonitor(shell.getDisplay(), pt).getClientArea();
		final int overlap= (pt.x + width) - (monitor.x + monitor.width);
		if (overlap > 0) {
			pt.x-= overlap;
		}
		if (pt.x < monitor.x) {
			pt.x= monitor.x;
		}
		
		shell.setLocation(pt);
		shell.setSize(width, height);
	}
	
	/**
	 * Returns the monitor whose client area contains the given point. If no monitor contains the
	 * point, returns the monitor that is closest to the point.
	 * <p>
	 * Copied from <code>org.eclipse.jface.window.Window.getClosestMonitor(Display, Point)</code>
	 * </p>
	 * 
	 * @param display the display showing the monitors
	 * @param point point to find (display coordinates)
	 * @return the monitor closest to the given point
	 */
	private static Monitor getClosestMonitor(final Display display, final Point point) {
		int closest= Integer.MAX_VALUE;
		
		final Monitor[] monitors= display.getMonitors();
		Monitor result= monitors[0];
		
		for (int i= 0; i < monitors.length; i++) {
			final Monitor current= monitors[i];
			
			final Rectangle clientArea= current.getClientArea();
			
			if (clientArea.contains(point)) {
				return current;
			}
			
			final int distance= Geometry.distanceSquared(Geometry.centerPoint(clientArea), point);
			if (distance < closest) {
				closest= distance;
				result= current;
			}
		}
		
		return result;
	}
	
	/**
	 * Set the size of the given shell such that more content can be shown. The shell size does not
	 * exceed {@link #DROP_DOWN_HIGHT} and {@link #DROP_DOWN_WIDTH}.
	 * 
	 * @param shell the shell to resize
	 */
	private void resizeShell(final Shell shell) {
		final Point size= shell.getSize();
		final int currentWidth= size.x;
		final int currentHeight= size.y;
		
		if (currentHeight >= DROP_DOWN_HIGHT && currentWidth >= DROP_DOWN_WIDTH) {
			return;
		}
		
		final Point preferedSize= shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		
		int newWidth;
		if (currentWidth >= DROP_DOWN_WIDTH) {
			newWidth= currentWidth;
		} else {
			newWidth= Math.min(Math.max(preferedSize.x, currentWidth), DROP_DOWN_WIDTH);
		}
		int newHeight;
		if (currentHeight >= DROP_DOWN_HIGHT) {
			newHeight= currentHeight;
		} else {
			newHeight= Math.min(Math.max(preferedSize.y, currentHeight), DROP_DOWN_HIGHT);
		}
		
		if (newHeight != currentHeight || newWidth != currentWidth) {
			shell.setRedraw(false);
			try {
				shell.setSize(newWidth, newHeight);
				if (!isLTR()) {
					final Point location= shell.getLocation();
					shell.setLocation(location.x - (newWidth - currentWidth), location.y);
				}
			} finally {
				shell.setRedraw(true);
			}
		}
	}
	
	/**
	 * Tells whether this the breadcrumb is in LTR or RTL mode.
	 * 
	 * @return <code>true</code> if the breadcrumb in left-to-right mode, <code>false</code>
	 *         otherwise
	 */
	private boolean isLTR() {
		return (fParentComposite.getStyle() & SWT.RIGHT_TO_LEFT) == 0;
	}
	
}
