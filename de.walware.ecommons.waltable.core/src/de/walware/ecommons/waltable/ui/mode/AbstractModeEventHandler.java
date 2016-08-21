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
package de.walware.ecommons.waltable.ui.mode;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;


public class AbstractModeEventHandler implements IModeEventHandler {

	private final ModeSupport modeSupport;
	
	public AbstractModeEventHandler(final ModeSupport modeSupport) {
		this.modeSupport= modeSupport;
	}
	
	protected ModeSupport getModeSupport() {
		return this.modeSupport;
	}
	
	protected void switchMode(final String mode) {
		this.modeSupport.switchMode(mode);
	}
	
	protected void switchMode(final IModeEventHandler modeEventHandler) {
		this.modeSupport.switchMode(modeEventHandler);
	}
	
	@Override
	public void cleanup() {
	}
	
	@Override
	public void keyPressed(final KeyEvent event) {
	}

	@Override
	public void keyReleased(final KeyEvent event) {
	}

	@Override
	public void mouseDoubleClick(final MouseEvent event) {
	}

	@Override
	public void mouseDown(final MouseEvent event) {
	}

	@Override
	public void mouseUp(final MouseEvent event) {
	}

	@Override
	public void mouseMove(final MouseEvent event) {
	}
	
	@Override
	public void mouseEnter(final MouseEvent e) {
	}
	
	@Override
	public void mouseExit(final MouseEvent e) {
	}
	
	@Override
	public void mouseHover(final MouseEvent e) {
	}

	@Override
	public void focusGained(final FocusEvent event) {
	}

	@Override
	public void focusLost(final FocusEvent event) {
		switchMode(Mode.NORMAL_MODE);
	}

}
