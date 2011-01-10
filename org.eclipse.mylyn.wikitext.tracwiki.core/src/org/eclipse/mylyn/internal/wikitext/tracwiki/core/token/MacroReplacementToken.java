/*******************************************************************************
 * Copyright (c) 2010 Mat Booth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mat Booth
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.tracwiki.core.token;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * Detects macro tags of the format: <code>[[MacroName]]</code> or <code>[[MacroName(arg1,arg2,...)]]</code>
 * 
 * @author Mat Booth
 */
public class MacroReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "(?:\\[\\[([a-zA-Z]+)(?:\\(((?:(?!\\]\\]).)*)\\))?\\]\\])"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new MacroReplacementTokenProcessor();
	}

	private static class MacroReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String macro = group(1);
			String[] args;
			if (group(2) != null && group(2).length() > 0) {
				args = group(2).split(","); //$NON-NLS-1$
			} else {
				args = new String[0];
			}

			// Handle image macro
			if (macro.equalsIgnoreCase("Image")) { //$NON-NLS-1$
				imageMacro(args);
			}

			// TODO: Support more macros! All others are currently ignored.
		}

		/**
		 * Process the image macro. The first argument is a mandatory file specification. Remaining arguments specify
		 * optional attributes and styles.
		 * 
		 * @param args
		 *            list of arguments passed into the macro
		 */
		public void imageMacro(String[] args) {
			// Don't do anything if we don't get at least one argument
			if (args.length < 1) {
				return;
			}

			// Border thickness attribute pattern
			Pattern borderPattern = Pattern.compile("border=([0-9]+)"); //$NON-NLS-1$

			// Size attribute pattern
			Pattern sizePattern = Pattern.compile("(?:(width|height)=|^)?([0-9]+)(%|px)?"); //$NON-NLS-1$

			// Floating alignment attribute pattern
			Pattern alignPattern = Pattern.compile("(?:align=)?(right|left|top|bottom)"); //$NON-NLS-1$

			ImageAttributes attributes = new ImageAttributes();
			for (int i = 1; i < args.length; i++) {
				String arg = args[i].trim();
				Matcher m;

				// Border thickness attribute
				m = borderPattern.matcher(arg);
				if (m.matches()) {
					attributes.setBorder(Integer.parseInt(m.group(1)));
					continue;
				}
				// Size attribute
				m = sizePattern.matcher(arg);
				if (m.matches()) {
					int size = Integer.parseInt(m.group(2));
					if (m.group(1) == null || m.group(1).length() == 0 || m.group(1).equalsIgnoreCase("width")) { //$NON-NLS-1$
						attributes.setWidth(size);
						attributes.setWidthPercentage(m.group(3) != null && m.group(3).equals("%")); //$NON-NLS-1$
					} else {
						attributes.setHeight(size);
						attributes.setHeightPercentage(m.group(3) != null && m.group(3).equals("%")); //$NON-NLS-1$
					}
					continue;
				}
				// Floating alignment attribute
				// (to create nice layouts, Trac translates the "align" parameter to a float CSS style 
				// instead of text-align or vertical-align, so we should emulate that here)
				m = alignPattern.matcher(arg);
				if (m.matches()) {
					attributes.appendCssStyle("float:" + m.group(1) + ";"); //$NON-NLS-1$ //$NON-NLS-2$
					continue;
				}

				// All other key/value pair attributes that aren't syntactically special
				String[] parts = arg.split("="); //$NON-NLS-1$
				if (parts.length == 2) {
					if (parts[0].equalsIgnoreCase("id")) { //$NON-NLS-1$
						attributes.setId(parts[1]);
					}
					if (parts[0].equalsIgnoreCase("class")) { //$NON-NLS-1$
						attributes.appendCssClass(parts[1]);
					}
					if (parts[0].equalsIgnoreCase("alt")) { //$NON-NLS-1$
						attributes.setAlt(parts[1]);
					}
					if (parts[0].equalsIgnoreCase("title")) { //$NON-NLS-1$
						attributes.setTitle(parts[1]);
					}
				}
			}

			builder.image(attributes, args[0]);
		}
	}
}
