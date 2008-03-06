/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class RepositoryCompletionProcessor implements IContentAssistProcessor {

	private final TaskRepository taskRepository;

	private final AbstractRepositoryConnector connector;

	public RepositoryCompletionProcessor(TaskRepository taskRepository) {
		Assert.isNotNull(taskRepository);

		this.taskRepository = taskRepository;
		this.connector = TasksUiPlugin.getConnector(taskRepository.getConnectorKind());
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		ITextSelection selection = (ITextSelection) viewer.getSelectionProvider().getSelection();
		// adjust offset to end of normalized selection
		if (selection.getOffset() == offset) {
			offset = selection.getOffset() + selection.getLength();
		}

		String prefix = extractPrefix(viewer, offset);

		TaskActivationHistory taskHistory = TasksUiPlugin.getTaskListManager().getTaskActivationHistory();
		List<AbstractTask> tasks = new ArrayList<AbstractTask>(
				taskHistory.getPreviousTasks(TaskListView.getActiveWorkingSets()));
		for (Iterator<AbstractTask> it = tasks.iterator(); it.hasNext();) {
			if (!it.next().getRepositoryUrl().equals(taskRepository.getUrl())) {
				it.remove();
			}
		}
		Collections.reverse(tasks);

		List<CompletionProposal> resultList = new ArrayList<CompletionProposal>(tasks.size());
		addTasks(resultList, tasks, prefix, offset);
		List<AbstractTask> repositoryTasks = new ArrayList<AbstractTask>(TasksUiPlugin.getTaskListManager()
				.getTaskList()
				.getRepositoryTasks(taskRepository.getUrl()));
		Collections.sort(repositoryTasks, new Comparator<AbstractTask>() {
			public int compare(AbstractTask o1, AbstractTask o2) {
				return o1.getTaskKey().compareTo(o2.getTaskKey());
			}
		});
		addTasks(resultList, repositoryTasks, prefix, offset);
		return resultList.toArray(new ICompletionProposal[resultList.size()]);
	}

	private void addTasks(List<CompletionProposal> resultList, List<AbstractTask> tasks, String prefix, int offset) {
		for (AbstractTask task : tasks) {
			String replacement = getTaskPrefix() + task.getTaskKey();
			if (replacement.startsWith(prefix)) {
				String displayString = replacement + ": " + task.getSummary();
				replacement = replacement.substring(prefix.length());
				resultList.add(new CompletionProposal(replacement, offset, 0, replacement.length(), null,
						displayString, null, task.getSummary()));
			} else {
				replacement = task.getTaskKey();
				if (replacement.startsWith(prefix)) {
					String displayString = replacement + ": " + task.getSummary();
					replacement = replacement.substring(prefix.length());
					resultList.add(new CompletionProposal(replacement, offset, 0, replacement.length(), null,
							displayString, null, null));
				}
			}
		}
	}

	private String getTaskPrefix() {
		String prefix = connector.getTaskIdPrefix();

		// FIXME work around for Trac "#" prefix
		return (prefix.length() > 1) ? prefix + " " : prefix;
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public String getErrorMessage() {
		return null;
	}

	/**
	 * Returns the prefix of the currently completed text. Assumes that any character that is not a line break or white
	 * space can be part of a task id.
	 */
	private String extractPrefix(ITextViewer viewer, int offset) {
		int i = offset;
		IDocument document = viewer.getDocument();
		if (i > document.getLength()) {
			return "";
		}

		try {
			while (i > 0) {
				char ch = document.getChar(i - 1);
				if (Character.isWhitespace(ch)) {
					break;
				}
				i--;
			}

			return document.get(i, offset - i);
		} catch (BadLocationException e) {
			return ""; //$NON-NLS-1$
		}
	}

}