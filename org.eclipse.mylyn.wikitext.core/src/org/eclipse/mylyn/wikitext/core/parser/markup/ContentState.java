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
package org.eclipse.mylyn.wikitext.core.parser.markup;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.mylyn.wikitext.core.parser.IdGenerator;
import org.eclipse.mylyn.wikitext.core.parser.Locator;

/**
 * State related to parsing content, propagated to {@link Block blocks} and other {@link Processor processors} during
 * the parse phase.
 * 
 * @author David Green
 * @since 1.0
 */
public class ContentState implements Locator {
	private final Map<String, String> footnoteIdToHtmlId = new HashMap<String, String>();

	private final Map<String, String> glossaryItems = new HashMap<String, String>();

	private String markupContent;

	private final IdGenerator idGenerator = new IdGenerator();

	private int lineNumber = -1;

	private int lineOffset = -1;

	private int lineCharacterOffset = 0;

	private int lineLength = 0;

	private int lineSegmentEndOffset;

	private int shift;

	public ContentState() {
	}

	public String getMarkupContent() {
		return markupContent;
	}

	protected void setMarkupContent(String markupContent) {
		this.markupContent = markupContent;
	}

	public String getFootnoteId(String footnote) {
		String id = footnoteIdToHtmlId.get(footnote);
		if (id == null) {
			id = "fn" + UUID.randomUUID().toString().replace("-", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			footnoteIdToHtmlId.put(footnote, id);
		}
		return id;
	}

	/**
	 * Add a glossary term (typically an acronym) with its definition. Has no effect if the term is already present in
	 * the glossary and the given definition is shorter or equal in length to the existing definition.
	 * 
	 * @param term
	 *            the term to add
	 * @param definition
	 *            the definition of the term.
	 */
	public void addGlossaryTerm(String term, String definition) {
		String previousDef = glossaryItems.put(term, definition);
		if (previousDef != null && previousDef.length() > definition.length()) {
			glossaryItems.put(term, previousDef);
		}
	}

	/**
	 * Get the glossary as a map of definition by acronym or term.
	 */
	public Map<String, String> getGlossaryTerms() {
		return glossaryItems;
	}

	public IdGenerator getIdGenerator() {
		return idGenerator;
	}

	/**
	 * Get the 1-based line number of the current line.
	 * 
	 * @return the line number or -1 if it is unknown.
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * the 1-based line number of the current line.
	 */
	protected void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * the 0-based character offset of the current line.
	 * 
	 * @return the offset or -1 if it is unknown.
	 */
	public int getLineOffset() {
		return lineOffset;
	}

	/**
	 * the 0-based character offset of the current line.
	 */
	protected void setLineOffset(int lineOffset) {
		this.lineOffset = lineOffset;
	}

	public int getLineDocumentOffset() {
		return lineOffset;
	}

	public int getLineCharacterOffset() {
		return lineCharacterOffset;
	}

	public void setLineCharacterOffset(int lineCharacterOffset) {
		this.lineCharacterOffset = lineCharacterOffset;
	}

	public int getDocumentOffset() {
		return lineOffset + lineCharacterOffset;
	}

	public int getLineLength() {
		return lineLength;
	}

	public void setLineLength(int lineLength) {
		this.lineLength = lineLength;
	}

	public void setLineSegmentEndOffset(int lineSegmentEndOffset) {
		this.lineSegmentEndOffset = lineSegmentEndOffset;
	}

	public int getLineSegmentEndOffset() {
		return lineSegmentEndOffset;
	}

	void setShift(int shift) {
		this.shift = shift;
	}

	int getShift() {
		return shift;
	}
}
