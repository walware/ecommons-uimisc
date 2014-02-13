/*=============================================================================#
 # Copyright (c) 2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.workbench.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;

import de.walware.ecommons.ui.viewers.DecoratingStyledCellLabelProvider;


public class DecoratingStyledLabelProvider extends DecoratingStyledCellLabelProvider
		implements IPropertyChangeListener {
	
	
	public static boolean showColoredLabels() {
		return PlatformUI.getPreferenceStore().getBoolean(IWorkbenchPreferenceConstants.USE_COLORED_LABELS);
	}
	
	public static final Collection<String> DEFAULT_UPDATE_PROPERTIES;
	
	static {
		final Set<String> properties = new HashSet<String>();
		properties.add(IWorkbenchPreferenceConstants.USE_COLORED_LABELS);
		properties.add(JFacePreferences.QUALIFIER_COLOR);
		properties.add(JFacePreferences.COUNTER_COLOR);
		properties.add(JFacePreferences.DECORATIONS_COLOR);
		DEFAULT_UPDATE_PROPERTIES = Collections.unmodifiableSet(properties);
	}
	
	
	private Collection<String> updateProperties= DEFAULT_UPDATE_PROPERTIES;
	
	
	public DecoratingStyledLabelProvider(final IStyledLabelProvider provider) {
		super(provider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), null);
	}
	
	public DecoratingStyledLabelProvider(final IStyledLabelProvider provider,
			final Collection<String> updateProperties) {
		super(provider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), null);
		this.updateProperties = updateProperties;
	}
	
	
	protected void setUpdateProperties(final Collection<String> properties) {
		this.updateProperties= properties;
	}
	
	protected Collection<String> getUpdateProperties() {
		return this.updateProperties;
	}
	
	
	@Override
	public void initialize(final ColumnViewer viewer, final ViewerColumn column) {
		PlatformUI.getPreferenceStore().addPropertyChangeListener(this);
		JFaceResources.getColorRegistry().addListener(this);
		
		setOwnerDrawEnabled(showColoredLabels());
		
		super.initialize(viewer, column);
	}
	
	@Override
	public void dispose() {
		PlatformUI.getPreferenceStore().removePropertyChangeListener(this);
		JFaceResources.getColorRegistry().removeListener(this);
		
		super.dispose();
	}
	
	
	private void refresh() {
		final ColumnViewer viewer= getViewer();
		if (viewer == null) {
			return;
		}
		
		boolean updateRequired= false;
		final boolean showColoredLabels= showColoredLabels();
		if (showColoredLabels != isOwnerDrawEnabled()) {
			setOwnerDrawEnabled(showColoredLabels);
			updateRequired= true;
		}
		else if (showColoredLabels) {
			updateRequired= true;
		}
		
		if (updateRequired) {
			fireLabelProviderChanged(new LabelProviderChangedEvent(this));
		}
	}
	
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (this.updateProperties.contains(event.getProperty())) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					refresh();
				}
			});
		}
	}
	
	
	@Override
	protected StyleRange prepareStyleRange(StyleRange styleRange, final boolean applyColors) {
		if (!applyColors && styleRange.background != null) {
			styleRange= super.prepareStyleRange(styleRange, applyColors);
			styleRange.borderStyle= SWT.BORDER_DOT;
			return styleRange;
		}
		return super.prepareStyleRange(styleRange, applyColors);
	}
	
}
