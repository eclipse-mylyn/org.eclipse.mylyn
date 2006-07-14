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

package org.eclipse.mylar.context.ui;

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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.context.core.AbstractRelationProvider;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.context.core.IMylarRelation;
import org.eclipse.mylar.context.core.IMylarStructureBridge;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.context.ui.AbstractContextLabelProvider;
import org.eclipse.mylar.internal.context.ui.ColorMap;
import org.eclipse.mylar.internal.context.ui.ContentOutlineManager;
import org.eclipse.mylar.internal.context.ui.Highlighter;
import org.eclipse.mylar.internal.context.ui.HighlighterList;
import org.eclipse.mylar.internal.context.ui.MylarPerspectiveManager;
import org.eclipse.mylar.internal.context.ui.MylarUiPrefContstants;
import org.eclipse.mylar.internal.context.ui.MylarViewerManager;
import org.eclipse.mylar.internal.context.ui.MylarWorkingSetManager;
import org.eclipse.mylar.internal.tasks.ui.ui.ITaskHighlighter;
import org.eclipse.mylar.monitor.MylarMonitorPlugin;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class ContextUiPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylar.ui";

	private Map<String, IMylarUiBridge> bridges = new HashMap<String, IMylarUiBridge>();

	private Map<String, ILabelProvider> contextLabelProviders = new HashMap<String, ILabelProvider>();

	private static ContextUiPlugin plugin;

	private ResourceBundle resourceBundle;

	private boolean decorateInterestMode = false;

	private HighlighterList highlighters = null;

	private Highlighter intersectionHighlighter;

	private ColorMap colorMap = new ColorMap();

	private MylarViewerManager viewerManager;

	private MylarPerspectiveManager perspectiveManager = new MylarPerspectiveManager();
	
	private ContentOutlineManager contentOutlineManager = new ContentOutlineManager();
	
	private List<MylarWorkingSetManager> workingSetUpdaters = null;
	
	private Map<IMylarUiBridge, ImageDescriptor> activeSearchIcons = new HashMap<IMylarUiBridge, ImageDescriptor>();

	private Map<IMylarUiBridge, String> activeSearchLabels = new HashMap<IMylarUiBridge, String>();
	
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

		public IMylarElement getElement(IEditorInput input) {
			return null;
		}

		public String getContentType() {
			return null;
		}
	};

	public ContextUiPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.eclipse.mylar.MylarUiPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		} catch (Throwable t) {
			MylarStatusHandler.log(t, "plug-in intialization failed");
		}
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initializeDefaultPreferences(getPreferenceStore());
		initializeHighlighters();
		initializeActions();
		
		UiExtensionPointReader.initExtensions();
		
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					viewerManager = new MylarViewerManager();
					ContextCorePlugin.getContextManager().addListener(viewerManager);
					TasksUiPlugin.getTaskListManager().addActivityListener(perspectiveManager);

					MylarMonitorPlugin.getDefault().addWindowPartListener(contentOutlineManager);
					TasksUiPlugin.getDefault().setHighlighter(DEFAULT_HIGHLIGHTER);
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
			ContextCorePlugin.getContextManager().removeListener(viewerManager);
			MylarMonitorPlugin.getDefault().removeWindowPartListener(contentOutlineManager);
//			ContextCorePlugin.getDefault().removeWindowPageListener(contentOutlineManager);
			viewerManager.dispose();
			colorMap.dispose();
			highlighters.dispose();
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Mylar UI stop failed", false);
		}
	}

	private void initializeActions() {
		// don't have any actions to initialize
	}

	private void initializeHighlighters() {
		String hlist = getPreferenceStore().getString(MylarUiPrefContstants.HIGHLIGHTER_PREFIX);
		if (hlist != null && hlist.length() != 0) {
			highlighters = new HighlighterList(hlist);
		} else {
			// Only get here if it is the first time running
			// mylar. load default colors
			highlighters = new HighlighterList();
			highlighters.setToDefaultList();
			getPreferenceStore().setValue(MylarUiPrefContstants.HIGHLIGHTER_PREFIX, this.highlighters.externalizeToString());
		}
	}

	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(MylarUiPrefContstants.AUTO_MANAGE_PERSPECTIVES, false);
		store.setDefault(MylarUiPrefContstants.AUTO_MANAGE_EDITORS, true);
		store.setDefault(MylarUiPrefContstants.AUTO_MANAGE_EDITORS_OPEN_NUM, 8);

		store.setDefault(MylarUiPrefContstants.GAMMA_SETTING_LIGHTENED, false);
		store.setDefault(MylarUiPrefContstants.GAMMA_SETTING_STANDARD, true);
		store.setDefault(MylarUiPrefContstants.GAMMA_SETTING_DARKENED, false);
	}

	public void setHighlighterMapping(String id, String name) {
		String prefId = MylarUiPrefContstants.TASK_HIGHLIGHTER_PREFIX + id;
		getPreferenceStore().putValue(prefId, name);
	}

	/**
	 * Returns the shared instance.
	 */
	public static ContextUiPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = ContextUiPlugin.getDefault().getResourceBundle();
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
//		if (!UiExtensionPointReader.extensionsRead)
//			UiExtensionPointReader.initExtensions();
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
//		if (!UiExtensionPointReader.extensionsRead)
//			UiExtensionPointReader.initExtensions();
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
//		if (!UiExtensionPointReader.extensionsRead)
//			UiExtensionPointReader.initExtensions();
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
		String highlighterName = getPreferenceStore().getString(prefId);
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

	public boolean isIntersectionMode() {
		return getPreferenceStore().getBoolean(MylarUiPrefContstants.INTERSECTION_MODE);
	}

	public void setIntersectionMode(boolean isIntersectionMode) {
		getPreferenceStore().setValue(MylarUiPrefContstants.INTERSECTION_MODE, isIntersectionMode);
	}

	public MylarViewerManager getViewerManager() {
		return viewerManager;
	}

	static class UiExtensionPointReader {

		private static boolean extensionsRead = false;

		private static UiExtensionPointReader thisReader = new UiExtensionPointReader();

		public static final String ELEMENT_STRUCTURE_BRIDGE_SEARCH_ICON = "activeSearchIcon";

		public static final String ELEMENT_STRUCTURE_BRIDGE_SEARCH_LABEL = "activeSearchLabel";
		
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
					ContextUiPlugin.getDefault().internalAddContextLabelProvider((String) contentType,
							(ILabelProvider) provider);
				} else {
					MylarStatusHandler.log("Could not load label provider: " + provider.getClass().getCanonicalName()
							+ " must implement " + ILabelProvider.class.getCanonicalName(), thisReader);
				}
			} catch (CoreException e) {
				MylarStatusHandler.log(e, "Could not load label provider extension");
			}
		}

		@SuppressWarnings("deprecation")
		private static void readBridge(IConfigurationElement element) {
			try {
				Object bridge = element.createExecutableExtension(UiExtensionPointReader.ELEMENT_UI_CLASS);
				Object contentType = element.getAttribute(UiExtensionPointReader.ELEMENT_UI_BRIDGE_CONTENT_TYPE);
				if (bridge instanceof IMylarUiBridge && contentType != null) {
					ContextUiPlugin.getDefault().internalAddBridge((String) contentType, (IMylarUiBridge) bridge);
				
					String iconPath = element.getAttribute(ELEMENT_STRUCTURE_BRIDGE_SEARCH_ICON);
					if (iconPath != null) {
						ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(element.getDeclaringExtension().getNamespace(), iconPath);
						if (descriptor != null) {
							ContextUiPlugin.getDefault().setActiveSearchIcon((IMylarUiBridge)bridge, descriptor);
						}
					}
					String label = element.getAttribute(ELEMENT_STRUCTURE_BRIDGE_SEARCH_LABEL);
					if (label != null) {
						ContextUiPlugin.getDefault().setActiveSearchLabel((IMylarUiBridge)bridge, label);
					}
				
				} else {
					MylarStatusHandler.log("Could not load bridge: " + bridge.getClass().getCanonicalName()
							+ " must implement " + IMylarUiBridge.class.getCanonicalName(), thisReader);
				}
			} catch (CoreException e) {
				MylarStatusHandler.log(e, "Could not load bridge extension");
			}
		}

		public static final String EXTENSION_ID_CONTEXT = "org.eclipse.mylar.context.ui.bridges";

		public static final String ELEMENT_UI_BRIDGE = "uiBridge";

		public static final String ELEMENT_UI_CLASS = "class";

		public static final String ELEMENT_UI_CONTEXT_LABEL_PROVIDER = "labelProvider";

		public static final String ELEMENT_UI_BRIDGE_CONTENT_TYPE = "contentType";
	}
	

	public String getPerspectiveIdFor(ITask task) {
		return getPreferenceStore().getString(MylarUiPrefContstants.PREFIX_TASK_TO_PERSPECTIVE + task.getHandleIdentifier());
	}

	public void setPerspectiveIdFor(ITask task, String perspectiveId) {
		getPreferenceStore().setValue(MylarUiPrefContstants.PREFIX_TASK_TO_PERSPECTIVE + task.getHandleIdentifier(), perspectiveId);
	}
	
	public void addWorkingSetManager(MylarWorkingSetManager updater) {
		if (workingSetUpdaters == null) {
			workingSetUpdaters = new ArrayList<MylarWorkingSetManager>();
		}
		workingSetUpdaters.add(updater);
		ContextCorePlugin.getContextManager().addListener(updater);
	}

	public MylarWorkingSetManager getWorkingSetUpdater() {
		if (workingSetUpdaters == null)
			return null;
		else
			return workingSetUpdaters.get(0);
	}

	private void setActiveSearchIcon(IMylarUiBridge bridge, ImageDescriptor descriptor) {
		activeSearchIcons.put(bridge, descriptor);
	}

	public ImageDescriptor getActiveSearchIcon(IMylarUiBridge bridge) {
//		if (!CoreExtensionPointReader.extensionsRead)
//			CoreExtensionPointReader.initExtensions();
		return activeSearchIcons.get(bridge);
	}

	private void setActiveSearchLabel(IMylarUiBridge bridge, String label) {
		activeSearchLabels.put(bridge, label);
	}

	public String getActiveSearchLabel(IMylarUiBridge bridge) {
//		if (!CoreExtensionPointReader.extensionsRead)
//			CoreExtensionPointReader.initExtensions();
		return activeSearchLabels.get(bridge);
	}

	public void updateDegreesOfSeparation(List<AbstractRelationProvider> providers, int degreeOfSeparation) {
		for (AbstractRelationProvider provider : providers) {
			updateDegreeOfSeparation(provider, degreeOfSeparation);
		}
	}
	
	public void updateDegreeOfSeparation(AbstractRelationProvider provider, int degreeOfSeparation) {
		ContextCorePlugin.getContextManager().resetLandmarkRelationshipsOfKind(provider.getId());
		ContextUiPlugin.getDefault().getPreferenceStore().setValue(provider.getGenericId(), degreeOfSeparation);
		provider.setDegreeOfSeparation(degreeOfSeparation);
		for (IMylarElement element : ContextCorePlugin.getContextManager().getActiveContext().getInteresting()) {
			if (element.getInterest().isLandmark()) {
				provider.landmarkAdded(element);
			}
 		}
	}
	
	public void refreshRelatedElements() {
		try {
			for (IMylarStructureBridge bridge : ContextCorePlugin.getDefault().getStructureBridges().values()) {
				if (bridge.getRelationshipProviders() != null) {
					for (AbstractRelationProvider provider : bridge.getRelationshipProviders()) {
						List<AbstractRelationProvider> providerList = new ArrayList<AbstractRelationProvider>();
						providerList.add(provider);
						updateDegreesOfSeparation(providerList, provider.getCurrentDegreeOfSeparation());
					}
				}
			}
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "Could not refresn related elements", false);
		}
	}
	
}
