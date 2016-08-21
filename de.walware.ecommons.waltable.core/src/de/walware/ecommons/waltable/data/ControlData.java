/*=============================================================================#
 # Copyright (c) 2010-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.waltable.data;


public class ControlData {
	
	
	public static final int ERROR= 1 << 0;
	public static final int NA= 1 << 1;
	public static final int ASYNC= 1 << 2;
	
	
	private final int code;
	
	private final String text;
	
	
	public ControlData(final int code, final String text) {
		this.code= code;
		this.text= text;
	}
	
	
	public int getCode() {
		return this.code;
	}
	
	@Override
	public String toString() {
		return this.text;
	}
	
	
	@Override
	public int hashCode() {
		return this.text.hashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		return (obj == this
				|| (obj instanceof ControlData
						&& this.code == ((ControlData) obj).code ));
	}
	
}
