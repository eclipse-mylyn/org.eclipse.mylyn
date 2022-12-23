/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author Mik Kersten
 */
public class PlanningPerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		defineActions(layout);
		defineLayout(layout);
	}

	public void defineActions(IPageLayout layout) {
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(ITasksUiConstants.ID_VIEW_TASKS);
		// layout.addShowViewShortcut(TaskActivityView.ID);

		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
		removeUninterestingActionSets(layout);
	}

	public void defineLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		IFolderLayout topRight = layout.createFolder("topRight", IPageLayout.RIGHT, (float) 0.6, editorArea);//$NON-NLS-1$
		topRight.addView(ITasksUiConstants.ID_VIEW_TASKS);

		// IFolderLayout bottomLeft = layout.createFolder(
		// "bottomLeft", IPageLayout.BOTTOM, (float) 0.50,//$NON-NLS-1$
		// "topLeft");//$NON-NLS-1$
		// bottomLeft.addView(TaskActivityView.ID);
		topRight.addPlaceholder(IPageLayout.ID_RES_NAV);

		// IFolderLayout bottomRight = layout.createFolder(
		// "bottomRight", IPageLayout.BOTTOM, (float) 0.66,//$NON-NLS-1$
		// editorArea);
		//		
		// bottomRight.addView(IPageLayout.ID_TASK_LIST);

	}

	// XXX e4.0 hiding action sets is no longer supported
	public static void removeUninterestingActionSets(IPageLayout layout) {
		try {
			Class<?> clazz = Class.forName("org.eclipse.ui.internal.PageLayout"); //$NON-NLS-1$
			if (clazz != null && clazz.isInstance(layout)) {
				Method method = clazz.getDeclaredMethod("getActionSets"); //$NON-NLS-1$
				ArrayList<?> actionSets = (ArrayList<?>) method.invoke(layout);
				actionSets.remove("org.eclipse.ui.edit.text.actionSet.annotationNavigation"); //$NON-NLS-1$
				actionSets.remove("org.eclipse.ui.edit.text.actionSet.convertLineDelimitersTo"); //$NON-NLS-1$
				actionSets.remove("org.eclipse.ui.externaltools.ExternalToolsSet"); //$NON-NLS-1$
			}
		} catch (Exception e) {
			// ignore
		}
	}
}
