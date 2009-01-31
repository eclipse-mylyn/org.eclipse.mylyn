/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util.css.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.swt.custom.StyleRange;

/**
 * 
 * based on the XML editor example
 * 
 * @author David Green
 */
class CommentDamagerRepairer implements IPresentationDamager, IPresentationRepairer {

	private IDocument document;

	private final TextAttribute defaultTextAttribute;

	public CommentDamagerRepairer(TextAttribute defaultTextAttribute) {
		super();
		this.defaultTextAttribute = defaultTextAttribute;
	}

	public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event, boolean documentPartitioningChanged) {
		if (!documentPartitioningChanged) {
			try {
				IRegion lineRegion = document.getLineInformationOfOffset(event.getOffset());
				int start = Math.max(lineRegion.getOffset(), partition.getOffset());
				int end = event.getOffset();
				if (event.getText() == null) {
					end += event.getLength();
				} else {
					end += event.getText().length();
				}
				if (lineRegion.getOffset() <= end && end <= lineRegion.getOffset() + lineRegion.getLength()) {
					// same line
					end = lineRegion.getOffset() + lineRegion.getLength();
				} else {
					end = toLineEnd(end);
				}
				int partitionEnd = partition.getOffset() + partition.getLength();
				end = Math.min(partitionEnd, end);
				return new Region(start, end - start);
			} catch (BadLocationException e) {
				// ignore
			}
		}
		return partition;
	}

	/**
	 * return the offset of the end of the line, or if the offset includes a line terminator then the end of the next
	 * line.
	 */
	private int toLineEnd(int offset) throws BadLocationException {
		IRegion lineRegion = document.getLineInformationOfOffset(offset);
		int lineEndOffset = lineRegion.getOffset() + lineRegion.getLength();
		if (offset <= lineEndOffset) {
			return lineEndOffset;
		}

		int line = document.getLineOfOffset(offset);
		try {
			lineRegion = document.getLineInformation(line + 1);
			lineEndOffset = lineRegion.getOffset() + lineRegion.getLength();
			return lineEndOffset;
		} catch (BadLocationException x) {
			return document.getLength();
		}
	}

	public void setDocument(IDocument document) {
		this.document = document;
	}

	public void createPresentation(TextPresentation presentation, ITypedRegion damage) {
		presentation.addStyleRange(new StyleRange(damage.getOffset(), damage.getLength(),
				defaultTextAttribute.getForeground(), defaultTextAttribute.getBackground(),
				defaultTextAttribute.getStyle()));
	}

}
