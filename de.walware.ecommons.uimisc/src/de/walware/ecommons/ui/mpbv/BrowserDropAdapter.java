/*=============================================================================#
 # Copyright (c) 2005-2014 IBM Corporation and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     IBM Corporation - Initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.mpbv;

import java.io.File;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;


public class BrowserDropAdapter implements DropTargetListener {
	
	
	/**
	 * The view to which this drop support has been added.
	 */
	private final Browser fBrowser;
	
	
	protected BrowserDropAdapter(final Browser view) {
		fBrowser = view;
	}
	
	
	private void validate(final DropTargetEvent event) {
		if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
			if ((event.operations & DND.DROP_LINK) == DND.DROP_LINK) {
				event.detail = DND.DROP_LINK;
				event.feedback = DND.FEEDBACK_SELECT;
				return;
			}
		}
		event.detail = DND.DROP_NONE;
	}
	
	@Override
	public void dragEnter(final DropTargetEvent event) {
		validate(event);
	}
	
	@Override
	public void dragOperationChanged(final DropTargetEvent event) {
		validate(event);
	}
	
	@Override
	public void dragOver(final DropTargetEvent event) {
	}
	
	@Override
	public void dragLeave(final DropTargetEvent event) {
	}
	
	@Override
	public void dropAccept(final DropTargetEvent event) {
	}
	
	@Override
	public void drop(final DropTargetEvent event) {
		final String[] files = (String[]) event.data;
		if (files == null || files.length == 0) {
			return;
		}
		try {
			final File f = new File(files[0]);
			fBrowser.setUrl(f.toURL().toExternalForm());
		}
		catch (final Exception e) {}
	}
	
	
}
