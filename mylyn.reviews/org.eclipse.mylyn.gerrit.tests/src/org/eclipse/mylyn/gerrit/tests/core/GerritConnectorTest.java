/*******************************************************************************
 * Copyright (c) 2012, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.support.GerritHarness;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;
import org.eclipse.mylyn.internal.gerrit.core.GerritTaskSchema;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tests.util.InMemoryTaskDataCollector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class GerritConnectorTest extends TestCase {

	private GerritHarness harness;

	private GerritConnector connector;

	private TaskRepository repository;

	@Override
	@Before
	public void setUp() throws Exception {
		harness = GerritFixture.current().harness();
		connector = new GerritConnector();
		repository = GerritFixture.current().singleRepository();

		harness.ensureOneReviewExists();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		harness.dispose();
	}

	@Test
	public void testPerformQueryAnonymous() throws Exception {
		// XXX some test repositories require OpenID auth which is not supported when running tests
		repository.setCredentials(AuthenticationType.REPOSITORY, null, false);

		IRepositoryQuery query = new RepositoryQuery(repository.getConnectorKind(), "query"); //$NON-NLS-1$
		query.setAttribute(GerritQuery.TYPE, GerritQuery.ALL_OPEN_CHANGES);
		query.setAttribute(GerritQuery.QUERY_STRING, GerritQuery.ALL_OPEN_CHANGES);
		InMemoryTaskDataCollector resultCollector = new InMemoryTaskDataCollector();

		IStatus status = connector.performQuery(repository, query, resultCollector, null, new NullProgressMonitor());
		assertEquals(Status.OK_STATUS, status);
		assertTrue(resultCollector.getResults().size() > 0);
		for (TaskData result : resultCollector.getResults()) {
			assertTrue(result.isPartial());
			assertNull(result.getRoot().getAttribute(GerritTaskSchema.getDefault().UPLOADED.getKey()));
			TaskAttribute key = result.getRoot().getAttribute(GerritTaskSchema.getDefault().KEY.getKey());
			assertNotNull(key);
			String value = key.getValue();
			assertNotNull(value);
			assertTrue(value.startsWith("I")); // Change-Ids are prefixed with an uppercase I
			// 'expand' the abbreviated SHA-1 with 'a's
			String objId = StringUtils.rightPad(value.substring(1), Constants.OBJECT_ID_STRING_LENGTH, 'a');
			assertTrue(ObjectId.isId(objId));
			TaskAttribute owner = result.getRoot().getAttribute(GerritTaskSchema.getDefault().OWNER.getKey());
			assertNotNull(owner);
			assertTrue(StringUtils.isNotBlank(owner.getValue()));
		}
	}
}
