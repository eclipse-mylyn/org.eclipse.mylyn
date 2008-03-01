/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.java.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.AbstractContextUiPlugin;
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
public class JavaUiBridgePlugin extends AbstractContextUiPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.java.ui";

	private static JavaUiBridgePlugin INSTANCE;

	private ResourceBundle resourceBundle;

	private ActiveFoldingEditorTracker editorTracker;

	private TypeHistoryManager typeHistoryManager;

	private LandmarkMarkerManager landmarkMarkerManager;

	private InterestInducingProblemListener problemListener;

	private JavaEditingMonitor javaEditingMonitor;

	private InterestUpdateDeltaListener javaElementChangeListener;

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
	}

	@Override
	protected void lazyStart(IWorkbench workbench) {
		landmarkMarkerManager = new LandmarkMarkerManager();
		ContextCorePlugin.getContextManager().addListener(landmarkMarkerManager);

		javaEditingMonitor = new JavaEditingMonitor();
		MonitorUiPlugin.getDefault().getSelectionMonitors().add(javaEditingMonitor);
		installEditorTracker(workbench);

		javaElementChangeListener = new InterestUpdateDeltaListener();
		JavaCore.addElementChangedListener(javaElementChangeListener);

		problemListener = new InterestInducingProblemListener();
		getPreferenceStore().addPropertyChangeListener(problemListener);
		if (getPreferenceStore().getBoolean(InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS)) {
			problemListener.enable();
		}

		try {
			typeHistoryManager = new TypeHistoryManager();
			ContextCorePlugin.getContextManager().addListener(typeHistoryManager);
		} catch (Throwable t) {
			// FIXME review error message
			StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.PLUGIN_ID,
					"Could not install type history manager: incompatible Eclipse version", t));
		}
	}

	private void initDefaultPrefs() {
		getPreferenceStore().setDefault(InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS, false);
		getPreferenceStore().setDefault(RecommendedPreferencesWizard.MYLYN_FIRST_RUN, true);
	}

	@Override
	protected void lazyStop() {
		ContextCorePlugin.getContextManager().removeListener(typeHistoryManager);
		ContextCorePlugin.getContextManager().removeListener(landmarkMarkerManager);
		MonitorUiPlugin.getDefault().getSelectionMonitors().remove(javaEditingMonitor);
		getPreferenceStore().removePropertyChangeListener(problemListener);
		JavaCore.removeElementChangedListener(javaElementChangeListener);
		// TODO: uninstall editor tracker
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		INSTANCE = null;
		resourceBundle = null;
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
				for (IEditorReference reference : references) {
					IEditorPart part = reference.getEditor(false);
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
	@Deprecated
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
	@Deprecated
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null) {
				resourceBundle = ResourceBundle.getBundle("org.eclipse.mylyn.java.JavaPluginResources");
			}
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
