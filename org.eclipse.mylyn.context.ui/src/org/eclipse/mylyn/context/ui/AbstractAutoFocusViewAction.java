/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.ui;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.ui.ContextUiPrefContstants;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * Extend to focus a view on task context, e.g. the filtering and expansion of a tree view such as the Package Explorer.
 * A structure bridge should be implemented or reused to determine the degree-of-interest of elements in the view.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractAutoFocusViewAction extends AbstractFocusViewAction implements
		IInteractionContextListener {

	private boolean initialized = false;

	public AbstractAutoFocusViewAction(InterestFilter interestFilter, boolean manageViewer, boolean manageFilters,
			boolean manageLinking) {
		super(interestFilter, manageViewer, manageFilters, manageLinking);
		ContextCorePlugin.getContextManager().addListener(this);
	}

	@Override
	public void dispose() {
		super.dispose();
		ContextCorePlugin.getContextManager().removeListener(this);
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
			public void run() {
				try {
					if (ContextCorePlugin.getContextManager().isContextActive()
							&& ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
									ContextUiPrefContstants.NAVIGATORS_AUTO_FILTER_ENABLE)) {
						internalSuppressExpandAll = true;
						update(true);
					}
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
							"Could not toggle focus action on view: " + getPartForAction(), e));
				}
			}
		});
	}

	public void contextActivated(IInteractionContext context) {
		if (ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
				ContextUiPrefContstants.NAVIGATORS_AUTO_FILTER_ENABLE)) {
			internalSuppressExpandAll = true;
			update(true);
		} else {
			internalSuppressExpandAll = true;
			update(false);
		}
	}

	public void contextDeactivated(IInteractionContext context) {
		// now happens in super
	}

	public void contextCleared(IInteractionContext context) {
		// ignore
	}

	public void relationsChanged(IInteractionElement element) {
		// ignore
	}

	public void interestChanged(List<IInteractionElement> elements) {
		// ignore
	}

	public void landmarkAdded(IInteractionElement element) {
		// ignore
	}

	public void landmarkRemoved(IInteractionElement element) {
		// ignore
	}

	public void elementDeleted(IInteractionElement element) {
		// ignore
	}

}
