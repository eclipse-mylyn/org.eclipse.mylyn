/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.actions;

import java.io.InputStream;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.patch.ApplyPatchOperation;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.team.ui.FocusedTeamUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class ApplyPatchAction extends BaseSelectionListenerAction implements IViewActionDelegate {

	public ApplyPatchAction() {
		super("Apply Patch");
	}

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
				final RepositoryAttachment attachment = (RepositoryAttachment) object;
				IStorage storage = new IStorage() {
					public InputStream getContents() throws CoreException {
						TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
								attachment.getRepositoryKind(), attachment.getRepositoryUrl());
						AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
								.getRepositoryConnector(attachment.getRepositoryKind());
						AbstractAttachmentHandler handler = connector.getAttachmentHandler();
						return handler.getAttachmentAsStream(repository, attachment, new NullProgressMonitor());
					}

					public IPath getFullPath() {
						return FocusedTeamUiPlugin.getDefault().getStateLocation();
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
				ApplyPatchOperation op = new ApplyPatchOperation(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().getActivePart(), storage, null, new CompareConfiguration());
				BusyIndicator.showWhile(Display.getDefault(), op);
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.currentSelection = selection;
	}
}
