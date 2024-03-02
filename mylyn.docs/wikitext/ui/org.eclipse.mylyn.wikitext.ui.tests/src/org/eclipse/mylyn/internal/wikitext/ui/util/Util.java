/*******************************************************************************
 * Copyright (c) 2009, 2024 David Green and others.
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

package org.eclipse.mylyn.internal.wikitext.ui.util;

import org.eclipse.mylyn.wikitext.parser.markup.ConfigurationBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguageConfiguration;
import org.eclipse.mylyn.wikitext.parser.markup.block.BugzillaGeneratedCommentBlock;
import org.eclipse.mylyn.wikitext.parser.markup.block.BugzillaQuoteBlock;

/**
 * @author David Green
 */
@SuppressWarnings("restriction")
public class Util {
	private static final String TASKTOP_TASKZILLA = "taskzilla"; //$NON-NLS-1$

	private static final String TASKTOP_ALM = "com.tasktop.alm.tasks"; //$NON-NLS-1$

	private static final String BUGZILLA = "bugzilla"; //$NON-NLS-1$

	public static MarkupLanguageConfiguration create(String repositoryKind) {
		ConfigurationBuilder builder = ConfigurationBuilder.create().repositorySettings();

		if (isBugzillaOrDerivative(repositoryKind)) {
			builder.block(new BugzillaQuoteBlock()).block(new BugzillaGeneratedCommentBlock());
		}
		return builder.configuration();
	}

	private static boolean isBugzillaOrDerivative(String repositoryKind) {
		return BUGZILLA.equals(repositoryKind) || TASKTOP_ALM.equals(repositoryKind)
				|| TASKTOP_TASKZILLA.equals(repositoryKind);
	}
}
