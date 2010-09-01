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

package org.eclipse.mylyn.internal.wikitext.mediawiki.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.mediawiki.core.Template;

public class WikiTemplateResolverTest extends TestCase {

	private WikiTemplateResolver resolver;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		resolver = new WikiTemplateResolver();
		resolver.setWikiBaseUrl("http://wiki.eclipse.org");
	}

	public void testResolveTemplate() {
		Template template = resolver.resolveTemplate("bug");
		assertNotNull(template);
		assertTrue(template.getName().equalsIgnoreCase("bug"));
		assertTrue(template.getTemplateMarkup().trim().length() > 0);
	}
}
