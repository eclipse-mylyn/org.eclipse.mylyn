/*******************************************************************************
 * Copyright (c) 2011, 2013, 2017 Torkild U. Resheim.
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
package org.eclipse.mylyn.docs.epub.tests.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.mylyn.docs.epub.internal.TOCGenerator;
import org.eclipse.mylyn.docs.epub.ncx.NCXFactory;
import org.eclipse.mylyn.docs.epub.ncx.NavMap;
import org.eclipse.mylyn.docs.epub.ncx.NavPoint;
import org.eclipse.mylyn.docs.epub.ncx.Ncx;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Tests for the table of contents generator.
 *
 * @author Torkild U. Resheim
 */
@SuppressWarnings({ "nls" })
public class TestTOCGenerator {

	@Test
	public void testNormal() throws ParserConfigurationException, SAXException, IOException {
		String html = "<body><h1 id='h1-1'>test</h1><h2 id='h2-1'>test</h2><h2 id='h2-2'>test</h2>"
				+ "<h3 id='h3-1'>test</h3><h1 id='h1-2'>test</h1></body>";
		Ncx ncx = createNcx();
		TOCGenerator.parse(new InputSource(new StringReader(html)), "test.html", ncx, 0);
		EList<NavPoint> points = ncx.getNavMap().getNavPoints();
		assertEquals(2, points.size()); // h1
		assertEquals(2, points.get(0).getNavPoints().size()); // h2
		assertEquals(1, points.get(0).getNavPoints().get(1).getNavPoints().size()); // h3
	}

	/**
	 * Verifies that the table of contents generator is capable of handling all levels from 1 through 6.
	 */
	@Test
	public void testH1throughH6() throws ParserConfigurationException, SAXException, IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("<body>");
		for (int i = 1; i < 7; i++) {
			sb.append(String.format("<h%1$d id='h%1$d'>test</h%1$d>%n", i));
		}
		sb.append("</body>");
		Ncx ncx = createNcx();
		TOCGenerator.parse(new InputSource(new StringReader(sb.toString())), "test.html", ncx, 0);
		EList<NavPoint> points = ncx.getNavMap().getNavPoints();
		for (int i = 1; i < 6; i++) {
			assertEquals(1, points.size());
			points = points.get(0).getNavPoints();
		}
	}

	@Test
	public void testMissingIdentifier() throws ParserConfigurationException, SAXException, IOException {
		String html = "<body><h1 id='h1-1'>test</h1><h2>test</h2><h1 id='h1-2'>test</h1></body>";
		Ncx ncx = createNcx();
		TOCGenerator.parse(new InputSource(new StringReader(html)), "test.html", ncx, 0);
		EList<NavPoint> points = ncx.getNavMap().getNavPoints();
		assertEquals(2, points.size());
		// "h2" will be added as a sub-node to the first "h1" regardless of the
		// missing identifier.
		assertEquals(1, points.get(0).getNavPoints().size());
	}

	@Test
	public void testCrazyStructure() throws ParserConfigurationException, SAXException, IOException {
		String html = "<body><h3 id='h3-1'>test</h3><h2 id='h2-1'>test</h2><h1 id='h1-1'>test</h1></body>";
		Ncx ncx = createNcx();
		TOCGenerator.parse(new InputSource(new StringReader(html)), "test.html", ncx, 0);
		EList<NavPoint> points = ncx.getNavMap().getNavPoints();
		// "h3" will be created as a "h1" and "h2" added to it
		assertEquals(2, points.size());
	}

	@Test
	public void testMissingParent() throws ParserConfigurationException, SAXException, IOException {
		String html = "<body><h1 id='h1-1'>test</h1><h3 id='h3-1'>test</h3><h1 id='h1-2'>test</h1></body>";
		Ncx ncx = createNcx();
		TOCGenerator.parse(new InputSource(new StringReader(html)), "test.html", ncx, 0);
		EList<NavPoint> points = ncx.getNavMap().getNavPoints();
		// should be two items at level 1 and one at level 2
		assertEquals(2, points.size());
		assertEquals(1, points.get(0).getNavPoints().size());
	}

	private Ncx createNcx() {
		Ncx ncx = NCXFactory.eINSTANCE.createNcx();
		NavMap navMap = NCXFactory.eINSTANCE.createNavMap();
		ncx.setNavMap(navMap);
		return ncx;
	}
}
