/*******************************************************************************
 * Copyright (c) 2004-2010 IBM Corporation and others.
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

package de.walware.ecommons.ui.components;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.dialogs.SearchPattern;

import de.walware.ecommons.ui.util.UIAccess;


public class FilteredTableController {
	
	
	private class RefreshJob extends Job implements IStructuredContentProvider {
		
		private String fLastText;
		
		private Object[] fFilteredInput;
		
		public RefreshJob() {
			super("Refresh Filter"); //$NON-NLS-1$
			setPriority(Job.INTERACTIVE);
			setSystem(true);
		}
		
		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			final String text = fFilterText;
			final List<?> input = fInput;
			if (text.equals(fLastText)) {
				return Status.OK_STATUS;
			}
			
			List<Object> filteredList;
			if (text.length() == 0) {
				filteredList = new ArrayList<Object>(input);
			}
			else {
				filteredList = new ArrayList<Object>(input.size());
				final SearchPattern pattern = createSearchPattern(text);
				for (final Object object : input) {
					if (include(object, pattern)) {
						filteredList.add(object);
					}
				}
			}
			final Object[] array = filteredList.toArray();
			filteredList = null;
			
			fLastText = text;
			fViewer.getTable().getDisplay().syncExec(new Runnable() {
				public void run() {
					fFilteredInput = array;
					if (!UIAccess.isOkToUse(fViewer)) {
						return;
					}
					fViewer.refresh();
					fViewer.getTable().getParent().layout();
				}
			});
			
			return Status.OK_STATUS;
		}
		
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		}
		
		public Object[] getElements(final Object inputElement) {
			return (fFilteredInput != null) ? fFilteredInput : new Object[0];
		}
		
		public void dispose() {
		}
		
	}
	
	
	/**
	 * The filter text widget to be used by this viewer. This value may be
	 * <code>null</code> if there is no filter widget, or if the controls have
	 * not yet been created.
	 */
	protected SearchText fFilteredText;
	
	/**
	 * The viewer for the filtered table. This value should never be
	 * <code>null</code> after the widget creation methods are complete.
	 */
	protected TableViewer fViewer;
	
	
	/**
	 * The job used to refresh the tree.
	 */
	private final RefreshJob fRefreshJob;
	
	/**
	 * Last applied filter text
	 */
	private volatile String fFilterText = "";
	
	private List<?> fInput;
	
	
	/**
	 * Create a new instance of the controller.
	 * 
	 * @param viewer the table viewer to connect to
	 * @param the search widget to connect to
	 */
	public FilteredTableController(final TableViewer viewer, final SearchText text) {
		fViewer = viewer;
		fFilteredText = text;
		
		fRefreshJob = new RefreshJob();
		fViewer.getTable().getVerticalBar().setVisible(true);
		fViewer.setContentProvider(fRefreshJob);
		fViewer.setInput(this);
		
		fFilteredText.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {
				fRefreshJob.cancel();
			}
		});
		fFilteredText.addListener(new SearchText.Listener() {
			public void textChanged(final boolean user) {
				FilteredTableController.this.textChanged(!user);
			}
			public void downPressed() {
				FilteredTableController.this.textChanged(true);
				final Table table = fViewer.getTable();
				table.setFocus();
				if (table.getItemCount() > 0) {
					if (table.getSelectionIndex() < 0) {
						table.select(0);
					}
					else {
						table.showSelection();
					}
				}
			}
			public void okPressed() {
				FilteredTableController.this.textChanged(true);
			}
		});
	}
	
	
	public void setInput(final List<?> input) {
		fInput = input;
		fRefreshJob.schedule();
	}
	
	protected SearchPattern createSearchPattern(final String text) {
		final SearchPattern pattern = new SearchPattern(SearchPattern.RULE_EXACT_MATCH
				| SearchPattern.RULE_PREFIX_MATCH | SearchPattern.RULE_CAMELCASE_MATCH
				| SearchPattern.RULE_PATTERN_MATCH | SearchPattern.RULE_BLANK_MATCH);
		pattern.setPattern(text);
		return pattern;
	}
	
	protected boolean include(final Object object, final SearchPattern pattern) {
		return pattern.matches(object.toString());
	}
	
	/**
	 * Update the receiver after the text has changed.
	 */
	protected void textChanged(final boolean direct) {
		final String text = getFilterString();
		if (fFilterText.equals(text)) {
			return;
		}
		fFilterText = text;
		// cancel currently running job first, to prevent unnecessary redraw
		fRefreshJob.cancel();
		fRefreshJob.schedule(direct ? 0 : getRefreshJobDelay());
	}
	
	/**
	 * Return the time delay that should be used when scheduling the
	 * filter refresh job.  Subclasses may override.
	 * 
	 * @return a time delay in milliseconds before the job should run
	 */
	protected long getRefreshJobDelay() {
		return 200;
	}
	
	
	/**
	 * Get the tree viewer of the receiver.
	 * 
	 * @return the tree viewer
	 */
	public TableViewer getViewer() {
		return fViewer;
	}
	
	/**
	 * Get the filter text for the receiver, if it was created. Otherwise return
	 * <code>null</code>.
	 * 
	 * @return the filter text, or null if it was not created
	 */
	public SearchText getFilterControl() {
		return fFilteredText;
	}
	
	/**
	 * Convenience method to return the text of the filter control. If the text
	 * widget is not created, then null is returned.
	 * 
	 * @return String in the text, or null if the text does not exist
	 */
	protected String getFilterString() {
		return fFilteredText != null ? fFilteredText.getText() : null;
	}
	
}
