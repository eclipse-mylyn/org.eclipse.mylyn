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

package org.eclipse.mylyn.internal.wikitext.tasks.ui.util;

import org.eclipse.mylyn.internal.wikitext.tasks.ui.util.bugzilla.BugzillaGeneratedCommentBlock;
import org.eclipse.mylyn.internal.wikitext.tasks.ui.util.bugzilla.BugzillaQuoteBlock;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguageConfiguration;
import org.eclipse.mylyn.wikitext.core.parser.markup.block.JavaStackTraceBlock;
import org.eclipse.mylyn.wikitext.core.parser.markup.block.EclipseErrorDetailsBlock;

/**
 * 
 * @author David Green
 */
public class Util {
	private static final String BUGZILLA = "bugzilla"; //$NON-NLS-1$

	public static MarkupLanguageConfiguration create(String repositoryKind) {
		MarkupLanguageConfiguration configuration = new MarkupLanguageConfiguration();
		configuration.setEnableUnwrappedParagraphs(false);
		configuration.setEscapingHtmlAndXml(true);
		configuration.setNewlinesMustCauseLineBreak(true);
		configuration.setOptimizeForRepositoryUsage(true);
		if (isBugzillaOrDerivative(repositoryKind)) {
			configuration.getBlocks().add(new BugzillaQuoteBlock());
			configuration.getBlocks().add(new BugzillaGeneratedCommentBlock());
		}
		configuration.getBlocks().add(new EclipseErrorDetailsBlock());
		configuration.getBlocks().add(new JavaStackTraceBlock());
		return configuration;
	}

	private static boolean isBugzillaOrDerivative(String repositoryKind) {
		return BUGZILLA.equals(repositoryKind);
	}
}
