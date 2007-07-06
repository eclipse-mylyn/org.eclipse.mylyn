/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.ui.UiUtil;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public abstract class AbstractInterestManipulationAction implements IViewActionDelegate, IWorkbenchWindowActionDelegate {

	private static final String MESSAGE_NO_CONTEXT = "No task context is active, or element not found in context";

	public static final String SOURCE_ID = "org.eclipse.mylyn.ui.interest.user";

	protected IViewPart view;

	protected IWorkbenchWindow window;
	
	private ISelection selection;

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void init(IViewPart view) {
		this.view = view;
	}

	protected abstract boolean isRemove();

	public void run(IAction action) {
		if (!ContextCorePlugin.getContextManager().isContextActive()) {
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), ITasksUiConstants.TITLE_DIALOG,
					MESSAGE_NO_CONTEXT);
			return;
		}

		boolean increment = isRemove();
		
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			for (Object object : structuredSelection.toList()) {
				IInteractionElement node = null;
				if (object instanceof IInteractionElement) {
					node = (IInteractionElement) object;
				} else {
					AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(object);
					String handle = bridge.getHandleIdentifier(object);
					node = ContextCorePlugin.getContextManager().getElement(handle);
				}
				if (node != null) {
					if (!increment) {
						try {
							// NOTE: need to set the selection null so the
							// automatic reselection does not induce interest
							PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow()
									.getActivePage()
									.getActivePart()
									.getSite()
									.getSelectionProvider()
									.setSelection(null);
						} catch (Exception e) {
							// ignore
						}
					}
					boolean manipulated = ContextCorePlugin.getContextManager().manipulateInterestForElement(node,
							increment, false, SOURCE_ID);
					if (!manipulated) {
						UiUtil.displayInterestManipulationFailure();
					}
				}
			}
		} else {
			IInteractionElement node = ContextCorePlugin.getContextManager().getActiveElement();
			if (node != null) {
				boolean manipulated = ContextCorePlugin.getContextManager().manipulateInterestForElement(node,
						increment, false, SOURCE_ID);
				if (!manipulated) {
					UiUtil.displayInterestManipulationFailure();
				}
			} else {
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), ITasksUiConstants.TITLE_DIALOG,
						MESSAGE_NO_CONTEXT);
				// MylarStatusHandler.log("no active element for interest
				// manipulation", this);
			}
		}
	}

	public void dispose() {
		// ignore
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
}
