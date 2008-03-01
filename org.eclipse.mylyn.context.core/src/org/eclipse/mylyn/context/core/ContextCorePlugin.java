/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.context.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.monitor.core.util.IStatusHandler;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.osgi.framework.BundleContext;

/**
 * Main entry point for the Context Core API.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public class ContextCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.core";

	public static final String CONTENT_TYPE_RESOURCE = "resource";

	private final Map<String, AbstractContextStructureBridge> bridges = new ConcurrentHashMap<String, AbstractContextStructureBridge>();

	private final Map<String, List<String>> childContentTypeMap = new ConcurrentHashMap<String, List<String>>();

	private AbstractContextStructureBridge defaultBridge = null;

	private static ContextCorePlugin INSTANCE;

	private InteractionContextManager contextManager;

	private AbstractContextStore contextStore;

	private final Map<String, Set<AbstractRelationProvider>> relationProviders = new HashMap<String, Set<AbstractRelationProvider>>();

	private boolean contextStoreRead = false;

	private static final AbstractContextStructureBridge DEFAULT_BRIDGE = new AbstractContextStructureBridge() {

		@Override
		public String getContentType() {
			return null;
		}

		@Override
		public String getHandleIdentifier(Object object) {
			return null;
		}

		@Override
		public Object getObjectForHandle(String handle) {
			return null;
		}

		@Override
		public String getParentHandle(String handle) {
			return null;
		}

		@Override
		public String getLabel(Object object) {
			return "";
		}

		@Override
		public boolean canBeLandmark(String handle) {
			return false;
		}

		@Override
		public boolean acceptsObject(Object object) {
			return false;
		}

		@Override
		public boolean canFilter(Object element) {
			return true;
		}

		@Override
		public boolean isDocument(String handle) {
			return false;
		}

		@Override
		public String getContentType(String elementHandle) {
			return getContentType();
		}

		@Override
		public String getHandleForOffsetInObject(Object resource, int offset) {
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

		contextManager = new InteractionContextManager();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			super.stop(context);
			INSTANCE = null;
			for (AbstractRelationProvider provider : getRelationProviders()) {
				provider.stopAllRunningJobs();
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, "Mylyn Core stop failed", e));
		}
	}

	private void addRelationProvider(String contentType, AbstractRelationProvider provider) {
		Set<AbstractRelationProvider> providers = relationProviders.get(contentType);
		if (providers == null) {
			providers = new HashSet<AbstractRelationProvider>();
			relationProviders.put(contentType, providers);
		}
		providers.add(provider);
		// TODO: need facility for removing
		getContextManager().addListener(provider);
	}

	/**
	 * @return all relation providers
	 */
	public Set<AbstractRelationProvider> getRelationProviders() {
		Set<AbstractRelationProvider> allProviders = new HashSet<AbstractRelationProvider>();
		for (Set<AbstractRelationProvider> providers : relationProviders.values()) {
			allProviders.addAll(providers);
		}
		return allProviders;
	}

	public Set<AbstractRelationProvider> getRelationProviders(String contentType) {
		return relationProviders.get(contentType);
	}

	public static ContextCorePlugin getDefault() {
		return INSTANCE;
	}

	public static InteractionContextManager getContextManager() {
		return INSTANCE.contextManager;
	}

	public Map<String, AbstractContextStructureBridge> getStructureBridges() {
		BridgesExtensionPointReader.initExtensions();
		return bridges;
	}

	public AbstractContextStructureBridge getStructureBridge(String contentType) {
		BridgesExtensionPointReader.initExtensions();
		if (contentType != null) {
			AbstractContextStructureBridge bridge = bridges.get(contentType);
			if (bridge != null) {
				return bridge;
			}
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

	/**
	 * Recommended bridge registration is via extension point, but bridges can also be added at runtime. Note that only
	 * one bridge per content type is supported. Overriding content types is not supported.
	 */
	public synchronized void addStructureBridge(AbstractContextStructureBridge bridge) {
		if (bridge.getContentType().equals(CONTENT_TYPE_RESOURCE)) {
			defaultBridge = bridge;
		} else {
			bridges.put(bridge.getContentType(), bridge);
		}
		if (bridge.getParentContentType() != null) {
			List<String> childContentTypes = childContentTypeMap.get(bridge.getParentContentType());
			if (childContentTypes == null) {
				// CopyOnWriteArrayList handles concurrent access to the content types
				childContentTypes = new CopyOnWriteArrayList<String>();
			}

			childContentTypes.add(bridge.getContentType());
			childContentTypeMap.put(bridge.getParentContentType(), childContentTypes);
		}
	}

	public synchronized AbstractContextStore getContextStore() {
		if (!contextStoreRead) {
			contextStoreRead = true;
			ContextStoreExtensionReader.initExtensions();
			if (contextStore != null) {
				contextStore.init();
			} else {
				StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.PLUGIN_ID, "No context store specified"));
			}
		}
		return contextStore;
	}

//	public void setContextStore(AbstractContextStore contextStore) {
//		ContextCorePlugin.contextStore = contextStore;
//	}

	static class ContextStoreExtensionReader {

		private static final String ELEMENT_CONTEXT_STORE = "contextStore";

		private static boolean extensionsRead = false;

		public static void initExtensions() {
			if (!extensionsRead) {
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IExtensionPoint extensionPoint = registry.getExtensionPoint(BridgesExtensionPointReader.EXTENSION_ID_CONTEXT);
				IExtension[] extensions = extensionPoint.getExtensions();
				for (IExtension extension : extensions) {
					IConfigurationElement[] elements = extension.getConfigurationElements();
					for (IConfigurationElement element : elements) {
						if (element.getName().compareTo(ELEMENT_CONTEXT_STORE) == 0) {
							readStore(element);
						}
					}
				}
				extensionsRead = true;
			}
		}

		private static void readStore(IConfigurationElement element) {
			// Currently disabled
			try {
				Object object = element.createExecutableExtension(BridgesExtensionPointReader.ATTR_CLASS);
				if (!(object instanceof AbstractContextStore)) {
					StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.PLUGIN_ID,
							"Could not load bridge: " + object.getClass().getCanonicalName() + " must implement "
									+ AbstractContextStructureBridge.class.getCanonicalName()));
					return;
				} else {
					INSTANCE.contextStore = (AbstractContextStore) object;
				}
			} catch (CoreException e) {
				StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.PLUGIN_ID,
						"Could not load bridge extension", e));
			}
		}
	}

	static class BridgesExtensionPointReader {

		private static final String EXTENSION_ID_CONTEXT = "org.eclipse.mylyn.context.core.bridges";

		private static final String ELEMENT_STRUCTURE_BRIDGE = "structureBridge";

		private static final String ELEMENT_RELATION_PROVIDER = "relationProvider";

		private static final String ATTR_CLASS = "class";

		private static final String ATTR_CONTENT_TYPE = "contentType";

		private static final String ATTR_PARENT_CONTENT_TYPE = "parentContentType";

		private static boolean extensionsRead = false;

		public static void initExtensions() {
			if (!extensionsRead) {
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IExtensionPoint extensionPoint = registry.getExtensionPoint(BridgesExtensionPointReader.EXTENSION_ID_CONTEXT);
				IExtension[] extensions = extensionPoint.getExtensions();
				for (IExtension extension : extensions) {
					IConfigurationElement[] elements = extension.getConfigurationElements();
					for (IConfigurationElement element : elements) {
						if (element.getName().compareTo(BridgesExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE) == 0) {
							readBridge(element);
						} else if (element.getName().compareTo(BridgesExtensionPointReader.ELEMENT_RELATION_PROVIDER) == 0) {
							readRelationProvider(element);
						}
					}
				}
				extensionsRead = true;
			}
		}

		private static void readBridge(IConfigurationElement element) {
			try {
				Object object = element.createExecutableExtension(BridgesExtensionPointReader.ATTR_CLASS);
				if (!(object instanceof AbstractContextStructureBridge)) {
					StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.PLUGIN_ID,
							"Could not load bridge: " + object.getClass().getCanonicalName() + " must implement "
									+ AbstractContextStructureBridge.class.getCanonicalName()));
					return;
				}

				AbstractContextStructureBridge bridge = (AbstractContextStructureBridge) object;
				if (element.getAttribute(BridgesExtensionPointReader.ATTR_PARENT_CONTENT_TYPE) != null) {
					String parentContentType = element.getAttribute(BridgesExtensionPointReader.ATTR_PARENT_CONTENT_TYPE);
					if (parentContentType != null) {
						bridge.setParentContentType(parentContentType);
					}
				}
				ContextCorePlugin.getDefault().addStructureBridge(bridge);
			} catch (CoreException e) {
				StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.PLUGIN_ID,
						"Could not load bridge extension", e));
			}
		}

		private static void readRelationProvider(IConfigurationElement element) {
			try {
				String contentType = element.getAttribute(BridgesExtensionPointReader.ATTR_CONTENT_TYPE);
				AbstractRelationProvider relationProvider = (AbstractRelationProvider) element.createExecutableExtension(BridgesExtensionPointReader.ATTR_CLASS);
				if (contentType != null) {
					ContextCorePlugin.getDefault().addRelationProvider(contentType, relationProvider);
				}
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.PLUGIN_ID,
						"Could not load relation provider", e));
			}
		}
	}

	static class HandlersExtensionPointReader {

		private static final String EXTENSION_ID_HANDLERS = "org.eclipse.mylyn.context.core.handlers";

		private static final String ELEMENT_STATUS = "status";

		private static final String ELEMENT_CLASS = "class";

		private static boolean extensionsRead = false;

		public static void initExtensions() {
			if (!extensionsRead) {
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_ID_HANDLERS);
				IExtension[] extensions = extensionPoint.getExtensions();
				for (IExtension extension : extensions) {
					IConfigurationElement[] elements = extension.getConfigurationElements();
					for (IConfigurationElement element : elements) {
						if (element.getName().compareTo(ELEMENT_STATUS) == 0) {
							readHandler(element);
						}
					}
				}
				extensionsRead = true;
			}
		}

		private static void readHandler(IConfigurationElement element) {
			try {
				Object object = element.createExecutableExtension(ELEMENT_CLASS);
				if (!(object instanceof IStatusHandler)) {
					StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.PLUGIN_ID,
							"Could not load handler: " + object.getClass().getCanonicalName() + " must implement "
									+ AbstractContextStructureBridge.class.getCanonicalName()));
					return;
				}

				IStatusHandler handler = (IStatusHandler) object;
				StatusHandler.addStatusHandler(handler);
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
