/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.resources.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.expressions.Expression;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.ui.AbstractAutoFocusViewAction;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.navigator.actions.LinkEditorAction;
import org.eclipse.ui.internal.navigator.filters.CommonFilterDescriptor;
import org.eclipse.ui.internal.navigator.filters.CommonFilterDescriptorManager;
import org.eclipse.ui.internal.navigator.filters.CoreExpressionFilter;
import org.eclipse.ui.internal.navigator.filters.SelectFiltersAction;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.ILinkHelper;
import org.eclipse.ui.navigator.LinkHelperService;

/**
 * @author Mik Kersten
 * @since 3.0
 */
public abstract class FocusCommonNavigatorAction extends AbstractAutoFocusViewAction {

	private LinkHelperService linkService;

	private boolean resolveFailed;

	private CommonNavigator commonNavigator;

	private CommonFilterDescriptor[] filterDescriptors;

	private Field filterExpressionField1;

	private Field filterExpressionField2;

	public FocusCommonNavigatorAction(InterestFilter interestFilter, boolean manageViewer, boolean manageFilters,
			boolean manageLinking) {
		super(interestFilter, manageViewer, manageFilters, manageLinking);
	}

	@Override
	protected boolean installInterestFilter(StructuredViewer viewer) {
		if (commonNavigator == null) {
			commonNavigator = (CommonNavigator) super.getPartForAction();
		}

		try {
			// XXX: reflection
			Class<?> clazz2 = CoreExpressionFilter.class;
			filterExpressionField1 = clazz2.getDeclaredField("filterExpression"); //$NON-NLS-1$
			filterExpressionField1.setAccessible(true);

			Class<?> clazz1 = CommonFilterDescriptor.class;
			filterExpressionField2 = clazz1.getDeclaredField("filterExpression"); //$NON-NLS-1$
			filterExpressionField2.setAccessible(true);
		} catch (Exception e) {
			StatusHandler
					.log(new Status(IStatus.ERROR, ResourcesUiBridgePlugin.ID_PLUGIN, "Could not determine filter", e)); //$NON-NLS-1$
		}

		filterDescriptors = CommonFilterDescriptorManager.getInstance()
				.findVisibleFilters(commonNavigator.getNavigatorContentService());

		return super.installInterestFilter(viewer);
	}

	@Override
	protected ISelection resolveSelection(IEditorPart editor, ITextSelection changedSelection, StructuredViewer viewer)
			throws CoreException {
		if (resolveFailed) {
			return null;
		}
		if (linkService == null) {
			try {
				// need reflection since the method is protected
				Method method = CommonNavigator.class.getDeclaredMethod("getLinkHelperService"); //$NON-NLS-1$
				method.setAccessible(true);
				linkService = (LinkHelperService) method.invoke(commonNavigator);
			} catch (Throwable e) {
				resolveFailed = true;
				StatusHandler.log(new Status(IStatus.ERROR, ResourcesUiBridgePlugin.ID_PLUGIN,
						"Initialization of LinkHelperService failed", e)); //$NON-NLS-1$
			}
		}

		IEditorInput input = editor.getEditorInput();
		ILinkHelper[] helpers = linkService.getLinkHelpersFor(input);

		IStructuredSelection selection = StructuredSelection.EMPTY;
		IStructuredSelection newSelection = StructuredSelection.EMPTY;

		for (ILinkHelper helper : helpers) {
			selection = helper.findSelection(input);
			if (selection != null && !selection.isEmpty()) {
				newSelection = mergeSelection(newSelection, selection);
			}
		}
		if (!newSelection.isEmpty()) {
			return newSelection;
		}
		return null;
	}

	@Override
	protected void select(StructuredViewer viewer, final ISelection toSelect) {
		// We need to delay the setting of the selection until after the selection event is processed
		// 288416: unable to open a C element when focus is enabled
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=288416
		Display.getDefault().asyncExec(() -> {
			if (commonNavigator == null) {
				commonNavigator = (CommonNavigator) FocusCommonNavigatorAction.super.getPartForAction();
			}
			if (commonNavigator != null) {
				commonNavigator.selectReveal(toSelect);
			}
		});
	}

	// TODO: should have better way of doing this
	@Override
	protected void setManualFilteringAndLinkingEnabled(boolean on) {
		IViewPart part = super.getPartForAction();
		if (part instanceof CommonNavigator) {
			for (IContributionItem item : ((CommonNavigator) part).getViewSite()
					.getActionBars()
					.getToolBarManager()
					.getItems()) {
				if (item instanceof ActionContributionItem actionItem) {
					if (actionItem.getAction() instanceof LinkEditorAction) {
						actionItem.getAction().setEnabled(on);
					}
				}
			}
			for (IContributionItem item : ((CommonNavigator) part).getViewSite()
					.getActionBars()
					.getMenuManager()
					.getItems()) {
				if (item instanceof ActionContributionItem actionItem) {
					if (actionItem.getAction() instanceof SelectFiltersAction) {
						actionItem.getAction().setEnabled(on);
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
	protected boolean isPreservedFilter(ViewerFilter filter) {
		if (filter instanceof CoreExpressionFilter expressionFilter) {
			Set<String> preservedIds = ContextUiPlugin.getDefault().getPreservedFilterIds(viewPart.getSite().getId());
			if (!preservedIds.isEmpty()) {
				try {
					Expression expression2 = (Expression) filterExpressionField1.get(expressionFilter);

					for (CommonFilterDescriptor commonFilterDescriptor : filterDescriptors) {
						if (preservedIds.contains(commonFilterDescriptor.getId())) {
							Expression expression1 = (Expression) filterExpressionField2.get(commonFilterDescriptor);
							if (expression1 != null && expression1.equals(expression2)) {
								return true;
							}
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					StatusHandler.log(new Status(IStatus.ERROR, ResourcesUiBridgePlugin.ID_PLUGIN,
							"Could not determine filter", e)); //$NON-NLS-1$
				}
			}
		}
		return false;
	}

	/**
	 * Copied from
	 * 
	 * @{link LinkEditorAction}
	 */
	@SuppressWarnings("unchecked")
	private IStructuredSelection mergeSelection(IStructuredSelection aBase, IStructuredSelection aSelectionToAppend) {
		if (aBase == null || aBase.isEmpty()) {
			return aSelectionToAppend != null ? aSelectionToAppend : StructuredSelection.EMPTY;
		} else if (aSelectionToAppend == null || aSelectionToAppend.isEmpty()) {
			return aBase;
		} else {
			List<Object> newItems = new ArrayList<Object>(aBase.toList());
			newItems.addAll(aSelectionToAppend.toList());
			return new StructuredSelection(newItems);
		}
	}

}
