/*******************************************************************************
 * Copyright (c) 2006, 2009 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *     David Green - improvements
 *     Jan Mauersberger - fixes for bug 350931
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.ui;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.ui.TracHyperlinkUtil;
import org.eclipse.mylyn.internal.trac.ui.WebHyperlink;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;

/**
 * @author Steffen Pingel
 * @author David Green
 * @see http://trac.edgewall.org/wiki/TracLinks
 */
public class TracHyperlinkUtilTest extends TestCase {

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		repository = new TaskRepository(TracCorePlugin.CONNECTOR_KIND, "http://localhost");
	}

	public void testFindHyperlinksComment() {
		IHyperlink[] links = findTracHyperlinks(repository, "comment:ticket:12:34", 0, 0);
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

	public void testFindHyperlinksNoTicket() {
		IHyperlink[] links = findTracHyperlinks(repository, "#11", 0, 0);
		assertNull(links);

		links = findTracHyperlinks(repository, "#11, #234", 6, 0);
		assertNull(links);

		links = findTracHyperlinks(repository, "  ticket:123  ", 2, 0);
		assertNull(links);

		links = findTracHyperlinks(repository, "#123 report:123", -1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/report/123", ((WebHyperlink) links[0]).getURLString());
	}

	public void testFindHyperlinksReport() {
		IHyperlink[] links = findTracHyperlinks(repository, "report:123", 0, 0);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 10), links[0].getHyperlinkRegion());
		assertEquals("http://localhost/report/123", ((WebHyperlink) links[0]).getURLString());

		links = findTracHyperlinks(repository, "{123}", 0, 0);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 5), links[0].getHyperlinkRegion());
		assertEquals("http://localhost/report/123", ((WebHyperlink) links[0]).getURLString());

		links = findTracHyperlinks(repository, "{{123}}", -1, 0);
		assertEquals(1, links.length);
		assertEquals(new Region(1, 5), links[0].getHyperlinkRegion());

		links = findTracHyperlinks(repository, "{abc}", -1, 0);
		assertNull(links);

		links = findTracHyperlinks(repository, "{{abc}}", -1, 0);
		assertNull(links);

		links = findTracHyperlinks(repository, "{{{123}}}", -1, 0);
		assertNull(links);
	}

	public void testFindHyperlinksChangeset() {
		IHyperlink[] links = findTracHyperlinks(repository, "r123", 0, 0);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 4), links[0].getHyperlinkRegion());
		assertEquals("http://localhost/changeset/123", ((WebHyperlink) links[0]).getURLString());

		links = findTracHyperlinks(repository, "alr123", 0, 0);
		assertNull(links);

		links = findTracHyperlinks(repository, "[123]", 0, 0);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 5), links[0].getHyperlinkRegion());
		assertEquals("http://localhost/changeset/123", ((WebHyperlink) links[0]).getURLString());

		links = findTracHyperlinks(repository, "![123]", 0, 0);
		assertNull(links);

		links = findTracHyperlinks(repository, "![123]", 1, 0);
		assertNull(links);

		links = findTracHyperlinks(repository, "changeset:123", 0, 0);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 13), links[0].getHyperlinkRegion());
		assertEquals("http://localhost/changeset/123", ((WebHyperlink) links[0]).getURLString());

		links = findTracHyperlinks(repository, "[123/trunk]", 0, 0);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 11), links[0].getHyperlinkRegion());
		assertEquals("http://localhost/changeset/123/trunk", ((WebHyperlink) links[0]).getURLString());

		links = findTracHyperlinks(repository, "changeset:123/trunk", 0, 0);
		assertEquals(1, links.length);
		assertEquals(new Region(0, 19), links[0].getHyperlinkRegion());
		assertEquals("http://localhost/changeset/123/trunk", ((WebHyperlink) links[0]).getURLString());
	}

	public void testFindHyperlinksRevisionLog() {
		IHyperlink[] links = findTracHyperlinks(repository, "r123:456", 0, 0);
		assertEquals(2, links.length);
		assertEquals("http://localhost/log/?rev=123&stop_rev=456", ((WebHyperlink) links[0]).getURLString());
		assertEquals(new Region(0, 8), links[0].getHyperlinkRegion());

		links = findTracHyperlinks(repository, "[123:456]", 0, 0);
		assertEquals(1, links.length);
		assertEquals("http://localhost/log/?rev=123&stop_rev=456", ((WebHyperlink) links[0]).getURLString());
		assertEquals(new Region(0, 9), links[0].getHyperlinkRegion());

		links = findTracHyperlinks(repository, "log:@123:456", 0, 0);
		assertEquals(1, links.length);
		assertEquals("http://localhost/log/?rev=123&stop_rev=456", ((WebHyperlink) links[0]).getURLString());
		assertEquals(new Region(0, 12), links[0].getHyperlinkRegion());

		links = findTracHyperlinks(repository, "log:trunk@123:456", 0, 0);
		assertEquals(1, links.length);
		assertEquals("http://localhost/log/trunk?rev=123&stop_rev=456", ((WebHyperlink) links[0]).getURLString());
		assertEquals(new Region(0, 17), links[0].getHyperlinkRegion());
	}

	public void testFindHyperlinksDiff() {
		IHyperlink[] links = findTracHyperlinks(repository, "diff:@123:456", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/changeset/?new=456&old=123", ((WebHyperlink) links[0]).getURLString());

		links = findTracHyperlinks(repository, "diff:trunk/trac@3538//sandbox/vc-refactoring/trac@3539", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(
				"http://localhost/changeset/?new_path=sandbox%2Fvc-refactoring%2Ftrac&old_path=trunk%2Ftrac&new=3539&old=3538",
				((WebHyperlink) links[0]).getURLString());

		links = findTracHyperlinks(repository, "diff:tags/trac-0.9.2/wiki-default//tags/trac-0.9.3/wiki-default", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(
				"http://localhost/changeset/?new_path=tags%2Ftrac-0.9.3%2Fwiki-default&old_path=tags%2Ftrac-0.9.2%2Fwiki-default",
				((WebHyperlink) links[0]).getURLString());
	}

	public void testFindHyperlinksWiki() {
		IHyperlink[] links = findTracHyperlinks(repository, "[wiki:page]", 1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/wiki/page", ((WebHyperlink) links[0]).getURLString());

		links = findTracHyperlinks(repository, "wiki:page", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/wiki/page", ((WebHyperlink) links[0]).getURLString());

		links = findTracHyperlinks(repository, "Page", 0, 0);
		assertNull(links);

		links = findTracHyperlinks(repository, "!Page", 0, 0);
		assertNull(links);

		links = findTracHyperlinks(repository, "ab Page dc", 0, 0);
		assertNull(links);

		links = findTracHyperlinks(repository, "paGe", 0, 0);
		assertNull(links);

		links = findTracHyperlinks(repository, "WikiPage", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/wiki/WikiPage", ((WebHyperlink) links[0]).getURLString());
		assertEquals(new Region(0, 8), links[0].getHyperlinkRegion());

		links = findTracHyperlinks(repository, "!WikiPage", 0, 0);
		assertNull(links);

		links = findTracHyperlinks(repository, "!WikiPage", 1, 0);
		assertNull(links);

		links = findTracHyperlinks(repository, "a WikiPage is here", 4, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/wiki/WikiPage", ((WebHyperlink) links[0]).getURLString());
		assertEquals(new Region(2, 8), links[0].getHyperlinkRegion());

		links = findTracHyperlinks(repository, "a !WikiPage is here", 4, 0);
		assertNull(links);
	}

	public void testFindHyperlinksWikiTwoCamelCaseWork() {
		IHyperlink[] links = findTracHyperlinks(repository, "aWIkiPage is here", 2, 0);
		assertNull(links);

		links = findTracHyperlinks(repository, "aWIkiPage is here", 4, 0);
		assertNull(links);
	}

	public void testFindHyperlinksMilestone() {
		IHyperlink[] links = findTracHyperlinks(repository, "milestone:1.0", 1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/milestone/1.0", ((WebHyperlink) links[0]).getURLString());
	}

	public void testFindHyperlinksAttachment() {
		IHyperlink[] links = findTracHyperlinks(repository, "attachment:ticket:123:foo.bar", 1, 0);
		assertNotNull(links);
		assertEquals("123", ((TaskHyperlink) links[0]).getTaskId());
	}

	public void testFindHyperlinksFiles() {
		IHyperlink[] links = findTracHyperlinks(repository, "source:trunk/foo", 1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/browser/trunk/foo", ((WebHyperlink) links[0]).getURLString());

		links = findTracHyperlinks(repository, "source:trunk/foo@123", 1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/browser/trunk/foo?rev=123", ((WebHyperlink) links[0]).getURLString());

		links = findTracHyperlinks(repository, "source:trunk/foo@123#L456", 1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/browser/trunk/foo?rev=123#L456", ((WebHyperlink) links[0]).getURLString());

		links = findTracHyperlinks(repository, "source:/tags/foo_bar-1.1", 1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/browser/tags/foo_bar-1.1", ((WebHyperlink) links[0]).getURLString());
	}

	private IHyperlink[] findTracHyperlinks(TaskRepository repository, String text, int offsetInText, int textOffset) {
		List<IHyperlink> links = TracHyperlinkUtil.findTracHyperlinks(repository, text, offsetInText, textOffset);
		return (links.isEmpty()) ? null : links.toArray(new IHyperlink[0]);
	}

}
