/*******************************************************************************
 * Copyright (c) 2007-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.workbench;

import static org.eclipse.ui.IWorkbenchCommandConstants.NAVIGATE_COLLAPSE_ALL;
import static org.eclipse.ui.IWorkbenchCommandConstants.NAVIGATE_EXPAND_ALL;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.walware.ecommons.FastList;
import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.actions.CollapseAllHandler;
import de.walware.ecommons.ui.actions.ExpandAllHandler;
import de.walware.ecommons.ui.actions.HandlerCollection;
import de.walware.ecommons.ui.actions.HandlerContributionItem;
import de.walware.ecommons.ui.util.UIAccess;


/**
 * Abstract content outline page.
 */
public abstract class AbstractEditorOutlinePage extends Page
		implements IContentOutlinePage, IAdaptable, IPostSelectionProvider {
	
	
	private static class SelectionChangeNotify extends SafeRunnable implements ISelectionChangedListener {
		
		
		private final FastList<ISelectionChangedListener> fSelectionListeners;
		
		private SelectionChangedEvent fCurrentEvent;
		private ISelectionChangedListener fCurrentListener;
		
		
		public SelectionChangeNotify(final FastList<ISelectionChangedListener> listenerList) {
			fSelectionListeners = listenerList;
		}
		
		
		@Override
		public void selectionChanged(final SelectionChangedEvent event) {
			fCurrentEvent = event;
			final ISelectionChangedListener[] listeners = fSelectionListeners.toArray();
			for (int i = 0; i < listeners.length; i++) {
				fCurrentListener = listeners[i];
				SafeRunner.run(this);
			}
		}
		
		@Override
		public void run() {
			fCurrentListener.selectionChanged(fCurrentEvent);
		}
		
	}
	
	private class DefaultSelectionListener implements ISelectionChangedListener {
		
		@Override
		public void selectionChanged(final SelectionChangedEvent event) {
			if (fIgnoreSelection == 0) {
				selectInEditor(event.getSelection());
			}
		}
		
	}
	
	protected abstract class AbstractToggleHandler extends AbstractHandler implements IElementUpdater {
		
		private final String fSettingsKey;
		private final String fCommandId;
		private final int fTime;
		
		private boolean fIsChecked;
		
		public AbstractToggleHandler(final String checkSettingsKey, final boolean checkSettingsDefault, 
				final String commandId, final int expensive) {
			assert (checkSettingsKey != null);
			
			fSettingsKey = checkSettingsKey;
			fCommandId = commandId;
			fTime = expensive;
			
			final IDialogSettings settings = getDialogSettings();
			final boolean on = (settings.get(fSettingsKey) == null) ?
					checkSettingsDefault : getDialogSettings().getBoolean(fSettingsKey);
			fIsChecked = on;
			apply(on);
		}
		
		protected void init() {
		}
		
		@Override
		public void updateElement(final UIElement element, final Map parameters) {
			element.setChecked(isChecked());
		}
		
		public boolean isChecked() {
			return fIsChecked;
		}
		
		@Override
		public Object execute(final ExecutionEvent event) throws ExecutionException {
			final boolean on = fIsChecked = !fIsChecked;
			final Runnable runnable = new Runnable() {
				@Override
				public void run() {
					apply(on);
					getDialogSettings().put(fSettingsKey, on); 
				}
			};
			if (fTime == 0) {
				runnable.run();
			}
			else {
				BusyIndicator.showWhile(Display.getCurrent(), runnable);
			}
			final ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
			if (fCommandId != null && commandService != null) {
				commandService.refreshElements(fCommandId, null);
			}
			return null;
		}
		
		protected abstract void apply(boolean on);
		
	}
	
	
	private TreeViewer fTreeViewer;
	private ISelection fCurrentSelection;
	
	private final FastList<ISelectionChangedListener> fSelectionListeners = new FastList<ISelectionChangedListener>(ISelectionChangedListener.class);
	private final ISelectionChangedListener fSelectionListener = new SelectionChangeNotify(fSelectionListeners);
	private final FastList<ISelectionChangedListener> fPostSelectionListeners = new FastList<ISelectionChangedListener>(ISelectionChangedListener.class);
	private final ISelectionChangedListener fPostSelectionListener = new SelectionChangeNotify(fPostSelectionListeners);
	private int fIgnoreSelection;
	
	private final String fContextMenuID;
	private Menu fContextMenu;
	
	private final HandlerCollection fHandlers = new HandlerCollection();
	private final FastList<IHandler2> fHandlersToUpdate = new FastList<IHandler2>(IHandler2.class);
	
	
	public AbstractEditorOutlinePage(final String contextMenuId) {
		fContextMenuID = contextMenuId;
	}
	
	
	@Override
	public void init(final IPageSite pageSite) {
		super.init(pageSite);
		pageSite.setSelectionProvider(this);
	}
	
	protected abstract IDialogSettings getDialogSettings();
	
	@Override
	public void createControl(final Composite parent) {
		final TreeViewer viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		viewer.setUseHashlookup(true);
		configureViewer(viewer);
		ColumnViewerToolTipSupport.enableFor(viewer);
		
		fTreeViewer = viewer;
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				fCurrentSelection = event.getSelection();
			}
		});
		final IPageSite site = getSite();
		initActions(site, fHandlers);
		fSelectionListeners.add(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				if (getControl().isVisible()) {
					final EvaluationContext evaluationContext = new EvaluationContext(null, event.getSelection());
					evaluationContext.addVariable(ISources.ACTIVE_SITE_NAME, site);
					evaluationContext.addVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME, event.getSelection());
					final IHandler2[] handlers = fHandlersToUpdate.toArray();
					for (final IHandler2 handler : handlers) {
						handler.setEnabled(evaluationContext);
					}
				}
			}
		});
		
		contributeToActionBars(site, site.getActionBars(), fHandlers);
		hookContextMenu();
		
		init();
	}
	
	private void hookContextMenu() {
		final MenuManager menuManager = new MenuManager(fContextMenuID, fContextMenuID);
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(final IMenuManager m) {
				contextMenuAboutToShow(m);
			}
		});
		fContextMenu = menuManager.createContextMenu(fTreeViewer.getTree());
		fTreeViewer.getTree().setMenu(fContextMenu);
		getSite().registerContextMenu(fContextMenuID, menuManager, fTreeViewer);
	}
	
	@Override
	public Control getControl() {
		if (fTreeViewer != null) {
			return fTreeViewer.getControl();
		}
		return null;
	}
	
	@Override
	public void setFocus() {
		final TreeViewer viewer = getViewer();
		if (UIAccess.isOkToUse(viewer)) {
			viewer.getTree().setFocus();
		}
	}
	
	protected abstract void configureViewer(TreeViewer viewer);
	
	protected void init() {
	}
	
	protected void beginIgnoreSelection() {
		fIgnoreSelection++;
	}
	
	protected void endIgnoreSelection(final boolean async) {
		if (async) {
			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run() {
					fIgnoreSelection--;
				};
			});
		}
		else {
			fIgnoreSelection--;
		}
	}
	
	protected void initActions(final IServiceLocator serviceLocator, final HandlerCollection handlers) {
		final TreeViewer viewer = getViewer();
		fPostSelectionListeners.add(new DefaultSelectionListener());
		viewer.addSelectionChangedListener(fSelectionListener);
		viewer.addPostSelectionChangedListener(fPostSelectionListener);
		
		final IHandlerService handlerService = (IHandlerService) serviceLocator.getService(IHandlerService.class);
		
		{	final CollapseAllHandler handler = new CollapseAllHandler(getViewer()) {
				@Override
				public Object execute(final ExecutionEvent event) {
					final TreeViewer viewer = getViewer();
					if (UIAccess.isOkToUse(viewer)) {
						beginIgnoreSelection();
						try {
							return super.execute(event);
						}
						finally {
							endIgnoreSelection(true);
						}
					}
					return null;
				}
			};
			handlers.add(NAVIGATE_COLLAPSE_ALL, handler);
			handlerService.activateHandler(NAVIGATE_COLLAPSE_ALL, handler);
		}
		{	final ExpandAllHandler handler = new ExpandAllHandler(getViewer());
			handlers.add(NAVIGATE_EXPAND_ALL, handler);
			handlerService.activateHandler(NAVIGATE_EXPAND_ALL, handler);
		}
	}
	
	protected void contributeToActionBars(final IServiceLocator serviceLocator,
			final IActionBars actionBars, final HandlerCollection handlers) {
		final IToolBarManager toolBarManager = actionBars.getToolBarManager();
		
		toolBarManager.add(new Separator(SharedUIResources.VIEW_EXPAND_MENU_ID)); 
		toolBarManager.appendToGroup(SharedUIResources.VIEW_EXPAND_MENU_ID,
				new HandlerContributionItem(new CommandContributionItemParameter(
						serviceLocator, null, NAVIGATE_COLLAPSE_ALL, HandlerContributionItem.STYLE_PUSH),
						handlers.get(NAVIGATE_COLLAPSE_ALL) ));
		toolBarManager.add(new Separator(SharedUIResources.VIEW_SORT_MENU_ID)); 
		final Separator viewFilter = new Separator(SharedUIResources.VIEW_FILTER_MENU_ID); 
		viewFilter.setVisible(false);
		toolBarManager.add(viewFilter);
	}
	
	protected void contextMenuAboutToShow(final IMenuManager m) {
	}
	
	
	@Override
	public void dispose() {
		fHandlers.dispose();
		fHandlersToUpdate.clear();
		fPostSelectionListeners.clear();
		
		if (fContextMenu != null && !fContextMenu.isDisposed()) {
			fContextMenu.dispose();
			fContextMenu = null;
		}
	}
	
	
	protected TreeViewer getViewer() {
		return fTreeViewer;
	}
	
	protected abstract void selectInEditor(final ISelection selection);
	
	protected void registerHandlerToUpdate(final IHandler2 handler) {
		fHandlersToUpdate.add(handler);
	}
	
	
	@Override
	public void setSelection(final ISelection selection) {
		final TreeViewer viewer = getViewer();
		if (UIAccess.isOkToUse(viewer)) {
			viewer.setSelection(selection);
		}
	}
	
	@Override
	public ISelection getSelection() {
		final ISelection selection = fCurrentSelection;
		if (selection != null) {
			return selection;
		}
		if (fTreeViewer != null) {
			return fTreeViewer.getSelection();
		}
		return null;
	}
	
	
	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		fSelectionListeners.add(listener);
	}
	
	@Override
	public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
		fSelectionListeners.remove(listener);
	}
	
	@Override
	public void addPostSelectionChangedListener(final ISelectionChangedListener listener) {
		fPostSelectionListeners.add(listener);
	}
	
	@Override
	public void removePostSelectionChangedListener(final ISelectionChangedListener listener) {
		fPostSelectionListeners.remove(listener);
	}
	
	
	@Override
	public Object getAdapter(final Class required) {
		return null;
	}
	
}
