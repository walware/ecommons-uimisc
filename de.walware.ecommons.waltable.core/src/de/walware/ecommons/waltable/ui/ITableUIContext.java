/*******************************************************************************
 * Copyright (c) 2016 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableContext;


public interface ITableUIContext extends IRunnableContext {
	
	
	void show(IStatus status);
	
}
