/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.wikitext.markdown.MarkdownLanguage;
import org.eclipse.mylyn.wikitext.markdown.internal.LinkDefinitionUsageTracker;
import org.eclipse.mylyn.wikitext.markdown.internal.LinkDefinitionUsageTracker.Position;
import org.eclipse.mylyn.wikitext.markdown.internal.MarkdownContentState;
import org.eclipse.mylyn.wikitext.parser.Locator;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.NoOpDocumentBuilder;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.validation.ValidationProblem.Severity;
import org.eclipse.mylyn.wikitext.validation.ValidationRule;

/**
 * {@link ValidationRule} that finds missing and unused link definitions.
 *
 * @author Stefan Seelmann
 */
public class LinkDefinitionValidationRule extends ValidationRule {

	@Override
	public ValidationProblem findProblem(String markup, int offset, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<ValidationProblem> findProblems(String markup, int offset, int length) {

		MarkupParser parser = new MarkupParser(new MarkdownLanguage());
		MarkdownContentStateDocumentBuilder builder = new MarkdownContentStateDocumentBuilder();
		parser.setBuilder(builder);
		parser.parse(markup);

		MarkdownContentState markdownContentState = builder.markdownContentState;
		LinkDefinitionUsageTracker linkDefinitionUsageTracker = markdownContentState.getLinkDefinitionUsageTracker();

		List<ValidationProblem> problems = new ArrayList<>();

		List<Position> missingLinkDefinitionPositions = linkDefinitionUsageTracker.getMissingLinkDefinitionPositions();
		for (Position position : missingLinkDefinitionPositions) {
			problems.add(new ValidationProblem(Severity.ERROR,
					MessageFormat.format(Messages.getString("LinkDefinitionValidationRule.missing"), //$NON-NLS-1$
							position.getId()),
					position.getOffset(), position.getLength()));
		}

		List<Position> unusedLinkDefinitionPositions = linkDefinitionUsageTracker.getUnusedLinkDefinitionPositions();
		for (Position position : unusedLinkDefinitionPositions) {
			problems.add(new ValidationProblem(Severity.WARNING,
					MessageFormat.format(Messages.getString("LinkDefinitionValidationRule.unused"), //$NON-NLS-1$
							position.getId()),
					position.getOffset(), position.getLength()));
		}

		return problems;
	}

	/**
	 * Document builder that keeps a reference to the set {@link MarkdownContentState}.
	 */
	private class MarkdownContentStateDocumentBuilder extends NoOpDocumentBuilder {

		MarkdownContentState markdownContentState;

		@Override
		public void setLocator(Locator locator) {
			if (locator != null) {
				markdownContentState = (MarkdownContentState) locator;
			}
			super.setLocator(locator);
		}
	}
}
