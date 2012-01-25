/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.parser.html;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.mylyn.internal.wikitext.core.util.css.CssParser;
import org.eclipse.mylyn.internal.wikitext.core.util.css.CssRule;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Selector;

/**
 * Removes excessive inline styles from HTML,
 * 
 * @author David Green
 */
public class RemoveExcessiveStylesProcessor extends DocumentProcessor {

	@Override
	public void process(Document document) {
		Element body = document.body();

		CssParser cssParser = new CssParser();

		for (Element element : Selector.select("[style], font, span", body)) { //$NON-NLS-1$
			String style = element.attr("style"); //$NON-NLS-1$

			String newStyle = ""; //$NON-NLS-1$
			List<CssRule> rules = null;

			if (style != null && style.length() > 0) {
				rules = cssParser.parseBlockContent(style);

				Iterator<CssRule> ruleIt = rules.iterator();
				while (ruleIt.hasNext()) {
					CssRule rule = ruleIt.next();
					if ("color".equals(rule.name)) { //$NON-NLS-1$
						if (!(rule.value.equalsIgnoreCase("black") || rule.value.equals("#010101"))) { //$NON-NLS-1$//$NON-NLS-2$
							continue;
						}
					} else if ("font-weight".equals(rule.name)) { //$NON-NLS-1$
						if (rule.value.equalsIgnoreCase("bold") || rule.value.equalsIgnoreCase("bolder")) { //$NON-NLS-1$ //$NON-NLS-2$
							continue;
						}
					} else if ("font-style".equals(rule.name)) { //$NON-NLS-1$
						if (rule.value.equalsIgnoreCase("bold") || rule.value.equalsIgnoreCase("italic")) { //$NON-NLS-1$ //$NON-NLS-2$
							continue;
						}
					}
					ruleIt.remove();
				}
			}
			if ("font".equalsIgnoreCase(element.nodeName())) { //$NON-NLS-1$
				String color = element.attr("color"); //$NON-NLS-1$
				if (color != null && color.trim().length() > 0) {
					if (rules == null) {
						rules = new ArrayList<CssRule>(1);
					}
					rules.add(new CssRule("color", color.trim(), 0, 0, 0, 0)); //$NON-NLS-1$
				}
			}

			if (rules != null) {
				for (CssRule rule : rules) {
					newStyle += rule.name + ": " + rule.value + ";"; //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			if (newStyle.length() > 0) {
				if ("font".equalsIgnoreCase(element.nodeName())) { //$NON-NLS-1$
					Element spanElement = document.createElement("span"); //$NON-NLS-1$
					for (Node child : new ArrayList<Node>(element.childNodes())) {
						child.remove();
						spanElement.appendChild(child);
					}
					element.before(spanElement);
					element.remove();
					element = spanElement;
				}

				element.attr("style", newStyle); //$NON-NLS-1$
			} else {
				element.removeAttr("style"); //$NON-NLS-1$

				if ("span".equalsIgnoreCase(element.nodeName()) || "font".equalsIgnoreCase(element.nodeName())) { //$NON-NLS-1$ //$NON-NLS-2$
					removeElementPreserveChildren(element);
				}
			}
		}
	}

	private void removeElementPreserveChildren(Element element) {
		final Element parent = element.parent();
		for (Node child : new ArrayList<Node>(element.childNodes())) {
			child.remove();
			element.before(child);
		}
		element.remove();

		if (parent != null) {
			normalizeTextNodes(parent);
		}
	}
}
