/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildParameterDefinition.java,v 1.1 2010/08/27 06:49:23 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;

import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameter Definition</b></em>'.
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildParameterDefinition()
 * @model kind="class"
 * @generated
 */
public class BuildParameterDefinition extends EObjectImpl implements IBuildParameterDefinition {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getBuildPlanId() <em>Build Plan Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getBuildPlanId()
	 * @generated
	 * @ordered
	 */
	protected static final String BUILD_PLAN_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBuildPlanId() <em>Build Plan Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getBuildPlanId()
	 * @generated
	 * @ordered
	 */
	protected String buildPlanId = BUILD_PLAN_ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected BuildParameterDefinition() {
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
		return BuildPackage.Literals.BUILD_PARAMETER_DEFINITION;
	}

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIParameterDefinition_Name()
	 * @model
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildParameterDefinition#getName
	 * <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PARAMETER_DEFINITION__NAME,
					oldName, name));
	}

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIParameterDefinition_Description()
	 * @model
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildParameterDefinition#getDescription
	 * <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PARAMETER_DEFINITION__DESCRIPTION,
					oldDescription, description));
	}

	/**
	 * Returns the value of the '<em><b>Containing Build Plan</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getParameterDefinitions
	 * <em>Parameter Definitions</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Containing Build Plan</em>' container reference isn't clear, there really should be
	 * more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Containing Build Plan</em>' container reference.
	 * @see #setContainingBuildPlan(IBuildPlan)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIParameterDefinition_ContainingBuildPlan()
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getParameterDefinitions
	 * @model type="org.eclipse.mylyn.internal.builds.core.IBuildPlan" opposite="parameterDefinitions" transient="false"
	 * @generated
	 */
	public IBuildPlan getContainingBuildPlan() {
		if (eContainerFeatureID() != BuildPackage.BUILD_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN)
			return null;
		return (IBuildPlan) eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetContainingBuildPlan(IBuildPlan newContainingBuildPlan, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject) newContainingBuildPlan,
				BuildPackage.BUILD_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN, msgs);
		return msgs;
	}

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.mylyn.internal.builds.core.BuildParameterDefinition#getContainingBuildPlan
	 * <em>Containing Build Plan</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Containing Build Plan</em>' container reference.
	 * @see #getContainingBuildPlan()
	 * @generated
	 */
	public void setContainingBuildPlan(IBuildPlan newContainingBuildPlan) {
		if (newContainingBuildPlan != eInternalContainer()
				|| (eContainerFeatureID() != BuildPackage.BUILD_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN && newContainingBuildPlan != null)) {
			if (EcoreUtil.isAncestor(this, (EObject) newContainingBuildPlan))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newContainingBuildPlan != null)
				msgs = ((InternalEObject) newContainingBuildPlan).eInverseAdd(this,
						BuildPackage.IBUILD_PLAN__PARAMETER_DEFINITIONS, IBuildPlan.class, msgs);
			msgs = basicSetContainingBuildPlan(newContainingBuildPlan, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					BuildPackage.BUILD_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN, newContainingBuildPlan,
					newContainingBuildPlan));
	}

	/**
	 * Returns the value of the '<em><b>Build Plan Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Build Plan Id</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Build Plan Id</em>' attribute.
	 * @see #setBuildPlanId(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildParameterDefinition_BuildPlanId()
	 * @model
	 * @generated
	 */
	public String getBuildPlanId() {
		return buildPlanId;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildParameterDefinition#getBuildPlanId
	 * <em>Build Plan Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Build Plan Id</em>' attribute.
	 * @see #getBuildPlanId()
	 * @generated
	 */
	public void setBuildPlanId(String newBuildPlanId) {
		String oldBuildPlanId = buildPlanId;
		buildPlanId = newBuildPlanId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					BuildPackage.BUILD_PARAMETER_DEFINITION__BUILD_PLAN_ID, oldBuildPlanId, buildPlanId));
	}

	/**
	 * Returns the value of the '<em><b>Build Plan</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Build Plan</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Build Plan</em>' reference.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildParameterDefinition_BuildPlan()
	 * @model type="org.eclipse.mylyn.internal.builds.core.IBuildPlan" transient="true" changeable="false"
	 *        volatile="true" derived="true"
	 * @generated
	 */
	public IBuildPlan getBuildPlan() {
		IBuildPlan buildPlan = basicGetBuildPlan();
		return buildPlan != null && ((EObject) buildPlan).eIsProxy() ? (IBuildPlan) eResolveProxy((InternalEObject) buildPlan)
				: buildPlan;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public IBuildPlan basicGetBuildPlan() {
		String buildPlanId = getBuildPlanId();
		if (buildPlanId == null) {
			return null;
		}

		IBuildPlan containingBuildPlan = getContainingBuildPlan();
		if (containingBuildPlan != null) {
			IBuildServer server = containingBuildPlan.getServer(); // TODO Consider derived EReference topLevelBuildPlan
			if (server instanceof EObject) {
				EObject container = ((EObject) server).eContainer(); // TODO Consider eOpposite for IBuildModel.plans, ...
				if (container instanceof IBuildModel) {
					List<IBuildPlan> plans = ((IBuildModel) container).getPlans();
					for (IBuildPlan plan : plans) {
						if (buildPlanId.equals(plan.getId())) {
							return plan;
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case BuildPackage.BUILD_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN:
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			return basicSetContainingBuildPlan((IBuildPlan) otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
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
		case BuildPackage.BUILD_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN:
			return basicSetContainingBuildPlan(null, msgs);
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
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
		case BuildPackage.BUILD_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN:
			return eInternalContainer().eInverseRemove(this, BuildPackage.IBUILD_PLAN__PARAMETER_DEFINITIONS,
					IBuildPlan.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
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
		case BuildPackage.BUILD_PARAMETER_DEFINITION__NAME:
			return getName();
		case BuildPackage.BUILD_PARAMETER_DEFINITION__DESCRIPTION:
			return getDescription();
		case BuildPackage.BUILD_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN:
			return getContainingBuildPlan();
		case BuildPackage.BUILD_PARAMETER_DEFINITION__BUILD_PLAN_ID:
			return getBuildPlanId();
		case BuildPackage.BUILD_PARAMETER_DEFINITION__BUILD_PLAN:
			if (resolve)
				return getBuildPlan();
			return basicGetBuildPlan();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case BuildPackage.BUILD_PARAMETER_DEFINITION__NAME:
			setName((String) newValue);
			return;
		case BuildPackage.BUILD_PARAMETER_DEFINITION__DESCRIPTION:
			setDescription((String) newValue);
			return;
		case BuildPackage.BUILD_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN:
			setContainingBuildPlan((IBuildPlan) newValue);
			return;
		case BuildPackage.BUILD_PARAMETER_DEFINITION__BUILD_PLAN_ID:
			setBuildPlanId((String) newValue);
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
		case BuildPackage.BUILD_PARAMETER_DEFINITION__NAME:
			setName(NAME_EDEFAULT);
			return;
		case BuildPackage.BUILD_PARAMETER_DEFINITION__DESCRIPTION:
			setDescription(DESCRIPTION_EDEFAULT);
			return;
		case BuildPackage.BUILD_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN:
			setContainingBuildPlan((IBuildPlan) null);
			return;
		case BuildPackage.BUILD_PARAMETER_DEFINITION__BUILD_PLAN_ID:
			setBuildPlanId(BUILD_PLAN_ID_EDEFAULT);
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
		case BuildPackage.BUILD_PARAMETER_DEFINITION__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case BuildPackage.BUILD_PARAMETER_DEFINITION__DESCRIPTION:
			return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
		case BuildPackage.BUILD_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN:
			return getContainingBuildPlan() != null;
		case BuildPackage.BUILD_PARAMETER_DEFINITION__BUILD_PLAN_ID:
			return BUILD_PLAN_ID_EDEFAULT == null ? buildPlanId != null : !BUILD_PLAN_ID_EDEFAULT.equals(buildPlanId);
		case BuildPackage.BUILD_PARAMETER_DEFINITION__BUILD_PLAN:
			return basicGetBuildPlan() != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(", description: ");
		result.append(description);
		result.append(", buildPlanId: ");
		result.append(buildPlanId);
		result.append(')');
		return result.toString();
	}

} // BuildParameterDefinition
