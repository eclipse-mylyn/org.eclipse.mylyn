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
package org.eclipse.mylar.internal.java;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.java.ui.LandmarkMarkerManager;
import org.eclipse.mylar.internal.java.ui.actions.ApplyMylarToBrowsingPerspectiveAction;
import org.eclipse.mylar.internal.java.ui.actions.ApplyMylarToPackageExplorerAction;
import org.eclipse.mylar.internal.java.ui.editor.ActiveFoldingListener;
import org.eclipse.mylar.internal.java.ui.wizards.MylarPreferenceWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.EditorRegistry;
import org.eclipse.ui.internal.registry.FileEditorMapping;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarJavaPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylar.java";

	private static MylarJavaPlugin plugin;

	private ResourceBundle resourceBundle;

	private ActiveFoldingEditorTracker editorTracker;

	private PackageExplorerExpansionManager packageExplorerExpansionManager = new PackageExplorerExpansionManager();

	private TypeHistoryManager typeHistoryManager = new TypeHistoryManager();

	private LandmarkMarkerManager landmarkMarkerManager = new LandmarkMarkerManager();

	private JavaProblemListener problemListener = new JavaProblemListener();

	private JavaEditingMonitor javaEditingMonitor;

	private InterestUpdateDeltaListener javaElementChangeListener = new InterestUpdateDeltaListener();

	public static final String FIRST_USE = "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"http://eclipse.org/mylar/doc/style.css\"/></head>"
			+ "<body bgcolor=\"#ffffff\">"
			+ "<p>If this is your first time using Mylar make sure to watch the \n"
			+ "<a target=\"_blank\" href=\"http://eclipse.org/mylar/doc/demo/mylar-demo-04.html\">\n"
			+ "<b>3 minute online flash demo</b></a>.</p><p>Mylar documentation is under \n"
			+ "Help-&gt;Help Contents.</p>" + "</body></html>";

	public MylarJavaPlugin() {
		super();
		plugin = this;
	}

	/**
	 * Startup order is critical.
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					setPreferenceDefaults();
					savePluginPreferences();

					MylarPlugin.getContextManager().addListener(packageExplorerExpansionManager);
					MylarPlugin.getContextManager().addListener(typeHistoryManager);
					MylarPlugin.getContextManager().addListener(landmarkMarkerManager);

					if (getPreferenceStore().getBoolean(MylarJavaPrefConstants.PREDICTED_INTEREST_ERRORS)) {
						problemListener.enable();
					}
					getPreferenceStore().addPropertyChangeListener(problemListener);

					ISelectionService service = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getSelectionService();
					service.addPostSelectionListener(packageExplorerExpansionManager);

					javaEditingMonitor = new JavaEditingMonitor();
					MylarPlugin.getDefault().getSelectionMonitors().add(javaEditingMonitor);
					installEditorTracker(workbench);

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

					if (!MylarPlugin.getDefault().suppressWizardsOnStartup()
							&& !getPreferenceStore().contains(MylarPreferenceWizard.MYLAR_FIRST_RUN)) {
						MylarPreferenceWizard wizard = new MylarPreferenceWizard(FIRST_USE);
						Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
						if (wizard != null && shell != null && !shell.isDisposed()) {
							WizardDialog dialog = new WizardDialog(shell, wizard);
							dialog.create();
							dialog.open();
							getPreferenceStore().putValue(MylarPreferenceWizard.MYLAR_FIRST_RUN, "false");
						}
					}

					JavaCore.addElementChangedListener(javaElementChangeListener);
				} catch (Throwable t) {
					MylarStatusHandler.fail(t, "Mylar Java plug-in initialization failed", true);
				}
			}
		});
	}

	private void setPreferenceDefaults() {
		getPreferenceStore().setDefault(MylarJavaPrefConstants.PACKAGE_EXPLORER_AUTO_FILTER_ENABLE, true);
		getPreferenceStore().setDefault(MylarJavaPrefConstants.PACKAGE_EXPLORER_AUTO_EXPAND, true);
		getPreferenceStore().setDefault(MylarJavaPrefConstants.PREDICTED_INTEREST_ERRORS, false);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			super.stop(context);
			plugin = null;
			resourceBundle = null;

			MylarPlugin.getContextManager().removeListener(packageExplorerExpansionManager);
			MylarPlugin.getContextManager().removeListener(typeHistoryManager);
			MylarPlugin.getContextManager().removeListener(landmarkMarkerManager);

			MylarPlugin.getDefault().getSelectionMonitors().remove(javaEditingMonitor);

			if (ApplyMylarToPackageExplorerAction.getDefault() != null) {
				getPreferenceStore().removePropertyChangeListener(ApplyMylarToPackageExplorerAction.getDefault());
			}

			if (PlatformUI.getWorkbench() != null && PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null
					&& !PlatformUI.getWorkbench().isClosing()) {
				ISelectionService service = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
				service.removePostSelectionListener(packageExplorerExpansionManager);
			}
			JavaCore.removeElementChangedListener(javaElementChangeListener);
			// CVSUIPlugin.getPlugin().getChangeSetManager().remove(changeSetManager);
			// TODO: uninstall editor tracker
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Mylar Java stop failed", false);
		}
	}

	private void installEditorTracker(IWorkbench workbench) {
		editorTracker = new ActiveFoldingEditorTracker();
		editorTracker.install(workbench);
//		workbench.addWindowListener(editorTracker);
//		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
//		for (int i = 0; i < windows.length; i++) {
//			windows[i].addPageListener(editorTracker);
//			IWorkbenchPage[] pages = windows[i].getPages();
//			for (int j = 0; j < pages.length; j++) {
//				pages[j].addPartListener(editorTracker);
//			}
//		}

		// update editors that are already opened
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
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
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
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
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.internal.java", path);
	}

	public static boolean isMylarEditorDefault() {
		IEditorRegistry editorRegistry = WorkbenchPlugin.getDefault().getEditorRegistry();
		IEditorDescriptor desc = editorRegistry.getDefaultEditor("*.java");

		return MylarJavaPrefConstants.MYLAR_JAVA_EDITOR_ID.equals(desc.getLabel());
	}

	public static void setDefaultEditorForJavaFiles(boolean mylar) {

		EditorRegistry editorRegistry = (EditorRegistry) WorkbenchPlugin.getDefault().getEditorRegistry();
		// HACK: cast to allow save to be called
		IFileEditorMapping[] array = WorkbenchPlugin.getDefault().getEditorRegistry().getFileEditorMappings();

		// HACK: cast to allow set to be called
		editorRegistry.setFileEditorMappings((FileEditorMapping[]) array);
		String defaultEditor = editorRegistry.getDefaultEditor("*.java").getId();

		if (mylar) {

			if (!(defaultEditor.equals(MylarJavaPrefConstants.MYLAR_JAVA_EDITOR_ID))) {
				editorRegistry.setDefaultEditor("*.java", MylarJavaPrefConstants.MYLAR_JAVA_EDITOR_ID);
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

	/**
	 * For testing.
	 */
	public ActiveFoldingEditorTracker getEditorTracker() {
		return editorTracker;
	}
}
