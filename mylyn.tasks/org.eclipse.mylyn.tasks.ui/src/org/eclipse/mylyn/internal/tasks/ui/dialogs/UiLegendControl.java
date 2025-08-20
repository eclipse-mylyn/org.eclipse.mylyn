/*******************************************************************************
 * Copyright (c) 2004, 2016 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.compatibility.CommonFonts;
import org.eclipse.mylyn.commons.ui.compatibility.CommonThemes;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.ui.LegendElement;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
public class UiLegendControl extends Composite {

	private final FormToolkit toolkit;

	private Window window = null;

	private final TaskElementLabelProvider labelProvider = new TaskElementLabelProvider(false);

	private final IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();

	private final ArrayList<LegendElement> legendElements = new ArrayList<>();

	public UiLegendControl(Composite parent, FormToolkit toolkit) {
		this(parent, toolkit, true, SWT.VERTICAL);
	}

	public UiLegendControl(Composite parent, FormToolkit toolkit, boolean showConnectors, int style) {
		super(parent, SWT.NONE);
		this.toolkit = toolkit;
		toolkit.adapt(this);

		addDisposeListener(e -> doDispose());

		TableWrapLayout layout = new TableWrapLayout();
		layout.leftMargin = 0;
		layout.rightMargin = 0;
		layout.topMargin = 0;
		layout.bottomMargin = 0;

		if (style == SWT.DEFAULT) {
			createContentsVertical(layout, showConnectors);
		} else if ((style & SWT.HORIZONTAL) != 0) {
			createContentsHorizontal(layout, showConnectors);
		} else {
			createContentsVertical(layout, showConnectors);
		}

		setLayout(layout);
		setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
	}

	private void doDispose() {
		for (LegendElement element : legendElements) {
			element.dispose();
		}
		if (labelProvider != null) {
			labelProvider.dispose();
		}
	}

	public void setWindow(Window window) {
		this.window = window;
	}

	public boolean close() {
		if (window != null) {
			return window.close();
		} else {
			return false;
		}
	}

	private void createContentsHorizontal(TableWrapLayout layout, boolean showConnectors) {
		layout.numColumns = 2;
		createTasksPrioritiesSection(this);
		createContextSection(this);
		createActivitySection(this);
		createSynchronizationSection(this);

		Composite subComp = toolkit.createComposite(this);
		TableWrapLayout subLayout = new TableWrapLayout();
		subLayout.topMargin = 0;
		subLayout.bottomMargin = 0;
		subLayout.leftMargin = 0;
		subLayout.rightMargin = 0;
		subComp.setLayout(subLayout);
		subComp.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB, 1, 2));

		if (showConnectors) {
			createConnectorsSection(subComp);
		}
		createGettingStartedSection(subComp);
	}

	private void createContentsVertical(TableWrapLayout layout, boolean showConnectors) {
		layout.numColumns = 1;
		createTasksPrioritiesSection(this);
		createActivitySection(this);
		createContextSection(this);
		createSynchronizationSection(this);
		if (showConnectors) {
			createConnectorsSection(this);
		}
		createGettingStartedSection(this);
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
		tasksSection.setText(Messages.UiLegendControl_Tasks);
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
		imageLabel = toolkit.createLabel(tasksClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.TASK));
		toolkit.createLabel(tasksClient, Messages.UiLegendControl_Task);

		imageLabel = toolkit.createLabel(tasksClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.TASK_OWNED));
		toolkit.createLabel(tasksClient, Messages.UiLegendControl_Task_Owned);

		imageLabel = toolkit.createLabel(tasksClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.CATEGORY));
		toolkit.createLabel(tasksClient, Messages.UiLegendControl_Category);

		imageLabel = toolkit.createLabel(tasksClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.QUERY));
		toolkit.createLabel(tasksClient, Messages.UiLegendControl_Query);

		imageLabel = toolkit.createLabel(tasksClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.CALENDAR));
		toolkit.createLabel(tasksClient, Messages.UiLegendControl_Date_range);

		imageLabel = toolkit.createLabel(tasksClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.BLANK));

		Hyperlink openView = toolkit.createHyperlink(tasksClient, Messages.UiLegendControl_Open_Task_List_, SWT.WRAP);
		openView.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				close();
				TasksUiUtil.openTasksViewInActivePerspective();
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				// ignore
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				// ignore
			}
		});
	}

	private void createPrioritiesSection(Composite parent) {
		Section prioritiesSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		prioritiesSection.setText(Messages.UiLegendControl_Priorities);
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
		imageLabel = toolkit.createLabel(prioritiesClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.PRIORITY_1));
		toolkit.createLabel(prioritiesClient, PriorityLevel.P1.getDescription());

		imageLabel = toolkit.createLabel(prioritiesClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.PRIORITY_2));
		toolkit.createLabel(prioritiesClient, PriorityLevel.P2.getDescription());

		imageLabel = toolkit.createLabel(prioritiesClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.PRIORITY_3));
		toolkit.createLabel(prioritiesClient, PriorityLevel.P3.getDescription() + Messages.UiLegendControl__default_);

		imageLabel = toolkit.createLabel(prioritiesClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.PRIORITY_4));
		toolkit.createLabel(prioritiesClient, PriorityLevel.P4.getDescription());

		imageLabel = toolkit.createLabel(prioritiesClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.PRIORITY_5));
		toolkit.createLabel(prioritiesClient, PriorityLevel.P5.getDescription());
	}

	private void createActivitySection(Composite parent) {
		Section activitySection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		activitySection.setText(Messages.UiLegendControl_Task_Activity);
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

		Label imageLabel = toolkit.createLabel(activityClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.TASK));
		Label labelToday = toolkit.createLabel(activityClient, Messages.UiLegendControl_Scheduled_for_today);
		labelToday.setForeground(
				themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_SCHEDULED_TODAY));

		imageLabel = toolkit.createLabel(activityClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.TASK));
		Label labelOverdue = toolkit.createLabel(activityClient, Messages.UiLegendControl_Past_scheduled_date);
		labelOverdue.setForeground(
				themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_SCHEDULED_PAST));

// imageLabel = toolkit.createLabel(activityClient, "");
// imageLabel.setImage(TasksUiImages.getImage(TasksUiImages.TASK));
// Label labelThisWeek = toolkit.createLabel(activityClient, "Scheduled for this
// week");
// labelThisWeek.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(
// TaskListColorsAndFonts.THEME_COLOR_TASK_THISWEEK_SCHEDULED));

		imageLabel = toolkit.createLabel(activityClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.TASK));
		Label labelCompleted = toolkit.createLabel(activityClient, Messages.UiLegendControl_Completed);
		labelCompleted.setFont(CommonFonts.STRIKETHROUGH);
// labelCompleted.setForeground(TaskListColorsAndFonts.COLOR_TASK_COMPLETED);
		labelCompleted
				.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_COMPLETED));

		imageLabel = toolkit.createLabel(activityClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.TASK));
		Label labelCompletedToday = toolkit.createLabel(activityClient, Messages.UiLegendControl_Completed_today);
		labelCompletedToday.setFont(CommonFonts.STRIKETHROUGH);
		labelCompletedToday.setForeground(
				themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_COMPLETED_TODAY));

		imageLabel = toolkit.createLabel(activityClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_DATE_DUE));
		toolkit.createLabel(activityClient, Messages.UiLegendControl_Has_Due_date);

		imageLabel = toolkit.createLabel(activityClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_DATE_OVERDUE));
		Label textLabel = toolkit.createLabel(activityClient, Messages.UiLegendControl_Past_due_date);
		textLabel.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_OVERDUE));

		imageLabel = toolkit.createLabel(activityClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.NOTES));
		toolkit.createLabel(activityClient, Messages.UiLegendControl_Notes);

		imageLabel = toolkit.createLabel(activityClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.BLANK));
		Hyperlink adjust = toolkit.createHyperlink(activityClient, Messages.UiLegendControl_Adjust_Colors_and_Fonts_,
				SWT.WRAP);
		adjust.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				PreferenceDialog dlg = PreferencesUtil.createPreferenceDialogOn(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						ITasksUiConstants.ID_PREFERENCES_COLORS_AND_FONTS,
						new String[] { ITasksUiConstants.ID_PREFERENCES_COLORS_AND_FONTS }, null);
				dlg.open();
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				// ignore
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				// ignore
			}
		});
	}

	private void createContextSection(Composite parent) {
		Section contextSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		contextSection.setText(Messages.UiLegendControl_Task_Context);
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
		imageLabel = toolkit.createLabel(contextClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_FOCUS));
		toolkit.createLabel(contextClient, Messages.UiLegendControl_Focus_view_on_active_task);

		imageLabel = toolkit.createLabel(contextClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_INACTIVE_EMPTY));
		toolkit.createLabel(contextClient, Messages.UiLegendControl_Inactive_task_with_no_context);

		imageLabel = toolkit.createLabel(contextClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_INACTIVE));
		toolkit.createLabel(contextClient, Messages.UiLegendControl_Inactive_task_with_context);

		imageLabel = toolkit.createLabel(contextClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(TasksUiImages.CONTEXT_ACTIVE));
		toolkit.createLabel(contextClient, Messages.UiLegendControl_Active_task);
	}

	private void createSynchronizationSection(Composite parent) {
		Section synchroSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		synchroSection.setText(Messages.UiLegendControl_Synchronization);
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
		imageLabel = toolkit.createLabel(synchroClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_SYNC_INCOMMING_NEW));
		toolkit.createLabel(synchroClient, Messages.UiLegendControl_New_task);

		imageLabel = toolkit.createLabel(synchroClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_SYNC_INCOMMING));
		toolkit.createLabel(synchroClient, Messages.UiLegendControl_Incoming_changes);

		imageLabel = toolkit.createLabel(synchroClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_SYNC_OUTGOING));
		toolkit.createLabel(synchroClient, Messages.UiLegendControl_Outgoing_changes);

		imageLabel = toolkit.createLabel(synchroClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_SYNC_OUTGOING_NEW));
		toolkit.createLabel(synchroClient, Messages.UiLegendControl_Unsubmitted_outgoing_changes);

		imageLabel = toolkit.createLabel(synchroClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_SYNC_WARNING));
		toolkit.createLabel(synchroClient, Messages.UiLegendControl_Synchronization_failed);

		imageLabel = toolkit.createLabel(synchroClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_SYNC_ERROR));
		toolkit.createLabel(synchroClient, Messages.UiLegendControl_Synchronization_error);

		imageLabel = toolkit.createLabel(synchroClient, ""); //$NON-NLS-1$
		imageLabel.setImage(CommonImages.getImage(CommonImages.OVERLAY_SYNC_CONFLICT));
		toolkit.createLabel(synchroClient, Messages.UiLegendControl_Conflicting_changes);
	}

	private void createConnectorsSection(Composite parent) {
		TableWrapLayout layout = new TableWrapLayout();
		layout.makeColumnsEqualWidth = true;
		layout.leftMargin = 0;
		layout.rightMargin = 0;
		layout.topMargin = 0;
		layout.bottomMargin = 0;

		ScrolledForm composite = toolkit.createScrolledForm(parent);
		composite.getBody().setLayout(layout);
		TableWrapData data = new TableWrapData(TableWrapData.FILL);
		composite.setLayoutData(data);

		List<AbstractRepositoryConnector> connectors = new ArrayList<>(
				TasksUi.getRepositoryManager().getRepositoryConnectors());
		Collections.sort(connectors, (o1, o2) -> o1.getLabel().compareToIgnoreCase(o2.getLabel()));
		for (AbstractRepositoryConnector connector : connectors) {
			if (TasksUi.getRepositoryManager().getRepositories(connector.getConnectorKind()).isEmpty()) {
				continue;
			}
			AbstractRepositoryConnectorUi connectorUi = TasksUi.getRepositoryConnectorUi(connector.getConnectorKind());
			if (connectorUi != null) {
				List<LegendElement> elements = connectorUi.getLegendElements();
				if (elements != null && elements.size() > 0) {
					legendElements.addAll(elements);
					addLegendElements(composite.getBody(), connector, elements);
				}
			}
		}

		layout.numColumns = Math.max(composite.getBody().getChildren().length, 1);

		// show 3 columns by default
		Point w = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		data.maxWidth = w.x / layout.numColumns * 3;
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
			imageLabel = toolkit.createLabel(connectorClient, ""); //$NON-NLS-1$
			imageLabel.setImage(element.getImage());
			toolkit.createLabel(connectorClient, element.getLabel());
		}

		if (elements.size() < 4) {
			imageLabel = toolkit.createLabel(connectorClient, ""); //$NON-NLS-1$
			toolkit.createLabel(connectorClient, ""); //$NON-NLS-1$
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
				Messages.UiLegendControl_Also_see_the_Getting_Started_documentation_online, SWT.WRAP);
		gettingStartedLink.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				close();
				TasksUiUtil.openUrl(Messages.UiLegendControl_http_www_eclipse_org_mylyn_start);
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				// ignore
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				// ignore
			}
		});
	}

}
