/*********************************************************************
 * Copyright (c) 2010, 2014 Sony Ericsson/ST Ericsson and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
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
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritConfigX;
import org.eclipse.mylyn.internal.gerrit.ui.wizards.GerritCustomQueryPage;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.tasks.ui.TasksUi;
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
 * @author Miles Parker
 */
public class GerritConnectorUi extends AbstractRepositoryConnectorUi {

	private static final Pattern PATTERN_CHANGE_ID = Pattern.compile("(?:\\W||^)(I[0-9a-f]{8}([0-9a-f]{32})?)"); //$NON-NLS-1$

	private final GerritConnector connector;

	public GerritConnectorUi() {
		connector = (GerritConnector) TasksUi.getRepositoryConnector(GerritConnector.CONNECTOR_KIND);
	}

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
		return Messages.GerritConnectorUi_Change;
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
			String key = matcher.group(1);
			if (task != null && task.getTaskKey() != null && key.startsWith(task.getTaskKey())) {
				continue;
			}
			if (links == null) {
				links = new ArrayList<IHyperlink>();
			}
			int start = matcher.start(1);
			Region region = new Region(textOffset + start, matcher.end(1) - start);
			links.add(new TaskHyperlink(region, repository, key));
		}
		GerritConfiguration configuration = connector.getConfiguration(repository);
		if (configuration != null) {
			GerritConfigX config = configuration.getGerritConfig();
			if (config != null) {
				GerritCommentLinkDetector detector = new GerritCommentLinkDetector(repository, config);
				List<IHyperlink> commentLinks = detector.findHyperlinks(text, index, textOffset);
				if (commentLinks != null) {
					if (links == null) {
						links = new ArrayList<IHyperlink>();
					}
					links.addAll(commentLinks);
				}
			}
		}
		return links != null ? links.toArray(new IHyperlink[links.size()]) : null;
	}
}
