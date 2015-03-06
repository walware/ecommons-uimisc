/*=============================================================================#
 # Copyright (c) 2007-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.debug.ui.config;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.ObservableEvent;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;

import de.walware.ecommons.ICommonStatusConstants;
import de.walware.ecommons.databinding.DirtyTracker;
import de.walware.ecommons.ui.internal.UIMiscellanyPlugin;


/**
 * Abstract LaunchConfigurationTab with support of a DataBindingContext.
 * <p>You have to implement:
 * <ul>
 *   <li>{@link #addBindings(DataBindingContext, Realm)} (add binding to the context)</li>
 *   <li>{@link #doInitialize(ILaunchConfiguration)} (load values from config to the model)</li>
 *   <li>{@link #doSave(ILaunchConfigurationWorkingCopy)} (save values from model to config)</li>
 * </ul></p>
 * <p>Validation status with severity WARNING are handled like errors, but can be saved.
 * </p>
 */
public abstract class LaunchConfigTabWithDbc extends AbstractLaunchConfigurationTab {
	
	
	protected static class SavableErrorValidator implements IValidator {
		
		private final IValidator wrappedValidator;
		
		public SavableErrorValidator(final IValidator validator) {
			assert (validator != null);
			this.wrappedValidator= validator;
		}
		
		@Override
		public IStatus validate(final Object value) {
			final IStatus status= this.wrappedValidator.validate(value);
			if (status != null) {
				switch (status.getSeverity()) {
				case IStatus.ERROR:
					return ValidationStatus.warning(status.getMessage());
				case IStatus.WARNING:
					return ValidationStatus.info(status.getMessage());
				}
			}
			return status;
		}
	}
	
	
	private DataBindingContext dbc;
	
	private AggregateValidationStatus aggregateStatus;
	private IStatus currentStatus;
	
	private boolean initializing;
	
	
	protected LaunchConfigTabWithDbc() {
	}
	
	public String getValidationErrorAttr() {
		return getId()+"/validation.hasError"; //$NON-NLS-1$
	}
	
	protected void updateDialogState() {
		if (!isInitializing()) {
			String message= null;
			String errorMessage= null;
			switch (this.currentStatus.getSeverity()) {
			case IStatus.ERROR:
				errorMessage= this.currentStatus.getMessage();
				break;
			case IStatus.WARNING:
				errorMessage= this.currentStatus.getMessage();
				break;
			case IStatus.INFO:
				message= this.currentStatus.getMessage();
				break;
			default:
				break;
			}
			setMessage(message);
			setErrorMessage(errorMessage);
			updateLaunchConfigurationDialog();
		}
	}
	
	protected Realm getRealm() {
		return Realm.getDefault();
	}
	
	protected void initBindings() {
		this.dbc= new DataBindingContext(getRealm());
		
		addBindings(this.dbc);
		
		this.aggregateStatus= new AggregateValidationStatus(this.dbc, AggregateValidationStatus.MAX_SEVERITY);
		this.aggregateStatus.addValueChangeListener(new IValueChangeListener() {
			@Override
			public void handleValueChange(final ValueChangeEvent event) {
				LaunchConfigTabWithDbc.this.currentStatus= (IStatus) event.diff.getNewValue();
				updateDialogState();
			}
		});
		this.currentStatus= ValidationStatus.ok();
		
		new DirtyTracker(this.dbc) {
			@Override
			public void handleChange(final ObservableEvent event) {
				if (!isDirty()) {
					LaunchConfigTabWithDbc.this.currentStatus= (IStatus) LaunchConfigTabWithDbc.this.aggregateStatus.getValue();
					setDirty(true);
					updateDialogState();
				}
			}
		};
		
		getControl().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				disposeBindings();
			}
		});
	}
	
	private void disposeBindings() {
		if (this.aggregateStatus != null) {
			this.aggregateStatus.dispose();
			this.aggregateStatus= null;
		}
		if (this.dbc != null) {
			this.dbc.dispose();
			this.dbc= null;
		}
	}
	
	protected DataBindingContext getDataBindingContext() {
		return this.dbc;
	}
	
	/**
	 * @param dbc
	 * @param realm
	 * @deprecated
	 */
	@Deprecated
	protected void addBindings(DataBindingContext dbc, Realm realm) {
	}
	
	protected void addBindings(DataBindingContext dbc) {
		addBindings(dbc, dbc.getValidationRealm());
	}
	
	@Override
	public void dispose() {
		super.dispose();
		disposeBindings();
	}
	
	protected void logReadingError(final CoreException e) {
		UIMiscellanyPlugin.log(new Status(IStatus.ERROR, UIMiscellanyPlugin.PLUGIN_ID,
				ICommonStatusConstants.LAUNCHCONFIG_ERROR,
				NLS.bind("An error occurred while reading launch configuration (name: ''{0}'', id: ''{1}'')", //$NON-NLS-1$
						getName(), getId()),
				e ));
	}
	
	@Override
	public void initializeFrom(final ILaunchConfiguration configuration) {
		this.initializing= true;
		doInitialize(configuration);
		setDirty(false);
		for (final Object obj : this.dbc.getBindings()) {
			((Binding) obj).validateTargetToModel();
		}
		this.currentStatus= (IStatus) this.aggregateStatus.getValue();
		this.initializing= false;
		updateDialogState();
	}
	
	@Override
	public void activated(final ILaunchConfigurationWorkingCopy workingCopy) {
		updateDialogState();
	}
	
	@Override
	public void deactivated(final ILaunchConfigurationWorkingCopy workingCopy) {
	}
	
	@Override
	public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
		if (!canSave()) {
			configuration.setAttribute(getValidationErrorAttr(), true); // To enable the revert button
			return;
		}
		configuration.setAttribute(getValidationErrorAttr(), (String) null);
		if (isDirty()) {
			doSave(configuration);
			setDirty(false);
		}
	}
	
	protected final boolean isInitializing() {
		return this.initializing;
	}
	
	protected abstract void doInitialize(ILaunchConfiguration configuration);
	protected abstract void doSave(ILaunchConfigurationWorkingCopy configuration);
	
	@Override
	public boolean isValid(final ILaunchConfiguration launchConfig) {
		return (this.currentStatus.getSeverity() < IStatus.WARNING);
	}
	
	@Override
	public boolean canSave() {
		return (this.currentStatus.getSeverity() < IStatus.ERROR);
	}
	
}
