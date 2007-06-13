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

package org.eclipse.mylyn.internal.trac.ui;

import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryQuery;
import org.eclipse.mylyn.internal.trac.core.TracTask;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.DelegatingTaskExternalizer;
import org.eclipse.mylyn.tasks.core.TaskExternalizationException;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Steffen Pingel
 * @author Mik Kersten
 */
public class TracTaskExternalizer extends DelegatingTaskExternalizer {

	private static final String KEY_TRAC = "Trac";

	private static final String KEY_TRAC_CATEGORY = KEY_TRAC + KEY_CATEGORY;

	private static final String KEY_TRAC_TASK = KEY_TRAC + KEY_TASK;

	private static final String KEY_TRAC_QUERY_HIT = KEY_TRAC + KEY_QUERY_HIT;

	private static final String KEY_TRAC_QUERY = KEY_TRAC + KEY_QUERY;

	// category related methods

	@Override
	public boolean canReadCategory(Node node) {
		return node.getNodeName().equals(KEY_TRAC_CATEGORY);
	}

	@Override
	public String getCategoryTagName() {
		return KEY_TRAC_CATEGORY;
	}

	// task related methods

	@Override
	public boolean canCreateElementFor(AbstractTask task) {
		return task instanceof TracTask;
	}

	@Override
	public String getTaskTagName() {
		return KEY_TRAC_TASK;
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String taskId, String summary, Element element, TaskList taskList,
			AbstractTaskContainer category, AbstractTask parent) throws TaskExternalizationException {
		TracTask task = new TracTask(repositoryUrl, taskId, summary);
		return task;
	}

	// query related methods

// @Override
// public boolean canCreateElementFor(AbstractQueryHit queryHit) {
// return queryHit instanceof TracQueryHit;
// }

	@Override
	public boolean canCreateElementFor(AbstractRepositoryQuery category) {
		return category instanceof TracRepositoryQuery;
	}

	@Override
	public boolean canReadQuery(Node node) {
		return node.getNodeName().equals(KEY_TRAC_QUERY);
	}

	@Override
	public boolean canReadQueryHit(Node node) {
		return node.getNodeName().equals(KEY_TRAC_QUERY_HIT);
	}

	@Override
	public Element createQueryElement(AbstractRepositoryQuery query, Document doc, Element parent) {
		String queryTagName = getQueryTagNameForElement(query);
		Element node = doc.createElement(queryTagName);

		node.setAttribute(KEY_LABEL, query.getSummary());
		node.setAttribute(KEY_REPOSITORY_URL, query.getRepositoryUrl());
		node.setAttribute(KEY_QUERY, query.getUrl());

		for (AbstractTask hit : query.getHits()) {
			try {
				Element element = null;
				if (element == null) {
					createQueryHitElement(hit, doc, node);
				}
			} catch (Exception e) {
				MylarStatusHandler.log(e, e.getMessage());
			}
		}
		parent.appendChild(node);
		return node;
	}

// @Override
// public String getQueryHitTagName() {
// return KEY_TRAC_QUERY_HIT;
// }

	@Override
	public String getQueryTagNameForElement(AbstractRepositoryQuery query) {
		if (query instanceof TracRepositoryQuery) {
			return KEY_TRAC_QUERY;
		}
		return "";
	}

	@Override
	public AbstractRepositoryQuery readQuery(Node node, TaskList taskList) throws TaskExternalizationException {
		Element element = (Element) node;
		String repositoryUrl;
		String queryUrl;
		String label;
		if (element.hasAttribute(KEY_REPOSITORY_URL)) {
			repositoryUrl = element.getAttribute(KEY_REPOSITORY_URL);
		} else {
			throw new TaskExternalizationException("Repository URL not stored for task");
		}
		if (element.hasAttribute(KEY_QUERY)) {
			queryUrl = element.getAttribute(KEY_QUERY);
		} else {
			throw new TaskExternalizationException("Query URL not stored for task");
		}
		if (element.hasAttribute(KEY_LABEL)) {
			label = element.getAttribute(KEY_LABEL);
		} else {
			throw new TaskExternalizationException("Description not stored for task");
		}

		return new TracRepositoryQuery(repositoryUrl, queryUrl, label);
	}

// @Override
// public AbstractQueryHit createQueryHit(String repositoryUrl, String taskId,
// String summary, Element element, TaskList taskList, AbstractRepositoryQuery
// query)
// throws TaskExternalizationException {
// return new TracQueryHit(taskList, repositoryUrl, summary, taskId);
// }

}
