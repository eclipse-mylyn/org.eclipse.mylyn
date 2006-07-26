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
package org.eclipse.mylar.internal.bugzilla.ui.wizard;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.NewBugzillaReport;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.editor.NewBugEditorInput;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryQuery;
import org.eclipse.mylar.internal.tasks.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasks.ui.editors.AbstractBugEditorInput;
import org.eclipse.mylar.internal.tasks.ui.editors.ExistingBugEditorInput;
import org.eclipse.mylar.internal.tasks.ui.wizards.AbstractDuplicateDetectingReportWizard;
import org.eclipse.mylar.internal.tasks.ui.wizards.DuplicateDetectionData;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class NewBugzillaReportWizard extends AbstractDuplicateDetectingReportWizard implements INewWizard {

	private static final String TITLE = "New Bugzilla Task";

	private IWorkbench workbenchInstance;

	private final TaskRepository repository;

	private final BugzillaProductPage productPage;

	/**
	 * Flag to indicate if the wizard can be completed (finish button enabled)
	 */
	protected boolean completed = false;

	/** The model used to store all of the data for the wizard */
	protected NewBugzillaReport model;

	// TODO: Change model to a RepositoryTaskData
	// protected RepositoryTaskData model;

	public NewBugzillaReportWizard(TaskRepository repository, IStructuredSelection selection) {
		this(false, repository, selection);
		model = new NewBugzillaReport(repository.getUrl(), TasksUiPlugin.getDefault().getOfflineReportsFile()
				.getNextOfflineBugId());
		super.setDefaultPageImageDescriptor(BugzillaUiPlugin.imageDescriptorFromPlugin(
				"org.eclipse.mylar.internal.bugzilla.ui", "icons/wizban/bug-wizard.gif"));
		super.setWindowTitle(TITLE);
		setNeedsProgressMonitor(true);
	}

	public NewBugzillaReportWizard(boolean fromDialog, TaskRepository repository, IStructuredSelection selection) {
		super();
		this.repository = repository;
		this.productPage = new BugzillaProductPage(workbenchInstance, this, repository, selection);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbenchInstance = workbench;
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(productPage);

		super.addQueuedPages();
	}

	@Override
	public boolean canFinish() {
		return completed && super.canFinish();
	}

	@Override
	public boolean performFinish() {
		List<AbstractRepositoryTask> dups = getSelectedDuplicates();
		if (dups != null && !dups.isEmpty()) {
			Iterator<AbstractRepositoryTask> iter = dups.iterator();
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			while (iter.hasNext()) {
				AbstractRepositoryTask task = iter.next();
				AbstractBugEditorInput editorInput = new ExistingBugEditorInput(TasksUiPlugin.getRepositoryManager()
						.getRepository(task.getRepositoryKind(), task.getRepositoryUrl()), task.getTaskData());
				TaskUiUtil.openEditor(editorInput, BugzillaUiPlugin.EXISTING_BUG_EDITOR_ID, page);
			}
			return true;
		}
		try {
			productPage.saveDataToModel();
			NewBugEditorInput editorInput = new NewBugEditorInput(repository, model);
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			TaskUiUtil.openEditor(editorInput, BugzillaUiPlugin.NEW_BUG_EDITOR_ID, page);
			return true;
		} catch (Exception e) {
			productPage.applyToStatusLine(new Status(IStatus.ERROR, "not_used", 0,
					"Problem occured retrieving repository configuration from " + repository.getUrl(), null));
		}
		return false;
	}

	/**
	 * Perform a query using the given duplicate detection criteria and return a
	 * list of tasks which match.
	 */
	@Override
	public List<AbstractRepositoryTask> searchForDuplicates(DuplicateDetectionData data) {
		// RepositoryQueryResultsFactory factory = new
		// RepositoryQueryResultsFactory();
		// IBugzillaSearchResultCollector collector = new
		// BugzillaSearchResultCollector();
		// factory.performQuery(repository.getUrl(), collector, queryUrl,
		// proxySettings, 20, BugzillaPlugin.ENCODING_UTF_8);

		String[] products = productPage.getSelectedProducts();

		// TODO: Is there a class that can create this string?
		String queryUrl;
		try {
			queryUrl = repository.getUrl() + "/buglist.cgi?long_desc_type=allwordssubstr&long_desc="
					+ URLEncoder.encode("Stack Trace:\n" + data.getStackTrace(), BugzillaPlugin.ENCODING_UTF_8);
		} catch (UnsupportedEncodingException e) {
			// This should never happen
			return null;
		}

		for (int i = 0; i < products.length; i++) {
			queryUrl += "&product=" + products[i];
		}

		List<AbstractRepositoryTask> tasks = new LinkedList<AbstractRepositoryTask>();
		BugzillaRepositoryQuery repositoryQuery = new BugzillaRepositoryQuery(repository.getUrl(), queryUrl,
				"DUPLICATE_DETECTION_QUERY", "20", TasksUiPlugin.getTaskListManager().getTaskList());
		AbstractRepositoryConnector connector = (AbstractRepositoryConnector) TasksUiPlugin.getRepositoryManager()
				.getRepositoryConnector(BugzillaPlugin.REPOSITORY_KIND);
		List<AbstractQueryHit> hits = connector.performQuery(repositoryQuery, new NullProgressMonitor(),
				new MultiStatus(TasksUiPlugin.PLUGIN_ID, IStatus.OK, "Query result", null));
		Iterator<AbstractQueryHit> iterator = hits.iterator();
		while (iterator.hasNext()) {
			tasks.add(iterator.next().getOrCreateCorrespondingTask());
		}

		return tasks;
	}
}

// @Override
// protected void saveBugOffline() {
// // AbstractRepositoryConnector client = (AbstractRepositoryConnector)
// // MylarTaskListPlugin.getRepositoryManager()
// // .getRepositoryConnector(BugzillaPlugin.REPOSITORY_KIND);
// // client.saveOffline(model);
// }
//
// @Override
// protected AbstractBugzillaWizardPage getWizardDataPage() {
// return null;
// }

// Open new bug editor

// if (super.performFinish()) {
//
// String bugIdString = this.getId();
// int bugId = -1;
// // boolean validId = false;
// try {
// if (bugIdString != null) {
// bugId = Integer.parseInt(bugIdString);
// // validId = true;
// }
// } catch (NumberFormatException nfe) {
// MessageDialog.openError(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
// "Could not create bug id, no valid id");
// return false;
// }
// // if (!validId) {
// // MessageDialog.openError(null,
// // IBugzillaConstants.TITLE_MESSAGE_DIALOG,
// // "Could not create bug id, no valid id");
// // return false;
// // }
//
// BugzillaTask newTask = new
// BugzillaTask(AbstractRepositoryTask.getHandle(repository.getUrl(), bugId),
// "<bugzilla info>", true);
// Object selectedObject = null;
// if (TaskListView.getFromActivePerspective() != null)
// selectedObject = ((IStructuredSelection)
// TaskListView.getFromActivePerspective().getViewer()
// .getSelection()).getFirstElement();
//
// // MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(newTask);
//
// if (selectedObject instanceof TaskCategory) {
// MylarTaskListPlugin.getTaskListManager().getTaskList()
// .addTask(newTask, ((TaskCategory) selectedObject));
// } else {
// MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(newTask,
// MylarTaskListPlugin.getTaskListManager().getTaskList().getRootCategory());
// }
//
// TaskUiUtil.refreshAndOpenTaskListElement(newTask);
// MylarTaskListPlugin.getSynchronizationManager().synchNow(0);
//
// return true;
// }