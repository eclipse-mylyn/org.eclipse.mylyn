/*******************************************************************************
 * Copyright (c) 2004, 2024 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.ExtensionPointReader;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.monitor.core.IInteractionEventListener;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.ui.AbstractUserActivityMonitor;
import org.eclipse.mylyn.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 * @since 3.0
 */
public class MonitorUiPlugin extends AbstractUIPlugin {

	private static final int DEFAULT_ACTIVITY_TIMEOUT = 180000;

	public static final String ID_PLUGIN = "org.eclipse.mylyn.monitor.ui"; //$NON-NLS-1$

	private static MonitorUiPlugin INSTANCE;

	private final List<AbstractUserInteractionMonitor> selectionMonitors = new ArrayList<>();

	/**
	 * TODO: this could be merged with context interaction events rather than requiring update from the monitor.
	 */
	private final List<IInteractionEventListener> interactionListeners = new ArrayList<>();

	private ActivityContextManager activityContextManager;

	private final List<AbstractUserActivityMonitor> monitors = new ArrayList<>();

	protected Set<IPartListener> partListeners = new HashSet<>();

	protected Set<IPageListener> pageListeners = new HashSet<>();

	protected Set<IPerspectiveListener> perspectiveListeners = new HashSet<>();

	protected Set<ISelectionListener> postSelectionListeners = new HashSet<>();

	private final Set<IWorkbenchWindow> monitoredWindows = new HashSet<>();

	public static final String OBFUSCATED_LABEL = "[obfuscated]"; //$NON-NLS-1$

	public static final String ACTIVITY_TRACKING_ENABLED = "org.eclipse.mylyn.monitor.activity.tracking.enabled"; //$NON-NLS-1$

	private IWorkbenchWindow launchingWorkbenchWindow = null;

	private final org.eclipse.jface.util.IPropertyChangeListener PROPERTY_LISTENER = event -> {
		if (event.getProperty().equals(ActivityContextManager.ACTIVITY_TIMEOUT)
				|| event.getProperty().equals(ActivityContextManager.ACTIVITY_TIMEOUT_ENABLED)) {
			updateActivityTimout();
		} else if (event.getProperty().equals(ACTIVITY_TRACKING_ENABLED)) {
			setActivityTrackingEnabled(getPreferenceStore().getBoolean(ACTIVITY_TRACKING_ENABLED));
		}
	};

	protected IWindowListener WINDOW_LISTENER = new IWindowListener() {
		@Override
		public void windowActivated(IWorkbenchWindow window) {
			// ignore
		}

		@Override
		public void windowDeactivated(IWorkbenchWindow window) {
			// ignore
		}

		@Override
		public void windowOpened(IWorkbenchWindow window) {
			if (PlatformUI.getWorkbench().isClosing()) {
				return;
			}

			if (window instanceof IMonitoredWindow awareWindow) {
				if (!awareWindow.isMonitored()) {
					return;
				}
			}

			addListenersToWindow(window);
		}

		@Override
		public void windowClosed(IWorkbenchWindow window) {
			removeListenersFromWindow(window);
			if (window == launchingWorkbenchWindow) {
				launchingWorkbenchWindow = null;
			}
		}
	};

	private boolean activityTrackingEnabled;

	public MonitorUiPlugin() {
		INSTANCE = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		getPreferenceStore().setDefault(ActivityContextManager.ACTIVITY_TIMEOUT, DEFAULT_ACTIVITY_TIMEOUT);
		getPreferenceStore().setDefault(ActivityContextManager.ACTIVITY_TIMEOUT_ENABLED, true);
		getPreferenceStore().setDefault(ACTIVITY_TRACKING_ENABLED, false);

		activityContextManager = new ActivityContextManager(new ArrayList<>(0));

		// delay initialization until workbench is realized
		UIJob job = new UIJob("Mylyn Monitor Startup") { //$NON-NLS-1$

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				init();
				return Status.OK_STATUS;
			}
		};
		job.setUser(false);
		job.setSystem(true);
		job.schedule();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		try {
			if (activityContextManager != null) {
				activityContextManager.stop();
			}
			if (Platform.isRunning()) {
				getPreferenceStore().removePropertyChangeListener(PROPERTY_LISTENER);
				if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()) {
					PlatformUI.getWorkbench().removeWindowListener(WINDOW_LISTENER);

					for (IWorkbenchWindow window : monitoredWindows) {
						removeListenersFromWindow(window);
					}
				}
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, MonitorUiPlugin.ID_PLUGIN, "Monitor UI stop failed", e)); //$NON-NLS-1$
		}
		INSTANCE = null;
	}

	public void addWindowPartListener(IPartListener listener) {
		partListeners.add(listener);
		for (IWorkbenchWindow window : monitoredWindows) {
			window.getPartService().addPartListener(listener);
		}
	}

	public void removeWindowPartListener(IPartListener listener) {
		partListeners.remove(listener);
		for (IWorkbenchWindow window : monitoredWindows) {
			window.getPartService().removePartListener(listener);
		}
	}

	public void addWindowPageListener(IPageListener listener) {
		pageListeners.add(listener);
		for (IWorkbenchWindow window : monitoredWindows) {
			window.addPageListener(listener);
		}
	}

	public void removeWindowPageListener(IPageListener listener) {
		pageListeners.remove(listener);
		for (IWorkbenchWindow window : monitoredWindows) {
			window.removePageListener(listener);
		}
	}

	public void addWindowPerspectiveListener(IPerspectiveListener listener) {
		perspectiveListeners.add(listener);
		for (IWorkbenchWindow window : monitoredWindows) {
			window.addPerspectiveListener(listener);
		}
	}

	public void removeWindowPerspectiveListener(IPerspectiveListener listener) {
		perspectiveListeners.remove(listener);
		for (IWorkbenchWindow window : monitoredWindows) {
			window.removePerspectiveListener(listener);
		}
	}

	public void addWindowPostSelectionListener(ISelectionListener listener) {
		postSelectionListeners.add(listener);
		for (IWorkbenchWindow window : monitoredWindows) {
			ISelectionService service = window.getSelectionService();
			service.addPostSelectionListener(listener);
		}
	}

	public void removeWindowPostSelectionListener(ISelectionListener listener) {
		getDefault().postSelectionListeners.remove(listener);
		for (IWorkbenchWindow window : monitoredWindows) {
			ISelectionService service = window.getSelectionService();
			service.removePostSelectionListener(listener);
		}
	}

	public static MonitorUiPlugin getDefault() {
		return INSTANCE;
	}

	public List<AbstractUserInteractionMonitor> getSelectionMonitors() {
		return selectionMonitors;
	}

	public void addInteractionListener(IInteractionEventListener listener) {
		interactionListeners.add(listener);
	}

	public void removeInteractionListener(IInteractionEventListener listener) {
		interactionListeners.remove(listener);
	}

	/**
	 * TODO: refactor this, it's awkward
	 */
	public void notifyInteractionObserved(InteractionEvent interactionEvent) {
		for (IInteractionEventListener listener : interactionListeners) {
			listener.interactionObserved(interactionEvent);
		}
	}

	public List<IInteractionEventListener> getInteractionListeners() {
		return interactionListeners;
	}

	static class MonitorUiExtensionPointReader {

		private static final String EXTENSION_ID_USER = "user"; //$NON-NLS-1$

		private static final String ELEMENT_ACTIVITY_TIMER = "osActivityTimer"; //$NON-NLS-1$

		private static boolean extensionsRead = false;

		private static void initExtensions(Collection<AbstractUserActivityMonitor> monitors) {
			if (!extensionsRead) {
				ExtensionPointReader<AbstractUserActivityMonitor> reader = new ExtensionPointReader<>(
						ID_PLUGIN, EXTENSION_ID_USER, ELEMENT_ACTIVITY_TIMER, AbstractUserActivityMonitor.class);
				reader.read();
				List<AbstractUserActivityMonitor> items = reader.getItems();
				Collections.reverse(items);
				// TODO set id for monitor to identify instance
				monitors.addAll(items);

				extensionsRead = true;
			}
		}
	}

	public ActivityContextManager getActivityContextManager() {
		return activityContextManager;
	}

	public boolean suppressConfigurationWizards() {
		List<String> commandLineArgs = Arrays.asList(Platform.getCommandLineArgs());
		if (commandLineArgs.contains("-showMylynWizards")) { //$NON-NLS-1$
			return false;
		} else {
			return commandLineArgs.contains("-pdelaunch"); //$NON-NLS-1$
		}
	}

	private void removeListenersFromWindow(IWorkbenchWindow window) {
		for (IPageListener listener : pageListeners) {
			window.removePageListener(listener);
		}
		for (IPartListener listener : partListeners) {
			window.getPartService().removePartListener(listener);
		}
		for (IPerspectiveListener listener : perspectiveListeners) {
			window.removePerspectiveListener(listener);
		}
		for (ISelectionListener listener : postSelectionListeners) {
			window.getSelectionService().removePostSelectionListener(listener);
		}
		monitoredWindows.remove(window);
	}

	// TODO: consider making API
	private void addListenersToWindow(IWorkbenchWindow window) {
		for (IPageListener listener : pageListeners) {
			window.addPageListener(listener);
		}
		for (IPartListener listener : partListeners) {
			window.getPartService().addPartListener(listener);
		}
		for (IPerspectiveListener listener : perspectiveListeners) {
			window.addPerspectiveListener(listener);
		}
		for (ISelectionListener listener : postSelectionListeners) {
			window.getSelectionService().addPostSelectionListener(listener);
		}

		monitoredWindows.add(window);
	}

	/**
	 * @since 2.2
	 */
	public Set<IWorkbenchWindow> getMonitoredWindows() {
		return monitoredWindows;
	}

	/**
	 * @since 2.2
	 */
	public IWorkbenchWindow getLaunchingWorkbenchWindow() {
		return launchingWorkbenchWindow;
	}

	private void init() {
		try {
			PlatformUI.getWorkbench().addWindowListener(WINDOW_LISTENER);
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if (windows.length > 0) {
				launchingWorkbenchWindow = windows[0];
			}
			for (IWorkbenchWindow window : windows) {
				addListenersToWindow(window);
			}

			// disabled, there is currently no need for this event
//			String productId = InteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH;
//			if (Platform.getProduct() != null) {
//				productId = Platform.getProduct().getId();
//			}
//			ContextCorePlugin.getContextManager().processActivityMetaContextEvent(
//					new InteractionEvent(InteractionEvent.Kind.ATTENTION,
//							InteractionContextManager.ACTIVITY_STRUCTUREKIND_LIFECYCLE, productId,
//							InteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
//							InteractionContextManager.ACTIVITY_DELTA_STARTED, 1f));

			MonitorUiExtensionPointReader.initExtensions(monitors);
			monitors.add(new WorkbenchUserActivityMonitor());

			activityContextManager.init(monitors);

			updateActivityTimout();

			activityContextManager.start();
			setActivityTrackingEnabled(getPreferenceStore().getBoolean(ACTIVITY_TRACKING_ENABLED));

			getPreferenceStore().addPropertyChangeListener(PROPERTY_LISTENER);

		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, MonitorUiPlugin.ID_PLUGIN, "Monitor UI start failed", e)); //$NON-NLS-1$
		}
	}

	private void updateActivityTimout() {
		if (getPreferenceStore().getBoolean(ActivityContextManager.ACTIVITY_TIMEOUT_ENABLED)) {
			activityContextManager
			.setInactivityTimeout(getPreferenceStore().getInt(ActivityContextManager.ACTIVITY_TIMEOUT));
		} else {
			activityContextManager.setInactivityTimeout(0);
		}
	}

	public void setActivityTrackingEnabled(boolean b) {
		activityTrackingEnabled = b;
	}

	public boolean isActivityTrackingEnabled() {
		return activityTrackingEnabled;
	}

	/**
	 * Returns true, if other activity monitors than {@link WorkbenchUserActivityMonitor} have been registered.
	 */
	public boolean isTrackingOsTime() {
		return monitors.size() > 1;
	}

}
