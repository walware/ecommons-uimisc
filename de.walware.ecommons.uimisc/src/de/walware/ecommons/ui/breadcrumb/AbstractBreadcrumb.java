/*******************************************************************************
 * Copyright (c) 2010-2011 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.breadcrumb;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.internal.services.IServiceLocatorCreator;
import org.eclipse.ui.internal.services.ServiceLocator;
import org.eclipse.ui.services.IDisposable;
import org.eclipse.ui.services.IServiceLocator;

import de.walware.ecommons.ui.util.LayoutUtil;


public abstract class AbstractBreadcrumb implements IBreadcrumb {
	
	
	private static final String ACTIVE_TAB_BG_END = "org.eclipse.ui.workbench.ACTIVE_TAB_BG_END"; //$NON-NLS-1$
	
	
	private BreadcrumbViewer fBreadcrumbViewer;
	
	private boolean fHasFocus;
	private boolean fIsActive;
	
	private Composite fComposite;
	
	private Listener fDisplayFocusListener;
	private Listener fDisplayKeyListener;
	
	private IPropertyChangeListener fPropertyChangeListener;
	
	private int fBreadcrumbServiceState;
	private ServiceLocator fBreadcrumbServices;
	
	
	public AbstractBreadcrumb() {
	}
	
	
	public ISelectionProvider getSelectionProvider() {
		return fBreadcrumbViewer;
	}
	
	public void setInput(final Object element) {
		if (element == null) {
			return;
		}
		final Object input = fBreadcrumbViewer.getInput();
		if (element.equals(input)) {
			return;
		}
		if (fBreadcrumbViewer.isDropDownOpen()) {
			return;
		}
		fBreadcrumbViewer.setInput(element);
	}
	
	public void activate() {
		fBreadcrumbViewer.setSelection(new StructuredSelection(fBreadcrumbViewer.getInput()));
		fBreadcrumbViewer.setFocus();
	}
	
	public boolean isActive() {
		return fIsActive;
	}
	
	public Control createContent(final Composite parent) {
		assert (fComposite == null);
		
		fComposite = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = LayoutUtil.applySashDefaults(new GridLayout(), 1);
		fComposite.setLayout(gridLayout);
		
		fDisplayFocusListener = new Listener() {
			public void handleEvent(final Event event) {
				if (isBreadcrumbEvent(event)) {
					if (fHasFocus) {
						return;
					}
					fIsActive = true;
					
					focusGained();
				}
				else {
					if (!fIsActive) {
						return;
					}
					if (hasInputFocus()) {
						fIsActive = false;
					}
					
					if (!fHasFocus) {
						return;
					}
					focusLost();
				}
			}
		};
		Display.getCurrent().addFilter(SWT.FocusIn, fDisplayFocusListener);
		
		fBreadcrumbViewer = createViewer(fComposite);
		fBreadcrumbViewer.getControl().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		
		fBreadcrumbViewer.addOpenListener(new IOpenListener() {
			public void open(final OpenEvent event) {
				doRevealOrOpen(event.getSelection());
			}
		});
		fBreadcrumbViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				final Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (element == null) {
					return;
				}
				if (doRevealOrOpen(event.getSelection())) {
					return;
				}
//				final BreadcrumbItem item = (BreadcrumbItem) fBreadcrumbViewer.doFindItem(element);
//				if (item == null) {
//					return;
//				}
//				final int index = fBreadcrumbViewer.getIndexOfItem(item);
//				final BreadcrumbItem parentItem = fBreadcrumbViewer.getItem(index - 1);
//				parentItem.openDropDownMenu();
			}
		});
		
//		fBreadcrumbViewer.addMenuDetectListener(new MenuDetectListener() {
//			public void menuDetected(final MenuDetectEvent event) {
//				final ISelectionProvider selectionProvider = (fBreadcrumbViewer.isDropDownOpen()) ?
//						fBreadcrumbViewer.getDropDownSelectionProvider() : fBreadcrumbViewer;
//				
//				System.out.println(fBreadcrumbViewer.isDropDownOpen());
//				System.out.println(event);
//				
//				final MenuManager manager = new MenuManager();
//				try {
//					fillContextMenu(manager, (IStructuredSelection) selectionProvider.getSelection());
//					if (manager.isEmpty()) {
//						return;
//					}
//					final Menu menu = manager.createContextMenu(fBreadcrumbViewer.getControl());
//					menu.setLocation(event.x + 10, event.y + 10);
//					menu.setVisible(true);
//					while (!menu.isDisposed() && menu.isVisible()) {
//						if (!menu.getDisplay().readAndDispatch()) {
//							menu.getDisplay().sleep();
//						}
//					}
//				}
//				finally {
//					manager.dispose();
//				}
//			}
//		});
		
		fPropertyChangeListener = new IPropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent event) {
				if (ACTIVE_TAB_BG_END.equals(event.getProperty())) {
					if (fComposite.isFocusControl()) {
						fComposite.setBackground(JFaceResources.getColorRegistry().get(ACTIVE_TAB_BG_END));
					}
				}
			}
		};
		JFaceResources.getColorRegistry().addListener(fPropertyChangeListener);
		
		return fComposite;
	}
	
//	protected void fillContextMenu(final MenuManager manager, final IStructuredSelection selection) {
//	}
	
	
	public void dispose() {
		if (isServiceLocatorReady(false)) {
			fBreadcrumbServiceState = -1;
			fBreadcrumbServices.dispose();
			fBreadcrumbServices = null;
		}
		if (fPropertyChangeListener != null) {
			JFaceResources.getColorRegistry().removeListener(fPropertyChangeListener);
		}
		if (fDisplayFocusListener != null) {
			Display.getDefault().removeFilter(SWT.FocusIn, fDisplayFocusListener);
		}
		deinstallDisplayListeners();
	}
	
	/**
	 * Either reveal the selection in the editor or open the selection in a new editor. If both fail
	 * open the child pop up of the selected element.
	 * 
	 * @param selection the selection to open
	 */
	private boolean doRevealOrOpen(final ISelection selection) {
		if (doReveal(selection)) {
			setFocusToInput();
			return true;
		}
		else if (doOpen(selection)) {
			fIsActive = false;
			focusLost();
			updateInput();
			return true;
		}
		return false;
	}
	
	private boolean doOpen(final ISelection selection) {
		if (!(selection instanceof StructuredSelection)) {
			return false;
		}
		final StructuredSelection structuredSelection = (StructuredSelection) selection;
		if (structuredSelection.isEmpty()) {
			return false;
		}
		return open(structuredSelection.getFirstElement());
	}
	
	private boolean doReveal(final ISelection selection) {
		if (!(selection instanceof StructuredSelection)) {
			return false;
		}
		final StructuredSelection structuredSelection = (StructuredSelection) selection;
		if (structuredSelection.isEmpty()) {
			return false;
		}
		return reveal(structuredSelection.getFirstElement());
	}
	
	/**
	 * Focus has been transfered into the breadcrumb.
	 */
	private void focusGained() {
		if (fHasFocus) {
			focusLost();
		}
		
		fComposite.setBackground(JFaceResources.getColorRegistry().get(ACTIVE_TAB_BG_END));
		fHasFocus = true;
		
		installDisplayListeners();
		activateBreadcrumb();
		updateActions();
	}
	
	/**
	 * Focus has been revoked from the breadcrumb.
	 */
	private void focusLost() {
		fComposite.setBackground(null);
		fHasFocus = false;
		
		deinstallDisplayListeners();
		deactivateBreadcrumb();
		updateActions();
	}
	
	/**
	 * Installs all display listeners.
	 */
	private void installDisplayListeners() {
		//Sanity check
		deinstallDisplayListeners();
		
		fDisplayKeyListener = new Listener() {
			public void handleEvent(final Event event) {
				if (event.keyCode != SWT.ESC) {
					return;
				}
				if (!isBreadcrumbEvent(event)) {
					return;
				}
				setFocusToInput();
			}
		};
		Display.getDefault().addFilter(SWT.KeyDown, fDisplayKeyListener);
	}
	
	/**
	 * Removes all previously installed display listeners.
	 */
	private void deinstallDisplayListeners() {
		if (fDisplayKeyListener != null) {
			Display.getDefault().removeFilter(SWT.KeyDown, fDisplayKeyListener);
			fDisplayKeyListener= null;
		}
	}
	
	/**
	 * Tells whether the given event was issued inside the breadcrumb viewer's control.
	 * 
	 * @param event the event to inspect
	 * @return <code>true</code> if event was generated by a breadcrumb child
	 */
	private boolean isBreadcrumbEvent(final Event event) {
		if (fBreadcrumbViewer == null) {
			return false;
		}
		
		final Widget item= event.widget;
		if (!(item instanceof Control)) {
			return false;
		}
		
		final Shell dropDownShell= fBreadcrumbViewer.getDropDownShell();
		if (dropDownShell != null && isChild((Control) item, dropDownShell)) {
			return true;
		}
		
		return isChild((Control) item, fBreadcrumbViewer.getControl());
	}
	
	private boolean isChild(final Control child, final Control parent) {
		if (child == null) {
			return false;
		}
		if (child == parent) {
			return true;
		}
		return isChild(child.getParent(), parent);
	}
	
	private boolean isServiceLocatorReady(final boolean init) {
		if (fBreadcrumbServiceState == 0) {
			fBreadcrumbServiceState = -1;
			final IServiceLocator pageServices = getParentServiceLocator();
			final IServiceLocatorCreator serviceCreator = (IServiceLocatorCreator) pageServices.getService(IServiceLocatorCreator.class);
			fBreadcrumbServices = (ServiceLocator) serviceCreator.createServiceLocator(pageServices, null, new IDisposable() {
				public void dispose() {
					fBreadcrumbServiceState = -1;
					fBreadcrumbServices = null;
				}
			});
			fBreadcrumbServiceState = 1;
			initActions(fBreadcrumbServices);
		}
		return (fBreadcrumbServiceState > 0);
	}
	
	protected void initActions(final IServiceLocator services) {
		final IContextService contextService = (IContextService) services.getService(IContextService.class);
		contextService.activateContext("org.eclipse.jdt.ui.breadcrumbEditorScope"); //$NON-NLS-1$
	}
	
	
	protected abstract BreadcrumbViewer createViewer(final Composite parent);
	
	protected abstract IServiceLocator getParentServiceLocator();
	
	protected abstract boolean hasInputFocus();
	
	protected abstract void setFocusToInput();
	
	/**
	 * Intend to implement.
	 */
	protected void updateActions() {
	}
	
	/**
	 * The breadcrumb has been activated. Implementors must retarget the editor actions to the
	 * breadcrumb aware actions.
	 */
	protected void activateBreadcrumb() {
		if (isServiceLocatorReady(true)) {
			fBreadcrumbServices.activate();
		}
	}
	
	/**
	 * The breadcrumb has been deactivated. Implementors must retarget the breadcrumb actions to the
	 * editor actions.
	 */
	protected void deactivateBreadcrumb() {
		if (isServiceLocatorReady(true)) {
			fBreadcrumbServices.deactivate();
		}
	}
	
	/**
	 * Intend to implement.
	 */
	protected void updateInput() {
	}
	
	/**
	 * Reveal the given element in the editor if possible.
	 *
	 * @param element the element to reveal
	 * @return true if the element could be revealed
	 */
	protected abstract boolean reveal(Object element);
	
	/**
	 * Open the element in a new editor if possible.
	 *
	 * @param element the element to open
	 * @return true if the element could be opened
	 */
	protected abstract boolean open(Object element);
	
}
