/*******************************************************************************
 * Copyright (c) 2012, 2013 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.selection.command;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.nebula.widgets.nattable.command.AbstractDimPositionsCommand;
import org.eclipse.nebula.widgets.nattable.command.LayerCommandUtil;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.ILayerDim;


/**
 * Abstract command to select column(s)/row(s).
 */
public abstract class AbstractSelectDimPositionsCommand extends AbstractDimPositionsCommand {
	
	
	private final int selectionFlags;
	
	private long positionToReveal;
	
	
	public AbstractSelectDimPositionsCommand(final ILayerDim dim,
			final long position,
			final int selectionFlags) {
		this(dim, position,
				Collections.singletonList(new Range(position)),
				position, selectionFlags );
	}
	
	public AbstractSelectDimPositionsCommand(final ILayerDim dim,
			final long refPosition, final Collection<Range> positions,
			final long positionToReveal, final int selectionFlags) {
		super(dim, refPosition, positions);
		
		this.positionToReveal = positionToReveal;
		this.selectionFlags = selectionFlags;
	}
	
	protected AbstractSelectDimPositionsCommand(final AbstractSelectDimPositionsCommand command) {
		super(command);
		
		this.positionToReveal = command.positionToReveal;
		this.selectionFlags = command.selectionFlags;
	}
	
	
	public int getSelectionFlags() {
		return this.selectionFlags;
	}
	
	public long getPositionToReveal() {
		return this.positionToReveal;
	}
	
	
	@Override
	protected boolean convertToTargetLayer(final ILayerDim dim, final long refPosition,
			final ILayerDim targetDim) {
		if (super.convertToTargetLayer(dim, refPosition, targetDim)) {
			if (this.positionToReveal != Long.MIN_VALUE) {
				this.positionToReveal = (this.positionToReveal == refPosition) ?
						getRefPosition() :
						LayerCommandUtil.convertPositionToTargetContext(dim,
								refPosition, this.positionToReveal, targetDim );
			}
			return true;
		}
		return false;
	}
	
}
