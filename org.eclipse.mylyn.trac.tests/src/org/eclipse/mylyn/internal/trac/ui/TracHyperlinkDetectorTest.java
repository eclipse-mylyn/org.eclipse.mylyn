package org.eclipse.mylar.internal.trac.ui;

import junit.framework.TestCase;

import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylar.internal.trac.core.TracCorePlugin;
import org.eclipse.mylar.tasks.core.TaskRepository;

public class TracHyperlinkDetectorTest extends TestCase {

	private TracHyperlinkDetector detector;
	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		repository = new TaskRepository(TracCorePlugin.REPOSITORY_KIND, "http://localhost");
		detector = new TracHyperlinkDetector();
	}
	
	public void testFindHyperlinks1() {
		IHyperlink[] links = detector.findHyperlinks(repository, "#11", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertTrue(links[0].getHyperlinkText().endsWith(" 11"));
	}

//	public void testFindHyperlinks2() {
//		IHyperlink[] links = detector.findHyperlinks(repository, "#11, #1", 0, 0);
//		assertNotNull(links);
//		assertEquals(2, links.length);
//		assertTrue(links[0].getHyperlinkText().endsWith(" 11"));
//		assertTrue(links[0].getHyperlinkText().endsWith(" 1"));
//	}

	public void testFindWikiHyperlinks1() {
		IHyperlink[] links = detector.findHyperlinks(repository, "[wiki:page]", 0, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals("http://localhost/wiki/page", ((WebHyperlink)links[0]).getURLString());
	}

}
