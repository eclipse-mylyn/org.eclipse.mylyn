/*******************************************************************************
 * Copyright (c) 2011, 2014 SAP and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Sascha Scholz (SAP) - initial API and implementation
 *      Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.tasks.core.TaskRepository;

import com.google.gerrit.reviewdb.Project;

/**
 * @author Sascha Scholz
 * @author Steffen Pingel
 */
public class ProjectNameContentProposalProvider implements IContentProposalProvider {

	private static class MissingConfigurationContentProposal implements IContentProposal {

		@Override
		public String getContent() {
			return ""; //$NON-NLS-1$
		}

		@Override
		public int getCursorPosition() {
			return 0;
		}

		@Override
		public String getLabel() {
			return Messages.ProjectNameContentProposalProvider_Repository_configuration_needs_to_be_refreshed;
		}

		@Override
		public String getDescription() {
			// ignore
			return null;
		}

	}

	private final TaskRepository repository;

	private final GerritConnector connector;

	public ProjectNameContentProposalProvider(GerritConnector connector, TaskRepository repository) {
		this.connector = connector;
		this.repository = repository;
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		String contentsLowerCase = contents.toLowerCase(Locale.ENGLISH);
		ArrayList<IContentProposal> proposals = new ArrayList<>();
		GerritClient client = connector.getClient(repository);
		List<Project> projects = getProjects(client);
		if (projects != null) {
			for (Project project : projects) {
				String projectName = project.getName();
				if (projectName.toLowerCase(Locale.ENGLISH).contains(contentsLowerCase)) {
					proposals.add(new ProjectNameContentProposal(projectName));
				}
			}
			return proposals.toArray(new IContentProposal[proposals.size()]);
		} else {
			return new IContentProposal[] { new MissingConfigurationContentProposal() };
		}
	}

	private List<Project> getProjects(GerritClient client) {
		GerritConfiguration config = client.getConfiguration();
		if (config != null) {
			return config.getProjects();
		}
		return null;
	}

}
