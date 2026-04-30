/*******************************************************************************
 * Copyright (c) 2015, 2024 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Max Rydahl Andersen- initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.editor.assist.Templates;
import org.eclipse.mylyn.wikitext.asciidoc.AsciiDocLanguage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Basic test for templates being present and resolving correctly.
 *
 * @author Max Rydahl Andersen
 */
@SuppressWarnings({ "nls", "restriction" })
public class AsciiDocTemplateResolverTest {

	private Templates templates;

	@BeforeEach
	void setUp() throws Exception {
		templates = WikiTextUiPlugin.getDefault().getTemplates().get(new AsciiDocLanguage().getName());
	}

	@Test
	public void hasTemplates() {
		assertNotEquals(0, templates.getTemplate().size(), "Should have non-zero templates");
	}

	@Test
	public void hasNoUnresolvedNLSStrings() {

		for (Template template : templates.getTemplate()) {
			assertFalse(template.getDescription().startsWith("%"),
					template.getName() + " with " + template.getDescription() + " has unresolved NLS string");
		}

	}
}
