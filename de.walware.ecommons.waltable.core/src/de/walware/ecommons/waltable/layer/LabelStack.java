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
package de.walware.ecommons.waltable.layer;

import java.util.ArrayList;
import java.util.List;


public class LabelStack {
	
	
	private final List<String> labels= new ArrayList<>(8);
	
	
	public LabelStack() {
	}
	
	public LabelStack(final String label) {
		this.labels.add(label);
	}
	
	public LabelStack(final String... labels) {
		for (final String label : labels) {
			this.labels.add(label);
		}
	}
	
	
	/**
	 * Adds a label to the bottom of the label stack.
	 * @param label
	 */
	public void addLabel(final String label) {
		if (!this.labels.contains(label)){
			this.labels.add(label);
		}
	}
	
	/**
	 * Adds a label to the top of the label stack.
	 * @param label
	 */
	public void addLabelOnTop(final String label) {
		final int idx= this.labels.indexOf(label);
		if (idx == 0) {
			return;
		}
		else if (idx > 0) {
			this.labels.remove(idx);
		}
		this.labels.add(0, label);
	}
	
	public void removeLabel(final String label) {
		this.labels.remove(label);
	}
	
	
	public List<String> getLabels() {
		return this.labels;
	}
	
	public boolean hasLabel(final String label) {
		return this.labels.contains(label);
	}
	
	
	@Override
	public int hashCode() {
		return this.labels.hashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof LabelStack)) {
			return false;
		}
		final LabelStack other= (LabelStack) obj;
		return this.labels.equals(other.labels);
	}
	
	@Override
	public String toString() {
		return this.labels.toString();
	}
	
}
