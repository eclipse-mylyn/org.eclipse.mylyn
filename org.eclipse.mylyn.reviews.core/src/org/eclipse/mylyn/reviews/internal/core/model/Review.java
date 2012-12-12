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

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.mylyn.reviews.core.model.IItem;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewState;
import org.eclipse.mylyn.reviews.core.model.ITaskReference;
import org.eclipse.mylyn.reviews.core.model.ITopic;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Review</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getTopics <em>Topics</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getItems <em>Items</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getReviewTask <em>Review Task</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getState <em>State</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class Review extends ReviewComponent implements IReview {
	/**
	 * The cached value of the '{@link #getTopics() <em>Topics</em>}' reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getTopics()
	 * @generated
	 * @ordered
	 */
	protected EList<ITopic> topics;

	/**
	 * The cached value of the '{@link #getItems() <em>Items</em>}' reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getItems()
	 * @generated
	 * @ordered
	 */
	protected EList<IReviewItem> items;

	/**
	 * The cached value of the '{@link #getReviewTask() <em>Review Task</em>}' containment reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getReviewTask()
	 * @generated
	 * @ordered
	 */
	protected ITaskReference reviewTask;

	/**
	 * The cached value of the '{@link #getState() <em>State</em>}' containment reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getState()
	 * @generated
	 * @ordered
	 */
	protected IReviewState state;

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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Review() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.REVIEW;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<ITopic> getTopics() {
		if (topics == null) {
			topics = new EObjectResolvingEList<ITopic>(ITopic.class, this, ReviewsPackage.REVIEW__TOPICS);
		}
		return topics;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<IReviewItem> getItems() {
		if (items == null) {
			items = new EObjectResolvingEList<IReviewItem>(IReviewItem.class, this, ReviewsPackage.REVIEW__ITEMS);
		}
		return items;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ITaskReference getReviewTask() {
		if (reviewTask != null && reviewTask.eIsProxy()) {
			InternalEObject oldReviewTask = (InternalEObject) reviewTask;
			reviewTask = (ITaskReference) eResolveProxy(oldReviewTask);
			if (reviewTask != oldReviewTask) {
				InternalEObject newReviewTask = (InternalEObject) reviewTask;
				NotificationChain msgs = oldReviewTask.eInverseRemove(this, EOPPOSITE_FEATURE_BASE
						- ReviewsPackage.REVIEW__REVIEW_TASK, null, null);
				if (newReviewTask.eInternalContainer() == null) {
					msgs = newReviewTask.eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ReviewsPackage.REVIEW__REVIEW_TASK,
							null, msgs);
				}
				if (msgs != null)
					msgs.dispatch();
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.REVIEW__REVIEW_TASK,
							oldReviewTask, reviewTask));
			}
		}
		return reviewTask;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ITaskReference basicGetReviewTask() {
		return reviewTask;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetReviewTask(ITaskReference newReviewTask, NotificationChain msgs) {
		ITaskReference oldReviewTask = reviewTask;
		reviewTask = newReviewTask;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					ReviewsPackage.REVIEW__REVIEW_TASK, oldReviewTask, newReviewTask);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setReviewTask(ITaskReference newReviewTask) {
		if (newReviewTask != reviewTask) {
			NotificationChain msgs = null;
			if (reviewTask != null)
				msgs = ((InternalEObject) reviewTask).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
						- ReviewsPackage.REVIEW__REVIEW_TASK, null, msgs);
			if (newReviewTask != null)
				msgs = ((InternalEObject) newReviewTask).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- ReviewsPackage.REVIEW__REVIEW_TASK, null, msgs);
			msgs = basicSetReviewTask(newReviewTask, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW__REVIEW_TASK, newReviewTask,
					newReviewTask));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IReviewState getState() {
		if (state != null && state.eIsProxy()) {
			InternalEObject oldState = (InternalEObject) state;
			state = (IReviewState) eResolveProxy(oldState);
			if (state != oldState) {
				InternalEObject newState = (InternalEObject) state;
				NotificationChain msgs = oldState.eInverseRemove(this, EOPPOSITE_FEATURE_BASE
						- ReviewsPackage.REVIEW__STATE, null, null);
				if (newState.eInternalContainer() == null) {
					msgs = newState.eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ReviewsPackage.REVIEW__STATE, null, msgs);
				}
				if (msgs != null)
					msgs.dispatch();
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.REVIEW__STATE, oldState,
							state));
			}
		}
		return state;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IReviewState basicGetState() {
		return state;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetState(IReviewState newState, NotificationChain msgs) {
		IReviewState oldState = state;
		state = newState;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					ReviewsPackage.REVIEW__STATE, oldState, newState);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setState(IReviewState newState) {
		if (newState != state) {
			NotificationChain msgs = null;
			if (state != null)
				msgs = ((InternalEObject) state).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
						- ReviewsPackage.REVIEW__STATE, null, msgs);
			if (newState != null)
				msgs = ((InternalEObject) newState).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- ReviewsPackage.REVIEW__STATE, null, msgs);
			msgs = basicSetState(newState, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW__STATE, newState, newState));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReviewsPackage.REVIEW__REVIEW_TASK:
			return basicSetReviewTask(null, msgs);
		case ReviewsPackage.REVIEW__STATE:
			return basicSetState(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ReviewsPackage.REVIEW__TOPICS:
			return getTopics();
		case ReviewsPackage.REVIEW__ITEMS:
			return getItems();
		case ReviewsPackage.REVIEW__REVIEW_TASK:
			if (resolve)
				return getReviewTask();
			return basicGetReviewTask();
		case ReviewsPackage.REVIEW__STATE:
			if (resolve)
				return getState();
			return basicGetState();
		case ReviewsPackage.REVIEW__ID:
			return getId();
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
		case ReviewsPackage.REVIEW__TOPICS:
			getTopics().clear();
			getTopics().addAll((Collection<? extends ITopic>) newValue);
			return;
		case ReviewsPackage.REVIEW__ITEMS:
			getItems().clear();
			getItems().addAll((Collection<? extends IReviewItem>) newValue);
			return;
		case ReviewsPackage.REVIEW__REVIEW_TASK:
			setReviewTask((ITaskReference) newValue);
			return;
		case ReviewsPackage.REVIEW__STATE:
			setState((IReviewState) newValue);
			return;
		case ReviewsPackage.REVIEW__ID:
			setId((String) newValue);
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
		case ReviewsPackage.REVIEW__TOPICS:
			getTopics().clear();
			return;
		case ReviewsPackage.REVIEW__ITEMS:
			getItems().clear();
			return;
		case ReviewsPackage.REVIEW__REVIEW_TASK:
			setReviewTask((ITaskReference) null);
			return;
		case ReviewsPackage.REVIEW__STATE:
			setState((IReviewState) null);
			return;
		case ReviewsPackage.REVIEW__ID:
			setId(ID_EDEFAULT);
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
		case ReviewsPackage.REVIEW__TOPICS:
			return topics != null && !topics.isEmpty();
		case ReviewsPackage.REVIEW__ITEMS:
			return items != null && !items.isEmpty();
		case ReviewsPackage.REVIEW__REVIEW_TASK:
			return reviewTask != null;
		case ReviewsPackage.REVIEW__STATE:
			return state != null;
		case ReviewsPackage.REVIEW__ID:
			return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
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
		result.append(" (id: "); //$NON-NLS-1$
		result.append(id);
		result.append(')');
		return result.toString();
	}

} //Review
