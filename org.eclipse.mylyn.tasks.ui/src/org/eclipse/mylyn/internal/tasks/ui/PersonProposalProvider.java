/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Shawn Minto
 */
public class PersonProposalProvider implements IContentProposalProvider {

	private AbstractTask currentTask;

	private RepositoryTaskData currentTaskData;

	private String currentUser;
	
	private Set<String> addressSet = null;

	public PersonProposalProvider(AbstractTask repositoryTask, RepositoryTaskData taskData) {
		this.currentTask = repositoryTask;
		this.currentTaskData = taskData;
	}

	public IContentProposal[] getProposals(String contents, int position) {
		ArrayList<IContentProposal> result = new ArrayList<IContentProposal>();

		contents = contents.substring(0, position);

		Set<String> addressSet = getAddressSet();
		for (final String address : addressSet) {
			if (address.startsWith(contents)) {
				result.add(new PersonContentProposal(address, address.compareToIgnoreCase(currentUser) == 0));
			}
		}

		return result.toArray(new IContentProposal[result.size()]);
	}

	private Set<String> getAddressSet() {
		if(addressSet!=null) {
			return addressSet;
		}
		
		addressSet = new TreeSet<String>(new Comparator<String>() {
			public int compare(String s1, String s2) {
				if (currentUser != null) {
					if (s1.compareToIgnoreCase(s2) == 0 && currentUser.compareToIgnoreCase(s1) == 0) {
						return 0;
					} else if (currentUser.compareToIgnoreCase(s1) == 0) {
						return -1;
					} else if (currentUser.compareToIgnoreCase(s2) == 0) {
						return 1;
					}
				}
				return s1.compareToIgnoreCase(s2);
			}
		});

		if(currentTask!=null) {
			addAddress(currentTask.getOwner(), addressSet);
		}
		addAddresses(currentTaskData, addressSet);

		String repositoryUrl = null;
		String repositoryKind = null;

		if (currentTask != null) {
			repositoryUrl = currentTask.getRepositoryUrl();
			repositoryKind = currentTask.getRepositoryKind();
		}

		if (repositoryUrl == null || repositoryKind == null) {
			if (currentTaskData != null) {
				repositoryUrl = currentTaskData.getRepositoryUrl();
				repositoryKind = currentTaskData.getRepositoryKind();
			}
		}

		if (repositoryUrl != null && repositoryKind != null) {
			Set<AbstractTask> tasks = new HashSet<AbstractTask>();
			if (currentTask != null) {
				tasks.add(currentTask);
			}

			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(repositoryKind,
					repositoryUrl);

			if (repository != null) {
				currentUser = repository.getUserName();
				if (currentUser != null && !repository.isAnonymous())
					addressSet.add(currentUser);
			}

			Collection<AbstractTask> allTasks = TasksUiPlugin.getTaskListManager().getTaskList().getAllTasks();
			for (AbstractTask task : allTasks) {
				if (task instanceof AbstractTask) {
					AbstractTask repositoryTask = (AbstractTask) task;
					if (repositoryTask.getRepositoryUrl().equals(repositoryUrl)) {
						tasks.add(repositoryTask);
					}
				}
			}

			for (AbstractTask task : tasks) {
				addAddresses(task, addressSet);
			}
		}

		return addressSet;
	}

	private void addAddresses(AbstractTask task, Set<String> addressSet) {
		// TODO: Owner, Creator, and CC should be stored on
		// AbstractTask
		// RepositoryTaskData data =
		// TasksUiPlugin.getDefault().getTaskData(task);

		addAddress(task.getOwner(), addressSet);

		RepositoryTaskData data = TasksUiPlugin.getDefault().getTaskDataManager() //
				.getOldTaskData(task.getHandleIdentifier());
		addAddresses(data, addressSet);
	}

	private void addAddresses(RepositoryTaskData data, Set<String> addressSet) {
		if (data != null) {
			// addressSet.add(data.getAssignedTo());  // owner
			addAddress(data.getReporter(), addressSet); // ??
			for (String address : data.getCC()) {
				addAddress(address, addressSet);
			}
			for (TaskComment comment : currentTaskData.getComments()) {
				addAddress(comment.getAuthor(), addressSet);
			}
			for (RepositoryAttachment attachment : currentTaskData.getAttachments()) {
				addAddress(attachment.getCreator(), addressSet);
			}
		}
	}

	private void addAddress(String address, Set<String> addressSet) {
		if (address != null && address.trim().length() > 0) {
			addressSet.add(address.trim());
		}
	}
	
}
