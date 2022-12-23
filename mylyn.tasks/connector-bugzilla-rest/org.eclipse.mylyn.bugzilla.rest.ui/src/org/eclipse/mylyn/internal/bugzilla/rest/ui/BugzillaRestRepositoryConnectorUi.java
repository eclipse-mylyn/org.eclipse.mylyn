/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
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

package org.eclipse.mylyn.internal.bugzilla.rest.ui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestCore;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestTaskAttributeMapper;
import org.eclipse.mylyn.internal.provisional.bugzilla.rest.ui.BugzillaRestQueryTypeWizardPage;
import org.eclipse.mylyn.internal.provisional.bugzilla.rest.ui.BugzillaRestUiUtil;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentModel;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;

public class BugzillaRestRepositoryConnectorUi extends AbstractRepositoryConnectorUi {

	public BugzillaRestRepositoryConnectorUi() {
	}

	public BugzillaRestRepositoryConnectorUi(AbstractRepositoryConnector connector) {
		super(connector);
	}

	@Override
	public String getConnectorKind() {
		return BugzillaRestCore.CONNECTOR_KIND;
	}

	@Override
	public ITaskRepositoryPage getSettingsPage(TaskRepository repository) {
		return new BugzillaRestRepositorySettingsPage(repository, getConnector(), this);
	}

	@Override
	public IWizard getQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		RepositoryQueryWizard wizard = new RepositoryQueryWizard(repository);
		AbstractRepositoryConnector connector = getConnector();
		BugzillaRestConnector connectorREST = (BugzillaRestConnector) connector;

		TaskData taskData = new TaskData(new BugzillaRestTaskAttributeMapper(repository, connectorREST),
				repository.getConnectorKind(), "Query", "Query"); //$NON-NLS-1$ //$NON-NLS-2$

		if (query == null) {
			wizard.addPage(new BugzillaRestQueryTypeWizardPage(repository, connector));
		} else {
			if (isCustomQuery(query)) {
				wizard.addPage(BugzillaRestUiUtil.createBugzillaRestSearchPage(true, true, taskData, connectorREST,
						repository, query));
			} else {
				wizard.addPage(BugzillaRestUiUtil.createBugzillaRestSearchPage(false, true, taskData, connectorREST,
						repository, query));
			}
		}
		return wizard;
	}

	private boolean isCustomQuery(IRepositoryQuery query2) {
		String custom = query2.getAttribute("SimpleURLQueryPage"); //$NON-NLS-1$
		return custom != null && custom.equals(Boolean.TRUE.toString());
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository repository, ITaskMapping selection) {
		return new NewTaskWizard(repository, selection);
	}

	@Override
	public boolean hasSearchPage() {
		return true;
	}

	@Override
	public IWizardPage getTaskAttachmentPage(TaskAttachmentModel model) {
		return new BugzillaRestTaskAttachmentPage(model);
	}

}
