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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.core.model.review.ReviewPackage;
import org.eclipse.mylyn.reviews.core.model.review.ReviewResult;
import org.eclipse.mylyn.reviews.core.model.review.ScopeItem;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Review</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.mylyn.reviews.core.model.review.impl.ReviewImpl#getResult <em>Result</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.core.model.review.impl.ReviewImpl#getScope <em>Scope</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ReviewImpl extends CDOObjectImpl implements Review {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ReviewImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewPackage.Literals.REVIEW;
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
	public ReviewResult getResult() {
		return (ReviewResult)eGet(ReviewPackage.Literals.REVIEW__RESULT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setResult(ReviewResult newResult) {
		eSet(ReviewPackage.Literals.REVIEW__RESULT, newResult);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<ScopeItem> getScope() {
		return (EList<ScopeItem>)eGet(ReviewPackage.Literals.REVIEW__SCOPE, true);
	}

} //ReviewImpl
