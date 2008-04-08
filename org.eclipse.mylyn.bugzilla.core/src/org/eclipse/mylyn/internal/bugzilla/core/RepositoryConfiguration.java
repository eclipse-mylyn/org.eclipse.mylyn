/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class describing the configuration of products and components for a given Bugzilla installation.
 * 
 * @author Rob Elves
 */
public class RepositoryConfiguration implements Serializable {

	private static final long serialVersionUID = -482656956042521023L;

	private static final String VERSION_UNKNOWN = "unknown";

	private String repositoryUrl = "<unknown>";

	private Map<String, ProductEntry> products = new HashMap<String, ProductEntry>();

	private List<String> platforms = new ArrayList<String>();

	private List<String> operatingSystems = new ArrayList<String>();

	private List<String> priorities = new ArrayList<String>();

	private List<String> severities = new ArrayList<String>();

	private List<String> bugStatus = new ArrayList<String>();

	private List<String> openStatusValues = new ArrayList<String>();

	private List<String> resolutionValues = new ArrayList<String>();

	private List<String> keywords = new ArrayList<String>();

	// master lists

	private List<String> versions = new ArrayList<String>();

	private List<String> components = new ArrayList<String>();

	private List<String> milestones = new ArrayList<String>();

	private List<BugzillaCustomField> customFields = new ArrayList<BugzillaCustomField>();
	
	private String version = VERSION_UNKNOWN;

	public RepositoryConfiguration() {
		super();
		// ignore
	}

	public void addStatus(String status) {
		bugStatus.add(status);
	}

	public List<String> getStatusValues() {
		return bugStatus;
	}

	public void addResolution(String res) {
		resolutionValues.add(res);
	}

	public List<String> getResolutions() {
		return resolutionValues;
	}

	/**
	 * Adds a product to the configuration.
	 */
	public void addProduct(String name) {
		if (!products.containsKey(name)) {
			ProductEntry product = new ProductEntry(name);
			products.put(name, product);
		}
	}

	/**
	 * Returns an array of names of current products.
	 */
	public List<String> getProducts() {
		ArrayList<String> productList = new ArrayList<String>(products.keySet());
		Collections.sort(productList);
		return productList;
	}

	/**
	 * Returns an array of names of component that exist for a given product or <code>null</code> if the product does
	 * not exist.
	 */
	public List<String> getComponents(String product) {
		ProductEntry entry = products.get(product);
		if (entry != null) {
			return entry.getComponents();
		} else
			return Collections.emptyList();
	}

	/**
	 * Returns an array of names of versions that exist for a given product or <code>null</code> if the product does
	 * not exist.
	 */
	public List<String> getVersions(String product) {
		ProductEntry entry = products.get(product);
		if (entry != null) {
			return entry.getVersions();
		} else
			return Collections.emptyList();
	}

	/**
	 * Returns an array of names of valid severity values.
	 */
	public List<String> getSeverities() {
		return severities;
	}

	/**
	 * Returns an array of names of valid OS values.
	 */
	public List<String> getOSs() {
		return operatingSystems;
	}

	public void addOS(String os) {
		operatingSystems.add(os);
	}

	/**
	 * Returns an array of names of valid platform values.
	 */
	public List<String> getPlatforms() {
		return platforms;
	}

	/**
	 * Returns an array of names of valid platform values.
	 */
	public List<String> getPriorities() {
		return priorities;
	}

	/**
	 * Adds a component to the given product.
	 */
	public void addComponent(String product, String component) {
		if (!components.contains(component))
			components.add(component);
		ProductEntry entry = products.get(product);
		if (entry == null) {
			entry = new ProductEntry(product);
			products.put(product, entry);
		}
		entry.addComponent(component);
	}

	// /**
	// * Adds a list of components to the given product.
	// */
	// public void addComponents(String product, List<String> components) {
	// ProductEntry entry = products.get(product);
	// if (entry == null) {
	// entry = new ProductEntry(product);
	// products.put(product, entry);
	// }
	// for (String component : components) {
	// entry.addComponent(component);
	// }
	// }
	// /**
	// * Adds a list of components to the given product.
	// */
	// public void addComponents(String product, List<String> components) {
	// ProductEntry entry = products.get(product);
	// if (entry == null) {
	// entry = new ProductEntry(product);
	// products.put(product, entry);
	// }
	// for (String component : components) {
	// entry.addComponent(component);
	// }
	// }

	public void addVersion(String product, String version) {
		if (!versions.contains(version))
			versions.add(version);
		ProductEntry entry = products.get(product);
		if (entry == null) {
			entry = new ProductEntry(product);
			products.put(product, entry);
		}
		entry.addVersion(version);
	}

	// /**
	// * Adds a list of components to the given product.
	// */
	// public void addVersions(String product, List<String> versions) {
	// ProductEntry entry = products.get(product);
	// if (entry == null) {
	// entry = new ProductEntry(product);
	// products.put(product, entry);
	// }
	// for (String version : versions) {
	// entry.addVersion(version);
	// }
	// }

	public void addKeyword(String keyword) {
		keywords.add(keyword);
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void addPlatform(String platform) {
		platforms.add(platform);
	}

	public void addPriority(String priority) {
		priorities.add(priority);
	}

	public void addSeverity(String severity) {
		severities.add(severity);

	}

	public void setInstallVersion(String version) {
		this.version = version;
	}

	public String getInstallVersion() {
		return version;
	}

	public void addTargetMilestone(String product, String target) {
		if (!milestones.contains(target))
			milestones.add(target);
		ProductEntry entry = products.get(product);
		if (entry == null) {
			entry = new ProductEntry(product);
			products.put(product, entry);
		}

		entry.addTargetMilestone(target);

	}

	public List<String> getTargetMilestones(String product) {
		ProductEntry entry = products.get(product);
		if (entry != null) {
			return entry.getTargetMilestones();
		} else
			return Collections.emptyList();
	}

	/**
	 * Container for product information: name, components.
	 */
	private static class ProductEntry implements Serializable {

		private static final long serialVersionUID = 4120139521246741120L;

		String productName;

		List<String> components = new ArrayList<String>();

		List<String> versions = new ArrayList<String>();

		List<String> milestones = new ArrayList<String>();

		ProductEntry(String name) {
			this.productName = name;
		}

		List<String> getComponents() {
			return components;
		}

		void addComponent(String componentName) {
			if (!components.contains(componentName)) {
				components.add(componentName);
			}
		}

		List<String> getVersions() {
			return versions;
		}

		void addVersion(String name) {
			if (!versions.contains(name)) {
				versions.add(name);
			}
		}

		List<String> getTargetMilestones() {
			return milestones;
		}

		void addTargetMilestone(String target) {
			milestones.add(target);
		}
	}

	public List<String> getOpenStatusValues() {
		return openStatusValues;
	}

	public void addOpenStatusValue(String value) {
		openStatusValues.add(value);
	}

	public List<String> getComponents() {
		return components;
	}

	public List<String> getTargetMilestones() {
		return milestones;
	}

	public List<String> getVersions() {
		return versions;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	/*
	 * Intermediate step until configuration is made generic.
	 */
	public List<String> getOptionValues(BugzillaReportElement element, String product) {
		switch (element) {
		case PRODUCT:
			return getProducts();
		case TARGET_MILESTONE:
			// return getTargetMilestones();
			return getTargetMilestones(product);
		case BUG_STATUS:
			return getStatusValues();
		case VERSION:
			// return getVersions();
			return getVersions(product);
		case COMPONENT:
			// return getComponents();
			return getComponents(product);
		case REP_PLATFORM:
			return getPlatforms();
		case OP_SYS:
			return getOSs();
		case PRIORITY:
			return getPriorities();
		case BUG_SEVERITY:
			return getSeverities();
		case KEYWORDS:
			return getKeywords();
		case RESOLUTION:
			return getResolutions();
		default:
			return new ArrayList<String>();
		}
	}

	/**
	 * Adds a field to the configuration.
	 */
	public void addCustomField(BugzillaCustomField newField) {
		customFields.add(newField);
	}

	public List<BugzillaCustomField> getCustomFields() {
		return customFields;
	}

}
