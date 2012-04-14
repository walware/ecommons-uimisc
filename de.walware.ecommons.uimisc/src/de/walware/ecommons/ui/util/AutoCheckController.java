/*******************************************************************************
 * Copyright (c) 2012 WalWare/StatET-Project (www.walware.de/goto/statet).
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

import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;


/**
 * Enables check by selection for checkbox viewers
 */
public class AutoCheckController implements Listener {
	
	
	private final TableViewer fViewer;
	
	private final WritableSet fSet;
	
	private int fLast;
	
	
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
		if (event.item == null) {
			return;
		}
		if (event.type == SWT.Selection) {
			if (event.detail == SWT.CHECK) {
				fLast = event.time;
			}
			else if (event.detail == 0 && event.time != fLast) {
				event.display.asyncExec(new Runnable() {
					int time = event.time;
					Widget item = event.item;
					int stateMask = event.stateMask;
					@Override
					public void run() {
						if (time == fLast) {
							return;
						}
						if ((stateMask & SWT.MOD2) != 0) {
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
						else if ((stateMask & SWT.MOD1) != 0) {
							final Object element = item.getData();
							if (element != null) {
								fSet.add(element);
							}
						}
						else {
							final Object element = item.getData();
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
		}
	}
	
}
