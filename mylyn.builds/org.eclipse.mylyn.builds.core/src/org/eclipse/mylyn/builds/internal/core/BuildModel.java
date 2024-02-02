/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildModel.java,v 1.2 2010/08/28 09:21:40 spingel Exp $
 */
package org.eclipse.mylyn.builds.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.internal.core.util.BuildScheduler;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Model</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildModel#getServers <em>Servers</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildModel#getPlans <em>Plans</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildModel#getBuilds <em>Builds</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BuildModel extends EObjectImpl implements IBuildModel {
	/**
	 * The cached value of the '{@link #getServers() <em>Servers</em>}' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see #getServers()
	 * @generated
	 * @ordered
	 */
	protected EList<IBuildServer> servers;

	/**
	 * The cached value of the '{@link #getPlans() <em>Plans</em>}' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see #getPlans()
	 * @generated
	 * @ordered
	 */
	protected EList<IBuildPlan> plans;

	/**
	 * The cached value of the '{@link #getBuilds() <em>Builds</em>}' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see #getBuilds()
	 * @generated
	 * @ordered
	 */
	protected EList<IBuild> builds;

	private BuildScheduler scheduler;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected BuildModel() {
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BuildPackage.Literals.BUILD_MODEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Servers</em>' containment reference list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public List<IBuildServer> getServers() {
		if (servers == null) {
			servers = new EObjectContainmentEList<>(IBuildServer.class, this,
					BuildPackage.BUILD_MODEL__SERVERS);
		}
		return servers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Plans</em>' containment reference list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public List<IBuildPlan> getPlans() {
		if (plans == null) {
			plans = new EObjectContainmentEList<>(IBuildPlan.class, this, BuildPackage.BUILD_MODEL__PLANS);
		}
		return plans;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Builds</em>' containment reference list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public List<IBuild> getBuilds() {
		if (builds == null) {
			builds = new EObjectContainmentEList<>(IBuild.class, this, BuildPackage.BUILD_MODEL__BUILDS);
		}
		return builds;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case BuildPackage.BUILD_MODEL__SERVERS:
				return ((InternalEList<?>) getServers()).basicRemove(otherEnd, msgs);
			case BuildPackage.BUILD_MODEL__PLANS:
				return ((InternalEList<?>) getPlans()).basicRemove(otherEnd, msgs);
			case BuildPackage.BUILD_MODEL__BUILDS:
				return ((InternalEList<?>) getBuilds()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case BuildPackage.BUILD_MODEL__SERVERS:
				return getServers();
			case BuildPackage.BUILD_MODEL__PLANS:
				return getPlans();
			case BuildPackage.BUILD_MODEL__BUILDS:
				return getBuilds();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case BuildPackage.BUILD_MODEL__SERVERS:
				getServers().clear();
				getServers().addAll((Collection<? extends IBuildServer>) newValue);
				return;
			case BuildPackage.BUILD_MODEL__PLANS:
				getPlans().clear();
				getPlans().addAll((Collection<? extends IBuildPlan>) newValue);
				return;
			case BuildPackage.BUILD_MODEL__BUILDS:
				getBuilds().clear();
				getBuilds().addAll((Collection<? extends IBuild>) newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case BuildPackage.BUILD_MODEL__SERVERS:
				getServers().clear();
				return;
			case BuildPackage.BUILD_MODEL__PLANS:
				getPlans().clear();
				return;
			case BuildPackage.BUILD_MODEL__BUILDS:
				getBuilds().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case BuildPackage.BUILD_MODEL__SERVERS:
				return servers != null && !servers.isEmpty();
			case BuildPackage.BUILD_MODEL__PLANS:
				return plans != null && !plans.isEmpty();
			case BuildPackage.BUILD_MODEL__BUILDS:
				return builds != null && !builds.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	public void refresh(IOperationMonitor monitor) {
		// FIXME implement
		//new RefreshPlansOperation(Collections.singletonList((IBuildServer) this)).run(monitor);
	}

	public IBuildPlan getPlanById(String id) {
		if (id != null) {
			for (IBuildPlan plan : getPlans()) {
				if (id.equals(plan.getName())) {
					return plan;
				}
			}
		}
		return null;
	}

	public List<IBuildPlan> getPlans(IBuildServer server) {
		List<IBuildPlan> result = new ArrayList<>();
		for (IBuildPlan plan : getPlans()) {
			if (plan.getServer() == server) {
				result.add(plan);
			}
		}
		return result;
	}

	public synchronized void setScheduler(BuildScheduler scheduler) {
		this.scheduler = scheduler;
	}

	public synchronized BuildScheduler getScheduler() {
		if (scheduler == null) {
			scheduler = new BuildScheduler();
		}
		return scheduler;
	}

	private IBuildLoader loader;

	public IBuildLoader getLoader() {
		return loader;
	}

	public void setLoader(IBuildLoader loader) {
		this.loader = loader;
	}

} // BuildModel
