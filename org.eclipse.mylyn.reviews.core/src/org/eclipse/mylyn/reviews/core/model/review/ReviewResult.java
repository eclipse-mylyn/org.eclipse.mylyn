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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Result</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.mylyn.reviews.core.model.review.ReviewResult#getText <em>Text</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.core.model.review.ReviewResult#getRating <em>Rating</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.core.model.review.ReviewResult#getReviewer <em>Reviewer</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.mylyn.reviews.core.model.review.ReviewPackage#getReviewResult()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface ReviewResult extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Text</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Text</em>' attribute.
	 * @see #setText(String)
	 * @see org.eclipse.mylyn.reviews.core.model.review.ReviewPackage#getReviewResult_Text()
	 * @model
	 * @generated
	 */
	String getText();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.review.ReviewResult#getText <em>Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Text</em>' attribute.
	 * @see #getText()
	 * @generated
	 */
	void setText(String value);

	/**
	 * Returns the value of the '<em><b>Rating</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.mylyn.reviews.core.model.review.Rating}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rating</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rating</em>' attribute.
	 * @see org.eclipse.mylyn.reviews.core.model.review.Rating
	 * @see #setRating(Rating)
	 * @see org.eclipse.mylyn.reviews.core.model.review.ReviewPackage#getReviewResult_Rating()
	 * @model
	 * @generated
	 */
	Rating getRating();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.review.ReviewResult#getRating <em>Rating</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rating</em>' attribute.
	 * @see org.eclipse.mylyn.reviews.core.model.review.Rating
	 * @see #getRating()
	 * @generated
	 */
	void setRating(Rating value);

	/**
	 * Returns the value of the '<em><b>Reviewer</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Reviewer</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Reviewer</em>' attribute.
	 * @see #setReviewer(String)
	 * @see org.eclipse.mylyn.reviews.core.model.review.ReviewPackage#getReviewResult_Reviewer()
	 * @model
	 * @generated
	 */
	String getReviewer();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.review.ReviewResult#getReviewer <em>Reviewer</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Reviewer</em>' attribute.
	 * @see #getReviewer()
	 * @generated
	 */
	void setReviewer(String value);

} // ReviewResult
