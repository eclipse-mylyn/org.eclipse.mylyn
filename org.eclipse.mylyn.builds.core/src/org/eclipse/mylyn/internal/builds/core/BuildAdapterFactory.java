/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildAdapterFactory.java,v 1.5 2010/07/03 06:40:32 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import java.util.Map;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildPlanData;
import org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy;
import org.eclipse.mylyn.builds.core.IBuildServer;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage
 * @generated
 */
public class BuildAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static BuildPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BuildAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = BuildPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject) object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BuildSwitch<Adapter> modelSwitch = new BuildSwitch<Adapter>() {
		@Override
		public Adapter caseBuildModel(BuildModel object) {
			return createBuildModelAdapter();
		}

		@Override
		public Adapter caseBuildPlan(BuildPlan object) {
			return createBuildPlanAdapter();
		}

		@Override
		public Adapter caseBuildServer(BuildServer object) {
			return createBuildServerAdapter();
		}

		@Override
		public Adapter caseIBuildModel(IBuildModel object) {
			return createIBuildModelAdapter();
		}

		@Override
		public Adapter caseIBuildElement(IBuildElement object) {
			return createIBuildElementAdapter();
		}

		@Override
		public Adapter caseIBuildPlan(IBuildPlan object) {
			return createIBuildPlanAdapter();
		}

		@Override
		public Adapter caseIBuildPlanData(IBuildPlanData object) {
			return createIBuildPlanDataAdapter();
		}

		@Override
		public Adapter caseIBuildPlanWorkingCopy(IBuildPlanWorkingCopy object) {
			return createIBuildPlanWorkingCopyAdapter();
		}

		@Override
		public Adapter caseIBuildServer(IBuildServer object) {
			return createIBuildServerAdapter();
		}

		@Override
		public Adapter caseStringToStringMap(Map.Entry<String, String> object) {
			return createStringToStringMapAdapter();
		}

		@Override
		public Adapter defaultCase(EObject object) {
			return createEObjectAdapter();
		}
	};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject) target);
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.internal.builds.core.BuildModel <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildModel
	 * @generated
	 */
	public Adapter createBuildModelAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan <em>Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPlan
	 * @generated
	 */
	public Adapter createBuildPlanAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.internal.builds.core.BuildServer <em>Server</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildServer
	 * @generated
	 */
	public Adapter createBuildServerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.builds.core.IBuildModel <em>IBuild Model</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.builds.core.IBuildModel
	 * @generated
	 */
	public Adapter createIBuildModelAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.builds.core.IBuildElement <em>IBuild Element</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.builds.core.IBuildElement
	 * @generated
	 */
	public Adapter createIBuildElementAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.builds.core.IBuildPlan <em>IBuild Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan
	 * @generated
	 */
	public Adapter createIBuildPlanAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.builds.core.IBuildPlanData <em>IBuild Plan Data</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlanData
	 * @generated
	 */
	public Adapter createIBuildPlanDataAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy <em>IBuild Plan Working Copy</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy
	 * @generated
	 */
	public Adapter createIBuildPlanWorkingCopyAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.builds.core.IBuildServer <em>IBuild Server</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.builds.core.IBuildServer
	 * @generated
	 */
	public Adapter createIBuildServerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>String To String Map</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public Adapter createStringToStringMapAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //BuildAdapterFactory
