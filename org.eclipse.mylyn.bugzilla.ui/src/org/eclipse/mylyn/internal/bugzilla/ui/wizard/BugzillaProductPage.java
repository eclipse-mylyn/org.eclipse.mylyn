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
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.NewBugzillaReport;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryQuery;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskAttribute;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Shawn Minto
 * @author Rob Elves
 * 
 * Product selection page of new bug wizard
 */
public class BugzillaProductPage extends WizardPage implements Listener {

	// A Map from Java's OS and Platform to Buzilla's
	private Map<String, String> java2buzillaOSMap = new HashMap<String, String>();

	private Map<String, String> java2buzillaPlatformMap = new HashMap<String, String>();

	private static final String NEW_BUGZILLA_TASK_ERROR_TITLE = "New Bugzilla Task Error";

	private static final String DESCRIPTION = "Pick a product to open the new bug editor.\n"
			+ "Press the Update button if the product is not in the list.";

	private static final String LABEL_UPDATE = "Update Products from Repository";

	/** The list of products to submit a bug report for */
	static List<String> products = null;

	/**
	 * Reference to the bug wizard which created this page so we can create the
	 * second page
	 */
	NewBugzillaReportWizard bugWizard;

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

	protected ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getShell());

	protected IPreferenceStore prefs = BugzillaUiPlugin.getDefault().getPreferenceStore();

	private final IStructuredSelection selection;

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
	public BugzillaProductPage(IWorkbench workbench, NewBugzillaReportWizard bugWiz, TaskRepository repository,
			IStructuredSelection selection) {
		super("Page1");
		this.selection = selection;
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
		java2buzillaOSMap.put("win32", "Windows");
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

		Button updateButton = new Button(composite, SWT.LEFT | SWT.PUSH);
		updateButton.setText(LABEL_UPDATE);
		updateButton.setLayoutData(new GridData());

		updateButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				monitorDialog.open();
				IProgressMonitor monitor = monitorDialog.getProgressMonitor();
				monitor.beginTask("Updating repository report options...", 55);

				try {
					BugzillaUiPlugin.updateQueryOptions(repository, monitor);

					products = new ArrayList<String>();
					for (String product : BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_PRODUCT, null,
							repository.getUrl())) {
						products.add(product);
					}
					monitor.worked(1);
					populateList(false);
				} catch (LoginException exception) {
					// we had a problem that seems to have been caused from bad
					// login info
					MessageDialog
							.openError(
									null,
									"Login Error",
									"Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease check your settings in the bugzilla preferences. ");
					BugzillaPlugin.log(exception);
				} catch (IOException exception) {
					MessageDialog.openError(null, "Connection Error",
							"\nPlease check your settings in the bugzilla preferences. ");
				} catch (Exception exception) {
					MessageDialog.openError(null, "Error updating product list", "Error reported:\n"
							+ exception.getMessage());
				} finally {
					monitor.done();
					monitorDialog.close();
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
					products = BugzillaRepositoryUtil.getProductList(repository.getUrl(), MylarTaskListPlugin
							.getDefault().getProxySettings(), repository.getUserName(), repository.getPassword(),
							repository.getCharacterEncoding());
				}
				bugWizard.model.setConnected(true);
				bugWizard.model.setParsedProductsStatus(true);

			} catch (Exception e) {
				bugWizard.model.setConnected(false);
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(Display.getDefault().getActiveShell(), NEW_BUGZILLA_TASK_ERROR_TITLE,
								"Unable to get products. Ensure proper repository configuration in "
										+ TaskRepositoriesView.NAME + ".");
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
		ArrayList<String> products = new ArrayList<String>();
		if (selection == null) {
			return products.toArray(new String[0]);
		}
		
		Object element = selection.getFirstElement();
		if (element instanceof BugzillaRepositoryQuery) {
			BugzillaRepositoryQuery query = (BugzillaRepositoryQuery) element;
			String queryUrl = query.getQueryUrl();
			queryUrl = queryUrl.substring(queryUrl.indexOf("?") + 1);
			String[] options = queryUrl.split("&");

			for (String option : options) {
				String key = option.substring(0, option.indexOf("="));
				if ("product".equals(key)) {
					try {
						products.add(URLDecoder.decode(option.substring(option.indexOf("=") + 1), "UTF-8"));
					} catch (UnsupportedEncodingException ex) {
						// ignore
					}
				}
			}
		}

		// TODO find a way to map from tasks/query hits back to query/category

		return products.toArray(new String[products.size()]);
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
	 */
	public void saveDataToModel() throws KeyManagementException, LoginException, NoSuchAlgorithmException, IOException {
		NewBugzillaReport model = bugWizard.model;
		prevProduct = model.getProduct();
		model.setProduct((listBox.getSelection())[0]);

		if (!model.hasParsedAttributes() || !model.getProduct().equals(prevProduct)) {
			BugzillaRepositoryUtil.setupNewBugAttributes(repository.getUrl(), MylarTaskListPlugin.getDefault()
					.getProxySettings(), repository.getUserName(), repository.getPassword(), model, repository
					.getCharacterEncoding());
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
			if (bugzillaOS != null && opSysAttribute != null)
				opSysAttribute.setValue(bugzillaOS);
			if (bugzillaPlatform != null && platformAttribute != null)
				platformAttribute.setValue(bugzillaPlatform);

		} catch (Exception e) {
			MylarStatusHandler.fail(e, "could not set platform options", false);
		}
	}

	// @Override
	// public IWizardPage getNextPage() {
	// // save the product information to the model
	// saveDataToModel();
	// NewBugzillaReportWizard wizard = (NewBugzillaReportWizard) getWizard();
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
