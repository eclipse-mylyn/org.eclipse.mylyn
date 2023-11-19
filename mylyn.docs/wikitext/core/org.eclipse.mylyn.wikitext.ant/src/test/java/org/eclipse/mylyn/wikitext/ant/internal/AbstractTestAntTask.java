/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.ant.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractTestAntTask {

	protected File tempFolder;

	protected String languageName = computeLanguageName();

	@Before
	public void setUp() throws Exception {
		tempFolder = File.createTempFile(getClass().getSimpleName(), ".tmp");
		tempFolder.delete();
		tempFolder.mkdirs();
	}

	protected ResourceBundle loadTaskdefBundle() {
		return ResourceBundle.getBundle("org.eclipse.mylyn.wikitext.ant.tasks");
	}

	private String computeLanguageName() {
		return TextileLanguage.class.getName();
	}

	@After
	public void tearDown() throws Exception {
		delete(tempFolder);
	}

	protected void delete(File f) {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			if (files != null) {
				for (File child : files) {
					delete(child);
				}
			}
		}
		f.delete();
	}

	protected String getContent(File file) throws IOException {
		Reader reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(file)),
				StandardCharsets.UTF_8);
		try {
			StringWriter writer = new StringWriter();
			int i;
			while ((i = reader.read()) != -1) {
				writer.write(i);
			}
			return writer.toString();
		} finally {
			reader.close();
		}
	}

	protected void listFiles() {
		listFiles("", tempFolder);
	}

	private void listFiles(String prefix, File dir) {
		for (File file : dir.listFiles()) {

			if (file.isDirectory()) {
				listFiles(prefix + file.getName() + "/", file);
			}
		}
	}
}
