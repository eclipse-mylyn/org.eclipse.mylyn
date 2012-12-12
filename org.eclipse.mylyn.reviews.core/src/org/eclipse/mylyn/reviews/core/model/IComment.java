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
package org.eclipse.mylyn.reviews.core.model;

import java.util.Date;
import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Comment</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#getAuthor <em>Author</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#getType <em>Type</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#getDescription <em>Description</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#getCreationDate <em>Creation Date</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#getId <em>Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#getReplies <em>Replies</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IComment#isDraft <em>Draft</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IComment extends IReviewComponent {
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
	 * Returns the value of the '<em><b>Type</b></em>' containment reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Type</em>' containment reference.
	 * @see #setType(ICommentType)
	 * @generated
	 */
	ICommentType getType();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IComment#getType <em>Type</em>}' containment
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Type</em>' containment reference.
	 * @see #getType()
	 * @generated
	 */
	void setType(ICommentType value);

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
	 * Returns the value of the '<em><b>Creation Date</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Creation Date</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Creation Date</em>' attribute.
	 * @see #setCreationDate(Date)
	 * @generated
	 */
	Date getCreationDate();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IComment#getCreationDate
	 * <em>Creation Date</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Creation Date</em>' attribute.
	 * @see #getCreationDate()
	 * @generated
	 */
	void setCreationDate(Date value);

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
	 * Returns the value of the '<em><b>Replies</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IComment}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Replies</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Replies</em>' containment reference list.
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

} // IComment
