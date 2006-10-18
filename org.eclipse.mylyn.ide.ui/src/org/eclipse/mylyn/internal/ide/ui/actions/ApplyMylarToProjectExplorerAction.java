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

package org.eclipse.mylar.internal.ide.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylar.context.ui.InterestFilter;
import org.eclipse.mylar.internal.context.ui.actions.AbstractAutoApplyMylarAction;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.navigator.NavigatorContentService;
import org.eclipse.ui.internal.navigator.extensions.LinkHelperService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.ILinkHelper;

/**
 * @author Mik Kersten
 */
public class ApplyMylarToProjectExplorerAction extends AbstractAutoApplyMylarAction {

	private LinkHelperService linkService;

	private CommonNavigator commonNavigator;

	public ApplyMylarToProjectExplorerAction() {
		super(new InterestFilter());
	}

	@Override
	public void init(IAction action) {
		super.init(action);
	}

	@Override
	public void run(IAction action) {
		super.run(action);
		if (commonNavigator == null) {
			commonNavigator = (CommonNavigator) super.getPartForAction();
		}
		if (linkService == null) {
			linkService = new LinkHelperService((NavigatorContentService) commonNavigator.getCommonViewer()
					.getNavigatorContentService());
		}

		IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
		if (activeEditor != null) {
			ILinkHelper[] helpers = linkService.getLinkHelpersFor(activeEditor.getEditorInput());
			IEditorInput input = activeEditor.getEditorInput();

			IStructuredSelection selection = StructuredSelection.EMPTY;
			IStructuredSelection newSelection = StructuredSelection.EMPTY;

			for (int i = 0; i < helpers.length; i++) {
				selection = helpers[i].findSelection(input);
				if (selection != null && !selection.isEmpty()) {
					newSelection = mergeSelection(newSelection, selection);
				}
			}
			if (!newSelection.isEmpty()) {
				commonNavigator.selectReveal(newSelection);
			}
		}
	}

	/**
	 * Copied from
	 * 
	 * @{link LinkEditorAction}
	 */
	@SuppressWarnings("unchecked")
	private IStructuredSelection mergeSelection(IStructuredSelection aBase, IStructuredSelection aSelectionToAppend) {
		if (aBase == null || aBase.isEmpty()) {
			return (aSelectionToAppend != null) ? aSelectionToAppend : StructuredSelection.EMPTY;
		} else if (aSelectionToAppend == null || aSelectionToAppend.isEmpty()) {
			return aBase;
		} else {
			List newItems = new ArrayList(aBase.toList());
			newItems.addAll(aSelectionToAppend.toList());
			return new StructuredSelection(newItems);
		}
	}

	// private boolean wasLinkingEnabled = false;
	//	
	// @Override
	// public void update(boolean on) {
	// super.update(on);
	// IViewPart view = super.getPartForAction();
	// if (view instanceof CommonNavigator) {
	// CommonNavigator navigator = (CommonNavigator)view;
	// if (on) {
	// wasLinkingEnabled = navigator.isLinkingEnabled();
	// navigator.setLinkingEnabled(true);
	// } else {
	// navigator.setLinkingEnabled(wasLinkingEnabled);
	// }
	// }
	// }

	protected ApplyMylarToProjectExplorerAction(InterestFilter filter) {
		super(filter);
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();

		IViewPart view = super.getPartForAction();
		if (view instanceof CommonNavigator) {
			CommonNavigator navigator = (CommonNavigator) view;
			viewers.add(navigator.getCommonViewer());
		}
		return viewers;
	}

	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<Class> getPreservedFilters() {
		return Collections.emptyList();
	}
}
