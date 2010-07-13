/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Kevin Barnes, IBM Corporation - fix for bug 277974
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonFonts;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.GradientToolTip;
import org.eclipse.mylyn.internal.provisional.commons.ui.ScalingHyperlink;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TaskScalingHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskDataDiff;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskListNotifier;
import org.eclipse.mylyn.internal.tasks.ui.util.PlatformUtil;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskScheduleContentProvider.StateTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.IFormColors;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;

/**
 * @author Mik Kersten
 * @author Eric Booth
 * @author Leo Dos Santos
 * @author Steffen Pingel
 */
public class TaskListToolTip extends GradientToolTip {

	private final static int MAX_TEXT_WIDTH = 300;

	private final static int MAX_WIDTH = 600;

	private final static int X_SHIFT = PlatformUtil.getToolTipXShift();

	private final static int Y_SHIFT = 1;

	private IRepositoryElement currentTipElement;

	private final List<TaskListToolTipListener> listeners = new ArrayList<TaskListToolTipListener>();

	private boolean visible;

	private boolean triggeredByMouse = true;

	private final Control control;

	private final Color titleColor;

	private boolean enabled;

	public TaskListToolTip(Control control) {
		super(control);
		this.control = control;
		setEnabled(true);
		setShift(new Point(1, 1));
		titleColor = TasksUiPlugin.getDefault().getFormColors(control.getDisplay()).getColor(IFormColors.TITLE);
	}

	public void dispose() {
		hide();
	}

	@Override
	protected void afterHideToolTip(Event event) {
		triggeredByMouse = true;
		visible = false;
		for (TaskListToolTipListener listener : listeners.toArray(new TaskListToolTipListener[0])) {
			listener.toolTipHidden(event);
		}
	}

	public void addTaskListToolTipListener(TaskListToolTipListener listener) {
		listeners.add(listener);
	}

	public void removeTaskListToolTipListener(TaskListToolTipListener listener) {
		listeners.remove(listener);
	}

	private IRepositoryElement getTaskListElement(Object hoverObject) {
		if (hoverObject instanceof ScalingHyperlink) {
			TaskScalingHyperlink hyperlink = (TaskScalingHyperlink) hoverObject;
			return hyperlink.getTask();
		} else if (hoverObject instanceof Widget) {
			Object data = ((Widget) hoverObject).getData();
			if (data != null) {
				if (data instanceof ITaskContainer) {
					return (IRepositoryElement) data;
				} else if (data instanceof IAdaptable) {
					return (IRepositoryElement) ((IAdaptable) data).getAdapter(AbstractTaskContainer.class);
				}
			}
		}
		return null;
	}

	private String getTitleText(IRepositoryElement element) {
		if (element instanceof ScheduledTaskContainer) {
			StringBuilder sb = new StringBuilder();
			sb.append(element.getSummary());
			if (!(element instanceof StateTaskContainer)) {
				Calendar start = ((ScheduledTaskContainer) element).getDateRange().getStartDate();
				sb.append("  ["); //$NON-NLS-1$
				sb.append(DateFormat.getDateInstance(DateFormat.LONG).format(start.getTime()));
				sb.append("]"); //$NON-NLS-1$
			}
			return sb.toString();
		} else if (element instanceof IRepositoryQuery) {
			IRepositoryQuery query = (IRepositoryQuery) element;
			StringBuilder sb = new StringBuilder();
			sb.append(element.getSummary());
			sb.append("  ["); //$NON-NLS-1$
			sb.append(getRepositoryLabel(query.getConnectorKind(), query.getRepositoryUrl()));
			sb.append("]"); //$NON-NLS-1$
			return sb.toString();
		} else if (element instanceof ITask) {
			return ((ITask) element).getSummary();
		} else {
			return new TaskElementLabelProvider(false).getText(element);
		}
	}

	private String getDetailsText(IRepositoryElement element) {
		if (element instanceof ScheduledTaskContainer) {
			ScheduledTaskContainer container = (ScheduledTaskContainer) element;
			int estimateTotal = 0;
			long activeTotal = 0;
			for (ITask child : container.getChildren()) {
				if (child instanceof AbstractTask) {
					estimateTotal += ((AbstractTask) child).getEstimatedTimeHours();
					activeTotal += TasksUiPlugin.getTaskActivityManager().getElapsedTime(child,
							container.getDateRange());
				}
			}
			StringBuilder sb = new StringBuilder();
			appendEstimateAndActive(sb, estimateTotal, activeTotal);
			return sb.toString();
		} else if (element instanceof ITask) {
			ITask task = (ITask) element;
			StringBuilder sb = new StringBuilder();
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(task.getConnectorKind());
			String kindLabel = null;
			if (connectorUi != null) {
				kindLabel = connectorUi.getTaskKindLabel(task);
				sb.append(kindLabel);
			}
			String key = task.getTaskKey();
			if (key != null) {
				sb.append(" "); //$NON-NLS-1$
				sb.append(key);
			}
			String taskKind = task.getTaskKind();
			if (taskKind != null && taskKind.length() > 0 && !taskKind.equals(kindLabel)) {
				sb.append(" ("); //$NON-NLS-1$
				sb.append(taskKind);
				sb.append(") "); //$NON-NLS-1$
			}
			sb.append(", "); //$NON-NLS-1$
			sb.append(task.getPriority());
			sb.append("  ["); //$NON-NLS-1$
			sb.append(getRepositoryLabel(task.getConnectorKind(), task.getRepositoryUrl()));
			sb.append("]"); //$NON-NLS-1$
			sb.append("\n"); //$NON-NLS-1$

			String owner = ((ITask) element).getOwner();
			if (owner != null) {
				sb.append(NLS.bind(Messages.TaskListToolTip_Assigned_to_X, owner));
				sb.append("\n"); //$NON-NLS-1$
			}

			String extendedToolTipInfo = task.getAttribute(ITasksCoreConstants.ATTRIBUTE_TASK_EXTENDED_TOOLTIP);
			if (extendedToolTipInfo != null && extendedToolTipInfo.length() > 0) {
				sb.append(extendedToolTipInfo);
				sb.append("\n"); //$NON-NLS-1$
			}
			return sb.toString();
		} else {
			return null;
		}
	}

	private void appendEstimateAndActive(StringBuilder sb, int estimateTotal, long activeTotal) {
		sb.append(NLS.bind(Messages.TaskListToolTip_Estimate, estimateTotal));
		sb.append("\n"); //$NON-NLS-1$
		if (MonitorUiPlugin.getDefault().isActivityTrackingEnabled()) {
			sb.append(NLS.bind(Messages.TaskListToolTip_Active_X, DateUtil.getFormattedDurationShort(activeTotal)));
			sb.append("\n"); //$NON-NLS-1$
		}
	}

	private String getRepositoryLabel(String repositoryKind, String repositoryUrl) {
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(repositoryKind, repositoryUrl);
		if (repository != null) {
			String label = repository.getRepositoryLabel();
			if (label.indexOf("//") != -1) { //$NON-NLS-1$
				return label.substring((repository.getRepositoryUrl().indexOf("//") + 2)); //$NON-NLS-1$
			}
			return label;
		}
		return ""; //$NON-NLS-1$
	}

	private String getActivityText(IRepositoryElement element) {
		if (element instanceof ITask) {
			AbstractTask task = (AbstractTask) element;

			StringBuilder sb = new StringBuilder();

			Date dueDate = task.getDueDate();
			if (dueDate != null) {
				sb.append(NLS.bind(Messages.TaskListToolTip_Due,
						new Object[] {
								new SimpleDateFormat("E").format(dueDate), //$NON-NLS-1$
								DateFormat.getDateInstance(DateFormat.LONG).format(dueDate),
								DateFormat.getTimeInstance(DateFormat.SHORT).format(dueDate) }));
				sb.append("\n"); //$NON-NLS-1$
			}

			DateRange scheduledDate = task.getScheduledForDate();
			if (scheduledDate != null) {
				sb.append(NLS.bind(Messages.TaskListToolTip_Scheduled, scheduledDate.toString()));
				sb.append("\n"); //$NON-NLS-1$
			}
			long elapsed = TasksUiPlugin.getTaskActivityManager().getElapsedTime(task);
			appendEstimateAndActive(sb, (task).getEstimatedTimeHours(), elapsed);
			return sb.toString();
		}
		return null;
	}

	private String getIncommingText(IRepositoryElement element) {
		if (element instanceof ITask) {
			ITask task = (ITask) element;
			if (task.getSynchronizationState().isIncoming()) {
				String text = null;
				TaskListNotifier notifier = new TaskListNotifier(TasksUiPlugin.getRepositoryModel(),
						TasksUiPlugin.getTaskDataManager());
				TaskDataDiff diff = notifier.getDiff(task);
				if (diff != null) {
					text = diff.toString(MAX_TEXT_WIDTH, false);
				}
				if (text != null && text.length() > 0) {
					return text;
				}
			}
		}
		return null;
	}

	private String getStatusText(IRepositoryElement element) {
		IStatus status = null;
		if (element instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) element;
			status = task.getStatus();
		} else if (element instanceof IRepositoryQuery) {
			RepositoryQuery query = (RepositoryQuery) element;
			status = query.getStatus();
		}

		if (status != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(status.getMessage());
			if (status instanceof RepositoryStatus && ((RepositoryStatus) status).isHtmlMessage()) {
				sb.append(Messages.TaskListToolTip_Please_synchronize_manually_for_full_error_message);
			}
			return sb.toString();
		}

		return null;
	}

	@Override
	public Point getLocation(Point tipSize, Event event) {
		Widget widget = getTipWidget(event);
		if (widget != null) {
			Rectangle bounds = getBounds(widget);
			if (bounds != null) {
				return control.toDisplay(bounds.x + X_SHIFT, bounds.y + bounds.height + Y_SHIFT);
			}
		}
		return super.getLocation(tipSize, event);//control.toDisplay(event.x + xShift, event.y + yShift);
	}

	private ProgressData getProgressData(IRepositoryElement element) {
		if (element instanceof ITaskContainer) {
			Object[] children = new Object[0];

			children = ((ITaskContainer) element).getChildren().toArray();

			int total = children.length;
			if (total > 0) {
				int completed = 0;
				for (ITask task : ((ITaskContainer) element).getChildren()) {
					if (task.isCompleted()) {
						completed++;
					}
				}
				String text = NLS.bind(Messages.TaskListToolTip_Total_Complete_Incomplete, new Object[] { //
						total, completed, (total - completed) });
				return new ProgressData(completed, total, text);
			}
		}
		return null;
	}

	private Image getImage(IRepositoryElement element) {
		if (element instanceof IRepositoryQuery) {
			IRepositoryQuery query = (IRepositoryQuery) element;
			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
					query.getConnectorKind());
			if (connector != null) {
				return TasksUiPlugin.getDefault().getBrandingIcon(connector.getConnectorKind());
			}
		} else if (element instanceof ITask) {
			ITask repositoryTask = (ITask) element;
			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
					repositoryTask.getConnectorKind());
			if (connector != null) {
				return TasksUiPlugin.getDefault().getBrandingIcon(connector.getConnectorKind());
			}
		} else if (element instanceof ScheduledTaskContainer) {
			return CommonImages.getImage(CommonImages.CALENDAR);
		}
		return null;
	}

	protected Widget getTipWidget(Event event) {
		Point widgetPosition = new Point(event.x, event.y);
		Widget widget = event.widget;
		if (widget instanceof ToolBar) {
			ToolBar w = (ToolBar) widget;
			return w.getItem(widgetPosition);
		}
		if (widget instanceof Table) {
			Table w = (Table) widget;
			return w.getItem(widgetPosition);
		}
		if (widget instanceof Tree) {
			Tree w = (Tree) widget;
			return w.getItem(widgetPosition);
		}

		return widget;
	}

	private Rectangle getBounds(Widget widget) {
		if (widget instanceof ToolItem) {
			ToolItem w = (ToolItem) widget;
			return w.getBounds();
		}
		if (widget instanceof TableItem) {
			TableItem w = (TableItem) widget;
			return w.getBounds();
		}
		if (widget instanceof TreeItem) {
			TreeItem w = (TreeItem) widget;
			return w.getBounds();
		}
		return null;
	}

	@Override
	protected boolean shouldCreateToolTip(Event event) {
		currentTipElement = null;

		if (isTriggeredByMouse() && !enabled) {
			return false;
		}

		if (super.shouldCreateToolTip(event)) {
			Widget tipWidget = getTipWidget(event);
			if (tipWidget != null) {
				Rectangle bounds = getBounds(tipWidget);
				if (tipWidget instanceof ScalingHyperlink) {
					currentTipElement = getTaskListElement(tipWidget);
				} else if (bounds != null && contains(bounds.x, bounds.y)) {
					currentTipElement = getTaskListElement(tipWidget);
				}
			}
		}

		if (currentTipElement == null) {
			hide();
			return false;
		} else {
			return true;
		}
	}

	private boolean contains(int x, int y) {
		if (control instanceof Scrollable) {
			return ((Scrollable) control).getClientArea().contains(x, y);
		} else {
			return control.getBounds().contains(x, y);
		}
	}

	@Override
	protected Composite createToolTipArea(Event event, Composite parent) {
		assert currentTipElement != null;

		Composite composite = createToolTipContentAreaComposite(parent);

		addIconAndLabel(composite, getImage(currentTipElement), getTitleText(currentTipElement), true);

		String detailsText = getDetailsText(currentTipElement);
		if (detailsText != null) {
			addIconAndLabel(composite, null, detailsText);
		}

		String synchText = getSynchText(currentTipElement);
		if (synchText != null) {
			addIconAndLabel(composite, CommonImages.getImage(TasksUiImages.REPOSITORY_SYNCHRONIZE), synchText);
		}

		String activityText = getActivityText(currentTipElement);
		if (activityText != null) {
			addIconAndLabel(composite, CommonImages.getImage(CommonImages.CALENDAR), activityText);
		}

		String incommingText = getIncommingText(currentTipElement);
		if (incommingText != null) {
			Image image = CommonImages.getImage(CommonImages.OVERLAY_SYNC_INCOMMING);
			if (currentTipElement instanceof ITask) {
				ITask task = (ITask) currentTipElement;
				if (task.getSynchronizationState() == SynchronizationState.INCOMING_NEW) {
					image = CommonImages.getImage(CommonImages.OVERLAY_SYNC_INCOMMING_NEW);
				} else if (task.getSynchronizationState() == SynchronizationState.OUTGOING_NEW) {
					image = CommonImages.getImage(CommonImages.OVERLAY_SYNC_OUTGOING_NEW);
				}
			}
			addIconAndLabel(composite, image, incommingText);
		}

		ProgressData progress = getProgressData(currentTipElement);
		if (progress != null) {
			addIconAndLabel(composite, null, progress.text);

			// label height need to be set to 0 to remove gap below the progress bar 
			Label label = new Label(composite, SWT.NONE);
			GridData labelGridData = new GridData(SWT.FILL, SWT.TOP, true, false);
			labelGridData.heightHint = 0;
			label.setLayoutData(labelGridData);

			Composite progressComposite = new Composite(composite, SWT.NONE);
			GridLayout progressLayout = new GridLayout(1, false);
			progressLayout.marginWidth = 0;
			progressLayout.marginHeight = 0;
			progressComposite.setLayout(progressLayout);
			progressComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

			WorkweekProgressBar taskProgressBar = new WorkweekProgressBar(progressComposite);
			taskProgressBar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			taskProgressBar.reset(progress.completed, progress.total);

			// do we really need custom canvas? code below renders the same
//			IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
//			Color color = themeManager.getCurrentTheme().getColorRegistry().get(
//					TaskListColorsAndFonts.THEME_COLOR_TASK_TODAY_COMPLETED);
//			ProgressBar bar = new ProgressBar(tipShell, SWT.SMOOTH);
//			bar.setForeground(color);
//			bar.setSelection((int) (100d * progress.completed / progress.total));
//			GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
//			gridData.heightHint = 5;
//			bar.setLayoutData(gridData);
		}

		String statusText = getStatusText(currentTipElement);
		if (statusText != null) {
			addIconAndLabel(composite, CommonImages.getImage(CommonImages.WARNING), statusText);
		}

		String helpText = getHelpText(currentTipElement);
		if (helpText != null) {
			addIconAndLabel(composite, CommonImages.getImage(CommonImages.QUESTION), helpText);
		}

		visible = true;

		return composite;
	}

	private String getHelpText(IRepositoryElement element) {
		if (element instanceof TaskCategory || element instanceof IRepositoryQuery) {
			if (AbstractTaskListFilter.hasDescendantIncoming((ITaskContainer) element)) {
				TaskListView taskListView = TaskListView.getFromActivePerspective();
				if (taskListView != null) {

					if (!taskListView.isFocusedMode()
							&& TasksUiPlugin.getDefault()
									.getPreferenceStore()
									.getBoolean(ITasksUiPreferenceConstants.FILTER_COMPLETE_MODE)) {
						Object[] children = ((TaskListContentProvider) taskListView.getViewer().getContentProvider()).getChildren(element);
						boolean hasIncoming = false;
						for (Object child : children) {
							if (child instanceof ITask) {
								if (((ITask) child).getSynchronizationState().isIncoming()) {
									hasIncoming = true;
									break;
								}
							}
						}
						if (!hasIncoming) {
							return Messages.TaskListToolTip_Some_incoming_elements_may_be_filtered;
						}
					}
				}
			}
			// if has incoming but no top level children have incoming, suggest incoming tasks may be filtered
		}
		if (element instanceof UncategorizedTaskContainer) {
			return Messages.TaskListToolTip_Automatic_container_for_all_local_tasks;
		} else if (element instanceof UnmatchedTaskContainer) {
			return Messages.TaskListToolTip_Automatic_container_for_repository_tasks;
		}
		return null;
	}

	protected Composite createToolTipContentAreaComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 2;
		composite.setLayout(gridLayout);
		composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		return composite;
	}

	private String getSynchText(IRepositoryElement element) {
		if (element instanceof IRepositoryQuery) {
			String syncStamp = ((RepositoryQuery) element).getLastSynchronizedTimeStamp();
			if (syncStamp != null) {
				return NLS.bind(Messages.TaskListToolTip_Synchronized, syncStamp);
			}
		}
		return null;
	}

	private String removeTrailingNewline(String text) {
		if (text.endsWith("\n")) { //$NON-NLS-1$
			return text.substring(0, text.length() - 1);
		}
		return text;
	}

	protected void addIconAndLabel(Composite parent, Image image, String text) {
		addIconAndLabel(parent, image, text, false);
	}

	protected void addIconAndLabel(Composite parent, Image image, String text, boolean title) {
		Label imageLabel = new Label(parent, SWT.NONE);
		imageLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		imageLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		imageLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING));
		imageLabel.setImage(image);

		Label textLabel = new Label(parent, SWT.WRAP);
		if (title) {
			textLabel.setFont(CommonFonts.BOLD);
		}
		textLabel.setForeground(titleColor);
		textLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		textLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));
		text = removeTrailingNewline(text);
		textLabel.setText(CommonUiUtil.toLabel(text));
		int width = Math.min(textLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, MAX_WIDTH);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).hint(width, SWT.DEFAULT).applyTo(textLabel);
	}

	private static class ProgressData {

		int completed;

		int total;

		String text;

		public ProgressData(int completed, int total, String text) {
			this.completed = completed;
			this.total = total;
			this.text = text;
		}

	}

	public static interface TaskListToolTipListener {

		void toolTipHidden(Event event);

	}

	public boolean isVisible() {
		return visible;
	}

	public boolean isTriggeredByMouse() {
		return triggeredByMouse;
	}

	@Override
	public void show(Point location) {
		triggeredByMouse = false;
		super.show(location);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
