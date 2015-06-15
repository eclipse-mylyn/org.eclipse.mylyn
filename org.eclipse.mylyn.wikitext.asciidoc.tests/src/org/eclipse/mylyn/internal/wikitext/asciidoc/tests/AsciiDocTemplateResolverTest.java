/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Max Rydahl Andersen- initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.editor.assist.Templates;
import org.eclipse.mylyn.wikitext.asciidoc.core.AsciiDocLanguage;
import org.junit.Before;
import org.junit.Test;

/**
 * Basic test for templates being present and resolving correctly.
 *
 * @author Max Rydahl Andersen
 */
@SuppressWarnings("restriction")
public class AsciiDocTemplateResolverTest {

	private Templates templates;

	@Before
	public void setUp() throws Exception {
		templates = WikiTextUiPlugin.getDefault().getTemplates().get(new AsciiDocLanguage().getName());
	}

	@Test
	public void hasTemplates() {
		assertTrue("Should have non-zero templates", templates.getTemplate().size() != 0);
	}

	@Test
	public void hasNoUnresolvedNLSStrings() {

		for (Template template : templates.getTemplate()) {
			assertFalse(template.getName() + " with " + template.getDescription() + " has unresolved NLS string",
					template.getDescription().startsWith("%"));
		}

	}
}
