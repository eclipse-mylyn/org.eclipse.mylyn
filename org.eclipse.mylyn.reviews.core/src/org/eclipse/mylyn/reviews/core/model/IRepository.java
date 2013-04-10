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

import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Repository</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRepository#getApprovalTypes <em>Approval Types</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IRepository#getReviewStates <em>Review States</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IRepository extends IReviewGroup {
	/**
	 * Returns the value of the '<em><b>Approval Types</b></em>' containment reference list. The list contents are of
	 * type {@link org.eclipse.mylyn.reviews.core.model.IApprovalType}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Approval Types</em>' containment reference list isn't clear, there really should be
	 * more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Approval Types</em>' containment reference list.
	 * @generated
	 */
	List<IApprovalType> getApprovalTypes();

	/**
	 * Returns the value of the '<em><b>Review States</b></em>' containment reference list. The list contents are of
	 * type {@link org.eclipse.mylyn.reviews.core.model.IReviewState}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Review States</em>' containment reference list isn't clear, there really should be
	 * more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Review States</em>' containment reference list.
	 * @generated
	 */
	List<IReviewState> getReviewStates();

} // IRepository
