/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.tasks.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.externalization.ExternalizationManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivationListener;
import org.eclipse.mylyn.tasks.core.ITaskActivityManager;
import org.eclipse.mylyn.tasks.core.TaskActivationAdapter;
import org.eclipse.mylyn.tasks.core.activity.AbstractTaskActivityMonitor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * Monitors task activity and maintains task activation history
 * 
 * @author Robert Elves
 * @author Steffen Pingel
 * @since 3.0
 */
@SuppressWarnings("restriction")
public class TaskActivityMonitor extends AbstractTaskActivityMonitor {

	public static class ContextTaskActivationListener extends TaskActivationAdapter {

		@Override
		public boolean canDeactivateTask(ITask task) {
			List<IEditorReference> dirtyRefs = findDirtyEditors();
			if (dirtyRefs.size() == 1) {
				IWorkbenchPart part = dirtyRefs.get(0).getPart(false);
				if (part instanceof TaskEditor) {
					// If the only dirty editor is the active task and the editor is active, do not display the dialog below
					TaskEditor editor = ((TaskEditor) part);
					TaskEditorInput input = editor.getTaskEditorInput();
					if (input != null && task.equals(input.getTask()) && editor.equals(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor())) {
						return true;
					}
				}
			}
			if (!dirtyRefs.isEmpty()) {
				int returnCode = openTaskDeactivationDialog(dirtyRefs);
				if (returnCode == 0) {
					saveEditors(dirtyRefs);
				} else if (returnCode == 2) {
					activateEditors(dirtyRefs);
					return false;
				}
			}
			return true;
		}

		private void saveEditors(List<IEditorReference> refs) {
			for (IEditorReference ref : refs) {
				IEditorPart editor = ref.getEditor(false);
				if (editor != null) {
					editor.doSave(new NullProgressMonitor());
				}
			}
		}

		private void activateEditors(List<IEditorReference> refs) {
			for (IEditorReference ref : refs) {
				IWorkbenchPart part = ref.getPart(true);
				if (part != null) {
					ref.getPage().activate(part);
				}
			}
		}

		public int openTaskDeactivationDialog(List<IEditorReference> dirtyRefs) {
			String editors = Joiner.on('\n')
					.join(Iterables.transform(dirtyRefs, new Function<IEditorReference, String>() {
						@Override
						public String apply(IEditorReference ref) {
							return ref.getTitle();
						}
					}));

			return new MessageDialog(WorkbenchUtil.getShell(), Messages.TaskActivityMonitor_Task_Deactivation, null,
					NLS.bind(Messages.TaskActivityMonitor_Task_Deactivation_Message, editors), MessageDialog.QUESTION,
					new String[] { Messages.TaskActivityMonitor_Deactivate_Task_and_Save_All,
							Messages.TaskActivityMonitor_Deactivate_and_Save_Some, IDialogConstants.CANCEL_LABEL },
					1).open();
		}

		public List<IEditorReference> findDirtyEditors() {
			List<IEditorReference> dirtyRefs = new ArrayList<IEditorReference>();
			for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
				for (IEditorReference ref : window.getActivePage().getEditorReferences()) {
					if (ref.isDirty()) {
						dirtyRefs.add(ref);
					}
				}
			}
			return dirtyRefs;
		}

		@Override
		public void preTaskActivated(ITask task) {
			// make sure that org.eclipse.mylyn.context.ui is active prior to the first task activation
			ContextUiPlugin.getDefault();
		}

		@Override
		public void taskActivated(final ITask task) {
			ContextCore.getContextManager().activateContext(task.getHandleIdentifier());
		}

		@Override
		public void taskDeactivated(final ITask task) {
			ContextCore.getContextManager().deactivateContext(task.getHandleIdentifier());
		}
	}

	private final AbstractContextListener CONTEXT_LISTENER = new AbstractContextListener() {

		@Override
		public void contextChanged(ContextChangeEvent event) {
			switch (event.getEventKind()) {
			case INTEREST_CHANGED:
				List<InteractionEvent> events = contextManager.getActivityMetaContext().getInteractionHistory();
				if (events.size() > 0) {
					InteractionEvent interactionEvent = events.get(events.size() - 1);
					parseInteractionEvent(interactionEvent, false);
				}
				break;
			}
		}
	};

	private static ITaskActivationListener CONTEXT_TASK_ACTIVATION_LISTENER = new ContextTaskActivationListener();

	private final InteractionContextManager contextManager;

	private TaskActivityManager taskActivityManager;

	private final TaskList taskList;

	private final List<ITask> activationHistory;

	private ActivityExternalizationParticipant externalizationParticipant;

	public TaskActivityMonitor() {
		this.contextManager = ContextCorePlugin.getContextManager();
		this.taskList = TasksUiPlugin.getTaskList();
		this.activationHistory = new ArrayList<ITask>();
	}

	@Override
	public void start(ITaskActivityManager taskActivityManager) {
		this.taskActivityManager = (TaskActivityManager) taskActivityManager;
		taskActivityManager.addActivationListener(CONTEXT_TASK_ACTIVATION_LISTENER);
		contextManager.addActivityMetaContextListener(CONTEXT_LISTENER);

		ExternalizationManager externalizationManager = TasksUiPlugin.getExternalizationManager();
		ActivityExternalizationParticipant ACTIVITY_EXTERNALIZTAION_PARTICIPANT = new ActivityExternalizationParticipant(
				externalizationManager);
		externalizationManager.addParticipant(ACTIVITY_EXTERNALIZTAION_PARTICIPANT);
		taskActivityManager.addActivityListener(ACTIVITY_EXTERNALIZTAION_PARTICIPANT);
		setExternalizationParticipant(ACTIVITY_EXTERNALIZTAION_PARTICIPANT);
	}

	/** public for testing */
	public boolean parseInteractionEvent(InteractionEvent event, boolean isReloading) {
		try {
			if (event.getKind().equals(InteractionEvent.Kind.COMMAND)) {
				if ((event.getDelta().equals(InteractionContextManager.ACTIVITY_DELTA_ACTIVATED))) {
					AbstractTask activatedTask = taskList.getTask(event.getStructureHandle());
					if (activatedTask != null) {
						activationHistory.add(activatedTask);
						return true;
					}
				}
			} else if (event.getKind().equals(InteractionEvent.Kind.ATTENTION)) {
				if ((event.getDelta().equals("added") || event.getDelta().equals("add"))) { //$NON-NLS-1$ //$NON-NLS-2$
					if (event.getDate().getTime() > 0 && event.getEndDate().getTime() > 0) {
						if (event.getStructureKind()
								.equals(InteractionContextManager.ACTIVITY_STRUCTUREKIND_WORKINGSET)) {
							taskActivityManager.addWorkingSetElapsedTime(event.getStructureHandle(), event.getDate(),
									event.getEndDate());
							if (!isReloading) {
								externalizationParticipant.setDirty(true);
								// save not requested for working set time updates so...
								externalizationParticipant.elapsedTimeUpdated(null, 0);
							}
						} else {
							AbstractTask activatedTask = taskList.getTask(event.getStructureHandle());
							if (activatedTask != null) {
								taskActivityManager.addElapsedTime(activatedTask, event.getDate(), event.getEndDate());
							}
						}
					}
				} else if (event.getDelta().equals("removed")) { //$NON-NLS-1$
					ITask task = taskList.getTask(event.getStructureHandle());
					if (task != null) {
						taskActivityManager.removeElapsedTime(task, event.getDate(), event.getEndDate());
					}
				}
			}
		} catch (Throwable t) {
			StatusHandler.log(
					new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Error parsing interaction event", t)); //$NON-NLS-1$
		}
		return false;
	}

	@Override
	public void stop() {
		contextManager.removeActivityMetaContextListener(CONTEXT_LISTENER);
	}

	@Override
	public void reloadActivityTime() {
		activationHistory.clear();
		taskActivityManager.clearActivity();
		List<InteractionEvent> events = contextManager.getActivityMetaContext().getInteractionHistory();
		for (InteractionEvent event : events) {
			parseInteractionEvent(event, true);
		}
	}

	public void setExternalizationParticipant(ActivityExternalizationParticipant participant) {
		this.externalizationParticipant = participant;
	}

	@Override
	public List<ITask> getActivationHistory() {
		return new ArrayList<ITask>(activationHistory);
	}

	@Override
	public void loadActivityTime() {
		ContextCorePlugin.getContextManager().loadActivityMetaContext();
		reloadActivityTime();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
