/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/**
 * Copied from newsgroup, forwarded from Make Technologies
 */

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.ITaskListNotification;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.monitor.core.DateUtil;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Eric Booth
 * @author Leo Dos Santos - multi-monitor support
 * @author Steffen Pingel
 */
public class TaskListToolTipHandler {

	private Shell tipShell;

	private Widget tipWidget;

	public TaskListToolTipHandler() {
	}

	private AbstractTaskContainer getTaskListElement(Object hoverObject) {
		if (hoverObject instanceof Widget) {
			Object data = ((Widget) hoverObject).getData();
			if (data != null) {
				if (data instanceof AbstractTaskContainer) {
					return (AbstractTaskContainer) data;
				} else if (data instanceof IAdaptable) {
					return (AbstractTaskContainer) ((IAdaptable) data).getAdapter(AbstractTaskContainer.class);
				}
			}
		}
		return null;
	}

	private String getTitleText(AbstractTaskContainer element) {
		if (element instanceof ScheduledTaskContainer) {
			StringBuilder sb = new StringBuilder();
			sb.append(element.getSummary());
			Calendar start = ((ScheduledTaskContainer) element).getStart();
			sb.append("  [");
			sb.append(DateFormat.getDateInstance(DateFormat.LONG).format(start.getTime()));
			sb.append("]");
			return sb.toString();
		} else if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
			StringBuilder sb = new StringBuilder();
			sb.append(element.getSummary());
			sb.append("  [");
			sb.append(getRepositoryLabel(query.getRepositoryKind(), query.getRepositoryUrl()));
			sb.append("]");
			return sb.toString();
		} else {
			return element.getSummary();
		}
	}

	private String getDetailsText(AbstractTaskContainer element) {
		if (element instanceof ScheduledTaskContainer) {
			ScheduledTaskContainer container = (ScheduledTaskContainer) element;
			StringBuilder sb = new StringBuilder();
			sb.append("Estimate: ");
			sb.append(container.getTotalEstimated());
			sb.append(" hours");
			sb.append("\n");
			sb.append("Elapsed: ");
			sb.append(DateUtil.getFormattedDurationShort(container.getTotalElapsed()));
			sb.append("\n");
			return sb.toString();
		} else if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
			StringBuilder sb = new StringBuilder();
			String syncStamp = query.getLastSynchronizedTimeStamp();
			if (syncStamp != null) {
				sb.append("Synchronized: " + syncStamp);
			}
			return sb.toString();
		} else if (element instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) element;
			StringBuilder sb = new StringBuilder();			
			sb.append(TasksUiPlugin.getConnectorUi(task.getConnectorKind()).getTaskKindLabel(task));
			String key = task.getTaskKey();
			if (key != null) {
				sb.append(" ");
				sb.append(key);
			}
			sb.append(", ");
			sb.append(task.getPriority());
			sb.append("  [");
			sb.append(getRepositoryLabel(task.getConnectorKind(), task.getRepositoryUrl()));
			sb.append("]");
			sb.append("\n");
			return sb.toString();
		}
		return null;
	}

	private String getRepositoryLabel(String repositoryKind, String repositoryUrl) {
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(repositoryKind, repositoryUrl);
		if (repository != null) {
			String label = repository.getRepositoryLabel();
			if (label.indexOf("//") != -1) {
				return label.substring((repository.getUrl().indexOf("//") + 2));
			}
			return label + "";
		}
		return "";
	}

	private String getActivityText(AbstractTaskContainer element) {
		if (element instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) element;

			StringBuilder sb = new StringBuilder();
			Date date = task.getScheduledForDate();
			if (date != null) {
				sb.append("Scheduled for: ");
				sb.append(DateFormat.getDateInstance(DateFormat.LONG).format(date));
				sb.append(" (");
				sb.append(DateFormat.getTimeInstance(DateFormat.SHORT).format(date));
				sb.append(")\n");
			}

			long elapsed = TasksUiPlugin.getTaskListManager().getElapsedTime(task);
			String elapsedTimeString = DateUtil.getFormattedDurationShort(elapsed);
			sb.append("Elapsed: ");
			sb.append(elapsedTimeString);
			sb.append("\n");

			return sb.toString();
		}
		return null;
	}

	private String getIncommingText(AbstractTaskContainer element) {
		if (element instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) element;
			if (task.getSynchronizationState() == RepositoryTaskSyncState.INCOMING) {
				AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
						task);
				if (connector != null) {
					ITaskListNotification notification = TasksUiPlugin.getIncommingNotification(connector, task);
					if (notification != null) {
						if (notification.getDescription() != null) {
							String descriptionText = notification.getDescription();
							if (descriptionText != null && descriptionText.length() > 0) {
								return descriptionText;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private String getStatusText(AbstractTaskContainer element) {
		IStatus status = null;
		if (element instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) element;
			status = task.getSynchronizationStatus();
		} else if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
			status = query.getSynchronizationStatus();
		}
		
		if (status != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(status.getMessage());
			if (status instanceof RepositoryStatus && ((RepositoryStatus) status).isHtmlMessage()) {
				sb.append(" Please synchronize manually for full error message.");
			}
			return sb.toString();
		} 
		
		return null;
	}

	private ProgressData getProgressData(AbstractTaskContainer element) {
		if (element instanceof AbstractTask) {
			return null;
		}

		int total = element.getChildren().size();
		int completed = 0;
		for (AbstractTask task : element.getChildren()) {
			if (task.isCompleted()) {
				completed++;
			}
		}

		String text = "Completed " + completed + " of " + total;
		return new ProgressData(completed, total, text);
	}

	private Image getImage(AbstractTaskContainer element) {
		if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					query.getRepositoryKind());
			if (connector != null) {
				return TasksUiPlugin.getDefault().getBrandingIcon(connector.getConnectorKind());
			}
		} else if (element instanceof AbstractTask) {
			AbstractTask repositoryTask = (AbstractTask) element;
			if (repositoryTask != null) {
				AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
						repositoryTask.getConnectorKind());
				if (connector != null) {
					return TasksUiPlugin.getDefault().getBrandingIcon(connector.getConnectorKind());
				}
			}
		} else if (element instanceof ScheduledTaskContainer) {
			return TasksUiImages.getImage(TasksUiImages.CALENDAR);
		}
		return null;
	}

	/**
	 * Enables customized hover help for a specified control
	 * 
	 * @control the control on which to enable hoverhelp
	 */
	public void activateHoverHelp(final Control control) {
		// hide tooltip if any window is deactivated 
		PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {

			public void windowActivated(IWorkbenchWindow window) {
			}

			public void windowClosed(IWorkbenchWindow window) {
			}

			public void windowDeactivated(IWorkbenchWindow window) {
				hideTooltip();
			}

			public void windowOpened(IWorkbenchWindow window) {
			}
		});

		// hide tooltip if control underneath is activated 
		control.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				hideTooltip();
				tipWidget = null;
			}
		});

		// trap hover events to pop-up tooltip
		control.addMouseTrackListener(new MouseTrackAdapter() {

			@Override
			public void mouseExit(MouseEvent e) {
				hideTooltip();
				tipWidget = null;
			}

			@Override
			public void mouseHover(MouseEvent event) {
				Point widgetPosition = new Point(event.x, event.y);
				Widget widget = event.widget;
				if (widget instanceof ToolBar) {
					ToolBar w = (ToolBar) widget;
					widget = w.getItem(widgetPosition);
				}
				if (widget instanceof Table) {
					Table w = (Table) widget;
					widget = w.getItem(widgetPosition);
				}
				if (widget instanceof Tree) {
					Tree w = (Tree) widget;
					widget = w.getItem(widgetPosition);
				}
				
				if (widget == null) {
					hideTooltip();
					tipWidget = null;
					return;
				}
				
				if (widget == tipWidget) {
					// already displaying tooltip
					return;
				}

				tipWidget = widget;
				showTooltip(control.toDisplay(widgetPosition));
			}

		});
	}

	/**
	 * Sets the location for a hovering shell
	 * 
	 * @param shell
	 *            the object that is to hover
	 * @param position
	 *            the position of a widget to hover over
	 * @return the top-left location for a hovering box
	 */
	private void setHoverLocation(Shell shell, Point position) {
		Rectangle displayBounds = shell.getMonitor().getClientArea();
		Rectangle shellBounds = shell.getBounds();

		// We need to find the exact monitor we're mousing over
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=166990
		Monitor[] array = PlatformUI.getWorkbench().getDisplay().getMonitors();
		for (Monitor m : array) {
			Rectangle monitorBounds = m.getBounds();
			if ((position.x >= monitorBounds.x) && (position.x < (monitorBounds.x + monitorBounds.width))
					&& (position.y >= monitorBounds.y) && (position.y < (monitorBounds.y + monitorBounds.height))) {
				displayBounds = m.getClientArea();
			}
		}

		if ((position.x + shellBounds.width) > (displayBounds.x + displayBounds.width))
			shellBounds.x = displayBounds.x + displayBounds.width - shellBounds.width;
		else
			shellBounds.x = position.x;

		if ((position.y + 10 + shellBounds.height) > (displayBounds.y + displayBounds.height))
			shellBounds.y = displayBounds.y + displayBounds.height - shellBounds.height;
		else
			shellBounds.y = position.y + 10;

		shell.setBounds(shellBounds);
	}

	private void hideTooltip() {
		// TODO: can these conditions be simplified? see bug 131776
		if (tipShell != null && !tipShell.isDisposed() && tipShell.getDisplay() != null
				&& !tipShell.getDisplay().isDisposed() && tipShell.isVisible()) {
//			tipShell.setVisible(false);
			tipShell.close();
			tipShell = null;
		}
	}

	private void showTooltip(Point location) {
		hideTooltip();

		AbstractTaskContainer element = getTaskListElement(tipWidget);
		String detailsText = getDetailsText(element);
		if (detailsText == null) {
			return;
		}

		Shell parent = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		if (parent == null) {
			return;
		}
		
		tipShell = new Shell(parent.getDisplay(), SWT.TOOL | SWT.NO_FOCUS | SWT.MODELESS | SWT.ON_TOP);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;
		tipShell.setLayout(gridLayout);
		tipShell.setBackground(tipShell.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

		addIconAndLabel(tipShell, getImage(element), getTitleText(element));

		addIconAndLabel(tipShell, null, detailsText);

		String statusText = getStatusText(element);
		if (statusText != null) {
			addIconAndLabel(tipShell, TasksUiImages.getImage(TasksUiImages.WARNING), statusText);
		}

		String activityText = getActivityText(element);
		if (activityText != null) {
			addIconAndLabel(tipShell, TasksUiImages.getImage(TasksUiImages.CALENDAR), activityText);
		}

		String incommingText = getIncommingText(element);
		if (incommingText != null) {
			addIconAndLabel(tipShell, TasksUiImages.getImage(TasksUiImages.OVERLAY_INCOMMING), incommingText);
		}

		ProgressData progress = getProgressData(element);
		if (progress != null) {
			addLabel(tipShell, progress.text);

			Composite progressComposite = new Composite(tipShell, SWT.NONE);
			GridLayout progressLayout = new GridLayout(1, false);
			progressLayout.marginWidth = 2;
			progressLayout.marginHeight = 0;
			progressLayout.marginBottom = 2;
			progressLayout.horizontalSpacing = 0;
			progressLayout.verticalSpacing = 0;
			progressComposite.setLayout(progressLayout);
			progressComposite.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 4, 1));

			WorkweekProgressBar taskProgressBar = new WorkweekProgressBar(progressComposite);
			taskProgressBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			taskProgressBar.reset(progress.completed, progress.total);
		}

		tipShell.pack();
		setHoverLocation(tipShell, location);
		tipShell.setVisible(true);
	}

	private void addLabel(Shell parent, String text) {
		Label textLabel = new Label(parent, SWT.NONE);
		textLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		textLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		textLabel.setAlignment(SWT.CENTER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		gd.horizontalSpan = 2;
		textLabel.setLayoutData(gd);
		textLabel.setText(removeTrailingNewline(text));
	}

	private String removeTrailingNewline(String text) {
		if (text.endsWith("\n")) {
			return text.substring(0, text.length() - 1);
		}
		return text;
	}

	private void addIconAndLabel(Composite parent, Image image, String text) {
		Label imageLabel = new Label(parent, SWT.NONE);
		imageLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		imageLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
		imageLabel.setLayoutData(gd);
		imageLabel.setImage(image);

		Label textLabel = new Label(parent, SWT.NONE);
		textLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		textLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		textLabel.setLayoutData(gd);
		textLabel.setText(removeTrailingNewline(text));
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

}
