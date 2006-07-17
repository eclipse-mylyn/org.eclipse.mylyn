package org.eclipse.mylar.team;

import org.eclipse.mylar.internal.team.ContextChangeSetManager;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MylarTeamPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylar.team";

	private static MylarTeamPlugin plugin;

	private ContextChangeSetManager changeSetManager;
	
	public static final String CHANGE_SET_MANAGE = "org.eclipse.mylar.team.changesets.manage";

	public static final String COMMIT_PREFIX_COMPLETED = "org.eclipse.mylar.team.commit.template.completed";

	public static final String COMMIT_PREFIX_PROGRESS = "org.eclipse.mylar.team.commit.template.progress";

	public static final String DEFAULT_PREFIX_PROGRESS = "Progress on:";

	public static final String DEFAULT_PREFIX_COMPLETED = "Completed:";
	
	public MylarTeamPlugin() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		initPreferenceDefaults();
		changeSetManager = new ContextChangeSetManager();
		
		if (getPreferenceStore().getBoolean(CHANGE_SET_MANAGE)) {
			changeSetManager.enable();
		}
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		changeSetManager.disable();
	}

	private void initPreferenceDefaults() {
		getPreferenceStore().setDefault(CHANGE_SET_MANAGE, true);
		getPreferenceStore().setDefault(COMMIT_PREFIX_COMPLETED, DEFAULT_PREFIX_COMPLETED);
		getPreferenceStore().setDefault(COMMIT_PREFIX_PROGRESS, DEFAULT_PREFIX_PROGRESS);
	}
	
	public static MylarTeamPlugin getDefault() {
		return plugin;
	}
	
	public ContextChangeSetManager getChangeSetManager() {
		return changeSetManager;
	}
}
