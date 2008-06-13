/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector.TaskKind;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.ui.wizard.TracQueryPage;
import org.eclipse.mylyn.internal.trac.ui.wizard.TracRepositorySettingsPage;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.LegendElement;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskSearchPage;
import org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.ui.wizards.NewWebTaskWizard;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class TracConnectorUi extends AbstractRepositoryConnectorUi {

	@SuppressWarnings("restriction")
	public TracConnectorUi() {
		org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin.getDefault().addSearchHandler(new TracSearchHandler());
	}

	@Override
	public IHyperlink[] findHyperlinks(TaskRepository repository, String text, int lineOffset, int regionOffset) {
		return TracHyperlinkUtil.findTicketHyperlinks(repository, text, lineOffset, regionOffset);
	}

	@Override
	public String getTaskKindLabel(ITask repositoryTask) {
		return "Ticket";
	}

	@Override
	public ITaskRepositoryPage getSettingsPage(TaskRepository taskRepository) {
		return new TracRepositorySettingsPage(taskRepository);
	}

	@Override
	public ITaskSearchPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		return new TracQueryPage(repository);
	}

	@Override
	public boolean hasSearchPage() {
		return true;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository repository, ITaskMapping selection) {
		if (TracRepositoryConnector.hasRichEditor(repository)) {
			return new NewTaskWizard(repository, selection);
		} else {
			return new NewWebTaskWizard(repository, repository.getRepositoryUrl() + ITracClient.NEW_TICKET_URL,
					selection);
		}
	}

	@Override
	public IWizard getQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		RepositoryQueryWizard wizard = new RepositoryQueryWizard(repository);
		wizard.addPage(new TracQueryPage(repository, query));
		return wizard;
	}

	@Override
	public String getConnectorKind() {
		return TracCorePlugin.CONNECTOR_KIND;
	}

	@Override
	public ImageDescriptor getTaskKindOverlay(ITask task) {
		TaskKind taskKind = TaskKind.fromString(task.getTaskKind());
		if (taskKind == TaskKind.DEFECT) {
			return TracImages.OVERLAY_DEFECT;
		} else if (taskKind == TaskKind.ENHANCEMENT) {
			return TracImages.OVERLAY_ENHANCEMENT;
		} else if (taskKind == TaskKind.TASK) {
			return null;
		}
		return super.getTaskKindOverlay(task);
	}

	@Override
	public List<LegendElement> getLegendElements() {
		List<LegendElement> legendItems = new ArrayList<LegendElement>();
		legendItems.add(LegendElement.createTask(TaskKind.DEFECT.toString(), TracImages.OVERLAY_DEFECT));
		legendItems.add(LegendElement.createTask(TaskKind.ENHANCEMENT.toString(), TracImages.OVERLAY_ENHANCEMENT));
		legendItems.add(LegendElement.createTask(TaskKind.TASK.toString(), null));
		return legendItems;
	}

}
