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

package org.eclipse.mylyn.internal.context.ui.state;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.CommonListenerList;
import org.eclipse.mylyn.commons.core.CommonListenerList.Notifier;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.ui.XMLMemento;

/**
 * @author Steffen Pingel
 */
public class ContextStateManager {

	private static final String TAG_CONTEXT_STATE = "ContextState"; //$NON-NLS-1$

	private static final String CHARSET = "UTF-8"; //$NON-NLS-1$

	private final CommonListenerList<ContextStateParticipant> participants;

	private final ContextState defaultState;

	public ContextStateManager() {
		participants = new CommonListenerList<>(ContextUiPlugin.ID_PLUGIN);
		defaultState = createMemento(null, "default"); //$NON-NLS-1$
	}

	public void addParticipant(ContextStateParticipant participant) {
		participants.add(participant);
	}

	public void clearState(final String contextHandle, final boolean isActiveContext) {
		participants.notify(new Notifier<ContextStateParticipant>() {
			@Override
			public void run(ContextStateParticipant participant) throws Exception {
				participant.clearState(contextHandle, isActiveContext);
			}
		});
	}

	public ContextState createMemento(IInteractionContext context, String contextHandle) {
		return new ContextState(context, contextHandle, XMLMemento.createWriteRoot(TAG_CONTEXT_STATE));
	}

	public ContextState read(IInteractionContext context, InputStream in) {
		ContextState memento = null;
		if (in != null) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, CHARSET));
				try (reader) {
					XMLMemento xmlMemento = XMLMemento.createReadRoot(reader);
					return new ContextState(context, context.getHandleIdentifier(), xmlMemento);
				}
			} catch (IOException | CoreException e) {
				StatusHandler.log(
						new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Failed to restore context state", e)); //$NON-NLS-1$
			}
		}

		if (memento == null) {
			memento = createMemento(context, context.getHandleIdentifier());
		}
		return memento;
	}

	public void removeParticipant(ContextStateParticipant participant) {
		participants.remove(participant);
	}

	public void restoreDefaultState() {
		participants.notify(new Notifier<ContextStateParticipant>() {
			@Override
			public void run(ContextStateParticipant participant) throws Exception {
				if (participant.isEnabled()) {
					participant.restoreDefaultState(defaultState);
				}
			}
		});
	}

	public void restoreState(IInteractionContext context, InputStream in) {
		final ContextState memento = read(context, in);
		participants.notify(new Notifier<ContextStateParticipant>() {
			@Override
			public void run(ContextStateParticipant participant) throws Exception {
				if (participant.isEnabled()) {
					participant.restoreState(memento);
				}
			}
		});
	}

	public void saveDefaultState() {
		participants.notify(new Notifier<ContextStateParticipant>() {
			@Override
			public void run(ContextStateParticipant participant) throws Exception {
				if (participant.isEnabled()) {
					participant.saveDefaultState(defaultState);
				}
			}
		});
	}

	public void saveState(IInteractionContext context, OutputStream storable) {
		saveState(context, storable, false);
	}

	public void saveState(IInteractionContext context, OutputStream storable, final boolean allowModifications) {
		final ContextState memento = createMemento(context, context.getHandleIdentifier());
		participants.notify(new Notifier<ContextStateParticipant>() {
			@Override
			public void run(ContextStateParticipant participant) throws Exception {
				if (participant.isEnabled()) {
					participant.saveState(memento, allowModifications);
				}
			}
		});

		write(storable, memento);
	}

	public void write(OutputStream out, ContextState memento) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, CHARSET));
			try (writer) {
				memento.getMemento().save(writer);
			}
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Failed to save context state", e)); //$NON-NLS-1$
		}
	}

}
