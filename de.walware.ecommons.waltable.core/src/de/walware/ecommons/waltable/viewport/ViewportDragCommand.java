/*******************************************************************************
 * Copyright (c) 2012-2016 Edwin Park and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Edwin Park - initial API and implementation
 ******************************************************************************/
package de.walware.ecommons.waltable.viewport;

import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.layer.ILayer;

public class ViewportDragCommand implements ILayerCommand {

	private final long x;
	private final long y;

	public ViewportDragCommand(final long x, final long y) {
		this.x= x;
		this.y= y;
	}
	
	public long getX() {
		return this.x;
	}
	
	public long getY() {
		return this.y;
	}
	
	@Override
	public boolean convertToTargetLayer(final ILayer targetLayer) {
		return true;
	}

	@Override
	public ILayerCommand cloneCommand() {
		return new ViewportDragCommand(this.x, this.y);
	}

}
