/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.help.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonFonts;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonThemes;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.ui.LegendElement;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Mik Kersten
 * @author Leo Dos Santos
 */
public class UiLegendDialog extends PopupDialog {

	public static final String TITLE = "Mylyn UI Overview";

	private FormToolkit toolkit;

	private ScrolledForm form;

	private final TaskElementLabelProvider labelProvider = new TaskElementLabelProvider(false);

	private final IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();

	private final ArrayList<LegendElement> legendElements = new ArrayList<LegendElement>();

	// TODO e3.4 move to new api
	@SuppressWarnings("deprecation")
	public UiLegendDialog(Shell parent) {
		super(parent, PopupDialog.INFOPOPUP_SHELLSTYLE | SWT.ON_TOP, false, false, false, false, null, null);
	}

	@Override
	protected Control createContents(Composite parent) {
		getShell().setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		return createDialogArea(parent);
	}

	@Override
	public int open() {
		int open = super.open();
//		getShell().setLocation(getShell().getLocation().x, getShell().getLocation().y+20);
		getShell().setFocus();
		return open;
	}

	@Override
	public boolean close() {
		for (LegendElement element : legendElements) {
			element.dispose();
		}
		if (toolkit != null) {
			if (toolkit.getColors() != null) {
				toolkit.dispose();
			}
		}
		return super.close();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText(TITLE);
		form.getToolBarManager().add(new CloseDialogAction());
		form.getToolBarManager().update(true);
		form.getBody().setLayout(new TableWrapLayout());
		toolkit.decorateFormHeading(form.getForm());

		createTasksPrioritiesSection(form.getBody());
		createActivitySection(form.getBody());
		createContextSection(form.getBody());
		createSynchronizationSection(form.getBody());
		createConnectorsSection(form.getBody());
		createGettingStartedSection(form.getBody());

		return parent;
	}

	private void createTasksPrioritiesSection(Composite parent) {
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		layout.leftMargin = 0;
		layout.rightMargin = 0;
		layout.topMargin = 0;
		layout.bottomMargin = 0;

		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(layout);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		createTasksSection(composite);
		createPrioritiesSection(composite);
	}

	private void createTasksSection(Composite parent) {
		Section tasksSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		tasksSection.setText("Tasks");
		tasksSection.setLayout(new TableWrapLayout());
		tasksSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.verticalSpacing = 1;
		layout.topMargin = 1;
		layout.bottomMargin = 1;

		Composite tasksClient = toolkit.createComposite(tasksSection);
		tasksClient.setLayout(layout);
		tasksClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		tasksSection.setClient(tasksClient);

		Label imageLabel;
		imageLabel = toolkit.createLabel(tasksClient, "");
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.TASK));
		toolkit.createLabel(tasksClient, "Task");

		imageLabel = toolkit.createLabel(tasksClient, "");
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.CATEGORY));
		toolkit.createLabel(tasksClient, "Category");

		imageLabel = toolkit.createLabel(tasksClient, "");
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.QUERY));
		toolkit.createLabel(tasksClient, "Query");

		imageLabel = toolkit.createLabel(tasksClient, "");
		imageLabel.setImage(CommonImages.getImage(CommonImages.CALENDAR));
		toolkit.createLabel(tasksClient, "Date range");

		imageLabel = toolkit.createLabel(tasksClient, "");
		imageLabel.setImage(CommonImages.getImage(CommonImages.BLANK));

		Hyperlink openView = toolkit.createHyperlink(tasksClient, "Open Task List...", SWT.WRAP);
		openView.addHyperlinkListener(new IHyperlinkListener() {
			public void linkActivated(HyperlinkEvent e) {
				close();
				TasksUiUtil.openTasksViewInActivePerspective();
			}

			public void linkEntered(HyperlinkEvent e) {
				// ignore
			}

			public void linkExited(HyperlinkEvent e) {
				// ignore
			}
		});
	}

	private void createPrioritiesSection(Composite parent) {
		Section prioritiesSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		prioritiesSection.setText("Priorities");
		prioritiesSection.setLayout(new TableWrapLayout());
		prioritiesSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.verticalSpacing = 1;
		layout.topMargin = 1;
		layout.bottomMargin = 1;

		Composite prioritiesClient = toolkit.createComposite(prioritiesSection);
		prioritiesClient.setLayout(layout);
		prioritiesClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		prioritiesSection.setClient(prioritiesClient);

		Label imageLabel;
		imageLabel = toolkit.createLabel(prioritiesClient, "");
		imageLabel.setImage(CommonImages.getImage(CommonImages.PRIORITY_1));
		toolkit.createLabel(prioritiesClient, PriorityLevel.P1.getDescription());

		imageLabel = toolkit.createLabel(prioritiesClient, "");
		imageLabel.setImage(CommonImages.getImage(CommonImages.PRIORITY_2));
		toolkit.createLabel(prioritiesClient, PriorityLevel.P2.getDescription());

		imageLabel = toolkit.createLabel(prioritiesClient, "");
		imageLabel.setImage(CommonImages.getImage(CommonImages.PRIORITY_3));
		toolkit.createLabel(prioritiesClient, PriorityLevel.P3.getDescription() + " (default)");

		imageLabel = toolkit.createLabel(prioritiesClient, "");
		imageLabel.setImage(CommonImages.getImage(CommonImages.PRIORITY_4));
		toolkit.createLabel(prioritiesClient, PriorityLevel.P4.getDescription());

		imageLabel = toolkit.createLabel(prioritiesClient, "");
		imageLabel.setImage(CommonImages.getImage(CommonImages.PRIORITY_5));
		toolkit.createLabel(prioritiesClient, PriorityLevel.P5.getDescription());
	}

	private void createActivitySection(Composite parent) {
		Section activitySection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		activitySection.setText("Task Activity");
		activitySection.setLayout(new TableWrapLayout());
		activitySection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.verticalSpacing = 1;
		layout.topMargin = 1;
		layout.bottomMargin = 1;

		Composite activityClient = toolkit.createComposite(activitySection);
		activityClient.setLayout(layout);
		activityClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		activitySection.setClient(activityClient);

		Label imageLabel = toolkit.createLabel(activityClient, "");
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.TASK));
		Label labelToday = toolkit.createLabel(activityClient, "Scheduled for today");
		labelToday.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(
				CommonThemes.COLOR_SCHEDULED_TODAY));

		imageLabel = toolkit.createLabel(activityClient, "");
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.TASK));
		Label labelOverdue = toolkit.createLabel(activityClient, "Past scheduled date");
		labelOverdue.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(
				CommonThemes.COLOR_SCHEDULED_PAST));

// imageLabel = toolkit.createLabel(activityClient, "");
// imageLabel.setImage(TasksUiImages.getImage(TasksUiImages.TASK));
// Label labelThisWeek = toolkit.createLabel(activityClient, "Scheduled for this
// week");
// labelThisWeek.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(
// TaskListColorsAndFonts.THEME_COLOR_TASK_THISWEEK_SCHEDULED));

		imageLabel = toolkit.createLabel(activityClient, "");
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.TASK));
		Label labelCompleted = toolkit.createLabel(activityClient, "Completed");
		labelCompleted.setFont(CommonFonts.STRIKETHROUGH);
// labelCompleted.setForeground(TaskListColorsAndFonts.COLOR_TASK_COMPLETED);
		labelCompleted.setForeground(themeManager.getCurrentTheme()
				.getColorRegistry()
				.get(CommonThemes.COLOR_COMPLETED));

		imageLabel = toolkit.createLabel(activityClient, "");
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.TASK));
		Label labelCompletedToday = toolkit.createLabel(activityClient, "Completed today");
		labelCompletedToday.setFont(CommonFonts.STRIKETHROUGH);
		labelCompletedToday.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(
				CommonThemes.COLOR_COMPLETED_TODAY));

		imageLabel = toolkit.createLabel(activityClient, "");
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_DATE_DUE));
		toolkit.createLabel(activityClient, "Has due date");

		imageLabel = toolkit.createLabel(activityClient, "");
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_DATE_OVERDUE));
		Label textLabel = toolkit.createLabel(activityClient, "Past Due date");
		textLabel.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_OVERDUE));

		imageLabel = toolkit.createLabel(activityClient, "");
		imageLabel.setImage(CommonImages.getImage(CommonImages.BLANK));
		Hyperlink adjust = toolkit.createHyperlink(activityClient, "Adjust Colors and Fonts...", SWT.WRAP);
		adjust.addHyperlinkListener(new IHyperlinkListener() {
			public void linkActivated(HyperlinkEvent e) {
				PreferenceDialog dlg = PreferencesUtil.createPreferenceDialogOn(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow()
						.getShell(), ITasksUiConstants.ID_PREFERENCES_COLORS_AND_FONTS,
						new String[] { ITasksUiConstants.ID_PREFERENCES_COLORS_AND_FONTS }, null);
				dlg.open();
			}

			public void linkEntered(HyperlinkEvent e) {
				// ignore
			}

			public void linkExited(HyperlinkEvent e) {
				// ignore
			}
		});
	}

	private void createContextSection(Composite parent) {
		Section contextSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		contextSection.setText("Task Context");
		contextSection.setLayout(new TableWrapLayout());
		contextSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.verticalSpacing = 1;
		layout.topMargin = 1;
		layout.bottomMargin = 1;

		Composite contextClient = toolkit.createComposite(contextSection);
		contextClient.setLayout(layout);
		contextClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		contextSection.setClient(contextClient);

		Label imageLabel;
		imageLabel = toolkit.createLabel(contextClient, "");
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_FOCUS));
		toolkit.createLabel(contextClient, "Focus view on active task");

		imageLabel = toolkit.createLabel(contextClient, "");
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_INACTIVE_EMPTY));
		toolkit.createLabel(contextClient, "Inactive task with no context");

		imageLabel = toolkit.createLabel(contextClient, "");
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_INACTIVE));
		toolkit.createLabel(contextClient, "Inactive task with context");

		imageLabel = toolkit.createLabel(contextClient, "");
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_ACTIVE));
		toolkit.createLabel(contextClient, "Active task");
	}

	private void createSynchronizationSection(Composite parent) {
		Section synchroSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		synchroSection.setText("Synchronization");
		synchroSection.setLayout(new TableWrapLayout());
		synchroSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.verticalSpacing = 3;
		layout.topMargin = 1;
		layout.bottomMargin = 1;

		Composite synchroClient = toolkit.createComposite(synchroSection);
		synchroClient.setLayout(layout);
		synchroClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		synchroSection.setClient(synchroClient);

		Label imageLabel;
		imageLabel = toolkit.createLabel(synchroClient, "");
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_SYNC_INCOMMING_NEW));
		toolkit.createLabel(synchroClient, "New task, open to view");

		imageLabel = toolkit.createLabel(synchroClient, "");
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_SYNC_INCOMMING));
		toolkit.createLabel(synchroClient, "Incoming changes, open to view");

		imageLabel = toolkit.createLabel(synchroClient, "");
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_SYNC_OUTGOING));
		toolkit.createLabel(synchroClient, "Outgoing changes");

		imageLabel = toolkit.createLabel(synchroClient, "");
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_SYNC_WARNING));
		toolkit.createLabel(synchroClient, "Synchronization failed, mouse over for details");

		imageLabel = toolkit.createLabel(synchroClient, "");
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_SYNC_CONFLICT));
		toolkit.createLabel(synchroClient, "Conflicting changes, need to synchronize");
	}

	@SuppressWarnings("deprecation")
	private void createConnectorsSection(Composite parent) {
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		layout.leftMargin = 0;
		layout.rightMargin = 0;
		layout.topMargin = 0;
		layout.bottomMargin = 0;

		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(layout);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		Collection<AbstractRepositoryConnector> connectors = TasksUi.getRepositoryManager().getRepositoryConnectors();
		for (AbstractRepositoryConnector connector : connectors) {
			AbstractRepositoryConnectorUi connectorUi = TasksUi.getRepositoryConnectorUi(connector.getConnectorKind());
			if (connectorUi != null) {
				List<LegendElement> elements = connectorUi.getLegendElements();
				if (elements != null && elements.size() > 0) {
					legendElements.addAll(elements);
					addLegendElements(composite, connector, elements);
				} else {
					List<ITask> items = connectorUi.getLegendItems();
					if (items != null && !items.isEmpty()) {
						addLegacyLegendItems(composite, connector, items);
					}
				}
			}
		}
	}

	private void addLegendElements(Composite composite, AbstractRepositoryConnector connector,
			List<LegendElement> elements) {
		Section connectorSection = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR);
		connectorSection.setLayout(new TableWrapLayout());
		connectorSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		String label = connector.getLabel();
		int parenIndex = label.indexOf('(');
		if (parenIndex != -1) {
			label = label.substring(0, parenIndex);
		}
		connectorSection.setText(label);

		TableWrapLayout clientLayout = new TableWrapLayout();
		clientLayout.numColumns = 2;
		clientLayout.makeColumnsEqualWidth = false;
		clientLayout.verticalSpacing = 1;
		clientLayout.topMargin = 1;
		clientLayout.bottomMargin = 1;

		Composite connectorClient = toolkit.createComposite(connectorSection);
		connectorClient.setLayout(clientLayout);
		connectorClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		connectorSection.setClient(connectorClient);

		Label imageLabel;
		for (LegendElement element : elements) {
			imageLabel = toolkit.createLabel(connectorClient, "");
			imageLabel.setImage(element.getImage());
			toolkit.createLabel(connectorClient, element.getLabel());
		}

		if (elements.size() < 4) {
			imageLabel = toolkit.createLabel(connectorClient, "");
			toolkit.createLabel(connectorClient, "");
		}
	}

	private void addLegacyLegendItems(Composite composite, AbstractRepositoryConnector connector, List<ITask> elements) {
		Section connectorSection = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR);
		connectorSection.setLayout(new TableWrapLayout());
		connectorSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		String label = connector.getLabel();
		int parenIndex = label.indexOf('(');
		if (parenIndex != -1) {
			label = label.substring(0, parenIndex);
		}
		connectorSection.setText(label);

		TableWrapLayout clientLayout = new TableWrapLayout();
		clientLayout.numColumns = 2;
		clientLayout.makeColumnsEqualWidth = false;
		clientLayout.verticalSpacing = 1;
		clientLayout.topMargin = 1;
		clientLayout.bottomMargin = 1;

		Composite connectorClient = toolkit.createComposite(connectorSection);
		connectorClient.setLayout(clientLayout);
		connectorClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		connectorSection.setClient(connectorClient);

		Label imageLabel;
		for (IRepositoryElement taskListElement : elements) {
			imageLabel = toolkit.createLabel(connectorClient, "");
			imageLabel.setImage(labelProvider.getImage(taskListElement));
			toolkit.createLabel(connectorClient, taskListElement.getSummary());
		}

		if (elements.size() < 4) {
			imageLabel = toolkit.createLabel(connectorClient, "");
			toolkit.createLabel(connectorClient, "");
		}
	}

	private void createGettingStartedSection(Composite parent) {
		TableWrapLayout layout = new TableWrapLayout();
		layout.verticalSpacing = 0;
		layout.leftMargin = 0;
		layout.rightMargin = 0;
		layout.topMargin = 0;
		layout.bottomMargin = 0;

		Composite hyperlinkClient = toolkit.createComposite(parent);
		hyperlinkClient.setLayout(layout);
		hyperlinkClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		Hyperlink gettingStartedLink = toolkit.createHyperlink(hyperlinkClient,
				"Also see the Getting Started documentation online", SWT.WRAP);
		gettingStartedLink.addHyperlinkListener(new IHyperlinkListener() {
			public void linkActivated(HyperlinkEvent e) {
				close();
				TasksUiUtil.openUrl("http://www.eclipse.org/mylyn/start/");
			}

			public void linkEntered(HyperlinkEvent e) {
				// ignore
			}

			public void linkExited(HyperlinkEvent e) {
				// ignore
			}
		});
	}

	private class CloseDialogAction extends Action {

		private CloseDialogAction() {
			setImageDescriptor(CommonImages.NOTIFICATION_CLOSE);
			setText("Close Dialog");
		}

		@Override
		public void run() {
			close();
		}

	}
}
