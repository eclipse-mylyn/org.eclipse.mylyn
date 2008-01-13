/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.core.CommentQuoter;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewSubTaskAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.ToggleTaskActivationAction;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.NewTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskFormPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.themes.IThemeManager;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * Extend to provide customized task editing.
 * 
 * NOTE: This class is work in progress
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Jeff Pound (Attachment work)
 * @author Steffen Pingel
 * @author Xiaoyang Guan (Wiki HTML preview)
 */
public abstract class AbstractTaskEditorPage extends TaskFormPage {

	// API-3.0 rename ATTRIBTUES_SECTION to ATTRIBUTES_SECTION (bug 208629)
	protected enum SECTION_NAME {
		ACTIONS_SECTION("Actions"), ATTACHMENTS_SECTION("Attachments"), ATTRIBTUES_SECTION("Attributes"), COMMENTS_SECTION(
				"Comments"), DESCRIPTION_SECTION("Description"), NEWCOMMENT_SECTION("New Comment"), PEOPLE_SECTION(
				"People"), RELATEDBUGS_SECTION("Related Tasks");

		private String prettyName;

		SECTION_NAME(String prettyName) {
			this.prettyName = prettyName;
		}

		public String getPrettyName() {
			return prettyName;
		}
	}

	private static final String ERROR_NOCONNECTIVITY = "Unable to submit at this time. Check connectivity and retry.";

	private static final String LABEL_HISTORY = "History";

	private static final String LABEL_JOB_SUBMIT = "Submitting to repository";

	private static final String LABEL_REPLY = "Reply";

	private static final Font TITLE_FONT = JFaceResources.getBannerFont();

	private ToggleTaskActivationAction activateAction;

	private final List<IRepositoryTaskAttributeListener> attributesListeners = new ArrayList<IRepositoryTaskAttributeListener>();

	private Section attributesSection;

	private Set<RepositoryTaskAttribute> changedAttributes;

	private Color colorIncoming;

	private TaskEditorCommentPart commentPart;

	private AbstractRepositoryConnector connector;

	private final HashMap<Object, Control> controlBySelectableObject = new HashMap<Object, Control>();

	private Composite editorComposite;

	private RepositoryTaskEditorInput editorInput;

	private boolean expandedStateAttributes = false;

	// once the following bug is fixed, this check for first focus is probably
	// not needed -> Bug# 172033: Restore editor focus
	private boolean firstFocus = true;

	private ScrolledForm form;

	private boolean formBusy = false;

	private Action historyAction;

	private IRepositoryTaskSelection lastSelected = null;

	private Menu menu;

	private Action openBrowserAction;

	private RepositoryTaskOutlinePage outlinePage = null;

	private TaskEditor parentEditor = null;

	private boolean reflow = true;

	private TaskRepository repository;

	private AbstractTask repositoryTask;

	private final List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

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
						if (commentPart != null) {
							commentPart.setFocus();
						}
					} else if (n.getKey().equals(RepositoryTaskOutlineNode.LABEL_DESCRIPTION)) {
						if (descriptionPart != null) {
							descriptionPart.setFocus();
						}
					} else if (data != null) {
						select(data, highlight);
					}
				}
				part.setFocus();
			}
		}
	};

	private final ISelectionProvider selectionProvider = new ISelectionProvider() {
		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			selectionChangedListeners.add(listener);
		}

		public ISelection getSelection() {
			TaskComment selectedComment = null;
			if (commentPart != null) {
				selectedComment = commentPart.getSelectedComment();
			}
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

	private boolean showAttachments = true;

	private SynchronizeEditorAction synchronizeEditorAction;

	protected RepositoryTaskData taskData;

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
							// MessageDialog.openInformation(AbstractTaskEditor.this.getSite().getShell(),
							// "Changed - " + repositoryTask.getSummary(),
							// "Editor will Test with new incoming
							// changes.");
							parentEditor.setMessage("Task has incoming changes, synchronize to view",
									IMessageProvider.WARNING);

							actionPart.setSubmitEnabled(false);
							// updateContents();
							// TasksUiPlugin.getSynchronizationManager().setTaskRead(repositoryTask,
							// true);
							// TasksUiPlugin.getTaskDataManager().clearIncoming(
							// repositoryTask.getHandleIdentifier());
						} else {
							refreshEditor();
						}
					}
				});
			}
		}
	};

	private RepositoryTaskOutlineNode taskOutlineModel = null;

	private FormToolkit toolkit;

	private Cursor waitCursor;

	private TaskEditorSummaryPart summaryPart;

	private TaskEditorActionPart actionPart;

	private TaskEditorDescriptionPart descriptionPart;

	private TaskEditorNewCommentPart newCommentPart;

	/**
	 * Creates a new <code>AbstractTaskEditor</code>.
	 */
	public AbstractTaskEditorPage(FormEditor editor) {
		// set the scroll increments so the editor scrolls normally with the
		// scroll wheel
		super(editor, "id", "label"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void addAttributeListener(IRepositoryTaskAttributeListener listener) {
		attributesListeners.add(listener);
	}

	private void addHeaderControls() {
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

			// TODO: Remove? Added to debug bug#197355
			toolBarManager.removeAll();
			toolBarManager.update(true);

			toolBarManager.add(repositoryLabelControl);
			fillToolBar(parentEditor.getTopForm().getToolBarManager());

			if (repositoryTask != null && taskData != null && !taskData.isNew()) {
				activateAction = new ToggleTaskActivationAction(repositoryTask, toolBarManager);
				toolBarManager.add(new Separator("activation"));
				toolBarManager.add(activateAction);
			}

			toolBarManager.update(true);
		}
	}

	/**
	 * @see #select(Object, boolean)
	 */
	public void addSelectableControl(Object item, Control control) {
		controlBySelectableObject.put(item, control);
	}

	@Override
	protected TextViewer addTextEditor(TaskRepository repository, Composite composite, String text, boolean spellCheck,
			int style) {
		return super.addTextEditor(repository, composite, text, spellCheck, style);
	}

	@Override
	protected TextViewer addTextViewer(TaskRepository repository, Composite composite, String text, int style) {
		return super.addTextViewer(repository, composite, text, style);
	}

	/**
	 * Call upon change to attribute value
	 * 
	 * @param attribute
	 *            changed attribute
	 */
	// TODO EDITOR make private?
	public boolean attributeChanged(RepositoryTaskAttribute attribute) {
		if (attribute == null) {
			return false;
		}
		changedAttributes.add(attribute);
		markDirty(true);
		validateInput();
		return true;
	}

	public void close() {
		Display activeDisplay = getSite().getShell().getDisplay();
		activeDisplay.asyncExec(new Runnable() {
			public void run() {
				if (getSite() != null && getSite().getPage() != null && !getManagedForm().getForm().isDisposed()) {
					if (parentEditor != null) {
						getSite().getPage().closeEditor(parentEditor, false);
					} else {
						getSite().getPage().closeEditor(AbstractTaskEditorPage.this, false);
					}
				}
			}
		});
	}

	/**
	 * Creates the button layout. This displays options and buttons at the bottom of the editor to allow actions to be
	 * performed on the bug.
	 */
	private void createActionsSection(Composite composite) {
		Section section = createSection(composite, getSectionLabel(SECTION_NAME.ACTIONS_SECTION));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, true).applyTo(section);

		actionPart = new TaskEditorActionPart(this);
		actionPart.setInput(connector, repository, taskData);
		actionPart.createControl(section, toolkit);
		section.setClient(actionPart.getControl());

		getManagedForm().addPart(actionPart);
	}

	private void createAttachmentSection(Composite composite) {
		// TODO: expand to show new attachments
		Section section = createSection(composite, getSectionLabel(SECTION_NAME.ATTACHMENTS_SECTION));
		section.setText(section.getText() + " (" + taskData.getAttachments().size() + ")");
		section.setExpanded(false);

		TaskEditorAttachmentPart attachmentPart = new TaskEditorAttachmentPart(this);
		attachmentPart.setSupportsDelete(supportsAttachmentDelete());
		attachmentPart.setInput(connector, repository, taskData);
		attachmentPart.createControl(section, toolkit);
		section.setClient(attachmentPart.getControl());

		getManagedForm().addPart(attachmentPart);
	}

	private void createAttributeSection() {
		attributesSection = createSection(editorComposite, getSectionLabel(SECTION_NAME.ATTRIBTUES_SECTION));
		attributesSection.setExpanded(expandedStateAttributes
				|| getAttributeEditorManager().hasVisibleOutgoingChanges(taskData));

		TaskEditorAttributePart attributePart = new TaskEditorAttributePart(this, attributesSection);
		attributePart.setInput(connector, repository, taskData);
		attributePart.createControl(attributesSection, toolkit);

		getManagedForm().addPart(attributePart);
	}

	private void createCommentSection(Composite composite) {
		Section commentsSection = createSection(composite, getSectionLabel(SECTION_NAME.COMMENTS_SECTION));

		commentPart = new TaskEditorCommentPart(this, commentsSection, editorInput, repositoryTask);
		commentPart.setSupportsDelete(supportsCommentDelete());
		commentPart.setInput(connector, repository, taskData);
		commentPart.createControl(commentsSection, toolkit);

		getManagedForm().addPart(commentPart);
	}

	private void createDescriptionSection(Composite composite) {
		Section descriptionSection = createSection(composite, getSectionLabel(SECTION_NAME.DESCRIPTION_SECTION));

		descriptionPart = new TaskEditorDescriptionPart(this, descriptionSection);
		descriptionPart.setInput(connector, repository, taskData);
		descriptionPart.createControl(descriptionSection, toolkit);
		descriptionSection.setClient(descriptionPart.getControl());

		getManagedForm().addPart(descriptionPart);
	}

	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		colorIncoming = themeManager.getCurrentTheme().getColorRegistry().get(
				TaskListColorsAndFonts.THEME_COLOR_TASKS_INCOMING_BACKGROUND);

		super.createFormContent(managedForm);

		this.parentEditor = (TaskEditor) getEditor();

		form = managedForm.getForm();
		toolkit = managedForm.getToolkit();
		registerDropListener(form);

		// ImageDescriptor overlay =
		// TasksUiPlugin.getDefault().getOverlayIcon(repository.getKind());
		// ImageDescriptor imageDescriptor =
		// TaskListImages.createWithOverlay(TaskListImages.REPOSITORY, overlay,
		// false,
		// false);
		// form.setImage(TaskListImages.getImage(imageDescriptor));

		// toolkit.decorateFormHeading(form.getForm());

		editorComposite = form.getBody();
		GridLayout editorLayout = new GridLayout();
		editorComposite.setLayout(editorLayout);
		editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (taskData == null) {
			parentEditor.setMessage(
					"Task data not available. Press synchronize button (right) to retrieve latest data.",
					IMessageProvider.WARNING);
		} else {
			createSections();
		}

		// setFormHeaderLabel();
		addHeaderControls();
	}

	private void createNewCommentSection(Composite composite) {
		Section newCommentSection = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR);
		newCommentSection.setText(getSectionLabel(SECTION_NAME.NEWCOMMENT_SECTION));
		newCommentSection.setLayout(new GridLayout());
		newCommentSection.setLayoutData(new GridData(GridData.FILL_BOTH));

		newCommentPart = new TaskEditorNewCommentPart(this);
		newCommentPart.setInput(connector, repository, taskData);
		newCommentPart.createControl(composite, toolkit);

		getManagedForm().addPart(newCommentPart);
	}

	private void createPeopleSection(Composite composite) {
		Section peopleSection = createSection(composite, getSectionLabel(SECTION_NAME.PEOPLE_SECTION));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, true).applyTo(peopleSection);

		TaskEditorPeoplePart peoplePart = new TaskEditorPeoplePart(this);
		getManagedForm().addPart(peoplePart);
		peoplePart.setInput(connector, repository, taskData);
		peoplePart.createControl(peopleSection, toolkit);		
		peopleSection.setClient(peoplePart.getControl());
	}

	ImageHyperlink createReplyHyperlink(final int commentNum, Composite composite, final String commentBody) {
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
				StringBuilder strBuilder = new StringBuilder();
				strBuilder.append(" (In reply to comment #" + commentNum + ")\n");
				CommentQuoter quoter = new CommentQuoter();
				strBuilder.append(quoter.quote(commentBody));
				newCommentPart.appendText(strBuilder.toString());
				newCommentPart.setFocus();
			}
		});

		return replyLink;
	}

	private Section createSection(Composite composite, String title) {
		Section section = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setText(title);
		section.setExpanded(true);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return section;
	}

	private void createSections() {
		createSummarySection(editorComposite);

		createAttributeSection();

		if (showAttachments) {
			createAttachmentSection(editorComposite);
		}

		createDescriptionSection(editorComposite);
		createCommentSection(editorComposite);
		createNewCommentSection(editorComposite);

		Composite bottomComposite = toolkit.createComposite(editorComposite);
		bottomComposite.setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(bottomComposite);

		createActionsSection(bottomComposite);
		createPeopleSection(bottomComposite);

		bottomComposite.pack(true);
		form.reflow(true);

		getSite().getPage().addSelectionListener(selectionListener);
		getSite().setSelectionProvider(selectionProvider);
	}

	private void createSummarySection(Composite composite) {
		summaryPart = new TaskEditorSummaryPart(this);
		summaryPart.setInput(connector, repository, taskData);
		summaryPart.createControl(composite, toolkit);

		getManagedForm().addPart(summaryPart);
	}

	// TODO EDITOR move
	protected void deleteAttachment(RepositoryAttachment attachment) {
	}

	// TODO EDITOR move
	protected void deleteComment(TaskComment comment) {
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

	@Override
	public void doSave(IProgressMonitor monitor) {
		saveTaskOffline(monitor);
		updateEditorTitle();
	}

	@Override
	public void doSaveAs() {
		// we don't save, so no need to implement
	}

	/**
	 * Override for customizing the toolbar.
	 * 
	 * @since 2.1 (NOTE: likely to change for 3.0)
	 */
	protected void fillToolBar(IToolBarManager toolBarManager) {
		if (taskData != null && !taskData.isNew()) {
			if (repositoryTask != null) {
				synchronizeEditorAction = new SynchronizeEditorAction();
				synchronizeEditorAction.selectionChanged(new StructuredSelection(this));
				toolBarManager.add(synchronizeEditorAction);

				NewSubTaskAction newSubTaskAction = new NewSubTaskAction();
				newSubTaskAction.selectionChanged(newSubTaskAction, new StructuredSelection(repositoryTask));
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

	/**
	 * Fires a <code>SelectionChangedEvent</code> to all listeners registered under
	 * <code>selectionChangedListeners</code>.
	 * 
	 * @param event
	 *            The selection event.
	 */
	protected void fireSelectionChanged(final SelectionChangedEvent event) {
		Object[] listeners = selectionChangedListeners.toArray();
		for (Object element : listeners) {
			final ISelectionChangedListener l = (ISelectionChangedListener) element;
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
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

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (IContentOutlinePage.class.equals(adapter)) {
			if (outlinePage == null && editorInput != null && taskOutlineModel != null) {
				outlinePage = new RepositoryTaskOutlinePage(taskOutlineModel);
			}
			return outlinePage;
		}
		return super.getAdapter(adapter);
	}

	protected abstract AttributeEditorFactory getAttributeEditorFactory();

	protected abstract AbstractAttributeEditorManager getAttributeEditorManager();

	public abstract AttributeEditorToolkit getAttributeEditorToolkit();

	protected AbstractTaskCategory getCategory() {
		return null;
	}

	// TODO EDITOR make private?
	public Color getColorIncoming() {
		return colorIncoming;
	}

	public AbstractRepositoryConnector getConnector() {
		return connector;
	}

	/**
	 * @return The composite for the whole editor.
	 */
	public Composite getEditorComposite() {
		return editorComposite;
	}

	/**
	 * Override to make hyperlink available. If not overridden hyperlink will simply not be displayed.
	 * 
	 * @return url String form of url that points to task's past activity
	 */
	// TODO EDITOR move to tasks core
	protected String getHistoryUrl() {
		return null;
	}

	public RepositoryTaskOutlinePage getOutline() {
		return outlinePage;
	}

	/**
	 * @since 2.1
	 */
	public TaskEditor getParentEditor() {
		return parentEditor;
	}

	private String getSectionLabel(SECTION_NAME labelName) {
		return labelName.getPrettyName();
	}

	protected IJobChangeListener getSubmitJobListener() {
		return null;
	}

	public RepositoryTaskData getTaskData() {
		return taskData;
	}

	public TaskRepository getTaskRepository() {
		return repository;
	}
	
	private IStatus handleSubmitError(final CoreException exception) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (form != null && !form.isDisposed()) {
					if (exception.getStatus().getCode() == RepositoryStatus.ERROR_IO) {
						parentEditor.setMessage(ERROR_NOCONNECTIVITY, IMessageProvider.ERROR);
						StatusHandler.log(exception.getStatus());
					} else if (exception.getStatus().getCode() == RepositoryStatus.REPOSITORY_COMMENT_REQUIRED) {
						StatusHandler.displayStatus("Comment required", exception.getStatus());
						if (!getManagedForm().getForm().isDisposed() && newCommentPart != null) {
							newCommentPart.setFocus();
						}
					} else if (exception.getStatus().getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN) {
						if (TasksUiUtil.openEditRepositoryWizard(repository) == Window.OK) {
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

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		if (getEditorInput() instanceof RepositoryTaskEditorInput) {
			RepositoryTaskEditorInput existingInput = (RepositoryTaskEditorInput) getEditorInput();
			setPartName(existingInput.getName());
		} else if (getEditorInput() instanceof NewTaskEditorInput) {
			String label = ((NewTaskEditorInput) getEditorInput()).getName();
			setPartName(label);
		}

		if (!(input instanceof RepositoryTaskEditorInput)) {
			return;
		}

		initTaskEditor(site, (RepositoryTaskEditorInput) input);

		if (taskData != null) {
			// TODO EDITOR editorInput.setToolTipText(taskData.getLabel());
			this.taskOutlineModel = RepositoryTaskOutlineNode.parseBugReport(taskData);
		}

		TasksUiPlugin.getTaskListManager().getTaskList().addChangeListener(TASKLIST_CHANGE_LISTENER);
	}

	protected void initTaskEditor(IEditorSite site, RepositoryTaskEditorInput input) {
		changedAttributes = new HashSet<RepositoryTaskAttribute>();
		editorInput = input;
		repositoryTask = editorInput.getRepositoryTask();
		repository = editorInput.getRepository();
		taskData = editorInput.getTaskData();
		connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getConnectorKind());

		setSite(site);
		setInput(input);

		isDirty = false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * @since 2.0 If existing task editor, update contents in place
	 */
	public void refreshEditor() {
		try {
			if (!getManagedForm().getForm().isDisposed()) {
				if (this.isDirty && !taskData.isNew()) {
					this.doSave(new NullProgressMonitor());
				}
				setGlobalBusy(true);
				changedAttributes.clear();
				// TODO EDITOR commentComposites.clear();
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
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (editorComposite != null && !editorComposite.isDisposed()) {
							if (taskData == null) {
								parentEditor.setMessage(
										"Task data not available. Press synchronize button (right) to retrieve latest data.",
										IMessageProvider.WARNING);
							} else {

								updateEditorTitle();
								menu = editorComposite.getMenu();
								removeSections();
								editorComposite.setMenu(menu);
								createSections();
								// setFormHeaderLabel();
								markDirty(false);
								parentEditor.setMessage(null, 0);
								AbstractTaskEditorPage.this.getEditor().setActivePage(
										AbstractTaskEditorPage.this.getId());

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

								if (actionPart != null) {
									actionPart.setSubmitEnabled(true);
								}
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

	private void registerDropListener(final Control control) {
		DropTarget target = new DropTarget(control, DND.DROP_COPY | DND.DROP_DEFAULT);
		final TextTransfer textTransfer = TextTransfer.getInstance();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		Transfer[] types = new Transfer[] { textTransfer, fileTransfer };
		target.setTransfer(types);

		// Adapted from eclipse.org DND Article by Veronika Irvine, IBM OTI Labs
		// http://www.eclipse.org/articles/Article-SWT-DND/DND-in-SWT.html#_dt10D
		// TODO EDITOR
		//target.addDropListener(new RepositoryTaskEditorDropListener(this, fileTransfer, textTransfer, control));
	}

	public void removeAttributeListener(IRepositoryTaskAttributeListener listener) {
		attributesListeners.remove(listener);
	}

	private void removeSections() {
		menu = editorComposite.getMenu();
		setMenu(editorComposite, null);
		for (Control control : editorComposite.getChildren()) {
			control.dispose();
		}
	}

	/**
	 * @see #addSelectableControl(Object, Control)
	 */
	public void removeSelectableControl(Object item) {
		controlBySelectableObject.remove(item);
	}

	/**
	 * force a re-layout of entire form
	 */
	protected void resetLayout() {
		if (reflow) {
			form.layout(true, true);
			form.reflow(true);
		}
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
					EditorUtil.toggleExpandableComposite(true, ex);
				}
			}

			// expand all parents of control
			Composite comp = control.getParent();
			while (comp != null) {
				if (comp instanceof Section) {
					((Section) comp).setExpanded(true);
				} else if (comp instanceof ExpandableComposite) {
					ExpandableComposite ex = (ExpandableComposite) comp;
					if (!ex.isExpanded()) {
						EditorUtil.toggleExpandableComposite(true, ex);
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

	public void setExpandAttributeSection(boolean expandAttributeSection) {
		this.expandedStateAttributes = expandAttributeSection;
	}

	@Override
	public void setFocus() {
		if (summaryPart != null) {
			if (firstFocus) {
				summaryPart.setFocus();
				firstFocus = false;
			}
		} else {
			form.setFocus();
		}
	}

	public void setGlobalBusy(boolean busy) {
		if (parentEditor != null) {
			parentEditor.showBusy(busy);
		} else {
			showBusy(busy);
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

	public void setParentEditor(TaskEditor parentEditor) {
		this.parentEditor = parentEditor;
	}

	public void setReflow(boolean refreshEnabled) {
		this.reflow = refreshEnabled;
		form.setRedraw(reflow);
	}

	public void setShowAttachments(boolean showAttachments) {
		this.showAttachments = showAttachments;
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

			if (actionPart != null) {
				actionPart.setSubmitEnabled(!busy);
			}

			EditorUtil.setEnabledState(editorComposite, !busy);

			formBusy = busy;
		}
	}

	public void submitToRepository() {
		setGlobalBusy(true);

		if (isDirty()) {
			saveTaskOffline(new NullProgressMonitor());
			markDirty(false);
		}

		final boolean attachContext = actionPart.getAttachContext();

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
					StatusHandler.fail(e, e.getMessage(), true);
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

	protected boolean supportsAttachmentDelete() {
		return false;
	}

	protected boolean supportsCommentDelete() {
		return false;
	}

	/**
	 * @since 2.2
	 */
	protected boolean supportsRefreshAttributes() {
		return true;
	}

	/**
	 * Updates the title of the editor
	 */
	protected void updateEditorTitle() {
		setPartName(editorInput.getName());
		((TaskEditor) this.getEditor()).updateTitle(editorInput.getName());
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

	protected abstract void validateInput();

}
