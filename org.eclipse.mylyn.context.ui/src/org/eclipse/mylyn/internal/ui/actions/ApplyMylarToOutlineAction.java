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
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.ui.IMylarUiBridge;
import org.eclipse.mylar.provisional.ui.InterestFilter;
import org.eclipse.mylar.provisional.ui.MylarUiPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class ApplyMylarToOutlineAction extends AbstractApplyMylarAction {

	private static ApplyMylarToOutlineAction INSTANCE;

	public ApplyMylarToOutlineAction() {
		super(new InterestFilter());
		INSTANCE = this;
	}

	/**
	 * TODO: refactor this optimization?
	 */
	public void update(IEditorPart editorPart) {
		boolean on = MylarPlugin.getDefault().getPreferenceStore().getBoolean(getPrefId());

		IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart);
		List<TreeViewer> outlineViewers = bridge.getContentOutlineViewers(editorPart);
		for (TreeViewer viewer : outlineViewers) {
			MylarUiPlugin.getDefault().getViewerManager().addManagedViewer(viewer, viewPart);
			installInterestFilter(on, viewer);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		if (!PlatformUI.getWorkbench().isClosing() && PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() != null) {
			try { 
				IEditorPart[] parts = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditors();
				for (int i = 0; i < parts.length; i++) {
					IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridgeForEditor(parts[i]);
					List<TreeViewer> outlineViewers = bridge.getContentOutlineViewers(parts[i]);
					for (TreeViewer viewer : outlineViewers) {
						if (viewer != null && !viewers.contains(viewer))
							viewers.add(viewer);
					}
				}
			} catch (NullPointerException e) {
				// ignore since this can be called on shutdown
			}
		}
		return viewers;
	}

	public static ApplyMylarToOutlineAction getDefault() {
		return INSTANCE;
	}

	public void propertyChange(PropertyChangeEvent event) {
		// ignore
	}
	
	@Override
	public List<Class> getPreservedFilters() {
		return Collections.emptyList();
	}
}