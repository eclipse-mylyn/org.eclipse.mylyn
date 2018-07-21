/*******************************************************************************
 * Copyright (c) 2010, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Itema AS - bug 326761: switched to use common service message control
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.notifications;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.mylyn.commons.notifications.feed.IServiceMessageListener;
import org.eclipse.mylyn.commons.notifications.feed.ServiceMessageEvent;
import org.eclipse.mylyn.commons.notifications.ui.NotificationControl;
import org.eclipse.mylyn.internal.commons.notifications.feed.ServiceMessage;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.AddRepositoryAction;
import org.eclipse.mylyn.internal.tasks.ui.preferences.TasksUiPreferencePage;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.wizards.Messages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 * @author Torkild U. Resheim
 */
@SuppressWarnings("restriction")
public class TaskListServiceMessageControl extends NotificationControl implements IServiceMessageListener {

	private ServiceMessage currentMessage;

	public TaskListServiceMessageControl(Composite parent) {
		super(parent);
		setPreferencesPageId(TasksUiPreferencePage.ID);
	}

	static ExecutionEvent createExecutionEvent(Command command, IHandlerService handlerService) {
		return new ExecutionEvent(command, Collections.emptyMap(), null,
				TasksUiInternal.createDiscoveryWizardEvaluationContext(handlerService));
	}

	@Override
	protected void closeMessage() {
		if (currentMessage != null && currentMessage.getId() != null && !currentMessage.getId().equals("0")) { //$NON-NLS-1$
			TasksUiPlugin.getDefault()
					.getPreferenceStore()
					.setValue(ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_ID, currentMessage.getId());
		}
		close();
	}

	public void handleEvent(final ServiceMessageEvent event) {
		switch (event.getEventKind()) {
		case MESSAGE_UPDATE:
			IPreferenceStore preferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
			preferenceStore.setValue(ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_CHECKTIME, new Date().getTime());
			String lastMessageId = preferenceStore.getString(ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_ID);
			if (lastMessageId != null && lastMessageId.startsWith("org.eclipse.mylyn.reset.")) { //$NON-NLS-1$
				lastMessageId = ""; //$NON-NLS-1$
				preferenceStore.setValue(ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_ID, lastMessageId);
			}

			for (final ServiceMessage message : event.getMessages()) {
				if (!message.isValid() || message.getId().equals("-1")) { //$NON-NLS-1$
					continue;
				}

				if (message.getId().compareTo(lastMessageId) > 0) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							setMessage(message);
							// event id is not used but must be set to make configure link visible
							setEventId(""); //$NON-NLS-1$
						}
					});
					break;
				}
			}
			break;
		case STOP:
			close();
			break;
		}
	}

	public void setMessage(ServiceMessage message) {
		if (message != null) {
			ensureControl();
			if (message.getETag() != null && message.getLastModified() != null) {
				IPreferenceStore preferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
				preferenceStore.setValue(ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_ETAG, message.getETag());
				preferenceStore.setValue(ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_LAST_MODIFIED,
						message.getLastModified());
			}

			this.currentMessage = message;

			setTitle(message.getTitle());
			setTitleImage(Dialog.getImage(message.getImage()));
			setDescription(message.getDescription());
		}
	}

	@Override
	public SelectionListener getLinkListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.text == null) {
					return;
				}

				final String action = getAction(e.text);
				if ("create-local-task".equals(action)) { //$NON-NLS-1$
					closeMessage();
					LocalTask task = TasksUiInternal.createNewLocalTask(null);
					TasksUiUtil.openTask(task);
				} else if ("connect".equals(action)) { //$NON-NLS-1$
					closeMessage();
					new AddRepositoryAction().run();
				} else if ("discovery".equals(action)) { //$NON-NLS-1$
					closeMessage();
					final Command discoveryWizardCommand = TasksUiInternal.getConfiguredDiscoveryWizardCommand();
					if (discoveryWizardCommand != null && discoveryWizardCommand.isEnabled()) {
						IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
								IHandlerService.class);
						try {
							handlerService.executeCommand(discoveryWizardCommand.getId(), null);
						} catch (Exception e1) {
							IStatus status = new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, NLS.bind(
									Messages.SelectRepositoryConnectorPage_discoveryProblemMessage,
									new Object[] { e1.getMessage() }), e1);
							TasksUiInternal.logAndDisplayStatus(
									Messages.SelectRepositoryConnectorPage_discoveryProblemTitle, status);
						}
					}
				} else if (TasksUiInternal.isValidUrl(e.text)) {
					TasksUiUtil.openUrl(e.text);
				} else if (currentMessage != null) {
					SafeRunner.run(new SafeRunnable() {
						public void run() throws Exception {
							if (currentMessage.openLink(action)) {
								closeMessage();
							}
						}
					});
				}
			}

		};
	}

	/**
	 * Extracts action from query part or a URL if applicable.
	 */
	public static String getAction(String action) {
		if (action.startsWith("http")) { //$NON-NLS-1$
			URL url;
			try {
				url = new URL(action);
				String query = url.getQuery();
				if (query != null && query.startsWith("action=")) { //$NON-NLS-1$
					int i = query.indexOf("&"); //$NON-NLS-1$
					if (i != -1) {
						action = query.substring(7, i);
					} else {
						action = query.substring(7);
					}
				} else {
					return null;
				}
			} catch (MalformedURLException e1) {
				// ignore
				return null;
			}
		}
		return action.toLowerCase();
	}

}
