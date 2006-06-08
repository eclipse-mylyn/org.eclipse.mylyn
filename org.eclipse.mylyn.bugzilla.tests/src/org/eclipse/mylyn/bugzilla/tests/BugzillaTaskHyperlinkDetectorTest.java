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

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaTaskHyperlinkDetector;
import org.eclipse.mylar.internal.tasklist.ui.editors.RepositoryTextViewer;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Rob Elves
 */
public class BugzillaTaskHyperlinkDetectorTest extends TestCase {

	private String BUG_FORMAT_1 = "bug# 1";
	private String BUG_FORMAT_1_2 = "bug# 2";
	private String BUG_FORMAT_2_1 = "bug # 1";
	private BugzillaTaskHyperlinkDetector detector = new BugzillaTaskHyperlinkDetector();
	private TaskRepository dummyRepository = new TaskRepository("repository_kind", "repository_url");
	private RepositoryTextViewer viewer = new RepositoryTextViewer(dummyRepository, new Shell(), SWT.NONE);

	protected void setUp() throws Exception {
		super.setUp(); 
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testBeginning() {
		String testString = BUG_FORMAT_1+" is at the beginning";
		viewer.setDocument(new Document(testString));
		Region region = new Region(0, testString.length());
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(testString.indexOf(BUG_FORMAT_1), links[0].getHyperlinkRegion().getOffset());
	}
	
	public void testEnd() {
		String testString = "is ends with bug# 1";
		viewer.setDocument(new Document(testString));
		Region region = new Region(testString.indexOf(BUG_FORMAT_1), testString.length());
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(testString.indexOf(BUG_FORMAT_1), links[0].getHyperlinkRegion().getOffset());
	}
	
	public void testMiddle() {
		String testString = "is a "+BUG_FORMAT_1+" in the middle";
		viewer.setDocument(new Document(testString));
		Region region = new Region(testString.indexOf(BUG_FORMAT_1), testString.length());
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(testString.indexOf(BUG_FORMAT_1), links[0].getHyperlinkRegion().getOffset());
	}
	
	public void testTwoOnSingleLine() {
		String testString = "is a "+BUG_FORMAT_1+" in the middle and at the end "+BUG_FORMAT_1_2;
		viewer.setDocument(new Document(testString));
		Region region = new Region(testString.indexOf(BUG_FORMAT_1_2), testString.length());
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(1, links.length);		
		assertEquals(testString.indexOf(BUG_FORMAT_1_2), links[0].getHyperlinkRegion().getOffset());
	}
	
	public void testMultiLine() {
		String testString = "is a the first line\n this is the second which ends with a bug, "+BUG_FORMAT_1_2;
		viewer.setDocument(new Document(testString));
		Region region = new Region(testString.indexOf(BUG_FORMAT_1_2), testString.length());
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(1, links.length);		
		assertEquals(testString.indexOf(BUG_FORMAT_1_2), links[0].getHyperlinkRegion().getOffset());
	}

	public void testFormat2() {
		String testString = "is a "+BUG_FORMAT_2_1+" in the middle";
		viewer.setDocument(new Document(testString));
		Region region = new Region(testString.indexOf(BUG_FORMAT_2_1), testString.length());
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(testString.indexOf(BUG_FORMAT_2_1), links[0].getHyperlinkRegion().getOffset());
	}
	
	// TODO: test other bug formats
}
