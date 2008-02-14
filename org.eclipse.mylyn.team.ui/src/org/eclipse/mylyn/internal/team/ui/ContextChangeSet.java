/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.team.ui.properties.TeamPropertiesLinkProvider;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.resources.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ILinkedTaskInfo;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.diff.IDiff;
import org.eclipse.team.core.diff.provider.ThreeWayDiff;
import org.eclipse.team.core.mapping.provider.ResourceDiff;
import org.eclipse.team.internal.ccvs.core.mapping.CVSActiveChangeSet;
import org.eclipse.team.internal.ccvs.core.mapping.ChangeSetResourceMapping;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.Preferences;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class ContextChangeSet extends CVSActiveChangeSet/*ActiveChangeSet*/implements IAdaptable {

	// HACK: copied from super
	private static final String CTX_TITLE = "title";

	public static final String SOURCE_ID = "org.eclipse.mylyn.java.context.changeset.add";

	private boolean suppressInterestContribution = false;

	private AbstractTask task;

	public ContextChangeSet(AbstractTask task, ActiveChangeSetManager manager) {
		super(manager, task.getSummary());
		this.task = task;
		initTitle();
	}

	@Override
	public boolean isUserCreated() {
		return true;
	}

	public void initTitle() {
		super.setName(task.getSummary());
		super.setTitle(task.getSummary());
	}

	/**
	 * Encodes the handle in the title, since init won't get called on this class.
	 */
	@Override
	public void save(Preferences prefs) {
		super.save(prefs);
		prefs.put(CTX_TITLE, getTitleForPersistance());
	}

	private String getTitleForPersistance() {
		return getTitle() + " (" + task.getHandleIdentifier() + ")";
	}

	public static String getHandleFromPersistedTitle(String title) {
		int delimStart = title.lastIndexOf('(');
		int delimEnd = title.lastIndexOf(')');
		if (delimStart != -1 && delimEnd != -1) {
			return title.substring(delimStart + 1, delimEnd);
		} else {
			return null;
		}
	}

	@Override
	public String getComment() {
		return internalGetComment(true);
	}

	private String internalGetComment(boolean checkTaskRepository) {
		String template = null;
		Set<IProject> projects = new HashSet<IProject>();
		IResource[] resources = super.getResources();
		for (IResource resource : resources) {
			IProject project = resource.getProject();
			if (project != null && project.isAccessible() && !projects.contains(project)) {
				TeamPropertiesLinkProvider provider = new TeamPropertiesLinkProvider();
				template = provider.getCommitCommentTemplate(project);
				if (template != null) {
					break;
				}
				projects.add(project);
			}
		}

		boolean proceed = true;
		
		if (checkTaskRepository) {
			boolean unmatchedRepositoryFound = false; 
			for (IProject project : projects) {
				TaskRepository repository = TasksUiPlugin.getDefault().getRepositoryForResource(project, true);
				if (repository != null) {
					if (!repository.getUrl().equals(task.getRepositoryUrl())) {
						unmatchedRepositoryFound = true;
					}
				}
			}

			if (unmatchedRepositoryFound) {
				proceed = MessageDialog.openQuestion(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"Mylyn Change Set Management",
						"You are attempting to commit a resource which is not associated with the selected task repository.  Proceed with creating the commit message?");
			}
		}

		if (proceed) {
			if (template == null) {
				template = FocusedTeamUiPlugin.getDefault().getPreferenceStore().getString(
						FocusedTeamUiPlugin.COMMIT_TEMPLATE);
			}
			return FocusedTeamUiPlugin.getDefault().getCommitTemplateManager().generateComment(task, template);
		} else {
			return "";
		}
	}

	@Override
	public void remove(IResource resource) {
		super.remove(resource);
	}

	@Override
	public void remove(IResource[] newResources) {
		super.remove(newResources);
	}

	@Override
	public void add(IDiff diff) {
		super.add(diff);
		IResource resource = getResourceFromDiff(diff);
		if (!suppressInterestContribution && resource != null) {
			Set<IResource> resources = new HashSet<IResource>();
			resources.add(resource);
			if (ResourcesUiBridgePlugin.getDefault() != null) {
				ResourcesUiBridgePlugin.getInterestUpdater().addResourceToContext(resources,
						InteractionEvent.Kind.SELECTION);
			}
		}
	}

	private IResource getResourceFromDiff(IDiff diff) {
		if (diff instanceof ResourceDiff) {
			return ((ResourceDiff) diff).getResource();
		} else if (diff instanceof ThreeWayDiff) {
			ThreeWayDiff threeWayDiff = (ThreeWayDiff) diff;
			return ResourcesPlugin.getWorkspace().getRoot().findMember(threeWayDiff.getPath());
		} else {
			return null;
		}
	}

	@Override
	public void add(IDiff[] diffs) {
		super.add(diffs);
	}

	@Override
	public void add(IResource[] newResources) throws CoreException {
		super.add(newResources);
	}

	public void restoreResources(IResource[] newResources) throws CoreException {
		suppressInterestContribution = true;
		try {
			super.add(newResources);
			setComment(internalGetComment(false));
		} catch (TeamException e) {
			throw e;
		} finally {
			suppressInterestContribution = false;
		}
	}

	@Override
	public IResource[] getResources() {
		List<IResource> allResources = getAllResourcesInChangeContext();
		return allResources.toArray(new IResource[allResources.size()]);
	}

	public List<IResource> getAllResourcesInChangeContext() {
		Set<IResource> allResources = new HashSet<IResource>();
		allResources.addAll(Arrays.asList(super.getResources()));
		if (Platform.isRunning() && ResourcesUiBridgePlugin.getDefault() != null && task.isActive()) {
			// TODO: if super is always managed correctly should remove
			// following line
			allResources.addAll(ResourcesUiBridgePlugin.getDefault().getInterestingResources(
					ContextCorePlugin.getContextManager().getActiveContext()));
		}
		return new ArrayList<IResource>(allResources);
	}

	/**
	 * TODO: unnessary check context?
	 */
	@Override
	public boolean contains(IResource local) {
		return getAllResourcesInChangeContext().contains(local);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof ContextChangeSet && task != null) {
			ContextChangeSet changeSet = (ContextChangeSet) object;
			return task.equals(changeSet.getTask());
		} else {
			return super.equals(object);
		}
	}

	@Override
	public int hashCode() {
		if (task != null) {
			return task.hashCode();
		} else {
			return super.hashCode();
		}
	}

	public AbstractTask getTask() {
		return task;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == ResourceMapping.class) {
			return new ChangeSetResourceMapping(this);
		} else if (adapter == AbstractTask.class) {
			return task;
		} else if (adapter == ILinkedTaskInfo.class) {
			return new LinkedTaskInfo(getTask(), this);
		} else {
			return null;
		}
	}
}
