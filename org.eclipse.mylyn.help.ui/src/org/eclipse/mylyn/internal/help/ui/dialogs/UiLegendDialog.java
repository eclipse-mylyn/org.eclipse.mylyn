/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.help.ui.dialogs;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.themes.ColorsAndFontsPreferencePage;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Mik Kersten
 */
public class UiLegendDialog extends PopupDialog {

	public static final String TITLE = "Mylyn UI Legend";

	private FormToolkit toolkit;

	private ScrolledForm form;

	private TaskElementLabelProvider labelProvider = new TaskElementLabelProvider(false);

	private IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();

	public UiLegendDialog(Shell parent) {
		super(parent, PopupDialog.INFOPOPUP_SHELLSTYLE | SWT.ON_TOP, false, false, false, false, null, null);
	}

	@Override
	protected Control createContents(Composite parent) {
		getShell().setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY));

		return createDialogArea(parent);
	}

	@Override
	public int open() {
		int open = super.open();
		getShell().setFocus();
		return open;
	}

	@Override
	public boolean close() {
		if (toolkit != null) {
			if (toolkit.getColors() != null) {
				toolkit.dispose();
			}
		}
		return super.close();
	}

	@Override
	protected final Control createDialogArea(final Composite parent) {

		getShell().setText(TITLE);

		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		GridLayout formLayout = new GridLayout();
		formLayout.numColumns = 1;
		form.getBody().setLayout(formLayout);
		Label image = null;

		Section section = toolkit.createSection(form.getBody(), Section.DESCRIPTION);
		section.setText(TITLE + "                                                        ");
		Composite sectionClient = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginTop = 0;
		sectionClient.setLayout(layout);

		Composite buttonsComposite = toolkit.createComposite(section);
		GridLayout buttonsLayout = new GridLayout();
		buttonsLayout.verticalSpacing = 0;
		buttonsLayout.verticalSpacing = 0;
		buttonsLayout.marginHeight = 0;
		buttonsLayout.marginWidth = 0;
		buttonsComposite.setLayout(buttonsLayout);
		section.setTextClient(buttonsComposite);
		final ImageHyperlink closeHyperlink = toolkit.createImageHyperlink(buttonsComposite, SWT.NONE);
		closeHyperlink.setLayout(buttonsLayout);
		closeHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.NOTIFICATION_CLOSE));
		closeHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				close();
			}
		});

		// TODO: get rid of ridiculous space-based padding

		Composite tasksComposite = toolkit.createComposite(form.getBody());
		tasksComposite.setLayout(new GridLayout(2, false));

		section = toolkit.createSection(tasksComposite, Section.TITLE_BAR);
		section.setText("Tasks");
		sectionClient = toolkit.createComposite(section);
		setSectionLayout(sectionClient, section, false);

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.TASK));
		toolkit.createLabel(sectionClient, "Task                       ");

// image = toolkit.createLabel(sectionClient, "");
// image.setImage(TasksUiImages.getImage(TasksUiImages.TASK_NOTES));
// toolkit.createLabel(sectionClient, "Task with notes");

// image = toolkit.createLabel(sectionClient, "");
// image.setImage(TasksUiImages.getImage(TasksUiImages.TASK_REPOSITORY));
// toolkit.createLabel(sectionClient, "Repository task");

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.CATEGORY));
		toolkit.createLabel(sectionClient, "Category");

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.QUERY));
		toolkit.createLabel(sectionClient, "Query");

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.CALENDAR));
		toolkit.createLabel(sectionClient, "Date range");

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.BLANK));

		section = toolkit.createSection(tasksComposite, Section.TITLE_BAR);
		section.setText("Priorities                       ");
		sectionClient = toolkit.createComposite(section);
		setSectionLayout(sectionClient, section, false);

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.PRIORITY_1));
		toolkit.createLabel(sectionClient, PriorityLevel.P1.getDescription());

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.PRIORITY_2));
		toolkit.createLabel(sectionClient, PriorityLevel.P2.getDescription());

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.PRIORITY_3));
		toolkit.createLabel(sectionClient, PriorityLevel.P3.getDescription() + " (default)");

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.PRIORITY_4));
		toolkit.createLabel(sectionClient, PriorityLevel.P4.getDescription());

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.PRIORITY_5));
		toolkit.createLabel(sectionClient, PriorityLevel.P5.getDescription());

		section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText("Task Activity                                                                   ");
		sectionClient = toolkit.createComposite(section);
		setSectionLayout(sectionClient, section, false);

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.TASK));
		Label labelOverdue = toolkit.createLabel(sectionClient, "Past scheduled date");
		labelOverdue.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(
				TaskListColorsAndFonts.THEME_COLOR_TASK_OVERDUE));

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.TASK));
		Label labelToday = toolkit.createLabel(sectionClient, "Scheduled for today");
		labelToday.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(
				TaskListColorsAndFonts.THEME_COLOR_TASK_TODAY_SCHEDULED));

// image = toolkit.createLabel(sectionClient, "");
// image.setImage(TasksUiImages.getImage(TasksUiImages.TASK));
// Label labelThisWeek = toolkit.createLabel(sectionClient, "Scheduled for this
// week");
// labelThisWeek.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(
// TaskListColorsAndFonts.THEME_COLOR_TASK_THISWEEK_SCHEDULED));

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.TASK));
		Label labelCompleted = toolkit.createLabel(sectionClient, "Completed");
		labelCompleted.setFont(TaskListColorsAndFonts.STRIKETHROUGH);
// labelCompleted.setForeground(TaskListColorsAndFonts.COLOR_TASK_COMPLETED);
		labelCompleted.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(
				TaskListColorsAndFonts.THEME_COLOR_COMPLETED));

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.TASK));
		Label labelCompletedToday = toolkit.createLabel(sectionClient, "Completed today");
		labelCompletedToday.setFont(TaskListColorsAndFonts.STRIKETHROUGH);
		labelCompletedToday.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(
				TaskListColorsAndFonts.THEME_COLOR_TASK_TODAY_COMPLETED));

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.OVERLAY_HAS_DUE));
		toolkit.createLabel(sectionClient, "Due date set");

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.OVERLAY_OVER_DUE));
		toolkit.createLabel(sectionClient, "Due date past");

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.BLANK));
		Hyperlink adjust = toolkit.createHyperlink(sectionClient, "Adjust Colors and Fonts...", SWT.NULL);
		adjust.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(HyperlinkEvent e) {
				ColorsAndFontsPreferencePage page = new ColorsAndFontsPreferencePage();
				page.init(PlatformUI.getWorkbench());
				TasksUiUtil.showPreferencePage(TasksUiUtil.PREFS_PAGE_ID_COLORS_AND_FONTS, page);
			}

			public void linkEntered(HyperlinkEvent e) {
				// ignore
			}

			public void linkExited(HyperlinkEvent e) {
				// ignore
			}
		});

		section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText("Context                                                                            ");
		sectionClient = toolkit.createComposite(section);
		setSectionLayout(sectionClient, section, false);

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.TASK_INACTIVE));
		toolkit.createLabel(sectionClient, "Inactive task with no context");

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.TASK_INACTIVE_CONTEXT));
		toolkit.createLabel(sectionClient, "Inactive task with context");

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.TASK_ACTIVE));
		toolkit.createLabel(sectionClient, "Active task");

		section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText("Synchronization                                                             ");
		sectionClient = toolkit.createComposite(section);
		setSectionLayout(sectionClient, section, true);

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.OVERLAY_INCOMMING_NEW));
		toolkit.createLabel(sectionClient, "New task, open to view");

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.OVERLAY_INCOMMING));
		toolkit.createLabel(sectionClient, "Incoming changes, open to view");

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.OVERLAY_OUTGOING));
		toolkit.createLabel(sectionClient, "Outgoing changes");

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.OVERLAY_WARNING));
		toolkit.createLabel(sectionClient, "Synchronization failed, mouse over for details");

		image = toolkit.createLabel(sectionClient, "");
		image.setImage(TasksUiImages.getImage(TasksUiImages.OVERLAY_CONFLICT));
		toolkit.createLabel(sectionClient, "Conflicting changes, need to synchronize");

		// Connector specifics
		Composite connectorComposite = toolkit.createComposite(form.getBody());
		connectorComposite.setLayout(new GridLayout(2, false));

		Collection<AbstractRepositoryConnector> connectors = TasksUiPlugin.getRepositoryManager()
				.getRepositoryConnectors();
		for (AbstractRepositoryConnector connector : connectors) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(connector.getConnectorKind());
			if (connectorUi != null) {
				List<AbstractTaskContainer> elements = connectorUi.getLegendItems();

				if (!elements.isEmpty()) {
					section = toolkit.createSection(connectorComposite, Section.TITLE_BAR);
					String label = connector.getLabel();
					int parenIndex = label.indexOf('(');
					if (parenIndex != -1) {
						label = label.substring(0, parenIndex);
					}

					section.setText("Connector: " + label + " ");
					sectionClient = toolkit.createComposite(section);
					setSectionLayout(sectionClient, section, true);

					for (AbstractTaskContainer taskListElement : elements) {
						image = toolkit.createLabel(sectionClient, "");
						image.setImage(labelProvider.getImage(taskListElement));
						toolkit.createLabel(sectionClient, taskListElement.getSummary());
					}
				}
			}
		}

		form.pack();
		return parent;
	}

	private GridLayout setSectionLayout(Composite sectionClient, Section section, boolean extraPadding) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.verticalSpacing = 0;
		if (extraPadding) {
			layout.verticalSpacing = 5;
			layout.horizontalSpacing = 8;
		} else {
			layout.horizontalSpacing = 5;
			layout.verticalSpacing = 1;
		}
		layout.marginHeight = 0;
		layout.marginTop = 0;
		sectionClient.setLayout(layout);
		section.setClient(sectionClient);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalSpan = 2;
		sectionClient.setLayoutData(gridData);

		return layout;
	}
}
