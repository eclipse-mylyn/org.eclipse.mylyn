/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.context.ui.ContextUiPrefContstants;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * Applies itself automatically with context activation
 * 
 * @author Mik Kersten
 */
public abstract class AbstractAutoFocusViewAction extends AbstractFocusViewAction implements IInteractionContextListener { // IPropertyChangeListener, 

	public AbstractAutoFocusViewAction(InterestFilter interestFilter, boolean manageViewer, boolean manageFilters, boolean manageLinking) {
		super(interestFilter, manageViewer, manageFilters, manageLinking);
		ContextCorePlugin.getContextManager().addListener(this);
	}

	@Override
	public void dispose() {
		ContextCorePlugin.getContextManager().removeListener(this);
	}

	@Override
	public void init(IAction action) {
		super.init(action);
		configureAction();
	}

	private void configureAction() {
		// can not run this until the view has been initialized
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					if (ContextCorePlugin.getContextManager().isContextActive() &&
							ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
							ContextUiPrefContstants.NAVIGATORS_AUTO_FILTER_ENABLE)) {
						update(true);
					} 
				} catch (Exception e) {
					MylarStatusHandler.fail(e, "could not toggle Mylar on view: " + getPartForAction(), true);
				}
			}
		});
	}

	public void contextActivated(IInteractionContext context) {
		if (ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
				ContextUiPrefContstants.NAVIGATORS_AUTO_FILTER_ENABLE)) {
			update(true);
		} 
	}

	public void contextDeactivated(IInteractionContext context) {
		if (ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
				ContextUiPrefContstants.NAVIGATORS_AUTO_FILTER_ENABLE)) {
			update(false);
		} 
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
