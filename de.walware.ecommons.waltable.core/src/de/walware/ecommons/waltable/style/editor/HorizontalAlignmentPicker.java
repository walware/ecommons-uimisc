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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import de.walware.ecommons.waltable.Messages;
import de.walware.ecommons.waltable.style.HorizontalAlignment;


/**
 * Component that lets the user select an alignment.
 */
public class HorizontalAlignmentPicker extends Composite {

    private final Combo combo;

    public HorizontalAlignmentPicker(final Composite parent, final HorizontalAlignment alignment) {
        super(parent, SWT.NONE);
        setLayout(new RowLayout());

        this.combo= new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
        this.combo.setItems(new String[] { Messages.getString("HorizontalAlignmentPicker.center"), Messages.getString("HorizontalAlignmentPicker.left"), Messages.getString("HorizontalAlignmentPicker.right") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        update(alignment);
    }

    private void update(final HorizontalAlignment alignment) {
        if (alignment.equals(HorizontalAlignment.CENTER)) {
			this.combo.select(0);
		} else if (alignment.equals(HorizontalAlignment.LEFT)) {
			this.combo.select(1);
		} else if (alignment.equals(HorizontalAlignment.RIGHT)) {
			this.combo.select(2);
		}
		else {
			throw new IllegalArgumentException("bad alignment: " + alignment); //$NON-NLS-1$
		}
    }

    public HorizontalAlignment getSelectedAlignment() {
        final long idx= this.combo.getSelectionIndex();
        if (idx == 0) {
			return HorizontalAlignment.CENTER;
		} else if (idx == 1) {
			return HorizontalAlignment.LEFT;
		} else if (idx == 2) {
			return HorizontalAlignment.RIGHT;
		}
		else {
			throw new IllegalStateException("shouldn't happen"); //$NON-NLS-1$
		}
    }

    public void setSelectedAlignment(final HorizontalAlignment horizontalAlignment) {
        if (horizontalAlignment == null)
		 {
			throw new IllegalArgumentException("null"); //$NON-NLS-1$
		}
        update(horizontalAlignment);
    }
    
}
