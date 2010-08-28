/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildSwitch.java,v 1.1 2010/08/28 06:14:17 spingel Exp $
 */
package org.eclipse.mylyn.builds.internal.core;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.builds.core.*;
import org.eclipse.mylyn.builds.core.IArtifact;
import org.eclipse.mylyn.builds.core.IBooleanParameterDefinition;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildParameterDefinition;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.IChange;
import org.eclipse.mylyn.builds.core.IChangeArtifact;
import org.eclipse.mylyn.builds.core.IChangeSet;
import org.eclipse.mylyn.builds.core.IChoiceParameterDefinition;
import org.eclipse.mylyn.builds.core.IFileParameterDefinition;
import org.eclipse.mylyn.builds.core.IParameterDefinition;
import org.eclipse.mylyn.builds.core.IPasswordParameterDefinition;
import org.eclipse.mylyn.builds.core.IPlanParameterDefinition;
import org.eclipse.mylyn.builds.core.IStringParameterDefinition;
import org.eclipse.mylyn.builds.core.ITestCase;
import org.eclipse.mylyn.builds.core.ITestElement;
import org.eclipse.mylyn.builds.core.ITestResult;
import org.eclipse.mylyn.builds.core.ITestSuite;
import org.eclipse.mylyn.builds.core.IUser;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each
 * class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage
 * @generated
 */
public class BuildSwitch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static BuildPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BuildSwitch() {
		if (modelPackage == null) {
			modelPackage = BuildPackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public T doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		} else {
			List<EClass> eSuperTypes = theEClass.getESuperTypes();
			return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
		case BuildPackage.STRING_TO_STRING_MAP: {
			@SuppressWarnings("unchecked")
			Map.Entry<String, String> stringToStringMap = (Map.Entry<String, String>) theEObject;
			T result = caseStringToStringMap(stringToStringMap);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.ARTIFACT: {
			IArtifact artifact = (IArtifact) theEObject;
			T result = caseArtifact(artifact);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.BUILD: {
			IBuild build = (IBuild) theEObject;
			T result = caseBuild(build);
			if (result == null)
				result = caseBuildElement(build);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.BUILD_ELEMENT: {
			IBuildElement buildElement = (IBuildElement) theEObject;
			T result = caseBuildElement(buildElement);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.BUILD_PLAN: {
			IBuildPlan buildPlan = (IBuildPlan) theEObject;
			T result = caseBuildPlan(buildPlan);
			if (result == null)
				result = caseBuildElement(buildPlan);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.BUILD_MODEL: {
			IBuildModel buildModel = (IBuildModel) theEObject;
			T result = caseBuildModel(buildModel);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.BUILD_SERVER: {
			IBuildServer buildServer = (IBuildServer) theEObject;
			T result = caseBuildServer(buildServer);
			if (result == null)
				result = caseBuildElement(buildServer);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.CHANGE: {
			IChange change = (IChange) theEObject;
			T result = caseChange(change);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.CHANGE_SET: {
			IChangeSet changeSet = (IChangeSet) theEObject;
			T result = caseChangeSet(changeSet);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.CHANGE_ARTIFACT: {
			IChangeArtifact changeArtifact = (IChangeArtifact) theEObject;
			T result = caseChangeArtifact(changeArtifact);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.USER: {
			IUser user = (IUser) theEObject;
			T result = caseUser(user);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.PARAMETER_DEFINITION: {
			IParameterDefinition parameterDefinition = (IParameterDefinition) theEObject;
			T result = caseParameterDefinition(parameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.CHOICE_PARAMETER_DEFINITION: {
			IChoiceParameterDefinition choiceParameterDefinition = (IChoiceParameterDefinition) theEObject;
			T result = caseChoiceParameterDefinition(choiceParameterDefinition);
			if (result == null)
				result = caseParameterDefinition(choiceParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION: {
			IBooleanParameterDefinition booleanParameterDefinition = (IBooleanParameterDefinition) theEObject;
			T result = caseBooleanParameterDefinition(booleanParameterDefinition);
			if (result == null)
				result = caseParameterDefinition(booleanParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.FILE_PARAMETER_DEFINITION: {
			IFileParameterDefinition fileParameterDefinition = (IFileParameterDefinition) theEObject;
			T result = caseFileParameterDefinition(fileParameterDefinition);
			if (result == null)
				result = caseParameterDefinition(fileParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.PLAN_PARAMETER_DEFINITION: {
			IPlanParameterDefinition planParameterDefinition = (IPlanParameterDefinition) theEObject;
			T result = casePlanParameterDefinition(planParameterDefinition);
			if (result == null)
				result = caseParameterDefinition(planParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.PASSWORD_PARAMETER_DEFINITION: {
			IPasswordParameterDefinition passwordParameterDefinition = (IPasswordParameterDefinition) theEObject;
			T result = casePasswordParameterDefinition(passwordParameterDefinition);
			if (result == null)
				result = caseParameterDefinition(passwordParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.BUILD_PARAMETER_DEFINITION: {
			IBuildParameterDefinition buildParameterDefinition = (IBuildParameterDefinition) theEObject;
			T result = caseBuildParameterDefinition(buildParameterDefinition);
			if (result == null)
				result = caseParameterDefinition(buildParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.STRING_PARAMETER_DEFINITION: {
			IStringParameterDefinition stringParameterDefinition = (IStringParameterDefinition) theEObject;
			T result = caseStringParameterDefinition(stringParameterDefinition);
			if (result == null)
				result = caseParameterDefinition(stringParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.TEST_RESULT: {
			ITestResult testResult = (ITestResult) theEObject;
			T result = caseTestResult(testResult);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.TEST_ELEMENT: {
			ITestElement testElement = (ITestElement) theEObject;
			T result = caseTestElement(testElement);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.TEST_SUITE: {
			ITestSuite testSuite = (ITestSuite) theEObject;
			T result = caseTestSuite(testSuite);
			if (result == null)
				result = caseTestElement(testSuite);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.TEST_CASE: {
			ITestCase testCase = (ITestCase) theEObject;
			T result = caseTestCase(testCase);
			if (result == null)
				result = caseTestElement(testCase);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		default:
			return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBuildElement(IBuildElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Artifact</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Artifact</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseArtifact(IArtifact object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Build</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Build</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBuild(IBuild object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Plan</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Plan</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBuildPlan(IBuildPlan object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Server</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Server</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBuildServer(IBuildServer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Model</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Model</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBuildModel(IBuildModel object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Change</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Change</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseChange(IChange object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Change Set</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Change Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseChangeSet(IChangeSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Change Artifact</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Change Artifact</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseChangeArtifact(IChangeArtifact object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String To String Map</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String To String Map</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringToStringMap(Map.Entry<String, String> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>User</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>User</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseUser(IUser object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameterDefinition(IParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Choice Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Choice Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseChoiceParameterDefinition(IChoiceParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Boolean Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Boolean Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBooleanParameterDefinition(IBooleanParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>File Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>File Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFileParameterDefinition(IFileParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Plan Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Plan Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePlanParameterDefinition(IPlanParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Password Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Password Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePasswordParameterDefinition(IPasswordParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBuildParameterDefinition(IBuildParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringParameterDefinition(IStringParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Test Result</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Test Result</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTestResult(ITestResult object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Test Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Test Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTestElement(ITestElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Test Suite</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Test Suite</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTestSuite(ITestSuite object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Test Case</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Test Case</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTestCase(ITestCase object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public T defaultCase(EObject object) {
		return null;
	}

} //BuildSwitch
