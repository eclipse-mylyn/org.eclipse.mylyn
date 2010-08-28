/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.core;

import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildModel#getServers <em>Servers</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildModel#getPlans <em>Plans</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.core.IBuildModel#getBuilds <em>Builds</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IBuildModel {
	/**
	 * Returns the value of the '<em><b>Servers</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.builds.core.IBuildServer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Servers</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Servers</em>' containment reference list.
	 * @generated
	 */
	List<IBuildServer> getServers();

	/**
	 * Returns the value of the '<em><b>Plans</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.builds.core.IBuildPlan}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Plans</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Plans</em>' containment reference list.
	 * @generated
	 */
	List<IBuildPlan> getPlans();

	/**
	 * Returns the value of the '<em><b>Builds</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.builds.core.IBuild}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Builds</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Builds</em>' containment reference list.
	 * @generated
	 */
	List<IBuild> getBuilds();

} // IBuildModel
