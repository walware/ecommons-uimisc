package net.sourceforge.nattable.layer;

import java.util.LinkedList;
import java.util.List;


public class LabelStack {
	
	/** 
	 * List implementation saves the overhead of popping labels off
	 * in the {@link #getLabels()} method
	 */
	private final List<String> labels = new LinkedList<String>();
	
	
	public LabelStack(String...labelNames) {
		for (String label : labelNames) {
			if (label != null) {
				labels.add(label);
			}
		}
	}
	
	
	/**
	 * Adds a label to the bottom of the label stack.
	 * @param label
	 */
	public void addLabel(String label) {
		if(! hasLabel(label)){
			labels.add(label);
		}
	}
	
	/**
	 * Adds a label to the top of the label stack.
	 * @param label
	 */
	public void addLabelOnTop(String label) {
		if(! hasLabel(label)){
			labels.add(0, label);
		}
	}
	
	public List<String> getLabels() {
		return labels;
	}
	
	public boolean hasLabel(String label) {
		return labels.contains(label);
	}
	
	
	public boolean removeLabel(String label) {
		return labels.remove(label);
	}
	
	
	@Override
	public int hashCode() {
		return labels.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof LabelStack)) {
			return false;
		}
		final LabelStack other = (LabelStack) obj;
		return labels.equals(other.labels);
	}
	
	@Override
	public String toString() {
		return labels.toString();
	}
	
}
