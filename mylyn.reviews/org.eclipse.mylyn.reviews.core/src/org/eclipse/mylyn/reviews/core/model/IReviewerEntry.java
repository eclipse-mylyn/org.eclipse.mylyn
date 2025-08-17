/**
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
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

import java.util.Map;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Reviewer Entry</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReviewerEntry#getApprovals <em>Approvals</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public interface IReviewerEntry extends EObject {
	/**
	 * Returns the value of the '<em><b>Approvals</b></em>' map. The key is of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IApprovalType}, and the value is of type {@link java.lang.Integer}, <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of the '<em>Approvals</em>' containment reference list isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Approvals</em>' map.
	 * @generated
	 */
	Map<IApprovalType, Integer> getApprovals();

} // IReviewerEntry
