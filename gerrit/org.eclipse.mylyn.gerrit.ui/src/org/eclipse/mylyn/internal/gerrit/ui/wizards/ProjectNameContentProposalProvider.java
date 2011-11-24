/*******************************************************************************
 * Copyright (c) 2011 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sascha Scholz (SAP) - initial API and implementation
 *      Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;

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
			return "(Repository configuration needs to be refreshed)";
		}

		@Override
		public String getDescription() {
			// ignore
			return null;
		}

	}

	private final GerritClient client;

	public ProjectNameContentProposalProvider(GerritClient client) {
		this.client = client;
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		ArrayList<IContentProposal> proposals = new ArrayList<IContentProposal>();
		List<Project> projects = getProjects();
		if (projects != null) {
			for (Project project : projects) {
				String projectName = project.getName().trim();
				if (projectName.startsWith(contents) && !GerritUtil.isPermissionOnlyProject(client, projectName)) {
					proposals.add(new ProjectNameContentProposal(projectName));
				}
			}
			return proposals.toArray(new IContentProposal[] {});
		} else {
			// TODO Trigger refresh of repository configuration when query dialog is opened  
			return new IContentProposal[] { new MissingConfigurationContentProposal() };
		}
	}

	private List<Project> getProjects() {
		GerritConfiguration config = client.getConfiguration();
		if (config != null) {
			return config.getProjects();
		}
		return null;
	}

}
