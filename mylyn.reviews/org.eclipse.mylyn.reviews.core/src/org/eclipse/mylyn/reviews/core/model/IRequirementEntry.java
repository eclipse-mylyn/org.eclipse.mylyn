/**
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.core.model;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Requirement Entry</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRequirementEntry#getStatus <em>Status</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRequirementEntry#getBy <em>By</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IRequirementEntry extends EObject {
	/**
	 * Returns the value of the '<em><b>Status</b></em>' attribute. The literals are from the enumeration
	 * {@link org.eclipse.mylyn.reviews.core.model.RequirementStatus}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Status</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Status</em>' attribute.
	 * @see org.eclipse.mylyn.reviews.core.model.RequirementStatus
	 * @see #setStatus(RequirementStatus)
	 * @generated
	 */
	RequirementStatus getStatus();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IRequirementEntry#getStatus <em>Status</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Status</em>' attribute.
	 * @see org.eclipse.mylyn.reviews.core.model.RequirementStatus
	 * @see #getStatus()
	 * @generated
	 */
	void setStatus(RequirementStatus value);

	/**
	 * Returns the value of the '<em><b>By</b></em>' reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>By</em>' reference isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>By</em>' reference.
	 * @see #setBy(IUser)
	 * @generated
	 */
	IUser getBy();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IRequirementEntry#getBy <em>By</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>By</em>' reference.
	 * @see #getBy()
	 * @generated
	 */
	void setBy(IUser value);

} // IRequirementEntry
