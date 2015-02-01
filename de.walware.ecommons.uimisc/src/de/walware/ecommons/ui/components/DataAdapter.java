/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.components;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.viewers.ITreeContentProvider;


public class DataAdapter<ItemType> {
	
	
	public static class ListAdapter<ItemType> extends DataAdapter<ItemType> {
		
		
		final IObservableCollection fList;
		
		
		public ListAdapter(final IObservableCollection list, final IObservableValue defaultValue) {
			super(null, defaultValue);
			fList = list;
		}
		
		public ListAdapter(final ITreeContentProvider contentProvider,
				final IObservableCollection list, final IObservableValue defaultValue) {
			super(contentProvider, defaultValue);
			fList = list;
		}
		
		
		@Override
		protected Collection<? super ItemType> getContainerFor(final Object element) {
			return fList;
		}
		
		@Override
		public boolean isMoveAllowed(final Object element, final int direction) {
			if (!super.isMoveAllowed(element, direction)) {
				return false;
			}
			if (fList instanceof List) {
				final int oldIdx = ((List) fList).indexOf(element);
				final int newIdx = oldIdx + direction;
				return (oldIdx >= 0 && newIdx >= 0 & newIdx < fList.size());
			}
			return false;
		}
		
		@Override
		public Object change(final ItemType oldItem, final ItemType newItem,
				final Object parent, final Object container ) {
			final Collection list = (Collection) container;
			setDirty(true);
			if (oldItem == null) {
				if (fDefault != null && fList != null && fList.isEmpty()) {
					fDefault.setValue(newItem);
				}
				list.add(newItem);
			}
			else {
				changeDefault(oldItem, newItem);
				if (oldItem != newItem) { // can be directly manipulated or replaced)
					if (list instanceof List) {
						final int idx = ((List) list).indexOf(oldItem);
						((List) list).set(idx, newItem);
					}
					else {
						list.remove(oldItem);
						list.add(newItem);
					}
				}
			}
			final Object editElement = getViewerElement(newItem, parent);
			
			changeChecked(oldItem, newItem);
			return editElement;
		}
		
		@Override
		public void delete(final List<? extends Object> elements) {
			setDirty(true);
			deleteDefault(elements);
			if (fList != null) {
				fList.removeAll(elements);
			}
			else {
				for (final Object element : elements) {
					getContainerFor(element).remove(getModelItem(element));
				}
			}
			deleteChecked(elements);
		}
		
		@Override
		public void move(final Object item, final int direction) {
			final int oldIdx = ((IObservableList) fList).indexOf(item);
			final int newIdx = oldIdx + direction;
			if (oldIdx < 0 || newIdx < 0 || newIdx >= fList.size()) {
				return;
			}
			moveByIdx(oldIdx, newIdx);
		}
		
		protected void moveByIdx(final int oldIdx, final int newIdx) {
			((IObservableList) fList).move(oldIdx, newIdx);
		}
		
	}
	
	
	private final ITreeContentProvider fTreeProvider;
	
	private Set<ItemType> fCheckedSet;
	
	protected final IObservableValue fDefault;
	
	private boolean fIsDirty;
	
	
	public DataAdapter(final ITreeContentProvider contentProvider,
			final IObservableValue defaultValue) {
		fTreeProvider = contentProvider;
		fDefault = defaultValue;
	}
	
	
	public Object getAddParent(final Object element) {
		if (fTreeProvider != null && isContentItem(element)) {
			return getParent(element);
		}
		return element;
	}
	
	public Object getParent(final Object element) {
		if (fTreeProvider != null) {
			return fTreeProvider.getParent(element);
		}
		else {
			return null;
		}
	}
	
	public Object[] getChildren(final Object element) {
		if (fTreeProvider != null) {
			return fTreeProvider.getChildren(element);
		}
		else {
			return null;
		}
	}
	
	public boolean isAddAllowed(final Object element) {
		return true;
	}
	
	public boolean isContentItem(final Object element) {
		return true;
	}
	
	public boolean isModifyAllowed(final Object element) {
		return isContentItem(element);
	}
	
	public boolean isMoveAllowed(final Object element, final int direction) {
		return isModifyAllowed(element);
	}
	
	public boolean isDeleteAllowed(final Object element) {
		return isModifyAllowed(element);
	}
	
	public ItemType getModelItem(final Object element) {
		return (ItemType) element;
	}
	
	public Object getViewerElement(final ItemType item, final Object parent) {
		return item;
	}
	
	
	public void setCheckedModel(final Set<ItemType> set) {
		fCheckedSet = set;
	}
	
	protected Object getContainerFor(final Object element) {
		return null;
	}
	
	protected IObservableValue getDefaultFor(final ItemType item) {
		return fDefault;
	}
	
	public void setDefault(final ItemType item) {
		final IObservableValue observable= getDefaultFor(item);
		if (observable == null) {
			return;
		}
		fIsDirty = true;
		if (item != null) {
			observable.setValue(getDefaultValue(item));
		}
	}
	
	public Object change(final ItemType oldItem, final ItemType newItem,
			final Object parent, final Object container ) {
		throw new UnsupportedOperationException();
	}
	
	protected Object getDefaultValue(final ItemType item) {
		return item;
	}
	
	protected void changeDefault(final ItemType oldItem, final ItemType newItem) {
		if (oldItem == null) {
			return;
		}
		final IObservableValue observable= getDefaultFor(oldItem);
		if (observable == null) {
			return;
		}
		final Object oldValue = getDefaultValue(oldItem);
		final Object newValue = getDefaultValue(newItem);
		if (oldValue != newValue && oldValue.equals(observable.getValue())) {
			observable.setValue(newValue);
		}
	}
	
	protected void changeChecked(final ItemType oldItem, final ItemType newItem) {
		if (fCheckedSet != null) {
			if (oldItem == null) {
				fCheckedSet.add(newItem);
			}
			else {
				if (fCheckedSet.remove(oldItem)) {
					fCheckedSet.add(newItem);
				}
			}
		}
	}
	
	public void delete(final List<? extends Object> elements) {
		throw new UnsupportedOperationException();
	}
	
	protected void deleteDefault(final List<? extends Object> elements) {
		if (elements.isEmpty()) {
			return;
		}
		for (final Object element : elements) {
			final ItemType item = getModelItem(element);
			if (item == null) {
				continue;
			}
			final IObservableValue observable= getDefaultFor(item);
			if (observable == null) {
				continue;
			}
			final Object itemValue= getDefaultValue(item);
			if (itemValue != null && itemValue.equals(observable.getValue())) {
				observable.setValue(null);
				return;
			}
		}
	}
	
	protected void deleteChecked(final List<? extends Object> elements) {
		if (fCheckedSet != null) {
			fCheckedSet.removeAll(elements);
		}
	}
	
	public void move(final Object item, final int direction) {
		throw new UnsupportedOperationException();
	}
	
	
	public void setDirty(final boolean isDirty) {
		fIsDirty = isDirty;
	}
	
	public boolean isDirty() {
		return fIsDirty;
	}
	
}