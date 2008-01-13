/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.core.CommentQuoter;
import org.eclipse.mylyn.internal.tasks.ui.PersonProposalLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.PersonProposalProvider;
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
import org.eclipse.mylyn.tasks.core.RepositoryOperation;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.ui.AbstractDuplicateDetector;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRenderingEngine;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskFormPage;
import org.eclipse.mylyn.tasks.ui.search.SearchHitCollector;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
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
import org.eclipse.ui.keys.IBindingService;
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

	/**
	 * Class to handle the selection change of the radio buttons.
	 */
	private class RadioButtonListener implements SelectionListener, ModifyListener {

		public void modifyText(ModifyEvent e) {
			Button selected = null;
			for (Button element : radios) {
				if (element.getSelection()) {
					selected = element;
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

		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		public void widgetSelected(SelectionEvent e) {
			Button selected = null;
			for (Button element : radios) {
				if (element.getSelection()) {
					selected = element;
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
	}

	// API-3.0 rename ATTRIBTUES_SECTION to ATTRIBUTES_SECTION (bug 208629)
	protected enum SECTION_NAME {
		ACTIONS_SECTION("Actions"), ATTACHMENTS_SECTION("Attachments"), ATTRIBTUES_SECTION("Attributes"), COMMENTS_SECTION(
				"Comments"), DESCRIPTION_SECTION("Description"), NEWCOMMENT_SECTION("New Comment"), PEOPLE_SECTION("People"), RELATEDBUGS_SECTION(
				"Related Tasks");

		private String prettyName;

		SECTION_NAME(String prettyName) {
			this.prettyName = prettyName;
		}

		public String getPrettyName() {
			return prettyName;
		}
	}

	protected static final String CONTEXT_MENU_ID = "#MylynRepositoryEditor";

	private static final int DESCRIPTION_HEIGHT = 10 * 14;

	private static final int DESCRIPTION_WIDTH = 79 * 7; // 500;

	private static final String ERROR_NOCONNECTIVITY = "Unable to submit at this time. Check connectivity and retry.";

//	private static final String LABEL_NO_DETECTOR = "No duplicate detector available.";

	private static final String LABEL_BUTTON_SUBMIT = "Submit";

	private static final String LABEL_HISTORY = "History";

	private static final String LABEL_JOB_SUBMIT = "Submitting to repository";

	private static final String LABEL_REPLY = "Reply";

	private static final String LABEL_SEARCH_DUPS = "Search";

	private static final String LABEL_SELECT_DETECTOR = "Duplicate Detection";

	private static final int RADIO_OPTION_WIDTH = 120;

	protected static final int SUMMARY_HEIGHT = 20;

	protected static final Font TEXT_FONT = JFaceResources.getDefaultFont();

	private static final Font TITLE_FONT = JFaceResources.getBannerFont();

	private ToggleTaskActivationAction activateAction;

	private StyledText addCommentsTextBox = null;

	private Button attachContextButton;

	private boolean attachContextEnabled = true;

	private final List<IRepositoryTaskAttributeListener> attributesListeners = new ArrayList<IRepositoryTaskAttributeListener>();

	private Section attributesSection;

	private Set<RepositoryTaskAttribute> changedAttributes;

	private Color colorIncoming;

	private TaskEditorCommentPart commentPart;

	private Section commentsSection;

	private AbstractRepositoryConnector connector;

	private final HashMap<Object, Control> controlBySelectableObject = new HashMap<Object, Control>();

	protected TextViewer descriptionTextViewer = null;

	protected CCombo duplicateDetectorChooser;

	protected Label duplicateDetectorLabel;

	private Composite editorComposite;

	private RepositoryTaskEditorInput editorInput;

	private boolean expandedStateAttributes = false;

	// once the following bug is fixed, this check for first focus is probably
	// not needed -> Bug# 172033: Restore editor focus
	private boolean firstFocus = true;

	private ScrolledForm form;

	private boolean formBusy = false;

	private boolean hasAttributeChanges = false;

	private Action historyAction;

	private boolean ignoreLocationEvents = false;

	private IRepositoryTaskSelection lastSelected = null;

	private Menu menu;

	private TextViewer newCommentTextViewer;

	private Action openBrowserAction;

	private RepositoryTaskOutlinePage outlinePage = null;

	private TaskEditor parentEditor = null;

	private Control[] radioOptions;

	private Button[] radios;

	private boolean reflow = true;

	protected TaskRepository repository;

	private AbstractTask repositoryTask;

	protected Button searchForDuplicates;

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

	protected final ISelectionProvider selectionProvider = new ISelectionProvider() {
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

	protected Button submitButton;

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

							setSubmitEnabled(false);
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

	/**
	 * Creates a new <code>AbstractTaskEditor</code>.
	 */
	public AbstractTaskEditorPage(FormEditor editor) {
		// set the scroll increments so the editor scrolls normally with the
		// scroll wheel
		super(editor, "id", "label"); //$NON-NLS-1$ //$NON-NLS-2$
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

	protected void addAttachContextButton(Composite buttonComposite, AbstractTask task) {
		attachContextButton = toolkit.createButton(buttonComposite, "Attach Context", SWT.CHECK);
		attachContextButton.setImage(TasksUiImages.getImage(TasksUiImages.CONTEXT_ATTACH));
	}

	public void addAttributeListener(IRepositoryTaskAttributeListener listener) {
		attributesListeners.add(listener);
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

	protected void addRadioButtons(Composite buttonComposite) {
		int i = 0;
		Button selected = null;
		radios = new Button[taskData.getOperations().size()];
		radioOptions = new Control[taskData.getOperations().size()];
		for (RepositoryOperation o : taskData.getOperations()) {
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
	public TextViewer addTextViewer(TaskRepository repository, Composite composite, String text, int style) {
		return super.addTextViewer(repository, composite, text, style);
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
		attributesSection.setExpanded(expandedStateAttributes || hasAttributeChanges);

		TaskEditorAttributePart attributePart = new TaskEditorAttributePart(this, attributesSection);
		attributePart.setInput(connector, repository, taskData);
		attributePart.createControl(commentsSection, toolkit);

		getManagedForm().addPart(attributePart);
	}

	private void createCommentSection(Composite composite) {
		commentsSection = createSection(composite, getSectionLabel(SECTION_NAME.COMMENTS_SECTION));

		commentPart = new TaskEditorCommentPart(this, commentsSection, editorInput, repositoryTask);
		commentPart.setSupportsDelete(supportsCommentDelete());
		commentPart.setInput(connector, repository, taskData);
		commentPart.createControl(commentsSection, toolkit);

		getManagedForm().addPart(commentPart);
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

	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		colorIncoming = themeManager.getCurrentTheme().getColorRegistry().get(
				TaskListColorsAndFonts.THEME_COLOR_TASKS_INCOMING_BACKGROUND);

		super.createFormContent(managedForm);
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

	private void createSummarySection(Composite composite) {
		summaryPart = new TaskEditorSummaryPart(this);
		summaryPart.setInput(connector, repository, taskData);
		summaryPart.createControl(composite, toolkit);

		getManagedForm().addPart(summaryPart);
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

	protected void createPeopleSection(Composite composite) {
		Section peopleSection = createSection(composite, getSectionLabel(SECTION_NAME.PEOPLE_SECTION));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, true).applyTo(peopleSection);
		
		TaskEditorPeoplePart peoplePart = new TaskEditorPeoplePart(this);
		peoplePart.setInput(connector, repository, taskData);
		peoplePart.createControl(composite, toolkit);

		getManagedForm().addPart(peoplePart);
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
			private static final String LABEL_BUTTON_EDIT = "Edit";

			private static final String LABEL_BUTTON_PREVIEW = "Preview";

			private int buttonState = 0;

			private Browser previewBrowser;

			private Button previewButton;

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

	protected ILabelProvider createProposalLabelProvider(RepositoryOperation operation) {

		return new PersonProposalLabelProvider();
	}

	protected ILabelProvider createProposalLabelProvider(RepositoryTaskAttribute attribute) {
		return new PersonProposalLabelProvider();
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

	protected Section createSection(Composite composite, String title) {
		Section section = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setText(title);
		section.setExpanded(true);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return section;
	}

	private void createSections() {
		createSummarySection(editorComposite);

		createAttributeSection();

		createRelatedBugsSection(editorComposite);

		if (showAttachments) {
			createAttachmentSection(editorComposite);
		}
		createDescriptionLayout(editorComposite);
		createCommentSection(editorComposite);
		createNewCommentLayout(editorComposite);
		Composite bottomComposite = toolkit.createComposite(editorComposite);
		bottomComposite.setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(bottomComposite);

		createActionsLayout(bottomComposite);
		createPeopleSection(bottomComposite);
		bottomComposite.pack(true);
		form.reflow(true);
		getSite().getPage().addSelectionListener(selectionListener);
		getSite().setSelectionProvider(selectionProvider);
	}

	protected void deleteAttachment(RepositoryAttachment attachment) {

	}

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
		//updateTask();
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
	 * @author Raphael Ackermann (bug 195514)
	 * @since 2.1
	 */
	protected void focusAttributes() {
		if (attributesSection != null) {
			focusOn(attributesSection, false);
		}
	}

	// // TODO: Remove once offline persistence is improved
	// private void runSaveJob() {
	// Job saveJob = new Job("Save") {
	//
	// @Override
	// protected IStatus run(IProgressMonitor monitor) {
	// saveTaskOffline(monitor);
	// return Status.OK_STATUS;
	// }
	//
	// };
	// saveJob.setSystem(true);
	// saveJob.schedule();
	// markDirty(false);
	// }

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

	public String formatDate(String dateString) {
		return dateString;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		return getAdapterDelgate(adapter);
	}

	/*----------------------------------------------------------*
	 * CODE TO SCROLL TO A COMMENT OR OTHER PIECE OF TEXT
	 *----------------------------------------------------------*/

	public Object getAdapterDelgate(Class<?> adapter) {
		if (IContentOutlinePage.class.equals(adapter)) {
			if (outlinePage == null && editorInput != null && taskOutlineModel != null) {
				outlinePage = new RepositoryTaskOutlinePage(taskOutlineModel);
			}
			return outlinePage;
		}
		return super.getAdapter(adapter);
	}

	public boolean getAttachContext() {
		if (attachContextButton == null || attachContextButton.isDisposed()) {
			return false;
		} else {
			return attachContextButton.getSelection();
		}
	}

	protected abstract AttributeEditorFactory getAttributeEditorFactory();

	protected abstract AbstractAttributeEditorManager getAttributeEditorManager();

	protected AbstractTaskCategory getCategory() {
		return null;
	}

	public Color getColorIncoming() {
		return colorIncoming;
	}

	public AbstractRepositoryConnector getConnector() {
		return connector;
	}

	public Control getControl() {
		return form;
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

	public AbstractTask getRepositoryTask() {
		return repositoryTask;
	}

	public String getSectionLabel(SECTION_NAME labelName) {
		return labelName.getPrettyName();
	}

	protected IJobChangeListener getSubmitJobListener() {
		return null;
	}

	public RepositoryTaskOutlineNode getTaskOutlineModel() {
		return taskOutlineModel;
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
	 * Called to check if there's content assist available for the given attribute.
	 * 
	 * @param attribute
	 *            the attribute
	 * @return true if content assist is available for the specified attribute.
	 */
	protected boolean hasContentAssist(RepositoryTaskAttribute attribute) {
		return false;
	}

	protected boolean hasOutgoingChange(RepositoryTaskAttribute newAttribute) {
		return editorInput.getOldEdits().contains(newAttribute);
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

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		if (!(input instanceof RepositoryTaskEditorInput)) {
			return;
		}

		initTaskEditor(site, (RepositoryTaskEditorInput) input);

		if (taskData != null) {
			// TODO EDITOR editorInput.setToolTipText(taskData.getLabel());
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

		setSite(site);
		setInput(input);

		isDirty = false;
	}

	public boolean isReflow() {
		return reflow;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private void previewWiki(final Browser browser, String sourceText) {
		final class PreviewWikiJob extends Job {
			private String htmlText;

			private IStatus jobStatus;

			private final String sourceText;

			public PreviewWikiJob(String sourceText) {
				super("Formatting Wiki Text");

				if (sourceText == null) {
					throw new IllegalArgumentException("source text must not be null");
				}

				this.sourceText = sourceText;
			}

			public String getHtmlText() {
				return htmlText;
			}

			public IStatus getStatus() {
				return jobStatus;
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

		}

		final PreviewWikiJob job = new PreviewWikiJob(sourceText);

		job.addJobChangeListener(new JobChangeAdapter() {

			@Override
			public void done(final IJobChangeEvent event) {
				if (!form.isDisposed()) {
					if (job.getStatus().isOK()) {
						getPartControl().getDisplay().asyncExec(new Runnable() {
							public void run() {
								AbstractTaskEditorPage.this.setText(browser, job.getHtmlText());
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
				if (comp instanceof Section) {
					((Section) comp).setExpanded(true);
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

	private void selectNewComment() {
		focusOn(addCommentsTextBox, false);
	}

	public void setAttachContextEnabled(boolean attachContextEnabled) {
		this.attachContextEnabled = attachContextEnabled;
//		if (attachContextButton != null && attachContextButton.isEnabled()) {
//			attachContextButton.setSelection(attachContext);
//		}
	}

	public void setDescriptionText(String text) {
		this.descriptionTextViewer.getDocument().set(text);
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

	private void setSubmitEnabled(boolean enabled) {
		if (submitButton != null && !submitButton.isDisposed()) {
			submitButton.setEnabled(enabled);
			if (enabled) {
				submitButton.setToolTipText("Submit to " + this.repository.getUrl());
			}
		}
	}

	public void setTaskOutlineModel(RepositoryTaskOutlineNode taskOutlineModel) {
		this.taskOutlineModel = taskOutlineModel;
	}

	private void setText(Browser browser, String html) {
		try {
			ignoreLocationEvents = true;
			browser.setText((html != null) ? html : "");
		} finally {
			ignoreLocationEvents = false;
		}

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

			if (submitButton != null && !submitButton.isDisposed()) {
				submitButton.setEnabled(!busy);
			}

			setEnabledState(editorComposite, !busy);

			formBusy = busy;
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
	 * Programmatically expand the provided ExpandableComposite, using reflection to fire the expansion listeners (see
	 * bug#70358)
	 * 
	 * @param comp
	 */
	// TODO EDITOR move to utility class?
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

	/**
	 * Updates the title of the editor
	 * 
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

	public abstract AttributeEditorToolkit getAttributeEditorToolkit();

}
