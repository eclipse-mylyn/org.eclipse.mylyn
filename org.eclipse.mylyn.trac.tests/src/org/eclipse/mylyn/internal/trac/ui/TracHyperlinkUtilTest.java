/*******************************************************************************
 * Copyright (c) 2005, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.trac.ui;

import junit.framework.TestCase;

import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;

public class TracHyperlinkUtilTest extends TestCase {

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		repository = new TaskRepository(TracCorePlugin.CONNECTOR_KIND, "http://localhost");
	}

	public void testFindHyperlinksComment() {
		IHyperlink[] links = TracHyperlinkUtil.findTracHyperlinks(repository, "comment:ticket:12:34", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 20), links[0].getHyperlinkRegion());
		assertEquals("12", ((TaskHyperlink) links[0]).getTaskId());
	}

	public void testFindHyperlinksTicket() {
		IHyperlink[] links = TracHyperlinkUtil.findTicketHyperlinks(repository, "#11", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 3), links[0].getHyperlinkRegion());
		assertEquals("11", ((TaskHyperlink) links[0]).getTaskId());

		links = TracHyperlinkUtil.findTicketHyperlinks(repository, "#11, #234", 6, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("234", ((TaskHyperlink) links[0]).getTaskId());

		links = TracHyperlinkUtil.findTicketHyperlinks(repository, "  ticket:123  ", 2, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(new Region(2, 10), links[0].getHyperlinkRegion());
		assertEquals("123", ((TaskHyperlink) links[0]).getTaskId());
	}

	public void testFindHyperlinksReport() {
		IHyperlink[] links = TracHyperlinkUtil.findTracHyperlinks(repository, "report:123", 0, 0);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 10), links[0].getHyperlinkRegion());
		assertEquals("http://localhost/report/123", ((WebHyperlink) links[0]).getURLString());

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "{123}", 0, 0);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 5), links[0].getHyperlinkRegion());
		assertEquals("http://localhost/report/123", ((WebHyperlink) links[0]).getURLString());
	}

	public void testFindHyperlinksChangeset() {
		IHyperlink[] links = TracHyperlinkUtil.findTracHyperlinks(repository, "r123", 0, 0);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 4), links[0].getHyperlinkRegion());
		assertEquals("http://localhost/changeset/123", ((WebHyperlink) links[0]).getURLString());

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "[123]", 0, 0);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 5), links[0].getHyperlinkRegion());
		assertEquals("http://localhost/changeset/123", ((WebHyperlink) links[0]).getURLString());

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "changeset:123", 0, 0);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 13), links[0].getHyperlinkRegion());
		assertEquals("http://localhost/changeset/123", ((WebHyperlink) links[0]).getURLString());

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "[123/trunk]", 0, 0);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 11), links[0].getHyperlinkRegion());
		assertEquals("http://localhost/changeset/123/trunk", ((WebHyperlink) links[0]).getURLString());

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "changeset:123/trunk", 0, 0);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 19), links[0].getHyperlinkRegion());
		assertEquals("http://localhost/changeset/123/trunk", ((WebHyperlink) links[0]).getURLString());
	}

	public void testFindHyperlinksRevisionLog() {
		IHyperlink[] links = TracHyperlinkUtil.findTracHyperlinks(repository, "r123:456", 0, 0);
		assertEquals(2, links.length);
		assertEquals("http://localhost/log/?rev=123&stop_rev=456", ((WebHyperlink) links[0]).getURLString());
		assertEquals(new Region(0, 8), links[0].getHyperlinkRegion());

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "[123:456]", 0, 0);
		assertEquals(1, links.length);
		assertEquals("http://localhost/log/?rev=123&stop_rev=456", ((WebHyperlink) links[0]).getURLString());
		assertEquals(new Region(0, 9), links[0].getHyperlinkRegion());

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "log:@123:456", 0, 0);
		assertEquals(1, links.length);
		assertEquals("http://localhost/log/?rev=123&stop_rev=456", ((WebHyperlink) links[0]).getURLString());
		assertEquals(new Region(0, 12), links[0].getHyperlinkRegion());

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "log:trunk@123:456", 0, 0);
		assertEquals(1, links.length);
		assertEquals("http://localhost/log/trunk?rev=123&stop_rev=456", ((WebHyperlink) links[0]).getURLString());
		assertEquals(new Region(0, 17), links[0].getHyperlinkRegion());
	}

	public void testFindHyperlinksDiff() {
		IHyperlink[] links = TracHyperlinkUtil.findTracHyperlinks(repository, "diff:@123:456", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/changeset/?new=456&old=123", ((WebHyperlink) links[0]).getURLString());

		links = TracHyperlinkUtil.findTracHyperlinks(repository,
				"diff:trunk/trac@3538//sandbox/vc-refactoring/trac@3539", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(
				"http://localhost/changeset/?new_path=sandbox%2Fvc-refactoring%2Ftrac&old_path=trunk%2Ftrac&new=3539&old=3538",
				((WebHyperlink) links[0]).getURLString());

		links = TracHyperlinkUtil.findTracHyperlinks(repository,
				"diff:tags/trac-0.9.2/wiki-default//tags/trac-0.9.3/wiki-default", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(
				"http://localhost/changeset/?new_path=tags%2Ftrac-0.9.3%2Fwiki-default&old_path=tags%2Ftrac-0.9.2%2Fwiki-default",
				((WebHyperlink) links[0]).getURLString());
	}

	public void testFindHyperlinksWiki() {
		IHyperlink[] links = TracHyperlinkUtil.findTracHyperlinks(repository, "[wiki:page]", 1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/wiki/page", ((WebHyperlink) links[0]).getURLString());

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "wiki:page", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/wiki/page", ((WebHyperlink) links[0]).getURLString());

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "Page", 0, 0);
		assertNull(links);

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "ab Page dc", 0, 0);
		assertNull(links);

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "paGe", 0, 0);
		assertNull(links);

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "WikiPage", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/wiki/WikiPage", ((WebHyperlink) links[0]).getURLString());
		assertEquals(new Region(0, 8), links[0].getHyperlinkRegion());

	}

	public void testFindHyperlinksMilestone() {
		IHyperlink[] links = TracHyperlinkUtil.findTracHyperlinks(repository, "milestone:1.0", 1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/milestone/1.0", ((WebHyperlink) links[0]).getURLString());
	}

	public void testFindHyperlinksAttachment() {
		IHyperlink[] links = TracHyperlinkUtil.findTracHyperlinks(repository, "attachment:ticket:123:foo.bar", 1, 0);
		assertNotNull(links);
		assertEquals("123", ((TaskHyperlink) links[0]).getTaskId());
	}

	public void testFindHyperlinksFiles() {
		IHyperlink[] links = TracHyperlinkUtil.findTracHyperlinks(repository, "source:trunk/foo", 1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/browser/trunk/foo", ((WebHyperlink) links[0]).getURLString());

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "source:trunk/foo@123", 1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/browser/trunk/foo?rev=123", ((WebHyperlink) links[0]).getURLString());

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "source:trunk/foo@123#L456", 1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/browser/trunk/foo?rev=123#L456", ((WebHyperlink) links[0]).getURLString());

		links = TracHyperlinkUtil.findTracHyperlinks(repository, "source:/tags/foo_bar-1.1", 1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/browser/tags/foo_bar-1.1", ((WebHyperlink) links[0]).getURLString());
	}

}
