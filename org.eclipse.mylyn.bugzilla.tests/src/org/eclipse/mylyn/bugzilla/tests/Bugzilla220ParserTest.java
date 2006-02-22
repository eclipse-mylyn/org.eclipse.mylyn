/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
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
import org.eclipse.mylar.core.tests.support.FileTool;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.internal.BugParser;

/**
 * @author Mik Kersten
 * @author Robert Elves
 */
public class Bugzilla220ParserTest extends TestCase {

	private static final String TEST_SERVER = IBugzillaConstants.ECLIPSE_BUGZILLA_URL;
	
//	public void testId220() throws Exception {
//
//		File f = FileTool.getFileInPlugin(BugzillaTestPlugin.getDefault(), new Path(
//				"testdata/pages/test-report-220.html"));
//		Reader in = new FileReader(f);
//
//		BugReport bug = BugParser.parseBug(in, 7, TEST_SERVER, false, null, null, null);
//		
//		assertEquals(7, bug.getId());
//		assertEquals("summary", bug.getSummary());
//		assertEquals("7", bug.getAttribute("Bug#").getValue());
//		assertEquals("7", bug.getAttribute("id").getValue());
//	}
	
	public void testId2201() throws Exception {
		
		File f = FileTool.getFileInPlugin(BugzillaTestPlugin.getDefault(), new Path(
				"testdata/pages/test-report-2201.html"));
		Reader in = new FileReader(f);

		BugReport bug = BugParser.parseBug(in, 125527, TEST_SERVER, false, null, null, null);
		
		assertEquals(125527, bug.getId());
		assertEquals("bugzilla refresh incorrect for new reports and newly opened hits", bug.getSummary());
		assertEquals("125527", bug.getAttribute("Bug#").getValue());
		assertEquals("125527", bug.getAttribute("id").getValue());
	}
}
