/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildPackage.java,v 1.3 2010/05/30 20:28:49 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy;
import org.eclipse.mylyn.builds.core.IBuildServer;

import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.mylyn.internal.builds.core.BuildFactory
 * @model kind="package"
 * @generated
 */
public class BuildPackage extends EPackageImpl {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String eNAME = "builds";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String eNS_URI = "http://eclipse.org/mylyn/models/build";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String eNS_PREFIX = "builds";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final BuildPackage eINSTANCE = org.eclipse.mylyn.internal.builds.core.BuildPackage.init();

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IBuildModel <em>IBuild Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.builds.core.IBuildModel
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildModel()
	 * @generated
	 */
	public static final int IBUILD_MODEL = 3;

	/**
	 * The feature id for the '<em><b>Servers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_MODEL__SERVERS = 0;

	/**
	 * The number of structural features of the '<em>IBuild Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_MODEL_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.BuildModel <em>Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.internal.builds.core.BuildModel
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildModel()
	 * @generated
	 */
	public static final int BUILD_MODEL = 0;

	/**
	 * The feature id for the '<em><b>Servers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_MODEL__SERVERS = IBUILD_MODEL__SERVERS;

	/**
	 * The number of structural features of the '<em>Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_MODEL_FEATURE_COUNT = IBUILD_MODEL_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IBuildElement <em>IBuild Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.builds.core.IBuildElement
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildElement()
	 * @generated
	 */
	public static final int IBUILD_ELEMENT = 4;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_ELEMENT__URL = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_ELEMENT__NAME = 1;

	/**
	 * The number of structural features of the '<em>IBuild Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_ELEMENT_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IBuildPlan <em>IBuild Plan</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan()
	 * @generated
	 */
	public static final int IBUILD_PLAN = 5;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__URL = IBUILD_ELEMENT__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__NAME = IBUILD_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Server</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__SERVER = IBUILD_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Children</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__CHILDREN = IBUILD_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__PARENT = IBUILD_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Health</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__HEALTH = IBUILD_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__ID = IBUILD_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Info</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__INFO = IBUILD_ELEMENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Selected</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__SELECTED = IBUILD_ELEMENT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__SUMMARY = IBUILD_ELEMENT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__STATE = IBUILD_ELEMENT_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN__STATUS = IBUILD_ELEMENT_FEATURE_COUNT + 9;

	/**
	 * The number of structural features of the '<em>IBuild Plan</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_FEATURE_COUNT = IBUILD_ELEMENT_FEATURE_COUNT + 10;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy <em>IBuild Plan Working Copy</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlanWorkingCopy()
	 * @generated
	 */
	public static final int IBUILD_PLAN_WORKING_COPY = 6;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_WORKING_COPY__URL = IBUILD_PLAN__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_WORKING_COPY__NAME = IBUILD_PLAN__NAME;

	/**
	 * The feature id for the '<em><b>Server</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_WORKING_COPY__SERVER = IBUILD_PLAN__SERVER;

	/**
	 * The feature id for the '<em><b>Children</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_WORKING_COPY__CHILDREN = IBUILD_PLAN__CHILDREN;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_WORKING_COPY__PARENT = IBUILD_PLAN__PARENT;

	/**
	 * The feature id for the '<em><b>Health</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_WORKING_COPY__HEALTH = IBUILD_PLAN__HEALTH;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_WORKING_COPY__ID = IBUILD_PLAN__ID;

	/**
	 * The feature id for the '<em><b>Info</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_WORKING_COPY__INFO = IBUILD_PLAN__INFO;

	/**
	 * The feature id for the '<em><b>Selected</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_WORKING_COPY__SELECTED = IBUILD_PLAN__SELECTED;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_WORKING_COPY__SUMMARY = IBUILD_PLAN__SUMMARY;

	/**
	 * The feature id for the '<em><b>State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_WORKING_COPY__STATE = IBUILD_PLAN__STATE;

	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_WORKING_COPY__STATUS = IBUILD_PLAN__STATUS;

	/**
	 * The number of structural features of the '<em>IBuild Plan Working Copy</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_PLAN_WORKING_COPY_FEATURE_COUNT = IBUILD_PLAN_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan <em>Plan</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPlan
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildPlan()
	 * @generated
	 */
	public static final int BUILD_PLAN = 1;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__URL = IBUILD_PLAN_WORKING_COPY__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__NAME = IBUILD_PLAN_WORKING_COPY__NAME;

	/**
	 * The feature id for the '<em><b>Server</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__SERVER = IBUILD_PLAN_WORKING_COPY__SERVER;

	/**
	 * The feature id for the '<em><b>Children</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__CHILDREN = IBUILD_PLAN_WORKING_COPY__CHILDREN;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__PARENT = IBUILD_PLAN_WORKING_COPY__PARENT;

	/**
	 * The feature id for the '<em><b>Health</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__HEALTH = IBUILD_PLAN_WORKING_COPY__HEALTH;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__ID = IBUILD_PLAN_WORKING_COPY__ID;

	/**
	 * The feature id for the '<em><b>Info</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__INFO = IBUILD_PLAN_WORKING_COPY__INFO;

	/**
	 * The feature id for the '<em><b>Selected</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__SELECTED = IBUILD_PLAN_WORKING_COPY__SELECTED;

	/**
	 * The feature id for the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__SUMMARY = IBUILD_PLAN_WORKING_COPY__SUMMARY;

	/**
	 * The feature id for the '<em><b>State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__STATE = IBUILD_PLAN_WORKING_COPY__STATE;

	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN__STATUS = IBUILD_PLAN_WORKING_COPY__STATUS;

	/**
	 * The number of structural features of the '<em>Plan</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_PLAN_FEATURE_COUNT = IBUILD_PLAN_WORKING_COPY_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.builds.core.IBuildServer <em>IBuild Server</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.builds.core.IBuildServer
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildServer()
	 * @generated
	 */
	public static final int IBUILD_SERVER = 7;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_SERVER__URL = IBUILD_ELEMENT__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_SERVER__NAME = IBUILD_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Plans</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_SERVER__PLANS = IBUILD_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Repository</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_SERVER__REPOSITORY = IBUILD_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Connector Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_SERVER__CONNECTOR_KIND = IBUILD_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Repository Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_SERVER__REPOSITORY_URL = IBUILD_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>IBuild Server</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IBUILD_SERVER_FEATURE_COUNT = IBUILD_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.internal.builds.core.BuildServer <em>Server</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.internal.builds.core.BuildServer
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildServer()
	 * @generated
	 */
	public static final int BUILD_SERVER = 2;

	/**
	 * The feature id for the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__URL = IBUILD_SERVER__URL;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__NAME = IBUILD_SERVER__NAME;

	/**
	 * The feature id for the '<em><b>Plans</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__PLANS = IBUILD_SERVER__PLANS;

	/**
	 * The feature id for the '<em><b>Repository</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__REPOSITORY = IBUILD_SERVER__REPOSITORY;

	/**
	 * The feature id for the '<em><b>Connector Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__CONNECTOR_KIND = IBUILD_SERVER__CONNECTOR_KIND;

	/**
	 * The feature id for the '<em><b>Repository Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__REPOSITORY_URL = IBUILD_SERVER__REPOSITORY_URL;

	/**
	 * The feature id for the '<em><b>Server</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER__SERVER = IBUILD_SERVER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Server</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int BUILD_SERVER_FEATURE_COUNT = IBUILD_SERVER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '<em>State</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.builds.core.BuildState
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildState()
	 * @generated
	 */
	public static final int BUILD_STATE = 9;

	/**
	 * The meta object id for the '<em>Task Repository</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.tasks.core.TaskRepository
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTaskRepository()
	 * @generated
	 */
	public static final int TASK_REPOSITORY = 8;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass buildModelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass buildPlanEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass buildServerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iBuildModelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iBuildElementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iBuildPlanEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iBuildServerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iBuildPlanWorkingCopyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType taskRepositoryEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType buildStateEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
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
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link BuildPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static BuildPackage init() {
		if (isInited)
			return (BuildPackage) EPackage.Registry.INSTANCE.getEPackage(BuildPackage.eNS_URI);

		// Obtain or create and register package
		BuildPackage theBuildPackage = (BuildPackage) (EPackage.Registry.INSTANCE.get(eNS_URI) instanceof BuildPackage ? EPackage.Registry.INSTANCE.get(eNS_URI)
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
	 * @return the meta object for class '<em>Server</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildServer
	 * @generated
	 */
	public EClass getBuildServer() {
		return buildServerEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.internal.builds.core.BuildServer#getServer <em>Server</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Server</em>'.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildServer#getServer()
	 * @see #getBuildServer()
	 * @generated
	 */
	public EReference getBuildServer_Server() {
		return (EReference) buildServerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildModel <em>IBuild Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IBuild Model</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildModel
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IBuildModel"
	 * @generated
	 */
	public EClass getIBuildModel() {
		return iBuildModelEClass;
	}

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.builds.core.IBuildModel#getServers <em>Servers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Servers</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildModel#getServers()
	 * @see #getIBuildModel()
	 * @generated
	 */
	public EReference getIBuildModel_Servers() {
		return (EReference) iBuildModelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildElement <em>IBuild Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IBuild Element</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildElement
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IBuildElement"
	 * @generated
	 */
	public EClass getIBuildElement() {
		return iBuildElementEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildElement#getUrl <em>Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Url</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildElement#getUrl()
	 * @see #getIBuildElement()
	 * @generated
	 */
	public EAttribute getIBuildElement_Url() {
		return (EAttribute) iBuildElementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildElement#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
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
	 * @return the meta object for class '<em>IBuild Plan</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IBuildPlan" superTypes="org.eclipse.mylyn.internal.builds.core.IBuildElement"
	 * @generated
	 */
	public EClass getIBuildPlan() {
		return iBuildPlanEClass;
	}

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getServer <em>Server</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Server</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getServer()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EReference getIBuildPlan_Server() {
		return (EReference) iBuildPlanEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getChildren <em>Children</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Children</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getChildren()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EReference getIBuildPlan_Children() {
		return (EReference) iBuildPlanEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Parent</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getParent()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EReference getIBuildPlan_Parent() {
		return (EReference) iBuildPlanEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getHealth <em>Health</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
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
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getId()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EAttribute getIBuildPlan_Id() {
		return (EAttribute) iBuildPlanEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getSummary <em>Summary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Summary</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getSummary()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EAttribute getIBuildPlan_Summary() {
		return (EAttribute) iBuildPlanEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getStatus <em>Status</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Status</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getStatus()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EAttribute getIBuildPlan_Status() {
		return (EAttribute) iBuildPlanEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getInfo <em>Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Info</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getInfo()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EAttribute getIBuildPlan_Info() {
		return (EAttribute) iBuildPlanEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getState <em>State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>State</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getState()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EAttribute getIBuildPlan_State() {
		return (EAttribute) iBuildPlanEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildPlan#isSelected <em>Selected</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Selected</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#isSelected()
	 * @see #getIBuildPlan()
	 * @generated
	 */
	public EAttribute getIBuildPlan_Selected() {
		return (EAttribute) iBuildPlanEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildServer <em>IBuild Server</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IBuild Server</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IBuildServer" superTypes="org.eclipse.mylyn.internal.builds.core.IBuildElement"
	 * @generated
	 */
	public EClass getIBuildServer() {
		return iBuildServerEClass;
	}

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.builds.core.IBuildServer#getPlans <em>Plans</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Plans</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer#getPlans()
	 * @see #getIBuildServer()
	 * @generated
	 */
	public EReference getIBuildServer_Plans() {
		return (EReference) iBuildServerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildServer#getRepository <em>Repository</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Repository</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer#getRepository()
	 * @see #getIBuildServer()
	 * @generated
	 */
	public EAttribute getIBuildServer_Repository() {
		return (EAttribute) iBuildServerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildServer#getConnectorKind <em>Connector Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Connector Kind</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer#getConnectorKind()
	 * @see #getIBuildServer()
	 * @generated
	 */
	public EAttribute getIBuildServer_ConnectorKind() {
		return (EAttribute) iBuildServerEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.builds.core.IBuildServer#getRepositoryUrl <em>Repository Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Repository Url</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer#getRepositoryUrl()
	 * @see #getIBuildServer()
	 * @generated
	 */
	public EAttribute getIBuildServer_RepositoryUrl() {
		return (EAttribute) iBuildServerEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy <em>IBuild Plan Working Copy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IBuild Plan Working Copy</em>'.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy
	 * @model instanceClass="org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy" superTypes="org.eclipse.mylyn.internal.builds.core.IBuildPlan"
	 * @generated
	 */
	public EClass getIBuildPlanWorkingCopy() {
		return iBuildPlanWorkingCopyEClass;
	}

	/**
	 * Returns the meta object for data type '{@link org.eclipse.mylyn.builds.core.BuildState <em>State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>State</em>'.
	 * @see org.eclipse.mylyn.builds.core.BuildState
	 * @model instanceClass="org.eclipse.mylyn.builds.core.BuildState"
	 * @generated
	 */
	public EDataType getBuildState() {
		return buildStateEDataType;
	}

	/**
	 * Returns the meta object for data type '{@link org.eclipse.mylyn.tasks.core.TaskRepository <em>Task Repository</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Task Repository</em>'.
	 * @see org.eclipse.mylyn.tasks.core.TaskRepository
	 * @model instanceClass="org.eclipse.mylyn.tasks.core.TaskRepository"
	 * @generated
	 */
	public EDataType getTaskRepository() {
		return taskRepositoryEDataType;
	}

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	public BuildFactory getBuildFactory() {
		return (BuildFactory) getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated)
			return;
		isCreated = true;

		// Create classes and their features
		buildModelEClass = createEClass(BUILD_MODEL);

		buildPlanEClass = createEClass(BUILD_PLAN);

		buildServerEClass = createEClass(BUILD_SERVER);
		createEReference(buildServerEClass, BUILD_SERVER__SERVER);

		iBuildModelEClass = createEClass(IBUILD_MODEL);
		createEReference(iBuildModelEClass, IBUILD_MODEL__SERVERS);

		iBuildElementEClass = createEClass(IBUILD_ELEMENT);
		createEAttribute(iBuildElementEClass, IBUILD_ELEMENT__URL);
		createEAttribute(iBuildElementEClass, IBUILD_ELEMENT__NAME);

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

		iBuildPlanWorkingCopyEClass = createEClass(IBUILD_PLAN_WORKING_COPY);

		iBuildServerEClass = createEClass(IBUILD_SERVER);
		createEReference(iBuildServerEClass, IBUILD_SERVER__PLANS);
		createEAttribute(iBuildServerEClass, IBUILD_SERVER__REPOSITORY);
		createEAttribute(iBuildServerEClass, IBUILD_SERVER__CONNECTOR_KIND);
		createEAttribute(iBuildServerEClass, IBUILD_SERVER__REPOSITORY_URL);

		// Create data types
		taskRepositoryEDataType = createEDataType(TASK_REPOSITORY);
		buildStateEDataType = createEDataType(BUILD_STATE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
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
		buildModelEClass.getESuperTypes().add(this.getIBuildModel());
		buildPlanEClass.getESuperTypes().add(this.getIBuildPlanWorkingCopy());
		buildServerEClass.getESuperTypes().add(this.getIBuildServer());
		iBuildPlanEClass.getESuperTypes().add(this.getIBuildElement());
		iBuildPlanWorkingCopyEClass.getESuperTypes().add(this.getIBuildPlan());
		iBuildServerEClass.getESuperTypes().add(this.getIBuildElement());

		// Initialize classes and features; add operations and parameters
		initEClass(buildModelEClass, BuildModel.class, "BuildModel", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(buildPlanEClass, BuildPlan.class, "BuildPlan", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(buildServerEClass, BuildServer.class, "BuildServer", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getBuildServer_Server(), this.getBuildServer(), null, "server", null, 1, 1, BuildServer.class,
				!IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(iBuildModelEClass, IBuildModel.class, "IBuildModel", IS_ABSTRACT, IS_INTERFACE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEReference(getIBuildModel_Servers(), this.getIBuildServer(), null, "servers", null, 0, -1,
				IBuildModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(iBuildElementEClass, IBuildElement.class, "IBuildElement", IS_ABSTRACT, IS_INTERFACE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIBuildElement_Url(), ecorePackage.getEString(), "url", null, 0, 1, IBuildElement.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIBuildElement_Name(), ecorePackage.getEString(), "name", null, 0, 1, IBuildElement.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(iBuildPlanEClass, IBuildPlan.class, "IBuildPlan", IS_ABSTRACT, IS_INTERFACE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEReference(getIBuildPlan_Server(), this.getIBuildServer(), this.getIBuildServer_Plans(), "server", null, 1,
				1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getIBuildPlan_Children(), this.getIBuildPlan(), this.getIBuildPlan_Parent(), "children", null,
				0, -1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getIBuildPlan_Parent(), this.getIBuildPlan(), this.getIBuildPlan_Children(), "parent", null, 0,
				1, IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIBuildPlan_Health(), ecorePackage.getEInt(), "health", "-1", 0, 1, IBuildPlan.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIBuildPlan_Id(), ecorePackage.getEString(), "id", null, 1, 1, IBuildPlan.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIBuildPlan_Info(), ecorePackage.getEString(), "info", null, 0, 1, IBuildPlan.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIBuildPlan_Selected(), ecorePackage.getEBoolean(), "selected", "false", 1, 1,
				IBuildPlan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEAttribute(getIBuildPlan_Summary(), ecorePackage.getEString(), "summary", null, 0, 1, IBuildPlan.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIBuildPlan_State(), this.getBuildState(), "state", null, 0, 1, IBuildPlan.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIBuildPlan_Status(), ecorePackage.getEString(), "status", null, 0, 1, IBuildPlan.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(iBuildPlanWorkingCopyEClass, IBuildPlanWorkingCopy.class, "IBuildPlanWorkingCopy", IS_ABSTRACT,
				IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);

		initEClass(iBuildServerEClass, IBuildServer.class, "IBuildServer", IS_ABSTRACT, IS_INTERFACE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEReference(getIBuildServer_Plans(), this.getIBuildPlan(), this.getIBuildPlan_Server(), "plans", null, 0,
				-1, IBuildServer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getIBuildServer_Repository(), this.getTaskRepository(), "repository", null, 0, 1,
				IBuildServer.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEAttribute(getIBuildServer_ConnectorKind(), ecorePackage.getEString(), "connectorKind", null, 0, 1,
				IBuildServer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEAttribute(getIBuildServer_RepositoryUrl(), ecorePackage.getEString(), "repositoryUrl", null, 0, 1,
				IBuildServer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);

		// Initialize data types
		initEDataType(taskRepositoryEDataType, TaskRepository.class, "TaskRepository", IS_SERIALIZABLE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEDataType(buildStateEDataType, BuildState.class, "BuildState", IS_SERIALIZABLE,
				!IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.BuildModel <em>Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.internal.builds.core.BuildModel
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildModel()
		 * @generated
		 */
		public static final EClass BUILD_MODEL = eINSTANCE.getBuildModel();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan <em>Plan</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPlan
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildPlan()
		 * @generated
		 */
		public static final EClass BUILD_PLAN = eINSTANCE.getBuildPlan();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.internal.builds.core.BuildServer <em>Server</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.internal.builds.core.BuildServer
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildServer()
		 * @generated
		 */
		public static final EClass BUILD_SERVER = eINSTANCE.getBuildServer();

		/**
		 * The meta object literal for the '<em><b>Server</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EReference BUILD_SERVER__SERVER = eINSTANCE.getBuildServer_Server();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IBuildModel <em>IBuild Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.builds.core.IBuildModel
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildModel()
		 * @generated
		 */
		public static final EClass IBUILD_MODEL = eINSTANCE.getIBuildModel();

		/**
		 * The meta object literal for the '<em><b>Servers</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EReference IBUILD_MODEL__SERVERS = eINSTANCE.getIBuildModel_Servers();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IBuildElement <em>IBuild Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.builds.core.IBuildElement
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildElement()
		 * @generated
		 */
		public static final EClass IBUILD_ELEMENT = eINSTANCE.getIBuildElement();

		/**
		 * The meta object literal for the '<em><b>Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EAttribute IBUILD_ELEMENT__URL = eINSTANCE.getIBuildElement_Url();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EAttribute IBUILD_ELEMENT__NAME = eINSTANCE.getIBuildElement_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IBuildPlan <em>IBuild Plan</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.builds.core.IBuildPlan
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan()
		 * @generated
		 */
		public static final EClass IBUILD_PLAN = eINSTANCE.getIBuildPlan();

		/**
		 * The meta object literal for the '<em><b>Server</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EReference IBUILD_PLAN__SERVER = eINSTANCE.getIBuildPlan_Server();

		/**
		 * The meta object literal for the '<em><b>Children</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EReference IBUILD_PLAN__CHILDREN = eINSTANCE.getIBuildPlan_Children();

		/**
		 * The meta object literal for the '<em><b>Parent</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EReference IBUILD_PLAN__PARENT = eINSTANCE.getIBuildPlan_Parent();

		/**
		 * The meta object literal for the '<em><b>Health</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EAttribute IBUILD_PLAN__HEALTH = eINSTANCE.getIBuildPlan_Health();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EAttribute IBUILD_PLAN__ID = eINSTANCE.getIBuildPlan_Id();

		/**
		 * The meta object literal for the '<em><b>Summary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EAttribute IBUILD_PLAN__SUMMARY = eINSTANCE.getIBuildPlan_Summary();

		/**
		 * The meta object literal for the '<em><b>Status</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EAttribute IBUILD_PLAN__STATUS = eINSTANCE.getIBuildPlan_Status();

		/**
		 * The meta object literal for the '<em><b>Info</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EAttribute IBUILD_PLAN__INFO = eINSTANCE.getIBuildPlan_Info();

		/**
		 * The meta object literal for the '<em><b>State</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EAttribute IBUILD_PLAN__STATE = eINSTANCE.getIBuildPlan_State();

		/**
		 * The meta object literal for the '<em><b>Selected</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EAttribute IBUILD_PLAN__SELECTED = eINSTANCE.getIBuildPlan_Selected();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IBuildServer <em>IBuild Server</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.builds.core.IBuildServer
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildServer()
		 * @generated
		 */
		public static final EClass IBUILD_SERVER = eINSTANCE.getIBuildServer();

		/**
		 * The meta object literal for the '<em><b>Plans</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EReference IBUILD_SERVER__PLANS = eINSTANCE.getIBuildServer_Plans();

		/**
		 * The meta object literal for the '<em><b>Repository</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EAttribute IBUILD_SERVER__REPOSITORY = eINSTANCE.getIBuildServer_Repository();

		/**
		 * The meta object literal for the '<em><b>Connector Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EAttribute IBUILD_SERVER__CONNECTOR_KIND = eINSTANCE.getIBuildServer_ConnectorKind();

		/**
		 * The meta object literal for the '<em><b>Repository Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public static final EAttribute IBUILD_SERVER__REPOSITORY_URL = eINSTANCE.getIBuildServer_RepositoryUrl();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy <em>IBuild Plan Working Copy</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlanWorkingCopy()
		 * @generated
		 */
		public static final EClass IBUILD_PLAN_WORKING_COPY = eINSTANCE.getIBuildPlanWorkingCopy();

		/**
		 * The meta object literal for the '<em>State</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.builds.core.BuildState
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildState()
		 * @generated
		 */
		public static final EDataType BUILD_STATE = eINSTANCE.getBuildState();

		/**
		 * The meta object literal for the '<em>Task Repository</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.tasks.core.TaskRepository
		 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getTaskRepository()
		 * @generated
		 */
		public static final EDataType TASK_REPOSITORY = eINSTANCE.getTaskRepository();

	}

} //BuildPackage
