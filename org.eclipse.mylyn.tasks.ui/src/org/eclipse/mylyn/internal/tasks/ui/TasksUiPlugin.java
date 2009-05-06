/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.net.proxy.IProxyChangeEvent;
import org.eclipse.core.net.proxy.IProxyChangeListener;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractNotification;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonColors;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonFonts;
import org.eclipse.mylyn.internal.tasks.core.AbstractSearchHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryModelListener;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.RepositoryExternalizationParticipant;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTemplateManager;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataStore;
import org.eclipse.mylyn.internal.tasks.core.externalization.ExternalizationManager;
import org.eclipse.mylyn.internal.tasks.core.externalization.IExternalizationParticipant;
import org.eclipse.mylyn.internal.tasks.core.externalization.TaskListExternalizationParticipant;
import org.eclipse.mylyn.internal.tasks.core.externalization.TaskListExternalizer;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskListNotificationReminder;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskListNotifier;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiExtensionReader;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylyn.tasks.core.AbstractDuplicateDetector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivationListener;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskActivationAdapter;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.AbstractTaskRepositoryLinkProvider;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Main entry point for the Tasks UI.
 * 
 * @author Mik Kersten
 * @since 3.0
 */
public class TasksUiPlugin extends AbstractUIPlugin {

	private static final int DELAY_QUERY_REFRESH_ON_STARTUP = 20 * 1000;

	private static final int DEFAULT_LINK_PROVIDER_TIMEOUT = 5 * 1000;

	public static final String ID_PLUGIN = "org.eclipse.mylyn.tasks.ui"; //$NON-NLS-1$

	private static final String DIRECTORY_METADATA = ".metadata"; //$NON-NLS-1$

	private static final String NAME_DATA_DIR = ".mylyn"; //$NON-NLS-1$

	private static final char DEFAULT_PATH_SEPARATOR = '/';

	private static final int NOTIFICATION_DELAY = 5000;

	private static TasksUiPlugin INSTANCE;

	private static ExternalizationManager externalizationManager;

	private static TaskActivityManager taskActivityManager;

	private static TaskRepositoryManager repositoryManager;

	private static TaskListSynchronizationScheduler synchronizationScheduler;

	private static TaskDataManager taskDataManager;

	private static Map<String, AbstractRepositoryConnectorUi> repositoryConnectorUiMap = new HashMap<String, AbstractRepositoryConnectorUi>();

	private TaskListNotificationManager taskListNotificationManager;

	private TaskListBackupManager taskListBackupManager;

	private RepositoryTemplateManager repositoryTemplateManager;

	private final Set<AbstractTaskEditorPageFactory> taskEditorPageFactories = new HashSet<AbstractTaskEditorPageFactory>();

	private final TreeSet<AbstractTaskRepositoryLinkProvider> repositoryLinkProviders = new TreeSet<AbstractTaskRepositoryLinkProvider>(
			new OrderComparator());

	private TaskListExternalizer taskListExternalizer;

	private ITaskHighlighter highlighter;

	private final Map<String, Image> brandingIcons = new HashMap<String, Image>();

	private final Map<String, ImageDescriptor> overlayIcons = new HashMap<String, ImageDescriptor>();

	private final Set<AbstractDuplicateDetector> duplicateDetectors = new HashSet<AbstractDuplicateDetector>();

	private ISaveParticipant saveParticipant;

	private TaskEditorBloatMonitor taskEditorBloatManager;

	private TaskJobFactory taskJobFactory;

	// shared colors for all forms
	private FormColors formColors;

	private final List<AbstractSearchHandler> searchHandlers = new ArrayList<AbstractSearchHandler>();

	private static final boolean DEBUG_HTTPCLIENT = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.mylyn.tasks.ui/debug/httpclient")); //$NON-NLS-1$ //$NON-NLS-2$

	// XXX reconsider if this is necessary
	public static class TasksUiStartup implements IStartup {

		public void earlyStartup() {
			// ignore
		}
	}

	private static final class OrderComparator implements Comparator<AbstractTaskRepositoryLinkProvider> {
		public int compare(AbstractTaskRepositoryLinkProvider p1, AbstractTaskRepositoryLinkProvider p2) {
			return p1.getOrder() - p2.getOrder();
		}
	}

	public enum TaskListSaveMode {
		ONE_HOUR, THREE_HOURS, DAY;
		@Override
		public String toString() {
			switch (this) {
			case ONE_HOUR:
				return "1 hour"; //$NON-NLS-1$
			case THREE_HOURS:
				return "3 hours"; //$NON-NLS-1$
			case DAY:
				return "1 day"; //$NON-NLS-1$
			default:
				return "3 hours"; //$NON-NLS-1$
			}
		}

		public static TaskListSaveMode fromString(String string) {
			if (string == null) {
				return null;
			}
			if (string.equals("1 hour")) { //$NON-NLS-1$
				return ONE_HOUR;
			}
			if (string.equals("3 hours")) { //$NON-NLS-1$
				return THREE_HOURS;
			}
			if (string.equals("1 day")) { //$NON-NLS-1$
				return DAY;
			}
			return null;
		}

		public static long fromStringToLong(String string) {
			long hour = 3600 * 1000;
			switch (fromString(string)) {
			case ONE_HOUR:
				return hour;
			case THREE_HOURS:
				return hour * 3;
			case DAY:
				return hour * 24;
			default:
				return hour * 3;
			}
		}
	}

	public enum ReportOpenMode {
		EDITOR, INTERNAL_BROWSER, EXTERNAL_BROWSER;
	}

	private static ITaskActivationListener CONTEXT_TASK_ACTIVATION_LISTENER = new TaskActivationAdapter() {

		@Override
		public void taskActivated(final ITask task) {
			ContextCore.getContextManager().activateContext(task.getHandleIdentifier());
		}

		@Override
		public void taskDeactivated(final ITask task) {
			ContextCore.getContextManager().deactivateContext(task.getHandleIdentifier());
		}

	};

	private static ITaskListNotificationProvider REMINDER_NOTIFICATION_PROVIDER = new ITaskListNotificationProvider() {

		public Set<AbstractNotification> getNotifications() {
			Collection<AbstractTask> allTasks = TasksUiPlugin.getTaskList().getAllTasks();
			Set<AbstractNotification> reminders = new HashSet<AbstractNotification>();
			for (AbstractTask task : allTasks) {
				if (TasksUiPlugin.getTaskActivityManager().isPastReminder(task) && !task.isReminded()) {
					reminders.add(new TaskListNotificationReminder(task));
					task.setReminded(true);
				}
			}
			return reminders;
		}
	};

//	private static ITaskListNotificationProvider INCOMING_NOTIFICATION_PROVIDER = new ITaskListNotificationProvider() {
//
//		@SuppressWarnings( { "deprecation", "restriction" })
//		public Set<AbstractNotification> getNotifications() {
//			Set<AbstractNotification> notifications = new HashSet<AbstractNotification>();
//			// Incoming Changes
//			for (TaskRepository repository : getRepositoryManager().getAllRepositories()) {
//				AbstractRepositoryConnector connector = getRepositoryManager().getRepositoryConnector(
//						repository.getConnectorKind());
//				if (connector instanceof AbstractLegacyRepositoryConnector) {
//					AbstractRepositoryConnectorUi connectorUi = getConnectorUi(repository.getConnectorKind());
//					if (connectorUi != null && !connectorUi.hasCustomNotifications()) {
//						for (ITask itask : TasksUiPlugin.getTaskList().getTasks(repository.getRepositoryUrl())) {
//							if (itask instanceof AbstractTask) {
//								AbstractTask task = (AbstractTask) itask;
//								if ((task.getLastReadTimeStamp() == null || task.getSynchronizationState() == SynchronizationState.INCOMING)
//										&& task.isNotified() == false) {
//									TaskListNotification notification = LegacyChangeManager.getIncommingNotification(
//											connector, task);
//									notifications.add(notification);
//									task.setNotified(true);
//								}
//							}
//						}
//					}
//				}
//			}
//			// New query hits
//			for (RepositoryQuery query : TasksUiPlugin.getTaskList().getQueries()) {
//				TaskRepository repository = getRepositoryManager().getRepository(query.getRepositoryUrl());
//				if (repository != null) {
//					AbstractRepositoryConnector connector = getRepositoryManager().getRepositoryConnector(
//							repository.getConnectorKind());
//					if (connector instanceof AbstractLegacyRepositoryConnector) {
//						AbstractRepositoryConnectorUi connectorUi = getConnectorUi(repository.getConnectorKind());
//						if (!connectorUi.hasCustomNotifications()) {
//							for (ITask hit : query.getChildren()) {
//								if (((AbstractTask) hit).isNotified() == false) {
//									notifications.add(new TaskListNotificationQueryIncoming(hit));
//									((AbstractTask) hit).setNotified(true);
//								}
//							}
//						}
//					}
//				}
//			}
//			return notifications;
//		}
//	};

//	private final IPropertyChangeListener PREFERENCE_LISTENER = new IPropertyChangeListener() {
//
//		public void propertyChange(PropertyChangeEvent event) {
//			// TODO: do we ever get here?
////			if (event.getProperty().equals(ContextPreferenceContstants.PREF_DATA_DIR)) {
////				if (event.getOldValue() instanceof String) {
////					reloadDataDirectory();
////				}
////			}
//		}
//	};

	private final org.eclipse.jface.util.IPropertyChangeListener PROPERTY_LISTENER = new org.eclipse.jface.util.IPropertyChangeListener() {

		public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
			if (event.getProperty().equals(ITasksUiPreferenceConstants.PLANNING_ENDHOUR)
					|| event.getProperty().equals(ITasksUiPreferenceConstants.WEEK_START_DAY)) {
				updateTaskActivityManager();
			}

			if (event.getProperty().equals(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED)
					|| event.getProperty().equals(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS)) {
				updateSynchronizationScheduler(false);
			}
		}
	};

	private TaskActivityMonitor taskActivityMonitor;

	private ServiceReference proxyServiceReference;

	private IProxyChangeListener proxyChangeListener;

	private static TaskListExternalizationParticipant taskListExternalizationParticipant;

	private final Set<IRepositoryModelListener> listeners = new HashSet<IRepositoryModelListener>();

	private static TaskList taskList;

	private static RepositoryModel repositoryModel;

	private static TasksUiFactory uiFactory;

	@Deprecated
	public static String LABEL_VIEW_REPOSITORIES = Messages.TasksUiPlugin_Task_Repositories;

	private class TasksUiInitializationJob extends UIJob {

		public TasksUiInitializationJob() {
			super(Messages.TasksUiPlugin_Initializing_Task_List);
			setSystem(true);
		}

		@SuppressWarnings("restriction")
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			// NOTE: failure in one part of the initialization should
			// not prevent others
			monitor.beginTask("Initializing Task List", 5); //$NON-NLS-1$
			try {
				// Needs to run after workbench is loaded because it
				// relies on images.
				TasksUiExtensionReader.initWorkbenchUiExtensions();

				if (externalizationManager.getLoadStatus() != null) {
					// XXX: recovery from task list load failure  (Rendered in task list)
				}

				List<String> commandLineArgs = Arrays.asList(Platform.getCommandLineArgs());
				boolean activateTask = !commandLineArgs.contains(ITasksCoreConstants.COMMAND_LINE_NO_ACTIVATE_TASK);
				if (activateTask) {
					try {
						Field field = org.eclipse.core.internal.resources.Workspace.class.getDeclaredField("crashed"); //$NON-NLS-1$
						field.setAccessible(true);
						Object value = field.get(ResourcesPlugin.getWorkspace());
						if (value instanceof Boolean) {
							activateTask = !(Boolean) value;
						}
					} catch (Throwable t) {
						t.printStackTrace();
						// ignore
					}
				}
				// Needs to happen asynchronously to avoid bug 159706
				for (AbstractTask task : taskList.getAllTasks()) {
					if (task.isActive()) {
						// the externalizer might set multiple tasks active 
						task.setActive(false);
						if (activateTask) {
							// make sure only one task is activated
							taskActivityManager.activateTask(task);
							activateTask = false;
						}
					}
				}
				//taskActivityMonitor.reloadActivityTime();
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not initialize task activity", t)); //$NON-NLS-1$
			}
			monitor.worked(1);

			try {
				taskListNotificationManager.addNotificationProvider(REMINDER_NOTIFICATION_PROVIDER);
//				taskListNotificationManager.addNotificationProvider(INCOMING_NOTIFICATION_PROVIDER);
				taskListNotificationManager.addNotificationProvider(new TaskListNotifier(getRepositoryModel(),
						getTaskDataManager()));
				taskListNotificationManager.startNotification(NOTIFICATION_DELAY);
				getPreferenceStore().addPropertyChangeListener(taskListNotificationManager);
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not initialize notifications", t)); //$NON-NLS-1$
			}
			monitor.worked(1);

			try {
				taskListBackupManager = new TaskListBackupManager(getBackupFolderPath());
				getPreferenceStore().addPropertyChangeListener(taskListBackupManager);

				synchronizationScheduler = new TaskListSynchronizationScheduler(taskJobFactory);
				updateSynchronizationScheduler(true);
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not initialize task list backup and synchronization", t)); //$NON-NLS-1$
			}
			monitor.worked(1);

			try {

				getPreferenceStore().addPropertyChangeListener(PROPERTY_LISTENER);

				// TODO: get rid of this, hack to make decorators show
				// up on startup
				TaskRepositoriesView repositoriesView = TaskRepositoriesView.getFromActivePerspective();
				if (repositoriesView != null) {
					repositoriesView.getViewer().refresh();
				}

				taskEditorBloatManager = new TaskEditorBloatMonitor();
				taskEditorBloatManager.install(PlatformUI.getWorkbench());
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not finish Tasks UI initialization", t)); //$NON-NLS-1$
			} finally {
				monitor.done();
			}
			return new Status(IStatus.OK, TasksUiPlugin.ID_PLUGIN, IStatus.OK, "", null); //$NON-NLS-1$
		}
	}

	public TasksUiPlugin() {
		super();
		INSTANCE = this;
	}

	private void updateSynchronizationScheduler(boolean initial) {
		boolean enabled = TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED);
		if (enabled) {
			long interval = TasksUiPlugin.getDefault().getPreferenceStore().getLong(
					ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS);
			if (initial) {
				synchronizationScheduler.setInterval(DELAY_QUERY_REFRESH_ON_STARTUP, interval);
			} else {
				synchronizationScheduler.setInterval(interval);
			}
		} else {
			synchronizationScheduler.setInterval(0);
		}
	}

	@SuppressWarnings( { "restriction" })
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		// NOTE: startup order is very sensitive
		try {
			// initialize framework and settings
			if (DEBUG_HTTPCLIENT) {
				// do this before anything else, once commons logging is initialized and an instance 
				// of Log has been created it's too late
				initHttpLogging();
			}
			WebUtil.init();
			initializePreferences(getPreferenceStore());

			// initialize CommonFonts from UI thread: bug 240076
			if (CommonFonts.BOLD == null) {
				// ignore
			}

			File dataDir = new File(getDataDirectory());
			dataDir.mkdirs();

			// create data model
			externalizationManager = new ExternalizationManager(getDataDirectory());

			repositoryManager = new TaskRepositoryManager();
			IExternalizationParticipant repositoryParticipant = new RepositoryExternalizationParticipant(
					externalizationManager, repositoryManager);
			externalizationManager.addParticipant(repositoryParticipant);

			taskList = new TaskList();
			repositoryModel = new RepositoryModel(taskList, repositoryManager);
			taskListExternalizer = new TaskListExternalizer(repositoryModel, repositoryManager);

			taskListExternalizationParticipant = new TaskListExternalizationParticipant(repositoryModel, taskList,
					taskListExternalizer, externalizationManager, repositoryManager);
			//externalizationManager.load(taskListSaveParticipant);
			externalizationManager.addParticipant(taskListExternalizationParticipant);
			taskList.addChangeListener(taskListExternalizationParticipant);

			taskActivityManager = new TaskActivityManager(repositoryManager, taskList);
			taskActivityManager.addActivationListener(taskListExternalizationParticipant);

			// initialize
			updateTaskActivityManager();

			proxyServiceReference = context.getServiceReference(IProxyService.class.getName());
			if (proxyServiceReference != null) {
				IProxyService proxyService = (IProxyService) context.getService(proxyServiceReference);
				if (proxyService != null) {
					proxyChangeListener = new IProxyChangeListener() {
						public void proxyInfoChanged(IProxyChangeEvent event) {
							List<TaskRepository> repos = repositoryManager.getAllRepositories();
							for (TaskRepository repo : repos) {
								if (repo.isDefaultProxyEnabled()) {
									repositoryManager.notifyRepositorySettingsChanged(repo);
								}
							}
						}
					};
					proxyService.addProxyChangeListener(proxyChangeListener);
				}
			}

			repositoryTemplateManager = new RepositoryTemplateManager();

			// NOTE: initializing extensions in start(..) has caused race
			// conditions previously
			TasksUiExtensionReader.initStartupExtensions(taskListExternalizer);

			// instantiate taskDataManager
			TaskDataStore taskDataStore = new TaskDataStore(repositoryManager);
			taskDataManager = new TaskDataManager(taskDataStore, repositoryManager, taskList, taskActivityManager);

			taskJobFactory = new TaskJobFactory(taskList, taskDataManager, repositoryManager, repositoryModel);

			taskActivityManager.addActivationListener(CONTEXT_TASK_ACTIVATION_LISTENER);

			taskActivityMonitor = new TaskActivityMonitor(taskActivityManager, ContextCorePlugin.getContextManager());
			taskActivityMonitor.start();

			saveParticipant = new ISaveParticipant() {

				public void doneSaving(ISaveContext context) {
				}

				public void prepareToSave(ISaveContext context) throws CoreException {
				}

				public void rollback(ISaveContext context) {
				}

				public void saving(ISaveContext context) throws CoreException {
					if (context.getKind() == ISaveContext.FULL_SAVE) {
						externalizationManager.stop();
					}
				}
			};
			ResourcesPlugin.getWorkspace().addSaveParticipant(this, saveParticipant);

			ActivityExternalizationParticipant ACTIVITY_EXTERNALIZTAION_PARTICIPANT = new ActivityExternalizationParticipant(
					externalizationManager);
			externalizationManager.addParticipant(ACTIVITY_EXTERNALIZTAION_PARTICIPANT);
			taskActivityManager.addActivityListener(ACTIVITY_EXTERNALIZTAION_PARTICIPANT);
			taskActivityMonitor.setExternalizationParticipant(ACTIVITY_EXTERNALIZTAION_PARTICIPANT);

			// initialize managers
			initializeDataSources();

			// make this available early for clients that are not initialized through tasks ui but need access 
			taskListNotificationManager = new TaskListNotificationManager();

			// trigger lazy initialization
			new TasksUiInitializationJob().schedule();
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Task list initialization failed", e)); //$NON-NLS-1$
		}
	}

	private void initHttpLogging() {
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog"); //$NON-NLS-1$ //$NON-NLS-2$
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug"); //$NON-NLS-1$ //$NON-NLS-2$
		System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug"); //$NON-NLS-1$ //$NON-NLS-2$
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug"); //$NON-NLS-1$ //$NON-NLS-2$
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.HttpConnection", //$NON-NLS-1$
				"trace"); //$NON-NLS-1$
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.axis.message", "debug"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void updateTaskActivityManager() {
		int endHour = getPreferenceStore().getInt(ITasksUiPreferenceConstants.PLANNING_ENDHOUR);
//		if (taskActivityManager.getEndHour() != endHour) {
//			taskActivityManager.setEndHour(endHour);
		TaskActivityUtil.setEndHour(endHour);
//		}

		int newWeekStartDay = getPreferenceStore().getInt(ITasksUiPreferenceConstants.WEEK_START_DAY);
		int oldWeekStartDay = taskActivityManager.getWeekStartDay();
		if (oldWeekStartDay != newWeekStartDay) {
			taskActivityManager.setWeekStartDay(newWeekStartDay);
//			taskActivityManager.setStartTime(new Date());
		}

		// event.getProperty().equals(TaskListPreferenceConstants.PLANNING_STARTDAY)
		// scheduledStartHour =
		// TasksUiPlugin.getDefault().getPreferenceStore().getInt(
		// TaskListPreferenceConstants.PLANNING_STARTHOUR);
	}

	private void loadTemplateRepositories() {
		// Add standard local task repository
		getLocalTaskRepository();

		// Add the automatically created templates
		for (AbstractRepositoryConnector connector : repositoryManager.getRepositoryConnectors()) {
			for (RepositoryTemplate template : repositoryTemplateManager.getTemplates(connector.getConnectorKind())) {
				if (template.addAutomatically && !TaskRepositoryUtil.isAddAutomaticallyDisabled(template.repositoryUrl)) {
					try {
						String repositoryUrl = TaskRepositoryManager.stripSlashes(template.repositoryUrl);
						TaskRepository taskRepository = repositoryManager.getRepository(connector.getConnectorKind(),
								repositoryUrl);
						if (taskRepository == null) {
							taskRepository = new TaskRepository(connector.getConnectorKind(), repositoryUrl);
							taskRepository.setVersion(template.version);
							taskRepository.setRepositoryLabel(template.label);
							taskRepository.setCharacterEncoding(template.characterEncoding);
							if (template.anonymous) {
								taskRepository.setCredentials(AuthenticationType.REPOSITORY, null, true);
							}
							repositoryManager.addRepository(taskRepository);
						}
					} catch (Throwable t) {
						StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								"Could not load repository template", t)); //$NON-NLS-1$
					}
				}
			}
		}
	}

	/**
	 * Returns the local task repository. If the repository does not exist it is created and added to the task
	 * repository manager.
	 * 
	 * @return the local task repository; never <code>null</code>
	 * @since 3.0
	 */
	public TaskRepository getLocalTaskRepository() {
		TaskRepository localRepository = repositoryManager.getRepository(LocalRepositoryConnector.CONNECTOR_KIND,
				LocalRepositoryConnector.REPOSITORY_URL);
		if (localRepository == null) {
			localRepository = new TaskRepository(LocalRepositoryConnector.CONNECTOR_KIND,
					LocalRepositoryConnector.REPOSITORY_URL);
			localRepository.setVersion(LocalRepositoryConnector.REPOSITORY_VERSION);
			localRepository.setRepositoryLabel(LocalRepositoryConnector.REPOSITORY_LABEL);
			repositoryManager.addRepository(localRepository);
		}
		return localRepository;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			if (formColors != null) {
				formColors.dispose();
				formColors = null;
			}
			if (taskActivityMonitor != null) {
				taskActivityMonitor.stop();
			}

			if (ResourcesPlugin.getWorkspace() != null) {
				ResourcesPlugin.getWorkspace().removeSaveParticipant(this);
			}

			if (proxyServiceReference != null) {
				IProxyService proxyService = (IProxyService) context.getService(proxyServiceReference);
				if (proxyService != null) {
					proxyService.removeProxyChangeListener(proxyChangeListener);
				}
				context.ungetService(proxyServiceReference);
			}

			if (PlatformUI.isWorkbenchRunning()) {
				getPreferenceStore().removePropertyChangeListener(taskListNotificationManager);
				getPreferenceStore().removePropertyChangeListener(taskListBackupManager);
				getPreferenceStore().removePropertyChangeListener(PROPERTY_LISTENER);
				//taskListManager.getTaskList().removeChangeListener(taskListSaveManager);
				CommonColors.dispose();
//				if (ContextCorePlugin.getDefault() != null) {
//					ContextCorePlugin.getDefault().getPluginPreferences().removePropertyChangeListener(
//							PREFERENCE_LISTENER);
//				}
				taskEditorBloatManager.dispose(PlatformUI.getWorkbench());
				INSTANCE = null;
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Task list stop terminated abnormally", e)); //$NON-NLS-1$
		} finally {
			super.stop(context);
		}
	}

	public String getDefaultDataDirectory() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/' + DIRECTORY_METADATA + '/'
				+ NAME_DATA_DIR;
	}

	public String getDataDirectory() {
		return getPreferenceStore().getString(ITasksUiPreferenceConstants.PREF_DATA_DIR);
	}

	/**
	 * Persist <code>path</code> as data directory and loads data from <code>path</code>. This method may block if other
	 * jobs are running that modify tasks data. This method will only execute after all conflicting jobs have been
	 * completed.
	 * 
	 * @throws CoreException
	 *             in case setting of the data directory did not complete normally
	 * @throws OperationCanceledException
	 *             if the operation is cancelled by the user
	 */
	public void setDataDirectory(final String path) throws CoreException {
		Assert.isNotNull(path);
		IRunnableWithProgress runner = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					monitor.beginTask(Messages.TasksUiPlugin_Load_Data_Directory, IProgressMonitor.UNKNOWN);
					if (monitor.isCanceled()) {
						throw new InterruptedException();
					}

					TasksUi.getTaskActivityManager().deactivateActiveTask();

					// set new preference in case of a change
					if (!path.equals(getDataDirectory())) {
						getPreferenceStore().setValue(ITasksUiPreferenceConstants.PREF_DATA_DIR, path);
					}

					// reload data from new directory				
					initializeDataSources();
				} finally {
					// FIXME roll back preferences change in case of an error?
					monitor.done();
				}
			}
		};

		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		try {
			service.runInUI(service, runner, ITasksCoreConstants.ROOT_SCHEDULING_RULE);
		} catch (InvocationTargetException e) {
			throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to set data directory", //$NON-NLS-1$
					e.getCause()));
		} catch (InterruptedException e) {
			throw new OperationCanceledException();
		}
	}

	public void reloadDataDirectory() throws CoreException {
		// no save just load what is there
		setDataDirectory(getDataDirectory());
	}

	/**
	 * Invoked on startup and when data is loaded from disk or when the data directory changes.
	 * 
	 * <p>
	 * Public for testing.
	 */
	@SuppressWarnings("restriction")
	public void initializeDataSources() {
		taskDataManager.setDataPath(getDataDirectory());
		externalizationManager.setRootFolderPath(getDataDirectory());
		ContextCorePlugin.getContextStore().setContextDirectory(getContextStoreDir());

		externalizationManager.load();
		// TODO: Move management of template repositories to TaskRepositoryManager
		loadTemplateRepositories();

		taskActivityManager.clear();
		ContextCorePlugin.getContextManager().loadActivityMetaContext();
		taskActivityMonitor.reloadActivityTime();
		taskActivityManager.reloadPlanningData();

		// inform listeners that initialization is complete
		for (final IRepositoryModelListener listener : listeners) {
			SafeRunner.run(new ISafeRunnable() {

				public void handleException(Throwable exception) {
					StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), exception));
				}

				public void run() throws Exception {
					listener.loaded();
				}
			});
		}
	}

	private File getContextStoreDir() {
		File storeFile = new File(getDataDirectory(), ITasksCoreConstants.CONTEXTS_DIRECTORY);
		if (!storeFile.exists()) {
			storeFile.mkdirs();
		}
		return storeFile;
	}

	@SuppressWarnings("deprecation")
	private void initializePreferences(IPreferenceStore store) {
		store.setDefault(ITasksUiPreferenceConstants.PREF_DATA_DIR, getDefaultDataDirectory());
		store.setDefault(ITasksUiPreferenceConstants.GROUP_SUBTASKS, true);
		store.setDefault(ITasksUiPreferenceConstants.NOTIFICATIONS_ENABLED, true);
		store.setDefault(ITasksUiPreferenceConstants.FILTER_PRIORITY, PriorityLevel.P5.toString());
		store.setDefault(ITasksUiPreferenceConstants.EDITOR_TASKS_RICH, true);
		store.setDefault(ITasksUiPreferenceConstants.ACTIVATE_WHEN_OPENED, false);
		store.setDefault(ITasksUiPreferenceConstants.SHOW_TRIM, false);
		// remove preference
		store.setToDefault(ITasksUiPreferenceConstants.LOCAL_SUB_TASKS_ENABLED);
		store.setDefault(ITasksUiPreferenceConstants.USE_STRIKETHROUGH_FOR_COMPLETED, true);

		store.setDefault(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, true);
		store.setDefault(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS, "" + (20 * 60 * 1000)); //$NON-NLS-1$

		//store.setDefault(TasksUiPreferenceConstants.BACKUP_SCHEDULE, 1);
		store.setDefault(ITasksUiPreferenceConstants.BACKUP_MAXFILES, 20);
		store.setDefault(ITasksUiPreferenceConstants.BACKUP_LAST, 0f);

		store.setDefault(ITasksUiPreferenceConstants.FILTER_ARCHIVE_MODE, true);
		store.setDefault(ITasksUiPreferenceConstants.ACTIVATE_MULTIPLE, false);
		store.setValue(ITasksUiPreferenceConstants.ACTIVATE_MULTIPLE, false);

		store.setDefault(ITasksUiPreferenceConstants.WEEK_START_DAY, Calendar.getInstance().getFirstDayOfWeek());
		//store.setDefault(TasksUiPreferenceConstants.PLANNING_STARTHOUR, 9);
		store.setDefault(ITasksUiPreferenceConstants.PLANNING_ENDHOUR, 18);

		store.setDefault(ITasksUiPreferenceConstants.AUTO_EXPAND_TASK_LIST, true);
	}

	public static TaskActivityManager getTaskActivityManager() {
		return taskActivityManager;
	}

	public static TaskListNotificationManager getTaskListNotificationManager() {
		return INSTANCE.taskListNotificationManager;
	}

	/**
	 * Returns the shared instance.
	 */
	public static TasksUiPlugin getDefault() {
		return INSTANCE;
	}

	public boolean groupSubtasks(ITaskContainer element) {
		boolean groupSubtasks = TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				ITasksUiPreferenceConstants.GROUP_SUBTASKS);

		if (element instanceof ITask) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(((ITask) element).getConnectorKind());
			if (connectorUi != null) {
				if (connectorUi.hasStrictSubtaskHierarchy()) {
					groupSubtasks = true;
				}
			}
		}

		if (element instanceof IRepositoryQuery) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(((IRepositoryQuery) element).getConnectorKind());
			if (connectorUi != null) {
				if (connectorUi.hasStrictSubtaskHierarchy()) {
					groupSubtasks = true;
				}
			}
		}

		return groupSubtasks;
	}

	private final Map<String, List<IDynamicSubMenuContributor>> menuContributors = new HashMap<String, List<IDynamicSubMenuContributor>>();

	public Map<String, List<IDynamicSubMenuContributor>> getDynamicMenuMap() {
		return menuContributors;
	}

	public void addDynamicPopupContributor(String menuPath, IDynamicSubMenuContributor contributor) {
		List<IDynamicSubMenuContributor> contributors = menuContributors.get(menuPath);
		if (contributors == null) {
			contributors = new ArrayList<IDynamicSubMenuContributor>();
			menuContributors.put(menuPath, contributors);
		}
		contributors.add(contributor);
	}

	public String[] getSaveOptions() {
		String[] options = { TaskListSaveMode.ONE_HOUR.toString(), TaskListSaveMode.THREE_HOURS.toString(),
				TaskListSaveMode.DAY.toString() };
		return options;
	}

	public String getBackupFolderPath() {
		return getDataDirectory() + DEFAULT_PATH_SEPARATOR + ITasksCoreConstants.DEFAULT_BACKUP_FOLDER_NAME;
	}

	public ITaskHighlighter getHighlighter() {
		return highlighter;
	}

	public void setHighlighter(ITaskHighlighter highlighter) {
		this.highlighter = highlighter;
	}

	/**
	 * @since 3.0
	 */
	public AbstractTaskEditorPageFactory[] getTaskEditorPageFactories() {
		return taskEditorPageFactories.toArray(new AbstractTaskEditorPageFactory[0]);
	}

	/**
	 * @since 3.0
	 */
	public void addTaskEditorPageFactory(AbstractTaskEditorPageFactory factory) {
		Assert.isNotNull(factory);
		taskEditorPageFactories.add(factory);
	}

	/**
	 * @since 3.0
	 */
	public void removeTaskEditorPageFactory(AbstractTaskEditorPageFactory factory) {
		Assert.isNotNull(factory);
		taskEditorPageFactories.remove(factory);
	}

	public static TaskRepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	/**
	 * @since 3.0
	 */
	public static RepositoryTemplateManager getRepositoryTemplateManager() {
		return INSTANCE.repositoryTemplateManager;
	}

	public void addBrandingIcon(String repositoryType, Image icon) {
		brandingIcons.put(repositoryType, icon);
	}

	public Image getBrandingIcon(String repositoryType) {
		return brandingIcons.get(repositoryType);
	}

	public void addOverlayIcon(String repositoryType, ImageDescriptor icon) {
		overlayIcons.put(repositoryType, icon);
	}

	public ImageDescriptor getOverlayIcon(String repositoryType) {
		return overlayIcons.get(repositoryType);
	}

	public void addRepositoryLinkProvider(AbstractTaskRepositoryLinkProvider repositoryLinkProvider) {
		if (repositoryLinkProvider != null) {
			this.repositoryLinkProviders.add(repositoryLinkProvider);
		}
	}

	public static TaskListBackupManager getBackupManager() {
		return INSTANCE.taskListBackupManager;
	}

	public void addRepositoryConnectorUi(AbstractRepositoryConnectorUi repositoryConnectorUi) {
		if (!repositoryConnectorUiMap.values().contains(repositoryConnectorUi)) {
			repositoryConnectorUiMap.put(repositoryConnectorUi.getConnectorKind(), repositoryConnectorUi);
		}
	}

	/**
	 * @since 3.0
	 */
	public static AbstractRepositoryConnector getConnector(String kind) {
		return getRepositoryManager().getRepositoryConnector(kind);
	}

	public static AbstractRepositoryConnectorUi getConnectorUi(String kind) {
		return repositoryConnectorUiMap.get(kind);
	}

	public static TaskListSynchronizationScheduler getSynchronizationScheduler() {
		return synchronizationScheduler;
	}

	/**
	 * @since 3.0
	 */
	public static TaskDataManager getTaskDataManager() {
		return taskDataManager;
	}

	/**
	 * @since 3.0
	 */
	public static TaskJobFactory getTaskJobFactory() {
		return INSTANCE.taskJobFactory;
	}

	public void addDuplicateDetector(AbstractDuplicateDetector duplicateDetector) {
		Assert.isNotNull(duplicateDetector);
		duplicateDetectors.add(duplicateDetector);
	}

	public Set<AbstractDuplicateDetector> getDuplicateSearchCollectorsList() {
		return duplicateDetectors;
	}

	public String getRepositoriesFilePath() {
		return getDataDirectory() + File.separator + TaskRepositoryManager.DEFAULT_REPOSITORIES_FILE;
	}

	public void addModelListener(IRepositoryModelListener listener) {
		listeners.add(listener);
	}

	public void removeModelListener(IRepositoryModelListener listener) {
		listeners.remove(listener);
	}

	public boolean canSetRepositoryForResource(final IResource resource) {
		if (resource == null) {
			return false;
		}

		// if a repository has already been linked only that provider should be queried to ensure that it is the same
		// provider that is used by getRepositoryForResource()
		final boolean result[] = new boolean[1];
		final boolean found[] = new boolean[1];
		for (final AbstractTaskRepositoryLinkProvider linkProvider : repositoryLinkProviders) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							"Task repository link provider failed: \"" + linkProvider.getId() + "\"", e)); //$NON-NLS-1$ //$NON-NLS-2$
				}

				public void run() throws Exception {
					if (linkProvider.getTaskRepository(resource, getRepositoryManager()) != null) {
						found[0] = true;
						result[0] = linkProvider.canSetTaskRepository(resource);
					}
				}
			});
			if (found[0]) {
				return result[0];
			}
		}

		// find a provider that can set new repository
		for (final AbstractTaskRepositoryLinkProvider linkProvider : repositoryLinkProviders) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							"Task repository link provider failed: \"" + linkProvider.getId() + "\"", e)); //$NON-NLS-1$ //$NON-NLS-2$
				}

				public void run() throws Exception {
					if (linkProvider.canSetTaskRepository(resource)) {
						result[0] = true;
					}
				}
			});
			if (result[0]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Associate a Task Repository with a workbench project
	 * 
	 * @param resource
	 *            project or resource belonging to a project
	 * @param repository
	 *            task repository to associate with given project
	 * @throws CoreException
	 */
	public void setRepositoryForResource(final IResource resource, final TaskRepository repository) {
		Assert.isNotNull(resource);
		for (final AbstractTaskRepositoryLinkProvider linkProvider : repositoryLinkProviders) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							"Task repository link provider failed: \"" + linkProvider.getId() + "\"", e)); //$NON-NLS-1$ //$NON-NLS-2$
				}

				public void run() throws Exception {
					boolean canSetRepository = linkProvider.canSetTaskRepository(resource);
					if (canSetRepository) {
						linkProvider.setTaskRepository(resource, repository);
					}
				}
			});
		}
	}

	/**
	 * Retrieve the task repository that has been associated with the given project (or resource belonging to a project)
	 * 
	 * NOTE: if call does not return in LINK_PROVIDER_TIMEOUT_SECONDS, the provide will be disabled until the next time
	 * that the Workbench starts.
	 */
	public TaskRepository getRepositoryForResource(final IResource resource) {
		Assert.isNotNull(resource);
		long timeout;
		try {
			timeout = Long.parseLong(System.getProperty(ITasksCoreConstants.PROPERTY_LINK_PROVIDER_TIMEOUT,
					DEFAULT_LINK_PROVIDER_TIMEOUT + "")); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			timeout = DEFAULT_LINK_PROVIDER_TIMEOUT;
		}
		Set<AbstractTaskRepositoryLinkProvider> defectiveLinkProviders = new HashSet<AbstractTaskRepositoryLinkProvider>();
		for (final AbstractTaskRepositoryLinkProvider linkProvider : repositoryLinkProviders) {
			long startTime = System.currentTimeMillis();
			final TaskRepository[] repository = new TaskRepository[1];
			SafeRunnable.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, ID_PLUGIN, "Task repository link provider failed: \"" //$NON-NLS-1$
							+ linkProvider.getId() + "\"", e)); //$NON-NLS-1$
				}

				public void run() throws Exception {
					repository[0] = linkProvider.getTaskRepository(resource, getRepositoryManager());
				}
			});
			long elapsed = System.currentTimeMillis() - startTime;
			if (timeout >= 0 && elapsed > timeout) {
				defectiveLinkProviders.add(linkProvider);
			}
			if (repository[0] != null) {
				return repository[0];
			}
		}
		if (!defectiveLinkProviders.isEmpty()) {
			repositoryLinkProviders.removeAll(defectiveLinkProviders);
			StatusHandler.log(new Status(IStatus.WARNING, ID_PLUGIN, "Repository link provider took over " + timeout //$NON-NLS-1$
					+ " ms to execute and was timed out: \"" + defectiveLinkProviders + "\"")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}

	public static ExternalizationManager getExternalizationManager() {
		return externalizationManager;
	}

	public static TaskActivityMonitor getTaskActivityMonitor() {
		return INSTANCE.taskActivityMonitor;
	}

	public static TaskList getTaskList() {
		return taskList;
	}

	public static RepositoryModel getRepositoryModel() {
		return repositoryModel;
	}

	/**
	 * Note: This is provisional API that is used by connectors.
	 * <p>
	 * DO NOT CHANGE.
	 */
	public void addSearchHandler(AbstractSearchHandler searchHandler) {
		searchHandlers.add(searchHandler);
	}

	/**
	 * Note: This is provisional API that is used by connectors.
	 * <p>
	 * DO NOT CHANGE.
	 */
	public void removeSearchHandler(AbstractSearchHandler searchHandler) {
		searchHandlers.remove(searchHandler);
	}

	public AbstractSearchHandler getSearchHandler(String connectorKind) {
		Assert.isNotNull(connectorKind);
		for (AbstractSearchHandler searchHandler : searchHandlers) {
			if (searchHandler.getConnectorKind().equals(connectorKind)) {
				return searchHandler;
			}
		}
		return null;
	}

	public FormColors getFormColors(Display display) {
		if (formColors == null) {
			formColors = new FormColors(display);
			formColors.markShared();
		}
		return formColors;
	}

	public void removeRepositoryLinkProvider(AbstractTaskRepositoryLinkProvider provider) {
		repositoryLinkProviders.remove(provider);
	}

	public TaskListExternalizer createTaskListExternalizer() {
		return new TaskListExternalizer(repositoryModel, repositoryManager);
	}

	public static TaskListExternalizationParticipant getTaskListExternalizationParticipant() {
		return taskListExternalizationParticipant;
	}

	public static TasksUiFactory getUiFactory() {
		if (uiFactory == null) {
			uiFactory = new TasksUiFactory();
		}
		return uiFactory;
	}

}
