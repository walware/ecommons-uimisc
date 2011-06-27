/*******************************************************************************
 * Copyright (c) 2009-2011 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.mpbv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.part.PageSwitcher;
import org.eclipse.ui.services.IServiceLocator;

import de.walware.ecommons.ConstList;
import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.actions.HandlerCollection;
import de.walware.ecommons.ui.actions.HandlerContributionItem;
import de.walware.ecommons.ui.actions.SimpleContributionItem;


public abstract class ManagedPageBookView<S extends ISession> extends PageBookView {
	
	
	protected static final String PAGE_CONTROL_MENU_ID = "page_control"; //$NON-NLS-1$
	
	
	private class SessionHandler implements IWorkbenchPart {
		
		
		private final S fSession;
		
		
		public SessionHandler(final S session) {
			fSession = session;
		}
		
		
		ManagedPageBookView<S> getView() {
			return ManagedPageBookView.this;
		}
		
		public IWorkbenchPartSite getSite() {
			return null;
		}
		
		public S getSession() {
			return fSession;
		}
		
		
		public String getTitle() {
			return ""; //$NON-NLS-1$
		}
		
		public Image getTitleImage() {
			return null;
		}
		
		public String getTitleToolTip() {
			return ""; //$NON-NLS-1$
		}
		
		public void addPropertyListener(final IPropertyListener listener) {
		}
		
		public void removePropertyListener(final IPropertyListener listener) {
		}
		
		public void createPartControl(final Composite parent) {
		}
		
		public void setFocus() {
		}
		
		public void dispose() {
		}
		
		public Object getAdapter(final Class adapter) {
			return null;
		}
		
		@Override
		public int hashCode() {
			return fSession.hashCode();
		}
		
		@Override
		public boolean equals(final Object obj) {
			return ( (obj instanceof ManagedPageBookView<?>.SessionHandler)
					&& (fSession == ((SessionHandler) obj).fSession) );
		}
		
	}
	
	private class NewPageHandler extends AbstractHandler {
		
		public NewPageHandler() {
		}
		
		@Override
		public void setEnabled(final Object evaluationContext) {
		}
		
		public Object execute(final ExecutionEvent event) throws ExecutionException {
			newPage(null, true);
			return null;
		}
		
	}
	
	private class CloseCurrentPageHandler extends AbstractHandler {
		
		public CloseCurrentPageHandler() {
		}
		
		@Override
		public void setEnabled(final Object evaluationContext) {
			setBaseEnabled(!fSessionList.isEmpty());
		}
		
		public Object execute(final ExecutionEvent event) throws ExecutionException {
			final S session = getCurrentSession();
			if (session != null) {
				closePage(session);
			}
			return null;
		}
		
	}
	
	public class CloseAllPagesHandler extends AbstractHandler {
		
		
		@Override
		public void setEnabled(final Object evaluationContext) {
			setBaseEnabled(!fSessionList.isEmpty());
		}
		
		public Object execute(final ExecutionEvent event) throws ExecutionException {
			fSessionHistory.clear();
			final List<S> sessions = getSessions();
			for (final S session : sessions) {
				closePage(session);
			}
			return null;
		}
		
	}
	
	
	private final List<S> fSessionList = new ArrayList<S>();
	private final Map<S, SessionHandler> fSessionMap = new HashMap<S, SessionHandler>();
	private Comparator<S> fSessionComparator;
	
	private final List<S> fSessionHistory = new LinkedList<S>();
	
	private SessionHandler fActiveSession;
	
	private final HandlerCollection fViewHandlers = new HandlerCollection();
	
	
	public ManagedPageBookView() {
	}
	
	
	protected void setSessionComparator(final Comparator<S> comparator) {
		fSessionComparator = comparator;
	}
	
	@Override
	protected boolean isImportant(final IWorkbenchPart part) {
		return ( (part instanceof ManagedPageBookView<?>.SessionHandler)
				&& ((ManagedPageBookView<?>.SessionHandler) part).getView() == this );
	}
	
	@Override
	protected IWorkbenchPart getBootstrapPart() {
		return null;
	}
	
	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);
		
		final IViewSite site = getViewSite();
		initActions(site, fViewHandlers);
		initPageSwitcher();
		contributeToActionBars(site, site.getActionBars(), fViewHandlers);
		
		updateState();
	}
	
	@Override
	protected IPage createDefaultPage(final PageBook book) {
		final MessagePage page = new MessagePage();
		page.createControl(getPageBook());
		initPage(page);
		return page;
	}
	
	@Override
	protected PageRec doCreatePage(final IWorkbenchPart part) {
		final SessionHandler sessionHandler = (SessionHandler) part;
		final S session = sessionHandler.getSession();
		
		final IPageBookViewPage page = doCreatePage(session);
		if (page != null) {
			initPage(page);
			page.createControl(getPageBook());
			
			final PageRec pageRecord = new PageRec(part, page);
			return pageRecord;
		}
		return null;
	}
	
	protected /* abstract */ IPageBookViewPage doCreatePage(final S session) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	protected void showPageRec(final PageRec pageRec) {
		if ((fActiveSession != null) ? (pageRec.part != fActiveSession) : (pageRec.part != null)) {
			onPageHiding((IPageBookViewPage) getCurrentPage(), (fActiveSession != null) ? fActiveSession.getSession() : null);
			fActiveSession = null;
			
			super.showPageRec(pageRec);
			
			fActiveSession = (SessionHandler) pageRec.part;
			
			final S session;
			if (fActiveSession != null) {
				session = fActiveSession.getSession();
				fSessionHistory.remove(session);
				fSessionHistory.add(0, session);
			}
			else {
				session = null;
			}
			onPageShowing((IPageBookViewPage) pageRec.page, session);
		}
		updateTitle();
	}
	
	protected void updateTitle() {
		final S session = getCurrentSession();
		if (session == null) {
			setContentDescription(getNoPageTitle());
		}
		else {
			setContentDescription(session.getLabel());
		}
	}
	
	protected String getNoPageTitle() {
		return "No page at this time.";
	}
	
	@Override
	public void partClosed(final IWorkbenchPart part) {
		if (part instanceof ManagedPageBookView<?>.SessionHandler) {
			final SessionHandler sessionHandler = (SessionHandler) part;
			final S session = sessionHandler.getSession();
			
			fSessionList.remove(session);
			fSessionHistory.remove(session);
			
			if (fActiveSession == part && !fSessionHistory.isEmpty()) {
				showPage(fSessionHistory.get(0));
			}
			super.partClosed(part);
		}
	}
	
	@Override
	protected void doDestroyPage(final IWorkbenchPart part, final PageRec pageRecord) {
		final SessionHandler sessionHandler = (SessionHandler) part;
		final S session = sessionHandler.getSession();
		
		pageRecord.page.dispose();
		pageRecord.dispose();
		
		fSessionMap.remove(session);
		if (sessionHandler == fActiveSession) {
			fActiveSession = null;
		}
	}
	
	
	private void initPageSwitcher() {
		new PageSwitcher(this) {
			@Override
			public Object[] getPages() {
				return fSessionList.toArray();
			}
			@Override
			public String getName(final Object page) {
				return ((S) page).getLabel();
			}
			@Override
			public ImageDescriptor getImageDescriptor(final Object page) {
				return ((S) page).getImageDescriptor();
			}
			@Override
			public int getCurrentPageIndex() {
				return fSessionList.indexOf(fActiveSession);
			}
			@Override
			public void activatePage(final Object page) {
				showPage((S) page);
			}
		};
	}
	
	
	protected void initActions(final IServiceLocator serviceLocator, final HandlerCollection handlers) {
		final IHandlerService handlerService = (IHandlerService) serviceLocator.getService(IHandlerService.class);
		
		final IHandler2 newPageHandler = createNewPageHandler();
		if (newPageHandler != null) {
			handlers.add(SharedUIResources.NEW_PAGE_COMMAND_ID, newPageHandler);
			handlerService.activateHandler(SharedUIResources.NEW_PAGE_COMMAND_ID, newPageHandler);
		}
		final IHandler2 closePageHandler = new CloseCurrentPageHandler();
		handlers.add(SharedUIResources.CLOSE_PAGE_COMMAND_ID, closePageHandler);
		handlerService.activateHandler(SharedUIResources.CLOSE_PAGE_COMMAND_ID, closePageHandler);
		final IHandler2 closeAllPagesHandler = new CloseAllPagesHandler();
		handlers.add(SharedUIResources.CLOSE_ALL_PAGES_COMMAND_ID, closeAllPagesHandler);
		handlerService.activateHandler(SharedUIResources.CLOSE_ALL_PAGES_COMMAND_ID, closeAllPagesHandler);
	}
	
	protected IHandler2 createNewPageHandler() {
		return new NewPageHandler();
	}
	
	protected void contributeToActionBars(final IServiceLocator serviceLocator,
			final IActionBars actionBars, final HandlerCollection handlers) {
		final IToolBarManager toolBarManager = actionBars.getToolBarManager();
		
		toolBarManager.add(new Separator(SharedUIResources.ADDITIONS_MENU_ID));
		toolBarManager.add(new Separator(PAGE_CONTROL_MENU_ID));
		{	final IHandler2 handler = handlers.get(SharedUIResources.NEW_PAGE_COMMAND_ID);
			if (handler != null) {
				toolBarManager.appendToGroup(PAGE_CONTROL_MENU_ID,
						new HandlerContributionItem(new CommandContributionItemParameter(
								serviceLocator, null, SharedUIResources.NEW_PAGE_COMMAND_ID, HandlerContributionItem.STYLE_PUSH),
								handler));
			}
		}
		toolBarManager.appendToGroup(PAGE_CONTROL_MENU_ID, 
				new SimpleContributionItem(
						SharedUIResources.getImages().getDescriptor(SharedUIResources.LOCTOOL_CHANGE_PAGE_IMAGE_ID), null,
						"Pages", null,
						SimpleContributionItem.STYLE_PULLDOWN) {
			@Override
			protected void dropDownMenuAboutToShow(final IMenuManager manager) {
				manager.add(new ShowPageDropdownContribution<S>(ManagedPageBookView.this));
			}
		});
		{	final IHandler2 handler = handlers.get(SharedUIResources.CLOSE_PAGE_COMMAND_ID);
			if (handler != null) {
				toolBarManager.appendToGroup(PAGE_CONTROL_MENU_ID,
						new HandlerContributionItem(new CommandContributionItemParameter(
								serviceLocator, null, SharedUIResources.CLOSE_PAGE_COMMAND_ID, HandlerContributionItem.STYLE_PUSH),
								handler));
			}
		}
		{	final IHandler2 handler = handlers.get(SharedUIResources.CLOSE_ALL_PAGES_COMMAND_ID);
			if (handler != null) {
				toolBarManager.appendToGroup(PAGE_CONTROL_MENU_ID,
						new HandlerContributionItem(new CommandContributionItemParameter(
								serviceLocator, null, SharedUIResources.CLOSE_ALL_PAGES_COMMAND_ID, HandlerContributionItem.STYLE_PUSH),
								handler));
			}
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		fViewHandlers.dispose();
	}
	
	
	public IPage newPage(S session, final boolean show) {
		session = checkNewSession(session);
		if (session == null || fSessionList.contains(session)) {
			return null;
		}
		final SessionHandler sessionHandler = new SessionHandler(session);
		if (fSessionComparator != null) {
			final int idx = Collections.binarySearch(fSessionList, session, fSessionComparator);
			fSessionList.add((idx >= 0) ? idx : -(idx+1), session);
		}
		else {
			fSessionList.add(session);
		}
		fSessionMap.put(session, sessionHandler);
		if (show) {
			partActivated(sessionHandler);
			final PageRec pageRec = getPageRec(sessionHandler);
			if (pageRec != null) {
				return pageRec.page;
			}
			else {
				fSessionMap.remove(sessionHandler);
				fSessionList.remove(session);
				return null;
			}
		}
		else {
			return null;
		}
	}
	
	protected S checkNewSession(final S session) {
		return session;
	}
	
	public IPage getPage(final S session) {
		final SessionHandler sessionHandler = fSessionMap.get(session);
		if (sessionHandler != null) {
			final PageRec pageRec = getPageRec(sessionHandler);
			if (pageRec != null) {
				return pageRec.page;
			}
		}
		return null;
	}
	
	public void showPage(final S session) {
		final SessionHandler sessionHandler = fSessionMap.get(session);
		if (sessionHandler != null) {
			partActivated(sessionHandler);
		}
	}
	
	public void closePage(final S session) {
		final SessionHandler sessionHandler = fSessionMap.get(session);
		if (sessionHandler != null) {
			partClosed(sessionHandler);
		}
	}
	
	public final List<S> getSessions() {
		return new ConstList<S>(fSessionList);
	}
	
	public final S getCurrentSession() {
		final SessionHandler sessionHandler = fActiveSession;
		if (sessionHandler != null) {
			return sessionHandler.getSession();
		}
		return null;
	}
	
	protected void onPageHiding(final IPageBookViewPage page, final S session) {
		updateState();
	}
	
	protected void onPageShowing(final IPageBookViewPage page, final S session) {
		updateState();
	}
	
	protected void updateState() {
		fViewHandlers.update(null);
	}
	
}
