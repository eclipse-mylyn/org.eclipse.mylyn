/**
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.core.model;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Requirement Review State</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRequirementReviewState#getStatus <em>Status</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IRequirementReviewState extends IReviewState {
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
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IRequirementReviewState#getStatus
	 * <em>Status</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Status</em>' attribute.
	 * @see org.eclipse.mylyn.reviews.core.model.RequirementStatus
	 * @see #getStatus()
	 * @generated
	 */
	void setStatus(RequirementStatus value);

} // IRequirementReviewState
