/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.team.ui.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.*;

import org.eclipse.compare.patch.ApplyPatchOperation;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.team.MylarTeamPlugin;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class ApplyPatchAction implements IViewActionDelegate {

	private ISelection currentSelection;

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		if (currentSelection instanceof StructuredSelection) {
			Object object = ((StructuredSelection) currentSelection).getFirstElement();
			if (object instanceof RepositoryAttachment) {
				RepositoryAttachment attachment = (RepositoryAttachment) object;
				if (attachment.getRepository() == null) {
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
							TasksUiPlugin.TITLE_DIALOG, 
							"Please synchronize this task in order to apply the patch.");
				} else {
					final String contents = TasksUiPlugin.getRepositoryManager().getAttachmentContents(attachment);
					// TODO: shouldn't need to do this
					IPath statePath = MylarTeamPlugin.getDefault().getStateLocation();
					final File tempFile = new File(statePath.toString() + File.separator + "patch.txt");
					tempFile.deleteOnExit();
					try {
						BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
						writer.write(contents);
//						System.err.println(">>> " + contents);
						writer.close();
					} catch (Exception e) {
						MylarStatusHandler.fail(e, "could not write patch file", true);
					} 
					
					IStorage storage = new IStorage() {

						@SuppressWarnings("deprecation")
						public InputStream getContents() throws CoreException {
							return new StringBufferInputStream(contents);
						}

						public IPath getFullPath() {
							return new Path(tempFile.getAbsolutePath());
						}

						public String getName() {
							return null;
						}

						public boolean isReadOnly() {
							return true;
						}

						public Object getAdapter(Class adapter) {
							return null;
						}

					};
					ApplyPatchOperation op = new ApplyPatchOperation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart(), storage, null, null);
					BusyIndicator.showWhile(Display.getDefault(), op);
				}
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.currentSelection = selection;
	}
}
