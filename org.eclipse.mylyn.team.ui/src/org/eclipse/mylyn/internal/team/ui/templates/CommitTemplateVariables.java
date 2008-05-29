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
package org.eclipse.mylyn.internal.team.ui.templates;

import java.util.Date;
import java.util.Locale;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.team.ui.AbstractCommitTemplateVariable;

/**
 * @author Eike Stepper
 * @author Mik Kersten
 * @author Steffen Pingel
 */
// TODO refactor into extension point 
public class CommitTemplateVariables {

	// TODO refactor completion labels
	private static final String LABEL_INCOMPLETE = "Incomplete";

	private static final String LABEL_COMPLETE = "Complete";

	private static String implode(String[] list, String separator) {
		if (list == null) {
			return null;
		}
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
			if (task != null) {
				AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
						task.getConnectorKind());
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
			if (task != null) {
				return task.getConnectorKind();
			}
			return null;
		}
	}

	public static class RepositoryUrl extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task != null) {
				return task.getRepositoryUrl();
			}

			return null;
		}
	}

	public static class TaskProduct extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			ITaskMapping taskMapping = getTaskMapping(task);
			if (taskMapping != null) {
				return taskMapping.getProduct();
			}
			return null;
		}
	}

	public static class TaskAssignee extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			ITaskMapping taskMapping = getTaskMapping(task);
			if (taskMapping != null) {
				return taskMapping.getOwner();
			}
			return null;
		}
	}

	public static class TaskReporter extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			ITaskMapping taskMapping = getTaskMapping(task);
			if (taskMapping != null) {
				return taskMapping.getReporter();
			}
			return null;
		}
	}

	public static class TaskResolution extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			ITaskMapping taskMapping = getTaskMapping(task);
			if (taskMapping != null) {
				return taskMapping.getResolution();
			}
			return null;
		}
	}

	public static class TaskStatus extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			ITaskMapping taskMapping = getTaskMapping(task);
			if (taskMapping != null) {
				String status = taskMapping.getTaskStatus();
				if (status != null) {
					return status.toUpperCase(Locale.ENGLISH);
				}
			}
			if (task != null) {
				if (task.isCompleted()) {
					return LABEL_COMPLETE;
				} else {
					return LABEL_INCOMPLETE;
				}
			}
			return null;
		}
	}

	public static class TaskCc extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			ITaskMapping taskMapping = getTaskMapping(task);
			if (taskMapping != null) {
				String[] list = taskMapping.getCc();
				return implode(list, ", ");
			}
			return null;
		}
	}

	public static class TaskKeywords extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			ITaskMapping taskMapping = getTaskMapping(task);
			if (taskMapping != null) {
				String[] list = getTaskMapping(task).getKeywords();
				return implode(list, ", ");
			}
			return null;
		}
	}

	public static class TaskLastModified extends CommitTemplateDate {
		@Override
		protected Date getDate(ITask task) {
			ITaskMapping taskMapping = getTaskMapping(task);
			if (taskMapping != null) {
				return taskMapping.getModificationDate();
			}
			return null;
		}
	}

	public static class TaskSummary extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			ITaskMapping taskMapping = getTaskMapping(task);
			if (taskMapping != null) {
				return getTaskMapping(task).getSummary();
			}
			return "";
		}
	}

	public static class TaskDescription extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task != null) {
				return task.getSummary();
			}
			return "";
		}
	}

	public static class TaskId extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task != null) {
				return task.getTaskId();
			} else {
				return null;
			}
		}
	}

	public static class TaskKey extends AbstractCommitTemplateVariable {
		@Override
		public String getValue(ITask task) {
			if (task != null) {
				String value = task.getTaskKey();
				if (value == null) {
					value = task.getTaskId();
				}
				return value;
			} else {
				return null;
			}
		}
	}

	public static class TaskNotes extends AbstractCommitTemplateVariable {
		@SuppressWarnings("restriction")
		@Override
		public String getValue(ITask task) {
			if (task instanceof AbstractTask) {
				return ((AbstractTask) task).getNotes();
			} else {
				return "";
			}
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
			return task.getUrl();
		}
	}

	public static ITaskMapping getTaskMapping(ITask task) {
		if (task != null) {
			TaskData taskData;
			try {
				taskData = TasksUi.getTaskDataManager().getTaskData(task);
				if (taskData != null) {
					AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
							task.getConnectorKind());
					return connector.getTaskMapping(taskData);
				}
			} catch (CoreException e) {
				// ignore
			}
		}
		return null;
	}

	/**
	 * @author Eike Stepper
	 */
	public static class TaskCompletion extends CommitTemplateDate {
		@Override
		protected Date getDate(ITask task) {
			return task.getCompletionDate();
		}
	}

	/**
	 * @author Eike Stepper
	 */
	public static class TaskCreation extends CommitTemplateDate {
		@Override
		protected Date getDate(ITask task) {
			return task.getCreationDate();
		}
	}

	/**
	 * @author Eike Stepper
	 */
	public static class TaskReminder extends CommitTemplateDate {
		@Override
		protected Date getDate(ITask task) {
//			 TODO: Hide this field?
			return ((AbstractTask) task).getScheduledForDate().getStartDate().getTime();
		}
	}

	/**
	 * @author Eike Stepper
	 */
	private static abstract class CommitTemplateDate extends AbstractCommitTemplateVariable {

		@Override
		public String getValue(ITask task) {
			java.util.Date date = getDate(task);
			return formatDate(date);
		}

		protected String formatDate(java.util.Date date) {
			return date.toString();
		}

		protected abstract java.util.Date getDate(ITask task);

	}

}
