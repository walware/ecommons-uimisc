/*=============================================================================#
 # Copyright (c) 2005-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.workbench;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.views.navigator.ResourceComparator;

import de.walware.ecommons.ui.SharedMessages;
import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.components.StatusInfo;
import de.walware.ecommons.ui.components.WidgetToolBarComposite;
import de.walware.ecommons.ui.internal.Messages;
import de.walware.ecommons.ui.util.ViewerUtil;


/**
 * Workbench-level composite for choosing a container.
 */
public class ContainerSelectionComposite extends Composite {
	
	
	public static abstract class ContainerFilter extends ViewerFilter {
		
		IPath fExcludePath; 
		
		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			
			if (!(element instanceof IContainer)) {
				return true; // never
			}
			
			final IContainer container = (IContainer) element;
			if (fExcludePath != null) {
				if (container.getFullPath().isPrefixOf(fExcludePath)) {
					return true;
				}
			}
			
			return select(container);
		}
		
		public abstract boolean select(IContainer container);
	}
	
	
	private class CollapseAllAction extends Action {
		CollapseAllAction() {
			super();
			setText(SharedMessages.CollapseAllAction_label); 
			setDescription(SharedMessages.CollapseAllAction_description); 
			setToolTipText(SharedMessages.CollapseAllAction_tooltip); 
			setImageDescriptor(SharedUIResources.getImages().getDescriptor(SharedUIResources.LOCTOOL_COLLAPSEALL_IMAGE_ID));
		}
		
		@Override
		public void run() {
			
			fTreeViewer.collapseAll();
		}
	}
	
	private class ExpandAllAction extends Action {
		ExpandAllAction() {
			super();
			setText(SharedMessages.ExpandAllAction_label); 
			setDescription(SharedMessages.ExpandAllAction_description); 
			setToolTipText(SharedMessages.ExpandAllAction_tooltip); 
			setImageDescriptor(SharedUIResources.getImages().getDescriptor(SharedUIResources.LOCTOOL_EXPANDALL_IMAGE_ID));
		}
		
		@Override
		public void run() {
			
			fTreeViewer.expandAll();
		}
		
	}
	
	private class ToggleFilterAction extends Action {
		ToggleFilterAction() {
			super();
			setText(Messages.FilterFavouredContainersAction_label); 
			setDescription(Messages.FilterFavouredContainersAction_description); 
			setToolTipText(Messages.FilterFavouredContainersAction_tooltip); 
			setImageDescriptor(SharedUIResources.getImages().getDescriptor(SharedUIResources.LOCTOOL_FILTER_IMAGE_ID));
			setDisabledImageDescriptor(SharedUIResources.getImages().getDescriptor(SharedUIResources.LOCTOOLD_FILTER_IMAGE_ID));
			setChecked(false);
		}
		
		@Override
		public void run() {
			
			final boolean enable = isChecked();
			doToggleFilter(enable);
			fIsToggleFilterActivated = enable;
		}
		
		void doToggleFilter(final boolean enable) {
			
			if (enable) {
				fTreeViewer.addFilter(fToggledFilter);
			} else {
				fTreeViewer.removeFilter(fToggledFilter);
			}
		}
		
	}
	
	
	// sizing constants
	private static final int SIZING_SELECTION_PANE_WIDTH = 320;
//	private static final int SIZING_SELECTION_PANE_HEIGHT = 300;
	
	
	// Enable user to type in new container name
	private boolean fAllowNewContainerName = true;
	
	// show all projects by default
	private boolean fShowClosedProjects = true;
	
	// Last selection made by user
	private IContainer fSelectedContainer;
	
	// handle on parts
	private Text fContainerNameField;
	private TreeViewer fTreeViewer;
	private ToolBarManager fRightToolBarMgr;
	
	private boolean fIsToggleFilterActivated;
	private ContainerFilter fToggledFilter;
	
	// The listener to notify of events
	private Listener fListener;
	
	
//	/**
//	 * Creates a new instance of the widget.
//	 * 
//	 * @param parent The parent widget of the group.
//	 * @param listener A listener to forward events to. Can be null if
//	 *     no listener is required.
//	 * @param allowNewContainerName Enable the user to type in a new container
//	 *     name instead of just selecting from the existing ones.
//	 */
//	public ContainerSelectionControl(Composite parent, Listener listener, boolean allowNewContainerName) {
//		
//		this(parent, listener, allowNewContainerName, null);
//	}
//	
//	/**
//	 * Creates a new instance of the widget.
//	 * 
//	 * @param parent The parent widget of the group.
//	 * @param listener A listener to forward events to.  Can be null if
//	 *	 no listener is required.
//	 * @param allowNewContainerName Enable the user to type in a new container
//	 *  name instead of just selecting from the existing ones.
//	 * @param message The text to present to the user.
//	 */
//	public ContainerSelectionControl(Composite parent, Listener listener, boolean allowNewContainerName, 
//			String message) {
//		
//		this(parent, listener, allowNewContainerName, message, true);
//	}
//	
//	/**
//	 * Creates a new instance of the widget.
//	 * 
//	 * @param parent The parent widget of the group.
//	 * @param listener A listener to forward events to.  Can be null if
//	 *	 no listener is required.
//	 * @param allowNewContainerName Enable the user to type in a new container
//	 *  name instead of just selecting from the existing ones.
//	 * @param message The text to present to the user.
//	 * @param showClosedProjects Whether or not to show closed projects.
//	 */
//	public ContainerSelectionControl(Composite parent, Listener listener, boolean allowNewContainerName, 
//			String message, boolean showClosedProjects) {
//		
//		this(parent, listener, allowNewContainerName, message,
//				showClosedProjects, SIZING_SELECTION_PANE_HEIGHT);
//	}
	
	/**
	 * Creates a new instance of the widget.
	 * 
	 * @param parent The parent widget of the group.
	 * @param listener A listener to forward events to.  Can be null if
	 *     no listener is required.
	 * @param allowNewContainerName Enable the user to type in a new container
	 *     name instead of just selecting from the existing ones.
	 * @param message The text to present to the user.
	 * @param showClosedProjects Whether or not to show closed projects.
	 * @param heightHint height hint for the drill down composite
	 */
	public ContainerSelectionComposite(final Composite parent, 
			final boolean allowNewContainerName, final boolean showClosedProjects, String message, final int heightHint) {
		super(parent, SWT.NONE);
		fAllowNewContainerName = allowNewContainerName;
		fShowClosedProjects = showClosedProjects;
		if (message == null) {
			if (allowNewContainerName) {
				message = Messages.ContainerSelectionControl_label_EnterOrSelectFolder;
			} else {
				message = Messages.ContainerSelectionControl_label_SelectFolder;
			}
		}
		createContents(message, heightHint);
	}
	
	/**
	 * Creates the contents of the composite.
	 * 
	 * @param heightHint height hint for the drill down composite
	 */
	public void createContents(final String message, final int heightHint) {
		final GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		setLayout(layout);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final Label label = new Label(this, SWT.WRAP);
		label.setText(message);
		
		if (fAllowNewContainerName) {
			fContainerNameField = new Text(this, SWT.SINGLE | SWT.BORDER);
			fContainerNameField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			fContainerNameField.setFont(getFont());
		}
		else {
			// filler...
			new Label(this, SWT.NONE); // ben�tigt?
		}
		
		createTreeViewer(heightHint);
		Dialog.applyDialogFont(this);
	}
	
	/**
	 * Returns a new drill down viewer for this dialog.
	 * 
	 * @param heightHint height hint for the drill down composite
	 * @return a new drill down viewer
	 */
	protected void createTreeViewer(final int heightHint) {
		final WidgetToolBarComposite treeGroup= new WidgetToolBarComposite(this, SWT.BORDER);
		{	final GridData gd= new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.widthHint= SIZING_SELECTION_PANE_WIDTH;
			gd.heightHint= heightHint;
			treeGroup.setLayoutData(gd);
		}
		
		final ToolBarManager leftToolBarMgr= new ToolBarManager(treeGroup.getLeftToolBar());
		
		fRightToolBarMgr= new ToolBarManager(treeGroup.getRightToolBar());
		
		// Create tree viewer
		fTreeViewer= new TreeViewer(treeGroup, SWT.NONE);
		fTreeViewer.getTree().setLayoutData(treeGroup.getContentLayoutData());
		
		// Fill toolbars
		final DrillDownAdapter adapter= new DrillDownAdapter(fTreeViewer);
		adapter.addNavigationActions(leftToolBarMgr);
		
		fRightToolBarMgr.add(new CollapseAllAction());
		fRightToolBarMgr.add(new ExpandAllAction());
		
		leftToolBarMgr.update(true);
		fRightToolBarMgr.update(true);
		
		// layout group
		treeGroup.layout();
		
		fTreeViewer.setUseHashlookup(true);
		final ContainerContentProvider cp= new ContainerContentProvider();
		cp.showClosedProjects(fShowClosedProjects);
		fTreeViewer.setContentProvider(cp);
		fTreeViewer.setComparator(new ResourceComparator(ResourceComparator.NAME));
		fTreeViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
		fTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				containerSelectionChanged((IContainer) selection.getFirstElement()); // allow null
			}
		});
		ViewerUtil.addDoubleClickExpansion(fTreeViewer);
		
		// This has to be done after the viewer has been laid out
		fTreeViewer.setInput(ResourcesPlugin.getWorkspace());
	}
	
	public void setToggleFilter(final ContainerFilter filter, final boolean initialEnabled) {
		fToggledFilter = filter;
		fIsToggleFilterActivated = initialEnabled;
		final ToggleFilterAction action = new ToggleFilterAction();
		fRightToolBarMgr.add(new Separator());
		fRightToolBarMgr.add(action);
		
		// check and init
		action.doToggleFilter(true);
		if (fTreeViewer.getTree().getItemCount() == 0) {
			action.doToggleFilter(false);
			action.setChecked(false);
			action.setEnabled(false);
		} 
		else {
			action.setChecked(initialEnabled);
			if (!initialEnabled) {
				action.doToggleFilter(false);
			}
		}
		
		fRightToolBarMgr.update(true);
		fRightToolBarMgr.getControl().getParent().layout();
	}
	
	public boolean getToggleFilterSetting() {
		return fIsToggleFilterActivated;
	}
	
	/**
	 * Listener will be notified, if container selection changed.
	 * 
	 * @param listener
	 */
	public void setListener(final Listener listener) {
		fListener = listener;
		if (fContainerNameField != null) {
			fContainerNameField.addListener(SWT.Modify, fListener);
		}
	}
	
	/**
	 * Gives focus to one of the widgets in the group, as determined by the group.
	 */
	public void setInitialFocus() {
		if (fAllowNewContainerName) {
			fContainerNameField.setFocus();
		} else {
			fTreeViewer.getTree().setFocus();
		}
	}
	
	/**
	 * The container selection has changed in the
	 * tree view. Update the container name field
	 * value and notify all listeners.
	 */
	public void containerSelectionChanged(final IContainer container) {
		if (fAllowNewContainerName) {
			if (container != null) {
				fSelectedContainer = container;
				fContainerNameField.setText(container.getFullPath().makeRelative().toString());
			}
		} else {
			fSelectedContainer = container;
			// fire an event so the parent can update its controls
			if (fListener != null) {
				final Event changeEvent = new Event();
				changeEvent.type = SWT.Selection;
				changeEvent.widget = this;
				fListener.handleEvent(changeEvent);
			}
		}
	}
	
	
	/**
	 * Returns the currently entered container name.
	 * <p>
	 * Note that the container may not exist yet if the user
	 * entered a new container name in the field.
	 * 
	 * @return Path of Container, <code>null</code> if the field is empty.
	 */
	public IPath getContainerFullPath() {
		if (fAllowNewContainerName) {
			final String pathName = fContainerNameField.getText();
			if (pathName == null || pathName.length() < 1) {
				return null;
			} else {
				//The user may not have made this absolute so do it for them
				return (new Path(pathName)).makeAbsolute();
			}
		} else {
			if (fSelectedContainer == null) {
				return null;
			} else {
				return fSelectedContainer.getFullPath();
			}
		}
	}
	
	/**
	 * Sets the selected existing container.
	 */
	public void selectContainer(final IContainer container) {
		fSelectedContainer = container;
		
		//expand to and select the specified container
		final List<IContainer> itemsToExpand = new ArrayList<IContainer>();
		IContainer parent = container.getParent();
		while (parent != null) {
			itemsToExpand.add(0, parent);
			parent = parent.getParent();
		}
		// update filter
		if (fToggledFilter != null) {
			fToggledFilter.fExcludePath = container.getFullPath();
			fTreeViewer.refresh();
		}
		// update selection
		fTreeViewer.setExpandedElements(itemsToExpand.toArray());
		fTreeViewer.setSelection(new StructuredSelection(container), true);
	}
	
	/**
	 * Sets the value of this page's container.
	 * 
	 * @param path Full path to the container.
	 */
	public void selectContainer(final IPath path) {
		IResource initial = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if (initial != null) {
			if (!(initial instanceof IContainer)) {
				initial = initial.getParent();
			}
			selectContainer((IContainer) initial);
		}
	}
	
	
	public static IStatus validate(IPath path) {
		// validate Container
		if (path == null || path.isEmpty()) {
			return new StatusInfo(IStatus.ERROR, Messages.ContainerSelectionControl_error_FolderEmpty);
		}
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final String projectName = path.segment(0);
		if (projectName == null || !root.getProject(projectName).exists()) {
			return new StatusInfo(IStatus.ERROR, Messages.ContainerSelectionControl_error_ProjectNotExists);
		}
		//path is invalid if any prefix is occupied by a file
		while (path.segmentCount() > 1) {
			if (root.getFile(path).exists()) {
				return new StatusInfo(IStatus.ERROR, NLS.bind(
						Messages.ContainerSelectionControl_error_PathOccupied, 
						path.makeRelative() ));
			}
			path = path.removeLastSegments(1);
		}
		return new StatusInfo();
	}
	
}
