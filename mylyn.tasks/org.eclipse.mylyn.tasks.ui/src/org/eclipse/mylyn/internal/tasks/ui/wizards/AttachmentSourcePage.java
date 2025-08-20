/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids <sdavids@gmx.de> - layout tweaks
 *     Jeff Pound - modified for attachment input
 *     Chris Aniszczyk <caniszczyk@gmail.com> - bug 20957
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskAttachmentWizard.ClipboardTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

/**
 * A wizard to input the source of the attachment.
 * <p>
 * Based on org.eclipse.compare.internal.InputPatchPage.
 */
public class AttachmentSourcePage extends WizardPage {

	// constants
	protected static final int SIZING_TEXT_FIELD_WIDTH = 250;

	protected static final int COMBO_HISTORY_LENGTH = 5;

	// input constants
	protected final static int CLIPBOARD = 1;

	protected final static int FILE = 2;

	protected final static int WORKSPACE = 3;

	protected final static int SCREENSHOT = 4;

	static final char SEPARATOR = FileSystems.getDefault().getSeparator().charAt(0);

	private boolean showError = false;

	// SWT widgets
	private Button useClipboardButton;

//	private Button useScreenshotButton;

	private Combo fileNameField;

	private Button fileBrowseButton;

	private Button useFileButton;

	private Button useWorkspaceButton;

	private Label workspaceSelectLabel;

	private TreeViewer treeViewer;

	private String clipboardContents;

	private boolean initUseClipboard = false;

	private final String DIALOG_SETTINGS = "InputAttachmentSourcePage"; //$NON-NLS-1$

	private final String S_LAST_SELECTION = "lastSelection"; //$NON-NLS-1$

	private final String S_FILE_HISTORY = "fileHistory"; //$NON-NLS-1$

	private final String S_LAST_FILE = "lastFile"; //$NON-NLS-1$

	private final TaskAttachmentModel model;

	/**
	 * The last filename that was selected through the browse button.
	 */
	private String lastFilename;

	public AttachmentSourcePage(TaskAttachmentModel model) {
		super("InputAttachmentPage"); //$NON-NLS-1$
		this.model = model;
		setTitle(Messages.AttachmentSourcePage_Select_attachment_source);
		setDescription(Messages.AttachmentSourcePage_Clipboard_supports_text_and_image_attachments_only);
		// setMessage("Please select the source for the attachment");
	}

	private void restoreDialogSettings() {
		IDialogSettings settings = getDialogSettings();
		if (settings == null) {
			updateWidgetEnablements();
			return;
		}

		String selection = settings.get(S_LAST_SELECTION);
		if (selection != null) {
			setInputMethod(Integer.parseInt(selection));
		} else {
			updateWidgetEnablements();
		}

		String[] fileNames = settings.getArray(S_FILE_HISTORY);
		if (fileNames != null) {
			// destination
			for (String fileName : fileNames) {
				fileNameField.add(fileName);
			}
		}
		lastFilename = settings.get(S_LAST_FILE);
	}

	/*
	 * Get a path from the supplied text widget. @return
	 * org.eclipse.core.runtime.IPath
	 */
	protected IPath getPathFromText(Text textField) {
		return new Path(textField.getText()).makeAbsolute();
	}

	public String getAttachmentName() {
		if (getInputMethod() == CLIPBOARD) {
			return Messages.AttachmentSourcePage__Clipboard_;
		} else if (getInputMethod() == WORKSPACE) {
			return getResources(treeViewer.getSelection())[0].getFullPath().toOSString();
		}
		return getAttachmentFilePath();
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.heightHint = 800;
		composite.setLayoutData(gd);
		setControl(composite);

		initializeDialogUnits(parent);

		createAttachmentFileGroup(composite);

		// No error for dialog opening
		showError = false;
		clearErrorMessage();
		restoreDialogSettings();

		Dialog.applyDialogFont(composite);
	}

	@Override
	public IWizardPage getNextPage() {
		AbstractTaskAttachmentSource source = getSource();
		model.setSource(source);
		if (source != null) {
			model.setContentType(source.getContentType());
		}
		saveDialogSettings();
		return super.getNextPage();
	}

	private void saveDialogSettings() {
		IDialogSettings settings = getDialogSettings();
		settings.put(S_LAST_SELECTION, getInputMethod());

		String[] fileNames = settings.getArray(S_FILE_HISTORY);
		String newFileName = fileNameField.getText().trim();
		if (getInputMethod() == FILE && newFileName.length() > 0) {
			List<String> history = new ArrayList<>(10);
			history.add(newFileName);
			if (fileNames != null) {
				for (int i = 0; i < fileNames.length && history.size() < COMBO_HISTORY_LENGTH; i++) {
					if (!newFileName.equals(fileNames[i])) {
						history.add(fileNames[i]);
					}
				}
			}
			settings.put(S_FILE_HISTORY, history.toArray(new String[0]));
		}

		settings.put(S_LAST_FILE, lastFilename);
	}

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
	private void createAttachmentFileGroup(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		composite.setLayout(gridLayout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// new row
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		useFileButton = new Button(composite, SWT.RADIO);
		useFileButton.setText(Messages.AttachmentSourcePage_File);

		fileNameField = new Combo(composite, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = SIZING_TEXT_FIELD_WIDTH;
		fileNameField.setLayoutData(gd);
		fileNameField.setText(""); //$NON-NLS-1$

		fileBrowseButton = new Button(composite, SWT.PUSH);
		fileBrowseButton.setText(Messages.AttachmentSourcePage_Browse_);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		Point minSize = fileBrowseButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);
		fileBrowseButton.setLayoutData(data);

		// new row
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 3;
		useClipboardButton = new Button(composite, SWT.RADIO);
		useClipboardButton.setText(Messages.AttachmentSourcePage_Clipboard);
		useClipboardButton.setLayoutData(gd);
		useClipboardButton.setSelection(initUseClipboard);

		// new row
		useWorkspaceButton = new Button(composite, SWT.RADIO);
		useWorkspaceButton.setText(Messages.AttachmentSourcePage_Workspace);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		useWorkspaceButton.setLayoutData(gd);

		addWorkspaceControls(parent);

		// Add listeners
		useClipboardButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!useClipboardButton.getSelection()) {
					return;
				}

				clearErrorMessage();
				showError = true;
				storeClipboardContents();
				updateWidgetEnablements();
			}
		});

		useFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!useFileButton.getSelection()) {
					return;
				}
				// If there is anything typed in at all
				clearErrorMessage();
				showError = fileNameField.getText() != ""; //$NON-NLS-1$
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
		fileNameField.addModifyListener(e -> {
			clearErrorMessage();
			showError = true;
			updateWidgetEnablements();
		});
		fileBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearErrorMessage();
				showError = true;

				FileDialog fileChooser = new FileDialog(composite.getShell(), SWT.OPEN);
				fileChooser.setText(Messages.AttachmentSourcePage_Select_File_Dialog_Title);
				if (fileNameField.getText().trim().length() > 0) {
					lastFilename = fileNameField.getText().trim();
				}
				fileChooser.setFileName(lastFilename);
				String file = fileChooser.open();
				if (file == null) {
					return;
				}

				// remember the last selected directory
				lastFilename = file;
				fileNameField.setText(file);
				updateWidgetEnablements();
			}
		});
		useWorkspaceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!useWorkspaceButton.getSelection()) {
					return;
				}
				clearErrorMessage();
				// If there is anything typed in at all
				showError = !treeViewer.getSelection().isEmpty();
				updateWidgetEnablements();
			}
		});

		treeViewer.addSelectionChangedListener(event -> {
			clearErrorMessage();
			updateWidgetEnablements();
		});

		treeViewer.addDoubleClickListener(event -> {
			ISelection selection = event.getSelection();
			if (selection instanceof TreeSelection treeSel) {
				Object res = treeSel.getFirstElement();
				if (res != null) {
					if (res instanceof IProject || res instanceof IFolder) {
						if (treeViewer.getExpandedState(res)) {
							treeViewer.collapseToLevel(res, 1);
						} else {
							treeViewer.expandToLevel(res, 1);
						}
					} else if (res instanceof IFile) {
						getContainer().showPage(getNextPage());
					}
				}
			}
		});

		useFileButton.setSelection(!initUseClipboard);
		setEnableWorkspaceAttachment(false);
	}

	private void addWorkspaceControls(Composite composite) {

		Composite newComp = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginLeft = 16; // align w/ lable of check button
		newComp.setLayout(layout);
		newComp.setLayoutData(new GridData(GridData.FILL_BOTH));

		workspaceSelectLabel = new Label(newComp, SWT.LEFT);
		workspaceSelectLabel.setText(Messages.AttachmentSourcePage_Select_the_location_of_the_attachment);

		treeViewer = new TreeViewer(newComp, SWT.BORDER);
		treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		treeViewer.setLabelProvider(new WorkbenchLabelProvider());
		treeViewer.setContentProvider(new WorkbenchContentProvider());
		treeViewer.setComparator(new ResourceComparator(ResourceComparator.NAME));
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
			if (ClipboardTaskAttachmentSource.isSupportedType(getControl().getDisplay())) {
				attachmentFound = true;
			} else {
				error = Messages.AttachmentSourcePage_Clipboard_contains_an_unsupported_data;
			}
		} else if (inputMethod == SCREENSHOT) {
			attachmentFound = true;
		} else if (inputMethod == FILE) {
			String path = fileNameField.getText();
			if (path != null && path.length() > 0) {
				File file = new File(path);
				attachmentFound = file.exists() && file.isFile() && file.length() > 0;
				if (!attachmentFound) {
					error = Messages.AttachmentSourcePage_Cannot_locate_attachment_file;
				}
			} else {
				error = Messages.AttachmentSourcePage_No_file_name;
			}
		} else if (inputMethod == WORKSPACE) {
			// Get the selected attachment file (tree will only allow for one
			// selection)
			IResource[] resources = getResources(treeViewer.getSelection());
			if (resources == null || resources.length <= 0) {
				error = Messages.AttachmentSourcePage_No_file_name;
			} else {
				IResource attachmentFile = resources[0];
				if (attachmentFile != null && attachmentFile.getType() == IResource.FILE) {
					File actualFile = attachmentFile.getRawLocation().toFile();
					attachmentFound = actualFile.exists() && actualFile.isFile() && actualFile.length() > 0;
					if (!attachmentFound) {
						error = Messages.AttachmentSourcePage_Cannot_locate_attachment_file;
					}
				}
			}
		}

		setPageComplete(attachmentFound);

		if (showError) {
			setErrorMessage(error);
		}

		setEnableAttachmentFile(inputMethod == FILE);
		setEnableWorkspaceAttachment(inputMethod == WORKSPACE);
	}

	/**
	 * Sets the source name of the import to be the supplied path. Adds the name of the path to the list of items in the source combo and
	 * selects it.
	 *
	 * @param path
	 *            the path to be added
	 */
	protected void setSourceName(String path) {

		if (path.length() > 0) {

			String[] currentItems = fileNameField.getItems();
			int selectionIndex = -1;
			for (int i = 0; i < currentItems.length; i++) {
				if (currentItems[i].equals(path)) {
					selectionIndex = i;
				}
			}

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

	protected void setInputMethod(int input) {
		switch (input) {
			case WORKSPACE:
				useWorkspaceButton.setSelection(true);
				useClipboardButton.setSelection(false);
				useFileButton.setSelection(false);
				break;
			case CLIPBOARD:
				storeClipboardContents();

				useClipboardButton.setSelection(true);
				useFileButton.setSelection(false);
				useWorkspaceButton.setSelection(false);
				break;
			default:
				useFileButton.setSelection(true);
				useWorkspaceButton.setSelection(false);
				useClipboardButton.setSelection(false);
				break;
		}
		updateWidgetEnablements();
	}

	private String getAttachmentFilePath() {
		if (fileNameField != null) {
			return fileNameField.getText();
		}
		return null;
	}

	public String getAbsoluteAttachmentPath() {
		switch (getInputMethod()) {
			case CLIPBOARD:
				return Messages.AttachmentSourcePage__Clipboard_;
			case SCREENSHOT:
				return Messages.AttachmentSourcePage__Screenshot_;
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
		ArrayList<IResource> tmp = new ArrayList<>();
		Class<?> type = IResource.class;
		if (selection instanceof IStructuredSelection) {
			Object[] s = ((IStructuredSelection) selection).toArray();

			for (Object o : s) {
				IResource resource = null;
				if (type.isInstance(o)) {
					resource = (IResource) o;

				} else if (o instanceof ResourceMapping) {
					try {
						ResourceTraversal[] travs = ((ResourceMapping) o)
								.getTraversals(ResourceMappingContext.LOCAL_CONTEXT, null);
						if (travs != null) {
							for (ResourceTraversal trav : travs) {
								IResource[] resources = trav.getResources();
								for (IResource resource2 : resources) {
									if (type.isInstance(resource2) && resource2.isAccessible()) {
										tmp.add(resource2);
									}
								}
							}
						}
					} catch (CoreException ex) {
						// TODO handle error
					}
				} else if (o instanceof IAdaptable a) {
					Object adapter = a.getAdapter(IResource.class);
					if (type.isInstance(adapter)) {
						resource = (IResource) adapter;
					}
				}

				if (resource != null && resource.isAccessible()) {
					tmp.add(resource);
				}
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

	@Override
	protected IDialogSettings getDialogSettings() {
		TasksUiPlugin plugin = TasksUiPlugin.getDefault();
		IDialogSettings settings = plugin.getDialogSettings();
		IDialogSettings section = settings.getSection(DIALOG_SETTINGS);
		if (section == null) {
			section = settings.addNewSection(DIALOG_SETTINGS);
		}
		return section;
	}

	public AbstractTaskAttachmentSource getSource() {
		switch (getInputMethod()) {
			case CLIPBOARD:
				return new TaskAttachmentWizard.ClipboardTaskAttachmentSource();
			case WORKSPACE:
				IResource[] resources = getResources(treeViewer.getSelection());
				if (resources.length > 0) {
					return new FileTaskAttachmentSource(resources[0].getLocation().toFile());
				} else {
					return null;
				}
			default: // FILE
				return new FileTaskAttachmentSource(new File(getAttachmentFilePath()));
		}
	}

}
