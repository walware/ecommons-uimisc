/*******************************************************************************
 * Copyright (c) 2007-2010 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;


/**
 * 
 */
public class MessageUtil {
	
	
	public static final Pattern AMPERSAND_PATTERN = Pattern.compile("\\&"); //$NON-NLS-1$
	public static final String AMPERSAND_TOOLTIP_REPLACEMENT = Matcher.quoteReplacement("&&"); //$NON-NLS-1$
	
	public static String removeMnemonics(final String label) {
		return LegacyActionTools.removeMnemonics(label);
	}
	
	public static String escapeForFormText(final String text) {
		final StringBuilder escaped = new StringBuilder(text.length());
		ITERATE_CHARS : for (int i = 0; i < text.length(); i++) {
			final char c = text.charAt(i);
			switch (c) {
			case '<':
				escaped.append("&lt;"); //$NON-NLS-1$
				continue ITERATE_CHARS;
			case '>':
				escaped.append("&gt;"); //$NON-NLS-1$
				continue ITERATE_CHARS;
			case '&':
				escaped.append("&amp;"); //$NON-NLS-1$
				continue ITERATE_CHARS;
			case '"':
				escaped.append("&quot;"); //$NON-NLS-1$
				continue ITERATE_CHARS;
			case '\'':
				escaped.append("&apos;"); //$NON-NLS-1$
				continue ITERATE_CHARS;
			default:
				escaped.append(c);
				continue ITERATE_CHARS;
			}
		}
		return escaped.toString();
	}
	
	public static String escapeForTooltip(final CharSequence text) {
		return AMPERSAND_PATTERN.matcher(text).replaceAll(AMPERSAND_TOOLTIP_REPLACEMENT);
	}
	
	public static String escapeForMenu(final String text) {
		final StringBuilder escaped = new StringBuilder(text.length());
		ITERATE_CHARS : for (int i = 0; i < text.length(); i++) {
			final char c = text.charAt(i);
			switch (c) {
			case '&':
				escaped.append("&&"); //$NON-NLS-1$
				continue ITERATE_CHARS;
			case '\t':
				escaped.append("  "); //$NON-NLS-1$
				continue ITERATE_CHARS;
			case '\n':
				escaped.append("  "); //$NON-NLS-1$
				continue ITERATE_CHARS;
			default:
				escaped.append(c);
				continue ITERATE_CHARS;
			}
		}
		return escaped.toString();
	}
	
	public static String processURLPart(final String url) {
		return TextProcessor.process(url, ":@?-"); //$NON-NLS-1$
	}
	
	
	/**
	 * Returns the modifier string for the given SWT modifier
	 * modifier bits.
	 *
	 * @param stateMask	the SWT modifier bits
	 * @return the modifier string
	 */
	public static String getModifierString(final int stateMask) {
		if (stateMask != 0) {
			final StringBuilder sb = new StringBuilder();
			if ((stateMask & SWT.CTRL) == SWT.CTRL) {
				sb.append(Action.findModifierString(SWT.CTRL));
				sb.append(" + "); //$NON-NLS-1$
			}
			if ((stateMask & SWT.ALT) == SWT.ALT) {
				sb.append(Action.findModifierString(SWT.ALT));
				sb.append(" + "); //$NON-NLS-1$
			}
			if ((stateMask & SWT.SHIFT) == SWT.SHIFT) {
				sb.append(Action.findModifierString(SWT.SHIFT));
				sb.append(" + "); //$NON-NLS-1$
			}
			if ((stateMask & SWT.COMMAND) == SWT.COMMAND) {
				sb.append(Action.findModifierString(SWT.COMMAND));
				sb.append(" + "); //$NON-NLS-1$
			}
			if (sb.length() > 0) {
				return sb.substring(0, sb.length()-3);
			}
		}
		return ""; //$NON-NLS-1$
	}
	
	
	private MessageUtil() {}
	
}
