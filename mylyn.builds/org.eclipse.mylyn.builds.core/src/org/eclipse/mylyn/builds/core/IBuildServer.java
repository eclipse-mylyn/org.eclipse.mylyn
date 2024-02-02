/**
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Server</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildServer#getLocation <em>Location</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildServer#getConnectorKind <em>Connector Kind</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildServer#getRepositoryUrl <em>Repository Url</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IBuildServer extends IBuildElement {
	/**
	 * Returns the value of the '<em><b>Location</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Location</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Location</em>' attribute.
	 * @see #setLocation(RepositoryLocation)
	 * @generated
	 */
	RepositoryLocation getLocation();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildServer#getLocation <em>Location</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Location</em>' attribute.
	 * @see #getLocation()
	 * @generated
	 */
	void setLocation(RepositoryLocation value);

	/**
	 * Returns the value of the '<em><b>Connector Kind</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Connector Kind</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Connector Kind</em>' attribute.
	 * @see #setConnectorKind(String)
	 * @generated
	 */
	String getConnectorKind();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildServer#getConnectorKind <em>Connector Kind</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Connector Kind</em>' attribute.
	 * @see #getConnectorKind()
	 * @generated
	 */
	void setConnectorKind(String value);

	/**
	 * Returns the value of the '<em><b>Repository Url</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Repository Url</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Repository Url</em>' attribute.
	 * @see #setRepositoryUrl(String)
	 * @generated
	 */
	String getRepositoryUrl();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildServer#getRepositoryUrl <em>Repository Url</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Repository Url</em>' attribute.
	 * @see #getRepositoryUrl()
	 * @generated
	 */
	void setRepositoryUrl(String value);

	IBuildServerConfiguration getConfiguration() throws CoreException;

} // IBuildServer
