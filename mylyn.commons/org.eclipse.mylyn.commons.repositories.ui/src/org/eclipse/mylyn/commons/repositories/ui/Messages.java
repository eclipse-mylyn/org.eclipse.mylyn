/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.ui;

import org.eclipse.osgi.util.NLS;

class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.commons.repositories.ui.messages"; //$NON-NLS-1$

	public static String RepositoryLocationPart_Anonymous;

	public static String RepositoryLocationPart_Browse;

	public static String RepositoryLocationPart_Certificate_Authentiation;

	public static String RepositoryLocationPart_Change_Settings;

	public static String RepositoryLocationPart_Disconnected;

	public static String RepositoryLocationPart_Enable_certificate_authentication;

	public static String RepositoryLocationPart_Enable_HTTP_Authentication;

	public static String RepositoryLocationPart_Enable_Proxy_Authentication;

	public static String RepositoryLocationPart_Enter_a_valid_server_url;

	public static String RepositoryLocationPart_HTTP_Authentication;

	public static String RepositoryLocationPart_Keystorefile;

	public static String RepositoryLocationPart_Label;

	public static String RepositoryLocationPart_Password;

	public static String RepositoryLocationPart_Proxy_Host;

	public static String RepositoryLocationPart_Proxy_Port;

	public static String RepositoryLocationPart_Proxy_Server_Configuration;

	public static String RepositoryLocationPart_Repository_is_valid;

	public static String RepositoryLocationPart_Save_Password;

	public static String RepositoryLocationPart_Server;

	public static String RepositoryLocationPart_Unexpected_error_during_repository_validation;

	public static String RepositoryLocationPart_Use_global_Network_Connections_preferences;

	public static String RepositoryLocationPart_User;

	public static String RepositoryLocationPart_Validating_repository;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

}
