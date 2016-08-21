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
// -depend ~
package de.walware.ecommons.waltable.edit.editor;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.walware.ecommons.waltable.Messages;
import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.Direction;
import de.walware.ecommons.waltable.data.convert.ConversionFailedException;
import de.walware.ecommons.waltable.data.convert.IDisplayConverter;
import de.walware.ecommons.waltable.data.validate.IDataValidator;
import de.walware.ecommons.waltable.data.validate.ValidationFailedException;
import de.walware.ecommons.waltable.edit.ActiveCellEditorRegistry;
import de.walware.ecommons.waltable.edit.EditConfigAttributes;
import de.walware.ecommons.waltable.edit.EditConfigHelper;
import de.walware.ecommons.waltable.edit.EditMode;
import de.walware.ecommons.waltable.edit.EditSelectionCommand;
import de.walware.ecommons.waltable.edit.ICellEditHandler;
import de.walware.ecommons.waltable.internal.WaLTablePlugin;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.style.CellStyleProxy;
import de.walware.ecommons.waltable.style.DisplayMode;
import de.walware.ecommons.waltable.style.IStyle;

/**
 * Abstract implementation of {@link ICellEditor} that wraps SWT controls to be
 * NatTable editors. It is used to hide several default behaviour and styling from
 * concrete editor implementations, so implementing an editor can focus on the editor
 * specific handling instead of NatTable default behaviour.
 * <p>
 * Note that most of the member variables defined will be set on activating the editor.
 * So you can not access those variables expecting reasonable values prior activation.
 * This makes it possible to use the same editor instance for several cells instead of
 * creating a new one for every cell.
 */
public abstract class AbstractCellEditor implements ICellEditor {


	/**
	 * Flag indicating if the editor is closed or not.
	 */
	private boolean closed;
	/**
	 * The parent Composite, needed for the creation of the editor control.
	 * Used internally for adding general behaviour, e.g. forcing the focus
	 * if the editor is closed.
	 */
	private Composite parent;
	/**
	 * The {@link ICellEditHandler} that will be used on commit.
	 */
	private ICellEditHandler editHandler;
	/**
	 * The style that should be used for rendering within the editor control.
	 * Mainly it will cover foreground color, background color and font. If 
	 * the editor control supports further styles, this needs to be specified
	 * be the ICellEditor implementation itself.
	 */
	protected IStyle cellStyle;
	/**
	 * The {@link IDisplayConverter} that should be used to convert the input value 
	 * to the canonical value and vice versa.
	 */
	protected IDisplayConverter displayConverter;
	/**
	 * The {@link IDataValidator} that should be used to validate the input value
	 * prior committing.
	 */
	protected IDataValidator dataValidator;
	/**
	 * The {@link EditMode} which is used to activate special behaviour
	 * and styling. This is needed because activating an editor inline will have
	 * different behaviour (e.g. moving the selection after commit) and styling
	 * than rendering the editor on a subdialog.
	 */
	protected EditMode editMode;
	/**
	 * The cell whose editor should be activated.
	 */
	protected ILayerCell layerCell;
	/**
	 * The {@link LabelStack} of the cell whose editor should be activated.
	 */
	protected LabelStack labelStack;
	/**
	 * The error handler that will be used to show conversion errors.
	 */
	protected IEditErrorHandler conversionEditErrorHandler;
	/**
	 * The error handler that will be used to show validation errors.
	 */
	protected IEditErrorHandler validationEditErrorHandler;
	/**
	 * The {@link IConfigRegistry} containing the configuration of the
	 * current NatTable instance. This is necessary because the editors in 
	 * the current architecture are not aware of the NatTable instance they 
	 * are running in.
	 */
	protected IConfigRegistry configRegistry;
	
	/**
	 * The {@link FocusListener} that will be added to the created editor control
	 * for {@link EditMode#INLINE} to close it if it loses focus.
	 */
	protected FocusListener focusListener= new InlineFocusListener();
	
	/**
	 * The {@link TraverseListener} that will be added to the created editor control
	 * for {@link EditMode#INLINE} trying to commit the editor prior to traversal.
	 */
	protected TraverseListener traverseListener= new InlineTraverseListener();
	
	@Override
	public final Control activateCell(final Composite parent, final Object originalCanonicalValue, final EditMode editMode, 
			final ICellEditHandler editHandler, final ILayerCell cell, final IConfigRegistry configRegistry) {

		this.closed= false;
		this.parent= parent;
		this.editHandler= editHandler;
		this.editMode= editMode;
		this.layerCell= cell;
		this.configRegistry= configRegistry;
		this.labelStack= cell.getConfigLabels();
		final List<String> configLabels= this.labelStack.getLabels();
		this.displayConverter= configRegistry.getConfigAttribute(
				CellConfigAttributes.DISPLAY_CONVERTER, DisplayMode.EDIT, configLabels);
		this.cellStyle= new CellStyleProxy(configRegistry, DisplayMode.EDIT, configLabels);
		this.dataValidator= configRegistry.getConfigAttribute(
				EditConfigAttributes.DATA_VALIDATOR, DisplayMode.EDIT, configLabels);
		
		this.conversionEditErrorHandler= EditConfigHelper.getEditErrorHandler(
				configRegistry, EditConfigAttributes.CONVERSION_ERROR_HANDLER, configLabels);
		this.validationEditErrorHandler= EditConfigHelper.getEditErrorHandler(
				configRegistry, EditConfigAttributes.VALIDATION_ERROR_HANDLER, configLabels);
		
		return activateCell(parent, originalCanonicalValue);
	}
	
	/**
	 * This method will be called by {@link AbstractCellEditor#activateCell(Composite, Object, EditMode, 
	 * ICellEditHandler, ILayerCell, IConfigRegistry)} after initializing the activation values and before
	 * adding the default listeners. In this method the underlying editor control should be created and
	 * initialized, hiding default configuration from editor implementors.
	 * @param parent The parent Composite, needed for the creation of the editor control.
	 * @param originalCanonicalValue The value that should be put to the activated editor control.
	 * @return The SWT {@link Control} to be used for capturing the new cell value.
	 */
	protected abstract Control activateCell(Composite parent, Object originalCanonicalValue);
	
	
	/**
	 * @see ILayerCell#getColumnPosition()
	 */
	@Override
	public long getColumnPosition() {
		return this.layerCell.getColumnPosition();
	}

	/**
	 * @see ILayerCell#getRowPosition()
	 */
	@Override
	public long getRowPosition() {
		return this.layerCell.getRowPosition();
	}
	
	/**
	 * Converts the current value in this editor using the configured {@link IDisplayConverter}.
	 * If there is no {@link IDisplayConverter} registered for this editor, the value itself 
	 * will be returned.
	 * @return The canonical value after converting the current value or the value itself
	 * 			if no {@link IDisplayConverter} is configured.
	 * @throws RuntimeException for conversion failures. As the {@link IDisplayConverter} interface
	 * 			does not specify throwing checked Exceptions on converting data, only unchecked
	 * 			Exceptions can occur. This is needed to stop further commit processing if the
	 * 			conversion failed.
	 * @see IDisplayConverter
	 */
	@Override
	public Object getCanonicalValue() {
		return getCanonicalValue(this.conversionEditErrorHandler);
	}
	
	/**
	 * Converts the current value in this editor using the configured {@link IDisplayConverter}.
	 * If there is no {@link IDisplayConverter} registered for this editor, the value itself 
	 * will be returned. Will use the specified {@link IEditErrorHandler} for handling 
	 * conversion errors.
	 * @param conversionErrorHandler The error handler that will be activated in case of 
	 * 			conversion errors.
	 * @return The canonical value after converting the current value or the value itself
	 * 			if no {@link IDisplayConverter} is configured.
	 * @throws RuntimeException for conversion failures. As the {@link IDisplayConverter} interface
	 * 			does not specify throwing checked Exceptions on converting data, only unchecked
	 * 			Exceptions can occur. This is needed to stop further commit processing if the
	 * 			conversion failed.
	 * @see IDisplayConverter
	 */
	@Override
	public Object getCanonicalValue(final IEditErrorHandler conversionErrorHandler) {
		return handleConversion(getEditorValue(), conversionErrorHandler);
	}
	
	/**
	 * Converts the given display value using the configured {@link IDisplayConverter}.
	 * If there is no {@link IDisplayConverter} registered for this editor, the value itself 
	 * will be returned. Will use the specified {@link IEditErrorHandler} for handling 
	 * conversion errors.
	 * @param displayValue The display value that needs to be converted.
	 * @param conversionErrorHandler The error handler that will be activated in case of 
	 * 			conversion errors.
	 * @return The canonical value after converting the current value or the value itself
	 * 			if no {@link IDisplayConverter} is configured.
	 * @throws RuntimeException for conversion failures. As the {@link IDisplayConverter} interface
	 * 			does not specify throwing checked Exceptions on converting data, only unchecked
	 * 			Exceptions can occur. This is needed to stop further commit processing if the
	 * 			conversion failed.
	 * @see IDisplayConverter
	 */
	protected Object handleConversion(final Object displayValue, final IEditErrorHandler conversionErrorHandler) {
		Object canonicalValue;
		try {
			if (this.displayConverter != null) {
				//always do the conversion to check for valid entered data
				canonicalValue= this.displayConverter.displayToCanonicalValue(
						this.layerCell, this.configRegistry, displayValue);
			} else {
				canonicalValue= displayValue;
			}

			//if the conversion succeeded, remove error rendering if exists
			conversionErrorHandler.removeError(this);
		} catch (final ConversionFailedException e) {
			// conversion failed
			conversionErrorHandler.displayError(this, e);
			throw e;
		} catch (final Exception e) {
			// conversion failed
			conversionErrorHandler.displayError(this, e);
			throw new ConversionFailedException(e.getMessage(), e);
		}
		return canonicalValue;
	}
	
	@Override
	public void setCanonicalValue(final Object canonicalValue) {
		Object displayValue;
		if (this.displayConverter != null) {
			displayValue= this.displayConverter.canonicalToDisplayValue(
					this.layerCell, this.configRegistry, canonicalValue);
		} else {
			displayValue= canonicalValue;
		}
		setEditorValue(displayValue);
	}
	
	@Override
	public boolean validateCanonicalValue(final Object canonicalValue) {
		return validateCanonicalValue(canonicalValue, this.validationEditErrorHandler);
	}
	
	@Override
	public boolean validateCanonicalValue(final Object canonicalValue, final IEditErrorHandler validationEditErrorHandler) {
		//do the validation if a validator is registered
		if (this.dataValidator != null) {
			try {
				final boolean validationResult= this.dataValidator.validate(
						this.layerCell, this.configRegistry, canonicalValue);

				//if the validation succeeded, remove error rendering if exists
				if (validationResult) {
					validationEditErrorHandler.removeError(this);
				} else {
					throw new ValidationFailedException(
							Messages.getString("AbstractCellEditor.validationFailure")); //$NON-NLS-1$
				}
				return validationResult;
			} catch (final Exception e) {
				//validation failed
				validationEditErrorHandler.displayError(this, e);
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public boolean commit(final Direction direction) {
		return commit(direction, true);
	}
	
	@Override
	public boolean commit(final Direction direction, final boolean closeAfterCommit) {
		return commit(direction, closeAfterCommit, false);
	}
	
	@Override
	public boolean commit(final Direction direction, final boolean closeAfterCommit, final boolean skipValidation) {
		if (this.editHandler != null && !this.closed) {
			try {
				//always do the conversion
				final Object canonicalValue= getCanonicalValue(); 
				if (skipValidation || (!skipValidation && validateCanonicalValue(canonicalValue))) {
					final boolean committed= this.editHandler.commit(canonicalValue, direction);
					
					if (committed && closeAfterCommit) {
						close();
						
						if (direction != null && openAdjacentEditor()) {
							this.layerCell.getLayer().doCommand(
									new EditSelectionCommand(this.parent, this.configRegistry));
						}
					}
					
					return committed;
				}
			}
			catch (final ConversionFailedException e) {
				//do nothing as exceptions caused by conversion are handled already
				//we just need this catch block for stopping the process if conversion 
				//failed with an exception
			}
			catch (final ValidationFailedException e) {
				//do nothing as exceptions caused by validation are handled already
				//we just need this catch block for stopping the process if validation 
				//failed with an exception
			}
			catch (final Exception e) {
				//if another exception occured that wasn't thrown by us, it should at least
				//be logged without killing the whole application
				WaLTablePlugin.log(new Status(IStatus.ERROR, WaLTablePlugin.PLUGIN_ID,
						"Error on updating cell value: " + e.getLocalizedMessage(), e )); //$NON-NLS-1$
			}
		}
		return false;
	}

	@Override
	public void close() {
		this.closed= true;
		if (this.parent != null && !this.parent.isDisposed()) {
			this.parent.forceFocus();
		}
		
		removeEditorControlListeners();
		
		final Control editorControl= getEditorControl();
		if (editorControl != null && !editorControl.isDisposed()) {
			editorControl.dispose();
		}
		
		ActiveCellEditorRegistry.unregisterActiveCellEditor();
	}

	@Override
	public boolean isClosed() {
		return this.closed;
	}
	
	@Override
	public boolean openInline(final IConfigRegistry configRegistry, final List<String> configLabels) {
		return EditConfigHelper.openInline(configRegistry, configLabels);
	}
	
	@Override
	public boolean supportMultiEdit(final IConfigRegistry configRegistry, final List<String> configLabels) {
		return EditConfigHelper.supportMultiEdit(configRegistry, configLabels);				
	}
	
	@Override
	public boolean openMultiEditDialog() {
		return true;
	}
	
	@Override
	public boolean openAdjacentEditor() {
		return EditConfigHelper.openAdjacentEditor(this.configRegistry, this.labelStack.getLabels());
	}

	@Override
	public boolean activateAtAnyPosition() {
		return true;
	}
	
	@Override
	public void addEditorControlListeners() {
		final Control editorControl= getEditorControl();
		if (editorControl != null && !editorControl.isDisposed() && this.editMode == EditMode.INLINE) {
			//only add the focus and traverse listeners for inline mode
			editorControl.addFocusListener(this.focusListener);
			editorControl.addTraverseListener(this.traverseListener);
		}
	}
	
	@Override
	public void removeEditorControlListeners() {
		final Control editorControl= getEditorControl();
		if (editorControl != null && !editorControl.isDisposed()) {
			editorControl.removeFocusListener(this.focusListener);
			editorControl.removeTraverseListener(this.traverseListener);
		}
	}
	
	@Override
	public Rectangle calculateControlBounds(final Rectangle cellBounds) {
		return cellBounds;
	}
	
	/**
	 * This method can be used to set the {@link IDataValidator} to use. This might be useful
	 * e.g. the configured validator needs to be wrapped to add special behaviour.
	 * Setting a validator prior to activating the editor will have no effect.
	 * <p>
	 * Note: It is not suggested to call this method in custom code. It is used e.g. by the
	 * 		 TickUpdateCellEditDialog as dependent on the selected update type, the validator
	 * 		 needs to be enabled or not.
	 * </p>
	 * @param validator The {@link IDataValidator} to set.
	 */
	public void setDataValidator(final IDataValidator validator) {
		this.dataValidator= validator;
	}
	
	
	private class InlineFocusListener extends FocusAdapter {
		@Override
		public void focusLost(final FocusEvent e) {
			if (!commit(null, true)) {
				if (e.widget instanceof Control && !e.widget.isDisposed()) {
					((Control)e.widget).forceFocus();
				}
			}
			else {
				AbstractCellEditor.this.parent.forceFocus();
			}
		}
	}
	
	/**
	 * {@link TraverseListener} that will try to commit and close this editor with the
	 * current value, prior to proceed the traversal. If the commit fails and the
	 * editor can not be closed, the traversal will not be processed.
	 */
	private class InlineTraverseListener implements TraverseListener {
		@Override
		public void keyTraversed(final TraverseEvent event) {
			boolean committed= false;
			if (event.keyCode == SWT.TAB && event.stateMask == SWT.MOD2) {
				committed= commit(Direction.LEFT);
			} else if (event.keyCode == SWT.TAB && event.stateMask == 0) {
				committed= commit(Direction.RIGHT);
			}
			if (!committed) {
				event.doit= false;
			}
		}
	}
	
}
