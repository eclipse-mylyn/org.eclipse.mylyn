package org.eclipse.mylar.bugzilla.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaTask;
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaTaskExternalizer;
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaTaskListActionContributor;
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaTaskListManager;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class BugzillaUiPlugin extends AbstractUIPlugin implements IStartup {

	private BugzillaTaskListManager bugzillaTaskListManager;
    private static BugzillaUiPlugin plugin;
		
	
	/**
	 * The constructor.
	 */
	public BugzillaUiPlugin() {
		plugin = this;
	}

    public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() {
        		BugzillaPlugin.setResultEditorMatchAdapter(new BugzillaResultMatchAdapter());
        		bugzillaTaskListManager = new BugzillaTaskListManager();
        		MylarTasksPlugin.getDefault().addPrimaryContributor(new BugzillaTaskListActionContributor());
        		
        		MylarTasksPlugin.getDefault().getTaskListExternalizer().addExternalizer(
        			new BugzillaTaskExternalizer()
        		);   
        		
            }
        });
    }
	
	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		List<Job> list = new ArrayList<Job>();
		list.addAll(BugzillaTask.REFRESH_JOBS.keySet());
		for(Job j: list){
			j.cancel();
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static BugzillaUiPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.bugzilla.ui", path);
	}
	
	public BugzillaTaskListManager getBugzillaTaskListManager() {
		return bugzillaTaskListManager;
	}
}
