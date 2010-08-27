/**
 * <copyright>
 * </copyright>
 *
 * $Id: IBuildParameterDefinition.java,v 1.1 2010/08/27 06:49:23 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import org.eclipse.mylyn.builds.core.IBuildPlan;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IBuild Parameter Definition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.IBuildParameterDefinition#getBuildPlanId <em>Build Plan Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.IBuildParameterDefinition#getBuildPlan <em>Build Plan</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildParameterDefinition()
 * @model kind="class" interface="true" abstract="true"
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
	 * 
	 * @return the value of the '<em>Build Plan Id</em>' attribute.
	 * @see #setBuildPlanId(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildParameterDefinition_BuildPlanId()
	 * @model
	 * @generated
	 */
	String getBuildPlanId();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.IBuildParameterDefinition#getBuildPlanId
	 * <em>Build Plan Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Build Plan Id</em>' attribute.
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
	 * 
	 * @return the value of the '<em>Build Plan</em>' reference.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildParameterDefinition_BuildPlan()
	 * @model type="org.eclipse.mylyn.internal.builds.core.IBuildPlan" transient="true" changeable="false"
	 *        volatile="true" derived="true"
	 * @generated
	 */
	IBuildPlan getBuildPlan();

} // IBuildParameterDefinition
