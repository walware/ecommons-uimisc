/*=============================================================================#
 # Copyright (c) 2000-2015 IBM Corporation and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     IBM Corporation - initial API and implementation for JDT
 #     Stephan Wahlbrink - initial API and implementation for LTK
 #=============================================================================*/

package de.walware.ecommons.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlExtension;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.ecommons.models.core.util.IElementProxy;
import de.walware.ecommons.ui.SharedMessages;
import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.components.SearchText;
import de.walware.ecommons.ui.content.IElementFilter;
import de.walware.ecommons.ui.content.ITextElementFilter;
import de.walware.ecommons.ui.content.TextElementFilter;
import de.walware.ecommons.ui.internal.UIMiscellanyPlugin;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.UIAccess;


/**
 * Abstract class for showing tree in light-weight controls.
 */
public abstract class QuickTreeInformationControl extends PopupDialog implements IInformationControl, IInformationControlExtension, IInformationControlExtension2, DisposeListener {
	
	private static class ElementWithName implements IElementProxy {
		
		
		private IAdaptable element;
		
		private String name;
		
		
		public void set(final IAdaptable element, final String name) {
			this.element= element;
			this.name= name;
		}
		
		@Override
		public IAdaptable getElement() {
			return this.element;
		}
		
		@Override
		public Object getAdapter(final Class adapter) {
			return this.element.getAdapter(adapter);
		}
		
		
		@Override
		public String toString() {
			return this.name;
		}
		
	}
	
	/**
	 * The filter selects the elements which match the given string patterns.
	 */
	protected class SearchFilter extends ViewerFilter {
		
		public SearchFilter() {
		}
		
		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			if (QuickTreeInformationControl.this.select(element)) {
				return true;
			}
			
			// has unfiltered child?
			final Object[] children=  ((ITreeContentProvider) QuickTreeInformationControl.this.treeViewer.getContentProvider())
					.getChildren(element);
			for (int i= 0; i < children.length; i++) {
				if (select(viewer, element, children[i])) {
					return true;
				}
			}
			return false;
		}
		
	}
	
	
	/** The control's text widget */
	private SearchText filterText;
	
	/** The control's tree widget */
	private TreeViewer treeViewer;
	
	/** The current string matcher */
	protected ITextElementFilter nameFilter;
	
	private IElementFilter.IFinalFilter finalNameFilter;
	private final ElementWithName nameFilterElement= new ElementWithName();
	
	private final int iterationCount;
	private int iterationPosition;
	
	private final String commandId;
	
	private String commandBestKeyStrokeFormatted;
	private List<KeyStroke> commandActiveKeyStrokes;
	private KeyListener commandKeyListener;
	
//	private IAction fShowViewMenuAction;
//	private HandlerSubmission fShowViewMenuHandlerSubmission;
	
	/**
	 * Creates a tree information control with the given shell as parent. The given
	 * styles are applied to the shell and the tree widget.
	 *
	 * @param parent the parent shell
	 * @param shellStyle the additional styles for the shell
	 * @param treeStyle the additional styles for the tree widget
	 * @param commandId the id of the command that invoked this control or <code>null</code>
	 */
	public QuickTreeInformationControl(final Shell parent, final int shellStyle,
			final boolean showStatusField, final String commandId, final int iterationCount) {
		super(parent, shellStyle, true, true, false, true, true, null, null);
		
		if (iterationCount < 1) {
			throw new IllegalArgumentException("iterationCount"); //$NON-NLS-1$
		}
		
		this.commandId= commandId;
		this.iterationCount= iterationCount;
		if (this.commandId != null && this.iterationCount > 1) {
			initIterateKeys();
		}
		
		// Title and status text must be set to get the title label created, so force empty values here.
		setInfoText(""); //  //$NON-NLS-1$
		
		this.nameFilter= createNameFilter();
		
		create();
	}
	
	private void initIterateKeys() {
		if (this.commandId == null || this.iterationCount == 1) {
			return;
		}
		final IBindingService bindingSvc= (IBindingService) PlatformUI.getWorkbench().getService(IBindingService.class);
		if (bindingSvc == null) {
			return;
		}
		{	final TriggerSequence sequence= bindingSvc.getBestActiveBindingFor(this.commandId);
			final KeyStroke keyStroke= getKeyStroke(sequence);
			if (keyStroke == null) {
				return;
			}
			this.commandBestKeyStrokeFormatted= keyStroke.format();
		}
		{	final TriggerSequence[] sequences= bindingSvc.getActiveBindingsFor(this.commandId);
			this.commandActiveKeyStrokes= new ArrayList<KeyStroke>(sequences.length);
			for (int i= 0; i < sequences.length; i++) {
				final KeyStroke keyStroke= getKeyStroke(sequences[i]);
				if (keyStroke != null) {
					this.commandActiveKeyStrokes.add(keyStroke);
				}
			}
		}
		this.commandKeyListener= new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent event) {
				final KeyStroke keyStroke= SWTKeySupport.convertAcceleratorToKeyStroke(
						SWTKeySupport.convertEventToUnmodifiedAccelerator(event) );
				for (final KeyStroke activeKeyStroke : QuickTreeInformationControl.this.commandActiveKeyStrokes) {
					if (activeKeyStroke.equals(keyStroke)) {
						event.doit= false;
						iterate();
						return;
					}
				}
			}
		};
	}
	
	private KeyStroke getKeyStroke(final TriggerSequence triggerSequence) {
		if (triggerSequence instanceof KeySequence) {
			final KeyStroke[] keyStrokes= ((KeySequence) triggerSequence).getKeyStrokes();
			if (keyStrokes.length == 1) {
				return keyStrokes[0];
			}
		}
		return null;
	}
	
	
	protected abstract String getDescription(int iterationPosition);
	
	protected void updateInfoText() {
		final StringBuilder sb= new StringBuilder(
				getDescription(getIterationPosition()) );
		if (this.commandBestKeyStrokeFormatted != null) {
			sb.append("\u2004\u2004"); //$NON-NLS-1$
			sb.append(NLS.bind(SharedMessages.DoToShow_message,
					this.commandBestKeyStrokeFormatted,
					getDescription(getNextIterationPosition()) ));
		}
		sb.append('\u2006');
		setInfoText(sb.toString());
	}
	
	
	/**
	 * Create the main content for this information control.
	 *
	 * @param parent The parent composite
	 * @return The control representing the main content.
	 * @since 3.2
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final TreeViewer viewer= new TreeViewer(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		final Tree tree= viewer.getTree();
		{	final GridData gd= new GridData(GridData.FILL_BOTH);
			gd.heightHint= LayoutUtil.hintHeight(tree, 12);
			tree.setLayoutData(gd);
		}
		
		viewer.setUseHashlookup(true);
		viewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		configureViewer(viewer);
		
		viewer.addFilter(new SearchFilter());
		
//		this.fCustomFiltersActionGroup= new CustomFiltersActionGroup(getId(), this.viewer);
		
		this.treeViewer= viewer;
		
		tree.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				// do nothing
			}
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				gotoSelectedElement();
			}
		});
		
		tree.addMouseMoveListener(new MouseMoveListener()	 {
			TreeItem lastItem= null;
			@Override
			public void mouseMove(final MouseEvent e) {
				if (tree.equals(e.getSource())) {
					final Object o= tree.getItem(new Point(e.x, e.y));
					if (this.lastItem == null ^ o == null) {
						tree.setCursor(o == null ? null : tree.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
					}
					if (o instanceof TreeItem) {
						final Rectangle clientArea= tree.getClientArea();
						if (!o.equals(this.lastItem)) {
							this.lastItem= (TreeItem)o;
							tree.setSelection(new TreeItem[] { this.lastItem });
						} else if (e.y - clientArea.y < tree.getItemHeight() / 4) {
							// Scroll up
							final Point p= tree.toDisplay(e.x, e.y);
							final Item item= QuickTreeInformationControl.this.treeViewer.scrollUp(p.x, p.y);
							if (item instanceof TreeItem) {
								this.lastItem= (TreeItem)item;
								tree.setSelection(new TreeItem[] { this.lastItem });
							}
						} else if (clientArea.y + clientArea.height - e.y < tree.getItemHeight() / 4) {
							// Scroll down
							final Point p= tree.toDisplay(e.x, e.y);
							final Item item= QuickTreeInformationControl.this.treeViewer.scrollDown(p.x, p.y);
							if (item instanceof TreeItem) {
								this.lastItem= (TreeItem)item;
								tree.setSelection(new TreeItem[] { this.lastItem });
							}
						}
					} else if (o == null) {
						this.lastItem= null;
					}
				}
			}
		});
		
		if (this.commandKeyListener != null) {
			tree.addKeyListener(this.commandKeyListener);
		}
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(final MouseEvent e) {
				if (tree.getSelectionCount() < 1) {
					return;
				}
				
				if (e.button != 1) {
					return;
				}
				
				if (tree.equals(e.getSource())) {
					final Object o= tree.getItem(new Point(e.x, e.y));
					final TreeItem selection= tree.getSelection()[0];
					if (selection.equals(o)) {
						gotoSelectedElement();
					}
				}
			}
		});
		
		addDisposeListener(this);
		return this.treeViewer.getControl();
	}
	
	protected abstract void configureViewer(TreeViewer viewer);
	
	protected TreeViewer getTreeViewer() {
		return this.treeViewer;
	}
	
	protected SearchText getFilterText() {
		return this.filterText;
	}
	
	protected SearchText createFilterText(final Composite parent) {
		this.filterText= new SearchText(parent, SWT.NONE, "", SWT.NONE); //$NON-NLS-1$
		
		Dialog.applyDialogFont(this.filterText);
		
		this.filterText.addListener(new SearchText.Listener() {
			@Override
			public void textChanged(final boolean user) {
				setMatcherString(QuickTreeInformationControl.this.filterText.getText(), true);
			}
			@Override
			public void okPressed() {
				gotoSelectedElement();
			}
			@Override
			public void downPressed() {
				QuickTreeInformationControl.this.treeViewer.getTree().setFocus();
			}
		});
		this.filterText.getTextControl().addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				if (event.character == SWT.ESC) {
					close();
					event.doit= false;
				}
			}
		});
		if (this.commandKeyListener != null) {
			this.filterText.getTextControl().addKeyListener(this.commandKeyListener);
		}
		
		return this.filterText;
	}
	
	protected void createHorizontalSeparator(final Composite parent) {
		final Label separator= new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_DOT);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	
	/**
	 * Sets the patterns to filter out for the receiver.
	 *
	 * @param pattern the pattern
	 * @param update <code>true</code> if the viewer should be updated
	 */
	protected void setMatcherString(final String pattern, final boolean update) {
		if (this.nameFilter.setText(pattern) && update) {
			stringMatcherUpdated();
		}
	}
	
	/**
	 * The string matcher has been modified.
	 * 
	 * The default implementation refreshes the view and selects the first matched element.
	 */
	protected void stringMatcherUpdated() {
		// refresh viewer to re-filter
		this.treeViewer.getControl().setRedraw(false);
		this.finalNameFilter= this.nameFilter.getFinal(false);
		this.treeViewer.refresh();
		this.treeViewer.expandAll();
		selectFirstMatch();
		this.treeViewer.getControl().setRedraw(true);
	}
	
	/**
	 * Implementers can modify
	 *
	 * @return the selected element
	 */
	protected Object getSelectedElement() {
		if (this.treeViewer == null) {
			return null;
		}
		
		return ((IStructuredSelection) this.treeViewer.getSelection()).getFirstElement();
	}
	
	protected void gotoSelectedElement() {
		final Object selectedElement= getSelectedElement();
		if (selectedElement != null) {
			try {
				dispose();
				
				openElement(selectedElement);
			}
			catch (final CoreException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, UIMiscellanyPlugin.PLUGIN_ID,
						"An error occurred when opening the selected element.", e ));
			}
		}
	}
	
	protected abstract void openElement(final Object element) throws CoreException;
	
	
	/**
	 * Selects the first element in the tree which matches the current filter pattern.
	 */
	protected void selectFirstMatch() {
		final Tree tree= this.treeViewer.getTree();
		final Object element= findFirstMatch(tree.getItems());
		if (element != null) {
			this.treeViewer.setSelection(new StructuredSelection(element), true);
		} else {
			this.treeViewer.setSelection(StructuredSelection.EMPTY);
		}
	}
	
	private Object findFirstMatch(final TreeItem[] items) {
		if (items.length > 0) {
			// Process each item in the tree
			for (int i= 0; i < items.length; i++) {
				final Object element= items[i].getData();
				if (element == null) {
					continue;
				}
				if (select(element)) {
					return element;
				}
			}
			
			for (int i= 0; i < items.length; i++) {
				// Recursively check the elements children for a match
				final Object element= findFirstMatch(items[i].getItems());
				// Return the child element match if found
				if (element != null) {
					return element;
				}
			}
		}
		// No match found
		return null;
	}
	
	
	@Override
	public void setInformation(final String information) {
		// this method is ignored, see IInformationControlExtension2
	}
	
	@Override
	public abstract void setInput(Object information);
	
	protected void inputChanged(final int iterationPage, final Object newInput,
			final Object newSelection) {
		this.filterText.clearText();
		resetFilter();
		
		this.iterationPosition= iterationPage;
		updateInfoText();
		
		this.treeViewer.setInput(newInput);
		if (newSelection != null) {
			this.treeViewer.setSelection(new StructuredSelection(newSelection));
		}
	}
	
	
	protected ITextElementFilter createNameFilter() {
		return new TextElementFilter();
	}
	
	protected void resetFilter() {
		this.nameFilter.setText(null);
		this.finalNameFilter= this.nameFilter.getFinal(true);
	}
	
	protected String getElementName(final IAdaptable element) {
		final IBaseLabelProvider labelProvider= this.treeViewer.getLabelProvider();
		if (labelProvider instanceof ILabelProvider) {
			return ((ILabelProvider) labelProvider).getText(element);
		}
		return element.toString();
	}
	
	protected boolean select(final Object element) {
		if (this.finalNameFilter == null) {
			return true;
		}
		if (element instanceof IAdaptable) {
			final IAdaptable adaptable= (IAdaptable) element;
			final String name= getElementName(adaptable);
			if (name != null) {
				this.nameFilterElement.set(adaptable, name);
				return this.finalNameFilter.select(this.nameFilterElement);
			}
		}
		return false;
	}
	
	
	@Override
	protected void fillDialogMenu(final IMenuManager menu) {
		super.fillDialogMenu(menu);
		
		menu.add(new Separator(SharedUIResources.VIEW_SORT_MENU_ID));
		menu.add(new Separator(SharedUIResources.VIEW_FILTER_MENU_ID));
	}
	
	@Override
	public void setVisible(final boolean visible) {
		if (visible) {
			open();
		} else {
			disableActions();
			saveDialogBounds(getShell());
			getShell().setVisible(false);
		}
	}
	
	@Override
	public int open() {
		initActions();
		return super.open();
	}
	
	@Override
	public final void dispose() {
		close();
	}
	
	@Override
	public void widgetDisposed(final DisposeEvent event) {
		disableActions();
		
		this.treeViewer= null;
		this.filterText= null;
	}
	
	/**
	 * Adds handler and key binding support.
	 */
	protected void initActions() {
		// IWorkbenchCommandConstants.WINDOW_SHOW_VIEW_MENU -> showDialogMenu()
		
//		// Register action with command support
//		if (this.fShowViewMenuHandlerSubmission == null) {
//			this.fShowViewMenuHandlerSubmission= new HandlerSubmission(null, getShell(), null, this.fShowViewMenuAction.getActionDefinitionId(), new ActionHandler(this.fShowViewMenuAction), Priority.MEDIUM);
//			PlatformUI.getWorkbench().getCommandSupport().addHandlerSubmission(this.fShowViewMenuHandlerSubmission);
//		}
	}
	
	/**
	 * Removes handler and key binding support.
	 */
	protected void disableActions() {
//		// Remove handler submission
//		if (this.fShowViewMenuHandlerSubmission != null) {
//			PlatformUI.getWorkbench().getCommandSupport().removeHandlerSubmission(this.fShowViewMenuHandlerSubmission);
//		}
	}
	
	@Override
	public boolean hasContents() {
		return (this.treeViewer != null && this.treeViewer.getInput() != null);
	}
	
	@Override
	public void setSizeConstraints(final int maxWidth, final int maxHeight) {
		// ignore
	}
	
	@Override
	public Point computeSizeHint() {
		//Rreturn the shell's size
		// Note that it already has the persisted size if persisting is enabled.
		return getShell().getSize();
	}
	
	@Override
	public void setLocation(final Point location) {
		/*
		 * If the location is persisted, it gets managed by PopupDialog - fine. Otherwise, the location is
		 * computed in Window#getInitialLocation, which will center it in the parent shell / main
		 * monitor, which is wrong for two reasons:
		 * - we want to center over the editor / subject control, not the parent shell
		 * - the center is computed via the initalSize, which may be also wrong since the size may
		 *   have been updated since via min/max sizing of AbstractInformationControlManager.
		 * In that case, override the location with the one computed by the manager. Note that
		 * the call to constrainShellSize in PopupDialog.open will still ensure that the shell is
		 * entirely visible.
		 */
		if (!getPersistLocation() || getDialogSettings() == null) {
			getShell().setLocation(location);
		}
	}
	
	@Override
	public void setSize(final int width, final int height) {
		getShell().setSize(width, height);
	}
	
	
	@Override
	public void addDisposeListener(final DisposeListener listener) {
		getShell().addDisposeListener(listener);
	}
	
	@Override
	public void removeDisposeListener(final DisposeListener listener) {
		getShell().removeDisposeListener(listener);
	}
	
	
	@Override
	public void setForegroundColor(final Color foreground) {
		applyForegroundColor(foreground, getContents());
	}
	
	@Override
	public void setBackgroundColor(final Color background) {
		applyBackgroundColor(background, getContents());
	}
	
	
	@Override
	public boolean isFocusControl() {
		final Shell shell= getShell();
		return (shell != null && shell.getDisplay().getActiveShell() == shell);
	}
	
	@Override
	public void setFocus() {
		getShell().forceFocus();
		this.filterText.setFocus();
	}
	
	@Override
	public void addFocusListener(final FocusListener listener) {
		getShell().addFocusListener(listener);
	}
	
	@Override
	public void removeFocusListener(final FocusListener listener) {
		getShell().removeFocusListener(listener);
	}
	
	
	protected final String getCommandId() {
		return this.commandId;
	}
	
	
	protected final int getIterationPosition() {
		return this.iterationPosition;
	}
	
	private int getNextIterationPosition() {
		final int page= this.iterationPosition + 1;
		return (page < this.iterationCount) ? page : 0;
	}
	
	private void iterate() {
		this.iterationPosition= getNextIterationPosition();
		updateInfoText();
		iterated(this.iterationPosition);
	}
	
	protected void iterated(final int iterationPosition) {
		final Object selectedElement= ((IStructuredSelection) this.treeViewer.getSelection()).getFirstElement();
		
		this.treeViewer.refresh();
		
		if (selectedElement != null && this.treeViewer.getTree().getSelectionCount() == 0) {
			// if tree path changed, try again
			treeViewer.setSelection(new StructuredSelection(selectedElement), true);
		}
	}
	
	
	@Override
	protected Control createTitleControl(final Composite parent) {
		this.filterText= createFilterText(parent);
		this.filterText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		return this.filterText;
	}
	
	protected IProgressMonitor getProgressMonitor() {
		final IWorkbenchPart part= UIAccess.getActiveWorkbenchPart(true);
		
		IEditorPart editor;
		if (part instanceof IEditorPart) {
			editor= (IEditorPart) part;
		}
		else {
			return null;
		}
		
		return editor.getEditorSite().getActionBars().getStatusLineManager().getProgressMonitor();
	}
	
}
