/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Eugene Kuleshov - improvements
 *     Willian Mitsuda - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.wizard;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;

/**
 * Product selection page of new bug wizard
 * 
 * @author Shawn Minto
 * @author Rob Elves
 * @author Mik Kersten
 */
public class BugzillaProductPage extends WizardPage {

	private static final String NEW_BUGZILLA_TASK_ERROR_TITLE = Messages.BugzillaProductPage_New_Bugzilla_Task_Error;

	private static final String DESCRIPTION = Messages.BugzillaProductPage_PICK_PRODUCT_TO_OPEN_NEW_BUG_EDITOR
			+ Messages.BugzillaProductPage_PRESS_UPDATE_BUTTON;

	private static final String LABEL_UPDATE = Messages.BugzillaProductPage_Update_Products_from_Repository;

	/** The list of products to submit a bug report for */
	private List<String> products = null;

	/**
	 * Handle product selection
	 */
	private FilteredTree productList;

	private final TaskRepository repository;

	protected IPreferenceStore prefs = BugzillaUiPlugin.getDefault().getPreferenceStore();

	/**
	 * Constructor for BugzillaProductPage
	 * 
	 * @param repository
	 *            The repository the data is coming from
	 * @param workbench
	 *            The instance of the workbench
	 * @param selection
	 */
	public BugzillaProductPage(TaskRepository repository) {
		super(Messages.BugzillaProductPage_PAGE_1);
		setTitle(IBugzillaConstants.TITLE_NEW_BUG);
		setDescription(DESCRIPTION);
		this.repository = repository;
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylyn.bugzilla.ui", //$NON-NLS-1$
				"icons/wizban/bug-wizard.gif")); //$NON-NLS-1$

	}

	public void createControl(Composite parent) {
		// create the composite to hold the widgets
		Composite composite = new Composite(parent, SWT.NULL);

		// create the desired layout for this wizard page
		composite.setLayout(new GridLayout());

		// create the list of bug reports
		productList = new FilteredTree(composite, SWT.SINGLE | SWT.BORDER, new PatternFilter());
		productList.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).hint(
				SWT.DEFAULT, 200).create());
		final TreeViewer productViewer = productList.getViewer();
		productViewer.setLabelProvider(new LabelProvider());
		productViewer.setContentProvider(new ITreeContentProvider() {

			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof Collection) {
					return ((Collection<?>) parentElement).toArray();
				}
				return null;
			}

			public Object getParent(Object element) {
				return null;
			}

			public boolean hasChildren(Object element) {
				return false;
			}

			public Object[] getElements(Object inputElement) {
				return getChildren(inputElement);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});

		productViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				if (getWizard().canFinish()) {
					if (getWizard().performFinish()) {
						((WizardDialog) getContainer()).close();
					}
				}
			}
		});

		initProducts();
		productViewer.setInput(products);
		productViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				// Initialize a variable with the no error status
				Status status = new Status(IStatus.OK, BugzillaUiPlugin.ID_PLUGIN, 0, "", null); //$NON-NLS-1$
				if (productViewer.getSelection().isEmpty()) {
					status = new Status(IStatus.ERROR, BugzillaUiPlugin.ID_PLUGIN, 0,
							Messages.BugzillaProductPage_YOU_MUST_SELECT_PRODUCT, null);
				}

				// Show the most serious error
				applyToStatusLine(status);
				isPageComplete();
				getWizard().getContainer().updateButtons();
			}

		});

		// HACK: waiting on delayed refresh of filtered tree
		final String[] selectedProducts = getSelectedProducts();
		if (selectedProducts.length > 0) {
			new UIJob("") { //$NON-NLS-1$
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					if (BugzillaProductPage.this.getControl() != null
							&& BugzillaProductPage.this.getControl().isDisposed()) {
						return Status.OK_STATUS;
					}
					productViewer.setSelection(new StructuredSelection(selectedProducts), true);
					productViewer.getControl().setFocus();
					return Status.OK_STATUS;
				}
			}.schedule(300L);
		} else {
			productList.setFocus();
		}

		Button updateButton = new Button(composite, SWT.LEFT | SWT.PUSH);
		updateButton.setText(LABEL_UPDATE);
		updateButton.setLayoutData(new GridData());

		updateButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateProdcts();
				productViewer.setInput(products);
			}
		});

		Dialog.applyDialogFont(composite);
		// set the composite as the control for this page
		setControl(composite);

		isPageComplete();
		getWizard().getContainer().updateButtons();
	}

	private void initProducts() {
		// try to get the list of products from the server
		try {
			products = BugzillaCorePlugin.getRepositoryConfiguration(repository, false, new NullProgressMonitor())
					.getProducts();

			if (products.isEmpty()) {
				updateProdcts();
			}

		} catch (final CoreException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(Display.getDefault().getActiveShell(), NEW_BUGZILLA_TASK_ERROR_TITLE,
							Messages.BugzillaProductPage_Unable_to_get_products);
				}
			});
		}
	}

	private String[] getSelectedProducts() {
		IStructuredSelection selection = getSelection();
		if (selection == null) {
			return new String[0];
		}

		ArrayList<String> products = new ArrayList<String>();

		Object element = selection.getFirstElement();
		if (element instanceof ITask) {
			ITask bugzillaTask = (ITask) element;
			if (bugzillaTask.getAttribute(BugzillaAttribute.PRODUCT.getKey()) != null) {
				products.add(bugzillaTask.getAttribute(BugzillaAttribute.PRODUCT.getKey()));
			}
		} else {
			IRepositoryQuery query = null;
			if (element instanceof IRepositoryQuery) {
				query = (IRepositoryQuery) element;
			}

			if (query != null && query.getConnectorKind().equals(BugzillaCorePlugin.CONNECTOR_KIND)) {
				String queryUrl = query.getUrl();
				queryUrl = queryUrl.substring(queryUrl.indexOf("?") + 1); //$NON-NLS-1$
				String[] options = queryUrl.split("&"); //$NON-NLS-1$

				for (String option : options) {
					int index = option.indexOf("="); //$NON-NLS-1$
					if (index != -1) {
						String key = option.substring(0, index);
						if ("product".equals(key)) { //$NON-NLS-1$
							try {
								products.add(URLDecoder.decode(option.substring(index + 1),
										repository.getCharacterEncoding()));
								// TODO: list box only accepts a single selection so
								// we break on first found
								break;
							} catch (UnsupportedEncodingException ex) {
								// ignore
							}
						}
					}
				}
			} else {
				if (element instanceof IAdaptable) {
					IAdaptable adaptable = (IAdaptable) element;
					ITask task = (ITask) adaptable.getAdapter(ITask.class);
					if (task != null) {
						ITask bugzillaTask = (ITask) element;
						if (bugzillaTask.getAttribute(BugzillaAttribute.PRODUCT.getKey()) != null) {
							products.add(bugzillaTask.getAttribute(BugzillaAttribute.PRODUCT.getKey()));
						}
					}
				}
			}
		}

		return products.toArray(new String[products.size()]);
	}

	private IStructuredSelection getSelection() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection = window.getSelectionService().getSelection();
		if (selection instanceof IStructuredSelection) {
			return (IStructuredSelection) selection;
		}
		return null;
	}

	/**
	 * Applies the status to the status line of a dialog page.
	 * 
	 * @param status
	 *            The status to apply to the status line
	 */
	protected void applyToStatusLine(IStatus status) {
		String message = status.getMessage();
		if (message.length() == 0) {
			message = null;
		}
		switch (status.getSeverity()) {
		case IStatus.OK:
			setErrorMessage(null);
			setMessage(message);
			break;
		case IStatus.WARNING:
			setErrorMessage(null);
			setMessage(message, IMessageProvider.WARNING);
			break;
		case IStatus.INFO:
			setErrorMessage(null);
			setMessage(message, IMessageProvider.INFORMATION);
			break;
		default:
			setErrorMessage(null);
			setMessage(message, IMessageProvider.ERROR);
			break;
		}
	}

//	/**
//	 * Save the currently selected product to the taskData when next is clicked
//	 */
//	public void saveDataToModel() throws CoreException {
//		RepositoryTaskData model = bugWizard.taskData;
//		model.setAttributeValue(BugzillaReportElement.PRODUCT.getKey(), getSelectedProduct());
//		AbstractLegacyRepositoryConnector connector = (AbstractLegacyRepositoryConnector) TasksUi.getRepositoryManager()
//				.getRepositoryConnector(repository.getConnectorKind());
//		if (connector == null) {
//			throw new CoreException(new Status(IStatus.ERROR, BugzillaUiPlugin.PLUGIN_ID,
//					"Error AbstractRepositoryConnector could not been retrieved.\n\n"));
//		}
//		AbstractTaskDataHandler taskDataHandler = connector.getLegacyTaskDataHandler();
//		if (taskDataHandler == null) {
//			throw new CoreException(new Status(IStatus.ERROR, BugzillaUiPlugin.PLUGIN_ID,
//					"Error AbstractTaskDataHandler could not been retrieved.\n\n"));
//		}
//		taskDataHandler.initializeTaskData(repository, model, null);
//
//		// platform/os are now set to All/All
//		BugzillaCorePlugin.getDefault().setPlatformDefaultsOrGuess(repository, model);
//	}

	public String getSelectedProduct() {
		return (String) ((IStructuredSelection) productList.getViewer().getSelection()).getFirstElement();
	}

	@Override
	public boolean isPageComplete() {
		return !productList.getViewer().getSelection().isEmpty();
//		return bugWizard.completed;
	}

	private void updateProdcts() {
		final AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(Messages.BugzillaProductPage_Updating_repository_report_options_,
							IProgressMonitor.UNKNOWN);
					try {
						connector.updateRepositoryConfiguration(repository, monitor);
					} catch (CoreException e) {
						// TODO: remove exceptions from communication of connectivity errors to the user
						if (e.getStatus().getException() instanceof GeneralSecurityException) {
							StatusHandler.fail(new Status(IStatus.WARNING, BugzillaUiPlugin.ID_PLUGIN,
									"Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\n" //$NON-NLS-1$
											+ "Please ensure your task repository is properly configured.", e)); //$NON-NLS-1$
						} else if (e.getStatus().getException() instanceof IOException) {
							StatusHandler.fail(new Status(IStatus.WARNING, BugzillaUiPlugin.ID_PLUGIN,
									"Connection Error, please ensure your task repository is properly configured.", e)); //$NON-NLS-1$
						} else {
							StatusHandler.fail(new Status(IStatus.WARNING, BugzillaUiPlugin.ID_PLUGIN,
									"Error updating repository attributes for " + repository.getRepositoryUrl(), e)); //$NON-NLS-1$
						}
						return;
					}

					RepositoryConfiguration repositoryConfiguration = null;
					try {
						repositoryConfiguration = BugzillaCorePlugin.getRepositoryConfiguration(repository, false,
								monitor);
					} catch (final CoreException e) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								MessageDialog.openError(Display.getDefault().getActiveShell(),
										Messages.BugzillaProductPage_Bugzilla_Search_Page,
										Messages.BugzillaProductPage_Unable_to_get_configuration);
							}
						});
					}
					products = new ArrayList<String>();
					if (repositoryConfiguration != null) {
						for (String product : repositoryConfiguration.getProducts()) {
							products.add(product);
						}
					}
				}
			});

		} catch (InvocationTargetException ex) {
			MessageDialog.openError(null, Messages.BugzillaProductPage_Error_updating_product_list,
					Messages.BugzillaProductPage_Error_reported + ex.getCause().getMessage());
		} catch (InterruptedException ex) {
			// canceled
		}
	}

}
