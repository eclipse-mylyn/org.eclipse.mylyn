/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.ConnectorBrand;
import org.eclipse.mylyn.internal.tasks.ui.IBrandManager;
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

	private ConnectorBrand connectorBrand;

	static class ConnectorBrandContentProvider implements IStructuredContentProvider {

		private final Collection<? extends AbstractRepositoryConnector> connectors;

		private final IBrandManager brandManager;

		public ConnectorBrandContentProvider(IBrandManager brandManager,
				Collection<? extends AbstractRepositoryConnector> connectors) {
			this.brandManager = brandManager;
			this.connectors = connectors;
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			List<ConnectorBrand> connectorBrands = new ArrayList<ConnectorBrand>();
			for (AbstractRepositoryConnector connector : connectors) {
				if (connector.isUserManaged() && connector.canCreateRepository()) {
					connectorBrands.add(new ConnectorBrand(connector, null));
					for (String brand : brandManager.getBrands(connector.getConnectorKind())) {
						connectorBrands.add(new ConnectorBrand(connector, brand));
					}
				}
			}
			return connectorBrands.toArray();
		}
	}

	public SelectRepositoryConnectorPage() {
		super(Messages.SelectRepositoryConnectorPage_Select_a_task_repository_type);
		setTitle(Messages.SelectRepositoryConnectorPage_Select_a_task_repository_type);
		setDescription(Messages.SelectRepositoryConnectorPage_You_can_connect_to_an_existing_account_using_one_of_the_installed_connectors);
	}

	@Override
	public boolean canFlipToNextPage() {
		return connectorBrand != null;
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayoutFactory.fillDefaults().numColumns(1).spacing(0, 3).applyTo(container);

		viewer = new TableViewer(container, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ConnectorBrandContentProvider(TasksUiPlugin.getDefault().getBrandManager(),
				TasksUi.getRepositoryManager().getRepositoryConnectors()));
		viewer.setSorter(new TaskRepositoriesSorter());
		viewer.setLabelProvider(new DecoratingLabelProvider(new TaskRepositoryLabelProvider(),
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		viewer.setInput(TasksUi.getRepositoryManager().getRepositoryConnectors());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.getFirstElement() instanceof ConnectorBrand) {
					setConnectorBrand((ConnectorBrand) selection.getFirstElement());
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
						handlerService.executeCommand(discoveryWizardCommand.getId(), null);
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

	void setConnectorBrand(ConnectorBrand connectorBrand) {
		this.connectorBrand = connectorBrand;
	}

	public AbstractRepositoryConnector getConnector() {
		return connectorBrand == null ? null : connectorBrand.getConnector();
	}

	public String getBrand() {
		return connectorBrand == null ? null : connectorBrand.getBrandId();
	}

}
