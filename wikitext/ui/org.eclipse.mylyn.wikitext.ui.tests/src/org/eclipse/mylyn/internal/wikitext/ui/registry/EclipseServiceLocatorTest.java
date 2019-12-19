/*******************************************************************************
 * Copyright (c) 2007, 2014 David Green and others.
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

package org.eclipse.mylyn.internal.wikitext.ui.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.tests.EclipseRuntimeRequired;
import org.eclipse.mylyn.wikitext.ui.WikiText;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

@EclipseRuntimeRequired
public class EclipseServiceLocatorTest {

	@Before
	public void before() {
		assertTrue("platform must be running for this test", Platform.isRunning());
		// verify that the OSGi plug-in has been initialized otherwise this test will fail
		assertNotNull(WikiTextUiPlugin.getDefault());
	}

	@Test
	public void instance() {
		ServiceLocator instance = ServiceLocator.getInstance();
		assertTrue(instance.getClass().getName(), instance instanceof EclipseServiceLocator);
	}

	@Test
	public void getAllMarkupLanguages() {
		Set<MarkupLanguage> allMarkupLanguages = ServiceLocator.getInstance().getAllMarkupLanguages();
		Set<String> names = ImmutableSet
				.copyOf(FluentIterable.from(allMarkupLanguages).transform(language -> language.getName()));
		assertEquals(WikiText.getMarkupLanguageNames(), names);
	}
}
