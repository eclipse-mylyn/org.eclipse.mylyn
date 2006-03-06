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
package org.eclipse.mylar.internal.ui;

import java.util.List;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.ui.actions.ApplyMylarToOutlineAction;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.ui.IMylarUiBridge;
import org.eclipse.mylar.provisional.ui.MylarUiPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * Manages the installation of the outline filter and sorter.
 * 
 * @author Mik Kersten
 */
public class ContentOutlineManager implements IPartListener, IPageListener {

	public void partBroughtToTop(IWorkbenchPart part) {
		if (!MylarPlugin.getContextManager().isContextActive()
				|| MylarPlugin.getContextManager().isContextCapturePaused())
			return;

		if (part instanceof IEditorPart) {
			IEditorPart editorPart = (IEditorPart) part;
			configureDecorator(editorPart);
			if (ApplyMylarToOutlineAction.getDefault() != null) {
				ApplyMylarToOutlineAction.getDefault().update(editorPart);
			}
		}
	}

	public void partActivated(IWorkbenchPart part) {

	}

	public void partOpened(IWorkbenchPart part) {

	}

	/**
	 * TODO: refactor, this will get called too often
	 */
	private void configureDecorator(IEditorPart editorPart) {
		if (ApplyMylarToOutlineAction.getDefault() == null)
			return;
		IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart);
		List<TreeViewer> viewers = bridge.getContentOutlineViewers(editorPart);
		if (viewers == null) {
			MylarStatusHandler.log("null viewer list from bridge: " + bridge + " for editor: " + editorPart, this);
			return;
		} else {
			for (TreeViewer viewer : viewers) {
				if (viewer != null) {
					if (!(viewer.getLabelProvider() instanceof DecoratingLabelProvider)) {
						viewer.setLabelProvider(new DecoratingLabelProvider((ILabelProvider) viewer.getLabelProvider(),
								PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
					}
				}
			}
		}
	}

	public void partClosed(IWorkbenchPart partRef) {
		// ignore
	}

	public void partDeactivated(IWorkbenchPart partRef) {
		// ignore
	}

	public void pageActivated(IWorkbenchPage page) {
		// ignore
	}

	public void pageClosed(IWorkbenchPage page) {
		// ignore
	}

	public void pageOpened(IWorkbenchPage page) {
		// ignore

	}

}
