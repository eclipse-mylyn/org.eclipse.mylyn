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

package org.eclipse.mylar.tasks.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.extensions.ActiveTestSuite;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.internal.tasks.core.WebTask;
import org.eclipse.mylar.internal.tasks.web.WebRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.RepositoryTemplate;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Eugene Kuleshov
 */
public class WebRepositoryConnectorTest extends TestCase {

	private final RepositoryTemplate template;

	public WebRepositoryConnectorTest(RepositoryTemplate template) {
		super("testRepositoryTemplate");
		this.template = template;
	}

	public void testRepositoryTemplate() throws Throwable {
  	    IProgressMonitor monitor = new NullProgressMonitor();
  	    MultiStatus queryStatus = new MultiStatus(TasksUiPlugin.PLUGIN_ID, IStatus.OK, "Query result", null);
  	    final List<AbstractQueryHit> hits = new ArrayList<AbstractQueryHit>();
  	    QueryHitCollector collector = new QueryHitCollector(TasksUiPlugin.getTaskListManager().getTaskList()) {
  	        @Override
  	        public void addMatch(AbstractQueryHit hit) {
  	          hits.add(hit);
  	        }
  	    };

	    Map<String, String> params = new HashMap<String, String>();
	    Map<String, String> attributes = new HashMap<String, String>(template.getAttributes());
	    for(Map.Entry<String, String> e : attributes.entrySet()) {
	        String key = e.getKey();
//	        if(key.startsWith(WebRepositoryConnector.PARAM_PREFIX)) {
	            params.put(key, e.getValue());
//	        }
	    }

        TaskRepository repository = new TaskRepository(WebTask.REPOSITORY_TYPE, template.repositoryUrl, params);
        if(repository.getUrl().equals("http://demo.otrs.org")) {
        	// HACK: OTRS repository require auth 
        	repository.setAuthenticationCredentials("skywalker", "skywalker");
//        } else {
//        	repository.setAuthenticationCredentials("user", "pwd");
        }
 
        String taskQueryUrl = WebRepositoryConnector.evaluateParams(template.taskQueryUrl, repository);
        String buffer = WebRepositoryConnector.fetchResource(taskQueryUrl, params, repository);
        assertTrue("Unable to fetch resource\n" + taskQueryUrl, buffer != null && buffer.length() > 0);
        
        String regexp = WebRepositoryConnector.evaluateParams(template.getAttribute(WebRepositoryConnector.PROPERTY_QUERY_REGEXP), repository);
        IStatus resultingStatus = WebRepositoryConnector.performQuery(buffer, regexp, null, monitor, collector, repository);

        assertTrue("Query failed\n"+taskQueryUrl+"\n"+regexp+"\n"+resultingStatus.toString(), queryStatus.isOK());
        try {
			assertTrue("Expected non-empty query result\n" + taskQueryUrl + "\n" + regexp, hits.size() > 0);
		} catch (Throwable t) {
			System.err.println(taskQueryUrl);
			System.err.println(buffer);
			System.err.println("--------------------------------------------------------");
			throw t;
		}
	}

	public String getName() {
		return template.label;
	}

	public static TestSuite suite() {
		TestSuite suite = new ActiveTestSuite(WebRepositoryConnectorTest.class.getName());

		AbstractRepositoryConnector repositoryConnector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(WebTask.REPOSITORY_TYPE);
		for (RepositoryTemplate template : repositoryConnector.getTemplates()) {
			suite.addTest(new WebRepositoryConnectorTest(template));
		}

		return suite;
	}

}
