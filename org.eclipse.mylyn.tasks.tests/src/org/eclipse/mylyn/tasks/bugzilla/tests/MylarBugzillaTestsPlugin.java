package org.eclipse.mylar.tasks.bugzilla.tests;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MylarBugzillaTestsPlugin extends Plugin {
	//The shared instance.
	private static MylarBugzillaTestsPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public MylarBugzillaTestsPlugin() {
		super();
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static MylarBugzillaTestsPlugin getDefault() {
		return plugin;
	}

}
