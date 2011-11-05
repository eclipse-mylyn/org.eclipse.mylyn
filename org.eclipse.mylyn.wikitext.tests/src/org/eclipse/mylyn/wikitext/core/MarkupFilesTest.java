/*******************************************************************************
 * Copyright (c) 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.logging.Logger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;
import org.eclipse.mylyn.wikitext.tests.NoDiscovery;
import org.eclipse.mylyn.wikitext.tests.TestUtil;
import org.junit.Assert;
import org.osgi.framework.Bundle;

/**
 * a test suite that discovers tests on the filesystem. See /org.eclipse.mylyn.wikitext.tests/markupLanguageTests
 * 
 * @author David Green
 */
public class MarkupFilesTest extends TestSuite {

	private static final String MARKUP_LANGUAGE_TESTS = "markupLanguageTests";

	@NoDiscovery
	private static class MarkupLanguageTestSuite extends TestSuite {

		public MarkupLanguageTestSuite(String name, File languageDir) {
			MarkupLanguage markupLanguage = ServiceLocator.getInstance().getMarkupLanguage(name);
			setName(markupLanguage.getName());

			discoverLanguageTests(markupLanguage, languageDir);
		}

		public MarkupLanguageTestSuite(String name, Bundle bundle) {
			MarkupLanguage markupLanguage = ServiceLocator.getInstance().getMarkupLanguage(name);
			setName(markupLanguage.getName());

			discoverLanguageTests(markupLanguage, bundle, "/" + MARKUP_LANGUAGE_TESTS + "/" + name + "/");
		}

		private void discoverLanguageTests(MarkupLanguage markupLanguage, Bundle bundle, String bundlePath) {
			final String markupFileExtension = '.' + markupLanguage.getName().toLowerCase();

			Enumeration<URL> entries = bundle.findEntries(bundlePath, null, false);
			while (entries.hasMoreElements()) {
				URL testElement = entries.nextElement();

				String name;
				try {
					name = testElement.toURI().toString();
				} catch (URISyntaxException e) {
					throw new IllegalStateException(e);
				}
				if (name.endsWith(markupFileExtension)) {
					name = name.substring(name.lastIndexOf('/') + 1, name.length());
					name = name.substring(0, name.lastIndexOf('.'));

					URL expectedOutcome = bundle.getEntry(bundlePath + "/" + name + ".txt");

					addTest(new PlatformMarkupFileCase(markupLanguage, name, testElement, expectedOutcome));
				} else if (!name.endsWith(".txt")) {
					Logger.getLogger(MarkupFilesTest.class.getName()).severe("Unexpected file: " + name);
				}
			}
		}

		private void discoverLanguageTests(MarkupLanguage markupLanguage, File languageDir) {

			File[] testFiles = languageDir.listFiles();

			if (testFiles != null) {
				final String markupFileExtension = '.' + markupLanguage.getName().toLowerCase();

				for (File file : testFiles) {
					if (file.getName().toLowerCase().endsWith(markupFileExtension)) {
						addTest(new MarkupFileCase(markupLanguage, file));
					} else if (!file.getName().toLowerCase().endsWith(".txt")) {
						Logger.getLogger(MarkupFilesTest.class.getName()).severe("Unexpected file: " + file);
					}
				}
			}
		}
	}

	@NoDiscovery
	private static abstract class AbstractMarkupFileCase extends TestCase {

		protected final MarkupLanguage markupLanguage;

		public AbstractMarkupFileCase(MarkupLanguage markupLanguage) {
			this.markupLanguage = markupLanguage.clone();
		}

		@Override
		protected void runTest() throws Throwable {

			String markupContent = readMarkupContent();
			String expectedContent = readExpectedContent();

			MarkupParser parser = new MarkupParser(markupLanguage);

			String html = parser.parseToHtml(markupContent);

			TestUtil.println("**************\nTesting - " + getName());
			TestUtil.println("Generated:\n" + html);
			TestUtil.println("Expecting:\n" + expectedContent);

			Assert.assertTrue(html.contains(expectedContent));
		}

		protected abstract String readExpectedContent() throws IOException;

		protected abstract String readMarkupContent() throws IOException;

		protected String readFully(InputStream in) throws IOException {
			Reader reader = new InputStreamReader(in);
			try {
				int i;
				StringWriter writer = new StringWriter();
				while ((i = reader.read()) != -1) {
					writer.write(i);
				}
				return writer.toString();
			} finally {
				reader.close();
			}
		}
	}

	@NoDiscovery
	private static class MarkupFileCase extends AbstractMarkupFileCase {

		private final File file;

		private final File resultsFile;

		public MarkupFileCase(MarkupLanguage markupLanguage, File file) {
			super(markupLanguage);
			this.file = file;
			setName(file.getName());

			String resultsFileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + ".txt";
			resultsFile = new File(file.getParentFile(), resultsFileName);

			assertTrue("Expected to find file: " + resultsFile, file.exists() && file.isFile());
		}

		private String readFully(File file) throws IOException {
			FileInputStream in = new FileInputStream(file);
			try {
				return readFully(in);
			} finally {
				in.close();
			}
		}

		@Override
		protected String readExpectedContent() throws IOException {
			return readFully(resultsFile);
		}

		@Override
		protected String readMarkupContent() throws IOException {
			return readFully(file);
		}
	}

	@NoDiscovery
	private static class PlatformMarkupFileCase extends AbstractMarkupFileCase {

		private final URL testElement;

		private final URL expectedOutcome;

		public PlatformMarkupFileCase(MarkupLanguage markupLanguage, String name, URL testElement, URL expectedOutcome) {
			super(markupLanguage);
			this.testElement = testElement;
			this.expectedOutcome = expectedOutcome;
			setName(name);
		}

		@Override
		protected String readExpectedContent() throws IOException {
			InputStream in = expectedOutcome.openStream();
			try {
				return readFully(in);
			} finally {
				in.close();
			}
		}

		@Override
		protected String readMarkupContent() throws IOException {
			InputStream in = testElement.openStream();
			try {
				return readFully(in);
			} finally {
				in.close();
			}
		}

	}

	public MarkupFilesTest() {
		setName(MarkupFilesTest.class.getSimpleName());
		discoverTests();
	}

	public static Test suite() {
		return new MarkupFilesTest();
	}

	private void discoverTests() {
		Logger log = Logger.getLogger(MarkupFilesTest.class.getName());

		if (Platform.isRunning()) {
			Bundle bundle = Platform.getBundle("org.eclipse.mylyn.wikitext.tests");
			Enumeration<URL> entries = bundle.findEntries("/" + MARKUP_LANGUAGE_TESTS, null, false);
			while (entries.hasMoreElements()) {
				URL entry = entries.nextElement();

				String path = entry.getPath();
				if (path.endsWith("/")) {
					path = path.substring(0, path.length() - 1);
				}
				String lastName = path.substring(path.lastIndexOf('/') + 1, path.length());

				log.info("Discovered: " + lastName);

				addTest(new MarkupLanguageTestSuite(lastName, bundle));
			}
		} else {
			String className = MarkupFilesTest.class.getSimpleName() + ".class";
			URL classResource = MarkupFilesTest.class.getResource(className);
			String path = classResource.toExternalForm();
			String classPath = MarkupFilesTest.class.getName().replace('.', '/') + ".class";
			if (!path.endsWith(classPath)) {
				throw new IllegalStateException(path);
			}
			String urlPrefix = "file:/";
			if (!path.startsWith(urlPrefix)) {
				throw new IllegalStateException(path);
			}
			path = path.substring(urlPrefix.length(), path.length() - classPath.length());

			File file;
			try {
				file = new File(URLDecoder.decode(path, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException(e);
			}

			if (file.getName().equals("bin")) {
				file = file.getParentFile();
			} else if (file.getName().equals("classes") && file.getParentFile().getName().equals("target")) {
				file = file.getParentFile().getParentFile();
			}

			file = new File(file, MARKUP_LANGUAGE_TESTS);

			log.info("Inspecting: " + file);

			if (!file.exists() || !file.isDirectory()) {
				throw new IllegalStateException(file.toString());
			}
			File[] languageDirectories = file.listFiles();
			if (languageDirectories == null || languageDirectories.length == 0) {
				throw new IllegalStateException(file.toString());
			}
			for (File languageDir : languageDirectories) {

				log.info("Discovered: " + languageDir);

				addTest(new MarkupLanguageTestSuite(languageDir.getName(), languageDir));
			}
		}
	}
}
