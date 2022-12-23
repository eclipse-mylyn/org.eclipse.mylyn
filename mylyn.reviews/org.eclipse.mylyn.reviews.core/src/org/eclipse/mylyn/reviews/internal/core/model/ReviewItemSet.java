/**
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
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
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ICommit;
import org.eclipse.mylyn.reviews.core.model.IDated;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Review Item Set</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItemSet#getCreationDate <em>Creation Date</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItemSet#getModificationDate <em>Modification Date
 * </em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItemSet#getItems <em>Items</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItemSet#getRevision <em>Revision</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ReviewItemSet extends ReviewItem implements IReviewItemSet {
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
	 * The cached value of the '{@link #getItems() <em>Items</em>}' containment reference list. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getItems()
	 * @generated
	 * @ordered
	 */
	protected EList<IFileItem> items;

	/**
	 * The default value of the '{@link #getRevision() <em>Revision</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getRevision()
	 * @generated
	 * @ordered
	 */
	protected static final String REVISION_EDEFAULT = ""; //$NON-NLS-1$

	/**
	 * The cached value of the '{@link #getRevision() <em>Revision</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getRevision()
	 * @generated
	 * @ordered
	 */
	protected String revision = REVISION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getParentCommits() <em>Parent Commits</em>}' containment reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getParentCommits()
	 * @generated
	 * @ordered
	 */
	protected EList<ICommit> parentCommits;

	/**
	 * The default value of the '{@link #isInNeedOfRetrieval() <em>In Need Of Retrieval</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #isInNeedOfRetrieval()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IN_NEED_OF_RETRIEVAL_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isInNeedOfRetrieval() <em>In Need Of Retrieval</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #isInNeedOfRetrieval()
	 * @generated
	 * @ordered
	 */
	protected boolean inNeedOfRetrieval = IN_NEED_OF_RETRIEVAL_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ReviewItemSet() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.REVIEW_ITEM_SET;
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
	 * @generated NOT
	 */
	public void setCreationDate(Date newCreationDate) {
		Date oldCreationDate = creationDate;
		//Protect against case where java.sql.Timestamp is used
		creationDate = new Date(newCreationDate.getTime());
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_ITEM_SET__CREATION_DATE,
					oldCreationDate, creationDate));
		}
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
	 * @generated NOT
	 */
	public void setModificationDate(Date newModificationDate) {
		Date oldModificationDate = modificationDate;
		//Protect against case where java.sql.Timestamp is used
		modificationDate = new Date(newModificationDate.getTime());
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_ITEM_SET__MODIFICATION_DATE,
					oldModificationDate, modificationDate));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<IFileItem> getItems() {
		if (items == null) {
			items = new EObjectContainmentWithInverseEList.Resolving<IFileItem>(IFileItem.class, this,
					ReviewsPackage.REVIEW_ITEM_SET__ITEMS, ReviewsPackage.FILE_ITEM__SET);
		}
		return items;
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
	public List<IComment> getAllComments() {
		List<IComment> all = new ArrayList<IComment>(getComments());
		for (IReviewItem item : getItems()) {
			all.addAll(item.getAllComments());
		}
		return new EObjectEList.UnmodifiableEList<IComment>(this,
				ReviewsPackage.Literals.COMMENT_CONTAINER__ALL_COMMENTS, all.size(), all.toArray());
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getRevision() {
		return revision;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setRevision(String newRevision) {
		String oldRevision = revision;
		revision = newRevision;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_ITEM_SET__REVISION, oldRevision,
					revision));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IReview getParentReview() {
		if (eContainerFeatureID() != ReviewsPackage.REVIEW_ITEM_SET__PARENT_REVIEW)
			return null;
		return (IReview) eContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IReview basicGetParentReview() {
		if (eContainerFeatureID() != ReviewsPackage.REVIEW_ITEM_SET__PARENT_REVIEW)
			return null;
		return (IReview) eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetParentReview(IReview newParentReview, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject) newParentReview, ReviewsPackage.REVIEW_ITEM_SET__PARENT_REVIEW,
				msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setParentReview(IReview newParentReview) {
		if (newParentReview != eInternalContainer()
				|| (eContainerFeatureID() != ReviewsPackage.REVIEW_ITEM_SET__PARENT_REVIEW
						&& newParentReview != null)) {
			if (EcoreUtil.isAncestor(this, newParentReview))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newParentReview != null)
				msgs = ((InternalEObject) newParentReview).eInverseAdd(this, ReviewsPackage.REVIEW__SETS, IReview.class,
						msgs);
			msgs = basicSetParentReview(newParentReview, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_ITEM_SET__PARENT_REVIEW,
					newParentReview, newParentReview));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<ICommit> getParentCommits() {
		if (parentCommits == null) {
			parentCommits = new EObjectContainmentEList.Resolving<ICommit>(ICommit.class, this,
					ReviewsPackage.REVIEW_ITEM_SET__PARENT_COMMITS);
		}
		return parentCommits;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public boolean isInNeedOfRetrieval() {
		return inNeedOfRetrieval;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setInNeedOfRetrieval(boolean newInNeedOfRetrieval) {
		boolean oldInNeedOfRetrieval = inNeedOfRetrieval;
		inNeedOfRetrieval = newInNeedOfRetrieval;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW_ITEM_SET__IN_NEED_OF_RETRIEVAL,
					oldInNeedOfRetrieval, inNeedOfRetrieval));
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
		case ReviewsPackage.REVIEW_ITEM_SET__ITEMS:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getItems()).basicAdd(otherEnd, msgs);
		case ReviewsPackage.REVIEW_ITEM_SET__PARENT_REVIEW:
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			return basicSetParentReview((IReview) otherEnd, msgs);
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
		case ReviewsPackage.REVIEW_ITEM_SET__ITEMS:
			return ((InternalEList<?>) getItems()).basicRemove(otherEnd, msgs);
		case ReviewsPackage.REVIEW_ITEM_SET__PARENT_REVIEW:
			return basicSetParentReview(null, msgs);
		case ReviewsPackage.REVIEW_ITEM_SET__PARENT_COMMITS:
			return ((InternalEList<?>) getParentCommits()).basicRemove(otherEnd, msgs);
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
		case ReviewsPackage.REVIEW_ITEM_SET__PARENT_REVIEW:
			return eInternalContainer().eInverseRemove(this, ReviewsPackage.REVIEW__SETS, IReview.class, msgs);
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
		case ReviewsPackage.REVIEW_ITEM_SET__CREATION_DATE:
			return getCreationDate();
		case ReviewsPackage.REVIEW_ITEM_SET__MODIFICATION_DATE:
			return getModificationDate();
		case ReviewsPackage.REVIEW_ITEM_SET__ITEMS:
			return getItems();
		case ReviewsPackage.REVIEW_ITEM_SET__REVISION:
			return getRevision();
		case ReviewsPackage.REVIEW_ITEM_SET__PARENT_REVIEW:
			if (resolve)
				return getParentReview();
			return basicGetParentReview();
		case ReviewsPackage.REVIEW_ITEM_SET__PARENT_COMMITS:
			return getParentCommits();
		case ReviewsPackage.REVIEW_ITEM_SET__IN_NEED_OF_RETRIEVAL:
			return isInNeedOfRetrieval();
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
		case ReviewsPackage.REVIEW_ITEM_SET__CREATION_DATE:
			setCreationDate((Date) newValue);
			return;
		case ReviewsPackage.REVIEW_ITEM_SET__MODIFICATION_DATE:
			setModificationDate((Date) newValue);
			return;
		case ReviewsPackage.REVIEW_ITEM_SET__ITEMS:
			getItems().clear();
			getItems().addAll((Collection<? extends IFileItem>) newValue);
			return;
		case ReviewsPackage.REVIEW_ITEM_SET__REVISION:
			setRevision((String) newValue);
			return;
		case ReviewsPackage.REVIEW_ITEM_SET__PARENT_REVIEW:
			setParentReview((IReview) newValue);
			return;
		case ReviewsPackage.REVIEW_ITEM_SET__PARENT_COMMITS:
			getParentCommits().clear();
			getParentCommits().addAll((Collection<? extends ICommit>) newValue);
			return;
		case ReviewsPackage.REVIEW_ITEM_SET__IN_NEED_OF_RETRIEVAL:
			setInNeedOfRetrieval((Boolean) newValue);
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
		case ReviewsPackage.REVIEW_ITEM_SET__CREATION_DATE:
			setCreationDate(CREATION_DATE_EDEFAULT);
			return;
		case ReviewsPackage.REVIEW_ITEM_SET__MODIFICATION_DATE:
			setModificationDate(MODIFICATION_DATE_EDEFAULT);
			return;
		case ReviewsPackage.REVIEW_ITEM_SET__ITEMS:
			getItems().clear();
			return;
		case ReviewsPackage.REVIEW_ITEM_SET__REVISION:
			setRevision(REVISION_EDEFAULT);
			return;
		case ReviewsPackage.REVIEW_ITEM_SET__PARENT_REVIEW:
			setParentReview((IReview) null);
			return;
		case ReviewsPackage.REVIEW_ITEM_SET__PARENT_COMMITS:
			getParentCommits().clear();
			return;
		case ReviewsPackage.REVIEW_ITEM_SET__IN_NEED_OF_RETRIEVAL:
			setInNeedOfRetrieval(IN_NEED_OF_RETRIEVAL_EDEFAULT);
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
		case ReviewsPackage.REVIEW_ITEM_SET__CREATION_DATE:
			return CREATION_DATE_EDEFAULT == null ? creationDate != null : !CREATION_DATE_EDEFAULT.equals(creationDate);
		case ReviewsPackage.REVIEW_ITEM_SET__MODIFICATION_DATE:
			return MODIFICATION_DATE_EDEFAULT == null
					? modificationDate != null
					: !MODIFICATION_DATE_EDEFAULT.equals(modificationDate);
		case ReviewsPackage.REVIEW_ITEM_SET__ITEMS:
			return items != null && !items.isEmpty();
		case ReviewsPackage.REVIEW_ITEM_SET__REVISION:
			return REVISION_EDEFAULT == null ? revision != null : !REVISION_EDEFAULT.equals(revision);
		case ReviewsPackage.REVIEW_ITEM_SET__PARENT_REVIEW:
			return basicGetParentReview() != null;
		case ReviewsPackage.REVIEW_ITEM_SET__PARENT_COMMITS:
			return parentCommits != null && !parentCommits.isEmpty();
		case ReviewsPackage.REVIEW_ITEM_SET__IN_NEED_OF_RETRIEVAL:
			return inNeedOfRetrieval != IN_NEED_OF_RETRIEVAL_EDEFAULT;
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
			case ReviewsPackage.REVIEW_ITEM_SET__CREATION_DATE:
				return ReviewsPackage.DATED__CREATION_DATE;
			case ReviewsPackage.REVIEW_ITEM_SET__MODIFICATION_DATE:
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
				return ReviewsPackage.REVIEW_ITEM_SET__CREATION_DATE;
			case ReviewsPackage.DATED__MODIFICATION_DATE:
				return ReviewsPackage.REVIEW_ITEM_SET__MODIFICATION_DATE;
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
		result.append(", revision: "); //$NON-NLS-1$
		result.append(revision);
		result.append(", inNeedOfRetrieval: "); //$NON-NLS-1$
		result.append(inNeedOfRetrieval);
		result.append(')');
		return result.toString();
	}

	/**
	 * @generated NOT
	 */
	@Override
	public IReview getReview() {
		return getParentReview();
	}

} //ReviewItemSet
