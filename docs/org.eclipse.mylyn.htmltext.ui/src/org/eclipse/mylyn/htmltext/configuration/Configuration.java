/*******************************************************************************
 * Copyright (c) 2011, 2021 Tom Seidel, Remus Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.htmltext.configuration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.htmltext.HtmlComposer;


/**
 * A configuration for the underlying CK-Editor. The editor has several
 * configuration elements that are collected in this class and are set
 * to the receiver of the {@link HtmlComposer}.
 * 
 * @author Tom Seidel <tom.seidel@remus-software.org>
 * @since 0.8
 * @noextend This class is not intended to be subclassed by clients.
 */
public class Configuration {

	private static final String QUERY_SEPARATOR = "&";
	
	private List<ConfigurationElement> configNodes;

	public Configuration() {
		super();
	}
	
	public Configuration(List<ConfigurationElement> configNodes) {
		super();
		this.configNodes = configNodes;
	}

	/**
	 * Adds a new configuration element to the current configuration
	 * @param element the element to add.
	 * @since 0.8
	 */
	public void addConfigurationNode(ConfigurationElement element) {
		if (configNodes == null) {
			configNodes = new ArrayList<>();
		}
		configNodes.add(element);
	}

	

	/**
	 * Iterates through all configuration nodes and constructs a
	 * query that will be appended to the html page of the composer.
	 * @return the query.
	 */
	public String toQuery() {
		StringBuilder sb = new StringBuilder();
		if (configNodes != null) {
			for (ConfigurationElement element : configNodes) {
				String query = element.toQuery();
				if (query != null && query.length() > 0) {
					sb.append(query).append(QUERY_SEPARATOR);
				}
			}
		}
		return sb.toString();
	}

}
