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

package org.eclipse.mylar.bugzilla.core.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;

/**
 * A factory for creating ProductConfiguration objects that encapsulate valid
 * combinations of products, components, and versions.
 */
public class ProductConfigurationFactory {
	/** Singleton factory instance */
	private static ProductConfigurationFactory instance;

	/**
	 * Private constructor to ensure singleton instances.
	 */
	private ProductConfigurationFactory() {
		// no initial setup needed
	}

	/**
	 * Returns the factory singletoninstance.
	 */
	public static synchronized ProductConfigurationFactory getInstance() {
		if (instance == null) {
			instance = new ProductConfigurationFactory();
		}
		return instance;
	}

	/**
	 * Builds a ProductConfiguration object by parsing the source of the
	 * Bugzilla query page.
	 */
	public ProductConfiguration getConfiguration(String server) throws IOException {
		URL serverURL = new URL(server + "/query.cgi");
		ProductConfiguration configuration = new ProductConfiguration();
		ArrayList<String[]> componentsMatrix = new ArrayList<String[]>();
		ArrayList<String[]> versionsMatrix = new ArrayList<String[]>();
		URLConnection c = serverURL.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			if (line.startsWith("  cpts[")) {
				String[] components = parseComponents(line);
				if (components.length > 0)
					componentsMatrix.add(components);
			} else if (line.startsWith("  vers[")) {
				String[] versions = parseComponents(line);
				if (versions.length > 0)
					versionsMatrix.add(versions);
			} else if (line.indexOf("<select name=\"product\"") != -1) {
				String[] products = parseProducts(in);
				for (int i = 0; i < products.length; i++) {
					String product = products[i];
					configuration.addProduct(product);
					// If components don't jibe with the products, just don't
					// make them available.
					if (products.length == componentsMatrix.size()) {
						configuration.addComponents(product, componentsMatrix.get(i));
					}

					// If versions don't jibe with the products, just don't make
					// them available
					if (products.length == versionsMatrix.size()) {
						configuration.addVersions(product, versionsMatrix.get(i));
					}
				}
			}
		}
		return configuration;
	}

	/**
	 * Returns an array of valid components or versions by parsing the
	 * JavaScript array in the Bugzilla query page.
	 */
	protected String[] parseComponents(String line) {
		ArrayList<String> components = new ArrayList<String>();
		int start = line.indexOf('\'');
		if (start >= 0) {
			boolean inName = true;
			StringBuffer name = new StringBuffer();
			for (int i = start + 1; i < line.length(); i++) {
				char ch = line.charAt(i);
				if (inName) {
					if (ch == '\'') {
						components.add(name.toString());
						name.setLength(0);
						inName = false;
					} else
						name.append(ch);
				} else {
					if (ch == '\'') {
						inName = true;
					}
				}
			}
		}
		return components.toArray(new String[0]);
	}

	/**
	 * Returns an array of valid product names by parsing the product selection
	 * list in the Bugzilla query page.
	 */
	protected String[] parseProducts(BufferedReader in) throws IOException {
		ArrayList<String> products = new ArrayList<String>();
		String line;
		while ((line = in.readLine()) != null) {
			if (line.indexOf("</select>") != -1)
				break;
			int optionIndex = line.indexOf("<option value=\"");
			if (optionIndex != -1) {
				boolean inName = false;
				StringBuffer name = new StringBuffer();
				for (int i = optionIndex; i < line.length(); i++) {
					char ch = line.charAt(i);
					if (inName) {
						if (ch == '<') {
							products.add(name.toString());
							break;
						} else
							name.append(ch);
					} else {
						if (ch == '>') {
							inName = true;
						}
					}
				}
			}
		}
		return products.toArray(new String[0]);
	}

	/**
	 * Restores a ProductConfiguration from a file.
	 */
	public ProductConfiguration readConfiguration(File file) throws IOException {
		if (!file.exists())
			return null;
		FileInputStream fin = null;
		ProductConfiguration configuration = null;
		try {
			fin = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fin);
			configuration = (ProductConfiguration) in.readObject();
		} catch (ClassNotFoundException e) {
			BugzillaPlugin.log(e);
			IOException ex = new IOException();
			ex.initCause(e);
			throw ex;
		} finally {
			if (fin != null)
				fin.close();
		}
		return configuration;
	}

	/**
	 * Saves a ProductConfiguration to a file.
	 */
	public void writeConfiguration(ProductConfiguration configuration, File file) throws IOException {
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fout);
			out.writeObject(configuration);
		} finally {
			if (fout != null)
				fout.close();
		}
	}
}
