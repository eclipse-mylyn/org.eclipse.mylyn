/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui.gist;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * Dialog to select a Gist task repository
 */
public class GistConnectorSelectionDialog extends SelectionDialog {

	private final Collection<TaskRepository> repos;

	/**
	 * @param parentShell
	 * @param repositories
	 */
	public GistConnectorSelectionDialog(Shell parentShell,
			Collection<TaskRepository> repositories) {
		super(parentShell);
		setTitle(Messages.GistConnectorSelectionDialog_Title);
		setMessage(Messages.GistConnectorSelectionDialog_Message);
		repos = repositories;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = (Composite) super.createDialogArea(parent);

		createMessageArea(c);

		TableViewer viewer = new TableViewer(c, SWT.SINGLE | SWT.FULL_SELECTION
				| SWT.V_SCROLL | SWT.H_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true)
				.applyTo(viewer.getControl());
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new DecoratingLabelProvider(
				new TaskRepositoryLabelProvider(), PlatformUI.getWorkbench()
						.getDecoratorManager().getLabelDecorator()));
		viewer.setComparator(new ViewerComparator());
		viewer.setInput(repos);
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				Object selected = ((IStructuredSelection) event.getSelection())
						.getFirstElement();
				setResult(Collections.singletonList(selected));
				okPressed();
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object selected = ((IStructuredSelection) event.getSelection())
						.getFirstElement();
				setResult(Collections.singletonList(selected));
			}
		});

		return c;
	}
}
