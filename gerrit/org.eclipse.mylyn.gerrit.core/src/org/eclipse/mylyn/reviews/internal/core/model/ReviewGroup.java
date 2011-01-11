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
package org.eclipse.mylyn.reviews.internal.core.model;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewGroup;
import org.eclipse.mylyn.reviews.core.model.ITaskReference;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Review Group</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewGroup#getReviews <em>Reviews</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewGroup#getReviewGroupTask <em>Review Group Task</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewGroup#getDescription <em>Description</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ReviewGroup extends ReviewComponent implements IReviewGroup {
	/**
	 * The cached value of the '{@link #getReviews() <em>Reviews</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReviews()
	 * @generated
	 * @ordered
	 */
	protected EList<IReview> reviews;

	/**
	 * The cached value of the '{@link #getReviewGroupTask() <em>Review Group Task</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReviewGroupTask()
	 * @generated
	 * @ordered
	 */
	protected ITaskReference reviewGroupTask;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ReviewGroup() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.REVIEW_GROUP;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<IReview> getReviews() {
		if (reviews == null) {
			reviews = new EObjectContainmentEList<IReview>(IReview.class, this, ReviewsPackage.REVIEW_GROUP__REVIEWS);
		}
		return reviews;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ITaskReference getReviewGroupTask() {
		return reviewGroupTask;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetReviewGroupTask(ITaskReference newReviewGroupTask, NotificationChain msgs) {
		ITaskReference oldReviewGroupTask = reviewGroupTask;
		reviewGroupTask = newReviewGroupTask;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					ReviewsPackage.REVIEW_GROUP__REVIEW_GROUP_TASK, oldReviewGroupTask, newReviewGroupTask);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setReviewGroupTask(ITaskReference newReviewGroupTask) {
		if (newReviewGroupTask != reviewGroupTask) {
			NotificationChain msgs = null;
			if (reviewGroupTask != null)
				msgs = ((InternalEObject) reviewGroupTask).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
						- ReviewsPackage.REVIEW_GROUP__REVIEW_GROUP_TASK, null, msgs);
			if (newReviewGroupTask != null)
				msgs = ((InternalEObject) newReviewGroupTask).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- ReviewsPackage.REVIEW_GROUP__REVIEW_GROUP_TASK, null, msgs);
			msgs = basicSetReviewGroupTask(newReviewGroupTask, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_GROUP__REVIEW_GROUP_TASK,
					newReviewGroupTask, newReviewGroupTask));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_GROUP__DESCRIPTION,
					oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReviewsPackage.REVIEW_GROUP__REVIEWS:
			return ((InternalEList<?>) getReviews()).basicRemove(otherEnd, msgs);
		case ReviewsPackage.REVIEW_GROUP__REVIEW_GROUP_TASK:
			return basicSetReviewGroupTask(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ReviewsPackage.REVIEW_GROUP__REVIEWS:
			return getReviews();
		case ReviewsPackage.REVIEW_GROUP__REVIEW_GROUP_TASK:
			return getReviewGroupTask();
		case ReviewsPackage.REVIEW_GROUP__DESCRIPTION:
			return getDescription();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ReviewsPackage.REVIEW_GROUP__REVIEWS:
			getReviews().clear();
			getReviews().addAll((Collection<? extends IReview>) newValue);
			return;
		case ReviewsPackage.REVIEW_GROUP__REVIEW_GROUP_TASK:
			setReviewGroupTask((ITaskReference) newValue);
			return;
		case ReviewsPackage.REVIEW_GROUP__DESCRIPTION:
			setDescription((String) newValue);
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
		case ReviewsPackage.REVIEW_GROUP__REVIEWS:
			getReviews().clear();
			return;
		case ReviewsPackage.REVIEW_GROUP__REVIEW_GROUP_TASK:
			setReviewGroupTask((ITaskReference) null);
			return;
		case ReviewsPackage.REVIEW_GROUP__DESCRIPTION:
			setDescription(DESCRIPTION_EDEFAULT);
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
		case ReviewsPackage.REVIEW_GROUP__REVIEWS:
			return reviews != null && !reviews.isEmpty();
		case ReviewsPackage.REVIEW_GROUP__REVIEW_GROUP_TASK:
			return reviewGroupTask != null;
		case ReviewsPackage.REVIEW_GROUP__DESCRIPTION:
			return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
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
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (description: "); //$NON-NLS-1$
		result.append(description);
		result.append(')');
		return result.toString();
	}

} //ReviewGroup
