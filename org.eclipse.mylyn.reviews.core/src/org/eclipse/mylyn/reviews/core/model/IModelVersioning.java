/**
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.core.model;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Model Versioning</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IModelVersioning#getFragmentVersion <em>Fragment Version</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IModelVersioning extends EObject {
	/**
	 * Returns the value of the '<em><b>Fragment Version</b></em>' attribute. The default value is <code>"1.0.0"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fragment Version</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Fragment Version</em>' attribute.
	 * @see #setFragmentVersion(String)
	 * @generated
	 */
	String getFragmentVersion();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IModelVersioning#getFragmentVersion
	 * <em>Fragment Version</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Fragment Version</em>' attribute.
	 * @see #getFragmentVersion()
	 * @generated
	 */
	void setFragmentVersion(String value);

} // IModelVersioning
