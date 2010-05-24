/**
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 */
package org.eclipse.mylyn.reviews.core.model.review;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Review</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.mylyn.reviews.core.model.review.Review#getResult <em>Result</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.core.model.review.Review#getScope <em>Scope</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.mylyn.reviews.core.model.review.ReviewPackage#getReview()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Review extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Result</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Result</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Result</em>' reference.
	 * @see #setResult(ReviewResult)
	 * @see org.eclipse.mylyn.reviews.core.model.review.ReviewPackage#getReview_Result()
	 * @model
	 * @generated
	 */
	ReviewResult getResult();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.review.Review#getResult <em>Result</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Result</em>' reference.
	 * @see #getResult()
	 * @generated
	 */
	void setResult(ReviewResult value);

	/**
	 * Returns the value of the '<em><b>Scope</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.reviews.core.model.review.ScopeItem}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Scope</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Scope</em>' reference list.
	 * @see org.eclipse.mylyn.reviews.core.model.review.ReviewPackage#getReview_Scope()
	 * @model
	 * @generated
	 */
	EList<ScopeItem> getScope();

} // Review
