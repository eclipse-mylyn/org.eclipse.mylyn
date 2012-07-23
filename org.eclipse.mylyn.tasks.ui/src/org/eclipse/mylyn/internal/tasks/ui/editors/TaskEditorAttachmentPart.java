/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.commons.ui.TableSorter;
import org.eclipse.mylyn.commons.ui.TableViewerSupport;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.commands.OpenTaskAttachmentHandler;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiMenus;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskKeyComparator;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskAttachmentWizard.Mode;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskEditorAttachmentPart extends AbstractTaskEditorPart {

	private class AttachmentTableSorter extends TableSorter {

		TaskKeyComparator keyComparator = new TaskKeyComparator();

		@Override
		public int compare(TableViewer viewer, Object e1, Object e2, int columnIndex) {
			ITaskAttachment attachment1 = (ITaskAttachment) e1;
			ITaskAttachment attachment2 = (ITaskAttachment) e2;
			switch (columnIndex) {
			case 0:
				return CoreUtil.compare(attachment1.getFileName(), attachment2.getFileName());
			case 1:
				String description1 = attachment1.getDescription();
				String description2 = attachment2.getDescription();
				return CoreUtil.compare(description1, description2);
			case 2:
				return CoreUtil.compare(attachment1.getLength(), attachment2.getLength());
			case 3:
				return CoreUtil.compare(attachment1.getAuthor().toString(), attachment2.getAuthor().toString());
			case 4:
				return CoreUtil.compare(attachment1.getCreationDate(), attachment2.getCreationDate());
			case 5:
				String key1 = AttachmentTableLabelProvider.getAttachmentId(attachment1);
				String key2 = AttachmentTableLabelProvider.getAttachmentId(attachment2);
				return keyComparator.compare2(key1, key2);
			}
			return super.compare(viewer, e1, e2, columnIndex);
		}

	}

	private class AttachmentTableFilter extends ViewerFilter {

		private boolean filterDeprecatedEnabled;

		public boolean isFilterDeprecatedEnabled() {
			return filterDeprecatedEnabled;
		}

		public void setFilterDeprecatedEnabled(boolean filterDeprecatedEnabled) {
			this.filterDeprecatedEnabled = filterDeprecatedEnabled;
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (filterDeprecatedEnabled) {
				if (element instanceof ITaskAttachment) {
					return !((ITaskAttachment) element).isDeprecated();
				}
			}
			return true;
		}

	}

	private static final String PREF_FILTER_DEPRECATED = "org.eclipse.mylyn.tasks.ui.editor.attachments.filter.deprecated"; //$NON-NLS-1$

	private static final String ID_POPUP_MENU = "org.eclipse.mylyn.tasks.ui.editor.menu.attachments"; //$NON-NLS-1$

	private final String[] attachmentsColumns = { Messages.TaskEditorAttachmentPart_Name,
			Messages.TaskEditorAttachmentPart_Description, /*"Type", */Messages.TaskEditorAttachmentPart_Size,
			Messages.TaskEditorAttachmentPart_Creator, Messages.TaskEditorAttachmentPart_Created,
			Messages.TaskEditorAttachmentPart_ID };

	private final int[] attachmentsColumnWidths = { 130, 150, /*100,*/70, 100, 100, 0 };

	private List<TaskAttribute> attachmentAttributes;

	private boolean hasIncoming;

	private MenuManager menuManager;

	private Composite attachmentsComposite;

	private Table attachmentsTable;

	private AttachmentTableFilter tableFilter;

	private TableViewer attachmentsViewer;

	private List<ITaskAttachment> attachmentList;

	private Section section;

	private int nonDeprecatedCount;

	private Action filterDeprecatedAttachmentsAction;

	public TaskEditorAttachmentPart() {
		setPartName(Messages.TaskEditorAttachmentPart_Attachments);
	}

	private void createAttachmentTable(FormToolkit toolkit, final Composite attachmentsComposite) {
		attachmentsTable = toolkit.createTable(attachmentsComposite, SWT.MULTI | SWT.FULL_SELECTION);
		attachmentsTable.setLinesVisible(true);
		attachmentsTable.setHeaderVisible(true);
		attachmentsTable.setLayout(new GridLayout());
		GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.FILL)
				.grab(true, false)
				.hint(500, SWT.DEFAULT)
				.applyTo(attachmentsTable);
		attachmentsTable.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);

		for (int i = 0; i < attachmentsColumns.length; i++) {
			TableColumn column = new TableColumn(attachmentsTable, SWT.LEFT, i);
			column.setText(attachmentsColumns[i]);
			column.setWidth(attachmentsColumnWidths[i]);
			column.setMoveable(true);
			if (i == 4) {
				attachmentsTable.setSortColumn(column);
				attachmentsTable.setSortDirection(SWT.DOWN);
			}
		}
		// size column
		attachmentsTable.getColumn(2).setAlignment(SWT.RIGHT);

		attachmentsViewer = new TableViewer(attachmentsTable);
		attachmentsViewer.setUseHashlookup(true);
		attachmentsViewer.setColumnProperties(attachmentsColumns);
		ColumnViewerToolTipSupport.enableFor(attachmentsViewer, ToolTip.NO_RECREATE);

		attachmentsViewer.setSorter(new AttachmentTableSorter());

		attachmentsViewer.setContentProvider(new ArrayContentProvider());
		attachmentsViewer.setLabelProvider(new AttachmentTableLabelProvider(getModel(),
				getTaskEditorPage().getAttributeEditorToolkit()));
		attachmentsViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				openAttachments(event);
			}
		});
		attachmentsViewer.addSelectionChangedListener(getTaskEditorPage());
		attachmentsViewer.setInput(attachmentList.toArray());

		menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TasksUiMenus.fillTaskAttachmentMenu(manager);
			}
		});
		getTaskEditorPage().getEditorSite().registerContextMenu(ID_POPUP_MENU, menuManager, attachmentsViewer, true);
		Menu menu = menuManager.createContextMenu(attachmentsTable);
		attachmentsTable.setMenu(menu);

		attachmentsViewer.addFilter(tableFilter);

		new TableViewerSupport(attachmentsViewer, getStateFile());
	}

	private File getStateFile() {
		IPath stateLocation = Platform.getStateLocation(TasksUiPlugin.getDefault().getBundle());
		return stateLocation.append("TaskEditorAttachmentPart.xml").toFile(); //$NON-NLS-1$
	}

	private void createButtons(Composite attachmentsComposite, FormToolkit toolkit) {
		final Composite attachmentControlsComposite = toolkit.createComposite(attachmentsComposite);
		attachmentControlsComposite.setLayout(new GridLayout(2, false));
		attachmentControlsComposite.setLayoutData(new GridData(GridData.BEGINNING));

		Button attachFileButton = toolkit.createButton(attachmentControlsComposite,
				Messages.TaskEditorAttachmentPart_Attach_, SWT.PUSH);
		attachFileButton.setImage(CommonImages.getImage(CommonImages.FILE_PLAIN));
		attachFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EditorUtil.openNewAttachmentWizard(getTaskEditorPage(), Mode.DEFAULT, null);
			}
		});
		getTaskEditorPage().registerDefaultDropListener(attachFileButton);

		Button attachScreenshotButton = toolkit.createButton(attachmentControlsComposite,
				Messages.TaskEditorAttachmentPart_Attach__Screenshot, SWT.PUSH);
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

		section = createSection(parent, toolkit, hasIncoming);
		updateSectionTitle();
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
		attachmentsComposite.setLayout(EditorUtil.createSectionClientLayout());
		attachmentsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		getTaskEditorPage().registerDefaultDropListener(section);

		if (attachmentAttributes.size() > 0) {
			createAttachmentTable(toolkit, attachmentsComposite);
		} else {
			Label label = toolkit.createLabel(attachmentsComposite, Messages.TaskEditorAttachmentPart_No_attachments);
			getTaskEditorPage().registerDefaultDropListener(label);
		}

		createButtons(attachmentsComposite, toolkit);

		toolkit.paintBordersFor(attachmentsComposite);
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
		attachmentAttributes = getTaskData().getAttributeMapper().getAttributesByType(getTaskData(),
				TaskAttribute.TYPE_ATTACHMENT);
		attachmentList = new ArrayList<ITaskAttachment>(attachmentAttributes.size());
		for (TaskAttribute attribute : attachmentAttributes) {
			if (getModel().hasIncomingChanges(attribute)) {
				hasIncoming = true;
			}
			TaskAttachment taskAttachment = new TaskAttachment(getModel().getTaskRepository(), getModel().getTask(),
					attribute);
			getTaskData().getAttributeMapper().updateTaskAttachment(taskAttachment, attribute);
			attachmentList.add(taskAttachment);
			if (!taskAttachment.isDeprecated()) {
				nonDeprecatedCount++;
			}
		}

		tableFilter = new AttachmentTableFilter();
	}

	@Override
	protected void fillToolBar(ToolBarManager toolBarManager) {
		filterDeprecatedAttachmentsAction = new Action("", IAction.AS_CHECK_BOX) { //$NON-NLS-1$
			@Override
			public void run() {
				TasksUiPlugin.getDefault().getPreferenceStore().setValue(PREF_FILTER_DEPRECATED, isChecked());
				filterDeprecated(isChecked());
			}
		};
		filterDeprecatedAttachmentsAction.setImageDescriptor(TasksUiImages.FILTER_OBSOLETE_SMALL);
		filterDeprecatedAttachmentsAction.setToolTipText(Messages.TaskEditorAttachmentPart_Hide_Obsolete_Tooltip);
		if (nonDeprecatedCount > 0 && nonDeprecatedCount < attachmentAttributes.size()) {
			filterDeprecated(TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(PREF_FILTER_DEPRECATED));
		} else {
			// do not allow filtering if it would cause the table to be empty or no change
			filterDeprecatedAttachmentsAction.setEnabled(false);
		}
		toolBarManager.add(filterDeprecatedAttachmentsAction);

		Action attachFileAction = new Action() {
			@Override
			public void run() {
				EditorUtil.openNewAttachmentWizard(getTaskEditorPage(), Mode.DEFAULT, null);
			}
		};
		attachFileAction.setToolTipText(Messages.TaskEditorAttachmentPart_Attach_);
		attachFileAction.setImageDescriptor(TasksUiImages.FILE_NEW_SMALL);
		toolBarManager.add(attachFileAction);

		Action attachScreenshotAction = new Action() {
			@Override
			public void run() {
				EditorUtil.openNewAttachmentWizard(getTaskEditorPage(), Mode.SCREENSHOT, null);
			}
		};
		attachScreenshotAction.setToolTipText(Messages.TaskEditorAttachmentPart_Attach__Screenshot);
		attachScreenshotAction.setImageDescriptor(TasksUiImages.IMAGE_CAPTURE_SMALL);
		toolBarManager.add(attachScreenshotAction);
	}

	private void updateSectionTitle() {
		if (tableFilter.isFilterDeprecatedEnabled()) {
			section.setText(NLS.bind(Messages.TaskEditorAttachmentPart_Attachment_Section_Title_X_of_Y, new Object[] {
					CommonUiUtil.toLabel(getPartName()), nonDeprecatedCount, attachmentAttributes.size() }));
		} else {
			section.setText(NLS.bind(Messages.TaskEditorAttachmentPart_Attachment_Section_Title_X,
					CommonUiUtil.toLabel(getPartName()), attachmentAttributes.size()));
		}
	}

	protected void openAttachments(OpenEvent event) {
		List<ITaskAttachment> attachments = new ArrayList<ITaskAttachment>();

		StructuredSelection selection = (StructuredSelection) event.getSelection();

		List<?> items = selection.toList();
		for (Object item : items) {
			if (item instanceof ITaskAttachment) {
				attachments.add((ITaskAttachment) item);
			}
		}

		if (attachments.isEmpty()) {
			return;
		}

		IWorkbenchPage page = getTaskEditorPage().getSite().getWorkbenchWindow().getActivePage();
		try {
			OpenTaskAttachmentHandler.openAttachments(page, attachments);
		} catch (OperationCanceledException e) {
			// canceled
		}
	}

	@Override
	public boolean setFormInput(Object input) {
		if (input instanceof String) {
			String text = (String) input;
			if (attachmentAttributes != null) {
				for (ITaskAttachment attachment : attachmentList) {
					if (text.equals(attachment.getTaskAttribute().getId())) {
						CommonFormUtil.setExpanded((ExpandableComposite) getControl(), true);
						return selectReveal(attachment);
					}
				}
			}
		}
		return super.setFormInput(input);
	}

	private boolean selectReveal(ITaskAttachment attachment) {
		if (attachment == null || attachmentsTable == null) {
			return false;
		}

		if (tableFilter.isFilterDeprecatedEnabled() && attachment.isDeprecated()) {
			filterDeprecated(false);
		}

		attachmentsViewer.setSelection(new StructuredSelection(attachment));

		IManagedForm mform = getManagedForm();
		ScrolledForm form = mform.getForm();
		EditorUtil.focusOn(form, attachmentsTable);

		return true;
	}

	void filterDeprecated(boolean filter) {
		if (filterDeprecatedAttachmentsAction.isChecked() != filter) {
			filterDeprecatedAttachmentsAction.setChecked(filter);
		}
		tableFilter.setFilterDeprecatedEnabled(filter);
		if (attachmentsViewer != null) {
			attachmentsViewer.refresh();
			getTaskEditorPage().reflow();
		}
		updateSectionTitle();
	}

}
