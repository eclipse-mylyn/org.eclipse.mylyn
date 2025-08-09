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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Reference</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildReference#getPlan <em>Plan</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildReference#getBuild <em>Build</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public interface IBuildReference {
	/**
	 * Returns the value of the '<em><b>Plan</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Plan</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Plan</em>' attribute.
	 * @see #setPlan(String)
	 * @generated
	 */
	String getPlan();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildReference#getPlan <em>Plan</em>}' attribute. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>Plan</em>' attribute.
	 * @see #getPlan()
	 * @generated
	 */
	void setPlan(String value);

	/**
	 * Returns the value of the '<em><b>Build</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Build</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Build</em>' attribute.
	 * @see #setBuild(String)
	 * @generated
	 */
	String getBuild();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildReference#getBuild <em>Build</em>}' attribute. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>Build</em>' attribute.
	 * @see #getBuild()
	 * @generated
	 */
	void setBuild(String value);

} // IBuildReference
