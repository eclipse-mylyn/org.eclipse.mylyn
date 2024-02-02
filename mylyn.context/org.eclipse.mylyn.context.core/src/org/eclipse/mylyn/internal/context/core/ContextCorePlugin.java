/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.ExtensionPointReader;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextContributor;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IContextContributor;
import org.eclipse.mylyn.context.core.IInteractionContextScaling;
import org.osgi.framework.BundleContext;

/**
 * Activator for the Context Core plug-in.
 * 
 * @author Mik Kersten
 * @since 3.0
 */
public class ContextCorePlugin extends Plugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.context.core"; //$NON-NLS-1$

	private final Map<String, AbstractContextStructureBridge> bridges = new ConcurrentHashMap<>();

	private final Map<String, Set<String>> childContentTypeMap = new ConcurrentHashMap<>();

	private List<IContextContributor> contextContributor = new CopyOnWriteArrayList<>();

	// specifies that one content type should shadow another
	// the <value> content type shadows the <key> content typee
	private final Map<String, String> contentTypeToShadowMap = new ConcurrentHashMap<>();

	private AbstractContextStructureBridge defaultBridge = null;

	private static ContextCorePlugin INSTANCE;

	private InteractionContextManager contextManager;

	private static LocalContextStore contextStore;

	private final Map<String, Set<AbstractRelationProvider>> relationProviders = new HashMap<>();

	private final InteractionContextScaling commonContextScaling = new InteractionContextScaling();

	private boolean contextContributorInitialized = false;

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
			return ""; //$NON-NLS-1$
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

	private static final String EXTENSION_ELEMENT_CONTRIBUTOR = "contextContributor"; //$NON-NLS-1$

	private static final String EXTENSION_ID_CONTRIBUTOR = "contributor"; //$NON-NLS-1$

	public ContextCorePlugin() {
		INSTANCE = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		contextStore = new LocalContextStore(commonContextScaling);
		File storeFile = getDefaultContextLocation().toFile();
		if (!storeFile.exists()) {
			storeFile.mkdirs();
		}
		contextStore.setContextDirectory(storeFile);
		contextManager = new InteractionContextManager(contextStore);
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
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN, "Mylyn Core stop failed", e)); //$NON-NLS-1$
		}
	}

	public IPath getDefaultContextLocation() {
		IPath stateLocation = Platform.getStateLocation(getBundle());
		IPath cacheFile = stateLocation.append("contexts"); //$NON-NLS-1$
		return cacheFile;
	}

	/**
	 * Shadows override existing shadows if present.
	 */
	private void addShadowsContent(String baseContentType, String shadowedByContentType) {
		contentTypeToShadowMap.put(baseContentType, shadowedByContentType);
	}

	private void addRelationProvider(String contentType, AbstractRelationProvider provider) {
		Set<AbstractRelationProvider> providers = relationProviders.get(contentType);
		if (providers == null) {
			providers = new HashSet<>();
			relationProviders.put(contentType, providers);
		}
		providers.add(provider);
		// TODO: need facility for removing
		ContextCore.getContextManager().addListener(provider);
	}

	/**
	 * @return all relation providers
	 */
	public Set<AbstractRelationProvider> getRelationProviders() {
		Set<AbstractRelationProvider> allProviders = new HashSet<>();
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

	public List<IContextContributor> getContextContributor() {
		initContextContributor();
		return contextContributor;
	}

	void initContextContributor() {
		if (!contextContributorInitialized) {
			ExtensionPointReader<IContextContributor> extensionPointReader = new ExtensionPointReader<>(
					ContextCorePlugin.ID_PLUGIN, ContextCorePlugin.EXTENSION_ID_CONTRIBUTOR,
					ContextCorePlugin.EXTENSION_ELEMENT_CONTRIBUTOR, IContextContributor.class);
			extensionPointReader.read();
			contextContributor = extensionPointReader.getItems();
			for (IContextContributor contributor : contextContributor) {
				ContextCorePlugin.getContextManager().addListener(contributor);
			}
			contextContributorInitialized = true;
		}
	}

	public void addContextContributor(AbstractContextContributor contributor) {
		contextContributor.add(contributor);
		ContextCorePlugin.getContextManager().addListener(contributor);
	}

	public void removeContextContributor(AbstractContextContributor contributor) {
		contextContributor.remove(contributor);
		ContextCorePlugin.getContextManager().removeListener(contributor);
	}

	/**
	 * Finds the shadowed content for the passed in base content
	 * 
	 * @param baseContent
	 * @return the shadowed content type or if null there is none
	 */
	private String getShadowedContentType(String baseContent) {
		return contentTypeToShadowMap.get(baseContent);
	}

	public AbstractContextStructureBridge getStructureBridge(String contentType) {
		BridgesExtensionPointReader.initExtensions();
		if (contentType != null) {
			// find the content type that shadows this one
			// if one exists.
			String shadowsContentType = getShadowedContentType(contentType);
			if (shadowsContentType != null) {
				AbstractContextStructureBridge bridge = bridges.get(shadowsContentType);
				if (bridge != null) {
					return bridge;
				}
			}

			// no shadowing of content, look at original content type
			AbstractContextStructureBridge bridge = bridges.get(contentType);
			if (bridge != null) {
				return bridge;
			}
		}
		return defaultBridge == null ? DEFAULT_BRIDGE : defaultBridge;
	}

	public Set<String> getContentTypes() {
		BridgesExtensionPointReader.initExtensions();
		return bridges.keySet();
	}

	/**
	 * TODO: cache this to improve performance?
	 */
	public AbstractContextStructureBridge getStructureBridge(Object object) {
		BridgesExtensionPointReader.initExtensions();

		for (Map.Entry<String, AbstractContextStructureBridge> entry : bridges.entrySet()) {
			// check to see if there is shadowing of content types going on.
			String shadowsContentType = getShadowedContentType(entry.getKey());
			if (shadowsContentType != null) {
				AbstractContextStructureBridge structureBridge = bridges.get(shadowsContentType);
				if (structureBridge.acceptsObject(object)) {
					return structureBridge;
				}
			}

			// no shadowing...look at actual content type
			AbstractContextStructureBridge bridge = entry.getValue();
			if (bridge != null && bridge.acceptsObject(object)) {
				return bridge;
			}
		}

		// use the default if not found
		return defaultBridge != null && defaultBridge.acceptsObject(object) ? defaultBridge : DEFAULT_BRIDGE;
	}

	/**
	 * Recommended bridge registration is via extension point, but bridges can also be added at runtime. Note that only one bridge per
	 * content type is supported. Overriding content types is not supported.
	 */
	public synchronized void addStructureBridge(AbstractContextStructureBridge bridge) {
		if (bridge.getContentType().equals(ContextCore.CONTENT_TYPE_RESOURCE)) {
			defaultBridge = bridge;
		} else {
			bridges.put(bridge.getContentType(), bridge);
		}
		if (bridge.getParentContentType() != null) {
			Set<String> childContentTypes = childContentTypeMap.get(bridge.getParentContentType());
			if (childContentTypes == null) {
				// CopyOnWriteArrayList handles concurrent access to the content types
				childContentTypes = new CopyOnWriteArraySet<>();
			}

			childContentTypes.add(bridge.getContentType());
			childContentTypeMap.put(bridge.getParentContentType(), childContentTypes);
		}
	}

	public static LocalContextStore getContextStore() {
//		if (!contextStoreRead) {
//			contextStoreRead = true;
//			ContextStoreExtensionReader.initExtensions();
//			if (contextStore != null) {
//				contextStore.init();
//			} else {
//				StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.PLUGIN_ID, "No context store specified"));
//			}
//		}
		return contextStore;
	}

//	public void setContextStore(AbstractContextStore contextStore) {
//		ContextCorePlugin.contextStore = contextStore;
//	}

//	static class ContextStoreExtensionReader {
//
//		private static final String ELEMENT_CONTEXT_STORE = "contextStore";
//
//		private static boolean extensionsRead = false;
//
//		public static void initExtensions() {
//			if (!extensionsRead) {
//				IExtensionRegistry registry = Platform.getExtensionRegistry();
//				IExtensionPoint extensionPoint = registry.getExtensionPoint(BridgesExtensionPointReader.EXTENSION_ID_CONTEXT);
//				IExtension[] extensions = extensionPoint.getExtensions();
//				for (IExtension extension : extensions) {
//					IConfigurationElement[] elements = extension.getConfigurationElements();
//					for (IConfigurationElement element : elements) {
//						if (element.getName().compareTo(ELEMENT_CONTEXT_STORE) == 0) {
//							readStore(element);
//						}
//					}
//				}
//				extensionsRead = true;
//			}
//		}
//
//		private static void readStore(IConfigurationElement element) {
//			// Currently disabled
//			try {
//				Object object = element.createExecutableExtension(BridgesExtensionPointReader.ATTR_CLASS);
//				if (!(object instanceof AbstractContextStore)) {
//					StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.PLUGIN_ID,
//							"Could not load bridge: " + object.getClass().getCanonicalName() + " must implement "
//									+ AbstractContextStructureBridge.class.getCanonicalName()));
//					return;
//				} else {
//					ContextCorePlugin.contextStore = (AbstractContextStore) object;
//				}
//			} catch (CoreException e) {
//				StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.PLUGIN_ID,
//						"Could not load bridge extension", e));
//			}
//		}
//	}

	static class BridgesExtensionPointReader {

		private static final String EXTENSION_ID_CONTEXT = "org.eclipse.mylyn.context.core.bridges"; //$NON-NLS-1$

		private static final String EXTENSION_ID_INTERNAL_CONTEXT = "org.eclipse.mylyn.context.core.internalBridges"; //$NON-NLS-1$

		private static final String EXTENSION_ID_RELATION_PROVIDERS = "org.eclipse.mylyn.context.core.relationProviders"; //$NON-NLS-1$

		private static final String ELEMENT_STRUCTURE_BRIDGE = "structureBridge"; //$NON-NLS-1$

		private static final String ELEMENT_RELATION_PROVIDER = "provider"; //$NON-NLS-1$

		private static final String ELEMENT_SHADOW = "shadow"; //$NON-NLS-1$

		private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

		private static final String ATTR_CONTENT_TYPE = "contentType"; //$NON-NLS-1$

		private static final String ATTR_PARENT_CONTENT_TYPE = "parentContentType"; //$NON-NLS-1$

		private static final String ATTR_BASE_CONTENT = "baseContent"; //$NON-NLS-1$

		private static final String ATTR_SHADOWED_BY_CONTENT = "shadowedByContent"; //$NON-NLS-1$

		private static boolean extensionsRead = false;

		public static void initExtensions() {
			if (!extensionsRead) {
				IExtensionRegistry registry = Platform.getExtensionRegistry();

				IExtensionPoint extensionPoint = registry
						.getExtensionPoint(BridgesExtensionPointReader.EXTENSION_ID_CONTEXT);
				IExtension[] extensions = extensionPoint.getExtensions();
				for (IExtension extension : extensions) {
					IConfigurationElement[] elements = extension.getConfigurationElements();
					for (IConfigurationElement element : elements) {
						if (element.getName().compareTo(BridgesExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE) == 0) {
							readBridge(element);
						}
					}
				}

				// internal bridges
				extensionPoint = registry.getExtensionPoint(BridgesExtensionPointReader.EXTENSION_ID_INTERNAL_CONTEXT);
				extensions = extensionPoint.getExtensions();
				for (IExtension extension : extensions) {
					IConfigurationElement[] elements = extension.getConfigurationElements();
					for (IConfigurationElement element : elements) {
						if (element.getName().compareTo(BridgesExtensionPointReader.ELEMENT_SHADOW) == 0) {
							readInternalBridge(element);
						}
					}
				}

				extensionPoint = registry
						.getExtensionPoint(BridgesExtensionPointReader.EXTENSION_ID_RELATION_PROVIDERS);
				extensions = extensionPoint.getExtensions();
				for (IExtension extension : extensions) {
					IConfigurationElement[] elements = extension.getConfigurationElements();
					for (IConfigurationElement element : elements) {
						if (element.getName().compareTo(BridgesExtensionPointReader.ELEMENT_RELATION_PROVIDER) == 0) {
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
				if (!(object instanceof AbstractContextStructureBridge bridge)) {
					StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN,
							"Could not load bridge: " + object.getClass().getCanonicalName() + " must implement " //$NON-NLS-1$ //$NON-NLS-2$
									+ AbstractContextStructureBridge.class.getCanonicalName()));
					return;
				}

				if (element.getAttribute(BridgesExtensionPointReader.ATTR_PARENT_CONTENT_TYPE) != null) {
					String parentContentType = element
							.getAttribute(BridgesExtensionPointReader.ATTR_PARENT_CONTENT_TYPE);
					if (parentContentType != null) {
						bridge.setParentContentType(parentContentType);
					}
				}
				ContextCorePlugin.getDefault().addStructureBridge(bridge);
			} catch (Throwable e) {
				StatusHandler.log(
						new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, "Could not load bridge extension", e)); //$NON-NLS-1$
			}
		}

		private static void readInternalBridge(IConfigurationElement element) {
			String baseContent = element.getAttribute(ATTR_BASE_CONTENT);
			String shadowedByContent = element.getAttribute(ATTR_SHADOWED_BY_CONTENT);

			if (baseContent == null || shadowedByContent == null) {
				StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN,
						"Ignoring bridge shadowing because of invalid extension point " //$NON-NLS-1$
								+ BridgesExtensionPointReader.ELEMENT_STRUCTURE_BRIDGE,
						new Exception()));
			}
			ContextCorePlugin.getDefault().addShadowsContent(baseContent, shadowedByContent);
		}

		private static void readRelationProvider(IConfigurationElement element) {
			try {
				String contentType = element.getAttribute(BridgesExtensionPointReader.ATTR_CONTENT_TYPE);
				AbstractRelationProvider relationProvider = (AbstractRelationProvider) element
						.createExecutableExtension(BridgesExtensionPointReader.ATTR_CLASS);
				if (contentType != null) {
					ContextCorePlugin.getDefault().addRelationProvider(contentType, relationProvider);
				}
			} catch (Throwable e) {
				StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN,
						"Could not load relation provider", e)); //$NON-NLS-1$
			}
		}
	}

	public Set<String> getChildContentTypes(String contentType) {
		Set<String> contentTypes = childContentTypeMap.get(contentType);
		if (contentTypes != null) {
			return contentTypes;
		} else {
			return Collections.emptySet();
		}
	}

	public IInteractionContextScaling getCommonContextScaling() {
		return commonContextScaling;
	}
}
