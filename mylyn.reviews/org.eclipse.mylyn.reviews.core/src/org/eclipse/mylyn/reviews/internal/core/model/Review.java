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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.reviews.core.model.IApprovalType;
import org.eclipse.mylyn.reviews.core.model.IChange;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IDated;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IRequirementEntry;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewerEntry;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.model.ReviewStatus;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Review</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getCreationDate <em>Creation Date</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getModificationDate <em>Modification Date</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getId <em>Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getKey <em>Key</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getSubject <em>Subject</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getMessage <em>Message</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getOwner <em>Owner</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getState <em>State</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getSets <em>Sets</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getRepository <em>Repository</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getParents <em>Parents</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getChildren <em>Children</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getReviewerApprovals <em>Reviewer Approvals</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Review#getRequirements <em>Requirements</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class Review extends CommentContainer implements IReview {
	/**
	 * The default value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getCreationDate()
	 * @generated
	 * @ordered
	 */
	protected static final Date CREATION_DATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getCreationDate()
	 * @generated
	 * @ordered
	 */
	protected Date creationDate = CREATION_DATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getModificationDate() <em>Modification Date</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getModificationDate()
	 * @generated
	 * @ordered
	 */
	protected static final Date MODIFICATION_DATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getModificationDate() <em>Modification Date</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getModificationDate()
	 * @generated
	 * @ordered
	 */
	protected Date modificationDate = MODIFICATION_DATE_EDEFAULT;

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
	 * The default value of the '{@link #getKey() <em>Key</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getKey()
	 * @generated
	 * @ordered
	 */
	protected static final String KEY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getKey() <em>Key</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getKey()
	 * @generated
	 * @ordered
	 */
	protected String key = KEY_EDEFAULT;

	/**
	 * The default value of the '{@link #getSubject() <em>Subject</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getSubject()
	 * @generated
	 * @ordered
	 */
	protected static final String SUBJECT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSubject() <em>Subject</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getSubject()
	 * @generated
	 * @ordered
	 */
	protected String subject = SUBJECT_EDEFAULT;

	/**
	 * The default value of the '{@link #getMessage() <em>Message</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getMessage()
	 * @generated
	 * @ordered
	 */
	protected static final String MESSAGE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMessage() <em>Message</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getMessage()
	 * @generated
	 * @ordered
	 */
	protected String message = MESSAGE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getOwner() <em>Owner</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getOwner()
	 * @generated
	 * @ordered
	 */
	protected IUser owner;

	/**
	 * The default value of the '{@link #getState() <em>State</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getState()
	 * @generated
	 * @ordered
	 */
	protected static final ReviewStatus STATE_EDEFAULT = ReviewStatus.NEW;

	/**
	 * The cached value of the '{@link #getState() <em>State</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getState()
	 * @generated
	 * @ordered
	 */
	protected ReviewStatus state = STATE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getSets() <em>Sets</em>}' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getSets()
	 * @generated
	 * @ordered
	 */
	protected EList<IReviewItemSet> sets;

	/**
	 * The cached value of the '{@link #getParents() <em>Parents</em>}' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getParents()
	 * @generated
	 * @ordered
	 */
	protected EList<IChange> parents;

	/**
	 * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getChildren()
	 * @generated
	 * @ordered
	 */
	protected EList<IChange> children;

	/**
	 * The cached value of the '{@link #getReviewerApprovals() <em>Reviewer Approvals</em>}' map. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getReviewerApprovals()
	 * @generated
	 * @ordered
	 */
	protected EMap<IUser, IReviewerEntry> reviewerApprovals;

	/**
	 * The cached value of the '{@link #getRequirements() <em>Requirements</em>}' map. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getRequirements()
	 * @generated
	 * @ordered
	 */
	protected EMap<IApprovalType, IRequirementEntry> requirements;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Review() {
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
	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setCreationDate(Date newCreationDate) {
		Date oldCreationDate = creationDate;
		creationDate = newCreationDate;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW__CREATION_DATE, oldCreationDate,
					creationDate));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setModificationDate(Date newModificationDate) {
		Date oldModificationDate = modificationDate;
		modificationDate = newModificationDate;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW__MODIFICATION_DATE,
					oldModificationDate, modificationDate));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public List<IReviewItemSet> getSets() {
		if (sets == null) {
			sets = new EObjectContainmentWithInverseEList.Resolving<>(IReviewItemSet.class, this,
					ReviewsPackage.REVIEW__SETS, ReviewsPackage.REVIEW_ITEM_SET__PARENT_REVIEW);
		}
		return sets;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IRepository getRepository() {
		if (eContainerFeatureID() != ReviewsPackage.REVIEW__REPOSITORY) {
			return null;
		}
		return (IRepository) eContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IRepository basicGetRepository() {
		if (eContainerFeatureID() != ReviewsPackage.REVIEW__REPOSITORY) {
			return null;
		}
		return (IRepository) eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetRepository(IRepository newRepository, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject) newRepository, ReviewsPackage.REVIEW__REPOSITORY, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setRepository(IRepository newRepository) {
		if (newRepository != eInternalContainer()
				|| eContainerFeatureID() != ReviewsPackage.REVIEW__REPOSITORY && newRepository != null) {
			if (EcoreUtil.isAncestor(this, newRepository)) {
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
			}
			NotificationChain msgs = null;
			if (eInternalContainer() != null) {
				msgs = eBasicRemoveFromContainer(msgs);
			}
			if (newRepository != null) {
				msgs = ((InternalEObject) newRepository).eInverseAdd(this, ReviewsPackage.REPOSITORY__REVIEWS,
						IRepository.class, msgs);
			}
			msgs = basicSetRepository(newRepository, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW__REPOSITORY, newRepository,
					newRepository));
		}
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
		List<IComment> all = new ArrayList<>(getComments());
		for (IReviewItemSet set : getSets()) {
			all.addAll(set.getAllComments());
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
	public ReviewStatus getState() {
		return state;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setState(ReviewStatus newState) {
		ReviewStatus oldState = state;
		state = newState == null ? STATE_EDEFAULT : newState;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW__STATE, oldState, state));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW__ID, oldId, id));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getKey() {
		return key;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setKey(String newKey) {
		String oldKey = key;
		key = newKey;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW__KEY, oldKey, key));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getSubject() {
		return subject;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setSubject(String newSubject) {
		String oldSubject = subject;
		subject = newSubject;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW__SUBJECT, oldSubject, subject));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setMessage(String newMessage) {
		String oldMessage = message;
		message = newMessage;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW__MESSAGE, oldMessage, message));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IUser getOwner() {
		if (owner != null && owner.eIsProxy()) {
			InternalEObject oldOwner = (InternalEObject) owner;
			owner = (IUser) eResolveProxy(oldOwner);
			if (owner != oldOwner) {
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.REVIEW__OWNER, oldOwner,
							owner));
				}
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
	@Override
	public void setOwner(IUser newOwner) {
		IUser oldOwner = owner;
		owner = newOwner;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.REVIEW__OWNER, oldOwner, owner));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public List<IChange> getParents() {
		if (parents == null) {
			parents = new EObjectContainmentEList.Resolving<>(IChange.class, this,
					ReviewsPackage.REVIEW__PARENTS);
		}
		return parents;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public List<IChange> getChildren() {
		if (children == null) {
			children = new EObjectContainmentEList.Resolving<>(IChange.class, this,
					ReviewsPackage.REVIEW__CHILDREN);
		}
		return children;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Map<IUser, IReviewerEntry> getReviewerApprovals() {
		if (reviewerApprovals == null) {
			reviewerApprovals = new EcoreEMap<>(ReviewsPackage.Literals.USER_APPROVALS_MAP,
					UserApprovalsMap.class, this, ReviewsPackage.REVIEW__REVIEWER_APPROVALS);
		}
		return reviewerApprovals.map();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Map<IApprovalType, IRequirementEntry> getRequirements() {
		if (requirements == null) {
			requirements = new EcoreEMap<>(
					ReviewsPackage.Literals.REVIEW_REQUIREMENTS_MAP, ReviewRequirementsMap.class, this,
					ReviewsPackage.REVIEW__REQUIREMENTS);
		}
		return requirements.map();
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
			case ReviewsPackage.REVIEW__SETS:
				return ((InternalEList<InternalEObject>) (InternalEList<?>) getSets()).basicAdd(otherEnd, msgs);
			case ReviewsPackage.REVIEW__REPOSITORY:
				if (eInternalContainer() != null) {
					msgs = eBasicRemoveFromContainer(msgs);
				}
				return basicSetRepository((IRepository) otherEnd, msgs);
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
			case ReviewsPackage.REVIEW__SETS:
				return ((InternalEList<?>) getSets()).basicRemove(otherEnd, msgs);
			case ReviewsPackage.REVIEW__REPOSITORY:
				return basicSetRepository(null, msgs);
			case ReviewsPackage.REVIEW__PARENTS:
				return ((InternalEList<?>) getParents()).basicRemove(otherEnd, msgs);
			case ReviewsPackage.REVIEW__CHILDREN:
				return ((InternalEList<?>) getChildren()).basicRemove(otherEnd, msgs);
			case ReviewsPackage.REVIEW__REVIEWER_APPROVALS:
				return ((InternalEList<?>) ((EMap.InternalMapView<IUser, IReviewerEntry>) getReviewerApprovals())
						.eMap()).basicRemove(otherEnd, msgs);
			case ReviewsPackage.REVIEW__REQUIREMENTS:
				return ((InternalEList<?>) ((EMap.InternalMapView<IApprovalType, IRequirementEntry>) getRequirements())
						.eMap()).basicRemove(otherEnd, msgs);
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
			case ReviewsPackage.REVIEW__REPOSITORY:
				return eInternalContainer().eInverseRemove(this, ReviewsPackage.REPOSITORY__REVIEWS, IRepository.class,
						msgs);
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
			case ReviewsPackage.REVIEW__CREATION_DATE:
				return getCreationDate();
			case ReviewsPackage.REVIEW__MODIFICATION_DATE:
				return getModificationDate();
			case ReviewsPackage.REVIEW__ID:
				return getId();
			case ReviewsPackage.REVIEW__KEY:
				return getKey();
			case ReviewsPackage.REVIEW__SUBJECT:
				return getSubject();
			case ReviewsPackage.REVIEW__MESSAGE:
				return getMessage();
			case ReviewsPackage.REVIEW__OWNER:
				if (resolve) {
					return getOwner();
				}
				return basicGetOwner();
			case ReviewsPackage.REVIEW__STATE:
				return getState();
			case ReviewsPackage.REVIEW__SETS:
				return getSets();
			case ReviewsPackage.REVIEW__REPOSITORY:
				if (resolve) {
					return getRepository();
				}
				return basicGetRepository();
			case ReviewsPackage.REVIEW__PARENTS:
				return getParents();
			case ReviewsPackage.REVIEW__CHILDREN:
				return getChildren();
			case ReviewsPackage.REVIEW__REVIEWER_APPROVALS:
				if (coreType) {
					return ((EMap.InternalMapView<IUser, IReviewerEntry>) getReviewerApprovals()).eMap();
				} else {
					return getReviewerApprovals();
				}
			case ReviewsPackage.REVIEW__REQUIREMENTS:
				if (coreType) {
					return ((EMap.InternalMapView<IApprovalType, IRequirementEntry>) getRequirements()).eMap();
				} else {
					return getRequirements();
				}
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
			case ReviewsPackage.REVIEW__ID:
				setId((String) newValue);
				return;
			case ReviewsPackage.REVIEW__KEY:
				setKey((String) newValue);
				return;
			case ReviewsPackage.REVIEW__SUBJECT:
				setSubject((String) newValue);
				return;
			case ReviewsPackage.REVIEW__MESSAGE:
				setMessage((String) newValue);
				return;
			case ReviewsPackage.REVIEW__OWNER:
				setOwner((IUser) newValue);
				return;
			case ReviewsPackage.REVIEW__STATE:
				setState((ReviewStatus) newValue);
				return;
			case ReviewsPackage.REVIEW__SETS:
				getSets().clear();
				getSets().addAll((Collection<? extends IReviewItemSet>) newValue);
				return;
			case ReviewsPackage.REVIEW__REPOSITORY:
				setRepository((IRepository) newValue);
				return;
			case ReviewsPackage.REVIEW__PARENTS:
				getParents().clear();
				getParents().addAll((Collection<? extends IChange>) newValue);
				return;
			case ReviewsPackage.REVIEW__CHILDREN:
				getChildren().clear();
				getChildren().addAll((Collection<? extends IChange>) newValue);
				return;
			case ReviewsPackage.REVIEW__REVIEWER_APPROVALS:
				((EStructuralFeature.Setting) ((EMap.InternalMapView<IUser, IReviewerEntry>) getReviewerApprovals())
						.eMap()).set(newValue);
				return;
			case ReviewsPackage.REVIEW__REQUIREMENTS:
				((EStructuralFeature.Setting) ((EMap.InternalMapView<IApprovalType, IRequirementEntry>) getRequirements())
						.eMap()).set(newValue);
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
			case ReviewsPackage.REVIEW__ID:
				setId(ID_EDEFAULT);
				return;
			case ReviewsPackage.REVIEW__KEY:
				setKey(KEY_EDEFAULT);
				return;
			case ReviewsPackage.REVIEW__SUBJECT:
				setSubject(SUBJECT_EDEFAULT);
				return;
			case ReviewsPackage.REVIEW__MESSAGE:
				setMessage(MESSAGE_EDEFAULT);
				return;
			case ReviewsPackage.REVIEW__OWNER:
				setOwner((IUser) null);
				return;
			case ReviewsPackage.REVIEW__STATE:
				setState(STATE_EDEFAULT);
				return;
			case ReviewsPackage.REVIEW__SETS:
				getSets().clear();
				return;
			case ReviewsPackage.REVIEW__REPOSITORY:
				setRepository((IRepository) null);
				return;
			case ReviewsPackage.REVIEW__PARENTS:
				getParents().clear();
				return;
			case ReviewsPackage.REVIEW__CHILDREN:
				getChildren().clear();
				return;
			case ReviewsPackage.REVIEW__REVIEWER_APPROVALS:
				getReviewerApprovals().clear();
				return;
			case ReviewsPackage.REVIEW__REQUIREMENTS:
				getRequirements().clear();
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
				return CREATION_DATE_EDEFAULT == null
						? creationDate != null
						: !CREATION_DATE_EDEFAULT.equals(creationDate);
			case ReviewsPackage.REVIEW__MODIFICATION_DATE:
				return MODIFICATION_DATE_EDEFAULT == null
						? modificationDate != null
						: !MODIFICATION_DATE_EDEFAULT.equals(modificationDate);
			case ReviewsPackage.REVIEW__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case ReviewsPackage.REVIEW__KEY:
				return KEY_EDEFAULT == null ? key != null : !KEY_EDEFAULT.equals(key);
			case ReviewsPackage.REVIEW__SUBJECT:
				return SUBJECT_EDEFAULT == null ? subject != null : !SUBJECT_EDEFAULT.equals(subject);
			case ReviewsPackage.REVIEW__MESSAGE:
				return MESSAGE_EDEFAULT == null ? message != null : !MESSAGE_EDEFAULT.equals(message);
			case ReviewsPackage.REVIEW__OWNER:
				return owner != null;
			case ReviewsPackage.REVIEW__STATE:
				return state != STATE_EDEFAULT;
			case ReviewsPackage.REVIEW__SETS:
				return sets != null && !sets.isEmpty();
			case ReviewsPackage.REVIEW__REPOSITORY:
				return basicGetRepository() != null;
			case ReviewsPackage.REVIEW__PARENTS:
				return parents != null && !parents.isEmpty();
			case ReviewsPackage.REVIEW__CHILDREN:
				return children != null && !children.isEmpty();
			case ReviewsPackage.REVIEW__REVIEWER_APPROVALS:
				return reviewerApprovals != null && !reviewerApprovals.isEmpty();
			case ReviewsPackage.REVIEW__REQUIREMENTS:
				return requirements != null && !requirements.isEmpty();
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
			return switch (derivedFeatureID) {
				case ReviewsPackage.REVIEW__CREATION_DATE -> ReviewsPackage.DATED__CREATION_DATE;
				case ReviewsPackage.REVIEW__MODIFICATION_DATE -> ReviewsPackage.DATED__MODIFICATION_DATE;
				default -> -1;
			};
		}
		if (baseClass == IChange.class) {
			return switch (derivedFeatureID) {
				case ReviewsPackage.REVIEW__ID -> ReviewsPackage.CHANGE__ID;
				case ReviewsPackage.REVIEW__KEY -> ReviewsPackage.CHANGE__KEY;
				case ReviewsPackage.REVIEW__SUBJECT -> ReviewsPackage.CHANGE__SUBJECT;
				case ReviewsPackage.REVIEW__MESSAGE -> ReviewsPackage.CHANGE__MESSAGE;
				case ReviewsPackage.REVIEW__OWNER -> ReviewsPackage.CHANGE__OWNER;
				case ReviewsPackage.REVIEW__STATE -> ReviewsPackage.CHANGE__STATE;
				default -> -1;
			};
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
			return switch (baseFeatureID) {
				case ReviewsPackage.DATED__CREATION_DATE -> ReviewsPackage.REVIEW__CREATION_DATE;
				case ReviewsPackage.DATED__MODIFICATION_DATE -> ReviewsPackage.REVIEW__MODIFICATION_DATE;
				default -> -1;
			};
		}
		if (baseClass == IChange.class) {
			return switch (baseFeatureID) {
				case ReviewsPackage.CHANGE__ID -> ReviewsPackage.REVIEW__ID;
				case ReviewsPackage.CHANGE__KEY -> ReviewsPackage.REVIEW__KEY;
				case ReviewsPackage.CHANGE__SUBJECT -> ReviewsPackage.REVIEW__SUBJECT;
				case ReviewsPackage.CHANGE__MESSAGE -> ReviewsPackage.REVIEW__MESSAGE;
				case ReviewsPackage.CHANGE__OWNER -> ReviewsPackage.REVIEW__OWNER;
				case ReviewsPackage.CHANGE__STATE -> ReviewsPackage.REVIEW__STATE;
				default -> -1;
			};
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
		if (eIsProxy()) {
			return super.toString();
		}

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (creationDate: "); //$NON-NLS-1$
		result.append(creationDate);
		result.append(", modificationDate: "); //$NON-NLS-1$
		result.append(modificationDate);
		result.append(", id: "); //$NON-NLS-1$
		result.append(id);
		result.append(", key: "); //$NON-NLS-1$
		result.append(key);
		result.append(", subject: "); //$NON-NLS-1$
		result.append(subject);
		result.append(", message: "); //$NON-NLS-1$
		result.append(message);
		result.append(", state: "); //$NON-NLS-1$
		result.append(state);
		result.append(')');
		return result.toString();
	}

} //Review
