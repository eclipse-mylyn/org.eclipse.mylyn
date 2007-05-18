/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.TracCorePlugin;
import org.eclipse.mylar.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylar.internal.trac.core.TracRepositoryQuery;
import org.eclipse.mylar.internal.trac.core.TracTask;
import org.eclipse.mylar.internal.trac.core.TracTask.Kind;
import org.eclipse.mylar.internal.trac.ui.wizard.EditTracQueryWizard;
import org.eclipse.mylar.internal.trac.ui.wizard.NewTracQueryWizard;
import org.eclipse.mylar.internal.trac.ui.wizard.TracCustomQueryPage;
import org.eclipse.mylar.internal.trac.ui.wizard.TracRepositorySettingsPage;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylar.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylar.tasks.ui.wizards.NewWebTaskWizard;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class TracRepositoryUi extends AbstractRepositoryConnectorUi {

	@Override
	public IHyperlink[] findHyperlinks(TaskRepository repository, String text, int lineOffset, int regionOffset) {
		return TracHyperlinkUtil.findHyperlinks(repository, text, lineOffset, regionOffset);
	}

	public String getTaskKindLabel(AbstractRepositoryTask repositoryTask) {
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
	public boolean hasRichEditor() {
		return true;
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
	public String getRepositoryType() {
		return TracCorePlugin.REPOSITORY_KIND;
	}

	@Override
	public ImageDescriptor getTaskKindOverlay(AbstractRepositoryTask task) {
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
	public List<ITaskListElement> getLegendItems() {
		List<ITaskListElement> legendItems = new ArrayList<ITaskListElement>();
		
		TracTask defect = new TracTask("", Kind.DEFECT.name(), Kind.DEFECT.toString(), false);
		defect.setKind(Kind.DEFECT.toString());		
		legendItems.add(defect);

		TracTask enhancement = new TracTask("", Kind.ENHANCEMENT.name(), Kind.ENHANCEMENT.toString(), false);
		enhancement.setKind(Kind.ENHANCEMENT.toString());		
		legendItems.add(enhancement);

		TracTask task = new TracTask("", Kind.TASK.name(), Kind.TASK.toString(), false);
		task.setKind(Kind.TASK.toString());		
		legendItems.add(task);
		
		return legendItems;
	}

}
