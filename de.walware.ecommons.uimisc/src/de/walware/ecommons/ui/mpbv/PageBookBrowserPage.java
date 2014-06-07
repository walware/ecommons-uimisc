/*=============================================================================#
 # Copyright (c) 2009-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.mpbv;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.services.IServiceLocator;

import de.walware.ecommons.ui.SharedMessages;
import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.actions.HandlerCollection;
import de.walware.ecommons.ui.actions.HandlerContributionItem;
import de.walware.ecommons.ui.actions.SearchContributionItem;
import de.walware.ecommons.ui.actions.SimpleContributionItem;
import de.walware.ecommons.ui.components.StatusInfo;
import de.walware.ecommons.ui.util.BrowserUtil;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.StatusLineMessageManager;
import de.walware.ecommons.ui.util.UIAccess;


public class PageBookBrowserPage extends Page implements ProgressListener,
		LocationListener, TitleListener, StatusTextListener, OpenWindowListener, CloseWindowListener {
	
	
	protected class SearchBar implements DisposeListener {
		
		
		private ToolBarManager toolBarManager;
		private ToolBar toolBar;
		private SearchContributionItem searchTextItem;
		
		private boolean searchCaseSensitive;
		
		
		public SearchBar(final Composite parent) {
			create(parent);
		}
		
		
		private void create(final Composite parent) {
			this.toolBarManager= new ToolBarManager(SWT.FLAT);
			this.toolBar= this.toolBarManager.createControl(parent);
			this.toolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			this.toolBar.addDisposeListener(this);
			
			this.toolBarManager.add(new ContributionItem() {
				@Override
				public void fill(final ToolBar parent, final int index) {
					final ToolItem item= new ToolItem(parent, SWT.PUSH);
					item.setImage(SharedUIResources.getImages().get(SharedUIResources.LOCTOOL_CLOSETRAY_IMAGE_ID));
					item.setHotImage(SharedUIResources.getImages().get(SharedUIResources.LOCTOOL_CLOSETRAY_H_IMAGE_ID));
					item.setToolTipText("Close Search");
					item.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent e) {
							hide();
						}
					});
				}
			});
			
			this.toolBarManager.add(new Separator());
			
			this.searchTextItem= new SearchContributionItem("search.text", SWT.NONE) { //$NON-NLS-1$
				@Override
				public void fill(final ToolBar parent, final int index) {
					super.fill(parent, index);
					getSearchText().getTextControl().addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(final KeyEvent e) {
							if (e.keyCode == SWT.ESC && e.doit) {
								hide();
								e.doit= false;
								return;
							}
						}
					});
				}
				@Override
				protected void search() {
					PageBookBrowserPage.this.search(true);
				}
			};
			this.searchTextItem.setToolTip("Find Text");
			this.searchTextItem.setSizeControl(parent);
			this.toolBarManager.add(this.searchTextItem);
			
			final ImageRegistry ecommonsImages= SharedUIResources.getImages();
			final HandlerCollection pageHandlers= PageBookBrowserPage.this.pageHandlers;
			this.toolBarManager.add(new HandlerContributionItem(new CommandContributionItemParameter(
					getSite(), "search.next", SharedUIResources.FIND_NEXT_COMMAND_ID, null, //$NON-NLS-1$
					ecommonsImages.getDescriptor(SharedUIResources.LOCTOOL_DOWN_IMAGE_ID), null, ecommonsImages.getDescriptor(SharedUIResources.LOCTOOL_DOWN_H_IMAGE_ID),
					SharedMessages.FindNext_tooltip, null, null, SWT.PUSH, null, false), pageHandlers.get(SharedUIResources.FIND_NEXT_COMMAND_ID)));
			this.toolBarManager.add(new HandlerContributionItem(new CommandContributionItemParameter(
					getSite(), "search.previous", SharedUIResources.FIND_PREVIOUS_COMMAND_ID, null, //$NON-NLS-1$
					ecommonsImages.getDescriptor(SharedUIResources.LOCTOOL_UP_IMAGE_ID), null, ecommonsImages.getDescriptor(SharedUIResources.LOCTOOL_UP_H_IMAGE_ID),
					SharedMessages.FindPrevious_tooltip, null, null, SWT.PUSH, null, false), pageHandlers.get(SharedUIResources.FIND_PREVIOUS_COMMAND_ID)));
			
			this.toolBarManager.add(new Separator());
			
			final SimpleContributionItem caseItem= new SimpleContributionItem(new CommandContributionItemParameter(null, null, null, null,
					ecommonsImages.getDescriptor(SharedUIResources.LOCTOOL_CASESENSITIVE_IMAGE_ID), null, null,
					null, null, "Case Sensitive", SimpleContributionItem.STYLE_CHECK, null, false)) {
				@Override
				protected void execute() throws ExecutionException {
					SearchBar.this.searchCaseSensitive= !SearchBar.this.searchCaseSensitive;
					setChecked(SearchBar.this.searchCaseSensitive);
				}
			};
			caseItem.setChecked(this.searchCaseSensitive);
			this.toolBarManager.add(caseItem);
			
			this.toolBarManager.update(true);
		}
		
		@Override
		public void widgetDisposed(final DisposeEvent e) {
			if (this.toolBar != null) {
				this.toolBarManager.dispose();
				this.toolBarManager= null;
				
				this.toolBar= null;
			}
		}
		
		
		public void show() {
			final GridData gd= (GridData) this.toolBar.getLayoutData();
			gd.exclude= false;
			this.toolBar.getParent().layout(true, true);
			this.searchTextItem.getSearchText().setFocus();
		}
		
		public void hide() {
			setFocusToBrowser();
			final GridData gd= (GridData) this.toolBar.getLayoutData();
			gd.exclude= true;
			this.toolBar.getParent().layout(new Control[] { this.toolBar });
		}
		
		public String getText() {
			return this.searchTextItem.getText();
		}
		
		public boolean isCaseSensitiveEnabled() {
			return this.searchCaseSensitive;
		}
		
	}
	
	
	private final ManagedPageBookView view;
	
	private final BrowserSession session;
	
	private Composite composite;
	
	private Browser browser;
	
	private SearchBar searchBar;
	
	private final HandlerCollection pageHandlers= new HandlerCollection();
	
	private IStatus browserStatus;
	
	private int progressTotal;
	private int progressWorked;
	
	private StatusLineMessageManager statusManager;
	
	
	public PageBookBrowserPage(final ManagedPageBookView view,
			final BrowserSession session) {
		this.view= view;
		this.session= session;
	}
	
	
	@Override
	public void createControl(final Composite parent) {
		this.composite= new Composite(parent, SWT.NONE) {
			@Override
			public boolean setFocus() {
				return setDefaultFocus();
			}
		};
		this.composite.setLayout(LayoutUtil.applySashDefaults(new GridLayout(), 1));
		
		{	final Control control= createAddressBar(this.composite);
			if (control != null) {
				control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			}
		}
		final Control browser= createBrowser(this.composite);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		initActions(getSite(), this.pageHandlers);
		
		// check required for open in new window/page
		if (this.session.fUrl != null && this.session.fUrl.length() > 0) {
			setUrl(this.session.fUrl);
		}
	}
	
	private Control createBrowser(final Composite parent) {
		this.browser= new Browser(parent, SWT.NONE);
		
		this.browser.addProgressListener(this);
		this.browser.addLocationListener(this);
		this.browser.addTitleListener(this);
		this.browser.addStatusTextListener(this);
		this.browser.addOpenWindowListener(this);
		this.browser.addCloseWindowListener(this);
		
		return this.browser;
	}
	
	protected Control createAddressBar(final Composite parent) {
		return null;
	}
	
	void setStatusManager(final StatusLineMessageManager statusManager) {
		this.statusManager= statusManager;
		if (this.statusManager != null) {
			this.statusManager.setSelectionMessage(this.browserStatus);
		}
	}
	
	protected void initActions(final IServiceLocator serviceLocator, final HandlerCollection handlers) {
		final IHandlerService handlerService= (IHandlerService) serviceLocator.getService(IHandlerService.class);
		
		final String browserType= this.browser.getBrowserType();
		if (browserType.equals("mozilla") || browserType.equals("webkit")) { //$NON-NLS-1$ //$NON-NLS-2$
			{	final IHandler2 handler= new AbstractHandler() {
					@Override
					public Object execute(final ExecutionEvent event) throws ExecutionException {
						if (!UIAccess.isOkToUse(PageBookBrowserPage.this.browser)) {
							return null;
						}
						if (PageBookBrowserPage.this.searchBar == null) {
							PageBookBrowserPage.this.searchBar= new SearchBar(PageBookBrowserPage.this.composite);
						}
						PageBookBrowserPage.this.searchBar.show();
						return null;
					}
				};
				handlers.add(IWorkbenchCommandConstants.EDIT_FIND_AND_REPLACE, handler);
				handlerService.activateHandler(IWorkbenchCommandConstants.EDIT_FIND_AND_REPLACE, handler);
			}
			{	final IHandler2 handler= new AbstractHandler() {
					@Override
					public Object execute(final ExecutionEvent arg0) {
						PageBookBrowserPage.this.search(true);
						return null;
					}
				};
				handlers.add(SharedUIResources.FIND_NEXT_COMMAND_ID, handler);
				handlerService.activateHandler("org.eclipse.ui.navigate.next", handler); //$NON-NLS-1$
			}
			{	final IHandler2 handler= new AbstractHandler() {
					@Override
					public Object execute(final ExecutionEvent arg0) {
						PageBookBrowserPage.this.search(false);
						return null;
					}
				};
				handlers.add(SharedUIResources.FIND_PREVIOUS_COMMAND_ID, handler);
				handlerService.activateHandler("org.eclipse.ui.navigate.previous", handler); //$NON-NLS-1$
			}
		}
	}
	
	private void search(final boolean forward) {
		if (this.searchBar == null || !UIAccess.isOkToUse(this.browser)) {
			return;
		}
		final String text= this.searchBar.getText();
		if (text == null || text.isEmpty()) {
			return;
		}
		final boolean caseSensitive= this.searchBar.isCaseSensitiveEnabled();
		
		final String message;
		if (BrowserUtil.searchText(this.browser, text, forward, caseSensitive, false)) {
			message= null;
		}
		else if (BrowserUtil.searchText(this.browser, text, forward, caseSensitive, true)) {
			message= forward ? "Search continued from top" : "Search continued from bottom";
		}
		else {
			Display.getCurrent().beep();
			message= "Search text not found";
		}
		
		if (this.statusManager != null) {
			this.statusManager.setMessage(new StatusInfo(IStatus.INFO, message));
		}
	}
	
	
	protected Browser getBrowser() {
		return this.browser;
	}
	
	BrowserSession getSession() {
		return this.session;
	}
	
	@Override
	public Control getControl() {
		return this.composite;
	}
	
	@Override
	public void setFocus() {
		setDefaultFocus();
	}
	
	protected boolean setDefaultFocus() {
		return setFocusToBrowser();
	}
	
	public boolean isBrowserFocusControl() {
		return (UIAccess.isOkToUse(this.browser) && this.browser.isFocusControl());
	}
	
	public boolean setFocusToBrowser() {
		return this.browser.setFocus();
	}
	
	
	public void setUrl(String url) {
		if (this.browser == null) {
			return;
		}
		if (url == null || url.isEmpty()) {
			url= "about:blank"; //$NON-NLS-1$
		}
		if (url.startsWith("html:///")) { //$NON-NLS-1$
			final int id= this.session.putStatic(url.substring(8));
			url= "estatic:///" + id; //$NON-NLS-1$
		}
		this.browser.setUrl(url);
	}
	
	public String getCurrentTitle() {
		final String title= this.session.fTitle;
		return (title != null) ? title : ""; //$NON-NLS-1$
	}
	
	public String getCurrentUrl() {
		return this.session.fUrl;
	}
	
	int getCurrentProgressTotal() {
		return this.progressTotal;
	}
	
	int getCurrentProgressWorked() {
		return this.progressWorked;
	}
	
	
	@Override
	public void changed(final ProgressEvent event) {
		if (event.total == 0) {
			this.progressTotal= 0;
			this.progressWorked= 0;
		}
		else {
			this.progressTotal= event.total;
			this.progressWorked= event.current;
		}
	}
	
	@Override
	public void changing(final LocationEvent event) {
		if (event.top) {
			this.session.fImageDescriptor= null;
		}
		if (event.location.startsWith("estatic:///")) { //$NON-NLS-1$
			event.doit= false;
			try {
				final String html= this.session.getStatic(Integer.parseInt(event.location.substring(11)));
				if (html != null) {
					this.browser.setText(html);
				}
			}
			catch (final Exception e) {
			}
			return;
		}
		if (event.location.startsWith("about:")) { //$NON-NLS-1$
			if (!event.location.equals("about:blank")) { //$NON-NLS-1$
				event.doit= false;
			}
			return;
		}
		if (event.location.startsWith("res:")) { //$NON-NLS-1$
			event.doit= false;
			return;
		}
	}
	
	@Override
	public void changed(final LocationEvent event) {
		if (!event.top) {
			return;
		}
		String location= event.location;
		if ("about:blank".equals(location)) { //$NON-NLS-1$
			location= ""; //$NON-NLS-1$
		}
		this.session.fUrl= location;
	}
	
	@Override
	public void completed(final ProgressEvent event) {
		this.progressTotal= 0;
		this.progressWorked= 0;
	}
	
	@Override
	public void changed(final TitleEvent event) {
		String title= event.title;
		if (title == null) {
			title= ""; //$NON-NLS-1$
		}
		else if (title.startsWith("http://")) { //$NON-NLS-1$
			final int idx= title.lastIndexOf('/');
			if (idx >= 0) {
				title= title.substring(idx+1);
			}
		}
		this.session.fTitle= title;
	}
	
	@Override
	public void changed(final StatusTextEvent event) {
		this.browserStatus= (event.text != null && !event.text.isEmpty()) ?
				new StatusInfo(IStatus.OK, event.text) : null;
		if (this.statusManager != null) {
			this.statusManager.setSelectionMessage(this.browserStatus);
		}
	}
	
	protected void setIcon(final ImageDescriptor imageDescriptor) {
		this.session.fImageDescriptor= imageDescriptor;
	}
	
	
	@Override
	public void open(final WindowEvent event) {
		final PageBookBrowserPage page= (PageBookBrowserPage) this.view.newPage(new BrowserSession(), true);
		if (page != null) {
			event.browser= page.browser;
		}
	}
	
	@Override
	public void close(final WindowEvent event) {
		this.view.closePage(this.session);
	}
	
	public String getSelectedText() {
		return BrowserUtil.getSelectedText(this.browser);
	}
	
}
