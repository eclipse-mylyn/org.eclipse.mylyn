/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContextManager;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.context.ui.ContextUi;

/**
 * Closes editors based on interest.
 *
 * @author Mik Kersten
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class ContextEditorManager {

	private final AbstractContextListener contextListener = new AbstractContextListener() {
		@Override
		public void contextChanged(ContextChangeEvent event) {
			switch (event.getEventKind()) {
				case INTEREST_CHANGED:
					for (IInteractionElement element : event.getElements()) {
						closeEditor(element, false);
					}
					break;
				case ELEMENTS_DELETED:
					for (IInteractionElement element : event.getElements()) {
						closeEditor(element, true);
					}
					break;
			}
		}
	};

	public ContextEditorManager() {
	}

	public void start(IInteractionContextManager contextManager) {
		contextManager.addListener(contextListener);
	}

	public void stop(IInteractionContextManager contextManager) {
		contextManager.removeListener(contextListener);
	}

	private void closeEditor(IInteractionElement element, boolean force) {
		if (!isEnabled()) {
			return;
		}

		if (force || !element.getInterest().isInteresting()) {
			AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(element.getContentType());
			if (bridge.isDocument(element.getHandleIdentifier())) {
				AbstractContextUiBridge uiBridge = ContextUi.getUiBridge(element.getContentType());
				uiBridge.close(element);
			}
		}
	}

	private boolean isEnabled() {
		return ContextUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(IContextUiPreferenceContstants.AUTO_MANAGE_EDITORS);
	}

}
