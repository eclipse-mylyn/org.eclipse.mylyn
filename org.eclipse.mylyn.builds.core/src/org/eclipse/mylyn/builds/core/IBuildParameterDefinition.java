/**
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.builds.core;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameter Definition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.mylyn.builds.core.IBuildParameterDefinition#getBuildPlanId <em>Build Plan Id</em>}</li>
 *   <li>{@link org.eclipse.mylyn.builds.core.IBuildParameterDefinition#getBuildPlan <em>Build Plan</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public interface IBuildParameterDefinition extends IParameterDefinition {
	/**
	 * Returns the value of the '<em><b>Build Plan Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Build Plan Id</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Build Plan Id</em>' attribute.
	 * @see #setBuildPlanId(String)
	 * @generated
	 */
	String getBuildPlanId();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.core.IBuildParameterDefinition#getBuildPlanId <em>Build Plan Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Build Plan Id</em>' attribute.
	 * @see #getBuildPlanId()
	 * @generated
	 */
	void setBuildPlanId(String value);

	/**
	 * Returns the value of the '<em><b>Build Plan</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Build Plan</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Build Plan</em>' reference.
	 * @generated
	 */
	IBuildPlan getBuildPlan();

} // IBuildParameterDefinition
