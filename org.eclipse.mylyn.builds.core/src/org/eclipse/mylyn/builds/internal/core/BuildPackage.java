/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildPackage.java,v 1.6 2010/09/02 06:23:14 spingel Exp $
 */
package org.eclipse.mylyn.builds.internal.core;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.EditType;
import org.eclipse.mylyn.builds.core.IArtifact;
import org.eclipse.mylyn.builds.core.IBooleanParameterDefinition;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildFactory;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildParameterDefinition;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.IChange;
import org.eclipse.mylyn.builds.core.IChangeArtifact;
import org.eclipse.mylyn.builds.core.IChangeSet;
import org.eclipse.mylyn.builds.core.IChoiceParameterDefinition;
import org.eclipse.mylyn.builds.core.IFileParameterDefinition;
import org.eclipse.mylyn.builds.core.IHealthReport;
import org.eclipse.mylyn.builds.core.IOperation;
import org.eclipse.mylyn.builds.core.IParameterDefinition;
import org.eclipse.mylyn.builds.core.IPasswordParameterDefinition;
import org.eclipse.mylyn.builds.core.IPlanParameterDefinition;
import org.eclipse.mylyn.builds.core.IStringParameterDefinition;
import org.eclipse.mylyn.builds.core.ITestCase;
import org.eclipse.mylyn.builds.core.ITestElement;
import org.eclipse.mylyn.builds.core.ITestResult;
import org.eclipse.mylyn.builds.core.ITestSuite;
import org.eclipse.mylyn.builds.core.IUser;
import org.eclipse.mylyn.builds.core.TestCaseResult;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.mylyn.builds.core.IBuildFactory
 * @generated
 */
public class BuildPackage extends EPackageImpl {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static final String eNAME = "builds"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static final String eNS_URI = "http://eclipse.org/mylyn/models/build"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static final String eNS_PREFIX = "builds"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static final BuildPackage eINSTANCE = org.eclipse.mylyn.builds.internal.core.BuildPackage.init();

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.BuildElement <em>Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.BuildElement
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuildElement()
	 * @generated
	 */
	public static final int BUILD_ELEMENT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.Artifact <em>Artifact</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.Artifact
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getArtifact()
	 * @generated
	 */
	public static final int ARTIFACT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.Build <em>Build</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.Build
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuild()
	 * @generated
	 */
	public static final int BUILD = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.BuildPlan <em>Plan</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPlan
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuildPlan()
	 * @generated
	 */
	public static final int BUILD_PLAN = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.BuildServer <em>Server</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.BuildServer
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuildServer()
	 * @generated
	 */
	public static final int BUILD_SERVER = 7;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.BuildModel <em>Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.BuildModel
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuildModel()
	 * @generated
	 */
	public static final int BUILD_MODEL = 6;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.Change <em>Change</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.Change
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getChange()
	 * @generated
	 */
	public static final int CHANGE = 8;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.ChangeSet <em>Change Set</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.ChangeSet
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getChangeSet()
	 * @generated
	 */
	public static final int CHANGE_SET = 9;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.ChangeArtifact
	 * <em>Change Artifact</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.ChangeArtifact
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getChangeArtifact()
	 * @generated
	 */
	public static final int CHANGE_ARTIFACT = 10;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.StringToStringMap
	 * <em>String To String Map</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.StringToStringMap
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getStringToStringMap()
	 * @generated
	 */
	public static final int STRING_TO_STRING_MAP = 0;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int STRING_TO_STRING_MAP__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int STRING_TO_STRING_MAP__VALUE = 1;

	/**
	 * The number of structural features of the '<em>String To String Map</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int STRING_TO_STRING_MAP_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '<em>IOperation</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.IOperation
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getIOperation()
	 * @generated
	 */
	public static final int IOPERATION = 29;

	/**
	 * The meta object id for the '<em>IStatus</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.core.runtime.IStatus
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getIStatus()
	 * @generated
	 */
	public static final int ISTATUS = 28;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_ELEMENT__URL = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_ELEMENT__NAME = 1;

	/**
	 * The feature id for the '<em><b>Operations</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_ELEMENT__OPERATIONS = 2;

	/**
	 * The feature id for the '<em><b>Element Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_ELEMENT__ELEMENT_STATUS = 3;

	/**
	 * The feature id for the '<em><b>Refresh Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_ELEMENT__REFRESH_DATE = 4;

	/**
	 * The number of structural features of the '<em>Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_ELEMENT_FEATURE_COUNT = 5;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ARTIFACT__URL = BUILD_ELEMENT__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ARTIFACT__NAME = BUILD_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Operations</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ARTIFACT__OPERATIONS = BUILD_ELEMENT__OPERATIONS;

	/**
	 * The feature id for the '<em><b>Element Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ARTIFACT__ELEMENT_STATUS = BUILD_ELEMENT__ELEMENT_STATUS;

	/**
	 * The feature id for the '<em><b>Refresh Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ARTIFACT__REFRESH_DATE = BUILD_ELEMENT__REFRESH_DATE;

	/**
	 * The feature id for the '<em><b>Relative Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ARTIFACT__RELATIVE_PATH = BUILD_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Artifact</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ARTIFACT_FEATURE_COUNT = BUILD_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__URL = BUILD_ELEMENT__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__NAME = BUILD_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Operations</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__OPERATIONS = BUILD_ELEMENT__OPERATIONS;

	/**
	 * The feature id for the '<em><b>Element Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__ELEMENT_STATUS = BUILD_ELEMENT__ELEMENT_STATUS;

	/**
	 * The feature id for the '<em><b>Refresh Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__REFRESH_DATE = BUILD_ELEMENT__REFRESH_DATE;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__ID = BUILD_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Build Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__BUILD_NUMBER = BUILD_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__TIMESTAMP = BUILD_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Duration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__DURATION = BUILD_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Display Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__DISPLAY_NAME = BUILD_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__STATE = BUILD_ELEMENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__STATUS = BUILD_ELEMENT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Artifacts</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__ARTIFACTS = BUILD_ELEMENT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Change Set</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__CHANGE_SET = BUILD_ELEMENT_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Plan</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__PLAN = BUILD_ELEMENT_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__LABEL = BUILD_ELEMENT_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Server</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__SERVER = BUILD_ELEMENT_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Test Result</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__TEST_RESULT = BUILD_ELEMENT_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Culprits</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__CULPRITS = BUILD_ELEMENT_FEATURE_COUNT + 13;

	/**
	 * The number of structural features of the '<em>Build</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_FEATURE_COUNT = BUILD_ELEMENT_FEATURE_COUNT + 14;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__URL = BUILD_ELEMENT__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__NAME = BUILD_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Operations</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__OPERATIONS = BUILD_ELEMENT__OPERATIONS;

	/**
	 * The feature id for the '<em><b>Element Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__ELEMENT_STATUS = BUILD_ELEMENT__ELEMENT_STATUS;

	/**
	 * The feature id for the '<em><b>Refresh Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__REFRESH_DATE = BUILD_ELEMENT__REFRESH_DATE;

	/**
	 * The feature id for the '<em><b>Server</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__SERVER = BUILD_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Children</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__CHILDREN = BUILD_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__PARENT = BUILD_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Health</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__HEALTH = BUILD_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__ID = BUILD_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Info</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__INFO = BUILD_ELEMENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Selected</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__SELECTED = BUILD_ELEMENT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__SUMMARY = BUILD_ELEMENT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__STATE = BUILD_ELEMENT_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__STATUS = BUILD_ELEMENT_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__DESCRIPTION = BUILD_ELEMENT_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Last Build</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__LAST_BUILD = BUILD_ELEMENT_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Parameter Definitions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__PARAMETER_DEFINITIONS = BUILD_ELEMENT_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Health Reports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__HEALTH_REPORTS = BUILD_ELEMENT_FEATURE_COUNT + 13;

	/**
	 * The number of structural features of the '<em>Plan</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN_FEATURE_COUNT = BUILD_ELEMENT_FEATURE_COUNT + 14;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.HealthReport <em>Health Report</em>}'
	 * class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.HealthReport
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getHealthReport()
	 * @generated
	 */
	public static final int HEALTH_REPORT = 5;

	/**
	 * The feature id for the '<em><b>Health</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int HEALTH_REPORT__HEALTH = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int HEALTH_REPORT__DESCRIPTION = 1;

	/**
	 * The number of structural features of the '<em>Health Report</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int HEALTH_REPORT_FEATURE_COUNT = 2;

	/**
	 * The feature id for the '<em><b>Servers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_MODEL__SERVERS = 0;

	/**
	 * The feature id for the '<em><b>Plans</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_MODEL__PLANS = 1;

	/**
	 * The feature id for the '<em><b>Builds</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_MODEL__BUILDS = 2;

	/**
	 * The number of structural features of the '<em>Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_MODEL_FEATURE_COUNT = 3;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__URL = BUILD_ELEMENT__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__NAME = BUILD_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Operations</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__OPERATIONS = BUILD_ELEMENT__OPERATIONS;

	/**
	 * The feature id for the '<em><b>Element Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__ELEMENT_STATUS = BUILD_ELEMENT__ELEMENT_STATUS;

	/**
	 * The feature id for the '<em><b>Refresh Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__REFRESH_DATE = BUILD_ELEMENT__REFRESH_DATE;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__ATTRIBUTES = BUILD_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__LOCATION = BUILD_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Connector Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__CONNECTOR_KIND = BUILD_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Repository Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__REPOSITORY_URL = BUILD_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Server</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER_FEATURE_COUNT = BUILD_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Artifacts</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__ARTIFACTS = 0;

	/**
	 * The feature id for the '<em><b>Author</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__AUTHOR = 1;

	/**
	 * The feature id for the '<em><b>Message</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__MESSAGE = 2;

	/**
	 * The feature id for the '<em><b>Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__DATE = 3;

	/**
	 * The feature id for the '<em><b>Revision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__REVISION = 4;

	/**
	 * The number of structural features of the '<em>Change</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_FEATURE_COUNT = 5;

	/**
	 * The feature id for the '<em><b>Changes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_SET__CHANGES = 0;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_SET__KIND = 1;

	/**
	 * The number of structural features of the '<em>Change Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_SET_FEATURE_COUNT = 2;

	/**
	 * The feature id for the '<em><b>File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_ARTIFACT__FILE = 0;

	/**
	 * The feature id for the '<em><b>Relative Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_ARTIFACT__RELATIVE_PATH = 1;

	/**
	 * The feature id for the '<em><b>Prev Revision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_ARTIFACT__PREV_REVISION = 2;

	/**
	 * The feature id for the '<em><b>Revision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_ARTIFACT__REVISION = 3;

	/**
	 * The feature id for the '<em><b>Dead</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_ARTIFACT__DEAD = 4;

	/**
	 * The feature id for the '<em><b>Edit Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_ARTIFACT__EDIT_TYPE = 5;

	/**
	 * The number of structural features of the '<em>Change Artifact</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_ARTIFACT_FEATURE_COUNT = 6;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.User <em>User</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.User
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getUser()
	 * @generated
	 */
	public static final int USER = 11;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER__URL = BUILD_ELEMENT__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER__NAME = BUILD_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Operations</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER__OPERATIONS = BUILD_ELEMENT__OPERATIONS;

	/**
	 * The feature id for the '<em><b>Element Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER__ELEMENT_STATUS = BUILD_ELEMENT__ELEMENT_STATUS;

	/**
	 * The feature id for the '<em><b>Refresh Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER__REFRESH_DATE = BUILD_ELEMENT__REFRESH_DATE;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER__ID = BUILD_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Email</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER__EMAIL = BUILD_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>User</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER_FEATURE_COUNT = BUILD_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.ParameterDefinition
	 * <em>Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.ParameterDefinition
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getParameterDefinition()
	 * @generated
	 */
	public static final int PARAMETER_DEFINITION = 12;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int PARAMETER_DEFINITION__NAME = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int PARAMETER_DEFINITION__DESCRIPTION = 1;

	/**
	 * The feature id for the '<em><b>Containing Build Plan</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN = 2;

	/**
	 * The number of structural features of the '<em>Parameter Definition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int PARAMETER_DEFINITION_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.ChoiceParameterDefinition
	 * <em>Choice Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.ChoiceParameterDefinition
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getChoiceParameterDefinition()
	 * @generated
	 */
	public static final int CHOICE_PARAMETER_DEFINITION = 13;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHOICE_PARAMETER_DEFINITION__NAME = PARAMETER_DEFINITION__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHOICE_PARAMETER_DEFINITION__DESCRIPTION = PARAMETER_DEFINITION__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Containing Build Plan</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHOICE_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN = PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN;

	/**
	 * The feature id for the '<em><b>Options</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHOICE_PARAMETER_DEFINITION__OPTIONS = PARAMETER_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Choice Parameter Definition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHOICE_PARAMETER_DEFINITION_FEATURE_COUNT = PARAMETER_DEFINITION_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.BooleanParameterDefinition
	 * <em>Boolean Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.BooleanParameterDefinition
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBooleanParameterDefinition()
	 * @generated
	 */
	public static final int BOOLEAN_PARAMETER_DEFINITION = 14;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BOOLEAN_PARAMETER_DEFINITION__NAME = PARAMETER_DEFINITION__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BOOLEAN_PARAMETER_DEFINITION__DESCRIPTION = PARAMETER_DEFINITION__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Containing Build Plan</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BOOLEAN_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN = PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN;

	/**
	 * The feature id for the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BOOLEAN_PARAMETER_DEFINITION__DEFAULT_VALUE = PARAMETER_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Boolean Parameter Definition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BOOLEAN_PARAMETER_DEFINITION_FEATURE_COUNT = PARAMETER_DEFINITION_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.FileParameterDefinition
	 * <em>File Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.FileParameterDefinition
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getFileParameterDefinition()
	 * @generated
	 */
	public static final int FILE_PARAMETER_DEFINITION = 15;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_PARAMETER_DEFINITION__NAME = PARAMETER_DEFINITION__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_PARAMETER_DEFINITION__DESCRIPTION = PARAMETER_DEFINITION__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Containing Build Plan</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN = PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN;

	/**
	 * The number of structural features of the '<em>File Parameter Definition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_PARAMETER_DEFINITION_FEATURE_COUNT = PARAMETER_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.PlanParameterDefinition
	 * <em>Plan Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.PlanParameterDefinition
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getPlanParameterDefinition()
	 * @generated
	 */
	public static final int PLAN_PARAMETER_DEFINITION = 16;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int PLAN_PARAMETER_DEFINITION__NAME = PARAMETER_DEFINITION__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int PLAN_PARAMETER_DEFINITION__DESCRIPTION = PARAMETER_DEFINITION__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Containing Build Plan</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int PLAN_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN = PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN;

	/**
	 * The number of structural features of the '<em>Plan Parameter Definition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int PLAN_PARAMETER_DEFINITION_FEATURE_COUNT = PARAMETER_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.PasswordParameterDefinition
	 * <em>Password Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.PasswordParameterDefinition
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getPasswordParameterDefinition()
	 * @generated
	 */
	public static final int PASSWORD_PARAMETER_DEFINITION = 17;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int PASSWORD_PARAMETER_DEFINITION__NAME = PARAMETER_DEFINITION__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int PASSWORD_PARAMETER_DEFINITION__DESCRIPTION = PARAMETER_DEFINITION__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Containing Build Plan</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int PASSWORD_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN = PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN;

	/**
	 * The feature id for the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int PASSWORD_PARAMETER_DEFINITION__DEFAULT_VALUE = PARAMETER_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Password Parameter Definition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int PASSWORD_PARAMETER_DEFINITION_FEATURE_COUNT = PARAMETER_DEFINITION_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.BuildParameterDefinition
	 * <em>Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.BuildParameterDefinition
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuildParameterDefinition()
	 * @generated
	 */
	public static final int BUILD_PARAMETER_DEFINITION = 18;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PARAMETER_DEFINITION__NAME = PARAMETER_DEFINITION__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PARAMETER_DEFINITION__DESCRIPTION = PARAMETER_DEFINITION__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Containing Build Plan</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN = PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN;

	/**
	 * The feature id for the '<em><b>Build Plan Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PARAMETER_DEFINITION__BUILD_PLAN_ID = PARAMETER_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Build Plan</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PARAMETER_DEFINITION__BUILD_PLAN = PARAMETER_DEFINITION_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Parameter Definition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PARAMETER_DEFINITION_FEATURE_COUNT = PARAMETER_DEFINITION_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.StringParameterDefinition
	 * <em>String Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.StringParameterDefinition
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getStringParameterDefinition()
	 * @generated
	 */
	public static final int STRING_PARAMETER_DEFINITION = 19;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int STRING_PARAMETER_DEFINITION__NAME = PARAMETER_DEFINITION__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int STRING_PARAMETER_DEFINITION__DESCRIPTION = PARAMETER_DEFINITION__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Containing Build Plan</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int STRING_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN = PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN;

	/**
	 * The feature id for the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int STRING_PARAMETER_DEFINITION__DEFAULT_VALUE = PARAMETER_DEFINITION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>String Parameter Definition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int STRING_PARAMETER_DEFINITION_FEATURE_COUNT = PARAMETER_DEFINITION_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.TestResult <em>Test Result</em>}'
	 * class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.TestResult
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getTestResult()
	 * @generated
	 */
	public static final int TEST_RESULT = 20;

	/**
	 * The feature id for the '<em><b>Build</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_RESULT__BUILD = 0;

	/**
	 * The feature id for the '<em><b>Duration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_RESULT__DURATION = 1;

	/**
	 * The feature id for the '<em><b>Fail Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_RESULT__FAIL_COUNT = 2;

	/**
	 * The feature id for the '<em><b>Pass Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_RESULT__PASS_COUNT = 3;

	/**
	 * The feature id for the '<em><b>Suites</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_RESULT__SUITES = 4;

	/**
	 * The number of structural features of the '<em>Test Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_RESULT_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.TestElement <em>Test Element</em>}'
	 * class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.TestElement
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getTestElement()
	 * @generated
	 */
	public static final int TEST_ELEMENT = 21;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_ELEMENT__LABEL = 0;

	/**
	 * The feature id for the '<em><b>Duration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_ELEMENT__DURATION = 1;

	/**
	 * The feature id for the '<em><b>Error Output</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_ELEMENT__ERROR_OUTPUT = 2;

	/**
	 * The feature id for the '<em><b>Output</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_ELEMENT__OUTPUT = 3;

	/**
	 * The number of structural features of the '<em>Test Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_ELEMENT_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.TestSuite <em>Test Suite</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.TestSuite
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getTestSuite()
	 * @generated
	 */
	public static final int TEST_SUITE = 22;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_SUITE__LABEL = TEST_ELEMENT__LABEL;

	/**
	 * The feature id for the '<em><b>Duration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_SUITE__DURATION = TEST_ELEMENT__DURATION;

	/**
	 * The feature id for the '<em><b>Error Output</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_SUITE__ERROR_OUTPUT = TEST_ELEMENT__ERROR_OUTPUT;

	/**
	 * The feature id for the '<em><b>Output</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_SUITE__OUTPUT = TEST_ELEMENT__OUTPUT;

	/**
	 * The feature id for the '<em><b>Cases</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_SUITE__CASES = TEST_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Result</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_SUITE__RESULT = TEST_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Test Suite</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_SUITE_FEATURE_COUNT = TEST_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.internal.core.TestCase <em>Test Case</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.internal.core.TestCase
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getTestCase()
	 * @generated
	 */
	public static final int TEST_CASE = 23;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_CASE__LABEL = TEST_ELEMENT__LABEL;

	/**
	 * The feature id for the '<em><b>Duration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_CASE__DURATION = TEST_ELEMENT__DURATION;

	/**
	 * The feature id for the '<em><b>Error Output</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_CASE__ERROR_OUTPUT = TEST_ELEMENT__ERROR_OUTPUT;

	/**
	 * The feature id for the '<em><b>Output</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_CASE__OUTPUT = TEST_ELEMENT__OUTPUT;

	/**
	 * The feature id for the '<em><b>Class Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_CASE__CLASS_NAME = TEST_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Skipped</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_CASE__SKIPPED = TEST_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Suite</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_CASE__SUITE = TEST_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_CASE__STATUS = TEST_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Test Case</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TEST_CASE_FEATURE_COUNT = TEST_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.TestCaseResult <em>Test Case Result</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.TestCaseResult
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getTestCaseResult()
	 * @generated
	 */
	public static final int TEST_CASE_RESULT = 24;

	/**
	 * The meta object id for the '<em>Repository Location</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.commons.repositories.RepositoryLocation
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getRepositoryLocation()
	 * @generated
	 */
	public static final int REPOSITORY_LOCATION = 30;

	/**
	 * The meta object id for the '<em>State</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.BuildState
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuildState()
	 * @generated
	 */
	public static final int BUILD_STATE = 25;

	/**
	 * The meta object id for the '<em>Status</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.BuildStatus
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuildStatus()
	 * @generated
	 */
	public static final int BUILD_STATUS = 26;

	/**
	 * The meta object id for the '<em>Edit Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.EditType
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getEditType()
	 * @generated
	 */
	public static final int EDIT_TYPE = 27;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass buildElementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass buildModelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass buildPlanEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass healthReportEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass buildServerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass artifactEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass buildEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass changeSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass changeArtifactEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass changeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass userEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass parameterDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass choiceParameterDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass booleanParameterDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass fileParameterDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass planParameterDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass passwordParameterDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass buildParameterDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass stringParameterDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass testResultEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass testElementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass testSuiteEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass testCaseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EEnum testCaseResultEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass stringToStringMapEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EDataType repositoryLocationEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EDataType buildStateEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EDataType buildStatusEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EDataType editTypeEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EDataType iStatusEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EDataType iOperationEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
	 * EPackage.Registry} by the package
	 * package URI value.
	 * <p>
	 * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
	 * performs initialization of the package, or returns the registered package, if one already exists. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private BuildPackage() {
		super(eNS_URI, ((EFactory) IBuildFactory.INSTANCE));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * <p>
	 * This method is used to initialize {@link BuildPackage#eINSTANCE} when that field is accessed. Clients should not
	 * invoke it directly. Instead, they should simply access that field to obtain the package. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static BuildPackage init() {
		if (isInited)
			return (BuildPackage) EPackage.Registry.INSTANCE.getEPackage(BuildPackage.eNS_URI);

		// Obtain or create and register package
		BuildPackage theBuildPackage = (BuildPackage) (EPackage.Registry.INSTANCE.get(eNS_URI) instanceof BuildPackage ? EPackage.Registry.INSTANCE
				.get(eNS_URI)
				: new BuildPackage());

		isInited = true;

		// Create package meta-data objects
		theBuildPackage.createPackageContents();

		// Initialize created meta-data
		theBuildPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theBuildPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(BuildPackage.eNS_URI, theBuildPackage);
		return theBuildPackage;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildElement <em>Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Element</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildElement
	 * @generated
	 */
	public EClass getBuildElement() {
		return buildElementEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildElement#getUrl
	 * <em>Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Url</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildElement#getUrl()
	 * @see #getBuildElement()
	 * @generated
	 */
	public EAttribute getBuildElement_Url() {
		return (EAttribute) buildElementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildElement#getName
	 * <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildElement#getName()
	 * @see #getBuildElement()
	 * @generated
	 */
	public EAttribute getBuildElement_Name() {
		return (EAttribute) buildElementEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.mylyn.builds.core.IBuildElement#getOperations
	 * <em>Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute list '<em>Operations</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildElement#getOperations()
	 * @see #getBuildElement()
	 * @generated
	 */
	public EAttribute getBuildElement_Operations() {
		return (EAttribute) buildElementEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildElement#getElementStatus
	 * <em>Element Status</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Element Status</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildElement#getElementStatus()
	 * @see #getBuildElement()
	 * @generated
	 */
	public EAttribute getBuildElement_ElementStatus() {
		return (EAttribute) buildElementEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildElement#getRefreshDate
	 * <em>Refresh Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Refresh Date</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildElement#getRefreshDate()
	 * @see #getBuildElement()
	 * @generated
	 */
	public EAttribute getBuildElement_RefreshDate() {
		return (EAttribute) buildElementEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildModel <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Model</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildModel
	 * @generated
	 */
	public EClass getBuildModel() {
		return buildModelEClass;
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.builds.core.IBuildModel#getServers <em>Servers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Servers</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildModel#getServers()
	 * @see #getBuildModel()
	 * @generated
	 */
	public EReference getBuildModel_Servers() {
		return (EReference) buildModelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.builds.core.IBuildModel#getPlans <em>Plans</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Plans</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildModel#getPlans()
	 * @see #getBuildModel()
	 * @generated
	 */
	public EReference getBuildModel_Plans() {
		return (EReference) buildModelEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.builds.core.IBuildModel#getBuilds <em>Builds</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Builds</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildModel#getBuilds()
	 * @see #getBuildModel()
	 * @generated
	 */
	public EReference getBuildModel_Builds() {
		return (EReference) buildModelEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildPlan <em>Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Plan</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan
	 * @generated
	 */
	public EClass getBuildPlan() {
		return buildPlanEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getServer
	 * <em>Server</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Server</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getServer()
	 * @see #getBuildPlan()
	 * @generated
	 */
	public EReference getBuildPlan_Server() {
		return (EReference) buildPlanEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getChildren
	 * <em>Children</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Children</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getChildren()
	 * @see #getBuildPlan()
	 * @generated
	 */
	public EReference getBuildPlan_Children() {
		return (EReference) buildPlanEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getParent
	 * <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Parent</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getParent()
	 * @see #getBuildPlan()
	 * @generated
	 */
	public EReference getBuildPlan_Parent() {
		return (EReference) buildPlanEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getHealth
	 * <em>Health</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Health</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getHealth()
	 * @see #getBuildPlan()
	 * @generated
	 */
	public EAttribute getBuildPlan_Health() {
		return (EAttribute) buildPlanEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getId()
	 * @see #getBuildPlan()
	 * @generated
	 */
	public EAttribute getBuildPlan_Id() {
		return (EAttribute) buildPlanEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getInfo <em>Info</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Info</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getInfo()
	 * @see #getBuildPlan()
	 * @generated
	 */
	public EAttribute getBuildPlan_Info() {
		return (EAttribute) buildPlanEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#isSelected
	 * <em>Selected</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Selected</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#isSelected()
	 * @see #getBuildPlan()
	 * @generated
	 */
	public EAttribute getBuildPlan_Selected() {
		return (EAttribute) buildPlanEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getSummary
	 * <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getSummary()
	 * @see #getBuildPlan()
	 * @generated
	 */
	public EAttribute getBuildPlan_Summary() {
		return (EAttribute) buildPlanEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getState
	 * <em>State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>State</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getState()
	 * @see #getBuildPlan()
	 * @generated
	 */
	public EAttribute getBuildPlan_State() {
		return (EAttribute) buildPlanEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getStatus
	 * <em>Status</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Status</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getStatus()
	 * @see #getBuildPlan()
	 * @generated
	 */
	public EAttribute getBuildPlan_Status() {
		return (EAttribute) buildPlanEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getDescription
	 * <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getDescription()
	 * @see #getBuildPlan()
	 * @generated
	 */
	public EAttribute getBuildPlan_Description() {
		return (EAttribute) buildPlanEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getLastBuild
	 * <em>Last Build</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Last Build</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getLastBuild()
	 * @see #getBuildPlan()
	 * @generated
	 */
	public EReference getBuildPlan_LastBuild() {
		return (EReference) buildPlanEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.builds.core.IBuildPlan#getParameterDefinitions <em>Parameter Definitions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Parameter Definitions</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getParameterDefinitions()
	 * @see #getBuildPlan()
	 * @generated
	 */
	public EReference getBuildPlan_ParameterDefinitions() {
		return (EReference) buildPlanEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.builds.core.IBuildPlan#getHealthReports <em>Health Reports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Health Reports</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getHealthReports()
	 * @see #getBuildPlan()
	 * @generated
	 */
	public EReference getBuildPlan_HealthReports() {
		return (EReference) buildPlanEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IHealthReport <em>Health Report</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Health Report</em>'.
	 * @see org.eclipse.mylyn.builds.core.IHealthReport
	 * @generated
	 */
	public EClass getHealthReport() {
		return healthReportEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IHealthReport#getHealth
	 * <em>Health</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Health</em>'.
	 * @see org.eclipse.mylyn.builds.core.IHealthReport#getHealth()
	 * @see #getHealthReport()
	 * @generated
	 */
	public EAttribute getHealthReport_Health() {
		return (EAttribute) healthReportEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IHealthReport#getDescription
	 * <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.mylyn.builds.core.IHealthReport#getDescription()
	 * @see #getHealthReport()
	 * @generated
	 */
	public EAttribute getHealthReport_Description() {
		return (EAttribute) healthReportEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildServer <em>Server</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Server</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer
	 * @generated
	 */
	public EClass getBuildServer() {
		return buildServerEClass;
	}

	/**
	 * Returns the meta object for the map '{@link org.eclipse.mylyn.builds.core.IBuildServer#getAttributes
	 * <em>Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the map '<em>Attributes</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer#getAttributes()
	 * @see #getBuildServer()
	 * @generated
	 */
	public EReference getBuildServer_Attributes() {
		return (EReference) buildServerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildServer#getLocation
	 * <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Location</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer#getLocation()
	 * @see #getBuildServer()
	 * @generated
	 */
	public EAttribute getBuildServer_Location() {
		return (EAttribute) buildServerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildServer#getConnectorKind
	 * <em>Connector Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Connector Kind</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer#getConnectorKind()
	 * @see #getBuildServer()
	 * @generated
	 */
	public EAttribute getBuildServer_ConnectorKind() {
		return (EAttribute) buildServerEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildServer#getRepositoryUrl
	 * <em>Repository Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Repository Url</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer#getRepositoryUrl()
	 * @see #getBuildServer()
	 * @generated
	 */
	public EAttribute getBuildServer_RepositoryUrl() {
		return (EAttribute) buildServerEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IArtifact <em>Artifact</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Artifact</em>'.
	 * @see org.eclipse.mylyn.builds.core.IArtifact
	 * @generated
	 */
	public EClass getArtifact() {
		return artifactEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IArtifact#getRelativePath
	 * <em>Relative Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Relative Path</em>'.
	 * @see org.eclipse.mylyn.builds.core.IArtifact#getRelativePath()
	 * @see #getArtifact()
	 * @generated
	 */
	public EAttribute getArtifact_RelativePath() {
		return (EAttribute) artifactEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuild <em>Build</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Build</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild
	 * @generated
	 */
	public EClass getBuild() {
		return buildEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getId()
	 * @see #getBuild()
	 * @generated
	 */
	public EAttribute getBuild_Id() {
		return (EAttribute) buildEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getBuildNumber
	 * <em>Build Number</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Build Number</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getBuildNumber()
	 * @see #getBuild()
	 * @generated
	 */
	public EAttribute getBuild_BuildNumber() {
		return (EAttribute) buildEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getTimestamp
	 * <em>Timestamp</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Timestamp</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getTimestamp()
	 * @see #getBuild()
	 * @generated
	 */
	public EAttribute getBuild_Timestamp() {
		return (EAttribute) buildEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getDuration
	 * <em>Duration</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Duration</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getDuration()
	 * @see #getBuild()
	 * @generated
	 */
	public EAttribute getBuild_Duration() {
		return (EAttribute) buildEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getDisplayName
	 * <em>Display Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Display Name</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getDisplayName()
	 * @see #getBuild()
	 * @generated
	 */
	public EAttribute getBuild_DisplayName() {
		return (EAttribute) buildEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getState <em>State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>State</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getState()
	 * @see #getBuild()
	 * @generated
	 */
	public EAttribute getBuild_State() {
		return (EAttribute) buildEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getStatus <em>Status</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Status</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getStatus()
	 * @see #getBuild()
	 * @generated
	 */
	public EAttribute getBuild_Status() {
		return (EAttribute) buildEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.builds.core.IBuild#getArtifacts <em>Artifacts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Artifacts</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getArtifacts()
	 * @see #getBuild()
	 * @generated
	 */
	public EReference getBuild_Artifacts() {
		return (EReference) buildEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.builds.core.IBuild#getChangeSet
	 * <em>Change Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference '<em>Change Set</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getChangeSet()
	 * @see #getBuild()
	 * @generated
	 */
	public EReference getBuild_ChangeSet() {
		return (EReference) buildEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.IBuild#getPlan <em>Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Plan</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getPlan()
	 * @see #getBuild()
	 * @generated
	 */
	public EReference getBuild_Plan() {
		return (EReference) buildEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getLabel()
	 * @see #getBuild()
	 * @generated
	 */
	public EAttribute getBuild_Label() {
		return (EAttribute) buildEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.IBuild#getServer <em>Server</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Server</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getServer()
	 * @see #getBuild()
	 * @generated
	 */
	public EReference getBuild_Server() {
		return (EReference) buildEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.builds.core.IBuild#getTestResult
	 * <em>Test Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference '<em>Test Result</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getTestResult()
	 * @see #getBuild()
	 * @generated
	 */
	public EReference getBuild_TestResult() {
		return (EReference) buildEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.builds.core.IBuild#getCulprits <em>Culprits</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Culprits</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getCulprits()
	 * @see #getBuild()
	 * @generated
	 */
	public EReference getBuild_Culprits() {
		return (EReference) buildEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IChangeSet <em>Change Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Change Set</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChangeSet
	 * @generated
	 */
	public EClass getChangeSet() {
		return changeSetEClass;
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.builds.core.IChangeSet#getChanges <em>Changes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Changes</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChangeSet#getChanges()
	 * @see #getChangeSet()
	 * @generated
	 */
	public EReference getChangeSet_Changes() {
		return (EReference) changeSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IChangeSet#getKind <em>Kind</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChangeSet#getKind()
	 * @see #getChangeSet()
	 * @generated
	 */
	public EAttribute getChangeSet_Kind() {
		return (EAttribute) changeSetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IChangeArtifact <em>Change Artifact</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Change Artifact</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChangeArtifact
	 * @generated
	 */
	public EClass getChangeArtifact() {
		return changeArtifactEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IChangeArtifact#getFile
	 * <em>File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>File</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChangeArtifact#getFile()
	 * @see #getChangeArtifact()
	 * @generated
	 */
	public EAttribute getChangeArtifact_File() {
		return (EAttribute) changeArtifactEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IChangeArtifact#getRelativePath
	 * <em>Relative Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Relative Path</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChangeArtifact#getRelativePath()
	 * @see #getChangeArtifact()
	 * @generated
	 */
	public EAttribute getChangeArtifact_RelativePath() {
		return (EAttribute) changeArtifactEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IChangeArtifact#getPrevRevision
	 * <em>Prev Revision</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Prev Revision</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChangeArtifact#getPrevRevision()
	 * @see #getChangeArtifact()
	 * @generated
	 */
	public EAttribute getChangeArtifact_PrevRevision() {
		return (EAttribute) changeArtifactEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IChangeArtifact#getRevision
	 * <em>Revision</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Revision</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChangeArtifact#getRevision()
	 * @see #getChangeArtifact()
	 * @generated
	 */
	public EAttribute getChangeArtifact_Revision() {
		return (EAttribute) changeArtifactEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IChangeArtifact#isDead
	 * <em>Dead</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Dead</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChangeArtifact#isDead()
	 * @see #getChangeArtifact()
	 * @generated
	 */
	public EAttribute getChangeArtifact_Dead() {
		return (EAttribute) changeArtifactEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IChangeArtifact#getEditType
	 * <em>Edit Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Edit Type</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChangeArtifact#getEditType()
	 * @see #getChangeArtifact()
	 * @generated
	 */
	public EAttribute getChangeArtifact_EditType() {
		return (EAttribute) changeArtifactEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IChange <em>Change</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Change</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChange
	 * @generated
	 */
	public EClass getChange() {
		return changeEClass;
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.builds.core.IChange#getArtifacts <em>Artifacts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Artifacts</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChange#getArtifacts()
	 * @see #getChange()
	 * @generated
	 */
	public EReference getChange_Artifacts() {
		return (EReference) changeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.builds.core.IChange#getAuthor
	 * <em>Author</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference '<em>Author</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChange#getAuthor()
	 * @see #getChange()
	 * @generated
	 */
	public EReference getChange_Author() {
		return (EReference) changeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IChange#getMessage
	 * <em>Message</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Message</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChange#getMessage()
	 * @see #getChange()
	 * @generated
	 */
	public EAttribute getChange_Message() {
		return (EAttribute) changeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IChange#getDate <em>Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Date</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChange#getDate()
	 * @see #getChange()
	 * @generated
	 */
	public EAttribute getChange_Date() {
		return (EAttribute) changeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IChange#getRevision
	 * <em>Revision</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Revision</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChange#getRevision()
	 * @see #getChange()
	 * @generated
	 */
	public EAttribute getChange_Revision() {
		return (EAttribute) changeEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IUser <em>User</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>User</em>'.
	 * @see org.eclipse.mylyn.builds.core.IUser
	 * @generated
	 */
	public EClass getUser() {
		return userEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IUser#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.builds.core.IUser#getId()
	 * @see #getUser()
	 * @generated
	 */
	public EAttribute getUser_Id() {
		return (EAttribute) userEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IUser#getEmail <em>Email</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Email</em>'.
	 * @see org.eclipse.mylyn.builds.core.IUser#getEmail()
	 * @see #getUser()
	 * @generated
	 */
	public EAttribute getUser_Email() {
		return (EAttribute) userEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IParameterDefinition
	 * <em>Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.builds.core.IParameterDefinition
	 * @generated
	 */
	public EClass getParameterDefinition() {
		return parameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IParameterDefinition#getName
	 * <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.mylyn.builds.core.IParameterDefinition#getName()
	 * @see #getParameterDefinition()
	 * @generated
	 */
	public EAttribute getParameterDefinition_Name() {
		return (EAttribute) parameterDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.builds.core.IParameterDefinition#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.mylyn.builds.core.IParameterDefinition#getDescription()
	 * @see #getParameterDefinition()
	 * @generated
	 */
	public EAttribute getParameterDefinition_Description() {
		return (EAttribute) parameterDefinitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the container reference '
	 * {@link org.eclipse.mylyn.builds.core.IParameterDefinition#getContainingBuildPlan <em>Containing Build Plan</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the container reference '<em>Containing Build Plan</em>'.
	 * @see org.eclipse.mylyn.builds.core.IParameterDefinition#getContainingBuildPlan()
	 * @see #getParameterDefinition()
	 * @generated
	 */
	public EReference getParameterDefinition_ContainingBuildPlan() {
		return (EReference) parameterDefinitionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IChoiceParameterDefinition
	 * <em>Choice Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Choice Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChoiceParameterDefinition
	 * @generated
	 */
	public EClass getChoiceParameterDefinition() {
		return choiceParameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for the attribute list '
	 * {@link org.eclipse.mylyn.builds.core.IChoiceParameterDefinition#getOptions <em>Options</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute list '<em>Options</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChoiceParameterDefinition#getOptions()
	 * @see #getChoiceParameterDefinition()
	 * @generated
	 */
	public EAttribute getChoiceParameterDefinition_Options() {
		return (EAttribute) choiceParameterDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBooleanParameterDefinition
	 * <em>Boolean Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Boolean Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBooleanParameterDefinition
	 * @generated
	 */
	public EClass getBooleanParameterDefinition() {
		return booleanParameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.builds.core.IBooleanParameterDefinition#isDefaultValue <em>Default Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Default Value</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBooleanParameterDefinition#isDefaultValue()
	 * @see #getBooleanParameterDefinition()
	 * @generated
	 */
	public EAttribute getBooleanParameterDefinition_DefaultValue() {
		return (EAttribute) booleanParameterDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IFileParameterDefinition
	 * <em>File Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>File Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.builds.core.IFileParameterDefinition
	 * @generated
	 */
	public EClass getFileParameterDefinition() {
		return fileParameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IPlanParameterDefinition
	 * <em>Plan Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Plan Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.builds.core.IPlanParameterDefinition
	 * @generated
	 */
	public EClass getPlanParameterDefinition() {
		return planParameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IPasswordParameterDefinition
	 * <em>Password Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Password Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.builds.core.IPasswordParameterDefinition
	 * @generated
	 */
	public EClass getPasswordParameterDefinition() {
		return passwordParameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.builds.core.IPasswordParameterDefinition#getDefaultValue <em>Default Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Default Value</em>'.
	 * @see org.eclipse.mylyn.builds.core.IPasswordParameterDefinition#getDefaultValue()
	 * @see #getPasswordParameterDefinition()
	 * @generated
	 */
	public EAttribute getPasswordParameterDefinition_DefaultValue() {
		return (EAttribute) passwordParameterDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildParameterDefinition
	 * <em>Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildParameterDefinition
	 * @generated
	 */
	public EClass getBuildParameterDefinition() {
		return buildParameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.builds.core.IBuildParameterDefinition#getBuildPlanId <em>Build Plan Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Build Plan Id</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildParameterDefinition#getBuildPlanId()
	 * @see #getBuildParameterDefinition()
	 * @generated
	 */
	public EAttribute getBuildParameterDefinition_BuildPlanId() {
		return (EAttribute) buildParameterDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the reference '
	 * {@link org.eclipse.mylyn.builds.core.IBuildParameterDefinition#getBuildPlan <em>Build Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Build Plan</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildParameterDefinition#getBuildPlan()
	 * @see #getBuildParameterDefinition()
	 * @generated
	 */
	public EReference getBuildParameterDefinition_BuildPlan() {
		return (EReference) buildParameterDefinitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IStringParameterDefinition
	 * <em>String Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>String Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.builds.core.IStringParameterDefinition
	 * @generated
	 */
	public EClass getStringParameterDefinition() {
		return stringParameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.builds.core.IStringParameterDefinition#getDefaultValue <em>Default Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Default Value</em>'.
	 * @see org.eclipse.mylyn.builds.core.IStringParameterDefinition#getDefaultValue()
	 * @see #getStringParameterDefinition()
	 * @generated
	 */
	public EAttribute getStringParameterDefinition_DefaultValue() {
		return (EAttribute) stringParameterDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.ITestResult <em>Test Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Test Result</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestResult
	 * @generated
	 */
	public EClass getTestResult() {
		return testResultEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.ITestResult#getBuild
	 * <em>Build</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Build</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestResult#getBuild()
	 * @see #getTestResult()
	 * @generated
	 */
	public EReference getTestResult_Build() {
		return (EReference) testResultEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.ITestResult#getDuration
	 * <em>Duration</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Duration</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestResult#getDuration()
	 * @see #getTestResult()
	 * @generated
	 */
	public EAttribute getTestResult_Duration() {
		return (EAttribute) testResultEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.ITestResult#getFailCount
	 * <em>Fail Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Fail Count</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestResult#getFailCount()
	 * @see #getTestResult()
	 * @generated
	 */
	public EAttribute getTestResult_FailCount() {
		return (EAttribute) testResultEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.ITestResult#getPassCount
	 * <em>Pass Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Pass Count</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestResult#getPassCount()
	 * @see #getTestResult()
	 * @generated
	 */
	public EAttribute getTestResult_PassCount() {
		return (EAttribute) testResultEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.builds.core.ITestResult#getSuites <em>Suites</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Suites</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestResult#getSuites()
	 * @see #getTestResult()
	 * @generated
	 */
	public EReference getTestResult_Suites() {
		return (EReference) testResultEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.ITestElement <em>Test Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Test Element</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestElement
	 * @generated
	 */
	public EClass getTestElement() {
		return testElementEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.ITestElement#getLabel
	 * <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestElement#getLabel()
	 * @see #getTestElement()
	 * @generated
	 */
	public EAttribute getTestElement_Label() {
		return (EAttribute) testElementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.ITestElement#getDuration
	 * <em>Duration</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Duration</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestElement#getDuration()
	 * @see #getTestElement()
	 * @generated
	 */
	public EAttribute getTestElement_Duration() {
		return (EAttribute) testElementEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.ITestElement#getErrorOutput
	 * <em>Error Output</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Error Output</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestElement#getErrorOutput()
	 * @see #getTestElement()
	 * @generated
	 */
	public EAttribute getTestElement_ErrorOutput() {
		return (EAttribute) testElementEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.ITestElement#getOutput
	 * <em>Output</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Output</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestElement#getOutput()
	 * @see #getTestElement()
	 * @generated
	 */
	public EAttribute getTestElement_Output() {
		return (EAttribute) testElementEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.ITestSuite <em>Test Suite</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Test Suite</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestSuite
	 * @generated
	 */
	public EClass getTestSuite() {
		return testSuiteEClass;
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.builds.core.ITestSuite#getCases <em>Cases</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Cases</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestSuite#getCases()
	 * @see #getTestSuite()
	 * @generated
	 */
	public EReference getTestSuite_Cases() {
		return (EReference) testSuiteEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.mylyn.builds.core.ITestSuite#getResult
	 * <em>Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the container reference '<em>Result</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestSuite#getResult()
	 * @see #getTestSuite()
	 * @generated
	 */
	public EReference getTestSuite_Result() {
		return (EReference) testSuiteEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.ITestCase <em>Test Case</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Test Case</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestCase
	 * @generated
	 */
	public EClass getTestCase() {
		return testCaseEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.ITestCase#getClassName
	 * <em>Class Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Class Name</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestCase#getClassName()
	 * @see #getTestCase()
	 * @generated
	 */
	public EAttribute getTestCase_ClassName() {
		return (EAttribute) testCaseEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.ITestCase#isSkipped
	 * <em>Skipped</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Skipped</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestCase#isSkipped()
	 * @see #getTestCase()
	 * @generated
	 */
	public EAttribute getTestCase_Skipped() {
		return (EAttribute) testCaseEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.mylyn.builds.core.ITestCase#getSuite
	 * <em>Suite</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the container reference '<em>Suite</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestCase#getSuite()
	 * @see #getTestCase()
	 * @generated
	 */
	public EReference getTestCase_Suite() {
		return (EReference) testCaseEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.ITestCase#getStatus
	 * <em>Status</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Status</em>'.
	 * @see org.eclipse.mylyn.builds.core.ITestCase#getStatus()
	 * @see #getTestCase()
	 * @generated
	 */
	public EAttribute getTestCase_Status() {
		return (EAttribute) testCaseEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for enum '{@link org.eclipse.mylyn.builds.core.TestCaseResult <em>Test Case Result</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for enum '<em>Test Case Result</em>'.
	 * @see org.eclipse.mylyn.builds.core.TestCaseResult
	 * @generated
	 */
	public EEnum getTestCaseResult() {
		return testCaseResultEEnum;
	}

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>String To String Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>String To String Map</em>'.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public EClass getStringToStringMap() {
		return stringToStringMapEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToStringMap()
	 * @generated
	 */
	public EAttribute getStringToStringMap_Key() {
		return (EAttribute) stringToStringMapEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToStringMap()
	 * @generated
	 */
	public EAttribute getStringToStringMap_Value() {
		return (EAttribute) stringToStringMapEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for data type '{@link org.eclipse.mylyn.builds.core.IOperation <em>IOperation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for data type '<em>IOperation</em>'.
	 * @see org.eclipse.mylyn.builds.core.IOperation
	 * @generated
	 */
	public EDataType getIOperation() {
		return iOperationEDataType;
	}

	/**
	 * Returns the meta object for data type '{@link org.eclipse.core.runtime.IStatus <em>IStatus</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for data type '<em>IStatus</em>'.
	 * @see org.eclipse.core.runtime.IStatus
	 * @generated
	 */
	public EDataType getIStatus() {
		return iStatusEDataType;
	}

	/**
	 * Returns the meta object for data type '{@link org.eclipse.mylyn.commons.repositories.RepositoryLocation
	 * <em>Repository Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for data type '<em>Repository Location</em>'.
	 * @see org.eclipse.mylyn.commons.repositories.RepositoryLocation
	 * @generated
	 */
	public EDataType getRepositoryLocation() {
		return repositoryLocationEDataType;
	}

	/**
	 * Returns the meta object for data type '{@link org.eclipse.mylyn.builds.core.BuildState <em>State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for data type '<em>State</em>'.
	 * @see org.eclipse.mylyn.builds.core.BuildState
	 * @generated
	 */
	public EDataType getBuildState() {
		return buildStateEDataType;
	}

	/**
	 * Returns the meta object for data type '{@link org.eclipse.mylyn.builds.core.BuildStatus <em>Status</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for data type '<em>Status</em>'.
	 * @see org.eclipse.mylyn.builds.core.BuildStatus
	 * @generated
	 */
	public EDataType getBuildStatus() {
		return buildStatusEDataType;
	}

	/**
	 * Returns the meta object for data type '{@link org.eclipse.mylyn.builds.core.EditType <em>Edit Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for data type '<em>Edit Type</em>'.
	 * @see org.eclipse.mylyn.builds.core.EditType
	 * @generated
	 */
	public EDataType getEditType() {
		return editTypeEDataType;
	}

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	public IBuildFactory getBuildFactory() {
		return (IBuildFactory) getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package. This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated)
			return;
		isCreated = true;

		// Create classes and their features
		stringToStringMapEClass = createEClass(STRING_TO_STRING_MAP);
		createEAttribute(stringToStringMapEClass, STRING_TO_STRING_MAP__KEY);
		createEAttribute(stringToStringMapEClass, STRING_TO_STRING_MAP__VALUE);

		artifactEClass = createEClass(ARTIFACT);
		createEAttribute(artifactEClass, ARTIFACT__RELATIVE_PATH);

		buildEClass = createEClass(BUILD);
		createEAttribute(buildEClass, BUILD__ID);
		createEAttribute(buildEClass, BUILD__BUILD_NUMBER);
		createEAttribute(buildEClass, BUILD__TIMESTAMP);
		createEAttribute(buildEClass, BUILD__DURATION);
		createEAttribute(buildEClass, BUILD__DISPLAY_NAME);
		createEAttribute(buildEClass, BUILD__STATE);
		createEAttribute(buildEClass, BUILD__STATUS);
		createEReference(buildEClass, BUILD__ARTIFACTS);
		createEReference(buildEClass, BUILD__CHANGE_SET);
		createEReference(buildEClass, BUILD__PLAN);
		createEAttribute(buildEClass, BUILD__LABEL);
		createEReference(buildEClass, BUILD__SERVER);
		createEReference(buildEClass, BUILD__TEST_RESULT);
		createEReference(buildEClass, BUILD__CULPRITS);

		buildElementEClass = createEClass(BUILD_ELEMENT);
		createEAttribute(buildElementEClass, BUILD_ELEMENT__URL);
		createEAttribute(buildElementEClass, BUILD_ELEMENT__NAME);
		createEAttribute(buildElementEClass, BUILD_ELEMENT__OPERATIONS);
		createEAttribute(buildElementEClass, BUILD_ELEMENT__ELEMENT_STATUS);
		createEAttribute(buildElementEClass, BUILD_ELEMENT__REFRESH_DATE);

		buildPlanEClass = createEClass(BUILD_PLAN);
		createEReference(buildPlanEClass, BUILD_PLAN__SERVER);
		createEReference(buildPlanEClass, BUILD_PLAN__CHILDREN);
		createEReference(buildPlanEClass, BUILD_PLAN__PARENT);
		createEAttribute(buildPlanEClass, BUILD_PLAN__HEALTH);
		createEAttribute(buildPlanEClass, BUILD_PLAN__ID);
		createEAttribute(buildPlanEClass, BUILD_PLAN__INFO);
		createEAttribute(buildPlanEClass, BUILD_PLAN__SELECTED);
		createEAttribute(buildPlanEClass, BUILD_PLAN__SUMMARY);
		createEAttribute(buildPlanEClass, BUILD_PLAN__STATE);
		createEAttribute(buildPlanEClass, BUILD_PLAN__STATUS);
		createEAttribute(buildPlanEClass, BUILD_PLAN__DESCRIPTION);
		createEReference(buildPlanEClass, BUILD_PLAN__LAST_BUILD);
		createEReference(buildPlanEClass, BUILD_PLAN__PARAMETER_DEFINITIONS);
		createEReference(buildPlanEClass, BUILD_PLAN__HEALTH_REPORTS);

		healthReportEClass = createEClass(HEALTH_REPORT);
		createEAttribute(healthReportEClass, HEALTH_REPORT__HEALTH);
		createEAttribute(healthReportEClass, HEALTH_REPORT__DESCRIPTION);

		buildModelEClass = createEClass(BUILD_MODEL);
		createEReference(buildModelEClass, BUILD_MODEL__SERVERS);
		createEReference(buildModelEClass, BUILD_MODEL__PLANS);
		createEReference(buildModelEClass, BUILD_MODEL__BUILDS);

		buildServerEClass = createEClass(BUILD_SERVER);
		createEReference(buildServerEClass, BUILD_SERVER__ATTRIBUTES);
		createEAttribute(buildServerEClass, BUILD_SERVER__LOCATION);
		createEAttribute(buildServerEClass, BUILD_SERVER__CONNECTOR_KIND);
		createEAttribute(buildServerEClass, BUILD_SERVER__REPOSITORY_URL);

		changeEClass = createEClass(CHANGE);
		createEReference(changeEClass, CHANGE__ARTIFACTS);
		createEReference(changeEClass, CHANGE__AUTHOR);
		createEAttribute(changeEClass, CHANGE__MESSAGE);
		createEAttribute(changeEClass, CHANGE__DATE);
		createEAttribute(changeEClass, CHANGE__REVISION);

		changeSetEClass = createEClass(CHANGE_SET);
		createEReference(changeSetEClass, CHANGE_SET__CHANGES);
		createEAttribute(changeSetEClass, CHANGE_SET__KIND);

		changeArtifactEClass = createEClass(CHANGE_ARTIFACT);
		createEAttribute(changeArtifactEClass, CHANGE_ARTIFACT__FILE);
		createEAttribute(changeArtifactEClass, CHANGE_ARTIFACT__RELATIVE_PATH);
		createEAttribute(changeArtifactEClass, CHANGE_ARTIFACT__PREV_REVISION);
		createEAttribute(changeArtifactEClass, CHANGE_ARTIFACT__REVISION);
		createEAttribute(changeArtifactEClass, CHANGE_ARTIFACT__DEAD);
		createEAttribute(changeArtifactEClass, CHANGE_ARTIFACT__EDIT_TYPE);

		userEClass = createEClass(USER);
		createEAttribute(userEClass, USER__ID);
		createEAttribute(userEClass, USER__EMAIL);

		parameterDefinitionEClass = createEClass(PARAMETER_DEFINITION);
		createEAttribute(parameterDefinitionEClass, PARAMETER_DEFINITION__NAME);
		createEAttribute(parameterDefinitionEClass, PARAMETER_DEFINITION__DESCRIPTION);
		createEReference(parameterDefinitionEClass, PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN);

		choiceParameterDefinitionEClass = createEClass(CHOICE_PARAMETER_DEFINITION);
		createEAttribute(choiceParameterDefinitionEClass, CHOICE_PARAMETER_DEFINITION__OPTIONS);

		booleanParameterDefinitionEClass = createEClass(BOOLEAN_PARAMETER_DEFINITION);
		createEAttribute(booleanParameterDefinitionEClass, BOOLEAN_PARAMETER_DEFINITION__DEFAULT_VALUE);

		fileParameterDefinitionEClass = createEClass(FILE_PARAMETER_DEFINITION);

		planParameterDefinitionEClass = createEClass(PLAN_PARAMETER_DEFINITION);

		passwordParameterDefinitionEClass = createEClass(PASSWORD_PARAMETER_DEFINITION);
		createEAttribute(passwordParameterDefinitionEClass, PASSWORD_PARAMETER_DEFINITION__DEFAULT_VALUE);

		buildParameterDefinitionEClass = createEClass(BUILD_PARAMETER_DEFINITION);
		createEAttribute(buildParameterDefinitionEClass, BUILD_PARAMETER_DEFINITION__BUILD_PLAN_ID);
		createEReference(buildParameterDefinitionEClass, BUILD_PARAMETER_DEFINITION__BUILD_PLAN);

		stringParameterDefinitionEClass = createEClass(STRING_PARAMETER_DEFINITION);
		createEAttribute(stringParameterDefinitionEClass, STRING_PARAMETER_DEFINITION__DEFAULT_VALUE);

		testResultEClass = createEClass(TEST_RESULT);
		createEReference(testResultEClass, TEST_RESULT__BUILD);
		createEAttribute(testResultEClass, TEST_RESULT__DURATION);
		createEAttribute(testResultEClass, TEST_RESULT__FAIL_COUNT);
		createEAttribute(testResultEClass, TEST_RESULT__PASS_COUNT);
		createEReference(testResultEClass, TEST_RESULT__SUITES);

		testElementEClass = createEClass(TEST_ELEMENT);
		createEAttribute(testElementEClass, TEST_ELEMENT__LABEL);
		createEAttribute(testElementEClass, TEST_ELEMENT__DURATION);
		createEAttribute(testElementEClass, TEST_ELEMENT__ERROR_OUTPUT);
		createEAttribute(testElementEClass, TEST_ELEMENT__OUTPUT);

		testSuiteEClass = createEClass(TEST_SUITE);
		createEReference(testSuiteEClass, TEST_SUITE__CASES);
		createEReference(testSuiteEClass, TEST_SUITE__RESULT);

		testCaseEClass = createEClass(TEST_CASE);
		createEAttribute(testCaseEClass, TEST_CASE__CLASS_NAME);
		createEAttribute(testCaseEClass, TEST_CASE__SKIPPED);
		createEReference(testCaseEClass, TEST_CASE__SUITE);
		createEAttribute(testCaseEClass, TEST_CASE__STATUS);

		// Create enums
		testCaseResultEEnum = createEEnum(TEST_CASE_RESULT);

		// Create data types
		buildStateEDataType = createEDataType(BUILD_STATE);
		buildStatusEDataType = createEDataType(BUILD_STATUS);
		editTypeEDataType = createEDataType(EDIT_TYPE);
		iStatusEDataType = createEDataType(ISTATUS);
		iOperationEDataType = createEDataType(IOPERATION);
		repositoryLocationEDataType = createEDataType(REPOSITORY_LOCATION);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model. This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized)
			return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		artifactEClass.getESuperTypes().add(this.getBuildElement());
		buildEClass.getESuperTypes().add(this.getBuildElement());
		buildPlanEClass.getESuperTypes().add(this.getBuildElement());
		buildServerEClass.getESuperTypes().add(this.getBuildElement());
		userEClass.getESuperTypes().add(this.getBuildElement());
		choiceParameterDefinitionEClass.getESuperTypes().add(this.getParameterDefinition());
		booleanParameterDefinitionEClass.getESuperTypes().add(this.getParameterDefinition());
		fileParameterDefinitionEClass.getESuperTypes().add(this.getParameterDefinition());
		planParameterDefinitionEClass.getESuperTypes().add(this.getParameterDefinition());
		passwordParameterDefinitionEClass.getESuperTypes().add(this.getParameterDefinition());
		buildParameterDefinitionEClass.getESuperTypes().add(this.getParameterDefinition());
		stringParameterDefinitionEClass.getESuperTypes().add(this.getParameterDefinition());
		testSuiteEClass.getESuperTypes().add(this.getTestElement());
		testCaseEClass.getESuperTypes().add(this.getTestElement());

		// Initialize classes and features; add operations and parameters
		initEClass(stringToStringMapEClass, Map.Entry.class,
				"StringToStringMap", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getStringToStringMap_Key(),
				ecorePackage.getEString(),
				"key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getStringToStringMap_Value(),
				ecorePackage.getEString(),
				"value", "", 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

		initEClass(artifactEClass, IArtifact.class,
				"Artifact", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getArtifact_RelativePath(),
				ecorePackage.getEString(),
				"relativePath", null, 0, 1, IArtifact.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(buildEClass, IBuild.class, "Build", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getBuild_Id(),
				ecorePackage.getEString(),
				"id", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuild_BuildNumber(),
				ecorePackage.getEInt(),
				"buildNumber", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuild_Timestamp(),
				ecorePackage.getELong(),
				"timestamp", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuild_Duration(),
				ecorePackage.getELong(),
				"duration", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuild_DisplayName(),
				ecorePackage.getEString(),
				"displayName", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuild_State(),
				this.getBuildState(),
				"state", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuild_Status(),
				this.getBuildStatus(),
				"status", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getBuild_Artifacts(),
				this.getArtifact(),
				null,
				"artifacts", null, 0, -1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getBuild_ChangeSet(),
				this.getChangeSet(),
				null,
				"changeSet", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getBuild_Plan(),
				this.getBuildPlan(),
				null,
				"plan", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuild_Label(),
				ecorePackage.getEString(),
				"label", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getBuild_Server(),
				this.getBuildServer(),
				null,
				"server", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getBuild_TestResult(),
				this.getTestResult(),
				null,
				"testResult", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getBuild_Culprits(),
				this.getUser(),
				null,
				"culprits", null, 0, -1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(buildElementEClass, IBuildElement.class,
				"BuildElement", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getBuildElement_Url(),
				ecorePackage.getEString(),
				"url", null, 0, 1, IBuildElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuildElement_Name(),
				ecorePackage.getEString(),
				"name", null, 0, 1, IBuildElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuildElement_Operations(),
				this.getIOperation(),
				"operations", null, 0, -1, IBuildElement.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuildElement_ElementStatus(),
				this.getIStatus(),
				"elementStatus", null, 0, 1, IBuildElement.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuildElement_RefreshDate(),
				ecorePackage.getEDate(),
				"refreshDate", null, 0, 1, IBuildElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		addEOperation(buildElementEClass, ecorePackage.getEString(), "getLabel", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

		addEOperation(buildElementEClass, this.getBuildServer(), "getServer", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

		initEClass(buildPlanEClass, IBuildPlan.class,
				"BuildPlan", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getBuildPlan_Server(),
				this.getBuildServer(),
				null,
				"server", null, 1, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getBuildPlan_Children(),
				this.getBuildPlan(),
				this.getBuildPlan_Parent(),
				"children", null, 0, -1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getBuildPlan_Parent(),
				this.getBuildPlan(),
				this.getBuildPlan_Children(),
				"parent", null, 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuildPlan_Health(),
				ecorePackage.getEInt(),
				"health", "-1", 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(
				getBuildPlan_Id(),
				ecorePackage.getEString(),
				"id", null, 1, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuildPlan_Info(),
				ecorePackage.getEString(),
				"info", null, 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuildPlan_Selected(),
				ecorePackage.getEBoolean(),
				"selected", "false", 1, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(
				getBuildPlan_Summary(),
				ecorePackage.getEString(),
				"summary", null, 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuildPlan_State(),
				this.getBuildState(),
				"state", null, 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuildPlan_Status(),
				this.getBuildStatus(),
				"status", null, 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuildPlan_Description(),
				ecorePackage.getEString(),
				"description", null, 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getBuildPlan_LastBuild(),
				this.getBuild(),
				null,
				"lastBuild", null, 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getBuildPlan_ParameterDefinitions(),
				this.getParameterDefinition(),
				this.getParameterDefinition_ContainingBuildPlan(),
				"parameterDefinitions", null, 0, -1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getBuildPlan_HealthReports(),
				this.getHealthReport(),
				null,
				"healthReports", null, 0, -1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(healthReportEClass, IHealthReport.class,
				"HealthReport", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getHealthReport_Health(),
				ecorePackage.getEInt(),
				"health", null, 0, 1, IHealthReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getHealthReport_Description(),
				ecorePackage.getEString(),
				"description", null, 0, 1, IHealthReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(buildModelEClass, IBuildModel.class,
				"BuildModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getBuildModel_Servers(),
				this.getBuildServer(),
				null,
				"servers", null, 0, -1, IBuildModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getBuildModel_Plans(),
				this.getBuildPlan(),
				null,
				"plans", null, 0, -1, IBuildModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getBuildModel_Builds(),
				this.getBuild(),
				null,
				"builds", null, 0, -1, IBuildModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(buildServerEClass, IBuildServer.class,
				"BuildServer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getBuildServer_Attributes(),
				this.getStringToStringMap(),
				null,
				"attributes", null, 0, -1, IBuildServer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuildServer_Location(),
				this.getRepositoryLocation(),
				"location", null, 0, 1, IBuildServer.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuildServer_ConnectorKind(),
				ecorePackage.getEString(),
				"connectorKind", null, 0, 1, IBuildServer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getBuildServer_RepositoryUrl(),
				ecorePackage.getEString(),
				"repositoryUrl", null, 0, 1, IBuildServer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(changeEClass, IChange.class, "Change", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getChange_Artifacts(),
				this.getChangeArtifact(),
				null,
				"artifacts", null, 0, -1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getChange_Author(),
				this.getUser(),
				null,
				"author", null, 0, 1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getChange_Message(),
				ecorePackage.getEString(),
				"message", null, 0, 1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getChange_Date(),
				ecorePackage.getELong(),
				"date", null, 0, 1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getChange_Revision(),
				ecorePackage.getEString(),
				"revision", null, 0, 1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(changeSetEClass, IChangeSet.class,
				"ChangeSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getChangeSet_Changes(),
				this.getChange(),
				null,
				"changes", null, 0, -1, IChangeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getChangeSet_Kind(),
				ecorePackage.getEString(),
				"kind", null, 0, 1, IChangeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(changeArtifactEClass, IChangeArtifact.class,
				"ChangeArtifact", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getChangeArtifact_File(),
				ecorePackage.getEString(),
				"file", null, 0, 1, IChangeArtifact.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getChangeArtifact_RelativePath(),
				ecorePackage.getEString(),
				"relativePath", null, 0, 1, IChangeArtifact.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getChangeArtifact_PrevRevision(),
				ecorePackage.getEString(),
				"prevRevision", null, 0, 1, IChangeArtifact.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getChangeArtifact_Revision(),
				ecorePackage.getEString(),
				"revision", null, 0, 1, IChangeArtifact.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getChangeArtifact_Dead(),
				ecorePackage.getEBoolean(),
				"dead", null, 0, 1, IChangeArtifact.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getChangeArtifact_EditType(),
				this.getEditType(),
				"editType", null, 0, 1, IChangeArtifact.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(userEClass, IUser.class, "User", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getUser_Id(),
				ecorePackage.getEString(),
				"id", null, 0, 1, IUser.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getUser_Email(),
				ecorePackage.getEString(),
				"email", null, 0, 1, IUser.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(parameterDefinitionEClass, IParameterDefinition.class,
				"ParameterDefinition", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getParameterDefinition_Name(),
				ecorePackage.getEString(),
				"name", null, 0, 1, IParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getParameterDefinition_Description(),
				ecorePackage.getEString(),
				"description", null, 0, 1, IParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getParameterDefinition_ContainingBuildPlan(),
				this.getBuildPlan(),
				this.getBuildPlan_ParameterDefinitions(),
				"containingBuildPlan", null, 0, 1, IParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(choiceParameterDefinitionEClass, IChoiceParameterDefinition.class,
				"ChoiceParameterDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getChoiceParameterDefinition_Options(),
				ecorePackage.getEString(),
				"options", null, 1, -1, IChoiceParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(booleanParameterDefinitionEClass, IBooleanParameterDefinition.class,
				"BooleanParameterDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getBooleanParameterDefinition_DefaultValue(),
				ecorePackage.getEBoolean(),
				"defaultValue", null, 0, 1, IBooleanParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(fileParameterDefinitionEClass, IFileParameterDefinition.class,
				"FileParameterDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(planParameterDefinitionEClass, IPlanParameterDefinition.class,
				"PlanParameterDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(passwordParameterDefinitionEClass, IPasswordParameterDefinition.class,
				"PasswordParameterDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getPasswordParameterDefinition_DefaultValue(),
				ecorePackage.getEString(),
				"defaultValue", null, 0, 1, IPasswordParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(buildParameterDefinitionEClass, IBuildParameterDefinition.class,
				"BuildParameterDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getBuildParameterDefinition_BuildPlanId(),
				ecorePackage.getEString(),
				"buildPlanId", null, 0, 1, IBuildParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getBuildParameterDefinition_BuildPlan(),
				this.getBuildPlan(),
				null,
				"buildPlan", null, 0, 1, IBuildParameterDefinition.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(stringParameterDefinitionEClass, IStringParameterDefinition.class,
				"StringParameterDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getStringParameterDefinition_DefaultValue(),
				ecorePackage.getEString(),
				"defaultValue", null, 0, 1, IStringParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(testResultEClass, ITestResult.class,
				"TestResult", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getTestResult_Build(),
				this.getBuild(),
				null,
				"build", null, 0, 1, ITestResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestResult_Duration(),
				ecorePackage.getELong(),
				"duration", null, 0, 1, ITestResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestResult_FailCount(),
				ecorePackage.getEInt(),
				"failCount", null, 0, 1, ITestResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestResult_PassCount(),
				ecorePackage.getEInt(),
				"passCount", null, 0, 1, ITestResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getTestResult_Suites(),
				this.getTestSuite(),
				this.getTestSuite_Result(),
				"suites", null, 0, -1, ITestResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(testElementEClass, ITestElement.class,
				"TestElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getTestElement_Label(),
				ecorePackage.getEString(),
				"label", null, 0, 1, ITestElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestElement_Duration(),
				ecorePackage.getELong(),
				"duration", null, 0, 1, ITestElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestElement_ErrorOutput(),
				ecorePackage.getEString(),
				"errorOutput", null, 0, 1, ITestElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestElement_Output(),
				ecorePackage.getEString(),
				"output", null, 0, 1, ITestElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(testSuiteEClass, ITestSuite.class,
				"TestSuite", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getTestSuite_Cases(),
				this.getTestCase(),
				this.getTestCase_Suite(),
				"cases", null, 0, -1, ITestSuite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getTestSuite_Result(),
				this.getTestResult(),
				this.getTestResult_Suites(),
				"result", null, 0, 1, ITestSuite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(testCaseEClass, ITestCase.class,
				"TestCase", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getTestCase_ClassName(),
				ecorePackage.getEString(),
				"className", null, 0, 1, ITestCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestCase_Skipped(),
				ecorePackage.getEBoolean(),
				"skipped", null, 0, 1, ITestCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getTestCase_Suite(),
				this.getTestSuite(),
				this.getTestSuite_Cases(),
				"suite", null, 0, 1, ITestCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestCase_Status(),
				this.getTestCaseResult(),
				"status", null, 0, 1, ITestCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		// Initialize enums and add enum literals
		initEEnum(testCaseResultEEnum, TestCaseResult.class, "TestCaseResult"); //$NON-NLS-1$
		addEEnumLiteral(testCaseResultEEnum, TestCaseResult.PASSED);
		addEEnumLiteral(testCaseResultEEnum, TestCaseResult.SKIPPED);
		addEEnumLiteral(testCaseResultEEnum, TestCaseResult.FAILED);
		addEEnumLiteral(testCaseResultEEnum, TestCaseResult.FIXED);
		addEEnumLiteral(testCaseResultEEnum, TestCaseResult.REGRESSION);

		// Initialize data types
		initEDataType(buildStateEDataType, BuildState.class,
				"BuildState", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEDataType(buildStatusEDataType, BuildStatus.class,
				"BuildStatus", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEDataType(editTypeEDataType, EditType.class, "EditType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEDataType(iStatusEDataType, IStatus.class, "IStatus", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEDataType(iOperationEDataType, IOperation.class,
				"IOperation", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEDataType(repositoryLocationEDataType, RepositoryLocation.class,
				"RepositoryLocation", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		// Create resource
		createResource(eNS_URI);
	}

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 * <li>each class,</li>
	 * <li>each feature of each class,</li>
	 * <li>each enum,</li>
	 * <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.BuildElement <em>Element</em>}
		 * ' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.BuildElement
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuildElement()
		 * @generated
		 */
		public static final EClass BUILD_ELEMENT = eINSTANCE.getBuildElement();

		/**
		 * The meta object literal for the '<em><b>Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_ELEMENT__URL = eINSTANCE.getBuildElement_Url();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_ELEMENT__NAME = eINSTANCE.getBuildElement_Name();

		/**
		 * The meta object literal for the '<em><b>Operations</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_ELEMENT__OPERATIONS = eINSTANCE.getBuildElement_Operations();

		/**
		 * The meta object literal for the '<em><b>Element Status</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_ELEMENT__ELEMENT_STATUS = eINSTANCE.getBuildElement_ElementStatus();

		/**
		 * The meta object literal for the '<em><b>Refresh Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_ELEMENT__REFRESH_DATE = eINSTANCE.getBuildElement_RefreshDate();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.Artifact <em>Artifact</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.Artifact
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getArtifact()
		 * @generated
		 */
		public static final EClass ARTIFACT = eINSTANCE.getArtifact();

		/**
		 * The meta object literal for the '<em><b>Relative Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute ARTIFACT__RELATIVE_PATH = eINSTANCE.getArtifact_RelativePath();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.Build <em>Build</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.Build
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuild()
		 * @generated
		 */
		public static final EClass BUILD = eINSTANCE.getBuild();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD__ID = eINSTANCE.getBuild_Id();

		/**
		 * The meta object literal for the '<em><b>Build Number</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD__BUILD_NUMBER = eINSTANCE.getBuild_BuildNumber();

		/**
		 * The meta object literal for the '<em><b>Timestamp</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD__TIMESTAMP = eINSTANCE.getBuild_Timestamp();

		/**
		 * The meta object literal for the '<em><b>Duration</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD__DURATION = eINSTANCE.getBuild_Duration();

		/**
		 * The meta object literal for the '<em><b>Display Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD__DISPLAY_NAME = eINSTANCE.getBuild_DisplayName();

		/**
		 * The meta object literal for the '<em><b>State</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD__STATE = eINSTANCE.getBuild_State();

		/**
		 * The meta object literal for the '<em><b>Status</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD__STATUS = eINSTANCE.getBuild_Status();

		/**
		 * The meta object literal for the '<em><b>Artifacts</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD__ARTIFACTS = eINSTANCE.getBuild_Artifacts();

		/**
		 * The meta object literal for the '<em><b>Change Set</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD__CHANGE_SET = eINSTANCE.getBuild_ChangeSet();

		/**
		 * The meta object literal for the '<em><b>Plan</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD__PLAN = eINSTANCE.getBuild_Plan();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD__LABEL = eINSTANCE.getBuild_Label();

		/**
		 * The meta object literal for the '<em><b>Server</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD__SERVER = eINSTANCE.getBuild_Server();

		/**
		 * The meta object literal for the '<em><b>Test Result</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD__TEST_RESULT = eINSTANCE.getBuild_TestResult();

		/**
		 * The meta object literal for the '<em><b>Culprits</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD__CULPRITS = eINSTANCE.getBuild_Culprits();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.BuildPlan <em>Plan</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPlan
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuildPlan()
		 * @generated
		 */
		public static final EClass BUILD_PLAN = eINSTANCE.getBuildPlan();

		/**
		 * The meta object literal for the '<em><b>Server</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD_PLAN__SERVER = eINSTANCE.getBuildPlan_Server();

		/**
		 * The meta object literal for the '<em><b>Children</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD_PLAN__CHILDREN = eINSTANCE.getBuildPlan_Children();

		/**
		 * The meta object literal for the '<em><b>Parent</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD_PLAN__PARENT = eINSTANCE.getBuildPlan_Parent();

		/**
		 * The meta object literal for the '<em><b>Health</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_PLAN__HEALTH = eINSTANCE.getBuildPlan_Health();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_PLAN__ID = eINSTANCE.getBuildPlan_Id();

		/**
		 * The meta object literal for the '<em><b>Info</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_PLAN__INFO = eINSTANCE.getBuildPlan_Info();

		/**
		 * The meta object literal for the '<em><b>Selected</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_PLAN__SELECTED = eINSTANCE.getBuildPlan_Selected();

		/**
		 * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_PLAN__SUMMARY = eINSTANCE.getBuildPlan_Summary();

		/**
		 * The meta object literal for the '<em><b>State</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_PLAN__STATE = eINSTANCE.getBuildPlan_State();

		/**
		 * The meta object literal for the '<em><b>Status</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_PLAN__STATUS = eINSTANCE.getBuildPlan_Status();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_PLAN__DESCRIPTION = eINSTANCE.getBuildPlan_Description();

		/**
		 * The meta object literal for the '<em><b>Last Build</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD_PLAN__LAST_BUILD = eINSTANCE.getBuildPlan_LastBuild();

		/**
		 * The meta object literal for the '<em><b>Parameter Definitions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD_PLAN__PARAMETER_DEFINITIONS = eINSTANCE
				.getBuildPlan_ParameterDefinitions();

		/**
		 * The meta object literal for the '<em><b>Health Reports</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD_PLAN__HEALTH_REPORTS = eINSTANCE.getBuildPlan_HealthReports();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.HealthReport
		 * <em>Health Report</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.HealthReport
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getHealthReport()
		 * @generated
		 */
		public static final EClass HEALTH_REPORT = eINSTANCE.getHealthReport();

		/**
		 * The meta object literal for the '<em><b>Health</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute HEALTH_REPORT__HEALTH = eINSTANCE.getHealthReport_Health();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute HEALTH_REPORT__DESCRIPTION = eINSTANCE.getHealthReport_Description();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.BuildServer <em>Server</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.BuildServer
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuildServer()
		 * @generated
		 */
		public static final EClass BUILD_SERVER = eINSTANCE.getBuildServer();

		/**
		 * The meta object literal for the '<em><b>Attributes</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD_SERVER__ATTRIBUTES = eINSTANCE.getBuildServer_Attributes();

		/**
		 * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_SERVER__LOCATION = eINSTANCE.getBuildServer_Location();

		/**
		 * The meta object literal for the '<em><b>Connector Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_SERVER__CONNECTOR_KIND = eINSTANCE.getBuildServer_ConnectorKind();

		/**
		 * The meta object literal for the '<em><b>Repository Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_SERVER__REPOSITORY_URL = eINSTANCE.getBuildServer_RepositoryUrl();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.BuildModel <em>Model</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.BuildModel
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuildModel()
		 * @generated
		 */
		public static final EClass BUILD_MODEL = eINSTANCE.getBuildModel();

		/**
		 * The meta object literal for the '<em><b>Servers</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD_MODEL__SERVERS = eINSTANCE.getBuildModel_Servers();

		/**
		 * The meta object literal for the '<em><b>Plans</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD_MODEL__PLANS = eINSTANCE.getBuildModel_Plans();

		/**
		 * The meta object literal for the '<em><b>Builds</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD_MODEL__BUILDS = eINSTANCE.getBuildModel_Builds();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.Change <em>Change</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.Change
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getChange()
		 * @generated
		 */
		public static final EClass CHANGE = eINSTANCE.getChange();

		/**
		 * The meta object literal for the '<em><b>Artifacts</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference CHANGE__ARTIFACTS = eINSTANCE.getChange_Artifacts();

		/**
		 * The meta object literal for the '<em><b>Author</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference CHANGE__AUTHOR = eINSTANCE.getChange_Author();

		/**
		 * The meta object literal for the '<em><b>Message</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute CHANGE__MESSAGE = eINSTANCE.getChange_Message();

		/**
		 * The meta object literal for the '<em><b>Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute CHANGE__DATE = eINSTANCE.getChange_Date();

		/**
		 * The meta object literal for the '<em><b>Revision</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute CHANGE__REVISION = eINSTANCE.getChange_Revision();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.ChangeSet <em>Change Set</em>}
		 * ' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.ChangeSet
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getChangeSet()
		 * @generated
		 */
		public static final EClass CHANGE_SET = eINSTANCE.getChangeSet();

		/**
		 * The meta object literal for the '<em><b>Changes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference CHANGE_SET__CHANGES = eINSTANCE.getChangeSet_Changes();

		/**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute CHANGE_SET__KIND = eINSTANCE.getChangeSet_Kind();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.ChangeArtifact
		 * <em>Change Artifact</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.ChangeArtifact
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getChangeArtifact()
		 * @generated
		 */
		public static final EClass CHANGE_ARTIFACT = eINSTANCE.getChangeArtifact();

		/**
		 * The meta object literal for the '<em><b>File</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute CHANGE_ARTIFACT__FILE = eINSTANCE.getChangeArtifact_File();

		/**
		 * The meta object literal for the '<em><b>Relative Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute CHANGE_ARTIFACT__RELATIVE_PATH = eINSTANCE.getChangeArtifact_RelativePath();

		/**
		 * The meta object literal for the '<em><b>Prev Revision</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute CHANGE_ARTIFACT__PREV_REVISION = eINSTANCE.getChangeArtifact_PrevRevision();

		/**
		 * The meta object literal for the '<em><b>Revision</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute CHANGE_ARTIFACT__REVISION = eINSTANCE.getChangeArtifact_Revision();

		/**
		 * The meta object literal for the '<em><b>Dead</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute CHANGE_ARTIFACT__DEAD = eINSTANCE.getChangeArtifact_Dead();

		/**
		 * The meta object literal for the '<em><b>Edit Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute CHANGE_ARTIFACT__EDIT_TYPE = eINSTANCE.getChangeArtifact_EditType();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.StringToStringMap
		 * <em>String To String Map</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.StringToStringMap
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getStringToStringMap()
		 * @generated
		 */
		public static final EClass STRING_TO_STRING_MAP = eINSTANCE.getStringToStringMap();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute STRING_TO_STRING_MAP__KEY = eINSTANCE.getStringToStringMap_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute STRING_TO_STRING_MAP__VALUE = eINSTANCE.getStringToStringMap_Value();

		/**
		 * The meta object literal for the '<em>IOperation</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.IOperation
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getIOperation()
		 * @generated
		 */
		public static final EDataType IOPERATION = eINSTANCE.getIOperation();

		/**
		 * The meta object literal for the '<em>IStatus</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.core.runtime.IStatus
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getIStatus()
		 * @generated
		 */
		public static final EDataType ISTATUS = eINSTANCE.getIStatus();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.User <em>User</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.User
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getUser()
		 * @generated
		 */
		public static final EClass USER = eINSTANCE.getUser();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute USER__ID = eINSTANCE.getUser_Id();

		/**
		 * The meta object literal for the '<em><b>Email</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute USER__EMAIL = eINSTANCE.getUser_Email();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.ParameterDefinition
		 * <em>Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.ParameterDefinition
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getParameterDefinition()
		 * @generated
		 */
		public static final EClass PARAMETER_DEFINITION = eINSTANCE.getParameterDefinition();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute PARAMETER_DEFINITION__NAME = eINSTANCE.getParameterDefinition_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute PARAMETER_DEFINITION__DESCRIPTION = eINSTANCE
				.getParameterDefinition_Description();

		/**
		 * The meta object literal for the '<em><b>Containing Build Plan</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN = eINSTANCE
				.getParameterDefinition_ContainingBuildPlan();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.ChoiceParameterDefinition
		 * <em>Choice Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.ChoiceParameterDefinition
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getChoiceParameterDefinition()
		 * @generated
		 */
		public static final EClass CHOICE_PARAMETER_DEFINITION = eINSTANCE.getChoiceParameterDefinition();

		/**
		 * The meta object literal for the '<em><b>Options</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute CHOICE_PARAMETER_DEFINITION__OPTIONS = eINSTANCE
				.getChoiceParameterDefinition_Options();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.BooleanParameterDefinition
		 * <em>Boolean Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.BooleanParameterDefinition
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBooleanParameterDefinition()
		 * @generated
		 */
		public static final EClass BOOLEAN_PARAMETER_DEFINITION = eINSTANCE.getBooleanParameterDefinition();

		/**
		 * The meta object literal for the '<em><b>Default Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BOOLEAN_PARAMETER_DEFINITION__DEFAULT_VALUE = eINSTANCE
				.getBooleanParameterDefinition_DefaultValue();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.FileParameterDefinition
		 * <em>File Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.FileParameterDefinition
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getFileParameterDefinition()
		 * @generated
		 */
		public static final EClass FILE_PARAMETER_DEFINITION = eINSTANCE.getFileParameterDefinition();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.PlanParameterDefinition
		 * <em>Plan Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.PlanParameterDefinition
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getPlanParameterDefinition()
		 * @generated
		 */
		public static final EClass PLAN_PARAMETER_DEFINITION = eINSTANCE.getPlanParameterDefinition();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.PasswordParameterDefinition
		 * <em>Password Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.PasswordParameterDefinition
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getPasswordParameterDefinition()
		 * @generated
		 */
		public static final EClass PASSWORD_PARAMETER_DEFINITION = eINSTANCE.getPasswordParameterDefinition();

		/**
		 * The meta object literal for the '<em><b>Default Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute PASSWORD_PARAMETER_DEFINITION__DEFAULT_VALUE = eINSTANCE
				.getPasswordParameterDefinition_DefaultValue();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.BuildParameterDefinition
		 * <em>Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.BuildParameterDefinition
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuildParameterDefinition()
		 * @generated
		 */
		public static final EClass BUILD_PARAMETER_DEFINITION = eINSTANCE.getBuildParameterDefinition();

		/**
		 * The meta object literal for the '<em><b>Build Plan Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute BUILD_PARAMETER_DEFINITION__BUILD_PLAN_ID = eINSTANCE
				.getBuildParameterDefinition_BuildPlanId();

		/**
		 * The meta object literal for the '<em><b>Build Plan</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference BUILD_PARAMETER_DEFINITION__BUILD_PLAN = eINSTANCE
				.getBuildParameterDefinition_BuildPlan();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.StringParameterDefinition
		 * <em>String Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.StringParameterDefinition
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getStringParameterDefinition()
		 * @generated
		 */
		public static final EClass STRING_PARAMETER_DEFINITION = eINSTANCE.getStringParameterDefinition();

		/**
		 * The meta object literal for the '<em><b>Default Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute STRING_PARAMETER_DEFINITION__DEFAULT_VALUE = eINSTANCE
				.getStringParameterDefinition_DefaultValue();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.TestResult
		 * <em>Test Result</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.TestResult
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getTestResult()
		 * @generated
		 */
		public static final EClass TEST_RESULT = eINSTANCE.getTestResult();

		/**
		 * The meta object literal for the '<em><b>Build</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference TEST_RESULT__BUILD = eINSTANCE.getTestResult_Build();

		/**
		 * The meta object literal for the '<em><b>Duration</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute TEST_RESULT__DURATION = eINSTANCE.getTestResult_Duration();

		/**
		 * The meta object literal for the '<em><b>Fail Count</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute TEST_RESULT__FAIL_COUNT = eINSTANCE.getTestResult_FailCount();

		/**
		 * The meta object literal for the '<em><b>Pass Count</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute TEST_RESULT__PASS_COUNT = eINSTANCE.getTestResult_PassCount();

		/**
		 * The meta object literal for the '<em><b>Suites</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference TEST_RESULT__SUITES = eINSTANCE.getTestResult_Suites();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.TestElement
		 * <em>Test Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.TestElement
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getTestElement()
		 * @generated
		 */
		public static final EClass TEST_ELEMENT = eINSTANCE.getTestElement();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute TEST_ELEMENT__LABEL = eINSTANCE.getTestElement_Label();

		/**
		 * The meta object literal for the '<em><b>Duration</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute TEST_ELEMENT__DURATION = eINSTANCE.getTestElement_Duration();

		/**
		 * The meta object literal for the '<em><b>Error Output</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute TEST_ELEMENT__ERROR_OUTPUT = eINSTANCE.getTestElement_ErrorOutput();

		/**
		 * The meta object literal for the '<em><b>Output</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute TEST_ELEMENT__OUTPUT = eINSTANCE.getTestElement_Output();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.TestSuite <em>Test Suite</em>}
		 * ' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.TestSuite
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getTestSuite()
		 * @generated
		 */
		public static final EClass TEST_SUITE = eINSTANCE.getTestSuite();

		/**
		 * The meta object literal for the '<em><b>Cases</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference TEST_SUITE__CASES = eINSTANCE.getTestSuite_Cases();

		/**
		 * The meta object literal for the '<em><b>Result</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference TEST_SUITE__RESULT = eINSTANCE.getTestSuite_Result();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.internal.core.TestCase <em>Test Case</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.internal.core.TestCase
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getTestCase()
		 * @generated
		 */
		public static final EClass TEST_CASE = eINSTANCE.getTestCase();

		/**
		 * The meta object literal for the '<em><b>Class Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute TEST_CASE__CLASS_NAME = eINSTANCE.getTestCase_ClassName();

		/**
		 * The meta object literal for the '<em><b>Skipped</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute TEST_CASE__SKIPPED = eINSTANCE.getTestCase_Skipped();

		/**
		 * The meta object literal for the '<em><b>Suite</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference TEST_CASE__SUITE = eINSTANCE.getTestCase_Suite();

		/**
		 * The meta object literal for the '<em><b>Status</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute TEST_CASE__STATUS = eINSTANCE.getTestCase_Status();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.TestCaseResult
		 * <em>Test Case Result</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.TestCaseResult
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getTestCaseResult()
		 * @generated
		 */
		public static final EEnum TEST_CASE_RESULT = eINSTANCE.getTestCaseResult();

		/**
		 * The meta object literal for the '<em>Repository Location</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.commons.repositories.RepositoryLocation
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getRepositoryLocation()
		 * @generated
		 */
		public static final EDataType REPOSITORY_LOCATION = eINSTANCE.getRepositoryLocation();

		/**
		 * The meta object literal for the '<em>State</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.BuildState
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuildState()
		 * @generated
		 */
		public static final EDataType BUILD_STATE = eINSTANCE.getBuildState();

		/**
		 * The meta object literal for the '<em>Status</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.BuildStatus
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getBuildStatus()
		 * @generated
		 */
		public static final EDataType BUILD_STATUS = eINSTANCE.getBuildStatus();

		/**
		 * The meta object literal for the '<em>Edit Type</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.EditType
		 * @see org.eclipse.mylyn.builds.internal.core.BuildPackage#getEditType()
		 * @generated
		 */
		public static final EDataType EDIT_TYPE = eINSTANCE.getEditType();

	}

} //BuildPackage
