/**
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
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

import org.eclipse.team.core.history.IFileRevision;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>File Version</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getPath <em>Path</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getDescription <em>Description</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getContent <em>Content</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getFile <em>File</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getFileRevision <em>File Revision</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getBinaryContent <em>Binary Content</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IFileVersion extends IReviewItem {
	/**
	 * Returns the value of the '<em><b>Path</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Path</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Path</em>' attribute.
	 * @see #setPath(String)
	 * @generated
	 */
	String getPath();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getPath <em>Path</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Path</em>' attribute.
	 * @see #getPath()
	 * @generated
	 */
	void setPath(String value);

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
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getDescription
	 * <em>Description</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Content</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Content</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Content</em>' attribute.
	 * @see #setContent(String)
	 * @generated
	 */
	String getContent();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getContent <em>Content</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Content</em>' attribute.
	 * @see #getContent()
	 * @generated
	 */
	void setContent(String value);

	/**
	 * Returns the value of the '<em><b>File</b></em>' reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>File</em>' reference isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>File</em>' reference.
	 * @see #setFile(IFileItem)
	 * @generated
	 */
	IFileItem getFile();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getFile <em>File</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>File</em>' reference.
	 * @see #getFile()
	 * @generated
	 */
	void setFile(IFileItem value);

	/**
	 * Returns the value of the '<em><b>File Revision</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>File Revision</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>File Revision</em>' attribute.
	 * @see #setFileRevision(IFileRevision)
	 * @generated
	 */
	IFileRevision getFileRevision();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getFileRevision <em>File
	 * Revision</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>File Revision</em>' attribute.
	 * @see #getFileRevision()
	 * @generated
	 */
	void setFileRevision(IFileRevision value);

	/**
	 * Returns the value of the '<em><b>Binary Content</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Binary Content</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Binary Content</em>' attribute.
	 * @see #setBinaryContent(byte[])
	 * @generated
	 */
	byte[] getBinaryContent();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getBinaryContent <em>Binary
	 * Content</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Binary Content</em>' attribute.
	 * @see #getBinaryContent()
	 * @generated
	 */
	void setBinaryContent(byte[] value);

} // IFileVersion
