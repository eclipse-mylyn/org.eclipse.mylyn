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

import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.team.ContextChangeSetManager;
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

	private ContextChangeSetManager changeSetManager;

	private CommitTemplateManager commitTemplateManager;
	
	public static final String CHANGE_SET_MANAGE = "org.eclipse.mylar.team.changesets.manage";

	public static final String COMMIT_TEMPLATE = "org.eclipse.mylar.team.commit.template";

	public static final String DEFAULT_COMMIT_TEMPLATE = "${task.status} - ${connector.task.prefix} ${task.id}: ${task.description} \n${task.url}"; 

	public MylarTeamPlugin() {
		INSTANCE = this;
	} 

	public void start(BundleContext context) throws Exception {
		super.start(context);
		initPreferenceDefaults();
		commitTemplateManager = new CommitTemplateManager();
		
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					changeSetManager = new ContextChangeSetManager();
					if (getPreferenceStore().getBoolean(CHANGE_SET_MANAGE)) {
						changeSetManager.enable();
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

	public void stop(BundleContext context) throws Exception {
		INSTANCE = null;
		super.stop(context);
		changeSetManager.disable();
	}

	private void initPreferenceDefaults() {
		getPreferenceStore().setDefault(CHANGE_SET_MANAGE, true);
		getPreferenceStore().setDefault(COMMIT_TEMPLATE, DEFAULT_COMMIT_TEMPLATE);
//		getPreferenceStore().setDefault(COMMIT_TEMPLATE_PROGRESS, DEFAULT_TEMPLATE_PROGRESS);
//		getPreferenceStore().setDefault(COMMIT_REGEX_TASK_ID, DEFAULT_REGEX_TASK_ID);
//		getPreferenceStore().setDefault(COMMIT_REGEX_AUTO_GUESS, true);
	}

	public static MylarTeamPlugin getDefault() {
		return INSTANCE;
	}

	public ContextChangeSetManager getChangeSetManager() {
		return changeSetManager;
	}

	public CommitTemplateManager getCommitTemplateManager() {
		return commitTemplateManager;
	}

	public void setChangeSetManager(ContextChangeSetManager changeSetManager) {
		this.changeSetManager = changeSetManager;
	}
}
