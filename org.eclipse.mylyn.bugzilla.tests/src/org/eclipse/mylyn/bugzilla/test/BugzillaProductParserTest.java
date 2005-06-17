/*******************************************************************************
 * Copyright (c) 2003 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.test;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.eclipse.mylar.bugzilla.core.internal.ProductParser;


/**
 * Tests for parsing Product Page for new Bugzilla reports
 */
public class BugzillaProductParserTest extends TestCase {

	public BugzillaProductParserTest() {
		super();
	}

	public BugzillaProductParserTest(String arg0) {
		super(arg0);
	}

	public void testFullReportBugNoBug() throws Exception {

		File f = FileTool.getFileInPlugin(BugzillaTestPlugin.getDefault(), new Path("TestPages/product-page.html"));
		
		Reader in = new FileReader(f);

		List<String> productList = new ArrayList<String>();
		productList = new ProductParser(in).getProducts();
//		printList(productList);

		Iterator<String> itr = productList.iterator();

		while (itr.hasNext()) {
			assertEquals("AJDT", "AJDT", itr.next());
			assertEquals("AspectJ", "AspectJ", itr.next());
			assertEquals("CDT", "CDT", itr.next());
			assertEquals("CME", "CME", itr.next());
			assertEquals("ECESIS", "ECESIS", itr.next());
			assertEquals("EMF", "EMF", itr.next());
			assertEquals("Equinox", "Equinox", itr.next());
			assertEquals("GEF", "GEF", itr.next());
			assertEquals("GMT", "GMT", itr.next());
			assertEquals("Hyades", "Hyades", itr.next());
			assertEquals("JDT", "JDT", itr.next());
			assertEquals("PDE", "PDE", itr.next());
			assertEquals("Platform", "Platform", itr.next());
			assertEquals("Stellation", "Stellation", itr.next());
			assertEquals("UML2", "UML2", itr.next());
			assertEquals("VE", "VE", itr.next());
			assertEquals("WSVT", "WSVT", itr.next());
			assertEquals("XSD", "XSD", itr.next());
		}
	}

//	private void printList(List<String> productList) {
//
//		Iterator<String> itr = productList.iterator();
//		System.out.println("Product List:");
//		while (itr.hasNext())
//			System.out.println(itr.next());
//	}
}