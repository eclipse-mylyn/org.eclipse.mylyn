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
import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.Comment;
import org.eclipse.mylar.bugzilla.core.internal.BugParser;


/**
 * Tests for parsing Bugzilla reports
 */
public class BugzillaParserTest extends TestCase {

	public BugzillaParserTest() {
		super();
	}

	public BugzillaParserTest(String arg0) {
		super(arg0);
	}

	public void testFullReportBug1() throws Exception {
		
		File f = FileTool.getFileInPlugin(BugzillaTestPlugin.getDefault(), new Path("testdata/pages/bug-1-full.html"));
		
		Reader in = new FileReader(f);
		
		BugReport bug = BugParser.parseBug(in, 1, "<server>", false, null, null);

//		displayBug(bug);
		assertEquals("Bug id", 1, bug.getId());
		assertEquals("Bug summary",
				"Usability issue with external editors (1GE6IRL)", bug
						.getSummary());
		assertEquals("Reporter", "andre_weinand@ch.ibm.com (Andre Weinand)",
				bug.getReporter());
		assertEquals("Reporter", "andre_weinand@ch.ibm.com (Andre Weinand)",
				bug.getAttribute("Reporter").getValue());
		assertEquals("Summary",
				"Usability issue with external editors (1GE6IRL)", bug
						.getSummary());
		assertEquals("Status", "VERIFIED", bug.getStatus());
		assertEquals("Resolution", "FIXED", bug.getResolution());
		assertEquals("Keywords", null, bug.getKeywords());
		assertEquals("Assigned To", "James_Moody@ca.ibm.com (James Moody)", bug
				.getAssignedTo());
		assertEquals("Priority", "P3", bug.getAttribute("Priority").getValue());
		assertEquals("OS", "All", bug.getAttribute("OS").getValue());
		assertEquals("Version", "2.0", bug.getAttribute("Version").getValue());
		assertEquals("Target Milestone", "---", bug.getAttribute(
				"Target Milestone").getValue());
		assertEquals("Keywords", "", bug.getAttribute("Keywords").getValue());
		assertEquals("Severity", "normal", bug.getAttribute("Severity")
				.getValue());
		assertEquals("Component", "VCM", bug.getAttribute("Component")
				.getValue());
		assertEquals("CC", "Kevin_McGuire@oti.com", bug.getCC()
				.iterator().next());
		assertEquals("Platform", "All", bug.getAttribute("Platform").getValue());
		assertEquals("Product", "Platform", bug.getAttribute("Product")
				.getValue());
		assertEquals("URL", "", bug.getAttribute("URL").getValue());
		assertEquals("Bug#", "1", bug.getAttribute("Bug#").getValue());

		// Description
		String description = "- Setup a project that contains a *.gif resource\n"
				+ "\t- release project to CVS\n"
				+ "\t- edit the *.gif resource with an external editor, e.g. PaintShop\n"
				+ "\t- save and close external editor\n"
				+ "\t- in Navigator open the icon resource and verify that your changes are there\n"
				+ "\t- release project\n"
				+ "\t\t-> nothing to release!\n"
				+ "\t- in Navigator open the icon resource and verify that your changes are still there\n\n"
				+

				"\tProblem: because I never \"Refreshed from local\", the workspace hasn't changed so \"Release\" didn't find anything.\n"
				+ "\tHowever opening the resource with an external editor found the modified file on disk and showed the changes.\n\n"
				+

				"\tThe real problem occurs if \"Release\" actually finds something to release but you don't spot that some resources are missing.\n"
				+ "\tThis is extremely error prone: one of my changes didn't made it into build 110 because of this!\n\n"
				+

				"NOTES:\n"
				+ "EG (5/23/01 3:00:33 PM)\n"
				+ "\tRelease should do a refresh from local before doing the release.\n"
				+ "\tMoving to VCM\n\n\n"
				+

				"KM (05/27/01 5:10:19 PM)\n"
				+ "\tComments from JM in related email:\n\n"
				+

				"\tShould not do this for free.  Could have a setting which made it optoinal but should nt be mandatory.  Default setting could be to have it on.\n"
				+ "\tConsider the SWT team who keep their workspaces on network drives.  This will be slow.  \n\n"
				+

				"\tSide effects will be that a build runs when the refresh is completed unless you somehow do it in a workspace runnable and don't end the\n"
				+ "\trunnable until after the release.  This would be less than optimal as some builders may be responsible for maintaining some invariants and deriving resources which are releasable.  If you don't run the builders before releasing, the invariants will not be maintained and you will release inconsistent state.\n\n"
				+

				"\tSummary:  Offer to \"ensure local consistency\" before releasing.\n\n"
				+

				"KM (5/31/01 1:30:35 PM)\n"
				+ "\tSee also 1GEAG1A: ITPVCM:WINNT - Internal error comparing with a document\n"
				+ "\twhich failed with an error.  Never got log from Tod though.";

		assert(description.length() == bug.getDescription().length());
		assertEquals("Description", description, bug.getDescription());

		// Comments:
		Iterator<Comment> it = bug.getComments().iterator();
		while (it.hasNext()) {
			// COMMENT #1
			Comment comment = it.next();
			assertEquals("Author1", "James_Moody@ca.ibm.com", comment
					.getAuthor());
			assertEquals("Name1", "James Moody", comment.getAuthorName());
			assertEquals(
					"Text1",
					"*** Bug 183 has been marked as a duplicate of this bug. ***",
					comment.getText());

			// COMMENT #2
			comment = it.next();
			assertEquals("Author2", "James_Moody@ca.ibm.com", comment
					.getAuthor());
			assertEquals("Name2", "James Moody", comment.getAuthorName());
			assertEquals("Text2",
					"Implemented 'auto refresh' option. Default value is off.",
					comment.getText());

			// COMMENT 3
			comment = it.next();
			assertEquals("Author3", "dj_houghton@ca.ibm.com", comment
					.getAuthor());
			assertEquals("Name3", "DJ Houghton", comment.getAuthorName());
			assertEquals("Text3", "PRODUCT VERSION:\n\t109\n\n", comment
					.getText());

			// COMMENT 4
			comment = it.next();
			assertEquals("Author4", "James_Moody@ca.ibm.com", comment
					.getAuthor());
			assertEquals("Name4", "James Moody", comment.getAuthorName());
			assertEquals("Text4", "Fixed in v206", comment.getText());
		}
	}

//	private static void displayBug(BugReport bug) {
//		System.out.println("Bug " + bug.getId() + ": " + bug.getSummary());
//		System.out.println("Opened: " + bug.getCreated());
//		for (Iterator<Attribute> it = bug.getAttributes().iterator(); it.hasNext();) {
//			Attribute attribute = it.next();
//			String key = attribute.getName();
//			System.out.println(key + ": " + attribute.getValue()
//					+ (attribute.isEditable() ? " [OK]" : " %%"));
//		}
//
//		System.out.print("CC: ");
//		for (Iterator<String> it = bug.getCC().iterator(); it.hasNext();) {
//			String email = it.next();
//			System.out.print(email + " ");
//		}
//		System.out.println();
//
//		System.out.println(bug.getDescription());
//		for (Iterator<Comment> it = bug.getComments().iterator(); it.hasNext();) {
//			Comment comment = it.next();
//			System.out.println(comment.getAuthorName() + " <"
//					+ comment.getAuthor() + "> (" + comment.getCreated() + ")");
//			System.out.print(comment.getText());
//			System.out.println();
//		}
//	}
//
//	private static void printComments(BugReport bug) {
//		for (Iterator<Comment> it = bug.getComments().iterator(); it.hasNext();) {
//			Comment comment = it.next();
//			System.out.println("Author:   " + comment.getAuthor());
//			System.out.println("Name:     " + comment.getAuthorName());
//			System.out.println("Date:     " + comment.getCreated());
//			System.out.println("Bug ID:   " + comment.getBug().getId());
//			System.out.println("Comment:  " + comment.getText());
//			System.out.println();
//		}
//	}
//
//	/** prints names of attributes */
//	private static void printAttributes(BugReport bug) {
//		System.out.println("ATTRIBUTE KEYS:");
//		for (Iterator<Attribute> it = bug.getAttributes().iterator(); it.hasNext();) {
//			Attribute att = it.next();
//			System.out.println(att.getName());
//		}
//	}
}
