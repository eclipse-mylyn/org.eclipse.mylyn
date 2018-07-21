/*******************************************************************************
 * Copyright (c) 2004, 2011 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
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

	public static class TaskCompletionProposal implements ICompletionProposal {

		private final LabelProvider labelProvider;

		private final TaskRepository repository;

		private final ITask task;

		private final int replacementOffset;

		private final int replacementLength;

		private String replacement;

		private final String defaultReplacement;

		private final boolean includePrefix;

		public TaskCompletionProposal(TaskRepository repository, ITask task, LabelProvider labelProvider,
				String defaultReplacement, boolean includePrefix, int replacementOffset, int replacementLength) {
			this.labelProvider = labelProvider;
			this.repository = repository;
			this.task = task;
			this.defaultReplacement = defaultReplacement;
			this.includePrefix = includePrefix;
			this.replacementOffset = replacementOffset;
			this.replacementLength = replacementLength;
		}

		public void apply(IDocument document) {
			try {
				document.replace(replacementOffset, replacementLength, getReplacement());
			} catch (BadLocationException x) {
				// ignore
			}
		}

		public String getReplacement() {
			if (replacement == null) {
				// add an absolute reference to the task if the viewer does not have a repository
				if (defaultReplacement == null || repository == null
						|| !repository.getRepositoryUrl().equals(task.getRepositoryUrl())) {
					replacement = CopyTaskDetailsAction.getTextForTask(task);
				} else if (includePrefix) {
					replacement = TasksUiInternal.getTaskPrefix(task.getConnectorKind()) + defaultReplacement;
				} else {
					replacement = defaultReplacement;
				}
			}
			return replacement;
		}

		public String getAdditionalProposalInfo() {
			return null;
		}

		public IContextInformation getContextInformation() {
			return null;
		}

		public String getDisplayString() {
			return labelProvider.getText(task);
		}

		public Image getImage() {
			return labelProvider.getImage(task);
		}

		public Point getSelection(IDocument document) {
			return new Point(replacementOffset + getReplacement().length(), 0);
		}

		public TaskRepository getRepository() {
			return repository;
		}

		public ITask getTask() {
			return task;
		}

	}

	private class ProposalComputer {

		public static final String LABEL_SEPARATOR = " -------------------------------------------- "; //$NON-NLS-1$

		private final Set<ITask> addedTasks = new HashSet<ITask>();

		private boolean addSeparator;

		private final int offset;

		private final String prefix;

		private final List<ICompletionProposal> resultList = new ArrayList<ICompletionProposal>();

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

			resultList.add(new TaskCompletionProposal(repository, task, labelProvider, replacement, includeTaskPrefix,
					offset - prefix.length(), prefix.length()));

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

			if (getNeverIncludePrefix() && !task.getRepositoryUrl().equals(repository.getRepositoryUrl())) {
				return;
			}

			String taskKey = task.getTaskKey();
			if (prefix.length() == 0) {
				addProposal(task, taskKey, !getNeverIncludePrefix());
			} else if (taskKey != null && taskKey.startsWith(prefix)) {
				// don't include prefix if completing id since it was most likely already added
				addProposal(task, taskKey, false);
			} else if (containsPrefix(task)) {
				addProposal(task, taskKey, !getNeverIncludePrefix());
			}
		}

		private boolean containsPrefix(ITask task) {
			String haystack = TasksUiInternal.getTaskPrefix(task.getConnectorKind())
					+ " " + labelProvider.getText(task); //$NON-NLS-1$
			String[] haystackTokens = haystack.split("\\s"); //$NON-NLS-1$
			String[] needles = prefix.trim().split("\\*"); //$NON-NLS-1$
			if (haystackTokens.length == 0 || needles.length == 0) {
				return false;
			}
			// check if all needles are contained in haystack  
			for (String needle : needles) {
				boolean matched = false;
				haystack: for (String haystackToken : haystackTokens) {
					if (haystackToken.toLowerCase().startsWith(needle)) {
						matched = true;
						break haystack;
					}
				}
				if (!matched) {
					return false;
				}
			}
			return true;
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
					char c = document.getChar(i - 1);
					if (Character.isWhitespace(c) || c == '(' || c == ':') {
						break;
					}
					i--;
				}
				if (i == offset && repository != null) {
					// check if document contains "{prefix} "
					String taskPrefix = TasksUiInternal.getTaskPrefix(repository.getConnectorKind());
					if (taskPrefix.length() > 1) {
						try {
							if (taskPrefix.equals(document.get(offset - taskPrefix.length(), taskPrefix.length()))) {
								return taskPrefix;
							}
						} catch (BadLocationException e) {
							// ignore
						}
					}
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
					&& (repository == null || task.getRepositoryUrl().equals(repository.getRepositoryUrl()));
		}

		public ICompletionProposal[] getResult() {
			return resultList.toArray(new ICompletionProposal[resultList.size()]);
		}

	}

	private static final int MAX_OPEN_EDITORS = 10;

	private static final int MAX_ACTIVATED_TASKS = 10;

	private final TaskElementLabelProvider labelProvider = new TaskElementLabelProvider(false);

	private final TaskRepository repository;

	private boolean neverIncludePrefix;

	public RepositoryCompletionProcessor(TaskRepository taskRepository) {
		this.repository = taskRepository;
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
		List<AbstractTask> tasks = taskHistory.getPreviousTasks(TasksUiInternal.getContainersFromWorkingSet(TaskWorkingSetUpdater.getActiveWorkingSets(window)));
		int count = 0;
		for (int i = tasks.size() - 1; i >= 0 && count < MAX_ACTIVATED_TASKS; i--) {
			AbstractTask task = tasks.get(i);
			if (!(task instanceof LocalTask)) {
				proposalComputer.addTask(task);
			}
		}

		// add all remaining tasks for repository
		if (repository != null) {
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

}