/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildFactory.java,v 1.2 2010/08/28 09:21:12 spingel Exp $
 */
package org.eclipse.mylyn.builds.internal.core;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.mylyn.builds.core.*;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.EditType;
import org.eclipse.mylyn.builds.core.IArtifact;
import org.eclipse.mylyn.builds.core.IBooleanParameterDefinition;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildParameterDefinition;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.IChange;
import org.eclipse.mylyn.builds.core.IChangeArtifact;
import org.eclipse.mylyn.builds.core.IChangeSet;
import org.eclipse.mylyn.builds.core.IChoiceParameterDefinition;
import org.eclipse.mylyn.builds.core.IFileParameterDefinition;
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
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @generated
 */
public class BuildFactory extends EFactoryImpl implements IBuildFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final BuildFactory eINSTANCE = init();

	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static BuildFactory init() {
		try {
			BuildFactory theBuildFactory = (BuildFactory) EPackage.Registry.INSTANCE
					.getEFactory("http://eclipse.org/mylyn/models/build"); //$NON-NLS-1$ 
			if (theBuildFactory != null) {
				return theBuildFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new BuildFactory();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BuildFactory() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case BuildPackage.STRING_TO_STRING_MAP:
			return (EObject) createStringToStringMap();
		case BuildPackage.ARTIFACT:
			return (EObject) createArtifact();
		case BuildPackage.BUILD:
			return (EObject) createBuild();
		case BuildPackage.BUILD_PLAN:
			return (EObject) createBuildPlan();
		case BuildPackage.BUILD_MODEL:
			return (EObject) createBuildModel();
		case BuildPackage.BUILD_SERVER:
			return (EObject) createBuildServer();
		case BuildPackage.CHANGE:
			return (EObject) createChange();
		case BuildPackage.CHANGE_SET:
			return (EObject) createChangeSet();
		case BuildPackage.CHANGE_ARTIFACT:
			return (EObject) createChangeArtifact();
		case BuildPackage.USER:
			return (EObject) createUser();
		case BuildPackage.CHOICE_PARAMETER_DEFINITION:
			return (EObject) createChoiceParameterDefinition();
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION:
			return (EObject) createBooleanParameterDefinition();
		case BuildPackage.FILE_PARAMETER_DEFINITION:
			return (EObject) createFileParameterDefinition();
		case BuildPackage.PLAN_PARAMETER_DEFINITION:
			return (EObject) createPlanParameterDefinition();
		case BuildPackage.PASSWORD_PARAMETER_DEFINITION:
			return (EObject) createPasswordParameterDefinition();
		case BuildPackage.BUILD_PARAMETER_DEFINITION:
			return (EObject) createBuildParameterDefinition();
		case BuildPackage.STRING_PARAMETER_DEFINITION:
			return (EObject) createStringParameterDefinition();
		case BuildPackage.TEST_RESULT:
			return (EObject) createTestResult();
		case BuildPackage.TEST_ELEMENT:
			return (EObject) createTestElement();
		case BuildPackage.TEST_SUITE:
			return (EObject) createTestSuite();
		case BuildPackage.TEST_CASE:
			return (EObject) createTestCase();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
		case BuildPackage.TEST_CASE_RESULT:
			return createTestCaseResultFromString(eDataType, initialValue);
		case BuildPackage.BUILD_STATE:
			return createBuildStateFromString(eDataType, initialValue);
		case BuildPackage.BUILD_STATUS:
			return createBuildStatusFromString(eDataType, initialValue);
		case BuildPackage.EDIT_TYPE:
			return createEditTypeFromString(eDataType, initialValue);
		case BuildPackage.ISTATUS:
			return createIStatusFromString(eDataType, initialValue);
		case BuildPackage.IOPERATION:
			return createIOperationFromString(eDataType, initialValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
		case BuildPackage.TEST_CASE_RESULT:
			return convertTestCaseResultToString(eDataType, instanceValue);
		case BuildPackage.BUILD_STATE:
			return convertBuildStateToString(eDataType, instanceValue);
		case BuildPackage.BUILD_STATUS:
			return convertBuildStatusToString(eDataType, instanceValue);
		case BuildPackage.EDIT_TYPE:
			return convertEditTypeToString(eDataType, instanceValue);
		case BuildPackage.ISTATUS:
			return convertIStatusToString(eDataType, instanceValue);
		case BuildPackage.IOPERATION:
			return convertIOperationToString(eDataType, instanceValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IBuildModel createBuildModel() {
		BuildModel buildModel = new BuildModel();
		return buildModel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IBuildPlan createBuildPlan() {
		BuildPlan buildPlan = new BuildPlan();
		return buildPlan;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IBuildServer createBuildServer() {
		BuildServer buildServer = new BuildServer();
		return buildServer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IArtifact createArtifact() {
		Artifact artifact = new Artifact();
		return artifact;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IBuild createBuild() {
		Build build = new Build();
		return build;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IChangeSet createChangeSet() {
		ChangeSet changeSet = new ChangeSet();
		return changeSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IChangeArtifact createChangeArtifact() {
		ChangeArtifact changeArtifact = new ChangeArtifact();
		return changeArtifact;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IChange createChange() {
		Change change = new Change();
		return change;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IUser createUser() {
		User user = new User();
		return user;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IChoiceParameterDefinition createChoiceParameterDefinition() {
		ChoiceParameterDefinition choiceParameterDefinition = new ChoiceParameterDefinition();
		return choiceParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IBooleanParameterDefinition createBooleanParameterDefinition() {
		BooleanParameterDefinition booleanParameterDefinition = new BooleanParameterDefinition();
		return booleanParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IFileParameterDefinition createFileParameterDefinition() {
		FileParameterDefinition fileParameterDefinition = new FileParameterDefinition();
		return fileParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IPlanParameterDefinition createPlanParameterDefinition() {
		PlanParameterDefinition planParameterDefinition = new PlanParameterDefinition();
		return planParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IPasswordParameterDefinition createPasswordParameterDefinition() {
		PasswordParameterDefinition passwordParameterDefinition = new PasswordParameterDefinition();
		return passwordParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IBuildParameterDefinition createBuildParameterDefinition() {
		BuildParameterDefinition buildParameterDefinition = new BuildParameterDefinition();
		return buildParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IStringParameterDefinition createStringParameterDefinition() {
		StringParameterDefinition stringParameterDefinition = new StringParameterDefinition();
		return stringParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ITestResult createTestResult() {
		TestResult testResult = new TestResult();
		return testResult;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ITestElement createTestElement() {
		TestElement testElement = new TestElement();
		return testElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ITestSuite createTestSuite() {
		TestSuite testSuite = new TestSuite();
		return testSuite;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ITestCase createTestCase() {
		TestCase testCase = new TestCase();
		return testCase;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TestCaseResult createTestCaseResultFromString(EDataType eDataType, String initialValue) {
		TestCaseResult result = TestCaseResult.get(initialValue);
		if (result == null)
			throw new IllegalArgumentException(
					"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTestCaseResultToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, String> createStringToStringMap() {
		StringToStringMap stringToStringMap = new StringToStringMap();
		return stringToStringMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BuildState createBuildStateFromString(EDataType eDataType, String initialValue) {
		return (BuildState) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertBuildStateToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BuildStatus createBuildStatusFromString(EDataType eDataType, String initialValue) {
		return (BuildStatus) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertBuildStatusToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EditType createEditTypeFromString(EDataType eDataType, String initialValue) {
		return (EditType) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertEditTypeToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IStatus createIStatusFromString(EDataType eDataType, String initialValue) {
		return (IStatus) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertIStatusToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IOperation createIOperationFromString(EDataType eDataType, String initialValue) {
		return (IOperation) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertIOperationToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BuildPackage getBuildPackage() {
		return (BuildPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static BuildPackage getPackage() {
		return BuildPackage.eINSTANCE;
	}

} //BuildFactory
