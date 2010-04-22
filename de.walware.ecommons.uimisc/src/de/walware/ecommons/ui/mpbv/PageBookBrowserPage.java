/*******************************************************************************
 * Copyright (c) 2009-2010 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.mpbv;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.services.IServiceLocator;

import de.walware.ecommons.ui.actions.HandlerCollection;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.UIAccess;


public class PageBookBrowserPage extends Page implements ProgressListener,
		LocationListener, TitleListener, StatusTextListener, OpenWindowListener, CloseWindowListener {
	
	
	private final PageBookBrowserView fView;
	
	private final BrowserSession fSession;
	
	private Composite fComposite;
	
	private Browser fBrowser;
	
	private final HandlerCollection fPageHandlers = new HandlerCollection();
	
	private String fStatusText;
	
	private int fProgressTotal;
	private int fProgressWorked;
	
	
	public PageBookBrowserPage(final PageBookBrowserView view, final BrowserSession session) {
		fView = view;
		fSession = session;
	}
	
	
	@Override
	public void createControl(final Composite parent) {
		fComposite = new Composite(parent, SWT.NONE);
		fComposite.setLayout(LayoutUtil.applySashDefaults(new GridLayout(), 1));
		
		{	final Control control = createAddressBar(fComposite);
			if (control != null) {
				control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			}
		}
		final Control browser = createBrowser(fComposite);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		initActions(getSite(), fPageHandlers);
		
		setUrl(fSession.fUrl);
		setFocus();
	}
	
	public void setUrl(String url) {
		if (fBrowser == null) {
			return;
		}
		if (url == null || url.length() == 0) {
			url = "about:blank"; //$NON-NLS-1$
		}
		if (url.startsWith("html:///")) { //$NON-NLS-1$
			final int id = fSession.putStatic(url.substring(8));
			url = "estatic:///" + id; //$NON-NLS-1$
		}
		fBrowser.setUrl(url);
	}
	
	private Control createBrowser(final Composite parent) {
		fBrowser = new Browser(parent, SWT.NONE);
		
		fBrowser.addProgressListener(this);
		fBrowser.addLocationListener(this);
		fBrowser.addTitleListener(this);
		fBrowser.addStatusTextListener(this);
		fBrowser.addOpenWindowListener(this);
		fBrowser.addCloseWindowListener(this);
		
		return fBrowser;
	}
	
	protected Control createAddressBar(final Composite parent) {
		return null;
	}
	
	protected void initActions(final IServiceLocator serviceLocator, final HandlerCollection handlers) {
		final IHandlerService handlerService = (IHandlerService) serviceLocator.getService(IHandlerService.class);
		
		if (fBrowser.getBrowserType().equals("mozilla")) { //$NON-NLS-1$
			final IHandler2 handler = new AbstractHandler() {
				public Object execute(final ExecutionEvent event) throws ExecutionException {
					if (!UIAccess.isOkToUse(fBrowser)) {
						return null;
					}
					fBrowser.execute("window.find()"); //$NON-NLS-1$
					return null;
				}
			};
			handlers.add(IWorkbenchCommandConstants.EDIT_FIND_AND_REPLACE, handler);
			handlerService.activateHandler(IWorkbenchCommandConstants.EDIT_FIND_AND_REPLACE, handler);
		}
	}
	
	
	protected Browser getBrowser() {
		return fBrowser;
	}
	
	BrowserSession getSession() {
		return fSession;
	}
	
	@Override
	public Control getControl() {
		return fComposite;
	}
	
	@Override
	public void setFocus() {
		fBrowser.setFocus();
	}
	
	public boolean isBrowserFocusControl() {
		return (UIAccess.isOkToUse(fBrowser) && fBrowser.isFocusControl());
	}
	
	public void setFocusToBrowser() {
		fBrowser.setFocus();
	}
	
	
	public String getCurrentTitle() {
		final String title = fSession.fTitle;
		return (title != null) ? title : ""; //$NON-NLS-1$
	}
	
	public String getCurrentUrl() {
		return fSession.fUrl;
	}
	
	public String getCurrentStatusText() {
		return fStatusText;
	}
	
	int getCurrentProgressTotal() {
		return fProgressTotal;
	}
	
	int getCurrentProgressWorked() {
		return fProgressWorked;
	}
	
	
	public void changed(final ProgressEvent event) {
		if (event.total == 0) {
			fProgressTotal = 0;
			fProgressWorked = 0;
		}
		else {
			fProgressTotal = event.total;
			fProgressWorked = event.current;
		}
	}
	
	public void changing(final LocationEvent event) {
		if (event.top) {
			fSession.fImageDescriptor = null;
		}
		if (event.location.startsWith("estatic:///")) { //$NON-NLS-1$
			event.doit = false;
			try {
				final String html = fSession.getStatic(Integer.parseInt(event.location.substring(11)));
				if (html != null) {
					fBrowser.setText(html);
				}
			}
			catch (final Exception e) {
			}
			return;
		}
		if (event.location.startsWith("esystem://")) { //$NON-NLS-1$
			final String file = event.location.substring(10);
			event.doit = false;
			if (file.length() > 0) {
				UIAccess.getDisplay().asyncExec(new Runnable() {
					public void run() {
						Program.launch(file);
					}
				});
			}
			return;
		}
		if (event.location.startsWith("about:")) { //$NON-NLS-1$
			if (!event.location.equals("about:blank")) { //$NON-NLS-1$
				event.doit = false;
			}
			return;
		}
		if (event.location.startsWith("res:")) { //$NON-NLS-1$
			event.doit = false;
			return;
		}
	}
	
	public void changed(final LocationEvent event) {
		if (!event.top) {
			return;
		}
		String location = event.location;
		if ("about:blank".equals(location)) { //$NON-NLS-1$
			location = ""; //$NON-NLS-1$
		}
		fSession.fUrl = location;
	}
	
	public void completed(final ProgressEvent event) {
		fProgressTotal = 0;
		fProgressWorked = 0;
	}
	
	public void changed(final TitleEvent event) {
		String title = event.title;
		if (title == null) {
			title = ""; //$NON-NLS-1$
		}
		else if (title.startsWith("http://")) { //$NON-NLS-1$
			final int idx = title.lastIndexOf('/');
			if (idx >= 0) {
				title = title.substring(idx+1);
			}
		}
		fSession.fTitle = title;
	}
	
	public void changed(final StatusTextEvent event) {
		fStatusText = event.text;
	}
	
	protected void setIcon(final ImageDescriptor imageDescriptor) {
		fSession.fImageDescriptor = imageDescriptor;
	}
	
	
	public void open(final WindowEvent event) {
		final PageBookBrowserPage page = (PageBookBrowserPage) fView.newPage(new BrowserSession(), true);
		if (page != null) {
			event.browser = page.fBrowser;
		}
	}
	
	public void close(final WindowEvent event) {
		fView.closePage(fSession);
	}
	
	public String getSelection() {
		final Object value = getBrowser().evaluate(
				"if (window.getSelection) {" + //$NON-NLS-1$
					"var sel = window.getSelection();" + //$NON-NLS-1$
					"if (sel.getRangeAt) {" + //$NON-NLS-1$
						"return sel.getRangeAt(0).toString();" + //$NON-NLS-1$
					"}" + //$NON-NLS-1$
					"return sel;" + //$NON-NLS-1$
				"}" + //$NON-NLS-1$
				"else if (document.getSelection) {" + //$NON-NLS-1$
					"return document.getSelection();" + //$NON-NLS-1$
				"}" + //$NON-NLS-1$
				"else if (document.selection) {" + //$NON-NLS-1$
					"return document.selection.createRange().text;" +
				"}" + //$NON-NLS-1$
				"else {" + //$NON-NLS-1$
					"return '';" + //$NON-NLS-1$
				"}"); //$NON-NLS-1$
		if (value instanceof String) {
			return (String) value;
		}
		return null;
	}
	
}
