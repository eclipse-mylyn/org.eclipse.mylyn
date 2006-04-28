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

package org.eclipse.mylar.provisional.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.ITaskHighlighter;
import org.eclipse.mylar.internal.ui.AbstractContextLabelProvider;
import org.eclipse.mylar.internal.ui.ColorMap;
import org.eclipse.mylar.internal.ui.ContentOutlineManager;
import org.eclipse.mylar.internal.ui.Highlighter;
import org.eclipse.mylar.internal.ui.HighlighterList;
import org.eclipse.mylar.internal.ui.MylarUiPrefContstants;
import org.eclipse.mylar.internal.ui.MylarViewerManager;
import org.eclipse.mylar.internal.ui.actions.ApplyMylarToOutlineAction;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.IMylarRelation;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarUiPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylar.ui";

	private Map<String, IMylarUiBridge> bridges = new HashMap<String, IMylarUiBridge>();

	private Map<String, ILabelProvider> contextLabelProviders = new HashMap<String, ILabelProvider>();

	private static MylarUiPlugin plugin;

	private ResourceBundle resourceBundle;

	private boolean decorateInterestMode = false;

	private HighlighterList highlighters = null;

	private Highlighter intersectionHighlighter;

	private ColorMap colorMap = new ColorMap();

	private MylarViewerManager viewerManager = new MylarViewerManager();

	private ContentOutlineManager contentOutlineManager = new ContentOutlineManager();

	private final ITaskHighlighter DEFAULT_HIGHLIGHTER = new ITaskHighlighter() {
		public Color getHighlightColor(ITask task) {
			Highlighter highlighter = getHighlighterForContextId("" + task.getHandleIdentifier());
			if (highlighter != null) {
				return highlighter.getHighlightColor();
			} else {
				return null;
			}
		}
	};

	private static final AbstractContextLabelProvider DEFAULT_LABEL_PROVIDER = new AbstractContextLabelProvider() {

		@Override
		protected Image getImage(IMylarElement node) {
			return null;
		}

		@Override
		protected Image getImage(IMylarRelation edge) {
			return null;
		}

		@Override
		protected String getText(IMylarElement node) {
			return "? " + node;
		}

		@Override
		protected String getText(IMylarRelation edge) {
			return "? " + edge;
		}

		@Override
		protected Image getImageForObject(Object object) {
			return null;
		}

		@Override
		protected String getTextForObject(Object node) {
			return "? " + node;
		}

	};

	private static final IMylarUiBridge DEFAULT_UI_BRIDGE = new IMylarUiBridge() {

		public void open(IMylarElement node) {
			// ignore
		}

		public void close(IMylarElement node) {
			// ignore
		}

		public boolean acceptsEditor(IEditorPart editorPart) {
			return false;
		}

		public List<TreeViewer> getContentOutlineViewers(IEditorPart editor) {
			return Collections.emptyList();
		}

		public Object getObjectForTextSelection(TextSelection selection, IEditorPart editor) {
			return null;
		}

		public void restoreEditor(IMylarElement document) {
			// ignore
		}

		public void setContextCapturePaused(boolean paused) {
			// ignore
		}
		
		public IMylarElement getElement(IEditorInput input) {
			return null;
		}
	};

	public MylarUiPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.eclipse.mylar.MylarUiPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		} catch (Throwable t) {
			MylarStatusHandler.log(t, "plug-in intialization failed");
		}
		initializeHighlighters();
		initializeDefaultPreferences(getPrefs());
		initializeActions();
	}

	// public void earlyStartup() {
	// }

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					// TODO: move to MylarPlugin?
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().addShellListener(MylarPlugin.getContextManager().getShellLifecycleListener());
					MylarPlugin.getContextManager().addListener(viewerManager);

					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(
							contentOutlineManager);
					IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
					for (int i = 0; i < windows.length; i++) {
						windows[i].addPageListener(contentOutlineManager);
					}
					if (ApplyMylarToOutlineAction.getDefault() != null)
						ApplyMylarToOutlineAction.getDefault().update();
					MylarTaskListPlugin.getDefault().setHighlighter(DEFAULT_HIGHLIGHTER);
				} catch (Exception e) {
					MylarStatusHandler.fail(e, "Mylar UI initialization failed", true);
				}
			}
		});
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			super.stop(context);
			MylarPlugin.getContextManager().removeListener(viewerManager);
			viewerManager.dispose();
			colorMap.dispose(); 
			highlighters.dispose();
			if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()) {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().removeShellListener(MylarPlugin.getContextManager().getShellLifecycleListener());
			}
		} catch (Exception e) { 
			MylarStatusHandler.fail(e, "Mylar UI stop failed", false);
		}
	}

	private void initializeActions() {
		// don't have any actions to initialize
	}

	private void initializeHighlighters() {
		String hlist = getPrefs().getString(MylarUiPrefContstants.HIGHLIGHTER_PREFIX);
		if (hlist != null && hlist.length() != 0) {
			highlighters = new HighlighterList(hlist);
		} else {
			// Only get here if it is the first time running
			// mylar. load default colors
			highlighters = new HighlighterList();
			highlighters.setToDefaultList();
			getPrefs().setValue(MylarUiPrefContstants.HIGHLIGHTER_PREFIX, this.highlighters.externalizeToString());
		}
	}

	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(MylarUiPrefContstants.AUTO_MANAGE_EDITORS, true);
		store.setDefault(MylarUiPrefContstants.AUTO_MANAGE_EDITORS_OPEN_NUM, 8);

		store.setDefault(MylarUiPrefContstants.GAMMA_SETTING_LIGHTENED, false);
		store.setDefault(MylarUiPrefContstants.GAMMA_SETTING_STANDARD, true);
		store.setDefault(MylarUiPrefContstants.GAMMA_SETTING_DARKENED, false);

		if (!store.contains(MylarUiPrefContstants.GLOBAL_FILTERING))
			store.setDefault(MylarUiPrefContstants.GLOBAL_FILTERING, true);
	}

	public void setHighlighterMapping(String id, String name) {
		String prefId = MylarUiPrefContstants.TASK_HIGHLIGHTER_PREFIX + id;
		getPrefs().putValue(prefId, name);
	}

	/**
	 * Returns the shared instance.
	 */
	public static MylarUiPlugin getDefault() {
		return plugin;
	}

	public static IPreferenceStore getPrefs() {
		return MylarPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = MylarUiPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getMessage(String key) {
		ResourceBundle bundle = getDefault().getResourceBundle();
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
		return resourceBundle;
	}

	public boolean isDecorateInterestMode() {
		return decorateInterestMode;
	}

	public void setDecorateInterestMode(boolean decorateInterestLevel) {
		this.decorateInterestMode = decorateInterestLevel;
	}

	public List<IMylarUiBridge> getUiBridges() {
		return new ArrayList<IMylarUiBridge>(bridges.values());
	}

	/**
	 * @return the corresponding adapter if found, or an adapter with no
	 *         behavior otherwise (so null is never returned)
	 */
	public IMylarUiBridge getUiBridge(String contentType) {
		if (!UiExtensionPointReader.extensionsRead)
			UiExtensionPointReader.initExtensions();
		IMylarUiBridge bridge = bridges.get(contentType);
		if (bridge != null) {
			return bridge;
		} else {
			return DEFAULT_UI_BRIDGE;
		}
	}

	/**
	 * TODO: cache this to improve performance?
	 */
	public IMylarUiBridge getUiBridgeForEditor(IEditorPart editorPart) {
		if (!UiExtensionPointReader.extensionsRead)
			UiExtensionPointReader.initExtensions();
		IMylarUiBridge foundBridge = null;
		for (IMylarUiBridge bridge : bridges.values()) {
			if (bridge.acceptsEditor(editorPart)) {
				foundBridge = bridge;
				break;
			}
		}
		if (foundBridge != null) {
			return foundBridge;
		} else {
			return DEFAULT_UI_BRIDGE;
		}
	}

	private void internalAddBridge(String extension, IMylarUiBridge bridge) {
		this.bridges.put(extension, bridge);
	}

	public ILabelProvider getContextLabelProvider(String extension) {
		if (!UiExtensionPointReader.extensionsRead)
			UiExtensionPointReader.initExtensions();
		ILabelProvider provider = contextLabelProviders.get(extension);
		if (provider != null) {
			return provider;
		} else {
			return DEFAULT_LABEL_PROVIDER;
		}
	}

	private void internalAddContextLabelProvider(String extension, ILabelProvider provider) {
		this.contextLabelProviders.put(extension, provider);
	}

	public void updateGammaSetting(ColorMap.GammaSetting setting) {
		if (colorMap.getGammaSetting() != setting) {
			highlighters.updateHighlighterWithGamma(colorMap.getGammaSetting(), setting);
			colorMap.setGammaSetting(setting);
		}
	}

	public ColorMap getColorMap() {
		return colorMap;
	}

	public Highlighter getDefaultHighlighter() {
		return HighlighterList.DEFAULT_HIGHLIGHTER;
	}

	/**
	 * @return null if not found
	 */
	public Highlighter getHighlighter(String name) {
		if (highlighters == null) {
			this.initializeHighlighters();
		}
		return highlighters.getHighlighter(name);
	}

	public Highlighter getHighlighterForContextId(String id) {
		String prefId = MylarUiPrefContstants.TASK_HIGHLIGHTER_PREFIX + id;
		String highlighterName = getPrefs().getString(prefId);
		return getHighlighter(highlighterName);
	}

	public HighlighterList getHighlighterList() {
		if (this.highlighters == null) {
			this.initializeHighlighters();
		}
		return this.highlighters;
	}

	public List<Highlighter> getHighlighters() {
		if (highlighters == null) {
			this.initializeHighlighters();
		}
		return highlighters.getHighlighters();
	}

	public Highlighter getIntersectionHighlighter() {
		return intersectionHighlighter;
	}

	public void setColorMap(ColorMap colorMap) {
		this.colorMap = colorMap;
	}

	public void setIntersectionHighlighter(Highlighter intersectionHighlighter) {
		this.intersectionHighlighter = intersectionHighlighter;
	}

	public boolean isGlobalFoldingEnabled() {
		return getPrefs().getBoolean(MylarUiPrefContstants.GLOBAL_FILTERING);
	}

	public void setGlobalFilteringEnabled(boolean globalFilteringEnabled) {
		getPrefs().setValue(MylarUiPrefContstants.GLOBAL_FILTERING, globalFilteringEnabled);
	}

	public boolean isIntersectionMode() {
		return getPrefs().getBoolean(MylarUiPrefContstants.INTERSECTION_MODE);
	}

	public void setIntersectionMode(boolean isIntersectionMode) {
		getPrefs().setValue(MylarUiPrefContstants.INTERSECTION_MODE, isIntersectionMode);
	}

	public MylarViewerManager getViewerManager() {
		return viewerManager;
	}

	static class UiExtensionPointReader {

		private static boolean extensionsRead = false;

		private static UiExtensionPointReader thisReader = new UiExtensionPointReader();

		// read the extensions and load the required plugins
		public static void initExtensions() {
			// code from "contributing to eclipse" with modifications for
			// deprecated code
			if (!extensionsRead) {
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IExtensionPoint extensionPoint = registry
						.getExtensionPoint(UiExtensionPointReader.EXTENSION_ID_CONTEXT);
				IExtension[] extensions = extensionPoint.getExtensions();
				for (int i = 0; i < extensions.length; i++) {
					IConfigurationElement[] elements = extensions[i].getConfigurationElements();
					for (int j = 0; j < elements.length; j++) {
						if (elements[j].getName().compareTo(UiExtensionPointReader.ELEMENT_UI_BRIDGE) == 0) {
							readBridge(elements[j]);
						} else if (elements[j].getName().compareTo(
								UiExtensionPointReader.ELEMENT_UI_CONTEXT_LABEL_PROVIDER) == 0) {
							readLabelProvider(elements[j]);
						}
					}
				}
				extensionsRead = true;
			}
		}

		private static void readLabelProvider(IConfigurationElement element) {
			try {
				Object provider = element.createExecutableExtension(UiExtensionPointReader.ELEMENT_UI_CLASS);
				Object contentType = element.getAttribute(UiExtensionPointReader.ELEMENT_UI_BRIDGE_CONTENT_TYPE);
				if (provider instanceof ILabelProvider && contentType != null) {
					MylarUiPlugin.getDefault().internalAddContextLabelProvider((String) contentType,
							(ILabelProvider) provider);
				} else {
					MylarStatusHandler.log("Could not load label provider: " + provider.getClass().getCanonicalName()
							+ " must implement " + ILabelProvider.class.getCanonicalName(), thisReader);
				}
			} catch (CoreException e) {
				MylarStatusHandler.log(e, "Could not load label provider extension");
			}
		}

		private static void readBridge(IConfigurationElement element) {
			try {
				Object bridge = element.createExecutableExtension(UiExtensionPointReader.ELEMENT_UI_CLASS);
				Object contentType = element.getAttribute(UiExtensionPointReader.ELEMENT_UI_BRIDGE_CONTENT_TYPE);
				if (bridge instanceof IMylarUiBridge && contentType != null) {
					MylarUiPlugin.getDefault().internalAddBridge((String) contentType, (IMylarUiBridge) bridge);
				} else {
					MylarStatusHandler.log("Could not load bridge: " + bridge.getClass().getCanonicalName()
							+ " must implement " + IMylarUiBridge.class.getCanonicalName(), thisReader);
				}
			} catch (CoreException e) {
				MylarStatusHandler.log(e, "Could not load bridge extension");
			}
		}

		public static final String EXTENSION_ID_CONTEXT = "org.eclipse.mylar.ui.context";

		public static final String ELEMENT_UI_BRIDGE = "uiBridge";

		public static final String ELEMENT_UI_CLASS = "class";

		public static final String ELEMENT_UI_CONTEXT_LABEL_PROVIDER = "labelProvider";

		public static final String ELEMENT_UI_BRIDGE_CONTENT_TYPE = "contentType";
	}
}
