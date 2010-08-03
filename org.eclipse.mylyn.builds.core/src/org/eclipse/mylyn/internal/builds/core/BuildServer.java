/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildServer.java,v 1.16 2010/08/03 18:09:28 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.IOperationMonitor;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.internal.builds.core.operations.RefreshOperation;
import org.eclipse.mylyn.internal.builds.core.tasks.IBuildLoader;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Server</b></em>'.
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildServer()
 * @model kind="class" superTypes="org.eclipse.mylyn.internal.builds.core.IBuildServer"
 * @generated
 */
public class BuildServer extends EObjectImpl implements EObject, IBuildServer {
	/**
	 * The default value of the '{@link #getUrl() <em>Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getUrl()
	 * @generated
	 * @ordered
	 */
	protected static final String URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUrl() <em>Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getUrl()
	 * @generated
	 * @ordered
	 */
	protected String url = URL_EDEFAULT;

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
	 * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getAttributes()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, String> attributes;

	/**
	 * The default value of the '{@link #getRepository() <em>Repository</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getRepository()
	 * @generated
	 * @ordered
	 */
	protected static final TaskRepository REPOSITORY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRepository() <em>Repository</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getRepository()
	 * @generated
	 * @ordered
	 */
	protected TaskRepository repository = REPOSITORY_EDEFAULT;

	/**
	 * The default value of the '{@link #getLocation() <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getLocation()
	 * @generated
	 * @ordered
	 */
	protected static final RepositoryLocation LOCATION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLocation() <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getLocation()
	 * @generated
	 * @ordered
	 */
	protected RepositoryLocation location = LOCATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getConnectorKind() <em>Connector Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getConnectorKind()
	 * @generated
	 * @ordered
	 */
	protected static final String CONNECTOR_KIND_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getConnectorKind() <em>Connector Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getConnectorKind()
	 * @generated
	 * @ordered
	 */
	protected String connectorKind = CONNECTOR_KIND_EDEFAULT;

	/**
	 * The default value of the '{@link #getRepositoryUrl() <em>Repository Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getRepositoryUrl()
	 * @generated
	 * @ordered
	 */
	protected static final String REPOSITORY_URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRepositoryUrl() <em>Repository Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getRepositoryUrl()
	 * @generated
	 * @ordered
	 */
	protected String repositoryUrl = REPOSITORY_URL_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	protected BuildServer() {
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BuildPackage.Literals.BUILD_SERVER;
	}

	/**
	 * Returns the value of the '<em><b>Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Url</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Url</em>' attribute.
	 * @see #setUrl(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildElement_Url()
	 * @model
	 * @generated
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildServer#getUrl <em>Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Url</em>' attribute.
	 * @see #getUrl()
	 * @generated
	 */
	public void setUrl(String newUrl) {
		String oldUrl = url;
		url = newUrl;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_SERVER__URL, oldUrl, url));
		}
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
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildElement_Name()
	 * @model
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildServer#getName <em>Name</em>}'
	 * attribute.
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
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_SERVER__NAME, oldName, name));
		}
	}

	/**
	 * Returns the value of the '<em><b>Plans</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.builds.core.IBuildPlan}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getServer
	 * <em>Server</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Plans</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Plans</em>' containment reference list.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildServer_Plans()
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getServer
	 * @model type="org.eclipse.mylyn.internal.builds.core.IBuildPlan" opposite="server" containment="true"
	 *        ordered="false"
	 * @generated
	 */
	public EList<IBuildPlan> getPlans() {
		if (plans == null) {
			plans = new EObjectContainmentWithInverseEList<IBuildPlan>(IBuildPlan.class, this,
					BuildPackage.BUILD_SERVER__PLANS, BuildPackage.IBUILD_PLAN__SERVER);
		}
		return plans;
	}

	/**
	 * Returns the value of the '<em><b>Attributes</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Attributes</em>' map isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Attributes</em>' map.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildServer_Attributes()
	 * @model mapType=
	 *        "org.eclipse.mylyn.internal.builds.core.StringToStringMap<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>"
	 * @generated
	 */
	public EMap<String, String> getAttributes() {
		if (attributes == null) {
			attributes = new EcoreEMap<String, String>(BuildPackage.Literals.STRING_TO_STRING_MAP,
					StringToStringMap.class, this, BuildPackage.BUILD_SERVER__ATTRIBUTES);
		}
		return attributes;
	}

	/**
	 * Returns the value of the '<em><b>Repository</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Repository</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Repository</em>' attribute.
	 * @see #setRepository(TaskRepository)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildServer_Repository()
	 * @model dataType="org.eclipse.mylyn.internal.builds.core.TaskRepository" transient="true"
	 * @generated
	 */
	public TaskRepository getRepository() {
		return repository;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildServer#getRepository
	 * <em>Repository</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Repository</em>' attribute.
	 * @see #getRepository()
	 * @generated
	 */
	public void setRepository(TaskRepository newRepository) {
		TaskRepository oldRepository = repository;
		repository = newRepository;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_SERVER__REPOSITORY, oldRepository,
					repository));
		}
	}

	/**
	 * Returns the value of the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Location</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Location</em>' attribute.
	 * @see #setLocation(RepositoryLocation)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildServer_Location()
	 * @model dataType="org.eclipse.mylyn.internal.builds.core.RepositoryLocation" transient="true"
	 * @generated
	 */
	public RepositoryLocation getLocation() {
		return location;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildServer#getLocation <em>Location</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Location</em>' attribute.
	 * @see #getLocation()
	 */
	public void setLocation(RepositoryLocation newLocation) {
		RepositoryLocation oldLocation = location;
		if (oldLocation != null) {
			oldLocation.removeChangeListener(locationChangeListener);
		}
		location = newLocation;
		if (location != null) {
			location.addChangeListener(locationChangeListener);
		}
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_SERVER__LOCATION, oldLocation,
					location));
		}
	}

	/**
	 * Returns the value of the '<em><b>Connector Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Connector Kind</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Connector Kind</em>' attribute.
	 * @see #setConnectorKind(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildServer_ConnectorKind()
	 * @model
	 * @generated
	 */
	public String getConnectorKind() {
		return connectorKind;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildServer#getConnectorKind
	 * <em>Connector Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Connector Kind</em>' attribute.
	 * @see #getConnectorKind()
	 * @generated
	 */
	public void setConnectorKind(String newConnectorKind) {
		String oldConnectorKind = connectorKind;
		connectorKind = newConnectorKind;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_SERVER__CONNECTOR_KIND,
					oldConnectorKind, connectorKind));
		}
	}

	/**
	 * Returns the value of the '<em><b>Repository Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Repository Url</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Repository Url</em>' attribute.
	 * @see #setRepositoryUrl(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildServer_RepositoryUrl()
	 * @model
	 * @generated
	 */
	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildServer#getRepositoryUrl
	 * <em>Repository Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Repository Url</em>' attribute.
	 * @see #getRepositoryUrl()
	 * @generated
	 */
	public void setRepositoryUrl(String newRepositoryUrl) {
		String oldRepositoryUrl = repositoryUrl;
		repositoryUrl = newRepositoryUrl;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_SERVER__REPOSITORY_URL,
					oldRepositoryUrl, repositoryUrl));
		}
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
		case BuildPackage.BUILD_SERVER__PLANS:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getPlans()).basicAdd(otherEnd, msgs);
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
		case BuildPackage.BUILD_SERVER__PLANS:
			return ((InternalEList<?>) getPlans()).basicRemove(otherEnd, msgs);
		case BuildPackage.BUILD_SERVER__ATTRIBUTES:
			return ((InternalEList<?>) getAttributes()).basicRemove(otherEnd, msgs);
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
		case BuildPackage.BUILD_SERVER__URL:
			return getUrl();
		case BuildPackage.BUILD_SERVER__NAME:
			return getName();
		case BuildPackage.BUILD_SERVER__PLANS:
			return getPlans();
		case BuildPackage.BUILD_SERVER__ATTRIBUTES:
			if (coreType) {
				return getAttributes();
			} else {
				return getAttributes().map();
			}
		case BuildPackage.BUILD_SERVER__REPOSITORY:
			return getRepository();
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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case BuildPackage.BUILD_SERVER__URL:
			setUrl((String) newValue);
			return;
		case BuildPackage.BUILD_SERVER__NAME:
			setName((String) newValue);
			return;
		case BuildPackage.BUILD_SERVER__PLANS:
			getPlans().clear();
			getPlans().addAll((Collection<? extends IBuildPlan>) newValue);
			return;
		case BuildPackage.BUILD_SERVER__ATTRIBUTES:
			((EStructuralFeature.Setting) getAttributes()).set(newValue);
			return;
		case BuildPackage.BUILD_SERVER__REPOSITORY:
			setRepository((TaskRepository) newValue);
			return;
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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case BuildPackage.BUILD_SERVER__URL:
			setUrl(URL_EDEFAULT);
			return;
		case BuildPackage.BUILD_SERVER__NAME:
			setName(NAME_EDEFAULT);
			return;
		case BuildPackage.BUILD_SERVER__PLANS:
			getPlans().clear();
			return;
		case BuildPackage.BUILD_SERVER__ATTRIBUTES:
			getAttributes().clear();
			return;
		case BuildPackage.BUILD_SERVER__REPOSITORY:
			setRepository(REPOSITORY_EDEFAULT);
			return;
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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case BuildPackage.BUILD_SERVER__URL:
			return URL_EDEFAULT == null ? url != null : !URL_EDEFAULT.equals(url);
		case BuildPackage.BUILD_SERVER__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case BuildPackage.BUILD_SERVER__PLANS:
			return plans != null && !plans.isEmpty();
		case BuildPackage.BUILD_SERVER__ATTRIBUTES:
			return attributes != null && !attributes.isEmpty();
		case BuildPackage.BUILD_SERVER__REPOSITORY:
			return REPOSITORY_EDEFAULT == null ? repository != null : !REPOSITORY_EDEFAULT.equals(repository);
		case BuildPackage.BUILD_SERVER__LOCATION:
			return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
		case BuildPackage.BUILD_SERVER__CONNECTOR_KIND:
			return CONNECTOR_KIND_EDEFAULT == null ? connectorKind != null : !CONNECTOR_KIND_EDEFAULT
					.equals(connectorKind);
		case BuildPackage.BUILD_SERVER__REPOSITORY_URL:
			return REPOSITORY_URL_EDEFAULT == null ? repositoryUrl != null : !REPOSITORY_URL_EDEFAULT
					.equals(repositoryUrl);
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
		if (eIsProxy()) {
			return super.toString();
		}

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (url: ");
		result.append(url);
		result.append(", name: ");
		result.append(name);
		result.append(", repository: ");
		result.append(repository);
		result.append(", location: ");
		result.append(location);
		result.append(", connectorKind: ");
		result.append(connectorKind);
		result.append(", repositoryUrl: ");
		result.append(repositoryUrl);
		result.append(')');
		return result.toString();
	}

	private final PropertyChangeListener locationChangeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			// FIXME run on UI thread
			if (evt.getNewValue() == null) {
				getAttributes().remove(evt.getPropertyName());
			}
			if (evt.getNewValue() instanceof String) {
				getAttributes().put(evt.getPropertyName(), (String) evt.getNewValue());
			}
			if ("label".equals(evt.getPropertyName())) {
				setName((String) evt.getNewValue());
			} else if ("uri".equals(evt.getPropertyName())) {
				setUrl(evt.getNewValue().toString());
			}
		}
	};

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

	public IBuildServer getServer() {
		return this;
	}

	public IStatus validate(IOperationMonitor monitor) throws CoreException {
		return getBehaviour().validate(monitor);
	}

	public List<IBuildPlan> refreshPlans(final IOperationMonitor monitor) throws CoreException {
		new RefreshOperation(Collections.singletonList((IBuildServer) this)).run(monitor);
		return getPlans();
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

	BuildServer original;

	public BuildServer getOriginal() {
		return original;
	}

	public BuildServer createWorkingCopy() {
		EcoreUtil.Copier copier = new EcoreUtil.Copier();
		BuildServer newServer = (BuildServer) copier.copy(this);
		copier.copyReferences();
		newServer.setLoader(getLoader());
		newServer.original = this;
		return newServer;
	}

	public void applyToOriginal() {
		if (original == null) {
			throw new IllegalStateException();
		}
		EcoreUtil.Copier copier = new EcoreUtil.Copier() {
			@Override
			protected EObject createCopy(EObject source) {
				return original;
			};

			@Override
			protected void copyContainment(EReference eReference, EObject eObject, EObject copyEObject) {
				// do nothing
			}
		};
		copier.copy(this);
	}

	public IBuildPlanWorkingCopy createBuildPlan() {
		BuildFactory factory = BuildPackage.eINSTANCE.getBuildFactory();
		BuildPlan plan = factory.createBuildPlan();
		return plan;
	}

} // BuildServer
