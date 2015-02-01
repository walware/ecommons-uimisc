/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.util;

import org.eclipse.swt.browser.Browser;


public class BrowserUtil {
	
	
	private static void appendEscapedJavascriptString(final StringBuilder sb, final String s) {
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			switch (c) {
			case '\\':
			case '\"':
			case '\'':
				sb.append('\\');
				sb.append(c);
				continue;
			default:
				sb.append(c);
				continue;
			}
		}
	}
	
	public static String getSelectedText(final Browser browser) {
		final Object value= browser.evaluate(
				"if (window.getSelection) {" + //$NON-NLS-1$
					"var sel = window.getSelection();" + //$NON-NLS-1$
					"if (sel.getRangeAt) {" + //$NON-NLS-1$
						"return sel.getRangeAt(0).toString();" + //$NON-NLS-1$
					"}" + //$NON-NLS-1$
					"return sel;" + //$NON-NLS-1$
				"}" + //$NON-NLS-1$
				"else if (document.getSelection) {" + //$NON-NLS-1$
					"return document.getSelection();" + //$NON-NLS-1$
				"}" + //$NON-NLS-1$
				"else if (document.selection) {" + //$NON-NLS-1$
					"return document.selection.createRange().text;" + //$NON-NLS-1$
				"}" + //$NON-NLS-1$
				"else {" + //$NON-NLS-1$
					"return '';" + //$NON-NLS-1$
				"}"); //$NON-NLS-1$
		if (value instanceof String) {
			return (String) value;
		}
		return null;
	}
	
	public static boolean searchText(final Browser browser, final String text,
			final boolean forward, final boolean caseSensitive, final boolean wrap) {
		final StringBuilder script = new StringBuilder(50);
		script.append("return window.find(\""); //$NON-NLS-1$
		appendEscapedJavascriptString(script, text);
		script.append("\","); //$NON-NLS-1$
		script.append(caseSensitive);
		script.append(',');
		script.append(!forward); // upward
		script.append(',');
		script.append(wrap); // wrap
		script.append(",false,true)"); // wholeWord, inFrames //$NON-NLS-1$
						// inFrames fixes wrap in some situations
		final Object found = browser.evaluate(script.toString());
		return Boolean.TRUE.equals(found);
	}
	
}
