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
package org.eclipse.mylar.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.internal.core.MylarContextManager;
import org.eclipse.mylar.internal.core.MylarPreferenceContstants;
import org.eclipse.mylar.internal.core.search.MylarWorkingSetUpdater;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylar.core";

	public static final String CONTENT_TYPE_ANY = "*";

	private static final String NAME_DATA_DIR = ".mylar";

	private Map<String, IMylarStructureBridge> bridges = new HashMap<String, IMylarStructureBridge>();

	private Map<IMylarStructureBridge, ImageDescriptor> activeSearchIcons = new HashMap<IMylarStructureBridge, ImageDescriptor>();

	private Map<IMylarStructureBridge, String> activeSearchLabels = new HashMap<IMylarStructureBridge, String>();

	private IMylarStructureBridge defaultBridge = null;

	private List<AbstractUserInteractionMonitor> selectionMonitors = new ArrayList<AbstractUserInteractionMonitor>();

	private List<AbstractCommandMonitor> commandMonitors = new ArrayList<AbstractCommandMonitor>();

	private List<MylarWorkingSetUpdater> workingSetUpdaters = null;

	/**
	 * TODO: this could be merged with context interaction events rather than
	 * requiring update from the monitor.
	 */
	private List<IInteractionEventListener> interactionListeners = new ArrayList<IInteractionEventListener>();

	private static MylarPlugin INSTANCE;

	private static MylarContextManager contextManager;

	private ResourceBundle resourceBundle;

	private static final IMylarStructureBridge DEFAULT_BRIDGE = new IMylarStructureBridge() {

		public String getContentType() {
			return null;
		}

		public String getHandleIdentifier(Object object) {
			throw new RuntimeException("null bridge for object: " + object.getClass());
		}

		public Object getObjectForHandle(String handle) {
			MylarStatusHandler.log("null bridge for handle: " + handle, this);
			return null;
		}

		public String getParentHandle(String handle) {
			MylarStatusHandler.log("null bridge for handle: " + handle, this);
			return null;
		}

		public String getName(Object object) {
			MylarStatusHandler.log("null bridge for object: " + object.getClass(), this);
			return "";
		}

		public boolean canBeLandmark(String handle) {
			return false;
		}

		public boolean acceptsObject(Object object) {
			throw new RuntimeException("null bridge for object: " + object.getClass());
		}

		public boolean canFilter(Object element) {
			return true;
		}

		public boolean isDocument(String handle) {
			// return false;
			throw new RuntimeException("null adapter for handle: " + handle);
		}

		public IProject getProjectForObject(Object object) {
			// return null;
			throw new RuntimeException("null brige for object: " + object);
		}

		public String getContentType(String elementHandle) {
			return getContentType();
		}

		public List<AbstractRelationProvider> getRelationshipProviders() {
			return Collections.emptyList();
		}

		public List<IDegreeOfSeparation> getDegreesOfSeparation() {
			return Collections.emptyList();
		}

		public String getHandleForOffsetInObject(Object resource, int offset) {
			MylarStatusHandler.log("null bridge for marker: " + resource.getClass(), this);
			return null;
		}

		public void setParentBridge(IMylarStructureBridge bridge) {
			// ignore
		}

		public List<String> getChildHandles(String handle) {
			return Collections.emptyList();
		}
	};

	public MylarPlugin() {
		INSTANCE = this;
	}

	/**
	 * Initialization order is important.
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		getPreferenceStore().setDefault(MylarPreferenceContstants.PREF_DATA_DIR, getDefaultDataDirectory());
		if (contextManager == null)
			contextManager = new MylarContextManager();
	}

	public String getDefaultDataDirectory() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/' + NAME_DATA_DIR;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			super.stop(context);
			INSTANCE = null;
			resourceBundle = null;

			// Stop all running jobs when we exit if the plugin didn't do it
			Map<String, IMylarStructureBridge> bridges = getStructureBridges();
			for (Entry<String, IMylarStructureBridge> entry : bridges.entrySet()) {
				IMylarStructureBridge bridge = entry.getValue();// bridges.get(extension);
				List<AbstractRelationProvider> providers = bridge.getRelationshipProviders();
				if (providers == null)
					continue;
				for (AbstractRelationProvider provider : providers) {
					provider.stopAllRunningJobs();
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Mylar Core stop failed", false);
		}
	}

	public String getDataDirectory() {
		return getPreferenceStore().getString(MylarPreferenceContstants.PREF_DATA_DIR);
	}

	public void setDataDirectory(String newPath) {
		getPreferenceStore().setValue(MylarPreferenceContstants.PREF_DATA_DIR, newPath);
	}

	public static MylarPlugin getDefault() {
		return INSTANCE;
	}

	public static MylarContextManager getContextManager() {
		return contextManager;
	}

	public List<AbstractUserInteractionMonitor> getSelectionMonitors() {
		return selectionMonitors;
	}

	public Map<String, IMylarStructureBridge> getStructureBridges() {
		if (!CoreExtensionPointReader.extensionsRead)
			CoreExtensionPointReader.initExtensions();
		return bridges;
	}

	public IMylarStructureBridge getStructureBridge(String contentType) {
		if (!CoreExtensionPointReader.extensionsRead)
			CoreExtensionPointReader.initExtensions();

		IMylarStructureBridge bridge = bridges.get(contentType);
		if (bridge != null) {
			return bridge;
		} else if (defaultBridge != null) {
			return defaultBridge;
		} else {
			return DEFAULT_BRIDGE;
		}
	}

	public Set<String> getKnownContentTypes() {
		return bridges.keySet();
	}

	private void setActiveSearchIcon(IMylarStructureBridge bridge, ImageDescriptor descriptor) {
		activeSearchIcons.put(bridge, descriptor);
	}

	public ImageDescriptor getActiveSearchIcon(IMylarStructureBridge bridge) {
		if (!CoreExtensionPointReader.extensionsRead)
			CoreExtensionPointReader.initExtensions();
		return activeSearchIcons.get(bridge);
	}

	private void setActiveSearchLabel(IMylarStructureBridge bridge, String label) {
		activeSearchLabels.put(bridge, label);
	}

	public String getActiveSearchLabel(IMylarStructureBridge bridge) {
		if (!CoreExtensionPointReader.extensionsRead)
			CoreExtensionPointReader.initExtensions();
		return activeSearchLabels.get(bridge);
	}

	/**
	 * TODO: cache this to improve performance?
	 * 
	 * @return null if there are no bridges loaded, null bridge otherwise
	 */
	public IMylarStructureBridge getStructureBridge(Object object) {
		if (!CoreExtensionPointReader.extensionsRead)
			CoreExtensionPointReader.initExtensions();

		IMylarStructureBridge bridge = null;
		// if (bridges.size() == 0) {
		// MylarStatusHandler.log("no bridges installed", this);
		// return DEFAULT_BRIDGE;
		// }
		for (IMylarStructureBridge structureBridge : bridges.values()) {
			if (structureBridge.acceptsObject(object)) {
				bridge = structureBridge;
				break;
			}
		}
		if (bridge != null) {
			return bridge;
		} else {
			if (defaultBridge != null && defaultBridge.acceptsObject(object)) {
				return defaultBridge;
			} else {
				return DEFAULT_BRIDGE;
			}
		}
	}

	private void internalAddBridge(IMylarStructureBridge bridge) {
		// MylarStatusHandler.log("> adding: " + bridge.getClass(), this);
		if (bridge.getRelationshipProviders() != null) {
			for (AbstractRelationProvider provider : bridge.getRelationshipProviders()) {
				getContextManager().addListener(provider);
			}
		}
		if (bridge.getContentType().equals(CONTENT_TYPE_ANY)) {
			defaultBridge = bridge;
		} else {
			bridges.put(bridge.getContentType(), bridge);
		}
	}

	public List<AbstractCommandMonitor> getCommandMonitors() {
		return commandMonitors;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = MylarPlugin.getDefault().getResourceBundle();
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
				resourceBundle = ResourceBundle.getBundle("org.eclipse.mylar.core.MylarPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	public boolean suppressWizardsOnStartup() {
		List<String> commandLineArgs = Arrays.asList(Platform.getCommandLineArgs());
		if (commandLineArgs.contains("-showmylarwizards")) {
			return false;
		} else {
			return commandLineArgs.contains("-pdelaunch");
		}
	}

	/**
	 * TODO: remove
	 */
	public void setDefaultBridge(IMylarStructureBridge defaultBridge) {
		this.defaultBridge = defaultBridge;
	}

	public void addInteractionListener(IInteractionEventListener listener) {
		interactionListeners.add(listener);
	}

	public void removeInteractionListener(IInteractionEventListener listener) {
		interactionListeners.remove(listener);
	}

	/**
	 * TODO: refactor this, it's awkward
	 */
	public void notifyInteractionObserved(InteractionEvent interactionEvent) {
		for (IInteractionEventListener listener : interactionListeners) {
			listener.interactionObserved(interactionEvent);
		}
	}

	public List<IInteractionEventListener> getInteractionListeners() {
		return interactionListeners;
	}

	public void addWorkingSetUpdater(MylarWorkingSetUpdater updater) {
		if (workingSetUpdaters == null)
			workingSetUpdaters = new ArrayList<MylarWorkingSetUpdater>();
		workingSetUpdaters.add(updater);
		MylarPlugin.getContextManager().addListener(updater);
	}

	public MylarWorkingSetUpdater getWorkingSetUpdater() {
		if (workingSetUpdaters == null)
			return null;
		else
			return workingSetUpdaters.get(0);
	}

	static class CoreExtensionPointReader {

		private static boolean extensionsRead = false;

		public static void initExtensions() {
			// based on Contributing to Eclipse
			// MylarStatusHandler.log("reading extensions", null);
			if (!extensionsRead) {
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IExtensionPoint extensionPoint = registry
						.getExtensionPoint(CoreExtensionPointReader.EXTENSION_ID_CONTEXT);
				IExtension[] extensions = extensionPoint.getExtensions();
				for (int i = 0; i < extensions.length; i++) {
					IConfigurationElement[] elements = extensions[i].getConfigurationElements();
					for (int j = 0; j < elements.length; j++) {
						if (elements[j].getName().compareTo(CoreExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE) == 0) {
							readBridge(elements[j]);
						}
					}
				}
				extensionsRead = true;
			}
		}

		private static void readBridge(IConfigurationElement element) {
			try {
				Object object = element
						.createExecutableExtension(CoreExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE_CLASS);
				// MylarStatusHandler.log("> reading bridge: " +
				// object.getClass(), null);
				if (object instanceof IMylarStructureBridge) {
					IMylarStructureBridge bridge = (IMylarStructureBridge) object;
					MylarPlugin.getDefault().internalAddBridge(bridge);
					if (element.getAttribute(CoreExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE_PARENT) != null) {
						Object parent = element
								.createExecutableExtension(CoreExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE_PARENT);
						if (parent instanceof IMylarStructureBridge) {
							((IMylarStructureBridge) bridge).setParentBridge(((IMylarStructureBridge) parent));
							// ((IMylarStructureBridge)parent).addChildBridge(((IMylarStructureBridge)bridge));
						} else {
							MylarStatusHandler.log("Could not load parent bridge: "
									+ parent.getClass().getCanonicalName() + " must implement "
									+ IMylarStructureBridge.class.getCanonicalName(), null);
						}
					}
					String iconPath = element
							.getAttribute(CoreExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE_SEARCH_ICON);
					if (iconPath != null) {
						ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(element.getNamespace(),
								iconPath);
						if (descriptor != null) {
							MylarPlugin.getDefault().setActiveSearchIcon(bridge, descriptor);
						}
					}
					String label = element.getAttribute(CoreExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE_SEARCH_LABEL);
					if (label != null) {
						MylarPlugin.getDefault().setActiveSearchLabel(bridge, label);
					}
				} else {
					MylarStatusHandler.log("Could not load bridge: " + object.getClass().getCanonicalName()
							+ " must implement " + IMylarStructureBridge.class.getCanonicalName(), null);
				}
			} catch (CoreException e) {
				MylarStatusHandler.log(e, "Could not load bridge extension");
			}
		}

		public static final String EXTENSION_ID_CONTEXT = "org.eclipse.mylar.core.context";

		public static final String ELEMENT_STRUCTURE_BRIDGE = "structureBridge";

		public static final String ELEMENT_STRUCTURE_BRIDGE_CLASS = "class";

		public static final String ELEMENT_STRUCTURE_BRIDGE_PARENT = "parent";

		public static final String ELEMENT_STRUCTURE_BRIDGE_SEARCH_ICON = "activeSearchIcon";

		public static final String ELEMENT_STRUCTURE_BRIDGE_SEARCH_LABEL = "activeSearchLabel";
	}
}
