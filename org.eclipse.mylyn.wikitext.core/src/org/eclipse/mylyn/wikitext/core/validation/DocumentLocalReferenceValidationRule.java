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

package org.eclipse.mylyn.wikitext.core.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.IdGenerator;
import org.eclipse.mylyn.wikitext.core.parser.Locator;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem.Severity;

/**
 * A validation rule that verifies that internal document links resolve to a document id.
 * 
 * @author David Green
 */
public abstract class DocumentLocalReferenceValidationRule extends ValidationRule {

	private final class LocalReference {
		public LocalReference(String name, int offset, int length) {
			super();
			this.name = name;
			this.offset = offset;
			this.length = length;
			if (length < 0) {
				throw new IllegalArgumentException();
			}
		}

		String name;

		int offset, length;
	}

	private final class OutlineParserExtension extends OutlineParser {
		public IdGenerator idGenerator;

		private final int offset;

		private final int length;

		private List<LocalReference> references;

		private OutlineParserExtension(int offset, int length) {
			this.offset = offset;
			this.length = length;
		}

		private final class OutlineBuilderExtension extends OutlineBuilder {
			private OutlineBuilderExtension(OutlineItem root, int labelMaxLength) {
				super(root, labelMaxLength);
				OutlineParserExtension.this.idGenerator = idGenerator;
			}

			@Override
			public void link(Attributes attributes, String hrefOrHashName, String text) {
				super.link(attributes, hrefOrHashName, text);
				processLink(getLocator(), hrefOrHashName);
			}

			@Override
			public void imageLink(Attributes linkAttributes, Attributes ImageAttributes, String href, String imageUrl) {
				super.imageLink(linkAttributes, ImageAttributes, href, imageUrl);
				processLink(getLocator(), href);
			}
		}

		@Override
		protected boolean isBlocksOnly() {
			return false;
		}

		@Override
		public DocumentBuilder createOutlineUpdater(OutlineItem rootItem) {
			return new OutlineBuilderExtension(rootItem, getLabelMaxLength());
		}

		public void processLink(Locator locator, String href) {
			if ((locator.getDocumentOffset() < offset) || (locator.getDocumentOffset() >= (offset + length))) {
				return;
			}
			if (href.length() > 0 && href.charAt(0) == '#') {
				if (references == null) {
					references = new ArrayList<LocalReference>();
				}
				String name = href.substring(1);
				int length = locator.getLineSegmentEndOffset() - locator.getLineCharacterOffset();
				references.add(new LocalReference(name, locator.getDocumentOffset(), length));
			}
		}
	}

	protected abstract MarkupLanguage createMarkupLanguage();

	@Override
	public ValidationProblem findProblem(String markup, int offset, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<ValidationProblem> findProblems(String markup, int offset, int length) {
		MarkupLanguage markupLanguage = createMarkupLanguage();

		OutlineParserExtension outlineParser = new OutlineParserExtension(offset, length);
		outlineParser.setMarkupLanguage(markupLanguage);
		outlineParser.parse(markup);

		List<ValidationProblem> problems = null;
		if (outlineParser.references != null) {
			Set<String> anchorNames = outlineParser.idGenerator.getAnchorNames();
			for (LocalReference reference : outlineParser.references) {
				if (!anchorNames.contains(reference.name)) {
					if (problems == null) {
						problems = new ArrayList<ValidationProblem>();
					}
					problems.add(new ValidationProblem(Severity.ERROR, MessageFormat.format(
							Messages.getString("DocumentLocalReferenceValidationRule.0"), reference.name), reference.offset, //$NON-NLS-1$
							reference.length));
				}
			}
		}
		if (problems == null) {
			return Collections.emptyList();
		}
		return problems;
	}

}
