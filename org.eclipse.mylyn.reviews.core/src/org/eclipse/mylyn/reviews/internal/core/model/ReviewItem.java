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
import java.util.Date;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.core.model.IUser;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Review Item</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItem#getAddedBy <em>Added By</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItem#getReview <em>Review</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItem#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItem#getTopics <em>Topics</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItem#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ReviewItem extends ReviewComponent implements IReviewItem {
	/**
	 * The cached value of the '{@link #getAddedBy() <em>Added By</em>}' reference.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getAddedBy()
	 * @generated
	 * @ordered
	 */
	protected IUser addedBy;

	/**
	 * The cached value of the '{@link #getReview() <em>Review</em>}' reference.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getReview()
	 * @generated
	 * @ordered
	 */
	protected IReview review;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getTopics() <em>Topics</em>}' reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getTopics()
	 * @generated
	 * @ordered
	 */
	protected EList<ITopic> topics;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected ReviewItem() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.REVIEW_ITEM;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public IUser getAddedBy() {
		if (addedBy != null && addedBy.eIsProxy()) {
			InternalEObject oldAddedBy = (InternalEObject) addedBy;
			addedBy = (IUser) eResolveProxy(oldAddedBy);
			if (addedBy != oldAddedBy) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.REVIEW_ITEM__ADDED_BY,
							oldAddedBy, addedBy));
			}
		}
		return addedBy;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public IUser basicGetAddedBy() {
		return addedBy;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setAddedBy(IUser newAddedBy) {
		IUser oldAddedBy = addedBy;
		addedBy = newAddedBy;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_ITEM__ADDED_BY, oldAddedBy,
					addedBy));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public IReview getReview() {
		if (review != null && review.eIsProxy()) {
			InternalEObject oldReview = (InternalEObject) review;
			review = (IReview) eResolveProxy(oldReview);
			if (review != oldReview) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.REVIEW_ITEM__REVIEW,
							oldReview, review));
			}
		}
		return review;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public IReview basicGetReview() {
		return review;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setReview(IReview newReview) {
		IReview oldReview = review;
		review = newReview;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_ITEM__REVIEW, oldReview, review));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_ITEM__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public List<ITopic> getTopics() {
		if (topics == null) {
			topics = new EObjectWithInverseResolvingEList<ITopic>(ITopic.class, this,
					ReviewsPackage.REVIEW_ITEM__TOPICS, ReviewsPackage.TOPIC__ITEM);
		}
		return topics;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_ITEM__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public ITopic createTopicComment(ILocation initalLocation, String commentText) {
		ITopic topic = ReviewsFactory.eINSTANCE.createTopic();
		topic.setDraft(true);
		IUser user = getAddedBy();
		if (user == null) {
			user = ReviewsFactory.eINSTANCE.createUser();
			user.setDisplayName("<Undefined>"); //$NON-NLS-1$
		}
		topic.setAuthor(user);
		topic.setDescription(commentText);
		topic.getLocations().add(initalLocation);

		IComment comment = ReviewsFactory.eINSTANCE.createComment();
		comment.setDescription(topic.getDescription());
		comment.setAuthor(topic.getAuthor());
		Date created = new Date();
		comment.setCreationDate(created);
		topic.setCreationDate(created);
		topic.getComments().add(comment);
		getTopics().add(topic);
		return topic;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReviewsPackage.REVIEW_ITEM__TOPICS:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getTopics()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReviewsPackage.REVIEW_ITEM__TOPICS:
			return ((InternalEList<?>) getTopics()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ReviewsPackage.REVIEW_ITEM__ADDED_BY:
			if (resolve)
				return getAddedBy();
			return basicGetAddedBy();
		case ReviewsPackage.REVIEW_ITEM__REVIEW:
			if (resolve)
				return getReview();
			return basicGetReview();
		case ReviewsPackage.REVIEW_ITEM__NAME:
			return getName();
		case ReviewsPackage.REVIEW_ITEM__TOPICS:
			return getTopics();
		case ReviewsPackage.REVIEW_ITEM__ID:
			return getId();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ReviewsPackage.REVIEW_ITEM__ADDED_BY:
			setAddedBy((IUser) newValue);
			return;
		case ReviewsPackage.REVIEW_ITEM__REVIEW:
			setReview((IReview) newValue);
			return;
		case ReviewsPackage.REVIEW_ITEM__NAME:
			setName((String) newValue);
			return;
		case ReviewsPackage.REVIEW_ITEM__TOPICS:
			getTopics().clear();
			getTopics().addAll((Collection<? extends ITopic>) newValue);
			return;
		case ReviewsPackage.REVIEW_ITEM__ID:
			setId((String) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case ReviewsPackage.REVIEW_ITEM__ADDED_BY:
			setAddedBy((IUser) null);
			return;
		case ReviewsPackage.REVIEW_ITEM__REVIEW:
			setReview((IReview) null);
			return;
		case ReviewsPackage.REVIEW_ITEM__NAME:
			setName(NAME_EDEFAULT);
			return;
		case ReviewsPackage.REVIEW_ITEM__TOPICS:
			getTopics().clear();
			return;
		case ReviewsPackage.REVIEW_ITEM__ID:
			setId(ID_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case ReviewsPackage.REVIEW_ITEM__ADDED_BY:
			return addedBy != null;
		case ReviewsPackage.REVIEW_ITEM__REVIEW:
			return review != null;
		case ReviewsPackage.REVIEW_ITEM__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case ReviewsPackage.REVIEW_ITEM__TOPICS:
			return topics != null && !topics.isEmpty();
		case ReviewsPackage.REVIEW_ITEM__ID:
			return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: "); //$NON-NLS-1$
		result.append(name);
		result.append(", id: "); //$NON-NLS-1$
		result.append(id);
		result.append(')');
		return result.toString();
	}

} //ReviewItem
