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
package org.eclipse.mylyn.reviews.core.model;

import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Comment</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#getAuthor <em>Author</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#getDescription <em>Description</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#getId <em>Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#getReplies <em>Replies</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#isDraft <em>Draft</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#getLocations <em>Locations</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#getReview <em>Review</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#getTitle <em>Title</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#getItem <em>Item</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#isMine <em>Mine</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IComment extends IIndexed, IDated {
	/**
	 * Returns the value of the '<em><b>Author</b></em>' reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Author</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Author</em>' reference.
	 * @see #setAuthor(IUser)
	 * @generated
	 */
	IUser getAuthor();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IComment#getAuthor <em>Author</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Author</em>' reference.
	 * @see #getAuthor()
	 * @generated
	 */
	void setAuthor(IUser value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IComment#getDescription <em>Description</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IComment#getId <em>Id</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Replies</b></em>' reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IComment}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Replies</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Replies</em>' reference list.
	 * @generated
	 */
	List<IComment> getReplies();

	/**
	 * Returns the value of the '<em><b>Draft</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Draft</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Draft</em>' attribute.
	 * @see #setDraft(boolean)
	 * @generated
	 */
	boolean isDraft();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IComment#isDraft <em>Draft</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Draft</em>' attribute.
	 * @see #isDraft()
	 * @generated
	 */
	void setDraft(boolean value);

	/**
	 * Returns the value of the '<em><b>Locations</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.ILocation}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Locations</em>' containment reference list isn't clear, there really should be more of
	 * a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Locations</em>' containment reference list.
	 * @generated
	 */
	List<ILocation> getLocations();

	/**
	 * Returns the value of the '<em><b>Review</b></em>' reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Review</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Review</em>' reference.
	 * @generated
	 */
	IReview getReview();

	/**
	 * Returns the value of the '<em><b>Title</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Title</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Title</em>' attribute.
	 * @see #setTitle(String)
	 * @generated
	 */
	String getTitle();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IComment#getTitle <em>Title</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Title</em>' attribute.
	 * @see #getTitle()
	 * @generated
	 */
	void setTitle(String value);

	/**
	 * Returns the value of the '<em><b>Item</b></em>' container reference. It is bidirectional and its opposite is '
	 * {@link org.eclipse.mylyn.reviews.core.model.ICommentContainer#getComments <em>Comments</em>}'. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Item</em>' container reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Item</em>' container reference.
	 * @see #setItem(ICommentContainer)
	 * @see org.eclipse.mylyn.reviews.core.model.ICommentContainer#getComments
	 * @generated
	 */
	ICommentContainer getItem();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IComment#getItem <em>Item</em>}' container
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Item</em>' container reference.
	 * @see #getItem()
	 * @generated
	 */
	void setItem(ICommentContainer value);

	/**
	 * Returns the value of the '<em><b>Mine</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mine</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Mine</em>' attribute.
	 * @generated
	 */
	boolean isMine();

} // IComment
