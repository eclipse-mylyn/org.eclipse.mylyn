/*******************************************************************************
 * Copyright (c) 2011, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.internal.parser.html;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import com.google.common.base.Strings;

/**
 * @author David Green
 */
class RemoveEmptySpansProcessor extends DocumentProcessor {

	@Override
	public void process(Document document) {
		Element body = document.body();

		boolean modifiedOne = false;

		do {
			modifiedOne = false;

			// remove empty spans, and eliminate tags that only contain whitespace
			for (Element element : body.getAllElements()) {
				if (Html.isSpanElement(element)) {
					// remove span with no children, but preserve hyperlinks
					List<Node> childNodes = element.childNodes();
					if (childNodes.isEmpty() && !isHyperlinkWithTarget(element)) {
						element.remove();
						modifiedOne = true;
					} else // a span with a single text child that is only whitespace is removed (text is retained)
						if (childNodes.size() == 1) {
							Node node = childNodes.get(0);
							if (node instanceof TextNode textNode) {
								String text = textNode.text();
								if (text.trim().length() == 0) {
									textNode.remove();
									element.before(textNode);
									element.remove();
									modifiedOne = true;
								}

								normalizeTextNodes((Element) textNode.parent());
							}
						}
				}
				// a br within a span that is a first or last child is moved out
				Element parent = element.parent();
				if (element.tagName().equalsIgnoreCase("br") && Html.isSpanElement(parent)) { //$NON-NLS-1$
					List<Node> childNodes = parent.childNodes();
					if (childNodes.get(0) == element) {
						element.remove();
						parent.before(element);
						modifiedOne = true;
					} else if (childNodes.get(childNodes.size() - 1) == element) {
						element.remove();
						parent.after(element);
						modifiedOne = true;
					}
				}
			}
		} while (modifiedOne);

	}

	private boolean isHyperlinkWithTarget(Element element) {
		return element.tagName().equalsIgnoreCase("a") && !Strings.isNullOrEmpty(element.attr("href")); //$NON-NLS-1$//$NON-NLS-2$
	}

}
