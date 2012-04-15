/*******************************************************************************
 * Copyright (c) 2011,2012 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.tests.ant;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.Assert;

import org.apache.tools.ant.BuildFileTest;

import com.adobe.epubcheck.api.EpubCheck;

/**
 * Tests for the <b>epub</b> ANT task.
 * 
 * @author Torkild U. Resheim
 */
@SuppressWarnings("nls")
public class TestAntTask extends BuildFileTest {

	static ClassLoader classLoader;

	private static final String SIMPLE_FILE_PATH = "test/ant/simple.epub";

	public TestAntTask(String s) {
		super(s);
		classLoader = getClass().getClassLoader();
	}

	private void assertEpub(String file) {
		File f = getFile(file);
		assertTrue("Missing publication " + file, f.exists());
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		EpubCheck checker = new EpubCheck(f, pw);
		//checker.validate();
		Assert.assertTrue(sw.getBuffer().toString().trim(), checker.validate());
	}

	private File getFile(String file) {
		return new File(getProjectDir().getAbsolutePath() + File.separator + file);
	}

	@Override
	public void setUp() {
		configureProject("ant-test.xml");
		project.setCoreLoader(this.getClass().getClassLoader());
	}

	/**
	 * Creates a simple book using the Ant task and tests it using the epub validator.
	 */
	public void testSimplePublication() {
		executeTarget("init");
		executeTarget("test.publication");
		assertEpub(SIMPLE_FILE_PATH);
	}

}
