/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *     Eike Stepper - template based commit templates
 *******************************************************************************/
package org.eclipse.mylar.team;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.team.LinkedTaskInfoAdapterFactory;
import org.eclipse.mylar.internal.team.MylarTeamExtensionPointReader;
import org.eclipse.mylar.internal.team.template.CommitTemplateManager;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MylarTeamPlugin extends AbstractUIPlugin implements IStartup {

	public static final String PLUGIN_ID = "org.eclipse.mylar.team";

	private static MylarTeamPlugin INSTANCE;

	private Set<AbstractContextChangeSetManager> changeSetManagers = new HashSet<AbstractContextChangeSetManager>();

	private Set<AbstractActiveChangeSetProvider> activeChangeSetProviders = new HashSet<AbstractActiveChangeSetProvider>();
	
	private CommitTemplateManager commitTemplateManager;
	
	public static final String CHANGE_SET_MANAGE = "org.eclipse.mylar.team.changesets.manage";

	public static final String COMMIT_TEMPLATE = "org.eclipse.mylar.team.commit.template";

	public static final String DEFAULT_COMMIT_TEMPLATE = "${task.status} - ${connector.task.prefix} ${task.id}: ${task.description} \n${task.url}"; 

	public MylarTeamPlugin() {
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
					MylarTeamExtensionPointReader extensionPointReader = new MylarTeamExtensionPointReader();
					extensionPointReader.readExtensions();
					
					LinkedTaskInfoAdapterFactory.registerAdapters();
					
					if (getPreferenceStore().getBoolean(CHANGE_SET_MANAGE)) {
						for (AbstractContextChangeSetManager changeSetManager : changeSetManagers) {
							changeSetManager.enable();
						}
					}
				} catch (Exception e) {
					MylarStatusHandler.fail(e, "Mylar Team start failed", false);
				}
			}
		});
	}

	public void earlyStartup() {
		// all done in start
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		INSTANCE = null;
		super.stop(context);
		for (AbstractContextChangeSetManager changeSetManager : changeSetManagers) {
			changeSetManager.disable();
		}
		
		LinkedTaskInfoAdapterFactory.unregisterAdapters();
	}

	private void initPreferenceDefaults() {
		getPreferenceStore().setDefault(CHANGE_SET_MANAGE, true);
		getPreferenceStore().setDefault(COMMIT_TEMPLATE, DEFAULT_COMMIT_TEMPLATE);
	}

	public static MylarTeamPlugin getDefault() {
		return INSTANCE;
	}

	public void addContextChangeSetManager(AbstractContextChangeSetManager changeSetManager) {
		changeSetManagers.add(changeSetManager);
	}
	
	public boolean removeContextChangeSetManager(AbstractContextChangeSetManager changeSetManager) {
		return changeSetManagers.remove(changeSetManager);
	}
	
	public void addActiveChangeSetProvider(AbstractActiveChangeSetProvider provider) {
		activeChangeSetProviders.add(provider);
	}
	
	public Set<AbstractActiveChangeSetProvider> getActiveChangeSetProviders() {
		return Collections.unmodifiableSet(activeChangeSetProviders);
	}
	
	public Set<AbstractContextChangeSetManager> getContextChangeSetManagers() {
		return Collections.unmodifiableSet(changeSetManagers);
	}

	public CommitTemplateManager getCommitTemplateManager() {
		return commitTemplateManager;
	}
}
