/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildFactory.java,v 1.5 2010/09/26 03:34:34 spingel Exp $
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
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.EditType;
import org.eclipse.mylyn.builds.core.IArtifact;
import org.eclipse.mylyn.builds.core.IBooleanParameterDefinition;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildCause;
import org.eclipse.mylyn.builds.core.IBuildFactory;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildParameterDefinition;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildReference;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.IChange;
import org.eclipse.mylyn.builds.core.IChangeArtifact;
import org.eclipse.mylyn.builds.core.IChangeSet;
import org.eclipse.mylyn.builds.core.IChoiceParameterDefinition;
import org.eclipse.mylyn.builds.core.IFileParameterDefinition;
import org.eclipse.mylyn.builds.core.IHealthReport;
import org.eclipse.mylyn.builds.core.IOperation;
import org.eclipse.mylyn.builds.core.IPasswordParameterDefinition;
import org.eclipse.mylyn.builds.core.IPlanParameterDefinition;
import org.eclipse.mylyn.builds.core.IStringParameterDefinition;
import org.eclipse.mylyn.builds.core.ITestCase;
import org.eclipse.mylyn.builds.core.ITestElement;
import org.eclipse.mylyn.builds.core.ITestResult;
import org.eclipse.mylyn.builds.core.ITestSuite;
import org.eclipse.mylyn.builds.core.IUser;
import org.eclipse.mylyn.builds.core.TestCaseResult;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model. <!--
 * end-user-doc -->
 *
 * @generated
 */
public class BuildFactory extends EFactoryImpl implements IBuildFactory {
	/**
	 * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public static final BuildFactory eINSTANCE = init();

	/**
	 * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
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
	 * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public BuildFactory() {
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		return switch (eClass.getClassifierID()) {
			case BuildPackage.STRING_TO_STRING_MAP -> (EObject) createStringToStringMap();
			case BuildPackage.ARTIFACT -> (EObject) createArtifact();
			case BuildPackage.BUILD -> (EObject) createBuild();
			case BuildPackage.BUILD_CAUSE -> (EObject) createBuildCause();
			case BuildPackage.BUILD_REFERENCE -> (EObject) createBuildReference();
			case BuildPackage.BUILD_PLAN -> (EObject) createBuildPlan();
			case BuildPackage.HEALTH_REPORT -> (EObject) createHealthReport();
			case BuildPackage.BUILD_MODEL -> (EObject) createBuildModel();
			case BuildPackage.BUILD_SERVER -> (EObject) createBuildServer();
			case BuildPackage.CHANGE -> (EObject) createChange();
			case BuildPackage.CHANGE_SET -> (EObject) createChangeSet();
			case BuildPackage.CHANGE_ARTIFACT -> (EObject) createChangeArtifact();
			case BuildPackage.USER -> (EObject) createUser();
			case BuildPackage.CHOICE_PARAMETER_DEFINITION -> (EObject) createChoiceParameterDefinition();
			case BuildPackage.BOOLEAN_PARAMETER_DEFINITION -> (EObject) createBooleanParameterDefinition();
			case BuildPackage.FILE_PARAMETER_DEFINITION -> (EObject) createFileParameterDefinition();
			case BuildPackage.PLAN_PARAMETER_DEFINITION -> (EObject) createPlanParameterDefinition();
			case BuildPackage.PASSWORD_PARAMETER_DEFINITION -> (EObject) createPasswordParameterDefinition();
			case BuildPackage.BUILD_PARAMETER_DEFINITION -> (EObject) createBuildParameterDefinition();
			case BuildPackage.STRING_PARAMETER_DEFINITION -> (EObject) createStringParameterDefinition();
			case BuildPackage.TEST_RESULT -> (EObject) createTestResult();
			case BuildPackage.TEST_ELEMENT -> (EObject) createTestElement();
			case BuildPackage.TEST_SUITE -> (EObject) createTestSuite();
			case BuildPackage.TEST_CASE -> (EObject) createTestCase();
			default -> throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		};
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		return switch (eDataType.getClassifierID()) {
			case BuildPackage.TEST_CASE_RESULT -> createTestCaseResultFromString(eDataType, initialValue);
			case BuildPackage.BUILD_STATE -> createBuildStateFromString(eDataType, initialValue);
			case BuildPackage.BUILD_STATUS -> createBuildStatusFromString(eDataType, initialValue);
			case BuildPackage.EDIT_TYPE -> createEditTypeFromString(eDataType, initialValue);
			case BuildPackage.ISTATUS -> createIStatusFromString(eDataType, initialValue);
			case BuildPackage.IOPERATION -> createIOperationFromString(eDataType, initialValue);
			default -> throw new IllegalArgumentException(
					"The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		};
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		return switch (eDataType.getClassifierID()) {
			case BuildPackage.TEST_CASE_RESULT -> convertTestCaseResultToString(eDataType, instanceValue);
			case BuildPackage.BUILD_STATE -> convertBuildStateToString(eDataType, instanceValue);
			case BuildPackage.BUILD_STATUS -> convertBuildStatusToString(eDataType, instanceValue);
			case BuildPackage.EDIT_TYPE -> convertEditTypeToString(eDataType, instanceValue);
			case BuildPackage.ISTATUS -> convertIStatusToString(eDataType, instanceValue);
			case BuildPackage.IOPERATION -> convertIOperationToString(eDataType, instanceValue);
			default -> throw new IllegalArgumentException(
					"The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		};
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IBuildModel createBuildModel() {
		BuildModel buildModel = new BuildModel();
		return buildModel;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IBuildPlan createBuildPlan() {
		BuildPlan buildPlan = new BuildPlan();
		return buildPlan;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IHealthReport createHealthReport() {
		HealthReport healthReport = new HealthReport();
		return healthReport;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IBuildServer createBuildServer() {
		BuildServer buildServer = new BuildServer();
		return buildServer;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IArtifact createArtifact() {
		Artifact artifact = new Artifact();
		return artifact;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IBuild createBuild() {
		Build build = new Build();
		return build;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IBuildCause createBuildCause() {
		BuildCause buildCause = new BuildCause();
		return buildCause;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IBuildReference createBuildReference() {
		BuildReference buildReference = new BuildReference();
		return buildReference;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IChangeSet createChangeSet() {
		ChangeSet changeSet = new ChangeSet();
		return changeSet;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IChangeArtifact createChangeArtifact() {
		ChangeArtifact changeArtifact = new ChangeArtifact();
		return changeArtifact;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IChange createChange() {
		Change change = new Change();
		return change;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IUser createUser() {
		User user = new User();
		return user;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IChoiceParameterDefinition createChoiceParameterDefinition() {
		ChoiceParameterDefinition choiceParameterDefinition = new ChoiceParameterDefinition();
		return choiceParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IBooleanParameterDefinition createBooleanParameterDefinition() {
		BooleanParameterDefinition booleanParameterDefinition = new BooleanParameterDefinition();
		return booleanParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IFileParameterDefinition createFileParameterDefinition() {
		FileParameterDefinition fileParameterDefinition = new FileParameterDefinition();
		return fileParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IPlanParameterDefinition createPlanParameterDefinition() {
		PlanParameterDefinition planParameterDefinition = new PlanParameterDefinition();
		return planParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IPasswordParameterDefinition createPasswordParameterDefinition() {
		PasswordParameterDefinition passwordParameterDefinition = new PasswordParameterDefinition();
		return passwordParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IBuildParameterDefinition createBuildParameterDefinition() {
		BuildParameterDefinition buildParameterDefinition = new BuildParameterDefinition();
		return buildParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IStringParameterDefinition createStringParameterDefinition() {
		StringParameterDefinition stringParameterDefinition = new StringParameterDefinition();
		return stringParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public ITestResult createTestResult() {
		TestResult testResult = new TestResult();
		return testResult;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public ITestElement createTestElement() {
		TestElement testElement = new TestElement();
		return testElement;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public ITestSuite createTestSuite() {
		TestSuite testSuite = new TestSuite();
		return testSuite;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public ITestCase createTestCase() {
		TestCase testCase = new TestCase();
		return testCase;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public TestCaseResult createTestCaseResultFromString(EDataType eDataType, String initialValue) {
		TestCaseResult result = TestCaseResult.get(initialValue);
		if (result == null) {
			throw new IllegalArgumentException(
					"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return result;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertTestCaseResultToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Map.Entry<String, String> createStringToStringMap() {
		StringToStringMap stringToStringMap = new StringToStringMap();
		return stringToStringMap;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public BuildState createBuildStateFromString(EDataType eDataType, String initialValue) {
		return (BuildState) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertBuildStateToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public BuildStatus createBuildStatusFromString(EDataType eDataType, String initialValue) {
		return (BuildStatus) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertBuildStatusToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EditType createEditTypeFromString(EDataType eDataType, String initialValue) {
		return (EditType) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertEditTypeToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public IStatus createIStatusFromString(EDataType eDataType, String initialValue) {
		return (IStatus) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertIStatusToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public IOperation createIOperationFromString(EDataType eDataType, String initialValue) {
		return (IOperation) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertIOperationToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public BuildPackage getBuildPackage() {
		return (BuildPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static BuildPackage getPackage() {
		return BuildPackage.eINSTANCE;
	}

} //BuildFactory
