/**
 * Copyright (c) 2013 Tasktop Technologies and others.
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

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ICommentContainer;
import org.eclipse.mylyn.reviews.core.model.ILocation;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Comment Container</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.CommentContainer#getAllComments <em>All Comments</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.CommentContainer#getComments <em>Comments</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class CommentContainer extends EObjectImpl implements ICommentContainer {
	/**
	 * The cached value of the '{@link #getComments() <em>Comments</em>}' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getComments()
	 * @generated
	 * @ordered
	 */
	protected EList<IComment> comments;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected CommentContainer() {
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.COMMENT_CONTAINER;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public List<IComment> getAllComments() {
		// TODO: implement this method to return the 'All Comments' reference list
		// Ensure that you remove @generated or mark it @generated NOT
		// The list is expected to implement org.eclipse.emf.ecore.util.InternalEList and org.eclipse.emf.ecore.EStructuralFeature.Setting
		// so it's likely that an appropriate subclass of org.eclipse.emf.ecore.util.EcoreEList should be used.
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public List<IComment> getComments() {
		if (comments == null) {
			comments = new EObjectContainmentWithInverseEList.Resolving<>(IComment.class, this,
					ReviewsPackage.COMMENT_CONTAINER__COMMENTS, ReviewsPackage.COMMENT__ITEM);
		}
		return comments;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public List<IComment> getAllDrafts() {
		List<IComment> drafts = new ArrayList<>(getComments());
		for (IComment comment : getAllComments()) {
			if (comment.isDraft()) {
				drafts.add(comment);
			}
		}
		return new EObjectEList.UnmodifiableEList<>(this, ReviewsPackage.Literals.COMMENT_CONTAINER__ALL_DRAFTS,
				drafts.size(), drafts.toArray());
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public List<IComment> getDrafts() {
		List<IComment> drafts = new ArrayList<>(getComments());
		for (IComment comment : getComments()) {
			if (comment.isDraft()) {
				drafts.add(comment);
			}
		}
		return new EObjectEList.UnmodifiableEList<>(this, ReviewsPackage.Literals.COMMENT_CONTAINER__DRAFTS,
				drafts.size(), drafts.toArray());
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public IComment createComment(ILocation initalLocation, String commentText) {
		IComment comment = ReviewsFactory.eINSTANCE.createComment();
		comment.setDescription(commentText);
		comment.setDraft(true);
		Date created = new Date();
		comment.setCreationDate(created);
		if (initalLocation != null) {
			comment.getLocations().add(initalLocation);
		}
		getComments().add(comment);
		return comment;
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
			case ReviewsPackage.COMMENT_CONTAINER__COMMENTS:
				return ((InternalEList<InternalEObject>) (InternalEList<?>) getComments()).basicAdd(otherEnd, msgs);
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
			case ReviewsPackage.COMMENT_CONTAINER__COMMENTS:
				return ((InternalEList<?>) getComments()).basicRemove(otherEnd, msgs);
			case ReviewsPackage.COMMENT_CONTAINER__DRAFTS:
				return ((InternalEList<?>) getDrafts()).basicRemove(otherEnd, msgs);
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
			case ReviewsPackage.COMMENT_CONTAINER__ALL_COMMENTS:
				return getAllComments();
			case ReviewsPackage.COMMENT_CONTAINER__COMMENTS:
				return getComments();
			case ReviewsPackage.COMMENT_CONTAINER__ALL_DRAFTS:
				return getAllDrafts();
			case ReviewsPackage.COMMENT_CONTAINER__DRAFTS:
				return getDrafts();
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
			case ReviewsPackage.COMMENT_CONTAINER__COMMENTS:
				getComments().clear();
				getComments().addAll((Collection<? extends IComment>) newValue);
				return;
			case ReviewsPackage.COMMENT_CONTAINER__DRAFTS:
				getDrafts().clear();
				getDrafts().addAll((Collection<? extends IComment>) newValue);
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
			case ReviewsPackage.COMMENT_CONTAINER__COMMENTS:
				getComments().clear();
				return;
			case ReviewsPackage.COMMENT_CONTAINER__DRAFTS:
				getDrafts().clear();
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
			case ReviewsPackage.COMMENT_CONTAINER__ALL_COMMENTS:
				return !getAllComments().isEmpty();
			case ReviewsPackage.COMMENT_CONTAINER__COMMENTS:
				return comments != null && !comments.isEmpty();
			case ReviewsPackage.COMMENT_CONTAINER__ALL_DRAFTS:
				return !getAllDrafts().isEmpty();
			case ReviewsPackage.COMMENT_CONTAINER__DRAFTS:
				return !getDrafts().isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //CommentContainer
