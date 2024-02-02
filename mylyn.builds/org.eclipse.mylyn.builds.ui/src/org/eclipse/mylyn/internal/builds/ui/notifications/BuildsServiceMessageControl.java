/*******************************************************************************
 * Copyright (c) 2010, 2013 Itema AS and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Itema AS - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.notifications;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.builds.ui.spi.BuildServerWizard;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.notifications.core.NotificationSinkEvent;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.mylyn.commons.notifications.ui.NotificationControl;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.builds.ui.view.NewBuildServerAction;
import org.eclipse.mylyn.internal.commons.repositories.ui.wizards.NewRepositoryWizardRegistry;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.wizards.IWizardDescriptor;

/**
 * GUI control designed to display build service messages. These messages can contain links that may be clicked by the user and handled
 * here. The following link formats are allowed:
 * <ul>
 * <li><code>create</code> - Calls the create wizard selection dialog.</li>
 * <li><code>discover:&lt;id&gt;?&lt;arguments&gt;</code> - Calls the wizard with the specified id and passes along the given
 * arguments.</li>
 * </ul>
 * <p>
 * An exact list of which properties are supported can be found in {@link RepositoryLocation}.
 * </p>
 * <p>
 * A stack of messages is maintained. When a new message arrives it is placed on top of the stack and displayed.
 * </p>
 * <p>
 * The code was lifted from org.eclipse.mylyn.internal.tasks.ui.notifications and modified to be suitable for this use. It also has been
 * simplified a bit.
 * </p>
 * 
 * @author Torkild U. Resheim
 */
public class BuildsServiceMessageControl extends NotificationControl {

	private final CopyOnWriteArrayList<AbstractNotification> messages;

	//private final Stack<AbstractNotification> messages;

	public BuildsServiceMessageControl(Composite parent) {
		super(parent);
		messages = new CopyOnWriteArrayList<>();
	}

	@Override
	protected void closeMessage() {
		if (messages.isEmpty()) {
			close();
		} else {
			nextMessage();
		}
	}

	private void nextMessage() {
		if (!messages.isEmpty()) {
			AbstractNotification message = messages.remove(0);
			if (ensureControl()) {
				setTitle(message.getLabel());
				setDescription(message.getDescription());
				if (message instanceof AbstractUiNotification) {
					setTitleImage(((AbstractUiNotification) message).getNotificationKindImage());
				}
				setEventId(message.getEventId());
			}
		}
	}

	/**
	 * Opens a new repository wizard using the given data.
	 * 
	 * @param data
	 */
	private void openRepositoryWizard(String data) {
		String connectorKind = data;
		if (data.indexOf('?') > -1) {
			connectorKind = data.substring(0, data.indexOf('?'));
		}
		// Get the Mylyn repository wizard
		IWizardDescriptor descriptor = NewRepositoryWizardRegistry.getInstance().findWizard(connectorKind);
		try {
			// Then if we have a wizard, open it.
			if (descriptor != null) {
				// This will use the default constructor so we need to convey some data.
				IWizard wizard = descriptor.createWizard();

				if (wizard instanceof BuildServerWizard) {
					try {

						// Set data we got from the discovery mechanism. This
						// comes as a list of properties with keys and values.
						HashMap<String, String> properties = new HashMap<>();
						int i = data.indexOf('?');
						if (i > -1) {
							String[] props = data.substring(i + 1).split("&"); //$NON-NLS-1$
							for (String set : props) {
								String[] kv = set.split("="); //$NON-NLS-1$
								properties.put(kv[0], kv[1]);
							}
						}
						IBuildServer bs = BuildsUi.createServer(connectorKind);
						RepositoryLocation rl = new RepositoryLocation(properties);
						bs.getLocation().apply(rl);
						((BuildServerWizard) wizard).setBuildServer(bs);
					} catch (Exception e) {
						StatusHandler.log(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
								Messages.BuildsServiceMessageControl_ErrorStartingWizard, e));
					}
				}
				WizardDialog wd = new WizardDialog(getShell(), wizard);
				wd.setTitle(wizard.getWindowTitle());
				wd.open();
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
					Messages.BuildsServiceMessageControl_ErrorStartingWizard, e));
		}
	}

	@Override
	protected SelectionListener getLinkListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.text != null) {
					String cmd = null;
					String args = null;
					if (e.text.contains(":")) { //$NON-NLS-1$
						cmd = e.text.substring(0, e.text.indexOf(':')).toLowerCase();
						args = e.text.substring(e.text.indexOf(':') + 1);
					} else {
						cmd = e.text.toLowerCase();
					}
					if (cmd.equals("create")) { //$NON-NLS-1$
						closeMessage();
						new NewBuildServerAction().run();
					} else if (cmd.equals("discover")) { //$NON-NLS-1$
						closeMessage();
						if (args != null) {
							openRepositoryWizard(args);
						}
					}
				}
			}
		};
	}

	public void notify(final NotificationSinkEvent event) {
		messages.addAll(event.getNotifications());
		// Show the next message but only if we're currently not
		// showing any messages.
		Display.getDefault().asyncExec(() -> {
			if (isClosed()) {
				nextMessage();
			}
		});
	}
}
