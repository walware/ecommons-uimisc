/*******************************************************************************
 * Copyright (c) 2007-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.util;

import java.util.Collection;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;


public class LayoutUtil {
	
	private static class DialogValues {
		
		int defaultEntryFieldWidth;
		
		int defaultHMargin;
		int defaultVMargin;
		int defaultHSpacing;
		int defaultVSpacing;
		int defaultIndent;
		int defaultSmallIndent;
		
		public DialogValues() {
			final GC gc = new GC(Display.getCurrent());
			gc.setFont(JFaceResources.getDialogFont());
			final FontMetrics fontMetrics = gc.getFontMetrics();
			
			defaultHMargin = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.HORIZONTAL_MARGIN);
			defaultVMargin = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.VERTICAL_MARGIN);
			defaultHSpacing = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.HORIZONTAL_SPACING);
			defaultVSpacing = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.VERTICAL_SPACING);
			defaultEntryFieldWidth = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.ENTRY_FIELD_WIDTH);
			defaultIndent = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.INDENT);
			defaultSmallIndent = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.SMALL_INDENT);
			
			gc.dispose();
		}
	}
	
	private static DialogValues gDialogValues;
	
	private static DialogValues getDialogValues() {
		if (gDialogValues == null) {
			JFaceResources.getFontRegistry().addListener(new IPropertyChangeListener() {
				@Override
				public void propertyChange(final PropertyChangeEvent event) {
					if (JFaceResources.DIALOG_FONT.equals(event.getProperty())) {
						UIAccess.getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								gDialogValues = new DialogValues();
							}
						});
					}
				}
			});
			gDialogValues = new DialogValues();
		}
		return gDialogValues;
	}
	
	
	public static int defaultHMargin() {
		return getDialogValues().defaultHMargin;
	}
	
	public static int defaultVMargin() {
		return getDialogValues().defaultVMargin;
	}
	
	public static Point defaultSpacing() {
		return new Point(getDialogValues().defaultHSpacing, getDialogValues().defaultVSpacing);
	}
	
	public static int defaultHSpacing() {
		return getDialogValues().defaultHSpacing;
	}
	
	public static int defaultVSpacing() {
		return getDialogValues().defaultVSpacing;
	}
	
	public static int defaultIndent() {
		return getDialogValues().defaultIndent;
	}
	
	public static int defaultSmallIndent() {
		return getDialogValues().defaultSmallIndent;
	}
	
	public static int hintWidth(final Button button) {
		button.setFont(JFaceResources.getFontRegistry().get(JFaceResources.DIALOG_FONT));
		final PixelConverter converter = new PixelConverter(button);
		final int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}
	
	public static GridData hintWidth(final GridData gd, final Button button) {
		gd.widthHint = hintWidth(button);
		return gd;
	}
	
	public static int hintWidth(final Text text, final int numChars) {
		return hintWidth(text, JFaceResources.DIALOG_FONT, numChars);
	}
	
	public static int hintWidth(final Text text, final String symbolicName, final int numChars) {
		if (symbolicName != null) {
			text.setFont(JFaceResources.getFontRegistry().get(symbolicName));
		}
		if (numChars == -1) {
			return getDialogValues().defaultEntryFieldWidth;
		}
		final PixelConverter converter = new PixelConverter(text);
		final int widthHint = converter.convertWidthInCharsToPixels(numChars);
		return widthHint;
	}
	
	public static int hintWidth(final StyledText text, final String symbolicName, final int numChars) {
		if (symbolicName != null) {
			text.setFont(JFaceResources.getFontRegistry().get(symbolicName));
		}
		if (numChars == -1) {
			return getDialogValues().defaultEntryFieldWidth;
		}
		final PixelConverter converter = new PixelConverter(text);
		final int widthHint = converter.convertWidthInCharsToPixels(numChars);
		return widthHint;
	}
	
	public static int hintWidth(final Combo combo, final int numChars) {
		return hintWidth(combo, JFaceResources.DIALOG_FONT, numChars);
	}
	
	public static int hintWidth(final Combo combo, final String fontName, final int numChars) {
		combo.setFont(JFaceResources.getFontRegistry().get(fontName));
		if (numChars == -1) {
			return getDialogValues().defaultEntryFieldWidth;
		}
		final PixelConverter converter = new PixelConverter(combo);
		int widthHint = converter.convertWidthInCharsToPixels(numChars+1);
		
		final Rectangle trim = combo.computeTrim(0, 0, 0, 0);
		widthHint += trim.x + trim.width;
		
		if (trim.width == 0 && (combo.getStyle() & SWT.DROP_DOWN) == SWT.DROP_DOWN) {
			final Button button = new Button(combo.getParent(), SWT.ARROW | SWT.DOWN);
			widthHint += button.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 2;
			button.dispose();
//			widthHint += combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		}
		
		return widthHint;
	}
	
	public static GridData hintWidth(final GridData gd, final Combo combo, final int numChars) {
		return hintWidth(gd, combo, JFaceResources.DIALOG_FONT, numChars);
	}
	
	public static GridData hintWidth(final GridData gd, final Combo combo, final String fontName, final int numChars) {
		gd.widthHint = hintWidth(combo, fontName, numChars);
		return gd;
	}
	
	public static int hintWidth(final Combo combo, final String[] items) {
		int max = 0;
		for (final String s : items) {
			max = Math.max(max, s.length());
		}
		return hintWidth(combo, JFaceResources.DIALOG_FONT, max);
	}
	
	public static int hintWidth(final Table table, final int numChars) {
		table.setFont(JFaceResources.getFontRegistry().get(JFaceResources.DIALOG_FONT));
		final PixelConverter converter = new PixelConverter(table);
		int width = converter.convertWidthInCharsToPixels(numChars);
		{	final ScrollBar scrollBar = table.getVerticalBar();
			if (scrollBar != null) {
				width += scrollBar.getSize().x;
			}
		}
		if ((table.getStyle() & SWT.CHECK) == SWT.CHECK) {
			width += 16 + converter.convertHorizontalDLUsToPixels(4) +  converter.convertWidthInCharsToPixels(1);
		}
		return width;
	}
	
	public static int hintWidth(final Table table, final Collection<String> items) {
		int max = 0;
		for (final String s : items) {
			max = Math.max(max, s.length());
		}
		return hintWidth(table, max);
	}
	
	public static int hintWidth(final Table table, final Object[] input, final ILabelProvider labelProvider) {
		int max = 0;
		for (final Object o : input) {
			final String s = labelProvider.getText(o);
			if (s != null) {
				max = Math.max(max, s.length());
			}
		}
		return hintWidth(table, max);
	}
	
	public static int hintColWidth(final Table table, final int numChars) {
		table.setFont(JFaceResources.getFontRegistry().get(JFaceResources.DIALOG_FONT));
		final PixelConverter converter = new PixelConverter(table);
		final int width = converter.convertWidthInCharsToPixels(numChars);
		return width;
	}
	
	public static int hintHeight(final List control, final int rows) {
		control.setFont(JFaceResources.getFontRegistry().get(JFaceResources.DIALOG_FONT));
		return control.getItemHeight() * rows;
	}
	
	public static int hintHeight(final Tree control, final int rows) {
		return hintHeight(control, rows, true);
	}
	
	public static int hintHeight(final Tree control, final int rows, final boolean withScrollbar) {
		control.setFont(JFaceResources.getFontRegistry().get(JFaceResources.DIALOG_FONT));
		
		int height = control.getHeaderHeight();
		height += control.getItemHeight() * rows;
		
		if (!withScrollbar && Platform.getWS().equals(Platform.WS_WIN32)) {
			final ScrollBar hBar = control.getHorizontalBar();
			if (hBar != null) {
				height -= hBar.getSize().y;
			}
		}
		else if (Platform.getWS().equals(Platform.WS_WIN32)) {
			height += control.getBorderWidth() * 2;
		}
		
		return height;
	}
	
	public static int hintHeight(final Table control, final int rows) {
		return hintHeight(control, rows, true);
	}
	
	public static int hintHeight(final Table control, final int rows, final boolean withScrollbar) {
		control.setFont(JFaceResources.getFontRegistry().get(JFaceResources.DIALOG_FONT));
		
		int height = control.getHeaderHeight();
		height += control.getItemHeight() * rows;
		
		if (!withScrollbar && Platform.getWS().equals(Platform.WS_WIN32)) {
			final ScrollBar hBar = control.getHorizontalBar();
			if (hBar != null) {
				height -= hBar.getSize().y;
			}
		}
		else if (Platform.getWS().equals(Platform.WS_WIN32)) {
			height += control.getBorderWidth() * 2;
		}
		
		return height;
	}
	
	public static int hintHeight(final Label control, final int lines) {
		final PixelConverter converter = new PixelConverter(control);
		return converter.convertHeightInCharsToPixels(lines);
	}
	
	public static int hintHeight(final StyledText control, final int lines) {
		final PixelConverter converter = new PixelConverter(control);
		return converter.convertHeightInCharsToPixels(lines);
	}
	
	
	public static GridData createGD(final Button button) {
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = hintWidth(button);
		return gd;
	}
	
	
	public static GridLayout createDialogGrid(final int numColumns) {
		final GridLayout gl = new GridLayout(numColumns, false);
		gl.numColumns = numColumns;
		applyDialogDefaults(gl);
		return gl;
	}
	
	public static GridLayout applyDialogDefaults(final GridLayout gl, final int numColumns) {
		gl.numColumns = numColumns;
		applyDialogDefaults(gl);
		return gl;
	}
	
	public static GridLayout applyDialogDefaults(final GridLayout gl) {
		final DialogValues dialogValues = getDialogValues();
		gl.marginWidth = dialogValues.defaultHMargin;
		gl.marginHeight = dialogValues.defaultVMargin;
		gl.horizontalSpacing = dialogValues.defaultHSpacing;
		gl.verticalSpacing = dialogValues.defaultVSpacing;
		return gl;
	}
	
	@Deprecated
	public static GridLayout applyCompositeDefaults(final GridLayout gl, final int numColumns) {
		gl.numColumns = numColumns;
		applyCompositeDefaults(gl);
		return gl;
	}
	
	public static GridLayout applyCompositeDefaults(final GridLayout gl) {
		final DialogValues dialogValues = getDialogValues();
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		gl.horizontalSpacing = dialogValues.defaultHSpacing;
		gl.verticalSpacing = dialogValues.defaultVSpacing;
		return gl;
	}
	
	public static GridLayout createCompositeGrid(final int numColumns) {
		final GridLayout gl = new GridLayout(numColumns, false);
		applyCompositeDefaults(gl);
		return gl;
	}
	
	public static GridLayout createCompositeGrid(final int numColumns, final boolean equalWidth) {
		final GridLayout gl = new GridLayout(numColumns, equalWidth);
		applyCompositeDefaults(gl);
		return gl;
	}
	
	@Deprecated
	public static GridLayout applyGroupDefaults(final GridLayout gl, final int numColumns) {
		gl.numColumns = numColumns;
		applyGroupDefaults(gl);
		return gl;
	}
	
	public static GridLayout applyGroupDefaults(final GridLayout gl) {
		final DialogValues dialogValues = getDialogValues();
		gl.marginWidth = dialogValues.defaultHSpacing;
		gl.marginHeight = dialogValues.defaultVSpacing;
		gl.horizontalSpacing = dialogValues.defaultHSpacing;
		gl.verticalSpacing = dialogValues.defaultVSpacing;
		return gl;
	}
	
	public static GridLayout createGroupGrid(final int numColumns) {
		final GridLayout gl = new GridLayout(numColumns, false);
		applyGroupDefaults(gl);
		return gl;
	}
	
	public static GridLayout createGroupGrid(final int numColumns, final boolean equalWidth) {
		final GridLayout gl = new GridLayout(numColumns, equalWidth);
		applyGroupDefaults(gl);
		return gl;
	}
	
	public static GridLayout applyContentDefaults(final GridLayout gl) {
		final DialogValues dialogValues = getDialogValues();
		gl.marginWidth = dialogValues.defaultHSpacing;
		gl.marginHeight = dialogValues.defaultVSpacing;
		gl.horizontalSpacing = dialogValues.defaultHSpacing;
		gl.verticalSpacing = dialogValues.defaultVSpacing;
		return gl;
	}
	
	public static GridLayout createContentGrid(final int numColumns) {
		final GridLayout gl = new GridLayout(numColumns, false);
		applyContentDefaults(gl);
		return gl;
	}
	
	public static GridLayout createContentGrid(final int numColumns, final boolean equalWidth) {
		final GridLayout gl = new GridLayout(numColumns, equalWidth);
		applyContentDefaults(gl);
		return gl;
	}
	
	@Deprecated
	public static GridLayout applyTabDefaults(final GridLayout gl, final int numColumns) {
		gl.numColumns = numColumns;
		applyTabDefaults(gl);
		return gl;
	}
	
	public static GridLayout applyTabDefaults(final GridLayout gl) {
		final DialogValues dialogValues = getDialogValues();
		gl.marginWidth = dialogValues.defaultHSpacing;
		gl.marginHeight = dialogValues.defaultVSpacing;
		gl.horizontalSpacing = dialogValues.defaultHSpacing;
		gl.verticalSpacing = dialogValues.defaultVSpacing;
		return gl;
	}
	
	public static GridLayout createTabGrid(final int numColumns) {
		final GridLayout gl = new GridLayout(numColumns, false);
		applyTabDefaults(gl);
		return gl;
	}
	
	public static GridLayout createTabGrid(final int numColumns, final boolean equalWidth) {
		final GridLayout gl = new GridLayout(numColumns, equalWidth);
		applyTabDefaults(gl);
		return gl;
	}
	
	public static FillLayout applyTabDefaults(final FillLayout fl) {
		final DialogValues dialogValues = getDialogValues();
		fl.marginWidth = dialogValues.defaultHSpacing;
		fl.marginHeight = dialogValues.defaultVSpacing;
		fl.spacing = (fl.type == SWT.HORIZONTAL) ?
				dialogValues.defaultHSpacing : dialogValues.defaultVSpacing;
		return fl;
	}
	
	public static GridLayout applySashDefaults(final GridLayout gl, final int numColumns) {
		gl.numColumns = numColumns;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		return gl;
	}
	
	
	public static void addGDDummy(final Composite composite) {
		addGDDummy(composite, false);
	}
	public static void addGDDummy(final Composite composite, final boolean grab) {
		final Label dummy = new Label(composite, SWT.NONE);
		dummy.setVisible(false);
		dummy.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, grab, false));
	}
	public static void addGDDummy(final Composite composite, final boolean grab, final int span) {
		final Label dummy = new Label(composite, SWT.NONE);
		dummy.setVisible(false);
		dummy.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, grab, false, span, 1));
	}
	
	/**
	 * Adds a small vertical space (filler) to the given composite
	 * 
	 * @param composite The composite to add the filler to
	 * @param grab Whether the filler should grap vertical space
	 */
	public static void addSmallFiller(final Composite composite, final boolean grab) {
		final Label filler = new Label(composite, SWT.NONE);
		final Layout layout = composite.getLayout();
		if (layout instanceof GridLayout) {
			final GridData gd = new GridData(SWT.FILL, SWT.FILL, false, grab);
			gd.horizontalSpan = ((GridLayout) layout).numColumns;
			gd.heightHint = defaultVSpacing() / 2;
			filler.setLayoutData(gd);
		}
	}
	
	
	private LayoutUtil() {}
	
}
