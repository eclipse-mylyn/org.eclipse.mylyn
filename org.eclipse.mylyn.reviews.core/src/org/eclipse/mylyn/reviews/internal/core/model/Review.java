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
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IDated;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewState;
import org.eclipse.mylyn.reviews.core.model.ITaskReference;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.core.model.IUser;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Review</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getCreationDate <em>Creation Date</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getModificationDate <em>Modification Date</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getItems <em>Items</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getReviewTask <em>Review Task</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getState <em>State</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getId <em>Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getOwner <em>Owner</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class Review extends TopicContainer implements IReview {
	/**
	 * The default value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getCreationDate()
	 * @generated
	 * @ordered
	 */
	protected static final Date CREATION_DATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getCreationDate()
	 * @generated
	 * @ordered
	 */
	protected Date creationDate = CREATION_DATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getModificationDate() <em>Modification Date</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getModificationDate()
	 * @generated
	 * @ordered
	 */
	protected static final Date MODIFICATION_DATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getModificationDate() <em>Modification Date</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getModificationDate()
	 * @generated
	 * @ordered
	 */
	protected Date modificationDate = MODIFICATION_DATE_EDEFAULT;

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
	 * The cached value of the '{@link #getOwner() <em>Owner</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getOwner()
	 * @generated
	 * @ordered
	 */
	protected IUser owner;

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
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setCreationDate(Date newCreationDate) {
		Date oldCreationDate = creationDate;
		creationDate = newCreationDate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW__CREATION_DATE,
					oldCreationDate, creationDate));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Date getModificationDate() {
		return modificationDate;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setModificationDate(Date newModificationDate) {
		Date oldModificationDate = modificationDate;
		modificationDate = newModificationDate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW__MODIFICATION_DATE,
					oldModificationDate, modificationDate));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public Date getLastChangeDate() {
		if (getModificationDate() != null) {
			return getModificationDate();
		}
		return getCreationDate();
	}

	/**
	 * <!-- begin-user-doc --> Unmodifiable and not updated. <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public List<IComment> getAllComments() {
		BasicEList<IComment> all = new BasicEList<IComment>(getTopics());
		for (ITopic topic : getTopics()) {
			all.addAll(topic.getReplies());
		}
		for (IReviewItem item : getItems()) {
			all.addAll(item.getAllComments());
		}
		return all;
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
	public IUser getOwner() {
		if (owner != null && owner.eIsProxy()) {
			InternalEObject oldOwner = (InternalEObject) owner;
			owner = (IUser) eResolveProxy(oldOwner);
			if (owner != oldOwner) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.REVIEW__OWNER, oldOwner,
							owner));
			}
		}
		return owner;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IUser basicGetOwner() {
		return owner;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setOwner(IUser newOwner) {
		IUser oldOwner = owner;
		owner = newOwner;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW__OWNER, oldOwner, owner));
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
		case ReviewsPackage.REVIEW__CREATION_DATE:
			return getCreationDate();
		case ReviewsPackage.REVIEW__MODIFICATION_DATE:
			return getModificationDate();
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
		case ReviewsPackage.REVIEW__OWNER:
			if (resolve)
				return getOwner();
			return basicGetOwner();
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
		case ReviewsPackage.REVIEW__CREATION_DATE:
			setCreationDate((Date) newValue);
			return;
		case ReviewsPackage.REVIEW__MODIFICATION_DATE:
			setModificationDate((Date) newValue);
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
		case ReviewsPackage.REVIEW__OWNER:
			setOwner((IUser) newValue);
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
		case ReviewsPackage.REVIEW__CREATION_DATE:
			setCreationDate(CREATION_DATE_EDEFAULT);
			return;
		case ReviewsPackage.REVIEW__MODIFICATION_DATE:
			setModificationDate(MODIFICATION_DATE_EDEFAULT);
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
		case ReviewsPackage.REVIEW__OWNER:
			setOwner((IUser) null);
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
		case ReviewsPackage.REVIEW__CREATION_DATE:
			return CREATION_DATE_EDEFAULT == null ? creationDate != null : !CREATION_DATE_EDEFAULT.equals(creationDate);
		case ReviewsPackage.REVIEW__MODIFICATION_DATE:
			return MODIFICATION_DATE_EDEFAULT == null
					? modificationDate != null
					: !MODIFICATION_DATE_EDEFAULT.equals(modificationDate);
		case ReviewsPackage.REVIEW__ITEMS:
			return items != null && !items.isEmpty();
		case ReviewsPackage.REVIEW__REVIEW_TASK:
			return reviewTask != null;
		case ReviewsPackage.REVIEW__STATE:
			return state != null;
		case ReviewsPackage.REVIEW__ID:
			return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
		case ReviewsPackage.REVIEW__OWNER:
			return owner != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		if (baseClass == IDated.class) {
			switch (derivedFeatureID) {
			case ReviewsPackage.REVIEW__CREATION_DATE:
				return ReviewsPackage.DATED__CREATION_DATE;
			case ReviewsPackage.REVIEW__MODIFICATION_DATE:
				return ReviewsPackage.DATED__MODIFICATION_DATE;
			default:
				return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		if (baseClass == IDated.class) {
			switch (baseFeatureID) {
			case ReviewsPackage.DATED__CREATION_DATE:
				return ReviewsPackage.REVIEW__CREATION_DATE;
			case ReviewsPackage.DATED__MODIFICATION_DATE:
				return ReviewsPackage.REVIEW__MODIFICATION_DATE;
			default:
				return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
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
		result.append(" (creationDate: "); //$NON-NLS-1$
		result.append(creationDate);
		result.append(", modificationDate: "); //$NON-NLS-1$
		result.append(modificationDate);
		result.append(", id: "); //$NON-NLS-1$
		result.append(id);
		result.append(')');
		return result.toString();
	}

} //Review
