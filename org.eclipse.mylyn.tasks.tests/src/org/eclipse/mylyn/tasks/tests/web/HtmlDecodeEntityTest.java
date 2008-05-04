/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.web;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.deprecated.ITaskFactory;
import org.eclipse.mylyn.internal.tasks.core.deprecated.QueryHitCollector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.web.tasks.WebRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

/**
 * @author George Lindholm
 * @author Steffen Pingel
 */
public class HtmlDecodeEntityTest extends TestCase {

	private final IProgressMonitor monitor = new NullProgressMonitor();

	private final TaskRepository repository = new TaskRepository("localhost", "file:///tmp/a");

	private final List<RepositoryTaskData> queryHits = new ArrayList<RepositoryTaskData>();

	private final ITaskFactory taskFactory = new ITaskFactory() {
		public AbstractTask createTask(RepositoryTaskData taskData, IProgressMonitor monitor) throws CoreException {
			return null;
		}
	};

	private final TaskDataCollector resultCollector = new QueryHitCollector(taskFactory) {
		@Override
		public void accept(RepositoryTaskData hit) {
			queryHits.add(hit);
		}
	};

	public void testEntities() {
		assertQuery("1:A quote &quot;", "(\\d+?):(.+)", "A quote \""); // Simple quote
		assertQuery("2:A quote '&quot;'", "(\\d+?):(.+)", "A quote '\"'"); // Simple quote
		assertQuery("3:A quote &quot;&quot; doubled", "({Id}\\d+?):({Description}.+)", "A quote \"\" doubled"); // Double quotes
		assertQuery("4:A quote &quot ;", "(\\d+?):(.+)", "A quote &quot ;"); // Bad entity syntax
		assertQuery("5:A quote & quot;", "(\\d+?):(.+)", "A quote & quot;"); // Bad entity syntax
		assertQuery("6:foo & boo", "(\\d+?):(.+)", "foo & boo"); // Non entity syntax
		assertQuery("7:foo&boo poo", "(\\d+?):(.+)", "foo&boo poo"); // Non entity  syntax
		assertQuery("8:foo&boo ;poo", "(\\d+?):(.+)", "foo&boo ;poo"); // Bad, non entity syntax
		assertQuery("9:foo&boo;poo", "(\\d+?):(.+)", "foo&boo;poo"); // Invalid entity
		assertQuery("10:&#32;", "(\\d+?):(.+)", " "); // HTML decimal entity
		assertQuery("11:&#X20;", "(\\d+?):(.+)", " "); // Hexadecimal entity
	}

	private void assertQuery(final String entity, final String regex, final String expected) {
		queryHits.clear();
		IStatus status = WebRepositoryConnector.performQuery(entity, regex, "", monitor, resultCollector, repository);
		assertTrue(status == Status.OK_STATUS);
		assertEquals(queryHits.get(0).getSummary(), expected);
	}

}
