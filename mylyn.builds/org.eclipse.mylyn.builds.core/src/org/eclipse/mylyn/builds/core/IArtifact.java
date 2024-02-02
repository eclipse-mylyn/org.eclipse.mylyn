/*******************************************************************************
 * Copyright (c) 2010, 2011 Markus Knittig and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Markus Knittig  - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.core;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Artifact</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.core.IArtifact#getRelativePath <em>Relative Path</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IArtifact extends IBuildElement {
	/**
	 * Returns the value of the '<em><b>Relative Path</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Relative Path</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Relative Path</em>' attribute.
	 * @see #setRelativePath(String)
	 * @generated
	 */
	String getRelativePath();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IArtifact#getRelativePath <em>Relative Path</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Relative Path</em>' attribute.
	 * @see #getRelativePath()
	 * @generated
	 */
	void setRelativePath(String value);

} // IArtifact
