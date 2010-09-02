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

import org.eclipse.core.runtime.IStatus;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public interface IBuildFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	IBuildFactory INSTANCE = org.eclipse.mylyn.builds.internal.core.BuildFactory.eINSTANCE;

	/**
	 * Returns a new object of class '<em>Artifact</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Artifact</em>'.
	 * @generated
	 */
	IArtifact createArtifact();

	/**
	 * Returns a new object of class '<em>Build</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Build</em>'.
	 * @generated
	 */
	IBuild createBuild();

	/**
	 * Returns a new object of class '<em>Plan</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Plan</em>'.
	 * @generated
	 */
	IBuildPlan createBuildPlan();

	/**
	 * Returns a new object of class '<em>Health Report</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Health Report</em>'.
	 * @generated
	 */
	IHealthReport createHealthReport();

	/**
	 * Returns a new object of class '<em>Server</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Server</em>'.
	 * @generated
	 */
	IBuildServer createBuildServer();

	/**
	 * Returns a new object of class '<em>Model</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Model</em>'.
	 * @generated
	 */
	IBuildModel createBuildModel();

	/**
	 * Returns a new object of class '<em>Change</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Change</em>'.
	 * @generated
	 */
	IChange createChange();

	/**
	 * Returns a new object of class '<em>Change Set</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Change Set</em>'.
	 * @generated
	 */
	IChangeSet createChangeSet();

	/**
	 * Returns a new object of class '<em>Change Artifact</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Change Artifact</em>'.
	 * @generated
	 */
	IChangeArtifact createChangeArtifact();

	/**
	 * Returns a new object of class '<em>User</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>User</em>'.
	 * @generated
	 */
	IUser createUser();

	/**
	 * Returns a new object of class '<em>Choice Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Choice Parameter Definition</em>'.
	 * @generated
	 */
	IChoiceParameterDefinition createChoiceParameterDefinition();

	/**
	 * Returns a new object of class '<em>Boolean Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Boolean Parameter Definition</em>'.
	 * @generated
	 */
	IBooleanParameterDefinition createBooleanParameterDefinition();

	/**
	 * Returns a new object of class '<em>File Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>File Parameter Definition</em>'.
	 * @generated
	 */
	IFileParameterDefinition createFileParameterDefinition();

	/**
	 * Returns a new object of class '<em>Plan Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Plan Parameter Definition</em>'.
	 * @generated
	 */
	IPlanParameterDefinition createPlanParameterDefinition();

	/**
	 * Returns a new object of class '<em>Password Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Password Parameter Definition</em>'.
	 * @generated
	 */
	IPasswordParameterDefinition createPasswordParameterDefinition();

	/**
	 * Returns a new object of class '<em>Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Parameter Definition</em>'.
	 * @generated
	 */
	IBuildParameterDefinition createBuildParameterDefinition();

	/**
	 * Returns a new object of class '<em>String Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>String Parameter Definition</em>'.
	 * @generated
	 */
	IStringParameterDefinition createStringParameterDefinition();

	/**
	 * Returns a new object of class '<em>Test Result</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Test Result</em>'.
	 * @generated
	 */
	ITestResult createTestResult();

	/**
	 * Returns a new object of class '<em>Test Element</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Test Element</em>'.
	 * @generated
	 */
	ITestElement createTestElement();

	/**
	 * Returns a new object of class '<em>Test Suite</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Test Suite</em>'.
	 * @generated
	 */
	ITestSuite createTestSuite();

	/**
	 * Returns a new object of class '<em>Test Case</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Test Case</em>'.
	 * @generated
	 */
	ITestCase createTestCase();

} //IBuildFactory
