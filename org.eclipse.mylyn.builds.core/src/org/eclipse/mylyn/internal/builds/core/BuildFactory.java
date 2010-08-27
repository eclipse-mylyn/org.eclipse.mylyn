/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildFactory.java,v 1.9 2010/08/27 06:49:23 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.EditType;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage
 * @generated
 */
public class BuildFactory extends EFactoryImpl {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static final BuildFactory eINSTANCE = init();

	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static BuildFactory init() {
		try {
			BuildFactory theBuildFactory = (BuildFactory) EPackage.Registry.INSTANCE
					.getEFactory("http://eclipse.org/mylyn/models/build");
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
	 * 
	 * @generated
	 */
	public BuildFactory() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case BuildPackage.ARTIFACT:
			return createArtifact();
		case BuildPackage.BUILD:
			return createBuild();
		case BuildPackage.BUILD_PLAN:
			return createBuildPlan();
		case BuildPackage.BUILD_SERVER:
			return createBuildServer();
		case BuildPackage.BUILD_MODEL:
			return createBuildModel();
		case BuildPackage.CHANGE:
			return createChange();
		case BuildPackage.CHANGE_SET:
			return createChangeSet();
		case BuildPackage.FILE:
			return createFile();
		case BuildPackage.STRING_TO_STRING_MAP:
			return (EObject) createStringToStringMap();
		case BuildPackage.USER:
			return createUser();
		case BuildPackage.CHOICE_PARAMETER_DEFINITION:
			return createChoiceParameterDefinition();
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION:
			return createBooleanParameterDefinition();
		case BuildPackage.FILE_PARAMETER_DEFINITION:
			return createFileParameterDefinition();
		case BuildPackage.PLAN_PARAMETER_DEFINITION:
			return createPlanParameterDefinition();
		case BuildPackage.PASSWORD_PARAMETER_DEFINITION:
			return createPasswordParameterDefinition();
		case BuildPackage.BUILD_PARAMETER_DEFINITION:
			return createBuildParameterDefinition();
		case BuildPackage.STRING_PARAMETER_DEFINITION:
			return createStringParameterDefinition();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
		case BuildPackage.BUILD_STATE:
			return createBuildStateFromString(eDataType, initialValue);
		case BuildPackage.BUILD_STATUS:
			return createBuildStatusFromString(eDataType, initialValue);
		case BuildPackage.EDIT_TYPE:
			return createEditTypeFromString(eDataType, initialValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
		case BuildPackage.BUILD_STATE:
			return convertBuildStateToString(eDataType, instanceValue);
		case BuildPackage.BUILD_STATUS:
			return convertBuildStatusToString(eDataType, instanceValue);
		case BuildPackage.EDIT_TYPE:
			return convertEditTypeToString(eDataType, instanceValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public BuildModel createBuildModel() {
		BuildModel buildModel = new BuildModel();
		return buildModel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public BuildPlan createBuildPlan() {
		BuildPlan buildPlan = new BuildPlan();
		return buildPlan;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public BuildServer createBuildServer() {
		BuildServer buildServer = new BuildServer();
		return buildServer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Artifact createArtifact() {
		Artifact artifact = new Artifact();
		return artifact;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Build createBuild() {
		Build build = new Build();
		return build;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ChangeSet createChangeSet() {
		ChangeSet changeSet = new ChangeSet();
		return changeSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Change createChange() {
		Change change = new Change();
		return change;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public File createFile() {
		File file = new File();
		return file;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public User createUser() {
		User user = new User();
		return user;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ChoiceParameterDefinition createChoiceParameterDefinition() {
		ChoiceParameterDefinition choiceParameterDefinition = new ChoiceParameterDefinition();
		return choiceParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public BooleanParameterDefinition createBooleanParameterDefinition() {
		BooleanParameterDefinition booleanParameterDefinition = new BooleanParameterDefinition();
		return booleanParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public FileParameterDefinition createFileParameterDefinition() {
		FileParameterDefinition fileParameterDefinition = new FileParameterDefinition();
		return fileParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public PlanParameterDefinition createPlanParameterDefinition() {
		PlanParameterDefinition planParameterDefinition = new PlanParameterDefinition();
		return planParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public PasswordParameterDefinition createPasswordParameterDefinition() {
		PasswordParameterDefinition passwordParameterDefinition = new PasswordParameterDefinition();
		return passwordParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public BuildParameterDefinition createBuildParameterDefinition() {
		BuildParameterDefinition buildParameterDefinition = new BuildParameterDefinition();
		return buildParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public StringParameterDefinition createStringParameterDefinition() {
		StringParameterDefinition stringParameterDefinition = new StringParameterDefinition();
		return stringParameterDefinition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Map.Entry<String, String> createStringToStringMap() {
		StringToStringMap stringToStringMap = new StringToStringMap();
		return stringToStringMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public BuildState createBuildStateFromString(EDataType eDataType, String initialValue) {
		return (BuildState) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String convertBuildStateToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public BuildStatus createBuildStatusFromString(EDataType eDataType, String initialValue) {
		return (BuildStatus) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String convertBuildStatusToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EditType createEditTypeFromString(EDataType eDataType, String initialValue) {
		return (EditType) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String convertEditTypeToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public BuildPackage getBuildPackage() {
		return (BuildPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static BuildPackage getPackage() {
		return BuildPackage.eINSTANCE;
	}

} //BuildFactory
