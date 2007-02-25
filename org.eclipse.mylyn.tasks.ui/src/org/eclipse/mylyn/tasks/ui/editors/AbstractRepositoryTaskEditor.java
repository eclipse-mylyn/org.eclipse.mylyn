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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.core.util.DateUtil;
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
import org.eclipse.mylar.tasks.core.ITaskListChangeListener;
import org.eclipse.mylar.tasks.core.MylarStatus;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
import org.eclipse.mylar.tasks.core.RepositoryOperation;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskComment;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
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
import org.eclipse.swt.graphics.Cursor;
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
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.ObjectActionContributorManager;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
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

	private static final String LABEL_HISTORY = "History";

	private static final String LABEL_REPLY = "Reply";

	public static final String LABEL_JOB_SUBMIT = "Submitting to repository";

	protected static final String HEADER_DATE_FORMAT = "yyyy-MM-dd HH:mm";

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

	protected FormToolkit toolkit;

	private ScrolledForm form;

	protected TaskRepository repository;

	public static final int RADIO_OPTION_WIDTH = 120;

	protected Display display;

	public static final Font TITLE_FONT = JFaceResources.getBannerFont();

	public static final Font TEXT_FONT = JFaceResources.getDefaultFont();

	public static final Font HEADER_FONT = JFaceResources.getDefaultFont();

	public static final int DESCRIPTION_WIDTH = 79 * 8; // 500;

	public static final int DESCRIPTION_HEIGHT = 10 * 14;

	private static final String REASSIGN_BUG_TO = "Reassign to";

	private static final String LABEL_BUTTON_SUBMIT = "Submit";

	protected final Object FAMILY_SUBMIT = new Object();

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

	// protected Button addSelfToCCCheck;

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

	protected Color backgroundIncoming;

	protected boolean hasAttributeChanges = false;

	protected boolean showAttachments = true;

	protected boolean attachContext = true;

	private boolean submitting = false;

	private Section newCommentSection;

	protected enum SECTION_NAME {
		ATTRIBTUES_SECTION("Attributes"), ATTACHMENTS_SECTION("Attachments"), DESCRIPTION_SECTION("Description"), COMMENTS_SECTION(
				"Comments"), NEWCOMMENT_SECTION("New Comment"), ACTIONS_SECTION("Actions"), PEOPLE_SECTION("People");

		private String prettyName;

		public String getPrettyName() {
			return prettyName;
		}

		SECTION_NAME(String prettyName) {
			this.prettyName = prettyName;
		}
	}

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

	private final ITaskListChangeListener TASKLIST_CHANGE_LISTENER = new ITaskListChangeListener() {

		public void containerAdded(AbstractTaskContainer container) {
		}

		public void containerDeleted(AbstractTaskContainer container) {
		}

		public void containerInfoChanged(AbstractTaskContainer container) {
		}

		public void localInfoChanged(ITask task) {
		}

		public void repositoryInfoChanged(final ITask task) {

			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

				public void run() {

					if (repositoryTask != null && task.equals(repositoryTask)) {
						// System.err.println(">>>
						// "+repositoryTask.getSyncState().toString());
						if ((repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING || repositoryTask
								.getSyncState() == RepositoryTaskSyncState.CONFLICT)
								&& !submitting) {
							MessageDialog.openInformation(AbstractRepositoryTaskEditor.this.getSite().getShell(),
									"Changed - " + repositoryTask.getSummary(),
									"Editor will refresh with new incoming changes.");

							updateContents();
							// TasksUiPlugin.getSynchronizationManager().setTaskRead(repositoryTask,
							// true);
							// TasksUiPlugin.getDefault().getTaskDataManager().clearIncoming(
							// repositoryTask.getHandleIdentifier());
						} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.OUTGOING
								&& !taskData.hasLocalChanges()) {
							submitting = false;
							updateContents();
						} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.SYNCHRONIZED) {
							submitting = false;
							updateContents();
						}
					}
				}

				private void updateContents() {
					if (repositoryTask != null && task.equals(repositoryTask)) {
						if (repositoryTask.getTaskData() == null) {
							close();
							TasksUiUtil.refreshAndOpenTaskListElement(repositoryTask);
							return;
						}
						showBusy(true);
						updateEditor();

					}
				}

			});

		}

		public void taskAdded(ITask task) {
		}

		public void taskDeleted(ITask task) {
		}

		public void taskMoved(ITask task, AbstractTaskContainer fromContainer, AbstractTaskContainer toContainer) {
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

	private Section attributesSection;

	private Section descriptionSection;

	private AbstractRepositoryTask repositoryTask;

	protected Set<RepositoryTaskAttribute> changedAttributes;

	protected Map<SECTION_NAME, String> alternateSectionLabels = new HashMap<SECTION_NAME, String>();

	private String kindLabel;

	private Menu menu;

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

		private IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();

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
				return themeManager.getCurrentTheme().getColorRegistry().get(
						TaskListColorsAndFonts.THEME_COLOR_COMPLETED);
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

		public void widgetSelected(SelectionEvent event) {
			if (comboListenerMap.containsKey(combo)) {
				if (combo.getSelectionIndex() > -1) {
					String sel = combo.getItem(combo.getSelectionIndex());
					RepositoryTaskAttribute attribute = comboListenerMap.get(combo);
					attribute.setValue(sel);
					attributeChanged(attribute);
				}
			}
		}
	}

	/**
	 * Call upon change to attribute value
	 * 
	 * @param attribute
	 *            changed attribute
	 */
	protected boolean attributeChanged(RepositoryTaskAttribute attribute) {
		if (attribute == null) {
			return false;
		}
		changedAttributes.add(attribute);
		markDirty(true);
		validateInput();
		return true;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		if (!(input instanceof RepositoryTaskEditorInput)) {
			return;
		}

		changedAttributes = new HashSet<RepositoryTaskAttribute>();
		editorInput = (RepositoryTaskEditorInput) input;
		repositoryTask = editorInput.getRepositoryTask();
		repository = editorInput.getRepository();
		taskData = editorInput.getTaskData();
		connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
		setSite(site);
		setInput(input);

		taskOutlineModel = RepositoryTaskOutlineNode.parseBugReport(taskData);
		hasAttributeChanges = hasVisibleAttributeChanges();
		isDirty = false;
		TasksUiPlugin.getTaskListManager().getTaskList().addChangeListener(TASKLIST_CHANGE_LISTENER);
		IWorkbenchSiteProgressService progressService = getProgressService();
		if (progressService != null) {
			progressService.showBusyForFamily(FAMILY_SUBMIT);
		}

	}

	protected IWorkbenchSiteProgressService getProgressService() {
		Object siteService = getSite().getAdapter(IWorkbenchSiteProgressService.class);
		if (siteService != null)
			return (IWorkbenchSiteProgressService) siteService;
		return null;
	}

	@Override
	public void markDirty(boolean dirty) {
		if (repositoryTask != null) {
			repositoryTask.setDirty(dirty);
		}
		super.markDirty(dirty);
	}

	/**
	 * Update task state
	 */
	protected void updateTask() {
		if (taskData == null)
			return;
		taskData.setHasLocalChanges(true);
		if (repositoryTask != null) {
			TasksUiPlugin.getSynchronizationManager().saveOutgoing(repositoryTask, changedAttributes);
		}
		if (parentEditor != null) {
			parentEditor.notifyTaskChanged();
		}
	}

	protected abstract void validateInput();

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

	/**
	 * 
	 */
	protected void createFormContent(final IManagedForm managedForm) {
		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		backgroundIncoming = themeManager.getCurrentTheme().getColorRegistry().get(
				TaskListColorsAndFonts.THEME_COLOR_TASKS_INCOMING_BACKGROUND);

		super.createFormContent(managedForm);
		form = managedForm.getForm();
		toolkit = managedForm.getToolkit();
		registerDropListener(form);

		ImageDescriptor overlay = TasksUiPlugin.getDefault().getOverlayIcon(repository.getKind());
		ImageDescriptor imageDescriptor = TaskListImages.createWithOverlay(TaskListImages.REPOSITORY, overlay, false,
				false);
		form.setImage(TaskListImages.getImage(imageDescriptor));

		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getRepositoryUi(repository.getKind());
		kindLabel = "";
		if (connectorUi != null) {
			kindLabel = connectorUi.getTaskKindLabel(repositoryTask);
		}
		String idLabel = "";

		if (repositoryTask != null) {
			idLabel = repositoryTask.getIdentifyingLabel();
		} else {
			idLabel = taskData.getId();
		}

		if (taskData != null && taskData.isNew()) {
			form.setText("New " + kindLabel);
		} else {
			form.setText(kindLabel + " " + idLabel);
		}

		toolkit.decorateFormHeading(form.getForm());

		editorComposite = form.getBody();
		GridLayout editorLayout = new GridLayout();
		editorComposite.setLayout(editorLayout);
		editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (taskData == null) {
			toolkit.createLabel(editorComposite, "Task data not available, please synchronize and reopen.");
			return;
		}

		createSections();
		getSite().getPage().addSelectionListener(selectionListener);
		getSite().setSelectionProvider(selectionProvider);
		if (summaryText != null) {
			summaryText.setFocus();
		}
		addHeaderControls();
	}

	protected void addHeaderControls() {
		ControlContribution repositoryLabelControl = new ControlContribution("TaskEditorHeader") { //$NON-NLS-1$
			protected Control createControl(Composite parent) {
				Text text = new Text(parent, SWT.FLAT | SWT.READ_ONLY);
				text.setFont(TITLE_FONT);
				text.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
				String label = repository.getRepositoryLabel();
				if(label == null || label.equals("")){
					if(repository.getUrl().indexOf("//") != -1) {
						label = repository.getUrl().substring((repository.getUrl().indexOf("//") + 2));
					} else {
						label = repository.getUrl();
					}
				}
				text.setText(label);
				text.setBackground(null);
				return text;
			}
		};
		if (form.getToolBarManager() != null) {
			form.getToolBarManager().add(repositoryLabelControl);
			form.getToolBarManager().update(true);
		}
	}

	protected void createSections() {
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
		GridDataFactory.fillDefaults().grab(true, true).applyTo(bottomComposite);
		// GridDataFactory.fillDefaults().align(SWT.FILL,
		// SWT.DEFAULT).applyTo(bottomComposite);

		createActionsLayout(bottomComposite);
		createPeopleLayout(bottomComposite);
		bottomComposite.pack(true);
		form.reflow(true);
	}

	protected void removeSections() {
		menu = editorComposite.getMenu();
		setMenu(editorComposite, null);
		for (Control control : editorComposite.getChildren()) {
			control.dispose();
		}
	}

	protected void createReportHeaderLayout(Composite composite) {
		addSummaryText(composite);

		Composite headerInfoComposite = toolkit.createComposite(composite);
		GridLayout headerLayout = new GridLayout(11, false);
		headerLayout.verticalSpacing = 1;
		headerLayout.marginHeight = 1;
		headerLayout.marginHeight = 1;
		headerLayout.marginWidth = 1;
		headerLayout.horizontalSpacing = 6;
		headerInfoComposite.setLayout(headerLayout);

		RepositoryTaskAttribute statusAtribute = taskData.getAttribute(RepositoryTaskAttribute.STATUS);
		addNameValue(headerInfoComposite, statusAtribute);
		toolkit.paintBordersFor(headerInfoComposite);

		RepositoryTaskAttribute priorityAttribute = taskData.getAttribute(RepositoryTaskAttribute.PRIORITY);
		addNameValue(headerInfoComposite, priorityAttribute);

		String idLabel = (repositoryTask != null) ? repositoryTask.getIdentifyingLabel() : taskData.getId();
		if (idLabel != null) {

			Composite nameValue = toolkit.createComposite(headerInfoComposite);
			nameValue.setLayout(new GridLayout(2, false));
			Label label = toolkit.createLabel(nameValue, "ID:");// .setFont(TITLE_FONT);
			label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
			// toolkit.createText(nameValue, idLabel, SWT.FLAT | SWT.READ_ONLY);
			Text text = new Text(nameValue, SWT.FLAT | SWT.READ_ONLY);
			toolkit.adapt(text, true, true);
			text.setText(idLabel);
		}

		String openedDateString = "";
		String modifiedDateString = "";
		final ITaskDataHandler taskDataManager = connector.getTaskDataHandler();
		if (taskDataManager != null) {
			Date created = taskData.getAttributeFactory().getDateForAttributeType(
					RepositoryTaskAttribute.DATE_CREATION, taskData.getCreated());
			openedDateString = created != null ? DateUtil.getFormattedDate(created, HEADER_DATE_FORMAT) : "";

			Date modified = taskData.getAttributeFactory().getDateForAttributeType(
					RepositoryTaskAttribute.DATE_MODIFIED, taskData.getLastModified());
			modifiedDateString = modified != null ? DateUtil.getFormattedDate(modified, HEADER_DATE_FORMAT) : "";
		}

		RepositoryTaskAttribute creationAttribute = taskData.getAttribute(RepositoryTaskAttribute.DATE_CREATION);
		if (creationAttribute != null) {
			Composite nameValue = toolkit.createComposite(headerInfoComposite);
			nameValue.setLayout(new GridLayout(2, false));
			createLabel(nameValue, creationAttribute);
			// toolkit.createText(nameValue, openedDateString, SWT.FLAT |
			// SWT.READ_ONLY);
			Text text = new Text(nameValue, SWT.FLAT | SWT.READ_ONLY);
			toolkit.adapt(text, true, true);
			text.setText(openedDateString);
		}

		RepositoryTaskAttribute modifiedAttribute = taskData.getAttribute(RepositoryTaskAttribute.DATE_MODIFIED);
		if (modifiedAttribute != null) {
			Composite nameValue = toolkit.createComposite(headerInfoComposite);
			nameValue.setLayout(new GridLayout(2, false));
			createLabel(nameValue, modifiedAttribute);
			// toolkit.createText(nameValue, modifiedDateString, SWT.FLAT |
			// SWT.READ_ONLY);
			Text text = new Text(nameValue, SWT.FLAT | SWT.READ_ONLY);
			toolkit.adapt(text, true, true);
			text.setText(modifiedDateString);
		}

		if (getActivityUrl() != null) {
			String linkName = LABEL_HISTORY;
			ImageHyperlink hyperlink = toolkit.createImageHyperlink(headerInfoComposite, SWT.NONE);
			hyperlink.setText(linkName);
			hyperlink.setToolTipText(kindLabel + " " + LABEL_HISTORY);
			hyperlink.setImage(TaskListImages.getImage(TaskListImages.TASK_REPOSITORY_HISTORY));
			hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					if (AbstractRepositoryTaskEditor.this.getEditor() instanceof TaskEditor) {
						TaskEditor mylarTaskEditor = (TaskEditor) AbstractRepositoryTaskEditor.this.getEditor();
						mylarTaskEditor.displayInBrowser(getActivityUrl());
					}
				}
			});
		}
	}

	private void addNameValue(Composite parent, RepositoryTaskAttribute attribute) {
		Composite nameValue = toolkit.createComposite(parent);
		nameValue.setLayout(new GridLayout(2, false));
		if (attribute != null) {
			createLabel(nameValue, attribute);
			createTextField(nameValue, attribute, SWT.FLAT | SWT.READ_ONLY);
		}
	}

	/**
	 * Utility method to create text field sets backgournd to
	 * TaskListColorsAndFonts.COLOR_ATTRIBUTE_CHANGED if attribute has .
	 * 
	 * @param composite
	 * @param attribute
	 * @param style
	 */
	protected Text createTextField(Composite composite, RepositoryTaskAttribute attribute, int style) {
		final Text text;
		String value;
		if (attribute == null || attribute.getValue() == null) {
			value = "";
		} else {
			value = attribute.getValue();
		}
		if ((SWT.READ_ONLY & style) == SWT.READ_ONLY) {
			text = new Text(composite, style);
			toolkit.adapt(text, true, true);
			text.setText(value);
		} else {
			text = toolkit.createText(composite, value, style);
		}

		if (attribute != null && !attribute.isReadOnly()) {
			text.setData(attribute);
			text.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					String newValue = text.getText();
					RepositoryTaskAttribute attribute = (RepositoryTaskAttribute) text.getData();
					attribute.setValue(newValue);
					attributeChanged(attribute);
				}
			});
		}
		if (hasChanged(attribute)) {
			text.setBackground(backgroundIncoming);
		}
		return text;
	}

	protected Label createLabel(Composite composite, RepositoryTaskAttribute attribute) {
		Label label;
		if (hasOutgoingChange(attribute)) {
			label = toolkit.createLabel(composite, "*" + attribute.getName());
		} else {
			label = toolkit.createLabel(composite, attribute.getName());
		}
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		return label;
	}

	public String getSectionLabel(SECTION_NAME labelName) {
		String label = alternateSectionLabels.get(labelName);
		if (label != null) {
			return label;
		} else {
			return labelName.getPrettyName();
		}
	}

	/**
	 * Creates the attribute section, which contains most of the basic
	 * attributes of the task (some of which are editable).
	 */
	protected Composite createAttributeLayout(Composite composite) {
		String title = taskData.getLabel();
		attributesSection = createSection(composite, getSectionLabel(SECTION_NAME.ATTRIBTUES_SECTION));
		attributesSection.setExpanded(expandedStateAttributes || hasAttributeChanges);

		// Attributes Composite- this holds all the combo fields and text fields
		Composite attributesComposite = toolkit.createComposite(attributesSection);
		GridLayout attributesLayout = new GridLayout();
		attributesLayout.numColumns = 4;
		attributesLayout.horizontalSpacing = 5;
		attributesLayout.verticalSpacing = 4;
		attributesComposite.setLayout(attributesLayout);
		GridData attributesData = new GridData(GridData.FILL_BOTH);
		attributesData.horizontalSpan = 1;
		attributesData.grabExcessVerticalSpace = false;
		attributesComposite.setLayoutData(attributesData);
		attributesSection.setClient(attributesComposite);
		editorInput.setToolTipText(title);

		int currentCol = 1;

		for (RepositoryTaskAttribute attribute : taskData.getAttributes()) {
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
				Label label = createLabel(attributesComposite, attribute);
				GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
				attributeCombo = new CCombo(attributesComposite, SWT.FLAT | SWT.READ_ONLY);
				toolkit.adapt(attributeCombo, true, true);
				attributeCombo.setFont(TEXT_FONT);
				attributeCombo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
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
				currentCol += 2;
			} else {
				Label label = createLabel(attributesComposite, attribute);
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
							String newValue = text.getText();
							RepositoryTaskAttribute attribute = (RepositoryTaskAttribute) text.getData();
							attribute.setValue(newValue);
							attributeChanged(attribute);
						}
					});
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
				currentCol++;
			}
		}
		return attributesComposite;
	}

	/**
	 * Adds a text field to display and edit the task's summary.
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

		RepositoryTaskAttribute attribute = taskData.getAttribute(RepositoryTaskAttribute.SUMMARY);
		if (attribute != null) {
			// Label summaryLabel = createLabel(summaryComposite, attribute);
			// summaryLabel.setFont(TITLE_FONT);
			summaryText = createTextField(summaryComposite, attribute, SWT.FLAT);
			IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
			Font summaryFont = themeManager.getCurrentTheme().getFontRegistry().get(
					TaskListColorsAndFonts.TASK_EDITOR_FONT);
			summaryText.setFont(summaryFont);

			GridDataFactory.fillDefaults().grab(true, false).hint(DESCRIPTION_WIDTH, SWT.DEFAULT).applyTo(summaryText);
			summaryText.addListener(SWT.KeyUp, new SummaryListener());
		}
		toolkit.paintBordersFor(summaryComposite);
	}

	protected void createAttachmentLayout(Composite composite) {

		// TODO: expand to show new attachments
		Section section = createSection(composite, getSectionLabel(SECTION_NAME.ATTACHMENTS_SECTION));
		section.setText(section.getText() + " (" + taskData.getAttachments().size() + ")");
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
						Date created1 = taskData.getAttributeFactory().getDateForAttributeType(
								RepositoryTaskAttribute.ATTACHMENT_DATE, attachment1.getDateCreated());
						Date created2 = taskData.getAttributeFactory().getDateForAttributeType(
								RepositoryTaskAttribute.ATTACHMENT_DATE, attachment2.getDateCreated());
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
						TasksUiUtil.openBrowser(attachment.getUrl());
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
						TasksUiUtil.openBrowser(attachment.getUrl());
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
						TasksUiUtil.openBrowser(attachment.getUrl());
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

					if (e.getSelection().isEmpty()) {
						return;
					}

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

		ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(), taskData.getId());
		if (task == null) {
			addAttachmentButton.setEnabled(false);
		}

		addAttachmentButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			public void widgetSelected(SelectionEvent e) {
				ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(),
						taskData.getId());
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
					ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(),
							taskData.getId());
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
						ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(),
								taskData.getId());
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
		descriptionSection = createSection(composite, getSectionLabel(SECTION_NAME.DESCRIPTION_SECTION));
		final Composite sectionComposite = toolkit.createComposite(descriptionSection);
		descriptionSection.setClient(sectionComposite);
		GridLayout addCommentsLayout = new GridLayout();
		addCommentsLayout.numColumns = 1;
		sectionComposite.setLayout(addCommentsLayout);
		GridData sectionCompositeData = new GridData(GridData.FILL_HORIZONTAL);
		sectionComposite.setLayoutData(sectionCompositeData);

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
					String newValue = descriptionTextViewer.getTextWidget().getText();
					RepositoryTaskAttribute attribute = (RepositoryTaskAttribute) taskData
							.getAttribute(RepositoryTaskAttribute.DESCRIPTION);
					attribute.setValue(newValue);
					attributeChanged(attribute);
					taskData.setDescription(newValue);
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

		createReplyHyperlink(0, descriptionSection, taskData.getDescription());

		// toolkit.paintBordersFor(sectionComposite);

	}

	private ImageHyperlink createReplyHyperlink(final int commentNum, final ExpandableComposite section,
			final String commentBody) {
		final ImageHyperlink replyLink = new ImageHyperlink(section, SWT.NULL);
		toolkit.adapt(replyLink, true, true);
		replyLink.setImage(TaskListImages.getImage(TaskListImages.REPLY));
		replyLink.setToolTipText(LABEL_REPLY);
		// no need for the background - transparency will take care of it
		replyLink.setBackground(null);
		// replyLink.setBackground(section.getTitleBarGradientBackground());
		replyLink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				String oldText = newCommentTextViewer.getDocument().get();
				StringBuilder strBuilder = new StringBuilder();
				strBuilder.append(oldText);
				if (strBuilder.length() != 0) {
					strBuilder.append("\n");
				}
				strBuilder.append(" (In reply to comment #" + commentNum + ")\n");
				CommentQuoter quoter = new CommentQuoter();
				strBuilder.append(quoter.quote(commentBody));
				newCommentTextViewer.getDocument().set(strBuilder.toString());
				RepositoryTaskAttribute attribute = taskData.getAttribute(RepositoryTaskAttribute.COMMENT_NEW);
				if (attribute != null) {
					attribute.setValue(strBuilder.toString());
					attributeChanged(attribute);
				}
				selectNewComment();
				newCommentTextViewer.getTextWidget().setCaretOffset(strBuilder.length());
			}
		});
		section.addExpansionListener(new ExpansionAdapter() {

			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				replyLink.setVisible(section.isExpanded());
			}
		});
		section.setTextClient(replyLink);

		return replyLink;
	}

	protected void createCustomAttributeLayout(Composite composite) {
		// override
	}

	protected void createPeopleLayout(Composite composite) {
		Section peopleSection = createSection(composite, getSectionLabel(SECTION_NAME.PEOPLE_SECTION));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, true).applyTo(peopleSection);
		Composite peopleComposite = toolkit.createComposite(peopleSection);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 5;
		peopleComposite.setLayout(layout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(peopleComposite);

		RepositoryTaskAttribute assignedAttribute = taskData.getAttribute(RepositoryTaskAttribute.USER_ASSIGNED);
		if (assignedAttribute != null) {

			Label label = createLabel(peopleComposite, assignedAttribute);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.DEFAULT).applyTo(label);
			Text textField = createTextField(peopleComposite, assignedAttribute, SWT.FLAT | SWT.READ_ONLY);
			GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).applyTo(textField);
		}

		RepositoryTaskAttribute reporterAttribute = taskData.getAttribute(RepositoryTaskAttribute.USER_REPORTER);
		if (reporterAttribute != null) {

			Label label = createLabel(peopleComposite, reporterAttribute);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.DEFAULT).applyTo(label);
			Text textField = createTextField(peopleComposite, reporterAttribute, SWT.FLAT | SWT.READ_ONLY);
			GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).applyTo(textField);
		}
		addSelfToCC(peopleComposite);
		addCCList(peopleComposite);
		getManagedForm().getToolkit().paintBordersFor(peopleComposite);
		peopleSection.setClient(peopleComposite);
		peopleSection.setEnabled(true);
	}

	protected void addCCList(Composite attributesComposite) {

		RepositoryTaskAttribute addCCattribute = taskData.getAttribute(RepositoryTaskAttribute.NEW_CC);
		if (addCCattribute == null) {
			// TODO: remove once TRAC is priming taskData with NEW_CC attribute
			taskData.setAttributeValue(RepositoryTaskAttribute.NEW_CC, "");
			addCCattribute = taskData.getAttribute(RepositoryTaskAttribute.NEW_CC);
		}
		if (addCCattribute != null) {
			Label label = createLabel(attributesComposite, addCCattribute);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.DEFAULT).applyTo(label);
			Text text = createTextField(attributesComposite, addCCattribute, SWT.FLAT);
			GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).applyTo(text);
		}

		RepositoryTaskAttribute CCattribute = taskData.getAttribute(RepositoryTaskAttribute.USER_CC);
		if (CCattribute != null) {
			Label label = createLabel(attributesComposite, CCattribute);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.DEFAULT).applyTo(label);
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
					for (String cc : ccList.getItems()) {
						int index = ccList.indexOf(cc);
						if (ccList.isSelected(index)) {
							taskData.addAttributeValue(RepositoryTaskAttribute.REMOVE_CC, cc);
						} else {
							taskData.removeAttributeValue(RepositoryTaskAttribute.REMOVE_CC, cc);
						}
					}
					attributeChanged(taskData.getAttribute(RepositoryTaskAttribute.REMOVE_CC));
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			toolkit.createLabel(attributesComposite, "");
			label = toolkit.createLabel(attributesComposite, "(Select to remove)");
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.DEFAULT).applyTo(label);
		}

	}

	/**
	 * A listener for selection of the summary field.
	 */
	protected class DescriptionListener implements Listener {
		public void handleEvent(Event event) {
			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
					new RepositoryTaskSelection(taskData.getId(), taskData.getRepositoryUrl(),
							getSectionLabel(SECTION_NAME.DESCRIPTION_SECTION), true, taskData.getSummary()))));
		}
	}

	protected void createCommentLayout(Composite composite) {
		commentsSection = createSection(composite, getSectionLabel(SECTION_NAME.COMMENTS_SECTION));
		commentsSection.setText(commentsSection.getText() + " (" + taskData.getComments().size() + ")");
		ImageHyperlink hyperlink = new ImageHyperlink(commentsSection, SWT.NONE);
		toolkit.adapt(hyperlink, true, true);
		hyperlink.setBackground(null);
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

		boolean foundNew = false;

		StyledText styledText = null;
		for (Iterator<TaskComment> it = taskData.getComments().iterator(); it.hasNext();) {
			final TaskComment taskComment = it.next();

			final ExpandableComposite expandableComposite = toolkit.createExpandableComposite(addCommentsComposite,
					ExpandableComposite.TREE_NODE | ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT);

			if (repositoryTask != null && repositoryTask.getLastSyncDateStamp() == null) {
				// hit? Expose all comments
				expandableComposite.setExpanded(true);
				foundNew = true;
			} else if (isNewComment(taskComment)) {
				expandableComposite.setBackground(backgroundIncoming);
				expandableComposite.setExpanded(true);
				foundNew = true;
			}

			expandableComposite.setTitleBarForeground(toolkit.getColors().getColor(IFormColors.TITLE));

			expandableComposite.setText(taskComment.getNumber() + ": " + taskComment.getAuthorName() + ", "
					+ taskComment.getCreated());

			expandableComposite.addExpansionListener(new ExpansionAdapter() {
				public void expansionStateChanged(ExpansionEvent e) {
					form.reflow(true);
				}
			});

			ImageHyperlink replyLink = createReplyHyperlink(taskComment.getNumber(), expandableComposite, taskComment
					.getText());
			replyLink.setVisible(expandableComposite.isExpanded());

			// HACK: This is necessary
			// due to a bug in SWT's ExpandableComposite.
			// 165803: Expandable bars should expand when clicking anywhere
			// https://bugs.eclipse.org/bugs/show_bug.cgi?taskId=165803
			expandableComposite.setData(replyLink);

			expandableComposite.setLayout(new GridLayout());
			expandableComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Composite ecComposite = toolkit.createComposite(expandableComposite);
			GridLayout ecLayout = new GridLayout();
			ecLayout.marginHeight = 0;
			ecLayout.marginBottom = 3;
			ecLayout.marginLeft = 10;
			ecComposite.setLayout(ecLayout);
			ecComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			expandableComposite.setClient(ecComposite);

			TextViewer viewer = addTextViewer(repository, ecComposite, taskComment.getText().trim(), SWT.MULTI
					| SWT.WRAP);
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
		if (foundNew) {
			commentsSection.setExpanded(true);
		} else if (taskData.getComments() == null || taskData.getComments().size() == 0) {
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

	private boolean isNewComment(TaskComment comment) {

		if (repositoryTask != null) {
			if (repositoryTask.getLastSyncDateStamp() == null) {
				// new hit
				return true;
			}
			AbstractRepositoryConnector connector = (AbstractRepositoryConnector) TasksUiPlugin.getRepositoryManager()
					.getRepositoryConnector(taskData.getRepositoryKind());
			ITaskDataHandler offlineHandler = connector.getTaskDataHandler();
			if (offlineHandler != null) {

				Date lastSyncDate = taskData.getAttributeFactory().getDateForAttributeType(
						RepositoryTaskAttribute.DATE_MODIFIED, repositoryTask.getLastSyncDateStamp());

				if (lastSyncDate != null) {

					// reduce granularity to minutes
					Calendar calLastMod = Calendar.getInstance();
					calLastMod.setTimeInMillis(lastSyncDate.getTime());
					calLastMod.set(Calendar.SECOND, 0);

					Date commentDate = taskData.getAttributeFactory().getDateForAttributeType(
							RepositoryTaskAttribute.COMMENT_DATE, comment.getCreated());
					if (commentDate != null) {
						if (commentDate.after(calLastMod.getTime())) {
							return true;
						}
					}
				}
			}
		}
		return false;

		// Simple test (will not reveal new comments if offline data was lost
		// return (comment.getNumber() > oldTaskData.getComments().size());
	}

	protected void createNewCommentLayout(Composite composite) {
		newCommentSection = createSection(composite, getSectionLabel(SECTION_NAME.NEWCOMMENT_SECTION));

		Composite newCommentsComposite = toolkit.createComposite(newCommentSection);
		newCommentsComposite.setLayout(new GridLayout());

		// HACK: new new comment attribute not created by connector, create one.
		if (taskData.getAttribute(RepositoryTaskAttribute.COMMENT_NEW) == null) {
			taskData.setAttributeValue(RepositoryTaskAttribute.COMMENT_NEW, "");
		}
		final RepositoryTaskAttribute attribute = taskData.getAttribute(RepositoryTaskAttribute.COMMENT_NEW);
		newCommentTextViewer = addTextEditor(repository, newCommentsComposite, attribute.getValue(), true, SWT.FLAT
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
				String newValue = addCommentsTextBox.getText();
				attribute.setValue(newValue);
				attributeChanged(attribute);
			}
		});

		newCommentTextViewer.getTextWidget().addListener(SWT.FocusIn, new NewCommentListener());
		addCommentsTextBox = newCommentTextViewer.getTextWidget();

		newCommentSection.setClient(newCommentsComposite);

		toolkit.paintBordersFor(newCommentsComposite);
	}

	/**
	 * Creates the button layout. This displays options and buttons at the
	 * bottom of the editor to allow actions to be performed on the bug.
	 */
	protected void createActionsLayout(Composite composite) {
		Section section = createSection(composite, getSectionLabel(SECTION_NAME.ACTIONS_SECTION));
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
		submitButton = toolkit.createButton(buttonComposite, LABEL_BUTTON_SUBMIT, SWT.NONE);
		GridData submitButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		submitButtonData.widthHint = 100;
		submitButton.setLayoutData(submitButtonData);
		submitButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				submitToRepository();
			}
		});
		submitButton.setToolTipText("Submit to " + this.repository.getUrl());

		toolkit.createLabel(buttonComposite, "    ");

		ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(), taskData.getId());
		if (attachContext && task != null) {
			addAttachContextButton(buttonComposite, task);
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
	 * Make sure that a String that is <code>null</code> is to a null string
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

	@Override
	public void setFocus() {
		if (summaryText != null && !summaryText.isDisposed()) {
			summaryText.setFocus();
		} else {
			form.setFocus();
		}
	}

	/**
	 * Updates the title of the editor
	 * 
	 */
	protected void updateEditorTitle() {
		setPartName(editorInput.getName());
		((TaskEditor) this.getEditor()).updateTitle(editorInput.getName());
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		updateTask();
		updateEditorTitle();
		runSaveJob();
	}

	// TODO: Remove once offline persistence is improved
	private void runSaveJob() {
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
		TasksUiPlugin.getTaskListManager().getTaskList().removeChangeListener(TASKLIST_CHANGE_LISTENER);
		getSite().getPage().removeSelectionListener(selectionListener);
		if (waitCursor != null) {
			waitCursor.dispose();
		}
		isDisposed = true;
		if (repositoryTask != null && repositoryTask.isDirty()) {
			// Edits are being made to the outgoing object
			// Must discard these unsaved changes
			TasksUiPlugin.getSynchronizationManager().discardOutgoing(repositoryTask);
			repositoryTask.setDirty(false);
		}

		super.dispose();
	}

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

	// /**
	// * A generic listener for selection of unimportant items. The default
	// * selection item sent out is the entire bug object.
	// */
	// public class GenericListener implements Listener {
	// public void handleEvent(Event event) {
	// RepositoryTaskData bug = taskData;
	// fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new
	// StructuredSelection(
	// new RepositoryTaskSelection(bug.getId(), bug.getRepositoryUrl(),
	// bug.getLabel(), false, bug
	// .getSummary()))));
	// }
	// }

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

					// HACK: This is necessary
					// due to a bug in SWT's ExpandableComposite.
					// 165803: Expandable bars should expand when clicking
					// anywhere
					// https://bugs.eclipse.org/bugs/show_bug.cgi?taskId=165803
					if (ex.getData() != null && ex.getData() instanceof ImageHyperlink) {
						((ImageHyperlink) ex.getData()).setVisible(true);
					}

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

	private Cursor waitCursor;

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

			if (o.hasOptions()) {
				radioData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
				radioData.horizontalSpan = 1;
				radioData.heightHint = 20;
				radioData.widthHint = RADIO_OPTION_WIDTH;
				radioOptions[i] = new CCombo(buttonComposite, SWT.FLAT | SWT.READ_ONLY);
				radioOptions[i].setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				toolkit.adapt(radioOptions[i], true, true);
				radioOptions[i].setFont(TEXT_FONT);
				radioOptions[i].setLayoutData(radioData);

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
				radioData.widthHint = RADIO_OPTION_WIDTH - 10;

				String assignmentValue = "";
				if (opName.equals(REASSIGN_BUG_TO)) {
					assignmentValue = repository.getUserName();
				}
				radioOptions[i] = toolkit.createText(buttonComposite, assignmentValue);
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
	 * @return true if one or more attributes exposed in the editor have
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

	protected boolean hasOutgoingChange(RepositoryTaskAttribute newAttribute) {
		return editorInput.getOldEdits().contains(newAttribute);
	}

	protected boolean hasChanged(RepositoryTaskAttribute newAttribute) {
		if (newAttribute == null)
			return false;
		RepositoryTaskData oldTaskData = editorInput.getOldTaskData();
		if (oldTaskData == null)
			return false;

		if (hasOutgoingChange(newAttribute)) {
			return false;
		}

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
				RepositoryTaskAttribute attribute = taskData.getAttribute(RepositoryTaskAttribute.ADD_SELF_CC);
				changedAttributes.add(attribute);
				markDirty(true);
			}
		});
	}

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

	public void showBusy(boolean busy) {
		form.setBusy(busy);
		if (busy) {
			setEnabledState(editorComposite, false);
			// regularCursor = form.getCursor();
			// if (waitCursor == null) {
			// waitCursor = new Cursor(form.getDisplay(), SWT.CURSOR_WAIT);
			// }
			// form.setCursor(waitCursor);
		} else {
			// form.setCursor(regularCursor);
			setEnabledState(editorComposite, true);
		}
	}

	private void setEnabledState(Composite composite, boolean enabled) {
		if (!composite.isDisposed()) {
			composite.setEnabled(enabled);
			for (Control control : composite.getChildren()) {
				control.setEnabled(enabled);
				if (control instanceof Composite) {
					setEnabledState(((Composite) control), enabled);
				}
			}
		}
	}

	public void submitToRepository() {
		submitButton.setEnabled(false);
		showBusy(true);
		updateTask();
		if (isDirty()) {
			runSaveJob();
		}

		final boolean attachContext = getAttachContext();

		Job submitJob = new Job(LABEL_JOB_SUBMIT) {

			public boolean belongsTo(Object family) {
				return family == FAMILY_SUBMIT;
			}

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					String taskId = connector.getTaskDataHandler().postTaskData(repository, taskData);
					final boolean isNew = taskData.isNew();
					if (isNew) {
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
								.getTask(repository.getUrl(), taskData.getId());
					}

					// Synchronization accounting...
					if (modifiedTask != null) {
						// Attach context if required
						if (attachContext) {
							attachContext(modifiedTask);
						}
						submitting = true;
						modifiedTask.getTaskData().setHasLocalChanges(true);
						TasksUiPlugin.getSynchronizationManager().synchronize(connector, modifiedTask, true,
								new JobChangeAdapter() {

									@Override
									public void done(IJobChangeEvent event) {

										if (isNew) {
											close();
											TasksUiPlugin.getSynchronizationManager().setTaskRead(modifiedTask, true);
											TasksUiUtil.openEditor(modifiedTask, false);
										}
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
					return handleSubmitError(e);
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

	/**
	 * @since 2.0 If existing task editor, update contents in place
	 */
	protected void updateEditor() {
		if (!this.isDisposed) {
			changedAttributes.clear();
			commentStyleText.clear();
			textHash.clear();
			editorInput.refreshInput();
			if (repositoryTask != null) {
				repositoryTask.setDirty(false);
			}
			this.setInputWithNotify(this.getEditorInput());
			this.init(this.getEditorSite(), this.getEditorInput());
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					updateEditorTitle();
					menu = editorComposite.getMenu();
					removeSections();
					editorComposite.setMenu(menu);
					createSections();
					markDirty(false);

					showBusy(false);
					AbstractRepositoryTaskEditor.this.getEditor().setActivePage(
							AbstractRepositoryTaskEditor.this.getId());
					AbstractRepositoryTaskEditor.this.getEditor().getEditorSite().getPage().activate(
							AbstractRepositoryTaskEditor.this);
					// TODO: expand sections that were previously expanded

					if (taskOutlineModel != null && outlinePage != null) {
						outlinePage.getOutlineTreeViewer().setInput(taskOutlineModel);
						outlinePage.getOutlineTreeViewer().refresh(true);
					}
					if (repositoryTask != null) {
						TasksUiPlugin.getSynchronizationManager().setTaskRead(repositoryTask, true);
					}
				}
			});

		}
	}

	/**
	 * Used to prevent form menu from being disposed when disposing elements on
	 * the form during refresh
	 */
	private void setMenu(Composite comp, Menu menu) {
		if (!comp.isDisposed()) {
			comp.setMenu(null);
			for (Control child : comp.getChildren()) {
				child.setMenu(null);
				if (child instanceof Composite) {
					setMenu((Composite) child, menu);
				}
			}
		}
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
