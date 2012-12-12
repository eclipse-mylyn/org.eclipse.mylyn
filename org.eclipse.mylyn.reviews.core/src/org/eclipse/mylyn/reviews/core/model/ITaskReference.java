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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Task Reference</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ITaskReference#getTaskId <em>Task Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ITaskReference#getRepositoryURL <em>Repository URL</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface ITaskReference extends IReviewComponent {
	/**
	 * Returns the value of the '<em><b>Task Id</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Task Id</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Task Id</em>' attribute.
	 * @see #setTaskId(String)
	 * @generated
	 */
	String getTaskId();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.ITaskReference#getTaskId <em>Task Id</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Task Id</em>' attribute.
	 * @see #getTaskId()
	 * @generated
	 */
	void setTaskId(String value);

	/**
	 * Returns the value of the '<em><b>Repository URL</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Repository URL</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Repository URL</em>' attribute.
	 * @see #setRepositoryURL(String)
	 * @generated
	 */
	String getRepositoryURL();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.ITaskReference#getRepositoryURL
	 * <em>Repository URL</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Repository URL</em>' attribute.
	 * @see #getRepositoryURL()
	 * @generated
	 */
	void setRepositoryURL(String value);

} // ITaskReference
