/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildServer.java,v 1.5 2010/06/24 06:07:52 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.IOperationMonitor;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.builds.core.tasks.IBuildLoader;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Server</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.BuildServer#getServer <em>Server</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildServer()
 * @model kind="class" superTypes="org.eclipse.mylyn.internal.builds.core.IBuildServer"
 * @generated
 */
public class BuildServer extends EObjectImpl implements EObject, IBuildServer {
	/**
	 * The default value of the '{@link #getUrl() <em>Url</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getUrl()
	 * @generated
	 * @ordered
	 */
	protected static final String URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUrl() <em>Url</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getUrl()
	 * @generated
	 * @ordered
	 */
	protected String url = URL_EDEFAULT;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getPlans() <em>Plans</em>}' containment reference list. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getPlans()
	 * @generated
	 * @ordered
	 */
	protected EList<IBuildPlan> plans;

	/**
	 * The default value of the '{@link #getRepository() <em>Repository</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getRepository()
	 * @generated
	 * @ordered
	 */
	protected static final TaskRepository REPOSITORY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRepository() <em>Repository</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getRepository()
	 * @generated
	 * @ordered
	 */
	protected TaskRepository repository = REPOSITORY_EDEFAULT;

	/**
	 * The default value of the '{@link #getConnectorKind() <em>Connector Kind</em>}' attribute. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getConnectorKind()
	 * @generated
	 * @ordered
	 */
	protected static final String CONNECTOR_KIND_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getConnectorKind() <em>Connector Kind</em>}' attribute. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getConnectorKind()
	 * @generated
	 * @ordered
	 */
	protected String connectorKind = CONNECTOR_KIND_EDEFAULT;

	/**
	 * The default value of the '{@link #getRepositoryUrl() <em>Repository Url</em>}' attribute. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getRepositoryUrl()
	 * @generated
	 * @ordered
	 */
	protected static final String REPOSITORY_URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRepositoryUrl() <em>Repository Url</em>}' attribute. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getRepositoryUrl()
	 * @generated
	 * @ordered
	 */
	protected String repositoryUrl = REPOSITORY_URL_EDEFAULT;

	/**
	 * The cached value of the '{@link #getServer() <em>Server</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getServer()
	 * @generated
	 * @ordered
	 */
	protected BuildServer server;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected BuildServer() {
		super();
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
	 * Returns the value of the '<em><b>Url</b></em>' attribute. <!-- begin-user-doc -->
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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
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
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the value of the '<em><b>Plans</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.builds.core.IBuildPlan}. It is bidirectional and its opposite is '
	 * {@link org.eclipse.mylyn.builds.core.IBuildPlan#getServer <em>Server</em>}'. <!-- begin-user-doc -->
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
	 * Returns the value of the '<em><b>Repository</b></em>' attribute. <!-- begin-user-doc -->
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
	 * <em>Repository</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the value of the '<em><b>Connector Kind</b></em>' attribute. <!-- begin-user-doc -->
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
	 * <em>Connector Kind</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the value of the '<em><b>Repository Url</b></em>' attribute. <!-- begin-user-doc -->
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
	 * <em>Repository Url</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the value of the '<em><b>Server</b></em>' reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Server</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Server</em>' reference.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildServer_Server()
	 * @model required="true" changeable="false" derived="true"
	 * @generated
	 */
	public BuildServer getServer() {
		if (server != null && server.eIsProxy()) {
			InternalEObject oldServer = server;
			server = (BuildServer) eResolveProxy(oldServer);
			if (server != oldServer) {
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, BuildPackage.BUILD_SERVER__SERVER,
							oldServer, server));
				}
			}
		}
		return server;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public BuildServer basicGetServer() {
		return server;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case BuildPackage.BUILD_SERVER__PLANS:
			return ((InternalEList<?>) getPlans()).basicRemove(otherEnd, msgs);
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
		case BuildPackage.BUILD_SERVER__URL:
			return getUrl();
		case BuildPackage.BUILD_SERVER__NAME:
			return getName();
		case BuildPackage.BUILD_SERVER__PLANS:
			return getPlans();
		case BuildPackage.BUILD_SERVER__REPOSITORY:
			return getRepository();
		case BuildPackage.BUILD_SERVER__CONNECTOR_KIND:
			return getConnectorKind();
		case BuildPackage.BUILD_SERVER__REPOSITORY_URL:
			return getRepositoryUrl();
		case BuildPackage.BUILD_SERVER__SERVER:
			if (resolve) {
				return getServer();
			}
			return basicGetServer();
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
		case BuildPackage.BUILD_SERVER__REPOSITORY:
			setRepository((TaskRepository) newValue);
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
		case BuildPackage.BUILD_SERVER__URL:
			setUrl(URL_EDEFAULT);
			return;
		case BuildPackage.BUILD_SERVER__NAME:
			setName(NAME_EDEFAULT);
			return;
		case BuildPackage.BUILD_SERVER__PLANS:
			getPlans().clear();
			return;
		case BuildPackage.BUILD_SERVER__REPOSITORY:
			setRepository(REPOSITORY_EDEFAULT);
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
		case BuildPackage.BUILD_SERVER__URL:
			return URL_EDEFAULT == null ? url != null : !URL_EDEFAULT.equals(url);
		case BuildPackage.BUILD_SERVER__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case BuildPackage.BUILD_SERVER__PLANS:
			return plans != null && !plans.isEmpty();
		case BuildPackage.BUILD_SERVER__REPOSITORY:
			return REPOSITORY_EDEFAULT == null ? repository != null : !REPOSITORY_EDEFAULT.equals(repository);
		case BuildPackage.BUILD_SERVER__CONNECTOR_KIND:
			return CONNECTOR_KIND_EDEFAULT == null ? connectorKind != null
					: !CONNECTOR_KIND_EDEFAULT.equals(connectorKind);
		case BuildPackage.BUILD_SERVER__REPOSITORY_URL:
			return REPOSITORY_URL_EDEFAULT == null ? repositoryUrl != null
					: !REPOSITORY_URL_EDEFAULT.equals(repositoryUrl);
		case BuildPackage.BUILD_SERVER__SERVER:
			return server != null;
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

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (url: ");
		result.append(url);
		result.append(", name: ");
		result.append(name);
		result.append(", repository: ");
		result.append(repository);
		result.append(", connectorKind: ");
		result.append(connectorKind);
		result.append(", repositoryUrl: ");
		result.append(repositoryUrl);
		result.append(')');
		return result.toString();
	}

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

	public IStatus validate(IOperationMonitor monitor) throws CoreException {
		return getBehaviour().validate(monitor);
	}

	public List<IBuildPlan> getPlans(final IOperationMonitor monitor) throws CoreException {
		final AtomicReference<List<IBuildPlan>> result = new AtomicReference<List<IBuildPlan>>();
		SafeRunner.run(new ISafeRunnable() {
			public void run() throws Exception {
				result.set(getBehaviour().getPlans(monitor));
			}

			public void handleException(Throwable e) {
				StatusHandler.log(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
						"Unexpected error during invocation in server behavior", e));
			}
		});
		if (result.get() == null) {
			throw new CoreException(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
					"Server did not provide any plans."));
		}
		ArrayList<IBuildPlan> oldPlans = new ArrayList<IBuildPlan>(getPlans());
		getPlans().clear();
		getPlans().addAll(result.get());
		for (IBuildPlan plan : oldPlans) {
			IBuildPlan newPlan = getPlanById(plan.getId());
			if (newPlan != null) {
				((BuildPlan) newPlan).setSelected(plan.isSelected());
			}
		}
		return result.get();
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

	public BuildServer createWorkingCopy() {
		EcoreUtil.Copier copier = new EcoreUtil.Copier();
		BuildServer newServer = (BuildServer) copier.copy(this);
		copier.copyReferences();
		newServer.setLoader(getLoader());
		return newServer;
	}

	public IBuildPlanWorkingCopy createBuildPlan() {
		BuildFactory factory = BuildPackage.eINSTANCE.getBuildFactory();
		BuildPlan plan = factory.createBuildPlan();
		return plan;
	}

} // BuildServer
