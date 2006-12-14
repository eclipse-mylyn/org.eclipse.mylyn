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
import java.net.URLEncoder;

import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.tasks.ui.editors.AbstractNewRepositoryTaskEditor;
import org.eclipse.mylar.tasks.ui.search.SearchHitCollector;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * An editor used to view a locally created bug that does not yet exist on a
 * server.
 * 
 * @author Rob Elves
 */
public class NewBugzillaTaskEditor extends AbstractNewRepositoryTaskEditor {

	private static final int WRAP_LENGTH = 90;

	public NewBugzillaTaskEditor(FormEditor editor) {
		super(editor);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		expandedStateAttributes = true;
	}

	@Override
	protected void updateTask() {
		String text = descriptionTextViewer.getTextWidget().getText();
		if (repository.getVersion().startsWith("2.18")) {
			text = formatTextToLineWrap(text, true);
			descriptionTextViewer.getTextWidget().setText(text);
		}
		super.updateTask();
	}

	// @Override
	// protected void submitToRepository() {
	// if (!prepareSubmit()) {
	// return;
	// }
	// updateTask();
	// boolean wrap =
	// IBugzillaConstants.BugzillaServerVersion.SERVER_218.equals(repository.getVersion());
	//
	// try {
	// final BugzillaReportSubmitForm bugzillaReportSubmitForm =
	// BugzillaReportSubmitForm.makeNewBugPost(
	// repository.getUrl(), repository.getUserName(), repository.getPassword(),
	// repository
	// .getCharacterEncoding(), taskData, wrap);
	//
	// submissionHandler.submitBugReport(bugzillaReportSubmitForm,
	// submitJobListener, false, getCategory());
	//
	// } catch (UnsupportedEncodingException e) {
	// MessageDialog.openError(null, "Posting Error", "Ensure proper encoding
	// selected in "
	// + TaskRepositoriesView.NAME + ".");
	// return;
	// } catch (Exception e) {
	// MylarStatusHandler.fail(e, "Posting Error. Ensure proper configuration in
	// " + TaskRepositoriesView.NAME
	// + ".", true);
	// return;
	// }
	//
	// }
	// protected void handleNewBugPost(String postResult) throws CoreException {
	// int bugId = -1;
	// try {
	// bugId = Integer.parseInt(postResult);
	// } catch (NumberFormatException e) {
	// throw new CoreException(new Status(Status.ERROR,
	// BugzillaUiPlugin.PLUGIN_ID,
	// "Invalid id returned by repository: " + postResult));
	// }
	//
	// BugzillaTask newTask = new
	// BugzillaTask(AbstractRepositoryTask.getHandle(repository.getUrl(),
	// bugId),
	// "<bugzilla info>", true);
	//
	// if (getCategory() != null) {
	// TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer(getCategory(),
	// newTask);
	// }
	// TasksUiPlugin.getSynchronizationScheduler().synchNow(0,
	// Collections.singletonList(repository));
	//
	// }

	// protected void addActionButtons(Composite buttonComposite) {
	//
	// FormToolkit toolkit = new FormToolkit(buttonComposite.getDisplay());
	// searchDuplicatesButton = toolkit.createButton(buttonComposite,
	// LABEL_SEARCH_DUPS, SWT.NONE);
	// GridData searchDuplicatesButtonData = new
	// GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
	// searchDuplicatesButton.setLayoutData(searchDuplicatesButtonData);
	// searchDuplicatesButton.addListener(SWT.Selection, new Listener() {
	// public void handleEvent(Event e) {
	// searchForDuplicates();
	// }
	// });
	//
	// super.addActionButtons(buttonComposite);
	// }

	@Override
	public SearchHitCollector getDuplicateSearchCollector(String searchString) {
		String queryUrl = "";
		try {
			queryUrl = repository.getUrl() + "/buglist.cgi?long_desc_type=allwordssubstr&long_desc="
					+ URLEncoder.encode(searchString, repository.getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			MylarStatusHandler.log(e, "Error during duplicate detection");
			return null;
		}

		queryUrl += "&product=" + getRepositoryTaskData().getProduct();

		BugzillaRepositoryQuery bugzillaQuery = new BugzillaRepositoryQuery(repository.getUrl(), queryUrl, "search",
				"100", TasksUiPlugin.getTaskListManager().getTaskList());

		SearchHitCollector collector = new SearchHitCollector(TasksUiPlugin.getTaskListManager().getTaskList(),
				repository, bugzillaQuery);
		return collector;
	}

	@Override
	protected String getPluginId() {
		return BugzillaUiPlugin.PLUGIN_ID;
	}

	// @Override
	// protected void handleErrorStatus(IJobChangeEvent event) {
	// if (event.getJob().getResult().getCode() == Status.INFO) {
	// WebBrowserDialog.openAcceptAgreement(NewBugzillaTaskEditor.this.getSite().getShell(),
	// IBugzillaConstants.REPORT_SUBMIT_ERROR,
	// event.getJob().getResult().getMessage(), event.getJob()
	// .getResult().getException().getMessage());
	// } else if (event.getJob().getResult().getCode() == Status.ERROR) {
	// MessageDialog.openError(NewBugzillaTaskEditor.this.getSite().getShell(),
	// IBugzillaConstants.REPORT_SUBMIT_ERROR, event.getResult().getMessage());
	// }
	// super.handleErrorStatus(event);
	// }

	/**
	 * Break text up into lines so that it is displayed properly in bugzilla
	 */
	private static String formatTextToLineWrap(String origText, boolean hardWrap) {
		// BugzillaServerVersion bugzillaServerVersion =
		// IBugzillaConstants.BugzillaServerVersion.fromString(repository
		// .getVersion());
		// if (bugzillaServerVersion != null &&
		// bugzillaServerVersion.compareTo(BugzillaServerVersion.SERVER_220) >=
		// 0) {
		// return origText;
		if (!hardWrap) {
			return origText;
		} else {
			String[] textArray = new String[(origText.length() / WRAP_LENGTH + 1) * 2];
			for (int i = 0; i < textArray.length; i++)
				textArray[i] = null;
			int j = 0;
			while (true) {
				int spaceIndex = origText.indexOf(" ", WRAP_LENGTH - 5);
				if (spaceIndex == origText.length() || spaceIndex == -1) {
					textArray[j] = origText;
					break;
				}
				textArray[j] = origText.substring(0, spaceIndex);
				origText = origText.substring(spaceIndex + 1, origText.length());
				j++;
			}

			String newText = "";

			for (int i = 0; i < textArray.length; i++) {
				if (textArray[i] == null)
					break;
				newText += textArray[i] + "\n";
			}
			return newText;
		}
	}

}
