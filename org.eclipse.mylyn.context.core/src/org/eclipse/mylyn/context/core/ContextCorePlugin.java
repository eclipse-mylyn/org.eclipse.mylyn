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
package org.eclipse.mylar.context.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.mylar.core.IStatusHandler;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.context.core.MylarContextManager;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class ContextCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "org.eclipse.mylar.core";

	public static final String CONTENT_TYPE_ANY = "*";

	private Map<String, AbstractContextStructureBridge> bridges = new HashMap<String, AbstractContextStructureBridge>();

	private Map<String, List<String>> childContentTypeMap = new HashMap<String, List<String>>();
	
	private AbstractContextStructureBridge defaultBridge = null;

	private static ContextCorePlugin INSTANCE;

	private static MylarContextManager contextManager;

	private static AbstractContextStore contextStore;

	private boolean extensionsLoaded = false;

	private static final AbstractContextStructureBridge DEFAULT_BRIDGE = new AbstractContextStructureBridge() {

		@Override
		public String getContentType() {
			return null;
		}

		@Override
		public String getHandleIdentifier(Object object) {
			return null;
//			throw new RuntimeException("null bridge for object: " + object.getClass());
		}

		@Override
		public Object getObjectForHandle(String handle) {
//			MylarStatusHandler.log("null bridge for handle: " + handle, this);
			return null;
		}

		@Override
		public String getParentHandle(String handle) {
//			MylarStatusHandler.log("null bridge for handle: " + handle, this);
			return null;
		}

		@Override
		public String getName(Object object) {
//			MylarStatusHandler.log("null bridge for object: " + object.getClass(), this);
			return "";
		}

		@Override
		public boolean canBeLandmark(String handle) {
			return false;
		}

		@Override
		public boolean acceptsObject(Object object) {
			return false;
//			throw new RuntimeException("null bridge for object: " + object.getClass());
		}

		@Override
		public boolean canFilter(Object element) {
			return true;
		}

		@Override
		public boolean isDocument(String handle) {
			 return false;
//			throw new RuntimeException("null adapter for handle: " + handle);
		}

		@Override
		public String getContentType(String elementHandle) {
			return getContentType();
		}

		@Override
		public List<AbstractRelationProvider> getRelationshipProviders() {
			return Collections.emptyList();
		}

		@Override
		public List<IDegreeOfSeparation> getDegreesOfSeparation() {
			return Collections.emptyList();
		}

		@Override
		public String getHandleForOffsetInObject(Object resource, int offset) {
//			MylarStatusHandler.log("null bridge for marker: " + resource.getClass(), this);
			return null;
		}

		@Override
		public List<String> getChildHandles(String handle) {
			return Collections.emptyList();
		}
	};

	public ContextCorePlugin() {
		INSTANCE = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		contextManager = new MylarContextManager();
		lazyLoadExtensions();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			super.stop(context);
			INSTANCE = null;
			// resourceBundle = null;

			// Stop all running jobs when we exit if the plugin didn't do it
			Map<String, AbstractContextStructureBridge> bridges = getStructureBridges();
			for (Entry<String, AbstractContextStructureBridge> entry : bridges.entrySet()) {
				AbstractContextStructureBridge bridge = entry.getValue();// bridges.get(extension);
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

	private void lazyLoadExtensions() {
		if (!extensionsLoaded) {
			ContextStoreExtensionReader.initExtensions();
			HandlersExtensionPointReader.initExtensions();

			for (AbstractContextStructureBridge bridge : bridges.values()) {
				if (bridge.getRelationshipProviders() != null) {
					for (AbstractRelationProvider provider : bridge.getRelationshipProviders()) {
						getContextManager().addListener(provider);
					}
				}
			}

			extensionsLoaded = true;
		}
	}

	public static ContextCorePlugin getDefault() {
		return INSTANCE;
	}

	public static MylarContextManager getContextManager() {
		return contextManager;
	}

	public Map<String, AbstractContextStructureBridge> getStructureBridges() {
		BridgesExtensionPointReader.initExtensions();
		return bridges;
	}

	public AbstractContextStructureBridge getStructureBridge(String contentType) {
		BridgesExtensionPointReader.initExtensions();
		AbstractContextStructureBridge bridge = bridges.get(contentType);
		if (bridge != null) {
			return bridge;
		}
		return (defaultBridge == null) ? DEFAULT_BRIDGE : defaultBridge;
	}

	public Set<String> getKnownContentTypes() {
		BridgesExtensionPointReader.initExtensions();
		return bridges.keySet();
	}

	/**
	 * TODO: cache this to improve performance?
	 * 
	 * @return null if there are no bridges loaded, null bridge otherwise
	 */
	public AbstractContextStructureBridge getStructureBridge(Object object) {
		BridgesExtensionPointReader.initExtensions();
		for (AbstractContextStructureBridge structureBridge : bridges.values()) {
			if (structureBridge.acceptsObject(object)) {
				return structureBridge;
			}
		}

		// use the default if not found
		return (defaultBridge != null && defaultBridge.acceptsObject(object)) ? defaultBridge : DEFAULT_BRIDGE;
	}

	private void internalAddBridge(AbstractContextStructureBridge bridge) {
		if (bridge.getContentType().equals(CONTENT_TYPE_ANY)) {
			defaultBridge = bridge;
		} else {
			bridges.put(bridge.getContentType(), bridge);
		}
		if (bridge.getParentContentType() != null) {
			List<String> childContentTypes = childContentTypeMap.get(bridge.getParentContentType());
			if (childContentTypes == null) {
				childContentTypes = new ArrayList<String>();
			}
			childContentTypes.add(bridge.getContentType());
			childContentTypeMap.put(bridge.getParentContentType(), childContentTypes);
		}
	}

	public AbstractContextStore getContextStore() {
		return contextStore;
	}

	public static void setContextStore(AbstractContextStore contextStore) {
		ContextCorePlugin.contextStore = contextStore;
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
	public void setDefaultBridge(AbstractContextStructureBridge defaultBridge) {
		this.defaultBridge = defaultBridge;
	}

	static class ContextStoreExtensionReader {

		private static final String ELEMENT_CONTEXT_STORE = "contextStore";

		private static boolean extensionsRead = false;

		public static void initExtensions() {
			if (!extensionsRead) {
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IExtensionPoint extensionPoint = registry
						.getExtensionPoint(BridgesExtensionPointReader.EXTENSION_ID_CONTEXT);
				IExtension[] extensions = extensionPoint.getExtensions();
				for (int i = 0; i < extensions.length; i++) {
					IConfigurationElement[] elements = extensions[i].getConfigurationElements();
					for (int j = 0; j < elements.length; j++) {
						if (elements[j].getName().compareTo(ELEMENT_CONTEXT_STORE) == 0) {
							readStore(elements[j]);
						}
					}
				}
				extensionsRead = true;
			}
		}

		private static void readStore(IConfigurationElement element) {
			// Currently disabled
//			try {
//				Object object = element.createExecutableExtension(BridgesExtensionPointReader.ELEMENT_CLASS);
//
//				if (!(object instanceof AbstractContextStore)) {
//					MylarStatusHandler.log("Could not load bridge: " + object.getClass().getCanonicalName()
//							+ " must implement " + AbstractContextStructureBridge.class.getCanonicalName(), null);
//					return;
//				} else {
//					contextStore = (AbstractContextStore) object;
//				}
//			} catch (CoreException e) {
//				MylarStatusHandler.log(e, "Could not load bridge extension");
//			}
		}
	}

	static class BridgesExtensionPointReader {

		private static final String EXTENSION_ID_CONTEXT = "org.eclipse.mylar.context.core.bridges";

		private static final String ELEMENT_STRUCTURE_BRIDGE = "structureBridge";

		private static final String ELEMENT_CLASS = "class";

		private static final String ELEMENT_STRUCTURE_BRIDGE_PARENT = "parentContentType";

		private static boolean extensionsRead = false;

		public static void initExtensions() {
			if (!extensionsRead) {
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IExtensionPoint extensionPoint = registry
						.getExtensionPoint(BridgesExtensionPointReader.EXTENSION_ID_CONTEXT);
				IExtension[] extensions = extensionPoint.getExtensions();
				for (int i = 0; i < extensions.length; i++) {
					IConfigurationElement[] elements = extensions[i].getConfigurationElements();
					for (int j = 0; j < elements.length; j++) {
						if (elements[j].getName().compareTo(BridgesExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE) == 0) {
							readBridge(elements[j]);
						}
					}
				}
				extensionsRead = true;
			}
		}

		@SuppressWarnings("deprecation")
		private static void readBridge(IConfigurationElement element) {
			try {
				Object object = element.createExecutableExtension(BridgesExtensionPointReader.ELEMENT_CLASS);
				if (!(object instanceof AbstractContextStructureBridge)) {
					MylarStatusHandler.log("Could not load bridge: " + object.getClass().getCanonicalName()
							+ " must implement " + AbstractContextStructureBridge.class.getCanonicalName(), null);
					return;
				}

				AbstractContextStructureBridge bridge = (AbstractContextStructureBridge) object;
				if (element.getAttribute(BridgesExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE_PARENT) != null) {
					String parentContentType = element
							.getAttribute(BridgesExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE_PARENT);
					if (parentContentType instanceof String) {
						bridge.setParentContentType(parentContentType);
					}
					// Object parent = element
					// .createExecutableExtension(BridgesExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE_PARENT);
					// if (parent instanceof AbstractContextStructureBridge) {
					// (bridge).setParentBridge(((AbstractContextStructureBridge)
					// parent));
					// } else {
					// MylarStatusHandler.log("Could not load parent bridge: "
					// + parent.getClass().getCanonicalName() + " must implement
					// "
					// +
					// AbstractContextStructureBridge.class.getCanonicalName(),
					// null);
					// }
				}
				ContextCorePlugin.getDefault().internalAddBridge(bridge);
			} catch (CoreException e) {
				MylarStatusHandler.log(e, "Could not load bridge extension");
			}
		}
	}

	static class HandlersExtensionPointReader {

		private static final String EXTENSION_ID_HANDLERS = "org.eclipse.mylar.context.core.handlers";

		private static final String ELEMENT_STATUS = "status";

		private static final String ELEMENT_CLASS = "class";

		private static boolean extensionsRead = false;

		public static void initExtensions() {
			if (!extensionsRead) {
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_ID_HANDLERS);
				IExtension[] extensions = extensionPoint.getExtensions();
				for (int i = 0; i < extensions.length; i++) {
					IConfigurationElement[] elements = extensions[i].getConfigurationElements();
					for (int j = 0; j < elements.length; j++) {
						if (elements[j].getName().compareTo(ELEMENT_STATUS) == 0) {
							readHandler(elements[j]);
						}
					}
				}
				extensionsRead = true;
			}
		}

		@SuppressWarnings("deprecation")
		private static void readHandler(IConfigurationElement element) {
			try {
				Object object = element.createExecutableExtension(ELEMENT_CLASS);
				if (!(object instanceof IStatusHandler)) {
					MylarStatusHandler.log("Could not load handler: " + object.getClass().getCanonicalName()
							+ " must implement " + AbstractContextStructureBridge.class.getCanonicalName(), null);
					return;
				}

				IStatusHandler handler = (IStatusHandler) object;
				MylarStatusHandler.addStatusHandler(handler);
			} catch (CoreException e) {
			}
		}
	}

	public List<String> getChildContentTypes(String contentType) {
		List<String> contentTypes = childContentTypeMap.get(contentType);
		if (contentTypes != null) {
			return contentTypes;
		} else {
			return Collections.emptyList();
		}
	}
}
