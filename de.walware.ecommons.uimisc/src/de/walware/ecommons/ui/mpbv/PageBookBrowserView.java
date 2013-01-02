/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
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
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.services.IServiceLocator;

import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.actions.HandlerCollection;
import de.walware.ecommons.ui.actions.HandlerContributionItem;
import de.walware.ecommons.ui.actions.SimpleContributionItem;
import de.walware.ecommons.ui.mpbv.BrowserHandler.CancelHandler;
import de.walware.ecommons.ui.mpbv.BrowserHandler.IBrowserProvider;
import de.walware.ecommons.ui.mpbv.BrowserHandler.NavigateBackHandler;
import de.walware.ecommons.ui.mpbv.BrowserHandler.NavigateForwardHandler;
import de.walware.ecommons.ui.mpbv.BrowserHandler.OpenExternalHandler;
import de.walware.ecommons.ui.util.UIAccess;


public class PageBookBrowserView extends ManagedPageBookView<BrowserSession> {
	
	
	private static final String BROWSERCONTROL_MENU_ID = "browser_control"; //$NON-NLS-1$
	private static final String BOOKMARKS_MENU_ID = "bookmarks"; //$NON-NLS-1$
	
	/** Action id (local) to open current page in external browser */
	protected static final String OPEN_EXTERNAL_ID = ".OpenExternal"; //$NON-NLS-1$
	/** Action id (local) to open create a bookmark */
	protected static final String CREATE_BOOKMARK_ID = ".CreateBookmark"; //$NON-NLS-1$
	/** Action id (command) to navigate one page back. */
	protected static final String NAVIGATE_BACK_ID = IWorkbenchCommandConstants.NAVIGATE_BACK;
	/** Action id (command) to navigate one page forward. */
	protected static final String NAVIGATE_FORWARD_ID = IWorkbenchCommandConstants.NAVIGATE_FORWARD;
	/** Action id (command) to go to home page. */
	protected static final String GOTO_HOME_ID = "de.walware.ecommons.base.commands.GoToHome";
	/** Action id (command) to refresh the current page. */
	protected static final String REFRESH_PAGE_ID = IWorkbenchCommandConstants.FILE_REFRESH;
	/** Action id (command) to print the current page. */
	protected static final String PRINT_PAGE_ID = IWorkbenchCommandConstants.FILE_REFRESH;
	
	
	private class GoToHomeHandler extends AbstractHandler {
		
		
		@Override
		public void setEnabled(final Object evaluationContext) {
		}
		
		@Override
		public Object execute(final ExecutionEvent event) throws ExecutionException {
			openUrl(getHomePageUrl(), getCurrentSession());
			return null;
		}
		
	}
	
	private class RefreshHandler extends AbstractHandler {
		
		
		@Override
		public void setEnabled(final Object evaluationContext) {
			setBaseEnabled(fCurrentBrowserPage != null);
		}
		
		@Override
		public Object execute(final ExecutionEvent event) throws ExecutionException {
			if (fCurrentBrowserPage != null) {
				final Browser browser = fCurrentBrowserPage.getBrowser();
				browser.refresh();
			}
			return null;
		}
		
	}
	
	private class PrintHandler extends AbstractHandler {
		
		
		@Override
		public void setEnabled(final Object evaluationContext) {
			setBaseEnabled(fCurrentBrowserPage != null);
		}
		
		@Override
		public Object execute(final ExecutionEvent event) throws ExecutionException {
			if (fCurrentBrowserPage != null) {
				final Browser browser = fCurrentBrowserPage.getBrowser();
				browser.execute("window.print();" );
			}
			return null;
		}
		
	}
	
	private class CreateBookmarkHandler extends AbstractHandler {
		
		
		@Override
		public Object execute(final ExecutionEvent event) throws ExecutionException {
			BrowserBookmark bookmark = createBookmark();
			final EditBookmarkDialog dialog = new EditBookmarkDialog(getSite().getShell(), bookmark);
			if (dialog.open() == Dialog.OK) {
				bookmark = dialog.getBookmark();
				if (bookmark != null) {
					fBookmarks.getBookmarks().add(bookmark);
					return bookmark;
				}
			}
			return null;
		}
		
	}
	
	
	private class BrowserListener implements ProgressListener, TitleListener, StatusTextListener {
		
		@Override
		public void changed(final ProgressEvent event) {
			if (fCurrentBrowserPage != null && fCurrentBrowserPage.getBrowser() == event.widget) {
				if (event.total == 0 || event.total == event.current) {
					clearProgress();
				}
				else if (fCurrentProgress == null) {
					initProgress(event.total, event.current);
				}
				else {
					fCurrentProgress.worked(event.current - fCurrentProgressWorked);
					fCurrentProgressWorked = event.current;
				}
				
				updateBrowserState();
			}
		}
		
		@Override
		public void completed(final ProgressEvent event) {
			if (fCurrentBrowserPage != null && fCurrentBrowserPage.getBrowser() == event.widget) {
				clearProgress();
				updateBrowserState();
			}
		}
		
		@Override
		public void changed(final TitleEvent event) {
			if (fCurrentBrowserPage != null && fCurrentBrowserPage.getBrowser() == event.widget) {
				updateTitle();
			}
		}
		
		@Override
		public void changed(final StatusTextEvent event) {
			setStatus(event.text);
			updateTitle();
		}
		
	}
	
	
	private final List<IHandler2> fBrowserHandlers = new ArrayList<IHandler2>();
	
	private final BrowserListener fBrowserListener = new BrowserListener();
	
	private PageBookBrowserPage fCurrentBrowserPage;
	
	private IProgressMonitor fCurrentProgress;
	private int fCurrentProgressWorked;
	
	private int fStatusCounter;
	private Image fStatusImage;
	private String fStatusMessage;
	
	private BookmarkCollection fBookmarks;
	
	private final IBrowserProvider fBrowserInterface = new IBrowserProvider() {
		@Override
		public Browser getBrowser() {
			if (fCurrentBrowserPage != null) {
				return fCurrentBrowserPage.getBrowser();
			}
			return null;
		}
		@Override
		public void showMessage(final int severity, final String message) {
			setTemporaryStatus(severity, message);
		}
	};
	
	
	public PageBookBrowserView() {
	}
	
	
	@Override
	public void init(final IViewSite site, final IMemento memento) throws PartInitException {
		super.init(site, memento);
		fBookmarks = initBookmarkCollection();
	}
	
	protected BookmarkCollection initBookmarkCollection() {
		return null;
	}
	
	@Override
	public void saveState(final IMemento memento) {
		fBookmarks.save();
		super.saveState(memento);
	}
	
	@Override
	protected PageBookBrowserPage doCreatePage(final BrowserSession session) {
		return new PageBookBrowserPage(this, session);
	}
	
	protected IBrowserProvider getBrowserInterface() {
		return fBrowserInterface;
	}
	
	@Override
	protected void initActions(final IServiceLocator serviceLocator, final HandlerCollection handlers) {
		super.initActions(serviceLocator, handlers);
		final IContextService contextService = (IContextService) serviceLocator.getService(IContextService.class);
		final IHandlerService handlerService = (IHandlerService) serviceLocator.getService(IHandlerService.class);
		
		contextService.activateContext("de.walware.ecommons.base.contexts.PageViewerContext"); //$NON-NLS-1$
		
		{	final IHandler2 handler = new NavigateBackHandler(getBrowserInterface());
			handlers.add(NAVIGATE_BACK_ID, handler);
			addBrowserHandler(handler);
			handlerService.activateHandler(NAVIGATE_BACK_ID, handler);
			handlerService.activateHandler(IWorkbenchCommandConstants.NAVIGATE_BACKWARD_HISTORY, handler);
		}
		{	final IHandler2 handler = new NavigateForwardHandler(getBrowserInterface());
			handlers.add(NAVIGATE_FORWARD_ID, handler);
			addBrowserHandler(handler);
			handlerService.activateHandler(NAVIGATE_FORWARD_ID, handler);
			handlerService.activateHandler(IWorkbenchCommandConstants.NAVIGATE_FORWARD_HISTORY, handler);
		}
		{	final IHandler2 handler = new GoToHomeHandler();
			handlers.add(GOTO_HOME_ID, handler);
			addBrowserHandler(handler);
			handlerService.activateHandler(GOTO_HOME_ID, handler);
		}
		{	final IHandler2 handler = new RefreshHandler();
			handlers.add(REFRESH_PAGE_ID, handler);
			addBrowserHandler(handler);
			handlerService.activateHandler(IWorkbenchCommandConstants.FILE_REFRESH, handler);
		}
		final IHandler2 cancelHandler = new CancelHandler(getBrowserInterface());
//		handlerService.activateHandler(, cancelHandler);
		addBrowserHandler(cancelHandler);
		{	final IHandler2 handler = new PrintHandler();
			handlers.add(PRINT_PAGE_ID, handler);
			handlerService.activateHandler(IWorkbenchCommandConstants.FILE_PRINT, handler);
		}
		{	final IHandler2 handler = new CreateBookmarkHandler();
			handlers.add(CREATE_BOOKMARK_ID, handler); 
			addBrowserHandler(handler);
		}
		{	final IHandler2 handler = new OpenExternalHandler(getBrowserInterface());
			handlers.add(OPEN_EXTERNAL_ID, handler); 
			addBrowserHandler(handler);
		}
	}
	
	@Override
	protected void contributeToActionBars(final IServiceLocator serviceLocator,
			final IActionBars actionBars, final HandlerCollection handlers) {
		super.contributeToActionBars(serviceLocator, actionBars, handlers);
		
		final IMenuManager menuManager = actionBars.getMenuManager();
		menuManager.add(new HandlerContributionItem(new CommandContributionItemParameter(
						serviceLocator, null, HandlerContributionItem.NO_COMMAND_ID, null,
						null, null, null,
						"Open in &external browser", null, null,
						HandlerContributionItem.STYLE_PUSH, null, false),
						handlers.get(OPEN_EXTERNAL_ID))); 
		menuManager.add(new Separator("settings")); //$NON-NLS-1$
		menuManager.appendToGroup("settings", //$NON-NLS-1$
				new SimpleContributionItem("Preferences...", "P") {
					@Override
					protected void execute() throws ExecutionException {
						final Shell shell = getSite().getShell();
						final List<String> pageIds = new ArrayList<String>();
						PageBookBrowserView.this.collectContextMenuPreferencePages(pageIds);
						if (!pageIds.isEmpty() && (shell == null || !shell.isDisposed())) {
							org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn(shell,
									pageIds.get(0), pageIds.toArray(new String[pageIds.size()]), null)
									.open();
						}
					}
				});
		
		final IToolBarManager toolBarManager = actionBars.getToolBarManager();
		toolBarManager.insertBefore(SharedUIResources.ADDITIONS_MENU_ID,
				new Separator(BROWSERCONTROL_MENU_ID));
		toolBarManager.appendToGroup(BROWSERCONTROL_MENU_ID,
				new HandlerContributionItem(new CommandContributionItemParameter(
						serviceLocator, null, NAVIGATE_BACK_ID, HandlerContributionItem.STYLE_PUSH),
						handlers.get(NAVIGATE_BACK_ID)));
		toolBarManager.appendToGroup(BROWSERCONTROL_MENU_ID, 
				new HandlerContributionItem(new CommandContributionItemParameter(
						serviceLocator, null, NAVIGATE_FORWARD_ID, HandlerContributionItem.STYLE_PUSH),
						handlers.get(NAVIGATE_FORWARD_ID)));
		toolBarManager.insertAfter(BROWSERCONTROL_MENU_ID,
				new Separator(BOOKMARKS_MENU_ID));
		toolBarManager.appendToGroup(BOOKMARKS_MENU_ID,
				new SimpleContributionItem(
						SharedUIResources.getImages().getDescriptor(SharedUIResources.LOCTOOL_FAVORITES_IMAGE_ID), null,
						"Manage Bookmarks", null,
						SimpleContributionItem.STYLE_PULLDOWN) {
					@Override
					protected void execute() throws ExecutionException {
						final ManageBookmarksDialog dialog = new ManageBookmarksDialog(PageBookBrowserView.this);
						dialog.open();
						fBookmarks.save();
					}
					@Override
					protected void dropDownMenuAboutToShow(final IMenuManager manager) {
						manager.add(new HandlerContributionItem(new CommandContributionItemParameter(
								serviceLocator, null, HandlerContributionItem.NO_COMMAND_ID, null,
								null, null, null,
								"Create Bookmark", "C", null,
								HandlerContributionItem.STYLE_PUSH, null, false),
								handlers.get(CREATE_BOOKMARK_ID)));
						manager.add(new Separator());
						manager.add(new ShowBookmarksDropdownContribution.OpenBookmarkContributionItem(
								PageBookBrowserView.this, new BrowserBookmark("Home Page", getHomePageUrl()),
								null, "H"));
						manager.add(new Separator());
						manager.add(new ShowBookmarksDropdownContribution(PageBookBrowserView.this));
					}
				});
	}
	
	protected void addBrowserHandler(final IHandler2 handler) {
		fBrowserHandlers.add(handler);
	}
	
	@Override
	public void dispose() {
		fBrowserHandlers.clear();
		super.dispose();
	}
	
	@Override
	protected void onPageHiding(final IPageBookViewPage page, final BrowserSession session) {
		final PageBookBrowserPage browserPage;
		if (session != null) {
			browserPage = (PageBookBrowserPage) page;
			final Browser browser = browserPage.getBrowser();
			browser.removeProgressListener(fBrowserListener);
			browser.removeTitleListener(fBrowserListener);
			browser.removeStatusTextListener(fBrowserListener);
			
			clearProgress();
		}
		else {
			browserPage = null;
		}
		fCurrentBrowserPage = null;
		setStatus(""); //$NON-NLS-1$
		
		super.onPageHiding(page, session);
	}
	
	@Override
	protected void onPageShowing(final IPageBookViewPage page, final BrowserSession session) {
		if (session != null) {
			fCurrentBrowserPage = (PageBookBrowserPage) page;
			final Browser browser = fCurrentBrowserPage.getBrowser();
			browser.addProgressListener(fBrowserListener);
			browser.addTitleListener(fBrowserListener);
			browser.addStatusTextListener(fBrowserListener);
			
			initProgress(fCurrentBrowserPage.getCurrentProgressTotal(), fCurrentBrowserPage.getCurrentProgressWorked());
			setStatus(fCurrentBrowserPage.getCurrentStatusText());
		}
		
		super.onPageShowing(page, session);
	}
	
	protected void updateBrowserState() {
		for (final IHandler2 handler : fBrowserHandlers) {
			handler.setEnabled(null);
		}
	}
	
	private void setStatus(final String text) {
		fStatusCounter++;
		fStatusImage = null;
		fStatusMessage = (text != null && text.length() > 0) ? text : null;
		
		final IStatusLineManager statusLine = getViewSite().getActionBars().getStatusLineManager();
		statusLine.setErrorMessage(null);
		statusLine.setMessage(fStatusImage, fStatusMessage);
	}
	
	void setTemporaryStatus(final int severity, String message) {
		final IStatusLineManager statusLine = getViewSite().getActionBars().getStatusLineManager();
		if (statusLine == null) {
			return;
		}
		
		boolean error = false;
		Image image;
		if (message != null && message.length() > 0) {
			switch (severity) {
			case IStatus.INFO:
				image = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO);
				break;
			case IStatus.WARNING:
				image = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);
				break;
			case IStatus.ERROR:
				image = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
				error = true;
				break;
			default:
				image = null;
			}
		}
		else {
			image = null;
			message = null;
		}
		
		final int id = ++fStatusCounter;
		if (error) {
			statusLine.setErrorMessage(image, message);
			Display.getCurrent().timerExec(5000, new Runnable() {
				@Override
				public void run() {
					if (fStatusCounter == id && UIAccess.isOkToUse(getPageBook())) {
						statusLine.setErrorMessage(null);
						statusLine.setMessage(fStatusImage, fStatusMessage);
					}
				}
			});
		}
		else {
			statusLine.setMessage(image, message);
			Display.getCurrent().timerExec(5000, new Runnable() {
				@Override
				public void run() {
					if (fStatusCounter == id &&  UIAccess.isOkToUse(getPageBook())) {
						statusLine.setErrorMessage(null);
						statusLine.setMessage(fStatusImage, fStatusMessage);
					}
				}
			});
		}
	}
	
	private void initProgress(final int total, final int worked) {
		if (worked < 0 || worked >= total) { // includes total == 0
			return;
		}
		final IStatusLineManager statusLine = getViewSite().getActionBars().getStatusLineManager();
		statusLine.setCancelEnabled(true);
		final IProgressMonitor monitor = statusLine.getProgressMonitor();
		fCurrentProgress = monitor;
		fCurrentProgress.beginTask("", total); //$NON-NLS-1$
		fCurrentProgressWorked = worked;
		fCurrentProgress.worked(worked);
		
		Display.getCurrent().timerExec(200, new Runnable() {
			@Override
			public void run() {
				if (monitor == fCurrentProgress) {
					if (monitor.isCanceled()) {
						fCurrentBrowserPage.getBrowser().stop();
					}
					else {
						Display.getCurrent().timerExec(100, this);
					}
				}
			}
		});
	}
	
	private void clearProgress() {
		if (fCurrentProgress != null) {
			fCurrentProgress.done();
			fCurrentProgress = null;
			fCurrentProgressWorked = 0;
		}
	}
	
	@Override
	protected BrowserSession checkNewSession(BrowserSession session) {
		if (session == null) {
			session = new BrowserSession();
			session.fUrl = getHomePageUrl();
		}
		if (session.fBound) {
			throw new IllegalArgumentException("Session is already bound");
		}
		session.fBound = true;
		return session;
	}
	
	@Override
	public void closePage(final BrowserSession session) {
		super.closePage(session);
		session.fBound = false;
	}
	
	public boolean canOpen(final BrowserSession session) {
		return (!session.fBound || getSessions().contains(session));
	}
	
	public BrowserSession openBookmark(final BrowserBookmark bookmark, final BrowserSession session) {
		return openUrl(bookmark.getUrl(), session);
	}
	
	public BrowserSession openUrl(final String url, BrowserSession session) {
		if (session != null) {
			final PageBookBrowserPage page = (PageBookBrowserPage) getPage(session);
			if (page != null) {
				page.setUrl(url);
				showPage(session);
				if (getViewSite().getPage().getActivePart() == this) {
					page.setFocusToBrowser();
				}
				return session;
			}
		}
		if (session == null) {
			session = new BrowserSession();
		}
		session.fUrl = url;
		newPage(session, true);
		return session;
	}
	
	public BrowserSession findBrowserSession(final String url) {
		final List<BrowserSession> sessions = getSessions();
		for (final BrowserSession session : sessions) {
			if (url.equals(session.getUrl())) {
				return session;
			}
		}
		return null;
	}
	
	protected PageBookBrowserPage getCurrentBrowserPage() {
		return fCurrentBrowserPage;
	}
	
	public String getHomePageUrl() {
		return "about:blank"; //$NON-NLS-1$
	}
	
	protected List<BrowserBookmark> getBookmarks() {
		return fBookmarks.getBookmarks();
	}
	
	protected BrowserBookmark createBookmark() {
		return new BrowserBookmark(fCurrentBrowserPage.getCurrentTitle(), fCurrentBrowserPage.getCurrentUrl());
	}
	
	protected void collectContextMenuPreferencePages(final List<String> pageIds) {
		pageIds.add("de.walware.statet.nico.preferencePages.Console"); //$NON-NLS-1$
	}
	
}
