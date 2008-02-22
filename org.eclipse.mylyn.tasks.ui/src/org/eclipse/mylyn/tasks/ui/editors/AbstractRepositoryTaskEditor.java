/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.mylyn.internal.tasks.core.CommentQuoter;
import org.eclipse.mylyn.internal.tasks.ui.PersonProposalLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.PersonProposalProvider;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.TaskListHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.actions.AbstractTaskEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.AttachAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.AttachScreenshotAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.ClearOutgoingAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyAttachmentToClipboardJob;
import org.eclipse.mylyn.internal.tasks.ui.actions.DownloadAttachmentJob;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewSubTaskAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.ToggleTaskActivationAction;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentTableLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentsTableContentProvider;
import org.eclipse.mylyn.internal.tasks.ui.editors.ContentOutlineTools;
import org.eclipse.mylyn.internal.tasks.ui.editors.IRepositoryTaskAttributeListener;
import org.eclipse.mylyn.internal.tasks.ui.editors.IRepositoryTaskSelection;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryAttachmentEditorInput;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTaskOutlineNode;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTaskOutlinePage;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTaskSelection;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskUrlHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.views.ResetRepositoryConfigurationAction;
import org.eclipse.mylyn.monitor.core.DateUtil;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryOperation;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.ui.AbstractDuplicateDetector;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.search.SearchHitCollector;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.ObjectActionContributorManager;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.themes.IThemeManager;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * Extend to provide customized task editing.
 * 
 * NOTE: likely to change for 3.0
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Jeff Pound (Attachment work)
 * @author Steffen Pingel
 * @author Xiaoyang Guan (Wiki HTML preview)
 * @since 2.0
 */
public abstract class AbstractRepositoryTaskEditor extends TaskFormPage {

	private static final String PREF_SORT_ORDER_PREFIX = "org.eclipse.mylyn.editor.comments.sortDirectionUp.";

	private static final String ERROR_NOCONNECTIVITY = "Unable to submit at this time. Check connectivity and retry.";

	private static final String LABEL_HISTORY = "History";

	private static final String LABEL_REPLY = "Reply";

	private static final String LABEL_JOB_SUBMIT = "Submitting to repository";

	private static final String HEADER_DATE_FORMAT = "yyyy-MM-dd HH:mm";

	private static final String ATTACHMENT_DEFAULT_NAME = "attachment";

	private static final String CTYPE_ZIP = "zip";

	private static final String CTYPE_OCTET_STREAM = "octet-stream";

	private static final String CTYPE_TEXT = "text";

	private static final String CTYPE_HTML = "html";

	private static final String LABEL_BROWSER = "Browser";

	private static final String LABEL_DEFAULT_EDITOR = "Default Editor";

	private static final String LABEL_TEXT_EDITOR = "Text Editor";

	protected static final String CONTEXT_MENU_ID = "#MylynRepositoryEditor";

	private FormToolkit toolkit;

	private ScrolledForm form;

	protected TaskRepository repository;

	private static final int RADIO_OPTION_WIDTH = 120;

	private static final Font TITLE_FONT = JFaceResources.getBannerFont();

	protected static final Font TEXT_FONT = JFaceResources.getDefaultFont();

	private static final int DESCRIPTION_WIDTH = 79 * 7; // 500;

	private static final int DESCRIPTION_HEIGHT = 10 * 14;

	protected static final int SUMMARY_HEIGHT = 20;

	private static final String LABEL_BUTTON_SUBMIT = "Submit";

	private static final String LABEL_COPY_URL_TO_CLIPBOARD = "Copy &URL";

	private static final String LABEL_COPY_TO_CLIPBOARD = "Copy Contents";

	private static final String LABEL_SAVE = "Save...";

	private static final String LABEL_SEARCH_DUPS = "Search";

	private static final String LABEL_SELECT_DETECTOR = "Duplicate Detection";

	private RepositoryTaskEditorInput editorInput;

	private TaskEditor parentEditor = null;

	private RepositoryTaskOutlineNode taskOutlineModel = null;

	private boolean expandedStateAttributes = false;

	protected Button submitButton;

	private Table attachmentsTable;

	private boolean refreshEnabled = true;

	private TableViewer attachmentsTableViewer;

	private String[] attachmentsColumns = { "Name", "Description", "Type", "Size", "Creator", "Created" };

	private int[] attachmentsColumnWidths = { 140, 160, 100, 70, 100, 100 };

	private Composite editorComposite;

	protected TextViewer summaryTextViewer;

	private ImageHyperlink sortHyperlink;

	private boolean commentSortIsUp = true;

	private boolean commentSortEnable = false;

	private Composite addCommentsComposite;

	/**
	 * WARNING: This is present for backward compatibility only. You can get and set text on this widget but all ui
	 * related changes to this widget will have no affect as ui is now being presented with a StyledText widget. This
	 * simply proxies get/setText calls to the StyledText widget.
	 * 
	 * API 3.0: Eliminate this field
	 */
	protected Text summaryText;

	private TextViewer newCommentTextViewer;

	private org.eclipse.swt.widgets.List ccList;

	private Section commentsSection;

	private Color colorIncoming;

	private boolean hasAttributeChanges = false;

	private boolean showAttachments = true;

	private boolean attachContextEnabled = true;

	protected Button searchForDuplicates;

	protected CCombo duplicateDetectorChooser;

	protected Label duplicateDetectorLabel;

	private boolean ignoreLocationEvents = false;

	private TaskComment selectedComment = null;

	/**
	 * @author Raphael Ackermann (bug 195514)
	 */
	private class TabVerifyKeyListener implements VerifyKeyListener {

		public void verifyKey(VerifyEvent event) {
			// if there is a tab key, do not "execute" it and instead select the Status control
			if (event.keyCode == SWT.TAB) {
				event.doit = false;
				if (headerInfoComposite != null) {
					headerInfoComposite.setFocus();
				}
			}
		}

	}

	// API-3.0 rename ATTRIBTUES_SECTION to ATTRIBUTES_SECTION (bug 208629)
	protected enum SECTION_NAME {
		ATTRIBTUES_SECTION("Attributes"), ATTACHMENTS_SECTION("Attachments"), DESCRIPTION_SECTION("Description"), COMMENTS_SECTION(
		"Comments"), NEWCOMMENT_SECTION("New Comment"), ACTIONS_SECTION("Actions"), PEOPLE_SECTION("People"), RELATEDBUGS_SECTION(
		"Related Tasks");

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
			RepositoryTaskSelection selection = new RepositoryTaskSelection(taskData.getId(),
					taskData.getRepositoryUrl(), taskData.getRepositoryKind(), "", selectedComment,
					taskData.getSummary());
			selection.setIsDescription(true);
			return selection;
		}

		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			selectionChangedListeners.remove(listener);
		}

		public void setSelection(ISelection selection) {
			// No implementation.
		}
	};

	private final ITaskListChangeListener TASKLIST_CHANGE_LISTENER = new ITaskListChangeListener() {

		public void containersChanged(Set<TaskContainerDelta> containers) {
			AbstractTask taskToRefresh = null;
			for (TaskContainerDelta taskContainerDelta : containers) {
				if (repositoryTask != null && repositoryTask.equals(taskContainerDelta.getContainer())) {
					if (taskContainerDelta.getKind().equals(TaskContainerDelta.Kind.CONTENT)) {
						taskToRefresh = (AbstractTask) taskContainerDelta.getContainer();
						break;
					}
				}
			}
			if (taskToRefresh != null) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (repositoryTask.getSynchronizationState() == RepositoryTaskSyncState.INCOMING
								|| repositoryTask.getSynchronizationState() == RepositoryTaskSyncState.CONFLICT) {
							parentEditor.setMessage("Task has incoming changes",
									IMessageProvider.WARNING, new HyperlinkAdapter() {
								@Override
								public void linkActivated(HyperlinkEvent e) {
									refreshEditor();
								}
							});
							setSubmitEnabled(false);
						} else {
							refreshEditor();
						}
					}
				});
			}
		}
	};

	private List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

	private IRepositoryTaskSelection lastSelected = null;

	/**
	 * Focuses on form widgets when an item in the outline is selected.
	 */
	private final ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if ((part instanceof ContentOutline) && (selection instanceof StructuredSelection)) {
				Object select = ((StructuredSelection) selection).getFirstElement();
				if (select instanceof RepositoryTaskOutlineNode) {
					RepositoryTaskOutlineNode n = (RepositoryTaskOutlineNode) select;

					if (lastSelected != null
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
						focusDescription();
					} else if (data != null) {
						select(data, highlight);
					}
				}
				part.setFocus();
			}
		}
	};

	private AbstractTask repositoryTask;

	private Set<RepositoryTaskAttribute> changedAttributes;

	private Menu menu;

	private SynchronizeEditorAction synchronizeEditorAction;

	private ToggleTaskActivationAction activateAction;

	private Action historyAction;

	private Action clearOutgoingAction;
	
	private Action openBrowserAction;

	private NewSubTaskAction newSubTaskAction;

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

		initTaskEditor(site, (RepositoryTaskEditorInput) input);

		if (taskData != null) {
			editorInput.setToolTipText(taskData.getLabel());
			taskOutlineModel = RepositoryTaskOutlineNode.parseBugReport(taskData);
		}
		hasAttributeChanges = hasVisibleAttributeChanges();
		TasksUiPlugin.getTaskListManager().getTaskList().addChangeListener(TASKLIST_CHANGE_LISTENER);
	}

	protected void initTaskEditor(IEditorSite site, RepositoryTaskEditorInput input) {
		changedAttributes = new HashSet<RepositoryTaskAttribute>();
		editorInput = input;
		repositoryTask = editorInput.getRepositoryTask();
		repository = editorInput.getRepository();
		taskData = editorInput.getTaskData();
		connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getConnectorKind());
		commentSortIsUp = TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(PREF_SORT_ORDER_PREFIX+repository.getConnectorKind());	
		setSite(site);
		setInput(input);

		isDirty = false;
	}

	public AbstractTask getRepositoryTask() {
		return repositoryTask;
	}

	// @Override
	// public void markDirty(boolean dirty) {
	// if (repositoryTask != null) {
	// repositoryTask.setDirty(dirty);
	// }
	// super.markDirty(dirty);
	// }

//	/**
//	 * Update task state
//	 */
//	protected void updateTask() {
//		if (taskData == null)
//			return;
//		if (repositoryTask != null) {
//			TasksUiPlugin.getSynchronizationManager().saveOutgoing(repositoryTask, changedAttributes);
//		}
//		if (parentEditor != null) {
//			parentEditor.notifyTaskChanged();
//		}
//		markDirty(false);
//	}

	protected abstract void validateInput();

	/**
	 * Creates a new <code>AbstractTaskEditor</code>.
	 */
	public AbstractRepositoryTaskEditor(FormEditor editor) {
		// set the scroll increments so the editor scrolls normally with the
		// scroll wheel
		super(editor, "id", "label"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected boolean supportsCommentSort() {
		return false;
	}
	

	@Override
	protected void createFormContent(final IManagedForm managedForm) {	
		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		colorIncoming = themeManager.getCurrentTheme().getColorRegistry().get(
				TaskListColorsAndFonts.THEME_COLOR_TASKS_INCOMING_BACKGROUND);

		super.createFormContent(managedForm);
		form = managedForm.getForm();
		form.setRedraw(false);

		toolkit = managedForm.getToolkit();
		registerDropListener(form);

		editorComposite = form.getBody();
		GridLayout editorLayout = new GridLayout();
		editorComposite.setLayout(editorLayout);
		editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));


		if (taskData != null) {
			createSections();
		}

		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(repository.getConnectorKind());
		if (connectorUi == null) {
			parentEditor.setMessage("The editor may not be fully loaded",
					IMessageProvider.INFORMATION, new HyperlinkAdapter() {
						@Override
						public void linkActivated(HyperlinkEvent e) {
							refreshEditor();
						}
					});
		}

		updateHeaderControls();
		if (summaryTextViewer != null) {
			summaryTextViewer.getTextWidget().setFocus();
		}

		form.setRedraw(true);
	}

	private void updateHeaderControls() {
		
		if (taskData == null) {
			parentEditor.setMessage(
					"Task data not available. Press synchronize button (right) to retrieve latest data.",
					IMessageProvider.WARNING, new HyperlinkAdapter() {
						@Override
						public void linkActivated(HyperlinkEvent e) {
							if (synchronizeEditorAction != null) {
								synchronizeEditorAction.run();
							}
						}
					});
		}
		
		ControlContribution repositoryLabelControl = new ControlContribution("Title") { //$NON-NLS-1$
			@Override
			protected Control createControl(Composite parent) {
				Composite composite = toolkit.createComposite(parent);
				composite.setLayout(new RowLayout());
				composite.setBackground(null);
				String label = repository.getRepositoryLabel();
				if (label.indexOf("//") != -1) {
					label = label.substring((repository.getUrl().indexOf("//") + 2));
				}

				Hyperlink link = new Hyperlink(composite, SWT.NONE);
				link.setText(label);
				link.setFont(TITLE_FONT);
				link.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
				link.addHyperlinkListener(new HyperlinkAdapter() {

					@Override
					public void linkActivated(HyperlinkEvent e) {
						TasksUiUtil.openEditRepositoryWizard(repository);
					}
				});

				return composite;
			}
		};

		if (parentEditor.getTopForm() != null) {
			IToolBarManager toolBarManager = parentEditor.getTopForm().getToolBarManager();

			// NOTE: the update call should not be needed, but toolbar can fail to show last item?
			toolBarManager.removeAll();
			toolBarManager.update(true);
			
			toolBarManager.add(repositoryLabelControl);
			fillToolBar(toolBarManager);

			if (repositoryTask != null && taskData != null && !taskData.isNew()) {
				activateAction = new ToggleTaskActivationAction(repositoryTask, toolBarManager);
				toolBarManager.add(new Separator("activation"));
				toolBarManager.add(activateAction);
			}

			toolBarManager.update(true);
		}
	}
	
	/**
	 * Override for customizing the toolbar.
	 * 
	 * @since 2.1 (NOTE: likely to change for 3.0)
	 */
	protected void fillToolBar(IToolBarManager toolBarManager) {
		
		if((taskData != null && !taskData.isNew()) || repositoryTask != null) {
			synchronizeEditorAction = new SynchronizeEditorAction();
			synchronizeEditorAction.selectionChanged(new StructuredSelection(this));
			toolBarManager.add(synchronizeEditorAction);			
		}
		
		if (taskData != null && !taskData.isNew()) {
			if (repositoryTask != null) {
				clearOutgoingAction = new ClearOutgoingAction(Collections.singletonList((AbstractTaskContainer)repositoryTask));

				if (clearOutgoingAction.isEnabled()) {
					toolBarManager.add(clearOutgoingAction);
				}
				
				newSubTaskAction = new NewSubTaskAction();
				newSubTaskAction.selectionChanged(newSubTaskAction, new StructuredSelection(getRepositoryTask()));
				if (newSubTaskAction.isEnabled()) {
					toolBarManager.add(newSubTaskAction);
				}
			}

			if (getHistoryUrl() != null) {
				historyAction = new Action() {
					@Override
					public void run() {
						TasksUiUtil.openUrl(getHistoryUrl(), false);
					}
				};

				historyAction.setImageDescriptor(TasksUiImages.TASK_REPOSITORY_HISTORY);
				historyAction.setToolTipText(LABEL_HISTORY);
				toolBarManager.add(historyAction);
			}

			if (connector != null) {
				String taskUrl = connector.getTaskUrl(taskData.getRepositoryUrl(), taskData.getTaskKey());
				if (taskUrl == null && repositoryTask != null && repositoryTask.hasValidUrl()) {
					taskUrl = repositoryTask.getUrl();
				}

				final String taskUrlToOpen = taskUrl;

				if (taskUrlToOpen != null) {
					openBrowserAction = new Action() {
						@Override
						public void run() {
							TasksUiUtil.openUrl(taskUrlToOpen, false);
						}
					};

					openBrowserAction.setImageDescriptor(TasksUiImages.BROWSER_OPEN_TASK);
					openBrowserAction.setToolTipText("Open with Web Browser");
					toolBarManager.add(openBrowserAction);
				}
			}
		}
	}

	private void createSections() {
		createSummaryLayout(editorComposite);

		Composite attribComp = createAttributeSection();
		createAttributeLayout(attribComp);
		createCustomAttributeLayout(attribComp);

		createRelatedBugsSection(editorComposite);

		if (showAttachments) {
			createAttachmentLayout(editorComposite);
		}
		createDescriptionLayout(editorComposite);
		createCommentLayout(editorComposite);
		createNewCommentLayout(editorComposite);
		Composite bottomComposite = toolkit.createComposite(editorComposite);
		bottomComposite.setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(bottomComposite);

		createActionsLayout(bottomComposite);
		createPeopleLayout(bottomComposite);
		bottomComposite.pack(true);
		form.reflow(true);
		getSite().getPage().addSelectionListener(selectionListener);
		getSite().setSelectionProvider(selectionProvider);
	}

	private void removeSections() {
		menu = editorComposite.getMenu();
		setMenu(editorComposite, null);
		for (Control control : editorComposite.getChildren()) {
			control.dispose();
		}
	}

	/**
	 * @author Raphael Ackermann (modifications) (bug 195514)
	 */
	protected void createSummaryLayout(Composite composite) {
		addSummaryText(composite);
		if (summaryTextViewer != null) {
			summaryTextViewer.prependVerifyKeyListener(new TabVerifyKeyListener());
		}

		headerInfoComposite = toolkit.createComposite(composite);
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

		String idLabel = (repositoryTask != null) ? repositoryTask.getTaskKey() : taskData.getTaskKey();
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
		final AbstractTaskDataHandler taskDataManager = connector.getTaskDataHandler();
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
	 * Utility method to create text field sets background to TaskListColorsAndFonts.COLOR_ATTRIBUTE_CHANGED if
	 * attribute has changed.
	 * 
	 * @param composite
	 * @param attribute
	 * @param style
	 */
	protected Text createTextField(Composite composite, RepositoryTaskAttribute attribute, int style) {
		String value;
		if (attribute == null || attribute.getValue() == null) {
			value = "";
		} else {
			value = attribute.getValue();
		}

		final Text text;
		if ((SWT.READ_ONLY & style) == SWT.READ_ONLY) {
			text = new Text(composite, style);
			toolkit.adapt(text, true, true);
			text.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
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
			text.setBackground(colorIncoming);
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
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
		return label;
	}

	public String getSectionLabel(SECTION_NAME labelName) {
		return labelName.getPrettyName();
	}

	protected Composite createAttributeSection() {
		attributesSection = createSection(editorComposite, getSectionLabel(SECTION_NAME.ATTRIBTUES_SECTION), expandedStateAttributes || hasAttributeChanges);

		Composite toolbarComposite = toolkit.createComposite(attributesSection);
		toolbarComposite.setBackground(null);
		RowLayout rowLayout = new RowLayout();
		rowLayout.marginTop = 0;
		rowLayout.marginBottom = 0;
		toolbarComposite.setLayout(rowLayout);
		ResetRepositoryConfigurationAction repositoryConfigRefresh = new ResetRepositoryConfigurationAction() {
			@Override
			public void performUpdate(TaskRepository repository, AbstractRepositoryConnector connector,
					IProgressMonitor monitor) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					public void run() {
						setGlobalBusy(true);

					}
				});
				try {
					super.performUpdate(repository, connector, monitor);
					if (connector != null) {
						TasksUiPlugin.getSynchronizationManager().synchronize(connector, repositoryTask, true,
								new JobChangeAdapter() {

							@Override
							public void done(IJobChangeEvent event) {
								PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

									public void run() {
										refreshEditor();
									}
								});

							}
						});
					}
				} catch (Exception e) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

						public void run() {
							refreshEditor();
						}
					});
				}
			}
		};
		repositoryConfigRefresh.setImageDescriptor(TasksUiImages.REPOSITORY_SYNCHRONIZE);
		repositoryConfigRefresh.selectionChanged(new StructuredSelection(repository));
		repositoryConfigRefresh.setToolTipText("Refresh attributes");

		ToolBarManager barManager = new ToolBarManager(SWT.FLAT);
		barManager.add(repositoryConfigRefresh);
		repositoryConfigRefresh.setEnabled(supportsRefreshAttributes());
		barManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		barManager.createControl(toolbarComposite);
		attributesSection.setTextClient(toolbarComposite);

		// Attributes Composite- this holds all the combo fields and text fields
		final Composite attribComp = toolkit.createComposite(attributesSection);
		attribComp.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Control focus = event.display.getFocusControl();
				if (focus instanceof Text && ((Text) focus).getEditable() == false) {
					form.setFocus();
				}
			}
		});
		attributesSection.setClient(attribComp);

		GridLayout attributesLayout = new GridLayout();
		attributesLayout.numColumns = 4;
		attributesLayout.horizontalSpacing = 5;
		attributesLayout.verticalSpacing = 4;
		attribComp.setLayout(attributesLayout);

		GridData attributesData = new GridData(GridData.FILL_BOTH);
		attributesData.horizontalSpan = 1;
		attributesData.grabExcessVerticalSpace = false;
		attribComp.setLayoutData(attributesData);

		return attribComp;
	}

	/**
	 * @since 2.2
	 */
	protected boolean supportsRefreshAttributes() {
		return true;
	}

	/**
	 * Creates the attribute section, which contains most of the basic attributes of the task (some of which are
	 * editable).
	 */
	protected void createAttributeLayout(Composite attributesComposite) {
		int numColumns = ((GridLayout) attributesComposite.getLayout()).numColumns;
		int currentCol = 1;

		for (final RepositoryTaskAttribute attribute : taskData.getAttributes()) {
			if (attribute.isHidden()) {
				continue;
			}

			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			data.horizontalSpan = 1;

			if (attribute.hasOptions() && !attribute.isReadOnly()) {
				Label label = createLabel(attributesComposite, attribute);
				GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
				final CCombo attributeCombo = new CCombo(attributesComposite, SWT.FLAT | SWT.READ_ONLY);
				toolkit.adapt(attributeCombo, true, true);
				attributeCombo.setFont(TEXT_FONT);
				attributeCombo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				if (hasChanged(attribute)) {
					attributeCombo.setBackground(colorIncoming);
				}
				attributeCombo.setLayoutData(data);

				List<String> values = attribute.getOptions();
				if (values != null) {
					for (String val : values) {
						if (val != null) {
							attributeCombo.add(val);
						}
					}
				}

				String value = attribute.getValue();
				if (value == null) {
					value = "";
				}
				if (attributeCombo.indexOf(value) != -1) {
					attributeCombo.select(attributeCombo.indexOf(value));
				}
				attributeCombo.clearSelection();
				attributeCombo.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						if (attributeCombo.getSelectionIndex() > -1) {
							String sel = attributeCombo.getItem(attributeCombo.getSelectionIndex());
							attribute.setValue(sel);
							attributeChanged(attribute);
							attributeCombo.clearSelection();
						}
					}
				});
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

					if (hasContentAssist(attribute)) {
						ContentAssistCommandAdapter adapter = applyContentAssist(text,
								createContentProposalProvider(attribute));

						ILabelProvider propsalLabelProvider = createProposalLabelProvider(attribute);
						if (propsalLabelProvider != null) {
							adapter.setLabelProvider(propsalLabelProvider);
						}
						adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
					}
				}

				currentCol += 2;
			}

			if (currentCol > numColumns) {
				currentCol -= numColumns;
			}
		}

		// make sure that we are in the first column
		if (currentCol > 1) {
			while (currentCol <= numColumns) {
				toolkit.createLabel(attributesComposite, "");
				currentCol++;
			}
		}

		toolkit.paintBordersFor(attributesComposite);
	}

	/**
	 * Adds a related bugs section to the bug editor
	 */
	protected void createRelatedBugsSection(Composite composite) {
//		Section relatedBugsSection = createSection(editorComposite, getSectionLabel(SECTION_NAME.RELATEDBUGS_SECTION));
//		Composite relatedBugsComposite = toolkit.createComposite(relatedBugsSection);
//		relatedBugsComposite.setLayout(new GridLayout(4, false));
//		relatedBugsComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
//		relatedBugsSection.setClient(relatedBugsComposite);
//		relatedBugsSection.setExpanded(repositoryTask == null);
//
//		List<AbstractDuplicateDetector> allCollectors = new ArrayList<AbstractDuplicateDetector>();
//		if (getDuplicateSearchCollectorsList() != null) {
//			allCollectors.addAll(getDuplicateSearchCollectorsList());
//		}
//		if (!allCollectors.isEmpty()) {
//			duplicateDetectorLabel = new Label(relatedBugsComposite, SWT.LEFT);
//			duplicateDetectorLabel.setText(LABEL_SELECT_DETECTOR);
//
//			duplicateDetectorChooser = new CCombo(relatedBugsComposite, SWT.FLAT | SWT.READ_ONLY | SWT.BORDER);
//
//			duplicateDetectorChooser.setLayoutData(GridDataFactory.swtDefaults().hint(150, SWT.DEFAULT).create());
//			duplicateDetectorChooser.setFont(TEXT_FONT);
//
//			Collections.sort(allCollectors, new Comparator<AbstractDuplicateDetector>() {
//
//				public int compare(AbstractDuplicateDetector c1, AbstractDuplicateDetector c2) {
//					return c1.getName().compareToIgnoreCase(c2.getName());
//				}
//
//			});
//
//			for (AbstractDuplicateDetector detector : allCollectors) {
//				duplicateDetectorChooser.add(detector.getName());
//			}
//
//			duplicateDetectorChooser.select(0);
//			duplicateDetectorChooser.setEnabled(true);
//			duplicateDetectorChooser.setData(allCollectors);
//
//			if (allCollectors.size() > 0) {
//
//				searchForDuplicates = toolkit.createButton(relatedBugsComposite, LABEL_SEARCH_DUPS, SWT.NONE);
//				GridData searchDuplicatesButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
//				searchForDuplicates.setLayoutData(searchDuplicatesButtonData);
//				searchForDuplicates.addListener(SWT.Selection, new Listener() {
//					public void handleEvent(Event e) {
//						searchForDuplicates();
//					}
//				});
//			}
//		} else {
//			Label label = new Label(relatedBugsComposite, SWT.LEFT);
//			label.setText(LABEL_NO_DETECTOR);
//		}
	}

	protected SearchHitCollector getDuplicateSearchCollector(String name) {
		String duplicateDetectorName = name.equals("default") ? "Stack Trace" : name;
		Set<AbstractDuplicateDetector> allDetectors = getDuplicateSearchCollectorsList();

		for (AbstractDuplicateDetector detector : allDetectors) {
			if (detector.getName().equals(duplicateDetectorName)) {
				return detector.getSearchHitCollector(repository, taskData);
			}
		}
		// didn't find it
		return null;
	}

	protected Set<AbstractDuplicateDetector> getDuplicateSearchCollectorsList() {
		Set<AbstractDuplicateDetector> duplicateDetectors = new HashSet<AbstractDuplicateDetector>();
		for (AbstractDuplicateDetector abstractDuplicateDetector : TasksUiPlugin.getDefault()
				.getDuplicateSearchCollectorsList()) {
			if (abstractDuplicateDetector.getKind() == null
					|| abstractDuplicateDetector.getKind().equals(getConnector().getConnectorKind())) {
				duplicateDetectors.add(abstractDuplicateDetector);
			}
		}
		return duplicateDetectors;
	}

	public boolean searchForDuplicates() {
		String duplicateDetectorName = duplicateDetectorChooser.getItem(duplicateDetectorChooser.getSelectionIndex());

		SearchHitCollector collector = getDuplicateSearchCollector(duplicateDetectorName);
		if (collector != null) {
			NewSearchUI.runQueryInBackground(collector);
			return true;
		}

		return false;
	}

	/**
	 * Adds content assist to the given text field.
	 * 
	 * @param text
	 *            text field to decorate.
	 * @param proposalProvider
	 *            instance providing content proposals
	 * @return the ContentAssistCommandAdapter for the field.
	 */
	protected ContentAssistCommandAdapter applyContentAssist(Text text, IContentProposalProvider proposalProvider) {
		ControlDecoration controlDecoration = new ControlDecoration(text, (SWT.TOP | SWT.LEFT));
		controlDecoration.setMarginWidth(0);
		controlDecoration.setShowHover(true);
		controlDecoration.setShowOnlyOnFocus(true);

		FieldDecoration contentProposalImage = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
		controlDecoration.setImage(contentProposalImage.getImage());

		TextContentAdapter textContentAdapter = new TextContentAdapter();

		ContentAssistCommandAdapter adapter = new ContentAssistCommandAdapter(text, textContentAdapter,
				proposalProvider, "org.eclipse.ui.edit.text.contentAssist.proposals", new char[0]);

		IBindingService bindingService = (IBindingService) PlatformUI.getWorkbench().getService(IBindingService.class);
		controlDecoration.setDescriptionText(NLS.bind("Content Assist Available ({0})",
				bindingService.getBestActiveBindingFormattedFor(adapter.getCommandId())));

		return adapter;
	}

	/**
	 * Creates an IContentProposalProvider to provide content assist proposals for the given attribute.
	 * 
	 * @param attribute
	 *            attribute for which to provide content assist.
	 * @return the IContentProposalProvider.
	 */
	protected IContentProposalProvider createContentProposalProvider(RepositoryTaskAttribute attribute) {
		return new PersonProposalProvider(repositoryTask, taskData);
	}

	/**
	 * Creates an IContentProposalProvider to provide content assist proposals for the given operation.
	 * 
	 * @param operation
	 *            operation for which to provide content assist.
	 * @return the IContentProposalProvider.
	 */
	protected IContentProposalProvider createContentProposalProvider(RepositoryOperation operation) {

		return new PersonProposalProvider(repositoryTask, taskData);
	}

	protected ILabelProvider createProposalLabelProvider(RepositoryTaskAttribute attribute) {
		return new PersonProposalLabelProvider();
	}

	protected ILabelProvider createProposalLabelProvider(RepositoryOperation operation) {

		return new PersonProposalLabelProvider();
	}

	/**
	 * Called to check if there's content assist available for the given attribute.
	 * 
	 * @param attribute
	 *            the attribute
	 * @return true if content assist is available for the specified attribute.
	 */
	protected boolean hasContentAssist(RepositoryTaskAttribute attribute) {
		return false;
	}

	/**
	 * Called to check if there's content assist available for the given operation.
	 * 
	 * @param operation
	 *            the operation
	 * @return true if content assist is available for the specified operation.
	 */
	protected boolean hasContentAssist(RepositoryOperation operation) {
		return false;
	}

	/**
	 * Adds a text editor with spell checking enabled to display and edit the task's summary.
	 * 
	 * @author Raphael Ackermann (modifications) (bug 195514)
	 * @param attributesComposite
	 *            The composite to add the text editor to.
	 */
	protected void addSummaryText(Composite attributesComposite) {
		Composite summaryComposite = toolkit.createComposite(attributesComposite);
		GridLayout summaryLayout = new GridLayout(2, false);
		summaryLayout.verticalSpacing = 0;
		summaryLayout.marginHeight = 2;
		summaryComposite.setLayout(summaryLayout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(summaryComposite);

		if (taskData != null) {
			RepositoryTaskAttribute attribute = taskData.getAttribute(RepositoryTaskAttribute.SUMMARY);
			if (attribute == null) {
				taskData.setAttributeValue(RepositoryTaskAttribute.SUMMARY, "");
				attribute = taskData.getAttribute(RepositoryTaskAttribute.SUMMARY);
			}

			final RepositoryTaskAttribute summaryAttribute = attribute;

			summaryTextViewer = addTextEditor(repository, summaryComposite, attribute.getValue(), true, SWT.FLAT
					| SWT.SINGLE);
			Composite hiddenComposite = new Composite(summaryComposite, SWT.NONE);
			hiddenComposite.setLayout(new GridLayout());
			GridData hiddenLayout = new GridData();
			hiddenLayout.exclude = true;
			hiddenComposite.setLayoutData(hiddenLayout);

			// bugg#210695 - work around for 2.0 api breakage
			summaryText = new Text(hiddenComposite, SWT.NONE);
			summaryText.setText(attribute.getValue());
			summaryText.addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					if (summaryTextViewer != null && !summaryTextViewer.getTextWidget().isDisposed()) {
						String newValue = summaryText.getText();
						String oldValue = summaryTextViewer.getTextWidget().getText();
						if (!newValue.equals(oldValue)) {
							summaryTextViewer.getTextWidget().setText(newValue);
							summaryAttribute.setValue(newValue);
							attributeChanged(summaryAttribute);
						}
					}
				}

				public void keyReleased(KeyEvent e) {
					// ignore
				}
			});

			summaryText.addFocusListener(new FocusListener() {

				public void focusGained(FocusEvent e) {
					summaryTextViewer.getTextWidget().setFocus();
				}

				public void focusLost(FocusEvent e) {
					// ignore
				}
			});

			summaryTextViewer.setEditable(true);
			summaryTextViewer.getTextWidget().setIndent(2);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(summaryTextViewer.getControl());

			if (hasChanged(attribute)) {
				summaryTextViewer.getTextWidget().setBackground(colorIncoming);
			}
			summaryTextViewer.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

			summaryTextViewer.addTextListener(new ITextListener() {
				public void textChanged(TextEvent event) {
					String newValue = summaryTextViewer.getTextWidget().getText();
					if (!newValue.equals(summaryAttribute.getValue())) {
						summaryAttribute.setValue(newValue);
						attributeChanged(summaryAttribute);
					}
					if (summaryText != null && !newValue.equals(summaryText.getText())) {
						summaryText.setText(newValue);
					}
				}
			});

		}
		toolkit.paintBordersFor(summaryComposite);
	}

	protected boolean supportsAttachmentDelete() {
		return false;
	}

	protected void deleteAttachment(RepositoryAttachment attachment) {

	}

	protected void createAttachmentLayout(Composite composite) {
		// TODO: expand to show new attachments
		Section section = createSection(composite, getSectionLabel(SECTION_NAME.ATTACHMENTS_SECTION), false);
		section.setText(section.getText() + " (" + taskData.getAttachments().size() + ")");
		final Composite attachmentsComposite = toolkit.createComposite(section);
		attachmentsComposite.setLayout(new GridLayout(1, false));
		attachmentsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		section.setClient(attachmentsComposite);

		if (taskData.getAttachments().size() > 0) {

			attachmentsTable = toolkit.createTable(attachmentsComposite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
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

			attachmentsTableViewer = new TableViewer(attachmentsTable);
			attachmentsTableViewer.setUseHashlookup(true);
			attachmentsTableViewer.setColumnProperties(attachmentsColumns);
			ColumnViewerToolTipSupport.enableFor(attachmentsTableViewer, ToolTip.NO_RECREATE);

			final AbstractTaskDataHandler offlineHandler = connector.getTaskDataHandler();
			if (offlineHandler != null) {
				attachmentsTableViewer.setSorter(new ViewerSorter() {
					@Override
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

			attachmentsTableViewer.setContentProvider(new AttachmentsTableContentProvider(taskData.getAttachments()));

			attachmentsTableViewer.setLabelProvider(new AttachmentTableLabelProvider(this, new LabelProvider(),
					PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));

			attachmentsTableViewer.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(DoubleClickEvent event) {
					if (!event.getSelection().isEmpty()) {
						StructuredSelection selection = (StructuredSelection) event.getSelection();
						RepositoryAttachment attachment = (RepositoryAttachment) selection.getFirstElement();
						TasksUiUtil.openUrl(attachment.getUrl(), false);
					}
				}
			});

			attachmentsTableViewer.setInput(taskData);

			final Action openWithBrowserAction = new Action(LABEL_BROWSER) {
				@Override
				public void run() {
					RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
					if (attachment != null) {
						TasksUiUtil.openUrl(attachment.getUrl(), false);
					}
				}
			};

			final Action openWithDefaultAction = new Action(LABEL_DEFAULT_EDITOR) {
				@Override
				public void run() {
					// browser shortcut
					RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
					if (attachment == null) {
						return;
					}

					if (attachment.getContentType().endsWith(CTYPE_HTML)) {
						TasksUiUtil.openUrl(attachment.getUrl(), false);
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
						StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unable to open editor for: " + attachment.getDescription(), e));
					}
				}
			};

			final Action openWithTextEditorAction = new Action(LABEL_TEXT_EDITOR) {
				@Override
				public void run() {
					RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
					IStorageEditorInput input = new RepositoryAttachmentEditorInput(repository, attachment);
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					if (page == null) {
						return;
					}

					try {
						page.openEditor(input, "org.eclipse.ui.DefaultTextEditor");
					} catch (PartInitException e) {
						StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unable to open editor for: " + attachment.getDescription(), e));
					}
				}
			};

			final Action saveAction = new Action(LABEL_SAVE) {
				@Override
				public void run() {
					RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
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
					// Check if the dialog was canceled or an error occurred
					if (filePath == null) {
						return;
					}

					DownloadAttachmentJob job = new DownloadAttachmentJob(attachment, new File(filePath));
					job.setUser(true);
					job.schedule();
				}
			};

			final Action copyURLToClipAction = new Action(LABEL_COPY_URL_TO_CLIPBOARD) {
				@Override
				public void run() {
					RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
					Clipboard clip = new Clipboard(PlatformUI.getWorkbench().getDisplay());
					clip.setContents(new Object[] { attachment.getUrl() },
							new Transfer[] { TextTransfer.getInstance() });
					clip.dispose();
				}
			};

			final Action copyToClipAction = new Action(LABEL_COPY_TO_CLIPBOARD) {
				@Override
				public void run() {
					RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
					CopyAttachmentToClipboardJob job = new CopyAttachmentToClipboardJob(attachment);
					job.setUser(true);
					job.schedule();
				}
			};

			final MenuManager popupMenu = new MenuManager();
			final Menu menu = popupMenu.createContextMenu(attachmentsTable);
			attachmentsTable.setMenu(menu);
			final MenuManager openMenu = new MenuManager("Open With");
			popupMenu.addMenuListener(new IMenuListener() {
				public void menuAboutToShow(IMenuManager manager) {
					popupMenu.removeAll();

					IStructuredSelection selection = (IStructuredSelection) attachmentsTableViewer.getSelection();
					if (selection.isEmpty()) {
						return;
					}

					if (selection.size() == 1) {
						RepositoryAttachment att = (RepositoryAttachment) ((StructuredSelection) selection).getFirstElement();

						// reinitialize open menu
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

						popupMenu.add(copyURLToClipAction);
						if (att.getContentType().startsWith(CTYPE_TEXT) || att.getContentType().endsWith("xml")) {
							popupMenu.add(copyToClipAction);
						}
					}
					popupMenu.add(new Separator("actions"));

					// TODO: use workbench mechanism for this?
					ObjectActionContributorManager.getManager().contributeObjectActions(
							AbstractRepositoryTaskEditor.this, popupMenu, attachmentsTableViewer);
				}
			});
		} else {
			Label label = toolkit.createLabel(attachmentsComposite, "No attachments");
			registerDropListener(label);
		}

		final Composite attachmentControlsComposite = toolkit.createComposite(attachmentsComposite);
		attachmentControlsComposite.setLayout(new GridLayout(2, false));
		attachmentControlsComposite.setLayoutData(new GridData(GridData.BEGINNING));

		Button attachFileButton = toolkit.createButton(attachmentControlsComposite, AttachAction.LABEL, SWT.PUSH);
		attachFileButton.setImage(WorkbenchImages.getImage(ISharedImages.IMG_OBJ_FILE));

		Button attachScreenshotButton = toolkit.createButton(attachmentControlsComposite, AttachScreenshotAction.LABEL,
				SWT.PUSH);
		attachScreenshotButton.setImage(TasksUiImages.getImage(TasksUiImages.IMAGE_CAPTURE));

		final AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(),
				taskData.getId());
		if (task == null) {
			attachFileButton.setEnabled(false);
			attachScreenshotButton.setEnabled(false);
		}

		attachFileButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			public void widgetSelected(SelectionEvent e) {
				AbstractTaskEditorAction attachFileAction = new AttachAction();
				attachFileAction.selectionChanged(new StructuredSelection(task));
				attachFileAction.setEditor(parentEditor);
				attachFileAction.run();
			}
		});

		attachScreenshotButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			public void widgetSelected(SelectionEvent e) {
				AttachScreenshotAction attachScreenshotAction = new AttachScreenshotAction();
				attachScreenshotAction.selectionChanged(new StructuredSelection(task));
				attachScreenshotAction.setEditor(parentEditor);
				attachScreenshotAction.run();
			}
		});

		Button deleteAttachmentButton = null;
		if (supportsAttachmentDelete()) {
			deleteAttachmentButton = toolkit.createButton(attachmentControlsComposite, "Delete Attachment...", SWT.PUSH);

			deleteAttachmentButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					// ignore
				}

				public void widgetSelected(SelectionEvent e) {
					AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(),
							taskData.getId());
					if (task == null) {
						// Should not happen
						return;
					}
					if (AbstractRepositoryTaskEditor.this.isDirty
							|| task.getSynchronizationState().equals(RepositoryTaskSyncState.OUTGOING)) {
						MessageDialog.openInformation(attachmentsComposite.getShell(),
								"Task not synchronized or dirty editor",
						"Commit edits or synchronize task before deleting attachments.");
						return;
					} else {
						if (attachmentsTableViewer != null
								&& attachmentsTableViewer.getSelection() != null
								&& ((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement() != null) {
							RepositoryAttachment attachment = (RepositoryAttachment) (((StructuredSelection) attachmentsTableViewer.getSelection()).getFirstElement());
							deleteAttachment(attachment);
							submitToRepository();
						}
					}
				}
			});

		}
		registerDropListener(section);
		registerDropListener(attachmentsComposite);
		registerDropListener(attachFileButton);
		if (supportsAttachmentDelete()) {
			registerDropListener(deleteAttachmentButton);
		}
	}

	private void registerDropListener(final Control control) {
		DropTarget target = new DropTarget(control, DND.DROP_COPY | DND.DROP_DEFAULT);
		final TextTransfer textTransfer = TextTransfer.getInstance();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		Transfer[] types = new Transfer[] { textTransfer, fileTransfer };
		target.setTransfer(types);

		// Adapted from eclipse.org DND Article by Veronika Irvine, IBM OTI Labs
		// http://www.eclipse.org/articles/Article-SWT-DND/DND-in-SWT.html#_dt10D
		target.addDropListener(new RepositoryTaskEditorDropListener(this, fileTransfer, textTransfer, control));
	}

	protected void createDescriptionLayout(Composite composite) {
		Section descriptionSection = createSection(composite, getSectionLabel(SECTION_NAME.DESCRIPTION_SECTION));
		final Composite sectionComposite = toolkit.createComposite(descriptionSection);
		descriptionSection.setClient(sectionComposite);
		GridLayout addCommentsLayout = new GridLayout();
		addCommentsLayout.numColumns = 1;
		sectionComposite.setLayout(addCommentsLayout);

		RepositoryTaskAttribute attribute = taskData.getDescriptionAttribute();
		if (attribute != null && !attribute.isReadOnly()) {
			if (getRenderingEngine() != null) {
				// composite with StackLayout to hold text editor and preview widget
				Composite descriptionComposite = toolkit.createComposite(sectionComposite);
				descriptionComposite.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				GridData descriptionGridData = new GridData(GridData.FILL_BOTH);
				descriptionGridData.widthHint = DESCRIPTION_WIDTH;
				descriptionGridData.minimumHeight = DESCRIPTION_HEIGHT;
				descriptionGridData.grabExcessHorizontalSpace = true;
				descriptionComposite.setLayoutData(descriptionGridData);
				final StackLayout descriptionLayout = new StackLayout();
				descriptionComposite.setLayout(descriptionLayout);

				descriptionTextViewer = addTextEditor(repository, descriptionComposite, taskData.getDescription(),
						true, SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
				descriptionLayout.topControl = descriptionTextViewer.getControl();
				descriptionComposite.layout();

				// composite for edit/preview button
				Composite buttonComposite = toolkit.createComposite(sectionComposite);
				buttonComposite.setLayout(new GridLayout());
				createPreviewButton(buttonComposite, descriptionTextViewer, descriptionComposite, descriptionLayout);
			} else {
				descriptionTextViewer = addTextEditor(repository, sectionComposite, taskData.getDescription(), true,
						SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
				final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				// wrap text at this margin, see comment below
				gd.widthHint = DESCRIPTION_WIDTH;
				gd.minimumHeight = DESCRIPTION_HEIGHT;
				gd.grabExcessHorizontalSpace = true;
				descriptionTextViewer.getControl().setLayoutData(gd);
				descriptionTextViewer.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				// the goal is to make the text viewer as big as the text so it does not require scrolling when first drawn 
				// on screen: when the descriptionTextViewer calculates its height it wraps the text according to the widthHint 
				// which does not reflect the actual size of the widget causing the widget to be taller 
				// (actual width > gd.widhtHint) or shorter (actual width < gd.widthHint) therefore the widthHint is tweaked 
				// once in the listener  
				sectionComposite.addControlListener(new ControlAdapter() {
					private boolean first;

					@Override
					public void controlResized(ControlEvent e) {
						if (!first) {
							first = true;
							int width = sectionComposite.getSize().x;
							Point size = descriptionTextViewer.getTextWidget().computeSize(width, SWT.DEFAULT, true);
							// limit width to parent widget
							gd.widthHint = width;
							// limit height to avoid dynamic resizing of the text widget
							gd.heightHint = Math.min(Math.max(DESCRIPTION_HEIGHT, size.y), DESCRIPTION_HEIGHT * 4);
							sectionComposite.layout();
						}
					}
				});
			}
			descriptionTextViewer.setEditable(true);
			descriptionTextViewer.addTextListener(new ITextListener() {
				public void textChanged(TextEvent event) {
					String newValue = descriptionTextViewer.getTextWidget().getText();
					RepositoryTaskAttribute attribute = taskData.getAttribute(RepositoryTaskAttribute.DESCRIPTION);
					if (attribute != null && !newValue.equals(attribute.getValue())) {
						attribute.setValue(newValue);
						attributeChanged(attribute);
						taskData.setDescription(newValue);
					}
				}
			});
			StyledText styledText = descriptionTextViewer.getTextWidget();
			controlBySelectableObject.put(taskData.getDescription(), styledText);
		} else {
			String text = taskData.getDescription();
			descriptionTextViewer = addTextViewer(repository, sectionComposite, text, SWT.MULTI | SWT.WRAP);
			StyledText styledText = descriptionTextViewer.getTextWidget();
			GridDataFactory.fillDefaults().hint(DESCRIPTION_WIDTH, SWT.DEFAULT).applyTo(
					descriptionTextViewer.getControl());

			controlBySelectableObject.put(text, styledText);
		}

		if (hasChanged(taskData.getAttribute(RepositoryTaskAttribute.DESCRIPTION))) {
			descriptionTextViewer.getTextWidget().setBackground(colorIncoming);
		}
		descriptionTextViewer.getTextWidget().addListener(SWT.FocusIn, new DescriptionListener());

		Composite replyComp = toolkit.createComposite(descriptionSection);
		replyComp.setLayout(new RowLayout());
		replyComp.setBackground(null);

		createReplyHyperlink(0, replyComp, taskData.getDescription());
		descriptionSection.setTextClient(replyComp);
		addDuplicateDetection(sectionComposite);
		toolkit.paintBordersFor(sectionComposite);
	}

	protected ImageHyperlink createReplyHyperlink(final int commentNum, Composite composite, final String commentBody) {
		final ImageHyperlink replyLink = new ImageHyperlink(composite, SWT.NULL);
		toolkit.adapt(replyLink, true, true);
		replyLink.setImage(TasksUiImages.getImage(TasksUiImages.REPLY));
		replyLink.setToolTipText(LABEL_REPLY);
		// no need for the background - transparency will take care of it
		replyLink.setBackground(null);
		// replyLink.setBackground(section.getTitleBarGradientBackground());
		replyLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
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

		return replyLink;
	}

	protected void addDuplicateDetection(Composite composite) {
		List<AbstractDuplicateDetector> allCollectors = new ArrayList<AbstractDuplicateDetector>();
		if (getDuplicateSearchCollectorsList() != null) {
			allCollectors.addAll(getDuplicateSearchCollectorsList());
		}
		if (!allCollectors.isEmpty()) {
			Section duplicatesSection = toolkit.createSection(composite, ExpandableComposite.TWISTIE
					| ExpandableComposite.SHORT_TITLE_BAR);
			duplicatesSection.setText(LABEL_SELECT_DETECTOR);
			duplicatesSection.setLayout(new GridLayout());
			GridDataFactory.fillDefaults().indent(SWT.DEFAULT, 15).applyTo(duplicatesSection);
			Composite relatedBugsComposite = toolkit.createComposite(duplicatesSection);
			relatedBugsComposite.setLayout(new GridLayout(4, false));
			relatedBugsComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			duplicatesSection.setClient(relatedBugsComposite);
			duplicateDetectorLabel = new Label(relatedBugsComposite, SWT.LEFT);
			duplicateDetectorLabel.setText("Detector:");

			duplicateDetectorChooser = new CCombo(relatedBugsComposite, SWT.FLAT | SWT.READ_ONLY);
			toolkit.adapt(duplicateDetectorChooser, true, true);
			duplicateDetectorChooser.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			duplicateDetectorChooser.setFont(TEXT_FONT);
			duplicateDetectorChooser.setLayoutData(GridDataFactory.swtDefaults().hint(150, SWT.DEFAULT).create());

			Collections.sort(allCollectors, new Comparator<AbstractDuplicateDetector>() {

				public int compare(AbstractDuplicateDetector c1, AbstractDuplicateDetector c2) {
					return c1.getName().compareToIgnoreCase(c2.getName());
				}

			});

			for (AbstractDuplicateDetector detector : allCollectors) {
				duplicateDetectorChooser.add(detector.getName());
			}

			duplicateDetectorChooser.select(0);
			duplicateDetectorChooser.setEnabled(true);
			duplicateDetectorChooser.setData(allCollectors);

			if (allCollectors.size() > 0) {

				searchForDuplicates = toolkit.createButton(relatedBugsComposite, LABEL_SEARCH_DUPS, SWT.NONE);
				GridData searchDuplicatesButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
				searchForDuplicates.setLayoutData(searchDuplicatesButtonData);
				searchForDuplicates.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {
						searchForDuplicates();
					}
				});
			}
//		} else {
//			Label label = new Label(composite, SWT.LEFT);
//			label.setText(LABEL_NO_DETECTOR);

			toolkit.paintBordersFor(relatedBugsComposite);

		}

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

		addAssignedTo(peopleComposite);
		boolean haveRealName = false;
		RepositoryTaskAttribute reporterAttribute = taskData.getAttribute(RepositoryTaskAttribute.USER_REPORTER_NAME);
		if (reporterAttribute == null) {
			reporterAttribute = taskData.getAttribute(RepositoryTaskAttribute.USER_REPORTER);
		} else {
			haveRealName = true;
		}
		if (reporterAttribute != null) {
			Label label = createLabel(peopleComposite, reporterAttribute);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
			Text textField = createTextField(peopleComposite, reporterAttribute, SWT.FLAT | SWT.READ_ONLY);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(textField);
			if (haveRealName) {
				textField.setText(textField.getText() + " <"
						+ taskData.getAttributeValue(RepositoryTaskAttribute.USER_REPORTER) + ">");
			}
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
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
			Text text = createTextField(attributesComposite, addCCattribute, SWT.FLAT);
			GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).applyTo(text);

			if (hasContentAssist(addCCattribute)) {
				ContentAssistCommandAdapter adapter = applyContentAssist(text,
						createContentProposalProvider(addCCattribute));
				ILabelProvider propsalLabelProvider = createProposalLabelProvider(addCCattribute);
				if (propsalLabelProvider != null) {
					adapter.setLabelProvider(propsalLabelProvider);
				}
				adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
			}
		}

		RepositoryTaskAttribute CCattribute = taskData.getAttribute(RepositoryTaskAttribute.USER_CC);
		if (CCattribute != null) {
			Label label = createLabel(attributesComposite, CCattribute);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.TOP).applyTo(label);
			ccList = new org.eclipse.swt.widgets.List(attributesComposite, SWT.MULTI | SWT.V_SCROLL);// SWT.BORDER
			ccList.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			ccList.setFont(TEXT_FONT);
			GridData ccListData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			ccListData.horizontalSpan = 1;
			ccListData.widthHint = 150;
			ccListData.heightHint = 95;
			ccList.setLayoutData(ccListData);
			if (hasChanged(taskData.getAttribute(RepositoryTaskAttribute.USER_CC))) {
				ccList.setBackground(colorIncoming);
			}
			java.util.List<String> ccs = taskData.getCc();
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
							List<String> remove = taskData.getAttributeValues(RepositoryTaskAttribute.REMOVE_CC);
							if (!remove.contains(cc)) {
								taskData.addAttributeValue(RepositoryTaskAttribute.REMOVE_CC, cc);
							}
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
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(label);
		}

	}

	/**
	 * A listener for selection of the summary field.
	 * 
	 * @since 2.1
	 */
	protected class DescriptionListener implements Listener {
		public DescriptionListener() {
		}

		public void handleEvent(Event event) {
			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
					new RepositoryTaskSelection(taskData.getId(), taskData.getRepositoryUrl(),
							taskData.getRepositoryKind(), getSectionLabel(SECTION_NAME.DESCRIPTION_SECTION), true,
							taskData.getSummary()))));
		}
	}

	protected boolean supportsCommentDelete() {
		return false;
	}

	protected void deleteComment(TaskComment comment) {

	}

	protected void createCommentLayout(Composite composite) {
		commentsSection = createSection(composite, getSectionLabel(SECTION_NAME.COMMENTS_SECTION), false);
		commentsSection.setText(commentsSection.getText() + " (" + taskData.getComments().size() + ")");
		
		final Composite commentsSectionClient = toolkit.createComposite(commentsSection);
		RowLayout rowLayout = new RowLayout();
		rowLayout.pack = true;
		rowLayout.marginLeft = 0;
		rowLayout.marginBottom = 0;
		rowLayout.marginTop = 0;
		commentsSectionClient.setLayout(rowLayout);
		commentsSectionClient.setBackground(null);
		
		if (supportsCommentSort()) {
			sortHyperlink = new ImageHyperlink(commentsSectionClient, SWT.NONE);
			sortHyperlink.setToolTipText("Change order of comments");
			toolkit.adapt(sortHyperlink, true, true);
			sortHyperlink.setBackground(null);

			sortHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					sortComments();
				}
			});
			sortHyperlinkState(false);
		}
		
		if (taskData.getComments().size() > 0) {
			commentsSection.setEnabled(true);
			commentsSection.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanged(ExpansionEvent e) {
					if (commentsSection.isExpanded()) {
						if (supportsCommentSort()) {
							sortHyperlinkState(true);
						}					
					} else {
						if (supportsCommentSort()) {
							sortHyperlinkState(false);
						}					
					}					
				}
			});



			ImageHyperlink collapseAllHyperlink = new ImageHyperlink(commentsSectionClient, SWT.NONE);
			collapseAllHyperlink.setToolTipText("Collapse All Comments");
			toolkit.adapt(collapseAllHyperlink, true, true);
			collapseAllHyperlink.setBackground(null);
			collapseAllHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.COLLAPSE_ALL));
			collapseAllHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					hideAllComments();
				}
			});

			ImageHyperlink expandAllHyperlink = new ImageHyperlink(commentsSectionClient, SWT.NONE);
			expandAllHyperlink.setToolTipText("Expand All Comments");
			toolkit.adapt(expandAllHyperlink, true, true);
			expandAllHyperlink.setBackground(null);
			expandAllHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.EXPAND_ALL));
			expandAllHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					BusyIndicator.showWhile(getControl().getDisplay(), new Runnable() {
						public void run() {
							revealAllComments();
							if (supportsCommentSort()) {
								sortHyperlinkState(true);
							}
						}
					});
				}
			});
			commentsSection.setTextClient(commentsSectionClient);
		} else {
			commentsSection.setEnabled(false);
		}

		// Additional (read-only) Comments Area
		addCommentsComposite = toolkit.createComposite(commentsSection);
		commentsSection.setClient(addCommentsComposite);
		GridLayout addCommentsLayout = new GridLayout();
		addCommentsLayout.numColumns = 1;
		addCommentsComposite.setLayout(addCommentsLayout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(addCommentsComposite);

		boolean foundNew = false;

		for (Iterator<TaskComment> it = taskData.getComments().iterator(); it.hasNext();) {
			final TaskComment taskComment = it.next();

			final ExpandableComposite expandableComposite = toolkit.createExpandableComposite(addCommentsComposite,
					ExpandableComposite.TREE_NODE | ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT);

			expandableComposite.setTitleBarForeground(toolkit.getColors().getColor(IFormColors.TITLE));

			final Composite toolbarComp = toolkit.createComposite(expandableComposite);
			rowLayout = new RowLayout();
			rowLayout.pack = true;
			rowLayout.marginLeft = 0;
			rowLayout.marginBottom = 0;
			rowLayout.marginTop = 0;
			toolbarComp.setLayout(rowLayout);
			toolbarComp.setBackground(null);

			ImageHyperlink formHyperlink = toolkit.createImageHyperlink(toolbarComp, SWT.NONE);
			formHyperlink.setBackground(null);
			formHyperlink.setFont(expandableComposite.getFont());
			formHyperlink.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
			if (taskComment.getAuthor() != null && taskComment.getAuthor().equalsIgnoreCase(repository.getUserName())) {
				formHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.PERSON_ME_NARROW));
			} else {
				formHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.PERSON_NARROW));
			}

			String authorName = taskComment.getAuthorName();
			String tooltipText = taskComment.getAuthor();
			if (authorName.length() == 0) {
				authorName = taskComment.getAuthor();
				tooltipText = null;
			}

			formHyperlink.setText(taskComment.getNumber() + ": " + authorName + ", "
					+ formatDate(taskComment.getCreated()));

			formHyperlink.setToolTipText(tooltipText);
			formHyperlink.setEnabled(true);
			formHyperlink.setUnderlined(false);

			final Composite toolbarButtonComp = toolkit.createComposite(toolbarComp);
			RowLayout buttonCompLayout = new RowLayout();
			buttonCompLayout.marginBottom = 0;
			buttonCompLayout.marginTop = 0;
			toolbarButtonComp.setLayout(buttonCompLayout);
			toolbarButtonComp.setBackground(null);

			if (supportsCommentDelete()) {
				final ImageHyperlink deleteComment = new ImageHyperlink(toolbarButtonComp, SWT.NULL);
				toolkit.adapt(deleteComment, true, true);
				deleteComment.setImage(TasksUiImages.getImage(TasksUiImages.REMOVE));
				deleteComment.setToolTipText("Remove");

				deleteComment.addHyperlinkListener(new HyperlinkAdapter() {

					@Override
					public void linkActivated(HyperlinkEvent e) {
						deleteComment(taskComment);
						submitToRepository();
					}
				});

			}

			final ImageHyperlink replyLink = createReplyHyperlink(taskComment.getNumber(), toolbarButtonComp,
					taskComment.getText());

			formHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					toggleExpandableComposite(!expandableComposite.isExpanded(), expandableComposite);
				}

				@Override
				public void linkEntered(HyperlinkEvent e) {
					replyLink.setUnderlined(true);
					super.linkEntered(e);
				}

				@Override
				public void linkExited(HyperlinkEvent e) {
					replyLink.setUnderlined(false);
					super.linkExited(e);
				}
			});

			expandableComposite.setTextClient(toolbarComp);


			toolbarButtonComp.setVisible(expandableComposite.isExpanded());

			// HACK: This is necessary
			// due to a bug in SWT's ExpandableComposite.
			// 165803: Expandable bars should expand when clicking anywhere
			// https://bugs.eclipse.org/bugs/show_bug.cgi?taskId=165803
			expandableComposite.setData(toolbarButtonComp);

			expandableComposite.setLayout(new GridLayout());
			expandableComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			final Composite ecComposite = toolkit.createComposite(expandableComposite);
			GridLayout ecLayout = new GridLayout();
			ecLayout.marginHeight = 0;
			ecLayout.marginBottom = 3;
			ecLayout.marginLeft = 15;
			ecComposite.setLayout(ecLayout);
			ecComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			expandableComposite.setClient(ecComposite);
			// code for outline
			commentComposites.add(expandableComposite);
			controlBySelectableObject.put(taskComment, expandableComposite);
			expandableComposite.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanged(ExpansionEvent e) {
					toolbarButtonComp.setVisible(expandableComposite.isExpanded());
					TextViewer viewer = null;
					if (e.getState() && expandableComposite.getData("viewer") == null) {
						viewer = addTextViewer(repository, ecComposite, taskComment.getText().trim(), SWT.MULTI
								| SWT.WRAP);
						expandableComposite.setData("viewer", viewer.getTextWidget());
						viewer.getTextWidget().addFocusListener(new FocusListener() {

							public void focusGained(FocusEvent e) {
								selectedComment = taskComment;

							}

							public void focusLost(FocusEvent e) {
								selectedComment = null;
							}
						});

						StyledText styledText = viewer.getTextWidget();
						GridDataFactory.fillDefaults().hint(DESCRIPTION_WIDTH, SWT.DEFAULT).applyTo(styledText);
						resetLayout();
					} else {
						// dispose viewer
						if (expandableComposite.getData("viewer") instanceof StyledText) {
							((StyledText) expandableComposite.getData("viewer")).dispose();
							expandableComposite.setData("viewer", null);
						}
						resetLayout();
					}
				}
			});

			if ((repositoryTask != null && repositoryTask.getLastReadTimeStamp() == null)
					|| editorInput.getOldTaskData() == null) {
				// hit or lost task data, expose all comments
				toggleExpandableComposite(true, expandableComposite);
				foundNew = true;
			} else if (isNewComment(taskComment)) {
				expandableComposite.setBackground(colorIncoming);
				toggleExpandableComposite(true, expandableComposite);
				foundNew = true;
			}

		}
		if (supportsCommentSort()) {
			if (commentSortIsUp) {
				commentSortIsUp = !commentSortIsUp;
				sortComments();				
			} else {
				sortHyperlinkState(commentSortEnable);
			}
		}
		if (foundNew) {
			commentsSection.setExpanded(true);
			if (supportsCommentSort()) {
				sortHyperlinkState(true);
			}
		} else if (taskData.getComments() == null || taskData.getComments().size() == 0) {
			if (supportsCommentSort()) {
				sortHyperlinkState(false);
			}
		} else if (editorInput.getTaskData() != null && editorInput.getOldTaskData() != null) {
			List<TaskComment> newTaskComments = editorInput.getTaskData().getComments();
			List<TaskComment> oldTaskComments = editorInput.getOldTaskData().getComments();
			if (newTaskComments == null || oldTaskComments == null) {
				commentsSection.setExpanded(true);
				if (supportsCommentSort()) {
					sortHyperlinkState(true);
				}
			} else if (newTaskComments.size() != oldTaskComments.size()) {
				commentsSection.setExpanded(true);
				if (supportsCommentSort()) {
					sortHyperlinkState(true);
				}
			}
		}
	}

	public String formatDate(String dateString) {
		return dateString;
	}

	private boolean isNewComment(TaskComment comment) {

		// Simple test (will not reveal new comments if offline data was lost
		if (editorInput.getOldTaskData() != null) {
			return (comment.getNumber() > editorInput.getOldTaskData().getComments().size());
		}
		return false;

		// OLD METHOD FOR DETERMINING NEW COMMENTS
		// if (repositoryTask != null) {
		// if (repositoryTask.getLastSyncDateStamp() == null) {
		// // new hit
		// return true;
		// }
		// AbstractRepositoryConnector connector = (AbstractRepositoryConnector)
		// TasksUiPlugin.getRepositoryManager()
		// .getRepositoryConnector(taskData.getRepositoryKind());
		// AbstractTaskDataHandler offlineHandler = connector.getTaskDataHandler();
		// if (offlineHandler != null) {
		//
		// Date lastSyncDate =
		// taskData.getAttributeFactory().getDateForAttributeType(
		// RepositoryTaskAttribute.DATE_MODIFIED,
		// repositoryTask.getLastSyncDateStamp());
		//
		// if (lastSyncDate != null) {
		//
		// // reduce granularity to minutes
		// Calendar calLastMod = Calendar.getInstance();
		// calLastMod.setTimeInMillis(lastSyncDate.getTime());
		// calLastMod.set(Calendar.SECOND, 0);
		//
		// Date commentDate =
		// taskData.getAttributeFactory().getDateForAttributeType(
		// RepositoryTaskAttribute.COMMENT_DATE, comment.getCreated());
		// if (commentDate != null) {
		//
		// Calendar calComment = Calendar.getInstance();
		// calComment.setTimeInMillis(commentDate.getTime());
		// calComment.set(Calendar.SECOND, 0);
		// if (calComment.after(calLastMod)) {
		// return true;
		// }
		// }
		// }
		// }
		// }
		// return false;

	}

	/**
	 * Subclasses that support HTML preview of ticket description and comments override this method to return an
	 * instance of AbstractRenderingEngine
	 * 
	 * @return <code>null</code> if HTML preview is not supported for the repository (default)
	 * @since 2.1
	 */
	protected AbstractRenderingEngine getRenderingEngine() {
		return null;
	}

	protected void createNewCommentLayout(Composite composite) {
		// Section newCommentSection = createSection(composite,
		// getSectionLabel(SECTION_NAME.NEWCOMMENT_SECTION));

		Section newCommentSection = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR);
		newCommentSection.setText(getSectionLabel(SECTION_NAME.NEWCOMMENT_SECTION));
		newCommentSection.setLayout(new GridLayout());
		newCommentSection.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite newCommentsComposite = toolkit.createComposite(newCommentSection);
		newCommentsComposite.setLayout(new GridLayout());

		// HACK: new new comment attribute not created by connector, create one.
		if (taskData.getAttribute(RepositoryTaskAttribute.COMMENT_NEW) == null) {
			taskData.setAttributeValue(RepositoryTaskAttribute.COMMENT_NEW, "");
		}
		final RepositoryTaskAttribute attribute = taskData.getAttribute(RepositoryTaskAttribute.COMMENT_NEW);

		if (getRenderingEngine() != null) {
			// composite with StackLayout to hold text editor and preview widget
			Composite editPreviewComposite = toolkit.createComposite(newCommentsComposite);
			GridData editPreviewData = new GridData(GridData.FILL_BOTH);
			editPreviewData.widthHint = DESCRIPTION_WIDTH;
			editPreviewData.minimumHeight = DESCRIPTION_HEIGHT;
			editPreviewData.grabExcessHorizontalSpace = true;
			editPreviewComposite.setLayoutData(editPreviewData);
			editPreviewComposite.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

			final StackLayout editPreviewLayout = new StackLayout();
			editPreviewComposite.setLayout(editPreviewLayout);

			newCommentTextViewer = addTextEditor(repository, editPreviewComposite, attribute.getValue(), true, SWT.FLAT
					| SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);

			editPreviewLayout.topControl = newCommentTextViewer.getControl();
			editPreviewComposite.layout();

			// composite for edit/preview button
			Composite buttonComposite = toolkit.createComposite(newCommentsComposite);
			buttonComposite.setLayout(new GridLayout());
			createPreviewButton(buttonComposite, newCommentTextViewer, editPreviewComposite, editPreviewLayout);
		} else {
			newCommentTextViewer = addTextEditor(repository, newCommentsComposite, attribute.getValue(), true, SWT.FLAT
					| SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			GridData addCommentsTextData = new GridData(GridData.FILL_BOTH);
			addCommentsTextData.widthHint = DESCRIPTION_WIDTH;
			addCommentsTextData.minimumHeight = DESCRIPTION_HEIGHT;
			addCommentsTextData.grabExcessHorizontalSpace = true;
			newCommentTextViewer.getControl().setLayoutData(addCommentsTextData);
			newCommentTextViewer.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		}
		newCommentTextViewer.setEditable(true);
		newCommentTextViewer.addTextListener(new ITextListener() {
			public void textChanged(TextEvent event) {
				String newValue = addCommentsTextBox.getText();
				if (!newValue.equals(attribute.getValue())) {
					attribute.setValue(newValue);
					attributeChanged(attribute);
				}
			}
		});

		newCommentTextViewer.getTextWidget().addListener(SWT.FocusIn, new NewCommentListener());
		addCommentsTextBox = newCommentTextViewer.getTextWidget();

		newCommentSection.setClient(newCommentsComposite);

		toolkit.paintBordersFor(newCommentsComposite);
	}

	private Browser addBrowser(Composite parent, int style) {
		Browser browser = new Browser(parent, style);
		// intercept links to open tasks in rich editor and urls in separate browser
		browser.addLocationListener(new LocationAdapter() {
			@Override
			public void changing(LocationEvent event) {
				// ignore events that are caused by manually setting the contents of the browser
				if (ignoreLocationEvents) {
					return;
				}

				if (event.location != null && !event.location.startsWith("about")) {
					event.doit = false;
					IHyperlink link = new TaskUrlHyperlink(
							new Region(0, 0)/* a fake region just to make constructor happy */, event.location);
					link.open();
				}
			}

		});

		return browser;
	}

	/**
	 * Creates and sets up the button for switching between text editor and HTML preview. Subclasses that support HTML
	 * preview of new comments must override this method.
	 * 
	 * @param buttonComposite
	 *            the composite that holds the button
	 * @param editor
	 *            the TextViewer for editing text
	 * @param previewBrowser
	 *            the Browser for displaying the preview
	 * @param editorLayout
	 *            the StackLayout of the <code>editorComposite</code>
	 * @param editorComposite
	 *            the composite that holds <code>editor</code> and <code>previewBrowser</code>
	 * @since 2.1
	 */
	private void createPreviewButton(final Composite buttonComposite, final TextViewer editor,
			final Composite editorComposite, final StackLayout editorLayout) {
		// create an anonymous object that encapsulates the edit/preview button together with
		// its state and String constants for button text;
		// this implementation keeps all information needed to set up the button 
		// in this object and the method parameters, and this method is reused by both the
		// description section and new comments section.
		new Object() {
			private static final String LABEL_BUTTON_PREVIEW = "Preview";

			private static final String LABEL_BUTTON_EDIT = "Edit";

			private int buttonState = 0;

			private Button previewButton;

			private Browser previewBrowser;

			{
				previewButton = toolkit.createButton(buttonComposite, LABEL_BUTTON_PREVIEW, SWT.PUSH);
				GridData previewButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
				previewButtonData.widthHint = 100;
				//previewButton.setImage(TasksUiImages.getImage(TasksUiImages.PREVIEW));
				previewButton.setLayoutData(previewButtonData);
				previewButton.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {
						if (previewBrowser == null) {
							previewBrowser = addBrowser(editorComposite, SWT.NONE);
						}

						buttonState = ++buttonState % 2;
						if (buttonState == 1) {
							setText(previewBrowser, "Loading preview...");
							previewWiki(previewBrowser, editor.getTextWidget().getText());
						}
						previewButton.setText(buttonState == 0 ? LABEL_BUTTON_PREVIEW : LABEL_BUTTON_EDIT);
						editorLayout.topControl = (buttonState == 0 ? editor.getControl() : previewBrowser);
						editorComposite.layout();
					}
				});
			}

		};
	}

	private void setText(Browser browser, String html) {
		try {
			ignoreLocationEvents = true;
			browser.setText((html != null) ? html : "");
		} finally {
			ignoreLocationEvents = false;
		}

	}

	private void previewWiki(final Browser browser, String sourceText) {
		final class PreviewWikiJob extends Job {
			private String sourceText;

			private String htmlText;

			private IStatus jobStatus;

			public PreviewWikiJob(String sourceText) {
				super("Formatting Wiki Text");

				if (sourceText == null) {
					throw new IllegalArgumentException("source text must not be null");
				}

				this.sourceText = sourceText;
			}

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				AbstractRenderingEngine htmlRenderingEngine = getRenderingEngine();
				if (htmlRenderingEngine == null) {
					jobStatus = new RepositoryStatus(repository, IStatus.INFO, TasksUiPlugin.ID_PLUGIN,
							RepositoryStatus.ERROR_INTERNAL, "The repository does not support HTML preview.");
					return Status.OK_STATUS;
				}

				jobStatus = Status.OK_STATUS;
				try {
					htmlText = htmlRenderingEngine.renderAsHtml(repository, sourceText, monitor);
				} catch (CoreException e) {
					jobStatus = e.getStatus();
				}
				return Status.OK_STATUS;
			}

			public String getHtmlText() {
				return htmlText;
			}

			public IStatus getStatus() {
				return jobStatus;
			}

		}

		final PreviewWikiJob job = new PreviewWikiJob(sourceText);

		job.addJobChangeListener(new JobChangeAdapter() {

			@Override
			public void done(final IJobChangeEvent event) {
				if (!form.isDisposed()) {
					if (job.getStatus().isOK()) {
						getPartControl().getDisplay().asyncExec(new Runnable() {
							public void run() {
								AbstractRepositoryTaskEditor.this.setText(browser, job.getHtmlText());
								parentEditor.setMessage(null, IMessageProvider.NONE);
							}
						});
					} else {
						getPartControl().getDisplay().asyncExec(new Runnable() {
							public void run() {
								parentEditor.setMessage(job.getStatus().getMessage(), IMessageProvider.ERROR);
							}
						});
					}
				}
				super.done(event);
			}
		});

		job.setUser(true);
		job.schedule();
	}

	/**
	 * Creates the button layout. This displays options and buttons at the bottom of the editor to allow actions to be
	 * performed on the bug.
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
		return createSection(composite, title, true);
	}

	/**
	 * @Since 2.3
	 */
	private Section createSection(Composite composite, String title, boolean expandedState) {
		Section section = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText(title);
		if (section.isExpanded() != expandedState) {
			section.setExpanded(expandedState);
		}
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return section;
	}

	/**
	 * Adds buttons to this composite. Subclasses can override this method to provide different/additional buttons.
	 * 
	 * @param buttonComposite
	 *            Composite to add the buttons to.
	 */
	protected void addActionButtons(Composite buttonComposite) {
		submitButton = toolkit.createButton(buttonComposite, LABEL_BUTTON_SUBMIT, SWT.NONE);
		GridData submitButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		submitButtonData.widthHint = 100;
		submitButton.setImage(TasksUiImages.getImage(TasksUiImages.REPOSITORY_SUBMIT));
		submitButton.setLayoutData(submitButtonData);
		submitButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				submitToRepository();
			}
		});

		setSubmitEnabled(true);

		toolkit.createLabel(buttonComposite, "    ");

		AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(),
				taskData.getId());
		if (attachContextEnabled && task != null) {
			addAttachContextButton(buttonComposite, task);
		}
	}

	private void setSubmitEnabled(boolean enabled) {
		if (submitButton != null && !submitButton.isDisposed()) {
			submitButton.setEnabled(enabled);
			if (enabled) {
				submitButton.setToolTipText("Submit to " + this.repository.getUrl());
			}
		}
	}

	/**
	 * Override to make hyperlink available. If not overridden hyperlink will simply not be displayed.
	 * 
	 * @return url String form of url that points to task's past activity
	 */
	protected String getHistoryUrl() {
		return null;
	}

	protected void saveTaskOffline(IProgressMonitor progressMonitor) {
		if (taskData == null) {
			return;
		}
		if (repositoryTask != null) {
			TasksUiPlugin.getSynchronizationManager().saveOutgoing(repositoryTask, changedAttributes);
		}
		if (repositoryTask != null) {
			TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask, false);
		}
		markDirty(false);
	}

	// once the following bug is fixed, this check for first focus is probably
	// not needed -> Bug# 172033: Restore editor focus
	private boolean firstFocus = true;

	@Override
	public void setFocus() {
		if (summaryTextViewer != null && !summaryTextViewer.getTextWidget().isDisposed()) {
			if (firstFocus) {
				summaryTextViewer.getTextWidget().setFocus();
				firstFocus = false;
			}
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
		saveTaskOffline(monitor);
		updateEditorTitle();
		updateHeaderControls();
//		fillToolBar(getParentEditor().getTopForm().getToolBarManager());
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
		if (activateAction != null) {
			activateAction.dispose();
		}
		super.dispose();
	}

	/**
	 * Fires a <code>SelectionChangedEvent</code> to all listeners registered under
	 * <code>selectionChangedListeners</code>.
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

	/*----------------------------------------------------------*
	 * CODE TO SCROLL TO A COMMENT OR OTHER PIECE OF TEXT
	 *----------------------------------------------------------*/

	private HashMap<Object, Control> controlBySelectableObject = new HashMap<Object, Control>();

	private List<ExpandableComposite> commentComposites = new ArrayList<ExpandableComposite>();

	private StyledText addCommentsTextBox = null;

	protected TextViewer descriptionTextViewer = null;

	private void revealAllComments() {
		try {
			form.setRedraw(false);
			refreshEnabled = false;
			if (supportsCommentSort()) {
				sortHyperlinkState(false);
			}

			if (commentsSection != null && !commentsSection.isExpanded()) {
				commentsSection.setExpanded(true);
				if (supportsCommentSort()) {
					sortHyperlinkState(true);
				}
			}
			for (ExpandableComposite composite : commentComposites) {
				if (composite.isDisposed()) {
					continue;
				}
				if (!composite.isExpanded()) {
					toggleExpandableComposite(true, composite);
				}
//			Composite comp = composite.getParent();
//			while (comp != null && !comp.isDisposed()) {
//				if (comp instanceof ExpandableComposite && !comp.isDisposed()) {
//					ExpandableComposite ex = (ExpandableComposite) comp;
//					setExpandableCompositeState(true, ex);
//
//					// HACK: This is necessary
//					// due to a bug in SWT's ExpandableComposite.
//					// 165803: Expandable bars should expand when clicking
//					// anywhere
//					// https://bugs.eclipse.org/bugs/show_bug.cgi?taskId=165803
//					if (ex.getData() != null && ex.getData() instanceof Composite) {
//						((Composite) ex.getData()).setVisible(true);
//					}
//
//					break;
//				}
//				comp = comp.getParent();
//			}
			}
		} finally {
			refreshEnabled = true;
			form.setRedraw(true);
		}
		resetLayout();
	}

	private void hideAllComments() {
		try {
			refreshEnabled = false;

			for (ExpandableComposite composite : commentComposites) {
				if (composite.isDisposed()) {
					continue;
				}

				if (composite.isExpanded()) {
					toggleExpandableComposite(false, composite);
				}

//			Composite comp = composite.getParent();
//			while (comp != null && !comp.isDisposed()) {
//				if (comp instanceof ExpandableComposite && !comp.isDisposed()) {
//					ExpandableComposite ex = (ExpandableComposite) comp;
//					setExpandableCompositeState(false, ex);
//
//					// HACK: This is necessary
//					// due to a bug in SWT's ExpandableComposite.
//					// 165803: Expandable bars should expand when clicking anywhere
//					// https://bugs.eclipse.org/bugs/show_bug.cgi?taskId=165803
//					if (ex.getData() != null && ex.getData() instanceof Composite) {
//						((Composite) ex.getData()).setVisible(false);
//					}
//
//					break;
//				}
//				comp = comp.getParent();
//			}
			}

//		if (commentsSection != null) {
//			commentsSection.setExpanded(false);
//		}

		} finally {
			refreshEnabled = true;
		}
		resetLayout();
	}

	private void sortHyperlinkState(boolean enabled) {
		commentSortEnable = enabled;

		if (sortHyperlink == null) {
			return;
		}

		sortHyperlink.setEnabled(enabled);
		if (enabled) {
			if (commentSortIsUp) {
				sortHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.SORT_COMMENT_UP));
			} else {
				sortHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.SORT_COMMENT_DOWN));
			}
		} else {
			if (commentSortIsUp) {
				sortHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.SORT_COMMENT_UP_GRAY));				
			} else {
				sortHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.SORT_COMMENT_DOWN_GRAY));
			}
		}
	}
	
	private void sortComments() {
		if (addCommentsComposite != null) {
			Control[] commentControlList = addCommentsComposite.getChildren();
			int commentControlListLength = commentControlList.length;
			for (int i = 1; i < commentControlListLength; i++) {
				commentControlList[commentControlListLength-i].moveAbove(commentControlList[0]);
			}
		}
		commentSortIsUp = !commentSortIsUp;
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(PREF_SORT_ORDER_PREFIX+repository.getConnectorKind(), commentSortIsUp);
		sortHyperlinkState(commentSortEnable);
		resetLayout();
	}

	/**
	 * Selects the given object in the editor.
	 * 
	 * @param o
	 *            The object to be selected.
	 * @param highlight
	 *            Whether or not the object should be highlighted.
	 */
	public boolean select(Object o, boolean highlight) {
		Control control = controlBySelectableObject.get(o);
		if (control != null && !control.isDisposed()) {

			// expand all children
			if (control instanceof ExpandableComposite) {
				ExpandableComposite ex = (ExpandableComposite) control;
				if (!ex.isExpanded()) {
					toggleExpandableComposite(true, ex);
				}
			}

			// expand all parents of control
			Composite comp = control.getParent();
			while (comp != null) {
				if(comp instanceof Section) {
					if (!((Section)comp).isExpanded()) {
						((Section)comp).setExpanded(true);
					}
				} else if (comp instanceof ExpandableComposite) {
					ExpandableComposite ex = (ExpandableComposite) comp;
					if (!ex.isExpanded()) {
						toggleExpandableComposite(true, ex);
					}

					// HACK: This is necessary
					// due to a bug in SWT's ExpandableComposite.
					// 165803: Expandable bars should expand when clicking anywhere
					// https://bugs.eclipse.org/bugs/show_bug.cgi?taskId=165803
					if (ex.getData() != null && ex.getData() instanceof Composite) {
						((Composite) ex.getData()).setVisible(true);
					}
				}
				comp = comp.getParent();
			}
			focusOn(control, highlight);
		} else if (o instanceof RepositoryTaskData) {
			focusOn(null, highlight);
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Programmatically expand the provided ExpandableComposite, using reflection to fire the expansion listeners (see
	 * bug#70358)
	 * 
	 * @param comp
	 */
	private void toggleExpandableComposite(boolean expanded, ExpandableComposite comp) {
		if (comp.isExpanded() != expanded) {
			Method method = null;
			try {
				method = comp.getClass().getDeclaredMethod("programmaticToggleState");
				method.setAccessible(true);
				method.invoke(comp);
			} catch (Exception e) {
				// ignore
			}
		}
	}


	private void selectNewComment() {
		focusOn(addCommentsTextBox, false);
	}

	/**
	 * @author Raphael Ackermann (bug 195514)
	 * @since 2.1
	 */
	protected void focusAttributes() {
		if (attributesSection != null) {
			focusOn(attributesSection, false);
		}
	}

	private void focusDescription() {
		if (descriptionTextViewer != null) {
			focusOn(descriptionTextViewer.getTextWidget(), false);
		}
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
			if (s.isDisposed()) {
				return;
			}
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
		if (!form.getBody().isDisposed()) {
			form.setOrigin(0, pos);
		}
	}

	private RepositoryTaskOutlinePage outlinePage = null;

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		return getAdapterDelgate(adapter);
	}

	public Object getAdapterDelgate(Class<?> adapter) {
		if (IContentOutlinePage.class.equals(adapter)) {
			if (outlinePage == null && editorInput != null && taskOutlineModel != null) {
				outlinePage = new RepositoryTaskOutlinePage(taskOutlineModel);
			}
			return outlinePage;
		}
		return super.getAdapter(adapter);
	}

	public RepositoryTaskOutlinePage getOutline() {
		return outlinePage;
	}

	private Button[] radios;

	private Control[] radioOptions;

	private Button attachContextButton;

	private AbstractRepositoryConnector connector;

	private Cursor waitCursor;

	private boolean formBusy = false;

	private Composite headerInfoComposite;

	private Section attributesSection;

	public void close() {
		Display activeDisplay = getSite().getShell().getDisplay();
		activeDisplay.asyncExec(new Runnable() {
			public void run() {
				if (getSite() != null && getSite().getPage() != null && !getManagedForm().getForm().isDisposed()) {
					if (parentEditor != null) {
						getSite().getPage().closeEditor(parentEditor, false);
					} else {
						getSite().getPage().closeEditor(AbstractRepositoryTaskEditor.this, false);
					}
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

	/**
	 * @since 2.1
	 */
	public TaskEditor getParentEditor() {
		return parentEditor;
	}

	public RepositoryTaskOutlineNode getTaskOutlineModel() {
		return taskOutlineModel;
	}

	public void setTaskOutlineModel(RepositoryTaskOutlineNode taskOutlineModel) {
		this.taskOutlineModel = taskOutlineModel;
	}

	/**
	 * A listener for selection of the textbox where a new comment is entered in.
	 */
	private class NewCommentListener implements Listener {
		public void handleEvent(Event event) {
			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
					new RepositoryTaskSelection(taskData.getId(), taskData.getRepositoryUrl(),
							taskData.getRepositoryKind(), getSectionLabel(SECTION_NAME.NEWCOMMENT_SECTION), false,
							taskData.getSummary()))));
		}
	}

	public Control getControl() {
		return form;
	}

	public void setSummaryText(String text) {
		if (summaryTextViewer != null && summaryTextViewer.getTextWidget() != null) {
			summaryTextViewer.getTextWidget().setText(text);
		}
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
			if (!o.hasOptions() && !o.isInput()) {
				radioData.horizontalSpan = 4;
			} else {
				radioData.horizontalSpan = 1;
			}
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
				radioData.horizontalSpan = 3;
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
					if (a[j] != null) {
						((CCombo) radioOptions[i]).add((String) a[j]);
						if (((String) a[j]).equals(o.getOptionSelection())) {
							((CCombo) radioOptions[i]).select(j);
						}
					}
				}
				((CCombo) radioOptions[i]).addSelectionListener(new RadioButtonListener());
			} else if (o.isInput()) {
				radioData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
				radioData.horizontalSpan = 3;
				radioData.widthHint = RADIO_OPTION_WIDTH - 10;

				String assignmentValue = "";
				// NOTE: removed this because we now have content assit
// if (opName.equals(REASSIGN_BUG_TO)) {
// assignmentValue = repository.getUserName();
// }
				radioOptions[i] = toolkit.createText(buttonComposite, assignmentValue);
				radioOptions[i].setFont(TEXT_FONT);
				radioOptions[i].setLayoutData(radioData);
				// radioOptions[i].setBackground(background);
				((Text) radioOptions[i]).setText(o.getInputValue());
				((Text) radioOptions[i]).addModifyListener(new RadioButtonListener());

				if (hasContentAssist(o)) {
					ContentAssistCommandAdapter adapter = applyContentAssist((Text) radioOptions[i],
							createContentProposalProvider(o));
					ILabelProvider propsalLabelProvider = createProposalLabelProvider(o);
					if (propsalLabelProvider != null) {
						adapter.setLabelProvider(propsalLabelProvider);
					}
					adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
				}
			}

			if (i == 0 || o.isChecked()) {
				if (selected != null) {
					selected.setSelection(false);
				}
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
		if (taskData == null) {
			return false;
		}
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
		if (newAttribute == null) {
			return false;
		}
		RepositoryTaskData oldTaskData = editorInput.getOldTaskData();
		if (oldTaskData == null) {
			return false;
		}

		if (hasOutgoingChange(newAttribute)) {
			return false;
		}

		RepositoryTaskAttribute oldAttribute = oldTaskData.getAttribute(newAttribute.getId());
		if (oldAttribute == null) {
			return true;
		}
		if (oldAttribute.getValue() != null && !oldAttribute.getValue().equals(newAttribute.getValue())) {
			return true;
		} else if (oldAttribute.getValues() != null && !oldAttribute.getValues().equals(newAttribute.getValues())) {
			return true;
		}
		return false;
	}

	protected void addAttachContextButton(Composite buttonComposite, AbstractTask task) {
		attachContextButton = toolkit.createButton(buttonComposite, "Attach Context", SWT.CHECK);
		attachContextButton.setImage(TasksUiImages.getImage(TasksUiImages.CONTEXT_ATTACH));
	}

	/**
	 * Creates a check box for adding the repository user to the cc list. Does nothing if the repository does not have a
	 * valid username, the repository user is the assignee, reporter or already on the the cc list.
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
		addSelfButton.setSelection(RepositoryTaskAttribute.TRUE.equals(taskData.getAttributeValue(RepositoryTaskAttribute.ADD_SELF_CC)));
		addSelfButton.setImage(TasksUiImages.getImage(TasksUiImages.PERSON));
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

	public void setExpandAttributeSection(boolean expandAttributeSection) {
		this.expandedStateAttributes = expandAttributeSection;
	}

	public void setAttachContextEnabled(boolean attachContextEnabled) {
		this.attachContextEnabled = attachContextEnabled;
//		if (attachContextButton != null && attachContextButton.isEnabled()) {
//			attachContextButton.setSelection(attachContext);
//		}
	}

	@Override
	public void showBusy(boolean busy) {
		if (!getManagedForm().getForm().isDisposed() && busy != formBusy) {
			// parentEditor.showBusy(busy);
			if (synchronizeEditorAction != null) {
				synchronizeEditorAction.setEnabled(!busy);
			}

			if (activateAction != null) {
				activateAction.setEnabled(!busy);
			}

			if (openBrowserAction != null) {
				openBrowserAction.setEnabled(!busy);
			}

			if (historyAction != null) {
				historyAction.setEnabled(!busy);
			}

			if (newSubTaskAction != null) {
				newSubTaskAction.setEnabled(!busy);
			}

			if (clearOutgoingAction != null) {
				clearOutgoingAction.setEnabled(!busy);
			}

			if (submitButton != null && !submitButton.isDisposed()) {
				submitButton.setEnabled(!busy);
			}

			setEnabledState(editorComposite, !busy);

			formBusy = busy;
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

	public void setGlobalBusy(boolean busy) {
		if (parentEditor != null) {
			parentEditor.showBusy(busy);
		} else {
			showBusy(busy);
		}
	}

	public void submitToRepository() {
		setGlobalBusy(true);

		if (isDirty()) {
			saveTaskOffline(new NullProgressMonitor());
			markDirty(false);
		}

		final boolean attachContext = getAttachContext();

		Job submitJob = new Job(LABEL_JOB_SUBMIT) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				AbstractTask modifiedTask = null;
				try {
					monitor.beginTask("Submitting task", 3);
					String taskId = connector.getTaskDataHandler().postTaskData(repository, taskData,
							new SubProgressMonitor(monitor, 1));
					final boolean isNew = taskData.isNew();
					if (isNew) {
						if (taskId != null) {
							modifiedTask = updateSubmittedTask(taskId, new SubProgressMonitor(monitor, 1));
						} else {
							// null taskId, assume task could not be created...
							throw new CoreException(
									new RepositoryStatus(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
											RepositoryStatus.ERROR_INTERNAL,
									"Task could not be created. No additional information was provided by the connector."));
						}
					} else {
						modifiedTask = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(),
								taskData.getId());
					}

					// Synchronization accounting...
					if (modifiedTask != null) {
						// Attach context if required
						if (attachContext && connector.getAttachmentHandler() != null) {
							connector.getAttachmentHandler().attachContext(repository, modifiedTask, "",
									new SubProgressMonitor(monitor, 1));
						}

						modifiedTask.setSubmitting(true);
						final AbstractTask finalModifiedTask = modifiedTask;
						TasksUiPlugin.getSynchronizationManager().synchronize(connector, modifiedTask, true,
								new JobChangeAdapter() {

							@Override
							public void done(IJobChangeEvent event) {

								if (isNew) {
									close();
									TasksUiPlugin.getSynchronizationManager().setTaskRead(finalModifiedTask,
											true);
									TasksUiUtil.openEditor(finalModifiedTask, false);
								} else {
									PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
										public void run() {
											refreshEditor();
										}
									});
								}
							}
						});
						TasksUiPlugin.getSynchronizationScheduler().synchNow(0, Collections.singletonList(repository),
								false);
					} else {
						close();
						// For some reason the task wasn't retrieved.
						// Try to
						// open local then via web browser...
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								TasksUiUtil.openRepositoryTask(repository.getUrl(), taskData.getId(),
										connector.getTaskUrl(taskData.getRepositoryUrl(), taskData.getId()));
							}
						});
					}

					return Status.OK_STATUS;
				} catch (CoreException e) {
					if (modifiedTask != null) {
						modifiedTask.setSubmitting(false);
					}
					return handleSubmitError(e);
				} catch (Exception e) {
					if (modifiedTask != null) {
						modifiedTask.setSubmitting(false);
					}
					StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							setGlobalBusy(false);// enableButtons();
						}
					});
				} finally {
					monitor.done();
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
	public void refreshEditor() {
		try {
			if (!getManagedForm().getForm().isDisposed()) {
				if (this.isDirty && taskData !=null && !taskData.isNew()) {
					this.doSave(new NullProgressMonitor());
				}
				setGlobalBusy(true);
				changedAttributes.clear();
				commentComposites.clear();
				controlBySelectableObject.clear();
				editorInput.refreshInput();
				
				// Note: Marking read must run synchronously
				// If not, incomings resulting from subsequent synchronization
				// can get marked as read (without having been viewed by user
				if (repositoryTask != null) {
					TasksUiPlugin.getSynchronizationManager().setTaskRead(repositoryTask, true);
				}
				
				this.setInputWithNotify(this.getEditorInput());
				this.init(this.getEditorSite(), this.getEditorInput());
				
				// Header must be updated after init is called to task data is available
				updateHeaderControls();
				
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (editorComposite != null && !editorComposite.isDisposed()) {
							if (taskData != null) {
								updateEditorTitle();
								menu = editorComposite.getMenu();
								removeSections();
								editorComposite.setMenu(menu);
								createSections();
								// setFormHeaderLabel();
								markDirty(false);
								parentEditor.setMessage(null, 0);
								AbstractRepositoryTaskEditor.this.getEditor().setActivePage(
										AbstractRepositoryTaskEditor.this.getId());

								// Activate editor disabled: bug#179078
								// AbstractTaskEditor.this.getEditor().getEditorSite().getPage().activate(
								// AbstractTaskEditor.this);

								// TODO: expand sections that were previously
								// expanded

								if (taskOutlineModel != null && outlinePage != null
										&& !outlinePage.getControl().isDisposed()) {
									outlinePage.getOutlineTreeViewer().setInput(taskOutlineModel);
									outlinePage.getOutlineTreeViewer().refresh(true);
								}

								if (repositoryTask != null) {
									TasksUiPlugin.getSynchronizationManager().setTaskRead(repositoryTask, true);
								}

								setSubmitEnabled(true);
							}
						}
					}
				});

			} else {
				// Editor possibly closed as part of submit, mark read

				// Note: Marking read must run synchronously
				// If not, incomings resulting from subsequent synchronization
				// can get marked as read (without having been viewed by user
				if (repositoryTask != null) {
					TasksUiPlugin.getSynchronizationManager().setTaskRead(repositoryTask, true);
				}
			}
		} finally {
			if (!getManagedForm().getForm().isDisposed()) {
				setGlobalBusy(false);
			}
		}
	}

	/**
	 * Used to prevent form menu from being disposed when disposing elements on the form during refresh
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

	protected AbstractTaskCategory getCategory() {
		return null;
	}

	protected IStatus handleSubmitError(final CoreException exception) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (form != null && !form.isDisposed()) {
					if (exception.getStatus().getCode() == RepositoryStatus.ERROR_IO) {
						parentEditor.setMessage(ERROR_NOCONNECTIVITY, IMessageProvider.ERROR);
						StatusHandler.log(exception.getStatus());
					} else if (exception.getStatus().getCode() == RepositoryStatus.REPOSITORY_COMMENT_REQUIRED) {
						StatusHandler.displayStatus("Comment required", exception.getStatus());
						if (!getManagedForm().getForm().isDisposed() && newCommentTextViewer != null
								&& !newCommentTextViewer.getControl().isDisposed()) {
							newCommentTextViewer.getControl().setFocus();
						}
					} else if (exception.getStatus().getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN) {
						if (TasksUiUtil.openEditRepositoryWizard(repository) == MessageDialog.OK) {
							submitToRepository();
							return;
						}
					} else {
						StatusHandler.displayStatus("Submit failed", exception.getStatus());
					}
					setGlobalBusy(false);
				}
			}

		});
		return Status.OK_STATUS;
	}

	protected AbstractTask updateSubmittedTask(String postResult, IProgressMonitor monitor) throws CoreException {
		final AbstractTask newTask = connector.createTaskFromExistingId(repository, postResult, monitor);

		if (newTask != null) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (getCategory() != null) {
						TasksUiPlugin.getTaskListManager().getTaskList().moveTask(newTask, getCategory());
					}
				}
			});
		}

		return newTask;
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
				if (radios[i].getSelection()) {
					selected = radios[i];
				}
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
					o.setOptionSelection(((CCombo) radioOptions[i]).getItem(((CCombo) radioOptions[i]).getSelectionIndex()));

					if (taskData.getSelectedOperation() != null) {
						taskData.getSelectedOperation().setChecked(false);
					}
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
				if (radios[i].getSelection()) {
					selected = radios[i];
				}
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

					if (taskData.getSelectedOperation() != null) {
						taskData.getSelectedOperation().setChecked(false);
					}
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

	public AbstractRepositoryConnector getConnector() {
		return connector;
	}

	public void setShowAttachments(boolean showAttachments) {
		this.showAttachments = showAttachments;
	}

	public String getCommonDateFormat() {
		return HEADER_DATE_FORMAT;
	}

	public Color getColorIncoming() {
		return colorIncoming;
	}

	/**
	 * @see #select(Object, boolean)
	 */
	public void addSelectableControl(Object item, Control control) {
		controlBySelectableObject.put(item, control);
	}

	/**
	 * @see #addSelectableControl(Object, Control)
	 */
	public void removeSelectableControl(Object item) {
		controlBySelectableObject.remove(item);
	}

	/**
	 * This method allow you to overwrite the generation of the form area for "assigned to" in the peopleLayout.<br>
	 * <br>
	 * The overwrite is used for Bugzilla Versions > 3.0
	 * 
	 * @since 2.1
	 * @author Frank Becker (bug 198027)
	 */
	protected void addAssignedTo(Composite peopleComposite) {
		boolean haveRealName = false;
		RepositoryTaskAttribute assignedAttribute = taskData.getAttribute(RepositoryTaskAttribute.USER_ASSIGNED_NAME);
		if (assignedAttribute == null) {
			assignedAttribute = taskData.getAttribute(RepositoryTaskAttribute.USER_ASSIGNED);
		} else {
			haveRealName = true;
		}
		if (assignedAttribute != null) {
			Label label = createLabel(peopleComposite, assignedAttribute);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
			Text textField;
			if (assignedAttribute.isReadOnly()) {
				textField = createTextField(peopleComposite, assignedAttribute, SWT.FLAT | SWT.READ_ONLY);
			} else {
				textField = createTextField(peopleComposite, assignedAttribute, SWT.FLAT);
				ContentAssistCommandAdapter adapter = applyContentAssist(textField,
						createContentProposalProvider(assignedAttribute));
				ILabelProvider propsalLabelProvider = createProposalLabelProvider(assignedAttribute);
				if (propsalLabelProvider != null) {
					adapter.setLabelProvider(propsalLabelProvider);
				}
				adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
			}
			GridDataFactory.fillDefaults().grab(true, false).applyTo(textField);
			if (haveRealName) {
				textField.setText(textField.getText() + " <"
						+ taskData.getAttributeValue(RepositoryTaskAttribute.USER_ASSIGNED) + ">");
			}

		}
	}

	/**
	 * force a re-layout of entire form
	 */
	protected void resetLayout() {
		if (refreshEnabled) {
			form.layout(true, true);
			form.reflow(true);
		}
	}

	/**
	 * @since 2.3
	 */
	protected Hyperlink createTaskListHyperlink(Composite parent, final String taskId, final String taskUrl,
			final AbstractTask task) {
		TaskListHyperlink hyperlink = new TaskListHyperlink(parent, SWT.SHORT
				| getManagedForm().getToolkit().getOrientation());
		getManagedForm().getToolkit().adapt(hyperlink, true, true);
		getManagedForm().getToolkit().getHyperlinkGroup().add(hyperlink);
		hyperlink.setTask(task);
		if (task == null) {
			hyperlink.setText(taskId);
		}
		hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				if (task != null) {
					TasksUiUtil.refreshAndOpenTaskListElement(task);
				} else {
					TasksUiUtil.openRepositoryTask(repository.getUrl(), taskId, taskUrl);
				}
			}
		});
		return hyperlink;
	}

}
