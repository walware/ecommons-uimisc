/*******************************************************************************
 * Copyright (c) 2007-2010 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;

import de.walware.ecommons.IStatusChangeListener;


/**
 * To support status update for {@link TitleAreaDialog}
 */
public class TitleAreaStatusUpdater implements IStatusChangeListener {
	
	
	private final TitleAreaDialog fDialog;
	
	private String fDefaultMessage;
	
	
	public TitleAreaStatusUpdater(final TitleAreaDialog dialog, final String defaultMessage) {
		fDialog = dialog;
		fDefaultMessage = defaultMessage;
		
		fDialog.setMessage(defaultMessage);
	}
	
	
	public void statusChanged(final IStatus status) {
//			setErrorMessage(null);
		switch (status.getSeverity()) {
		case IStatus.ERROR:
			fDialog.setMessage(status.getMessage(), IMessageProvider.ERROR);
			break;
		case IStatus.WARNING:
			fDialog.setMessage(status.getMessage(), IMessageProvider.WARNING);
			break;
		case IStatus.INFO:
			fDialog.setMessage(status.getMessage(), IMessageProvider.INFORMATION);
			break;
		case IStatus.OK:
			fDialog.setMessage(fDefaultMessage);
			break;
		default:
			break;
		}
	}
	
}
