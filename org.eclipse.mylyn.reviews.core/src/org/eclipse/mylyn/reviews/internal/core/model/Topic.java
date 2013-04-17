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
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.core.model.ITopicContainer;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Topic</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Topic#getLocations <em>Locations</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Topic#getComments <em>Comments</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Topic#getReview <em>Review</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Topic#getTitle <em>Title</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Topic#getItem <em>Item</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class Topic extends Comment implements ITopic {
	/**
	 * The cached value of the '{@link #getLocations() <em>Locations</em>}' containment reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getLocations()
	 * @generated
	 * @ordered
	 */
	protected EList<ILocation> locations;

	/**
	 * The cached value of the '{@link #getComments() <em>Comments</em>}' containment reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getComments()
	 * @generated
	 * @ordered
	 */
	protected EList<IComment> comments;

	/**
	 * The cached value of the '{@link #getReview() <em>Review</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getReview()
	 * @generated
	 * @ordered
	 */
	protected IReview review;

	/**
	 * The default value of the '{@link #getTitle() <em>Title</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected static final String TITLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTitle() <em>Title</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected String title = TITLE_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Topic() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.TOPIC;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<ILocation> getLocations() {
		if (locations == null) {
			locations = new EObjectContainmentEList.Resolving<ILocation>(ILocation.class, this,
					ReviewsPackage.TOPIC__LOCATIONS);
		}
		return locations;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<IComment> getComments() {
		if (comments == null) {
			comments = new EObjectContainmentWithInverseEList.Resolving<IComment>(IComment.class, this,
					ReviewsPackage.TOPIC__COMMENTS, ReviewsPackage.COMMENT__PARENT_TOPIC);
		}
		return comments;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IReview getReview() {
		if (review != null && review.eIsProxy()) {
			InternalEObject oldReview = (InternalEObject) review;
			review = (IReview) eResolveProxy(oldReview);
			if (review != oldReview) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.TOPIC__REVIEW, oldReview,
							review));
			}
		}
		return review;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IReview basicGetReview() {
		return review;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setReview(IReview newReview) {
		IReview oldReview = review;
		review = newReview;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.TOPIC__REVIEW, oldReview, review));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setTitle(String newTitle) {
		String oldTitle = title;
		title = newTitle;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.TOPIC__TITLE, oldTitle, title));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ITopicContainer getItem() {
		if (eContainerFeatureID() != ReviewsPackage.TOPIC__ITEM)
			return null;
		return (ITopicContainer) eContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ITopicContainer basicGetItem() {
		if (eContainerFeatureID() != ReviewsPackage.TOPIC__ITEM)
			return null;
		return (ITopicContainer) eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetItem(ITopicContainer newItem, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject) newItem, ReviewsPackage.TOPIC__ITEM, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setItem(ITopicContainer newItem) {
		if (newItem != eInternalContainer() || (eContainerFeatureID() != ReviewsPackage.TOPIC__ITEM && newItem != null)) {
			if (EcoreUtil.isAncestor(this, newItem))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newItem != null)
				msgs = ((InternalEObject) newItem).eInverseAdd(this, ReviewsPackage.TOPIC_CONTAINER__DIRECT_TOPICS,
						ITopicContainer.class, msgs);
			msgs = basicSetItem(newItem, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.TOPIC__ITEM, newItem, newItem));
	}

	/**
	 * <!-- begin-user-doc --> Returns 0; base comments aren't ordered. <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public long getIndex() {
		long index = Long.MAX_VALUE;
		for (ILocation location : getLocations()) {
			index = Math.min(index, location.getIndex());
		}
		return index;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReviewsPackage.TOPIC__COMMENTS:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getComments()).basicAdd(otherEnd, msgs);
		case ReviewsPackage.TOPIC__ITEM:
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			return basicSetItem((ITopicContainer) otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReviewsPackage.TOPIC__LOCATIONS:
			return ((InternalEList<?>) getLocations()).basicRemove(otherEnd, msgs);
		case ReviewsPackage.TOPIC__COMMENTS:
			return ((InternalEList<?>) getComments()).basicRemove(otherEnd, msgs);
		case ReviewsPackage.TOPIC__ITEM:
			return basicSetItem(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
		case ReviewsPackage.TOPIC__ITEM:
			return eInternalContainer().eInverseRemove(this, ReviewsPackage.TOPIC_CONTAINER__DIRECT_TOPICS,
					ITopicContainer.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ReviewsPackage.TOPIC__LOCATIONS:
			return getLocations();
		case ReviewsPackage.TOPIC__COMMENTS:
			return getComments();
		case ReviewsPackage.TOPIC__REVIEW:
			if (resolve)
				return getReview();
			return basicGetReview();
		case ReviewsPackage.TOPIC__TITLE:
			return getTitle();
		case ReviewsPackage.TOPIC__ITEM:
			if (resolve)
				return getItem();
			return basicGetItem();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ReviewsPackage.TOPIC__LOCATIONS:
			getLocations().clear();
			getLocations().addAll((Collection<? extends ILocation>) newValue);
			return;
		case ReviewsPackage.TOPIC__COMMENTS:
			getComments().clear();
			getComments().addAll((Collection<? extends IComment>) newValue);
			return;
		case ReviewsPackage.TOPIC__REVIEW:
			setReview((IReview) newValue);
			return;
		case ReviewsPackage.TOPIC__TITLE:
			setTitle((String) newValue);
			return;
		case ReviewsPackage.TOPIC__ITEM:
			setItem((ITopicContainer) newValue);
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
		case ReviewsPackage.TOPIC__LOCATIONS:
			getLocations().clear();
			return;
		case ReviewsPackage.TOPIC__COMMENTS:
			getComments().clear();
			return;
		case ReviewsPackage.TOPIC__REVIEW:
			setReview((IReview) null);
			return;
		case ReviewsPackage.TOPIC__TITLE:
			setTitle(TITLE_EDEFAULT);
			return;
		case ReviewsPackage.TOPIC__ITEM:
			setItem((ITopicContainer) null);
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
		case ReviewsPackage.TOPIC__LOCATIONS:
			return locations != null && !locations.isEmpty();
		case ReviewsPackage.TOPIC__COMMENTS:
			return comments != null && !comments.isEmpty();
		case ReviewsPackage.TOPIC__REVIEW:
			return review != null;
		case ReviewsPackage.TOPIC__TITLE:
			return TITLE_EDEFAULT == null ? title != null : !TITLE_EDEFAULT.equals(title);
		case ReviewsPackage.TOPIC__ITEM:
			return basicGetItem() != null;
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
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (title: "); //$NON-NLS-1$
		result.append(title);
		result.append(')');
		return result.toString();
	}

} //Topic
