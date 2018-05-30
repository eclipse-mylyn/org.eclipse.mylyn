/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.issue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.util.LabelComparator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.mylyn.commons.core.ICoreRunnable;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.internal.github.core.QueryUtils;
import org.eclipse.mylyn.internal.github.core.issue.IssueConnector;
import org.eclipse.mylyn.internal.github.ui.GitHubImages;
import org.eclipse.mylyn.internal.github.ui.GitHubRepositoryQueryPage;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;

/**
 * GitHub issue repository query page class.
 */
public class IssueRepositoryQueryPage extends GitHubRepositoryQueryPage {

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
	public IssueRepositoryQueryPage(String pageName,
			TaskRepository taskRepository, IRepositoryQuery query) {
		super(pageName, taskRepository, query);
		setDescription(Messages.IssueRepositoryQueryPage_Description);
		setPageComplete(false);
	}

	/**
	 * @param taskRepository
	 * @param query
	 */
	public IssueRepositoryQueryPage(TaskRepository taskRepository,
			IRepositoryQuery query) {
		this("issueQueryPage", taskRepository, query); //$NON-NLS-1$
	}

	private void createLabelsArea(Composite parent) {
		Group labelsArea = new Group(parent, SWT.NONE);
		labelsArea.setText(Messages.IssueRepositoryQueryPage_LabelsLabel);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(labelsArea);
		GridLayoutFactory.swtDefaults().applyTo(labelsArea);

		labelsViewer = CheckboxTableViewer.newCheckList(labelsArea, SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true).hint(100, 80)
				.applyTo(labelsViewer.getControl());
		labelsViewer.setContentProvider(ArrayContentProvider.getInstance());
		labelsViewer.setLabelProvider(new LabelProvider() {

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

	private void createOptionsArea(Composite parent) {
		Composite optionsArea = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(optionsArea);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(optionsArea);

		Composite statusArea = new Composite(optionsArea, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(4).equalWidth(false)
				.applyTo(statusArea);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
				.applyTo(statusArea);

		new Label(statusArea, SWT.NONE)
				.setText(Messages.IssueRepositoryQueryPage_StatusLabel);

		openButton = new Button(statusArea, SWT.CHECK);
		openButton.setSelection(true);
		openButton.setText(Messages.IssueRepositoryQueryPage_StatusOpen);
		openButton.addSelectionListener(completeListener);

		closedButton = new Button(statusArea, SWT.CHECK);
		closedButton.setSelection(true);
		closedButton.setText(Messages.IssueRepositoryQueryPage_StatusClosed);
		closedButton.addSelectionListener(completeListener);

		ToolBar toolbar = new ToolBar(statusArea, SWT.FLAT);
		ToolItem updateItem = new ToolItem(toolbar, SWT.PUSH);
		final Image updateImage = TasksUiImages.REPOSITORY_UPDATE_CONFIGURATION
				.createImage();
		toolbar.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				updateImage.dispose();
			}
		});
		updateItem.setImage(updateImage);
		updateItem
				.setToolTipText(Messages.IssueRepositoryQueryPage_TooltipUpdateRepository);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.FILL)
				.grab(true, false).applyTo(toolbar);
		updateItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				refreshRepository();
			}

		});

		Label milestonesLabel = new Label(optionsArea, SWT.NONE);
		milestonesLabel
				.setText(Messages.IssueRepositoryQueryPage_MilestoneLabel);

		milestoneCombo = new Combo(optionsArea, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, false)
				.applyTo(milestoneCombo);

		Label assigneeLabel = new Label(optionsArea, SWT.NONE);
		assigneeLabel.setText(Messages.IssueRepositoryQueryPage_AssigneeLabel);

		assigneeText = new Text(optionsArea, SWT.BORDER | SWT.SINGLE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(assigneeText);

		Label mentionLabel = new Label(optionsArea, SWT.NONE);
		mentionLabel.setText(Messages.IssueRepositoryQueryPage_MentionsLabel);

		mentionText = new Text(optionsArea, SWT.BORDER | SWT.SINGLE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(mentionText);

	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite displayArea = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true)
				.applyTo(displayArea);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(displayArea);

		if (!inSearchContainer()) {
			Composite titleArea = new Composite(displayArea, SWT.NONE);
			GridLayoutFactory.fillDefaults().numColumns(2).applyTo(titleArea);
			GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
					.applyTo(titleArea);

			new Label(titleArea, SWT.NONE)
					.setText(Messages.IssueRepositoryQueryPage_TitleLabel);
			titleText = new Text(titleArea, SWT.SINGLE | SWT.BORDER);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(titleText);
			titleText.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					setPageComplete(isPageComplete());
				}
			});
		}

		createOptionsArea(displayArea);

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
		if (milestoneNumber != null && milestones != null) {
			int index = 0;
			for (Milestone milestone : milestones) {
				index++;
				if (milestoneNumber.equals(Integer.toString(milestone
						.getNumber()))) {
					milestoneCombo.select(index);
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

		String assignee = query.getAttribute(IssueService.FILTER_ASSIGNEE);
		if (assignee != null)
			assigneeText.setText(assignee);

		String mentioning = query.getAttribute(IssueService.FILTER_MENTIONED);
		if (mentioning != null)
			mentionText.setText(mentioning);
	}

	private boolean updateLabels() {
		if (labelsViewer.getControl().isDisposed())
			return false;

		IssueConnector connector = IssueConnectorUi.getCoreConnector();
		TaskRepository repository = getTaskRepository();
		boolean hasLabels = connector.hasCachedLabels(repository);
		if (hasLabels) {
			List<org.eclipse.egit.github.core.Label> labels = connector
					.getLabels(repository);
			Collections.sort(labels, new LabelComparator());
			List<String> labelNames = new ArrayList<String>(labels.size());
			for (org.eclipse.egit.github.core.Label label : labels)
				labelNames.add(label.getName());
			labelsViewer.setInput(labelNames);
		}
		return hasLabels;
	}

	private boolean updateMilestones() {
		if (milestoneCombo.isDisposed())
			return false;

		IssueConnector connector = IssueConnectorUi.getCoreConnector();
		TaskRepository repository = getTaskRepository();
		boolean hasMilestones = connector.hasCachedMilestones(repository);
		if (hasMilestones) {
			milestones = connector.getMilestones(repository);
			milestoneCombo.removeAll();
			milestoneCombo.add(Messages.IssueRepositoryQueryPage_MilestoneNone);
			Collections.sort(milestones, new Comparator<Milestone>() {

				public int compare(Milestone m1, Milestone m2) {
					return m1.getTitle().compareToIgnoreCase(m2.getTitle());
				}
			});
			for (Milestone milestone : milestones)
				milestoneCombo.add(milestone.getTitle());

			milestoneCombo.select(0);
		}
		return hasMilestones;
	}

	private void refreshRepository() {
		try {
			ICoreRunnable runnable = new ICoreRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					Policy.monitorFor(monitor);
					monitor.beginTask("", 2); //$NON-NLS-1$
					IssueConnector connector = IssueConnectorUi
							.getCoreConnector();
					TaskRepository repository = getTaskRepository();

					monitor.setTaskName(Messages.IssueRepositoryQueryPage_TaskLoadingLabels);
					connector.refreshLabels(repository);
					monitor.worked(1);

					monitor.setTaskName(Messages.IssueRepositoryQueryPage_TaskLoadingMilestones);
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
				}
			};
			IRunnableContext context = getContainer();
			if (context == null)
				if (inSearchContainer())
					context = getSearchContainer().getRunnableContext();
				else
					context = PlatformUI.getWorkbench().getProgressService();
			CommonUiUtil.run(context, runnable);
		} catch (CoreException e) {
			IStatus status = e.getStatus();
			ErrorDialog.openError(getShell(),
					Messages.IssueRepositoryQueryPage_ErrorLoading,
					e.getLocalizedMessage(), status);
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
		boolean complete = inSearchContainer() ? true : super.isPageComplete();
		if (complete) {
			String message = null;
			if (!openButton.getSelection() && !closedButton.getSelection())
				message = Messages.IssueRepositoryQueryPage_ErrorStatus;

			setErrorMessage(message);
			complete = message == null;
		}
		return complete;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage#getQueryTitle()
	 */
	public String getQueryTitle() {
		return titleText != null ? titleText.getText() : null;
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

		String assignee = assigneeText.getText().trim();
		if (assignee.length() > 0)
			query.setAttribute(IssueService.FILTER_ASSIGNEE, assignee);
		else
			query.setAttribute(IssueService.FILTER_ASSIGNEE, null);

		String mentions = mentionText.getText().trim();
		if (mentions.length() > 0)
			query.setAttribute(IssueService.FILTER_MENTIONED, mentions);
		else
			query.setAttribute(IssueService.FILTER_MENTIONED, null);

		int milestoneSelected = milestoneCombo.getSelectionIndex() - 1;
		if (milestoneSelected >= 0)
			query.setAttribute(IssueService.FILTER_MILESTONE, Integer
					.toString(milestones.get(milestoneSelected).getNumber()));
		else
			query.setAttribute(IssueService.FILTER_MILESTONE, null);

		List<String> labels = new LinkedList<String>();
		for (Object label : labelsViewer.getCheckedElements())
			labels.add(label.toString());
		QueryUtils.setAttribute(IssueService.FILTER_LABELS, labels, query);
	}
}
