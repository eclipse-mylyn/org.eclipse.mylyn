/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.ui.editor;

import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.Calendar;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.tasks.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasks.ui.editors.AbstractNewRepositoryTaskEditor;
import org.eclipse.mylar.internal.tasks.ui.search.SearchHitCollector;
import org.eclipse.mylar.internal.tasks.ui.util.WebBrowserDialog;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskCategory;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * An editor used to view a locally created bug that does not yet exist on a
 * server.
 * 
 * @author Rob Elves (modifications)
 */
public class NewBugEditor extends AbstractNewRepositoryTaskEditor {

	private static final String LABEL_SEARCH_DUPS = "Search for Duplicates";

	private static final String NO_STACK_MESSAGE = "Unable to locate a stack trace in the description text.\nDuplicate search currently only supports stack trace matching.";

	private static final String ERROR_CREATING_BUG_REPORT = "Error creating bug report";

	private Button searchDuplicatesButton;

	private BugSubmissionHandler submissionHandler;

	public NewBugEditor(FormEditor editor) {
		super(editor);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		expandedStateAttributes = true;
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getKind());
		submissionHandler = new BugSubmissionHandler(connector);
	}

	public boolean searchForDuplicates() {

		String stackTrace = getStackTraceFromDescription();
		if (stackTrace == null) {
			MessageDialog.openWarning(null, "No Stack Trace Found", NO_STACK_MESSAGE);
			return false;
		}

		String queryUrl = "";
		try {
			queryUrl = repository.getUrl() + "/buglist.cgi?long_desc_type=allwordssubstr&long_desc="
					+ URLEncoder.encode(stackTrace, repository.getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			// This should never happen
		}

		queryUrl += "&product=" + getRepositoryTaskData().getProduct();

		BugzillaRepositoryQuery bugzillaQuery = new BugzillaRepositoryQuery(repository.getUrl(), queryUrl, "search",
				"100", TasksUiPlugin.getTaskListManager().getTaskList());
		Proxy proxySettings = TasksUiPlugin.getDefault().getProxySettings();
		SearchHitCollector collector = new SearchHitCollector(TasksUiPlugin.getTaskListManager().getTaskList(),
				repository, bugzillaQuery, proxySettings);

		NewSearchUI.runQueryInBackground(collector);
		return true;
	}

	@Override
	protected void submitBug() {
		submitButton.setEnabled(false);
		showBusy(true);
		if (summaryText != null && summaryText.getText().trim().equals("")) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openInformation(NewBugEditor.this.getSite().getShell(), ERROR_CREATING_BUG_REPORT,
							"A summary must be provided with new bug reports.");
					summaryText.setFocus();
					submitButton.setEnabled(true);
					showBusy(false);
				}
			});
			return;
		}
		if (descriptionTextViewer != null && descriptionTextViewer.getTextWidget().getText().trim().equals("")) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openInformation(NewBugEditor.this.getSite().getShell(), ERROR_CREATING_BUG_REPORT,
							"A description must be provided with new reports.");
					descriptionTextViewer.getTextWidget().setFocus();
					submitButton.setEnabled(true);
					showBusy(false);
				}
			});
			return;
		}

		updateBug();
		Proxy proxySettings = TasksUiPlugin.getDefault().getProxySettings();
		boolean wrap = IBugzillaConstants.BugzillaServerVersion.SERVER_218.equals(repository.getVersion());

		try {
			final BugzillaReportSubmitForm bugzillaReportSubmitForm = BugzillaReportSubmitForm.makeNewBugPost(
					repository.getUrl(), repository.getUserName(), repository.getPassword(), proxySettings, repository
							.getCharacterEncoding(), taskData, wrap);

			JobChangeAdapter submitJobListener = new JobChangeAdapter() {

				public void done(final IJobChangeEvent event) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (event.getJob().getResult().getCode() == Status.OK
									&& event.getJob().getResult().getMessage() != null) {
								close();
								String newTaskHandle = AbstractRepositoryTask.getHandle(repository.getUrl(), event
										.getJob().getResult().getMessage());
								ITask newTask = TasksUiPlugin.getTaskListManager().getTaskList().getTask(newTaskHandle);
								if (newTask != null) {
									Calendar selectedDate = datePicker.getDate();
									if (selectedDate != null) {
										// NewLocalTaskAction.scheduleNewTask(newTask);
										TasksUiPlugin.getTaskListManager().setScheduledFor(newTask, selectedDate.getTime());
									}

									newTask.setEstimatedTimeHours(estimated.getSelection());

									Object selectedObject = null;
									if (TaskListView.getFromActivePerspective() != null)
										selectedObject = ((IStructuredSelection) TaskListView
												.getFromActivePerspective().getViewer().getSelection())
												.getFirstElement();

									if (selectedObject instanceof TaskCategory) {
										TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer(
												((TaskCategory) selectedObject), newTask);
									}
									TaskUiUtil.refreshAndOpenTaskListElement(newTask);
								}
								return;
							} else if (event.getJob().getResult().getCode() == Status.INFO) {
								WebBrowserDialog.openAcceptAgreement(NewBugEditor.this.getSite().getShell(),
										IBugzillaConstants.REPORT_SUBMIT_ERROR,
										event.getJob().getResult().getMessage(), event.getJob().getResult()
												.getException().getMessage());
								submitButton.setEnabled(true);
								NewBugEditor.this.showBusy(false);
							} else if (event.getJob().getResult().getCode() == Status.ERROR) {
								MessageDialog.openError(NewBugEditor.this.getSite().getShell(),
										IBugzillaConstants.REPORT_SUBMIT_ERROR, event.getResult().getMessage());
								submitButton.setEnabled(true);
								NewBugEditor.this.showBusy(false);
							}
						}
					});
				}
			};

			submissionHandler.submitBugReport(bugzillaReportSubmitForm, submitJobListener, false, addToTaskListRoot
					.getSelection());

		} catch (UnsupportedEncodingException e) {
			MessageDialog.openError(null, "Posting Error", "Ensure proper encoding selected in "
					+ TaskRepositoriesView.NAME + ".");
			return;
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Posting Error. Ensure proper configuration in " + TaskRepositoriesView.NAME
					+ ".", true);
			return;
		}

	}

	protected void addActionButtons(Composite buttonComposite) {

		FormToolkit toolkit = new FormToolkit(buttonComposite.getDisplay());
		searchDuplicatesButton = toolkit.createButton(buttonComposite, LABEL_SEARCH_DUPS, SWT.NONE);
		GridData searchDuplicatesButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		searchDuplicatesButton.setLayoutData(searchDuplicatesButtonData);
		searchDuplicatesButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				searchForDuplicates();
			}
		});

		super.addActionButtons(buttonComposite);
	}

}
