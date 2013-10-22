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

package org.eclipse.mylyn.internal.wikitext.core.osgi.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.internal.wikitext.core.osgi.WikiTextPlugin;
import org.eclipse.mylyn.wikitext.core.osgi.WikiText;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;
import org.eclipse.mylyn.wikitext.tests.EclipseRuntimeRequired;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

@EclipseRuntimeRequired
public class EclipseServiceLocatorTest {

	@Before
	public void before() {
		assertTrue("platform must be running for this test", Platform.isRunning());
		// verify that the OSGi plug-in has been initialized otherwise this test will fail
		assertNotNull(WikiTextPlugin.getDefault());
	}

	@Test
	public void instance() {
		ServiceLocator instance = ServiceLocator.getInstance();
		assertTrue(instance.getClass().getName(), instance instanceof EclipseServiceLocator);
	}

	@Test
	public void getAllMarkupLanguages() {
		Set<MarkupLanguage> allMarkupLanguages = ServiceLocator.getInstance().getAllMarkupLanguages();
		Set<String> names = FluentIterable.from(allMarkupLanguages).transform(new Function<MarkupLanguage, String>() {
			public String apply(MarkupLanguage language) {
				return language.getName();
			}
		}).toImmutableSet();
		assertEquals(WikiText.getMarkupLanguageNames(), names);
	}
}
