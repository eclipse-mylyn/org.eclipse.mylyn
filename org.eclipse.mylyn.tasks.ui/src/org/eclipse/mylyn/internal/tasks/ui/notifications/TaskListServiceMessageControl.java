/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Itema AS - bug 326761: switched to use common service message control
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.notifications;

import java.util.Collections;
import java.util.Date;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylyn.internal.provisional.commons.ui.ServiceMessageControl;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.notifications.IServiceMessageListener;
import org.eclipse.mylyn.internal.tasks.core.notifications.ServiceMessage;
import org.eclipse.mylyn.internal.tasks.core.notifications.ServiceMessageEvent;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.AddRepositoryAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.wizards.Messages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.osgi.framework.Version;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 * @author Torkild U. Resheim
 */
public class TaskListServiceMessageControl extends ServiceMessageControl implements IServiceMessageListener {

	private ServiceMessage currentMessage;

	public TaskListServiceMessageControl(Composite parent) {
		super(parent);
	}

	static ExecutionEvent createExecutionEvent(Command command, IHandlerService handlerService) {
		return new ExecutionEvent(command, Collections.emptyMap(), null,
				TasksUiInternal.createDiscoveryWizardEvaluationContext(handlerService));
	}

	@Override
	protected void closeMessage() {
		if (currentMessage != null) {
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

			for (final ServiceMessage message : event.getMessages()) {
				if (!message.isValid() || message.getId().equals("-1")) { //$NON-NLS-1$
					continue;
				}

				if (!lastMessageId.equals(message.getId()) && isForCurrentVersion(message)) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							setMessage(message);
						}
					});
				}
			}
			break;
		case STOP:
			close();
			break;
		}
	}

	private boolean isForCurrentVersion(ServiceMessage message) {
		if (message.getVersion() == null) {
			return true;
		}

		try {
			VersionRange version = new VersionRange(message.getVersion());
			String versionString = TasksUiPlugin.getDefault().getBundle().getHeaders().get("Bundle-Version"); //$NON-NLS-1$
			return version.isIncluded(new Version(versionString));
		} catch (IllegalArgumentException e) {
			// invalid version range
			return false;
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
			setDescription(message.getDescription());
			setTitleImage(Dialog.getImage(message.getImage()));
		}
	}

	@Override
	public SelectionListener getLinkListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.text != null) {
					String action = e.text.toLowerCase();
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
					}
				}
			}
		};
	}

}
