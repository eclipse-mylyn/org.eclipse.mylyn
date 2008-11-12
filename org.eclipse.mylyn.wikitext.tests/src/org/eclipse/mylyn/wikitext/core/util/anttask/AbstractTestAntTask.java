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

package org.eclipse.mylyn.wikitext.core.util.anttask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

public class AbstractTestAntTask extends TestCase {

	protected File tempFolder;

	protected String languageName = computeLanguageName();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tempFolder = File.createTempFile(getClass().getSimpleName(), ".tmp");
		tempFolder.delete();
		tempFolder.mkdirs();
	}

	private String computeLanguageName() {
		if (ResourcesPlugin.getPlugin() == null) {
			return TextileLanguage.class.getName();
		}
		return "Textile";
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
			System.out.println(String.format("%s: %s", prefix + file.getName(), file.isFile() ? "File" : "Folder"));
			if (file.isDirectory()) {
				listFiles(prefix + file.getName() + "/", file);
			}
		}
	}
}
