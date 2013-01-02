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


import org.eclipse.nebula.widgets.nattable.Messages;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignment;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * Component that lets the user select an alignment.
 */
public class HorizontalAlignmentPicker extends Composite {

    private final Combo combo;

    public HorizontalAlignmentPicker(Composite parent, HorizontalAlignment alignment) {
        super(parent, SWT.NONE);
        setLayout(new RowLayout());

        combo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
        combo.setItems(new String[] { Messages.getString("HorizontalAlignmentPicker.center"), Messages.getString("HorizontalAlignmentPicker.left"), Messages.getString("HorizontalAlignmentPicker.right") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        update(alignment);
    }

    private void update(HorizontalAlignment alignment) {
        if (alignment.equals(HorizontalAlignment.CENTER))
            combo.select(0);
        else if (alignment.equals(HorizontalAlignment.LEFT))
            combo.select(1);
        else if (alignment.equals(HorizontalAlignment.RIGHT))
            combo.select(2);
        else
            throw new IllegalArgumentException("bad alignment: " + alignment); //$NON-NLS-1$
    }

    public HorizontalAlignment getSelectedAlignment() {
        int idx = combo.getSelectionIndex();
        if (idx == 0)
            return HorizontalAlignment.CENTER;
        else if (idx == 1)
            return HorizontalAlignment.LEFT;
        else if (idx == 2)
            return HorizontalAlignment.RIGHT;
        else
            throw new IllegalStateException("shouldn't happen"); //$NON-NLS-1$
    }

    public void setSelectedAlignment(HorizontalAlignment horizontalAlignment) {
        if (horizontalAlignment == null) throw new IllegalArgumentException("null"); //$NON-NLS-1$
        update(horizontalAlignment);
    }
}
