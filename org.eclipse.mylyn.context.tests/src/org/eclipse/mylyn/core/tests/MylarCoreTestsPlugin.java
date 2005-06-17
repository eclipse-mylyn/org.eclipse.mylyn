package org.eclipse.mylar.core.tests;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MylarCoreTestsPlugin extends Plugin {
	//The shared instance.
	private static MylarCoreTestsPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public MylarCoreTestsPlugin() {
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
	public static MylarCoreTestsPlugin getDefault() {
		return plugin;
	}

}
