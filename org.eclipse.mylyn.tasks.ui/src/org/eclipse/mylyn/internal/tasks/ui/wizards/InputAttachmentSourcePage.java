/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids <sdavids@gmx.de> - layout tweaks
 *     Jeff Pound <jeff.bagu@gmail.com> - modified for attachment input
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.resources.mapping.ResourceMappingContext;
import org.eclipse.core.resources.mapping.ResourceTraversal;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.*;

/**
 * A wizard to input the source of the attachment. This is a modified version of
 * org.eclipse.compare.internal.InputPatchPage.
 * 
 * @author Jeff Pound
 */
public class InputAttachmentSourcePage extends WizardPage {

	// constants
	protected static final int SIZING_TEXT_FIELD_WIDTH = 250;

	protected static final int COMBO_HISTORY_LENGTH = 5;

	public static final String CLIPBOARD_LABEL = "<Clipboard>";

	// input constants
	protected final static int CLIPBOARD = 1;

	protected final static int FILE = 2;

	protected final static int WORKSPACE = 3;

	static final char SEPARATOR = System.getProperty("file.separator").charAt(0); //$NON-NLS-1$

	private boolean showError = false;

	private ActivationListener activationListener = new ActivationListener();

	// SWT widgets
	private Button useClipboardButton;

	private Combo fileNameField;

	private Button fileBrowseButton;

	private Button useFileButton;

	private Button useWorkspaceButton;

	private Label workspaceSelectLabel;

	private TreeViewer treeViewer;

	private NewAttachmentWizard wizard;

	private String clipboardContents;

	private boolean initUseClipboard = false;

	class ActivationListener extends ShellAdapter {
		@Override
		public void shellActivated(ShellEvent e) {
			// allow error messages if the selected input actually has something
			// selected in it
			showError = true;
			switch (getInputMethod()) {
			case FILE:
				showError = (fileNameField.getText() != ""); //$NON-NLS-1$
				break;

			case WORKSPACE:
				showError = (!treeViewer.getSelection().isEmpty());
				break;
			}
			updateWidgetEnablements();
		}
	}

	public InputAttachmentSourcePage(NewAttachmentWizard wizard) {
		super("InputAttachmentPage");
		this.wizard = wizard;
		setTitle("Select source");
		setDescription("Clipboard contents are for text attachments only.");
		// setMessage("Please select the source for the attachment");
	}

	/*
	 * Get a path from the supplied text widget. @return
	 * org.eclipse.core.runtime.IPath
	 */
	protected IPath getPathFromText(Text textField) {
		return (new Path(textField.getText())).makeAbsolute();
	}

	public String getAttachmentName() {
		if (getInputMethod() == CLIPBOARD) {
			return CLIPBOARD_LABEL;
		} else if (getInputMethod() == WORKSPACE) {
			return getResources(treeViewer.getSelection())[0].getFullPath().toOSString();
		}
		return getAttachmentFilePath();
	}

	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.heightHint = 800;
		composite.setLayoutData(gd);
		setControl(composite);

		initializeDialogUnits(parent);

		buildAttachmentFileGroup(composite);

		// No error for dialog opening
		showError = false;
		clearErrorMessage();
		updateWidgetEnablements();

		Shell shell = getShell();
		shell.addShellListener(activationListener);

		Dialog.applyDialogFont(composite);

	}

	@Override
	public IWizardPage getNextPage() {
		return wizard.getNextPage(this);
	}

	/*
	 * (non-JavaDoc) Method declared in IWizardPage.
	 */
	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	private void setEnableAttachmentFile(boolean enable) {
		fileNameField.setEnabled(enable);
		fileBrowseButton.setEnabled(enable);
	}

	private void setEnableWorkspaceAttachment(boolean enable) {
		workspaceSelectLabel.setEnabled(enable);
		treeViewer.getTree().setEnabled(enable);
	}

	/*
	 * Create the group for selecting the attachment file
	 */
	private void buildAttachmentFileGroup(Composite parent) {

		final Composite composite = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		composite.setLayout(gridLayout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// 1st row
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 3;
		useClipboardButton = new Button(composite, SWT.RADIO);
		useClipboardButton.setText("Clipboard");
		useClipboardButton.setLayoutData(gd);
		useClipboardButton.setSelection(initUseClipboard);

		// 2nd row
		useFileButton = new Button(composite, SWT.RADIO);
		useFileButton.setText("File");

		fileNameField = new Combo(composite, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = SIZING_TEXT_FIELD_WIDTH;
		fileNameField.setLayoutData(gd);
		fileNameField.setText(wizard.getAttachment().getFilePath());

		fileBrowseButton = new Button(composite, SWT.PUSH);
		fileBrowseButton.setText("Browse...");
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		Point minSize = fileBrowseButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);
		fileBrowseButton.setLayoutData(data);

		// 3rd row
		useWorkspaceButton = new Button(composite, SWT.RADIO);
		useWorkspaceButton.setText("Workspace");
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		useWorkspaceButton.setLayoutData(gd);

		addWorkspaceControls(parent);

		// Add listeners
		useClipboardButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!useClipboardButton.getSelection())
					return;

				clearErrorMessage();
				showError = true;
				int state = getInputMethod();
				setEnableAttachmentFile(state == FILE);
				setEnableWorkspaceAttachment(state == WORKSPACE);
				storeClipboardContents();
				updateWidgetEnablements();
			}
		});

		useFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!useFileButton.getSelection())
					return;
				// If there is anything typed in at all
				clearErrorMessage();
				showError = (fileNameField.getText() != ""); //$NON-NLS-1$
				int state = getInputMethod();
				setEnableAttachmentFile(state == FILE);
				setEnableWorkspaceAttachment(state == WORKSPACE);
				updateWidgetEnablements();
			}
		});
		fileNameField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setSourceName(fileNameField.getText());
				updateWidgetEnablements();
			}
		});
		fileNameField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				clearErrorMessage();
				showError = true;
				updateWidgetEnablements();
			}
		});
		fileBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearErrorMessage();
				showError = true;
				/* Launch Browser */
				FileDialog fileChooser = new FileDialog(composite.getShell(), SWT.OPEN);
				String file = fileChooser.open();

				// Check if the dialog was canceled or an error occured
				if (file == null) {
					return;
				}
				// update UI
				fileNameField.setText(file);
				updateWidgetEnablements();
			}
		});
		useWorkspaceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!useWorkspaceButton.getSelection())
					return;
				clearErrorMessage();
				// If there is anything typed in at all
				showError = (!treeViewer.getSelection().isEmpty());
				int state = getInputMethod();
				setEnableAttachmentFile(state == FILE);
				setEnableWorkspaceAttachment(state == WORKSPACE);
				updateWidgetEnablements();
			}
		});

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				clearErrorMessage();
				updateWidgetEnablements();
			}
		});

		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof TreeSelection) {
					TreeSelection treeSel = (TreeSelection) selection;
					Object res = treeSel.getFirstElement();
					if (res != null) {
						if (res instanceof IProject || res instanceof IFolder) {
							if (treeViewer.getExpandedState(res)) {
								treeViewer.collapseToLevel(res, 1);
							} else {
								treeViewer.expandToLevel(res, 1);
							}
						} else if (res instanceof IFile) {
							wizard.showPage(getNextPage());
						}
					}
				}
			}
		});

		useFileButton.setSelection(!initUseClipboard);
		setEnableWorkspaceAttachment(false);
	}

	@SuppressWarnings("deprecation")
	private void addWorkspaceControls(Composite composite) {

		Composite newComp = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginLeft = 16; // align w/ lable of check button
		newComp.setLayout(layout);
		newComp.setLayoutData(new GridData(GridData.FILL_BOTH));

		workspaceSelectLabel = new Label(newComp, SWT.LEFT);
		workspaceSelectLabel.setText("Select the location of the attachment");

		treeViewer = new TreeViewer(newComp, SWT.BORDER);
		treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		treeViewer.setLabelProvider(new WorkbenchLabelProvider());
		treeViewer.setContentProvider(new WorkbenchContentProvider());
		treeViewer.setSorter(new ResourceSorter(ResourceSorter.NAME));
		treeViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
	}

	/**
	 * Updates the enable state of this page's controls.
	 */
	private void updateWidgetEnablements() {

		String error = null;

		boolean attachmentFound = false;
		int inputMethod = getInputMethod();
		if (inputMethod == CLIPBOARD) {
			Control c = getControl();
			if (c != null) {
				Clipboard clipboard = new Clipboard(c.getDisplay());
				Object o = clipboard.getContents(TextTransfer.getInstance());
				clipboard.dispose();
				if (o instanceof String) {
					String s = ((String) o).trim();
					if (s.length() > 0)
						attachmentFound = true;
					else
						error = "Clipboard is empty";
				} else
					error = "Clipboard does not contain text";
			} else
				error = "Cannot retrieve clipboard contents";
		} else if (inputMethod == FILE) {
			String path = fileNameField.getText();
			if (path != null && path.length() > 0) {
				File file = new File(path);
				attachmentFound = file.exists() && file.isFile() && file.length() > 0;
				if (!attachmentFound)
					error = "Cannot locate attachment file";
			} else {
				error = "No file name";
			}
		} else if (inputMethod == WORKSPACE) {
			// Get the selected attachment file (tree will only allow for one
			// selection)
			IResource[] resources = getResources(treeViewer.getSelection());
			if (resources == null || resources.length <= 0) {
				error = "No file name";
			} else {
				IResource attachmentFile = resources[0];
				if (attachmentFile != null && attachmentFile.getType() == IResource.FILE) {
					File actualFile = attachmentFile.getRawLocation().toFile();
					attachmentFound = actualFile.exists() && actualFile.isFile() && actualFile.length() > 0;
					if (!attachmentFound) {
						error = "Cannot locate attachment file";
					}
				}
			}
		}

		setPageComplete(attachmentFound);
		wizard.getAttachment().setFilePath(getAbsoluteAttachmentPath());

		if (showError) {
			setErrorMessage(error);
		}
	}

	/**
	 * Sets the source name of the import to be the supplied path. Adds the name of the path to the list of items in the
	 * source combo and selects it.
	 * 
	 * @param path
	 *            the path to be added
	 */
	protected void setSourceName(String path) {

		if (path.length() > 0) {

			String[] currentItems = fileNameField.getItems();
			int selectionIndex = -1;
			for (int i = 0; i < currentItems.length; i++)
				if (currentItems[i].equals(path))
					selectionIndex = i;

			if (selectionIndex < 0) { // not found in history
				int oldLength = currentItems.length;
				String[] newItems = new String[oldLength + 1];
				System.arraycopy(currentItems, 0, newItems, 0, oldLength);
				newItems[oldLength] = path;
				fileNameField.setItems(newItems);
				selectionIndex = oldLength;
			}
			fileNameField.select(selectionIndex);

			// resetSelection();
		}
	}

	// private String getWorkspacePath() {
	// if (fTreeViewer ! = null){
	// IResource[] resources = getResources(fTreeViewer.getSelection());
	// if (resources.length > 0) {
	// IResource patchFile = resources[0];
	// return patchFile.getFullPath().toString();
	// }
	//			
	// }
	// return ""; //$NON-NLS-1$
	// }

	/*
	 * Clears the dialog message box
	 */
	private void clearErrorMessage() {
		setErrorMessage(null);
	}

	protected int getInputMethod() {
		if (useClipboardButton == null) {
			if (initUseClipboard) {
				return CLIPBOARD;
			}
			return FILE;
		}
		if (useClipboardButton.getSelection()) {
			return CLIPBOARD;
		}
		if (useFileButton.getSelection()) {
			return FILE;
		}
		return WORKSPACE;
	}

	private String getAttachmentFilePath() {
		if (fileNameField != null) {
			return fileNameField.getText();
		}
		return wizard.getAttachment().getFilePath();
	}

	public String getAbsoluteAttachmentPath() {
		switch (getInputMethod()) {
		case CLIPBOARD:
			return CLIPBOARD_LABEL;
		case WORKSPACE:
			IResource[] resources = getResources(treeViewer.getSelection());
			if (resources.length > 0 && resources[0].getRawLocation() != null) {
				return resources[0].getRawLocation().toOSString();
			} else {
				return null;
			}
		case FILE:
		default:
			return getAttachmentFilePath();
		}
	}

	/*
	 * Based on .eclipse.compare.internal.Utilities
	 * 
	 * Convenience method: extract all accessible <code>IResources</code> from
	 * given selection. Never returns null.
	 */
	public static IResource[] getResources(ISelection selection) {
		ArrayList<IResource> tmp = new ArrayList<IResource>();
		Class<?> type = IResource.class;
		if (selection instanceof IStructuredSelection) {
			Object[] s = ((IStructuredSelection) selection).toArray();

			for (int i = 0; i < s.length; i++) {
				IResource resource = null;
				Object o = s[i];
				if (type.isInstance(o)) {
					resource = (IResource) o;

				} else if (o instanceof ResourceMapping) {
					try {
						ResourceTraversal[] travs = ((ResourceMapping) o).getTraversals(
								ResourceMappingContext.LOCAL_CONTEXT, null);
						if (travs != null) {
							for (int k = 0; k < travs.length; k++) {
								IResource[] resources = travs[k].getResources();
								for (int j = 0; j < resources.length; j++) {
									if (type.isInstance(resources[j]) && resources[j].isAccessible())
										tmp.add(resources[j]);
								}
							}
						}
					} catch (CoreException ex) {
						// TODO handle error
					}
				} else if (o instanceof IAdaptable) {
					IAdaptable a = (IAdaptable) o;
					Object adapter = a.getAdapter(IResource.class);
					if (type.isInstance(adapter))
						resource = (IResource) adapter;
				}

				if (resource != null && resource.isAccessible())
					tmp.add(resource);
			}
		}

		return tmp.toArray(new IResource[tmp.size()]);
	}

	private void storeClipboardContents() {
		Control c = getControl();
		if (c != null) {
			Clipboard clipboard = new Clipboard(c.getDisplay());
			Object o = clipboard.getContents(TextTransfer.getInstance());
			clipboard.dispose();
			if (o instanceof String) {
				clipboardContents = ((String) o).trim();
			}
		}
	}

	public String getClipboardContents() {
		return clipboardContents;
	}

	public void setClipboardContents(String attachContents) {
		clipboardContents = attachContents;
	}

	public void setUseClipboard(boolean b) {
		if (useClipboardButton != null) {
			useClipboardButton.setSelection(b);
		}
		initUseClipboard = b;
	}
}
