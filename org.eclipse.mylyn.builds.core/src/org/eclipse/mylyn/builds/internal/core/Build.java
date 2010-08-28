/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.builds.internal.core;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IArtifact;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.IChangeSet;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Build</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Build#getId <em>Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Build#getBuildNumber <em>Build Number</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Build#getTimestamp <em>Timestamp</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Build#getDuration <em>Duration</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Build#getDisplayName <em>Display Name</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Build#getState <em>State</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Build#getStatus <em>Status</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Build#getArtifacts <em>Artifacts</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Build#getChangeSet <em>Change Set</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Build#getPlan <em>Plan</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Build#getLabel <em>Label</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.Build#getServer <em>Server</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class Build extends BuildElement implements IBuild {
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
	 * The default value of the '{@link #getBuildNumber() <em>Build Number</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getBuildNumber()
	 * @generated
	 * @ordered
	 */
	protected static final int BUILD_NUMBER_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getBuildNumber() <em>Build Number</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getBuildNumber()
	 * @generated
	 * @ordered
	 */
	protected int buildNumber = BUILD_NUMBER_EDEFAULT;

	/**
	 * The default value of the '{@link #getTimestamp() <em>Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getTimestamp()
	 * @generated
	 * @ordered
	 */
	protected static final long TIMESTAMP_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getTimestamp() <em>Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getTimestamp()
	 * @generated
	 * @ordered
	 */
	protected long timestamp = TIMESTAMP_EDEFAULT;

	/**
	 * The default value of the '{@link #getDuration() <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getDuration()
	 * @generated
	 * @ordered
	 */
	protected static final long DURATION_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getDuration() <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getDuration()
	 * @generated
	 * @ordered
	 */
	protected long duration = DURATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getDisplayName() <em>Display Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getDisplayName()
	 * @generated
	 * @ordered
	 */
	protected static final String DISPLAY_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDisplayName() <em>Display Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getDisplayName()
	 * @generated
	 * @ordered
	 */
	protected String displayName = DISPLAY_NAME_EDEFAULT;

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
	 * The cached value of the '{@link #getArtifacts() <em>Artifacts</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getArtifacts()
	 * @generated
	 * @ordered
	 */
	protected EList<IArtifact> artifacts;

	/**
	 * The cached value of the '{@link #getChangeSet() <em>Change Set</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getChangeSet()
	 * @generated
	 * @ordered
	 */
	protected IChangeSet changeSet;

	/**
	 * The cached value of the '{@link #getPlan() <em>Plan</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getPlan()
	 * @generated
	 * @ordered
	 */
	protected IBuildPlan plan;

	/**
	 * The default value of the '{@link #getLabel() <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getLabel()
	 * @generated
	 * @ordered
	 */
	protected static final String LABEL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLabel() <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getLabel()
	 * @generated
	 * @ordered
	 */
	protected String label = LABEL_EDEFAULT;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Build() {
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
		return BuildPackage.Literals.BUILD;
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
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Build Number</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public int getBuildNumber() {
		return buildNumber;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setBuildNumber(int newBuildNumber) {
		int oldBuildNumber = buildNumber;
		buildNumber = newBuildNumber;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD__BUILD_NUMBER, oldBuildNumber,
					buildNumber));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Timestamp</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setTimestamp(long newTimestamp) {
		long oldTimestamp = timestamp;
		timestamp = newTimestamp;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD__TIMESTAMP, oldTimestamp,
					timestamp));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Duration</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setDuration(long newDuration) {
		long oldDuration = duration;
		duration = newDuration;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD__DURATION, oldDuration, duration));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Display Name</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setDisplayName(String newDisplayName) {
		String oldDisplayName = displayName;
		displayName = newDisplayName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD__DISPLAY_NAME, oldDisplayName,
					displayName));
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
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD__STATE, oldState, state));
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
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD__STATUS, oldStatus, status));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Artifacts</em>' reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<IArtifact> getArtifacts() {
		if (artifacts == null) {
			artifacts = new EObjectResolvingEList<IArtifact>(IArtifact.class, this, BuildPackage.BUILD__ARTIFACTS);
		}
		return artifacts;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Change Set</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IChangeSet getChangeSet() {
		if (changeSet != null && ((EObject) changeSet).eIsProxy()) {
			InternalEObject oldChangeSet = (InternalEObject) changeSet;
			changeSet = (IChangeSet) eResolveProxy(oldChangeSet);
			if (changeSet != oldChangeSet) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, BuildPackage.BUILD__CHANGE_SET,
							oldChangeSet, changeSet));
			}
		}
		return changeSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IChangeSet basicGetChangeSet() {
		return changeSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setChangeSet(IChangeSet newChangeSet) {
		IChangeSet oldChangeSet = changeSet;
		changeSet = newChangeSet;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD__CHANGE_SET, oldChangeSet,
					changeSet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Plan</em>' reference isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IBuildPlan getPlan() {
		if (plan != null && ((EObject) plan).eIsProxy()) {
			InternalEObject oldPlan = (InternalEObject) plan;
			plan = (IBuildPlan) eResolveProxy(oldPlan);
			if (plan != oldPlan) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, BuildPackage.BUILD__PLAN, oldPlan, plan));
			}
		}
		return plan;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IBuildPlan basicGetPlan() {
		return plan;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setPlan(IBuildPlan newPlan) {
		IBuildPlan oldPlan = plan;
		plan = newPlan;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD__PLAN, oldPlan, plan));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Label</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setLabel(String newLabel) {
		String oldLabel = label;
		label = newLabel;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD__LABEL, oldLabel, label));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Server</em>' reference isn't clear, there really should be more of a description
	 * here...
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
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, BuildPackage.BUILD__SERVER, oldServer,
							server));
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
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD__SERVER, oldServer, server));
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
		case BuildPackage.BUILD__ID:
			return getId();
		case BuildPackage.BUILD__BUILD_NUMBER:
			return getBuildNumber();
		case BuildPackage.BUILD__TIMESTAMP:
			return getTimestamp();
		case BuildPackage.BUILD__DURATION:
			return getDuration();
		case BuildPackage.BUILD__DISPLAY_NAME:
			return getDisplayName();
		case BuildPackage.BUILD__STATE:
			return getState();
		case BuildPackage.BUILD__STATUS:
			return getStatus();
		case BuildPackage.BUILD__ARTIFACTS:
			return getArtifacts();
		case BuildPackage.BUILD__CHANGE_SET:
			if (resolve)
				return getChangeSet();
			return basicGetChangeSet();
		case BuildPackage.BUILD__PLAN:
			if (resolve)
				return getPlan();
			return basicGetPlan();
		case BuildPackage.BUILD__LABEL:
			return getLabel();
		case BuildPackage.BUILD__SERVER:
			if (resolve)
				return getServer();
			return basicGetServer();
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
		case BuildPackage.BUILD__ID:
			setId((String) newValue);
			return;
		case BuildPackage.BUILD__BUILD_NUMBER:
			setBuildNumber((Integer) newValue);
			return;
		case BuildPackage.BUILD__TIMESTAMP:
			setTimestamp((Long) newValue);
			return;
		case BuildPackage.BUILD__DURATION:
			setDuration((Long) newValue);
			return;
		case BuildPackage.BUILD__DISPLAY_NAME:
			setDisplayName((String) newValue);
			return;
		case BuildPackage.BUILD__STATE:
			setState((BuildState) newValue);
			return;
		case BuildPackage.BUILD__STATUS:
			setStatus((BuildStatus) newValue);
			return;
		case BuildPackage.BUILD__ARTIFACTS:
			getArtifacts().clear();
			getArtifacts().addAll((Collection<? extends IArtifact>) newValue);
			return;
		case BuildPackage.BUILD__CHANGE_SET:
			setChangeSet((IChangeSet) newValue);
			return;
		case BuildPackage.BUILD__PLAN:
			setPlan((IBuildPlan) newValue);
			return;
		case BuildPackage.BUILD__LABEL:
			setLabel((String) newValue);
			return;
		case BuildPackage.BUILD__SERVER:
			setServer((IBuildServer) newValue);
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
		case BuildPackage.BUILD__ID:
			setId(ID_EDEFAULT);
			return;
		case BuildPackage.BUILD__BUILD_NUMBER:
			setBuildNumber(BUILD_NUMBER_EDEFAULT);
			return;
		case BuildPackage.BUILD__TIMESTAMP:
			setTimestamp(TIMESTAMP_EDEFAULT);
			return;
		case BuildPackage.BUILD__DURATION:
			setDuration(DURATION_EDEFAULT);
			return;
		case BuildPackage.BUILD__DISPLAY_NAME:
			setDisplayName(DISPLAY_NAME_EDEFAULT);
			return;
		case BuildPackage.BUILD__STATE:
			setState(STATE_EDEFAULT);
			return;
		case BuildPackage.BUILD__STATUS:
			setStatus(STATUS_EDEFAULT);
			return;
		case BuildPackage.BUILD__ARTIFACTS:
			getArtifacts().clear();
			return;
		case BuildPackage.BUILD__CHANGE_SET:
			setChangeSet((IChangeSet) null);
			return;
		case BuildPackage.BUILD__PLAN:
			setPlan((IBuildPlan) null);
			return;
		case BuildPackage.BUILD__LABEL:
			setLabel(LABEL_EDEFAULT);
			return;
		case BuildPackage.BUILD__SERVER:
			setServer((IBuildServer) null);
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
		case BuildPackage.BUILD__ID:
			return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
		case BuildPackage.BUILD__BUILD_NUMBER:
			return buildNumber != BUILD_NUMBER_EDEFAULT;
		case BuildPackage.BUILD__TIMESTAMP:
			return timestamp != TIMESTAMP_EDEFAULT;
		case BuildPackage.BUILD__DURATION:
			return duration != DURATION_EDEFAULT;
		case BuildPackage.BUILD__DISPLAY_NAME:
			return DISPLAY_NAME_EDEFAULT == null ? displayName != null : !DISPLAY_NAME_EDEFAULT.equals(displayName);
		case BuildPackage.BUILD__STATE:
			return STATE_EDEFAULT == null ? state != null : !STATE_EDEFAULT.equals(state);
		case BuildPackage.BUILD__STATUS:
			return STATUS_EDEFAULT == null ? status != null : !STATUS_EDEFAULT.equals(status);
		case BuildPackage.BUILD__ARTIFACTS:
			return artifacts != null && !artifacts.isEmpty();
		case BuildPackage.BUILD__CHANGE_SET:
			return changeSet != null;
		case BuildPackage.BUILD__PLAN:
			return plan != null;
		case BuildPackage.BUILD__LABEL:
			return LABEL_EDEFAULT == null ? label != null : !LABEL_EDEFAULT.equals(label);
		case BuildPackage.BUILD__SERVER:
			return server != null;
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
		result.append(" (id: "); //$NON-NLS-1$
		result.append(id);
		result.append(", buildNumber: "); //$NON-NLS-1$
		result.append(buildNumber);
		result.append(", timestamp: "); //$NON-NLS-1$
		result.append(timestamp);
		result.append(", duration: "); //$NON-NLS-1$
		result.append(duration);
		result.append(", displayName: "); //$NON-NLS-1$
		result.append(displayName);
		result.append(", state: "); //$NON-NLS-1$
		result.append(state);
		result.append(", status: "); //$NON-NLS-1$
		result.append(status);
		result.append(", label: "); //$NON-NLS-1$
		result.append(label);
		result.append(')');
		return result.toString();
	}

	@Override
	public Build createWorkingCopy() {
		return (Build) super.createWorkingCopy();
	}

} // Build
