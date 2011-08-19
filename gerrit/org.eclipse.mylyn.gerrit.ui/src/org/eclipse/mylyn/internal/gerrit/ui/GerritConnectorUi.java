/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Technologies - improvements
 *      GitHub Inc. - fixes for bug 355179      
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.ui.wizards.GerritCustomQueryPage;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskSearchPage;
import org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;

/**
 * Connector specific UI for Gerrit.
 * 
 * @author Mikael Kober
 * @author Thomas Westling
 * @author Steffen Pingel
 * @author Kevin Sawicki
 */
public class GerritConnectorUi extends AbstractRepositoryConnectorUi {

	private static final Pattern PATTERN_CHANGE_ID = Pattern.compile("(?:\\W||^)(Change-Id: (I[0-9a-f]{40}))"); //$NON-NLS-1$

	@Override
	public String getConnectorKind() {
		return GerritConnector.CONNECTOR_KIND;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository, ITaskMapping taskSelection) {
		return new NewTaskWizard(taskRepository, taskSelection);
	}

	@Override
	public IWizard getQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		RepositoryQueryWizard wizard = new RepositoryQueryWizard(repository);
		wizard.addPage(new GerritCustomQueryPage(repository, "GerritQueryPage", query)); //$NON-NLS-1$
		return wizard;
	}

	@Override
	public ITaskRepositoryPage getSettingsPage(TaskRepository taskRepository) {
		return new GerritRepositorySettingsPage(taskRepository);
	}

	@Override
	public ITaskSearchPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		return new GerritCustomQueryPage(repository, "GerritQueryPage", null); //$NON-NLS-1$
	}

	@Override
	public boolean hasSearchPage() {
		return true;
	}

	@Override
	public String getTaskKindLabel(ITask task) {
		return "Change";
	}

	@Override
	public ImageDescriptor getTaskKindOverlay(ITask task) {
		return GerritImages.OVERLAY_REVIEW;
	}

	@Override
	public IHyperlink[] findHyperlinks(final TaskRepository repository, ITask task, String text, int index,
			int textOffset) {
		List<IHyperlink> links = null;
		Matcher matcher = PATTERN_CHANGE_ID.matcher(text);
		while (matcher.find()) {
			if (index != -1 && (index < matcher.start() || index > matcher.end())) {
				continue;
			}
			if (links == null) {
				links = new ArrayList<IHyperlink>();
			}
			String key = matcher.group(2);
			if (task == null || !key.startsWith(task.getTaskKey())) {
				int start = matcher.start(1);
				Region region = new Region(textOffset + start, matcher.end(1) - start);
				links.add(new TaskHyperlink(region, repository, key));
			}
		}
		return links != null ? links.toArray(new IHyperlink[links.size()]) : null;
	}

}
