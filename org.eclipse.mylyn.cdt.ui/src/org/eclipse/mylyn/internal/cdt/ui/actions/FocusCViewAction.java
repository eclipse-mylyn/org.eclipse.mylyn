/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 		Red Hat Inc. - Modification for CDT usage
 *******************************************************************************/

package org.eclipse.cdt.mylyn.internal.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IMethod;
import org.eclipse.cdt.internal.ui.actions.SelectionConverter;
import org.eclipse.cdt.internal.ui.cview.CView;
import org.eclipse.cdt.internal.ui.cview.ToggleLinkingAction;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.mylyn.internal.ui.CDTDeclarationsFilter;
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

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 */
public class FocusCViewAction extends AbstractAutoFocusViewAction {

	public FocusCViewAction() {
		super(new InterestFilter(), true, true, true);
	}

	@Override
	protected ISelection resolveSelection(IEditorPart part, ITextSelection changedSelection, StructuredViewer viewer)
			throws CoreException {
		Object elementToSelect = null;
		if (changedSelection instanceof TextSelection && part instanceof CEditor) {
			TextSelection textSelection = (TextSelection) changedSelection;
			ICElement[] cdtElements = SelectionConverter.codeResolve(SelectionConverter.getInput((CEditor)part), textSelection);
			if (cdtElements != null) {
				elementToSelect = cdtElements[0];
			}
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
		if (part instanceof CView) {
			for (IContributionItem item : ((CView) part).getViewSite()
					.getActionBars()
					.getToolBarManager()
					.getItems()) {
				if (item instanceof ActionContributionItem) {
					ActionContributionItem actionItem = (ActionContributionItem) item;
					if (actionItem.getAction() instanceof ToggleLinkingAction) {
						actionItem.getAction().setEnabled(enabled);
					}
				}
			}
			for (IContributionItem item : ((CView) part).getViewSite()
					.getActionBars()
					.getMenuManager()
					.getItems()) {
				if (item instanceof ActionContributionItem) {
					ActionContributionItem actionItem = (ActionContributionItem) item;
					// TODO: file bug asking for extensibility
					if (actionItem.getAction().getClass().getSimpleName().equals("ShowFilterDialogAction")) { // $NON-NLS-1$
						actionItem.getAction().setEnabled(enabled);
					}
				}
				// NOTE: turning off dynamically contributed filter items is not currently feasible
//				else if (item instanceof ContributionItem) {
//					ContributionItem contributionItem = (ContributionItem) item;
//					
//					if (contributionItem.getClass().getSimpleName().equals("FilterActionMenuContributionItem")) {
//						try {
//							Class<?> clazz = contributionItem.getClass();
//							Field field = clazz.getDeclaredField("fActionGroup");
//							field.setAccessible(true);
//							Object object = field.get(contributionItem);
//							if (object instanceof CustomFiltersActionGroup) {
//								CustomFiltersActionGroup group = (CustomFiltersActionGroup) object;
//								group.setFilters(new String[] { });
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}
			}
		}
	}

	@Override
	protected void setDefaultLinkingEnabled(boolean on) {
		IViewPart part = super.getPartForAction();
		if (part instanceof CView) {
			((CView) part).setLinkingEnabled(on);
		}
	}

	@Override
	protected boolean isDefaultLinkingEnabled() {
		IViewPart part = super.getPartForAction();
		if (part instanceof CView) {
			return ((CView) part).isLinkingEnabled();
		}
		return false;
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		// TODO: get from super
		IViewPart part = super.getPartForAction();
		if (part instanceof CView) {
			viewers.add(((CView) part).getViewer());
		}
		return viewers;
	}
}
