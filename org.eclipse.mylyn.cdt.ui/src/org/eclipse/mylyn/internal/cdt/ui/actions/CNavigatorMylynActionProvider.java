/*******************************************************************************
 * Copyright (c) 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Anton Leherbauer (Wind River Systems) - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.mylyn.internal.ui.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * Common navigator action provider for Mylyn actions.
 */
public class CNavigatorMylynActionProvider extends CommonActionProvider {

	private static final String GROUP_MYLYN= "group.mylyn"; //$NON-NLS-1$

	private FocusCommonNavigatorAction fFocusViewAction;
	private ShowFilteredChildrenAction fShowFilteredChildrenAction;

	/** Flag inidicating whether actions have been contributed to action bars already */
	private boolean fContributedToActionBars;

	/** Mandatory default constructor (executable extension) */
	public CNavigatorMylynActionProvider() {
	}

	@Override
	public void init(ICommonActionExtensionSite site) {
		super.init(site);
		ICommonViewerWorkbenchSite workbenchSite = null;
		if (site.getViewSite() instanceof ICommonViewerWorkbenchSite) {
			workbenchSite = (ICommonViewerWorkbenchSite) site.getViewSite();
		}
		if (workbenchSite != null) {
			if (workbenchSite.getPart() != null && workbenchSite.getPart() instanceof IViewPart) {
				IViewPart viewPart = (IViewPart) workbenchSite.getPart();

				// create actions
				fFocusViewAction= new FocusCommonNavigatorAction();
				fShowFilteredChildrenAction= new ShowFilteredChildrenAction();
				// satisfy IViewActionDelegate API
				fFocusViewAction.init(viewPart);
				fShowFilteredChildrenAction.init(viewPart);
				// satisfy IActionDelegate2 API
				fFocusViewAction.init(fFocusViewAction);
			}
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if (fFocusViewAction != null) {
			fFocusViewAction.dispose();
		}
	}
	
	@Override
	public void fillActionBars(IActionBars actionBars) {
		if (fFocusViewAction == null) {
			return;
		}
		if (!fContributedToActionBars) {
			fContributedToActionBars= true;
			actionBars.getToolBarManager().add(fFocusViewAction);
			final IMenuManager menu= actionBars.getMenuManager();
			menu.appendToGroup(ICommonMenuConstants.GROUP_ADDITIONS, new Separator(GROUP_MYLYN));
			menu.appendToGroup(GROUP_MYLYN, fFocusViewAction);
		}
	}
	
	@Override
	public void fillContextMenu(IMenuManager menu) {
		if (fFocusViewAction == null) {
			return;
		}
		menu.appendToGroup(ICommonMenuConstants.GROUP_REORGANIZE, fShowFilteredChildrenAction);
	}
	
	@Override
	public void setContext(ActionContext context) {
		super.setContext(context);
		if (fFocusViewAction == null || context == null) {
			return;
		}
		// satisfy IActionDelegate API
		ISelection selection= context.getSelection();
		fFocusViewAction.selectionChanged(fFocusViewAction, selection);
		fShowFilteredChildrenAction.selectionChanged(fShowFilteredChildrenAction, selection);
	}
}
