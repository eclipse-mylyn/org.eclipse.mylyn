/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *     Itema AS - Added support for build service messages
 *     Itema AS - Automatic refresh when a new repo has been added; bug 330910
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import java.io.IOException;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.ui.BuildsUiStartup;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;

/**
 * @author Steffen Pingel
 * @author Torkild U. Resheim
 */
public class BuildsUiPlugin extends AbstractUIPlugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.builds.ui"; //$NON-NLS-1$

	private static BuildsUiPlugin instance;

	public static BuildsUiPlugin getDefault() {
		return instance;
	}

	private BuildRefresher refresher;

	private BuildNotifier notifier;

	private boolean startupExtensionsInitialized;

	@Override
	public void start(BundleContext context) throws Exception {
		instance = this;
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (refresher != null) {
			refresher.stop();
			refresher = null;
		}
		try {
			BuildsUiInternal.save();
		} catch (IOException e) {
			StatusManager.getManager()
					.handle(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, Messages.BuildsUiPlugin_unexpectedErrorWhileSavingBuilds,
							e));
		}
		super.stop(context);
		instance = null;
	}

	protected IPath getBuildsFile() {
		IPath stateLocation = Platform.getStateLocation(getBundle());
		IPath cacheFile = stateLocation.append("builds.xmi"); //$NON-NLS-1$
		return cacheFile;
	}

	public void initializeRefresh() {
		if (notifier == null) {
			notifier = new BuildNotifier();
			notifier.register(BuildsUiInternal.getModel());
		}

		if (refresher == null) {
			refresher = new BuildRefresher(getPreferenceStore(), BuildsUiInternal.getModel().getScheduler());
			refresher.start();
		}

		if (!startupExtensionsInitialized) {
			// start automatic discovery services etc.
			UiStartupExtensionPointReader.runStartupExtensions();
			startupExtensionsInitialized = true;
		}
	}

	/**
	 * Performs a one-shot refresh of build server data regardless of the automatic refresh preference setting. This method should be called
	 * when build service settings has been changed in a way that require update of the data. For instance when a new repository has been
	 * added.
	 */
	public void refreshBuilds() {
		initializeRefresh();
		refresher.refresh();

		// delay the save until other async tasks complete to ensure that the model is updated before it's persisted
		BuildsUiInternal.getModel().getLoader().getRealm().asyncExec(() -> {
			// trigger a save in case Eclipse crashes and stop() is not executed
			try {
				BuildsUiInternal.save();
			} catch (IOException e) {
				StatusManager.getManager()
						.handle(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
								Messages.BuildsUiPlugin_unexpectedErrorWhileSavingBuilds, e));
			}
		});
	}

	static class UiStartupExtensionPointReader {

		private static final String EXTENSION_ID_STARTUP = "org.eclipse.mylyn.builds.ui.startup"; //$NON-NLS-1$

		private static final String ELEMENT_STARTUP = "startup"; //$NON-NLS-1$

		private static final String ELEMENT_CLASS = "class"; //$NON-NLS-1$

		public static void runStartupExtensions() {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_ID_STARTUP);
			IExtension[] extensions = extensionPoint.getExtensions();
			for (IExtension extension : extensions) {
				IConfigurationElement[] elements = extension.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					if (element.getName().compareTo(ELEMENT_STARTUP) == 0) {
						runStartupExtension(element);
					}
				}
			}
		}

		private static void runStartupExtension(IConfigurationElement configurationElement) {
			try {
				Object object = configurationElement.createExecutableExtension(ELEMENT_CLASS);
				if (!(object instanceof final BuildsUiStartup startup)) {
					StatusHandler.log(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
							NLS.bind("Startup extension failed: {0} does notimplement {1}", //$NON-NLS-1$
									object.getClass().getCanonicalName(), BuildsUiStartup.class.getCanonicalName())));
					return;
				}
				SafeRunner.run(new ISafeRunnable() {
					@Override
					public void run() throws Exception {
						startup.lazyStartup();
					}

					@Override
					public void handleException(Throwable exception) {
						// ignore, handled by SafeRunner
					}
				});
			} catch (Throwable e) {
				StatusHandler.log(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, "Startup extension failed", e)); //$NON-NLS-1$
			}
		}

	}

}
