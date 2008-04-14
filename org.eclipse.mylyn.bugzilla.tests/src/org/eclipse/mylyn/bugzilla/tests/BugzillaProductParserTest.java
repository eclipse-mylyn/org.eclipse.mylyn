/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.bugzilla.tests;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Tests for parsing Product Page for new Bugzilla reports
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaProductParserTest extends TestCase {

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public BugzillaProductParserTest(String arg0) {
		super(arg0);
	}

	private TaskRepository setRepository(String url) {
		repository = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND, url);
		Credentials credentials = TestUtil.readCredentials();
		repository.setAuthenticationCredentials(credentials.username, credentials.password);
		return repository;
	}

	public void test222Products() throws Exception {
		setRepository(IBugzillaConstants.TEST_BUGZILLA_222_URL);
		List<String> productList = BugzillaCorePlugin.getRepositoryConfiguration(repository, false, null).getProducts();
		Iterator<String> itr = productList.iterator();
		assertTrue(itr.hasNext());
		assertEquals("Read Only Test Cases", itr.next());
	}

	public void test2201Products() throws Exception {
		setRepository(IBugzillaConstants.TEST_BUGZILLA_2201_URL);
		List<String> productList = BugzillaCorePlugin.getRepositoryConfiguration(repository, false, null).getProducts();
		Iterator<String> itr = productList.iterator();
		assertTrue(itr.hasNext());
		assertEquals("TestProduct", "TestProduct", itr.next());

	}

	public void test220Products() throws Exception {
		setRepository(IBugzillaConstants.TEST_BUGZILLA_220_URL);
		List<String> productList = BugzillaCorePlugin.getRepositoryConfiguration(repository, false, null).getProducts();
		assertEquals(2, productList.size());
		assertTrue(productList.contains("TestProduct"));
		assertTrue(productList.contains("Widget"));

	}

	public void test218Products() throws Exception {
		setRepository(IBugzillaConstants.TEST_BUGZILLA_218_URL);
		List<String> productList = BugzillaCorePlugin.getRepositoryConfiguration(repository, false, null).getProducts();
		assertEquals(1, productList.size());
		assertTrue(productList.contains("TestProduct"));
	}

//  No longer supporting 216
//	public void test216Products() throws Exception {
//
//		repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_216_URL,
//				IBugzillaConstants.BugzillaServerVersion.SERVER_216.toString());
//
//		List<String> productList = BugzillaRepositoryUtil.getProductList(repository);
//		Iterator<String> itr = productList.iterator();
//		assertTrue(itr.hasNext());
//		assertEquals("TestProduct", "TestProduct", itr.next());
//	}

}
