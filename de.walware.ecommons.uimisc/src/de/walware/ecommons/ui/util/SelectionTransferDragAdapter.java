/*******************************************************************************
 * Copyright (c) 2000-2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.util;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;


public class SelectionTransferDragAdapter extends DragSourceAdapter implements TransferDragSourceListener {
	
	
	private final ISelectionProvider fSelectionProvider;
	
	
	public SelectionTransferDragAdapter(final ISelectionProvider selectionProvider) {
		assert (selectionProvider != null);
		fSelectionProvider = selectionProvider;
	}
	
	
	@Override
	public Transfer getTransfer() {
		return LocalSelectionTransfer.getTransfer();
	}
	
	@Override
	public void dragStart(final DragSourceEvent event) {
		final ISelection selection = fSelectionProvider.getSelection();
		final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
		transfer.setSelection(selection);
		transfer.setSelectionSetTime(event.time & 0xFFFFFFFFL);
		
		if (selection.isEmpty()) {
			event.doit = false;
			return;
		}
	}
	
	@Override
	public void dragSetData(final DragSourceEvent event) {
		event.data = LocalSelectionTransfer.getTransfer().getSelection();
	}
	
	@Override
	public void dragFinished(final DragSourceEvent event) {
		final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
		transfer.setSelection(null);
		transfer.setSelectionSetTime(0);
	}
	
}
