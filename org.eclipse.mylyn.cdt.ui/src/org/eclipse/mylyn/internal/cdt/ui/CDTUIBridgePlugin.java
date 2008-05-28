/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 		Red Hat Inc. - modification from Java to CDT
 *******************************************************************************/
package org.eclipse.cdt.mylyn.internal.ui;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.mylyn.internal.ui.editor.ActiveFoldingListener;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.context.ui.IContextUiStartup;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
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
 * @author Jeff Johnston
 */
public class CDTUIBridgePlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.cdt.mylyn.ui"; //$NON-NLS-1$
	
	public static final String AUTO_FOLDING_ENABLED = "org.eclipse.mylyn.context.ui.editor.folding.enabled"; //$NON-NLS-1$

	private static final String MYLYN_FIRST_RUN = "org.eclipse.mylyn.ui.first.run.0_4_9"; //$NON-NLS-1$

	public static final int	START_ACTIVATION_POLICY	= 0x00000002;
	
	private static CDTUIBridgePlugin INSTANCE;

	private ResourceBundle resourceBundle;

	private ActiveFoldingEditorTracker editorTracker;

//	private PackageExplorerManager packageExplorerManager = new PackageExplorerManager();

//	private TypeHistoryManager typeHistoryManager = null;

	private LandmarkMarkerManager landmarkMarkerManager = new LandmarkMarkerManager();

	private InterestInducingProblemListener problemListener = new InterestInducingProblemListener();

	private CDTEditorMonitor cEditingMonitor;

	private InterestUpdateDeltaListener cElementChangeListener = new InterestUpdateDeltaListener();

	public CDTUIBridgePlugin() {
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
		if (getPreferenceStore().getBoolean(MYLYN_FIRST_RUN)) {
			getPreferenceStore().setValue(MYLYN_FIRST_RUN, false);
			CDTUiUtil.installContentAssist(CUIPlugin.getDefault().getPreferenceStore(), true);
		}
	}

	private void lazyStart() {
		ContextCorePlugin.getContextManager().addListener(landmarkMarkerManager);
		cEditingMonitor = new CDTEditorMonitor();
		MonitorUiPlugin.getDefault().getSelectionMonitors().add(cEditingMonitor);
		installEditorTracker(PlatformUI.getWorkbench());
		CoreModel.getDefault().addElementChangedListener(cElementChangeListener);

		getPreferenceStore().addPropertyChangeListener(problemListener);
		if (getPreferenceStore().getBoolean(InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS)) {
			problemListener.enable();
		}

//		try {
//			typeHistoryManager = new TypeHistoryManager();
//			ContextCorePlugin.getContextManager().addListener(typeHistoryManager);
//		} catch (Throwable t) {
//			StatusHandler.log(t, "Could not install type history manager, incompatible Eclipse version.");
//		}
	}

	private void initDefaultPrefs() {
		getPreferenceStore().setDefault(InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS, false);
		getPreferenceStore().setDefault(MYLYN_FIRST_RUN, true);
	}


	private void lazyStop() {
//		ContextCorePlugin.getContextManager().removeListener(typeHistoryManager);
		ContextCorePlugin.getContextManager().removeListener(landmarkMarkerManager);
		MonitorUiPlugin.getDefault().getSelectionMonitors().remove(cEditingMonitor);
		CoreModel.getDefault().removeElementChangedListener(cElementChangeListener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		lazyStop();

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
				for (int i = 0; i < references.length; i++) {
					IEditorPart part = references[i].getEditor(false);
					if (part != null && part instanceof CEditor) {
						CEditor editor = (CEditor) part;
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
	public static CDTUIBridgePlugin getDefault() {
		return INSTANCE;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = CDTUIBridgePlugin.getDefault().getResourceBundle();
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
				resourceBundle = ResourceBundle.getBundle("org.eclipse.cdt.mylyn.internal.ui.PluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
	
	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 * 
	 * @param key the message key
	 * @param args an array of substituition strings
	 * @return the resource bundle message
	 */
	public static String getFormattedString(String key, String[] args) {
		return MessageFormat.format(getResourceString(key), (Object[])args);
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


	/**
	 * Lazy startup. See extension point "org.eclipse.mylyn.context.ui.startup".
	 */
	public static class CDTUIBridgeStartup implements IContextUiStartup {

		public void lazyStartup() {
			CDTUIBridgePlugin.getDefault().lazyStart();
		}

	}

}
