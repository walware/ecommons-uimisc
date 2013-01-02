/*******************************************************************************
 * Copyright (c) 2012-2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.style.editor;

import static org.eclipse.swt.SWT.NONE;

import org.eclipse.nebula.widgets.nattable.Messages;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle.LineStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * Component to select a {@link LineStyle}.
 */
public class LineStylePicker extends Composite {
    
    private Combo combo;
    
    public LineStylePicker(Composite parent) {
        super(parent, NONE);
        setLayout(new RowLayout());
        
        combo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
        combo.setItems(new String[] { Messages.getString("LineStylePicker.solid"), Messages.getString("LineStylePicker.dashed"), Messages.getString("LineStylePicker.dotted"), Messages.getString("LineStylePicker.dashdot"), Messages.getString("LineStylePicker.dashdotdot") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        combo.select(0);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        combo.setEnabled(enabled);
    }
    
    public void setSelectedLineStyle(LineStyle lineStyle) {
        int index = 0;
        if (lineStyle.equals(LineStyle.SOLID)) index = 0;
        else if (lineStyle.equals(LineStyle.DASHED)) index = 1;
        else if (lineStyle.equals(LineStyle.DOTTED)) index = 2;
        else if (lineStyle.equals(LineStyle.DASHDOT)) index = 3;
        else if (lineStyle.equals(LineStyle.DASHDOTDOT)) index = 4;
        combo.select(index);
    }
    
    public LineStyle getSelectedLineStyle() {
        int index = combo.getSelectionIndex();
        if (index == 0) return LineStyle.SOLID;
        else if (index == 1) return LineStyle.DASHED;
        else if (index == 2) return LineStyle.DOTTED;
        else if (index == 3) return LineStyle.DASHDOT;
        else if (index == 4) return LineStyle.DASHDOTDOT;
        else throw new IllegalStateException("never happen"); //$NON-NLS-1$
    }

}
