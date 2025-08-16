/**
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.internal.core.model;

import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.util.XMLProcessor;

/**
 * This class contains helper methods to serialize and deserialize XML documents <!-- begin-user-doc --> <!-- end-user-doc -->
 *
 * @generated
 */
public class ReviewsXMLProcessor extends XMLProcessor {

	/**
	 * Public constructor to instantiate the helper. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ReviewsXMLProcessor() {
		super(EPackage.Registry.INSTANCE);
		ReviewsPackage.eINSTANCE.eClass();
	}

	/**
	 * Register for "*" and "xml" file extensions the ReviewsResourceFactory factory. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected Map<String, Resource.Factory> getRegistrations() {
		if (registrations == null) {
			super.getRegistrations();
			registrations.put(XML_EXTENSION, new ReviewsResourceFactory());
			registrations.put(STAR_EXTENSION, new ReviewsResourceFactory());
		}
		return registrations;
	}

} //ReviewsXMLProcessor
