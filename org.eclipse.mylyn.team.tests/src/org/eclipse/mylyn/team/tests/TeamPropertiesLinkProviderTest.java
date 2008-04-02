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
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.team.core.diff.IDiff;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;

/**
 * @author Steffen Pingel
 */
public class TeamPropertiesLinkProviderTest extends TestCase {

	private IProject project1;

	private List<IResource> resources;

	private IProject project2;

	@Override
	protected void setUp() throws Exception {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		project1 = root.getProject("Test Project1");
		project1.create(null);
		project1.open(null);

		project2 = root.getProject("Test Project2");
		project2.create(null);
		project2.open(null);

		resources = new ArrayList<IResource>();
	}

	@Override
	protected void tearDown() throws Exception {
		project1.delete(true, null);
		project2.delete(true, null);
	}

	public void testCommitCommentTemplate() throws Exception {
		MockTask task = new MockTask("1");
		task.setSummary("summary");
		task.setUrl("http://url");

		ContextChangeSet changeSet = new ContextChangeSet(task, new StubChangeSetManager()) {
			@Override
			public IResource[] getChangedResources() {
				return resources.toArray(new IResource[0]);
			}
		};
		resources.add(project1);

		FocusedTeamUiPlugin.getDefault().getPreferenceStore().setValue(FocusedTeamUiPlugin.COMMIT_TEMPLATE,
				"${task.key}: ${task.description}");
		assertEquals("1: summary", changeSet.getComment());

		TeamPropertiesLinkProvider linkProvider = new TeamPropertiesLinkProvider();
		assertNull(linkProvider.getCommitCommentTemplate(project1));
		assertTrue(linkProvider.canAccessProperties(project1));

		assertTrue(linkProvider.setCommitCommentTemplate(project1, "ab${task.url}cd"));
		assertEquals("ab${task.url}cd", linkProvider.getCommitCommentTemplate(project1));
		assertEquals("abhttp://urlcd", changeSet.getComment());
		assertTrue(linkProvider.canAccessProperties(project1));

		// create file
		IFile file = project1.getFile("file");
		file.create(new ByteArrayInputStream(new byte[0]), true, null);
		resources.clear();
		resources.add(file);

		assertEquals("ab${task.url}cd", linkProvider.getCommitCommentTemplate(file));
		assertEquals("abhttp://urlcd", changeSet.getComment());
		assertTrue(linkProvider.canAccessProperties(file));

		linkProvider.setCommitCommentTemplate(file, null);
		assertNull(linkProvider.getCommitCommentTemplate(file));
		assertNull(linkProvider.getCommitCommentTemplate(project1));
		assertTrue(linkProvider.canAccessProperties(file));
		assertTrue(linkProvider.canAccessProperties(project1));
	}

	public void testChangeSetCommitCommentMultipleProjects() throws Exception {
		MockTask task = new MockTask("1");
		task.setSummary("summary");
		task.setUrl("http://url");

		ContextChangeSet changeSet = new ContextChangeSet(task, new StubChangeSetManager()) {
			@Override
			public IResource[] getChangedResources() {
				return resources.toArray(new IResource[0]);
			}
		};

		resources.add(project1);

		FocusedTeamUiPlugin.getDefault().getPreferenceStore().setValue(FocusedTeamUiPlugin.COMMIT_TEMPLATE,
				"global template: ${task.key}");

		// only set template on project 2
		TeamPropertiesLinkProvider linkProvider = new TeamPropertiesLinkProvider();
		assertTrue(linkProvider.setCommitCommentTemplate(project2, "project template: ${task.key}"));

		resources.add(project1);
		assertEquals("global template: 1", changeSet.getComment());

		resources.add(project2);
		assertEquals("project template: 1", changeSet.getComment());
	}

	public void testChangeSetCommitCommentChangedResources() throws Exception {
		MockTask task = new MockTask("1");
		task.setSummary("summary");
		task.setUrl("http://url");

		ContextChangeSet changeSet = new ContextChangeSet(task, new StubChangeSetManager()) {
			@Override
			public IResource[] getResources() {
				return new IResource[] { project1, project2 };
			}

			@Override
			public IResource[] getChangedResources() {
				return new IResource[] { project2 };
			}

		};

		FocusedTeamUiPlugin.getDefault().getPreferenceStore().setValue(FocusedTeamUiPlugin.COMMIT_TEMPLATE, "global");

		// only the template project 2 should matter
		TeamPropertiesLinkProvider linkProvider = new TeamPropertiesLinkProvider();
		assertTrue(linkProvider.setCommitCommentTemplate(project1, "project1"));
		assertEquals("global", changeSet.getComment());

		assertTrue(linkProvider.setCommitCommentTemplate(project2, "project2"));
		assertEquals("project2", changeSet.getComment());
	}

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
