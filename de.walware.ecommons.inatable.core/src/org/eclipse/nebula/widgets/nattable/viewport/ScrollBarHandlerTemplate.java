/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// ~
package org.eclipse.nebula.widgets.nattable.viewport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.SWTUtil;
import org.eclipse.nebula.widgets.nattable.edit.command.EditUtils;


public class ScrollBarHandlerTemplate implements Listener {
	
	
	private final ViewportLayer viewportLayer;
	
	private final Orientation orientation;
	
	private final ScrollBar scrollBar;
	
	/**
	 * Flag to remember if the scroll bar is moved by dragging.
	 * Needed because if the scroll bar is moved by dragging, there will be 
	 * another event that is handled for releasing the drag mode. 
	 * We only need to handle the dragging once, otherwise if the 
	 * DialogErrorHandling strategy is used, the dialog would be showed
	 * twice.
	 */
	private boolean dragging = false;
	
	
	public ScrollBarHandlerTemplate(final ViewportLayer viewportLayer, final Orientation orientation,
			final ScrollBar scrollBar) {
		this.viewportLayer = viewportLayer;
		this.orientation = orientation;
		this.scrollBar = scrollBar;
		this.scrollBar.addListener(SWT.Selection, this);
		
		scrollBar.getParent().addListener(SWTUtil.getMouseWheelEventType(this.orientation), this);
	}
	
	
	public void dispose() {
		if (this.scrollBar != null && !this.scrollBar.isDisposed()) {
			this.scrollBar.removeListener(SWT.Selection, this);
			this.scrollBar.removeListener(SWTUtil.getMouseWheelEventType(this.orientation), this);
		}
	}
	
	
	public void handleEvent(final Event event) {
		boolean handle = true;
		
		if (!this.dragging) {
			if (!EditUtils.commitAndCloseActiveEditor()) {
				handle = false;
			}
		}
		this.dragging = (event.detail == SWT.DRAG);
		
		if (!handle || !event.doit) {
			adjustScrollBar();
			return;
		}
		
		final IViewportDim dim = this.viewportLayer.getDim(this.orientation);
		switch (event.type) {
		case SWT.MouseHorizontalWheel:
		case SWT.MouseVerticalWheel:
			if (event.count > 0) {
				for (; event.count > 0; event.count--) {
					dim.scrollBackwardByStep();
				}
			}
			else if (event.count < 0) {
				for (; event.count < 0; event.count++) {
					dim.scrollForwardByStep();
				}
			}
			event.doit = false;
			return;
		case SWT.Selection:
			switch (event.detail) {
			case SWT.HOME:
				dim.scrollBackwardToBound();
				return;
			case SWT.END:
				dim.scrollForwardToBound();
				return;
			case SWT.PAGE_UP:
				dim.scrollBackwardByPage();
				return;
			case SWT.PAGE_DOWN:
				dim.scrollForwardByPage();
				return;
			case SWT.ARROW_UP:
			case SWT.ARROW_LEFT:
				dim.scrollBackwardByStep();
				return;
			case SWT.ARROW_DOWN:
			case SWT.ARROW_RIGHT:
				dim.scrollForwardByStep();
				return;
			default:
				dim.setOriginPixel(dim.getMinimumOriginPixel() + this.scrollBar.getSelection());
				return;
			}
		}
	}
	
	ScrollBar getScrollBar() {
		return this.scrollBar;
	}
	
	void adjustScrollBar() {
		if (this.scrollBar.isDisposed()) {
			return;
		}
		final IViewportDim dim = this.viewportLayer.getDim(this.orientation);
		
		final int startPixel = dim.getOriginPixel() - dim.getMinimumOriginPixel();
		
		this.scrollBar.setSelection(startPixel);
	}
	
	void recalculateScrollBarSize() {
		if (this.scrollBar.isDisposed()) {
			return;
		}
		
		final IViewportDim dim = this.viewportLayer.getDim(this.orientation);
		
		final int max = dim.getScrollable().getSize() - dim.getMinimumOriginPixel();
		if (!this.scrollBar.isDisposed()) {
			this.scrollBar.setMaximum(max);
		}
		
		final int viewportWindowSpan = dim.getSize();
		this.scrollBar.setPageIncrement(viewportWindowSpan / 4);
		
		int thumbSize;
		if (viewportWindowSpan < max && viewportWindowSpan != 0) {
			thumbSize = viewportWindowSpan;
			this.scrollBar.setEnabled(true);
			this.scrollBar.setVisible(true);
		} else {
			thumbSize = max;
			this.scrollBar.setEnabled(false);
			this.scrollBar.setVisible(false);
		}
		this.scrollBar.setThumb(thumbSize);
		
		adjustScrollBar();
	}
	
}
