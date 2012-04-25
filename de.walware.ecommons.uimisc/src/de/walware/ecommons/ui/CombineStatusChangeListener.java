/*******************************************************************************
 * Copyright (c) 2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.walware.ecommons.IStatusChangeListener;


public class CombineStatusChangeListener implements IStatusChangeListener {
	
	
	private class StackListeners implements IStatusChangeListener {
		
		private IStatus status = Status.OK_STATUS;
		
		@Override
		public void statusChanged(final IStatus status) {
			this.status = status;
			update();
		}
		
	}
	
	
	private final IStatusChangeListener fParent;
	
	private IStatus fBaseStatus = Status.OK_STATUS;
	
	private final List<StackListeners> fStack = new ArrayList<StackListeners>();
	
	
	public CombineStatusChangeListener(final IStatusChangeListener statusListener) {
		fParent = statusListener;
	}
	
	
	@Override
	public void statusChanged(final IStatus status) {
		fBaseStatus = status;
		update();
	}
	
	private void update() {
		if (fStack.isEmpty()) {
			fParent.statusChanged(fBaseStatus);
			return;
		}
		int severity = 0;
		for (int i = 0; i < fStack.size(); i++) {
			final int s2 = fStack.get(i).status.getSeverity();
			if (s2 > severity) {
				severity = s2;
			}
		}
		System.out.println(severity);
		fParent.statusChanged(new OverlayStatus(fStack.get(fStack.size()-1).status, severity));
	}
	
	public IStatusChangeListener newListener() {
		final StackListeners listener = new StackListeners();
		fStack.add(listener);
		return listener;
	}
	
	public void removeListener(final IStatusChangeListener listener) {
		fStack.remove(listener);
		update();
	}
	
}
