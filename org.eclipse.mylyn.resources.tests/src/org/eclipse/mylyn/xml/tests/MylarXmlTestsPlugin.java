package org.eclipse.mylar.xml.tests;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MylarXmlTestsPlugin extends Plugin {
	//The shared instance.
	private static MylarXmlTestsPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public MylarXmlTestsPlugin() {
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
	public static MylarXmlTestsPlugin getDefault() {
		return plugin;
	}

}
