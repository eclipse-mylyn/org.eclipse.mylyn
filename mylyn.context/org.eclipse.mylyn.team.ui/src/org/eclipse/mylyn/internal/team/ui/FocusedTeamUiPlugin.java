/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.team.ui.templates.CommitTemplateManager;
import org.eclipse.mylyn.team.ui.AbstractActiveChangeSetProvider;
import org.eclipse.mylyn.team.ui.AbstractContextChangeSetManager;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
// TODO 3.9 change the name of this class to avoid the word "focused"
public class FocusedTeamUiPlugin extends AbstractUIPlugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.team.ui"; //$NON-NLS-1$

	private static FocusedTeamUiPlugin INSTANCE;

	private final Set<AbstractContextChangeSetManager> changeSetManagers = new HashSet<AbstractContextChangeSetManager>();

	private final Map<ActiveChangeSetManager, AbstractActiveChangeSetProvider> activeChangeSetProviders = new HashMap<ActiveChangeSetManager, AbstractActiveChangeSetProvider>();

	private CommitTemplateManager commitTemplateManager;

	public static final String CHANGE_SET_MANAGE = "org.eclipse.mylyn.team.changesets.manage"; //$NON-NLS-1$

	public static final String COMMIT_TEMPLATE = "org.eclipse.mylyn.team.commit.template"; //$NON-NLS-1$

	public static final String DEFAULT_COMMIT_TEMPLATE = "${task.key}: ${task.description}\n\nTask-Url: ${task.url}"; //$NON-NLS-1$

	public FocusedTeamUiPlugin() {
		INSTANCE = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initPreferenceDefaults();
		commitTemplateManager = new CommitTemplateManager();

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					FocusedTeamExtensionPointReader extensionPointReader = new FocusedTeamExtensionPointReader();
					extensionPointReader.readExtensions();

					if (getPreferenceStore().getBoolean(CHANGE_SET_MANAGE)) {
						for (AbstractContextChangeSetManager changeSetManager : changeSetManagers) {
							changeSetManager.enable();
						}
					}
				} catch (Exception e) {
					StatusHandler.log(
							new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN, "Mylyn Team start failed", e)); //$NON-NLS-1$
				}
			}
		});
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		INSTANCE = null;
		super.stop(context);
		for (AbstractContextChangeSetManager changeSetManager : changeSetManagers) {
			changeSetManager.disable();
		}
	}

	private void initPreferenceDefaults() {
		getPreferenceStore().setDefault(CHANGE_SET_MANAGE, true);
		getPreferenceStore().setDefault(COMMIT_TEMPLATE, DEFAULT_COMMIT_TEMPLATE);
	}

	public static FocusedTeamUiPlugin getDefault() {
		return INSTANCE;
	}

	public void addContextChangeSetManager(AbstractContextChangeSetManager changeSetManager) {
		changeSetManagers.add(changeSetManager);
	}

	public boolean removeContextChangeSetManager(AbstractContextChangeSetManager changeSetManager) {
		return changeSetManagers.remove(changeSetManager);
	}

	public void addActiveChangeSetProvider(AbstractActiveChangeSetProvider provider) {
		ActiveChangeSetManager manager = provider.getActiveChangeSetManager();
		if (manager != null) {
			activeChangeSetProviders.put(manager, provider);
		}
	}

	public Collection<AbstractActiveChangeSetProvider> getActiveChangeSetProviders() {
		return activeChangeSetProviders.values();
	}

	public AbstractActiveChangeSetProvider getActiveChangeSetProvider(ActiveChangeSetManager manager) {
		return activeChangeSetProviders.get(manager);
	}

	public Set<AbstractContextChangeSetManager> getContextChangeSetManagers() {
		return Collections.unmodifiableSet(changeSetManagers);
	}

	public CommitTemplateManager getCommitTemplateManager() {
		return commitTemplateManager;
	}
}
