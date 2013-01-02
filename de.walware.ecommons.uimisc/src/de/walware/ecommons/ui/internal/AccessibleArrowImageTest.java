/*******************************************************************************
 * Copyright (c) 2012-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class AccessibleArrowImageTest {
	
	
	public static void main(final String[] args) {
		final int[] directions = new int[] { SWT.UP, SWT.DOWN, SWT.LEFT, SWT.RIGHT };
		final int size = 4;
		
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout (new GridLayout(directions.length, false));
		
		for (int i = 0; i < directions.length; i++) {
			final AccessibleArrowImage image = new AccessibleArrowImage(directions[i], size,
					display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND).getRGB(),
					display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND).getRGB() );
			final Button button = new Button(shell, SWT.PUSH);
			button.setImage(image.createImage());
		}
		shell.setSize(300, 300);
		shell.open();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) {
				display.sleep ();
			}
		}
		display.dispose ();
	}
	
}
