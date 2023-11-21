/*******************************************************************************
 * Copyright (c) 2014, 2017 Torkild U. Resheim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.docs.epub.tests.core;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.mylyn.docs.epub.core.OPSPublication;
import org.eclipse.mylyn.docs.epub.internal.EclipseTocImporter;
import org.eclipse.mylyn.docs.epub.tests.api.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 * The purpose of this type is to test that the Eclipse Table of Contents importer works as it should.
 */
public class TestEclipseTocImporter extends AbstractTest {

	private OPSPublication oebps;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		oebps = new OPSPublication(logger);
	}

	@Test
	public void testEclipseTocImporter()
			throws DOMException, ParserConfigurationException, SAXException, IOException, URISyntaxException {
		File rootFile = new File("testdata/import/eclipse-toc/root.xml"); //$NON-NLS-1$
		EclipseTocImporter.importFile(oebps, rootFile);
		// a.html, b.html and c.html
		assertEquals(3, oebps.getPackage().getManifest().getItems().size());
		assertEquals("a.html", oebps.getPackage().getManifest().getItems().get(0).getHref()); //$NON-NLS-1$
		assertEquals("b.html", oebps.getPackage().getManifest().getItems().get(1).getHref()); //$NON-NLS-1$
		assertEquals("c.html", oebps.getPackage().getManifest().getItems().get(2).getHref()); //$NON-NLS-1$
	}
}
