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
package de.walware.ecommons.waltable.edit;

import java.util.List;

import de.walware.ecommons.waltable.config.ConfigRegistry;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.edit.config.LoggingErrorHandling;
import de.walware.ecommons.waltable.edit.editor.IEditErrorHandler;
import de.walware.ecommons.waltable.style.ConfigAttribute;
import de.walware.ecommons.waltable.style.DisplayMode;

/**
 * Helper class that will retrieve edit configuration values out of the {@link IConfigRegistry}.
 */
public class EditConfigHelper {

	/**
	 * Searches for the registered {@link IEditErrorHandler} that should be used by this editor.
	 * @param configRegistry The {@link ConfigRegistry} of the NatTable instance this editor is connected to.
	 * @param configAttribute The config attribute specifying if the {@link IEditErrorHandler} for conversion
	 * 			or validation errors is requested.
	 * @param configLabels The config labels attached to the cell this editor is opened for, needed to 
	 * 			find the registered {@link IEditErrorHandler} in {@link ConfigRegistry}.
	 * @return The registered {@link IEditErrorHandler} out of the specified {@link ConfigRegistry} for
	 * 			the config attribute and config labels, or the {@link LoggingErrorHandling} if no other
	 * 			{@link IEditErrorHandler} is registered.
	 * @see EditConfigAttributes#CONVERSION_ERROR_HANDLER
	 * @see EditConfigAttributes#VALIDATION_ERROR_HANDLER
	 */
	public static IEditErrorHandler getEditErrorHandler(
			final IConfigRegistry configRegistry, final ConfigAttribute<IEditErrorHandler> configAttribute, final List<String> configLabels) {
		
		IEditErrorHandler errorHandler= configRegistry.getConfigAttribute(configAttribute, DisplayMode.EDIT, configLabels);
		if (errorHandler == null) {
			//set LoggingErrorHandling as default
			errorHandler= new LoggingErrorHandling();
		}
		return errorHandler;
	}
	
	/**
	 * Determines whether the editor should be opened inline or using a dialog. By default it
	 * will check this by configuration attribute {@link EditConfigAttributes#OPEN_IN_DIALOG}. 
	 * If there is no configuration found for this, <code>true</code> will be returned for 
	 * backwards compatibility.
	 * <p>If this method returns <code>true</code>, the editor will be opened inline (default).</p>
	 * <p>There might be editors that are only able to be opened in a dialog. These implementations
	 * need to override this method to always return <code>false</code>, so the editor never
	 * gets opened inline.</p>
	 * @param configRegistry The {@link IConfigRegistry} to retrieve the configuration for
	 * 			inline/dialog editing out of. Needed here because the instance {@link IConfigRegistry}
	 * 			might not be set on calling this method.
	 * @param configLabels The labels out of the LabelStack of the cell whose editor should be activated.
	 * 			Needed here because this method needs to be called prior to activation to determine
	 * 			where to activate it.
	 * @return <code>true</code> if the editor should opened inline, <code>false</code>
	 * 			if not.
	 * @see EditConfigAttributes#OPEN_IN_DIALOG
	 */
	public static boolean openInline(final IConfigRegistry configRegistry, final List<String> configLabels) {
		final Boolean openInDialog= configRegistry.getConfigAttribute(
				EditConfigAttributes.OPEN_IN_DIALOG, DisplayMode.EDIT, configLabels);
		return (openInDialog == null || !openInDialog);
	}
	
	/**
	 * Determines whether this editor supports multi edit behaviour or not. If this method returns
	 * <code>true</code>, on selecting and pressing F2 on several cells that are editable, having 
	 * the same editor type and converter registered, a multi edit dialog will open. 
	 * By default this method will return <code>true</code>. You can change this behaviour by setting
	 * the configuration attribute {@link EditConfigAttributes#SUPPORT_MULTI_EDIT}. 
	 * <p>You should consider returning <code>false</code> e.g. if the update operation is complex or 
	 * you use conditional validation, where a value is validated against another value in the data model.
	 * @param configRegistry The {@link IConfigRegistry} to retrieve the configuration for
	 * 			multi edit support out of. Needed here because the instance {@link IConfigRegistry}
	 * 			might not be set on calling this method.
	 * @param configLabels The labels out of the LabelStack of the cell whose editor should be activated.
	 * 			Needed here because this method needs to be called prior to activation to determine
	 * 			where to activate it.
	 * @return <code>true</code> if this editor will open in a subdialog for multi editing, <code>false</code>
	 * 			if the multi editing of this kind of cell editor is not supported.
	 * @see EditConfigAttributes#SUPPORT_MULTI_EDIT
	 */
	public static boolean supportMultiEdit(final IConfigRegistry configRegistry, final List<String> configLabels) {
		final Boolean supportMultiEdit= configRegistry.getConfigAttribute(
				EditConfigAttributes.SUPPORT_MULTI_EDIT, DisplayMode.EDIT, configLabels);
		return (supportMultiEdit == null || supportMultiEdit);
	}
	
	/**
	 * Determines behaviour after committing the value of this editor in combination with selection
	 * movement. If this method return <code>true</code> and the selection is moved after committing, 
	 * the editor for the newly selected cell will be activated immediately. If this method returns
	 * <code>false</code> or the selection is not moved after commit, no action should be executed.
	 * <p>
	 * The behaviour previous to this configuration was to not open the adjacent editor. So if there
	 * is no configuration registered for this, <code>false</code> will be returned by default.</p>
	 * <p>
	 * Note: It only makes sense to call this method if the editor is already activated. Calling this
	 * 		 method on an editor that has not been activated already will lead to exceptions.</p>
	 * @param configRegistry The {@link IConfigRegistry} to retrieve the configuration for
	 * 			multi edit support out of. Needed here because the instance {@link IConfigRegistry}
	 * 			might not be set on calling this method.
	 * @param configLabels The labels out of the LabelStack of the cell whose editor should be activated.
	 * 			Needed here because this method needs to be called prior to activation to determine
	 * 			where to activate it.
	 * @return <code>true</code> if the adjacent editor should be opened if the selection moves after
	 * 			commit, <code>false</code> if not.
	 * @see EditConfigAttributes#OPEN_ADJACENT_EDITOR
	 */
	public static boolean openAdjacentEditor(final IConfigRegistry configRegistry, final List<String> configLabels) {
		final Boolean openAdjacentEditor= configRegistry.getConfigAttribute(
				EditConfigAttributes.OPEN_ADJACENT_EDITOR, DisplayMode.EDIT, configLabels);
		return (openAdjacentEditor != null && openAdjacentEditor);
	}

}
