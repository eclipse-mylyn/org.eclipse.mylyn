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
package org.eclipse.mylar.internal.tasks.ui.editors;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.tasks.ui.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasks.ui.search.SearchHitCollector;
import org.eclipse.mylar.internal.tasks.ui.views.DatePicker;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskCategory;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * An editor used to view a locally created bug that does not yet exist on a
 * server.
 * 
 * @author Rob Elves (modifications)
 */
public abstract class AbstractNewRepositoryTaskEditor extends AbstractRepositoryTaskEditor {

	private static final int DEFAULT_ESTIMATED_TIME = 1;

	private static final String LABEL_CREATE = "Create New";

	private static final String LABEL_SEARCH_DUPS = "Search for Duplicates";

	private static final String ERROR_CREATING_BUG_REPORT = "Error creating bug report";

	private static final String NO_STACK_MESSAGE = "Unable to locate a stack trace in the description text.\nDuplicate search currently only supports stack trace matching.";

	protected RepositoryTaskData taskData;

	protected Button searchDuplicatesButton;

	protected DatePicker datePicker;

	protected Spinner estimated;

	protected String newSummary = "";

	protected Button addToCategory;

	protected CCombo categoryCombo;

	// protected JobChangeAdapter submitJobListener = new JobChangeAdapter() {
	// @Override
	// public void done(final IJobChangeEvent event) {
	// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
	// public void run() {
	// if (event.getJob().getResult().getCode() == Status.OK
	// && event.getJob().getResult().getMessage() != null) {
	// handleOkayStatus(event);
	// } else {// if (event.getJob().getResult().getCode() ==
	// // Status.ERROR) {
	// handleSubmitError(event);
	// }
	// }
	// });
	// }
	// };

	public AbstractNewRepositoryTaskEditor(FormEditor editor) {
		super(editor);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		NewTaskEditorInput ei = (NewTaskEditorInput) input;
		setSite(site);
		setInput(input);
		editorInput = ei;
		taskData = ei.getTaskData();
		taskOutlineModel = RepositoryTaskOutlineNode.parseBugReport(taskData, false);
		newSummary = taskData.getSummary();
		repository = editorInput.getRepository();
		connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
		isDirty = false;
	}

	@Override
	public RepositoryTaskData getRepositoryTaskData() {
		return taskData;
	}

	@Override
	protected void createDescriptionLayout(Composite composite) {
		FormToolkit toolkit = this.getManagedForm().getToolkit();
		Section section = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR);
		section.setText(LABEL_SECTION_DESCRIPTION);
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite descriptionComposite = toolkit.createComposite(section);
		GridLayout descriptionLayout = new GridLayout();

		descriptionComposite.setLayout(descriptionLayout);
		GridData descriptionData = new GridData(GridData.FILL_BOTH);
		descriptionData.grabExcessVerticalSpace = true;
		descriptionComposite.setLayoutData(descriptionData);
		section.setClient(descriptionComposite);

		descriptionTextViewer = addTextEditor(repository, descriptionComposite,
				getRepositoryTaskData().getNewComment(), true, SWT.FLAT | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		descriptionTextViewer.setEditable(true);

		GridData descriptionTextData = new GridData(GridData.FILL_BOTH);
		descriptionTextViewer.getControl().setLayoutData(descriptionTextData);
		descriptionTextViewer.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

		toolkit.paintBordersFor(descriptionComposite);

	}

	@Override
	protected void createReportHeaderLayout(Composite comp) {
		addSummaryText(comp);
	}

	// @Override
	// protected void createReportHeaderLayout(Composite comp) {
	// FormToolkit toolkit = new FormToolkit(comp.getDisplay());
	// Composite headerComposite = toolkit.createComposite(editorComposite);
	// headerComposite.setLayout(new GridLayout(2, false));
	// GridDataFactory.fillDefaults().grab(true,
	// false).applyTo(headerComposite);
	// toolkit.createLabel(headerComposite, "Posting To:").setFont(TITLE_FONT);
	// Text target = toolkit.createText(headerComposite, repository.getUrl(),
	// SWT.FLAT);
	// target.setFont(TITLE_FONT);
	// target.setEditable(false);
	// addSummaryText(headerComposite);
	// toolkit.paintBordersFor(headerComposite);
	// }

	@Override
	protected void createAttachmentLayout(Composite comp) {
		// currently can't attach while creating new bug
	}

	@Override
	protected void createCommentLayout(Composite comp) {
		// Since NewBugModels have no comments, there is no
		// GUI for them.
	}

	@Override
	protected void createNewCommentLayout(Composite comp) {
		createPlanningLayout(comp);
	}

	protected void createPlanningLayout(Composite comp) {
		FormToolkit toolkit = this.getManagedForm().getToolkit();

		Section section = toolkit.createSection(comp, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText("Personal Planning");
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.setExpanded(true);

		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		GridLayout layout = new GridLayout();
		layout.numColumns = 7;
		layout.makeColumnsEqualWidth = false;
		sectionClient.setLayout(layout);
		GridData clientDataLayout = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		sectionClient.setLayoutData(clientDataLayout);

		// Reminder
		Label label = toolkit.createLabel(sectionClient, "Scheduled for:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		datePicker = new DatePicker(sectionClient, SWT.NONE, DatePicker.LABEL_CHOOSE);
		datePicker.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		datePicker.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		Calendar newTaskSchedule = Calendar.getInstance();
		int scheduledEndHour = TasksUiPlugin.getDefault().getPreferenceStore().getInt(
				TaskListPreferenceConstants.PLANNING_ENDHOUR);
		// If past scheduledEndHour set for following day
		if (newTaskSchedule.get(Calendar.HOUR_OF_DAY) >= scheduledEndHour) {
			TasksUiPlugin.getTaskListManager().setSecheduledIn(newTaskSchedule, 1);
		} else {
			TasksUiPlugin.getTaskListManager().setScheduledToday(newTaskSchedule);
		}
		datePicker.setDate(newTaskSchedule);
		Button removeReminder = toolkit.createButton(sectionClient, "Clear", SWT.PUSH | SWT.CENTER);
		removeReminder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				datePicker.setDate(null);
			}
		});

		// 1 Blank column after Reminder clear button
		Label dummy = toolkit.createLabel(sectionClient, "");
		GridData dummyLabelDataLayout = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		dummyLabelDataLayout.horizontalSpan = 1;
		dummyLabelDataLayout.widthHint = 30;
		dummy.setLayoutData(dummyLabelDataLayout);

		// Estimated time
		label = toolkit.createLabel(sectionClient, "Estimated time:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		estimated = new Spinner(sectionClient, SWT.NONE);
		estimated.setDigits(0);
		estimated.setMaximum(100);
		estimated.setMinimum(0);
		estimated.setIncrement(1);
		estimated.setSelection(DEFAULT_ESTIMATED_TIME);
		estimated.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		GridData estimatedDataLayout = new GridData();
		estimatedDataLayout.widthHint = 110;
		estimated.setLayoutData(estimatedDataLayout);
		label = toolkit.createLabel(sectionClient, "hours ");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		// 1 Blank column
		Label blankLabel2 = toolkit.createLabel(sectionClient, "");
		GridData blankLabl2Layout = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		blankLabl2Layout.horizontalSpan = 1;
		blankLabl2Layout.widthHint = 25;
		blankLabel2.setLayoutData(blankLabl2Layout);

		toolkit.paintBordersFor(sectionClient);

	}

	@Override
	protected void addRadioButtons(Composite buttonComposite) {
		// Since NewBugModels have no special submitting actions,
		// no radio buttons are required.
	}

	@Override
	protected void createCustomAttributeLayout(Composite composite) {
		// ignore
	}

	@Override
	protected String getTitleString() {
		return taskData.getLabel();
	}

	// public boolean searchForDuplicates() {
	//
	// String stackTrace = getStackTraceFromDescription();
	// if (stackTrace == null) {
	// MessageDialog.openWarning(null, "No Stack Trace Found",
	// NO_STACK_MESSAGE);
	// return false;
	// }
	//
	// String queryUrl = "";
	// try {
	// queryUrl = repository.getUrl() +
	// "/buglist.cgi?long_desc_type=allwordssubstr&long_desc="
	// + URLEncoder.encode(stackTrace, BugzillaCorePlugin.ENCODING_UTF_8);
	// } catch (UnsupportedEncodingException e) {
	// // This should never happen
	// }
	//
	// queryUrl += "&product=" + getRepositoryTaskData().getProduct();
	//
	// BugzillaRepositoryQuery bugzillaQuery = new
	// BugzillaRepositoryQuery(repository.getUrl(), queryUrl, "search",
	// "100", TasksUiPlugin.getTaskListManager().getTaskList());
	// Proxy proxySettings = TasksUiPlugin.getDefault().getProxySettings();
	// SearchHitCollector collector = new
	// SearchHitCollector(TasksUiPlugin.getTaskListManager().getTaskList(),
	// repository, bugzillaQuery, proxySettings);
	//
	// NewSearchUI.runQueryInBackground(collector);
	// return true;
	// }

	public String getStackTraceFromDescription() {
		String description = descriptionTextViewer.getTextWidget().getText().trim();
		String stackTrace = null;

		if (description == null) {
			return null;
		}

		String punct = "!\"#$%&'\\(\\)*+,-./:;\\<=\\>?@\\[\\]^_`\\{|\\}~\n";
		String lineRegex = " *at\\s+[\\w" + punct + "]+ ?\\(.*\\) *\n?";
		Pattern tracePattern = Pattern.compile(lineRegex);
		Matcher match = tracePattern.matcher(description);

		if (match.find()) {
			// record the index of the first stack trace line
			int start = match.start();
			int lastEnd = match.end();

			// find the last stack trace line
			while (match.find()) {
				lastEnd = match.end();
			}

			// make sure there's still room to find the exception
			if (start <= 0) {
				return null;
			}

			// count back to the line before the stack trace to find the
			// exception
			int stackStart = 0;
			int index = start - 1;
			while (index > 1 && description.charAt(index) == ' ') {
				index--;
			}

			// locate the exception line index
			stackStart = description.substring(0, index - 1).lastIndexOf("\n");
			stackStart = (stackStart == -1) ? 0 : stackStart + 1;

			stackTrace = description.substring(stackStart, lastEnd);
		}

		return stackTrace;
	}

	@Override
	protected void updateTask() {
		taskData.setSummary(newSummary);
		taskData.setDescription(descriptionTextViewer.getTextWidget().getText());
	}

	/**
	 * A listener for selection of the description textbox.
	 */
	protected class DescriptionListener implements Listener {
		public void handleEvent(Event event) {
			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
					new RepositoryTaskSelection(taskData.getId(), taskData.getRepositoryUrl(), "New Description",
							false, taskData.getSummary()))));
		}
	}

	@Override
	public void handleSummaryEvent() {
		String sel = summaryText.getText();
		if (!(newSummary.equals(sel))) {
			newSummary = sel;
			markDirty(true);
		}
	}

	@Override
	protected void validateInput() {
		// ignore
	}

	@Override
	public boolean isDirty() {
		return true;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Creates the button layout. This displays options and buttons at the
	 * bottom of the editor to allow actions to be performed on the bug.
	 */
	@Override
	protected void createActionsLayout(Composite formComposite) {
		FormToolkit toolkit = new FormToolkit(formComposite.getDisplay());
		Section section = toolkit.createSection(formComposite, ExpandableComposite.TITLE_BAR);
		section.setText(LABEL_SECTION_ACTIONS);
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite buttonComposite = toolkit.createComposite(section);
		buttonComposite.setLayout(new GridLayout(4, false));
		buttonComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		section.setClient(buttonComposite);
		addActionButtons(buttonComposite);

		addToCategory = toolkit.createButton(buttonComposite, "Add to Category", SWT.CHECK);
		categoryCombo = new CCombo(buttonComposite, SWT.FLAT | SWT.READ_ONLY);
		categoryCombo.setLayoutData(GridDataFactory.swtDefaults().hint(150, SWT.DEFAULT).create());
		toolkit.adapt(categoryCombo, true, true);
		categoryCombo.setFont(TEXT_FONT);
		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		List<AbstractTaskContainer> categories = taskList.getUserCategories();
		Collections.sort(categories, new Comparator<AbstractTaskContainer>() {

			public int compare(AbstractTaskContainer c1, AbstractTaskContainer c2) {
				return c1.getSummary().compareToIgnoreCase(c2.getSummary());
			}

		});
		categoryCombo.add("<root>");
		for (AbstractTaskContainer category : categories) {
			categoryCombo.add(category.getSummary());
		}
		categoryCombo.select(0);
		categoryCombo.setEnabled(false);
		categoryCombo.setData(categories);
		addToCategory.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				categoryCombo.setEnabled(addToCategory.getSelection());
			}

		});

		toolkit.paintBordersFor(buttonComposite);
	}

	/**
	 * Returns the {@link AbstractTaskContainer category} the new task belongs
	 * to
	 * 
	 * @return {@link AbstractTaskContainer category} where the new task must be
	 *         added to, or null if it must not be added to the task list
	 */
	@SuppressWarnings("unchecked")
	protected AbstractTaskContainer getCategory() {
		int index = categoryCombo.getSelectionIndex();
		if (addToCategory.getSelection() && index != -1) {
			if (index == 0) {
				return TasksUiPlugin.getTaskListManager().getTaskList().getRootCategory();
			}
			return ((List<AbstractTaskContainer>) categoryCombo.getData()).get(index - 1);
		}
		return null;
	}

	@Override
	protected void addActionButtons(Composite buttonComposite) {
		FormToolkit toolkit = new FormToolkit(buttonComposite.getDisplay());

		searchDuplicatesButton = toolkit.createButton(buttonComposite, LABEL_SEARCH_DUPS, SWT.NONE);
		GridData searchDuplicatesButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		searchDuplicatesButton.setLayoutData(searchDuplicatesButtonData);
		searchDuplicatesButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				searchForDuplicates();
			}
		});

		submitButton = toolkit.createButton(buttonComposite, LABEL_CREATE, SWT.NONE);
		GridData submitButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		submitButton.setLayoutData(submitButtonData);
		submitButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				submitToRepository();
			}
		});
		submitButton.addListener(SWT.FocusIn, new GenericListener());
	}

	protected boolean prepareSubmit() {
		submitButton.setEnabled(false);
		showBusy(true);

		if (summaryText != null && summaryText.getText().trim().equals("")) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openInformation(AbstractNewRepositoryTaskEditor.this.getSite().getShell(),
							ERROR_CREATING_BUG_REPORT, "A summary must be provided with new bug reports.");
					summaryText.setFocus();
					submitButton.setEnabled(true);
					showBusy(false);
				}
			});
			return false;
		}

		if (descriptionTextViewer != null && descriptionTextViewer.getTextWidget().getText().trim().equals("")) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openInformation(AbstractNewRepositoryTaskEditor.this.getSite().getShell(),
							ERROR_CREATING_BUG_REPORT, "A description must be provided with new reports.");
					descriptionTextViewer.getTextWidget().setFocus();
					submitButton.setEnabled(true);
					showBusy(false);
				}
			});
			return false;
		}

		return true;
	}

	public boolean searchForDuplicates() {

		String stackTrace = getStackTraceFromDescription();
		if (stackTrace == null) {
			MessageDialog.openWarning(null, "No Stack Trace Found", NO_STACK_MESSAGE);
			return false;
		}
		SearchHitCollector collector = getDuplicateSearchCollector(stackTrace);
		if (collector != null) {
			NewSearchUI.runQueryInBackground(collector);
			return true;
		}

		return false;
	}

	@Override
	protected void createPeopleLayout(Composite composite) {
		// ignore, new editor doesn't have people section
	}

	public AbstractRepositoryTask handleNewBugPost(String id) throws CoreException {
		AbstractRepositoryTask newTask = super.handleNewBugPost(id);

		if (newTask != null) {
			Calendar selectedDate = datePicker.getDate();
			if (selectedDate != null) {
				// NewLocalTaskAction.scheduleNewTask(newTask);
				TasksUiPlugin.getTaskListManager().setScheduledFor(newTask, selectedDate.getTime());
			}

			newTask.setEstimatedTimeHours(estimated.getSelection());

			Object selectedObject = null;
			if (TaskListView.getFromActivePerspective() != null)
				selectedObject = ((IStructuredSelection) TaskListView.getFromActivePerspective().getViewer()
						.getSelection()).getFirstElement();

			if (selectedObject instanceof TaskCategory) {
				TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer(((TaskCategory) selectedObject),
						newTask);
			}
		}

		return newTask;
	}

	// protected void handleOkayStatus(final IJobChangeEvent event) {
	// close();
	// String newTaskHandle =
	// AbstractRepositoryTask.getHandle(repository.getUrl(),
	// event.getJob().getResult()
	// .getMessage());
	// ITask newTask =
	// TasksUiPlugin.getTaskListManager().getTaskList().getTask(newTaskHandle);
	// if (newTask != null) {
	// Calendar selectedDate = datePicker.getDate();
	// if (selectedDate != null) {
	// // NewLocalTaskAction.scheduleNewTask(newTask);
	// TasksUiPlugin.getTaskListManager().setScheduledFor(newTask,
	// selectedDate.getTime());
	// }
	//
	// newTask.setEstimatedTimeHours(estimated.getSelection());
	//
	// Object selectedObject = null;
	// if (TaskListView.getFromActivePerspective() != null)
	// selectedObject = ((IStructuredSelection)
	// TaskListView.getFromActivePerspective().getViewer()
	// .getSelection()).getFirstElement();
	//
	// if (selectedObject instanceof TaskCategory) {
	// TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer(((TaskCategory)
	// selectedObject),
	// newTask);
	// }
	// TaskUiUtil.refreshAndOpenTaskListElement(newTask);
	// }
	// }

	protected abstract SearchHitCollector getDuplicateSearchCollector(String description);

}
