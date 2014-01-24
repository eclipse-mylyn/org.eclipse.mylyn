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

package org.eclipse.mylyn.wikitext.core.util;

import static org.junit.Assert.assertNotNull;

import org.eclipse.mylyn.wikitext.core.osgi.OsgiServiceLocator;
import org.junit.Test;

public class ServiceLocatorIntegrationTest {

	@Test
	public void expectedMarkupLanguagesPresent() {
		ServiceLocator serviceLocator = OsgiServiceLocator.getApplicableInstance();
		assertNotNull(serviceLocator.getMarkupLanguage("HTML"));
		assertNotNull(serviceLocator.getMarkupLanguage("Textile"));
		assertNotNull(serviceLocator.getMarkupLanguage("MediaWiki"));
	}
}
