/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.java;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.search.JUnitReferencesProvider;
import org.eclipse.mylar.java.search.JavaImplementorsProvider;
import org.eclipse.mylar.java.search.JavaReadAccessProvider;
import org.eclipse.mylar.java.search.JavaReferencesProvider;
import org.eclipse.mylar.java.search.JavaWriteAccessProvider;
import org.eclipse.mylar.java.ui.JavaUiBridge;
import org.eclipse.mylar.java.ui.LandmarkMarkerManager;
import org.eclipse.mylar.java.ui.actions.ApplyMylarToBrowsingPerspectiveAction;
import org.eclipse.mylar.java.ui.actions.ApplyMylarToPackageExplorerAction;
import org.eclipse.mylar.java.ui.wizards.MylarPreferenceWizard;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.EditorRegistry;
import org.eclipse.ui.internal.registry.FileEditorMapping;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarJavaPlugin extends AbstractUIPlugin implements IStartup {
	private static MylarJavaPlugin plugin;
	private ResourceBundle resourceBundle;
    private static JavaStructureBridge structureBridge = new JavaStructureBridge();
//    private static JavaUiUpdateBridge modelUpdateBridge = new JavaUiUpdateBridge();
    private static JavaUiBridge uiBridge = new JavaUiBridge();
	private JavaEditorTracker editorTracker;
    

    public static final String PLUGIN_ID = "org.eclipse.mylar.java";
    public static final String MYLAR_JAVA_EDITOR_ID = "org.eclipse.mylar.java.ui.editor.MylarCompilationUnitEditor";
    
	public MylarJavaPlugin() {
		super();
		plugin = this;
    }

    public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() {
                MylarPlugin.getDefault().addBridge(structureBridge); 
//                MylarPlugin.getTaskscapeManager().addListener(modelUpdateBridge);
                
                MylarPlugin.getTaskscapeManager().addListener(new JavaReferencesProvider());
                MylarPlugin.getTaskscapeManager().addListener(new JavaImplementorsProvider());
                MylarPlugin.getTaskscapeManager().addListener(new JavaReadAccessProvider());
                MylarPlugin.getTaskscapeManager().addListener(new JavaWriteAccessProvider()); 
                MylarPlugin.getTaskscapeManager().addListener(new JUnitReferencesProvider());
                
                MylarPlugin.getDefault().getSelectionMonitors().add(new JavaEditingMonitor());
                MylarPlugin.getTaskscapeManager().addListener(new LandmarkMarkerManager());
                MylarUiPlugin.getDefault().addAdapter(structureBridge.getResourceExtension(), uiBridge);
                
            	installEditorTracker(workbench);
            
            	if (ApplyMylarToPackageExplorerAction.getDefault() != null) {
            		ApplyMylarToPackageExplorerAction.getDefault().update();
            	}
            	if (ApplyMylarToBrowsingPerspectiveAction.getDefault() != null) {
            		ApplyMylarToBrowsingPerspectiveAction.getDefault().update();
            	}
            }
        });
    }

    @Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		if(!MylarPlugin.getDefault().suppressWizardsOnStartup() && !getPreferenceStore().contains(MylarPreferenceWizard.MYLAR_FIRST_RUN)){
			final IWorkbench workbench = PlatformUI.getWorkbench();
	        workbench.getDisplay().asyncExec(new Runnable() {
	            public void run() {
	            	MylarPreferenceWizard wizard= new MylarPreferenceWizard();
        			Shell shell = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
	        		if (wizard != null && shell != null && !shell.isDisposed()) { 
	        			WizardDialog dialog = new WizardDialog(shell, wizard);
	        			dialog.create();
	        			dialog.open();
	        		}
	            }
	        });
		}
		getPreferenceStore().putValue(MylarPreferenceWizard.MYLAR_FIRST_RUN, "false");
		getPreferenceStore().putValue(MylarPreferenceWizard.MYLAR_FIRST_RUN, "false");
		savePluginPreferences();
	}

    @Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	private void installEditorTracker(IWorkbench workbench) {
		editorTracker = new JavaEditorTracker();
		workbench.addWindowListener(editorTracker);
		IWorkbenchWindow[] windows= workbench.getWorkbenchWindows();
		for (int i= 0; i < windows.length; i++) {
			windows[i].addPageListener(editorTracker);
			IWorkbenchPage[] pages= windows[i].getPages();
			for (int j= 0; j < pages.length; j++) {
				pages[j].addPartListener(editorTracker);
			}
		}
		
		// update editos that are already opene
        IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
        if (page != null) {
            IEditorReference[] references = page.getEditorReferences();
            for (int i = 0; i < references.length; i++) {
                IEditorPart part = references[i].getEditor(false);
                if (part != null  && part instanceof JavaEditor) {
                	JavaEditor editor = (JavaEditor)part;
                	editorTracker.registerEditor(editor);
                	editor.doSave(null); // HACK: to avoid discarding changes
                	editor.setInput(editor.getEditorInput()); // HACK: to fold
                }
            }
        }
	}
    
	/**
	 * Returns the shared instance.
	 */
	public static MylarJavaPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = MylarJavaPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("org.eclipse.mylar.java.JavaPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.java", path);
	}

    public static JavaUiBridge getUiBridge() {
        return uiBridge;
    }

    public static JavaStructureBridge getStructureBridge() {
        return structureBridge;
    }
    
    public static boolean isMylarEditorDefault() {
		IEditorRegistry editorRegistry = WorkbenchPlugin.getDefault()
				.getEditorRegistry();
		IEditorDescriptor desc = editorRegistry.getDefaultEditor("*.java");
		// return "AspectJ/Java Editor".equals(desc.getLabel());

		return MYLAR_JAVA_EDITOR_ID.equals(desc.getLabel());
	}

	public static void setDefaultEditorForJavaFiles(boolean mylar) {

		EditorRegistry editorRegistry = (EditorRegistry) WorkbenchPlugin
				.getDefault().getEditorRegistry(); // HACK: cast to allow save
		// to be called
		IFileEditorMapping[] array = WorkbenchPlugin.getDefault()
				.getEditorRegistry().getFileEditorMappings();

		// HACK: cast to allow set to be called
		editorRegistry.setFileEditorMappings((FileEditorMapping[]) array);
		String defaultEditor = editorRegistry.getDefaultEditor("*.java")
				.getId();

		if (mylar) {

			if (!(defaultEditor.equals(MYLAR_JAVA_EDITOR_ID))) {
				editorRegistry.setDefaultEditor("*.java", MYLAR_JAVA_EDITOR_ID);
				editorRegistry.saveAssociations();
			}
		} else {
			if (!(defaultEditor.equals(JavaUI.ID_CU_EDITOR))) {
				editorRegistry.setDefaultEditor("*.java", JavaUI.ID_CU_EDITOR);
				editorRegistry.saveAssociations();
			}
		}
	}

    
    
//    /**
//	 * 
//	 * CODE FROM
//	 * 
//	 * @see org.eclipse.jdt.ui.actions.CustomFiltersActionGroup
//	 * 
//	 * Slightly modified. Needed to initialize the structure view to have no
//	 * filter
//	 * 
//	 */
//    
//	private static final String TAG_USER_DEFINED_PATTERNS_ENABLED= "userDefinedPatternsEnabled"; //$NON-NLS-1$
//	private static final String TAG_USER_DEFINED_PATTERNS= "userDefinedPatterns"; //$NON-NLS-1$
//	private static final String TAG_LRU_FILTERS = "lastRecentlyUsedFilters"; //$NON-NLS-1$
//
//	private static final String SEPARATOR= ",";  //$NON-NLS-1$
//	
//	private final String fTargetId = "org.eclipse.jdt.internal.ui.text.QuickOutline";
	
//    // HACK: used to disable the filter from the quick outline by default
//    public void initializeWithPluginContributions() {
//    	IPreferenceStore store= JavaPlugin.getDefault().getPreferenceStore();
//    	if (store.contains(getPreferenceKey("TAG_DUMMY_TO_TEST_EXISTENCE")))
//    		return;
//    	
//		FilterDescriptor[] filterDescs= getCachedFilterDescriptors();
//		Map<String, FilterDescriptor> fFilterDescriptorMap= new HashMap<String, FilterDescriptor>(filterDescs.length);
//		Map<String, Boolean> fEnabledFilterIds= new HashMap<String, Boolean>(filterDescs.length);
//		for (int i= 0; i < filterDescs.length; i++) {
//			String id= filterDescs[i].getId();
//			Boolean isEnabled= new Boolean(filterDescs[i].isEnabled());
//			if (fEnabledFilterIds.containsKey(id))
//				JavaPlugin.logErrorMessage("WARNING: Duplicate id for extension-point \"org.eclipse.jdt.ui.javaElementFilters\""); //$NON-NLS-1$
//			fEnabledFilterIds.put(id, isEnabled);
//			fFilterDescriptorMap.put(id, filterDescs[i]);
//		}
//		storeViewDefaults(fEnabledFilterIds, store);
//	}
    
//    private void storeViewDefaults(Map<String, Boolean> fEnabledFilterIds, IPreferenceStore store) {
//		// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=22533
//		store.setValue(getPreferenceKey("TAG_DUMMY_TO_TEST_EXISTENCE"), "storedViewPreferences");//$NON-NLS-1$//$NON-NLS-2$
//		
//		store.setValue(getPreferenceKey(TAG_USER_DEFINED_PATTERNS_ENABLED), false);
//		store.setValue(getPreferenceKey(TAG_USER_DEFINED_PATTERNS), CustomFiltersDialog.convertToString(new String[0],SEPARATOR));
//
//		Iterator iter= fEnabledFilterIds.entrySet().iterator();
//		while (iter.hasNext()) {
//			Map.Entry entry= (Map.Entry)iter.next();
//			String id= (String)entry.getKey();
//			boolean isEnabled= ((Boolean)entry.getValue()).booleanValue();
//			if(id.equals("org.eclipse.mylar.ui.java.InterestFilter")){
//				store.setValue(id, false);	
//			} else {
//				store.setValue(id, isEnabled);
//			}
//		}
//
//		StringBuffer buf= new StringBuffer("");
//		store.setValue(TAG_LRU_FILTERS, buf.toString());
//	}
	
//	private String getPreferenceKey(String tag) {
//		return "CustomFiltersActionGroup." + fTargetId + '.' + tag; //$NON-NLS-1$
//	}
//    
//	private FilterDescriptor[] getCachedFilterDescriptors() {
//		FilterDescriptor[] fCachedFilterDescriptors= FilterDescriptor.getFilterDescriptors(fTargetId);
//		return fCachedFilterDescriptors;
//	}

	
//	/**
//	 * TODO: remove
//	 */
//	public static JavaUiUpdateBridge getModelUpdateBridge() {
//		return modelUpdateBridge;
//	}
}
