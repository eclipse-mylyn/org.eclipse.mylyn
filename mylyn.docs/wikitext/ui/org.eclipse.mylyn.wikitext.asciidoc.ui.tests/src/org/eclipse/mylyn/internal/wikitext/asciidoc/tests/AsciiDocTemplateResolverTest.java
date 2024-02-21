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
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.editor.assist.Templates;
import org.eclipse.mylyn.wikitext.asciidoc.AsciiDocLanguage;
import org.junit.Before;
import org.junit.Test;

/**
 * Basic test for templates being present and resolving correctly.
 *
 * @author Max Rydahl Andersen
 */
@SuppressWarnings({ "nls", "restriction" })
public class AsciiDocTemplateResolverTest {

	private Templates templates;

	@Before
	public void setUp() throws Exception {
		templates = WikiTextUiPlugin.getDefault().getTemplates().get(new AsciiDocLanguage().getName());
	}

	@Test
	public void hasTemplates() {
		assertNotEquals("Should have non-zero templates", 0, templates.getTemplate().size());
	}

	@Test
	public void hasNoUnresolvedNLSStrings() {

		for (Template template : templates.getTemplate()) {
			assertFalse(template.getName() + " with " + template.getDescription() + " has unresolved NLS string",
					template.getDescription().startsWith("%"));
		}

	}
}
