/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.bugzilla.tests;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.tests.support.MylarTestUtils;
import org.eclipse.mylyn.context.tests.support.MylarTestUtils.Credentials;
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
		// repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
		// new URL(IBugzillaConstants.ECLIPSE_BUGZILLA_URL));
		// MylarTaskListPlugin.getRepositoryManager().addRepository(repository);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		// MylarTaskListPlugin.getRepositoryManager().removeRepository(repository);
	}

	public BugzillaProductParserTest(String arg0) {
		super(arg0);
	}

	private TaskRepository setRepository(String url) {
		repository = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND, url);
		Credentials credentials = MylarTestUtils.readCredentials();
		repository.setAuthenticationCredentials(credentials.username, credentials.password);
		return repository;
	}
	
	public void test222Products() throws Exception {
		setRepository(IBugzillaConstants.TEST_BUGZILLA_222_URL);
		List<String> productList = BugzillaCorePlugin.getRepositoryConfiguration(repository, false).getProducts();
		Iterator<String> itr = productList.iterator();
		assertTrue(itr.hasNext());
		assertEquals("Read Only Test Cases", itr.next());
	}

	public void test2201Products() throws Exception {
		setRepository(IBugzillaConstants.TEST_BUGZILLA_2201_URL);		
		List<String> productList = BugzillaCorePlugin.getRepositoryConfiguration(repository, false).getProducts();
		Iterator<String> itr = productList.iterator();
		assertTrue(itr.hasNext());
		assertEquals("TestProduct", "TestProduct", itr.next());

	}

	public void test220Products() throws Exception {
		setRepository(IBugzillaConstants.TEST_BUGZILLA_220_URL);	
		List<String> productList = BugzillaCorePlugin.getRepositoryConfiguration(repository, false).getProducts();
		assertEquals(2, productList.size());
		assertTrue(productList.contains("TestProduct"));
		assertTrue(productList.contains("Widget"));	

	}

	public void test218Products() throws Exception {
		setRepository(IBugzillaConstants.TEST_BUGZILLA_218_URL);	
		List<String> productList = BugzillaCorePlugin.getRepositoryConfiguration(repository, false).getProducts();
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
