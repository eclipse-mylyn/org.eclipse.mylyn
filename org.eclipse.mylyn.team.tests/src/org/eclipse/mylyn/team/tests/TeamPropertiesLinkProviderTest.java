/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.team.tests;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.team.ui.ContextChangeSet;
import org.eclipse.mylyn.internal.team.ui.FocusedTeamUiPlugin;
import org.eclipse.mylyn.internal.team.ui.properties.TeamPropertiesLinkProvider;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryTask;
import org.eclipse.team.core.diff.IDiff;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("restriction")
public class TeamPropertiesLinkProviderTest extends TestCase {

	private IProject project;
	private List<IResource> resources;

	@Override
	protected void setUp() throws Exception {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject("Test Project");
		project.create(null);
		project.open(null);
		
		resources = new ArrayList<IResource>();
	}

	@Override
	protected void tearDown() throws Exception {
		project.delete(true, null);
	}

	public void testCommitCommentTemplate() throws Exception {
		MockRepositoryTask task = new MockRepositoryTask("1");
		task.setSummary("summary");
		task.setUrl("http://url");

		ContextChangeSet changeSet = new ContextChangeSet(task, new StubChangeSetManager()) {
			@Override
			public IResource[] getResources() {
				return resources.toArray(new IResource[0]);
			}
		};
		resources.add(project);

		FocusedTeamUiPlugin.getDefault().getPreferenceStore().setValue(FocusedTeamUiPlugin.COMMIT_TEMPLATE,
				"${task.key}: ${task.description}");
		assertEquals("1: summary", changeSet.getComment());

		TeamPropertiesLinkProvider linkProvider = new TeamPropertiesLinkProvider();
		assertNull(linkProvider.getCommitCommentTemplate(project));
		assertTrue(linkProvider.canAccessProperties(project));

		assertTrue(linkProvider.setCommitCommentTemplate(project, "ab${task.url}cd"));
		assertEquals("ab${task.url}cd", linkProvider.getCommitCommentTemplate(project));
		assertEquals("abhttp://urlcd", changeSet.getComment());
		assertTrue(linkProvider.canAccessProperties(project));

		// create file
		IFile file = project.getFile("file");
		file.create(new ByteArrayInputStream(new byte[0]), true, null);
		resources.clear();
		resources.add(file);
		
		assertEquals("ab${task.url}cd", linkProvider.getCommitCommentTemplate(file));
		assertEquals("abhttp://urlcd", changeSet.getComment());
		assertTrue(linkProvider.canAccessProperties(file));

		linkProvider.setCommitCommentTemplate(file, null);
		assertNull(linkProvider.getCommitCommentTemplate(file));
		assertNull(linkProvider.getCommitCommentTemplate(project));
		assertTrue(linkProvider.canAccessProperties(file));
		assertTrue(linkProvider.canAccessProperties(project));
	}

	@SuppressWarnings("restriction")
	public class StubChangeSetManager extends ActiveChangeSetManager {

		@Override
		public IDiff getDiff(IResource resource) throws CoreException {
			return null;
		}

		@Override
		protected String getName() {
			return null;
		}

		@Override
		protected void initializeSets() {
		}

	}

}
