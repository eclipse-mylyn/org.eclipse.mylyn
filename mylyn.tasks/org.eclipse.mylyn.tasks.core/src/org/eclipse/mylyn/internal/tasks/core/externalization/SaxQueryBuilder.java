/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.ITransferList;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.xml.sax.Attributes;

public class SaxQueryBuilder extends SaxTaskListElementBuilder<RepositoryQuery> {

	private RepositoryQuery query;

	private final RepositoryModel repositoryModel;

	private final IRepositoryManager repositoryManager;

	public SaxQueryBuilder(RepositoryModel repositoryModel, IRepositoryManager repositoryManager) {
		this.repositoryModel = repositoryModel;
		this.repositoryManager = repositoryManager;
	}

	@Override
	public void beginItem(Attributes elementAttributes) {
		try {
			String repositoryUrl = StringUtils
					.defaultString(elementAttributes.getValue(TaskListExternalizationConstants.KEY_REPOSITORY_URL));
			String connectorKind = StringUtils
					.defaultString(elementAttributes.getValue(TaskListExternalizationConstants.KEY_CONNECTOR_KIND));

			if (repositoryManager.getRepositoryConnector(connectorKind) == null) {
				addError(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
						String.format("Unable to read query, missing connector with kind \"%s\"", connectorKind))); //$NON-NLS-1$
				return;
			}

			query = readDefaultQuery(connectorKind, repositoryUrl);

			readQueryAttributes(elementAttributes);
		} catch (Exception e) {
			addError(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
					String.format("Exception reading query: %s", e.getMessage()), e)); //$NON-NLS-1$
		}
	}

	private RepositoryQuery readDefaultQuery(String connectorKind, String repositoryUrl) {
		TaskRepository taskRepository = repositoryModel.getTaskRepository(connectorKind, repositoryUrl);
		RepositoryQuery query = (RepositoryQuery) repositoryModel.createRepositoryQuery(taskRepository);
		return query;
	}

	private void readQueryAttributes(Attributes elementAttributes) {
		String handle = elementAttributes.getValue(TaskListExternalizationConstants.KEY_HANDLE);
		if (StringUtils.isNotEmpty(handle)) {
			query.setHandleIdentifier(handle);
		}

		String label = elementAttributes.getValue(TaskListExternalizationConstants.KEY_NAME);
		if (StringUtils.isEmpty(label)) { // fall back for legacy
			label = StringUtils.defaultString(elementAttributes.getValue(TaskListExternalizationConstants.KEY_LABEL));
		}
		query.setSummary(label);

		String queryString = StringUtils
				.defaultString(elementAttributes.getValue(TaskListExternalizationConstants.KEY_QUERY_STRING));
		if (StringUtils.isEmpty(queryString)) { // fall back for legacy
			queryString = StringUtils
					.defaultString(elementAttributes.getValue(TaskListExternalizationConstants.KEY_QUERY));
		}
		query.setUrl(queryString);

		String lastRefresh = elementAttributes.getValue(TaskListExternalizationConstants.KEY_LAST_REFRESH);
		if (StringUtils.isNotEmpty(lastRefresh)) {
			query.setLastSynchronizedStamp(lastRefresh);
		}
	}

	@Override
	protected void applyAttribute(String attributeKey, String attributeValue) {
		getItem().setAttribute(attributeKey, attributeValue);
	}

	@Override
	public RepositoryQuery getItem() {
		return query;
	}

	@Override
	public void addToTaskList(ITransferList taskList) {
		taskList.addQuery(query);
	}

}
