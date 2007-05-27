/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.team.ui.actions;

import java.io.*;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.patch.ApplyPatchOperation;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.team.MylarTeamPlugin;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 */
public class ApplyPatchAction extends BaseSelectionListenerAction implements IViewActionDelegate {

	protected ApplyPatchAction(String text) {
		super(text);
	}

	private ISelection currentSelection;

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		if (currentSelection instanceof StructuredSelection) {
			Object object = ((StructuredSelection) currentSelection).getFirstElement();
			if (object instanceof RepositoryAttachment) {
				RepositoryAttachment attachment = (RepositoryAttachment) object;
				final String contents = TasksUiPlugin.getRepositoryManager().getAttachmentContents(attachment);
				if (contents == null) {
					MessageDialog
							.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
									ITasksUiConstants.TITLE_DIALOG,
									"Patch could not be retrieved. Please try re-synchronizing task in order to apply the patch.");
				} else {
					IStorage storage = new IStorage() {

						@SuppressWarnings("deprecation")
						public InputStream getContents() throws CoreException {
							return new StringBufferInputStream(contents);
						}

						public IPath getFullPath() {
							return MylarTeamPlugin.getDefault().getStateLocation();
						}

						public String getName() {
							return null;
						}

						public boolean isReadOnly() {
							return true;
						}

						@SuppressWarnings("unchecked")
						public Object getAdapter(Class adapter) {
							return null;
						}

					};
					ApplyPatchOperation op = new ApplyPatchOperation(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart(), 
							storage, null, new CompareConfiguration());
					BusyIndicator.showWhile(Display.getDefault(), op);
				}

			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.currentSelection = selection;
	}
}
