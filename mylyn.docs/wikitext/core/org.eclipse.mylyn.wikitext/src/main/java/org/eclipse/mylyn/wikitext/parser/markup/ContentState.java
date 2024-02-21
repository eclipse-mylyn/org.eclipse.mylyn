/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
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
package org.eclipse.mylyn.wikitext.parser.markup;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.wikitext.parser.IdGenerator;
import org.eclipse.mylyn.wikitext.parser.Locator;

/**
 * State related to parsing content, propagated to {@link Block blocks} and other {@link Processor processors} during the parse phase.
 *
 * @author David Green
 * @since 3.0
 */
public class ContentState implements Locator {
	private final Map<String, String> footnoteIdToHtmlId = new HashMap<>();

	private final Map<String, String> glossaryItems = new HashMap<>();

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
		// Create a stable id.  This has a greater chance of collisions, however
		// it's an acceptable risk to gain stable ids.  Unfortunately there's no way to
		// know if the id is already in use without doing a two-pass parse, which we don't
		// want to do.  Instead, we choose a prefix of '___' since the '_' is not used by
		// the default id generator and it's unlikely to be found in a document.
		return footnoteIdToHtmlId.computeIfAbsent(footnote, s -> "___fn" + s); //$NON-NLS-1$
	}

	/**
	 * Add a glossary term (typically an acronym) with its definition. Has no effect if the term is already present in the glossary and the
	 * given definition is shorter or equal in length to the existing definition.
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
	@Override
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

	@Override
	public int getLineDocumentOffset() {
		return lineOffset;
	}

	@Override
	public int getLineCharacterOffset() {
		return lineCharacterOffset;
	}

	public void setLineCharacterOffset(int lineCharacterOffset) {
		this.lineCharacterOffset = lineCharacterOffset;
	}

	@Override
	public int getDocumentOffset() {
		return lineOffset + lineCharacterOffset;
	}

	@Override
	public int getLineLength() {
		return lineLength;
	}

	public void setLineLength(int lineLength) {
		this.lineLength = lineLength;
	}

	public void setLineSegmentEndOffset(int lineSegmentEndOffset) {
		this.lineSegmentEndOffset = lineSegmentEndOffset;
	}

	@Override
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
