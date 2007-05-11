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

package org.eclipse.mylar.internal.tasks.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskComment;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Shawn Minto
 */
public class PersonProposalProvider implements IContentProposalProvider {

	private AbstractRepositoryTask currentTask;

	private RepositoryTaskData currentTaskData;

	private String currentUser;

	public PersonProposalProvider(AbstractRepositoryTask repositoryTask, RepositoryTaskData taskData) {
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
		Set<String> addressSet = new TreeSet<String>(new Comparator<String>() {
			public int compare(String s1, String s2) {
				if (currentUser != null) {
					if(s1.compareToIgnoreCase(s2) == 0 && currentUser.compareToIgnoreCase(s1) == 0){
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

		if(currentTask != null){
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					currentTask.getRepositoryKind(), currentTask.getRepositoryUrl());
			if (repository != null) {
				currentUser = repository.getUserName();
				if (currentUser != null && !repository.isAnonymous())
					addressSet.add(currentUser);
			}
		
			Set<AbstractRepositoryTask> tasks = new HashSet<AbstractRepositoryTask>();
			tasks.add(currentTask);

			Collection<ITask> allTasks = TasksUiPlugin.getTaskListManager().getTaskList().getAllTasks();
			for (ITask task : allTasks) {
				if (task instanceof AbstractRepositoryTask) {
					AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
					if (repositoryTask.getRepositoryUrl().equals(currentTask.getRepositoryUrl())) {
						tasks.add(repositoryTask);
					}
				}
			}

			for (AbstractRepositoryTask task : tasks) {
				addEmailAddresses(task, addressSet);
			}
		}
		
		if (currentTaskData != null) {
			java.util.List<TaskComment> comments = currentTaskData.getComments();
			for (TaskComment comment : comments) {
				addressSet.add(comment.getAuthor());
			}
		}

		return addressSet;
	}

	private void addEmailAddresses(AbstractRepositoryTask task, Set<String> addressSet) {
		// TODO: Owner, Creator, and CC should be stored on
		// AbstractRepositoryTask
		// RepositoryTaskData data =
		// TasksUiPlugin.getDefault().getTaskData(task);
		if (task.getOwner() != null) {
			addressSet.add(task.getOwner());
		}
		// if (data != null) {
		// addressSet.add(data.getAssignedTo());
		// addressSet.addAll(data.getCC());
		// }
	}
}
