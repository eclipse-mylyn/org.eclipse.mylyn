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

package org.eclipse.mylar.internal.bugzilla.core.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class describing the configuration of products and components for a given
 * Bugzilla installation.
 */
public class ProductConfiguration implements Serializable {

	/** Automatically generated serialVersionUID */
	private static final long serialVersionUID = 3257004354337519410L;

	private Map<String, ProductEntry> products = new HashMap<String, ProductEntry>();

	public ProductConfiguration() {
		super();
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
	public String[] getProducts() {
		return products.keySet().toArray(new String[0]);
	}

	/**
	 * Returns an array of names of component that exist for a given product or
	 * <code>null</code> if the product does not exist.
	 */
	public String[] getComponents(String product) {
		ProductEntry entry = products.get(product);
		if (entry != null) {
			return entry.getComponents();
		} else
			return null;
	}

	/**
	 * Returns an array of names of versions that exist for a given product or
	 * <code>null</code> if the product does not exist.
	 */
	public String[] getVersions(String product) {
		ProductEntry entry = products.get(product);
		if (entry != null) {
			return entry.getVersions();
		} else
			return null;
	}

	/**
	 * Returns an array of names of valid severity values.
	 */
	public String[] getSeverities() {
		return new String[] { "blocker", "critical", "major", "normal", "minor", "trivial", "enhancement" };
	}

	/**
	 * Returns an array of names of valid OS values.
	 */
	public String[] getOSs() {
		return new String[] { "All", "Windows XP", "Linux", "other" };
	}

	/**
	 * Returns an array of names of valid platform values.
	 */
	public String[] getPlatforms() {
		return new String[] { "All", "Macintosh", "PC" };
	}

	/**
	 * Returns an array of names of valid platform values.
	 */
	public String[] getPriorities() {
		return new String[] { "P1", "P2", "P3", "P4", "P5" };
	}

	/**
	 * Adds a component to the given product.
	 */
	public void addComponent(String product, String component) {
		ProductEntry entry = products.get(product);
		if (entry == null) {
			entry = new ProductEntry(product);
			products.put(product, entry);
		}
		entry.addComponent(component);
	}

	/**
	 * Adds a list of components to the given product.
	 */
	public void addComponents(String product, String[] components) {
		ProductEntry entry = products.get(product);
		if (entry == null) {
			entry = new ProductEntry(product);
			products.put(product, entry);
		}
		for (int i = 0; i < components.length; i++) {
			String component = components[i];
			entry.addComponent(component);
		}
	}

	/**
	 * Adds a list of components to the given product.
	 */
	public void addVersions(String product, String[] versions) {
		ProductEntry entry = products.get(product);
		if (entry == null) {
			entry = new ProductEntry(product);
			products.put(product, entry);
		}
		for (int i = 0; i < versions.length; i++) {
			String version = versions[i];
			entry.addVersion(version);
		}
	}

	/**
	 * Container for product information: name, components.
	 */
	private static class ProductEntry implements Serializable {

		/** Automatically generated serialVersionUID */
		private static final long serialVersionUID = 3977018465733391668L;

		String productName;

		List<String> components = new ArrayList<String>();

		List<String> versions = new ArrayList<String>();

		ProductEntry(String name) {
			this.productName = name;
		}

		String[] getComponents() {
			return components.toArray(new String[0]);
		}

		void addComponent(String componentName) {
			if (!components.contains(componentName)) {
				components.add(componentName);
			}
		}

		String[] getVersions() {
			return versions.toArray(new String[0]);
		}

		void addVersion(String name) {
			if (!versions.contains(name)) {
				versions.add(name);
			}
		}
	}
}
