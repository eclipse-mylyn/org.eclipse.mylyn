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
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

/**
 * @author David Green
 */
public abstract class DocumentProcessor {
	public abstract void process(Document document);

	/**
	 * normalize text node children of the given parent element. Ensures that adjacent text nodes are combined into a
	 * single text node.
	 * 
	 * @param parentElement
	 *            the parent element whose children should be normalized
	 */
	protected static void normalizeTextNodes(Element parentElement) {
		List<Node> children = parentElement.childNodes();
		if (!children.isEmpty()) {
			children = new ArrayList<Node>(children);
			for (Node child : children) {
				if (child instanceof TextNode) {
					Node previousSibling = child.previousSibling();
					if (previousSibling instanceof TextNode) {
						TextNode childTextNode = (TextNode) child;
						TextNode previousSiblingTextNode = (TextNode) previousSibling;
						childTextNode.text(previousSiblingTextNode.text() + childTextNode.text());
						previousSibling.remove();
					}
				}
			}
		}
	}
}