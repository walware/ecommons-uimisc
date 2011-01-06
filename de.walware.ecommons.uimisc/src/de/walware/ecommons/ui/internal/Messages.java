/*******************************************************************************
 * Copyright (c) 2006-2011 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.internal;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
	
	
	public static String CopyToClipboard_error_title;
	public static String CopyToClipboard_error_message;
	
	public static String SearchWorkspace_label;
	public static String BrowseFilesystem_label;
	public static String BrowseWorkspace_label;
	public static String BrowseFilesystem_ForFile_label;
	public static String BrowseWorkspace_ForFile_label;
	public static String BrowseFilesystem_ForDir_label;
	public static String BrowseWorkspace_ForDir_label;
	
	public static String ChooseResource_Task_description;
	public static String ResourceSelectionDialog_title;
	public static String ResourceSelectionDialog_message;
	
	public static String ContainerSelectionControl_label_EnterOrSelectFolder;
	public static String ContainerSelectionControl_label_SelectFolder;
	public static String ContainerSelectionControl_error_FolderEmpty;
	public static String ContainerSelectionControl_error_ProjectNotExists;
	public static String ContainerSelectionControl_error_PathOccupied;
	
	public static String FilterFavouredContainersAction_label;
	public static String FilterFavouredContainersAction_description;
	public static String FilterFavouredContainersAction_tooltip;
	
	public static String Hover_FocusAffordance_message;
	
	
	static {
		NLS.initializeMessages(Messages.class.getName(), Messages.class);
	}
	private Messages() {}
	
}
