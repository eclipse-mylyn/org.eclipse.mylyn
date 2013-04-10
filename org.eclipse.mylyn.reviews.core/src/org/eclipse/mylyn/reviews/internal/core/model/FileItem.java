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

import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileRevision;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>File Item</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.FileItem#getBase <em>Base</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.FileItem#getTarget <em>Target</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.FileItem#getSet <em>Set</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class FileItem extends ReviewItem implements IFileItem {
	/**
	 * The cached value of the '{@link #getBase() <em>Base</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getBase()
	 * @generated
	 * @ordered
	 */
	protected IFileRevision base;

	/**
	 * The cached value of the '{@link #getTarget() <em>Target</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getTarget()
	 * @generated
	 * @ordered
	 */
	protected IFileRevision target;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected FileItem() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.FILE_ITEM;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public IReview getReview() {
		if (getSet() != null) {
			return getSet().getParentReview();
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IFileRevision getBase() {
		if (base != null && base.eIsProxy()) {
			InternalEObject oldBase = (InternalEObject) base;
			base = (IFileRevision) eResolveProxy(oldBase);
			if (base != oldBase) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.FILE_ITEM__BASE, oldBase,
							base));
			}
		}
		return base;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IFileRevision basicGetBase() {
		return base;
	}

	/**
	 * <!-- begin-user-doc --> Unmodifiable and not updated. <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public List<IComment> getAllComments() {
		BasicEList<IComment> all = new BasicEList<IComment>(getTopics());
		if (getBase() != null) {
			all.addAll(getBase().getTopics());
		}
		if (getTarget() != null) {
			all.addAll(getTarget().getTopics());
		}
		return all;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setBase(IFileRevision newBase) {
		IFileRevision oldBase = base;
		base = newBase;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.FILE_ITEM__BASE, oldBase, base));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IFileRevision getTarget() {
		if (target != null && target.eIsProxy()) {
			InternalEObject oldTarget = (InternalEObject) target;
			target = (IFileRevision) eResolveProxy(oldTarget);
			if (target != oldTarget) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.FILE_ITEM__TARGET,
							oldTarget, target));
			}
		}
		return target;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IFileRevision basicGetTarget() {
		return target;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setTarget(IFileRevision newTarget) {
		IFileRevision oldTarget = target;
		target = newTarget;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.FILE_ITEM__TARGET, oldTarget, target));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IReviewItemSet getSet() {
		if (eContainerFeatureID() != ReviewsPackage.FILE_ITEM__SET)
			return null;
		return (IReviewItemSet) eContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IReviewItemSet basicGetSet() {
		if (eContainerFeatureID() != ReviewsPackage.FILE_ITEM__SET)
			return null;
		return (IReviewItemSet) eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetSet(IReviewItemSet newSet, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject) newSet, ReviewsPackage.FILE_ITEM__SET, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setSet(IReviewItemSet newSet) {
		if (newSet != eInternalContainer()
				|| (eContainerFeatureID() != ReviewsPackage.FILE_ITEM__SET && newSet != null)) {
			if (EcoreUtil.isAncestor(this, newSet))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newSet != null)
				msgs = ((InternalEObject) newSet).eInverseAdd(this, ReviewsPackage.REVIEW_ITEM_SET__ITEMS,
						IReviewItemSet.class, msgs);
			msgs = basicSetSet(newSet, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.FILE_ITEM__SET, newSet, newSet));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReviewsPackage.FILE_ITEM__SET:
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			return basicSetSet((IReviewItemSet) otherEnd, msgs);
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
		case ReviewsPackage.FILE_ITEM__SET:
			return basicSetSet(null, msgs);
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
		case ReviewsPackage.FILE_ITEM__SET:
			return eInternalContainer().eInverseRemove(this, ReviewsPackage.REVIEW_ITEM_SET__ITEMS,
					IReviewItemSet.class, msgs);
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
		case ReviewsPackage.FILE_ITEM__BASE:
			if (resolve)
				return getBase();
			return basicGetBase();
		case ReviewsPackage.FILE_ITEM__TARGET:
			if (resolve)
				return getTarget();
			return basicGetTarget();
		case ReviewsPackage.FILE_ITEM__SET:
			if (resolve)
				return getSet();
			return basicGetSet();
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
		case ReviewsPackage.FILE_ITEM__BASE:
			setBase((IFileRevision) newValue);
			return;
		case ReviewsPackage.FILE_ITEM__TARGET:
			setTarget((IFileRevision) newValue);
			return;
		case ReviewsPackage.FILE_ITEM__SET:
			setSet((IReviewItemSet) newValue);
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
		case ReviewsPackage.FILE_ITEM__BASE:
			setBase((IFileRevision) null);
			return;
		case ReviewsPackage.FILE_ITEM__TARGET:
			setTarget((IFileRevision) null);
			return;
		case ReviewsPackage.FILE_ITEM__SET:
			setSet((IReviewItemSet) null);
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
		case ReviewsPackage.FILE_ITEM__BASE:
			return base != null;
		case ReviewsPackage.FILE_ITEM__TARGET:
			return target != null;
		case ReviewsPackage.FILE_ITEM__SET:
			return basicGetSet() != null;
		}
		return super.eIsSet(featureID);
	}

} //FileItem
