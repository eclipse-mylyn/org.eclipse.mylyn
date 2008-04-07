/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.web.core.AuthenticationCredentials;
import org.eclipse.mylyn.web.core.AuthenticationType;

/**
 * @author Shawn Minto
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
public class PersonProposalProvider implements IContentProposalProvider {

	private final AbstractTask currentTask;

	private final RepositoryTaskData currentTaskData;

	private String currentUser;

	private SortedSet<String> addressSet = null;

	private String repositoryUrl;

	private String connectorKind;

	public PersonProposalProvider(AbstractTask task, RepositoryTaskData taskData) {
		this.currentTask = task;
		this.currentTaskData = taskData;
		if (task != null) {
			repositoryUrl = task.getRepositoryUrl();
			connectorKind = task.getConnectorKind();
		} else if (taskData != null) {
			repositoryUrl = taskData.getRepositoryUrl();
			connectorKind = taskData.getConnectorKind();
		}
	}

	public PersonProposalProvider(String repositoryUrl, String repositoryKind) {
		this.currentTask = null;
		this.currentTaskData = null;
		this.repositoryUrl = repositoryUrl;
		this.connectorKind = repositoryKind;
	}

	public IContentProposal[] getProposals(String contents, int position) {
		if (contents == null) {
			throw new IllegalArgumentException();
		}

		int leftSeparator = getIndexOfLeftSeparator(contents, position);
		int rightSeparator = getIndexOfRightSeparator(contents, position);

		assert leftSeparator <= position;
		assert position <= rightSeparator;

		String searchText = contents.substring(leftSeparator + 1, position);
		String resultPrefix = contents.substring(0, leftSeparator + 1);
		String resultPostfix = contents.substring(rightSeparator);

		// retrieve subset of the tree set using key range
		SortedSet<String> addressSet = getAddressSet();
		if (!searchText.equals("")) {
			searchText = searchText.toLowerCase();
			char[] nextWord = searchText.toCharArray();
			nextWord[searchText.length() - 1]++;
			addressSet = addressSet.subSet(searchText, new String(nextWord));
		}

		IContentProposal[] result = new IContentProposal[addressSet.size()];
		int i = 0;
		for (final String address : addressSet) {
			result[i++] = new PersonContentProposal(address, address.equalsIgnoreCase(currentUser), resultPrefix
					+ address + resultPostfix, resultPrefix.length() + address.length());
		}
		Arrays.sort(result);
		return result;
	}

	private int getIndexOfLeftSeparator(String contents, int position) {
		int i = contents.lastIndexOf(' ', position - 1);
		i = Math.max(contents.lastIndexOf(',', position - 1), i);
		return i;
	}

	private int getIndexOfRightSeparator(String contents, int position) {
		int index = contents.length();
		int i = contents.indexOf(' ', position);
		if (i != -1) {
			index = Math.min(i, index);
		}
		i = contents.indexOf(',', position);
		if (i != -1) {
			index = Math.min(i, index);
		}
		return index;
	}

	private SortedSet<String> getAddressSet() {
		if (addressSet != null) {
			return addressSet;
		}

		addressSet = new TreeSet<String>(new Comparator<String>() {
			public int compare(String s1, String s2) {
				return s1.compareToIgnoreCase(s2);
			}
		});

		if (currentTask != null) {
			addAddress(currentTask.getOwner(), addressSet);
		}

		if (currentTaskData != null) {
			addAddresses(currentTaskData, addressSet);
		}

		if (repositoryUrl != null && connectorKind != null) {
			Set<AbstractTask> tasks = new HashSet<AbstractTask>();
			if (currentTask != null) {
				tasks.add(currentTask);
			}

			TaskRepository repository = TasksUiPlugin.getRepositoryManager()
					.getRepository(connectorKind, repositoryUrl);

			if (repository != null) {
				AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
				if (credentials != null && credentials.getUserName().length() > 0) {
					addressSet.add(credentials.getUserName());
				}
			}

			Collection<AbstractTask> allTasks = TasksUiPlugin.getTaskListManager().getTaskList().getAllTasks();
			for (AbstractTask task : allTasks) {
				if (repositoryUrl.equals(task.getRepositoryUrl())) {
					tasks.add(task);
				}
			}

			for (AbstractTask task : tasks) {
				addAddresses(task, addressSet);
			}
		}

		return addressSet;
	}

	private void addAddresses(AbstractTask task, Set<String> addressSet) {
		// TODO: Creator, and CC should be stored on AbstractTask

		addAddress(task.getOwner(), addressSet);
	}

	private void addAddresses(RepositoryTaskData data, Set<String> addressSet) {
		// addressSet.add(data.getAssignedTo());  // owner
		addAddress(data.getReporter(), addressSet); // ??
		for (String address : data.getCc()) {
			addAddress(address, addressSet);
		}
		for (TaskComment comment : currentTaskData.getComments()) {
			addAddress(comment.getAuthor(), addressSet);
		}
		for (RepositoryAttachment attachment : currentTaskData.getAttachments()) {
			addAddress(attachment.getCreator(), addressSet);
		}
	}

	private void addAddress(String address, Set<String> addressSet) {
		if (address != null && address.trim().length() > 0) {
			addressSet.add(address.trim());
		}
	}

}
