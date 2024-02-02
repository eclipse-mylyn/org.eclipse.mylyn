/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildServer.java,v 1.3 2010/08/28 20:59:54 spingel Exp $
 */
package org.eclipse.mylyn.builds.internal.core;

import java.beans.PropertyChangeListener;
import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.mylyn.builds.core.IBuildFactory;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.builds.core.spi.BuildServerConfiguration;
import org.eclipse.mylyn.builds.internal.core.operations.RefreshConfigurationOperation;
import org.eclipse.mylyn.builds.internal.core.operations.RefreshSession;
import org.eclipse.mylyn.commons.core.net.NetUtil;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Server</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildServer#getLocation <em>Location</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildServer#getConnectorKind <em>Connector Kind</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildServer#getRepositoryUrl <em>Repository Url</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class BuildServer extends BuildElement implements IBuildServer {
	/**
	 * The default value of the '{@link #getLocation() <em>Location</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getLocation()
	 * @generated
	 * @ordered
	 */
	protected static final RepositoryLocation LOCATION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLocation() <em>Location</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getLocation()
	 * @generated
	 * @ordered
	 */
	protected RepositoryLocation location = LOCATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getConnectorKind() <em>Connector Kind</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getConnectorKind()
	 * @generated
	 * @ordered
	 */
	protected static final String CONNECTOR_KIND_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getConnectorKind() <em>Connector Kind</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getConnectorKind()
	 * @generated
	 * @ordered
	 */
	protected String connectorKind = CONNECTOR_KIND_EDEFAULT;

	/**
	 * The default value of the '{@link #getRepositoryUrl() <em>Repository Url</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getRepositoryUrl()
	 * @generated
	 * @ordered
	 */
	protected static final String REPOSITORY_URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRepositoryUrl() <em>Repository Url</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getRepositoryUrl()
	 * @generated
	 * @ordered
	 */
	protected String repositoryUrl = REPOSITORY_URL_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 */
	protected BuildServer() {
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BuildPackage.Literals.BUILD_SERVER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Location</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public RepositoryLocation getLocation() {
		return location;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.builds.internal.core.BuildServer#getLocation <em>Location</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Location</em>' attribute.
	 * @see #getLocation()
	 */
	@Override
	public void setLocation(RepositoryLocation newLocation) {
		RepositoryLocation oldLocation = location;
		if (oldLocation != null) {
			oldLocation.removePropertyChangeListener(locationChangeListener);
		}
		location = newLocation;
		if (location != null) {
			location.addPropertyChangeListener(locationChangeListener);
		}
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_SERVER__LOCATION, oldLocation,
					location));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Connector Kind</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getConnectorKind() {
		return connectorKind;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setConnectorKind(String newConnectorKind) {
		String oldConnectorKind = connectorKind;
		connectorKind = newConnectorKind;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_SERVER__CONNECTOR_KIND,
					oldConnectorKind, connectorKind));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Repository Url</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setRepositoryUrl(String newRepositoryUrl) {
		String oldRepositoryUrl = repositoryUrl;
		repositoryUrl = newRepositoryUrl;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_SERVER__REPOSITORY_URL,
					oldRepositoryUrl, repositoryUrl));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case BuildPackage.BUILD_SERVER__LOCATION:
				return getLocation();
			case BuildPackage.BUILD_SERVER__CONNECTOR_KIND:
				return getConnectorKind();
			case BuildPackage.BUILD_SERVER__REPOSITORY_URL:
				return getRepositoryUrl();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case BuildPackage.BUILD_SERVER__LOCATION:
				setLocation((RepositoryLocation) newValue);
				return;
			case BuildPackage.BUILD_SERVER__CONNECTOR_KIND:
				setConnectorKind((String) newValue);
				return;
			case BuildPackage.BUILD_SERVER__REPOSITORY_URL:
				setRepositoryUrl((String) newValue);
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
			case BuildPackage.BUILD_SERVER__LOCATION:
				setLocation(LOCATION_EDEFAULT);
				return;
			case BuildPackage.BUILD_SERVER__CONNECTOR_KIND:
				setConnectorKind(CONNECTOR_KIND_EDEFAULT);
				return;
			case BuildPackage.BUILD_SERVER__REPOSITORY_URL:
				setRepositoryUrl(REPOSITORY_URL_EDEFAULT);
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
			case BuildPackage.BUILD_SERVER__LOCATION:
				return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
			case BuildPackage.BUILD_SERVER__CONNECTOR_KIND:
				return CONNECTOR_KIND_EDEFAULT == null
						? connectorKind != null
						: !CONNECTOR_KIND_EDEFAULT.equals(connectorKind);
			case BuildPackage.BUILD_SERVER__REPOSITORY_URL:
				return REPOSITORY_URL_EDEFAULT == null
						? repositoryUrl != null
						: !REPOSITORY_URL_EDEFAULT.equals(repositoryUrl);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) {
			return super.toString();
		}

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (location: "); //$NON-NLS-1$
		result.append(location);
		result.append(", connectorKind: "); //$NON-NLS-1$
		result.append(connectorKind);
		result.append(", repositoryUrl: "); //$NON-NLS-1$
		result.append(repositoryUrl);
		result.append(')');
		return result.toString();
	}

	// --- non-generated methods ---

	private final PropertyChangeListener locationChangeListener = event -> getLoader().getRealm().asyncExec(() -> {
		if (event.getNewValue() == null) {
			getAttributes().remove(event.getPropertyName());
		}
		if (event.getNewValue() instanceof String) {
			getAttributes().put(event.getPropertyName(), (String) event.getNewValue());
		}
		if (RepositoryLocation.PROPERTY_LABEL.equals(event.getPropertyName())) {
			setName((String) event.getNewValue());
		} else if (RepositoryLocation.PROPERTY_URL.equals(event.getPropertyName())) {
			setUrl(event.getNewValue().toString());
		}
	});

	private RefreshSession refreshSession;

	private IBuildLoader loader;

	private BuildServerBehaviour behaviour;

	public IBuildLoader getLoader() {
		return loader;
	}

	public void setLoader(IBuildLoader loader) {
		this.loader = loader;
	}

	public BuildServerBehaviour getBehaviour() throws CoreException {
		if (behaviour == null) {
			behaviour = getLoader().loadBehaviour(this);
		}
		return behaviour;
	}

	@Override
	public IBuildServer getServer() {
		return this;
	}

	public IStatus validate(IOperationMonitor monitor) throws CoreException {
		return getBehaviour().validate(monitor);
	}

	@Override
	public BuildServer getOriginal() {
		return (BuildServer) super.getOriginal();
	}

	@Override
	public BuildServer createWorkingCopy() {
		BuildServer newServer = (BuildServer) super.createWorkingCopy();
		newServer.setLoader(getLoader());
		return newServer;
	}

	public void applyToOriginal() {
		if (original == null) {
			throw new IllegalStateException();
		}
		@SuppressWarnings("serial")
		EcoreUtil.Copier copier = new EcoreUtil.Copier() {
			@Override
			protected EObject createCopy(EObject source) {
				return original;
			}

			@Override
			protected void copyAttribute(EAttribute eAttribute, EObject eObject, EObject copyEObject) {
				if (eAttribute.getFeatureID() == BuildPackage.BUILD_SERVER__LOCATION) {
					return;
				}
				super.copyAttribute(eAttribute, eObject, copyEObject);
			}

			@Override
			protected void copyContainment(EReference eReference, EObject eObject, EObject copyEObject) {
				// do nothing
			}
		};
		copier.copy(this);
	}

	public IBuildPlan createBuildPlan() {
		IBuildPlan plan = IBuildFactory.INSTANCE.createBuildPlan();
		return plan;
	}

	@Override
	public BuildServerConfiguration getConfiguration() throws CoreException {
		return getBehaviour().getConfiguration();
	}

	public BuildServerConfiguration refreshConfiguration(final IOperationMonitor monitor) throws CoreException {
		new RefreshConfigurationOperation(Collections.singletonList((IBuildServer) this)).run(monitor);
		return getConfiguration();
	}

	public String getShortUrl() {
		String url = getUrl();
		if (url != null) {
			return NetUtil.getHost(url);
		}
		return url;
	}

	@Override
	public String getLabel() {
		String name = getName();
		if (name != null && name.length() > 0) {
			return name;
		}
		return getShortUrl();
	}

	public synchronized RefreshSession getRefreshSession() {
		if (refreshSession == null) {
			refreshSession = new RefreshSession(this);
		}
		return refreshSession;
	}

} // BuildServer
