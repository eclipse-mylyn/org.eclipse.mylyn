/**
 * Copyright (c) 2011, 2015 Tasktop Technologies and others.
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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Review Item Set</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getItems <em>Items</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getRevision <em>Revision</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getParentReview <em>Parent Review</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getParentCommits <em>Parent Commits</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#isInNeedOfRetrieval <em>In Need Of Retrieval</em>}</li>
 * </ul>
 *
 * @generated
 */
public interface IReviewItemSet extends IReviewItem, IDated {
	/**
	 * Returns the value of the '<em><b>Items</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IFileItem}. It is bidirectional and its opposite is
	 * '{@link org.eclipse.mylyn.reviews.core.model.IFileItem#getSet <em>Set</em>}'. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Items</em>' containment reference list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Items</em>' containment reference list.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileItem#getSet
	 * @generated
	 */
	List<IFileItem> getItems();

	/**
	 * Returns the value of the '<em><b>Revision</b></em>' attribute. The default value is <code>""</code>. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Revision</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Revision</em>' attribute.
	 * @see #setRevision(String)
	 * @generated
	 */
	String getRevision();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getRevision <em>Revision</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Revision</em>' attribute.
	 * @see #getRevision()
	 * @generated
	 */
	void setRevision(String value);

	/**
	 * Returns the value of the '<em><b>Parent Review</b></em>' container reference. It is bidirectional and its opposite is
	 * '{@link org.eclipse.mylyn.reviews.core.model.IReview#getSets <em>Sets</em>}'. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Review</em>' container reference isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Parent Review</em>' container reference.
	 * @see #setParentReview(IReview)
	 * @see org.eclipse.mylyn.reviews.core.model.IReview#getSets
	 * @generated
	 */
	IReview getParentReview();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getParentReview <em>Parent Review</em>}' container
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Parent Review</em>' container reference.
	 * @see #getParentReview()
	 * @generated
	 */
	void setParentReview(IReview value);

	/**
	 * Returns the value of the '<em><b>Parent Commits</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.ICommit}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Commits</em>' containment reference list isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Parent Commits</em>' containment reference list.
	 * @generated
	 */
	List<ICommit> getParentCommits();

	/**
	 * Returns the value of the '<em><b>In Need Of Retrieval</b></em>' attribute. The default value is <code>"false"</code>. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>In Need Of Retrieval</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>In Need Of Retrieval</em>' attribute.
	 * @see #setInNeedOfRetrieval(boolean)
	 * @generated
	 */
	boolean isInNeedOfRetrieval();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#isInNeedOfRetrieval <em>In Need Of Retrieval</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>In Need Of Retrieval</em>' attribute.
	 * @see #isInNeedOfRetrieval()
	 * @generated
	 */
	void setInNeedOfRetrieval(boolean value);

} // IReviewItemSet
