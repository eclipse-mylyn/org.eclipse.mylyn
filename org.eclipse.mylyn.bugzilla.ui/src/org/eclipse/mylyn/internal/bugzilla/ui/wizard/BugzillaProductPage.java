/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.ui.wizard;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaQueryHit;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.NewBugzillaReport;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Shawn Minto
 * @author Rob Elves
 * @author Mik Kersten
 * @author Eugene Kuleshov
 * 
 * Product selection page of new bug wizard
 */
public class BugzillaProductPage extends WizardPage implements Listener {

	private static final String OPTION_ALL = "All";

	// A Map from Java's OS and Platform to Buzilla's
	private Map<String, String> java2buzillaOSMap = new HashMap<String, String>();

	private Map<String, String> java2buzillaPlatformMap = new HashMap<String, String>();

	private static final String NEW_BUGZILLA_TASK_ERROR_TITLE = "New Bugzilla Task Error";

	private static final String DESCRIPTION = "Pick a product to open the new bug editor.\n"
			+ "Press the Update button if the product is not in the list.";

	private static final String LABEL_UPDATE = "Update Products from Repository";

	/** The list of products to submit a bug report for */
	private List<String> products = null;

	/**
	 * Reference to the bug wizard which created this page so we can create the
	 * second page
	 */
	private NewBugzillaTaskWizard bugWizard;

	/** The instance of the workbench */
	protected IWorkbench workbench;

	/** The list box for the list of items to choose from */
	protected org.eclipse.swt.widgets.List listBox;

	/** Status variable for the possible errors on this page */
	protected IStatus listStatus;

	/**
	 * String to hold previous product; determines if attribute option values
	 * need to be updated
	 */
	private String prevProduct;

	private final TaskRepository repository;
	
	protected IPreferenceStore prefs = BugzillaUiPlugin.getDefault().getPreferenceStore();


	/**
	 * Constructor for BugzillaProductPage
	 * 
	 * @param workbench
	 *            The instance of the workbench
	 * @param bugWiz
	 *            The bug wizard which created this page
	 * @param repository
	 *            The repository the data is coming from
	 * @param selection
	 */
	public BugzillaProductPage(IWorkbench workbench, NewBugzillaTaskWizard bugWiz, TaskRepository repository) {
		super("Page1");
		setTitle(IBugzillaConstants.TITLE_NEW_BUG);
		setDescription(DESCRIPTION);
		this.workbench = workbench;

		// set the status for the page
		listStatus = new Status(IStatus.OK, "not_used", 0, "", null);

		this.bugWizard = bugWiz;
		this.repository = repository;
		setImageDescriptor(BugzillaUiPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.bugzilla.ui",
				"icons/wizban/bug-wizard.gif"));

		java2buzillaPlatformMap.put("x86", "PC");
		java2buzillaPlatformMap.put("x86_64", "PC");
		java2buzillaPlatformMap.put("ia64", "PC");
		java2buzillaPlatformMap.put("ia64_32", "PC");
		java2buzillaPlatformMap.put("sparc", "Sun");
		java2buzillaPlatformMap.put("ppc", "Power");

		java2buzillaOSMap.put("aix", "AIX");
		java2buzillaOSMap.put("hpux", "HP-UX");
		java2buzillaOSMap.put("linux", "Linux");
		java2buzillaOSMap.put("macosx", "MacOS X");
		java2buzillaOSMap.put("qnx", "QNX-Photon");
		java2buzillaOSMap.put("solaris", "Solaris");
		java2buzillaOSMap.put("win32", "Windows XP");
	}

	public void createControl(Composite parent) {
		// create the composite to hold the widgets
		Composite composite = new Composite(parent, SWT.NULL);

		// create the desired layout for this wizard page
		composite.setLayout(new GridLayout(1, true));

		// create the list of bug reports
		listBox = new org.eclipse.swt.widgets.List(composite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 200;
		listBox.setLayoutData(gd);

		// Each wizard has different types of items to add to the list
		populateList(true);

		listBox.addListener(SWT.Selection, this);

		listBox.setSelection(getSelectedProducts());
		listBox.showSelection();
		// listBox.addSelectionListener(new SelectionListener() {
		//
		// public void widgetDefaultSelected(SelectionEvent e) {
		// // ignore
		// }
		//
		// public void widgetSelected(SelectionEvent e) {
		// getWizard().performFinish();
		// getWizard().dispose();
		// // TODO: is this the wrong way of doing the close?
		// getContainer().getShell().close();
		// }
		// });

		Button updateButton = new Button(composite, SWT.LEFT | SWT.PUSH);
		updateButton.setText(LABEL_UPDATE);
		updateButton.setLayoutData(new GridData());

		updateButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					final AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
							.getRepositoryConnector(repository.getKind());

					getContainer().run(true, false, new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) throws InvocationTargetException,
								InterruptedException {
							monitor.beginTask("Updating repository report options...", IProgressMonitor.UNKNOWN);
							try {
								connector.updateAttributes(repository, monitor);
							} catch (CoreException ce) {
								if (ce.getStatus().getException() instanceof GeneralSecurityException) {
									MylarStatusHandler.fail(ce,
											"Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\n"
													+ "Please ensure proper configuration in "
													+ TaskRepositoriesView.NAME + ". ", true);
								} else if (ce.getStatus().getException() instanceof IOException) {
									MylarStatusHandler.fail(ce,
											"Connection Error, please ensure proper configuration in "
													+ TaskRepositoriesView.NAME + ".", true);
								} else {
									MylarStatusHandler.fail(ce, "Error updating repository attributes for "
											+ repository.getUrl(), true);
								}
								return;
							}
							BugzillaUiPlugin.updateQueryOptions(repository, monitor);

							products = new ArrayList<String>();
							for (String product : BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_PRODUCT,
									null, repository.getUrl())) {
								products.add(product);
							}
						}
					});
					populateList(false);
				} catch (InvocationTargetException ex) {
					MessageDialog.openError(null, "Error updating product list", "Error reported:\n"
							+ ex.getCause().getMessage());
				} catch (InterruptedException ex) {
					// Was cancelled...
				}
			}
		});

		// set the composite as the control for this page
		setControl(composite);

		isPageComplete();
		getWizard().getContainer().updateButtons();
	}

	private void initProducts() {
		// try to get the list of products from the server
		if (!bugWizard.model.hasParsedProducts()) {
			String repositoryUrl = repository.getUrl();
			try {
				String[] storedProducts = BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_PRODUCT, null,
						repositoryUrl);
				if (storedProducts.length > 0) {
					products = Arrays.asList(storedProducts);
				} else {
					products = BugzillaCorePlugin.getDefault().getRepositoryConfiguration(repository, false).getProducts();
				}
				bugWizard.model.setConnected(true);
				bugWizard.model.setParsedProductsStatus(true);

			} catch (final Exception e) {
				bugWizard.model.setConnected(false);
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(Display.getDefault().getActiveShell(), NEW_BUGZILLA_TASK_ERROR_TITLE,
								"Unable to get products. Ensure proper repository configuration in "
										+ TaskRepositoriesView.NAME + ".\n\n");
						MylarStatusHandler.log(e, "Failed to retrieve products from server");
					}
				});
			}
		}
	}

	/**
	 * Populates the listBox with all available products.
	 * 
	 * @param read
	 */
	protected void populateList(boolean init) {
		if (init) {
			initProducts();
		}

		if (products != null) {
			listBox.removeAll();
			Iterator<String> itr = products.iterator();

			while (itr.hasNext()) {
				String prod = itr.next();
				listBox.add(prod);
			}
		}
		listBox.setFocus();
	}

	private String[] getSelectedProducts() {
		IStructuredSelection selection = getSelection();
		if (selection == null) {
			return new String[0];
		}

		ArrayList<String> products = new ArrayList<String>();

		Object element = selection.getFirstElement();
		if (element instanceof BugzillaTask) {
			BugzillaTask task = (BugzillaTask) element;
			products.add(task.getTaskData().getProduct());
		} else {
			BugzillaRepositoryQuery query = null;
			if (element instanceof BugzillaRepositoryQuery) {
				query = (BugzillaRepositoryQuery) element;
	
			} else if (element instanceof BugzillaQueryHit) {
				BugzillaQueryHit hit = (BugzillaQueryHit) element;
				if (hit.getParent() != null && hit.getParent() instanceof BugzillaRepositoryQuery) {
					query = (BugzillaRepositoryQuery) hit.getParent();
				}
			}
			
			if (query != null) {
				String queryUrl = query.getUrl();
				queryUrl = queryUrl.substring(queryUrl.indexOf("?") + 1);
				String[] options = queryUrl.split("&");
	
				for (String option : options) {
					String key = option.substring(0, option.indexOf("="));
					if ("product".equals(key)) {
						try {
							products.add(URLDecoder.decode(option.substring(option.indexOf("=") + 1), repository
									.getCharacterEncoding()));
							// TODO: list box only accepts a single selection so we
							// break on first found
							break;
						} catch (UnsupportedEncodingException ex) {
							// ignore
						}
					}
				}
			} else {
				if(element instanceof IAdaptable) {
					IAdaptable adaptable = (IAdaptable) element;
					ITask task = (ITask) adaptable.getAdapter(ITask.class);
					if(task instanceof BugzillaTask) {
						BugzillaTask bugzillaTask = (BugzillaTask) task;
						products.add(bugzillaTask.getTaskData().getProduct());
					}
				}				
			}
		}

		return products.toArray(new String[products.size()]);
	}

	private IStructuredSelection getSelection() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection = window.getSelectionService().getSelection();
		if(selection instanceof IStructuredSelection) {
			return (IStructuredSelection) selection;
		}		
		return null;
	}

	public void handleEvent(Event event) {
		handleEventHelper(event, "You must select a product");
	}

	/**
	 * A helper function for "handleEvent"
	 * 
	 * @param event
	 *            the event which occurred
	 * @param errorMessage
	 *            the error message unique to the wizard calling this function
	 */
	protected void handleEventHelper(Event event, String errorMessage) {
		// Initialize a variable with the no error status
		Status status = new Status(IStatus.OK, "not_used", 0, "", null);

		// If the event is triggered by the list of items, respond with the
		// corresponding status
		if (event.widget == listBox) {
			if (listBox.getSelectionIndex() == -1)
				status = new Status(IStatus.ERROR, "not_used", 0, errorMessage, null);
			listStatus = status;
		}

		// Show the most serious error
		applyToStatusLine(listStatus);
		isPageComplete();
		getWizard().getContainer().updateButtons();
	}

	/**
	 * Applies the status to the status line of a dialog page.
	 * 
	 * @param status
	 *            The status to apply to the status line
	 */
	protected void applyToStatusLine(IStatus status) {
		String message = status.getMessage();
		if (message.length() == 0)
			message = null;
		switch (status.getSeverity()) {
		case IStatus.OK:
			setErrorMessage(null);
			setMessage(message);
			break;
		case IStatus.WARNING:
			setErrorMessage(null);
			setMessage(message, WizardPage.WARNING);
			break;
		case IStatus.INFO:
			setErrorMessage(null);
			setMessage(message, WizardPage.INFORMATION);
			break;
		default:
			setErrorMessage(null);
			setMessage(message, WizardPage.ERROR);
			break;
		}
	}

	/**
	 * Save the currently selected product to the model when next is clicked
	 * 
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws LoginException
	 * @throws KeyManagementException
	 * @throws BugzillaException
	 */
	public void saveDataToModel() throws CoreException {
		NewBugzillaReport model = bugWizard.model;
		prevProduct = model.getProduct();
		model.setProduct((listBox.getSelection())[0]);

		if (!model.hasParsedAttributes() || !model.getProduct().equals(prevProduct)) {
			BugzillaRepositoryConnector.setupNewBugAttributes(repository, model);
			model.setParsedAttributesStatus(true);
		}

		setPlatformOptions(model);
	}

	@Override
	public boolean isPageComplete() {
		bugWizard.completed = listBox.getSelectionIndex() != -1;
		return bugWizard.completed;
	}

	public void setPlatformOptions(NewBugzillaReport newBugModel) {
		try {

			// Get OS Lookup Map
			// Check that the result is in Values, if it is not, set it to other
			RepositoryTaskAttribute opSysAttribute = newBugModel.getAttribute(BugzillaReportElement.OP_SYS
					.getKeyString());
			RepositoryTaskAttribute platformAttribute = newBugModel.getAttribute(BugzillaReportElement.REP_PLATFORM
					.getKeyString());

			String OS = Platform.getOS();
			String platform = Platform.getOSArch();

			String bugzillaOS = null; // Bugzilla String for OS
			String bugzillaPlatform = null; // Bugzilla String for Platform

			if (java2buzillaOSMap != null && java2buzillaOSMap.containsKey(OS) && opSysAttribute != null
					&& opSysAttribute.getOptionValues() != null) {
				bugzillaOS = java2buzillaOSMap.get(OS);
				if (opSysAttribute != null && !opSysAttribute.getOptionValues().values().contains(bugzillaOS)) {
					// If the OS we found is not in the list of available
					// options, set bugzillaOS
					// to null, and just use "other"
					bugzillaOS = null;
				}
			} else {
				// If we have a strangeOS, then just set buzillaOS to null, and
				// use "other"
				bugzillaOS = null;
			}

			if (platform != null && java2buzillaPlatformMap.containsKey(platform)) {
				bugzillaPlatform = java2buzillaPlatformMap.get(platform);
				if (platformAttribute != null
						&& !platformAttribute.getOptionValues().values().contains(bugzillaPlatform)) {
					// If the platform we found is not int the list of available
					// optinos, set the
					// Bugzilla Platform to null, and juse use "other"
					bugzillaPlatform = null;
				}
			} else {
				// If we have a strange platform, then just set bugzillaPatforrm
				// to null, and use "other"
				bugzillaPlatform = null;
			}

			// Set the OS and the Platform in the model
			if (bugzillaOS != null && opSysAttribute != null) {
				opSysAttribute.setValue(bugzillaOS);
			} else if (opSysAttribute != null && opSysAttribute.getOptionValues().values().contains(OPTION_ALL)) {
				opSysAttribute.setValue(OPTION_ALL);
			}

			if (bugzillaPlatform != null && platformAttribute != null) {
				platformAttribute.setValue(bugzillaPlatform);
			} else if (platformAttribute != null && platformAttribute.getOptionValues().values().contains(OPTION_ALL)) {
				opSysAttribute.setValue(OPTION_ALL);
			}

		} catch (Exception e) {
			MylarStatusHandler.fail(e, "could not set platform options", false);
		}
	}

	// @Override
	// public IWizardPage getNextPage() {
	// // save the product information to the model
	// saveDataToModel();
	// NewBugzillaTaskWizard wizard = (NewBugzillaTaskWizard) getWizard();
	// NewBugzillaReport model = wizard.model;
	//
	// // try to get the attributes from the bugzilla server
	// try {
	// if (!model.hasParsedAttributes() ||
	// !model.getProduct().equals(prevProduct)) {
	// BugzillaRepositoryUtil.setupNewBugAttributes(repository.getUrl(),
	// MylarTaskListPlugin.getDefault()
	// .getProxySettings(), repository.getUserName(), repository.getPassword(),
	// model, repository
	// .getCharacterEncoding());
	// model.setParsedAttributesStatus(true);
	// }
	//
	// // if (prevProduct == null) {
	// // bugWizard.setAttributePage(new WizardAttributesPage(workbench));
	// // bugWizard.addPage(bugWizard.getAttributePage());
	// // } else {
	// // // selected product has changed
	// // // will createControl again with new attributes in model
	// // bugWizard.getAttributePage().setControl(null);
	// // }
	//			
	// } catch (final Exception e) {
	// e.printStackTrace();
	// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
	// public void run() {
	// MessageDialog.openError(Display.getDefault().getActiveShell(),
	// NEW_BUGZILLA_TASK_ERROR_TITLE, e
	// .getLocalizedMessage()
	// + " Ensure proper repository configuration in " +
	// TaskRepositoriesView.NAME + ".");
	// }
	// });
	// // MylarStatusHandler.fail(e, e.getLocalizedMessage()+" Ensure
	// // proper repository configuration in
	// // "+TaskRepositoriesView.NAME+".", true);
	// // BugzillaPlugin.getDefault().logAndShowExceptionDetailsDialog(e,
	// // "occurred.", "Bugzilla Error");
	// }
	// return super.getNextPage();
	// }

}
