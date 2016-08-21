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
// ~
package de.walware.ecommons.waltable.style.editor;

import static org.eclipse.swt.SWT.NONE;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import de.walware.ecommons.waltable.Messages;
import de.walware.ecommons.waltable.style.BorderStyle.LineStyle;


/**
 * Component to select a {@link LineStyle}.
 */
public class LineStylePicker extends Composite {
    
    private final Combo combo;
    
    public LineStylePicker(final Composite parent) {
        super(parent, NONE);
        setLayout(new RowLayout());
        
        this.combo= new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
        this.combo.setItems(new String[] { Messages.getString("LineStylePicker.solid"), Messages.getString("LineStylePicker.dashed"), Messages.getString("LineStylePicker.dotted"), Messages.getString("LineStylePicker.dashdot"), Messages.getString("LineStylePicker.dashdotdot") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        this.combo.select(0);
    }
    
    @Override
    public void setEnabled(final boolean enabled) {
        this.combo.setEnabled(enabled);
    }
    
    public void setSelectedLineStyle(final LineStyle lineStyle) {
        int index= 0;
        if (lineStyle.equals(LineStyle.SOLID)) {
			index= 0;
		} else if (lineStyle.equals(LineStyle.DASHED)) {
			index= 1;
		} else if (lineStyle.equals(LineStyle.DOTTED)) {
			index= 2;
		} else if (lineStyle.equals(LineStyle.DASHDOT)) {
			index= 3;
		} else if (lineStyle.equals(LineStyle.DASHDOTDOT)) {
			index= 4;
		}
        this.combo.select(index);
    }
    
    public LineStyle getSelectedLineStyle() {
        final int index= this.combo.getSelectionIndex();
        if (index == 0) {
			return LineStyle.SOLID;
		} else if (index == 1) {
			return LineStyle.DASHED;
		} else if (index == 2) {
			return LineStyle.DOTTED;
		} else if (index == 3) {
			return LineStyle.DASHDOT;
		} else if (index == 4) {
			return LineStyle.DASHDOTDOT;
		}
		else {
			throw new IllegalStateException("never happen"); //$NON-NLS-1$
		}
    }

}
