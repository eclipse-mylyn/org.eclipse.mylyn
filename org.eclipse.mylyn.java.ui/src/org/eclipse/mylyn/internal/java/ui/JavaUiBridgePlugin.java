/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.ui.IContextUiStartup;
import org.eclipse.mylyn.internal.java.ui.editor.ActiveFoldingListener;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class JavaUiBridgePlugin extends AbstractUIPlugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.java.ui"; //$NON-NLS-1$

	public static final String AUTO_FOLDING_ENABLED = "org.eclipse.mylyn.context.ui.editor.folding.enabled"; //$NON-NLS-1$

	private static JavaUiBridgePlugin INSTANCE;

	private ActiveFoldingEditorTracker editorTracker;

	private TypeHistoryManager typeHistoryManager;

	private LandmarkMarkerManager landmarkMarkerManager;

	private JavaEditingMonitor javaEditingMonitor;

	private InterestUpdateDeltaListener javaElementChangeListener;

	private static final String MYLYN_PREVIOUS_RUN = "org.eclipse.mylyn.ui.first.run.0_4_9"; //$NON-NLS-1$

	private static final String MYLYN_RUN_COUNT = "org.eclipse.mylyn.java.ui.run.count.3_1_0"; //$NON-NLS-1$

	private static final String NUM_COMPUTERS_PREF_KEY = "content_assist_number_of_computers"; //$NON-NLS-1$

	public JavaUiBridgePlugin() {
	}

	/**
	 * Startup order is critical.
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		INSTANCE = this;

		IPreferenceStore javaPrefs = JavaPlugin.getDefault().getPreferenceStore();
		// NOTE: moved out of wizard and first task activation to avoid bug 194766
		int count = getPreferenceStore().getInt(MYLYN_RUN_COUNT);
		if (count < 1) {
			getPreferenceStore().setValue(MYLYN_RUN_COUNT, count + 1);

			// Mylyn 3.1 removes 2 computers, migrate JDT setting on first run to avoid prevent JDT from displaying a warning dialog 
			if (count == 0 && getPreferenceStore().contains(MYLYN_PREVIOUS_RUN)) {
				if (javaPrefs.contains(NUM_COMPUTERS_PREF_KEY)) {
					int lastNumberOfComputers = javaPrefs.getInt(NUM_COMPUTERS_PREF_KEY);
					if (lastNumberOfComputers > 0) {
						javaPrefs.putValue(NUM_COMPUTERS_PREF_KEY, Integer.toString(lastNumberOfComputers - 2));
					}
				}
			}

			// try installing Task-Focused content assist twice
			new UIJob("Initialize Content Assist") { //$NON-NLS-1$
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					IPreferenceStore store = JavaPlugin.getDefault().getPreferenceStore();
					JavaUiUtil.installContentAssist(store, true);
					return Status.OK_STATUS;
				}
			}.schedule();
		}

		// the Task-Focused category should be disabled if the user reverts to the default 
		String defaultValue = javaPrefs.getDefaultString(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES);
		javaPrefs.setDefault(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES, defaultValue
				+ JavaUiUtil.ASSIST_MYLYN_ALL + JavaUiUtil.SEPARATOR_CODEASSIST);
	}

	private void lazyStart() {
		landmarkMarkerManager = new LandmarkMarkerManager();
		ContextCore.getContextManager().addListener(landmarkMarkerManager);

		javaEditingMonitor = new JavaEditingMonitor();
		MonitorUi.getSelectionMonitors().add(javaEditingMonitor);
		installEditorTracker(PlatformUI.getWorkbench());

		javaElementChangeListener = new InterestUpdateDeltaListener();
		JavaCore.addElementChangedListener(javaElementChangeListener);

		try {
			typeHistoryManager = new TypeHistoryManager();
			ContextCore.getContextManager().addListener(typeHistoryManager);
		} catch (Throwable t) {
			// FIXME review error message
			StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN,
					"Could not install type history manager: incompatible Eclipse version", t)); //$NON-NLS-1$
		}
	}

	private void lazyStop() {
		if (typeHistoryManager != null) {
			ContextCore.getContextManager().removeListener(typeHistoryManager);
		}
		if (landmarkMarkerManager != null) {
			ContextCore.getContextManager().removeListener(landmarkMarkerManager);
		}
		if (javaEditingMonitor != null) {
			MonitorUi.getSelectionMonitors().remove(javaEditingMonitor);
		}
		if (javaElementChangeListener != null) {
			JavaCore.removeElementChangedListener(javaElementChangeListener);
		}
		// TODO: uninstall editor tracker
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		lazyStop();

		super.stop(context);
		INSTANCE = null;
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
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(ID_PLUGIN, path);
	}

	/**
	 * For testing.
	 */
	public ActiveFoldingEditorTracker getEditorTracker() {
		return editorTracker;
	}

	public static class JavaUiBridgeStartup implements IContextUiStartup {

		public void lazyStartup() {
			JavaUiBridgePlugin.getDefault().lazyStart();
		}

	}

}
