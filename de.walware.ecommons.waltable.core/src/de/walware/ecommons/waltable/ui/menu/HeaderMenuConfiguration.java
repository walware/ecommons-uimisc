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
package de.walware.ecommons.waltable.ui.menu;


import de.walware.ecommons.waltable.NatTable;

public class HeaderMenuConfiguration extends AbstractHeaderMenuConfiguration {

	public HeaderMenuConfiguration(final NatTable natTable) {
		super(natTable);
	}

	@Override
	protected PopupMenuBuilder createColumnHeaderMenu(final NatTable natTable) {
		return super.createColumnHeaderMenu(natTable)
//								.withHideColumnMenuItem()
//								.withShowAllColumnsMenuItem()
//								.withCreateColumnGroupsMenuItem()
//								.withUngroupColumnsMenuItem()
								.withAutoResizeSelectedColumnsMenuItem()
								.withColumnStyleEditor();
//								.withClearAllFilters();
	}

	@Override
	protected PopupMenuBuilder createRowHeaderMenu(final NatTable natTable) {
		return super.createRowHeaderMenu(natTable)
								.withAutoResizeSelectedRowsMenuItem();
	}

	@Override
	protected PopupMenuBuilder createCornerMenu(final NatTable natTable) {
		return super.createCornerMenu(natTable);
//								.withShowAllColumnsMenuItem();
	}
}
