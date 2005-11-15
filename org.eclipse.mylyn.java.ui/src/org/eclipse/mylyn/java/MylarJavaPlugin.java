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

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.ui.LandmarkMarkerManager;
import org.eclipse.mylar.java.ui.actions.ApplyMylarToBrowsingPerspectiveAction;
import org.eclipse.mylar.java.ui.actions.ApplyMylarToPackageExplorerAction;
import org.eclipse.mylar.java.ui.editor.ActiveFoldingListener;
import org.eclipse.mylar.java.ui.wizards.MylarPreferenceWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.ISelectionService;
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
public class MylarJavaPlugin extends AbstractUIPlugin {

	private static MylarJavaPlugin plugin;

	private ResourceBundle resourceBundle;

	private JavaEditorTracker editorTracker;

	private PackageExplorerManager packageExplorerManager = new PackageExplorerManager();

	private TypeHistoryManager typeHistoryManager = new TypeHistoryManager();

	private LandmarkMarkerManager landmarkMarkerManager = new LandmarkMarkerManager();

	private JavaProblemListener problemListener = new JavaProblemListener();

	private JavaEditingMonitor javaEditingMonitor;

	private InterestUpdateDeltaListener javaElementChangeListener = new InterestUpdateDeltaListener();

	private MylarChangeSetManager changeSetManager = new MylarChangeSetManager();

	public static final String PLUGIN_ID = "org.eclipse.mylar.java";

	public static final String MYLAR_JAVA_EDITOR_ID = "org.eclipse.mylar.java.ui.editor.MylarCompilationUnitEditor";

	public static final String PACKAGE_EXPLORER_AUTO_FILTER_ENABLE = "org.eclipse.mylar.java.ui.explorer.filter.auto.enable";

	public static final String PREDICTED_INTEREST_ERRORS = "org.eclipse.mylar.java.interest.predicted.errors";

	public static final String PACKAGE_EXPLORER_AUTO_EXPAND = "org.eclipse.mylar.java.explorer.auto.exapand";

	public static ImageDescriptor EDGE_REF_JUNIT = getImageDescriptor("icons/elcl16/edge-ref-junit.gif");

	public static final String FIRST_USE = 
			"<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"http://eclipse.org/mylar/doc/style.css\"/></head>" +
			"<body bgcolor=\"#ffffff\">" + "<p>If this is your first time using Mylar make sure to watch the \n"
			+ "<a target=\"_blank\" href=\"http://eclipse.org/mylar/doc/demo/mylar-demo-04.html\">\n"
			+ "<b>3 minute online flash demo</b></a>.</p><p>Mylar documentation is under \n" + "Help-&gt;Help Contents.</p>" + "</body></html>";

	public MylarJavaPlugin() {
		super();
		plugin = this;
	}

	/**
	 * Startup order is critical.
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		try {
			super.start(context);
			MylarPlugin.getContextManager().addListener(packageExplorerManager);
			MylarPlugin.getContextManager().addListener(typeHistoryManager);
			MylarPlugin.getContextManager().addListener(landmarkMarkerManager);
			MylarPlugin.getContextManager().addListener(changeSetManager);

			setPreferenceDefaults();
			if (getPreferenceStore().getBoolean(PREDICTED_INTEREST_ERRORS)) {
				problemListener.enable();
			}
			getPreferenceStore().addPropertyChangeListener(problemListener);

			final IWorkbench workbench = PlatformUI.getWorkbench();
			workbench.getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (ApplyMylarToPackageExplorerAction.getDefault() != null) {
						ApplyMylarToPackageExplorerAction.getDefault().update();
						getPreferenceStore().addPropertyChangeListener(ApplyMylarToPackageExplorerAction.getDefault());
					}
					if (ApplyMylarToBrowsingPerspectiveAction.getDefault() != null) {
						ApplyMylarToBrowsingPerspectiveAction.getDefault().update();
					}
					if (ApplyMylarToBrowsingPerspectiveAction.getDefault() != null) {
						ApplyMylarToBrowsingPerspectiveAction.getDefault().update();
					} 

					javaEditingMonitor = new JavaEditingMonitor();
					MylarPlugin.getDefault().getSelectionMonitors().add(javaEditingMonitor);
					installEditorTracker(workbench);

					ISelectionService service = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();
					service.addPostSelectionListener(packageExplorerManager);
					
					if (!MylarPlugin.getDefault().suppressWizardsOnStartup() && !getPreferenceStore().contains(MylarPreferenceWizard.MYLAR_FIRST_RUN)) {
						MylarPreferenceWizard wizard = new MylarPreferenceWizard(FIRST_USE);
						Shell shell = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
						if (wizard != null && shell != null && !shell.isDisposed()) {
							WizardDialog dialog = new WizardDialog(shell, wizard);
							dialog.create();
							dialog.open();
							getPreferenceStore().putValue(MylarPreferenceWizard.MYLAR_FIRST_RUN, "false");
						}
					}
				}
			});
			
			JavaCore.addElementChangedListener(javaElementChangeListener);
			savePluginPreferences();
		} catch (Exception e) {
			MylarPlugin.fail(e, "Mylar Java Plug-in Initialization failed", true);
		}
	}

	private void setPreferenceDefaults() {
		getPreferenceStore().setDefault(PACKAGE_EXPLORER_AUTO_FILTER_ENABLE, true);
		getPreferenceStore().setDefault(PACKAGE_EXPLORER_AUTO_EXPAND, true);
		getPreferenceStore().setDefault(PREDICTED_INTEREST_ERRORS, false);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;

		MylarPlugin.getContextManager().removeListener(packageExplorerManager);
		MylarPlugin.getContextManager().removeListener(typeHistoryManager);
		MylarPlugin.getContextManager().removeListener(landmarkMarkerManager);
		MylarPlugin.getContextManager().removeListener(changeSetManager);
		
		MylarPlugin.getDefault().getSelectionMonitors().remove(javaEditingMonitor);

		if (ApplyMylarToPackageExplorerAction.getDefault() != null) {
			getPreferenceStore().removePropertyChangeListener(ApplyMylarToPackageExplorerAction.getDefault());
		}

		if (Workbench.getInstance() != null && Workbench.getInstance().getActiveWorkbenchWindow() != null && !Workbench.getInstance().isClosing()) {
			ISelectionService service = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();
			service.removePostSelectionListener(packageExplorerManager);
		}
		JavaCore.removeElementChangedListener(javaElementChangeListener);
//		CVSUIPlugin.getPlugin().getChangeSetManager().remove(changeSetManager);

		// TODO: uninstall editor tracker
	}

	private void installEditorTracker(IWorkbench workbench) {
		editorTracker = new JavaEditorTracker();
		workbench.addWindowListener(editorTracker);
		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			windows[i].addPageListener(editorTracker);
			IWorkbenchPage[] pages = windows[i].getPages();
			for (int j = 0; j < pages.length; j++) {
				pages[j].addPartListener(editorTracker);
			}
		}

		// update editors that are already opened
		if (Workbench.getInstance().getActiveWorkbenchWindow() != null) {
			IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
			if (page != null) {
				IEditorReference[] references = page.getEditorReferences();
				for (int i = 0; i < references.length; i++) {
					IEditorPart part = references[i].getEditor(false);
					if (part != null && part instanceof JavaEditor) {
						JavaEditor editor = (JavaEditor) part;
						editorTracker.registerEditor(editor);
						ActiveFoldingListener.resetProjection(editor);
					}
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

	public static boolean isMylarEditorDefault() {
		IEditorRegistry editorRegistry = WorkbenchPlugin.getDefault().getEditorRegistry();
		IEditorDescriptor desc = editorRegistry.getDefaultEditor("*.java");
		// return "AspectJ/Java Editor".equals(desc.getLabel());

		return MYLAR_JAVA_EDITOR_ID.equals(desc.getLabel());
	}

	public static void setDefaultEditorForJavaFiles(boolean mylar) {

		EditorRegistry editorRegistry = (EditorRegistry) WorkbenchPlugin.getDefault().getEditorRegistry(); // HACK: cast to allow save
		// to be called
		IFileEditorMapping[] array = WorkbenchPlugin.getDefault().getEditorRegistry().getFileEditorMappings();

		// HACK: cast to allow set to be called
		editorRegistry.setFileEditorMappings((FileEditorMapping[]) array);
		String defaultEditor = editorRegistry.getDefaultEditor("*.java").getId();

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

	public TypeHistoryManager getTypeHistoryManager() {
		return typeHistoryManager;
	}

	public static ImageDescriptor getEDGE_REF_JUNIT() {
		return EDGE_REF_JUNIT;
	}

	public static void setEDGE_REF_JUNIT(ImageDescriptor edge_ref_junit) {
		EDGE_REF_JUNIT = edge_ref_junit;
	}

	public MylarChangeSetManager getChangeSetManager() {
		return changeSetManager;
	}

//	private void resetActiveEditor() {
//		IEditorPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
//		if (part instanceof MylarCompilationUnitEditor) {
//			MylarCompilationUnitEditor editor = (MylarCompilationUnitEditor)part;
//			IJavaElement inputElement = editor.getInputJavaElement();
//			editor.close(true);
//			try {
//				JavaUI.openInEditor(inputElement);
//			} catch (Exception e) {
//				MylarPlugin.fail(e, "Could not reset active editor", false);
//			}
//		}
//	}
	
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
