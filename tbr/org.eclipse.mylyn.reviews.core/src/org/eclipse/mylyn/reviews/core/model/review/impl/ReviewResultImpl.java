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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
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
public class ReviewResultImpl extends EObjectImpl implements ReviewResult {
	/**
	 * The default value of the '{@link #getText() <em>Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getText()
	 * @generated
	 * @ordered
	 */
	protected static final String TEXT_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getText() <em>Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getText()
	 * @generated
	 * @ordered
	 */
	protected String text = TEXT_EDEFAULT;
	/**
	 * The default value of the '{@link #getRating() <em>Rating</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRating()
	 * @generated
	 * @ordered
	 */
	protected static final Rating RATING_EDEFAULT = Rating.NONE;
	/**
	 * The cached value of the '{@link #getRating() <em>Rating</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRating()
	 * @generated
	 * @ordered
	 */
	protected Rating rating = RATING_EDEFAULT;
	/**
	 * The default value of the '{@link #getReviewer() <em>Reviewer</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReviewer()
	 * @generated
	 * @ordered
	 */
	protected static final String REVIEWER_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getReviewer() <em>Reviewer</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReviewer()
	 * @generated
	 * @ordered
	 */
	protected String reviewer = REVIEWER_EDEFAULT;

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
	public String getText() {
		return text;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setText(String newText) {
		String oldText = text;
		text = newText;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewPackage.REVIEW_RESULT__TEXT, oldText, text));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Rating getRating() {
		return rating;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRating(Rating newRating) {
		Rating oldRating = rating;
		rating = newRating == null ? RATING_EDEFAULT : newRating;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewPackage.REVIEW_RESULT__RATING, oldRating, rating));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getReviewer() {
		return reviewer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setReviewer(String newReviewer) {
		String oldReviewer = reviewer;
		reviewer = newReviewer;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewPackage.REVIEW_RESULT__REVIEWER, oldReviewer, reviewer));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ReviewPackage.REVIEW_RESULT__TEXT:
				return getText();
			case ReviewPackage.REVIEW_RESULT__RATING:
				return getRating();
			case ReviewPackage.REVIEW_RESULT__REVIEWER:
				return getReviewer();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ReviewPackage.REVIEW_RESULT__TEXT:
				setText((String)newValue);
				return;
			case ReviewPackage.REVIEW_RESULT__RATING:
				setRating((Rating)newValue);
				return;
			case ReviewPackage.REVIEW_RESULT__REVIEWER:
				setReviewer((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ReviewPackage.REVIEW_RESULT__TEXT:
				setText(TEXT_EDEFAULT);
				return;
			case ReviewPackage.REVIEW_RESULT__RATING:
				setRating(RATING_EDEFAULT);
				return;
			case ReviewPackage.REVIEW_RESULT__REVIEWER:
				setReviewer(REVIEWER_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ReviewPackage.REVIEW_RESULT__TEXT:
				return TEXT_EDEFAULT == null ? text != null : !TEXT_EDEFAULT.equals(text);
			case ReviewPackage.REVIEW_RESULT__RATING:
				return rating != RATING_EDEFAULT;
			case ReviewPackage.REVIEW_RESULT__REVIEWER:
				return REVIEWER_EDEFAULT == null ? reviewer != null : !REVIEWER_EDEFAULT.equals(reviewer);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (text: ");
		result.append(text);
		result.append(", rating: ");
		result.append(rating);
		result.append(", reviewer: ");
		result.append(reviewer);
		result.append(')');
		return result.toString();
	}

} //ReviewResultImpl
