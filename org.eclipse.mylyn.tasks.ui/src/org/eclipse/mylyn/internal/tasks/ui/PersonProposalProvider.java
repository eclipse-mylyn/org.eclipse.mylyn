/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Eugene Kuleshov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;

/**
 * @author Shawn Minto
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 * @author David Shepherd
 * @author Sam Davis
 * @author Thomas Ehrnhoefer
 * @author Mike Wu
 */
public class PersonProposalProvider implements IContentProposalProvider {

	private final AbstractTask currentTask;

	private String currentUser;

	private SortedSet<String> addressSet;

	private String repositoryUrl;

	private String connectorKind;

	private TaskData currentTaskData;

	private final Map<String, String> proposals;

	private Map<String, String> errorProposals;

	public PersonProposalProvider(AbstractTask task, TaskData taskData) {
		this(task, taskData, new HashMap<String, String>(0));
	}

	public PersonProposalProvider(AbstractTask task, TaskData taskData, Map<String, String> proposals) {
		this.currentTask = task;
		this.currentTaskData = taskData;
		if (task != null) {
			repositoryUrl = task.getRepositoryUrl();
			connectorKind = task.getConnectorKind();
		} else if (taskData != null) {
			repositoryUrl = taskData.getRepositoryUrl();
			connectorKind = taskData.getConnectorKind();
		}
		this.proposals = new HashMap<String, String>(proposals);
	}

	public PersonProposalProvider(String repositoryUrl, String repositoryKind) {
		this(repositoryUrl, repositoryKind, new HashMap<String, String>(0));
	}

	public PersonProposalProvider(String repositoryUrl, String repositoryKind, Map<String, String> proposals) {
		this.currentTask = null;
		this.repositoryUrl = repositoryUrl;
		this.connectorKind = repositoryKind;
		this.proposals = new HashMap<String, String>(proposals);
	}

	protected String getRepositoryUrl() {
		return repositoryUrl;
	}

	protected String getConnectorKind() {
		return connectorKind;
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
		if (errorProposals == null || errorProposals.isEmpty()) {
			if (!searchText.equals("")) { //$NON-NLS-1$
				// lower bounds
				searchText = searchText.toLowerCase();

				// compute the upper bound
				char[] nextWord = searchText.toCharArray();
				nextWord[searchText.length() - 1]++;

				// filter matching keys 
				addressSet = new TreeSet<String>(addressSet.subSet(searchText, new String(nextWord)));

				// add matching keys based on pretty names 
				addMatchingProposalsByPrettyName(addressSet, searchText);
			}
		}

		IContentProposal[] result = new IContentProposal[addressSet.size()];
		int i = 0;
		for (final String address : addressSet) {
			result[i++] = createPersonProposal(address, address.equalsIgnoreCase(currentUser), resultPrefix + address
					+ resultPostfix, resultPrefix.length() + address.length());
		}
		Arrays.sort(result);
		return result;
	}

	private void addMatchingProposalsByPrettyName(SortedSet<String> addressSet, String searchText) {
		if (proposals.size() > 0) {
			for (Map.Entry<String, String> entry : proposals.entrySet()) {
				if (matchesSubstring(entry.getValue(), searchText)) {
					addressSet.add(entry.getKey());
				}
			}
		}
	}

	private boolean matchesSubstring(String value, String searchText) {
		if (value != null) {
			String tokens[] = value.split("\\s"); //$NON-NLS-1$
			for (String token : tokens) {
				if (token.toLowerCase().startsWith(searchText)) {
					return true;
				}
			}
		}
		return false;
	}

	protected PersonContentProposal createPersonProposal(String address, boolean isCurrentUser, String replacementText,
			int cursorPosition) {
		return new PersonContentProposal(getPrettyName(address), isCurrentUser, replacementText, cursorPosition);
	}

	protected String getPrettyName(String address) {
		String value = proposals.get(address);
		if (value != null) {
			return NLS.bind("{0} <{1}>", value, address); //$NON-NLS-1$
		}
		return address;
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

		if (errorProposals != null && !errorProposals.isEmpty()) {
			for (String proposal : errorProposals.keySet()) {
				addAddress(addressSet, proposal);
			}
			return addressSet;
		}

		if (proposals.size() > 0) {
			if (repositoryUrl != null && connectorKind != null) {
				currentUser = getCurrentUser(repositoryUrl, connectorKind);
			}
			for (String proposal : proposals.keySet()) {
				addAddress(addressSet, proposal);
			}
			return addressSet;
		}

		if (currentTask != null) {
			addAddress(addressSet, currentTask.getOwner());
		}

		if (currentTaskData != null) {
			addAddresses(currentTaskData, addressSet);
		}

		if (repositoryUrl != null && connectorKind != null) {
			Set<AbstractTask> tasks = new HashSet<AbstractTask>();
			if (currentTask != null) {
				tasks.add(currentTask);
			}

			currentUser = getCurrentUser(repositoryUrl, connectorKind);
			if (currentUser != null) {
				addressSet.add(currentUser);
			}

			Collection<AbstractTask> allTasks = TasksUiPlugin.getTaskList().getAllTasks();
			for (AbstractTask task : allTasks) {
				if (repositoryUrl.equals(task.getRepositoryUrl())) {
					tasks.add(task);
				}
			}

			for (ITask task : tasks) {
				addAddresses(task, addressSet);
			}
		}

		return addressSet;
	}

	private String getCurrentUser(String repositoryUrl, String connectorKind) {
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(connectorKind, repositoryUrl);

		if (repository != null) {
			AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
			if (credentials != null && credentials.getUserName().length() > 0) {
				return credentials.getUserName();
			}
		}
		return null;
	}

	private void addAddresses(ITask task, Set<String> addressSet) {
		addAddress(addressSet, task.getOwner());
	}

	private void addAddresses(TaskData data, Set<String> addressSet) {
		addPerson(data, addressSet, TaskAttribute.USER_REPORTER);
		addPerson(data, addressSet, TaskAttribute.USER_ASSIGNED);
		addPerson(data, addressSet, TaskAttribute.USER_CC);
		List<TaskAttribute> comments = data.getAttributeMapper().getAttributesByType(data, TaskAttribute.TYPE_COMMENT);
		for (TaskAttribute commentAttribute : comments) {
			addPerson(data, addressSet, commentAttribute);
		}
		List<TaskAttribute> attachments = data.getAttributeMapper().getAttributesByType(data,
				TaskAttribute.TYPE_ATTACHMENT);
		for (TaskAttribute attachmentAttribute : attachments) {
			addPerson(data, addressSet, attachmentAttribute);
		}
	}

	private void addPerson(TaskData data, Set<String> addresses, String key) {
		TaskAttribute attribute = data.getRoot().getMappedAttribute(key);
		// ignore modifiable attributes which may have a value edited by the user which may not be a valid proposal 
		if (attribute != null && attribute.getMetaData().isReadOnly()) {
			addPerson(data, addresses, attribute);
		}
	}

	private void addPerson(TaskData data, Set<String> addresses, TaskAttribute attribute) {
		TaskAttributeMetaData metaData = attribute.getMetaData();
		if (TaskAttribute.TYPE_COMMENT.equals(metaData.getType())) {
			TaskCommentMapper mapper = TaskCommentMapper.createFrom(attribute);
			addPerson(addresses, mapper.getAuthor());
		} else if (TaskAttribute.TYPE_ATTACHMENT.equals(metaData.getType())) {
			TaskAttachmentMapper mapper = TaskAttachmentMapper.createFrom(attribute);
			addPerson(addresses, mapper.getAuthor());
		} else if (TaskAttribute.TYPE_PERSON.equals(metaData.getType())) {
			addPerson(addresses, data.getAttributeMapper().getRepositoryPerson(attribute));
		} else {
			List<String> values = attribute.getValues();
			for (String value : values) {
				addAddress(addresses, value);
			}
		}
	}

	private void addPerson(Set<String> addresses, IRepositoryPerson repositoryPerson) {
		if (repositoryPerson != null) {
			addresses.add(repositoryPerson.getPersonId());
			if (repositoryPerson.getName() != null) {
				proposals.put(repositoryPerson.getPersonId(), repositoryPerson.getName());
			}
		}
	}

	private void addAddress(Set<String> addresses, String address) {
		if (address != null && address.trim().length() > 0) {
			addresses.add(address.trim());
		}
	}

	public Map<String, String> getProposals() {
		return proposals;
	}

	public Map<String, String> getErrorProposals() {
		return errorProposals;
	}

	public void setErrorProposals(Map<String, String> errorProposals) {
		this.errorProposals = errorProposals;
		addressSet = null;
	}
}
