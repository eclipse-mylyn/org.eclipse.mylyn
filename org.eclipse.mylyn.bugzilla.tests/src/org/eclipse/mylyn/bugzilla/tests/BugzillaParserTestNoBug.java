/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.tests;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.internal.BugParser;


/**
 * Tests for parsing Bugzilla reports
 */
public class BugzillaParserTestNoBug extends TestCase {

	public BugzillaParserTestNoBug() {
		super();
	}

	public BugzillaParserTestNoBug(String arg0) {
		super(arg0);
	}

	public void testBugNotFound() throws Exception {

		File f = FileTool.getFileInPlugin(BugzillaTestPlugin.getDefault(), new Path("testdata/pages/bug-not-found-eclipse.html"));
		
		Reader in = new FileReader(f);

		BugReport bug = BugParser.parseBug(in, 666, "<server>", false, null, null, null);
		assertNull(bug);
	}
}
