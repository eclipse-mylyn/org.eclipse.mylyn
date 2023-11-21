/*******************************************************************************
 * Copyright (c) 2015 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.tests.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.mylyn.docs.epub.core.EPUBPublication;
import org.junit.Test;

/**
 * @author Torkild U. Resheim
 */
@SuppressWarnings("nls")
public class TestEPUBPublication extends AbstractTest {

	private class EPUB_OPF_Test extends EPUBPublication {
		public void testReadOPF(File rootFile) throws IOException {
			readOPF(rootFile);
		}
	}

	/**
	 * Test whether or not a basic EPUB 3 OPF can be read.
	 *
	 * @throws Exception
	 */
	@Test
	public final void testReadOPF() throws Exception {
		File rootFile = new File("testdata/OPF-Tests/EPUB3/content.opf");
		EPUB_OPF_Test epub = new EPUB_OPF_Test();
		epub.testReadOPF(rootFile);
	}

	/**
	 * Test method for <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=380729">bug 380729</a>: Allow reference
	 * elements to have "other." types
	 *
	 * @throws Exception
	 */
	@Test
	public final void test_addMeta() throws Exception {
		File rootFile = new File("testdata/OPF-Tests/EPUB3/basic.opf");
		EPUB_OPF_Test oebps = new EPUB_OPF_Test();
		oebps.testReadOPF(rootFile);

		// Validate that data already read from the OPF file is OK.
		List<Diagnostic> problems = oebps.validateMetadata();
		assertEquals(0, problems.size());

		// Add a EPUB 3 Meta item
		oebps.addMeta("id", "property", "refines", "scheme");
		assertEquals("id", oebps.getPackage().getMetadata().getMetas().get(0).getId());
		assertEquals("property", oebps.getPackage().getMetadata().getMetas().get(0).getProperty());
		assertEquals("refines", oebps.getPackage().getMetadata().getMetas().get(0).getRefines());
		assertEquals("scheme", oebps.getPackage().getMetadata().getMetas().get(0).getScheme());
		try {
			oebps.addMeta("id", null, "refines", "scheme");
			fail();
		} catch (IllegalArgumentException e) {
		}

		problems = oebps.validateMetadata();
		assertEquals(0, problems.size());
	}
}
