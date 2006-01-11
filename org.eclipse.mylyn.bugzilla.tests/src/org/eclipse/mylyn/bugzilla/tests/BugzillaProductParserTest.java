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
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.login.LoginException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.core.internal.ProductParser;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.repositories.TaskRepository;

/**
 * Tests for parsing Product Page for new Bugzilla reports
 * 
 * @author Mik Kersten
 */
public class BugzillaProductParserTest extends TestCase {

	private TaskRepository repository;
	
    @Override
	protected void setUp() throws Exception {
		super.setUp();
		repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND, new URL(IBugzillaConstants.ECLIPSE_BUGZILLA_URL));
		MylarTaskListPlugin.getRepositoryManager().addRepository(repository);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		MylarTaskListPlugin.getRepositoryManager().removeRepository(repository);
	}
	
	public BugzillaProductParserTest(String arg0) {
		super(arg0);
	}

	public void test220Products() throws LoginException, IOException, ParseException {
		BugzillaPlugin.getDefault().getPluginPreferences().setValue(
				IBugzillaConstants.SERVER_VERSION, 
				IBugzillaConstants.SERVER_220);

		File file = FileTool.getFileInPlugin(BugzillaTestPlugin.getDefault(), new Path("testdata/pages/test-products-220.html"));
		Reader in = new FileReader(file);
		List<String> productList = new ArrayList<String>();
		System.err.println(">>>> " + repository.getServerUrl());
		productList = new ProductParser(in).getProducts(repository);
		
		Iterator<String> itr = productList.iterator();
		assertTrue(itr.hasNext());
		assertEquals("AJDT", "AJDT", itr.next());
		assertEquals("ALF", "ALF", itr.next());
		assertEquals("AspectJ", "AspectJ", itr.next());
		assertEquals("BIRT", "BIRT", itr.next());
	}
	
	public void test218Products() throws LoginException, IOException, ParseException {
		BugzillaPlugin.getDefault().getPluginPreferences().setValue(
				IBugzillaConstants.SERVER_VERSION, 
				IBugzillaConstants.SERVER_218);

		File file = FileTool.getFileInPlugin(BugzillaTestPlugin.getDefault(), new Path("testdata/pages/test-products-218.html"));
		Reader in = new FileReader(file);
		List<String> productList = new ArrayList<String>();
		productList = new ProductParser(in).getProducts(repository);
		
		Iterator<String> itr = productList.iterator();
		assertTrue(itr.hasNext());
		assertEquals("AJDT", "AJDT", itr.next());
		assertEquals("ALF", "ALF", itr.next());
		assertEquals("AspectJ", "AspectJ", itr.next());
		assertEquals("BIRT", "BIRT", itr.next());
	}
	
	public void testFullReportBugNoBug() throws Exception {

		BugzillaPlugin.getDefault().getPluginPreferences().setValue(
				IBugzillaConstants.SERVER_VERSION, 
				IBugzillaConstants.SERVER_218);
		 
		File file = FileTool.getFileInPlugin(BugzillaTestPlugin.getDefault(), new Path("testdata/pages/product-page.html"));
		Reader in = new FileReader(file);
		List<String> productList = new ArrayList<String>();
		productList = new ProductParser(in).getProducts(repository);

		Iterator<String> itr = productList.iterator();
		assertTrue(itr.hasNext());
		assertEquals("AJDT", "AJDT", itr.next());
		assertEquals("ALF", "ALF", itr.next());
		assertEquals("AspectJ", "AspectJ", itr.next());
		assertEquals("BIRT", "BIRT", itr.next());
		
//			assertEquals("CME", "CME", itr.next());
//			assertEquals("ECESIS", "ECESIS", itr.next());
//			assertEquals("EMF", "EMF", itr.next());
//			assertEquals("Equinox", "Equinox", itr.next());
//			assertEquals("GEF", "GEF", itr.next());
//			assertEquals("GMT", "GMT", itr.next());
//			assertEquals("Hyades", "Hyades", itr.next());
//			assertEquals("JDT", "JDT", itr.next());
//			assertEquals("PDE", "PDE", itr.next());
//			assertEquals("Platform", "Platform", itr.next());
//			assertEquals("Stellation", "Stellation", itr.next());
//			assertEquals("UML2", "UML2", itr.next());
//			assertEquals("VE", "VE", itr.next());
//			assertEquals("WSVT", "WSVT", itr.next());
//			assertEquals("XSD", "XSD", itr.next());
//		}
	}
}
