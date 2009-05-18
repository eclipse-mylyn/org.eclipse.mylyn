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

package org.eclipse.mylyn.internal.tasks.bugs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IBundleGroupProvider;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.IProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.branding.IBundleGroupConstants;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Steffen Pingel
 */
public class SupportProviderManager {

	private static final String ATTRIBUTE_CATEGORY_ID = "categoryId"; //$NON-NLS-1$

	private static final String ATTRIBUTE_DESCRIPTION = "description"; //$NON-NLS-1$

	private static final String ATTRIBUTE_FEATURE_ID = "featureId"; //$NON-NLS-1$

	private static final String ATTRIBUTE_ICON = "icon"; //$NON-NLS-1$

	private static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$

	private static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$

	private static final String ATTRIBUTE_NAMESPACE = "namespace"; //$NON-NLS-1$

	private static final String ATTRIBUTE_PLUGIN_ID = "pluginId"; //$NON-NLS-1$

	private static final String ATTRIBUTE_PRODUCT_ID = "productId"; //$NON-NLS-1$

	private static final String ATTRIBUTE_PROVIDER_ID = "providerId"; //$NON-NLS-1$

	private static final String ATTRIBUTE_REPOSITORY_KIND = "kind"; //$NON-NLS-1$

	private static final String ATTRIBUTE_REPOSITORY_URL = "url"; //$NON-NLS-1$

	private static final String ATTRIBUTE_URL = "url"; //$NON-NLS-1$

	private static final String ATTRIBUTE_VALUE = "value"; //$NON-NLS-1$

	private static final String ATTRIBUTE_WEIGHT = "weight"; //$NON-NLS-1$

	private static final String ELEMENT_CATEGORY = "category"; //$NON-NLS-1$

	private static final String ELEMENT_MAPPING = "mapping"; //$NON-NLS-1$

	private static final String ELEMENT_PRODUCT = "product"; //$NON-NLS-1$

	private static final String ELEMENT_PROPERTY = "property"; //$NON-NLS-1$

	private static final String ELEMENT_PROVIDER = "provider"; //$NON-NLS-1$

	private static final String ELEMENT_REPOSITORY = "repository"; //$NON-NLS-1$

	private static final String EXTENSION_ID_PLUGIN_SUPPORT = "org.eclipse.mylyn.tasks.bugs.support"; //$NON-NLS-1$

	private HashMap<String, IBundleGroup> bundleGroupById;

	private List<SupportCategory> categories;

	private SupportProduct defaultProduct;

	private Map<String, SupportProduct> productById;

	private Map<String, SupportProvider> providerById;

	public SupportProviderManager() {
		readExtensions();
	}

	public void addCategory(SupportCategory category) {
		categories.add(category);
	}

	public boolean addProduct(SupportProduct product) {
		if (providerById.containsKey(product.getId())) {
			return false;
		}
		productById.put(product.getId(), product);
		return true;
	}

	public boolean addProvider(SupportProvider provider) {
		if (providerById.containsKey(provider.getId())) {
			return false;
		}
		providerById.put(provider.getId(), provider);
		return true;
	}

	private IBundleGroup getBundleGroup(String featureId) {
		if (bundleGroupById == null) {
			bundleGroupById = new HashMap<String, IBundleGroup>();
			IBundleGroupProvider[] providers = Platform.getBundleGroupProviders();
			if (providers != null) {
				for (IBundleGroupProvider provider : providers) {
					for (IBundleGroup bundleGroup : provider.getBundleGroups()) {
						bundleGroupById.put(bundleGroup.getIdentifier(), bundleGroup);
					}
				}
			}
		}
		return bundleGroupById.get(featureId);
	}

	public Collection<SupportCategory> getCategories() {
		return Collections.unmodifiableList(categories);
	}

	public SupportCategory getCategory(String categoryId) {
		for (SupportCategory category : categories) {
			if (category.getId().equals(categoryId)) {
				return category;
			}
		}
		return null;
	}

	public SupportProduct getDefaultProduct() {
		return defaultProduct;
	}

	public SupportProduct getProduct(String productId) {
		return productById.get(productId);
	}

	public Collection<SupportProduct> getProducts() {
		return Collections.unmodifiableCollection(productById.values());
	}

	public SupportProvider getProvider(String providerId) {
		return providerById.get(providerId);
	}

	public Collection<SupportProvider> getProviders() {
		return Collections.unmodifiableCollection(providerById.values());
	}

	private boolean readAttributes(IConfigurationElement element, AbstractSupportElement item) {
		item.setId(element.getAttribute(ATTRIBUTE_ID));
		item.setName(element.getAttribute(ATTRIBUTE_NAME));
		item.setDescription(element.getAttribute(ATTRIBUTE_DESCRIPTION));
		item.setUrl(element.getAttribute(ATTRIBUTE_URL));
		String iconPath = element.getAttribute(ATTRIBUTE_ICON);
		if (iconPath != null) {
			ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(element.getContributor().getName(),
					iconPath);
			if (descriptor != null) {
				item.setIcon(descriptor);
			}
		}

		// optionally complement data from referenced feature
		String featureId = element.getAttribute(ATTRIBUTE_FEATURE_ID);
		if (featureId != null) {
			IBundleGroup bundleGroup = getBundleGroup(featureId);
			if (bundleGroup == null) {
				// indicate that the specified feature was not found
				return false;
			} else {
				if (item.getName() == null) {
					item.setName(bundleGroup.getName());
				}
				if (item.getDescription() == null) {
					item.setDescription(bundleGroup.getDescription());
				}
				if (item.getIcon() == null) {
					String imageUrl = bundleGroup.getProperty(IBundleGroupConstants.FEATURE_IMAGE);
					if (imageUrl != null) {
						try {
							item.setIcon(ImageDescriptor.createFromURL(new URL(imageUrl)));
						} catch (MalformedURLException e) {
							// ignore 
						}
					}
				}
				if (item instanceof SupportProduct) {
					((SupportProduct) item).setBundleGroup(bundleGroup);
				}
			}
		}
		return true;
	}

	private void readCategory(IConfigurationElement element) {
		SupportCategory category = new SupportCategory();
		readAttributes(element, category);
		String weight = element.getAttribute(ATTRIBUTE_WEIGHT);
		if (weight != null) {
			try {
				category.setWeight(Integer.parseInt(weight));
			} catch (NumberFormatException e) {
				// ignore
			}
		}
		categories.add(category);
	}

	private void readExtensions() {
		categories = new ArrayList<SupportCategory>();
		productById = new HashMap<String, SupportProduct>();
		providerById = new HashMap<String, SupportProvider>();
		defaultProduct = new SupportProduct();

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_ID_PLUGIN_SUPPORT);
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(ELEMENT_CATEGORY)) {
					readCategory(element);
				}
			}
		}
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(ELEMENT_PROVIDER)) {
					readProvider(element);
				}
			}
		}
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(ELEMENT_PRODUCT)) {
					readProduct(element);
				}
			}
		}
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(ELEMENT_MAPPING)) {
					readMapping(element);
				}
			}
		}

		// clear cache
		bundleGroupById = null;
	}

	private ProductRepositoryMapping readMapping(IConfigurationElement element) {
		String namespace = element.getAttribute(ATTRIBUTE_NAMESPACE);
		String productId = element.getAttribute(ATTRIBUTE_PRODUCT_ID);
		Map<String, String> attributes = new HashMap<String, String>();
		// repository
		for (IConfigurationElement attributeElement : element.getChildren(ELEMENT_REPOSITORY)) {
			String repositoryUrl = attributeElement.getAttribute(ATTRIBUTE_REPOSITORY_URL);
			attributes.put(IRepositoryConstants.REPOSITORY_URL, repositoryUrl);
			String connectorKind = attributeElement.getAttribute(ATTRIBUTE_REPOSITORY_KIND);
			attributes.put(IRepositoryConstants.CONNECTOR_KIND, connectorKind);
		}
		// attributes
		for (IConfigurationElement attributeElement : element.getChildren(ELEMENT_PROPERTY)) {
			String name = attributeElement.getAttribute(ATTRIBUTE_NAME);
			String value = attributeElement.getAttribute(ATTRIBUTE_VALUE);
			attributes.put(name, value);
		}

		if (!attributes.isEmpty()) {
			ProductRepositoryMapping mapping = new ProductRepositoryMapping(namespace);
			mapping.addAttributes(attributes);

			final SupportProduct product;
			if (productId == null) {
				product = defaultProduct;
			} else {
				product = getProduct(productId);
				if (product == null) {
					StatusHandler.log(new Status(
							IStatus.WARNING,
							TasksBugsPlugin.ID_PLUGIN,
							NLS.bind(
									"Mapping contributed by {0} with namespace ''{1}'' ignored, invalid product id ''{1}'' specified", //$NON-NLS-1$
									new String[] { element.getNamespaceIdentifier(), namespace, productId })));
					return null;
				}
			}
			product.addRepositoryMapping(mapping);
			return mapping;
		} else {
			StatusHandler.log(new Status(IStatus.WARNING, TasksBugsPlugin.ID_PLUGIN, NLS.bind(
					"Mapping contributed by {0} with namespace ''{1}'' ignored, no attributes specified", //$NON-NLS-1$
					new String[] { element.getNamespaceIdentifier(), namespace })));
			return null;
		}
	}

	private SupportProduct readProduct(IConfigurationElement element) {
		String id = element.getAttribute(ATTRIBUTE_ID);
		String providerId = element.getAttribute(ATTRIBUTE_PROVIDER_ID);
		IProvider provider = getProvider(providerId);
		if (provider == null) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksBugsPlugin.ID_PLUGIN, NLS.bind(
					"Product contributed by {0} with id ''{1}'' ignored, product id ''{2}'' is invalid", //$NON-NLS-1$
					new String[] { element.getNamespaceIdentifier(), id, providerId })));
			return null;
		}
		boolean enabled = true;
		String pluginId = element.getAttribute(ATTRIBUTE_PLUGIN_ID);
		if (pluginId != null) {
			enabled &= Platform.getBundle(pluginId) != null;
		}
		SupportProduct product = new SupportProduct();
		enabled &= readAttributes(element, product);
		// disable products that do not have a corresponding plug-in or feature installed
		product.setInstalled(enabled);
		product.setProvider(provider);
		if (!addProduct(product)) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksBugsPlugin.ID_PLUGIN, NLS.bind(
					"Product contributed by {0} ignored, id ''{1}'' already present", //$NON-NLS-1$
					element.getNamespaceIdentifier(), id)));
			return null;
		}
		((SupportProvider) provider).add(product);
		productById.put(id, product);
		return product;
	}

	private SupportProvider readProvider(IConfigurationElement element) {
		String id = element.getAttribute(ATTRIBUTE_ID);
		SupportProvider provider = new SupportProvider();
		readAttributes(element, provider);
		if (!addProvider(provider)) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksBugsPlugin.ID_PLUGIN, NLS.bind(
					"Provider contributed by {0} ignored, id ''{1}'' already present", //$NON-NLS-1$
					element.getNamespaceIdentifier(), id)));
			return null;
		}
		String categoryId = element.getAttribute(ATTRIBUTE_CATEGORY_ID);
		SupportCategory category = getCategory(categoryId);
		if (category == null) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksBugsPlugin.ID_PLUGIN, NLS.bind(
					"Provider contributed by {0} ignored with id ''{1}'' ignored, category id ''{2}'' is invalid", //$NON-NLS-1$
					new String[] { element.getNamespaceIdentifier(), id, categoryId })));
			return null;
		}
		providerById.put(id, provider);
		category.add(provider);
		provider.setCategory(category);
		return provider;
	}

}
