/*=============================================================================#
 # Copyright (c) 2005-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.workbench.ui;

import org.eclipse.ui.IPageLayout;


public interface IWorkbenchPerspectiveElements {
	
	
	String PROJECT_EXPLORER_VIEW = IPageLayout.ID_PROJECT_EXPLORER;
	@SuppressWarnings("deprecation")
	String RESOURCE_NAVIGATOR_VIEW = IPageLayout.ID_RES_NAV;
	
	String TASKS_VIEW = IPageLayout.ID_TASK_LIST;
	String PROBLEM_VIEW = IPageLayout.ID_PROBLEM_VIEW;
	String BOOKMARKS_VIEW = IPageLayout.ID_BOOKMARKS;
	String PROGRESS_VIEW = IPageLayout.ID_PROGRESS_VIEW;
	String PROPERTIES_VIEW = IPageLayout.ID_PROP_SHEET;
	
	String OUTLINE_VIEW = IPageLayout.ID_OUTLINE;
	String TEMPLATES_VIEW = "org.eclipse.ui.texteditor.TemplatesView"; // TemplatesView.ID //$NON-NLS-1$
	String FILTERS_VIEW = "de.walware.statet.base.views.ContentFilter"; //$NON-NLS-1$
	
	String SEARCH_VIEW = "org.eclipse.search.ui.views.SearchView"; // NewSearchUI.SEARCH_VIEW_ID //$NON-NLS-1$
	String CONSOLE_VIEW = "org.eclipse.ui.console.ConsoleView"; // IConsoleConstants.ID_CONSOLE_VIEW //$NON-NLS-1$
	
	String NICO_CMDHISTORY_VIEW = "de.walware.statet.nico.views.HistoryView"; //$NON-NLS-1$
	String NICO_OBJECTBROWSER_VIEW = "de.walware.statet.nico.views.ObjectBrowser"; //$NON-NLS-1$
	
	
	String LAUNCH_ACTION_SET = "org.eclipse.debug.ui.launchActionSet"; //$NON-NLS-1$
	String BREAKPOINT_ACTION_SET = "org.eclipse.debug.ui.breakpointActionSet"; //$NON-NLS-1$
	String NAVIGATE_ACTION_SET = IPageLayout.ID_NAVIGATE_ACTION_SET;
	
	
	String NEW_FOLDER_WIZARD = "org.eclipse.ui.wizards.new.folder"; //$NON-NLS-1$
	String NEW_TEXTFILE_WIZARD = "org.eclipse.ui.wizards.new.file"; //$NON-NLS-1$
	String NEW_UNTITLED_TEXTFILE_WIZARD = "org.eclipse.ui.editors.wizards.UntitledTextFileWizard"; //$NON-NLS-1$
	
	
	String DEBUG_PERSPECTIVE = "org.eclipse.debug.ui.DebugPerspective"; //$NON-NLS-1$
	
}
