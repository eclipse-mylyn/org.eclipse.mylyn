package org.eclipse.mylar.ide;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ide.ui.NavigatorRefreshListener;
import org.eclipse.mylar.ide.ui.ProblemsListInterestFilter;
import org.eclipse.mylar.ide.ui.ResourceUiBridge;
import org.eclipse.mylar.ide.ui.actions.ApplyMylarToNavigatorAction;
import org.eclipse.mylar.ide.ui.actions.ApplyMylarToProblemsListAction;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MylarIdePlugin extends AbstractUIPlugin implements IStartup {

    private ResourceStructureBridge genericResourceBridge;
    
    private NavigatorRefreshListener navigatorRefreshListener = new NavigatorRefreshListener();
    protected ProblemsListInterestFilter interestFilter = new ProblemsListInterestFilter();    
    
	//The shared instance.
	private static MylarIdePlugin plugin;
	
	/**
	 * The constructor.
	 */
	public MylarIdePlugin() {
		plugin = this;
	}

    public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() {
                MylarPlugin.getContextManager().addListener(navigatorRefreshListener);
                MylarPlugin.getDefault().getSelectionMonitors().add(new ResourceSelectionMonitor());
                MylarUiPlugin.getDefault().addAdapter(ResourceStructureBridge.EXTENSION, new ResourceUiBridge());
                
                if (ApplyMylarToNavigatorAction.getDefault() != null) ApplyMylarToNavigatorAction.getDefault().update();
                if (ApplyMylarToProblemsListAction.getDefault() != null) ApplyMylarToProblemsListAction.getDefault().update();
            }
        });
    }
	
	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
        genericResourceBridge = new ResourceStructureBridge(MylarPlugin.getDefault().isPredictedInterestEnabled());
        MylarPlugin.getDefault().setDefaultBridge(genericResourceBridge);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static MylarIdePlugin getDefault() {
		return plugin;
	}

    public ResourceStructureBridge getGenericResourceBridge() {
        return genericResourceBridge;
    }
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.ide", path);
	}
}
