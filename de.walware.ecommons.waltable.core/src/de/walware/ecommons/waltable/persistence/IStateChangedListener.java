/*******************************************************************************
 * Copyright (c) 2012-2016 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@gmail.com> - initial API and implementation
 *******************************************************************************/
package de.walware.ecommons.waltable.persistence;

/**
 * Listener interface to react on changes on NatTable states/view configurations
 * made by using the PersistenceDialog.
 * 
 * @see StateChangeEvent
 */
public interface IStateChangedListener {

	/**
	 * Handle the {@link StateChangeEvent} fired by the PersistenceDialog.
	 * @param event The {@link StateChangeEvent} to handle
	 */
	void handleStateChange(final StateChangeEvent event);
}
