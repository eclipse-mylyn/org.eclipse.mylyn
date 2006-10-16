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

import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.team.ICommitTemplateVariable;

import java.util.List;

/**
 * @author Eike Stepper
 * @author Mik Kersten
 */
public abstract class CommitTemplateVariable implements ICommitTemplateVariable {
	
	protected String description;

	protected String recognizedKeyword;

	public String getDescription() {
		return description != null ? description : "Handler for '" + recognizedKeyword + "'";
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRecognizedKeyword() {
		return recognizedKeyword;
	}

	public void setRecognizedKeyword(String recognizedKeyword) {
		if (recognizedKeyword == null) {
			throw new IllegalArgumentException("Keyword to recognize must not be null"); //$NON-NLS-1$
		}

		this.recognizedKeyword = recognizedKeyword;
	}

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
	
	public static class ConnectorTaskPrefix extends CommitTemplateVariable {

		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector((AbstractRepositoryTask)task);
				if (connector != null) {
					return connector.getTaskPrefix();
				}
			}
			return null;
		}
		
	}
	
	public static class RepositoryKind extends CommitTemplateVariable {
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getRepositoryKind();
			}
			return null;
		}
	}

	public static class RepositoryUrl extends CommitTemplateVariable {
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getRepositoryUrl();
			}

			return null;
		}
	}

	public static class TaskProduct extends CommitTemplateVariable {
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getTaskData().getProduct();
			}

			return null;
		}
	}

	public static class TaskAssignee extends CommitTemplateVariable {
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getTaskData().getAssignedTo();
			}

			return null;
		}
	}

	public static class TaskReporter extends CommitTemplateVariable {
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getTaskData().getReporter();
			}

			return null;
		}
	}

	public static class TaskResolution extends CommitTemplateVariable {
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getTaskData().getResolution();
			}

			return null;
		}
	}

	public static class TaskStatus extends CommitTemplateVariable {
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getTaskData().getStatus().toUpperCase();
			}

			return null;
		}
	}

	public static class TaskCc extends CommitTemplateVariable {
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				List<String> list = ((AbstractRepositoryTask) task).getTaskData().getCC();
				return implode(list, ", ");
			}

			return null;
		}
	}

	public static class TaskKeywords extends CommitTemplateVariable {
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				List<String> list = ((AbstractRepositoryTask) task).getTaskData().getKeywords();
				return implode(list, ", ");
			}

			return null;
		}
	}

	public static class TaskLastModified extends CommitTemplateVariable {
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getTaskData().getLastModified();
			}

			return null;
		}
	}

	public static class TaskSummary extends CommitTemplateVariable {
		public String getValue(ITask task) {
			if (task instanceof AbstractRepositoryTask) {
				return ((AbstractRepositoryTask) task).getTaskData().getSummary();
			}

			return task.getDescription();
		}
	}

	public static class TaskDescription extends CommitTemplateVariable {
		public String getValue(ITask task) {
			return task.getDescription();
		}
	}

	public static class TaskHandle extends CommitTemplateVariable {
		public String getValue(ITask task) {
			return task.getHandleIdentifier();
		}
	}

	public static class TaskID extends CommitTemplateVariable {
		public String getValue(ITask task) {
			return AbstractRepositoryTask.getTaskId(task.getHandleIdentifier());
		}
	}

	public static class TaskNotes extends CommitTemplateVariable {
		public String getValue(ITask task) {
			return task.getNotes();
		}
	}

	public static class TaskPriority extends CommitTemplateVariable {
		public String getValue(ITask task) {
			return task.getPriority();
		}
	}

	public static class TaskType extends CommitTemplateVariable {
		public String getValue(ITask task) {
			return task.getTaskType();
		}
	}

	public static class TaskURL extends CommitTemplateVariable {
		public String getValue(ITask task) {
			return task.getUrl();
		}
	}

	/**
	 * @author Eike Stepper
	 */
	protected static abstract class CommitTemplateDate extends CommitTemplateVariable {
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
				return task.getReminderDate();
			}
		}
	}
}
