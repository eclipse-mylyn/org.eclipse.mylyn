/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.java;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.java.ui.JavaEditingMonitor;
import org.eclipse.mylyn.internal.java.ui.JavaUiUtil;
import org.eclipse.mylyn.internal.java.ui.LandmarkMarkerManager;
import org.eclipse.mylyn.internal.java.ui.editor.ActiveFoldingListener;
import org.eclipse.mylyn.internal.java.ui.wizards.RecommendedPreferencesWizard;
import org.eclipse.mylyn.monitor.ui.MonitorUiPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class FocusedJavaPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.java";

	private static FocusedJavaPlugin INSTANCE;

	private ResourceBundle resourceBundle;

	private ActiveFoldingEditorTracker editorTracker;

//	private PackageExplorerManager packageExplorerManager = new PackageExplorerManager();

	private TypeHistoryManager typeHistoryManager = null;

	private LandmarkMarkerManager landmarkMarkerManager = new LandmarkMarkerManager();

	private InterestInducingProblemListener problemListener = new InterestInducingProblemListener();

	private JavaEditingMonitor javaEditingMonitor;

	private InterestUpdateDeltaListener javaElementChangeListener = new InterestUpdateDeltaListener();

	@Deprecated
	public static final String PREDICTED_INTEREST_ERRORS = "org.eclipse.mylyn.java.interest.prediction.errors";
	
	public FocusedJavaPlugin() {
		super();
		INSTANCE = this;
	}

	/**
	 * Startup order is critical.
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);	
		initDefaultPrefs();
	
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
//					ContextCorePlugin.getContextManager().addListener(packageExplorerManager);
					ContextCorePlugin.getContextManager().addListener(landmarkMarkerManager);
					
					try {
						typeHistoryManager = new TypeHistoryManager();
						ContextCorePlugin.getContextManager().addListener(typeHistoryManager);
					} catch (Throwable t) {
						MylarStatusHandler.log(t, "Could not install type history manager, incompatible Eclipse version.");
					}					
				
					if (getPreferenceStore().getBoolean(PREDICTED_INTEREST_ERRORS)) {
						problemListener.enable();
					}
					
					getPreferenceStore().addPropertyChangeListener(problemListener);

//					MylarMonitorPlugin.getDefault().addWindowPostSelectionListener(packageExplorerManager);

					javaEditingMonitor = new JavaEditingMonitor();
					MonitorUiPlugin.getDefault().getSelectionMonitors().add(javaEditingMonitor);
					installEditorTracker(workbench);

					// TODO: race conditions prevents this from running?
//					if (FocusPackageExplorerAction.getDefault() != null) {
//						FocusPackageExplorerAction.getDefault().update();
//					}
//					if (FocusBrowsingPerspectiveAction.getDefault() != null) {
//						FocusBrowsingPerspectiveAction.getDefault().update();
//					}

					if (!getPreferenceStore().contains(RecommendedPreferencesWizard.MYLAR_FIRST_RUN)) {
						JavaUiUtil.installContentAssist(JavaPlugin.getDefault().getPreferenceStore(), true);
					}

					if (!ContextCorePlugin.getDefault().suppressWizardsOnStartup()
							&& !getPreferenceStore().contains(RecommendedPreferencesWizard.MYLAR_FIRST_RUN)) {
						RecommendedPreferencesWizard wizard = new RecommendedPreferencesWizard();
						Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
						if (wizard != null && shell != null && !shell.isDisposed()) {
							WizardDialog dialog = new WizardDialog(shell, wizard);
							dialog.create();
							dialog.open();
							getPreferenceStore().putValue(RecommendedPreferencesWizard.MYLAR_FIRST_RUN, "false");
						}
					}

					JavaCore.addElementChangedListener(javaElementChangeListener);
				} catch (Throwable t) {
					MylarStatusHandler.fail(t, "Mylar Java plug-in initialization failed", true);
				}
			}
		});
	}

	private void initDefaultPrefs() {
//		getPreferenceStore().setDefault(MylarJavaPrefConstants.PACKAGE_EXPLORER_AUTO_EXPAND, true);
		getPreferenceStore().setDefault(PREDICTED_INTEREST_ERRORS, false);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			super.stop(context);
			INSTANCE = null;
			resourceBundle = null;

//			ContextCorePlugin.getContextManager().removeListener(packageExplorerManager);
			ContextCorePlugin.getContextManager().removeListener(typeHistoryManager);
			ContextCorePlugin.getContextManager().removeListener(landmarkMarkerManager);

			MonitorUiPlugin.getDefault().getSelectionMonitors().remove(javaEditingMonitor);

//			if (FocusPackageExplorerAction.getDefault() != null) {
//				getPreferenceStore().removePropertyChangeListener(FocusPackageExplorerAction.getDefault());
//			}

//			if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()) {
//				for(IWorkbenchWindow w : PlatformUI.getWorkbench().getWorkbenchWindows()) {
//					ISelectionService service = w.getSelectionService();
//					service.removePostSelectionListener(packageExplorerManager);
//				}
//  		}
			JavaCore.removeElementChangedListener(javaElementChangeListener);
			// CVSUIPlugin.getPlugin().getChangeSetManager().remove(changeSetManager);
			// TODO: uninstall editor tracker
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Mylar Java stop terminated abnormally", false);
		}
	}

	private void installEditorTracker(IWorkbench workbench) {
		editorTracker = new ActiveFoldingEditorTracker();
		editorTracker.install(workbench);
		// workbench.addWindowListener(editorTracker);
		// IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
		// for (int i = 0; i < windows.length; i++) {
		// windows[i].addPageListener(editorTracker);
		// IWorkbenchPage[] pages = windows[i].getPages();
		// for (int j = 0; j < pages.length; j++) {
		// pages[j].addPartListener(editorTracker);
		// }
		// }

		// update editors that are already opened
		for(IWorkbenchWindow w : PlatformUI.getWorkbench().getWorkbenchWindows()) {
			IWorkbenchPage page = w.getActivePage();
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
	public static FocusedJavaPlugin getDefault() {
		return INSTANCE;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = FocusedJavaPlugin.getDefault().getResourceBundle();
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
				resourceBundle = ResourceBundle.getBundle("org.eclipse.mylyn.java.JavaPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	// public static boolean isMylarEditorDefault() {
	// IEditorRegistry editorRegistry =
	// WorkbenchPlugin.getDefault().getEditorRegistry();
	// IEditorDescriptor desc = editorRegistry.getDefaultEditor("*.java");
	//
	// return
	// MylarJavaPrefConstants.MYLAR_JAVA_EDITOR_ID.equals(desc.getLabel());
	// }

	// public static void setDefaultEditorForJavaFiles(boolean mylar) {
	//
	// EditorRegistry editorRegistry = (EditorRegistry)
	// WorkbenchPlugin.getDefault().getEditorRegistry();
	// // HACK: cast to allow save to be called
	// IFileEditorMapping[] array =
	// WorkbenchPlugin.getDefault().getEditorRegistry().getFileEditorMappings();
	//
	// // HACK: cast to allow set to be called
	// editorRegistry.setFileEditorMappings((FileEditorMapping[]) array);
	// String defaultEditor = editorRegistry.getDefaultEditor("*.java").getId();
	//
	// if (mylar) {
	//
	// if (!(defaultEditor.equals(MylarJavaPrefConstants.MYLAR_JAVA_EDITOR_ID)))
	// {
	// editorRegistry.setDefaultEditor("*.java",
	// MylarJavaPrefConstants.MYLAR_JAVA_EDITOR_ID);
	// editorRegistry.saveAssociations();
	// }
	// } else {
	// if (!(defaultEditor.equals(JavaUI.ID_CU_EDITOR))) {
	// editorRegistry.setDefaultEditor("*.java", JavaUI.ID_CU_EDITOR);
	// editorRegistry.saveAssociations();
	// }
	// }
	// }

//	public TypeHistoryManager getTypeHistoryManager() {
//		return typeHistoryManager;
//	}

	/**
	 * For testing.
	 */
	public ActiveFoldingEditorTracker getEditorTracker() {
		return editorTracker;
	}
}
