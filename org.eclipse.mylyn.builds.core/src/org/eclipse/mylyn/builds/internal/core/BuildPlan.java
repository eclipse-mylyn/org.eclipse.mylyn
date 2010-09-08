/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildPlan.java,v 1.5 2010/09/08 00:31:12 spingel Exp $
 */
package org.eclipse.mylyn.builds.internal.core;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.IHealthReport;
import org.eclipse.mylyn.builds.core.IParameterDefinition;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Plan</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildPlan#getServer <em>Server</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildPlan#getChildren <em>Children</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildPlan#getParent <em>Parent</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildPlan#getHealth <em>Health</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildPlan#getId <em>Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildPlan#getInfo <em>Info</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildPlan#isSelected <em>Selected</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildPlan#getSummary <em>Summary</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildPlan#getState <em>State</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildPlan#getStatus <em>Status</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildPlan#getDescription <em>Description</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildPlan#getLastBuild <em>Last Build</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildPlan#getParameterDefinitions <em>Parameter Definitions</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildPlan#getHealthReports <em>Health Reports</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class BuildPlan extends BuildElement implements IBuildPlan {
	/**
	 * The cached value of the '{@link #getServer() <em>Server</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getServer()
	 * @generated
	 * @ordered
	 */
	protected IBuildServer server;

	/**
	 * The cached value of the '{@link #getChildren() <em>Children</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getChildren()
	 * @generated
	 * @ordered
	 */
	protected EList<IBuildPlan> children;

	/**
	 * The cached value of the '{@link #getParent() <em>Parent</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getParent()
	 * @generated
	 * @ordered
	 */
	protected IBuildPlan parent;

	/**
	 * The default value of the '{@link #getHealth() <em>Health</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getHealth()
	 * @generated
	 * @ordered
	 */
	protected static final int HEALTH_EDEFAULT = -1;

	/**
	 * The cached value of the '{@link #getHealth() <em>Health</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getHealth()
	 * @generated
	 * @ordered
	 */
	protected int health = HEALTH_EDEFAULT;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getInfo() <em>Info</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getInfo()
	 * @generated
	 * @ordered
	 */
	protected static final String INFO_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getInfo() <em>Info</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getInfo()
	 * @generated
	 * @ordered
	 */
	protected String info = INFO_EDEFAULT;

	/**
	 * The default value of the '{@link #isSelected() <em>Selected</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #isSelected()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SELECTED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isSelected() <em>Selected</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #isSelected()
	 * @generated
	 * @ordered
	 */
	protected boolean selected = SELECTED_EDEFAULT;

	/**
	 * The default value of the '{@link #getSummary() <em>Summary</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getSummary()
	 * @generated
	 * @ordered
	 */
	protected static final String SUMMARY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSummary() <em>Summary</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getSummary()
	 * @generated
	 * @ordered
	 */
	protected String summary = SUMMARY_EDEFAULT;

	/**
	 * The default value of the '{@link #getState() <em>State</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getState()
	 * @generated
	 * @ordered
	 */
	protected static final BuildState STATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getState() <em>State</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getState()
	 * @generated
	 * @ordered
	 */
	protected BuildState state = STATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getStatus() <em>Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getStatus()
	 * @generated
	 * @ordered
	 */
	protected static final BuildStatus STATUS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getStatus() <em>Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getStatus()
	 * @generated
	 * @ordered
	 */
	protected BuildStatus status = STATUS_EDEFAULT;

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
	 * The cached value of the '{@link #getLastBuild() <em>Last Build</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getLastBuild()
	 * @generated
	 * @ordered
	 */
	protected IBuild lastBuild;

	/**
	 * The cached value of the '{@link #getParameterDefinitions() <em>Parameter Definitions</em>}' containment reference
	 * list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getParameterDefinitions()
	 * @generated
	 * @ordered
	 */
	protected EList<IParameterDefinition> parameterDefinitions;

	/**
	 * The cached value of the '{@link #getHealthReports() <em>Health Reports</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getHealthReports()
	 * @generated
	 * @ordered
	 */
	protected EList<IHealthReport> healthReports;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected BuildPlan() {
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
		return BuildPackage.Literals.BUILD_PLAN;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Server</em>' container reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IBuildServer getServer() {
		if (server != null && ((EObject) server).eIsProxy()) {
			InternalEObject oldServer = (InternalEObject) server;
			server = (IBuildServer) eResolveProxy(oldServer);
			if (server != oldServer) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, BuildPackage.BUILD_PLAN__SERVER,
							oldServer, server));
			}
		}
		return server;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IBuildServer basicGetServer() {
		return server;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setServer(IBuildServer newServer) {
		IBuildServer oldServer = server;
		server = newServer;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__SERVER, oldServer, server));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Children</em>' reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<IBuildPlan> getChildren() {
		if (children == null) {
			children = new EObjectWithInverseResolvingEList<IBuildPlan>(IBuildPlan.class, this,
					BuildPackage.BUILD_PLAN__CHILDREN, BuildPackage.BUILD_PLAN__PARENT);
		}
		return children;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IBuildPlan getParent() {
		if (parent != null && ((EObject) parent).eIsProxy()) {
			InternalEObject oldParent = (InternalEObject) parent;
			parent = (IBuildPlan) eResolveProxy(oldParent);
			if (parent != oldParent) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, BuildPackage.BUILD_PLAN__PARENT,
							oldParent, parent));
			}
		}
		return parent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IBuildPlan basicGetParent() {
		return parent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetParent(IBuildPlan newParent, NotificationChain msgs) {
		IBuildPlan oldParent = parent;
		parent = newParent;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					BuildPackage.BUILD_PLAN__PARENT, oldParent, newParent);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setParent(IBuildPlan newParent) {
		if (newParent != parent) {
			NotificationChain msgs = null;
			if (parent != null)
				msgs = ((InternalEObject) parent).eInverseRemove(this, BuildPackage.BUILD_PLAN__CHILDREN,
						IBuildPlan.class, msgs);
			if (newParent != null)
				msgs = ((InternalEObject) newParent).eInverseAdd(this, BuildPackage.BUILD_PLAN__CHILDREN,
						IBuildPlan.class, msgs);
			msgs = basicSetParent(newParent, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__PARENT, newParent, newParent));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Health</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setHealth(int newHealth) {
		int oldHealth = health;
		health = newHealth;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__HEALTH, oldHealth, health));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Info</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setInfo(String newInfo) {
		String oldInfo = info;
		info = newInfo;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__INFO, oldInfo, info));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Selected</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setSelected(boolean newSelected) {
		boolean oldSelected = selected;
		selected = newSelected;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__SELECTED, oldSelected,
					selected));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Summary</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setSummary(String newSummary) {
		String oldSummary = summary;
		summary = newSummary;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__SUMMARY, oldSummary, summary));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>State</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public BuildState getState() {
		return state;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setState(BuildState newState) {
		BuildState oldState = state;
		state = newState;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__STATE, oldState, state));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Status</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public BuildStatus getStatus() {
		return status;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setStatus(BuildStatus newStatus) {
		BuildStatus oldStatus = status;
		status = newStatus;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__STATUS, oldStatus, status));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__DESCRIPTION, oldDescription,
					description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Last Build</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IBuild getLastBuild() {
		if (lastBuild != null && ((EObject) lastBuild).eIsProxy()) {
			InternalEObject oldLastBuild = (InternalEObject) lastBuild;
			lastBuild = (IBuild) eResolveProxy(oldLastBuild);
			if (lastBuild != oldLastBuild) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, BuildPackage.BUILD_PLAN__LAST_BUILD,
							oldLastBuild, lastBuild));
			}
		}
		return lastBuild;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IBuild basicGetLastBuild() {
		return lastBuild;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setLastBuild(IBuild newLastBuild) {
		IBuild oldLastBuild = lastBuild;
		lastBuild = newLastBuild;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__LAST_BUILD, oldLastBuild,
					lastBuild));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Definitions</em>' containment reference list isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<IParameterDefinition> getParameterDefinitions() {
		if (parameterDefinitions == null) {
			parameterDefinitions = new EObjectContainmentWithInverseEList<IParameterDefinition>(
					IParameterDefinition.class, this, BuildPackage.BUILD_PLAN__PARAMETER_DEFINITIONS,
					BuildPackage.PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN);
		}
		return parameterDefinitions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<IHealthReport> getHealthReports() {
		if (healthReports == null) {
			healthReports = new EObjectContainmentEList<IHealthReport>(IHealthReport.class, this,
					BuildPackage.BUILD_PLAN__HEALTH_REPORTS);
		}
		return healthReports;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case BuildPackage.BUILD_PLAN__CHILDREN:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getChildren()).basicAdd(otherEnd, msgs);
		case BuildPackage.BUILD_PLAN__PARENT:
			if (parent != null)
				msgs = ((InternalEObject) parent).eInverseRemove(this, BuildPackage.BUILD_PLAN__CHILDREN,
						IBuildPlan.class, msgs);
			return basicSetParent((IBuildPlan) otherEnd, msgs);
		case BuildPackage.BUILD_PLAN__PARAMETER_DEFINITIONS:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getParameterDefinitions()).basicAdd(otherEnd,
					msgs);
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
		case BuildPackage.BUILD_PLAN__CHILDREN:
			return ((InternalEList<?>) getChildren()).basicRemove(otherEnd, msgs);
		case BuildPackage.BUILD_PLAN__PARENT:
			return basicSetParent(null, msgs);
		case BuildPackage.BUILD_PLAN__PARAMETER_DEFINITIONS:
			return ((InternalEList<?>) getParameterDefinitions()).basicRemove(otherEnd, msgs);
		case BuildPackage.BUILD_PLAN__HEALTH_REPORTS:
			return ((InternalEList<?>) getHealthReports()).basicRemove(otherEnd, msgs);
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
		case BuildPackage.BUILD_PLAN__SERVER:
			if (resolve)
				return getServer();
			return basicGetServer();
		case BuildPackage.BUILD_PLAN__CHILDREN:
			return getChildren();
		case BuildPackage.BUILD_PLAN__PARENT:
			if (resolve)
				return getParent();
			return basicGetParent();
		case BuildPackage.BUILD_PLAN__HEALTH:
			return getHealth();
		case BuildPackage.BUILD_PLAN__ID:
			return getId();
		case BuildPackage.BUILD_PLAN__INFO:
			return getInfo();
		case BuildPackage.BUILD_PLAN__SELECTED:
			return isSelected();
		case BuildPackage.BUILD_PLAN__SUMMARY:
			return getSummary();
		case BuildPackage.BUILD_PLAN__STATE:
			return getState();
		case BuildPackage.BUILD_PLAN__STATUS:
			return getStatus();
		case BuildPackage.BUILD_PLAN__DESCRIPTION:
			return getDescription();
		case BuildPackage.BUILD_PLAN__LAST_BUILD:
			if (resolve)
				return getLastBuild();
			return basicGetLastBuild();
		case BuildPackage.BUILD_PLAN__PARAMETER_DEFINITIONS:
			return getParameterDefinitions();
		case BuildPackage.BUILD_PLAN__HEALTH_REPORTS:
			return getHealthReports();
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
		case BuildPackage.BUILD_PLAN__SERVER:
			setServer((IBuildServer) newValue);
			return;
		case BuildPackage.BUILD_PLAN__CHILDREN:
			getChildren().clear();
			getChildren().addAll((Collection<? extends IBuildPlan>) newValue);
			return;
		case BuildPackage.BUILD_PLAN__PARENT:
			setParent((IBuildPlan) newValue);
			return;
		case BuildPackage.BUILD_PLAN__HEALTH:
			setHealth((Integer) newValue);
			return;
		case BuildPackage.BUILD_PLAN__ID:
			setId((String) newValue);
			return;
		case BuildPackage.BUILD_PLAN__INFO:
			setInfo((String) newValue);
			return;
		case BuildPackage.BUILD_PLAN__SELECTED:
			setSelected((Boolean) newValue);
			return;
		case BuildPackage.BUILD_PLAN__SUMMARY:
			setSummary((String) newValue);
			return;
		case BuildPackage.BUILD_PLAN__STATE:
			setState((BuildState) newValue);
			return;
		case BuildPackage.BUILD_PLAN__STATUS:
			setStatus((BuildStatus) newValue);
			return;
		case BuildPackage.BUILD_PLAN__DESCRIPTION:
			setDescription((String) newValue);
			return;
		case BuildPackage.BUILD_PLAN__LAST_BUILD:
			setLastBuild((IBuild) newValue);
			return;
		case BuildPackage.BUILD_PLAN__PARAMETER_DEFINITIONS:
			getParameterDefinitions().clear();
			getParameterDefinitions().addAll((Collection<? extends IParameterDefinition>) newValue);
			return;
		case BuildPackage.BUILD_PLAN__HEALTH_REPORTS:
			getHealthReports().clear();
			getHealthReports().addAll((Collection<? extends IHealthReport>) newValue);
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
		case BuildPackage.BUILD_PLAN__SERVER:
			setServer((IBuildServer) null);
			return;
		case BuildPackage.BUILD_PLAN__CHILDREN:
			getChildren().clear();
			return;
		case BuildPackage.BUILD_PLAN__PARENT:
			setParent((IBuildPlan) null);
			return;
		case BuildPackage.BUILD_PLAN__HEALTH:
			setHealth(HEALTH_EDEFAULT);
			return;
		case BuildPackage.BUILD_PLAN__ID:
			setId(ID_EDEFAULT);
			return;
		case BuildPackage.BUILD_PLAN__INFO:
			setInfo(INFO_EDEFAULT);
			return;
		case BuildPackage.BUILD_PLAN__SELECTED:
			setSelected(SELECTED_EDEFAULT);
			return;
		case BuildPackage.BUILD_PLAN__SUMMARY:
			setSummary(SUMMARY_EDEFAULT);
			return;
		case BuildPackage.BUILD_PLAN__STATE:
			setState(STATE_EDEFAULT);
			return;
		case BuildPackage.BUILD_PLAN__STATUS:
			setStatus(STATUS_EDEFAULT);
			return;
		case BuildPackage.BUILD_PLAN__DESCRIPTION:
			setDescription(DESCRIPTION_EDEFAULT);
			return;
		case BuildPackage.BUILD_PLAN__LAST_BUILD:
			setLastBuild((IBuild) null);
			return;
		case BuildPackage.BUILD_PLAN__PARAMETER_DEFINITIONS:
			getParameterDefinitions().clear();
			return;
		case BuildPackage.BUILD_PLAN__HEALTH_REPORTS:
			getHealthReports().clear();
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
		case BuildPackage.BUILD_PLAN__SERVER:
			return server != null;
		case BuildPackage.BUILD_PLAN__CHILDREN:
			return children != null && !children.isEmpty();
		case BuildPackage.BUILD_PLAN__PARENT:
			return parent != null;
		case BuildPackage.BUILD_PLAN__HEALTH:
			return health != HEALTH_EDEFAULT;
		case BuildPackage.BUILD_PLAN__ID:
			return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
		case BuildPackage.BUILD_PLAN__INFO:
			return INFO_EDEFAULT == null ? info != null : !INFO_EDEFAULT.equals(info);
		case BuildPackage.BUILD_PLAN__SELECTED:
			return selected != SELECTED_EDEFAULT;
		case BuildPackage.BUILD_PLAN__SUMMARY:
			return SUMMARY_EDEFAULT == null ? summary != null : !SUMMARY_EDEFAULT.equals(summary);
		case BuildPackage.BUILD_PLAN__STATE:
			return STATE_EDEFAULT == null ? state != null : !STATE_EDEFAULT.equals(state);
		case BuildPackage.BUILD_PLAN__STATUS:
			return STATUS_EDEFAULT == null ? status != null : !STATUS_EDEFAULT.equals(status);
		case BuildPackage.BUILD_PLAN__DESCRIPTION:
			return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
		case BuildPackage.BUILD_PLAN__LAST_BUILD:
			return lastBuild != null;
		case BuildPackage.BUILD_PLAN__PARAMETER_DEFINITIONS:
			return parameterDefinitions != null && !parameterDefinitions.isEmpty();
		case BuildPackage.BUILD_PLAN__HEALTH_REPORTS:
			return healthReports != null && !healthReports.isEmpty();
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
		result.append(" (health: "); //$NON-NLS-1$
		result.append(health);
		result.append(", id: "); //$NON-NLS-1$
		result.append(id);
		result.append(", info: "); //$NON-NLS-1$
		result.append(info);
		result.append(", selected: "); //$NON-NLS-1$
		result.append(selected);
		result.append(", summary: "); //$NON-NLS-1$
		result.append(summary);
		result.append(", state: "); //$NON-NLS-1$
		result.append(state);
		result.append(", status: "); //$NON-NLS-1$
		result.append(status);
		result.append(", description: "); //$NON-NLS-1$
		result.append(description);
		result.append(')');
		return result.toString();
	}

	public IBuildPlan toBuildPlan() {
		return this;
	}

	private IStatus operationStatus;

	public IStatus getOperationStatus() {
		return operationStatus;
	}

	public void setOperationStatus(IStatus operationStatus) {
		this.operationStatus = operationStatus;
	}

	public void merge(BuildPlan source) {
		Assert.isNotNull(source);
		EcoreUtil.Copier copier = new EcoreUtil.Copier() {
			@Override
			protected EObject createCopy(EObject source) {
				return BuildPlan.this; // TODO This will certainly fail for nested plans!
			};

			@Override
			protected void copyAttribute(EAttribute eAttribute, EObject eObject, EObject copyEObject) {
				super.copyAttribute(eAttribute, eObject, copyEObject);
			}

			@Override
			protected void copyContainment(EReference eReference, EObject eObject, EObject copyEObject) {
				// XXX do nothing, throws a ClassCastException due to overridded createCopy() method
			}
		};
		copier.copy(source);

		// FIXME implement proper merge
		getParameterDefinitions().clear();
		getParameterDefinitions().addAll(EcoreUtil.copyAll(source.getParameterDefinitions()));

		getHealthReports().clear();
		getHealthReports().addAll(EcoreUtil.copyAll(source.getHealthReports()));
	}

	@Override
	public String getLabel() {
		return getName();
	}

	@Override
	public BuildPlan createWorkingCopy() {
		return (BuildPlan) super.createWorkingCopy();
	}

} // BuildPlan
