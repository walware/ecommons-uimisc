/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package de.walware.ecommons.waltable.style.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Shell;

import de.walware.ecommons.waltable.Messages;
import de.walware.ecommons.waltable.util.GUIHelper;

/**
 * A button that displays a font name and allows the user to pick another font. 
 */
public class FontPicker extends Button {
    
	private Font originalFont;
    private Font selectedFont;
    private final FontData[] fontData= new FontData[1];
    private Font displayFont; 
    
    public FontPicker(final Composite parent, final Font originalFont) {
        super(parent, SWT.NONE);
        if (originalFont == null)
		 {
			throw new IllegalArgumentException("null"); //$NON-NLS-1$
		}
        
        update(originalFont.getFontData()[0]);
        
        addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(final SelectionEvent e) {
                    	final FontDialog dialog= new FontDialog(new Shell(Display.getDefault(), SWT.SHELL_TRIM));
                        dialog.setFontList(FontPicker.this.fontData);
                        final FontData selected= dialog.open();
                        if (selected != null) {                            
                            update(selected);
                            pack(true);
                        }
                    }
                });
    }
    
    private void update(final FontData data) {
        this.fontData[0]= data;
        this.selectedFont= GUIHelper.getFont(data);
        if (this.originalFont == null) {
        	this.originalFont= this.selectedFont;
        }
        setText(data.getName() + ", " + data.getHeight() + "pt"); //$NON-NLS-1$ //$NON-NLS-2$
        setFont(createDisplayFont(data));
        setAlignment(SWT.CENTER);
        setToolTipText(Messages.getString("FontPicker.tooltip")); //$NON-NLS-1$
    }
    
    private Font createDisplayFont(final FontData data) {
        final FontData resizedData= new FontData(data.getName(), data.getHeight(), data.getStyle());
        this.displayFont= GUIHelper.getFont(resizedData);
        return this.displayFont;
    }
    
    /**
     * @return Font selected by the user. <em>Note that it is the responsibility of the client to dispose of this
     *         resource.</em>
     */
    public Font getSelectedFont() {
        return this.selectedFont;
    }
    
    public Font getOriginalFont() {
		return this.originalFont;
	}
    
    /**
     * Set the selected font. <em>Note that this class will not take ownership of the passed resource. Instead it will
     * create and manage its own internal copy.</em>
     */
    public void setOriginalFont(final Font font) {
        if (font != null) {
        	this.originalFont= font;
        	update(font.getFontData()[0]);
        }
    }

    @Override
    protected void checkSubclass() {
        ; // do nothing
    }
}

    
