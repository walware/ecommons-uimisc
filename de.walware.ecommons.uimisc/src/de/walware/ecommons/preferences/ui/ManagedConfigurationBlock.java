/*=============================================================================#
 # Copyright (c) 2005-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.preferences.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.preferences.IWorkingCopyManager;
import org.osgi.service.prefs.BackingStoreException;

import de.walware.ecommons.IStatusChangeListener;
import de.walware.ecommons.databinding.jface.DataBindingSupport;
import de.walware.ecommons.io.BuildUtil;
import de.walware.ecommons.preferences.IPreferenceAccess;
import de.walware.ecommons.preferences.Preference;
import de.walware.ecommons.preferences.SettingsChangeNotifier;


/**
 * Allows load, save, restore of managed preferences, including:
 * <p><ul>
 * <li>Connected databinding context:<ul>
 *     <li>use {@link #initBindings()} to create dbc</li>
 *     <li>use {@link #createObservable(Object)} to create observables for model</li>
 *     <li>override {@link #addBindings(DataBindingSupport)}) to register bindings</li>
 *   </ul></li>
 *   <li>optional project scope</li>
 *   <li>change settings groups ({@link SettingsChangeNotifier})</li>
 * </ul>
 * Instead of data binding, it is possible to overwrite
 *   {@link #updatePreferences()} and {@link #updateControls()}
 * to map the preferences to UI.
 */
public abstract class ManagedConfigurationBlock extends ConfigurationBlock
		implements IPreferenceAccess, IObservableFactory {
	
	
	protected class PreferenceManager {
		
		private final IScopeContext[] fLookupOrder;
		private final IScopeContext fInheritScope;
		protected final Map<Preference<?>, String> fPreferences;
		
		/** Manager for a working copy of the preferences */
		private final IWorkingCopyManager fManager;
		/** Map saving the project settings, if disabled */
		private Map<Preference<?>, Object> fDisabledProjectSettings;
		
		
		PreferenceManager(final Map<Preference<?>, String> prefs) {
			fManager = getContainer().getWorkingCopyManager();
			fPreferences = prefs;
			
			fPreferenceManager = this;
			
			if (fProject != null) {
				fLookupOrder = new IScopeContext[] {
						new ProjectScope(fProject),
						InstanceScope.INSTANCE,
						DefaultScope.INSTANCE
				};
				fInheritScope = null;
			}
			else {
				fLookupOrder = new IScopeContext[] {
						InstanceScope.INSTANCE,
						DefaultScope.INSTANCE
				};
				fInheritScope = fLookupOrder[1];
			}
			
			// testIfOptionsComplete();
			
			// init disabled settings, if required
			if (fProject == null || hasProjectSpecificSettings(fProject)) {
				fDisabledProjectSettings = null;
			} else {
				saveDisabledProjectSettings();
			}
		}
		
		
/* Managing methods ***********************************************************/
		
		/**
		 * Checks, if project specific options exists
		 * 
		 * @param project to look up
		 * @return
		 */
		boolean hasProjectSpecificSettings(final IProject project) {
			final IScopeContext projectContext = new ProjectScope(project);
			for (final Preference<?> key : fPreferences.keySet()) {
				if (getInternalValue(key, projectContext, true) != null) {
					return true;
				}
			}
			return false;
		}
		
		void setUseProjectSpecificSettings(final boolean enable) {
			final boolean hasProjectSpecificOption = (fDisabledProjectSettings == null);
			if (enable != hasProjectSpecificOption) {
				if (enable) {
					loadDisabledProjectSettings();
				} else {
					saveDisabledProjectSettings();
				}
			}
		}
		
		private void saveDisabledProjectSettings() {
			fDisabledProjectSettings = new IdentityHashMap<Preference<?>, Object>();
			for (final Preference<?> key : fPreferences.keySet()) {
				fDisabledProjectSettings.put(key, getValue(key));
				setInternalValue(key, null); // clear project settings
			}
			
		}
		
		private void loadDisabledProjectSettings() {
			for (final Preference<?> key : fPreferences.keySet()) {
				// Copy values from saved disabled settings to working store
				setValue((Preference) key, fDisabledProjectSettings.get(key));
			}
			fDisabledProjectSettings = null;
		}
		
		boolean processChanges(final boolean saveStore) {
			final List<Preference<?>> changedPrefs = new ArrayList<Preference<?>>();
			final boolean needsBuild = getChanges(changedPrefs);
			if (changedPrefs.isEmpty()) {
				return true;
			}
			
			boolean doBuild = false;
			if (needsBuild) {
				final String[] strings = getFullBuildDialogStrings(fProject == null);
				if (strings != null) {
					final MessageDialog dialog = new MessageDialog(getShell(),
							strings[0], null, strings[1],
							MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL }, 2);
					final int res = dialog.open();
					if (res == 0) {
						doBuild = true;
					}
					else if (res != 1) {
						return false; // cancel pressed
					}
				}
			}
			if (saveStore) {
				try {
					fManager.applyChanges();
				} catch (final BackingStoreException e) {
					logSaveError(e);
					return false;
				}
				if (doBuild) {
					BuildUtil.getBuildJob(fProject).schedule();
				}
			}
			else {
				if (doBuild) {
					getContainer().registerUpdateJob(BuildUtil.getBuildJob(fProject));
				}
			}
			final Set<String> groupIds = new HashSet<String>();
			for (final Preference<?> pref : changedPrefs) {
				final String groupId = fPreferences.get(pref);
				if (groupId != null) {
					groupIds.add(groupId);
				}
			}
			scheduleChangeNotification(groupIds, saveStore);
			return true;
		}
		
		/**
		 * 
		 * @param currContext
		 * @param changedSettings
		 * @return true, if rebuild is required.
		 */
		private boolean getChanges(final List<Preference<?>> changedSettings) {
			final IScopeContext currContext = fLookupOrder[0];
			boolean needsBuild = false;
			for (final Preference<?> key : fPreferences.keySet()) {
				final String oldValue = getInternalValue(key, currContext, false);
				final String value = getInternalValue(key, currContext, true);
				if (value == null) {
					if (oldValue != null) {
						changedSettings.add(key);
						needsBuild |= !oldValue.equals(getInternalValue(key, true));
					}
				}
				else if (!value.equals(oldValue)) {
					changedSettings.add(key);
					needsBuild |= (oldValue != null || !value.equals(getInternalValue(key, true)));
					
					if (fInheritScope != null
							&& value.equals(getInternalValue(key, fInheritScope, false) )) {
						final IEclipsePreferences node = getNode(currContext, key.getQualifier(), true);
						node.remove(key.getKey());
					}
				}
			}
			return needsBuild;
		}
		
		
		void loadDefaults() {
			final IScopeContext defaultScope = DefaultScope.INSTANCE;
			for (final Preference<?> key : fPreferences.keySet()) {
				final String defValue = getInternalValue(key, defaultScope, false);
				setInternalValue(key, defValue);
			}
		}
		
		// DEBUG
		private void testIfOptionsComplete() {
			for (final Preference<?> key : fPreferences.keySet()) {
				if (getInternalValue(key, false) == null) {
					System.out.println("preference option missing: " + key + " (" + this.getClass().getName() +')');  //$NON-NLS-1$//$NON-NLS-2$
				}
			}
		}
		
		private IEclipsePreferences getNode(final IScopeContext context, final String qualifier, final boolean useWorkingCopy) {
			final IEclipsePreferences node = context.getNode(qualifier);
			if (useWorkingCopy) {
				return fManager.getWorkingCopy(node);
			}
			return node;
		}
		
		private String getInternalValue(final Preference<?> key, final IScopeContext context, final boolean useWorkingCopy) {
			final IEclipsePreferences node = getNode(context, key.getQualifier(), useWorkingCopy);
			return node.get(key.getKey(), null);
		}
		
		private String getInternalValue(final Preference<?> key, final boolean ignoreTopScope) {
			for (int i = ignoreTopScope ? 1 : 0; i < fLookupOrder.length; i++) {
				final String value = getInternalValue(key, fLookupOrder[i], true);
				if (value != null) {
					return value;
				}
			}
			return null;
		}
		
		private <T> void setInternalValue(final Preference<T> key, final String value) {
			final IEclipsePreferences node = getNode(fLookupOrder[0], key.getQualifier(), true);
			if (value != null) {
				node.put(key.getKey(), value);
			}
			else {
				node.remove(key.getKey());
			}
		}
		
		
		private <T> void setValue(final Preference<T> key, final T value) {
			final IEclipsePreferences node = getNode(fLookupOrder[0], key.getQualifier(), true);
			if (value == null) {
				node.remove(key.getKey());
				return;
			}
			
			final Object valueToStore = key.usage2Store(value);
			switch (key.getStoreType()) {
			case BOOLEAN:
				node.putBoolean(key.getKey(), (Boolean) valueToStore);
				break;
			case INT:
				node.putInt(key.getKey(), (Integer) valueToStore);
				break;
			case LONG:
				node.putLong(key.getKey(), (Long) valueToStore);
				break;
			case DOUBLE:
				node.putDouble(key.getKey(), (Double) valueToStore);
				break;
			case FLOAT:
				node.putFloat(key.getKey(), (Float) valueToStore);
				break;
			default:
				node.put(key.getKey(), (String) valueToStore);
				break;
			}
		}
		
		private <T> T getValue(final Preference<T> key) {
			IEclipsePreferences node = null;
			int lookupIndex = 0;
			for (; lookupIndex < fLookupOrder.length; lookupIndex++) {
				final IEclipsePreferences nodeToCheck = getNode(fLookupOrder[lookupIndex], key.getQualifier(), true);
				if (nodeToCheck.get(key.getKey(), null) != null) {
					node = nodeToCheck;
					break;
				}
			}
			
			Object storedValue;
			if (node != null) {
				switch (key.getStoreType()) {
				case BOOLEAN:
					storedValue = Boolean.valueOf(node.getBoolean(key.getKey(), Preference.BOOLEAN_DEFAULT_VALUE));
					break;
				case INT:
					storedValue = Integer.valueOf(node.getInt(key.getKey(), Preference.INT_DEFAULT_VALUE));
					break;
				case LONG:
					storedValue = Long.valueOf(node.getLong(key.getKey(), Preference.LONG_DEFAULT_VALUE));
					break;
				case DOUBLE:
					storedValue = Double.valueOf(node.getDouble(key.getKey(), Preference.DOUBLE_DEFAULT_VALUE));
					break;
				case FLOAT:
					storedValue = Float.valueOf(node.getFloat(key.getKey(), Preference.FLOAT_DEFAULT_OBJECT));
					break;
				default:
					storedValue = node.get(key.getKey(), null);
					break;
				}
			}
			else {
				storedValue = null;
			}
			return key.store2Usage(storedValue);
		}
	}
	
	
	protected IProject fProject;
	protected PreferenceManager fPreferenceManager;
	
	private DataBindingSupport fDataBinding;
	private IStatusChangeListener fStatusListener;
	
	private Composite fPageComposite;
	
	
	protected ManagedConfigurationBlock(final IProject project) {
		this(project, null, null);
	}
	
	protected ManagedConfigurationBlock(final IProject project, final IStatusChangeListener statusListener) {
		this(project, null, statusListener);
	}
	
	protected ManagedConfigurationBlock(final IProject project, final String title,
			final IStatusChangeListener statusListener) {
		super(title);
		fProject = project;
		fStatusListener = statusListener;
	}
	
	
	protected void setStatusListener(final IStatusChangeListener listener) {
		fStatusListener = listener;
	}
	
	
	public IProject getProject() {
		return fProject;
	}
	
	@Override
	public void createContents(final Composite pageComposite,
			final IWorkbenchPreferenceContainer container, final IPreferenceStore preferenceStore) {
		fPageComposite = pageComposite;
		super.createContents(pageComposite, container, preferenceStore);
	}
	
	/**
	 * initialize preference management
	 * 
	 * @param container
	 * @param prefs map with preference objects as key and their settings group id as optional value
	 */
	protected void setupPreferenceManager(final Map<Preference<?>, String> prefs) {
		new PreferenceManager(prefs);
	}
	
	protected void initBindings() {
		fDataBinding = new DataBindingSupport(fPageComposite);
		addBindings(fDataBinding);
		
		fDataBinding.installStatusListener(fStatusListener);
	}
	
	protected DataBindingSupport getDataBinding() {
		return fDataBinding;
	}
	
	protected void addBindings(final DataBindingSupport db) {
	}
	
	/**
	 * Point to hook, before the managed preference values are saved to store.
	 * E.g. you can set some additional (or all) values.
	 */
	protected void updatePreferences() {
	}
	
	@Override
	public void performApply() {
		if (fPreferenceManager != null) {
			updatePreferences();
			fPreferenceManager.processChanges(true);
		}
	}
	
	@Override
	public boolean performOk() {
		if (fPreferenceManager != null) {
			updatePreferences();
			return fPreferenceManager.processChanges(false);
		}
		return true;
	}
	
	@Override
	public void performDefaults() {
		if (fPreferenceManager != null) {
			fPreferenceManager.loadDefaults();
			updateControls();
		}
	}
	
	
/* */
	
	/**
	 * Checks, if project specific options exists
	 * 
	 * @param project to look up
	 * @return
	 */
	public boolean hasProjectSpecificOptions(final IProject project) {
		if (project != null && fPreferenceManager != null) {
			return fPreferenceManager.hasProjectSpecificSettings(project);
		}
		return false;
	}
	
	@Override
	public void setUseProjectSpecificSettings(final boolean enable) {
		super.setUseProjectSpecificSettings(enable);
		if (fProject != null && fPreferenceManager != null) {
			fPreferenceManager.setUseProjectSpecificSettings(enable);
		}
	}
	
	protected void updateControls() {
		if (fDataBinding != null) {
			fDataBinding.getContext().updateTargets();
		}
	}
	
	
/* Access preference values ***************************************************/
	
	/**
	 * Returns the value for the specified preference.
	 * 
	 * @param key preference key
	 * @return value of the preference
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getPreferenceValue(final Preference<T> key) {
		assert (fPreferenceManager != null);
		assert (key != null);
		
		if (fPreferenceManager.fDisabledProjectSettings != null) {
			return (T) fPreferenceManager.fDisabledProjectSettings.get(key);
		}
		return fPreferenceManager.getValue(key);
	}
	
	@Override
	public IEclipsePreferences[] getPreferenceNodes(final String nodeQualifier) {
		assert (fPreferenceManager != null);
		assert (nodeQualifier != null);
		
		final IEclipsePreferences[] nodes = new IEclipsePreferences[fPreferenceManager.fLookupOrder.length - 1];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = fPreferenceManager.getNode(fPreferenceManager.fLookupOrder[i], nodeQualifier, true);
		}
		return nodes;
	}
	
	@Override
	public IScopeContext[] getPreferenceContexts() {
		assert (fPreferenceManager != null);
		
		return fPreferenceManager.fLookupOrder;
	}
	
	/**
	 * Sets a preference value in the default store.
	 * 
	 * @param key preference key
	 * @param value new value
	 * @return old value
	 */
	@SuppressWarnings("unchecked")
	public <T> T setPrefValue(final Preference<T> key, final T value) {
		assert (fPreferenceManager != null);
		
		if (fPreferenceManager.fDisabledProjectSettings != null) {
			return (T) fPreferenceManager.fDisabledProjectSettings.put(key, value);
		}
		final T oldValue = getPreferenceValue(key);
		fPreferenceManager.setValue(key, value);
		return oldValue;
	}
	
	public void setPrefValues(final Map<Preference<?>, Object> map) {
		for (final Entry<Preference<?>, Object> entry : map.entrySet()) {
			setPrefValue((Preference) entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * Not (yet) supported
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void addPreferenceNodeListener(final String nodeQualifier, final IPreferenceChangeListener listener) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not (yet) supported
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void removePreferenceNodeListener(final String nodeQualifier, final IPreferenceChangeListener listener) {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public IObservableValue createObservable(final Object target) {
		return createObservable((Preference<?>) target);
	}
	
	public IObservableValue createObservable(final Preference<?> pref) {
		return new AbstractObservableValue() {
			@Override
			public Object getValueType() {
				return pref.getUsageType();
			}
			@Override
			protected void doSetValue(final Object value) {
				setPrefValue((Preference) pref, value);
			}
			@Override
			protected Object doGetValue() {
				return getPreferenceValue(pref);
			}
			@Override
			public synchronized void dispose() {
				super.dispose();
			}
		};
	}
	
	/**
	 * Changes requires full build, this method should be overwritten
	 * and return the Strings for the dialog.
	 * 
	 * @param workspaceSettings true, if settings for workspace; false, if settings for project.
	 * @return
	 */
	protected String[] getFullBuildDialogStrings(final boolean workspaceSettings) {
		return null;
	}
	
}
