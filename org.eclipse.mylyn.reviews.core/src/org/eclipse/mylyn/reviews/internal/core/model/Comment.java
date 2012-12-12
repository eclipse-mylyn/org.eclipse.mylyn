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

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ICommentType;
import org.eclipse.mylyn.reviews.core.model.IDated;
import org.eclipse.mylyn.reviews.core.model.IIndexed;
import org.eclipse.mylyn.reviews.core.model.ITopic;
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
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getType <em>Type</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getDescription <em>Description</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getId <em>Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getReplies <em>Replies</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#isDraft <em>Draft</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.Comment#getParentTopic <em>Parent Topic</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class Comment extends ReviewComponent implements IComment {
	/**
	 * The default value of the '{@link #getIndex() <em>Index</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getIndex()
	 * @generated
	 * @ordered
	 */
	protected static final long INDEX_EDEFAULT = 0L;

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
	 * The cached value of the '{@link #getAuthor() <em>Author</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getAuthor()
	 * @generated
	 * @ordered
	 */
	protected IUser author;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' containment reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected ICommentType type;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
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
	 * The cached value of the '{@link #getReplies() <em>Replies</em>}' containment reference list. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @see #getReplies()
	 * @generated
	 * @ordered
	 */
	protected EList<IComment> replies;

	/**
	 * The default value of the '{@link #isDraft() <em>Draft</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #isDraft()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DRAFT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isDraft() <em>Draft</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #isDraft()
	 * @generated
	 * @ordered
	 */
	protected boolean draft = DRAFT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getParentTopic() <em>Parent Topic</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getParentTopic()
	 * @generated
	 * @ordered
	 */
	protected ITopic parentTopic;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Comment() {
		super();
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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public long getIndex() {
		if (getParentTopic() != null) {
			return getParentTopic().getIndex();
		}
		return 0;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IUser getAuthor() {
		return author;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setAuthor(IUser newAuthor) {
		IUser oldAuthor = author;
		author = newAuthor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__AUTHOR, oldAuthor, author));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ICommentType getType() {
		if (type != null && type.eIsProxy()) {
			InternalEObject oldType = (InternalEObject) type;
			type = (ICommentType) eResolveProxy(oldType);
			if (type != oldType) {
				InternalEObject newType = (InternalEObject) type;
				NotificationChain msgs = oldType.eInverseRemove(this, EOPPOSITE_FEATURE_BASE
						- ReviewsPackage.COMMENT__TYPE, null, null);
				if (newType.eInternalContainer() == null) {
					msgs = newType.eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ReviewsPackage.COMMENT__TYPE, null, msgs);
				}
				if (msgs != null)
					msgs.dispatch();
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.COMMENT__TYPE, oldType,
							type));
			}
		}
		return type;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ICommentType basicGetType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetType(ICommentType newType, NotificationChain msgs) {
		ICommentType oldType = type;
		type = newType;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					ReviewsPackage.COMMENT__TYPE, oldType, newType);
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
	public void setType(ICommentType newType) {
		if (newType != type) {
			NotificationChain msgs = null;
			if (type != null)
				msgs = ((InternalEObject) type).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
						- ReviewsPackage.COMMENT__TYPE, null, msgs);
			if (newType != null)
				msgs = ((InternalEObject) newType).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- ReviewsPackage.COMMENT__TYPE, null, msgs);
			msgs = basicSetType(newType, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__TYPE, newType, newType));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__DESCRIPTION, oldDescription,
					description));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__CREATION_DATE,
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
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__MODIFICATION_DATE,
					oldModificationDate, modificationDate));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<IComment> getReplies() {
		if (replies == null) {
			replies = new EObjectContainmentEList.Resolving<IComment>(IComment.class, this,
					ReviewsPackage.COMMENT__REPLIES);
		}
		return replies;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public boolean isDraft() {
		return draft;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setDraft(boolean newDraft) {
		boolean oldDraft = draft;
		draft = newDraft;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__DRAFT, oldDraft, draft));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ITopic getParentTopic() {
		if (parentTopic != null && parentTopic.eIsProxy()) {
			InternalEObject oldParentTopic = (InternalEObject) parentTopic;
			parentTopic = (ITopic) eResolveProxy(oldParentTopic);
			if (parentTopic != oldParentTopic) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReviewsPackage.COMMENT__PARENT_TOPIC,
							oldParentTopic, parentTopic));
			}
		}
		return parentTopic;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ITopic basicGetParentTopic() {
		return parentTopic;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetParentTopic(ITopic newParentTopic, NotificationChain msgs) {
		ITopic oldParentTopic = parentTopic;
		parentTopic = newParentTopic;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					ReviewsPackage.COMMENT__PARENT_TOPIC, oldParentTopic, newParentTopic);
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
	public void setParentTopic(ITopic newParentTopic) {
		if (newParentTopic != parentTopic) {
			NotificationChain msgs = null;
			if (parentTopic != null)
				msgs = ((InternalEObject) parentTopic).eInverseRemove(this, ReviewsPackage.TOPIC__COMMENTS,
						ITopic.class, msgs);
			if (newParentTopic != null)
				msgs = ((InternalEObject) newParentTopic).eInverseAdd(this, ReviewsPackage.TOPIC__COMMENTS,
						ITopic.class, msgs);
			msgs = basicSetParentTopic(newParentTopic, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReviewsPackage.COMMENT__PARENT_TOPIC, newParentTopic,
					newParentTopic));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReviewsPackage.COMMENT__PARENT_TOPIC:
			if (parentTopic != null)
				msgs = ((InternalEObject) parentTopic).eInverseRemove(this, ReviewsPackage.TOPIC__COMMENTS,
						ITopic.class, msgs);
			return basicSetParentTopic((ITopic) otherEnd, msgs);
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
		case ReviewsPackage.COMMENT__TYPE:
			return basicSetType(null, msgs);
		case ReviewsPackage.COMMENT__REPLIES:
			return ((InternalEList<?>) getReplies()).basicRemove(otherEnd, msgs);
		case ReviewsPackage.COMMENT__PARENT_TOPIC:
			return basicSetParentTopic(null, msgs);
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
		case ReviewsPackage.COMMENT__INDEX:
			return getIndex();
		case ReviewsPackage.COMMENT__CREATION_DATE:
			return getCreationDate();
		case ReviewsPackage.COMMENT__MODIFICATION_DATE:
			return getModificationDate();
		case ReviewsPackage.COMMENT__AUTHOR:
			return getAuthor();
		case ReviewsPackage.COMMENT__TYPE:
			if (resolve)
				return getType();
			return basicGetType();
		case ReviewsPackage.COMMENT__DESCRIPTION:
			return getDescription();
		case ReviewsPackage.COMMENT__ID:
			return getId();
		case ReviewsPackage.COMMENT__REPLIES:
			return getReplies();
		case ReviewsPackage.COMMENT__DRAFT:
			return isDraft();
		case ReviewsPackage.COMMENT__PARENT_TOPIC:
			if (resolve)
				return getParentTopic();
			return basicGetParentTopic();
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
		case ReviewsPackage.COMMENT__TYPE:
			setType((ICommentType) newValue);
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
		case ReviewsPackage.COMMENT__PARENT_TOPIC:
			setParentTopic((ITopic) newValue);
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
		case ReviewsPackage.COMMENT__TYPE:
			setType((ICommentType) null);
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
		case ReviewsPackage.COMMENT__PARENT_TOPIC:
			setParentTopic((ITopic) null);
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
			return CREATION_DATE_EDEFAULT == null ? creationDate != null : !CREATION_DATE_EDEFAULT.equals(creationDate);
		case ReviewsPackage.COMMENT__MODIFICATION_DATE:
			return MODIFICATION_DATE_EDEFAULT == null
					? modificationDate != null
					: !MODIFICATION_DATE_EDEFAULT.equals(modificationDate);
		case ReviewsPackage.COMMENT__AUTHOR:
			return author != null;
		case ReviewsPackage.COMMENT__TYPE:
			return type != null;
		case ReviewsPackage.COMMENT__DESCRIPTION:
			return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
		case ReviewsPackage.COMMENT__ID:
			return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
		case ReviewsPackage.COMMENT__REPLIES:
			return replies != null && !replies.isEmpty();
		case ReviewsPackage.COMMENT__DRAFT:
			return draft != DRAFT_EDEFAULT;
		case ReviewsPackage.COMMENT__PARENT_TOPIC:
			return parentTopic != null;
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
		if (baseClass == IIndexed.class) {
			switch (derivedFeatureID) {
			case ReviewsPackage.COMMENT__INDEX:
				return ReviewsPackage.INDEXED__INDEX;
			default:
				return -1;
			}
		}
		if (baseClass == IDated.class) {
			switch (derivedFeatureID) {
			case ReviewsPackage.COMMENT__CREATION_DATE:
				return ReviewsPackage.DATED__CREATION_DATE;
			case ReviewsPackage.COMMENT__MODIFICATION_DATE:
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
		if (baseClass == IIndexed.class) {
			switch (baseFeatureID) {
			case ReviewsPackage.INDEXED__INDEX:
				return ReviewsPackage.COMMENT__INDEX;
			default:
				return -1;
			}
		}
		if (baseClass == IDated.class) {
			switch (baseFeatureID) {
			case ReviewsPackage.DATED__CREATION_DATE:
				return ReviewsPackage.COMMENT__CREATION_DATE;
			case ReviewsPackage.DATED__MODIFICATION_DATE:
				return ReviewsPackage.COMMENT__MODIFICATION_DATE;
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
		result.append(", description: "); //$NON-NLS-1$
		result.append(description);
		result.append(", id: "); //$NON-NLS-1$
		result.append(id);
		result.append(", draft: "); //$NON-NLS-1$
		result.append(draft);
		result.append(')');
		return result.toString();
	}

} //Comment
