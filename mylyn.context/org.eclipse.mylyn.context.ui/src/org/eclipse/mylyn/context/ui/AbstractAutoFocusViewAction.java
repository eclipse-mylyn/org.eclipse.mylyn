/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.IContextUiPreferenceContstants;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * Extend to focus a view on task context, e.g. the filtering and expansion of a tree view such as the Package Explorer. A structure bridge
 * should be implemented or reused to determine the degree-of-interest of elements in the view.
 *
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractAutoFocusViewAction extends AbstractFocusViewAction {

	private boolean initialized = false;

	private final AbstractContextListener CONTEXT_LISTENER = new AbstractContextListener() {

		@SuppressWarnings("deprecation")
		@Override
		public void contextActivated(IInteractionContext context) {
			if (ContextUiPlugin.getDefault()
					.getPreferenceStore()
					.getBoolean(IContextUiPreferenceContstants.AUTO_FOCUS_NAVIGATORS)
					&& context.getAllElements().size() > 0) {
				AbstractAutoFocusViewAction.super.internalSuppressExpandAll = true;
				AbstractAutoFocusViewAction.super.update(true);
			} else {
				AbstractAutoFocusViewAction.super.internalSuppressExpandAll = true;
				AbstractAutoFocusViewAction.super.update(false);
			}
		}
	};

	public AbstractAutoFocusViewAction(InterestFilter interestFilter, boolean manageViewer, boolean manageFilters,
			boolean manageLinking) {
		super(interestFilter, manageViewer, manageFilters, manageLinking);
		super.showEmptyViewMessage = true;
		ContextCore.getContextManager().addListener(CONTEXT_LISTENER);
	}

	@Override
	public void dispose() {
		super.dispose();
		ContextCore.getContextManager().removeListener(CONTEXT_LISTENER);
	}

	@Override
	public void init(IViewPart view) {
		super.init(view);
		configureAction();
	}

	@Override
	public void init(IAction action) {
		super.init(action);
		configureAction();
	}

	private void configureAction() {
		if (initialized) {
			return;
		}
		initialized = true;

		// can not run this until the view has been initialized
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			@SuppressWarnings("deprecation")
			public void run() {
				try {
					if (ContextCore.getContextManager().isContextActive() && ContextUiPlugin.getDefault()
							.getPreferenceStore()
							.getBoolean(IContextUiPreferenceContstants.AUTO_FOCUS_NAVIGATORS)) {
						internalSuppressExpandAll = true;
						update(true);
					}
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
							"Could not toggle focus action on view: " + getPartForAction(), e)); //$NON-NLS-1$
				}
			}
		});
	}
}
