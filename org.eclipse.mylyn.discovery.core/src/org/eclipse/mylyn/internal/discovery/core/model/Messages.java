/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.discovery.core.model;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author David Green
 */
class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.discovery.core.model.messages"; //$NON-NLS-1$

	public static String BundleDiscoveryStrategy_3;

	public static String BundleDiscoveryStrategy_task_loading_local_extensions;

	public static String BundleDiscoveryStrategy_task_processing_extensions;

	public static String BundleDiscoveryStrategy_unexpected_element;

	public static String ConnectorCategory_must_specify_connectorCategory_id;

	public static String ConnectorCategory_must_specify_connectorCategory_name;

	public static String ConnectorDescriptor_invalid_connectorDescriptor_siteUrl;

	public static String ConnectorDescriptor_must_specify_connectorDescriptor_categoryId;

	public static String ConnectorDescriptor_must_specify_connectorDescriptor_id;

	public static String ConnectorDescriptor_must_specify_connectorDescriptor_kind;

	public static String ConnectorDescriptor_must_specify_connectorDescriptor_license;

	public static String ConnectorDescriptor_must_specify_connectorDescriptor_name;

	public static String ConnectorDescriptor_must_specify_connectorDescriptor_provider;

	public static String ConnectorDescriptor_must_specify_connectorDescriptor_siteUrl;

	public static String ConnectorDiscovery_bundle_references_unknown_category;

	public static String ConnectorDiscovery_duplicate_category_id;

	public static String ConnectorDiscovery_illegal_filter_syntax;

	public static String ConnectorDiscovery_task_discovering_connectors;

	public static String ConnectorDiscoveryExtensionReader_unexpected_element_icon;

	public static String ConnectorDiscoveryExtensionReader_unexpected_element_overview;

	public static String ConnectorDiscoveryExtensionReader_unexpected_value_kind;

	public static String DirectoryParser_no_directory;

	public static String DirectoryParser_unexpected_element;

	public static String DiscoveryRegistryStrategy_cannot_load_bundle;

	public static String DiscoveryRegistryStrategy_missing_pluginxml;

	public static String RemoteBundleDiscoveryStrategy_cannot_download_bundle;

	public static String RemoteBundleDiscoveryStrategy_empty_directory;

	public static String RemoteBundleDiscoveryStrategy_io_failure_discovery_directory;

	public static String RemoteBundleDiscoveryStrategy_io_failure_temp_storage;

	public static String RemoteBundleDiscoveryStrategy_task_loading_remote_extensions;

	public static String RemoteBundleDiscoveryStrategy_task_remote_discovery;

	public static String RemoteBundleDiscoveryStrategy_unrecognized_discovery_url;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
