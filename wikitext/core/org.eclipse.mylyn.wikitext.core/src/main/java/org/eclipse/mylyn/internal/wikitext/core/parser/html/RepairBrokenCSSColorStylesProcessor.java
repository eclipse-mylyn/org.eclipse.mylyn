/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Billy Huang - Bug 396332
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.parser.html;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.core.util.css.CssParser;
import org.eclipse.mylyn.internal.wikitext.core.util.css.CssRule;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Selector;

/**
 * Repairs missing "#"-prefix in CSS color styles using hex color values
 * 
 * @author Billy Huang
 */
public class RepairBrokenCSSColorStylesProcessor extends DocumentProcessor {

	@Override
	public void process(Document document) {
		Element body = document.body();

		CssParser cssParser = new CssParser();

		for (Element element : Selector.select("[style]", body)) { //$NON-NLS-1$
			String style = element.attr("style"); //$NON-NLS-1$

			String newStyle = ""; //$NON-NLS-1$
			List<CssRule> rules = null;
			CssRule newRule = null;

			if (style != null && style.length() > 0) {
				rules = cssParser.parseBlockContent(style);

				Iterator<CssRule> ruleIt = rules.iterator();
				while (ruleIt.hasNext()) {
					CssRule rule = ruleIt.next();
					if ("color".equals(rule.name)) { //$NON-NLS-1$
						String color = rule.value;
						// no 3- or 6-character CSS color names are written in hex characters
						Matcher invalidHexColorMatcher = Pattern.compile(
								"^\\s*([0-9a-fA-F]{6}|[0-9a-fA-F]{3})(?:\\s+(.+))?\\s*$") //$NON-NLS-1$
								.matcher(color);
						if (invalidHexColorMatcher.matches()) {
							String newColor = "#" + invalidHexColorMatcher.group(1); //$NON-NLS-1$
							String additionalDeclarations = invalidHexColorMatcher.group(2);
							if (additionalDeclarations != null) {
								newColor += " " + additionalDeclarations; //$NON-NLS-1$
							}
							ruleIt.remove();
							newRule = new CssRule("color", newColor.trim(), 0, 0, 0, 0); //$NON-NLS-1$
						}
					}
				}
			}

			if (rules != null && newRule != null) {
				newStyle = addRuleToStyle(newStyle, newRule);
				for (CssRule rule : rules) {
					newStyle = addRuleToStyle(newStyle, rule);
				}
				element.attr("style", newStyle); //$NON-NLS-1$
			}
		}
	}

	private String addRuleToStyle(String style, CssRule rule) {
		return style += rule.name + ": " + rule.value + ";"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
