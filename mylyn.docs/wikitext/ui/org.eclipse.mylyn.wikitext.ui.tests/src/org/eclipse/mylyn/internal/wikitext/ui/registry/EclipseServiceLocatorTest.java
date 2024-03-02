/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.tests.EclipseRuntimeRequired;
import org.eclipse.mylyn.wikitext.ui.WikiText;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;
import org.junit.Before;
import org.junit.Test;

@EclipseRuntimeRequired
@SuppressWarnings({ "nls", "restriction" })
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
		Set<String> names = Set
				.copyOf(allMarkupLanguages.stream().map(MarkupLanguage::getName).collect(Collectors.toSet()));
		assertEquals(WikiText.getMarkupLanguageNames(), names);
	}
}
