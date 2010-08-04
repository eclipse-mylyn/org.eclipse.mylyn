/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildModel.java,v 1.5 2010/08/04 07:38:41 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.commons.core.IOperationMonitor;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildModel()
 * @model kind="class" superTypes="org.eclipse.mylyn.internal.builds.core.IBuildModel"
 * @generated
 */
public class BuildModel extends EObjectImpl implements EObject, IBuildModel {
	/**
	 * The cached value of the '{@link #getServers() <em>Servers</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getServers()
	 * @generated
	 * @ordered
	 */
	protected EList<IBuildServer> servers;

	/**
	 * The cached value of the '{@link #getPlans() <em>Plans</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getPlans()
	 * @generated
	 * @ordered
	 */
	protected EList<IBuildPlan> plans;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected BuildModel() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BuildPackage.Literals.BUILD_MODEL;
	}

	/**
	 * Returns the value of the '<em><b>Servers</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.builds.core.IBuildServer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Servers</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Servers</em>' containment reference list.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildModel_Servers()
	 * @model type="org.eclipse.mylyn.internal.builds.core.IBuildServer" containment="true"
	 * @generated
	 */
	public EList<IBuildServer> getServers() {
		if (servers == null) {
			servers = new EObjectContainmentEList<IBuildServer>(IBuildServer.class, this,
					BuildPackage.BUILD_MODEL__SERVERS);
		}
		return servers;
	}

	/**
	 * Returns the value of the '<em><b>Plans</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.builds.core.IBuildPlan}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Plans</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Plans</em>' containment reference list.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildModel_Plans()
	 * @model type="org.eclipse.mylyn.internal.builds.core.IBuildPlan" containment="true" ordered="false"
	 * @generated
	 */
	public EList<IBuildPlan> getPlans() {
		if (plans == null) {
			plans = new EObjectContainmentEList<IBuildPlan>(IBuildPlan.class, this, BuildPackage.BUILD_MODEL__PLANS);
		}
		return plans;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
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
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
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
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
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
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
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
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
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
		List<IBuildPlan> result = new ArrayList<IBuildPlan>();
		for (IBuildPlan plan : getPlans()) {
			if (plan.getServer() == server) {
				result.add(plan);
			}
		}
		return result;
	}

} // BuildModel
