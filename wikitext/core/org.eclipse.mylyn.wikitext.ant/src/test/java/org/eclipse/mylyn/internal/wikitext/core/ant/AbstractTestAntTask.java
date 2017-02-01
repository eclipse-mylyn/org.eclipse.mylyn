/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.ant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ResourceBundle;

import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

import junit.framework.TestCase;

public abstract class AbstractTestAntTask extends TestCase {

	protected File tempFolder;

	protected String languageName = computeLanguageName();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tempFolder = File.createTempFile(getClass().getSimpleName(), ".tmp");
		tempFolder.delete();
		tempFolder.mkdirs();
	}

	protected ResourceBundle loadTaskdefBundle() {
		return ResourceBundle.getBundle("org.eclipse.mylyn.wikitext.core.ant.tasks");
	}

	private String computeLanguageName() {
		return TextileLanguage.class.getName();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
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
		Reader reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(file)), "utf-8");
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
