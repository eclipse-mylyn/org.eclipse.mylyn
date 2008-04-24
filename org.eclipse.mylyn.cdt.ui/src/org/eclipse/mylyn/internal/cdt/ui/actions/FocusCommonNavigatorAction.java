/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat Inc. - Modification for CDT usage
 *     Anton Leherbauer (Wind River Systems) - Project Explorer integration
 *******************************************************************************/

package org.eclipse.cdt.mylyn.internal.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IMethod;
import org.eclipse.cdt.internal.ui.actions.SelectionConverter;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.mylyn.internal.ui.CDTDeclarationsFilter;
import org.eclipse.cdt.mylyn.internal.ui.CDTUIBridgePlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.context.ui.AbstractAutoFocusViewAction;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.navigator.actions.LinkEditorAction;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 */
public class FocusCommonNavigatorAction extends AbstractAutoFocusViewAction {

	public FocusCommonNavigatorAction() {
		super(new InterestFilter(), true, true, true);
		setText(CDTUIBridgePlugin.getResourceString("FocusActiveTask.label")); //$NON-NLS-1$
		setToolTipText(CDTUIBridgePlugin.getResourceString("FocusActiveTask.tooltip")); //$NON-NLS-1$
	}

	@Override
	public void run() {
		super.run(this);
	}

	@Override
	protected ISelection resolveSelection(IEditorPart part, ITextSelection changedSelection, StructuredViewer viewer)
			throws CoreException {
		Object elementToSelect = null;
		if (changedSelection instanceof TextSelection && part instanceof CEditor) {
			ICElement cdtElement = SelectionConverter.getElementAtOffset(((CEditor) part).getInputCElement(), changedSelection);
			elementToSelect = cdtElement;
		}

		if (elementToSelect != null) {
			StructuredSelection currentSelection = (StructuredSelection) viewer.getSelection();
			if (currentSelection.size() <= 1) {
				for (ViewerFilter filter : Arrays.asList(viewer.getFilters())) {
					if (filter instanceof CDTDeclarationsFilter && elementToSelect instanceof IMethod) {
						elementToSelect = ((IMethod) elementToSelect).getTranslationUnit();
					}
				}
			}
			return new StructuredSelection(elementToSelect);
		} else {
			return null;
		}
	}

	// TODO: should have better way of doing this
	protected void setManualFilteringAndLinkingEnabled(boolean enabled) {
		IViewPart part = super.getPartForAction();
		if (part instanceof CommonNavigator) {
			for (IContributionItem item : ((CommonNavigator) part).getViewSite()
					.getActionBars()
					.getToolBarManager()
					.getItems()) {
				if (item instanceof ActionContributionItem) {
					ActionContributionItem actionItem = (ActionContributionItem) item;
					if (actionItem.getAction() instanceof LinkEditorAction) {
						actionItem.getAction().setEnabled(enabled);
					}
				}
			}
		}
	}

	@Override
	protected void setDefaultLinkingEnabled(boolean on) {
		IViewPart part = super.getPartForAction();
		if (part instanceof CommonNavigator) {
			((CommonNavigator) part).setLinkingEnabled(on);
		}
	}

	@Override
	protected boolean isDefaultLinkingEnabled() {
		IViewPart part = super.getPartForAction();
		if (part instanceof CommonNavigator) {
			return ((CommonNavigator) part).isLinkingEnabled();
		}
		return false;
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		// TODO: get from super
		IViewPart part = super.getPartForAction();
		if (part instanceof CommonNavigator) {
			viewers.add((StructuredViewer)part.getAdapter(CommonViewer.class));
		}
		return viewers;
	}
}
