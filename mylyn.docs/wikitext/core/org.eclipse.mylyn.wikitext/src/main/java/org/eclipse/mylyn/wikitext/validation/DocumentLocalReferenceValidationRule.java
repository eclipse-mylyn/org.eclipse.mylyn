/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.IdGenerator;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.Locator;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem.Severity;

/**
 * A validation rule that verifies that internal document links resolve to a document id.
 *
 * @author David Green
 * @since 3.0
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
			public void beginSpan(SpanType type, Attributes attributes) {
				super.beginSpan(type, attributes);
				if (type == SpanType.LINK) {
					if (attributes instanceof LinkAttributes) {
						LinkAttributes linkAttributes = (LinkAttributes) attributes;
						processLink(getLocator(), linkAttributes.getHref());
					}
				}
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
					references = new ArrayList<>();
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
						problems = new ArrayList<>();
					}
					problems.add(new ValidationProblem(Severity.ERROR,
							MessageFormat.format(Messages.getString("DocumentLocalReferenceValidationRule.0"), //$NON-NLS-1$
									reference.name),
							reference.offset, reference.length));
				}
			}
		}
		if (problems == null) {
			return Collections.emptyList();
		}
		return problems;
	}

}
