/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor.assist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem.Visitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * A content assist implementation that provides proposals for completing document-internal anchor names.
 * 
 * @author David Green
 */
public class AnchorCompletionProcessor implements IContentAssistProcessor {

	private static final CompletionProposalComparator PROPOSAL_COMPARATOR = new CompletionProposalComparator();

	private static class CompletionProposal implements ICompletionProposal, ICompletionProposalExtension4 {

		private final String proposalText;

		private final int offset;

		private final int replacementLength;

		private int relevance;

		public CompletionProposal(int offset, String proposalText, int replacementLength) {
			this.offset = offset;
			this.proposalText = proposalText;
			this.replacementLength = replacementLength;
		}

		public void apply(IDocument document) {
			try {
				document.replace(offset, replacementLength, proposalText);
			} catch (BadLocationException x) {
				// ignore
			}
		}

		public String getAdditionalProposalInfo() {
			return null;
		}

		public IContextInformation getContextInformation() {
			return null;
		}

		public String getDisplayString() {
			return proposalText;
		}

		public Image getImage() {
			return null;
		}

		public Point getSelection(IDocument document) {
			return new Point(offset + proposalText.length(), 0);
		}

		public boolean isAutoInsertable() {
			return true;
		}

		public int getRelevance() {
			return relevance;
		}

		public void setRelevance(int relevance) {
			this.relevance = relevance;
		}
	}

	private static class CompletionProposalComparator implements Comparator<CompletionProposal> {
		public int compare(CompletionProposal o1, CompletionProposal o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o1.getRelevance() > o2.getRelevance()) {
				return -1;
			}
			if (o2.getRelevance() > o1.getRelevance()) {
				return 1;
			}
			return o1.proposalText.compareTo(o2.proposalText);
		}
	}

	private OutlineItem outline;

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		if (outline == null) {
			return null;
		}

		ITextSelection selection = (ITextSelection) viewer.getSelectionProvider().getSelection();

		// adjust offset to end of normalized selection
		if (selection.getOffset() == offset) {
			offset = selection.getOffset() + selection.getLength();
		}

		final String prefix = extractPrefix(viewer, offset);
		if (prefix == null) {
			return null;
		}
		final List<CompletionProposal> suggestions = new ArrayList<CompletionProposal>(20);
		final int prefixOffset = offset - prefix.length();
		outline.accept(new Visitor() {
			public boolean visit(OutlineItem item) {
				if (item != outline) {
					String id = item.getId();
					if (id != null && id.length() > 0) {
						suggestions.add(createProposal(prefix, prefixOffset, id));
					}
				}
				return true;
			}

		});
		if (suggestions.isEmpty()) {
			return null;
		}
		Collections.sort(suggestions, PROPOSAL_COMPARATOR);

		return suggestions.toArray(new ICompletionProposal[suggestions.size()]);
	}

	private CompletionProposal createProposal(String prefix, int offset, String id) {
		CompletionProposal proposal = new CompletionProposal(offset, id, prefix.length());
		if (id.startsWith(prefix)) {
			proposal.setRelevance(90);
		} else {
			proposal.setRelevance(0);
		}
		return proposal;
	}

	/**
	 * get a prefix, but only if it is preceded by a '#' character
	 * 
	 * @return the prefix (which may be the empty string) that is prefixed by '#', otherwise null
	 */
	protected String extractPrefix(ITextViewer viewer, int offset) {
		int i = offset;
		IDocument document = viewer.getDocument();
		if (i > document.getLength()) {
			return null;
		}

		try {
			while (i > 0) {
				char ch = document.getChar(i - 1);
				if (!Character.isJavaIdentifierPart(ch)) {
					break;
				}
				i--;
			}
			if (i == 0 || document.getChar(i - 1) != '#') {
				return null;
			}
			return document.get(i, offset - i);
		} catch (BadLocationException e) {
			return null;
		}
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public String getErrorMessage() {
		return null;
	}

	public OutlineItem getOutline() {
		return outline;
	}

	public void setOutline(OutlineItem outline) {
		this.outline = outline;
	}

}
