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
package org.eclipse.mylyn.reviews.core.model;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Comment Container</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ICommentContainer#getAllComments <em>All Comments</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ICommentContainer#getComments <em>Comments</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public interface ICommentContainer extends EObject {
	/**
	 * Returns the value of the '<em><b>All Comments</b></em>' reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IComment}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>All Comments</em>' reference list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>All Comments</em>' reference list.
	 * @generated
	 */
	List<IComment> getAllComments();

	/**
	 * Returns the value of the '<em><b>Comments</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IComment}. It is bidirectional and its opposite is '
	 * {@link org.eclipse.mylyn.reviews.core.model.IComment#getItem <em>Item</em>}'. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Comments</em>' containment reference list isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Comments</em>' containment reference list.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#getItem
	 * @generated
	 */
	List<IComment> getComments();

	/**
	 * Returns the value of the '<em><b>All Drafts</b></em>' reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IComment}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>All Drafts</em>' reference list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>All Drafts</em>' reference list.
	 * @generated
	 */
	List<IComment> getAllDrafts();

	/**
	 * Returns the value of the '<em><b>Drafts</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IComment}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Drafts</em>' containment reference list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Drafts</em>' containment reference list.
	 * @generated
	 */
	List<IComment> getDrafts();

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	IComment createComment(ILocation initalLocation, String commentText);

} // ICommentContainer
