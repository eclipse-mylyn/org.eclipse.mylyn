/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryQuery;
import org.eclipse.mylyn.internal.trac.core.TracTask;
import org.eclipse.mylyn.internal.trac.core.TracTask.Kind;
import org.eclipse.mylyn.internal.trac.ui.wizard.EditTracQueryWizard;
import org.eclipse.mylyn.internal.trac.ui.wizard.NewTracQueryWizard;
import org.eclipse.mylyn.internal.trac.ui.wizard.TracCustomQueryPage;
import org.eclipse.mylyn.internal.trac.ui.wizard.TracRepositorySettingsPage;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.ui.wizards.NewWebTaskWizard;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class TracConnectorUi extends AbstractRepositoryConnectorUi {

	@Override
	public IHyperlink[] findHyperlinks(TaskRepository repository, String text, int lineOffset, int regionOffset) {
		return TracHyperlinkUtil.findHyperlinks(repository, text, lineOffset, regionOffset);
	}

	public String getTaskKindLabel(AbstractTask repositoryTask) {
		return "Ticket";
	}

	@Override
	public String getTaskKindLabel(RepositoryTaskData taskData) {
		return "Ticket";
	}

	@Override
	public AbstractRepositorySettingsPage getSettingsPage() {
		return new TracRepositorySettingsPage(this);
	}

	@Override
	public WizardPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		return new TracCustomQueryPage(repository);
	}

	@Override
	public boolean hasSearchPage() {
		return true;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository repository) {
		if (TracRepositoryConnector.hasRichEditor(repository)) {
			return new NewTaskWizard(repository);
		} else {
			return new NewWebTaskWizard(repository, repository.getUrl() + ITracClient.NEW_TICKET_URL);
		}
	}

	@Override
	public IWizard getQueryWizard(TaskRepository repository, AbstractRepositoryQuery query) {
		if (query instanceof TracRepositoryQuery) {
			return new EditTracQueryWizard(repository, query);
		} else {
			return new NewTracQueryWizard(repository);
		}
	}

	@Override
	public String getConnectorKind() {
		return TracCorePlugin.REPOSITORY_KIND;
	}

	@Override
	public ImageDescriptor getTaskKindOverlay(AbstractTask task) {
		Kind kind = Kind.fromString(task.getTaskKind());
		if (kind == Kind.DEFECT) {
			return TracImages.OVERLAY_DEFECT;
		} else if (kind == Kind.ENHANCEMENT) {
			return TracImages.OVERLAY_ENHANCEMENT;
		} else if (kind == Kind.TASK) {
			return null;
		}
		return super.getTaskKindOverlay(task);
	}

	@Override
	public List<AbstractTaskContainer> getLegendItems() {
		List<AbstractTaskContainer> legendItems = new ArrayList<AbstractTaskContainer>();
		
		TracTask defect = new TracTask("", Kind.DEFECT.name(), Kind.DEFECT.toString());
		defect.setTaskKind(Kind.DEFECT.toString());		
		legendItems.add(defect);

		TracTask enhancement = new TracTask("", Kind.ENHANCEMENT.name(), Kind.ENHANCEMENT.toString());
		enhancement.setTaskKind(Kind.ENHANCEMENT.toString());		
		legendItems.add(enhancement);

		TracTask task = new TracTask("", Kind.TASK.name(), Kind.TASK.toString());
		task.setTaskKind(Kind.TASK.toString());		
		legendItems.add(task);
		
		return legendItems;
	}

}
