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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ICommentContainer;
import org.eclipse.mylyn.reviews.core.model.IDated;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IUser;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Comment</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getIndex <em>Index</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getCreationDate <em>Creation Date</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getModificationDate <em>Modification Date</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getAuthor <em>Author</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getDescription <em>Description</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getId <em>Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getReplies <em>Replies</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#isDraft <em>Draft</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getLocations <em>Locations</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getReview <em>Review</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getTitle <em>Title</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getItem <em>Item</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#isMine <em>Mine</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class Comment extends EObjectImpl implements IComment {
	/**
	 * The default value of the '{@link #getIndex() <em>Index</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getIndex()
	 * @generated
	 * @ordered
	 */
	protected static final long INDEX_EDEFAULT = 0L;

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
	 * The cached value of the '{@link #getAuthor() <em>Author</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getAuthor()
	 * @generated
	 * @ordered
	 */
	protected IUser author;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

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
	 * The cached value of the '{@link #getReplies() <em>Replies</em>}' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getReplies()
	 * @generated
	 * @ordered
	 */
	protected EList<IComment> replies;

	/**
	 * The default value of the '{@link #isDraft() <em>Draft</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #isDraft()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DRAFT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isDraft() <em>Draft</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #isDraft()
	 * @generated
	 * @ordered
	 */
	protected boolean draft = DRAFT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getLocations() <em>Locations</em>}' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see #getLocations()
	 * @generated
	 * @ordered
	 */
	protected EList<ILocation> locations;

	/**
	 * The cached value of the '{@link #getReview() <em>Review</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getReview()
	 * @generated
	 * @ordered
	 */
	protected IReview review;

	/**
	 * The default value of the '{@link #getTitle() <em>Title</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected static final String TITLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTitle() <em>Title</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected String title = TITLE_EDEFAULT;

	/**
	 * The default value of the '{@link #isMine() <em>Mine</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #isMine()
	 * @generated
	 * @ordered
	 */
	protected static final boolean MINE_EDEFAULT = false;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected Comment() {
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.COMMENT;
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
	@Override
	public IUser getAuthor() {
		return author;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setAuthor(IUser newAuthor) {
		IUser oldAuthor = author;
		author = newAuthor;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__AUTHOR, oldAuthor, author));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__DESCRIPTION, oldDescription,
					description));
		}
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
	 * @generated NOT
	 */
	@Override
	public void setCreationDate(Date newCreationDate) {
		Date oldCreationDate = creationDate;
		//Protect against case where java.sql.Timestamp is used
		creationDate = new Date(newCreationDate.getTime());
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__CREATION_DATE,
					oldCreationDate, creationDate));
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
	 * @generated NOT
	 */
	@Override
	public void setModificationDate(Date newModificationDate) {
		Date oldModificationDate = modificationDate;
		//Protect against case where java.sql.Timestamp is used
		modificationDate = new Date(newModificationDate.getTime());
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__MODIFICATION_DATE,
					oldModificationDate, modificationDate));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__ID, oldId, id));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public List<IComment> getReplies() {
		if (replies == null) {
			replies = new EObjectResolvingEList<>(IComment.class, this, ReviewsPackage.COMMENT__REPLIES);
		}
		return replies;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean isDraft() {
		return draft;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setDraft(boolean newDraft) {
		boolean oldDraft = draft;
		draft = newDraft;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__DRAFT, oldDraft, draft));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public List<ILocation> getLocations() {
		if (locations == null) {
			locations = new EObjectContainmentEList.Resolving<>(ILocation.class, this,
					ReviewsPackage.COMMENT__LOCATIONS);
		}
		return locations;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public IReview getReview() {
		if (getItem() instanceof IReview) {
			return (IReview) getItem();
		}
		if (getItem() instanceof IReviewItem) {
			return ((IReviewItem) getItem()).getReview();
		}
		return review;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setTitle(String newTitle) {
		String oldTitle = title;
		title = newTitle;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__TITLE, oldTitle, title));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public ICommentContainer getItem() {
		if (eContainerFeatureID() != ReviewsPackage.COMMENT__ITEM) {
			return null;
		}
		return (ICommentContainer) eContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ICommentContainer basicGetItem() {
		if (eContainerFeatureID() != ReviewsPackage.COMMENT__ITEM) {
			return null;
		}
		return (ICommentContainer) eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public NotificationChain basicSetItem(ICommentContainer newItem, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject) newItem, ReviewsPackage.COMMENT__ITEM, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setItem(ICommentContainer newItem) {
		if (newItem != eInternalContainer()
				|| eContainerFeatureID() != ReviewsPackage.COMMENT__ITEM && newItem != null) {
			if (EcoreUtil.isAncestor(this, newItem)) {
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
			}
			NotificationChain msgs = null;
			if (eInternalContainer() != null) {
				msgs = eBasicRemoveFromContainer(msgs);
			}
			if (newItem != null) {
				msgs = ((InternalEObject) newItem).eInverseAdd(this, ReviewsPackage.COMMENT_CONTAINER__COMMENTS,
						ICommentContainer.class, msgs);
			}
			msgs = basicSetItem(newItem, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__ITEM, newItem, newItem));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public boolean isMine() {
		IRepository repos = getReview() != null ? getReview().getRepository() : null;
		return getAuthor() != null && repos != null
				&& (repos.getAccount() == getAuthor()
						|| repos.getAccount() != null && repos.getAccount().getEmail() != null
								&& repos.getAccount().getEmail().equals(getAuthor().getEmail()));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ReviewsPackage.COMMENT__ITEM:
				if (eInternalContainer() != null) {
					msgs = eBasicRemoveFromContainer(msgs);
				}
				return basicSetItem((ICommentContainer) otherEnd, msgs);
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
			case ReviewsPackage.COMMENT__LOCATIONS:
				return ((InternalEList<?>) getLocations()).basicRemove(otherEnd, msgs);
			case ReviewsPackage.COMMENT__ITEM:
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
			case ReviewsPackage.COMMENT__ITEM:
				return eInternalContainer().eInverseRemove(this, ReviewsPackage.COMMENT_CONTAINER__COMMENTS,
						ICommentContainer.class, msgs);
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
			case ReviewsPackage.COMMENT__INDEX:
				return getIndex();
			case ReviewsPackage.COMMENT__CREATION_DATE:
				return getCreationDate();
			case ReviewsPackage.COMMENT__MODIFICATION_DATE:
				return getModificationDate();
			case ReviewsPackage.COMMENT__AUTHOR:
				return getAuthor();
			case ReviewsPackage.COMMENT__DESCRIPTION:
				return getDescription();
			case ReviewsPackage.COMMENT__ID:
				return getId();
			case ReviewsPackage.COMMENT__REPLIES:
				return getReplies();
			case ReviewsPackage.COMMENT__DRAFT:
				return isDraft();
			case ReviewsPackage.COMMENT__LOCATIONS:
				return getLocations();
			case ReviewsPackage.COMMENT__REVIEW:
				return getReview();
			case ReviewsPackage.COMMENT__TITLE:
				return getTitle();
			case ReviewsPackage.COMMENT__ITEM:
				if (resolve) {
					return getItem();
				}
				return basicGetItem();
			case ReviewsPackage.COMMENT__MINE:
				return isMine();
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
			case ReviewsPackage.COMMENT__CREATION_DATE:
				setCreationDate((Date) newValue);
				return;
			case ReviewsPackage.COMMENT__MODIFICATION_DATE:
				setModificationDate((Date) newValue);
				return;
			case ReviewsPackage.COMMENT__AUTHOR:
				setAuthor((IUser) newValue);
				return;
			case ReviewsPackage.COMMENT__DESCRIPTION:
				setDescription((String) newValue);
				return;
			case ReviewsPackage.COMMENT__ID:
				setId((String) newValue);
				return;
			case ReviewsPackage.COMMENT__REPLIES:
				getReplies().clear();
				getReplies().addAll((Collection<? extends IComment>) newValue);
				return;
			case ReviewsPackage.COMMENT__DRAFT:
				setDraft((Boolean) newValue);
				return;
			case ReviewsPackage.COMMENT__LOCATIONS:
				getLocations().clear();
				getLocations().addAll((Collection<? extends ILocation>) newValue);
				return;
			case ReviewsPackage.COMMENT__TITLE:
				setTitle((String) newValue);
				return;
			case ReviewsPackage.COMMENT__ITEM:
				setItem((ICommentContainer) newValue);
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
			case ReviewsPackage.COMMENT__CREATION_DATE:
				setCreationDate(CREATION_DATE_EDEFAULT);
				return;
			case ReviewsPackage.COMMENT__MODIFICATION_DATE:
				setModificationDate(MODIFICATION_DATE_EDEFAULT);
				return;
			case ReviewsPackage.COMMENT__AUTHOR:
				setAuthor((IUser) null);
				return;
			case ReviewsPackage.COMMENT__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case ReviewsPackage.COMMENT__ID:
				setId(ID_EDEFAULT);
				return;
			case ReviewsPackage.COMMENT__REPLIES:
				getReplies().clear();
				return;
			case ReviewsPackage.COMMENT__DRAFT:
				setDraft(DRAFT_EDEFAULT);
				return;
			case ReviewsPackage.COMMENT__LOCATIONS:
				getLocations().clear();
				return;
			case ReviewsPackage.COMMENT__TITLE:
				setTitle(TITLE_EDEFAULT);
				return;
			case ReviewsPackage.COMMENT__ITEM:
				setItem((ICommentContainer) null);
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
			case ReviewsPackage.COMMENT__INDEX:
				return getIndex() != INDEX_EDEFAULT;
			case ReviewsPackage.COMMENT__CREATION_DATE:
				return CREATION_DATE_EDEFAULT == null
						? creationDate != null
						: !CREATION_DATE_EDEFAULT.equals(creationDate);
			case ReviewsPackage.COMMENT__MODIFICATION_DATE:
				return MODIFICATION_DATE_EDEFAULT == null
						? modificationDate != null
						: !MODIFICATION_DATE_EDEFAULT.equals(modificationDate);
			case ReviewsPackage.COMMENT__AUTHOR:
				return author != null;
			case ReviewsPackage.COMMENT__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case ReviewsPackage.COMMENT__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case ReviewsPackage.COMMENT__REPLIES:
				return replies != null && !replies.isEmpty();
			case ReviewsPackage.COMMENT__DRAFT:
				return draft != DRAFT_EDEFAULT;
			case ReviewsPackage.COMMENT__LOCATIONS:
				return locations != null && !locations.isEmpty();
			case ReviewsPackage.COMMENT__REVIEW:
				return review != null;
			case ReviewsPackage.COMMENT__TITLE:
				return TITLE_EDEFAULT == null ? title != null : !TITLE_EDEFAULT.equals(title);
			case ReviewsPackage.COMMENT__ITEM:
				return basicGetItem() != null;
			case ReviewsPackage.COMMENT__MINE:
				return isMine() != MINE_EDEFAULT;
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
				case ReviewsPackage.COMMENT__CREATION_DATE -> ReviewsPackage.DATED__CREATION_DATE;
				case ReviewsPackage.COMMENT__MODIFICATION_DATE -> ReviewsPackage.DATED__MODIFICATION_DATE;
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
				case ReviewsPackage.DATED__CREATION_DATE -> ReviewsPackage.COMMENT__CREATION_DATE;
				case ReviewsPackage.DATED__MODIFICATION_DATE -> ReviewsPackage.COMMENT__MODIFICATION_DATE;
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
		result.append(", description: "); //$NON-NLS-1$
		result.append(description);
		result.append(", id: "); //$NON-NLS-1$
		result.append(id);
		result.append(", draft: "); //$NON-NLS-1$
		result.append(draft);
		result.append(", title: "); //$NON-NLS-1$
		result.append(title);
		result.append(')');
		return result.toString();
	}

} //Comment
