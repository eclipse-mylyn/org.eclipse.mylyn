/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gitlab.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.gitlab.core.GitlabConfiguration;
import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator;
import org.eclipse.mylyn.gitlab.core.GitlabRepositoryConnector;
import org.eclipse.mylyn.gitlab.core.GitlabTaskAttributeMapper;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.QueryPageDetails;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;

public class GitlabConnectorUI extends AbstractRepositoryConnectorUi {

	@Override
	public String getConnectorKind() {
		return GitlabCoreActivator.CONNECTOR_KIND;
	}

	@Override
	public ITaskRepositoryPage getSettingsPage(TaskRepository repository) {
		return new GitlabRepositorySettingsPage("New repository", "Enter the HTTPS-URL to your GitLab repository",
				repository);
	}

	@Override
	public IWizard getQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		RepositoryQueryWizard wizard = new RepositoryQueryWizard(repository);
		GitlabRepositoryConnector connector = (GitlabRepositoryConnector) getConnector();
		TaskData taskData = new TaskData(
				new GitlabTaskAttributeMapper(repository, connector),
				repository.getConnectorKind(), "Query", "Query"); //$NON-NLS-1$ //$NON-NLS-2$
		GitlabSearchQueryPageSchema.getInstance().initialize(taskData);
		try {
			GitlabConfiguration config = connector.getRepositoryConfiguration(repository);
			config.updateQueryOptions(taskData);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GitlabQuerySchemaPage mp = new GitlabQuerySchemaPage(getConnectorKind(), repository, query,
				GitlabSearchQueryPageSchema.getInstance(), taskData,
				new QueryPageDetails(true, "", "EnterQueryParameter", //$NON-NLS-1$
						"EnterTitleAndURL", "([a-zA-Z][a-zA-Z+.-]{0,10}://[a-zA-Z0-9%._~!$&?#'()*+,;:@/=-]+)",
						null));
		wizard.addPage(mp);

		return wizard;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository repository, ITaskMapping selection) {
		return new NewTaskWizard(repository, selection);
	}

	@Override
	public boolean hasSearchPage() {
		// TODO Auto-generated method stub
		return false;
	}

}
