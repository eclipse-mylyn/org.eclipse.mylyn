/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ui.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.context.ui.AbstractAutoFocusViewAction;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.views.navigator.FilterSelectionAction;
import org.eclipse.ui.views.navigator.IResourceNavigator;
import org.eclipse.ui.views.navigator.ResourceNavigator;
import org.eclipse.ui.views.navigator.ResourcePatternFilter;
import org.eclipse.ui.views.navigator.ToggleLinkingAction;

/**
 * @author Mik Kersten
 */
public class FocusResourceNavigatorAction extends AbstractAutoFocusViewAction {

	public FocusResourceNavigatorAction() {
		super(new InterestFilter(), true, true, true);
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		IViewPart part = super.getPartForAction();
		if (part instanceof ResourceNavigator) {
			viewers.add(((ResourceNavigator) part).getTreeViewer());
		}
		return viewers;
	}

	@Override
	protected ISelection resolveSelection(IEditorPart part, ITextSelection changedSelection, StructuredViewer viewer)
			throws CoreException {
		IEditorInput input = part.getEditorInput();
		Object adapted = input.getAdapter(IResource.class);
		if (adapted instanceof IResource) {
			return new StructuredSelection(adapted);
		} else {
			return null;
		}
	}

	// TODO: should have better way of doing this
	@Override
	protected void setManualFilteringAndLinkingEnabled(boolean on) {
		IViewPart part = super.getPartForAction();
		if (part instanceof IResourceNavigator) {
			for (IContributionItem item : ((IResourceNavigator) part).getViewSite()
					.getActionBars()
					.getToolBarManager()
					.getItems()) {
				if (item instanceof ActionContributionItem) {
					ActionContributionItem actionItem = (ActionContributionItem) item;
					if (actionItem.getAction() instanceof ToggleLinkingAction) {
						actionItem.getAction().setEnabled(on);
					}
				}
			}
			for (IContributionItem item : ((IResourceNavigator) part).getViewSite()
					.getActionBars()
					.getMenuManager()
					.getItems()) {
				if (item instanceof ActionContributionItem) {
					ActionContributionItem actionItem = (ActionContributionItem) item;
					// TODO: consider filing bug asking for extensibility
					if (actionItem.getAction() instanceof FilterSelectionAction) {
						actionItem.getAction().setEnabled(on);
					}
				}
			}
		}
	}

	@Override
	protected void setDefaultLinkingEnabled(boolean on) {
		IViewPart part = super.getPartForAction();
		if (part instanceof IResourceNavigator) {
			((IResourceNavigator) part).setLinkingEnabled(on);
		}
	}

	@Override
	protected boolean isDefaultLinkingEnabled() {
		IViewPart part = super.getPartForAction();
		if (part instanceof IResourceNavigator) {
			return ((IResourceNavigator) part).isLinkingEnabled();
		}
		return false;
	}

	private Set<String> getPreservedFilterPatterns() {
		Set<String> preservedIds = ContextUiPlugin.getDefault().getPreservedFilterIds(viewPart.getSite().getId());
		IExtensionPoint extension = Platform.getExtensionRegistry().getExtensionPoint(
				"org.eclipse.ui.ide.resourceFilters"); //$NON-NLS-1$
		Set<String> filters = new HashSet<String>();
		if (extension != null) {
			IExtension[] extensions = extension.getExtensions();
			for (IExtension extension2 : extensions) {
				IConfigurationElement[] configElements = extension2.getConfigurationElements();
				if (extension2.getUniqueIdentifier() != null && preservedIds.contains(extension2.getUniqueIdentifier())) {
					for (IConfigurationElement configElement : configElements) {
						String pattern = configElement.getAttribute("pattern");//$NON-NLS-1$
						if (pattern != null) {
							filters.add(pattern);
						}
					}
				}

			}
		}
		return filters;
	}

	@Override
	protected boolean isPreservedFilter(ViewerFilter filter) {
		if (filter instanceof ResourcePatternFilter) {
			Set<String> preservedFilterPatterns = getPreservedFilterPatterns();
			//NOTE: since the resource filters are all contained in one filter, if one is preserved, then we preserve all filters
			for (String pattern : ((ResourcePatternFilter) filter).getPatterns()) {
				if (preservedFilterPatterns.contains(pattern)) {
					return true;
				}
			}
		}
		return super.isPreservedFilter(filter);
	}

}
