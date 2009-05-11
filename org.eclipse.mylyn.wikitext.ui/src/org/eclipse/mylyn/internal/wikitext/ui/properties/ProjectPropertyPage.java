/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextNature;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.wikitext.core.WikiText;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * A properties page for IProject that allow for configuration of WikiText settings such as the presence of a
 * {@link WikiTextNature}.
 * 
 * @author David Green
 */
public class ProjectPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

	private IProject project;

	private Button wikiTextNatureButton;

	public ProjectPropertyPage() {
		setTitle(Messages.ProjectPropertyPage_wikiText);
		setDescription(Messages.ProjectPropertyPage_configureInfo);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(1, false));

		wikiTextNatureButton = new Button(container, SWT.CHECK);
		wikiTextNatureButton.setText(Messages.ProjectPropertyPage_enableValidation);

		StringBuilder buf = new StringBuilder();
		SortedSet<String> extensions = new TreeSet<String>(WikiText.getMarkupFileExtensions());
		for (String extension : extensions) {
			if (buf.length() > 0) {
				buf.append(Messages.ProjectPropertyPage_3);
			}
			buf.append(Messages.ProjectPropertyPage_4);
			buf.append(extension);
		}
		wikiTextNatureButton.setToolTipText(NLS.bind(Messages.ProjectPropertyPage_validation_tooltip,
				new Object[] { buf.toString() }));

		project = (IProject) getElement().getAdapter(IProject.class);

		try {
			if (project.hasNature(WikiTextNature.ID)) {
				wikiTextNatureButton.setSelection(true);
			}
		} catch (CoreException e) {
			WikiTextUiPlugin.getDefault().log(e);
			container.setEnabled(false);
			setErrorMessage(e.getMessage());
			setValid(false);
		}

		return container;
	}

	@Override
	public void performHelp() {
		getControl().notifyListeners(SWT.Help, new Event());
	}

	@Override
	protected void setControl(Control newControl) {
		super.setControl(newControl);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(newControl,
				"org.eclipse.mylyn.wikitext.help.ui.projectSettings"); //$NON-NLS-1$
	}

	@Override
	public boolean performOk() {
		final boolean wantNature = wikiTextNatureButton.getSelection();
		final boolean[] ok = new boolean[1];
		ok[0] = false;
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation(ResourcesPlugin.getWorkspace().getRoot()) {
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException,
					InterruptedException {
				if (project.hasNature(WikiTextNature.ID) != wantNature) {
					if (wantNature) {
						WikiTextNature.install(project, monitor);
					} else {
						WikiTextNature.uninstall(project, monitor);
					}
				}
				ok[0] = true;
			}
		};
		try {
			new ProgressMonitorDialog(getShell()).run(true, true, operation);
		} catch (InvocationTargetException e) {
			String message = Messages.ProjectPropertyPage_operationError;
			String title = Messages.ProjectPropertyPage_unexpectedError;
			ErrorDialog.openError(getShell(), title, message, WikiTextUiPlugin.getDefault().createStatus(IStatus.ERROR,
					e.getCause()));
		} catch (InterruptedException e) {
			// Do nothing. Operation has been canceled by user.
		}

		return ok[0];
	}

}
