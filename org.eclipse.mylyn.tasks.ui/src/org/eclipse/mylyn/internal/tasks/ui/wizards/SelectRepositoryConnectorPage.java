/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesSorter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Mik Kersten
 * @author David Green
 */
public class SelectRepositoryConnectorPage extends WizardPage {

	private TableViewer viewer;

	private AbstractRepositoryConnector connector;

	static class RepositoryContentProvider implements IStructuredContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			List<AbstractRepositoryConnector> userManagedRepositories = new ArrayList<AbstractRepositoryConnector>();
			for (AbstractRepositoryConnector connector : TasksUi.getRepositoryManager().getRepositoryConnectors()) {
				if (connector.isUserManaged()) {
					userManagedRepositories.add(connector);
				}
			}

			return userManagedRepositories.toArray();
		}
	}

	public SelectRepositoryConnectorPage() {
		super(Messages.SelectRepositoryConnectorPage_Select_a_task_repository_type);
		setTitle(Messages.SelectRepositoryConnectorPage_Select_a_task_repository_type);
		setDescription(Messages.SelectRepositoryConnectorPage_You_can_connect_to_an_existing_account_using_one_of_the_installed_connectors);
	}

	@Override
	public boolean canFlipToNextPage() {
		return connector != null;
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayoutFactory.fillDefaults().numColumns(1).spacing(0, 3).applyTo(container);

		viewer = new TableViewer(container, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new RepositoryContentProvider());
		viewer.setSorter(new TaskRepositoriesSorter());
		viewer.setLabelProvider(new TaskRepositoryLabelProvider());
		viewer.setInput(TasksUi.getRepositoryManager().getRepositoryConnectors());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.getFirstElement() instanceof AbstractRepositoryConnector) {
					connector = (AbstractRepositoryConnector) selection.getFirstElement();
					setPageComplete(true);
				}
			}

		});

		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				getContainer().showPage(getNextPage());
			}
		});
		GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getControl());

		// add a hyperlink for connector discovery if it's available and enabled.

		// we integrate with discovery via a well-known command, which when invoked launches the discovery wizard
		final Command discoveryWizardCommand = TasksUiInternal.getConfiguredDiscoveryWizardCommand();
		if (discoveryWizardCommand != null && discoveryWizardCommand.isEnabled()) {
			Button discoveryButton = new Button(container, SWT.PUSH);
			GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(discoveryButton);
			discoveryButton.setText(Messages.SelectRepositoryConnectorPage_activateDiscovery);
			discoveryButton.setImage(CommonImages.getImage(CommonImages.DISCOVERY));
			discoveryButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
							IHandlerService.class);
					try {
						discoveryWizardCommand.executeWithChecks(createExecutionEvent(discoveryWizardCommand,
								handlerService));
					} catch (Exception e) {
						IStatus status = new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, NLS.bind(
								Messages.SelectRepositoryConnectorPage_discoveryProblemMessage,
								new Object[] { e.getMessage() }), e);
						TasksUiInternal.logAndDisplayStatus(
								Messages.SelectRepositoryConnectorPage_discoveryProblemTitle, status);
					}
				}

			});
		}

		Dialog.applyDialogFont(container);
		setControl(container);
	}

	private ExecutionEvent createExecutionEvent(Command command, IHandlerService handlerService) {
		return new ExecutionEvent(command, Collections.emptyMap(), null,
				TasksUiInternal.createDiscoveryWizardEvaluationContext(handlerService));
	}

	public AbstractRepositoryConnector getConnector() {
		return connector;
	}

}
