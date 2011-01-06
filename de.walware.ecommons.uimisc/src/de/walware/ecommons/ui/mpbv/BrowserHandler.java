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

import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.browser.Browser;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.util.UIAccess;


/**
 * Abstract command handler which can be bind to a browser.
 */
public abstract class BrowserHandler extends AbstractHandler {
	
	
	public static interface IBrowserProvider {
		
		Browser getBrowser();
		
		void showMessage(int severity, String message);
		
	}
	
	
	public static class NavigateBackHandler extends BrowserHandler {
		
		
		public NavigateBackHandler(final Browser browser) {
			super(browser);
		}
		
		public NavigateBackHandler(final IBrowserProvider browser) {
			super(browser);
		}
		
		
		@Override
		public void setEnabled(final Object evaluationContext) {
			final Browser browser = getBrowser();
			setBaseEnabled(UIAccess.isOkToUse(browser) && browser.isBackEnabled());
		}
		
		public Object execute(final ExecutionEvent event) throws ExecutionException {
			final Browser browser = getBrowser();
			if (UIAccess.isOkToUse(browser)) {
				browser.back();
			}
			return null;
		}
		
	}
	
	public static class NavigateForwardHandler extends BrowserHandler {
		
		
		public NavigateForwardHandler(final Browser browser) {
			super(browser);
		}
		
		public NavigateForwardHandler(final IBrowserProvider browser) {
			super(browser);
		}
		
		
		@Override
		public void setEnabled(final Object evaluationContext) {
			final Browser browser = getBrowser();
			setBaseEnabled(UIAccess.isOkToUse(browser) && browser.isForwardEnabled());
		}
		
		public Object execute(final ExecutionEvent event) throws ExecutionException {
			final Browser browser = getBrowser();
			if (UIAccess.isOkToUse(browser)) {
				browser.forward();
			}
			return null;
		}
		
	}
	
	public static class CancelHandler extends BrowserHandler {
		
		
		public CancelHandler(final Browser browser) {
			super(browser);
		}
		
		public CancelHandler(final IBrowserProvider browser) {
			super(browser);
		}
		
		
		@Override
		public void setEnabled(final Object evaluationContext) {
			final Browser browser = getBrowser();
			setBaseEnabled(UIAccess.isOkToUse(browser));
		}
		
		public Object execute(final ExecutionEvent event) throws ExecutionException {
			final Browser browser = getBrowser();
			if (UIAccess.isOkToUse(browser)) {
				browser.stop();
			}
			return null;
		}
		
	}
	
	public static class OpenExternalHandler extends BrowserHandler {
		
		
		public OpenExternalHandler(final Browser browser) {
			super(browser);
		}
		
		public OpenExternalHandler(final IBrowserProvider browser) {
			super(browser);
		}
		
		
		@Override
		public void setEnabled(final Object evaluationContext) {
			final Browser browser = getBrowser();
			setBaseEnabled(browser != null
					&& browser.getUrl().length() > 0);
		}
		
		public Object execute(final ExecutionEvent event) throws ExecutionException {
			final Browser browser = getBrowser();
			if (browser != null) {
				final IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
				try {
					final IWebBrowser externalBrowser = browserSupport.getExternalBrowser();
					final URL url = new URL(browser.getUrl());
					externalBrowser.openURL(url);
				}
				catch (final Exception e) {
					StatusManager.getManager().handle(new Status(IStatus.ERROR, SharedUIResources.PLUGIN_ID, -1,
							"An error occurred when opening the page in an external browser.", e));
					showMessage(IStatus.ERROR, "Open external browser failed");
				}
			}
			return null;
		}
		
	}
	
	
	private IBrowserProvider fBrowserProvider;
	
	
	public BrowserHandler() {
	}
	
	public BrowserHandler(final Browser browser) {
		fBrowserProvider = new IBrowserProvider() {
			public Browser getBrowser() {
				return browser;
			}
			public void showMessage(final int severity, final String message) {
			}
		};
	}
	
	public BrowserHandler(final IBrowserProvider browser) {
		fBrowserProvider = browser;
	}
	
	
	public Browser getBrowser() {
		return fBrowserProvider.getBrowser();
	}
	
	protected void showMessage(final int severity, final String message) {
		fBrowserProvider.showMessage(severity, message);
	}
	
}
