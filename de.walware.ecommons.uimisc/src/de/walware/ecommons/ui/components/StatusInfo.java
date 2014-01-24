/*=============================================================================#
 # Copyright (c) 2005-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.components;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;


public class StatusInfo extends Status {
	
	/**
	 * Applies the status to the status line of a dialog page.
	 */
	public static void applyToStatusLine(final DialogPage page, final IStatus status) {
		String message = status.getMessage();
		switch (status.getSeverity()) {
			case IStatus.OK:
				page.setMessage(null, IMessageProvider.NONE);
				page.setErrorMessage(null);
				break;
			case IStatus.WARNING:
				page.setMessage(message, IMessageProvider.WARNING);
				page.setErrorMessage(null);
				break;				
			case IStatus.INFO:
				page.setMessage(message, IMessageProvider.INFORMATION);
				page.setErrorMessage(null);
				break;			
			default:
				if (message.isEmpty()) {
					message = null;
				}
				page.setMessage(null);
				page.setErrorMessage(message);
				break;
		}
	}
	
	/**
	 * Applies the status to the status line of a title area dialog.
	 */
	public static void applyToStatusLine(final TitleAreaDialog page, final IStatus status) {
		String message = status.getMessage();
		switch (status.getSeverity()) {
		case IStatus.OK:
			if (message.equals("OK") || message.equals(OK_STATUS.getMessage())) { //$NON-NLS-1$
				page.setMessage(null, IMessageProvider.NONE);
			}
			else {
				page.setMessage(message, IMessageProvider.NONE);
			}
			page.setMessage(!status.getMessage().equals("OK") ? status.getMessage() : null,
					IMessageProvider.NONE );
			page.setErrorMessage(null);
			break;
		case IStatus.WARNING:
			page.setMessage(message, IMessageProvider.WARNING);
			page.setErrorMessage(null);
			break;				
		case IStatus.INFO:
			page.setMessage(message, IMessageProvider.INFORMATION);
			page.setErrorMessage(null);
			break;			
		default:
			if (message.length() == 0) {
				message = null;
			}
			page.setMessage(null);
			page.setErrorMessage(message);
			break;
		}
	}
	
	
	public StatusInfo() {
		this(IStatus.OK, ""); //$NON-NLS-1$
	}
	
	public StatusInfo(final int severity, final String message) {
		super(severity, "no supported", IStatus.OK, message, null); //$NON-NLS-1$
	}
	
	
	public void setOK() {
		setSeverity(IStatus.OK);
		setMessage(""); //$NON-NLS-1$
	}
	
	public void setWarning(final String message) {
		setSeverity(IStatus.WARNING);
		setMessage(message);
	}
	
	public void setError(final String message) {
		setSeverity(IStatus.ERROR);
		setMessage(message);
	}
	
}
