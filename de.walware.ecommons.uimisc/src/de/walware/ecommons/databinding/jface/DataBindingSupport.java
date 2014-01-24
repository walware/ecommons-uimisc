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

package de.walware.ecommons.databinding.jface;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.ObservableEvent;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.ecommons.IStatusChangeListener;
import de.walware.ecommons.databinding.DirtyTracker;
import de.walware.ecommons.ui.SharedUIResources;


/**
 * 
 */
public class DataBindingSupport {
	
	private static final int STARTED = 1;
	private static final int DISPOSING = 2;
	private static final int DISPOSED = 3;
	
	
	private Realm fRealm;
	private DataBindingContext fDbc;
	
	private DirtyTracker fTracker;
	
	private int fState;
	
	
	public DataBindingSupport(final Control rootControl) {
		fRealm = Realm.getDefault();
		fDbc = createContext(fRealm);
		
		rootControl.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				dispose();
			}
		});
		
		fState = STARTED;
	}
	
	
	protected DataBindingContext createContext(final Realm realm) {
		return new DataBindingContext(realm);
	}
	
	public DataBindingContext getContext() {
		return fDbc;
	}
	
	public Realm getRealm() {
		return fRealm;
	}
	
	private void dispose() {
		if (fDbc != null) {
			fState = DISPOSING;
			try {
				fDbc.dispose();
			}
			catch (final Throwable e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, SharedUIResources.PLUGIN_ID,
						"An error occurend when dispose databinding", e)); //$NON-NLS-1$
			}
			fState = DISPOSED;
			fDbc = null;
			fRealm = null;
		}
	}
	
	public void installStatusListener(final IStatusChangeListener listener) {
		if (fState > STARTED) {
			throw new IllegalStateException();
		}
		final AggregateValidationStatus validationStatus = new AggregateValidationStatus(fDbc, AggregateValidationStatus.MAX_SEVERITY);
		validationStatus.addValueChangeListener(new IValueChangeListener() {
			@Override
			public void handleValueChange(final ValueChangeEvent event) {
				if (fState == STARTED) {
					final IStatus status = (IStatus) event.diff.getNewValue();
					listener.statusChanged(status);
				}
			}
		});
		
		listener.statusChanged((IStatus) validationStatus.getValue());
		fTracker = new DirtyTracker(fDbc) { // sets initial status on first change again, because initial errors are suppressed
			@Override
			public void handleChange(final ObservableEvent event) {
				if (!isDirty()) {
					if (fState == STARTED) {
						listener.statusChanged((IStatus) validationStatus.getValue());
					}
					super.handleChange(event);
				}
			}
		};
	}
	
	public void updateStatus() {
		if (fTracker != null) {
			fTracker.resetDirty();
			fTracker.handleChange(null);
		}
	}
	
}
