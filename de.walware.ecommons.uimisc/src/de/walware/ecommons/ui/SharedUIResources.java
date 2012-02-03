/*******************************************************************************
 * Copyright (c) 2009-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui;

import org.eclipse.jface.resource.ImageRegistry;

import de.walware.ecommons.ui.internal.UIMiscellanyPlugin;


public class SharedUIResources {
	
	
	public static final String PLUGIN_ID = UIMiscellanyPlugin.PLUGIN_ID;
	
	
	public static final String ADDITIONS_MENU_ID = "additions"; //$NON-NLS-1$
	
	public static final String EDIT_COPYPASTE_MENU_ID = "edit.copypaste"; //$NON-NLS-1$
	
	public static final String VIEW_EXPAND_MENU_ID = "view.expand"; //$NON-NLS-1$
	
	public static final String VIEW_SORT_MENU_ID = "view.sort"; //$NON-NLS-1$
	
	public static final String VIEW_FILTER_MENU_ID = "view.filter"; //$NON-NLS-1$
	
	
	public static final String NEW_PAGE_COMMAND_ID = "de.walware.ecommons.base.commands.NewPage"; //$NON-NLS-1$
	public static final String CLOSE_PAGE_COMMAND_ID = "de.walware.ecommons.base.commands.ClosePage"; //$NON-NLS-1$
	public static final String CLOSE_ALL_PAGES_COMMAND_ID = "de.walware.ecommons.base.commands.CloseAllPages"; //$NON-NLS-1$
	
	/** equal to org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds#FIND_NEXT */
	public static final String FIND_NEXT_COMMAND_ID = "org.eclipse.ui.edit.findNext"; //$NON-NLS-1$
	/** equal to org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds#FIND_PREVIOUS */
	public static final String FIND_PREVIOUS_COMMAND_ID = "org.eclipse.ui.edit.findPrevious"; //$NON-NLS-1$
	
	
	public static final String PLACEHOLDER_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/obj/dummy"; //$NON-NLS-1$
	
	public static final String OBJ_USER_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/obj/user"; //$NON-NLS-1$
	
	public static final String OBJ_LINE_MATCH_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/obj/line_match"; //$NON-NLS-1$
	
	public static final String OVR_DEFAULT_MARKER_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/ovr/default_marker"; //$NON-NLS-1$
	
	public static final String LOCTOOL_FILTER_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/filter_view"; //$NON-NLS-1$
	public static final String LOCTOOLD_FILTER_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctoold/filter_view"; //$NON-NLS-1$
	
	public static final String LOCTOOL_SORT_ALPHA_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/sort.alpha"; //$NON-NLS-1$
	public static final String LOCTOOL_SORT_SCORE_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/sort.score"; //$NON-NLS-1$
	
	public static final String LOCTOOL_CASESENSITIVE_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/casesensitive"; //$NON-NLS-1$
	
	public static final String LOCTOOL_EXPANDALL_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/expandall"; //$NON-NLS-1$
	public static final String LOCTOOL_COLLAPSEALL_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/collapseall"; //$NON-NLS-1$
	
	public static final String LOCTOOL_SCROLLLOCK_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/scrolllock"; //$NON-NLS-1$
	
	public static final String LOCTOOL_CLEARSEARCH_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/clearsearch"; //$NON-NLS-1$
	public static final String LOCTOOLD_CLEARSEARCH_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctoold/clearsearch"; //$NON-NLS-1$
	
	public static final String LOCTOOL_SYNCHRONIZED_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/synchronized"; //$NON-NLS-1$
	
	public static final String LOCTOOL_FAVORITES_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctoolh/favorites"; //$NON-NLS-1$
	
	public static final String LOCTOOL_CLOSETRAY_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/close"; //$NON-NLS-1$
	public static final String LOCTOOL_CLOSETRAY_H_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctoolh/close"; //$NON-NLS-1$
	
	public static final String LOCTOOL_LEFT_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/left"; //$NON-NLS-1$
	public static final String LOCTOOL_LEFT_H_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctoolh/left"; //$NON-NLS-1$
	public static final String LOCTOOL_RIGHT_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/right"; //$NON-NLS-1$
	public static final String LOCTOOL_RIGHT_H_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctoolh/right"; //$NON-NLS-1$
	
	public static final String LOCTOOL_UP_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/up"; //$NON-NLS-1$
	public static final String LOCTOOL_UP_H_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctoolh/up"; //$NON-NLS-1$
	public static final String LOCTOOL_DOWN_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/down"; //$NON-NLS-1$
	public static final String LOCTOOL_DOWN_H_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctoolh/down"; //$NON-NLS-1$
	
	public static final String LOCTOOL_CHANGE_PAGE_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/change_page"; //$NON-NLS-1$
	public static final String LOCTOOL_PIN_PAGE_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctool/pin_page"; //$NON-NLS-1$
	public static final String LOCTOOLD_PIN_PAGE_IMAGE_ID = UIMiscellanyPlugin.PLUGIN_ID + "/image/loctoold/pin_page"; //$NON-NLS-1$
	
	
	/**
	 * A shared color manager.
	 * 
	 * @return the color manager
	 */
	public static ColorManager getColors() {
		return UIMiscellanyPlugin.getDefault().getColorManager();
	}
	
	/**
	 * The image registry of ECommonsUI
	 * 
	 * @return the image registry
	 */
	public static ImageRegistry getImages() {
		return UIMiscellanyPlugin.getDefault().getImageRegistry();
	}
	
}
