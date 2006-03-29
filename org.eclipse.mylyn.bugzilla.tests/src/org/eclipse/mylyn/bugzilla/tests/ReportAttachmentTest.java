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
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.core.tests.support.FileTool;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.internal.BugParser;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

import junit.framework.TestCase;

/**
 * @author Rob Elves
 */
public class ReportAttachmentTest extends TestCase {

	public void testExistingBugWithAttachment() throws Exception {
		File f = FileTool.getFileInPlugin(BugzillaTestPlugin.getDefault(), new Path(
				"testdata/pages/test-report-222attachment.html"));
		Reader in = new FileReader(f);

		BugReport bug = BugParser.parseBug(in, 4, IBugzillaConstants.ECLIPSE_BUGZILLA_URL, false, null, null, null);
		
		assertEquals(4, bug.getId());
		assertEquals("4", bug.getAttribute("Bug#").getValue());
		assertEquals("4", bug.getAttribute("id").getValue());
		
		assertNotNull(bug.getComments());
		assertEquals(1, bug.getComments().size());
		assertTrue(bug.getComments().get(0).hasAttachment());
		assertEquals("Testing upload", bug.getComments().get(0).getAttachmentDescription());
//		System.err.println(bug.getComments().get(0).getText());
//		assertEquals(1, bug.getAttachements().size());
//		assertEquals("Testing upload", bug.getAttachements().get(1));
	}
	
	public void testAttachementDownload() throws Exception {
		URL localURL = null;

		URL installURL = BugzillaTestPlugin.getDefault().getBundle().getEntry("testdata/contexts/");
		localURL = FileLocator.toFileURL(installURL);
		
		File destinationFile = new File(localURL.getPath()+"downloadedContext.xml");
		
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);
		boolean result = BugzillaRepositoryUtil.downloadAttachment(repository, 2, destinationFile, true);
		assertTrue(result);
	}
	
//	public void testAttachementUpload() throws Exception {
//		File sourceFile = FileTool.getFileInPlugin(BugzillaTestPlugin.getDefault(), new Path(
//		"testdata/contexts/downloadedContext.xml"));
//		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);
//		repository.setAuthenticationCredentials("relves@cs.ubc.ca", "***");
//		boolean result = BugzillaRepositoryUtil.uploadAttachment(repository, 4, "Upload Comment 2", "Upload Description 2", sourceFile, "application/xml", false);		
//		assertTrue(result);
//	}
	
}
