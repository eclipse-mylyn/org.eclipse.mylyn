/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildPackage.java,v 1.16 2010/08/28 03:38:02 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.EditType;
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
 * @see org.eclipse.mylyn.internal.builds.core.BuildFactory
 * @model kind="package"
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
	public static final String eNAME = "builds";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static final String eNS_URI = "http://eclipse.org/mylyn/models/build";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static final String eNS_PREFIX = "builds";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static final BuildPackage eINSTANCE = org.eclipse.mylyn.internal.builds.core.BuildPackage.init();

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IBuildModel <em>IBuild Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.IBuildModel
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildModel()
	 * @generated
	 */
	public static final int IBUILD_MODEL = 0;

	/**
	 * The feature id for the '<em><b>Servers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_MODEL__SERVERS = 0;

	/**
	 * The feature id for the '<em><b>Plans</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_MODEL__PLANS = 1;

	/**
	 * The feature id for the '<em><b>Builds</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_MODEL__BUILDS = 2;

	/**
	 * The number of structural features of the '<em>IBuild Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_MODEL_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.BuildModel <em>Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.BuildModel
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildModel()
	 * @generated
	 */
	public static final int BUILD_MODEL = 17;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IBuildPlanData <em>IBuild Plan Data</em>}'
	 * class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.IBuildPlanData
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlanData()
	 * @generated
	 */
	public static final int IBUILD_PLAN_DATA = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IBuildElement <em>IBuild Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.IBuildElement
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildElement()
	 * @generated
	 */
	public static final int IBUILD_ELEMENT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IBuildPlan <em>IBuild Plan</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan()
	 * @generated
	 */
	public static final int IBUILD_PLAN = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy
	 * <em>IBuild Plan Working Copy</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlanWorkingCopy()
	 * @generated
	 */
	public static final int IBUILD_PLAN_WORKING_COPY = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan <em>Plan</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPlan
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildPlan()
	 * @generated
	 */
	public static final int BUILD_PLAN = 15;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_ELEMENT__URL = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_ELEMENT__NAME = 1;

	/**
	 * The number of structural features of the '<em>IBuild Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_ELEMENT_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IBuildServer <em>IBuild Server</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.IBuildServer
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildServer()
	 * @generated
	 */
	public static final int IBUILD_SERVER = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.BuildServer <em>Server</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.BuildServer
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildServer()
	 * @generated
	 */
	public static final int BUILD_SERVER = 16;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_SERVER__URL = IBUILD_ELEMENT__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_SERVER__NAME = IBUILD_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_SERVER__ATTRIBUTES = IBUILD_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_SERVER__LOCATION = IBUILD_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Connector Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_SERVER__CONNECTOR_KIND = IBUILD_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Repository Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_SERVER__REPOSITORY_URL = IBUILD_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>IBuild Server</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_SERVER_FEATURE_COUNT = IBUILD_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__URL = IBUILD_ELEMENT__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__NAME = IBUILD_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Server</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__SERVER = IBUILD_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Children</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__CHILDREN = IBUILD_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__PARENT = IBUILD_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Health</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__HEALTH = IBUILD_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__ID = IBUILD_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Info</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__INFO = IBUILD_ELEMENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Selected</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__SELECTED = IBUILD_ELEMENT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__SUMMARY = IBUILD_ELEMENT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__STATE = IBUILD_ELEMENT_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__STATUS = IBUILD_ELEMENT_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__DESCRIPTION = IBUILD_ELEMENT_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Last Build</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__LAST_BUILD = IBUILD_ELEMENT_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Parameter Definitions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__PARAMETER_DEFINITIONS = IBUILD_ELEMENT_FEATURE_COUNT + 12;

	/**
	 * The number of structural features of the '<em>IBuild Plan</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_FEATURE_COUNT = IBUILD_ELEMENT_FEATURE_COUNT + 13;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IArtifact <em>IArtifact</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.IArtifact
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIArtifact()
	 * @generated
	 */
	public static final int IARTIFACT = 8;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.Artifact <em>Artifact</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.Artifact
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getArtifact()
	 * @generated
	 */
	public static final int ARTIFACT = 13;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IBuild <em>IBuild</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.IBuild
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuild()
	 * @generated
	 */
	public static final int IBUILD = 6;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IBuildWorkingCopy <em>IBuild Working Copy</em>}'
	 * class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.IBuildWorkingCopy
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildWorkingCopy()
	 * @generated
	 */
	public static final int IBUILD_WORKING_COPY = 7;

	/**
	 * The number of structural features of the '<em>IBuild Plan Data</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_DATA_FEATURE_COUNT = 0;

	/**
	 * The number of structural features of the '<em>IBuild Plan Working Copy</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_WORKING_COPY_FEATURE_COUNT = IBUILD_PLAN_DATA_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD__URL = IBUILD_ELEMENT__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD__NAME = IBUILD_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD__ID = IBUILD_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Build Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD__BUILD_NUMBER = IBUILD_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD__TIMESTAMP = IBUILD_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Duration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD__DURATION = IBUILD_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Display Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD__DISPLAY_NAME = IBUILD_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD__STATE = IBUILD_ELEMENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD__STATUS = IBUILD_ELEMENT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Artifacts</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD__ARTIFACTS = IBUILD_ELEMENT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Change Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD__CHANGE_SET = IBUILD_ELEMENT_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Plan</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD__PLAN = IBUILD_ELEMENT_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD__LABEL = IBUILD_ELEMENT_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Server</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD__SERVER = IBUILD_ELEMENT_FEATURE_COUNT + 11;

	/**
	 * The number of structural features of the '<em>IBuild</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_FEATURE_COUNT = IBUILD_ELEMENT_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_WORKING_COPY__URL = IBUILD__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_WORKING_COPY__NAME = IBUILD__NAME;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_WORKING_COPY__ID = IBUILD__ID;

	/**
	 * The feature id for the '<em><b>Build Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_WORKING_COPY__BUILD_NUMBER = IBUILD__BUILD_NUMBER;

	/**
	 * The feature id for the '<em><b>Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_WORKING_COPY__TIMESTAMP = IBUILD__TIMESTAMP;

	/**
	 * The feature id for the '<em><b>Duration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_WORKING_COPY__DURATION = IBUILD__DURATION;

	/**
	 * The feature id for the '<em><b>Display Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_WORKING_COPY__DISPLAY_NAME = IBUILD__DISPLAY_NAME;

	/**
	 * The feature id for the '<em><b>State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_WORKING_COPY__STATE = IBUILD__STATE;

	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_WORKING_COPY__STATUS = IBUILD__STATUS;

	/**
	 * The feature id for the '<em><b>Artifacts</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_WORKING_COPY__ARTIFACTS = IBUILD__ARTIFACTS;

	/**
	 * The feature id for the '<em><b>Change Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_WORKING_COPY__CHANGE_SET = IBUILD__CHANGE_SET;

	/**
	 * The feature id for the '<em><b>Plan</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_WORKING_COPY__PLAN = IBUILD__PLAN;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_WORKING_COPY__LABEL = IBUILD__LABEL;

	/**
	 * The feature id for the '<em><b>Server</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_WORKING_COPY__SERVER = IBUILD__SERVER;

	/**
	 * The number of structural features of the '<em>IBuild Working Copy</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_WORKING_COPY_FEATURE_COUNT = IBUILD_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Display Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IARTIFACT__DISPLAY_NAME = 0;

	/**
	 * The feature id for the '<em><b>Filename</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IARTIFACT__FILENAME = 1;

	/**
	 * The feature id for the '<em><b>Relative Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IARTIFACT__RELATIVE_PATH = 2;

	/**
	 * The number of structural features of the '<em>IArtifact</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IARTIFACT_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.Build <em>Build</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.Build
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuild()
	 * @generated
	 */
	public static final int BUILD = 14;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IChangeSet <em>IChange Set</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.IChangeSet
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIChangeSet()
	 * @generated
	 */
	public static final int ICHANGE_SET = 10;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.ChangeSet <em>Change Set</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.ChangeSet
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getChangeSet()
	 * @generated
	 */
	public static final int CHANGE_SET = 19;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IChange <em>IChange</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.IChange
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIChange()
	 * @generated
	 */
	public static final int ICHANGE = 9;

	/**
	 * The feature id for the '<em><b>Author</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ICHANGE__AUTHOR = 0;

	/**
	 * The feature id for the '<em><b>File</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ICHANGE__FILE = 1;

	/**
	 * The feature id for the '<em><b>Message</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ICHANGE__MESSAGE = 2;

	/**
	 * The feature id for the '<em><b>Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ICHANGE__DATE = 3;

	/**
	 * The feature id for the '<em><b>User</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ICHANGE__USER = 4;

	/**
	 * The number of structural features of the '<em>IChange</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ICHANGE_FEATURE_COUNT = 5;

	/**
	 * The feature id for the '<em><b>Changes</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ICHANGE_SET__CHANGES = 0;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ICHANGE_SET__KIND = 1;

	/**
	 * The number of structural features of the '<em>IChange Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ICHANGE_SET_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.Change <em>Change</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.Change
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getChange()
	 * @generated
	 */
	public static final int CHANGE = 18;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IFile <em>IFile</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.IFile
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIFile()
	 * @generated
	 */
	public static final int IFILE = 11;

	/**
	 * The feature id for the '<em><b>Relative Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IFILE__RELATIVE_PATH = 0;

	/**
	 * The feature id for the '<em><b>Prev Revision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IFILE__PREV_REVISION = 1;

	/**
	 * The feature id for the '<em><b>Revision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IFILE__REVISION = 2;

	/**
	 * The feature id for the '<em><b>Dead</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IFILE__DEAD = 3;

	/**
	 * The feature id for the '<em><b>Edit Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IFILE__EDIT_TYPE = 4;

	/**
	 * The number of structural features of the '<em>IFile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IFILE_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.File <em>File</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.File
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getFile()
	 * @generated
	 */
	public static final int FILE = 20;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IUser <em>IUser</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.IUser
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIUser()
	 * @generated
	 */
	public static final int IUSER = 12;

	/**
	 * The feature id for the '<em><b>Fullname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IUSER__FULLNAME = 0;

	/**
	 * The feature id for the '<em><b>Username</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IUSER__USERNAME = 1;

	/**
	 * The feature id for the '<em><b>Email</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IUSER__EMAIL = 2;

	/**
	 * The number of structural features of the '<em>IUser</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int IUSER_FEATURE_COUNT = 3;

	/**
	 * The feature id for the '<em><b>Display Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ARTIFACT__DISPLAY_NAME = IARTIFACT__DISPLAY_NAME;

	/**
	 * The feature id for the '<em><b>Filename</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ARTIFACT__FILENAME = IARTIFACT__FILENAME;

	/**
	 * The feature id for the '<em><b>Relative Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ARTIFACT__RELATIVE_PATH = IARTIFACT__RELATIVE_PATH;

	/**
	 * The number of structural features of the '<em>Artifact</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int ARTIFACT_FEATURE_COUNT = IARTIFACT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__URL = IBUILD__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__NAME = IBUILD__NAME;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__ID = IBUILD__ID;

	/**
	 * The feature id for the '<em><b>Build Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__BUILD_NUMBER = IBUILD__BUILD_NUMBER;

	/**
	 * The feature id for the '<em><b>Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__TIMESTAMP = IBUILD__TIMESTAMP;

	/**
	 * The feature id for the '<em><b>Duration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__DURATION = IBUILD__DURATION;

	/**
	 * The feature id for the '<em><b>Display Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__DISPLAY_NAME = IBUILD__DISPLAY_NAME;

	/**
	 * The feature id for the '<em><b>State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__STATE = IBUILD__STATE;

	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__STATUS = IBUILD__STATUS;

	/**
	 * The feature id for the '<em><b>Artifacts</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__ARTIFACTS = IBUILD__ARTIFACTS;

	/**
	 * The feature id for the '<em><b>Change Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__CHANGE_SET = IBUILD__CHANGE_SET;

	/**
	 * The feature id for the '<em><b>Plan</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__PLAN = IBUILD__PLAN;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__LABEL = IBUILD__LABEL;

	/**
	 * The feature id for the '<em><b>Server</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD__SERVER = IBUILD__SERVER;

	/**
	 * The number of structural features of the '<em>Build</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_FEATURE_COUNT = IBUILD_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__URL = IBUILD_PLAN__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__NAME = IBUILD_PLAN__NAME;

	/**
	 * The feature id for the '<em><b>Server</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__SERVER = IBUILD_PLAN__SERVER;

	/**
	 * The feature id for the '<em><b>Children</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__CHILDREN = IBUILD_PLAN__CHILDREN;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__PARENT = IBUILD_PLAN__PARENT;

	/**
	 * The feature id for the '<em><b>Health</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__HEALTH = IBUILD_PLAN__HEALTH;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__ID = IBUILD_PLAN__ID;

	/**
	 * The feature id for the '<em><b>Info</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__INFO = IBUILD_PLAN__INFO;

	/**
	 * The feature id for the '<em><b>Selected</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__SELECTED = IBUILD_PLAN__SELECTED;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__SUMMARY = IBUILD_PLAN__SUMMARY;

	/**
	 * The feature id for the '<em><b>State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__STATE = IBUILD_PLAN__STATE;

	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__STATUS = IBUILD_PLAN__STATUS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__DESCRIPTION = IBUILD_PLAN__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Last Build</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__LAST_BUILD = IBUILD_PLAN__LAST_BUILD;

	/**
	 * The feature id for the '<em><b>Parameter Definitions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__PARAMETER_DEFINITIONS = IBUILD_PLAN__PARAMETER_DEFINITIONS;

	/**
	 * The number of structural features of the '<em>Plan</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN_FEATURE_COUNT = IBUILD_PLAN_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__URL = IBUILD_SERVER__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__NAME = IBUILD_SERVER__NAME;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__ATTRIBUTES = IBUILD_SERVER__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__LOCATION = IBUILD_SERVER__LOCATION;

	/**
	 * The feature id for the '<em><b>Connector Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__CONNECTOR_KIND = IBUILD_SERVER__CONNECTOR_KIND;

	/**
	 * The feature id for the '<em><b>Repository Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__REPOSITORY_URL = IBUILD_SERVER__REPOSITORY_URL;

	/**
	 * The number of structural features of the '<em>Server</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER_FEATURE_COUNT = IBUILD_SERVER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Servers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_MODEL__SERVERS = IBUILD_MODEL__SERVERS;

	/**
	 * The feature id for the '<em><b>Plans</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_MODEL__PLANS = IBUILD_MODEL__PLANS;

	/**
	 * The feature id for the '<em><b>Builds</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_MODEL__BUILDS = IBUILD_MODEL__BUILDS;

	/**
	 * The number of structural features of the '<em>Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_MODEL_FEATURE_COUNT = IBUILD_MODEL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Author</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__AUTHOR = ICHANGE__AUTHOR;

	/**
	 * The feature id for the '<em><b>File</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__FILE = ICHANGE__FILE;

	/**
	 * The feature id for the '<em><b>Message</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__MESSAGE = ICHANGE__MESSAGE;

	/**
	 * The feature id for the '<em><b>Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__DATE = ICHANGE__DATE;

	/**
	 * The feature id for the '<em><b>User</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__USER = ICHANGE__USER;

	/**
	 * The number of structural features of the '<em>Change</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_FEATURE_COUNT = ICHANGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Changes</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_SET__CHANGES = ICHANGE_SET__CHANGES;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_SET__KIND = ICHANGE_SET__KIND;

	/**
	 * The number of structural features of the '<em>Change Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_SET_FEATURE_COUNT = ICHANGE_SET_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Relative Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE__RELATIVE_PATH = IFILE__RELATIVE_PATH;

	/**
	 * The feature id for the '<em><b>Prev Revision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE__PREV_REVISION = IFILE__PREV_REVISION;

	/**
	 * The feature id for the '<em><b>Revision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE__REVISION = IFILE__REVISION;

	/**
	 * The feature id for the '<em><b>Dead</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE__DEAD = IFILE__DEAD;

	/**
	 * The feature id for the '<em><b>Edit Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE__EDIT_TYPE = IFILE__EDIT_TYPE;

	/**
	 * The number of structural features of the '<em>File</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_FEATURE_COUNT = IFILE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.User <em>User</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.User
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getUser()
	 * @generated
	 */
	public static final int USER = 22;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.StringToStringMap
	 * <em>String To String Map</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.StringToStringMap
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getStringToStringMap()
	 * @generated
	 */
	public static final int STRING_TO_STRING_MAP = 21;

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
	 * The feature id for the '<em><b>Fullname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER__FULLNAME = IUSER__FULLNAME;

	/**
	 * The feature id for the '<em><b>Username</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER__USERNAME = IUSER__USERNAME;

	/**
	 * The feature id for the '<em><b>Email</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER__EMAIL = IUSER__EMAIL;

	/**
	 * The number of structural features of the '<em>User</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER_FEATURE_COUNT = IUSER_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.ParameterDefinition
	 * <em>Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.ParameterDefinition
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getParameterDefinition()
	 * @generated
	 */
	public static final int PARAMETER_DEFINITION = 23;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.ChoiceParameterDefinition
	 * <em>Choice Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.ChoiceParameterDefinition
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getChoiceParameterDefinition()
	 * @generated
	 */
	public static final int CHOICE_PARAMETER_DEFINITION = 24;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.BooleanParameterDefinition
	 * <em>Boolean Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.BooleanParameterDefinition
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBooleanParameterDefinition()
	 * @generated
	 */
	public static final int BOOLEAN_PARAMETER_DEFINITION = 25;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.FileParameterDefinition
	 * <em>File Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.FileParameterDefinition
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getFileParameterDefinition()
	 * @generated
	 */
	public static final int FILE_PARAMETER_DEFINITION = 26;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.PlanParameterDefinition
	 * <em>Plan Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.PlanParameterDefinition
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getPlanParameterDefinition()
	 * @generated
	 */
	public static final int PLAN_PARAMETER_DEFINITION = 27;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.PasswordParameterDefinition
	 * <em>Password Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.PasswordParameterDefinition
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getPasswordParameterDefinition()
	 * @generated
	 */
	public static final int PASSWORD_PARAMETER_DEFINITION = 28;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.BuildParameterDefinition
	 * <em>Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.BuildParameterDefinition
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildParameterDefinition()
	 * @generated
	 */
	public static final int BUILD_PARAMETER_DEFINITION = 29;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.StringParameterDefinition
	 * <em>String Parameter Definition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.StringParameterDefinition
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getStringParameterDefinition()
	 * @generated
	 */
	public static final int STRING_PARAMETER_DEFINITION = 30;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.TestResult <em>Test Result</em>}'
	 * class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.TestResult
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestResult()
	 * @generated
	 */
	public static final int TEST_RESULT = 31;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.TestElement <em>Test Element</em>}'
	 * class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.TestElement
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestElement()
	 * @generated
	 */
	public static final int TEST_ELEMENT = 32;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.TestSuite <em>Test Suite</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.TestSuite
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestSuite()
	 * @generated
	 */
	public static final int TEST_SUITE = 33;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.TestCase <em>Test Case</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.TestCase
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestCase()
	 * @generated
	 */
	public static final int TEST_CASE = 34;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.TestCaseResult
	 * <em>Test Case Result</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.internal.builds.core.TestCaseResult
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestCaseResult()
	 * @generated
	 */
	public static final int TEST_CASE_RESULT = 35;

	/**
	 * The meta object id for the '<em>Repository Location</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.commons.repositories.RepositoryLocation
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getRepositoryLocation()
	 * @generated
	 */
	public static final int REPOSITORY_LOCATION = 36;

	/**
	 * The meta object id for the '<em>State</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.BuildState
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildState()
	 * @generated
	 */
	public static final int BUILD_STATE = 37;

	/**
	 * The meta object id for the '<em>Status</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.BuildStatus
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildStatus()
	 * @generated
	 */
	public static final int BUILD_STATUS = 38;

	/**
	 * The meta object id for the '<em>Edit Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.builds.core.EditType
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getEditType()
	 * @generated
	 */
	public static final int EDIT_TYPE = 39;

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
	private EClass buildServerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass iBuildModelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass iBuildElementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass iBuildPlanEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass iBuildWorkingCopyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass iBuildPlanDataEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass iBuildPlanWorkingCopyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass iBuildServerEClass = null;

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
	private EClass changeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass fileEClass = null;

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
	private EClass iArtifactEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass iBuildEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass iChangeSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass iChangeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass iFileEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass iUserEClass = null;

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
	 * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
	 * EPackage.Registry} by the package
	 * package URI value.
	 * <p>
	 * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
	 * performs initialization of the package, or returns the registered package, if one already exists. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private BuildPackage() {
		super(eNS_URI, BuildFactory.eINSTANCE);
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
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.BuildModel <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Model</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildModel
	 * @generated
	 */
	public EClass getBuildModel() {
		return buildModelEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan <em>Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Plan</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPlan
	 * @generated
	 */
	public EClass getBuildPlan() {
		return buildPlanEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.BuildServer <em>Server</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Server</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildServer
	 * @generated
	 */
	public EClass getBuildServer() {
		return buildServerEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildModel <em>IBuild Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>IBuild Model</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildModel
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IBuildModel"
	 * @generated
	 */
	public EClass getIBuildModel() {
		return iBuildModelEClass;
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.builds.core.IBuildModel#getServers <em>Servers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Servers</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildModel#getServers()
	 * @see #getIBuildModel()
	 * @generated
	 */
	public EReference getIBuildModel_Servers() {
		return (EReference) iBuildModelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.builds.core.IBuildModel#getPlans <em>Plans</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Plans</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildModel#getPlans()
	 * @see #getIBuildModel()
	 * @generated
	 */
	public EReference getIBuildModel_Plans() {
		return (EReference) iBuildModelEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.builds.core.IBuildModel#getBuilds <em>Builds</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Builds</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildModel#getBuilds()
	 * @see #getIBuildModel()
	 * @generated
	 */
	public EReference getIBuildModel_Builds() {
		return (EReference) iBuildModelEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildElement <em>IBuild Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>IBuild Element</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildElement
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IBuildElement"
	 * @generated
	 */
	public EClass getIBuildElement() {
		return iBuildElementEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildElement#getUrl
	 * <em>Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Url</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildElement#getUrl()
	 * @see #getIBuildElement()
	 * @generated
	 */
	public EAttribute getIBuildElement_Url() {
		return (EAttribute) iBuildElementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildElement#getName
	 * <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildElement#getName()
	 * @see #getIBuildElement()
	 * @generated
	 */
	public EAttribute getIBuildElement_Name() {
		return (EAttribute) iBuildElementEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildPlan <em>IBuild Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>IBuild Plan</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IBuildPlan"
	 *        superTypes="org.eclipse.mylyn.internal.builds.core.IBuildElement"
	 * @generated
	 */
	public EClass getIBuildPlan() {
		return iBuildPlanEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getServer
	 * <em>Server</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Server</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getServer()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EReference getIBuildPlan_Server() {
		return (EReference) iBuildPlanEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getChildren
	 * <em>Children</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Children</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getChildren()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EReference getIBuildPlan_Children() {
		return (EReference) iBuildPlanEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getParent
	 * <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Parent</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getParent()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EReference getIBuildPlan_Parent() {
		return (EReference) iBuildPlanEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getHealth
	 * <em>Health</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Health</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getHealth()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EAttribute getIBuildPlan_Health() {
		return (EAttribute) iBuildPlanEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getId()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EAttribute getIBuildPlan_Id() {
		return (EAttribute) iBuildPlanEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getInfo <em>Info</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Info</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getInfo()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EAttribute getIBuildPlan_Info() {
		return (EAttribute) iBuildPlanEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#isSelected
	 * <em>Selected</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Selected</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#isSelected()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EAttribute getIBuildPlan_Selected() {
		return (EAttribute) iBuildPlanEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getSummary
	 * <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getSummary()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EAttribute getIBuildPlan_Summary() {
		return (EAttribute) iBuildPlanEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getState
	 * <em>State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>State</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getState()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EAttribute getIBuildPlan_State() {
		return (EAttribute) iBuildPlanEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getStatus
	 * <em>Status</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Status</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getStatus()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EAttribute getIBuildPlan_Status() {
		return (EAttribute) iBuildPlanEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getDescription
	 * <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getDescription()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EAttribute getIBuildPlan_Description() {
		return (EAttribute) iBuildPlanEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getLastBuild
	 * <em>Last Build</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Last Build</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getLastBuild()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EReference getIBuildPlan_LastBuild() {
		return (EReference) iBuildPlanEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.builds.core.IBuildPlan#getParameterDefinitions <em>Parameter Definitions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Parameter Definitions</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getParameterDefinitions()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EReference getIBuildPlan_ParameterDefinitions() {
		return (EReference) iBuildPlanEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildWorkingCopy
	 * <em>IBuild Working Copy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>IBuild Working Copy</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildWorkingCopy
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IBuildWorkingCopy"
	 *        superTypes="org.eclipse.mylyn.internal.builds.core.IBuild"
	 * @generated
	 */
	public EClass getIBuildWorkingCopy() {
		return iBuildWorkingCopyEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildPlanData <em>IBuild Plan Data</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>IBuild Plan Data</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlanData
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IBuildPlanData"
	 * @generated
	 */
	public EClass getIBuildPlanData() {
		return iBuildPlanDataEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy
	 * <em>IBuild Plan Working Copy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>IBuild Plan Working Copy</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy"
	 *        superTypes="org.eclipse.mylyn.internal.builds.core.IBuildPlanData"
	 * @generated
	 */
	public EClass getIBuildPlanWorkingCopy() {
		return iBuildPlanWorkingCopyEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildServer <em>IBuild Server</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>IBuild Server</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IBuildServer"
	 *        superTypes="org.eclipse.mylyn.internal.builds.core.IBuildElement"
	 * @generated
	 */
	public EClass getIBuildServer() {
		return iBuildServerEClass;
	}

	/**
	 * Returns the meta object for the map '{@link org.eclipse.mylyn.builds.core.IBuildServer#getAttributes
	 * <em>Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the map '<em>Attributes</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer#getAttributes()
	 * @see #getIBuildServer()
	 * @generated
	 */
	public EReference getIBuildServer_Attributes() {
		return (EReference) iBuildServerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildServer#getLocation
	 * <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Location</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer#getLocation()
	 * @see #getIBuildServer()
	 * @generated
	 */
	public EAttribute getIBuildServer_Location() {
		return (EAttribute) iBuildServerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildServer#getConnectorKind
	 * <em>Connector Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Connector Kind</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer#getConnectorKind()
	 * @see #getIBuildServer()
	 * @generated
	 */
	public EAttribute getIBuildServer_ConnectorKind() {
		return (EAttribute) iBuildServerEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildServer#getRepositoryUrl
	 * <em>Repository Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Repository Url</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer#getRepositoryUrl()
	 * @see #getIBuildServer()
	 * @generated
	 */
	public EAttribute getIBuildServer_RepositoryUrl() {
		return (EAttribute) iBuildServerEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.Artifact <em>Artifact</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Artifact</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.Artifact
	 * @generated
	 */
	public EClass getArtifact() {
		return artifactEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.Build <em>Build</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Build</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.Build
	 * @generated
	 */
	public EClass getBuild() {
		return buildEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.ChangeSet <em>Change Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Change Set</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.ChangeSet
	 * @generated
	 */
	public EClass getChangeSet() {
		return changeSetEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.Change <em>Change</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Change</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.Change
	 * @generated
	 */
	public EClass getChange() {
		return changeEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.File <em>File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>File</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.File
	 * @generated
	 */
	public EClass getFile() {
		return fileEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.User <em>User</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>User</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.User
	 * @generated
	 */
	public EClass getUser() {
		return userEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.ParameterDefinition
	 * <em>Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.ParameterDefinition
	 * @generated
	 */
	public EClass getParameterDefinition() {
		return parameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.internal.builds.core.ParameterDefinition#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.ParameterDefinition#getName()
	 * @see #getParameterDefinition()
	 * @generated
	 */
	public EAttribute getParameterDefinition_Name() {
		return (EAttribute) parameterDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.internal.builds.core.ParameterDefinition#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.ParameterDefinition#getDescription()
	 * @see #getParameterDefinition()
	 * @generated
	 */
	public EAttribute getParameterDefinition_Description() {
		return (EAttribute) parameterDefinitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the container reference '
	 * {@link org.eclipse.mylyn.internal.builds.core.ParameterDefinition#getContainingBuildPlan
	 * <em>Containing Build Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the container reference '<em>Containing Build Plan</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.ParameterDefinition#getContainingBuildPlan()
	 * @see #getParameterDefinition()
	 * @generated
	 */
	public EReference getParameterDefinition_ContainingBuildPlan() {
		return (EReference) parameterDefinitionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.ChoiceParameterDefinition
	 * <em>Choice Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Choice Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.ChoiceParameterDefinition
	 * @generated
	 */
	public EClass getChoiceParameterDefinition() {
		return choiceParameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for the attribute list '
	 * {@link org.eclipse.mylyn.internal.builds.core.ChoiceParameterDefinition#getOptions <em>Options</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute list '<em>Options</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.ChoiceParameterDefinition#getOptions()
	 * @see #getChoiceParameterDefinition()
	 * @generated
	 */
	public EAttribute getChoiceParameterDefinition_Options() {
		return (EAttribute) choiceParameterDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.BooleanParameterDefinition
	 * <em>Boolean Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Boolean Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.BooleanParameterDefinition
	 * @generated
	 */
	public EClass getBooleanParameterDefinition() {
		return booleanParameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.internal.builds.core.BooleanParameterDefinition#isDefaultValue <em>Default Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Default Value</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.BooleanParameterDefinition#isDefaultValue()
	 * @see #getBooleanParameterDefinition()
	 * @generated
	 */
	public EAttribute getBooleanParameterDefinition_DefaultValue() {
		return (EAttribute) booleanParameterDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.FileParameterDefinition
	 * <em>File Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>File Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.FileParameterDefinition
	 * @generated
	 */
	public EClass getFileParameterDefinition() {
		return fileParameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.PlanParameterDefinition
	 * <em>Plan Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Plan Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.PlanParameterDefinition
	 * @generated
	 */
	public EClass getPlanParameterDefinition() {
		return planParameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.PasswordParameterDefinition
	 * <em>Password Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Password Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.PasswordParameterDefinition
	 * @generated
	 */
	public EClass getPasswordParameterDefinition() {
		return passwordParameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.internal.builds.core.PasswordParameterDefinition#getDefaultValue <em>Default Value</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Default Value</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.PasswordParameterDefinition#getDefaultValue()
	 * @see #getPasswordParameterDefinition()
	 * @generated
	 */
	public EAttribute getPasswordParameterDefinition_DefaultValue() {
		return (EAttribute) passwordParameterDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.BuildParameterDefinition
	 * <em>Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildParameterDefinition
	 * @generated
	 */
	public EClass getBuildParameterDefinition() {
		return buildParameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.internal.builds.core.BuildParameterDefinition#getBuildPlanId <em>Build Plan Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Build Plan Id</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildParameterDefinition#getBuildPlanId()
	 * @see #getBuildParameterDefinition()
	 * @generated
	 */
	public EAttribute getBuildParameterDefinition_BuildPlanId() {
		return (EAttribute) buildParameterDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the reference '
	 * {@link org.eclipse.mylyn.internal.builds.core.BuildParameterDefinition#getBuildPlan <em>Build Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Build Plan</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildParameterDefinition#getBuildPlan()
	 * @see #getBuildParameterDefinition()
	 * @generated
	 */
	public EReference getBuildParameterDefinition_BuildPlan() {
		return (EReference) buildParameterDefinitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.StringParameterDefinition
	 * <em>String Parameter Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>String Parameter Definition</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.StringParameterDefinition
	 * @generated
	 */
	public EClass getStringParameterDefinition() {
		return stringParameterDefinitionEClass;
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.internal.builds.core.StringParameterDefinition#getDefaultValue <em>Default Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Default Value</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.StringParameterDefinition#getDefaultValue()
	 * @see #getStringParameterDefinition()
	 * @generated
	 */
	public EAttribute getStringParameterDefinition_DefaultValue() {
		return (EAttribute) stringParameterDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.TestResult <em>Test Result</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Test Result</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestResult
	 * @generated
	 */
	public EClass getTestResult() {
		return testResultEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.internal.builds.core.TestResult#getBuild
	 * <em>Build</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Build</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestResult#getBuild()
	 * @see #getTestResult()
	 * @generated
	 */
	public EReference getTestResult_Build() {
		return (EReference) testResultEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.internal.builds.core.TestResult#getDuration
	 * <em>Duration</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Duration</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestResult#getDuration()
	 * @see #getTestResult()
	 * @generated
	 */
	public EAttribute getTestResult_Duration() {
		return (EAttribute) testResultEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.internal.builds.core.TestResult#getFailCount
	 * <em>Fail Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Fail Count</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestResult#getFailCount()
	 * @see #getTestResult()
	 * @generated
	 */
	public EAttribute getTestResult_FailCount() {
		return (EAttribute) testResultEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.internal.builds.core.TestResult#getPassCount
	 * <em>Pass Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Pass Count</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestResult#getPassCount()
	 * @see #getTestResult()
	 * @generated
	 */
	public EAttribute getTestResult_PassCount() {
		return (EAttribute) testResultEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.internal.builds.core.TestResult#getSuites <em>Suites</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Suites</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestResult#getSuites()
	 * @see #getTestResult()
	 * @generated
	 */
	public EReference getTestResult_Suites() {
		return (EReference) testResultEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.TestElement
	 * <em>Test Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Test Element</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestElement
	 * @generated
	 */
	public EClass getTestElement() {
		return testElementEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.internal.builds.core.TestElement#getLabel
	 * <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestElement#getLabel()
	 * @see #getTestElement()
	 * @generated
	 */
	public EAttribute getTestElement_Label() {
		return (EAttribute) testElementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.internal.builds.core.TestElement#getDuration
	 * <em>Duration</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Duration</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestElement#getDuration()
	 * @see #getTestElement()
	 * @generated
	 */
	public EAttribute getTestElement_Duration() {
		return (EAttribute) testElementEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.internal.builds.core.TestElement#getErrorOutput <em>Error Output</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Error Output</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestElement#getErrorOutput()
	 * @see #getTestElement()
	 * @generated
	 */
	public EAttribute getTestElement_ErrorOutput() {
		return (EAttribute) testElementEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.internal.builds.core.TestElement#getOutput
	 * <em>Output</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Output</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestElement#getOutput()
	 * @see #getTestElement()
	 * @generated
	 */
	public EAttribute getTestElement_Output() {
		return (EAttribute) testElementEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.TestSuite <em>Test Suite</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Test Suite</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestSuite
	 * @generated
	 */
	public EClass getTestSuite() {
		return testSuiteEClass;
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.internal.builds.core.TestSuite#getCases <em>Cases</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Cases</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestSuite#getCases()
	 * @see #getTestSuite()
	 * @generated
	 */
	public EReference getTestSuite_Cases() {
		return (EReference) testSuiteEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the container reference '
	 * {@link org.eclipse.mylyn.internal.builds.core.TestSuite#getResult <em>Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the container reference '<em>Result</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestSuite#getResult()
	 * @see #getTestSuite()
	 * @generated
	 */
	public EReference getTestSuite_Result() {
		return (EReference) testSuiteEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.internal.builds.core.TestCase <em>Test Case</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Test Case</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestCase
	 * @generated
	 */
	public EClass getTestCase() {
		return testCaseEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.internal.builds.core.TestCase#getClassName
	 * <em>Class Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Class Name</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestCase#getClassName()
	 * @see #getTestCase()
	 * @generated
	 */
	public EAttribute getTestCase_ClassName() {
		return (EAttribute) testCaseEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.internal.builds.core.TestCase#isSkipped
	 * <em>Skipped</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Skipped</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestCase#isSkipped()
	 * @see #getTestCase()
	 * @generated
	 */
	public EAttribute getTestCase_Skipped() {
		return (EAttribute) testCaseEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the container reference '
	 * {@link org.eclipse.mylyn.internal.builds.core.TestCase#getSuite <em>Suite</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the container reference '<em>Suite</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestCase#getSuite()
	 * @see #getTestCase()
	 * @generated
	 */
	public EReference getTestCase_Suite() {
		return (EReference) testCaseEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.internal.builds.core.TestCase#getStatus
	 * <em>Status</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Status</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestCase#getStatus()
	 * @see #getTestCase()
	 * @generated
	 */
	public EAttribute getTestCase_Status() {
		return (EAttribute) testCaseEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for enum '{@link org.eclipse.mylyn.internal.builds.core.TestCaseResult
	 * <em>Test Case Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for enum '<em>Test Case Result</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.TestCaseResult
	 * @generated
	 */
	public EEnum getTestCaseResult() {
		return testCaseResultEEnum;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IArtifact <em>IArtifact</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>IArtifact</em>'.
	 * @see org.eclipse.mylyn.builds.core.IArtifact
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IArtifact"
	 * @generated
	 */
	public EClass getIArtifact() {
		return iArtifactEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IArtifact#getDisplayName
	 * <em>Display Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Display Name</em>'.
	 * @see org.eclipse.mylyn.builds.core.IArtifact#getDisplayName()
	 * @see #getIArtifact()
	 * @generated
	 */
	public EAttribute getIArtifact_DisplayName() {
		return (EAttribute) iArtifactEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IArtifact#getFilename
	 * <em>Filename</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Filename</em>'.
	 * @see org.eclipse.mylyn.builds.core.IArtifact#getFilename()
	 * @see #getIArtifact()
	 * @generated
	 */
	public EAttribute getIArtifact_Filename() {
		return (EAttribute) iArtifactEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IArtifact#getRelativePath
	 * <em>Relative Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Relative Path</em>'.
	 * @see org.eclipse.mylyn.builds.core.IArtifact#getRelativePath()
	 * @see #getIArtifact()
	 * @generated
	 */
	public EAttribute getIArtifact_RelativePath() {
		return (EAttribute) iArtifactEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuild <em>IBuild</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>IBuild</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IBuild"
	 *        superTypes="org.eclipse.mylyn.internal.builds.core.IBuildElement"
	 * @generated
	 */
	public EClass getIBuild() {
		return iBuildEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getId()
	 * @see #getIBuild()
	 * @generated
	 */
	public EAttribute getIBuild_Id() {
		return (EAttribute) iBuildEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getBuildNumber
	 * <em>Build Number</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Build Number</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getBuildNumber()
	 * @see #getIBuild()
	 * @generated
	 */
	public EAttribute getIBuild_BuildNumber() {
		return (EAttribute) iBuildEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getTimestamp
	 * <em>Timestamp</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Timestamp</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getTimestamp()
	 * @see #getIBuild()
	 * @generated
	 */
	public EAttribute getIBuild_Timestamp() {
		return (EAttribute) iBuildEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getDuration
	 * <em>Duration</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Duration</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getDuration()
	 * @see #getIBuild()
	 * @generated
	 */
	public EAttribute getIBuild_Duration() {
		return (EAttribute) iBuildEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getDisplayName
	 * <em>Display Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Display Name</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getDisplayName()
	 * @see #getIBuild()
	 * @generated
	 */
	public EAttribute getIBuild_DisplayName() {
		return (EAttribute) iBuildEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getState <em>State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>State</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getState()
	 * @see #getIBuild()
	 * @generated
	 */
	public EAttribute getIBuild_State() {
		return (EAttribute) iBuildEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getStatus <em>Status</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Status</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getStatus()
	 * @see #getIBuild()
	 * @generated
	 */
	public EAttribute getIBuild_Status() {
		return (EAttribute) iBuildEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.mylyn.builds.core.IBuild#getArtifacts
	 * <em>Artifacts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Artifacts</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getArtifacts()
	 * @see #getIBuild()
	 * @generated
	 */
	public EReference getIBuild_Artifacts() {
		return (EReference) iBuildEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.IBuild#getChangeSet
	 * <em>Change Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Change Set</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getChangeSet()
	 * @see #getIBuild()
	 * @generated
	 */
	public EReference getIBuild_ChangeSet() {
		return (EReference) iBuildEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.IBuild#getPlan <em>Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Plan</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getPlan()
	 * @see #getIBuild()
	 * @generated
	 */
	public EReference getIBuild_Plan() {
		return (EReference) iBuildEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuild#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getLabel()
	 * @see #getIBuild()
	 * @generated
	 */
	public EAttribute getIBuild_Label() {
		return (EAttribute) iBuildEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.IBuild#getServer <em>Server</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Server</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuild#getServer()
	 * @see #getIBuild()
	 * @generated
	 */
	public EReference getIBuild_Server() {
		return (EReference) iBuildEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IChangeSet <em>IChange Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>IChange Set</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChangeSet
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IChangeSet"
	 * @generated
	 */
	public EClass getIChangeSet() {
		return iChangeSetEClass;
	}

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.mylyn.builds.core.IChangeSet#getChanges
	 * <em>Changes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Changes</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChangeSet#getChanges()
	 * @see #getIChangeSet()
	 * @generated
	 */
	public EReference getIChangeSet_Changes() {
		return (EReference) iChangeSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IChangeSet#getKind <em>Kind</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChangeSet#getKind()
	 * @see #getIChangeSet()
	 * @generated
	 */
	public EAttribute getIChangeSet_Kind() {
		return (EAttribute) iChangeSetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IChange <em>IChange</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>IChange</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChange
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IChange"
	 * @generated
	 */
	public EClass getIChange() {
		return iChangeEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.IChange#getAuthor
	 * <em>Author</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Author</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChange#getAuthor()
	 * @see #getIChange()
	 * @generated
	 */
	public EReference getIChange_Author() {
		return (EReference) iChangeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.IChange#getFile <em>File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>File</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChange#getFile()
	 * @see #getIChange()
	 * @generated
	 */
	public EReference getIChange_File() {
		return (EReference) iChangeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IChange#getMessage
	 * <em>Message</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Message</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChange#getMessage()
	 * @see #getIChange()
	 * @generated
	 */
	public EAttribute getIChange_Message() {
		return (EAttribute) iChangeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IChange#getDate <em>Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Date</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChange#getDate()
	 * @see #getIChange()
	 * @generated
	 */
	public EAttribute getIChange_Date() {
		return (EAttribute) iChangeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.IChange#getUser <em>User</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>User</em>'.
	 * @see org.eclipse.mylyn.builds.core.IChange#getUser()
	 * @see #getIChange()
	 * @generated
	 */
	public EReference getIChange_User() {
		return (EReference) iChangeEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IFile <em>IFile</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>IFile</em>'.
	 * @see org.eclipse.mylyn.builds.core.IFile
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IFile"
	 * @generated
	 */
	public EClass getIFile() {
		return iFileEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IFile#getRelativePath
	 * <em>Relative Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Relative Path</em>'.
	 * @see org.eclipse.mylyn.builds.core.IFile#getRelativePath()
	 * @see #getIFile()
	 * @generated
	 */
	public EAttribute getIFile_RelativePath() {
		return (EAttribute) iFileEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IFile#getPrevRevision
	 * <em>Prev Revision</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Prev Revision</em>'.
	 * @see org.eclipse.mylyn.builds.core.IFile#getPrevRevision()
	 * @see #getIFile()
	 * @generated
	 */
	public EAttribute getIFile_PrevRevision() {
		return (EAttribute) iFileEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IFile#getRevision
	 * <em>Revision</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Revision</em>'.
	 * @see org.eclipse.mylyn.builds.core.IFile#getRevision()
	 * @see #getIFile()
	 * @generated
	 */
	public EAttribute getIFile_Revision() {
		return (EAttribute) iFileEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IFile#isDead <em>Dead</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Dead</em>'.
	 * @see org.eclipse.mylyn.builds.core.IFile#isDead()
	 * @see #getIFile()
	 * @generated
	 */
	public EAttribute getIFile_Dead() {
		return (EAttribute) iFileEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IFile#getEditType
	 * <em>Edit Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Edit Type</em>'.
	 * @see org.eclipse.mylyn.builds.core.IFile#getEditType()
	 * @see #getIFile()
	 * @generated
	 */
	public EAttribute getIFile_EditType() {
		return (EAttribute) iFileEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IUser <em>IUser</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>IUser</em>'.
	 * @see org.eclipse.mylyn.builds.core.IUser
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IUser"
	 * @generated
	 */
	public EClass getIUser() {
		return iUserEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IUser#getFullname
	 * <em>Fullname</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Fullname</em>'.
	 * @see org.eclipse.mylyn.builds.core.IUser#getFullname()
	 * @see #getIUser()
	 * @generated
	 */
	public EAttribute getIUser_Fullname() {
		return (EAttribute) iUserEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IUser#getUsername
	 * <em>Username</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Username</em>'.
	 * @see org.eclipse.mylyn.builds.core.IUser#getUsername()
	 * @see #getIUser()
	 * @generated
	 */
	public EAttribute getIUser_Username() {
		return (EAttribute) iUserEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IUser#getEmail <em>Email</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Email</em>'.
	 * @see org.eclipse.mylyn.builds.core.IUser#getEmail()
	 * @see #getIUser()
	 * @generated
	 */
	public EAttribute getIUser_Email() {
		return (EAttribute) iUserEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>String To String Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>String To String Map</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueDefault="" valueDataType="org.eclipse.emf.ecore.EString"
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
	 * Returns the meta object for data type '{@link org.eclipse.mylyn.commons.repositories.RepositoryLocation
	 * <em>Repository Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for data type '<em>Repository Location</em>'.
	 * @see org.eclipse.mylyn.commons.repositories.RepositoryLocation
	 * @model instanceClass="org.eclipse.mylyn.commons.repositories.RepositoryLocation" serializeable="false"
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
	 * @model instanceClass="org.eclipse.mylyn.builds.core.BuildState"
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
	 * @model instanceClass="org.eclipse.mylyn.builds.core.BuildStatus"
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
	 * @model instanceClass="org.eclipse.mylyn.builds.core.EditType"
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
	public BuildFactory getBuildFactory() {
		return (BuildFactory) getEFactoryInstance();
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
		iBuildModelEClass = createEClass(IBUILD_MODEL);
		createEReference(iBuildModelEClass, IBUILD_MODEL__SERVERS);
		createEReference(iBuildModelEClass, IBUILD_MODEL__PLANS);
		createEReference(iBuildModelEClass, IBUILD_MODEL__BUILDS);

		iBuildElementEClass = createEClass(IBUILD_ELEMENT);
		createEAttribute(iBuildElementEClass, IBUILD_ELEMENT__URL);
		createEAttribute(iBuildElementEClass, IBUILD_ELEMENT__NAME);

		iBuildServerEClass = createEClass(IBUILD_SERVER);
		createEReference(iBuildServerEClass, IBUILD_SERVER__ATTRIBUTES);
		createEAttribute(iBuildServerEClass, IBUILD_SERVER__LOCATION);
		createEAttribute(iBuildServerEClass, IBUILD_SERVER__CONNECTOR_KIND);
		createEAttribute(iBuildServerEClass, IBUILD_SERVER__REPOSITORY_URL);

		iBuildPlanEClass = createEClass(IBUILD_PLAN);
		createEReference(iBuildPlanEClass, IBUILD_PLAN__SERVER);
		createEReference(iBuildPlanEClass, IBUILD_PLAN__CHILDREN);
		createEReference(iBuildPlanEClass, IBUILD_PLAN__PARENT);
		createEAttribute(iBuildPlanEClass, IBUILD_PLAN__HEALTH);
		createEAttribute(iBuildPlanEClass, IBUILD_PLAN__ID);
		createEAttribute(iBuildPlanEClass, IBUILD_PLAN__INFO);
		createEAttribute(iBuildPlanEClass, IBUILD_PLAN__SELECTED);
		createEAttribute(iBuildPlanEClass, IBUILD_PLAN__SUMMARY);
		createEAttribute(iBuildPlanEClass, IBUILD_PLAN__STATE);
		createEAttribute(iBuildPlanEClass, IBUILD_PLAN__STATUS);
		createEAttribute(iBuildPlanEClass, IBUILD_PLAN__DESCRIPTION);
		createEReference(iBuildPlanEClass, IBUILD_PLAN__LAST_BUILD);
		createEReference(iBuildPlanEClass, IBUILD_PLAN__PARAMETER_DEFINITIONS);

		iBuildPlanDataEClass = createEClass(IBUILD_PLAN_DATA);

		iBuildPlanWorkingCopyEClass = createEClass(IBUILD_PLAN_WORKING_COPY);

		iBuildEClass = createEClass(IBUILD);
		createEAttribute(iBuildEClass, IBUILD__ID);
		createEAttribute(iBuildEClass, IBUILD__BUILD_NUMBER);
		createEAttribute(iBuildEClass, IBUILD__TIMESTAMP);
		createEAttribute(iBuildEClass, IBUILD__DURATION);
		createEAttribute(iBuildEClass, IBUILD__DISPLAY_NAME);
		createEAttribute(iBuildEClass, IBUILD__STATE);
		createEAttribute(iBuildEClass, IBUILD__STATUS);
		createEReference(iBuildEClass, IBUILD__ARTIFACTS);
		createEReference(iBuildEClass, IBUILD__CHANGE_SET);
		createEReference(iBuildEClass, IBUILD__PLAN);
		createEAttribute(iBuildEClass, IBUILD__LABEL);
		createEReference(iBuildEClass, IBUILD__SERVER);

		iBuildWorkingCopyEClass = createEClass(IBUILD_WORKING_COPY);

		iArtifactEClass = createEClass(IARTIFACT);
		createEAttribute(iArtifactEClass, IARTIFACT__DISPLAY_NAME);
		createEAttribute(iArtifactEClass, IARTIFACT__FILENAME);
		createEAttribute(iArtifactEClass, IARTIFACT__RELATIVE_PATH);

		iChangeEClass = createEClass(ICHANGE);
		createEReference(iChangeEClass, ICHANGE__AUTHOR);
		createEReference(iChangeEClass, ICHANGE__FILE);
		createEAttribute(iChangeEClass, ICHANGE__MESSAGE);
		createEAttribute(iChangeEClass, ICHANGE__DATE);
		createEReference(iChangeEClass, ICHANGE__USER);

		iChangeSetEClass = createEClass(ICHANGE_SET);
		createEReference(iChangeSetEClass, ICHANGE_SET__CHANGES);
		createEAttribute(iChangeSetEClass, ICHANGE_SET__KIND);

		iFileEClass = createEClass(IFILE);
		createEAttribute(iFileEClass, IFILE__RELATIVE_PATH);
		createEAttribute(iFileEClass, IFILE__PREV_REVISION);
		createEAttribute(iFileEClass, IFILE__REVISION);
		createEAttribute(iFileEClass, IFILE__DEAD);
		createEAttribute(iFileEClass, IFILE__EDIT_TYPE);

		iUserEClass = createEClass(IUSER);
		createEAttribute(iUserEClass, IUSER__FULLNAME);
		createEAttribute(iUserEClass, IUSER__USERNAME);
		createEAttribute(iUserEClass, IUSER__EMAIL);

		artifactEClass = createEClass(ARTIFACT);

		buildEClass = createEClass(BUILD);

		buildPlanEClass = createEClass(BUILD_PLAN);

		buildServerEClass = createEClass(BUILD_SERVER);

		buildModelEClass = createEClass(BUILD_MODEL);

		changeEClass = createEClass(CHANGE);

		changeSetEClass = createEClass(CHANGE_SET);

		fileEClass = createEClass(FILE);

		stringToStringMapEClass = createEClass(STRING_TO_STRING_MAP);
		createEAttribute(stringToStringMapEClass, STRING_TO_STRING_MAP__KEY);
		createEAttribute(stringToStringMapEClass, STRING_TO_STRING_MAP__VALUE);

		userEClass = createEClass(USER);

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
		repositoryLocationEDataType = createEDataType(REPOSITORY_LOCATION);
		buildStateEDataType = createEDataType(BUILD_STATE);
		buildStatusEDataType = createEDataType(BUILD_STATUS);
		editTypeEDataType = createEDataType(EDIT_TYPE);
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
		iBuildServerEClass.getESuperTypes().add(this.getIBuildElement());
		iBuildPlanEClass.getESuperTypes().add(this.getIBuildElement());
		iBuildPlanWorkingCopyEClass.getESuperTypes().add(this.getIBuildPlanData());
		iBuildEClass.getESuperTypes().add(this.getIBuildElement());
		iBuildWorkingCopyEClass.getESuperTypes().add(this.getIBuild());
		artifactEClass.getESuperTypes().add(this.getIArtifact());
		buildEClass.getESuperTypes().add(this.getIBuild());
		buildEClass.getESuperTypes().add(this.getIBuildWorkingCopy());
		buildPlanEClass.getESuperTypes().add(this.getIBuildPlan());
		buildPlanEClass.getESuperTypes().add(this.getIBuildPlanWorkingCopy());
		buildServerEClass.getESuperTypes().add(this.getIBuildServer());
		buildModelEClass.getESuperTypes().add(this.getIBuildModel());
		changeEClass.getESuperTypes().add(this.getIChange());
		changeSetEClass.getESuperTypes().add(this.getIChangeSet());
		fileEClass.getESuperTypes().add(this.getIFile());
		userEClass.getESuperTypes().add(this.getIUser());
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
		initEClass(iBuildModelEClass, IBuildModel.class,
				"IBuildModel", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getIBuildModel_Servers(),
				this.getIBuildServer(),
				null,
				"servers", null, 0, -1, IBuildModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getIBuildModel_Plans(),
				this.getIBuildPlan(),
				null,
				"plans", null, 0, -1, IBuildModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getIBuildModel_Builds(),
				this.getIBuild(),
				null,
				"builds", null, 0, -1, IBuildModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(iBuildElementEClass, IBuildElement.class,
				"IBuildElement", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getIBuildElement_Url(),
				ecorePackage.getEString(),
				"url", null, 0, 1, IBuildElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuildElement_Name(),
				ecorePackage.getEString(),
				"name", null, 0, 1, IBuildElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(iBuildServerEClass, IBuildServer.class,
				"IBuildServer", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getIBuildServer_Attributes(),
				this.getStringToStringMap(),
				null,
				"attributes", null, 0, -1, IBuildServer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuildServer_Location(),
				this.getRepositoryLocation(),
				"location", null, 0, 1, IBuildServer.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuildServer_ConnectorKind(),
				ecorePackage.getEString(),
				"connectorKind", null, 0, 1, IBuildServer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuildServer_RepositoryUrl(),
				ecorePackage.getEString(),
				"repositoryUrl", null, 0, 1, IBuildServer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(iBuildPlanEClass, IBuildPlan.class,
				"IBuildPlan", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getIBuildPlan_Server(),
				this.getIBuildServer(),
				null,
				"server", null, 1, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getIBuildPlan_Children(),
				this.getIBuildPlan(),
				this.getIBuildPlan_Parent(),
				"children", null, 0, -1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getIBuildPlan_Parent(),
				this.getIBuildPlan(),
				this.getIBuildPlan_Children(),
				"parent", null, 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuildPlan_Health(),
				ecorePackage.getEInt(),
				"health", "-1", 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(
				getIBuildPlan_Id(),
				ecorePackage.getEString(),
				"id", null, 1, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuildPlan_Info(),
				ecorePackage.getEString(),
				"info", null, 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuildPlan_Selected(),
				ecorePackage.getEBoolean(),
				"selected", "false", 1, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(
				getIBuildPlan_Summary(),
				ecorePackage.getEString(),
				"summary", null, 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuildPlan_State(),
				this.getBuildState(),
				"state", null, 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuildPlan_Status(),
				this.getBuildStatus(),
				"status", null, 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuildPlan_Description(),
				ecorePackage.getEString(),
				"description", null, 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getIBuildPlan_LastBuild(),
				this.getIBuild(),
				null,
				"lastBuild", null, 0, 1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getIBuildPlan_ParameterDefinitions(),
				this.getParameterDefinition(),
				this.getParameterDefinition_ContainingBuildPlan(),
				"parameterDefinitions", null, 0, -1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(iBuildPlanDataEClass, IBuildPlanData.class,
				"IBuildPlanData", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(iBuildPlanWorkingCopyEClass, IBuildPlanWorkingCopy.class,
				"IBuildPlanWorkingCopy", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(iBuildEClass, IBuild.class, "IBuild", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getIBuild_Id(),
				ecorePackage.getEString(),
				"id", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuild_BuildNumber(),
				ecorePackage.getEInt(),
				"buildNumber", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuild_Timestamp(),
				ecorePackage.getELong(),
				"timestamp", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuild_Duration(),
				ecorePackage.getELong(),
				"duration", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuild_DisplayName(),
				ecorePackage.getEString(),
				"displayName", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuild_State(),
				this.getBuildState(),
				"state", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuild_Status(),
				this.getBuildStatus(),
				"status", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getIBuild_Artifacts(),
				this.getIArtifact(),
				null,
				"artifacts", null, 0, -1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getIBuild_ChangeSet(),
				this.getIChangeSet(),
				null,
				"changeSet", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getIBuild_Plan(),
				this.getIBuildPlan(),
				null,
				"plan", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIBuild_Label(),
				ecorePackage.getEString(),
				"label", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getIBuild_Server(),
				this.getIBuildServer(),
				null,
				"server", null, 0, 1, IBuild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(iBuildWorkingCopyEClass, IBuildWorkingCopy.class,
				"IBuildWorkingCopy", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(iArtifactEClass, IArtifact.class,
				"IArtifact", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getIArtifact_DisplayName(),
				ecorePackage.getEString(),
				"displayName", null, 0, 1, IArtifact.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIArtifact_Filename(),
				ecorePackage.getEString(),
				"filename", null, 0, 1, IArtifact.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIArtifact_RelativePath(),
				ecorePackage.getEString(),
				"relativePath", null, 0, 1, IArtifact.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(iChangeEClass, IChange.class, "IChange", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getIChange_Author(),
				this.getIUser(),
				null,
				"author", null, 0, 1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getIChange_File(),
				this.getIFile(),
				null,
				"file", null, 0, 1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIChange_Message(),
				ecorePackage.getEString(),
				"message", null, 0, 1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIChange_Date(),
				ecorePackage.getELong(),
				"date", null, 0, 1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getIChange_User(),
				this.getIUser(),
				null,
				"user", null, 0, 1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(iChangeSetEClass, IChangeSet.class,
				"IChangeSet", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getIChangeSet_Changes(),
				this.getIChange(),
				null,
				"changes", null, 0, -1, IChangeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIChangeSet_Kind(),
				ecorePackage.getEString(),
				"kind", null, 0, 1, IChangeSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(iFileEClass, IFile.class, "IFile", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getIFile_RelativePath(),
				ecorePackage.getEString(),
				"relativePath", null, 0, 1, IFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIFile_PrevRevision(),
				ecorePackage.getEString(),
				"prevRevision", null, 0, 1, IFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIFile_Revision(),
				ecorePackage.getEString(),
				"revision", null, 0, 1, IFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIFile_Dead(),
				ecorePackage.getEBoolean(),
				"dead", null, 0, 1, IFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIFile_EditType(),
				this.getEditType(),
				"editType", null, 0, 1, IFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(iUserEClass, IUser.class, "IUser", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getIUser_Fullname(),
				ecorePackage.getEString(),
				"fullname", null, 0, 1, IUser.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIUser_Username(),
				ecorePackage.getEString(),
				"username", null, 0, 1, IUser.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getIUser_Email(),
				ecorePackage.getEString(),
				"email", null, 0, 1, IUser.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(artifactEClass, Artifact.class, "Artifact", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(buildEClass, Build.class, "Build", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(buildPlanEClass, BuildPlan.class,
				"BuildPlan", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(buildServerEClass, BuildServer.class,
				"BuildServer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(buildModelEClass, BuildModel.class,
				"BuildModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(changeEClass, Change.class, "Change", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(changeSetEClass, ChangeSet.class,
				"ChangeSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(fileEClass, File.class, "File", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

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

		initEClass(userEClass, User.class, "User", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(parameterDefinitionEClass, ParameterDefinition.class,
				"ParameterDefinition", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getParameterDefinition_Name(),
				ecorePackage.getEString(),
				"name", null, 0, 1, ParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getParameterDefinition_Description(),
				ecorePackage.getEString(),
				"description", null, 0, 1, ParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getParameterDefinition_ContainingBuildPlan(),
				this.getIBuildPlan(),
				this.getIBuildPlan_ParameterDefinitions(),
				"containingBuildPlan", null, 0, 1, ParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(choiceParameterDefinitionEClass, ChoiceParameterDefinition.class,
				"ChoiceParameterDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getChoiceParameterDefinition_Options(),
				ecorePackage.getEString(),
				"options", null, 1, -1, ChoiceParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(booleanParameterDefinitionEClass, BooleanParameterDefinition.class,
				"BooleanParameterDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getBooleanParameterDefinition_DefaultValue(),
				ecorePackage.getEBoolean(),
				"defaultValue", null, 0, 1, BooleanParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(fileParameterDefinitionEClass, FileParameterDefinition.class,
				"FileParameterDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(planParameterDefinitionEClass, PlanParameterDefinition.class,
				"PlanParameterDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(passwordParameterDefinitionEClass, PasswordParameterDefinition.class,
				"PasswordParameterDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getPasswordParameterDefinition_DefaultValue(),
				ecorePackage.getEString(),
				"defaultValue", null, 0, 1, PasswordParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(buildParameterDefinitionEClass, BuildParameterDefinition.class,
				"BuildParameterDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getBuildParameterDefinition_BuildPlanId(),
				ecorePackage.getEString(),
				"buildPlanId", null, 0, 1, BuildParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getBuildParameterDefinition_BuildPlan(),
				this.getIBuildPlan(),
				null,
				"buildPlan", null, 0, 1, BuildParameterDefinition.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(stringParameterDefinitionEClass, StringParameterDefinition.class,
				"StringParameterDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getStringParameterDefinition_DefaultValue(),
				ecorePackage.getEString(),
				"defaultValue", null, 0, 1, StringParameterDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(testResultEClass, TestResult.class,
				"TestResult", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getTestResult_Build(),
				this.getIBuild(),
				null,
				"build", null, 0, 1, TestResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestResult_Duration(),
				ecorePackage.getELong(),
				"duration", null, 0, 1, TestResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestResult_FailCount(),
				ecorePackage.getEInt(),
				"failCount", null, 0, 1, TestResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestResult_PassCount(),
				ecorePackage.getEInt(),
				"passCount", null, 0, 1, TestResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getTestResult_Suites(),
				this.getTestSuite(),
				this.getTestSuite_Result(),
				"suites", null, 0, -1, TestResult.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(testElementEClass, TestElement.class,
				"TestElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getTestElement_Label(),
				ecorePackage.getEString(),
				"label", null, 0, 1, TestElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestElement_Duration(),
				ecorePackage.getELong(),
				"duration", null, 0, 1, TestElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestElement_ErrorOutput(),
				ecorePackage.getEString(),
				"errorOutput", null, 0, 1, TestElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestElement_Output(),
				ecorePackage.getEString(),
				"output", null, 0, 1, TestElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(testSuiteEClass, TestSuite.class,
				"TestSuite", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getTestSuite_Cases(),
				this.getTestCase(),
				this.getTestCase_Suite(),
				"cases", null, 0, -1, TestSuite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getTestSuite_Result(),
				this.getTestResult(),
				this.getTestResult_Suites(),
				"result", null, 0, 1, TestSuite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(testCaseEClass, TestCase.class, "TestCase", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getTestCase_ClassName(),
				ecorePackage.getEString(),
				"className", null, 0, 1, TestCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestCase_Skipped(),
				ecorePackage.getEBoolean(),
				"skipped", null, 0, 1, TestCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getTestCase_Suite(),
				this.getTestSuite(),
				this.getTestSuite_Cases(),
				"suite", null, 0, 1, TestCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTestCase_Status(),
				this.getTestCaseResult(),
				"status", null, 0, 1, TestCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		// Initialize enums and add enum literals
		initEEnum(testCaseResultEEnum, TestCaseResult.class, "TestCaseResult"); //$NON-NLS-1$
		addEEnumLiteral(testCaseResultEEnum, TestCaseResult.PASSED);
		addEEnumLiteral(testCaseResultEEnum, TestCaseResult.SKIPPED);
		addEEnumLiteral(testCaseResultEEnum, TestCaseResult.FAILED);
		addEEnumLiteral(testCaseResultEEnum, TestCaseResult.FIXED);
		addEEnumLiteral(testCaseResultEEnum, TestCaseResult.REGRESSION);

		// Initialize data types
		initEDataType(repositoryLocationEDataType, RepositoryLocation.class,
				"RepositoryLocation", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEDataType(buildStateEDataType, BuildState.class,
				"BuildState", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEDataType(buildStatusEDataType, BuildStatus.class,
				"BuildStatus", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEDataType(editTypeEDataType, EditType.class, "EditType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

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
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.BuildModel <em>Model</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.BuildModel
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildModel()
		 * @generated
		 */
		public static final EClass BUILD_MODEL = eINSTANCE.getBuildModel();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan <em>Plan</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPlan
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildPlan()
		 * @generated
		 */
		public static final EClass BUILD_PLAN = eINSTANCE.getBuildPlan();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.BuildServer <em>Server</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.BuildServer
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildServer()
		 * @generated
		 */
		public static final EClass BUILD_SERVER = eINSTANCE.getBuildServer();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IBuildModel <em>IBuild Model</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.IBuildModel
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildModel()
		 * @generated
		 */
		public static final EClass IBUILD_MODEL = eINSTANCE.getIBuildModel();

		/**
		 * The meta object literal for the '<em><b>Servers</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference IBUILD_MODEL__SERVERS = eINSTANCE.getIBuildModel_Servers();

		/**
		 * The meta object literal for the '<em><b>Plans</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference IBUILD_MODEL__PLANS = eINSTANCE.getIBuildModel_Plans();

		/**
		 * The meta object literal for the '<em><b>Builds</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference IBUILD_MODEL__BUILDS = eINSTANCE.getIBuildModel_Builds();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IBuildElement <em>IBuild Element</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.IBuildElement
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildElement()
		 * @generated
		 */
		public static final EClass IBUILD_ELEMENT = eINSTANCE.getIBuildElement();

		/**
		 * The meta object literal for the '<em><b>Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD_ELEMENT__URL = eINSTANCE.getIBuildElement_Url();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD_ELEMENT__NAME = eINSTANCE.getIBuildElement_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IBuildPlan <em>IBuild Plan</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.IBuildPlan
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan()
		 * @generated
		 */
		public static final EClass IBUILD_PLAN = eINSTANCE.getIBuildPlan();

		/**
		 * The meta object literal for the '<em><b>Server</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference IBUILD_PLAN__SERVER = eINSTANCE.getIBuildPlan_Server();

		/**
		 * The meta object literal for the '<em><b>Children</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference IBUILD_PLAN__CHILDREN = eINSTANCE.getIBuildPlan_Children();

		/**
		 * The meta object literal for the '<em><b>Parent</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference IBUILD_PLAN__PARENT = eINSTANCE.getIBuildPlan_Parent();

		/**
		 * The meta object literal for the '<em><b>Health</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD_PLAN__HEALTH = eINSTANCE.getIBuildPlan_Health();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD_PLAN__ID = eINSTANCE.getIBuildPlan_Id();

		/**
		 * The meta object literal for the '<em><b>Info</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD_PLAN__INFO = eINSTANCE.getIBuildPlan_Info();

		/**
		 * The meta object literal for the '<em><b>Selected</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD_PLAN__SELECTED = eINSTANCE.getIBuildPlan_Selected();

		/**
		 * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD_PLAN__SUMMARY = eINSTANCE.getIBuildPlan_Summary();

		/**
		 * The meta object literal for the '<em><b>State</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD_PLAN__STATE = eINSTANCE.getIBuildPlan_State();

		/**
		 * The meta object literal for the '<em><b>Status</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD_PLAN__STATUS = eINSTANCE.getIBuildPlan_Status();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD_PLAN__DESCRIPTION = eINSTANCE.getIBuildPlan_Description();

		/**
		 * The meta object literal for the '<em><b>Last Build</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference IBUILD_PLAN__LAST_BUILD = eINSTANCE.getIBuildPlan_LastBuild();

		/**
		 * The meta object literal for the '<em><b>Parameter Definitions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference IBUILD_PLAN__PARAMETER_DEFINITIONS = eINSTANCE
				.getIBuildPlan_ParameterDefinitions();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IBuildWorkingCopy
		 * <em>IBuild Working Copy</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.IBuildWorkingCopy
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildWorkingCopy()
		 * @generated
		 */
		public static final EClass IBUILD_WORKING_COPY = eINSTANCE.getIBuildWorkingCopy();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IBuildPlanData
		 * <em>IBuild Plan Data</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.IBuildPlanData
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlanData()
		 * @generated
		 */
		public static final EClass IBUILD_PLAN_DATA = eINSTANCE.getIBuildPlanData();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy
		 * <em>IBuild Plan Working Copy</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlanWorkingCopy()
		 * @generated
		 */
		public static final EClass IBUILD_PLAN_WORKING_COPY = eINSTANCE.getIBuildPlanWorkingCopy();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IBuildServer <em>IBuild Server</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.IBuildServer
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildServer()
		 * @generated
		 */
		public static final EClass IBUILD_SERVER = eINSTANCE.getIBuildServer();

		/**
		 * The meta object literal for the '<em><b>Attributes</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference IBUILD_SERVER__ATTRIBUTES = eINSTANCE.getIBuildServer_Attributes();

		/**
		 * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD_SERVER__LOCATION = eINSTANCE.getIBuildServer_Location();

		/**
		 * The meta object literal for the '<em><b>Connector Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD_SERVER__CONNECTOR_KIND = eINSTANCE.getIBuildServer_ConnectorKind();

		/**
		 * The meta object literal for the '<em><b>Repository Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD_SERVER__REPOSITORY_URL = eINSTANCE.getIBuildServer_RepositoryUrl();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.Artifact <em>Artifact</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.Artifact
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getArtifact()
		 * @generated
		 */
		public static final EClass ARTIFACT = eINSTANCE.getArtifact();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.Build <em>Build</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.Build
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuild()
		 * @generated
		 */
		public static final EClass BUILD = eINSTANCE.getBuild();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.ChangeSet <em>Change Set</em>}
		 * ' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.ChangeSet
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getChangeSet()
		 * @generated
		 */
		public static final EClass CHANGE_SET = eINSTANCE.getChangeSet();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.Change <em>Change</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.Change
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getChange()
		 * @generated
		 */
		public static final EClass CHANGE = eINSTANCE.getChange();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.File <em>File</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.File
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getFile()
		 * @generated
		 */
		public static final EClass FILE = eINSTANCE.getFile();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.User <em>User</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.User
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getUser()
		 * @generated
		 */
		public static final EClass USER = eINSTANCE.getUser();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.ParameterDefinition
		 * <em>Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.ParameterDefinition
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getParameterDefinition()
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
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.ChoiceParameterDefinition
		 * <em>Choice Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.ChoiceParameterDefinition
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getChoiceParameterDefinition()
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
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.BooleanParameterDefinition
		 * <em>Boolean Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.BooleanParameterDefinition
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBooleanParameterDefinition()
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
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.FileParameterDefinition
		 * <em>File Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.FileParameterDefinition
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getFileParameterDefinition()
		 * @generated
		 */
		public static final EClass FILE_PARAMETER_DEFINITION = eINSTANCE.getFileParameterDefinition();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.PlanParameterDefinition
		 * <em>Plan Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.PlanParameterDefinition
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getPlanParameterDefinition()
		 * @generated
		 */
		public static final EClass PLAN_PARAMETER_DEFINITION = eINSTANCE.getPlanParameterDefinition();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.PasswordParameterDefinition
		 * <em>Password Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.PasswordParameterDefinition
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getPasswordParameterDefinition()
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
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.BuildParameterDefinition
		 * <em>Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.BuildParameterDefinition
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildParameterDefinition()
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
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.StringParameterDefinition
		 * <em>String Parameter Definition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.StringParameterDefinition
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getStringParameterDefinition()
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
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.TestResult
		 * <em>Test Result</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.TestResult
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestResult()
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
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.TestElement
		 * <em>Test Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.TestElement
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestElement()
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
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.TestSuite <em>Test Suite</em>}
		 * ' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.TestSuite
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestSuite()
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
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.TestCase <em>Test Case</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.TestCase
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestCase()
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
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.TestCaseResult
		 * <em>Test Case Result</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.TestCaseResult
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTestCaseResult()
		 * @generated
		 */
		public static final EEnum TEST_CASE_RESULT = eINSTANCE.getTestCaseResult();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IArtifact <em>IArtifact</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.IArtifact
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIArtifact()
		 * @generated
		 */
		public static final EClass IARTIFACT = eINSTANCE.getIArtifact();

		/**
		 * The meta object literal for the '<em><b>Display Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IARTIFACT__DISPLAY_NAME = eINSTANCE.getIArtifact_DisplayName();

		/**
		 * The meta object literal for the '<em><b>Filename</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IARTIFACT__FILENAME = eINSTANCE.getIArtifact_Filename();

		/**
		 * The meta object literal for the '<em><b>Relative Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IARTIFACT__RELATIVE_PATH = eINSTANCE.getIArtifact_RelativePath();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IBuild <em>IBuild</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.IBuild
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuild()
		 * @generated
		 */
		public static final EClass IBUILD = eINSTANCE.getIBuild();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD__ID = eINSTANCE.getIBuild_Id();

		/**
		 * The meta object literal for the '<em><b>Build Number</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD__BUILD_NUMBER = eINSTANCE.getIBuild_BuildNumber();

		/**
		 * The meta object literal for the '<em><b>Timestamp</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD__TIMESTAMP = eINSTANCE.getIBuild_Timestamp();

		/**
		 * The meta object literal for the '<em><b>Duration</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD__DURATION = eINSTANCE.getIBuild_Duration();

		/**
		 * The meta object literal for the '<em><b>Display Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD__DISPLAY_NAME = eINSTANCE.getIBuild_DisplayName();

		/**
		 * The meta object literal for the '<em><b>State</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD__STATE = eINSTANCE.getIBuild_State();

		/**
		 * The meta object literal for the '<em><b>Status</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD__STATUS = eINSTANCE.getIBuild_Status();

		/**
		 * The meta object literal for the '<em><b>Artifacts</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference IBUILD__ARTIFACTS = eINSTANCE.getIBuild_Artifacts();

		/**
		 * The meta object literal for the '<em><b>Change Set</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference IBUILD__CHANGE_SET = eINSTANCE.getIBuild_ChangeSet();

		/**
		 * The meta object literal for the '<em><b>Plan</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference IBUILD__PLAN = eINSTANCE.getIBuild_Plan();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IBUILD__LABEL = eINSTANCE.getIBuild_Label();

		/**
		 * The meta object literal for the '<em><b>Server</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference IBUILD__SERVER = eINSTANCE.getIBuild_Server();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IChangeSet <em>IChange Set</em>}'
		 * class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.IChangeSet
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIChangeSet()
		 * @generated
		 */
		public static final EClass ICHANGE_SET = eINSTANCE.getIChangeSet();

		/**
		 * The meta object literal for the '<em><b>Changes</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference ICHANGE_SET__CHANGES = eINSTANCE.getIChangeSet_Changes();

		/**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute ICHANGE_SET__KIND = eINSTANCE.getIChangeSet_Kind();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IChange <em>IChange</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.IChange
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIChange()
		 * @generated
		 */
		public static final EClass ICHANGE = eINSTANCE.getIChange();

		/**
		 * The meta object literal for the '<em><b>Author</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference ICHANGE__AUTHOR = eINSTANCE.getIChange_Author();

		/**
		 * The meta object literal for the '<em><b>File</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference ICHANGE__FILE = eINSTANCE.getIChange_File();

		/**
		 * The meta object literal for the '<em><b>Message</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute ICHANGE__MESSAGE = eINSTANCE.getIChange_Message();

		/**
		 * The meta object literal for the '<em><b>Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute ICHANGE__DATE = eINSTANCE.getIChange_Date();

		/**
		 * The meta object literal for the '<em><b>User</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference ICHANGE__USER = eINSTANCE.getIChange_User();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IFile <em>IFile</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.IFile
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIFile()
		 * @generated
		 */
		public static final EClass IFILE = eINSTANCE.getIFile();

		/**
		 * The meta object literal for the '<em><b>Relative Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IFILE__RELATIVE_PATH = eINSTANCE.getIFile_RelativePath();

		/**
		 * The meta object literal for the '<em><b>Prev Revision</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IFILE__PREV_REVISION = eINSTANCE.getIFile_PrevRevision();

		/**
		 * The meta object literal for the '<em><b>Revision</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IFILE__REVISION = eINSTANCE.getIFile_Revision();

		/**
		 * The meta object literal for the '<em><b>Dead</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IFILE__DEAD = eINSTANCE.getIFile_Dead();

		/**
		 * The meta object literal for the '<em><b>Edit Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IFILE__EDIT_TYPE = eINSTANCE.getIFile_EditType();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IUser <em>IUser</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.IUser
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIUser()
		 * @generated
		 */
		public static final EClass IUSER = eINSTANCE.getIUser();

		/**
		 * The meta object literal for the '<em><b>Fullname</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IUSER__FULLNAME = eINSTANCE.getIUser_Fullname();

		/**
		 * The meta object literal for the '<em><b>Username</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IUSER__USERNAME = eINSTANCE.getIUser_Username();

		/**
		 * The meta object literal for the '<em><b>Email</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute IUSER__EMAIL = eINSTANCE.getIUser_Email();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.StringToStringMap
		 * <em>String To String Map</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.internal.builds.core.StringToStringMap
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getStringToStringMap()
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
		 * The meta object literal for the '<em>Repository Location</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.commons.repositories.RepositoryLocation
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getRepositoryLocation()
		 * @generated
		 */
		public static final EDataType REPOSITORY_LOCATION = eINSTANCE.getRepositoryLocation();

		/**
		 * The meta object literal for the '<em>State</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.BuildState
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildState()
		 * @generated
		 */
		public static final EDataType BUILD_STATE = eINSTANCE.getBuildState();

		/**
		 * The meta object literal for the '<em>Status</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.BuildStatus
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildStatus()
		 * @generated
		 */
		public static final EDataType BUILD_STATUS = eINSTANCE.getBuildStatus();

		/**
		 * The meta object literal for the '<em>Edit Type</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.builds.core.EditType
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getEditType()
		 * @generated
		 */
		public static final EDataType EDIT_TYPE = eINSTANCE.getEditType();

	}

} //BuildPackage
