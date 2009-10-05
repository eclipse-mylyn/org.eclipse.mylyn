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
package org.eclipse.mylyn.internal.cdt.ui;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.ui.IContextUiStartup;
import org.eclipse.mylyn.internal.cdt.ui.contentassist.CDTContentAssistUtils;
import org.eclipse.mylyn.internal.cdt.ui.editor.ActiveFoldingEditorTracker;
import org.eclipse.mylyn.internal.cdt.ui.editor.ActiveFoldingListener;
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
 * @author Jeff Johnston
 */
public class CDTUIBridgePlugin extends AbstractUIPlugin implements IContextUiStartup {

	// TODO CHANGE plugin id?
	public static final String ID_PLUGIN = "org.eclipse.cdt.mylyn.ui"; //$NON-NLS-1$

	public static final String AUTO_FOLDING_ENABLED = "org.eclipse.mylyn.context.ui.editor.folding.enabled"; //$NON-NLS-1$

	private static final String MYLYN_RUN_COUNT = "org.eclipse.mylyn.cdt.ui.run.count.3_3_0"; //$NON-NLS-1$

	private static CDTUIBridgePlugin INSTANCE;

	private final LandmarkMarkerManager landmarkMarkerManager = new LandmarkMarkerManager();

	private CDTEditorMonitor cEditingMonitor;

	private final InterestUpdateDeltaListener cElementChangeListener = new InterestUpdateDeltaListener();

	private ActiveFoldingEditorTracker editorTracker;

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
		initializeContentAssist();
	}

	public void lazyStartup() {
		ContextCore.getContextManager().addListener(landmarkMarkerManager);
		cEditingMonitor = new CDTEditorMonitor();
		MonitorUi.getSelectionMonitors().add(cEditingMonitor);
		installEditorTracker(PlatformUI.getWorkbench());
		CoreModel.getDefault().addElementChangedListener(cElementChangeListener);

	}

	private void lazyStop() {
		ContextCore.getContextManager().removeListener(landmarkMarkerManager);
		MonitorUi.getSelectionMonitors().remove(cEditingMonitor);
		CoreModel.getDefault().removeElementChangedListener(cElementChangeListener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		lazyStop();

		super.stop(context);
		INSTANCE = null;
	}

	private void initializeContentAssist() {

		int count = getPreferenceStore().getInt(MYLYN_RUN_COUNT);
		if (count < 1) {
			getPreferenceStore().setValue(MYLYN_RUN_COUNT, count + 1);

			new UIJob("Initializing Focused CDT Content Assist") { //$NON-NLS-1$
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					CDTContentAssistUtils.installContentAssist(CUIPlugin.getDefault().getPreferenceStore(), true);
					return Status.OK_STATUS;
				}
			}.schedule();
		}

		CDTContentAssistUtils.updateDefaultPreference(CUIPlugin.getDefault().getPreferenceStore());
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
				for (IEditorReference element : references) {
					IEditorPart part = element.getEditor(false);
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
}
