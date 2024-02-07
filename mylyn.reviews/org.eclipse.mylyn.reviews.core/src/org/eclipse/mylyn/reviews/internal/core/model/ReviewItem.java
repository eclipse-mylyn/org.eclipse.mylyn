/**
 * Copyright (c) 2011, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.internal.core.model;

import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IUser;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Review Item</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItem#getAddedBy <em>Added By</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItem#getCommittedBy <em>Committed By</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItem#getReview <em>Review</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItem#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItem#getId <em>Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItem#getReference <em>Reference</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class ReviewItem extends CommentContainer implements IReviewItem {
	/**
	 * The cached value of the '{@link #getAddedBy() <em>Added By</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getAddedBy()
	 * @generated
	 * @ordered
	 */
	protected IUser addedBy;

	/**
	 * The cached value of the '{@link #getCommittedBy() <em>Committed By</em>}' containment reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getCommittedBy()
	 * @generated
	 * @ordered
	 */
	protected IUser committedBy;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getReference() <em>Reference</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getReference()
	 * @generated
	 * @ordered
	 */
	protected static final String REFERENCE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getReference() <em>Reference</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getReference()
	 * @generated
	 * @ordered
	 */
	protected String reference = REFERENCE_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ReviewItem() {
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.REVIEW_ITEM;
	}

	/**
	 * <!-- begin-user-doc --> Unmodifiable and not updated. <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public List<IComment> getAllComments() {
		return getComments();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IUser getAddedBy() {
		if (addedBy != null && addedBy.eIsProxy()) {
			InternalEObject oldAddedBy = (InternalEObject) addedBy;
			addedBy = (IUser) eResolveProxy(oldAddedBy);
			if (addedBy != oldAddedBy) {
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.REVIEW_ITEM__ADDED_BY,
							oldAddedBy, addedBy));
				}
			}
		}
		return addedBy;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IUser basicGetAddedBy() {
		return addedBy;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setAddedBy(IUser newAddedBy) {
		IUser oldAddedBy = addedBy;
		addedBy = newAddedBy;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_ITEM__ADDED_BY, oldAddedBy,
					addedBy));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IUser getCommittedBy() {
		if (committedBy != null && committedBy.eIsProxy()) {
			InternalEObject oldCommittedBy = (InternalEObject) committedBy;
			committedBy = (IUser) eResolveProxy(oldCommittedBy);
			if (committedBy != oldCommittedBy) {
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.REVIEW_ITEM__COMMITTED_BY,
							oldCommittedBy, committedBy));
				}
			}
		}
		return committedBy;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IUser basicGetCommittedBy() {
		return committedBy;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setCommittedBy(IUser newCommittedBy) {
		IUser oldCommittedBy = committedBy;
		committedBy = newCommittedBy;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_ITEM__COMMITTED_BY,
					oldCommittedBy, committedBy));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IReview getReview() {
		// TODO: implement this method to return the 'Review' reference
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_ITEM__NAME, oldName, name));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_ITEM__ID, oldId, id));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getReference() {
		return reference;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setReference(String newReference) {
		String oldReference = reference;
		reference = newReference;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_ITEM__REFERENCE, oldReference,
					reference));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public IComment createComment(ILocation initalLocation, String commentText) {
		IComment comment = super.createComment(initalLocation, commentText);
		if (getReview() != null && getReview().getRepository() != null) {
			comment.setAuthor(getReview().getRepository().getAccount());
		}
		return comment;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ReviewsPackage.REVIEW_ITEM__ADDED_BY:
				if (resolve) {
					return getAddedBy();
				}
				return basicGetAddedBy();
			case ReviewsPackage.REVIEW_ITEM__COMMITTED_BY:
				if (resolve) {
					return getCommittedBy();
				}
				return basicGetCommittedBy();
			case ReviewsPackage.REVIEW_ITEM__REVIEW:
				return getReview();
			case ReviewsPackage.REVIEW_ITEM__NAME:
				return getName();
			case ReviewsPackage.REVIEW_ITEM__ID:
				return getId();
			case ReviewsPackage.REVIEW_ITEM__REFERENCE:
				return getReference();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ReviewsPackage.REVIEW_ITEM__ADDED_BY:
				setAddedBy((IUser) newValue);
				return;
			case ReviewsPackage.REVIEW_ITEM__COMMITTED_BY:
				setCommittedBy((IUser) newValue);
				return;
			case ReviewsPackage.REVIEW_ITEM__NAME:
				setName((String) newValue);
				return;
			case ReviewsPackage.REVIEW_ITEM__ID:
				setId((String) newValue);
				return;
			case ReviewsPackage.REVIEW_ITEM__REFERENCE:
				setReference((String) newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ReviewsPackage.REVIEW_ITEM__ADDED_BY:
				setAddedBy((IUser) null);
				return;
			case ReviewsPackage.REVIEW_ITEM__COMMITTED_BY:
				setCommittedBy((IUser) null);
				return;
			case ReviewsPackage.REVIEW_ITEM__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ReviewsPackage.REVIEW_ITEM__ID:
				setId(ID_EDEFAULT);
				return;
			case ReviewsPackage.REVIEW_ITEM__REFERENCE:
				setReference(REFERENCE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ReviewsPackage.REVIEW_ITEM__ADDED_BY:
				return addedBy != null;
			case ReviewsPackage.REVIEW_ITEM__COMMITTED_BY:
				return committedBy != null;
			case ReviewsPackage.REVIEW_ITEM__REVIEW:
				return getReview() != null;
			case ReviewsPackage.REVIEW_ITEM__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ReviewsPackage.REVIEW_ITEM__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case ReviewsPackage.REVIEW_ITEM__REFERENCE:
				return REFERENCE_EDEFAULT == null ? reference != null : !REFERENCE_EDEFAULT.equals(reference);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) {
			return super.toString();
		}

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (name: "); //$NON-NLS-1$
		result.append(name);
		result.append(", id: "); //$NON-NLS-1$
		result.append(id);
		result.append(", reference: "); //$NON-NLS-1$
		result.append(reference);
		result.append(')');
		return result.toString();
	}

} //ReviewItem
