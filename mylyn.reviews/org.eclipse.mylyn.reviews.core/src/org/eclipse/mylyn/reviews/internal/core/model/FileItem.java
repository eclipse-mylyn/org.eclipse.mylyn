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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
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
	 * The cached value of the '{@link #getBase() <em>Base</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getBase()
	 * @generated
	 * @ordered
	 */
	protected IFileVersion base;

	/**
	 * The cached value of the '{@link #getTarget() <em>Target</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getTarget()
	 * @generated
	 * @ordered
	 */
	protected IFileVersion target;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected FileItem() {
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
	 * @generated
	 */
	@Override
	public IFileVersion getBase() {
		if (base != null && base.eIsProxy()) {
			InternalEObject oldBase = (InternalEObject) base;
			base = (IFileVersion) eResolveProxy(oldBase);
			if (base != oldBase) {
				InternalEObject newBase = (InternalEObject) base;
				NotificationChain msgs = oldBase.eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE - ReviewsPackage.FILE_ITEM__BASE, null, null);
				if (newBase.eInternalContainer() == null) {
					msgs = newBase.eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ReviewsPackage.FILE_ITEM__BASE, null,
							msgs);
				}
				if (msgs != null) {
					msgs.dispatch();
				}
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.FILE_ITEM__BASE, oldBase,
							base));
				}
			}
		}
		return base;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IFileVersion basicGetBase() {
		return base;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetBase(IFileVersion newBase, NotificationChain msgs) {
		IFileVersion oldBase = base;
		base = newBase;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					ReviewsPackage.FILE_ITEM__BASE, oldBase, newBase);
			if (msgs == null) {
				msgs = notification;
			} else {
				msgs.add(notification);
			}
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setBase(IFileVersion newBase) {
		if (newBase != base) {
			NotificationChain msgs = null;
			if (base != null) {
				msgs = ((InternalEObject) base).eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE - ReviewsPackage.FILE_ITEM__BASE, null, msgs);
			}
			if (newBase != null) {
				msgs = ((InternalEObject) newBase).eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - ReviewsPackage.FILE_ITEM__BASE, null, msgs);
			}
			msgs = basicSetBase(newBase, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.FILE_ITEM__BASE, newBase, newBase));
		}
	}

	/**
	 * <!-- begin-user-doc --> Unmodifiable and not updated. <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public List<IComment> getAllComments() {
		List<IComment> all = new ArrayList<>(getComments());
		if (getBase() != null) {
			all.addAll(getBase().getComments());
		}
		if (getTarget() != null) {
			all.addAll(getTarget().getComments());
		}
		return new EObjectEList.UnmodifiableEList<>(this,
				ReviewsPackage.Literals.COMMENT_CONTAINER__ALL_COMMENTS, all.size(), all.toArray());
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IFileVersion getTarget() {
		if (target != null && target.eIsProxy()) {
			InternalEObject oldTarget = (InternalEObject) target;
			target = (IFileVersion) eResolveProxy(oldTarget);
			if (target != oldTarget) {
				InternalEObject newTarget = (InternalEObject) target;
				NotificationChain msgs = oldTarget.eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE - ReviewsPackage.FILE_ITEM__TARGET, null, null);
				if (newTarget.eInternalContainer() == null) {
					msgs = newTarget.eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ReviewsPackage.FILE_ITEM__TARGET, null,
							msgs);
				}
				if (msgs != null) {
					msgs.dispatch();
				}
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.FILE_ITEM__TARGET,
							oldTarget, target));
				}
			}
		}
		return target;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IFileVersion basicGetTarget() {
		return target;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetTarget(IFileVersion newTarget, NotificationChain msgs) {
		IFileVersion oldTarget = target;
		target = newTarget;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					ReviewsPackage.FILE_ITEM__TARGET, oldTarget, newTarget);
			if (msgs == null) {
				msgs = notification;
			} else {
				msgs.add(notification);
			}
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setTarget(IFileVersion newTarget) {
		if (newTarget != target) {
			NotificationChain msgs = null;
			if (target != null) {
				msgs = ((InternalEObject) target).eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE - ReviewsPackage.FILE_ITEM__TARGET, null, msgs);
			}
			if (newTarget != null) {
				msgs = ((InternalEObject) newTarget).eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - ReviewsPackage.FILE_ITEM__TARGET, null, msgs);
			}
			msgs = basicSetTarget(newTarget, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.FILE_ITEM__TARGET, newTarget,
					newTarget));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IReviewItemSet getSet() {
		if (eContainerFeatureID() != ReviewsPackage.FILE_ITEM__SET) {
			return null;
		}
		return (IReviewItemSet) eContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public IReview getReview() {
		if (getSet() != null) {
			return getSet().getReview();
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IReviewItemSet basicGetSet() {
		if (eContainerFeatureID() != ReviewsPackage.FILE_ITEM__SET) {
			return null;
		}
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
	@Override
	public void setSet(IReviewItemSet newSet) {
		if (newSet != eInternalContainer()
				|| eContainerFeatureID() != ReviewsPackage.FILE_ITEM__SET && newSet != null) {
			if (EcoreUtil.isAncestor(this, newSet)) {
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
			}
			NotificationChain msgs = null;
			if (eInternalContainer() != null) {
				msgs = eBasicRemoveFromContainer(msgs);
			}
			if (newSet != null) {
				msgs = ((InternalEObject) newSet).eInverseAdd(this, ReviewsPackage.REVIEW_ITEM_SET__ITEMS,
						IReviewItemSet.class, msgs);
			}
			msgs = basicSetSet(newSet, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.FILE_ITEM__SET, newSet, newSet));
		}
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
				if (eInternalContainer() != null) {
					msgs = eBasicRemoveFromContainer(msgs);
				}
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
			case ReviewsPackage.FILE_ITEM__BASE:
				return basicSetBase(null, msgs);
			case ReviewsPackage.FILE_ITEM__TARGET:
				return basicSetTarget(null, msgs);
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
				if (resolve) {
					return getBase();
				}
				return basicGetBase();
			case ReviewsPackage.FILE_ITEM__TARGET:
				if (resolve) {
					return getTarget();
				}
				return basicGetTarget();
			case ReviewsPackage.FILE_ITEM__SET:
				if (resolve) {
					return getSet();
				}
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
				setBase((IFileVersion) newValue);
				return;
			case ReviewsPackage.FILE_ITEM__TARGET:
				setTarget((IFileVersion) newValue);
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
				setBase((IFileVersion) null);
				return;
			case ReviewsPackage.FILE_ITEM__TARGET:
				setTarget((IFileVersion) null);
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
