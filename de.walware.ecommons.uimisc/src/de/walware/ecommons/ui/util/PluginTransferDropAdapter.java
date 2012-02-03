/*******************************************************************************
 * Copyright (c) 2000-2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.util;

import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.part.PluginDropAdapter;
import org.eclipse.ui.part.PluginTransfer;


public class PluginTransferDropAdapter extends PluginDropAdapter implements TransferDropTargetListener {
	
	
	public PluginTransferDropAdapter(final StructuredViewer viewer) {
		super(viewer);
		setFeedbackEnabled(false);
	}
	
	
	public Transfer getTransfer() {
		return PluginTransfer.getInstance();
	}
	
	public boolean isEnabled(final DropTargetEvent event) {
		return PluginTransfer.getInstance().isSupportedType(event.currentDataType);
	}
	
}
