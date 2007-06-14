package org.eclipse.mylyn.internal.tasks.ui;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.TaskCategory;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Retrieves an existing repository task and adds it to the tasklist
 * 
 * @author Willian Mitsuda
 */
public class AddExistingTaskJob extends Job {

	/**
	 * Task repository whose task will be added
	 */
	private TaskRepository repository;

	/**
	 * Identifies a existing task on the repository
	 */
	private String taskId;

	/**
	 * Optional; informs the task container the task initialy belongs to; if
	 * null, it will be added to the current selected task's category in task
	 * list
	 */
	private AbstractTaskCategory taskContainer;

	public AddExistingTaskJob(TaskRepository repository, String taskId) {
		this(repository, taskId, null);
	}

	@Deprecated // Use TaskCategory instead
	public AddExistingTaskJob(TaskRepository repository, String taskId, AbstractTaskCategory taskContainer) {
		super(MessageFormat.format("Adding task: \"{0}\"...", taskId));
		this.repository = repository;
		this.taskId = taskId;
		this.taskContainer = taskContainer;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		final AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getKind());
		try {
			final AbstractTask newTask = connector.createTaskFromExistingId(repository, taskId, monitor);

//			if (newTask instanceof AbstractTask) {
//				// TODO: encapsulate in abstract connector
//				AbstractTask repositoryTask = (AbstractTask) newTask;
//				TasksUiPlugin.getDefault().getTaskDataManager().push(newTask.getHandleIdentifier(),
//						repositoryTask.getTaskData());
//			}

			if (newTask != null) {
				TasksUiUtil.refreshAndOpenTaskListElement(newTask);
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					public void run() {
						AbstractTaskCategory category = taskContainer;
						TaskListView taskListView = TaskListView.getFromActivePerspective();
						if (category == null) {
							Object selectedObject = ((IStructuredSelection) taskListView.getViewer().getSelection())
									.getFirstElement();
							if (selectedObject instanceof TaskCategory) {
								category = (TaskCategory) selectedObject;
							} else {
								category = TasksUiPlugin.getTaskListManager().getTaskList().getDefaultCategory();
							}
						}
						TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer(category, newTask);
						taskListView.getViewer().setSelection(new StructuredSelection(newTask));
					}

				});
			} else {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					public void run() {
						IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
						if (window != null) {
							MessageDialog.openWarning(window.getShell(), "Add Existing Task Failed", MessageFormat
									.format("Unable to retrieve task \"{0}\" from repository.", taskId));
						}
					}

				});
			}
		} catch (final CoreException e) {
			MylarStatusHandler.fail(e.getStatus().getException(), e.getMessage(), true);
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}

}
