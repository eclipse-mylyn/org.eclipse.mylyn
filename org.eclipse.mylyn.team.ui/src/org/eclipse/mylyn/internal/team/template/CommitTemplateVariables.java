/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.mylar.internal.team.template;

import java.util.List;
import java.util.Locale;

import org.eclipse.mylar.internal.tasks.ui.editors.TaskPlanningEditor;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.team.AbstractCommitTemplateVariable;

/**
 * @author Eike Stepper
 * @author Mik Kersten
 */
public class CommitTemplateVariables {

	public static String implode(List<String> list, String separator) {
		StringBuilder builder = new StringBuilder();
		for (String cc : list) {
			if (builder.length() != 0) {
				builder.append(separator);
			}

			builder.append(cc);
		}

		return builder.toString();
	}
	
	public static class ConnectorTaskPrefix extends AbstractCommitTemplateVariable {

		@Override
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector((AbstractRepositoryTask)task);
				if (connector != null) {
					return connector.getTaskIdPrefix();
				}
			}
			return null;
		}
		
	}
	
	public static class RepositoryKind extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getRepositoryKind();
			}
			return null;
		}
	}

	public static class RepositoryUrl extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getRepositoryUrl();
			}

			return null;
		}
	}

	public static class TaskProduct extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getTaskData().getProduct();
			}

			return null;
		}
	}

	public static class TaskAssignee extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getTaskData().getAssignedTo();
			}

			return null;
		}
	}

	public static class TaskReporter extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getTaskData().getReporter();
			}

			return null;
		}
	}

	public static class TaskResolution extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getTaskData().getResolution();
			}

			return null;
		}
	}

	public static class TaskStatus extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask && ((AbstractRepositoryTask)task).getTaskData() != null) {
				return ((AbstractRepositoryTask) task).getTaskData().getStatus().toUpperCase(Locale.ENGLISH);
			} else {
				// TODO: refactor completion labels
				if (task.isCompleted()) {
					return TaskPlanningEditor.LABEL_COMPLETE;
				} else {
					return TaskPlanningEditor.LABEL_INCOMPLETE;
				}
			}
		}
	}

	public static class TaskCc extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				List<String> list = ((AbstractRepositoryTask) task).getTaskData().getCC();
				return implode(list, ", ");
			}

			return null;
		}
	}

	public static class TaskKeywords extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				List<String> list = ((AbstractRepositoryTask) task).getTaskData().getKeywords();
				return implode(list, ", ");
			}

			return null;
		}
	}

	public static class TaskLastModified extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getTaskData().getLastModified();
			}

			return null;
		}
	}

	public static class TaskSummary extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getTaskData().getSummary();
			}

			return task.getSummary();
		}
	}

	public static class TaskDescription extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			return task.getSummary();
		}
	}

	public static class TaskHandle extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			return task.getHandleIdentifier();
		}
	}

	public static class TaskId extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask)task).getIdentifyingLabel();
//				return AbstractRepositoryTask.getTaskId(task.getHandleIdentifier());
			} else {
				return null;
			}
		}
	}

	public static class TaskNotes extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			return task.getNotes();
		}
	}

	public static class TaskPriority extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			return task.getPriority();
		}
	}

	public static class TaskType extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			return task.getTaskKind();
		}
	}

	public static class TaskURL extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			return task.getTaskUrl();
		}
	}

	/**
	 * @author Eike Stepper
	 */
	protected static abstract class CommitTemplateDate extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			java.util.Date date = getDate(task);
			return formatDate(date);
		}

		protected String formatDate(java.util.Date date) {
			return date.toString();
		}

		protected abstract java.util.Date getDate(ITask task);

		/**
		 * @author Eike Stepper
		 */
		public static class TaskCompletion extends CommitTemplateDate {
			@Override
			protected java.util.Date getDate(ITask task) {
				return task.getCompletionDate();
			}
		}

		/**
		 * @author Eike Stepper
		 */
		public static class TaskCreation extends CommitTemplateDate {
			@Override
			protected java.util.Date getDate(ITask task) {
				return task.getCreationDate();
			}
		}

		/**
		 * @author Eike Stepper
		 */
		public static class TaskReminder extends CommitTemplateDate {
			@Override
			protected java.util.Date getDate(ITask task) {
				return task.getScheduledForDate();
			}
		}
	}
}
