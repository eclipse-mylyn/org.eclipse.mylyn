/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.core.data.TextTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.ui.actions.AttachAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.AttachScreenshotAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskAttachmentWizard.Mode;
import org.eclipse.mylyn.tasks.core.ITaskAttachment2;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Jeff Pound (Attachment work)
 * @author Steffen Pingel
 */
public class TaskEditorAttachmentPart extends AbstractTaskEditorPart {

//	private static final String ATTACHMENT_DEFAULT_NAME = "attachment";
//
//	private static final String CTYPE_ZIP = "zip";
//
//	private static final String CTYPE_OCTET_STREAM = "octet-stream";
//
//	private static final String CTYPE_TEXT = "text";
//
//	private static final String CTYPE_HTML = "html";
//
//	private static final String LABEL_TEXT_EDITOR = "Text Editor";
//
//	private static final String LABEL_COPY_URL_TO_CLIPBOARD = "Copy &URL";
//
//	private static final String LABEL_COPY_TO_CLIPBOARD = "Copy Contents";
//
//	private static final String LABEL_SAVE = "Save...";
//
//	private static final String LABEL_BROWSER = "Browser";
//
//	private static final String LABEL_DEFAULT_EDITOR = "Default Editor";

	private static final String ID_POPUP_MENU = "org.eclipse.mylyn.tasks.ui.editor.menu.attachments";

	private final String[] attachmentsColumns = { "Name", "Description", "Type", "Size", "Creator", "Created" };

	private final int[] attachmentsColumnWidths = { 140, 160, 100, 70, 100, 100 };

//	private Table attachmentsTable;
//
//	private TableViewer attachmentsTableViewer;

	private TaskAttribute[] attachments;

	private boolean hasIncoming;

	private MenuManager menuManager;

	private TaskAttachmentDropListener dropListener;

	public TaskEditorAttachmentPart() {
		setPartName("Attachments");
	}

	private void createAttachmentTable(FormToolkit toolkit, final Composite attachmentsComposite) {
		Table attachmentsTable = toolkit.createTable(attachmentsComposite, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		attachmentsTable.setLinesVisible(true);
		attachmentsTable.setHeaderVisible(true);
		attachmentsTable.setLayout(new GridLayout());
		GridData tableGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		attachmentsTable.setLayoutData(tableGridData);

		for (int i = 0; i < attachmentsColumns.length; i++) {
			TableColumn column = new TableColumn(attachmentsTable, SWT.LEFT, i);
			column.setText(attachmentsColumns[i]);
			column.setWidth(attachmentsColumnWidths[i]);
		}
		attachmentsTable.getColumn(3).setAlignment(SWT.RIGHT);

		TableViewer attachmentsViewer = new TableViewer(attachmentsTable);
		attachmentsViewer.setUseHashlookup(true);
		attachmentsViewer.setColumnProperties(attachmentsColumns);
		ColumnViewerToolTipSupport.enableFor(attachmentsViewer, ToolTip.NO_RECREATE);

		attachmentsViewer.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				ITaskAttachment2 attachment1 = (ITaskAttachment2) e1;
				ITaskAttachment2 attachment2 = (ITaskAttachment2) e2;
				Date created1 = attachment1.getCreationDate();
				Date created2 = attachment2.getCreationDate();
				if (created1 != null && created2 != null) {
					return created1.compareTo(created2);
				} else if (created1 == null && created2 != null) {
					return -1;
				} else if (created1 != null && created2 == null) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		List<ITaskAttachment2> attachmentList = new ArrayList<ITaskAttachment2>(attachments.length);
		for (TaskAttribute attribute : attachments) {
			TaskAttachment taskAttachment = new TaskAttachment(getModel().getTaskRepository(), getModel().getTask(),
					attribute);
			getTaskData().getAttributeMapper().updateTaskAttachment(taskAttachment, attribute);
			attachmentList.add(taskAttachment);
		}
		attachmentsViewer.setContentProvider(new AttachmentsTableContentProvider2(attachmentList));
		attachmentsViewer.setLabelProvider(new AttachmentTableLabelProvider2(
				getTaskEditorPage().getAttributeEditorToolkit()));
		attachmentsViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				if (!event.getSelection().isEmpty()) {
					StructuredSelection selection = (StructuredSelection) event.getSelection();
					ITaskAttachment2 attachment = (ITaskAttachment2) selection.getFirstElement();
					TasksUiUtil.openUrl(attachment.getUrl());
				}
			}
		});
		attachmentsViewer.addSelectionChangedListener(getTaskEditorPage());
		attachmentsViewer.setInput(getTaskData());

		menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		getTaskEditorPage().getEditorSite().registerContextMenu(ID_POPUP_MENU, menuManager, attachmentsViewer, false);
		Menu menu = menuManager.createContextMenu(attachmentsTable);
		attachmentsTable.setMenu(menu);
	}

//	private void createAttachmentTableMenu() {
	// FIXME EDITOR
//		final Action openWithBrowserAction = new Action(LABEL_BROWSER) {
//			@Override
//			public void run() {
//				TaskAttachment attachment = (TaskAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
//				if (attachment != null) {
//					TasksUiUtil.openUrl(attachment.getUrl());
//				}
//			}
//		};
//
//		final Action openWithDefaultAction = new Action(LABEL_DEFAULT_EDITOR) {
//			@Override
//			public void run() {
//				// browser shortcut
//				TaskAttachment attachment = (TaskAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
//				if (attachment == null) {
//					return;
//				}
//
//				if (attachment.getContentType().endsWith(CTYPE_HTML)) {
//					TasksUiUtil.openUrl(attachment.getUrl());
//					return;
//				}
//
//				IStorageEditorInput input = new RepositoryAttachmentEditorInput(getTaskRepository(), attachment);
//				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//				if (page == null) {
//					return;
//				}
//				IEditorDescriptor desc = PlatformUI.getWorkbench()
//						.getEditorRegistry()
//						.getDefaultEditor(input.getName());
//				try {
//					page.openEditor(input, desc.getId());
//				} catch (PartInitException e) {
//					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unable to open editor for: "
//							+ attachment.getDescription(), e));
//				}
//			}
//		};
//
//		final Action openWithTextEditorAction = new Action(LABEL_TEXT_EDITOR) {
//			@Override
//			public void run() {
//				TaskAttachment attachment = (TaskAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
//				IStorageEditorInput input = new RepositoryAttachmentEditorInput(getTaskRepository(), attachment);
//				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//				if (page == null) {
//					return;
//				}
//
//				try {
//					page.openEditor(input, "org.eclipse.ui.DefaultTextEditor");
//				} catch (PartInitException e) {
//					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unable to open editor for: "
//							+ attachment.getDescription(), e));
//				}
//			}
//		};
//
//		final Action saveAction = new Action(LABEL_SAVE) {
//			@Override
//			public void run() {
//				TaskAttachment attachment = (TaskAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
//				/* Launch Browser */
//				FileDialog fileChooser = new FileDialog(attachmentsTable.getShell(), SWT.SAVE);
//				String fname = attachment.getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_FILENAME);
//				// Default name if none is found
//				if (fname.equals("")) {
//					String ctype = attachment.getContentType();
//					if (ctype.endsWith(CTYPE_HTML)) {
//						fname = ATTACHMENT_DEFAULT_NAME + ".html";
//					} else if (ctype.startsWith(CTYPE_TEXT)) {
//						fname = ATTACHMENT_DEFAULT_NAME + ".txt";
//					} else if (ctype.endsWith(CTYPE_OCTET_STREAM)) {
//						fname = ATTACHMENT_DEFAULT_NAME;
//					} else if (ctype.endsWith(CTYPE_ZIP)) {
//						fname = ATTACHMENT_DEFAULT_NAME + "." + CTYPE_ZIP;
//					} else {
//						fname = ATTACHMENT_DEFAULT_NAME + "." + ctype.substring(ctype.indexOf("/") + 1);
//					}
//				}
//				fileChooser.setFileName(fname);
//				String filePath = fileChooser.open();
//				// Check if the dialog was canceled or an error occurred
//				if (filePath == null) {
//					return;
//				}
//
//				DownloadAttachmentJob job = new DownloadAttachmentJob(attachment, new File(filePath));
//				job.setUser(true);
//				job.schedule();
//			}
//		};
//
//		final Action copyURLToClipAction = new Action(LABEL_COPY_URL_TO_CLIPBOARD) {
//			@Override
//			public void run() {
//				TaskAttachment attachment = (TaskAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
//				Clipboard clip = new Clipboard(PlatformUI.getWorkbench().getDisplay());
//				clip.setContents(new Object[] { attachment.getUrl() }, new Transfer[] { TextTransfer.getInstance() });
//				clip.dispose();
//			}
//		};
//
//		final Action copyToClipAction = new Action(LABEL_COPY_TO_CLIPBOARD) {
//			@Override
//			public void run() {
//				TaskAttachment attachment = (TaskAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
//				CopyAttachmentToClipboardJob job = new CopyAttachmentToClipboardJob(attachment);
//				job.setUser(true);
//				job.schedule();
//			}
//		};
//
//		final MenuManager popupMenu = new MenuManager();
//		final Menu menu = popupMenu.createContextMenu(attachmentsTable);
//		attachmentsTable.setMenu(menu);
//		final MenuManager openMenu = new MenuManager("Open With");
//		popupMenu.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				popupMenu.removeAll();
//
//				ISelection selection = attachmentsTableViewer.getSelection();
//				if (selection.isEmpty()) {
//					return;
//				}
//
//				TaskAttachment att = (TaskAttachment) ((StructuredSelection) selection).getFirstElement();
//
//				// reinitialize menu
//				popupMenu.add(openMenu);
//				openMenu.removeAll();
//				IStorageEditorInput input = new RepositoryAttachmentEditorInput(getTaskRepository(), att);
//				IEditorDescriptor desc = PlatformUI.getWorkbench()
//						.getEditorRegistry()
//						.getDefaultEditor(input.getName());
//				if (desc != null) {
//					openMenu.add(openWithDefaultAction);
//				}
//				openMenu.add(openWithBrowserAction);
//				openMenu.add(openWithTextEditorAction);
//
//				popupMenu.add(new Separator());
//				popupMenu.add(saveAction);
//
//				popupMenu.add(copyURLToClipAction);
//				if (att.getContentType().startsWith(CTYPE_TEXT) || att.getContentType().endsWith("xml")) {
//					popupMenu.add(copyToClipAction);
//				}
//				popupMenu.add(new Separator("actions"));
//
//				// TODO: use workbench mechanism for this?
//				ObjectActionContributorManager.getManager().contributeObjectActions(getTaskEditorPage(), popupMenu,
//						attachmentsTableViewer);
//			}
//		});
//	}

	private void createButtons(Composite attachmentsComposite, FormToolkit toolkit) {
		final Composite attachmentControlsComposite = toolkit.createComposite(attachmentsComposite);
		attachmentControlsComposite.setLayout(new GridLayout(2, false));
		attachmentControlsComposite.setLayoutData(new GridData(GridData.BEGINNING));

		Button attachFileButton = toolkit.createButton(attachmentControlsComposite, AttachAction.LABEL, SWT.PUSH);
		attachFileButton.setImage(WorkbenchImages.getImage(ISharedImages.IMG_OBJ_FILE));
		attachFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openNewAttachmentWizard(Mode.DEFAULT, null);
			}
		});
		registerDropListener(attachFileButton, dropListener);

		Button attachScreenshotButton = toolkit.createButton(attachmentControlsComposite, AttachScreenshotAction.LABEL,
				SWT.PUSH);
		attachScreenshotButton.setImage(CommonImages.getImage(CommonImages.IMAGE_CAPTURE));
		attachScreenshotButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openNewAttachmentWizard(Mode.SCREENSHOT, null);
			}
		});
		registerDropListener(attachScreenshotButton, dropListener);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		initialize();

		Section section = createSection(parent, toolkit, hasIncoming);
		section.setText(getPartName() + " (" + attachments.length + ")");

		final Composite attachmentsComposite = toolkit.createComposite(section);
		attachmentsComposite.setLayout(new GridLayout(1, false));
		attachmentsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		dropListener = new TaskAttachmentDropListener();
		registerDropListener(section, dropListener);

		if (attachments.length > 0) {
			createAttachmentTable(toolkit, attachmentsComposite);
		} else {
			toolkit.createLabel(attachmentsComposite, "No attachments");
			// TODO EDITOR registerDropListener(label);
		}

		createButtons(attachmentsComposite, toolkit);

		section.setClient(attachmentsComposite);
		setSection(toolkit, section);
	}

	private void registerDropListener(Control control, TaskAttachmentDropListener dropListener) {
		DropTarget target = new DropTarget(control, DND.DROP_COPY | DND.DROP_DEFAULT);
		final TextTransfer textTransfer = TextTransfer.getInstance();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		Transfer[] types = new Transfer[] { textTransfer, fileTransfer };
		target.setTransfer(types);
		target.addDropListener(dropListener);
	}

	@Override
	public void dispose() {
		if (menuManager != null) {
			menuManager.dispose();
		}
		super.dispose();
	}

	private void initialize() {
		attachments = getTaskData().getAttributeMapper().getAttributesByType(getTaskData(),
				TaskAttribute.TYPE_ATTACHMENT);
	}

	private NewAttachmentWizardDialog openNewAttachmentWizard(Mode mode, AbstractTaskAttachmentSource source) {
		TaskAttributeMapper mapper = getModel().getTaskData().getAttributeMapper();
		TaskAttribute attribute = mapper.createTaskAttachment(getModel().getTaskData());
		return TasksUiInternal.openNewAttachmentWizard(getTaskEditorPage().getSite().getShell(),
				getTaskEditorPage().getTaskRepository(), getTaskEditorPage().getTask(), attribute, mode, source);
	}

	/**
	 * @author Mik Kersten
	 * @author Maarten Meijer
	 * @author Steffen Pingel
	 */
	public class TaskAttachmentDropListener implements DropTargetListener {

		public TaskAttachmentDropListener() {
		}

		public void dragEnter(DropTargetEvent event) {
			if (event.detail == DND.DROP_DEFAULT) {
				if ((event.operations & DND.DROP_COPY) != 0) {
					event.detail = DND.DROP_COPY;
				} else {
					event.detail = DND.DROP_NONE;
				}
			}
			// will accept text but prefer to have files dropped
			for (TransferData dataType : event.dataTypes) {
				if (FileTransfer.getInstance().isSupportedType(dataType)) {
					event.currentDataType = dataType;
					// files should only be copied
					if (event.detail != DND.DROP_COPY) {
						event.detail = DND.DROP_NONE;
					}
					break;
				}
			}
		}

		public void dragOver(DropTargetEvent event) {
			event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
		}

		public void dragOperationChanged(DropTargetEvent event) {
			if ((event.detail == DND.DROP_DEFAULT) || (event.operations & DND.DROP_COPY) != 0) {
				event.detail = DND.DROP_COPY;
			} else {
				event.detail = DND.DROP_NONE;
			}
			// allow text to be moved but files should only be copied
			if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
				if (event.detail != DND.DROP_COPY) {
					event.detail = DND.DROP_NONE;
				}
			}
		}

		public void dragLeave(DropTargetEvent event) {
		}

		public void dropAccept(DropTargetEvent event) {
		}

		public void drop(DropTargetEvent event) {
			if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
				String text = (String) event.data;
				openNewAttachmentWizard(null, new TextTaskAttachmentSource(text));
			}
			if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
				String[] files = (String[]) event.data;
				if (files.length > 0) {
					File file = new File(files[0]);
					NewAttachmentWizardDialog dialog = openNewAttachmentWizard(null, new FileTaskAttachmentSource(file));
					if (files.length > 1) {
						dialog.setMessage("Note that only the first file dragged will be attached.",
								IMessageProvider.WARNING);
					}
				}
			}
		}
	}
}
