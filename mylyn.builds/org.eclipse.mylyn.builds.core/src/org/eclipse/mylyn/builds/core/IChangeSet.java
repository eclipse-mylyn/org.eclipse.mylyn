/**
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.builds.core;

import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Change Set</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.core.IChangeSet#getChanges <em>Changes</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IChangeSet#getKind <em>Kind</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IChangeSet {
	/**
	 * Returns the value of the '<em><b>Changes</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.builds.core.IChange}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Changes</em>' reference list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Changes</em>' containment reference list.
	 * @generated
	 */
	List<IChange> getChanges();

	/**
	 * Returns the value of the '<em><b>Kind</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Kind</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Kind</em>' attribute.
	 * @see #setKind(String)
	 * @generated
	 */
	String getKind();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IChangeSet#getKind <em>Kind</em>}' attribute. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Kind</em>' attribute.
	 * @see #getKind()
	 * @generated
	 */
	void setKind(String value);

} // IChangeSet
