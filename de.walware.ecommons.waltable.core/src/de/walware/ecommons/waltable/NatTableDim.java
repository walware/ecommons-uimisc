/*=============================================================================#
 # Copyright (c) 2005-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.waltable;

import static de.walware.ecommons.waltable.painter.cell.GraphicsUtils.check;

import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.layer.ForwardLayerDim;
import de.walware.ecommons.waltable.layer.ILayerDim;


public class NatTableDim extends ForwardLayerDim<NatTable> {
	
	
	public NatTableDim(final NatTable layer, final ILayerDim underlyingDim) {
		super(layer, underlyingDim);
	}
	
	
	@Override
	public final NatTable getLayer() {
		return this.layer;
	}
	
	public void repaintPosition(final long position) {
		if (position == POSITION_NA) {
			return;
		}
		final long start= getPositionStart(position);
		this.layer.repaint(this.orientation, check(start), getPositionSize(position));
	}
	
	public void repaintPositions(final LRange positions) {
		if (positions.size() == 0) {
			return;
		}
		final long start= getPositionStart(positions.start);
		final long end= ((positions.size() == 1) ? start : getPositionStart(positions.end - 1))
				+ getPositionSize(positions.end - 1);
		this.layer.repaint(this.orientation, check(start), check(end - start));
	}
	
}
