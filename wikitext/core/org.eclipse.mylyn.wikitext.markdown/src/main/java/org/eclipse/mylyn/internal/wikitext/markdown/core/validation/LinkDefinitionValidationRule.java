/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.core.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.internal.wikitext.markdown.core.LinkDefinitionUsageTracker;
import org.eclipse.mylyn.internal.wikitext.markdown.core.LinkDefinitionUsageTracker.Position;
import org.eclipse.mylyn.internal.wikitext.markdown.core.MarkdownContentState;
import org.eclipse.mylyn.wikitext.core.parser.Locator;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.NoOpDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem;
import org.eclipse.mylyn.wikitext.core.validation.ValidationProblem.Severity;
import org.eclipse.mylyn.wikitext.core.validation.ValidationRule;
import org.eclipse.mylyn.wikitext.markdown.core.MarkdownLanguage;

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

		List<ValidationProblem> problems = new ArrayList<ValidationProblem>();

		List<Position> missingLinkDefinitionPositions = linkDefinitionUsageTracker.getMissingLinkDefinitionPositions();
		for (Position position : missingLinkDefinitionPositions) {
			problems.add(new ValidationProblem(Severity.ERROR, MessageFormat.format(
					Messages.getString("LinkDefinitionValidationRule.missing"), //$NON-NLS-1$
					position.getId()), position.getOffset(), position.getLength()));
		}

		List<Position> unusedLinkDefinitionPositions = linkDefinitionUsageTracker.getUnusedLinkDefinitionPositions();
		for (Position position : unusedLinkDefinitionPositions) {
			problems.add(new ValidationProblem(Severity.WARNING, MessageFormat.format(
					Messages.getString("LinkDefinitionValidationRule.unused"),//$NON-NLS-1$
					position.getId()), position.getOffset(), position.getLength()));
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
