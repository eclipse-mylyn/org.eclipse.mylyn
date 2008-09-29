/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Jeff Pound - attachment support
 *     Frank Becker - improvements for bug 204051
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
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
import org.eclipse.mylyn.internal.tasks.ui.actions.AttachAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.AttachScreenshotAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiMenus;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskAttachmentWizard.Mode;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskEditorAttachmentPart extends AbstractTaskEditorPart {

	private static final String ID_POPUP_MENU = "org.eclipse.mylyn.tasks.ui.editor.menu.attachments";

	private final String[] attachmentsColumns = { "Name", "Description", /*"Type", */"Size", "Creator", "Created" };

	private final int[] attachmentsColumnWidths = { 130, 150, /*100,*/70, 100, 100 };

	private List<TaskAttribute> attachments;

	private boolean hasIncoming;

	private MenuManager menuManager;

	private Composite attachmentsComposite;

	public TaskEditorAttachmentPart() {
		setPartName("Attachments");
	}

	private void createAttachmentTable(FormToolkit toolkit, final Composite attachmentsComposite) {
		Table attachmentsTable = toolkit.createTable(attachmentsComposite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		attachmentsTable.setLinesVisible(true);
		attachmentsTable.setHeaderVisible(true);
		attachmentsTable.setLayout(new GridLayout());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).hint(500, SWT.DEFAULT).applyTo(
				attachmentsTable);

		for (int i = 0; i < attachmentsColumns.length; i++) {
			TableColumn column = new TableColumn(attachmentsTable, SWT.LEFT, i);
			column.setText(attachmentsColumns[i]);
			column.setWidth(attachmentsColumnWidths[i]);
		}
		// size column
		attachmentsTable.getColumn(2).setAlignment(SWT.RIGHT);

		TableViewer attachmentsViewer = new TableViewer(attachmentsTable);
		attachmentsViewer.setUseHashlookup(true);
		attachmentsViewer.setColumnProperties(attachmentsColumns);
		ColumnViewerToolTipSupport.enableFor(attachmentsViewer, ToolTip.NO_RECREATE);

		attachmentsViewer.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				ITaskAttachment attachment1 = (ITaskAttachment) e1;
				ITaskAttachment attachment2 = (ITaskAttachment) e2;
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

		List<ITaskAttachment> attachmentList = new ArrayList<ITaskAttachment>(attachments.size());
		for (TaskAttribute attribute : attachments) {
			TaskAttachment taskAttachment = new TaskAttachment(getModel().getTaskRepository(), getModel().getTask(),
					attribute);
			getTaskData().getAttributeMapper().updateTaskAttachment(taskAttachment, attribute);
			attachmentList.add(taskAttachment);
		}
		attachmentsViewer.setContentProvider(new AttachmentsTableContentProvider2(attachmentList));
		attachmentsViewer.setLabelProvider(new AttachmentTableLabelProvider2(getModel(),
				getTaskEditorPage().getAttributeEditorToolkit()));
		attachmentsViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				if (!event.getSelection().isEmpty()) {
					StructuredSelection selection = (StructuredSelection) event.getSelection();
					ITaskAttachment attachment = (ITaskAttachment) selection.getFirstElement();
					TasksUiUtil.openUrl(attachment.getUrl());
				}
			}
		});
		attachmentsViewer.addSelectionChangedListener(getTaskEditorPage());
		attachmentsViewer.setInput(getTaskData());

		menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TasksUiMenus.fillTaskAttachmentMenu(manager);
			}
		});
		getTaskEditorPage().getEditorSite().registerContextMenu(ID_POPUP_MENU, menuManager, attachmentsViewer, false);
		Menu menu = menuManager.createContextMenu(attachmentsTable);
		attachmentsTable.setMenu(menu);
	}

	private void createButtons(Composite attachmentsComposite, FormToolkit toolkit) {
		final Composite attachmentControlsComposite = toolkit.createComposite(attachmentsComposite);
		attachmentControlsComposite.setLayout(new GridLayout(2, false));
		attachmentControlsComposite.setLayoutData(new GridData(GridData.BEGINNING));

		Button attachFileButton = toolkit.createButton(attachmentControlsComposite, AttachAction.LABEL, SWT.PUSH);
		attachFileButton.setImage(WorkbenchImages.getImage(ISharedImages.IMG_OBJ_FILE));
		attachFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EditorUtil.openNewAttachmentWizard(getTaskEditorPage(), Mode.DEFAULT, null);
			}
		});
		getTaskEditorPage().registerDefaultDropListener(attachFileButton);

		Button attachScreenshotButton = toolkit.createButton(attachmentControlsComposite, AttachScreenshotAction.LABEL,
				SWT.PUSH);
		attachScreenshotButton.setImage(CommonImages.getImage(CommonImages.IMAGE_CAPTURE));
		attachScreenshotButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EditorUtil.openNewAttachmentWizard(getTaskEditorPage(), Mode.SCREENSHOT, null);
			}
		});
		getTaskEditorPage().registerDefaultDropListener(attachScreenshotButton);
	}

	@Override
	public void createControl(Composite parent, final FormToolkit toolkit) {
		initialize();

		final Section section = createSection(parent, toolkit, hasIncoming);
		section.setText(getPartName() + " (" + attachments.size() + ")");
		if (hasIncoming) {
			expandSection(toolkit, section);
		} else {
			section.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanged(ExpansionEvent event) {
					if (attachmentsComposite == null) {
						expandSection(toolkit, section);
						getTaskEditorPage().reflow();
					}
				}
			});
		}
		setSection(toolkit, section);
	}

	private void expandSection(FormToolkit toolkit, Section section) {
		attachmentsComposite = toolkit.createComposite(section);
		attachmentsComposite.setLayout(new GridLayout(1, false));
		attachmentsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		getTaskEditorPage().registerDefaultDropListener(section);

		if (attachments.size() > 0) {
			createAttachmentTable(toolkit, attachmentsComposite);
		} else {
			Label label = toolkit.createLabel(attachmentsComposite, "No attachments");
			getTaskEditorPage().registerDefaultDropListener(label);
		}

		createButtons(attachmentsComposite, toolkit);

		section.setClient(attachmentsComposite);
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
		for (TaskAttribute attachmentAttribute : attachments) {
			if (getModel().hasIncomingChanges(attachmentAttribute)) {
				hasIncoming = true;
				break;
			}
		}
	}

}
