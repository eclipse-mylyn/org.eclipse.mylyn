/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor.preferences;

import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.CssStyleManager;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.CssStyleManager.CssRule;
import org.eclipse.swt.widgets.Composite;

/**
 * A field editor for CSS styles. Adds validation to CSS rule input.
 * 
 * @author David Green
 */
public class CssStyleFieldEditor extends StringFieldEditor {

	private final CssStyleManager cssStyleManager;

	public CssStyleFieldEditor(CssStyleManager cssStyleManager, String preferenceKey, String key, Composite parent) {
		super(preferenceKey, key, parent);
		this.cssStyleManager = cssStyleManager;
	}

	/**
	 * validate the input
	 */
	@Override
	protected boolean doCheckState() {
		String value = getStringValue();
		if (value != null && value.trim().length() > 0) {
			// here we validate that the value looks like it is composed of valid CSS rules

			int offset = 0;
			Iterator<CssRule> ruleIterator = cssStyleManager.createRuleIterator(value);
			while (ruleIterator.hasNext()) {
				CssRule rule = ruleIterator.next();

				// detect gaps between rules.  Such gaps are areas of text that weren't detected as a CSS rule.
				if (rule.offset > offset) {
					String gap = value.substring(offset, rule.offset);
					if (gap.trim().length() != 0) {
						setErrorMessage(MessageFormat.format(
								Messages.getString("CssStyleFieldEditor.1"), gap.trim(), offset)); //$NON-NLS-1$
						return false;
					}
				}
				offset = rule.offset + rule.length;
				// unknown rules should create an error
				if (!rule.knownRule) {
					StringBuilder recognizedNames = new StringBuilder();
					for (String recognizedName : cssStyleManager.getRecognizedRuleNames()) {
						if (recognizedNames.length() > 0) {
							recognizedNames.append(Messages.getString("CssStyleFieldEditor.2")); //$NON-NLS-1$
						}
						recognizedNames.append(recognizedName);
					}
					setErrorMessage(MessageFormat.format(
							Messages.getString("CssStyleFieldEditor.0"), rule.name, recognizedNames)); //$NON-NLS-1$
					return false;
				}
				if (CssStyleManager.RULE_COLOR.equals(rule.name)
						|| CssStyleManager.RULE_BACKGROUND_COLOR.equals(rule.name)) {
					Integer rgb = CssStyleManager.cssColorRgb(rule.value);
					if (rgb == null) {
						setErrorMessage(MessageFormat.format(Messages.getString("CssStyleFieldEditor.3"), rule.value)); //$NON-NLS-1$
						return false;
					}
				}
			}
			// detect trailing text that wasn't detected as a CSS rule.
			if (offset < value.length() - 1) {
				String gap = value.substring(offset, value.length());
				if (gap.trim().length() != 0) {
					setErrorMessage(MessageFormat.format(
							Messages.getString("CssStyleFieldEditor.1"), gap.trim(), offset)); //$NON-NLS-1$
					return false;
				}
			}
		}
		return super.doCheckState();
	}
}
