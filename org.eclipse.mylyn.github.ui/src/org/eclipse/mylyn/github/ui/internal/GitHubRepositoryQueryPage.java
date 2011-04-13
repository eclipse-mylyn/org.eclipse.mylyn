/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.github.ui.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.github.internal.GitHubRepositoryConnector;
import org.eclipse.mylyn.github.internal.IssueService;
import org.eclipse.mylyn.github.internal.LabelComparator;
import org.eclipse.mylyn.github.internal.Milestone;
import org.eclipse.mylyn.github.internal.QueryUtils;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * GitHub issue repository query page class.
 */
public class GitHubRepositoryQueryPage extends AbstractRepositoryQueryPage {

	private Button openButton;
	private Button closedButton;
	private Text titleText;
	private Text assigneeText;
	private Text mentionText;
	private Combo milestoneCombo;
	private CheckboxTableViewer labelsViewer;
	private List<Milestone> milestones;

	private SelectionListener completeListener = new SelectionAdapter() {

		public void widgetSelected(SelectionEvent e) {
			setPageComplete(isPageComplete());
		}

	};

	/**
	 * @param pageName
	 * @param taskRepository
	 * @param query
	 */
	public GitHubRepositoryQueryPage(String pageName,
			TaskRepository taskRepository, IRepositoryQuery query) {
		super(pageName, taskRepository, query);
		setDescription(Messages.GitHubRepositoryQueryPage_Description);
		setPageComplete(false);
	}

	/**
	 * @param taskRepository
	 * @param query
	 */
	public GitHubRepositoryQueryPage(TaskRepository taskRepository,
			IRepositoryQuery query) {
		this("issueQueryPage", taskRepository, query); //$NON-NLS-1$
	}

	private void createLabelsArea(Composite parent) {
		Group labelsArea = new Group(parent, SWT.NONE);
		labelsArea.setText(Messages.GitHubRepositoryQueryPage_LabelsLabel);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(labelsArea);
		GridLayoutFactory.swtDefaults().applyTo(labelsArea);

		labelsViewer = CheckboxTableViewer.newCheckList(labelsArea, SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true).hint(100, 80)
				.applyTo(labelsViewer.getControl());
		labelsViewer.setContentProvider(ArrayContentProvider.getInstance());
		labelsViewer.setLabelProvider(new LabelProvider() {

			public String getText(Object element) {
				return ((org.eclipse.mylyn.github.internal.Label) element)
						.getName();
			}

			public Image getImage(Object element) {
				return GitHubImages.get(GitHubImages.GITHUB_ISSUE_LABEL_OBJ);
			}

		});
		labelsViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						setPageComplete(isPageComplete());
					}
				});
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite displayArea = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false)
				.applyTo(displayArea);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(displayArea);

		if (!inSearchContainer()) {
			Composite titleArea = new Composite(displayArea, SWT.NONE);
			GridLayoutFactory.fillDefaults().numColumns(2).applyTo(titleArea);
			GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
					.applyTo(titleArea);

			new Label(titleArea, SWT.NONE)
					.setText(Messages.GitHubRepositoryQueryPage_TitleLabel);
			titleText = new Text(titleArea, SWT.SINGLE | SWT.BORDER);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(titleText);
			titleText.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					setPageComplete(isPageComplete());
				}
			});
		}

		Composite leftArea = new Composite(displayArea, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(leftArea);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(leftArea);

		Composite statusArea = new Composite(leftArea, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false)
				.applyTo(statusArea);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
				.applyTo(statusArea);

		new Label(statusArea, SWT.NONE)
				.setText(Messages.GitHubRepositoryQueryPage_StatusLabel);

		openButton = new Button(statusArea, SWT.CHECK);
		openButton.setSelection(true);
		openButton.setText(Messages.GitHubRepositoryQueryPage_StatusOpen);
		openButton.addSelectionListener(this.completeListener);

		closedButton = new Button(statusArea, SWT.CHECK);
		closedButton.setSelection(true);
		closedButton.setText(Messages.GitHubRepositoryQueryPage_StatusClosed);
		closedButton.addSelectionListener(this.completeListener);

		Label milestonesLabel = new Label(leftArea, SWT.NONE);
		milestonesLabel
				.setText(Messages.GitHubRepositoryQueryPage_MilestoneLabel);

		milestoneCombo = new Combo(leftArea, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, false)
				.applyTo(milestoneCombo);

		Label assigneeLabel = new Label(leftArea, SWT.NONE);
		assigneeLabel.setText(Messages.GitHubRepositoryQueryPage_AssigneeLabel);

		assigneeText = new Text(leftArea, SWT.BORDER | SWT.SINGLE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(assigneeText);

		Label mentionLabel = new Label(leftArea, SWT.NONE);
		mentionLabel.setText(Messages.GitHubRepositoryQueryPage_MentionsLabel);

		mentionText = new Text(leftArea, SWT.BORDER | SWT.SINGLE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(mentionText);

		createLabelsArea(displayArea);

		loadRepository();

		initialize();
		setControl(displayArea);
	}

	private void initialize() {
		IRepositoryQuery query = getQuery();
		if (query == null)
			return;

		String milestoneNumber = query
				.getAttribute(IssueService.FILTER_MILESTONE);
		if (milestoneNumber != null && this.milestones != null) {
			int index = 0;
			for (Milestone milestone : this.milestones) {
				index++;
				if (milestoneNumber.equals(Integer.toString(milestone
						.getNumber()))) {
					this.milestoneCombo.select(index);
					break;
				}
			}
		}

		titleText.setText(query.getSummary());
		labelsViewer.setCheckedElements(QueryUtils.getAttributes(
				IssueService.FILTER_LABELS, query).toArray());
		List<String> status = QueryUtils.getAttributes(
				IssueService.FILTER_STATE, query);
		closedButton.setSelection(status.contains(IssueService.STATE_CLOSED));
		openButton.setSelection(status.contains(IssueService.STATE_OPEN));
	}

	private boolean updateLabels() {
		if (this.labelsViewer.getControl().isDisposed())
			return false;

		GitHubRepositoryConnector connector = GitHubRepositoryConnectorUI
				.getCoreConnector();
		TaskRepository repository = getTaskRepository();
		boolean hasLabels = connector.hasCachedLabels(repository);
		if (hasLabels) {
			List<org.eclipse.mylyn.github.internal.Label> labels = connector
					.getLabels(repository);
			Collections.sort(labels, new LabelComparator());
			this.labelsViewer.setInput(labels);
		}
		return hasLabels;
	}

	private boolean updateMilestones() {
		if (this.milestoneCombo.isDisposed())
			return false;

		GitHubRepositoryConnector connector = GitHubRepositoryConnectorUI
				.getCoreConnector();
		TaskRepository repository = getTaskRepository();
		boolean hasMilestones = connector.hasCachedMilestones(repository);
		if (hasMilestones) {
			this.milestones = connector.getMilestones(repository);
			this.milestoneCombo.removeAll();
			this.milestoneCombo
					.add(Messages.GitHubRepositoryQueryPage_MilestoneNone);
			for (Milestone milestone : milestones)
				this.milestoneCombo.add(milestone.getTitle());

			this.milestoneCombo.select(0);
		}
		return hasMilestones;
	}

	private void refreshRepository() {
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					Policy.monitorFor(monitor);
					monitor.beginTask("", 2); //$NON-NLS-1$
					try {
						GitHubRepositoryConnector connector = GitHubRepositoryConnectorUI
								.getCoreConnector();
						TaskRepository repository = getTaskRepository();

						monitor.setTaskName(Messages.GitHubRepositoryQueryPage_TaskLoadingLabels);
						connector.refreshLabels(repository);
						monitor.worked(1);

						monitor.setTaskName(Messages.GitHubRepositoryQueryPage_TaskLoadingMilestones);
						connector.refreshMilestones(repository);
						monitor.done();

						PlatformUI.getWorkbench().getDisplay()
								.asyncExec(new Runnable() {

									public void run() {
										updateLabels();
										updateMilestones();
										initialize();
									}
								});
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		} catch (InvocationTargetException e) {
			Throwable target = e.getTargetException();
			if (target instanceof CoreException) {
				IStatus status = ((CoreException) target).getStatus();
				ErrorDialog.openError(getShell(),
						Messages.GitHubRepositoryQueryPage_ErrorLoading,
						target.getLocalizedMessage(), status);
			}
		} catch (InterruptedException ignore) {
			// Ignore
		}
	}

	private void loadRepository() {
		boolean labelsLoaded = updateLabels();
		boolean milestonesLoaded = updateMilestones();
		if (!labelsLoaded || !milestonesLoaded)
			refreshRepository();
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage#isPageComplete()
	 */
	public boolean isPageComplete() {
		boolean complete = super.isPageComplete();
		if (complete) {
			String message = null;
			if (!openButton.getSelection() && !closedButton.getSelection())
				message = Messages.GitHubRepositoryQueryPage_ErrorStatus;

			setErrorMessage(message);
			complete = message == null;
		}
		return complete;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage#getQueryTitle()
	 */
	public String getQueryTitle() {
		return this.titleText != null ? this.titleText.getText() : null;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage#applyTo(org.eclipse.mylyn.tasks.core.IRepositoryQuery)
	 */
	public void applyTo(IRepositoryQuery query) {
		query.setSummary(getQueryTitle());

		List<String> statuses = new LinkedList<String>();
		if (openButton.getSelection())
			statuses.add(IssueService.STATE_OPEN);
		if (closedButton.getSelection())
			statuses.add(IssueService.STATE_CLOSED);
		QueryUtils.setAttribute(IssueService.FILTER_STATE, statuses, query);

		String assignee = this.assigneeText.getText().trim();
		if (assignee.length() > 0)
			query.setAttribute(IssueService.FILTER_ASSIGNEE, assignee);
		else
			query.setAttribute(IssueService.FILTER_ASSIGNEE, null);

		String mentions = this.mentionText.getText().trim();
		if (assignee.length() > 0)
			query.setAttribute(IssueService.FILTER_MENTIONED, mentions);
		else
			query.setAttribute(IssueService.FILTER_MENTIONED, null);

		int milestoneSelected = this.milestoneCombo.getSelectionIndex() - 1;
		if (milestoneSelected >= 0)
			query.setAttribute(IssueService.FILTER_MILESTONE, Integer
					.toString(this.milestones.get(milestoneSelected)
							.getNumber()));
		else
			query.setAttribute(IssueService.FILTER_MILESTONE, null);

		List<String> labels = new LinkedList<String>();
		for (Object label : labelsViewer.getCheckedElements())
			labels.add(label.toString());
		QueryUtils.setAttribute(IssueService.FILTER_LABELS, labels, query);
	}
}
