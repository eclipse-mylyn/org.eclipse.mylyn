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

package org.eclipse.mylyn.internal.context.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.context.ui.AbstractFocusViewAction;
import org.eclipse.mylyn.context.ui.ContextUi;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class FocusOutlineAction extends AbstractFocusViewAction {

	// TODO: move or delete?
	public static final String ID_CONTENT_OUTLINE = "org.eclipse.ui.views.ContentOutline"; //$NON-NLS-1$

	public FocusOutlineAction() {
		super(new InterestFilter(), true, false, false);
	}

	/**
	 * TODO: refactor this optimization?
	 */
	public void update(IEditorPart editorPart) {
		if (!super.isChecked()) {
			return;
		}
		boolean on = ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(getGlobalPrefId());

		AbstractContextUiBridge bridge = ContextUi.getUiBridgeForEditor(editorPart);
		List<TreeViewer> outlineViewers = bridge.getContentOutlineViewers(editorPart);
		for (TreeViewer viewer : outlineViewers) {
			if (viewPart != null) {
				ContextUiPlugin.getViewerManager().addManagedViewer(viewer, viewPart);
			}
			updateInterestFilter(on, viewer);
			configureDecorator(viewer);
		}
	}

	/**
	 * TODO: remove once all outlines have platform decorator
	 */
	private void configureDecorator(TreeViewer viewer) {
		if (viewer != null) {
			if (viewer.getLabelProvider() instanceof ILabelProvider
					&& !(viewer.getLabelProvider() instanceof DecoratingLabelProvider)) {
				viewer.setLabelProvider(new DecoratingLabelProvider((ILabelProvider) viewer.getLabelProvider(),
						PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
			}
		}
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<>();
		if (PlatformUI.getWorkbench().isClosing()) {
			return viewers;
		}
		for (IWorkbenchWindow w : PlatformUI.getWorkbench().getWorkbenchWindows()) {
			IWorkbenchPage page = w.getActivePage();
			if (page != null) {
				IEditorReference[] editorRefs = page.getEditorReferences();
				for (IEditorReference editorRef : editorRefs) {
					IEditorPart part = editorRef.getEditor(false);
					if (part == null) {
						continue;
					}
					AbstractContextUiBridge bridge = ContextUi.getUiBridgeForEditor(part);
					List<TreeViewer> outlineViewers = bridge.getContentOutlineViewers(part);
					for (TreeViewer viewer : outlineViewers) {
						if (viewer != null && !viewers.contains(viewer)) {
							viewers.add(viewer);
						}
					}
				}
			}
		}
		return viewers;
	}

	public static FocusOutlineAction getOutlineActionForEditor(IEditorPart part) {
		IViewPart outlineView = part.getSite().getPage().findView(ID_CONTENT_OUTLINE);
		if (outlineView != null) {
			return (FocusOutlineAction) AbstractFocusViewAction.getActionForPart(outlineView);
		} else {
			return null;
		}
	}
}
