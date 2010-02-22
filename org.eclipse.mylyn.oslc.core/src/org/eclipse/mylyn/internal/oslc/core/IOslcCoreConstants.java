/*******************************************************************************
 * Copyright (c) 2009, 2010 Tasktop Technologies and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *      Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.oslc.core;

import org.jdom.Namespace;

/**
 * @author Robert Elves
 */
public interface IOslcCoreConstants {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.oslc.core"; //$NON-NLS-1$

	//Namespaces

	public static final Namespace NAMESPACE_OSLC_CM_1_0 = Namespace.getNamespace("http://open-services.net/xmlns/cm/1.0/"); //$NON-NLS-1$

	public static final Namespace NAMESPACE_OSLC_DISCOVERY_1_0 = Namespace.getNamespace("http://open-services.net/xmlns/discovery/1.0/"); //$NON-NLS-1$

	public static final Namespace NAMESPACE_DC = Namespace.getNamespace("http://purl.org/dc/terms/"); //$NON-NLS-1$

	public static final Namespace NAMESPACE_RDF = Namespace.getNamespace(
			"rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"); //$NON-NLS-1$ //$NON-NLS-2$

	public static final Namespace NAMESPACE_ATOM = Namespace.getNamespace("atom", "http://www.w3.org/2005/Atom"); //$NON-NLS-1$ //$NON-NLS-2$

	// XML element ids
	public static final String ELEMENT_SERVICE_PROVIDER_CATALOG = "ServiceProviderCatalog"; //$NON-NLS-1$

	public static final String ELEMENT_SERVICE_PROVIDER = "ServiceProvider"; //$NON-NLS-1$

	public static final String ELEMENT_CHANGEREQUEST = "ChangeRequest"; //$NON-NLS-1$

	public static final String ELEMENT_SERVICES = "services"; //$NON-NLS-1$

	public static final String ELEMENT_CREATIONDIALOG = "creationDialog"; //$NON-NLS-1$

	public static final String ELEMENT_SELECTIONDIALOG = "selectionDialog"; //$NON-NLS-1$

	public static final String ELEMENT_FACTORY = "factory"; //$NON-NLS-1$

	public static final String ELEMENT_HOME = "home"; //$NON-NLS-1$

	public static final String ELEMENT_TITLE = "title"; //$NON-NLS-1$

	public static final String ELEMENT_TYPE = "type"; //$NON-NLS-1$

	public static final String ELEMENT_IDENTIFIER = "identifier"; //$NON-NLS-1$

	public static final String ELEMENT_DESCRIPTION = "description"; //$NON-NLS-1$

	public static final String ELEMENT_CREATOR = "creator"; //$NON-NLS-1$

	public static final String ELEMENT_MODIFIED = "modified"; //$NON-NLS-1$

	public static final String ELEMENT_SUBJECT = "subject"; //$NON-NLS-1$

	public static final String ELEMENT_URL = "url"; //$NON-NLS-1$

	public static final String ELEMENT_SIMPLEQUERY = "simpleQuery"; //$NON-NLS-1$

	public static final String ELEMENT_LABEL = "label"; //$NON-NLS-1$

	// XML attribute ids
	public static final String ATTRIBUTE_RESOURCE = "resource"; //$NON-NLS-1$

	public static final String ATTRIBUTE_DEFAULT = "default"; //$NON-NLS-1$

	public static final String ATTRIBUTE_HINTWIDTH = "hintWidth"; //$NON-NLS-1$

	public static final String ATTRIBUTE_HINTHEIGHT = "hintHeight"; //$NON-NLS-1$

	public static final String ATTRIBUTE_ABOUT = "about"; //$NON-NLS-1$

	// Http header keys
	public static final String HEADER_ETAG = "ETag"; //$NON-NLS-1$

	public static final String HEADER_IF_MATCH = "If-Match"; //$NON-NLS-1$

}
