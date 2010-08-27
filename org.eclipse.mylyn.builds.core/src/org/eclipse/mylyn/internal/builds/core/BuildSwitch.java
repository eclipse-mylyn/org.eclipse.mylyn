/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildSwitch.java,v 1.9 2010/08/27 06:49:23 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.builds.core.IArtifact;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildPlanData;
import org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.IBuildWorkingCopy;
import org.eclipse.mylyn.builds.core.IChange;
import org.eclipse.mylyn.builds.core.IChangeSet;
import org.eclipse.mylyn.builds.core.IFile;
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
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage
 * @generated
 */
public class BuildSwitch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected static BuildPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public BuildSwitch() {
		if (modelPackage == null) {
			modelPackage = BuildPackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that
	 * result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public T doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that
	 * result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
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
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that
	 * result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
		case BuildPackage.ARTIFACT: {
			Artifact artifact = (Artifact) theEObject;
			T result = caseArtifact(artifact);
			if (result == null)
				result = caseIArtifact(artifact);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.BUILD: {
			Build build = (Build) theEObject;
			T result = caseBuild(build);
			if (result == null)
				result = caseIBuildWorkingCopy(build);
			if (result == null)
				result = caseIBuild(build);
			if (result == null)
				result = caseIBuildElement(build);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.BUILD_PLAN: {
			BuildPlan buildPlan = (BuildPlan) theEObject;
			T result = caseBuildPlan(buildPlan);
			if (result == null)
				result = caseIBuildPlan(buildPlan);
			if (result == null)
				result = caseIBuildPlanWorkingCopy(buildPlan);
			if (result == null)
				result = caseIBuildElement(buildPlan);
			if (result == null)
				result = caseIBuildPlanData(buildPlan);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.BUILD_SERVER: {
			BuildServer buildServer = (BuildServer) theEObject;
			T result = caseBuildServer(buildServer);
			if (result == null)
				result = caseIBuildServer(buildServer);
			if (result == null)
				result = caseIBuildElement(buildServer);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.BUILD_MODEL: {
			BuildModel buildModel = (BuildModel) theEObject;
			T result = caseBuildModel(buildModel);
			if (result == null)
				result = caseIBuildModel(buildModel);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.CHANGE: {
			Change change = (Change) theEObject;
			T result = caseChange(change);
			if (result == null)
				result = caseIChange(change);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.CHANGE_SET: {
			ChangeSet changeSet = (ChangeSet) theEObject;
			T result = caseChangeSet(changeSet);
			if (result == null)
				result = caseIChangeSet(changeSet);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.FILE: {
			File file = (File) theEObject;
			T result = caseFile(file);
			if (result == null)
				result = caseIFile(file);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.STRING_TO_STRING_MAP: {
			@SuppressWarnings("unchecked")
			Map.Entry<String, String> stringToStringMap = (Map.Entry<String, String>) theEObject;
			T result = caseStringToStringMap(stringToStringMap);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.USER: {
			User user = (User) theEObject;
			T result = caseUser(user);
			if (result == null)
				result = caseIUser(user);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.IPARAMETER_DEFINITION: {
			IParameterDefinition iParameterDefinition = (IParameterDefinition) theEObject;
			T result = caseIParameterDefinition(iParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.IFILE_PARAMETER_DEFINITION: {
			IFileParameterDefinition iFileParameterDefinition = (IFileParameterDefinition) theEObject;
			T result = caseIFileParameterDefinition(iFileParameterDefinition);
			if (result == null)
				result = caseIParameterDefinition(iFileParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.IBOOLEAN_PARAMETER_DEFINITION: {
			IBooleanParameterDefinition iBooleanParameterDefinition = (IBooleanParameterDefinition) theEObject;
			T result = caseIBooleanParameterDefinition(iBooleanParameterDefinition);
			if (result == null)
				result = caseIParameterDefinition(iBooleanParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.ICHOICE_PARAMETER_DEFINITION: {
			IChoiceParameterDefinition iChoiceParameterDefinition = (IChoiceParameterDefinition) theEObject;
			T result = caseIChoiceParameterDefinition(iChoiceParameterDefinition);
			if (result == null)
				result = caseIParameterDefinition(iChoiceParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.CHOICE_PARAMETER_DEFINITION: {
			ChoiceParameterDefinition choiceParameterDefinition = (ChoiceParameterDefinition) theEObject;
			T result = caseChoiceParameterDefinition(choiceParameterDefinition);
			if (result == null)
				result = caseIChoiceParameterDefinition(choiceParameterDefinition);
			if (result == null)
				result = caseIParameterDefinition(choiceParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION: {
			BooleanParameterDefinition booleanParameterDefinition = (BooleanParameterDefinition) theEObject;
			T result = caseBooleanParameterDefinition(booleanParameterDefinition);
			if (result == null)
				result = caseIBooleanParameterDefinition(booleanParameterDefinition);
			if (result == null)
				result = caseIParameterDefinition(booleanParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.FILE_PARAMETER_DEFINITION: {
			FileParameterDefinition fileParameterDefinition = (FileParameterDefinition) theEObject;
			T result = caseFileParameterDefinition(fileParameterDefinition);
			if (result == null)
				result = caseIFileParameterDefinition(fileParameterDefinition);
			if (result == null)
				result = caseIParameterDefinition(fileParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.IPLAN_PARAMETER_DEFINITION: {
			IPlanParameterDefinition iPlanParameterDefinition = (IPlanParameterDefinition) theEObject;
			T result = caseIPlanParameterDefinition(iPlanParameterDefinition);
			if (result == null)
				result = caseIParameterDefinition(iPlanParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.PLAN_PARAMETER_DEFINITION: {
			PlanParameterDefinition planParameterDefinition = (PlanParameterDefinition) theEObject;
			T result = casePlanParameterDefinition(planParameterDefinition);
			if (result == null)
				result = caseIPlanParameterDefinition(planParameterDefinition);
			if (result == null)
				result = caseIParameterDefinition(planParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.IPASSWORD_PARAMETER_DEFINITION: {
			IPasswordParameterDefinition iPasswordParameterDefinition = (IPasswordParameterDefinition) theEObject;
			T result = caseIPasswordParameterDefinition(iPasswordParameterDefinition);
			if (result == null)
				result = caseIParameterDefinition(iPasswordParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.PASSWORD_PARAMETER_DEFINITION: {
			PasswordParameterDefinition passwordParameterDefinition = (PasswordParameterDefinition) theEObject;
			T result = casePasswordParameterDefinition(passwordParameterDefinition);
			if (result == null)
				result = caseIPasswordParameterDefinition(passwordParameterDefinition);
			if (result == null)
				result = caseIParameterDefinition(passwordParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.IBUILD_PARAMETER_DEFINITION: {
			IBuildParameterDefinition iBuildParameterDefinition = (IBuildParameterDefinition) theEObject;
			T result = caseIBuildParameterDefinition(iBuildParameterDefinition);
			if (result == null)
				result = caseIParameterDefinition(iBuildParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.BUILD_PARAMETER_DEFINITION: {
			BuildParameterDefinition buildParameterDefinition = (BuildParameterDefinition) theEObject;
			T result = caseBuildParameterDefinition(buildParameterDefinition);
			if (result == null)
				result = caseIBuildParameterDefinition(buildParameterDefinition);
			if (result == null)
				result = caseIParameterDefinition(buildParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.ISTRING_PARAMETER_DEFINITION: {
			IStringParameterDefinition iStringParameterDefinition = (IStringParameterDefinition) theEObject;
			T result = caseIStringParameterDefinition(iStringParameterDefinition);
			if (result == null)
				result = caseIParameterDefinition(iStringParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case BuildPackage.STRING_PARAMETER_DEFINITION: {
			StringParameterDefinition stringParameterDefinition = (StringParameterDefinition) theEObject;
			T result = caseStringParameterDefinition(stringParameterDefinition);
			if (result == null)
				result = caseIStringParameterDefinition(stringParameterDefinition);
			if (result == null)
				result = caseIParameterDefinition(stringParameterDefinition);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		default:
			return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Model</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Model</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBuildModel(BuildModel object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Plan</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Plan</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBuildPlan(BuildPlan object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Server</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Server</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBuildServer(BuildServer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IBuild Model</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IBuild Model</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIBuildModel(IBuildModel object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IBuild Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IBuild Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIBuildElement(IBuildElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IBuild Plan</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IBuild Plan</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIBuildPlan(IBuildPlan object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IBuild Working Copy</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IBuild Working Copy</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIBuildWorkingCopy(IBuildWorkingCopy object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IBuild Plan Data</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IBuild Plan Data</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIBuildPlanData(IBuildPlanData object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IBuild Plan Working Copy</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IBuild Plan Working Copy</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIBuildPlanWorkingCopy(IBuildPlanWorkingCopy object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IBuild Server</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IBuild Server</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIBuildServer(IBuildServer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Artifact</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Artifact</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseArtifact(Artifact object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Build</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Build</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBuild(Build object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Change Set</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Change Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseChangeSet(ChangeSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Change</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Change</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseChange(Change object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>File</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>File</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFile(File object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>User</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>User</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseUser(User object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IParameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IParameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIParameterDefinition(IParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IFile Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IFile Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIFileParameterDefinition(IFileParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IBoolean Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IBoolean Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIBooleanParameterDefinition(IBooleanParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IChoice Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IChoice Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIChoiceParameterDefinition(IChoiceParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Choice Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Choice Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseChoiceParameterDefinition(ChoiceParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Boolean Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Boolean Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBooleanParameterDefinition(BooleanParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>File Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>File Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFileParameterDefinition(FileParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IPlan Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IPlan Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIPlanParameterDefinition(IPlanParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Plan Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Plan Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePlanParameterDefinition(PlanParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IPassword Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IPassword Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIPasswordParameterDefinition(IPasswordParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Password Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Password Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePasswordParameterDefinition(PasswordParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IBuild Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IBuild Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIBuildParameterDefinition(IBuildParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBuildParameterDefinition(BuildParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IString Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IString Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIStringParameterDefinition(IStringParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String Parameter Definition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String Parameter Definition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringParameterDefinition(StringParameterDefinition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IArtifact</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IArtifact</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIArtifact(IArtifact object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IBuild</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IBuild</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIBuild(IBuild object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IChange Set</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IChange Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIChangeSet(IChangeSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IChange</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IChange</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIChange(IChange object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IFile</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IFile</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIFile(IFile object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IUser</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IUser</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIUser(IUser object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String To String Map</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String To String Map</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringToStringMap(Map.Entry<String, String> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public T defaultCase(EObject object) {
		return null;
	}

} //BuildSwitch
