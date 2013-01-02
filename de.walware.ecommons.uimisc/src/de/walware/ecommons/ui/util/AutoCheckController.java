/*******************************************************************************
 * Copyright (c) 2012-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.services.IServiceLocator;

import de.walware.ecommons.ui.actions.ControlServicesUtil;


/**
 * Enables check by selection for checkbox viewers
 */
public class AutoCheckController implements Listener {
	
	
	private final CheckboxTableViewer fViewer;
	
	private final WritableSet fSet;
	
	private int fIgnoreTime;
	
	
	public AutoCheckController(final CheckboxTableViewer viewer, final WritableSet set) {
		fViewer = viewer;
		fSet = set;
		
		viewer.getTable().addListener(SWT.Selection, this);
	}
	
	
	public WritableSet getChecked() {
		return fSet;
	}
	
	@Override
	public void handleEvent(final Event event) {
		if (event.type == SWT.Selection) {
			if (event.item == null) {
				return;
			}
			if (event.detail == SWT.CHECK) {
				fIgnoreTime = event.time;
			}
			else if (event.detail == 0 && event.time != fIgnoreTime
					&& ((event.stateMask & SWT.BUTTON_MASK) == 0
							|| (event.stateMask & SWT.BUTTON_MASK) == SWT.BUTTON1 )) {
				event.display.asyncExec(new Runnable() {
					@Override
					public void run() {
						if (event.time == fIgnoreTime) {
							return;
						}
						if ((event.stateMask & SWT.MOD2) != 0) {
							final TableItem[] selection = fViewer.getTable().getSelection();
							final List<Object> list = new ArrayList<Object>();
							for (final TableItem item : selection) {
								final Object element = item.getData();
								if (element != null) {
									list.add(element);
								}
							}
							fSet.retainAll(list);
							if (fSet.size() != list.size()) {
								fSet.addAll(list);
							}
						}
						else if ((event.stateMask & SWT.MOD1) != 0) {
							final Object element = event.item.getData();
							if (element != null) {
								fSet.add(element);
							}
						}
						else {
							final Object element = event.item.getData();
							if (element != null) {
								fSet.retainAll(Collections.singleton(element));
								if (fSet.isEmpty()) {
									fSet.add(element);
								}
							}
						}
					}
				});
			}
			return;
		}
	}
	
	
	public void addActions(final IServiceLocator serviceLocator) {
		final ControlServicesUtil servicesUtil = new ControlServicesUtil(serviceLocator,
				fViewer.getClass().getName()+'#'+hashCode(), fViewer.getControl() );
		servicesUtil.addControl(fViewer.getControl());
		
		servicesUtil.activateHandler(IWorkbenchCommandConstants.EDIT_SELECT_ALL, createSelectAllHandler());
	}
	
	public IHandler2 createSelectAllHandler() {
		return new AbstractHandler() {
			@Override
			public Object execute(final ExecutionEvent event) throws ExecutionException {
				fViewer.getTable().selectAll();
				fSet.addAll(((IStructuredSelection) fViewer.getSelection()).toList());
				return null;
			}
		};
	}
	
}
