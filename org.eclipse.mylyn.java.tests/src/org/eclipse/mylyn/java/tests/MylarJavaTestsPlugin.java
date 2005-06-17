package org.eclipse.mylar.java.tests;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MylarJavaTestsPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static MylarJavaTestsPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public MylarJavaTestsPlugin() {
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
	public static MylarJavaTestsPlugin getDefault() {
		return plugin;
	}

}
