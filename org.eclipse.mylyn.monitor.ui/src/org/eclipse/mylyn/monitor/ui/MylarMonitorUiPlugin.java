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

package org.eclipse.mylar.monitor.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.monitor.core.IInteractionEventListener;
import org.eclipse.mylar.monitor.core.InteractionEvent;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarMonitorUiPlugin extends AbstractUIPlugin {

	private static final int TIMEOUT_INACTIVITY_MILLIS = 2 * 60 * 1000;

	private int inactivityTimeout = TIMEOUT_INACTIVITY_MILLIS;

	private static MylarMonitorUiPlugin INSTANCE;

	private ShellLifecycleListener shellLifecycleListener;

	private List<AbstractUserInteractionMonitor> selectionMonitors = new ArrayList<AbstractUserInteractionMonitor>();

	/**
	 * TODO: this could be merged with context interaction events rather than
	 * requiring update from the monitor.
	 */
	private List<IInteractionEventListener> interactionListeners = new ArrayList<IInteractionEventListener>();

	private ActivityContextManager activityContextManager;

	private AbstractUserActivityTimer osActivityTimer = null;

	protected Set<IPartListener> partListeners = new HashSet<IPartListener>();

	protected Set<IPageListener> pageListeners = new HashSet<IPageListener>();

	protected Set<IPerspectiveListener> perspectiveListeners = new HashSet<IPerspectiveListener>();

	protected Set<ISelectionListener> postSelectionListeners = new HashSet<ISelectionListener>();

	public static final String OBFUSCATED_LABEL = "[obfuscated]";

	protected IWindowListener WINDOW_LISTENER = new IWindowListener() {
		public void windowActivated(IWorkbenchWindow window) {
			// ignore
		}

		public void windowDeactivated(IWorkbenchWindow window) {
			// ignore
		}

		public void windowOpened(IWorkbenchWindow window) {
			if (getWorkbench().isClosing()) {
				return;
			}
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

		}

		public void windowClosed(IWorkbenchWindow window) {
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
		}
	};

	public MylarMonitorUiPlugin() {
		INSTANCE = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					getWorkbench().addWindowListener(WINDOW_LISTENER);
					shellLifecycleListener = new ShellLifecycleListener(ContextCorePlugin.getContextManager());
					getWorkbench().getActiveWorkbenchWindow().getShell().addShellListener(shellLifecycleListener);

					new MonitorUiExtensionPointReader().initExtensions();

					AbstractUserActivityTimer activityTimer;
					if (osActivityTimer != null) {
						activityTimer = osActivityTimer;
					} else {
						activityTimer = new WorkbenchUserActivityTimer(TIMEOUT_INACTIVITY_MILLIS);
					}

					activityContextManager = new ActivityContextManager(activityTimer);
					activityContextManager.start();
				} catch (Exception e) {
					MylarStatusHandler.fail(e, "Mylar Monitor start failed", false);
				}
			}
		});
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		try {
			activityContextManager.stop();
			if (getWorkbench() != null && !getWorkbench().isClosing()) {
				getWorkbench().removeWindowListener(WINDOW_LISTENER);
				getWorkbench().getActiveWorkbenchWindow().getShell().removeShellListener(shellLifecycleListener);
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Mylar Monitor stop failed", false);
		}
		INSTANCE = null;
	}

	public ShellLifecycleListener getShellLifecycleListener() {
		return shellLifecycleListener;
	}

	public void setInactivityTimeout(int millis) {
		inactivityTimeout = millis;
		activityContextManager.setTimeoutMillis(millis);
	}

	/**
	 * @return timeout in mililiseconds
	 */
	public int getInactivityTimeout() {
		return inactivityTimeout;
	}

	public void addWindowPartListener(IPartListener listener) {
		partListeners.add(listener);
		for (IWorkbenchWindow window : getWorkbench().getWorkbenchWindows()) {
			window.getPartService().addPartListener(listener);
		}
	}

	public void removeWindowPartListener(IPartListener listener) {
		partListeners.remove(listener);
		for (IWorkbenchWindow window : getWorkbench().getWorkbenchWindows()) {
			window.getPartService().removePartListener(listener);
		}
	}

	public void addWindowPageListener(IPageListener listener) {
		pageListeners.add(listener);
		for (IWorkbenchWindow window : getWorkbench().getWorkbenchWindows()) {
			window.addPageListener(listener);
		}
	}

	public void removeWindowPageListener(IPageListener listener) {
		pageListeners.remove(listener);
		for (IWorkbenchWindow window : getWorkbench().getWorkbenchWindows()) {
			window.removePageListener(listener);
		}
	}

	public void addWindowPerspectiveListener(IPerspectiveListener listener) {
		perspectiveListeners.add(listener);
		for (IWorkbenchWindow window : getWorkbench().getWorkbenchWindows()) {
			window.addPerspectiveListener(listener);
		}
	}

	public void removeWindowPerspectiveListener(IPerspectiveListener listener) {
		perspectiveListeners.remove(listener);
		for (IWorkbenchWindow window : getWorkbench().getWorkbenchWindows()) {
			window.removePerspectiveListener(listener);
		}
	}

	public void addWindowPostSelectionListener(ISelectionListener listener) {
		postSelectionListeners.add(listener);
		for (IWorkbenchWindow window : getWorkbench().getWorkbenchWindows()) {
			ISelectionService service = window.getSelectionService();
			service.addPostSelectionListener(listener);
		}
	}

	public void removeWindowPostSelectionListener(ISelectionListener listener) {
		getDefault().postSelectionListeners.remove(listener);
		for (IWorkbenchWindow window : getDefault().getWorkbench().getWorkbenchWindows()) {
			ISelectionService service = window.getSelectionService();
			service.removePostSelectionListener(listener);
		}
	}

	public static MylarMonitorUiPlugin getDefault() {
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

	class MonitorUiExtensionPointReader {

		public static final String EXTENSION_ID_STUDY = "org.eclipse.mylar.monitor.ui";

		public static final String ELEMENT_ACTIVITY_TIMER = "osActivityTimer";

		public static final String ELEMENT_CLASS = "class";

		private boolean extensionsRead = false;

		@SuppressWarnings("deprecation")
		public void initExtensions() {
			try {
				if (!extensionsRead) {
					IExtensionRegistry registry = Platform.getExtensionRegistry();
					IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_ID_STUDY);
					if (extensionPoint != null) {
						IExtension[] extensions = extensionPoint.getExtensions();
						for (int i = 0; i < extensions.length; i++) {
							IConfigurationElement[] elements = extensions[i].getConfigurationElements();
							for (int j = 0; j < elements.length; j++) {
								if (elements[j].getName().compareTo(ELEMENT_ACTIVITY_TIMER) == 0) {
									readActivityTimer(elements[j]);
								}
							}
						}
						extensionsRead = true;
					}
				}
			} catch (Throwable t) {
				MylarStatusHandler.fail(t, "could not read monitor extension", false);
			}
		}

		private void readActivityTimer(IConfigurationElement element) throws CoreException {
			try {
				if (element.getAttribute(ELEMENT_CLASS) != null) {
					Object activityTimer = element.createExecutableExtension(ELEMENT_CLASS);
					if (activityTimer instanceof AbstractUserActivityTimer) {
						osActivityTimer = (AbstractUserActivityTimer) activityTimer;
					}
				}
			} catch (CoreException throwable) {
				MylarStatusHandler.log(throwable, "could not load activity timer");
			}
		}
	}

	public ActivityContextManager getActivityContextManager() {
		return activityContextManager;
	}
}
