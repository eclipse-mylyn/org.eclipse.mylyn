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

package org.eclipse.mylyn.wikitext.core.util;

import static org.junit.Assert.assertNotNull;

import org.eclipse.mylyn.wikitext.core.osgi.OsgiServiceLocator;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;
import org.junit.Test;

@SuppressWarnings("nls")
public class ServiceLocatorIntegrationTest {

	@Test
	public void expectedMarkupLanguagesPresent() {
		ServiceLocator serviceLocator = OsgiServiceLocator.getApplicableInstance();
		assertNotNull(serviceLocator.getMarkupLanguage("HTML"));
		assertNotNull(serviceLocator.getMarkupLanguage("Textile"));
		assertNotNull(serviceLocator.getMarkupLanguage("MediaWiki"));
	}
}
