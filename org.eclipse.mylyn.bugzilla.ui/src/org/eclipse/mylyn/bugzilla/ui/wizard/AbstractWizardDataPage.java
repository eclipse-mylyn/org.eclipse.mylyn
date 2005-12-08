/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.ui.wizard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.bugzilla.core.Attribute;
import org.eclipse.mylar.bugzilla.core.NewBugModel;
import org.eclipse.mylar.bugzilla.ui.editor.AbstractBugEditor;
import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;

/**
 * Class that contains shared functions for the last page of the wizards that
 * submit bug reports. This page allows the user to set the bug report's
 * attributes before submitting it.
 * 
 * @author Mik Kersten (hardening of initial prototype)
 */
public abstract class AbstractWizardDataPage extends WizardPage implements Listener {

	public static final String ATTRIBUTE_URL = "URL";
	public static final String ATTRIBUTE_PRIORITY = "Priority";
	public static final String ATTRIBUTE_COMPONENT = "Component";
	public static final String ATTRIBUTE_SEVERITY = "Severity";
	public static final String ATTRIBUTE_VERSION = "Version";
	public static final String ATTRIBUTE_PLATFORM = "Platform";
	public static final String ATTRIBUTE_OS = "OS";

	/** The instance of the workbench */
	protected IWorkbench workbench;

	/** Text field for the bugs url */
	protected Text urlText;

	/** Text field for the description of the bug */
	protected Text descriptionText;

	/** Text field for the summary of the bug */
	protected Text summaryText;

	/** Text field for the assignedTo of the bug */
	protected Text assignedToText;

	/** Radio button to select when sending the new bug report to the server */
	protected Button serverButton;

	/** Radio button to select when saving the new bug report offline */
	protected Button offlineButton;

	/** Combo box for the component that caused the bug */
	protected Combo componentCombo;

	/** Combo box for the priority of the bug */
	protected Combo priorityCombo;

	/** Combo box for the platform the bug occurred on */
	protected Combo platformCombo;

	/** Combo box for the severity of the bug */
	protected Combo severityCombo;

	/** Combo box for the products version */
	protected Combo versionCombo;

	/** Combo box for the OS that the bug occurred under */
	protected Combo oSCombo;

	/** Enum for value */
	protected final String VALUE = "VALUE";

	/** Enum for property */
	protected final String PROPERTY = "PROPERTY";

	/** Enum for header */
	protected final String HEADER = "HEADER";

	/** The horizontal indentation of the labels */
	protected final int HORZ_INDENT = 0;

	/** Status variable for the possible errors on this page */
	protected IStatus attributeStatus;

	/**
	 * Constructor for AbstractWizardDataPage
	 * 
	 * @param pageName
	 *            the name of the page
	 * @param title
	 *            the title of the page
	 * @param description
	 *            the description text for the page
	 * @param workbench
	 *            the instance of the workbench
	 */
	public AbstractWizardDataPage(String pageName, String title, String description, IWorkbench workbench) {
		super(pageName);
		setTitle(title);
		setDescription(description);
		this.workbench = workbench;

		// set the status for the page
		attributeStatus = new Status(IStatus.OK, "not_used", 0, "", null);
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
	 * Make sure that a String that is <code>null</code> is changed to a null
	 * string
	 * 
	 * @param text
	 *            The text to check if it is null or not
	 * @return The string in its proper format
	 */
	public String checkText(String text) {
		if (text == null)
			return "";
		else
			return text;
	}

	@Override
	public boolean canFlipToNextPage() {
		// no next page for this path through the wizard
		return false;
	}

	/**
	 * Create a new layout for a component
	 * 
	 * @param composite
	 *            The parent composite
	 * @param colSpan
	 *            The number of columns that this can span
	 * @param text
	 *            The text to add to the control
	 * @param style
	 *            The style that the control should have
	 */
	public void newLayout(Composite composite, int colSpan, String text, String style) {
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = colSpan;

		// create the proper layout for the style
		if (style.equalsIgnoreCase(VALUE)) {
			Label l = new Label(composite, SWT.NONE);
			FontData fontData = l.getFont().getFontData()[0];
			fontData.setStyle(SWT.BOLD | fontData.getStyle());
			Font font = new Font(null, fontData);
			l.setFont(font);
			l.setText(checkText(text));

			data.horizontalIndent = HORZ_INDENT;
			l.setLayoutData(data);
		} else if (style.equalsIgnoreCase(PROPERTY)) {
			Label l = new Label(composite, SWT.NONE);
			FontData fontData = l.getFont().getFontData()[0];
			fontData.setStyle(SWT.BOLD | fontData.getStyle());
			Font font = new Font(null, fontData);
			l.setFont(font);
			l.setText(checkText(text));

			data.horizontalIndent = HORZ_INDENT;
			l.setLayoutData(data);
		} else {
			Composite generalTitleGroup = new Composite(composite, SWT.NONE);
			generalTitleGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			generalTitleGroup.setLayoutData(data);
			GridLayout generalTitleLayout = new GridLayout();
			generalTitleLayout.numColumns = 2;
			generalTitleLayout.marginWidth = 0;
			generalTitleLayout.marginHeight = 9;
			generalTitleGroup.setLayout(generalTitleLayout);

			Label image = new Label(generalTitleGroup, SWT.NONE);
			image.setImage(WorkbenchImages.getImage(IDEInternalWorkbenchImages.IMG_OBJS_WELCOME_ITEM));

			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
			image.setLayoutData(gd);
			Label l = new Label(composite, SWT.NONE);
			FontData fontData = l.getFont().getFontData()[0];
			fontData.setStyle(SWT.BOLD | fontData.getStyle());
			Font font = new Font(null, fontData);
			l.setFont(font);
			l.setText(checkText(text));

			data.horizontalIndent = HORZ_INDENT;
			l.setLayoutData(data);
		}
	}

	/**
	 * Determine if the page is complete when the summary is changed
	 * 
	 * @param e
	 *            The event which occurred
	 */
	public void handleEvent(Event e) {
		boolean pageComplete = isPageComplete();

		// Initialize a variable with the no error status
		Status status = new Status(IStatus.OK, "not_used", 0, "", null);

		setPageComplete(pageComplete);

		if (!pageComplete)
			status = new Status(IStatus.ERROR, "not_used", 0, "You must enter a summary and a description", null);

		attributeStatus = status;

		// Show the most serious error
		applyToStatusLine(attributeStatus);

		setPageComplete(pageComplete);
		getWizard().getContainer().updateButtons();
	}

	@Override
	public IWizardPage getNextPage() {
		saveDataToModel();
		return null;
	}

	/**
	 * Sets the completed field on the wizard class when all the needed
	 * information is entered and the wizard can be completed
	 * 
	 * @return true if the wizard can be completed, false otherwise
	 */
	@Override
	public boolean isPageComplete() {
		AbstractBugWizard wizard = (AbstractBugWizard) getWizard();
		if (summaryText.getText() == null || summaryText.getText().equals("") || descriptionText.getText() == null || descriptionText.getText().equals("")) {
			wizard.attributeCompleted = false;
			return false;
		}
		saveDataToModel();
		return true;
	}

	/**
	 * Save the data obtained from this point in the wizard to the model.
	 */
	public void saveDataToModel() {
		// get the model that we are using
		AbstractBugWizard wizard = (AbstractBugWizard) getWizard();
		NewBugModel nbm = wizard.model;

		nbm.setDescription(descriptionText.getText());
		nbm.setSummary(summaryText.getText());

		// go through each of the attributes and sync their values with the
		// combo boxes
		for (Iterator<Attribute> it = nbm.getAttributes().iterator(); it.hasNext();) {
			Attribute attribute = it.next();
			String key = attribute.getName();
			Map<String, String> values = attribute.getOptionValues();

			try {
				if (values == null) values = new HashMap<String, String>();
				if (key.equals(ATTRIBUTE_OS)) {
					String os = oSCombo.getItem(oSCombo.getSelectionIndex());
					attribute.setValue(os);
				} else if (key.equals(ATTRIBUTE_VERSION)) {
					String version = versionCombo.getItem(versionCombo.getSelectionIndex());
					attribute.setValue(version);
				} else if (key.equals(ATTRIBUTE_SEVERITY)) {
					String severity = severityCombo.getItem(severityCombo.getSelectionIndex());
					attribute.setValue(severity);
				} else if (key.equals(ATTRIBUTE_PLATFORM)) {
					String platform = platformCombo.getItem(platformCombo.getSelectionIndex());
					attribute.setValue(platform);
				} else if (key.equals(ATTRIBUTE_COMPONENT)) {
					String component = componentCombo.getItem(componentCombo.getSelectionIndex());
					attribute.setValue(component);
				} else if (key.equals(ATTRIBUTE_PRIORITY)) {
					String priority = priorityCombo.getItem(priorityCombo.getSelectionIndex());
					attribute.setValue(priority);
				} else if (key.equals(ATTRIBUTE_URL)) {
					String url = urlText.getText();
					if (url.equalsIgnoreCase("http://")) url = "";
					attribute.setValue(url);
				} else if (key.equals("Assign To")) {
					String assignTo = assignedToText.getText();
					attribute.setValue(assignTo);
				} else {
					// do nothing
				}
			} catch (IllegalArgumentException e) {
				ErrorLogger.fail(e, "could not set attribute: " + attribute, false);
			}
		}
		wizard.attributeCompleted = true;
	}

	@Override
	protected void setControl(Control c) {
		super.setControl(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		// whether the priority exists or not
		boolean priExist = false;

		String url = null;

		// get the model for the new bug
		AbstractBugWizard wizard = (AbstractBugWizard) getWizard();
		NewBugModel nbm = wizard.model;

		// Set the current platform and OS on the model
		setPlatformOptions(nbm);

		// Attributes Composite- this holds all the combo fields and text
		// fields
		Composite attributesComposite = new Composite(parent, SWT.NONE);
		GridLayout attributesLayout = new GridLayout();
		attributesLayout.numColumns = 4;
		attributesLayout.horizontalSpacing = 14;
		attributesLayout.verticalSpacing = 6;
		attributesComposite.setLayout(attributesLayout);

		GridData attributesData = new GridData(GridData.FILL_BOTH);
		attributesData.horizontalSpan = 1;
		attributesData.grabExcessVerticalSpace = false;
		attributesComposite.setLayoutData(attributesData);
		// End Attributes Composite

		GridLayout attributesTitleLayout = new GridLayout();
		attributesTitleLayout.horizontalSpacing = 0;
		attributesTitleLayout.marginWidth = 0;

		GridData attributesTitleData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		attributesTitleData.horizontalSpan = 4;

		attributesTitleData.grabExcessVerticalSpace = false;

		// Add the product to the composite
		newLayout(attributesComposite, 1, "Product", PROPERTY);
		newLayout(attributesComposite, 1, nbm.getProduct(), VALUE);

		// Populate Attributes
		for (Iterator<Attribute> it = nbm.getAttributes().iterator(); it.hasNext();) {
			Attribute attribute = it.next();
			String key = attribute.getParameterName();
			String name = attribute.getName();
			String value = checkText(attribute.getValue());
			Map<String, String> values = attribute.getOptionValues();

			// if it is a hidden field, don't try to display it
			if (attribute.isHidden())
				continue;

			if (key == null)
				key = "";

			if (values == null)
				values = new HashMap<String, String>();

			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			data.horizontalSpan = 1;
			data.horizontalIndent = HORZ_INDENT;

			// create and populate the combo fields for the attributes
			if (key.equals("op_sys")) {
				newLayout(attributesComposite, 1, name, PROPERTY);
				oSCombo = new Combo(attributesComposite, SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY);

				oSCombo.setLayoutData(data);
				Set<String> s = values.keySet();
				String[] a = s.toArray(new String[s.size()]);
				for (int i = 0; i < a.length; i++) {
					oSCombo.add(a[i]);
				}
				int index;
				if ((index = oSCombo.indexOf(value)) == -1)
					index = 0;
				oSCombo.select(index);
				oSCombo.addListener(SWT.Modify, this);
			} else if (key.equals("version")) {
				newLayout(attributesComposite, 1, name, PROPERTY);

				versionCombo = new Combo(attributesComposite, SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY);

				versionCombo.setLayoutData(data);
				Set<String> s = values.keySet();
				String[] a = s.toArray(new String[s.size()]);
				for (int i = 0; i < a.length; i++) {
					versionCombo.add(a[i]);
				}
				int index;
				if ((index = versionCombo.indexOf(value)) == -1)
					index = 0;
				versionCombo.select(index);
				versionCombo.addListener(SWT.Modify, this);
			} else if (key.equals("bug_severity")) {
				newLayout(attributesComposite, 1, name, PROPERTY);
				severityCombo = new Combo(attributesComposite, SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY);

				severityCombo.setLayoutData(data);
				Set<String> s = values.keySet();
				String[] a = s.toArray(new String[s.size()]);
				for (int i = 0; i < a.length; i++) {
					severityCombo.add(a[i]);
				}
				int index;
				if ((index = severityCombo.indexOf(value)) == -1)
					index = 0;
				severityCombo.select(index);
				severityCombo.addListener(SWT.Modify, this);

			} else if (key.equals("rep_platform")) {
				newLayout(attributesComposite, 1, name, PROPERTY);
				platformCombo = new Combo(attributesComposite, SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY);

				platformCombo.setLayoutData(data);
				Set<String> s = values.keySet();
				String[] a = s.toArray(new String[s.size()]);
				for (int i = 0; i < a.length; i++) {
					platformCombo.add(a[i]);
				}
				int index;
				if ((index = platformCombo.indexOf(value)) == -1)
					index = 0;
				platformCombo.select(index);
				platformCombo.addListener(SWT.Modify, this);
			} else if (key.equals("component")) {
				newLayout(attributesComposite, 1, name, PROPERTY);
				componentCombo = new Combo(attributesComposite, SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY);

				componentCombo.setLayoutData(data);
				Set<String> s = values.keySet();
				String[] a = s.toArray(new String[s.size()]);
				for (int i = 0; i < a.length; i++) {
					componentCombo.add(a[i]);
				}
				int index;
				if ((index = componentCombo.indexOf(value)) == -1)
					index = 0;
				componentCombo.select(index);
				componentCombo.addListener(SWT.Modify, this);
			} else if (key.equals("priority")) {
				newLayout(attributesComposite, 1, name, PROPERTY);
				priorityCombo = new Combo(attributesComposite, SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY);

				priorityCombo.setLayoutData(data);
				Set<String> s = values.keySet();
				String[] a = s.toArray(new String[s.size()]);
				for (int i = 0; i < a.length; i++) {
					priorityCombo.add(a[i]);
				}
				int index;
				if ((index = priorityCombo.indexOf(value)) == -1)
					index = 0;
				priorityCombo.select(index);
				priorityCombo.addListener(SWT.Modify, this);
				priExist = true;
			} else if (key.equals("bug_file_loc")) {
				url = value;
			} else {
				// do nothing if it isn't a standard value to change
			}
		}

		if (priExist) {
			newLayout(attributesComposite, 1, "", PROPERTY);
			newLayout(attributesComposite, 1, "", PROPERTY);
		}

		GridData summaryTextData;

		if (url != null) {
			// add the assigned to text field
			newLayout(attributesComposite, 1, ATTRIBUTE_URL, PROPERTY);
			urlText = new Text(attributesComposite, SWT.BORDER | SWT.SINGLE | SWT.WRAP);
			summaryTextData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);

			summaryTextData.horizontalSpan = 3;
			summaryTextData.widthHint = 200;
			urlText.setLayoutData(summaryTextData);
			urlText.setText(url);
			urlText.addListener(SWT.FocusOut, this);
		}

		newLayout(attributesComposite, 1, "Assigned To", PROPERTY);
		Label l = new Label(attributesComposite, SWT.NONE);
		l.setText("NOTE: If e-mail incorrect, submit will fail silently");
		summaryTextData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		summaryTextData.horizontalSpan = 2;
		l.setLayoutData(summaryTextData);
		assignedToText = new Text(attributesComposite, SWT.BORDER | SWT.SINGLE | SWT.WRAP);
		summaryTextData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);

		summaryTextData.horizontalSpan = 1;
		summaryTextData.widthHint = 200;
		assignedToText.setLayoutData(summaryTextData);
		assignedToText.setText("");

		// add the summary text field
		newLayout(attributesComposite, 1, "Summary", PROPERTY);
		summaryText = new Text(attributesComposite, SWT.BORDER | SWT.SINGLE | SWT.WRAP);
		summaryTextData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);

		summaryTextData.horizontalSpan = 3;
		summaryTextData.widthHint = 200;
		summaryText.setLayoutData(summaryTextData);
		summaryText.setText(nbm.getSummary());
		summaryText.addListener(SWT.Modify, this);

		// Description Text
		Composite descriptionComposite = new Composite(attributesComposite, SWT.NONE);

		descriptionComposite.setLayout(attributesTitleLayout);

		GridData descriptionData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		descriptionData.horizontalSpan = 4;
		descriptionData.grabExcessVerticalSpace = false;
		descriptionComposite.setLayoutData(descriptionData);
		newLayout(descriptionComposite, 4, "Description:", HEADER);

		// add the description text field
		descriptionText = new Text(attributesComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);

		descriptionText.setFont(AbstractBugEditor.COMMENT_FONT);
		GridData descriptionTextData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		descriptionTextData.horizontalSpan = 4;
		descriptionTextData.widthHint = AbstractBugEditor.DESCRIPTION_WIDTH;
		descriptionTextData.heightHint = AbstractBugEditor.DESCRIPTION_HEIGHT;
		descriptionText.setLayoutData(descriptionTextData);
		descriptionText.addListener(SWT.Modify, this);

		serverButton = new Button(attributesComposite, SWT.RADIO);
		serverButton.setText("Submit bug report to the server.");
		GridData toServerButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		toServerButtonData.horizontalSpan = 4;
		serverButton.setLayoutData(toServerButtonData);
		serverButton.setSelection(true);

		offlineButton = new Button(attributesComposite, SWT.RADIO);
		offlineButton.setText("Save bug report offline.");
		GridData offlineButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		offlineButtonData.horizontalSpan = 4;
		offlineButton.setLayoutData(offlineButtonData);
		offlineButton.setSelection(false);

		if (wizard.fromDialog)
			offlineButton.setEnabled(false);

		setControl(attributesComposite);
		return;
	}

	/**
	 * @return <code>true</code> if the radio button to submit the bug to the
	 *         server is selected.
	 */
	public boolean serverSelected() {
		return (serverButton == null) ? false : serverButton.getSelection();
	}

	/**
	 * @return <code>true</code> if the radio button to save the bug offline
	 *         is selected.
	 */
	public boolean offlineSelected() {
		return (offlineButton == null) ? false : offlineButton.getSelection();
	}

	/*
	 * The following are Bugzilla's: OS's All AIX Windows 95 Windows 98 Windows
	 * CE Windows Mobile 2003 Windows Mobile 2005 Windows ME Windows 2000
	 * Windows NT Windows XP Windows 2003 Server Windows All MacOS X Linux
	 * Linux-GTK Linux-Motif HP-UX Neutrino QNX-Photon Solaris Solaris-GTK
	 * Solaris-Motif SymbianOS-Series 80 Unix All other 
	 * 
	 * The following are the platforsm in Bugzilla:
	 *  All Macintosh PC Power PC Sun Other
	 * 
	 * The following are Java's Archictures: [PA_RISC, ppc, sparc, x86,
	 * x86_64, ia64, ia64_32]
	 * 
	 * The following are Java's OS's: [aix, hpux, linux, macosx, qnx,
	 * solaris, win32]
	 */
	/**
	 * Sets the OS and Platform for the new bug
	 * 
	 * @param newBugModel
	 *            The bug to set the options for
	 */
	public void setPlatformOptions(NewBugModel newBugModel) {
		try {
			// A Map from Java's OS and Platform to Buzilla's
			Map<String, String> java2buzillaOSMap = new HashMap<String, String>();
			Map<String, String> java2buzillaPlatformMap = new HashMap<String, String>();

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
			java2buzillaOSMap.put("win32", "Windows All");

			// Get OS
			// Lookup Map
			// Check that the result is in Values, if it is not, set it to other
			Attribute opSysAttribute = newBugModel.getAttribute(ATTRIBUTE_OS);
			Attribute platformAttribute = newBugModel.getAttribute(ATTRIBUTE_PLATFORM);

			String OS = Platform.getOS();
			String platform = Platform.getOSArch();

			String bugzillaOS = null; // Bugzilla String for OS
			String bugzillaPlatform = null; // Bugzilla String for Platform

			if (java2buzillaOSMap.containsKey(OS)) {
				bugzillaOS = java2buzillaOSMap.get(OS);
				if (!opSysAttribute.getOptionValues().values().contains(bugzillaOS)) {
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

			if (java2buzillaPlatformMap.containsKey(platform)) {
				bugzillaPlatform = java2buzillaPlatformMap.get(platform);
				if (!platformAttribute.getOptionValues().values().contains(bugzillaPlatform)) {
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
			if (bugzillaOS != null) opSysAttribute.setValue(bugzillaOS);
			if (bugzillaPlatform != null) platformAttribute.setValue(bugzillaPlatform);
		} catch (Exception e) {
			ErrorLogger.fail(e, "could not set platform options", false);
		}
	}

}
