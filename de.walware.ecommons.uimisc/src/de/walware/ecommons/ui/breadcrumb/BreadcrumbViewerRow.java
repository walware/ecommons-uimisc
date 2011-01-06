/*******************************************************************************
 * Copyright (c) 2007-2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.breadcrumb;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;


/**
 * A viewer row for the breadcrumb viewer.
 */
class BreadcrumbViewerRow extends ViewerRow {
	
	
	private Color fForeground;
	private Font fFont;
	private Color fBackground;
	
	private final BreadcrumbItem fItem;
	private final BreadcrumbViewer fViewer;
	
	
	public BreadcrumbViewerRow(final BreadcrumbViewer viewer, final BreadcrumbItem item) {
		fViewer= viewer;
		fItem= item;
	}
	
	
	@Override
	public Object clone() {
		return new BreadcrumbViewerRow(fViewer, fItem);
	}
	
	@Override
	public Color getBackground(final int columnIndex) {
		return fBackground;
	}
	
	@Override
	public Rectangle getBounds(final int columnIndex) {
		return getBounds();
	}
	
	@Override
	public Rectangle getBounds() {
		return fItem.getBounds();
	}
	
	@Override
	public int getColumnCount() {
		return 1;
	}
	
	@Override
	public Control getControl() {
		return fViewer.getControl();
	}
	
	@Override
	public Object getElement() {
		return fItem.getData();
	}
	
	@Override
	public Font getFont(final int columnIndex) {
		return fFont;
	}
	
	@Override
	public Color getForeground(final int columnIndex) {
		return fForeground;
	}
	
	@Override
	public Image getImage(final int columnIndex) {
		return fItem.getImage();
	}
	
	@Override
	public Widget getItem() {
		return fItem;
	}
	
	@Override
	public ViewerRow getNeighbor(final int direction, final boolean sameLevel) {
		return null;
	}
	
	@Override
	public String getText(final int columnIndex) {
		return fItem.getText();
	}
	
	@Override
	public TreePath getTreePath() {
		return new TreePath(new Object[] { getElement() });
	}
	
	@Override
	public void setBackground(final int columnIndex, final Color color) {
		fBackground= color;
	}
	
	@Override
	public void setFont(final int columnIndex, final Font font) {
		fFont= font;
	}
	
	@Override
	public void setForeground(final int columnIndex, final Color color) {
		fForeground= color;
	}
	
	@Override
	public void setImage(final int columnIndex, final Image image) {
		fItem.setImage(image);
	}
	
	@Override
	public void setText(final int columnIndex, final String text) {
		fItem.setText(text);
	}
	
}
