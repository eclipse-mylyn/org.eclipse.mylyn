/*******************************************************************************
 * Copyright (c) 2009, 2014 Atlassian and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Atlassian - initial API and implementation
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.LineRange;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Provides utility methods for the Atlassian Connector for Eclipse
 *
 * @author Thomas Ehrnhoefer
 */
public final class ReviewUiUtil {

	public static final String CONFLUENCE_WIKI_TASK_EDITOR_EXTENSION = "org.eclipse.mylyn.wikitext.tasks.ui.editor.confluenceTaskEditorExtension"; //$NON-NLS-1$

	private ReviewUiUtil() {
	}

	public static boolean isAnimationsEnabled() {
		IPreferenceStore store = PlatformUI.getPreferenceStore();
		return store.getBoolean(IWorkbenchPreferenceConstants.ENABLE_ANIMATIONS);
	}

	/**
	 * Must be invoked in UI thread
	 *
	 * @param viewId
	 * @return <code>true</code> when the view has been successfully made visible or it already was, <code>false</code> if operation failed
	 */
	public static boolean ensureViewIsVisible(String viewId) {
		final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) {
			return false;
		}
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		if (activePage == null) {
			return false;
		}
		for (IViewReference view : activePage.getViewReferences()) {
			if (view.getId().equals(viewId)) {
				return true;
			}
		}
		try {
			activePage.showView(viewId, null, IWorkbenchPage.VIEW_ACTIVATE);
			return true;
		} catch (PartInitException e) {
//			StatusHandler.log(new Status(IStatus.ERROR, AtlassianUiPlugin.PLUGIN_ID, "Could not initialize " + viewId
//					+ " view."));
			return false;
		}
	}

	public static boolean showViewInActiveWorkbenchPage(String viewId) {
		final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) {
			return false;
		}
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		if (activePage == null) {
			return false;
		}
		try {
			activePage.showView(viewId);
			return true;
		} catch (PartInitException e) {
//			StatusHandler.log(new Status(IStatus.ERROR, AtlassianUiPlugin.PLUGIN_ID, "Could not initialize " + viewId
//					+ " view."));
			return false;
		}
	}

	public static LineRange getSelectedLineNumberRangeFromEditorInput(IEditorPart editor, IEditorInput editorInput) {

		if (editor instanceof ITextEditor && editor.getEditorInput() == editorInput) {
			ISelection selection = ((ITextEditor) editor).getSelectionProvider().getSelection();
			return getLineRange(selection);
		} else if (editor.getAdapter(ITextEditor.class) != null) {
			ISelection selection = editor.getAdapter(ITextEditor.class).getSelectionProvider().getSelection();
			return getLineRange(selection);
		}
		return null;
	}

	private static LineRange getLineRange(ISelection selection) {
		if (selection instanceof TextSelection textSelection) {
			return new LineRange(textSelection.getStartLine() + 1,
					textSelection.getEndLine() - textSelection.getStartLine() + 1);
		}
		return null;
	}

	public static Display getDisplay(Shell shell) {
		if (shell != null) {
			Display d = shell.getDisplay();

			if (d != null) {
				return d;
			}
		}

		return getDisplay();
	}

	public static Display getDisplay() {

		Display d = Display.getCurrent();

		if (d == null) {
			d = Display.getDefault();
		}

		if (d == null) {
			d = WorkbenchUtil.getShell().getDisplay();
		}
		return d;
	}

	public static boolean isAnonymous(IReviewItem item) {
		return item != null && isAnonymous(item.getReview());
	}

	public static boolean isAnonymous(IReview review) {
		return review != null && review.getRepository() != null && review.getRepository().getAccount() == null;
	}

}
