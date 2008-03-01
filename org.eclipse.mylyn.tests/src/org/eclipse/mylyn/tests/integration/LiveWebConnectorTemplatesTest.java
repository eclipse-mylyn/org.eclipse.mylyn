/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tests.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.extensions.ActiveTestSuite;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.web.tasks.WebQuery;
import org.eclipse.mylyn.internal.web.tasks.WebRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskFactory;
import org.eclipse.mylyn.tasks.core.QueryHitCollector;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Eugene Kuleshov
 */
public class LiveWebConnectorTemplatesTest extends TestCase {

	private final RepositoryTemplate template;

	public LiveWebConnectorTemplatesTest(RepositoryTemplate template) {
		super("testRepositoryTemplate");
		this.template = template;
	}

	public void testRepositoryTemplate() throws Throwable {
		IProgressMonitor monitor = new NullProgressMonitor();
		MultiStatus queryStatus = new MultiStatus(TasksUiPlugin.ID_PLUGIN, IStatus.OK, "Query result", null);
		final List<AbstractTask> hits = new ArrayList<AbstractTask>();
		QueryHitCollector collector = new QueryHitCollector(new ITaskFactory() {

			public AbstractTask createTask(RepositoryTaskData taskData, IProgressMonitor monitor) throws CoreException {
				// ignore
				return null;
			}
		}) {
			@Override
			public void accept(AbstractTask hit) {
				hits.add(hit);
			}
		};

		Map<String, String> params = new HashMap<String, String>(template.getAttributes());

		String repositoryUrl = template.repositoryUrl;
		TaskRepository repository = new TaskRepository(WebRepositoryConnector.REPOSITORY_TYPE, repositoryUrl);
		for (Map.Entry<String, String> e : template.getAttributes().entrySet()) {
			repository.setProperty(e.getKey(), e.getValue());
		}

		String url = repository.getUrl();
		// HACK: repositories that require auth
		if ("http://demo.otrs.org".equals(url)) {
			repository.setAuthenticationCredentials("skywalker", "skywalker");
		} else if ("http://changelogic.araneaframework.org".equals(url)) {
			repository.setAuthenticationCredentials("mylar2", "mylar123");
		}

		String queryUrlTemplate = template.taskQueryUrl;
		String queryUrl = WebRepositoryConnector.evaluateParams(queryUrlTemplate, repository);

		String queryPattern = template.getAttribute(WebRepositoryConnector.PROPERTY_QUERY_REGEXP);
		String regexp = WebRepositoryConnector.evaluateParams(queryPattern, repository);

		String taskPrefix = template.taskPrefixUrl;

		WebQuery query = new WebQuery(template.label, queryUrl, queryUrlTemplate, queryPattern, taskPrefix,
				repositoryUrl, params);

		WebRepositoryConnector connector = new WebRepositoryConnector();
		IStatus status = connector.performQuery(query, repository, monitor, collector);

//		IStatus resultingStatus; 
//		if(regexp!=null && regexp.length()>0) {
//			resultingStatus = WebRepositoryConnector.performQuery(buffer, regexp, null, monitor, collector,
//					repository);
//		} else {
//			resultingStatus = WebRepositoryConnector.performRssQuery(queryUrl, monitor, collector, repository);
//		}

		assertTrue("Query failed\n" + queryUrl + "\n" + regexp + "\n" + status.toString(), queryStatus.isOK());
		try {
			assertTrue("Expected non-empty query result\n" + queryUrl + "\n" + regexp, hits.size() > 0);
		} catch (Throwable t) {
			String buffer = WebRepositoryConnector.fetchResource(queryUrl, params, repository);

			System.err.println(queryUrl);
			System.err.println(buffer);
			System.err.println("--------------------------------------------------------");
			throw t;
		}
	}

	@Override
	public String getName() {
		return template.label;
	}

	private static final String excluded = "http://demo.otrs.org,";

	public static TestSuite suite() {
		TestSuite suite = new ActiveTestSuite(LiveWebConnectorTemplatesTest.class.getName());

		AbstractRepositoryConnector repositoryConnector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				WebRepositoryConnector.REPOSITORY_TYPE);
		for (RepositoryTemplate template : repositoryConnector.getTemplates()) {
			if (excluded.indexOf(template.repositoryUrl + ",") == -1) {
				suite.addTest(new LiveWebConnectorTemplatesTest(template));
			}
		}

		return suite;
	}

}
