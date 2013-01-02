/*******************************************************************************
 * Copyright (c) 2012-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.util;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;


public abstract class AbstractLocalTransfer extends ByteArrayTransfer {
	
	
	private final int fTypeId;
	private final String fTypeName;
	
	private Object fObject ;
	
	
	protected AbstractLocalTransfer(final String name) {
		fTypeName = name + ':' + System.currentTimeMillis() + '-' + hashCode();
		fTypeId = registerType(fTypeName);
	}
	
	
	@Override
	protected int[] getTypeIds() {
		return new int[] { fTypeId };
	}
	
	@Override
	protected String[] getTypeNames() {
		return new String[] { fTypeName };
	}
	
	
	protected abstract boolean isValidType(Object object);
	
	protected Object getObject() {
		return fObject;
	}
	
	
	@Override
	protected void javaToNative(final Object object, final TransferData transferData) {
		if (object != null && isValidType(object) && isSupportedType(transferData)) {
			fObject = object;
		}
		else {
			fObject = null;
		}
		super.javaToNative(fTypeName.getBytes(), transferData);
	}
	
	@Override
	protected Object /*T*/ nativeToJava(final TransferData transferData) {
		final Object result = super.nativeToJava(transferData);
		if ((result instanceof byte[]) && fTypeName.equals(new String((byte[]) result))) {
			return fObject;
		}
		else {
			return null;
		}
	}
	
}
