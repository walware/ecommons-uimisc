/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


public class OverlayStatus extends Status implements IOverlayStatus {
	
	
	private final int fCombinedSeverity;
	
	
	public OverlayStatus(final IStatus status, final int combinedSeverity) {
		super(status.getSeverity(), status.getPlugin(), status.getCode(), status.getMessage(),
				status.getException() );
		fCombinedSeverity = combinedSeverity;
	}
	
	@Override
	public int getCombinedSeverity() {
		return fCombinedSeverity;
	}
	
}

