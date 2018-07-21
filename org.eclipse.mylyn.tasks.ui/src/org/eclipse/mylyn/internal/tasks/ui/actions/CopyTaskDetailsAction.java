/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.ui.ClipboardCopier;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @author Thomas Ehrnhoefer
 */
public class CopyTaskDetailsAction extends BaseSelectionListenerAction {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.copy"; //$NON-NLS-1$

	public enum Mode {
		KEY(Messages.CopyTaskDetailsAction_ID_Menu_Label), URL(
				Messages.CopyTaskDetailsAction_Url_Menu_Label), ID_SUMMARY(
						Messages.CopyTaskDetailsAction_ID_Summary_Menu_Label), ID_SUMMARY_URL(
								Messages.CopyTaskDetailsAction_ID_Summary_and_Url_Menu_Label);
		/**
		 * @deprecated Use {@link #ID_SUMMARY} instead
		 */
		@Deprecated
		public static Mode SUMMARY = ID_SUMMARY;

		/**
		 * @deprecated Use {@link #ID_SUMMARY_URL} instead
		 */
		@Deprecated
		public static Mode SUMMARY_URL = ID_SUMMARY_URL;

		private String message;

		private Mode(String message) {
			this.message = message;
		}
	}

	private Mode mode;

	public CopyTaskDetailsAction(Mode mode) {
		super(""); //$NON-NLS-1$
		setMode(mode);
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		Assert.isNotNull(mode);
		this.mode = mode;
		setText(mode.message);
	}

	@Override
	public void run() {
		ClipboardCopier.getDefault().copy(getStructuredSelection(), new ClipboardCopier.TextProvider() {
			public String getTextForElement(Object element) {
				return getTextForTask(element, getMode());
			}
		});
	}

	public static String getTextForTask(Object object) {
		return getTextForTask(object, Mode.ID_SUMMARY_URL);
	}

	// TODO move to TasksUiUtil / into core
	public static String getTextForTask(Object object, Mode mode) {
		StringBuffer sb = new StringBuffer();
		switch (mode) {
		case KEY:
			if (object instanceof ITask) {
				ITask task = (ITask) object;
				if (task.getTaskKey() != null) {
					sb.append(task.getTaskKey());
				}
			}
			break;
		case URL:
			if (object instanceof IRepositoryElement) {
				String taskUrl = getUrl((IRepositoryElement) object);
				if (TasksUiInternal.isValidUrl(taskUrl)) {
					sb.append(taskUrl);
				}
			}
			break;
		case ID_SUMMARY:
			if (object instanceof ITask) {
				ITask task = (ITask) object;
				if (task.getTaskKey() != null) {
					sb.append(StringUtils.capitalize(TasksUiInternal.getTaskPrefix(task.getConnectorKind())));
					sb.append(task.getTaskKey());
					sb.append(": "); //$NON-NLS-1$
				}
				sb.append(task.getSummary());
			} else if (object instanceof IRepositoryElement) {
				IRepositoryElement element = (IRepositoryElement) object;
				sb.append(element.getSummary());
			}
			break;
		case ID_SUMMARY_URL:
			if (object instanceof ITask) {
				ITask task = (ITask) object;
				if (task.getTaskKey() != null) {
					sb.append(task.getTaskKey());
					sb.append(": "); //$NON-NLS-1$
				}

				sb.append(task.getSummary());
				String taskUrl = getUrl((IRepositoryElement) object);
				if (TasksUiInternal.isValidUrl(taskUrl)) {
					sb.append(ClipboardCopier.LINE_SEPARATOR);
					sb.append(taskUrl);
				}
			} else if (object instanceof IRepositoryQuery) {
				RepositoryQuery query = (RepositoryQuery) object;
				sb.append(query.getSummary());
				if (TasksUiInternal.isValidUrl(query.getUrl())) {
					sb.append(ClipboardCopier.LINE_SEPARATOR);
					sb.append(query.getUrl());
				}
			} else if (object instanceof IRepositoryElement) {
				IRepositoryElement element = (IRepositoryElement) object;
				sb.append(element.getSummary());
			}
			break;
		}
		return sb.toString();
	}

	private static String getUrl(IRepositoryElement element) {
		if (element instanceof ITask) {
			ITask task = (ITask) element;
			AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(task.getConnectorKind());
			TaskRepository repository = TasksUiInternal.getRepository(task);
			URL location = connector.getBrowserUrl(repository, element);
			if (location != null) {
				return location.toString();
			} else if (task.getUrl() != null) {
				return task.getUrl();
			} else {
				return connector.getTaskUrl(task.getRepositoryUrl(), task.getTaskId());
			}
		} else if (element.getUrl() != null) {
			return element.getUrl();
		}
		return null;
	}

}
