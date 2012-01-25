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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

/**
 * @author David Green
 */
class WhitespaceCleanupProcessor extends DocumentProcessor {

	@Override
	public void process(Document document) {
		Element body = document.body();

		Set<Node> affectedParents = new HashSet<Node>();

		// for every element containing text with leading or trailing whitespace, move the whitespace out of the element.
		for (Element element : body.getAllElements()) {
			if (!Html.isWhitespacePreserve(element)) {
				List<Node> children = element.childNodes();
				if (!children.isEmpty()) {
					Node firstChild = children.get(0);
					if (firstChild instanceof TextNode) {
						TextNode textNode = (TextNode) firstChild;
						String text = textNode.getWholeText();
						int nonWhitespaceIndex = firstIndexOfNonWhitespace(text);
						if (nonWhitespaceIndex > 0) {
							affectedParents.add(textNode.parent());

							// split
							textNode.splitText(nonWhitespaceIndex);
							// move outside
							textNode.remove();
							computeBeforeTarget(element).before(textNode);

							affectedParents.add(textNode.parent());
						} else if (nonWhitespaceIndex == -1) {
							// move outside
							textNode.remove();
							computeAfterTarget(element).after(textNode);

							affectedParents.add(textNode.parent());
						}
					}
					children = element.childNodes();
					if (!children.isEmpty()) {

						Node lastChild = children.get(children.size() - 1);
						if (lastChild instanceof TextNode) {

							TextNode textNode = (TextNode) lastChild;
							String text = textNode.getWholeText();
							int lastNonWhitespaceIndex = lastIndexOfNonWhitespace(text);
							if (lastNonWhitespaceIndex < 0) {
								// move outside
								textNode.remove();
								computeAfterTarget(element).after(textNode);

								affectedParents.add(textNode.parent());
							} else if (lastNonWhitespaceIndex < (text.length() - 1)) {
								affectedParents.add(textNode.parent());

								// split
								textNode.splitText(lastNonWhitespaceIndex + 1);
								// move outside
								textNode = (TextNode) textNode.nextSibling();
								textNode.remove();
								computeAfterTarget(element).after(textNode);

								affectedParents.add(textNode.parent());
							}
						}
					}
				}
				if (!affectedParents.isEmpty()) {
					for (Node parent : affectedParents) {
						if (parent instanceof Element) {
							normalizeTextNodes((Element) parent);
						}
					}
					affectedParents.clear();
				}
			}
		}
	}

	private Element computeAfterTarget(Element element) {
		if (element.parent() != null && !element.nodeName().equalsIgnoreCase("html")) { //$NON-NLS-1$
			List<Node> elementParentChildNodes = element.parent().childNodes();
			if (elementParentChildNodes.size() == 1
					|| elementParentChildNodes.get(elementParentChildNodes.size() - 1) == element) {
				return computeAfterTarget(element.parent());
			}
		}
		return element;
	}

	private Element computeBeforeTarget(Element element) {
		if (element.parent() != null && !element.parent().nodeName().equalsIgnoreCase("html")) { //$NON-NLS-1$
			List<Node> elementParentChildNodes = element.parent().childNodes();
			if (elementParentChildNodes.size() == 1 || elementParentChildNodes.get(0) == element) {
				return computeBeforeTarget(element.parent());
			}
		}
		return element;
	}

	private static int lastIndexOfNonWhitespace(String text) {
		int i = text.length() - 1;
		while (i > -1) {
			if (!Character.isWhitespace(text.charAt(i))) {
				return i;
			}
			--i;
		}
		return i;
	}

	private static int firstIndexOfNonWhitespace(String text) {
		int i = 0;
		while (i < text.length()) {
			if (!Character.isWhitespace(text.charAt(i))) {
				return i;
			}
			++i;
		}
		return -1;
	}

}
