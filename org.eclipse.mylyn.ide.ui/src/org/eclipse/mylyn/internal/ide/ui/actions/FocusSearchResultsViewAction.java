/*******************************************************************************
 * Copyright (c) 2017 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylyn.context.ui.AbstractFocusViewAction;
import org.eclipse.mylyn.context.ui.SearchInterestFilter;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search2.internal.ui.SearchView;
import org.eclipse.ui.IViewPart;

public class FocusSearchResultsViewAction extends AbstractFocusViewAction {

	private static final List<String> WHITELISTED_IDS = Arrays
			.asList(new String[] { "org.eclipse.search.text.FileSearchResultPage", //$NON-NLS-1$
					"org.eclipse.jdt.ui.JavaSearchResultPage" //$NON-NLS-1$
			});

	public FocusSearchResultsViewAction() {
		super(new SearchInterestFilter(), true, true, true);
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();

		ISearchResultPage page = getCurrentPage();
		if (page instanceof AbstractTextSearchViewPage) {
			try {
				Method getViewer = AbstractTextSearchViewPage.class.getDeclaredMethod("getViewer"); //$NON-NLS-1$
				getViewer.setAccessible(true);
				StructuredViewer viewer = (StructuredViewer) getViewer.invoke(page);
				viewers.add(viewer);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				// ignore
			}
		}

		return viewers;
	}

	@SuppressWarnings("restriction")
	private ISearchResultPage getCurrentPage() {
		IViewPart view = super.getPartForAction();
		return ((SearchView) view).getActivePage();
	}

	@Override
	public boolean isEnabled() {
		List<StructuredViewer> viewers = getViewers();
		ISearchResultPage page = getCurrentPage();

		return viewers.size() > 0 && WHITELISTED_IDS.contains(page.getID());
	}

	@Override
	protected void updateEnablement(IAction action) {
		action.setEnabled(isEnabled());
	}
}
