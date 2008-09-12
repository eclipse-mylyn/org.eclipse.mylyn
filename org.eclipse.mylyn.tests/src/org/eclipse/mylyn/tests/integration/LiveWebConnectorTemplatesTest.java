/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.extensions.ActiveTestSuite;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.web.tasks.WebRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.ui.TasksUi;

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
		final List<TaskData> hits = new ArrayList<TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(TaskData hit) {
				hits.add(hit);
			}
		};

		Map<String, String> params = new HashMap<String, String>(template.getAttributes());

		String repositoryUrl = template.repositoryUrl;
		TaskRepository repository = new TaskRepository(WebRepositoryConnector.REPOSITORY_TYPE, repositoryUrl);
		for (Map.Entry<String, String> e : template.getAttributes().entrySet()) {
			repository.setProperty(e.getKey(), e.getValue());
		}

		String url = repository.getRepositoryUrl();
		// HACK: repositories that require auth
		if ("http://demo.otrs.org".equals(url)) {
			repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("skywalker",
					"skywalker"), false);
		} else if ("http://changelogic.araneaframework.org".equals(url)) {
			repository.setCredentials(AuthenticationType.REPOSITORY,
					new AuthenticationCredentials("mylar2", "mylar123"), false);
		}

		String queryUrlTemplate = template.taskQueryUrl;
		String queryUrl = WebRepositoryConnector.evaluateParams(queryUrlTemplate, repository);

		String queryPattern = template.getAttribute(WebRepositoryConnector.PROPERTY_QUERY_REGEXP);
		String regexp = WebRepositoryConnector.evaluateParams(queryPattern, repository);

		String taskPrefix = template.taskPrefixUrl;

		IRepositoryQuery query = TasksUi.getRepositoryModel().createRepositoryQuery(repository);
		query.setSummary(template.label);
		query.setUrl(queryUrl);
		query.setAttribute(WebRepositoryConnector.KEY_QUERY_PATTERN, queryPattern);
		query.setAttribute(WebRepositoryConnector.KEY_QUERY_TEMPLATE, queryUrlTemplate);
		query.setAttribute(WebRepositoryConnector.KEY_TASK_PREFIX, taskPrefix);
		for (Map.Entry<String, String> entry : params.entrySet()) {
			query.setAttribute(entry.getKey(), entry.getValue());
		}

		WebRepositoryConnector connector = new WebRepositoryConnector();
		IStatus status = connector.performQuery(repository, query, collector, null, monitor);

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
		Set<RepositoryTemplate> templates = TasksUiPlugin.getRepositoryTemplateManager().getTemplates(
				WebRepositoryConnector.REPOSITORY_TYPE);
		if (templates.isEmpty()) {
			throw new AssertionFailedException("No temlates found");
		}
		for (RepositoryTemplate template : templates) {
			if (excluded.indexOf(template.repositoryUrl + ",") == -1) {
				suite.addTest(new LiveWebConnectorTemplatesTest(template));
			}
		}

		return suite;
	}

}
