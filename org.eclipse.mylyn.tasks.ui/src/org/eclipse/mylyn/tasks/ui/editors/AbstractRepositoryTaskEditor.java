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
package org.eclipse.mylar.tasks.ui.editors;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.context.core.util.DateUtil;
import org.eclipse.mylar.internal.tasks.core.CommentQuoter;
import org.eclipse.mylar.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.internal.tasks.ui.actions.AttachFileAction;
import org.eclipse.mylar.internal.tasks.ui.actions.CopyToClipboardAction;
import org.eclipse.mylar.internal.tasks.ui.actions.SaveRemoteFileAction;
import org.eclipse.mylar.internal.tasks.ui.editors.ContentOutlineTools;
import org.eclipse.mylar.internal.tasks.ui.editors.IRepositoryTaskAttributeListener;
import org.eclipse.mylar.internal.tasks.ui.editors.IRepositoryTaskSelection;
import org.eclipse.mylar.internal.tasks.ui.editors.RepositoryAttachmentEditorInput;
import org.eclipse.mylar.internal.tasks.ui.editors.RepositoryTaskOutlinePage;
import org.eclipse.mylar.internal.tasks.ui.wizards.NewAttachmentWizard;
import org.eclipse.mylar.internal.tasks.ui.wizards.NewAttachmentWizardDialog;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IMylarStatusConstants;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskDataHandler;
import org.eclipse.mylar.tasks.core.MylarStatus;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
import org.eclipse.mylar.tasks.core.RepositoryOperation;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskComment;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.ObjectActionContributorManager;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.themes.IThemeManager;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Jeff Pound (Attachment work)
 * @author Steffen Pingel
 */
public abstract class AbstractRepositoryTaskEditor extends TaskFormPage {

	public static final String LABEL_JOB_SUBMIT = "Submitting to repository";

	private static final String HEADER_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static final String ATTACHMENT_DEFAULT_NAME = "attachment";

	private static final String CTYPE_ZIP = "zip";

	private static final String CTYPE_OCTET_STREAM = "octet-stream";

	private static final String CTYPE_TEXT = "text";

	private static final String CTYPE_HTML = "html";

	private static final String LABEL_BROWSER = "Browser";

	private static final String LABEL_DEFAULT_EDITOR = "Default Editor";

	private static final String LABEL_TEXT_EDITOR = "Text Editor";

	protected static final String CONTEXT_MENU_ID = "#MylarRepositoryEditor";

	public static final String HYPERLINK_TYPE_TASK = "task";

	public static final String HYPERLINK_TYPE_JAVA = "java";

	private static final String LABEL_BUTTON_SUBMIT = "Submit to Repository";

	protected static final String LABEL_SECTION_ACTIONS = "Actions";

	private static final String LABEL_SECTION_ATTRIBUTES = "Attributes";

	private static final String LABEL_SECTION_ATTACHMENTS = "Attachments";

	protected static final String LABEL_SECTION_DESCRIPTION = "Description";

	protected static final String LABEL_SECTION_COMMENTS = "Comments";

	protected static final String LABEL_SECTION_NEW_COMMENT = "New Comment";

	protected static final String SECTION_TITLE_PEOPLE = "People";

	protected FormToolkit toolkit;

	private ScrolledForm form;

	protected TaskRepository repository;

	public static final int RADIO_OPTION_WIDTH = 150;

	protected Display display;

	public static final Font TITLE_FONT = JFaceResources.getBannerFont();

	public static final Font TEXT_FONT = JFaceResources.getDefaultFont();

	public static final Font HEADER_FONT = JFaceResources.getDefaultFont();

	public static final int DESCRIPTION_WIDTH = 79 * 8; // 500;

	public static final int DESCRIPTION_HEIGHT = 10 * 14;

	private static final String REASSIGN_BUG_TO = "Reassign  bug to";

	protected RepositoryTaskEditorInput editorInput;

	private TaskEditor parentEditor = null;

	protected RepositoryTaskOutlineNode taskOutlineModel = null;

	protected boolean expandedStateAttributes = false;

	private AbstractRepositoryTask modifiedTask;

	/**
	 * Style option for function <code>newLayout</code>. This will create a
	 * plain-styled, selectable text label.
	 */
	protected final String VALUE = "VALUE";

	/**
	 * Style option for function <code>newLayout</code>. This will create a
	 * bolded, selectable header. It will also have an arrow image before the
	 * text (simply for decoration).
	 */
	protected final String HEADER = "HEADER";

	/**
	 * Style option for function <code>newLayout</code>. This will create a
	 * bolded, unselectable label.
	 */
	protected final String PROPERTY = "PROPERTY";

	protected final int HORZ_INDENT = 0;

	protected CCombo attributeCombo;

	protected Button addSelfToCCCheck;

	protected Text summaryText;

	protected Button submitButton;

	protected Table attachmentsTable;

	protected TableViewer attachmentTableViewer;

	protected String[] attachmentsColumns = { "Description", "Type", "Creator", "Created" };

	protected int[] attachmentsColumnWidths = { 200, 100, 100, 200 };

	protected int scrollIncrement;

	protected int scrollVertPageIncrement;

	protected int scrollHorzPageIncrement;

	protected StyledText currentSelectedText;

	protected RetargetAction cutAction;

	protected RetargetAction pasteAction;

	protected Composite editorComposite;

	protected TextViewer newCommentTextViewer;

	protected org.eclipse.swt.widgets.List ccList;

	protected Text ccText;

	private TableViewer attachmentsTableViewer;

	private Section commentsSection;

	private List<IRepositoryTaskAttributeListener> attributesListeners = new ArrayList<IRepositoryTaskAttributeListener>();

	protected RepositoryTaskData taskData;

	protected final ISelectionProvider selectionProvider = new ISelectionProvider() {
		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			selectionChangedListeners.add(listener);
		}

		public ISelection getSelection() {
			return null;
		}

		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			selectionChangedListeners.remove(listener);
		}

		public void setSelection(ISelection selection) {
			// No implementation.
		}
	};

	protected List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

	protected HashMap<CCombo, RepositoryTaskAttribute> comboListenerMap = new HashMap<CCombo, RepositoryTaskAttribute>();

	private IRepositoryTaskSelection lastSelected = null;

	/**
	 * Focuses on form widgets when an item in the outline is selected.
	 */
	protected final ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if ((part instanceof ContentOutline) && (selection instanceof StructuredSelection)) {
				Object select = ((StructuredSelection) selection).getFirstElement();
				if (select instanceof RepositoryTaskOutlineNode) {
					RepositoryTaskOutlineNode n = (RepositoryTaskOutlineNode) select;

					if (n != null && lastSelected != null
							&& ContentOutlineTools.getHandle(n).equals(ContentOutlineTools.getHandle(lastSelected))) {
						// we don't need to set the selection if it is already
						// set
						return;
					}
					lastSelected = n;

					boolean highlight = true;
					if (n.getKey().equals(RepositoryTaskOutlineNode.LABEL_COMMENTS)) {
						highlight = false;
					}

					Object data = n.getData();
					if (n.getKey().equals(RepositoryTaskOutlineNode.LABEL_NEW_COMMENT)) {
						selectNewComment();
					} else if (n.getKey().equals(RepositoryTaskOutlineNode.LABEL_DESCRIPTION)
							&& descriptionTextViewer.isEditable()) {
						selectDescription();
					} else if (data != null) {
						select(data, highlight);
					}
				}
			}
		}
	};

	private final class AttachmentLabelProvider extends LabelProvider implements IColorProvider {

		public Color getBackground(Object element) {
			return attachmentsTable.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		}

		public Color getForeground(Object element) {
			return attachmentsTable.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		}

	}

	private static final class AttachmentTableLabelProvider extends DecoratingLabelProvider implements
			ITableColorProvider, ITableLabelProvider {

		public AttachmentTableLabelProvider(ILabelProvider provider, ILabelDecorator decorator) {
			super(provider, decorator);
		}

		public Image getColumnImage(Object element, int columnIndex) {
			// RepositoryAttachment attachment = (RepositoryAttachment)
			// element;
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			RepositoryAttachment attachment = (RepositoryAttachment) element;
			switch (columnIndex) {
			case 0:
				return attachment.getDescription();
			case 1:
				if (attachment.isPatch()) {
					return "patch";
				} else {
					return attachment.getContentType();
				}
			case 2:
				return attachment.getCreator();
			case 3:
				// TODO should retrieve Date object from IOfflineTaskHandler
				return attachment.getDateCreated();
			}
			return "unrecognized column";
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
			// ignore

		}

		@Override
		public void dispose() {
			// ignore

		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// ignore
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// ignore

		}

		public Color getForeground(Object element, int columnIndex) {
			RepositoryAttachment att = (RepositoryAttachment) element;
			if (att.isObsolete()) {
				return TaskListColorsAndFonts.COLOR_GRAY_LIGHT;
			}
			return super.getForeground(element);
		}

		public Color getBackground(Object element, int columnIndex) {
			return super.getBackground(element);
		}

		public Font getFont(Object element, int columnIndex) {
			return super.getFont(element);
		}
	}

	protected class ComboSelectionListener extends SelectionAdapter {

		private CCombo combo;

		public ComboSelectionListener(CCombo combo) {
			this.combo = combo;
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			// ignore
		}

		public void widgetSelected(SelectionEvent event) {
			if (comboListenerMap.containsKey(combo)) {
				if (combo.getSelectionIndex() > -1) {
					String sel = combo.getItem(combo.getSelectionIndex());
					RepositoryTaskAttribute attribute = comboListenerMap.get(combo);
					if (sel != null && !(sel.equals(attribute.getValue()))) {
						attribute.setValue(sel);
						for (IRepositoryTaskAttributeListener client : attributesListeners) {
							client.attributeChanged(attribute.getName(), sel);
						}
						markDirty(true);
					}
				}
			}
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		if (!(input instanceof RepositoryTaskEditorInput)) {
			return;
		}

		editorInput = (RepositoryTaskEditorInput) input;
		repository = editorInput.getRepository();
		taskData = editorInput.getTaskData();
		connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
		setSite(site);
		setInput(input);

		taskOutlineModel = RepositoryTaskOutlineNode.parseBugReport(taskData);
		hasAttributeChanges = hasVisibleAttributeChanges();
		isDirty = false;
		updateEditorTitle();
	}

	// TODO: Use to block synchronization when editor dirty?
	// @Override
	// public void markDirty(boolean dirty) {
	// editorInput.getRepositoryTask().setDirty(dirty);
	// super.markDirty(dirty);
	// }

	/**
	 * Update task state
	 */
	protected void updateTask() {
		if (taskData == null)
			return;
		taskData.setHasLocalChanges(true);
		AbstractRepositoryTask repositoryTask = editorInput.getRepositoryTask();
		if (repositoryTask != null) {
			TasksUiPlugin.getSynchronizationManager().updateOfflineState(repositoryTask, taskData, false);
			TasksUiPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);
		}
		if (parentEditor != null) {
			parentEditor.notifyTaskChanged();
		}

	}

	protected abstract void validateInput();

	protected Color backgroundIncoming;

	protected boolean hasAttributeChanges = false;

	protected boolean showAttachments = true;

	/**
	 * Creates a new <code>AbstractRepositoryTaskEditor</code>. Sets up the
	 * default fonts and cut/copy/paste actions.
	 */
	public AbstractRepositoryTaskEditor(FormEditor editor) {
		// set the scroll increments so the editor scrolls normally with the
		// scroll wheel
		super(editor, "id", "label"); //$NON-NLS-1$ //$NON-NLS-2$
		FontData[] fd = TEXT_FONT.getFontData();
		int cushion = 4;
		scrollIncrement = fd[0].getHeight() + cushion;
		scrollVertPageIncrement = 0;
		scrollHorzPageIncrement = 0;
	}

	public String getNewCommentText() {
		return addCommentsTextBox.getText();
	}

	protected void createFormContent(final IManagedForm managedForm) {
		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		backgroundIncoming = themeManager.getCurrentTheme().getColorRegistry().get(
				TaskListColorsAndFonts.THEME_COLOR_TASKS_INCOMING_BACKGROUND);

		super.createFormContent(managedForm);
		form = managedForm.getForm();
		toolkit = managedForm.getToolkit();
		registerDropListener(form);
		
		editorComposite = form.getBody();
		GridLayout editorLayout = new GridLayout();
		editorComposite.setLayout(editorLayout);
		editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (taskData == null) {
			toolkit.createLabel(editorComposite, "Task data not available, please synchronize and reopen.");
			return;
		}

		createReportHeaderLayout(editorComposite);
		Composite attribComp = createAttributeLayout(editorComposite);
		createCustomAttributeLayout(attribComp);
		if (showAttachments) {
			createAttachmentLayout(editorComposite);
		}
		createDescriptionLayout(editorComposite);
		createCommentLayout(editorComposite);
		createNewCommentLayout(editorComposite);
		Composite bottomComposite = toolkit.createComposite(editorComposite);
		bottomComposite.setLayout(new GridLayout(2, false));
		// GridDataFactory.fillDefaults().grab(true,
		// false).applyTo(bottomComposite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.DEFAULT).applyTo(bottomComposite);

		createActionsLayout(bottomComposite);
		createPeopleLayout(bottomComposite);

		form.reflow(true);
		getSite().getPage().addSelectionListener(selectionListener);
		getSite().setSelectionProvider(selectionProvider);
		// if (this.addCommentsTextBox != null) {
		// registerDropListener(this.addCommentsTextBox);
		// }
		if (summaryText != null) {
			summaryText.setFocus();
		}
	}

	protected void createReportHeaderLayout(Composite composite) {
		addSummaryText(composite);

		Composite headerInfoComposite = toolkit.createComposite(composite);
		GridLayout headerLayout = new GridLayout(10, false);
		headerLayout.verticalSpacing = 1;
		headerLayout.marginHeight = 1;
		headerLayout.horizontalSpacing = 2;
		headerInfoComposite.setLayout(headerLayout);

		toolkit.createLabel(headerInfoComposite, " Status: ").setFont(TITLE_FONT);
		RepositoryTaskAttribute attribute = taskData.getAttribute(RepositoryTaskAttribute.STATUS);
		createTextField(headerInfoComposite, attribute, SWT.FLAT | SWT.READ_ONLY);

		toolkit.createLabel(headerInfoComposite, " Priority: ").setFont(TITLE_FONT);
		attribute = taskData.getAttribute(RepositoryTaskAttribute.PRIORITY);
		createTextField(headerInfoComposite, attribute, SWT.FLAT | SWT.READ_ONLY);
		// RepositoryTaskAttribute attribute =
		// taskData.getAttribute(RepositoryTaskAttribute.PRIORITY);
		// if (attribute != null) {
		// String value = attribute.getValue() != null ? attribute.getValue() :
		// "";
		// attributeCombo = new CCombo(headerInfoComposite, SWT.FLAT |
		// SWT.READ_ONLY);
		// toolkit.adapt(attributeCombo, true, true);
		// attributeCombo.setFont(TEXT_FONT);
		// if (attribute.getValues() != null) {
		//
		// Set<String> s = attribute.getOptionValues().keySet();
		// String[] a = s.toArray(new String[s.size()]);
		// for (int i = 0; i < a.length; i++) {
		// attributeCombo.add(a[i]);
		// }
		// if (attributeCombo.indexOf(value) != -1) {
		// attributeCombo.select(attributeCombo.indexOf(value));
		// }
		// }
		// attributeCombo.addSelectionListener(new
		// ComboSelectionListener(attributeCombo));
		// comboListenerMap.put(attributeCombo, attribute);
		// attributeCombo.addListener(SWT.FocusIn, new GenericListener());
		// }

		toolkit.createLabel(headerInfoComposite, "  ID: ").setFont(TITLE_FONT);
		toolkit.createText(headerInfoComposite, "" + taskData.getId(), SWT.FLAT | SWT.READ_ONLY);

		String openedDateString = "";
		String modifiedDateString = "";
		final ITaskDataHandler taskDataManager = connector.getTaskDataHandler();
		if (taskDataManager != null) {
			Date created = taskDataManager.getDateForAttributeType(RepositoryTaskAttribute.DATE_CREATION, taskData
					.getCreated());
			openedDateString = created != null ? DateUtil.getFormattedDate(created, HEADER_DATE_FORMAT) : "";

			Date modified = taskDataManager.getDateForAttributeType(RepositoryTaskAttribute.DATE_MODIFIED, taskData
					.getLastModified());
			modifiedDateString = modified != null ? DateUtil.getFormattedDate(modified, HEADER_DATE_FORMAT) : "";
		}

		toolkit.createLabel(headerInfoComposite, " Opened: ").setFont(TITLE_FONT);
		toolkit.createText(headerInfoComposite, openedDateString, SWT.FLAT | SWT.READ_ONLY);
		toolkit.createLabel(headerInfoComposite, " Modified: ").setFont(TITLE_FONT);
		toolkit.createText(headerInfoComposite, modifiedDateString, SWT.FLAT | SWT.READ_ONLY);
	}

	/**
	 * Utility method to create text field sets backgournd to
	 * TaskListColorsAndFonts.COLOR_ATTRIBUTE_CHANGED if attribute has changed.
	 * 
	 * @param composite
	 * @param attribute
	 * @param style
	 */
	protected Text createTextField(Composite composite, RepositoryTaskAttribute attribute, int style) {
		final Text text;
		String value;
		if (attribute == null) {
			value = "";
		} else {
			value = attribute.getValue();
		}
		text = toolkit.createText(composite, value, style);
		text.setData(attribute);
		if (attribute != null && !attribute.isReadOnly()) {
			text.setData(attribute);
			text.addListener(SWT.KeyUp, new Listener() {
				public void handleEvent(Event event) {
					String sel = text.getText();
					RepositoryTaskAttribute a = (RepositoryTaskAttribute) text.getData();
					if (!(a.getValue().equals(sel))) {
						a.setValue(sel);
						markDirty(true);
					}
				}
			});
		}
		if (hasChanged(attribute)) {
			text.setBackground(backgroundIncoming);
		}
		return text;
	}

	/**
	 * Creates the attribute layout, which contains most of the basic attributes
	 * of the bug (some of which are editable).
	 */
	protected Composite createAttributeLayout(Composite composite) {
		String title = taskData.getLabel();
		Section section = createSection(composite, LABEL_SECTION_ATTRIBUTES);
		section.setExpanded(expandedStateAttributes || hasAttributeChanges);

		// Attributes Composite- this holds all the combo fields and text fields
		Composite attributesComposite = toolkit.createComposite(section);
		GridLayout attributesLayout = new GridLayout();
		attributesLayout.numColumns = 4;
		attributesLayout.horizontalSpacing = 5;
		attributesLayout.verticalSpacing = 4;
		attributesComposite.setLayout(attributesLayout);
		GridData attributesData = new GridData(GridData.FILL_BOTH);
		attributesData.horizontalSpan = 1;
		attributesData.grabExcessVerticalSpace = false;
		attributesComposite.setLayoutData(attributesData);
		section.setClient(attributesComposite);
		editorInput.setToolTipText(title);

		int currentCol = 1;

		for (RepositoryTaskAttribute attribute : taskData.getAttributes()) {
			String name = attribute.getName();
			String value = "";
			value = checkText(attribute.getValue());
			if (attribute.isHidden())
				continue;

			List<String> values = attribute.getOptions();

			if (values == null)
				values = new ArrayList<String>();

			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			data.horizontalSpan = 1;
			data.horizontalIndent = HORZ_INDENT;

			if (attribute.hasOptions() && !attribute.isReadOnly()) {
				Label label = toolkit.createLabel(attributesComposite, name);
				GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
				attributeCombo = new CCombo(attributesComposite, SWT.FLAT | SWT.READ_ONLY);
				toolkit.adapt(attributeCombo, true, true);
				attributeCombo.setFont(TEXT_FONT);

				if (hasChanged(attribute)) {
					attributeCombo.setBackground(backgroundIncoming);
				}
				attributeCombo.setLayoutData(data);
				for (String val : values) {
					attributeCombo.add(val);
				}

				if (attributeCombo.indexOf(value) != -1) {
					attributeCombo.select(attributeCombo.indexOf(value));
				}
				attributeCombo.addSelectionListener(new ComboSelectionListener(attributeCombo));
				comboListenerMap.put(attributeCombo, attribute);
				attributeCombo.addListener(SWT.FocusIn, new GenericListener());
				currentCol += 2;
			} else {
				Label label = toolkit.createLabel(attributesComposite, name);
				GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
				Composite textFieldComposite = toolkit.createComposite(attributesComposite);
				GridLayout textLayout = new GridLayout();
				textLayout.marginWidth = 1;
				textLayout.marginHeight = 2;
				textFieldComposite.setLayout(textLayout);
				GridData textData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
				textData.horizontalSpan = 1;
				textData.widthHint = 135;

				if (attribute.isReadOnly()) {
					final Text text = createTextField(textFieldComposite, attribute, SWT.FLAT | SWT.READ_ONLY);
					text.setLayoutData(textData);
				} else {
					final Text text = createTextField(textFieldComposite, attribute, SWT.FLAT);
					// text.setFont(COMMENT_FONT);
					text.setLayoutData(textData);
					toolkit.paintBordersFor(textFieldComposite);
					text.setData(attribute);
					text.addListener(SWT.KeyUp, new Listener() {
						public void handleEvent(Event event) {
							String sel = text.getText();
							RepositoryTaskAttribute a = (RepositoryTaskAttribute) text.getData();
							if (!(a.getValue().equals(sel))) {
								a.setValue(sel);
								markDirty(true);
							}
						}
					});
					text.addListener(SWT.FocusIn, new GenericListener());
				}

				currentCol += 2;
			}
			if (currentCol > attributesLayout.numColumns) {
				currentCol -= attributesLayout.numColumns;
			}
		}
		toolkit.paintBordersFor(attributesComposite);
		// make sure that we are in the first column
		if (currentCol > 1) {
			while (currentCol <= attributesLayout.numColumns) {
				toolkit.createLabel(attributesComposite, "");
				// newLayout(attributesComposite, 1, "", PROPERTY);
				currentCol++;
			}
		}
		return attributesComposite;
	}

	/**
	 * Adds a text field to display and edit the bug's summary.
	 * 
	 * @param attributesComposite
	 *            The composite to add the text field to.
	 */
	protected void addSummaryText(Composite attributesComposite) {

		Composite summaryComposite = toolkit.createComposite(attributesComposite);
		GridLayout summaryLayout = new GridLayout(2, false);
		summaryLayout.verticalSpacing = 0;
		summaryLayout.marginHeight = 2;
		summaryComposite.setLayout(summaryLayout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(summaryComposite);

		toolkit.createLabel(summaryComposite, "Summary:").setFont(TITLE_FONT);
		summaryText = createTextField(summaryComposite, taskData.getAttribute(RepositoryTaskAttribute.SUMMARY),
				SWT.FLAT);
		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		Font summaryFont = themeManager.getCurrentTheme().getFontRegistry()
				.get(TaskListColorsAndFonts.TASK_EDITOR_FONT);
		summaryText.setFont(summaryFont);

		GridDataFactory.fillDefaults().grab(true, false).hint(DESCRIPTION_WIDTH, SWT.DEFAULT).applyTo(summaryText);

		summaryText.addListener(SWT.KeyUp, new SummaryListener());
		summaryText.addListener(SWT.FocusIn, new GenericListener());
		toolkit.paintBordersFor(summaryComposite);
	}

	protected void createAttachmentLayout(Composite composite) {

		// TODO: expand to show new attachments
		Section section = createSection(composite, LABEL_SECTION_ATTACHMENTS + " (" + taskData.getAttachments().size()
				+ ")");
		section.setExpanded(false);
		final Composite attachmentsComposite = toolkit.createComposite(section);
		attachmentsComposite.setLayout(new GridLayout(1, false));
		attachmentsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		section.setClient(attachmentsComposite);

		if (taskData.getAttachments().size() > 0) {

			attachmentsTable = toolkit.createTable(attachmentsComposite, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
			attachmentsTable.setLinesVisible(true);
			attachmentsTable.setHeaderVisible(true);
			attachmentsTable.setLayout(new GridLayout());
			GridData tableGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			// tableGridData.heightHint = 100;
			// tableGridData.widthHint = DESCRIPTION_WIDTH;
			attachmentsTable.setLayoutData(tableGridData);

			for (int i = 0; i < attachmentsColumns.length; i++) {
				TableColumn column = new TableColumn(attachmentsTable, SWT.LEFT, i);
				column.setText(attachmentsColumns[i]);
				column.setWidth(attachmentsColumnWidths[i]);
			}

			attachmentsTableViewer = new TableViewer(attachmentsTable);
			attachmentsTableViewer.setUseHashlookup(true);
			attachmentsTableViewer.setColumnProperties(attachmentsColumns);

			final ITaskDataHandler offlineHandler = connector.getTaskDataHandler();
			if (offlineHandler != null) {
				attachmentsTableViewer.setSorter(new ViewerSorter() {
					public int compare(Viewer viewer, Object e1, Object e2) {
						RepositoryAttachment attachment1 = (RepositoryAttachment) e1;
						RepositoryAttachment attachment2 = (RepositoryAttachment) e2;
						Date created1 = offlineHandler.getDateForAttributeType(RepositoryTaskAttribute.ATTACHMENT_DATE,
								attachment1.getDateCreated());
						Date created2 = offlineHandler.getDateForAttributeType(RepositoryTaskAttribute.ATTACHMENT_DATE,
								attachment2.getDateCreated());
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
			}

			attachmentsTableViewer.setContentProvider(new IStructuredContentProvider() {

				public Object[] getElements(Object inputElement) {
					List<RepositoryAttachment> attachments = taskData.getAttachments();
					return attachments.toArray();
				}

				public void dispose() {
					// ignore
				}

				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
					if (!viewer.getControl().isDisposed()) {
						viewer.refresh();
					}
				}
			});

			attachmentsTableViewer.setLabelProvider(new AttachmentTableLabelProvider(new AttachmentLabelProvider(),
					PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));

			attachmentsTableViewer.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(DoubleClickEvent event) {
					// String address = repository.getUrl() +
					// ATTACHMENT_URL_SUFFIX;
					if (!event.getSelection().isEmpty()) {
						StructuredSelection selection = (StructuredSelection) event.getSelection();
						RepositoryAttachment attachment = (RepositoryAttachment) selection.getFirstElement();
						// address += attachment.getId() + "&amp;action=view";
						TasksUiUtil.openUrl(attachment.getUrl());
					}
				}
			});

			attachmentsTableViewer.setInput(taskData);

			final MenuManager popupMenu = new MenuManager();
			final Menu menu = popupMenu.createContextMenu(attachmentsTable);
			attachmentsTable.setMenu(menu);

			popupMenu.addMenuListener(new IMenuListener() {
				public void menuAboutToShow(IMenuManager manager) {
					// TODO: use workbench mechanism for this?
					ObjectActionContributorManager.getManager().contributeObjectActions(
							AbstractRepositoryTaskEditor.this, popupMenu, attachmentsTableViewer);
				}
			});

			final MenuManager openMenu = new MenuManager("Open With");

			final Action openWithBrowserAction = new Action(LABEL_BROWSER) {
				public void run() {
					RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer
							.getSelection()).getFirstElement());
					if (attachment != null) {
						TasksUiUtil.openUrl(attachment.getUrl());
					}
				}
			};

			final Action openWithDefaultAction = new Action(LABEL_DEFAULT_EDITOR) {
				public void run() {
					// browser shortcut
					RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer
							.getSelection()).getFirstElement());
					if (attachment == null)
						return;

					if (attachment.getContentType().endsWith(CTYPE_HTML)) {
						TasksUiUtil.openUrl(attachment.getUrl());
						return;
					}

					IStorageEditorInput input = new RepositoryAttachmentEditorInput(repository, attachment);
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					if (page == null) {
						return;
					}
					IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(
							input.getName());
					try {
						page.openEditor(input, desc.getId());
					} catch (PartInitException e) {
						MylarStatusHandler.fail(e, "Unable to open editor for: " + attachment.getDescription(), false);
					}
				}
			};

			final Action openWithTextEditorAction = new Action(LABEL_TEXT_EDITOR) {
				public void run() {
					RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer
							.getSelection()).getFirstElement());
					IStorageEditorInput input = new RepositoryAttachmentEditorInput(repository, attachment);
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					if (page == null) {
						return;
					}

					try {
						page.openEditor(input, "org.eclipse.ui.DefaultTextEditor");
					} catch (PartInitException e) {
						MylarStatusHandler.fail(e, "Unable to open editor for: " + attachment.getDescription(), false);
					}
				}
			};

			final Action saveAction = new Action(SaveRemoteFileAction.TITLE) {
				public void run() {
					RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer
							.getSelection()).getFirstElement());
					/* Launch Browser */
					FileDialog fileChooser = new FileDialog(attachmentsTable.getShell(), SWT.SAVE);
					String fname = attachment.getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_FILENAME);
					// Default name if none is found
					if (fname.equals("")) {
						String ctype = attachment.getContentType();
						if (ctype.endsWith(CTYPE_HTML)) {
							fname = ATTACHMENT_DEFAULT_NAME + ".html";
						} else if (ctype.startsWith(CTYPE_TEXT)) {
							fname = ATTACHMENT_DEFAULT_NAME + ".txt";
						} else if (ctype.endsWith(CTYPE_OCTET_STREAM)) {
							fname = ATTACHMENT_DEFAULT_NAME;
						} else if (ctype.endsWith(CTYPE_ZIP)) {
							fname = ATTACHMENT_DEFAULT_NAME + "." + CTYPE_ZIP;
						} else {
							fname = ATTACHMENT_DEFAULT_NAME + "." + ctype.substring(ctype.indexOf("/") + 1);
						}
					}
					fileChooser.setFileName(fname);
					String filePath = fileChooser.open();

					// Check if the dialog was canceled or an error occured
					if (filePath == null) {
						return;
					}

					// TODO: Use IAttachmentHandler instead

					AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
							.getRepositoryConnector(repository.getKind());
					IAttachmentHandler handler = connector.getAttachmentHandler();

					SaveRemoteFileAction save = new SaveRemoteFileAction();
					try {
						save
								.setInputStream(new ByteArrayInputStream(handler.getAttachmentData(repository,
										attachment)));
						save.setDestinationFilePath(filePath);
						save.run();
					} catch (CoreException e) {
						MylarStatusHandler.fail(e.getStatus().getException(), "Attachment save failed", false);
					}
				}
			};

			final Action copyToClipAction = new Action(CopyToClipboardAction.TITLE) {
				public void run() {
					RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer
							.getSelection()).getFirstElement());
					CopyToClipboardAction copyToClip = new CopyToClipboardAction();
					copyToClip.setContents(TasksUiPlugin.getRepositoryManager().getAttachmentContents(attachment));
					copyToClip.setControl(attachmentsTable.getParent());
					copyToClip.run();
				}
			};

			// final Action applyPatchAction = new ApplyPatchAction() {
			//
			// };
			// applyPatchAction.setEnabled(true); // pending bug 98707

			/*
			 * Rebuild menu with the appropriate items for the selection
			 */
			attachmentsTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent e) {
					RepositoryAttachment att = (RepositoryAttachment) (((StructuredSelection) e.getSelection())
							.getFirstElement());
					popupMenu.removeAll();
					popupMenu.add(openMenu);
					openMenu.removeAll();

					IStorageEditorInput input = new RepositoryAttachmentEditorInput(repository, att);
					IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(
							input.getName());
					if (desc != null) {
						openMenu.add(openWithDefaultAction);
					}
					openMenu.add(openWithBrowserAction);
					openMenu.add(openWithTextEditorAction);

					popupMenu.add(new Separator());
					popupMenu.add(saveAction);

					if (att.getContentType().startsWith(CTYPE_TEXT) || att.getContentType().endsWith("xml")) {
						popupMenu.add(copyToClipAction);
					}
					popupMenu.add(new Separator("actions"));
					// if (att.isPatch()) {
					// popupMenu.add(applyPatchAction);
					// }
				}
			});

		} else {
			Label label = toolkit.createLabel(attachmentsComposite, "No attachments");
			registerDropListener(label);
		}

		/* Launch a NewAttachemntWizard */
		Button addAttachmentButton = toolkit.createButton(attachmentsComposite, "Attach File...", SWT.PUSH);

		ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
				AbstractRepositoryTask.getHandle(repository.getUrl(), taskData.getId()));
		if (task == null) {
			addAttachmentButton.setEnabled(false);
		}

		addAttachmentButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			public void widgetSelected(SelectionEvent e) {
				ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
						AbstractRepositoryTask.getHandle(repository.getUrl(), taskData.getId()));
				if (!(task instanceof AbstractRepositoryTask)) {
					// Should not happen
					return;
				}
				if (AbstractRepositoryTaskEditor.this.isDirty
						|| ((AbstractRepositoryTask) task).getSyncState().equals(RepositoryTaskSyncState.OUTGOING)) {
					MessageDialog.openInformation(attachmentsComposite.getShell(),
							"Task not synchronized or dirty editor",
							"Commit edits or synchronize task before adding attachments.");
					return;
				} else {
					AttachFileAction attachFileAction = new AttachFileAction();
					attachFileAction.selectionChanged(new StructuredSelection(task));
					attachFileAction.run();
				}
				// NewAttachmentWizard naw = new NewAttachmentWizard(repository,
				// (AbstractRepositoryTask) task);
				// NewAttachmentWizardDialog dialog = new
				// NewAttachmentWizardDialog(attachmentsComposite.getShell(),
				// naw);
				// naw.setDialog(dialog);
				// dialog.create();
				// dialog.open();
			}
		});

		registerDropListener(section);
		registerDropListener(attachmentsComposite);
		registerDropListener(addAttachmentButton);
	}

	// protected ITaskDataHandler getOfflineTaskHandler() {
	// final AbstractRepositoryConnector connector =
	// TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
	// taskData.getRepositoryKind());
	// if (connector != null) {
	// return connector.getTaskDataHandler();
	// }
	// return null;
	// }

	private void registerDropListener(final Control control) {
		DropTarget target = new DropTarget(control, DND.DROP_COPY | DND.DROP_DEFAULT);
		final TextTransfer textTransfer = TextTransfer.getInstance();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		Transfer[] types = new Transfer[] { textTransfer, fileTransfer };
		target.setTransfer(types);

		// Adapted from eclipse.org DND Article by Veronika Irvine, IBM OTI Labs
		// http://www.eclipse.org/articles/Article-SWT-DND/DND-in-SWT.html#_dt10D
		target.addDropListener(new DropTargetListener() {
			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
				// will accept text but prefer to have files dropped
				for (int i = 0; i < event.dataTypes.length; i++) {
					if (fileTransfer.isSupportedType(event.dataTypes[i])) {
						event.currentDataType = event.dataTypes[i];
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
				// if (textTransfer.isSupportedType(event.currentDataType)) {
				// // NOTE: on unsupported platforms this will return null
				// Object o = textTransfer.nativeToJava(event.currentDataType);
				// String t = (String)o;
				// if (t != null) System.out.println(t);
				// }
			}

			public void dragOperationChanged(DropTargetEvent event) {
				if ((event.detail == DND.DROP_DEFAULT) || (event.operations & DND.DROP_COPY) != 0) {

					event.detail = DND.DROP_COPY;
				} else {
					event.detail = DND.DROP_NONE;
				}

				// allow text to be moved but files should only be copied
				if (fileTransfer.isSupportedType(event.currentDataType)) {
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
				if (textTransfer.isSupportedType(event.currentDataType)) {
					String text = (String) event.data;
					ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
							AbstractRepositoryTask.getHandle(repository.getUrl(), taskData.getId()));
					if (!(task instanceof AbstractRepositoryTask)) {
						// Should not happen
						return;
					}

					NewAttachmentWizard naw = new NewAttachmentWizard(repository, (AbstractRepositoryTask) task, text);
					NewAttachmentWizardDialog dialog = new NewAttachmentWizardDialog(control.getShell(), naw);
					naw.setDialog(dialog);
					dialog.create();
					dialog.open();
				}
				if (fileTransfer.isSupportedType(event.currentDataType)) {
					String[] files = (String[]) event.data;
					if (files.length > 0) {
						ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
								AbstractRepositoryTask.getHandle(repository.getUrl(), taskData.getId()));
						if (!(task instanceof AbstractRepositoryTask)) {
							// Should not happen
							return;
						}

						NewAttachmentWizard naw = new NewAttachmentWizard(repository, (AbstractRepositoryTask) task,
								new File(files[0]));
						NewAttachmentWizardDialog dialog = new NewAttachmentWizardDialog(control.getShell(), naw);
						naw.setDialog(dialog);
						dialog.create();
						dialog.open();
					}
				}
			}
		});
	}

	// public static InputStream getAttachmentInputStream(String url) {
	// URLConnection urlConnect;
	// InputStream stream = null;
	// try {
	// urlConnect = (new URL(url)).openConnection();
	// urlConnect.connect();
	// stream = urlConnect.getInputStream();
	//
	// } catch (MalformedURLException e) {
	// MylarStatusHandler.fail(e, "Attachment url was malformed.", false);
	// } catch (IOException e) {
	// MylarStatusHandler.fail(e, "I/O Error occurred reading attachment.",
	// false);
	// }
	// return stream;
	// }

	protected void createDescriptionLayout(Composite composite) {
		final Section section = createSection(composite, LABEL_SECTION_DESCRIPTION);

		final Composite sectionComposite = toolkit.createComposite(section);
		section.setClient(sectionComposite);
		GridLayout addCommentsLayout = new GridLayout();
		addCommentsLayout.numColumns = 1;
		sectionComposite.setLayout(addCommentsLayout);
		GridData sectionCompositeData = new GridData(GridData.FILL_HORIZONTAL);
		sectionComposite.setLayoutData(sectionCompositeData);

		// descriptionTextViewer = addRepositoryTextViewer(repository,
		// sectionComposite, taskData
		// .getDescription(), SWT.MULTI | SWT.WRAP);
		// final StyledText styledText = descriptionTextViewer.getTextWidget();
		// styledText.addListener(SWT.FocusIn, new DescriptionListener());
		// styledText.setLayout(new GridLayout());
		// GridDataFactory.fillDefaults().hint(DESCRIPTION_WIDTH,
		// SWT.DEFAULT).applyTo(styledText);
		//
		// texts.add(textsindex, styledText);
		// textHash.put(taskData.getDescription(), styledText);
		// textsindex++;

		RepositoryTaskAttribute attribute = taskData.getDescriptionAttribute();
		if (attribute != null && !attribute.isReadOnly()) {
			descriptionTextViewer = addTextEditor(repository, sectionComposite, taskData.getDescription(), true,
					SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			descriptionTextViewer.setEditable(true);
			StyledText styledText = descriptionTextViewer.getTextWidget();
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.widthHint = DESCRIPTION_WIDTH;
			gd.heightHint = SWT.DEFAULT;
			gd.grabExcessHorizontalSpace = true;
			descriptionTextViewer.getControl().setLayoutData(gd);
			descriptionTextViewer.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			descriptionTextViewer.getTextWidget().addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					String sel = descriptionTextViewer.getTextWidget().getText();
					if (!(taskData.getDescription().equals(sel))) {
						taskData.setDescription(sel);
						markDirty(true);
					}
					validateInput();
				}
			});
			textHash.put(taskData.getDescription(), styledText);
		} else {
			String text = taskData.getDescription();
			descriptionTextViewer = addTextViewer(repository, sectionComposite, text, SWT.MULTI | SWT.WRAP);
			StyledText styledText = descriptionTextViewer.getTextWidget();
			GridDataFactory.fillDefaults().hint(DESCRIPTION_WIDTH, SWT.DEFAULT).applyTo(
					descriptionTextViewer.getControl());

			textHash.put(text, styledText);
		}

		if (hasChanged(taskData.getAttribute(RepositoryTaskAttribute.DESCRIPTION))) {
			descriptionTextViewer.getTextWidget().setBackground(backgroundIncoming);
		}
		descriptionTextViewer.getTextWidget().addListener(SWT.FocusIn, new DescriptionListener());

		toolkit.paintBordersFor(sectionComposite);

	}

	protected void createCustomAttributeLayout(Composite composite) {
		// override
	}

	protected void createPeopleLayout(Composite composite) {
		FormToolkit toolkit = getManagedForm().getToolkit();
		Section peopleSection = createSection(composite, SECTION_TITLE_PEOPLE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(peopleSection);
		Composite peopleComposite = toolkit.createComposite(peopleSection);
		GridLayout layout = new GridLayout(2, false);
		layout.marginRight = 5;
		peopleComposite.setLayout(layout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(peopleComposite);

		Label label = toolkit.createLabel(peopleComposite, "Assigned to:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.DEFAULT).applyTo(label);
		Composite textFieldComposite = toolkit.createComposite(peopleComposite);
		GridLayout textLayout = new GridLayout();
		textLayout.marginWidth = 1;
		textLayout.verticalSpacing = 0;
		textLayout.marginHeight = 0;
		textLayout.marginRight = 5;
		textFieldComposite.setLayout(textLayout);
		Text textField = createTextField(textFieldComposite, taskData
				.getAttribute(RepositoryTaskAttribute.USER_ASSIGNED), SWT.FLAT | SWT.READ_ONLY);

		GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).applyTo(textField);

		label = toolkit.createLabel(peopleComposite, "Reporter:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.DEFAULT).applyTo(label);
		textFieldComposite = toolkit.createComposite(peopleComposite);
		textLayout = new GridLayout();
		textLayout.marginWidth = 1;
		textLayout.verticalSpacing = 0;
		textLayout.marginHeight = 0;
		textFieldComposite.setLayout(textLayout);
		textField = createTextField(textFieldComposite, taskData.getAttribute(RepositoryTaskAttribute.USER_REPORTER),
				SWT.FLAT | SWT.READ_ONLY);

		GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).applyTo(textField);
		addSelfToCC(peopleComposite);

		addCCList(peopleComposite);
		getManagedForm().getToolkit().paintBordersFor(peopleComposite);
		peopleSection.setClient(peopleComposite);
	}

	protected void addCCList(Composite attributesComposite) {
		// newLayout(attributesComposite, 1, "Add CC:", PROPERTY);
		FormToolkit toolkit = getManagedForm().getToolkit();
		Label label = toolkit.createLabel(attributesComposite, "Add CC:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.DEFAULT).applyTo(label);
		ccText = toolkit.createText(attributesComposite, taskData.getAttributeValue(RepositoryTaskAttribute.NEW_CC));
		ccText.setFont(TEXT_FONT);
		ccText.setEditable(true);
		// ccText.setForeground(foreground);
		// ccText.setBackground(background);
		GridData ccData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		ccData.horizontalSpan = 1;
		ccData.widthHint = 150;
		ccText.setLayoutData(ccData);
		// ccText.setText(ccValue);
		ccText.addListener(SWT.FocusIn, new GenericListener());
		ccText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				markDirty(true);
				taskData.setAttributeValue(RepositoryTaskAttribute.NEW_CC, ccText.getText());
			}
		});

		// newLayout(attributesComposite, 1, "CC: (Select to remove)",
		// PROPERTY);
		Label ccListLabel = toolkit.createLabel(attributesComposite, "CC:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.TOP).applyTo(ccListLabel);
		ccList = new org.eclipse.swt.widgets.List(attributesComposite, SWT.MULTI | SWT.V_SCROLL);// SWT.BORDER
		ccList.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		ccList.setFont(TEXT_FONT);
		GridData ccListData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		ccListData.horizontalSpan = 1;
		ccListData.widthHint = 150;
		ccListData.heightHint = 95;
		ccList.setLayoutData(ccListData);
		if (hasChanged(taskData.getAttribute(RepositoryTaskAttribute.USER_CC))) {
			ccList.setBackground(backgroundIncoming);
		}
		java.util.List<String> ccs = taskData.getCC();
		if (ccs != null) {
			for (Iterator<String> it = ccs.iterator(); it.hasNext();) {
				String cc = it.next();
				ccList.add(cc);
			}
		}
		java.util.List<String> removedCCs = taskData.getAttributeValues(RepositoryTaskAttribute.REMOVE_CC);
		if (removedCCs != null) {
			for (String item : removedCCs) {
				int i = ccList.indexOf(item);
				if (i != -1) {
					ccList.select(i);
				}
			}
		}
		ccList.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				markDirty(true);

				for (String cc : ccList.getItems()) {
					int index = ccList.indexOf(cc);
					if (ccList.isSelected(index)) {
						taskData.addAttributeValue(RepositoryTaskAttribute.REMOVE_CC, cc);
					} else {
						taskData.removeAttributeValue(RepositoryTaskAttribute.REMOVE_CC, cc);
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		ccList.addListener(SWT.FocusIn, new GenericListener());
		toolkit.createLabel(attributesComposite, "");
		label = toolkit.createLabel(attributesComposite, "(Select to remove)");
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.DEFAULT).applyTo(label);

	}

	/**
	 * A listener for selection of the description field.
	 */
	protected class DescriptionListener implements Listener {
		public void handleEvent(Event event) {
			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
					new RepositoryTaskSelection(taskData.getId(), taskData.getRepositoryUrl(),
							LABEL_SECTION_DESCRIPTION, true, taskData.getSummary()))));
		}
	}

	protected void createCommentLayout(Composite composite) {

		commentsSection = createSection(composite, LABEL_SECTION_COMMENTS + " (" + taskData.getComments().size() + ")");

		ImageHyperlink hyperlink = toolkit.createImageHyperlink(commentsSection, SWT.NONE);
		hyperlink.setBackgroundMode(SWT.INHERIT_NONE);
		hyperlink.setBackground(commentsSection.getTitleBarBackground());
		hyperlink.setImage(TaskListImages.getImage(TaskListImages.EXPAND_ALL));
		hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				revealAllComments();
			}
		});

		commentsSection.setTextClient(hyperlink);

		// Additional (read-only) Comments Area
		Composite addCommentsComposite = toolkit.createComposite(commentsSection);
		commentsSection.setClient(addCommentsComposite);
		GridLayout addCommentsLayout = new GridLayout();
		addCommentsLayout.numColumns = 1;
		addCommentsComposite.setLayout(addCommentsLayout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(addCommentsComposite);
		AbstractRepositoryTask repositoryTask = null;
		ITaskDataHandler offlineHandler = null;
		IEditorInput input = this.getEditorInput();
		if (input instanceof RepositoryTaskEditorInput) {
			RepositoryTaskEditorInput existingInput = (RepositoryTaskEditorInput) input;
			repositoryTask = existingInput.getRepositoryTask();

			AbstractRepositoryConnector connector = (AbstractRepositoryConnector) TasksUiPlugin.getRepositoryManager()
					.getRepositoryConnector(taskData.getRepositoryKind());
			offlineHandler = connector.getTaskDataHandler();
		}
		StyledText styledText = null;
		for (Iterator<TaskComment> it = taskData.getComments().iterator(); it.hasNext();) {
			final TaskComment taskComment = it.next();

			final ExpandableComposite expandableComposite = toolkit.createExpandableComposite(addCommentsComposite,
					ExpandableComposite.TREE_NODE | ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT);

			final ImageHyperlink replyLink = toolkit.createImageHyperlink(expandableComposite, SWT.NONE);
			replyLink.setImage(TaskListImages.getImage(TaskListImages.REPLY));
			replyLink.setToolTipText("Reply");
			replyLink.addHyperlinkListener(new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					String oldText = newCommentTextViewer.getDocument().get();
					StringBuilder strBuilder = new StringBuilder();
					strBuilder.append(oldText);
					if (strBuilder.length() != 0) {
						strBuilder.append("\n");
					}
					strBuilder.append(" (In reply to comment #" + taskComment.getNumber() + ")\n");
					CommentQuoter quoter = new CommentQuoter();
					strBuilder.append(quoter.quote(taskComment.getText()));
					newCommentTextViewer.getDocument().set(strBuilder.toString());
					selectNewComment();
					newCommentTextViewer.getTextWidget().setCaretOffset(strBuilder.length());
				}
			});

			expandableComposite.addExpansionListener(new ExpansionAdapter() {

				@Override
				public void expansionStateChanged(ExpansionEvent e) {
					replyLink.setVisible(expandableComposite.isExpanded());
				}
			});

			expandableComposite.setTextClient(replyLink);

			// Expand new comments
			if (repositoryTask != null && offlineHandler != null) {
				Date lastModDate = offlineHandler.getDateForAttributeType(RepositoryTaskAttribute.DATE_MODIFIED,
						repositoryTask.getLastSyncDateStamp());

				if (lastModDate != null) {
					// reduce granularity to minutes
					Calendar calLastMod = Calendar.getInstance();
					calLastMod.setTime(lastModDate);
					calLastMod.set(Calendar.SECOND, 0);

					Date commentDate = offlineHandler.getDateForAttributeType(RepositoryTaskAttribute.COMMENT_DATE,
							taskComment.getCreated());
					if (commentDate != null
							&& (commentDate.after(calLastMod.getTime()) || commentDate.equals(calLastMod.getTime()))) {
						expandableComposite.setExpanded(true);
						expandableComposite.setBackground(backgroundIncoming);
						if (expandableComposite.getTextClient() != null) {
							expandableComposite.getTextClient().setBackground(backgroundIncoming);
						}
					}
				}
			} else if (repositoryTask != null && repositoryTask.getLastSyncDateStamp() == null && !it.hasNext()) {
				// no task data (query hit?) so expand last comment
				expandableComposite.setExpanded(true);
			}

			expandableComposite.setText(taskComment.getNumber() + ": " + taskComment.getAuthorName() + ", "
					+ taskComment.getCreated());

			expandableComposite.addExpansionListener(new ExpansionAdapter() {
				public void expansionStateChanged(ExpansionEvent e) {
					form.reflow(true);
				}
			});

			replyLink.setVisible(expandableComposite.isExpanded());
			expandableComposite.setLayout(new GridLayout());
			expandableComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Composite ecComposite = toolkit.createComposite(expandableComposite);
			GridLayout ecLayout = new GridLayout();
			ecLayout.marginHeight = 0;
			ecLayout.marginBottom = 10;
			ecLayout.marginLeft = 10;
			ecComposite.setLayout(ecLayout);
			ecComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			expandableComposite.setClient(ecComposite);

			TextViewer viewer = addTextViewer(repository, ecComposite, taskComment.getText(), SWT.MULTI | SWT.WRAP);
			// viewer.getControl().setBackground(new
			// Color(expandableComposite.getDisplay(), 123, 34, 155));
			styledText = viewer.getTextWidget();
			GridDataFactory.fillDefaults().hint(DESCRIPTION_WIDTH, SWT.DEFAULT).applyTo(styledText);
			// GridDataFactory.fillDefaults().hint(DESCRIPTION_WIDTH,
			// SWT.DEFAULT).applyTo(viewer.getControl());

			// code for outline
			commentStyleText.add(styledText);
			textHash.put(taskComment, styledText);
		}
		if (taskData.getComments() == null || taskData.getComments().size() == 0) {
			commentsSection.setExpanded(false);
		} else if (editorInput.getTaskData() != null && editorInput.getOldTaskData() != null) {
			List<TaskComment> newTaskComments = editorInput.getTaskData().getComments();
			List<TaskComment> oldTaskComments = editorInput.getOldTaskData().getComments();
			if (newTaskComments == null || oldTaskComments == null) {
				commentsSection.setExpanded(true);
			} else {
				commentsSection.setExpanded(newTaskComments.size() != oldTaskComments.size());
			}
		}

	}

	protected void createNewCommentLayout(Composite composite) {
		Section section = createSection(composite, LABEL_SECTION_NEW_COMMENT);

		Composite newCommentsComposite = toolkit.createComposite(section);
		newCommentsComposite.setLayout(new GridLayout());

		newCommentTextViewer = addTextEditor(repository, newCommentsComposite, taskData.getNewComment(), true, SWT.FLAT
				| SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		newCommentTextViewer.setEditable(true);

		GridData addCommentsTextData = new GridData(GridData.FILL_HORIZONTAL);
		addCommentsTextData.widthHint = DESCRIPTION_WIDTH;
		addCommentsTextData.heightHint = DESCRIPTION_HEIGHT;
		addCommentsTextData.grabExcessHorizontalSpace = true;
		newCommentTextViewer.getControl().setLayoutData(addCommentsTextData);
		newCommentTextViewer.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

		newCommentTextViewer.getTextWidget().addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String sel = addCommentsTextBox.getText();
				if (!(taskData.getNewComment().equals(sel))) {
					taskData.setNewComment(sel);
					markDirty(true);
				}
				validateInput();
			}
		});

		newCommentTextViewer.getTextWidget().addListener(SWT.FocusIn, new NewCommentListener());
		addCommentsTextBox = newCommentTextViewer.getTextWidget();

		section.setClient(newCommentsComposite);

		toolkit.paintBordersFor(newCommentsComposite);

	}

	/**
	 * Creates the button layout. This displays options and buttons at the
	 * bottom of the editor to allow actions to be performed on the bug.
	 */
	protected void createActionsLayout(Composite composite) {
		Section section = createSection(composite, LABEL_SECTION_ACTIONS);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, true).applyTo(section);
		Composite buttonComposite = toolkit.createComposite(section);
		GridLayout buttonLayout = new GridLayout();
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(buttonComposite);
		buttonLayout.numColumns = 4;
		buttonComposite.setLayout(buttonLayout);
		addRadioButtons(buttonComposite);
		addActionButtons(buttonComposite);
		section.setClient(buttonComposite);
	}

	protected Section createSection(Composite composite, String title) {
		Section section = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText(title);
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				form.reflow(true);
			}

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		return section;
	}

	/**
	 * Adds buttons to this composite. Subclasses can override this method to
	 * provide different/additional buttons.
	 * 
	 * @param buttonComposite
	 *            Composite to add the buttons to.
	 */
	protected void addActionButtons(Composite buttonComposite) {
		ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
				AbstractRepositoryTask.getHandle(repository.getUrl(), taskData.getId()));
		if (task != null) {
			addAttachContextButton(buttonComposite, task);
		}
		submitButton = toolkit.createButton(buttonComposite, LABEL_BUTTON_SUBMIT, SWT.NONE);
		GridData submitButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		submitButtonData.horizontalSpan = 3;
		submitButton.setLayoutData(submitButtonData);
		submitButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				submitToRepository();
			}
		});
		submitButton.addListener(SWT.FocusIn, new GenericListener());
		submitButton.setToolTipText("Submit to " + this.repository.getUrl());

		//toolkit.createLabel(buttonComposite, "");
		if (getActivityUrl() != null) {
			Hyperlink hyperlink = toolkit.createHyperlink(buttonComposite, "View past activity", SWT.NONE);
			hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					if (AbstractRepositoryTaskEditor.this.getEditor() instanceof TaskEditor) {
						TaskEditor mylarTaskEditor = (TaskEditor) AbstractRepositoryTaskEditor.this.getEditor();
						mylarTaskEditor.displayInBrowser(getActivityUrl());
					}
				}
			});
			GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).align(SWT.RIGHT, SWT.DEFAULT).applyTo(hyperlink);
		}
	}

	/**
	 * Override to make hyperlink available. If not overridden hyperlink will
	 * simply not be displayed.
	 * 
	 * @return url String form of url that points to task's past activity
	 */
	protected String getActivityUrl() {
		return null;
	}

	/**
	 * Make sure that a String that is <code>null</code> is changed to a null
	 * string
	 * 
	 * @param text
	 *            The text to check if it is null or not
	 * @return If the text is <code>null</code>, then return the null string (<code>""</code>).
	 *         Otherwise, return the text.
	 */
	public static String checkText(String text) {
		if (text == null)
			return "";
		else
			return text;
	}

	/**
	 * This refreshes the text in the title label of the info area (it contains
	 * elements which can change).
	 */
	protected void setGeneralTitleText() {
		// String text = "[Open in Internal Browser]";
		// linkToBug.setText(text);
		// linkToBug.setFont(TEXT_FONT);
		// if (this instanceof ExistingBugEditor) {
		// linkToBug.setUnderlined(true);
		// linkToBug.setForeground(JFaceColors.getHyperlinkText(Display.getCurrent()));
		// linkToBug.addMouseListener(new MouseListener() {
		//
		// public void mouseDoubleClick(MouseEvent e) {
		// }
		//
		// public void mouseUp(MouseEvent e) {
		// }
		//
		// public void mouseDown(MouseEvent e) {
		// TaskListUiUtil.openUrl(getTitle(), getTitleToolTip(),
		// BugzillaRepositoryUtil.getBugUrlWithoutLogin(
		// bugzillaInput.getBug().getRepositoryUrl(),
		// bugzillaInput.getBug().getId()));
		// if (e.stateMask == SWT.MOD3) {
		// // XXX come back to look at this ui
		// close();
		// }
		//
		// }
		// });
		// } else {
		// linkToBug.setEnabled(false);
		// }
		// linkToBug.addListener(SWT.FocusIn, new GenericListener());
		//
		// // Resize the composite, in case the new summary is longer than the
		// // previous one.
		// // Then redraw it to show the changes.
		// linkToBug.getParent().pack(true);
		// linkToBug.redraw();

		// String text = getTitleString();
		// generalTitleText.setText(text);
		// StyleRange sr = new StyleRange(generalTitleText.getOffsetAtLine(0),
		// text.length(), foreground, background,
		// SWT.BOLD);
		// generalTitleText.setStyleRange(sr);
		// generalTitleText.addListener(SWT.FocusIn, new GenericListener());
		//
		// // Resize the composite, in case the new summary is longer than the
		// // previous one.
		// // Then redraw it to show the changes.
		// generalTitleText.getParent().pack(true);
		// generalTitleText.redraw();
	}

	public void saveTaskOffline(IProgressMonitor progressMonitor) {
		if (progressMonitor == null) {
			progressMonitor = new NullProgressMonitor();
		}
		try {
			progressMonitor.beginTask("Saving...", IProgressMonitor.UNKNOWN);
			TasksUiPlugin.getDefault().getTaskDataManager().save();
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Saving of offline task data failed", true);
		} finally {
			progressMonitor.done();
		}

	}

	/**
	 * Refreshes any text labels in the editor that contain information that
	 * might change.
	 */
	protected void updateEditor() {
		setGeneralTitleText();
	}

	@Override
	public void setFocus() {
		if (summaryText != null && !summaryText.isDisposed()) {
			summaryText.setFocus();
		} else {
			form.setFocus();
		}
	}

	// /**
	// * Updates the dirty status of this editor page. The dirty status is true
	// if
	// * the bug report has been modified but not saved. The title of the editor
	// * is also updated to reflect the status.
	// *
	// * @param newDirtyStatus
	// * is true when the bug report has been modified but not saved
	// */
	// public void changeDirtyStatus(boolean newDirtyStatus) {
	// isDirty = newDirtyStatus;
	// // if (parentEditor == null) {
	// // firePropertyChange(PROP_DIRTY);
	// // } else {
	// // parentEditor.markDirty();
	// // }
	// getManagedForm().dirtyStateChanged();
	// }

	/**
	 * Updates the title of the editor to reflect dirty status. If the bug
	 * report has been modified but not saved, then an indicator will appear in
	 * the title.
	 */
	protected void updateEditorTitle() {
		setPartName(editorInput.getName());
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		updateTask();
		updateEditor();
		saveTaskOffline(monitor);
		markDirty(false);
	}

	@Override
	public void doSaveAs() {
		// we don't save, so no need to implement
	}

	/**
	 * @return The composite for the whole editor.
	 */
	public Composite getEditorComposite() {
		return editorComposite;
	}

	@Override
	public void dispose() {
		super.dispose();
		isDisposed = true;
		getSite().getPage().removeSelectionListener(selectionListener);
	}

	// public void handleEvent(Event event) {
	// if (event.widget instanceof CCombo) {
	// CCombo combo = (CCombo) event.widget;
	// if (comboListenerMap.containsKey(combo)) {
	// if (combo.getSelectionIndex() > -1) {
	// String sel = combo.getItem(combo.getSelectionIndex());
	// Attribute attribute = getBug().getAttribute(comboListenerMap.get(combo));
	// if (sel != null && !(sel.equals(attribute.getNewValue()))) {
	// attribute.setNewValue(sel);
	// for (IRepositoryTaskAttributeListener client : attributesListeners) {
	// client.attributeChanged(attribute.getName(), sel);
	// }
	// changeDirtyStatus(true);
	// }
	// }
	// }
	// }
	// }

	/**
	 * Fires a <code>SelectionChangedEvent</code> to all listeners registered
	 * under <code>selectionChangedListeners</code>.
	 * 
	 * @param event
	 *            The selection event.
	 */
	protected void fireSelectionChanged(final SelectionChangedEvent event) {
		Object[] listeners = selectionChangedListeners.toArray();
		for (int i = 0; i < listeners.length; i++) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}

	/**
	 * A generic listener for selection of unimportant items. The default
	 * selection item sent out is the entire bug object.
	 */
	public class GenericListener implements Listener {
		public void handleEvent(Event event) {
			RepositoryTaskData bug = taskData;
			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
					new RepositoryTaskSelection(bug.getId(), bug.getRepositoryUrl(), bug.getLabel(), false, bug
							.getSummary()))));
		}
	}

	/**
	 * A listener to check if the summary field was modified.
	 */
	protected class SummaryListener implements Listener {
		public void handleEvent(Event event) {
			handleSummaryEvent();
		}
	}

	/**
	 * Check if the summary field was modified, and update it if necessary.
	 */
	public void handleSummaryEvent() {
		String sel = summaryText.getText();
		RepositoryTaskAttribute a = taskData.getAttribute(RepositoryTaskAttribute.SUMMARY);
		if (!(a.getValue().equals(sel))) {
			a.setValue(sel);
			markDirty(true);
		}
	}

	/*----------------------------------------------------------*
	 * CODE TO SCROLL TO A COMMENT OR OTHER PIECE OF TEXT
	 *----------------------------------------------------------*/

	/** List of the StyledText's so that we can get the previous and the next */
	// protected ArrayList<StyledText> texts = new ArrayList<StyledText>();
	protected HashMap<Object, StyledText> textHash = new HashMap<Object, StyledText>();

	protected List<StyledText> commentStyleText = new ArrayList<StyledText>();

	/** Index into the styled texts */
	protected int textsindex = 0;

	protected StyledText addCommentsTextBox = null;

	// protected Text descriptionTextBox = null;
	protected TextViewer descriptionTextViewer = null;

	// private FormText previousText = null;

	/**
	 * Selects the given object in the editor.
	 * 
	 * @param commentNumber
	 *            The comment number to be selected
	 */
	public void select(int commentNumber) {
		if (commentNumber == -1)
			return;

		for (Object o : textHash.keySet()) {
			if (o instanceof TaskComment) {
				if (((TaskComment) o).getNumber() == commentNumber) {
					select(o, true);
				}
			}
		}
	}

	public void revealAllComments() {
		if (commentsSection != null) {
			commentsSection.setExpanded(true);
		}
		for (StyledText text : commentStyleText) {
			Composite comp = text.getParent();
			while (comp != null) {
				if (comp instanceof ExpandableComposite) {
					ExpandableComposite ex = (ExpandableComposite) comp;
					ex.setExpanded(true);
					break;
				}
				comp = comp.getParent();
			}
		}
		form.reflow(true);
	}

	/**
	 * Selects the given object in the editor.
	 * 
	 * @param o
	 *            The object to be selected.
	 * @param highlight
	 *            Whether or not the object should be highlighted.
	 */
	public void select(Object o, boolean highlight) {
		if (textHash.containsKey(o)) {
			StyledText t = textHash.get(o);
			if (t != null) {
				Composite comp = t.getParent();
				while (comp != null) {
					if (comp instanceof ExpandableComposite) {
						ExpandableComposite ex = (ExpandableComposite) comp;
						ex.setExpanded(true);
					}
					comp = comp.getParent();
				}
				focusOn(t, highlight);
			}
		} else if (o instanceof RepositoryTaskData) {
			focusOn(null, highlight);
		}
	}

	// public void selectDescription() {
	// for (Object o : textHash.keySet()) {
	// if (o.equals(editorInput.taskData.getDescription())) {
	// select(o, true);
	// }
	// }
	// }

	public void selectNewComment() {
		focusOn(addCommentsTextBox, false);
	}

	public void selectDescription() {
		focusOn(descriptionTextViewer.getTextWidget(), false);
	}

	/**
	 * Scroll to a specified piece of text
	 * 
	 * @param selectionComposite
	 *            The StyledText to scroll to
	 */
	private void focusOn(Control selectionComposite, boolean highlight) {
		int pos = 0;
		// if (previousText != null && !previousText.isDisposed()) {
		// previousText.setsetSelection(0);
		// }

		// if (selectionComposite instanceof FormText)
		// previousText = (FormText) selectionComposite;

		if (selectionComposite != null) {

			// if (highlight && selectionComposite instanceof FormText &&
			// !selectionComposite.isDisposed())
			// ((FormText) selectionComposite).set.setSelection(0, ((FormText)
			// selectionComposite).getText().length());

			// get the position of the text in the composite
			pos = 0;
			Control s = selectionComposite;
			if (s.isDisposed())
				return;
			s.setEnabled(true);
			s.setFocus();
			s.forceFocus();
			while (s != null && s != getEditorComposite()) {
				if (!s.isDisposed()) {
					pos += s.getLocation().y;
					s = s.getParent();
				}
			}

			pos = pos - 60; // form.getOrigin().y;

		}
		if (!form.getBody().isDisposed())
			form.setOrigin(0, pos);
	}

	private RepositoryTaskOutlinePage outlinePage = null;

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		return getAdapterDelgate(adapter);
	}

	public Object getAdapterDelgate(Class<?> adapter) {
		if (IContentOutlinePage.class.equals(adapter)) {
			if (outlinePage == null && editorInput != null) {
				outlinePage = new RepositoryTaskOutlinePage(taskOutlineModel);
			}
			return outlinePage;
		}
		return super.getAdapter(adapter);
	}

	public RepositoryTaskOutlineNode getOutlineModel() {
		return taskOutlineModel;
	}

	public RepositoryTaskOutlinePage getOutline() {
		return outlinePage;
	}

	private boolean isDisposed = false;

	protected Button[] radios;

	protected Control[] radioOptions;

	protected Button attachContextButton;

	public AbstractRepositoryConnector connector;

	public boolean isDisposed() {
		return isDisposed;
	}

	public void close() {
		Display activeDisplay = getSite().getShell().getDisplay();
		activeDisplay.asyncExec(new Runnable() {
			public void run() {
				if (getSite() != null && getSite().getPage() != null && !AbstractRepositoryTaskEditor.this.isDisposed())
					if (parentEditor != null) {
						getSite().getPage().closeEditor(parentEditor, false);
					} else {
						getSite().getPage().closeEditor(AbstractRepositoryTaskEditor.this, false);
					}
			}
		});
	}

	public void addAttributeListener(IRepositoryTaskAttributeListener listener) {
		attributesListeners.add(listener);
	}

	public void removeAttributeListener(IRepositoryTaskAttributeListener listener) {
		attributesListeners.remove(listener);
	}

	public void setParentEditor(TaskEditor parentEditor) {
		this.parentEditor = parentEditor;
	}

	public RepositoryTaskOutlineNode getTaskOutlineModel() {
		return taskOutlineModel;
	}

	public void setTaskOutlineModel(RepositoryTaskOutlineNode taskOutlineModel) {
		this.taskOutlineModel = taskOutlineModel;
	}

	/**
	 * A listener for selection of the textbox where a new comment is entered
	 * in.
	 */
	protected class NewCommentListener implements Listener {
		public void handleEvent(Event event) {
			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
					new RepositoryTaskSelection(taskData.getId(), taskData.getRepositoryUrl(), "New Comment", false,
							taskData.getSummary()))));
		}
	}

	public Control getControl() {
		return form;
	}

	public void setSummaryText(String text) {
		this.summaryText.setText(text);
		handleSummaryEvent();
	}

	public void setDescriptionText(String text) {
		this.descriptionTextViewer.getDocument().set(text);
	}

	protected void addRadioButtons(Composite buttonComposite) {
		FormToolkit toolkit = new FormToolkit(buttonComposite.getDisplay());
		int i = 0;
		Button selected = null;
		radios = new Button[taskData.getOperations().size()];
		radioOptions = new Control[taskData.getOperations().size()];
		for (Iterator<RepositoryOperation> it = taskData.getOperations().iterator(); it.hasNext();) {
			RepositoryOperation o = it.next();
			radios[i] = toolkit.createButton(buttonComposite, "", SWT.RADIO);
			radios[i].setFont(TEXT_FONT);
			GridData radioData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			if (!o.hasOptions() && !o.isInput())
				radioData.horizontalSpan = 4;
			else
				radioData.horizontalSpan = 3;
			radioData.heightHint = 20;
			String opName = o.getOperationName();
			opName = opName.replaceAll("</.*>", "");
			opName = opName.replaceAll("<.*>", "");
			radios[i].setText(opName);
			radios[i].setLayoutData(radioData);
			// radios[i].setBackground(background);
			radios[i].addSelectionListener(new RadioButtonListener());
			radios[i].addListener(SWT.FocusIn, new GenericListener());

			if (o.hasOptions()) {
				radioData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
				radioData.horizontalSpan = 1;
				radioData.heightHint = 20;
				radioData.widthHint = AbstractRepositoryTaskEditor.RADIO_OPTION_WIDTH;
				// radioOptions[i] = new Combo(buttonComposite, SWT.NULL);
				radioOptions[i] = new CCombo(buttonComposite, SWT.FLAT | SWT.READ_ONLY);
				toolkit.adapt(radioOptions[i], true, true);
				// radioOptions[i] = new Combo(buttonComposite, SWT.MULTI |
				// SWT.V_SCROLL | SWT.READ_ONLY);
				// radioOptions[i].setData(FormToolkit.KEY_DRAW_BORDER,
				// FormToolkit.TEXT_BORDER);
				// radioOptions[i] = new Combo(buttonComposite,
				// SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL
				// | SWT.READ_ONLY);
				radioOptions[i].setFont(TEXT_FONT);
				radioOptions[i].setLayoutData(radioData);
				// radioOptions[i].setBackground(background);

				Object[] a = o.getOptionNames().toArray();
				Arrays.sort(a);
				for (int j = 0; j < a.length; j++) {
					((CCombo) radioOptions[i]).add((String) a[j]);
				}
				((CCombo) radioOptions[i]).select(0);
				((CCombo) radioOptions[i]).addSelectionListener(new RadioButtonListener());
			} else if (o.isInput()) {
				radioData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
				radioData.horizontalSpan = 1;
				radioData.widthHint = 120;

				// TODO: add condition for if opName = reassign to...
				String assignmentValue = "";
				if (opName.equals(REASSIGN_BUG_TO)) {
					assignmentValue = repository.getUserName();
				}
				radioOptions[i] = toolkit.createText(buttonComposite, assignmentValue);// ,
				// SWT.SINGLE);
				radioOptions[i].setFont(TEXT_FONT);
				radioOptions[i].setLayoutData(radioData);
				// radioOptions[i].setBackground(background);
				((Text) radioOptions[i]).setText(o.getInputValue());
				((Text) radioOptions[i]).addModifyListener(new RadioButtonListener());
			}

			if (i == 0 || o.isChecked()) {
				if (selected != null)
					selected.setSelection(false);
				selected = radios[i];
				radios[i].setSelection(true);
				if (o.hasOptions() && o.getOptionSelection() != null) {
					int j = 0;
					for (String s : ((CCombo) radioOptions[i]).getItems()) {
						if (s.compareTo(o.getOptionSelection()) == 0) {
							((CCombo) radioOptions[i]).select(j);
						}
						j++;
					}
				}
				taskData.setSelectedOperation(o);
			}

			i++;
		}

		toolkit.paintBordersFor(buttonComposite);
	}

	/**
	 * If implementing custom attributes you may need to override this method
	 * 
	 * @return true if one or more attributes exposed in the editor have changed
	 */
	protected boolean hasVisibleAttributeChanges() {
		if (taskData == null)
			return false;
		for (RepositoryTaskAttribute attribute : taskData.getAttributes()) {
			if (!attribute.isHidden()) {
				if (hasChanged(attribute)) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean hasChanged(RepositoryTaskAttribute newAttribute) {
		if (newAttribute == null)
			return false;
		RepositoryTaskData oldTaskData = editorInput.getOldTaskData();
		if (oldTaskData == null)
			return false;
		RepositoryTaskAttribute oldAttribute = oldTaskData.getAttribute(newAttribute.getID());
		if (oldAttribute == null)
			return true;
		if (oldAttribute.getValue() != null && !oldAttribute.getValue().equals(newAttribute.getValue())) {
			return true;
		} else if (oldAttribute.getValues() != null && !oldAttribute.getValues().equals(newAttribute.getValues())) {
			return true;
		}
		return false;
	}

	protected void addAttachContextButton(Composite buttonComposite, ITask task) {
		attachContextButton = toolkit.createButton(buttonComposite, "Attach Context", SWT.CHECK);
		attachContextButton.setImage(TaskListImages.getImage(TaskListImages.CONTEXT_ATTACH));
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 4;
		attachContextButton.setLayoutData(data);
	}

	/**
	 * Creates a check box for adding the repository user to the cc list. Does
	 * nothing if the repository does not have a valid username, the repository
	 * user is the assignee, reporter or already on the the cc list.
	 */
	protected void addSelfToCC(Composite composite) {
		if (repository.getUserName() == null) {
			return;
		}

		RepositoryTaskAttribute owner = taskData.getAttribute(RepositoryTaskAttribute.USER_ASSIGNED);
		if (owner != null && owner.getValue().indexOf(repository.getUserName()) != -1) {
			return;
		}

		RepositoryTaskAttribute reporter = taskData.getAttribute(RepositoryTaskAttribute.USER_REPORTER);
		if (reporter != null && reporter.getValue().indexOf(repository.getUserName()) != -1) {
			return;
		}

		RepositoryTaskAttribute ccAttribute = taskData.getAttribute(RepositoryTaskAttribute.USER_CC);
		if (ccAttribute != null && ccAttribute.getValues().contains(repository.getUserName())) {
			return;
		}

		FormToolkit toolkit = getManagedForm().getToolkit();
		toolkit.createLabel(composite, "");
		final Button addSelfButton = toolkit.createButton(composite, "Add me to CC", SWT.CHECK);
		addSelfButton.setSelection(RepositoryTaskAttribute.TRUE.equals(taskData
				.getAttributeValue(RepositoryTaskAttribute.ADD_SELF_CC)));
		addSelfButton.setImage(TaskListImages.getImage(TaskListImages.PERSON));
		addSelfButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (addSelfButton.getSelection()) {
					taskData.setAttributeValue(RepositoryTaskAttribute.ADD_SELF_CC, RepositoryTaskAttribute.TRUE);
				} else {
					taskData.setAttributeValue(RepositoryTaskAttribute.ADD_SELF_CC, RepositoryTaskAttribute.FALSE);
				}
				markDirty(true);
			}
		});
		// GridDataFactory.fillDefaults().span(2,
		// SWT.DEFAULT).applyTo(addSelfButton);
	}

	// // The implementation of the attach context UI is connector dependant.
	// protected boolean getAttachContext() {
	// return false;
	// }
	//
	// // The implementation of the attach context UI is connector dependant.
	// // this method is called when a user attaches a patch to the task
	// protected void setAttachContext(boolean attachContext) {
	//
	// }

	public boolean getAttachContext() {
		if (attachContextButton == null || attachContextButton.isDisposed()) {
			return false;
		} else {
			return attachContextButton.getSelection();
		}
	}

	public void setAttachContext(boolean attachContext) {
		if (attachContextButton != null && attachContextButton.isEnabled()) {
			attachContextButton.setSelection(attachContext);
		}
	}

	public void submitToRepository() {
		submitButton.setEnabled(false);
		showBusy(true);
		updateEditor();
		updateTask();
		if (isDirty()) {

			Job saveJob = new Job("Save") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					saveTaskOffline(monitor);
					return Status.OK_STATUS;
				}

			};
			saveJob.setSystem(true);
			saveJob.schedule();
			markDirty(false);
		}

		final boolean attachContext = getAttachContext();

		Job submitJob = new Job(LABEL_JOB_SUBMIT) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					String taskId = connector.getTaskDataHandler().postTaskData(repository, taskData);

					if (taskData.isNew()) {
						if (taskId != null) {
							modifiedTask = handleNewBugPost(taskId);
						} else {
							// null taskId, assume task could not be created...
							throw new CoreException(
									new MylarStatus(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID,
											IMylarStatusConstants.INTERNAL_ERROR,
											"Task could not be created. No additional information was provided by the connector."));
						}
					} else {
						modifiedTask = (AbstractRepositoryTask) TasksUiPlugin.getTaskListManager().getTaskList()
								.getTask(AbstractRepositoryTask.getHandle(repository.getUrl(), taskData.getId()));
					}

					// Attach context if required
					if (attachContext) {
						attachContext(modifiedTask);
					}

					// Synchronization accounting...
					if (modifiedTask != null) {
						// Mark as synchronized because the content DID
						// get submitted
						//
						// Since the task data is marked as having
						// local changes
						// the subsequent synchronization doesn't result
						// in INCOMING
						modifiedTask.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);

						TasksUiPlugin.getSynchronizationManager().synchronize(connector, modifiedTask, true,
								new JobChangeAdapter() {

									@Override
									public void done(IJobChangeEvent event) {
										close();
										TasksUiPlugin.getSynchronizationManager().setTaskRead(modifiedTask, true);
										TasksUiUtil.openEditor(modifiedTask, false);
									}
								});
						TasksUiPlugin.getSynchronizationScheduler().synchNow(0, Collections.singletonList(repository));
					} else {
						close();
						// For some reason the task wasn't retrieved.
						// Try to
						// open local then via web browser...
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								TasksUiUtil.openRepositoryTask(repository.getUrl(), taskData.getId(), connector
										.getTaskWebUrl(taskData.getRepositoryUrl(), taskData.getId()));
							}
						});
					}

					return Status.OK_STATUS;
				} catch (CoreException e) {
					handleSubmitError(e);
				} catch (Exception e) {
					MylarStatusHandler.fail(e, e.getMessage(), true);
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							enableButtons();
						}
					});
				}
				return Status.OK_STATUS;
			}

		};

		IJobChangeListener jobListener = getSubmitJobListener();
		if (jobListener != null) {
			submitJob.addJobChangeListener(jobListener);
		}
		submitJob.schedule();
	}

	protected IJobChangeListener getSubmitJobListener() {
		return null;
	}

	protected void attachContext(final AbstractRepositoryTask modifiedTask) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				IProgressService ps = PlatformUI.getWorkbench().getProgressService();
				try {
					ps.busyCursorWhile(new IRunnableWithProgress() {
						public void run(IProgressMonitor pm) {
							try {
								// TODO: pass progress monitor to handler
								connector.attachContext(repository, modifiedTask, "");
							} catch (Exception e) {
								MylarStatusHandler.fail(e, "Failed to attach task context.\n\n" + e.getMessage(), true);
							}
						}
					});
				} catch (InvocationTargetException e) {
					MylarStatusHandler.fail(e.getCause(), "Failed to attach task context.\n\n" + e.getMessage(), true);
				} catch (InterruptedException ignore) {
				}
			}
		});

	}

	protected AbstractTaskContainer getCategory() {
		return null;
	}

	protected IStatus handleSubmitError(final CoreException exception) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (exception.getStatus().getCode() == IMylarStatusConstants.REPOSITORY_COMMENT_REQD) {
					MylarStatusHandler.displayStatus("Comment required", exception.getStatus());
					if (!isDisposed && newCommentTextViewer != null && !newCommentTextViewer.getControl().isDisposed()) {
						newCommentTextViewer.getControl().setFocus();
					}
				} else {
					MylarStatusHandler.displayStatus("Submit failed", exception.getStatus());
				}
				enableButtons();
			}

		});
		return Status.OK_STATUS;
	}

	protected AbstractRepositoryTask handleNewBugPost(String postResult) throws CoreException {
		final AbstractRepositoryTask newTask = connector.createTaskFromExistingKey(repository, postResult);

		if (newTask != null) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (getCategory() != null) {
						TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer(getCategory(), newTask);

					}
				}
			});

		}

		return newTask;

	}

	private void enableButtons() {
		if (!isDisposed() && !submitButton.isDisposed()) {
			submitButton.setEnabled(true);
			showBusy(false);
		}
	}

	/**
	 * Class to handle the selection change of the radio buttons.
	 */
	private class RadioButtonListener implements SelectionListener, ModifyListener {

		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		public void widgetSelected(SelectionEvent e) {
			Button selected = null;
			for (int i = 0; i < radios.length; i++) {
				if (radios[i].getSelection())
					selected = radios[i];
			}
			// determine the operation to do to the bug
			for (int i = 0; i < radios.length; i++) {
				if (radios[i] != e.widget && radios[i] != selected) {
					radios[i].setSelection(false);
				}

				if (e.widget == radios[i]) {
					RepositoryOperation o = taskData.getOperation(radios[i].getText());
					taskData.setSelectedOperation(o);
					markDirty(true);
				} else if (e.widget == radioOptions[i]) {
					RepositoryOperation o = taskData.getOperation(radios[i].getText());
					o.setOptionSelection(((CCombo) radioOptions[i]).getItem(((CCombo) radioOptions[i])
							.getSelectionIndex()));

					if (taskData.getSelectedOperation() != null)
						taskData.getSelectedOperation().setChecked(false);
					o.setChecked(true);

					taskData.setSelectedOperation(o);
					radios[i].setSelection(true);
					if (selected != null && selected != radios[i]) {
						selected.setSelection(false);
					}
					markDirty(true);
				}
			}
			validateInput();
		}

		public void modifyText(ModifyEvent e) {
			Button selected = null;
			for (int i = 0; i < radios.length; i++) {
				if (radios[i].getSelection())
					selected = radios[i];
			}
			// determine the operation to do to the bug
			for (int i = 0; i < radios.length; i++) {
				if (radios[i] != e.widget && radios[i] != selected) {
					radios[i].setSelection(false);
				}

				if (e.widget == radios[i]) {
					RepositoryOperation o = taskData.getOperation(radios[i].getText());
					taskData.setSelectedOperation(o);
					markDirty(true);
				} else if (e.widget == radioOptions[i]) {
					RepositoryOperation o = taskData.getOperation(radios[i].getText());
					o.setInputValue(((Text) radioOptions[i]).getText());

					if (taskData.getSelectedOperation() != null)
						taskData.getSelectedOperation().setChecked(false);
					o.setChecked(true);

					taskData.setSelectedOperation(o);
					radios[i].setSelection(true);
					if (selected != null && selected != radios[i]) {
						selected.setSelection(false);
					}
					markDirty(true);
				}
			}
			validateInput();
		}
	}
}

// private class DisplayableLocalAttachment extends RepositoryAttachment {
// private static final long serialVersionUID = 900218036143022422L;
//		
// private Date date;
// private String description;
// private String creator;
// private String name;
//		
// public String getCreator() {
// return creator;
// }
// public void setCreator(String creator) {
// this.creator = creator;
// }
// public Date getDateCreated() {
// return date;
// }
// public void setDateCreated(Date date) {
// this.date = date;
// }
// public String getDescription() {
// return description;
// }
// public void setDescription(String description) {
// this.description = description;
// }
// public String getName() {
// return name;
// }
// public void setName(String name) {
// this.name = name;
// }
// public DisplayableLocalAttachment(LocalAttachment att) {
// super(null);
// setName(att.getFilePath());
// setDescription(att.getDescription());
// setDateCreated(new Date());
// }
// }
