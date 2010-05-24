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
package org.eclipse.mylyn.reviews.core.model.review.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.mylyn.reviews.core.model.review.Rating;
import org.eclipse.mylyn.reviews.core.model.review.ReviewPackage;
import org.eclipse.mylyn.reviews.core.model.review.ReviewResult;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Result</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.mylyn.reviews.core.model.review.impl.ReviewResultImpl#getText <em>Text</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.core.model.review.impl.ReviewResultImpl#getRating <em>Rating</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.core.model.review.impl.ReviewResultImpl#getReviewer <em>Reviewer</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ReviewResultImpl extends CDOObjectImpl implements ReviewResult {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ReviewResultImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewPackage.Literals.REVIEW_RESULT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected int eStaticFeatureCount() {
		return 0;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getText() {
		return (String)eGet(ReviewPackage.Literals.REVIEW_RESULT__TEXT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setText(String newText) {
		eSet(ReviewPackage.Literals.REVIEW_RESULT__TEXT, newText);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Rating getRating() {
		return (Rating)eGet(ReviewPackage.Literals.REVIEW_RESULT__RATING, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRating(Rating newRating) {
		eSet(ReviewPackage.Literals.REVIEW_RESULT__RATING, newRating);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getReviewer() {
		return (String)eGet(ReviewPackage.Literals.REVIEW_RESULT__REVIEWER, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setReviewer(String newReviewer) {
		eSet(ReviewPackage.Literals.REVIEW_RESULT__REVIEWER, newReviewer);
	}

} //ReviewResultImpl
