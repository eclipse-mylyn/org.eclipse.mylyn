/*******************************************************************************
 * Copyright (c) 2011, 2014 Torkild U. Resheim.
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

import java.io.File;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil.FeatureEList;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.mylyn.docs.epub.core.EPUB;
import org.eclipse.mylyn.docs.epub.core.ILogger;
import org.eclipse.mylyn.docs.epub.dc.DCType;
import org.eclipse.mylyn.docs.epub.dc.Identifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

@SuppressWarnings("nls")
public abstract class AbstractTest {

	private static final boolean DEBUGGING = false;;

	private class StdOutLogger implements ILogger {

		public void log(String message) {
			log(message, Severity.INFO);
		}

		public void log(String message, Severity severity) {
			if (DEBUGGING) {
				switch (severity) {
				case ERROR:
					System.out.print("[ERROR] ");
					break;
				case DEBUG:
					System.out.print("[DEBUG] ");
					break;
				case INFO:
					System.out.print("[INFO ] ");
					break;
				case VERBOSE:
					System.out.print("[VERBO] ");
					break;
				case WARNING:
					System.out.print("[WARN ] ");
					break;
				default:
					break;
				}
				System.out.println(message);
			}
		}
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	protected static final EStructuralFeature TEXT = XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_Text();

	protected EPUB epub;

	private File epubFile;

	private File epubFolder;

	protected final StdOutLogger logger = new StdOutLogger();

	protected boolean deleteFolder(File folder) {
		if (folder.isDirectory()) {
			String[] children = folder.list();
			for (String element : children) {
				boolean ok = deleteFolder(new File(folder, element));
				if (!ok) {
					return false;
				}
			}
		}
		return folder.delete();
	}

	@SuppressWarnings("rawtypes")
	public String getText(DCType identifier) {
		FeatureMap fm = identifier.getMixed();
		Object o = fm.get(TEXT, false);
		if (o instanceof FeatureEList) {
			if (((FeatureEList) o).size() > 0) {
				return ((FeatureEList) o).get(0).toString();
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public String getText(Identifier element) {
		FeatureMap fm = element.getMixed();
		Object o = fm.get(TEXT, false);
		if (o instanceof FeatureEList) {
			if (((FeatureEList) o).size() > 0) {
				return ((FeatureEList) o).get(0).toString();
			}
		}
		return null;
	}

	@Before
	public void setUp() throws Exception {
		epubFolder = temporaryFolder.newFolder("epub");
		epubFile = new File(temporaryFolder.getRoot(), "test.epub");
		epub = new EPUB(logger);
	}

	public File getEpubFile() {
		return epubFile;
	}

	public File getEpubFolder() {
		return epubFolder;
	}

	/* Bug 454932 - fix or remove failing EPUB test
	@Override
	@After
	public void tearDown() throws Exception {
		if (epubFolder.exists()) {
			deleteFolder(epubFolder);
		}
		if (epubFile.exists()) {
			ValidationReport report = new ValidationReport(epubFile.toString());
			EpubCheck checker = new EpubCheck(epubFile, report);
			checker.validate();
			final String logMessage = report.getErrors();
			epubFile.delete();
			if (!errorExpected && report.getErrorCount() > 0) {
				fail(logMessage);
			}
		}
	}
	 */
}
