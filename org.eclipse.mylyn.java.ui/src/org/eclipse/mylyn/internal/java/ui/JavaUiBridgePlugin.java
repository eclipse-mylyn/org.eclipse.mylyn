/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.java.ui;

import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.java.ui.editor.ActiveFoldingListener;
import org.eclipse.mylyn.internal.java.ui.wizards.RecommendedPreferencesWizard;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.monitor.ui.MonitorUiPlugin;
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
public class JavaUiBridgePlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.java.ui";

	private static JavaUiBridgePlugin INSTANCE;

	private ResourceBundle resourceBundle;

	private ActiveFoldingEditorTracker editorTracker;

//	private PackageExplorerManager packageExplorerManager = new PackageExplorerManager();

	private TypeHistoryManager typeHistoryManager = null;

	private LandmarkMarkerManager landmarkMarkerManager = new LandmarkMarkerManager();

	private InterestInducingProblemListener problemListener = new InterestInducingProblemListener();

	private JavaEditingMonitor javaEditingMonitor;

	private InterestUpdateDeltaListener javaElementChangeListener = new InterestUpdateDeltaListener();

	// TODO: remove
	final IInteractionContextListener PREFERENCES_WIZARD_LISTENER = new IInteractionContextListener() {

		public void contextActivated(IInteractionContext context) {
//			if (getPreferenceStore().getBoolean(RecommendedPreferencesWizard.MYLYN_FIRST_RUN)) {
//				getPreferenceStore().setValue(RecommendedPreferencesWizard.MYLYN_FIRST_RUN, false);
//				getDefault().savePluginPreferences();
//				JavaUiUtil.installContentAssist(JavaPlugin.getDefault().getPreferenceStore(), true);
//				if (!MonitorUiPlugin.getDefault().suppressConfigurationWizards()) {
//					RecommendedPreferencesWizard wizard = new RecommendedPreferencesWizard();
//					Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//					if (shell != null && !shell.isDisposed()) {
//						WizardDialog dialog = new WizardDialog(shell, wizard);
//						dialog.create();
//						dialog.open();
//					}
//				}
//			}
		}

		public void contextCleared(IInteractionContext context) {
			// ignore
		}

		public void contextDeactivated(IInteractionContext context) {
			// ignore
		}

		public void elementDeleted(IInteractionElement element) {
			// ignore
		}

		public void interestChanged(List<IInteractionElement> elements) {
			// ignore
		}

		public void landmarkAdded(IInteractionElement element) {
			// ignore
		}

		public void landmarkRemoved(IInteractionElement element) {
			// ignore
		}

		public void relationsChanged(IInteractionElement element) {
			// ignore
		}
	};

	public JavaUiBridgePlugin() {
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

		// NOTE: moved out of wizard and first task activation to avoid bug 194766
		if (getPreferenceStore().getBoolean(RecommendedPreferencesWizard.MYLYN_FIRST_RUN)) {
			getPreferenceStore().setValue(RecommendedPreferencesWizard.MYLYN_FIRST_RUN, false);
			JavaUiUtil.installContentAssist(JavaPlugin.getDefault().getPreferenceStore(), true);
		}
		
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
//					ContextCorePlugin.getContextManager().addListener(PREFERENCES_WIZARD_LISTENER);
					ContextCorePlugin.getContextManager().addListener(landmarkMarkerManager);

					try {
						typeHistoryManager = new TypeHistoryManager();
						ContextCorePlugin.getContextManager().addListener(typeHistoryManager);
					} catch (Throwable t) {
						StatusHandler.log(t, "Could not install type history manager, incompatible Eclipse version.");
					}

					if (getPreferenceStore().getBoolean(InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS)) {
						problemListener.enable();
					}

					getPreferenceStore().addPropertyChangeListener(problemListener);

					javaEditingMonitor = new JavaEditingMonitor();
					MonitorUiPlugin.getDefault().getSelectionMonitors().add(javaEditingMonitor);
					installEditorTracker(workbench);

					JavaCore.addElementChangedListener(javaElementChangeListener);
				} catch (Throwable t) {
					StatusHandler.fail(t, "Mylyn Java plug-in initialization failed", true);
				}
			}
		});
	}

	private void initDefaultPrefs() {
		getPreferenceStore().setDefault(InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS, false);
		getPreferenceStore().setDefault(RecommendedPreferencesWizard.MYLYN_FIRST_RUN, true);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			super.stop(context);
			INSTANCE = null;
			resourceBundle = null;

//			ContextCorePlugin.getContextManager().removeListener(PREFERENCES_WIZARD_LISTENER);
			ContextCorePlugin.getContextManager().removeListener(typeHistoryManager);
			ContextCorePlugin.getContextManager().removeListener(landmarkMarkerManager);

			MonitorUiPlugin.getDefault().getSelectionMonitors().remove(javaEditingMonitor);

			JavaCore.removeElementChangedListener(javaElementChangeListener);
			// CVSUIPlugin.getPlugin().getChangeSetManager().remove(changeSetManager);
			// TODO: uninstall editor tracker
		} catch (Exception e) {
			StatusHandler.fail(e, "Mylyn Java stop terminated abnormally", false);
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
		for (IWorkbenchWindow w : PlatformUI.getWorkbench().getWorkbenchWindows()) {
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
	public static JavaUiBridgePlugin getDefault() {
		return INSTANCE;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = JavaUiBridgePlugin.getDefault().getResourceBundle();
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
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * For testing.
	 */
	public ActiveFoldingEditorTracker getEditorTracker() {
		return editorTracker;
	}
}
