/*******************************************************************************
 * Copyright (c) 2004-2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation of Filtered Tree
 *     Jacek Pospychala - bug 187762
 *     Stephan Wahlbrink
 *******************************************************************************/

package de.walware.ecommons.ui.content;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Event;

import de.walware.ecommons.FastList;
import de.walware.ecommons.collections.IntArrayMap;
import de.walware.ecommons.ui.content.IElementFilter.IFinalFilter;
import de.walware.ecommons.ui.util.UIAccess;


public class TableFilterController {
	
	
	public static interface Listener {
		
		
		void inputUpdated(boolean newInput);
		
	}
	
	private class RefreshJob extends Job implements ILazyContentProvider, Runnable {
		
		
		private int[] fTemp;
		
		private int fFilterInputId = -1;
		private IFinalFilter[] fCacheFilters = new IFinalFilter[4];
		private int[][] fCacheData = new int[4][];
		
		private int fCompleteInputId;
		private int[] fCompleteData;
		
		private int fActiveInputId = -1;
		private List<?> fActiveInput;
		private int[] fActiveData;
		
		private boolean fDisplayScheduled;
		
		
		public RefreshJob() {
			super("Refresh Filter"); //$NON-NLS-1$
			setPriority(Job.INTERACTIVE);
			setSystem(true);
		}
		
		@Override
		protected IStatus run(final IProgressMonitor monitor) { // background job
			final IElementFilter[] currentProvider;
			final List<?> currentInput;
			final int currentInputId;
			synchronized (TableFilterController.this) {
				currentProvider = fFilter.toArray(IElementFilter.class);
				currentInput = fInput;
				currentInputId = fInputId;
			}
			
			final IFinalFilter[] currentFilters = new IFinalFilter[currentProvider.length];
			{	final boolean newData = (currentInputId != fFilterInputId);
				{	for (int i = 0; i < currentFilters.length; i++) {
						final IElementFilter provider = currentProvider[i];
						currentFilters[i] = (provider != null) ? provider.getFinal(newData) : null;
					}
					if (newData) {
						fFilterInputId = currentInputId;
						for (int i = 0; i < fCacheFilters.length; i++) {
							fCacheFilters[i] = null;
							fCacheData[i] = null;
						}
					}
				}
			}
			if (fCacheFilters.length < currentFilters.length) {
				fCacheFilters = Arrays.copyOf(fCacheFilters, currentFilters.length);
				fCacheData = Arrays.copyOf(fCacheData, currentFilters.length);
			}
			if (fTemp == null || fTemp.length < currentInput.size()) {
				fTemp = new int[currentInput.size()];
			}
			
			final int[][] data = new int[currentFilters.length][];
			ITER_FILTER: for (int filterIdx = 0; filterIdx < currentFilters.length; filterIdx++) {
				final IFinalFilter filter = currentFilters[filterIdx];
				if (filter == null) {
					continue ITER_FILTER;
				}
				
				if (fCacheFilters[filterIdx] != null) {
					if (filter.isSubOf(fCacheFilters[filterIdx])) {
						final int[] prev = fCacheData[filterIdx];
						if (filter.isEqualTo(fCacheFilters[filterIdx])) {
							// equal filter
							data[filterIdx] = prev;
							continue ITER_FILTER;
						}
						if (prev != null) {
							// sub filter
							final int[] filtered = fTemp;
							int idx = 0;
							final int num = prev.length;
							for (int prevIdx = 0; prevIdx < num; prevIdx++) {
								if ((prevIdx % 50) == 0 && monitor.isCanceled()) {
									return Status.CANCEL_STATUS;
								}
								if (filter.select(currentInput.get(prev[prevIdx]))) {
									filtered[idx++] = prev[prevIdx];
								}
							}
							if (idx == num) {
								data[filterIdx] = prev;
							}
							else {
								data[filterIdx] = Arrays.copyOf(filtered, idx);
							}
							fCacheFilters[filterIdx] = filter;
							fCacheData[filterIdx] = data[filterIdx];
							continue ITER_FILTER;
						}
					}
				}
				{	// new filter
					final int[] filtered = fTemp;
					int idx = 0;
					final int num = currentInput.size();
					for (int prevIdx = 0; prevIdx < num; prevIdx++) {
						if ((prevIdx % 50) == 0 && monitor.isCanceled()) {
							return Status.CANCEL_STATUS;
						}
						if (filter.select(currentInput.get(prevIdx))) {
							filtered[idx++] = prevIdx;
						}
					}
					if (idx == num) {
						data[filterIdx] = null;
					}
					else {
						data[filterIdx] = Arrays.copyOf(filtered, idx);
					}
					fCacheFilters[filterIdx] = filter;
					fCacheData[filterIdx] = data[filterIdx];
					continue;
				}
			}
			
			int[] merge = null;
			int num = -1;
			ITER_FILTER: for (int filterIdx = 0; filterIdx < currentFilters.length; filterIdx++) {
				final int[] filtered = data[filterIdx];
				if (filtered == null) {
					continue ITER_FILTER;
				}
				if (merge == null) {
					merge = filtered;
					continue ITER_FILTER;
				}
				if (merge != fTemp) {
					num = merge.length;
					System.arraycopy(merge, 0, fTemp, 0, num);
					merge = fTemp;
				}
				
				int idx = 0;
				ITER_I: for (int i = 0, j = 0; i < num; i++) {
					final int tmp = merge[i];
					ITER_J: while (j < filtered.length) {
						if (filtered[j] < tmp) {
							j++;
							continue ITER_J;
						}
						else if (filtered[j] == tmp) {
							merge[idx++] = tmp;
							j++;
							continue ITER_I;
						}
						else {
							continue ITER_I;
						}
					}
					break ITER_I;
				}
				num = idx;
			}
			if (merge != null && num >= 0 && (merge.length != num || merge == fTemp)) {
				merge = Arrays.copyOf(merge, num);
			}
			
			synchronized (TableFilterController.this) {
				fCompleteInputId = currentInputId;
				fCompleteData = merge;
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				if (fDisplayScheduled) {
					return Status.OK_STATUS;
				}
				fDisplayScheduled = true;
			}
			fViewer.getTable().getDisplay().asyncExec(this);
			return Status.OK_STATUS;
		}
		
		@Override
		public void run() { // display runnable
			if (!UIAccess.isOkToUse(fViewer)) {
				return;
			}
			final int[] completeData;
			final int completeInputId;
			final List<?> input;
			final int inputId;
			synchronized (TableFilterController.this) {
				completeData = fCompleteData;
				completeInputId = fCompleteInputId;
				input = fInput;
				inputId = fInputId;
				fDisplayScheduled = false;
			}
			
			boolean newInput = (fActiveInputId != completeInputId);
			if (completeInputId == inputId) {
				IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
				int idx;
				final int count = - 1 + fViewer.getTable().getClientArea().height / fViewer.getTable().getItemHeight();
				if (!newInput && (completeData == null || completeData.length >= count)) {
					idx = fViewer.getTable().getTopIndex();
					if (idx >= 0) {
						int idx2 = -1;
						if (count > 0) {
							final int[] idxs = fViewer.getTable().getSelectionIndices();
							for (int i = 0; i < idxs.length; i++) {
								if (idxs[i] >= idx && idxs[i] < idx + count) {
									idx2 = idxs[i];
									break;
								}
							}
						}
						idx = active2model(idx);
						idx2 = active2model(idx2);
						if (completeData != null) {
							if (idx >= 0) {
								idx = Arrays.binarySearch(completeData, idx);
								if (idx < 0) {
									idx = -idx - 1;
								}
							}
							if (idx2 >= 0) {
								idx2 = Arrays.binarySearch(completeData, idx2);
							}
						}
						if (idx >= 0 && idx2 >= 0 && idx2 - idx > count) {
							idx = idx2 - count + 1;
						}
					}
				}
				else {
					idx = 0;
					if (input != null && completeData != null && completeData.length == 1) {
						selection = new StructuredSelection(input.get(completeData[0]));
					}
				}
				
				fActiveInputId = completeInputId;
				fActiveInput = input;
				fActiveData = completeData;
				
				fViewer.setItemCount((completeData != null) ? completeData.length : input.size());
				if (idx >= 0) {
					fViewer.getTable().setTopIndex(idx);
				}
				fViewer.refresh(newInput);
				
				fViewer.setSelection(selection);
				
				fViewer.getTable().getParent().layout();
			}
			else if (fActiveInput != null) {
				newInput = true;
				fActiveInput = null;
				fActiveData = null;
				fViewer.setItemCount(0);
				fViewer.refresh(true);
			}
			else {
				return;
			}
			
			notifiyListeners(newInput);
		}
		
		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		}
		
		@Override
		public void dispose() {
		}
		
		private int active2model(final int index) {
			if (fActiveInput != null && index >= 0) {
				if (fActiveData != null) {
					return (index < fActiveData.length && fActiveData[index] < fActiveInput.size()) ?
							fActiveData[index] : -1;
				}
				else {
					return (index < fActiveInput.size()) ?
							index : -1;
				}
			}
			return -1;
		}
		
		private int model2active(final int index) {
			if (fActiveInput != null && index >= 0) {
				if (fActiveData != null) {
					final int idx = Arrays.binarySearch(fActiveData, index);
					return (idx >= 0) ? idx : -1;
				}
				else {
					return (index < fActiveInput.size()) ?
							index : -1;
				}
			}
			return -1;
		}
		
		@Override
		public void updateElement(final int index) {
			final int idx = active2model(index);
			if (idx >= 0) {
				fViewer.replace(fActiveInput.get(idx), index);
				return;
			}
		}
		
	}
	
	
	/**
	 * The viewer for the filtered table. This value should never be
	 * <code>null</code> after the widget creation methods are complete.
	 */
	private final TableViewer fViewer;
	
	private int fUpdate;
	
	/**
	 * The job used to refresh the tree.
	 */
	private final RefreshJob fRefreshJob;
	
	private List<?> fInput = Collections.emptyList();
	
	private int fInputId;
	
	private final IntArrayMap<IElementFilter> fFilter = new IntArrayMap<IElementFilter>(8);
	private int fMaxFilter = -1;
	
	private final FastList<Listener> fListener = new FastList<Listener>(Listener.class);
	
	
	/**
	 * Create a new instance of the controller.
	 * 
	 * @param viewer the table viewer to connect to
	 * @param the search widget to connect to
	 */
	public TableFilterController(final TableViewer viewer) {
		fViewer = viewer;
		
		fRefreshJob = new RefreshJob();
		fViewer.getTable().getVerticalBar().setVisible(true);
		fViewer.setContentProvider(fRefreshJob);
		fViewer.setInput(this);
		
		viewer.getControl().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				fRefreshJob.cancel();
			}
		});
	}
	
	
	/**
	 * Return the time delay that should be used when scheduling the
	 * filter refresh job.  Subclasses may override.
	 * 
	 * @return a time delay in milliseconds before the job should run
	 */
	protected long getRefreshJobDelay() {
		return 10;
	}
	
	
	public void addListener(final Listener listener) {
		fListener.add(listener);
	}
	
	public void removeListener(final Listener listener) {
		fListener.remove(listener);
	}
	
	protected void notifiyListeners(final boolean newInput) {
		final Listener[] array = fListener.toArray();
		
		for (final Listener listener : array) {
			listener.inputUpdated(newInput);
		}
	}
	
	protected boolean isUpToDate() {
		if (fUpdate == 0 && fRefreshJob.getState() == Job.NONE) {
			synchronized (this) {
				return (!fRefreshJob.fDisplayScheduled);
			}
		}
		return false;
	}
	
	
	/**
	 * Get the tree viewer of the receiver.
	 * 
	 * @return the tree viewer
	 */
	public TableViewer getViewer() {
		return fViewer;
	}
	
	public void setFilter(final int id, final IElementFilter provider) {
		synchronized (this) {
			if (id > fMaxFilter) {
				fMaxFilter = id;
			}
			fFilter.put(id, provider);
		}
	}
	
	public int addFilter(final IElementFilter provider) {
		synchronized (this) {
			fMaxFilter++;
			fFilter.put(fMaxFilter, provider);
			return fMaxFilter;
		}
	}
	
	public void setInput(final List<?> input) {
		if (input == null) {
			throw new NullPointerException("input"); //$NON-NLS-1$
		}
		synchronized (this) {
			fInput = input;
			fInputId++;
		}
		if (fUpdate == 0) {
			fRefreshJob.schedule();
		}
	}
	
	public void refresh(final boolean direct) {
		fRefreshJob.cancel();
		fRefreshJob.schedule(direct ? 0 : getRefreshJobDelay());
	}
	
	public void startUpdate() {
		if (fUpdate == 0) {
			fRefreshJob.cancel();
		}
		fUpdate++;
	}
	
	public void endUpdate() {
		fUpdate--;
		if (fUpdate == 0) {
			fRefreshJob.schedule();
		}
	}
	
	public void setSelection(final Object element) {
		if (fUpdate == 0 && fRefreshJob.fActiveInput != null) {
			int idx = fRefreshJob.fActiveInput.indexOf(element);
			if (idx < 0) {
				return;
			}
			idx = fRefreshJob.model2active(idx);
			if (idx < 0) {
				return;
			}
			fViewer.getTable().setSelection(idx);
			fViewer.getTable().showSelection();
			fViewer.getTable().notifyListeners(SWT.Selection, new Event());
		}
	}
	
	public void setSelection(final List<?> elements) {
		if (fUpdate == 0 && fRefreshJob.fActiveInput != null) {
			int[] idxs = new int[elements.size()];
			int i = 0;
			for (Object element : elements) {
				int idx = fRefreshJob.fActiveInput.indexOf(element);
				if (idx < 0) {
					continue;
				}
				idx = fRefreshJob.model2active(idx);
				if (idx < 0) {
					continue;
				}
				idxs[i++] = idx;
			}
			if (i != idxs.length) {
				idxs = Arrays.copyOf(idxs, i);
			}
			fViewer.getTable().setSelection(idxs);
			fViewer.getTable().showSelection();
			fViewer.getTable().notifyListeners(SWT.Selection, new Event());
		}
	}
	
	public void schedule(final Runnable runnable) {
		if (isUpToDate()) {
			runnable.run();
			return;
		}
		addListener(new Listener() {
			@Override
			public void inputUpdated(boolean newInput) {
				removeListener(this);
				runnable.run();
			}
		});
	}
	
}
