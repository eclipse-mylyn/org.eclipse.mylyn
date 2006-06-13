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

package org.eclipse.mylar.internal.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.ui.IMylarUiBridge;
import org.eclipse.mylar.provisional.ui.InterestFilter;
import org.eclipse.mylar.provisional.ui.MylarUiPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class ApplyMylarToOutlineAction extends AbstractApplyMylarAction {

	// TODO: move or delete?
	public static final String ID_CONTENT_OUTLINE = "org.eclipse.ui.views.ContentOutline";

	public ApplyMylarToOutlineAction() {
		super(new InterestFilter());
	}

	/**
	 * TODO: refactor this optimization?
	 */
	public void update(IEditorPart editorPart) {
		if (!super.isChecked()) {
			return;
		}
		boolean on = MylarPlugin.getDefault().getPreferenceStore().getBoolean(getGlobalPrefId()) ;

		IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart);
		List<TreeViewer> outlineViewers = bridge.getContentOutlineViewers(editorPart);
		for (TreeViewer viewer : outlineViewers) {
			if (viewPart != null) {
				MylarUiPlugin.getDefault().getViewerManager().addManagedViewer(viewer, viewPart);
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
			if (!(viewer.getLabelProvider() instanceof DecoratingLabelProvider)) {
				viewer.setLabelProvider(new DecoratingLabelProvider((ILabelProvider) viewer.getLabelProvider(),
						PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		if (PlatformUI.getWorkbench().isClosing()) {
			return viewers;
		}
		for (IWorkbenchWindow w : PlatformUI.getWorkbench().getWorkbenchWindows()) {
			IWorkbenchPage page = w.getActivePage();
			if (page != null) {
				IEditorPart[] parts = page.getEditors();
				for (int i = 0; i < parts.length; i++) {
					IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridgeForEditor(parts[i]);
					List<TreeViewer> outlineViewers = bridge.getContentOutlineViewers(parts[i]);
					for (TreeViewer viewer : outlineViewers) {
						if (viewer != null && !viewers.contains(viewer))
							viewers.add(viewer);
					}
				}
			}
		}
		return viewers;
	}

	public void propertyChange(PropertyChangeEvent event) {
		// ignore
	}

	@Override
	public List<Class> getPreservedFilters() {
		return Collections.emptyList();
	}

	public static ApplyMylarToOutlineAction getOutlineActionForEditor(IEditorPart part) {
		IViewPart outlineView = part.getSite().getPage().findView(ID_CONTENT_OUTLINE);
		if (outlineView != null) {
			return (ApplyMylarToOutlineAction) AbstractApplyMylarAction.getActionForPart(outlineView);
		} else {
			return null;
		}
	}
}
