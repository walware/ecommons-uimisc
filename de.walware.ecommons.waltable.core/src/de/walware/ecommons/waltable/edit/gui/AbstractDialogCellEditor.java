/*******************************************************************************
 * Copyright (c) 2013-2016 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@gmail.com> - initial API and implementation
 *******************************************************************************/
// -depend, ~
package de.walware.ecommons.waltable.edit.gui;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
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
import de.walware.ecommons.waltable.edit.DialogEditHandler;
import de.walware.ecommons.waltable.edit.EditConfigAttributes;
import de.walware.ecommons.waltable.edit.EditConfigHelper;
import de.walware.ecommons.waltable.edit.EditMode;
import de.walware.ecommons.waltable.edit.EditTypeEnum;
import de.walware.ecommons.waltable.edit.ICellEditHandler;
import de.walware.ecommons.waltable.edit.editor.ICellEditor;
import de.walware.ecommons.waltable.edit.editor.IEditErrorHandler;
import de.walware.ecommons.waltable.internal.WaLTablePlugin;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.style.DisplayMode;


/**
 * Abstract implementation of a {@link ICellEditor} that is also a {@link ICellEditDialog}.
 * By creating a {@link ICellEditor} based on this abstract implementation, you are able
 * to create an editor that wraps a SWT or JFace dialog. As SWT and JFace dialogs does not
 * extend the same base classes, the local instance for the wrapped dialog is of type object
 * in here. In the concrete implementation the {@link AbstractDialogCellEditor#getDialogInstance()}
 * should return the concrete dialog type that is wrapped.
 * <p>
 * By using this implementation, the {@link CellEditDialogFactory} will return the instance of this
 * editor, after it was activated previously.
 */
public abstract class AbstractDialogCellEditor implements ICellEditor, ICellEditDialog {
	
	
	/**
	 * The parent Composite, needed for the creation of the dialog.
	 */
	protected Composite parent;
	/**
	 * The {@link Dialog} that should be used as a cell editor.
	 */
	protected Object dialog;
	/**
	 * The cell whose editor should be activated.
	 */
	protected ILayerCell layerCell;
	/**
	 * The {@link ICellEditHandler} that will be used on commit.
	 */
	protected DialogEditHandler editHandler= new DialogEditHandler();
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
	 * Map that contains custom configurations for this {@link CellEditDialog}.
	 * We do not use the {@link IDialogSettings} provided by JFace, because they are
	 * used to store and load the settings in XML rather than overriding the behaviour. 
	 */
	protected Map<String, Object> editDialogSettings;

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.gui.ICellEditDialog#getEditType()
	 */
	@Override
	public EditTypeEnum getEditType() {
		//by default the value selected in the wrapped dialog should simply be set to the
		//data model on commit. 
		return EditTypeEnum.SET;
	}

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.gui.ICellEditDialog#calculateValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object calculateValue(final Object currentValue, final Object processValue) {
		//by default the value selected in the wrapped dialog should simply be set to the
		//data model on commit. 
		return processValue;
	}

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.gui.ICellEditDialog#open()
	 */
	@Override
	public abstract int open();

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.editor.ICellEditor#activateCell(org.eclipse.swt.widgets.Composite, java.lang.Object, de.walware.ecommons.waltable.widget.EditModeEnum, de.walware.ecommons.waltable.edit.ICellEditHandler, de.walware.ecommons.waltable.layer.cell.ILayerCell, de.walware.ecommons.waltable.config.IConfigRegistry)
	 */
	@Override
	public Control activateCell(final Composite parent,
			final Object originalCanonicalValue, final EditMode editMode,
			final ICellEditHandler editHandler, final ILayerCell cell,
			final IConfigRegistry configRegistry) {

		this.parent= parent;
		this.layerCell= cell;
		this.configRegistry= configRegistry;

		final List<String> configLabels= cell.getConfigLabels().getLabels();
		this.displayConverter= configRegistry.getConfigAttribute(
				CellConfigAttributes.DISPLAY_CONVERTER, DisplayMode.EDIT, configLabels);
		this.dataValidator= configRegistry.getConfigAttribute(
				EditConfigAttributes.DATA_VALIDATOR, DisplayMode.EDIT, configLabels);
		
		this.conversionEditErrorHandler= EditConfigHelper.getEditErrorHandler(
				configRegistry, EditConfigAttributes.CONVERSION_ERROR_HANDLER, configLabels);
		this.validationEditErrorHandler= EditConfigHelper.getEditErrorHandler(
				configRegistry, EditConfigAttributes.VALIDATION_ERROR_HANDLER, configLabels);

		this.dialog= createDialogInstance();
		
		setCanonicalValue(originalCanonicalValue);
		
		//this method is only needed to initialize the dialog editor, there will be no control to return
		return null;
	}

	/**
	 * Will create the dialog instance that should be wrapped by this {@link AbstractDialogCellEditor}.
	 * Note that you always need to create and return a new instance because on commit or close the
	 * dialog will be closed, which disposes the shell of the dialog. Therefore the instance will not
	 * be usable after commit/close.
	 * @return The dialog instance that should be wrapped by this {@link AbstractDialogCellEditor}
	 */
	public abstract Object createDialogInstance();
	
	/**
	 * @return The current dialog instance that is wrapped by this {@link AbstractDialogCellEditor}
	 */
	public abstract Object getDialogInstance();
	
	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.editor.ICellEditor#getEditorValue()
	 */
	@Override
	public abstract Object getEditorValue();

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.editor.ICellEditor#setEditorValue(java.lang.Object)
	 */
	@Override
	public abstract void setEditorValue(Object value);

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.editor.ICellEditor#getCanonicalValue()
	 */
	@Override
	public Object getCanonicalValue() {
		return getCanonicalValue(this.conversionEditErrorHandler);
	}

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.editor.ICellEditor#getCanonicalValue(de.walware.ecommons.waltable.edit.editor.IEditErrorHandler)
	 */
	@Override
	public Object getCanonicalValue(final IEditErrorHandler conversionErrorHandler) {
		Object canonicalValue;
		try {
			if (this.displayConverter != null) {
				//always do the conversion to check for valid entered data
				canonicalValue= this.displayConverter.displayToCanonicalValue(
						this.layerCell, this.configRegistry, getEditorValue());
			} else {
				canonicalValue= getEditorValue();
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

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.editor.ICellEditor#setCanonicalValue(java.lang.Object)
	 */
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

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.editor.ICellEditor#validateCanonicalValue(java.lang.Object)
	 */
	@Override
	public boolean validateCanonicalValue(final Object canonicalValue) {
		return validateCanonicalValue(canonicalValue, this.validationEditErrorHandler);
	}

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.editor.ICellEditor#validateCanonicalValue(java.lang.Object, de.walware.ecommons.waltable.edit.editor.IEditErrorHandler)
	 */
	@Override
	public boolean validateCanonicalValue(final Object canonicalValue, final IEditErrorHandler validationErrorHandler) {
		//do the validation if a validator is registered
		if (this.dataValidator != null) {
			try {
				final boolean validationResult= this.dataValidator.validate(
						this.layerCell, this.configRegistry, canonicalValue);

				//if the validation succeeded, remove error rendering if exists
				if (validationResult) {
					this.validationEditErrorHandler.removeError(this);
				} else {
					throw new ValidationFailedException(
							Messages.getString("AbstractCellEditor.validationFailure")); //$NON-NLS-1$
				}
				return validationResult;
			} catch (final Exception e) {
				//validation failed
				this.validationEditErrorHandler.displayError(this, e);
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
		if (this.editHandler != null && this.dialog != null && !isClosed()) {
			try {
				//always do the conversion
				final Object canonicalValue= getCanonicalValue(); 
				if (skipValidation || (!skipValidation && validateCanonicalValue(canonicalValue))) {
					final boolean committed= this.editHandler.commit(canonicalValue, direction);
					
					if (committed && closeAfterCommit) {
						close();
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

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.gui.ICellEditDialog#getCommittedValue()
	 */
	@Override
	public Object getCommittedValue() {
		return this.editHandler.getCommittedValue();
	}

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.editor.ICellEditor#close()
	 */
	@Override
	public abstract void close();

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.editor.ICellEditor#isClosed()
	 */
	@Override
	public abstract boolean isClosed();

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.editor.ICellEditor#getEditorControl()
	 */
	@Override
	public Control getEditorControl() {
		//as this editor wraps a dialog, there is no explicit editor control
		return null;
	}

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.editor.ICellEditor#createEditorControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createEditorControl(final Composite parent) {
		//as this editor wraps a dialog, there is no explicit editor control
		return null;
	}

	/* (non-Javadoc)
	 * @see de.walware.ecommons.waltable.edit.editor.ICellEditor#openInline(de.walware.ecommons.waltable.config.IConfigRegistry, java.util.List)
	 */
	@Override
	public boolean openInline(final IConfigRegistry configRegistry, final List<String> configLabels) {
		return false;
	}

	@Override
	public boolean supportMultiEdit(final IConfigRegistry configRegistry, final List<String> configLabels) {
		final Boolean supportMultiEdit= configRegistry.getConfigAttribute(
				EditConfigAttributes.SUPPORT_MULTI_EDIT, DisplayMode.EDIT, configLabels);
		return (supportMultiEdit == null || supportMultiEdit);
	}

	@Override
	public boolean openMultiEditDialog() {
		return true;
	}
	
	@Override
	public boolean openAdjacentEditor() {
		//as editing with a dialog should only result in committing the value and then 
		//set the selection to the edited value, it doesn't make sense to open the adjacent editor.
		return false;
	}

	@Override
	public boolean activateAtAnyPosition() {
		return true;
	}
	
	@Override
	public void addEditorControlListeners() {
		//there is no need for special editor control listeners here
	}
	
	@Override
	public void removeEditorControlListeners() {
		//there is no need for special editor control listeners here
	}

	@Override
	public Rectangle calculateControlBounds(final Rectangle cellBounds) {
		return cellBounds;
	}

	@Override
	public void setDialogSettings(final Map<String, Object> editDialogSettings) {
		this.editDialogSettings= editDialogSettings;
	}

	@Override
	public long getColumnPosition() {
		return this.layerCell.getColumnPosition();
	}

	@Override
	public long getRowPosition() {
		return this.layerCell.getRowPosition();
	}
	
}
