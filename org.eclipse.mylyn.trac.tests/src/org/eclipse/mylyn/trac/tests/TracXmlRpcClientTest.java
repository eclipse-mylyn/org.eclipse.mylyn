/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.trac.core.TracPermissionDeniedException;
import org.eclipse.mylyn.internal.trac.core.TracRemoteException;
import org.eclipse.mylyn.internal.trac.core.TracXmlRpcClient;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracVersion;

/**
 * @author Steffen Pingel
 * @author Xiaoyang Guan
 */
public class TracXmlRpcClientTest extends AbstractTracClientRepositoryTest {

	public TracXmlRpcClientTest() {
		super(Version.XML_RPC);
	}

	@Override
	public void testValidate011() throws Exception {
		validate(TracTestConstants.TEST_TRAC_011_URL);
	}

	public void testValidateFailNoAuth() throws Exception {
		connect(TracTestConstants.TEST_TRAC_010_URL, "", "");
		try {
			repository.validate();
			fail("Expected TracLoginException");
		} catch (TracPermissionDeniedException e) {
		}
	}

	public void testMulticallExceptions() throws Exception {
		connect010();
		try {
			((TracXmlRpcClient) repository).getTickets(new int[] { 1, Integer.MAX_VALUE });
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}
	}

	public void testUpdateAttributes010() throws Exception {
		connect010();
		updateAttributes();
	}

	public void testUpdateAttributes011() throws Exception {
		connect011();
		updateAttributes();
	}

	public void updateAttributes() throws Exception {
		assertNull(repository.getMilestones());
		repository.updateAttributes(new NullProgressMonitor(), true);
		TracVersion[] versions = repository.getVersions();
		assertEquals(2, versions.length);
		Arrays.sort(versions, new Comparator<TracVersion>() {
			public int compare(TracVersion o1, TracVersion o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		assertEquals("1.0", versions[0].getName());
		assertEquals("", versions[0].getDescription());
		assertEquals(new Date(0), versions[0].getTime());
		assertEquals("2.0", versions[1].getName());
		assertEquals("", versions[1].getDescription());
		assertEquals(new Date(0), versions[1].getTime());
	}

	public void testWikiToHtml() throws Exception {
		connect010();
		String html = ((TracXmlRpcClient) repository).wikiToHtml("");
		assertEquals("", html);

		html = ((TracXmlRpcClient) repository).wikiToHtml("A simple line of text.");
		assertEquals("<p>\nA simple line of text.\n</p>\n", html);

		String source = "= WikiFormattingTesting =\n" + " * '''bold''', '''!''' can be bold too''', and '''! '''\n"
				+ " * ''italic''\n" + " * '''''bold italic'''''\n" + " * __underline__\n"
				+ " * {{{monospace}}} or `monospace`\n" + " * ~~strike-through~~\n" + " * ^superscript^ \n"
				+ " * ,,subscript,,\n" + "= Heading =\n" + "== Subheading ==\n";

		String expectedHtml = "<h1 id=\"WikiFormattingTesting\"><a class=\"missing wiki\" href=\"http://mylyn.eclipse.org/trac010/wiki/WikiFormattingTesting\" rel=\"nofollow\">WikiFormattingTesting?</a></h1>\n<ul><li><strong>bold</strong>, <strong>\'\'\' can be bold too</strong>, and <strong>! </strong>\n</li><li><i>italic</i>\n</li><li><strong><i>bold italic</i></strong>\n</li><li><span class=\"underline\">underline</span>\n</li><li><tt>monospace</tt> or <tt>monospace</tt>\n</li><li><del>strike-through</del>\n</li><li><sup>superscript</sup> \n</li><li><sub>subscript</sub>\n</li></ul><h1 id=\"Heading\">Heading</h1>\n<h2 id=\"Subheading\">Subheading</h2>\n";

		html = ((TracXmlRpcClient) repository).wikiToHtml(source);
		assertEquals(expectedHtml, html);
	}
}
