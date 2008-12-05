/*******************************************************************************
 * Copyright (c) 2004, 2008 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 * @since 3.0
 */
public class RepositoryCompletionProcessor implements IContentAssistProcessor {

	private class ProposalComputer {

		public static final String LABEL_SEPARATOR = " -------------------------------------------- "; //$NON-NLS-1$

		private final Set<ITask> addedTasks = new HashSet<ITask>();

		private boolean addSeparator;

		private final int offset;

		private final String prefix;

		private final List<CompletionProposal> resultList = new ArrayList<CompletionProposal>();

		public ProposalComputer(ITextViewer viewer, int offset) {
			this.offset = offset;
			this.prefix = extractPrefix(viewer, offset).toLowerCase();
		}

		private void addProposal(ITask task, String replacement, boolean includeTaskPrefix) {
			if (addSeparator) {
				if (!addedTasks.isEmpty()) {
					resultList.add(createSeparator());
				}
				addSeparator = false;
			}

			replacement = getReplacement(task, replacement, includeTaskPrefix);
			String displayString = labelProvider.getText(task);
			resultList.add(new CompletionProposal(replacement, offset - prefix.length(), prefix.length(),
					replacement.length(), labelProvider.getImage(task), displayString, null, null));

			addedTasks.add(task);
		}

		public void addSeparator() {
			addSeparator = true;
		}

		public void addTasks(List<AbstractTask> tasks) {
			for (AbstractTask task : tasks) {
				addTask(task);
			}
		}

		public void addTask(ITask task) {
			if (addedTasks.contains(task)) {
				return;
			}

			if (getNeverIncludePrefix() && !task.getRepositoryUrl().equals(taskRepository.getRepositoryUrl())) {
				return;
			}

			String taskKey = task.getTaskKey();
			if (prefix.length() == 0) {
				addProposal(task, taskKey, !getNeverIncludePrefix());
			} else if (taskKey != null && taskKey.startsWith(prefix)) {
				addProposal(task, taskKey, false);
			} else if (containsPrefix(task)) {
				addProposal(task, taskKey, !getNeverIncludePrefix());
			}
		}

		private String getReplacement(ITask task, String text, boolean includeTaskPrefix) {
			// add an absolute reference to the task if the viewer does not have a repository
			if (taskRepository == null || text == null
					|| !taskRepository.getRepositoryUrl().equals(task.getRepositoryUrl())) {
				return CopyTaskDetailsAction.getTextForTask(task);
			}

			if (includeTaskPrefix) {
				return getTaskPrefix(task) + text;
			} else {
				return text;
			}
		}

		private boolean containsPrefix(ITask task) {
			String searchTest = getTaskPrefix(task) + " " + labelProvider.getText(task); //$NON-NLS-1$
			String[] tokens = searchTest.split("\\s"); //$NON-NLS-1$
			for (String token : tokens) {
				if (token.toLowerCase().startsWith(prefix)) {
					return true;
				}
			}
			return false;
		}

		private CompletionProposal createSeparator() {
			return new CompletionProposal("", offset, 0, 0, CommonImages.getImage(CommonImages.SEPARATOR_LIST), //$NON-NLS-1$
					LABEL_SEPARATOR, null, null);
		}

		/**
		 * Returns the prefix of the currently completed text. Assumes that any character that is not a line break or
		 * white space can be part of a task id.
		 */
		private String extractPrefix(ITextViewer viewer, int offset) {
			int i = offset;
			IDocument document = viewer.getDocument();
			if (i > document.getLength()) {
				return ""; //$NON-NLS-1$
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

		public void filterTasks(List<AbstractTask> tasks) {
			for (Iterator<AbstractTask> it = tasks.iterator(); it.hasNext();) {
				ITask task = it.next();
				if (!select(task)) {
					it.remove();
				}
			}
		}

		private boolean select(ITask task) {
			return !(task instanceof LocalTask) //
					&& (taskRepository == null || task.getRepositoryUrl().equals(taskRepository.getRepositoryUrl()));
		}

		public ICompletionProposal[] getResult() {
			return resultList.toArray(new ICompletionProposal[resultList.size()]);
		}

	}

	private static final int MAX_OPEN_EDITORS = 10;

	private static final int MAX_ACTIVATED_TASKS = 10;

	private final TaskElementLabelProvider labelProvider = new TaskElementLabelProvider(false);

	private final TaskRepository taskRepository;

	private boolean neverIncludePrefix;

	public RepositoryCompletionProcessor(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
		this.neverIncludePrefix = false;
	}

	public boolean getNeverIncludePrefix() {
		return neverIncludePrefix;
	}

	public void setNeverIncludePrefix(boolean includePrefix) {
		this.neverIncludePrefix = includePrefix;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		ITextSelection selection = (ITextSelection) viewer.getSelectionProvider().getSelection();
		// adjust offset to end of normalized selection
		if (selection.getOffset() == offset) {
			offset = selection.getOffset() + selection.getLength();
		}

		ProposalComputer proposalComputer = new ProposalComputer(viewer, offset);

		// add tasks from navigation history
//		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//		if (window != null) {
//			IWorkbenchPage page = window.getActivePage();
//			if (page != null) {
//				INavigationHistory history = page.getNavigationHistory();
//				INavigationLocation[] locations = history.getLocations();
//				if (locations != null) {
//					for (INavigationLocation location : locations) {
//						// location is always null
//					}
//				}
//			}
//		}

		// add open editor
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IEditorReference[] editorReferences = page.getEditorReferences();
				int count = 0;
				for (int i = editorReferences.length - 1; i >= 0 && count < MAX_OPEN_EDITORS; i--) {
					try {
						if (editorReferences[i].getEditorInput() instanceof TaskEditorInput) {
							TaskEditorInput input = (TaskEditorInput) editorReferences[i].getEditorInput();
							ITask task = input.getTask();
							if (task != null && !(task instanceof LocalTask)) {
								proposalComputer.addTask(task);
								count++;
							}
						}
					} catch (PartInitException e) {
						// ignore
					}
				}
			}
		}

		// add tasks from activation history
		TaskActivationHistory taskHistory = TasksUiPlugin.getTaskActivityManager().getTaskActivationHistory();
		List<AbstractTask> tasks = taskHistory.getPreviousTasks(TasksUiInternal.getContainersFromWorkingSet(TaskListView.getActiveWorkingSets()));
		int count = 0;
		for (int i = tasks.size() - 1; i >= 0 && count < MAX_ACTIVATED_TASKS; i--) {
			AbstractTask task = tasks.get(i);
			if (!(task instanceof LocalTask)) {
				proposalComputer.addTask(task);
			}
		}

		// add all remaining tasks for repository
		if (taskRepository != null) {
			proposalComputer.addSeparator();

			TaskList taskList = TasksUiPlugin.getTaskList();
			tasks = new ArrayList<AbstractTask>(taskList.getAllTasks());
			proposalComputer.filterTasks(tasks);
			Collections.sort(tasks, new Comparator<AbstractTask>() {
				public int compare(AbstractTask o1, AbstractTask o2) {
					return labelProvider.getText(o1).compareTo(labelProvider.getText(o2));
				}
			});
			proposalComputer.addTasks(tasks);
		}

		return proposalComputer.getResult();
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

	private String getTaskPrefix(ITask task) {
		AbstractRepositoryConnector connector = TasksUiPlugin.getConnector(task.getConnectorKind());
		String prefix = connector.getTaskIdPrefix();
		// FIXME work around for Trac "#" prefix
		return (prefix.length() > 1) ? prefix + " " : prefix; //$NON-NLS-1$
	}

}