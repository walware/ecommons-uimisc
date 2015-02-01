/*=============================================================================#
 # Copyright (c) 2013-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.walware.ecommons.ui.components.StatusInfo;


/**
 * Utility to manage messages for the status line.
 * 
 * Uses status objects (e.g. {@link StatusInfo}) for messages and provides support for <ul>
 *     <li>Default messages (selection message)</li>
 *     <li>Error messages</li>
 *     <li>Timeout of messages</li>
 * </ul>
 */
public class StatusLineMessageManager {
	
	
	private class Handler implements Runnable {
		
		/**
		 * Current status
		 */
		private IStatus status;
		
		private IStatus defaultStatus;
		
		/**
		 * Timestamp to clear status, in milliseconds
		 * {@link Long#MIN_VALUE} for no clear
		 **/
		private long timeout;
		
		private long scheduledTimeout;
		
		
		public Handler() {
		}
		
		
		private void doSetMessage(final Image icon, final String message) {
			if (this == StatusLineMessageManager.this.errorHandler) {
				getStatusLine().setErrorMessage(icon, message);
			}
			else {
				getStatusLine().setMessage(icon, message);
			}
		}
		
		public void setMessage(final IStatus status, final int timeout) {
			final Image icon = getIcon(status.getSeverity());
			final String message = status.getMessage();
			
			this.status = status;
			if (timeout > 0) {
				this.timeout = System.nanoTime() / 1000000 + (long) timeout * 1000;
				if (this.scheduledTimeout > this.timeout) {
					this.scheduledTimeout = Long.MIN_VALUE;
					StatusLineMessageManager.this.display.timerExec(-1, this);
				}
				if (this.scheduledTimeout == Long.MIN_VALUE) {
					this.scheduledTimeout = this.timeout;
					StatusLineMessageManager.this.display.timerExec(timeout * 1000, this);
				}
			}
			else {
				this.timeout = Long.MIN_VALUE;
			}
			
			doSetMessage(icon, message);
		}
		
		public void setDefaultMessage(IStatus status) {
			if (status != null) {
				this.defaultStatus = status;
				setMessage(status, 0);
			}
			else {
				status = this.defaultStatus;
				this.defaultStatus = null;
				clear(status);
			}
		}
		
		public void clear(final IStatus status) {
			if (this.status == status) {
				clear();
			}
		}
		
		public void clear() {
			if (this.defaultStatus != null) {
				setMessage(this.defaultStatus, 0);
			}
			else {
				this.status = null;
				this.timeout = Long.MIN_VALUE;
				doSetMessage(null, null);
			}
		}
		
		@Override
		public void run() {
			if (this.timeout != Long.MIN_VALUE) {
				final int diff = (int) (this.timeout - System.nanoTime() / 1000000);
				if (diff > 0) {
					this.scheduledTimeout = this.timeout;
					StatusLineMessageManager.this.display.timerExec(diff + 10, this);
					return;
				}
				
				clear();
			}
			
			this.scheduledTimeout = Long.MIN_VALUE;
		}
		
	}
	
	
	private final IStatusLineManager statusLineManager;
	
	private final Display display;
	
	private final boolean showIcons;
	
	private final Handler infoHandler = new Handler();
	private final Handler errorHandler = new Handler();
	
	
	public StatusLineMessageManager(final IStatusLineManager statusLineManager) {
		this(statusLineManager, true);
	}
	
	public StatusLineMessageManager(final IStatusLineManager statusLineManager,
			final boolean showIcons) {
		this.statusLineManager = statusLineManager;
		this.display = UIAccess.getDisplay();
		this.showIcons = showIcons;
	}
	
	
	protected IStatusLineManager getStatusLine() {
		return this.statusLineManager;
	}
	
	public void setMessage(final IStatus status) {
		setMessage(status, 30);
	}
	
	/**
	 * Sets the specified message to the status line.
	 * 
	 * @param status the status to show
	 * @param timeout timeout for the status in seconds
	 */
	public void setMessage(final IStatus status, final int timeout) {
		getHandler(status).setMessage(status, timeout);
	}
	
	public void setSelectionMessage(final IStatus status) {
		this.infoHandler.setDefaultMessage(status);
	}
	
	public void clear(final IStatus status) {
		getHandler(status).clear(status);
	}
	
	public void clearAll() {
		this.infoHandler.clear();
		this.errorHandler.clear();
	}
	
	
	private Handler getHandler(final IStatus status) {
		return (status.getSeverity() == IStatus.ERROR) ? this.errorHandler : this.infoHandler;
	}
	
	private Image getIcon(final int severity) {
		if (!this.showIcons) {
			return null;
		}
		switch (severity) {
		case IStatus.INFO:
			return JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO);
		case IStatus.WARNING:
			return JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);
		case IStatus.ERROR:
			return JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
		default:
			return null;
		}
	}
	
}
