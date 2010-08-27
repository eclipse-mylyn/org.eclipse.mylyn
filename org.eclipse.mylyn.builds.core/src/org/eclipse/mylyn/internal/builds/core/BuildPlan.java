/**
 * <copyright>
 * </copyright>
 *
 * $Id: BuildPlan.java,v 1.15 2010/08/27 09:00:23 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.RunBuildRequest;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.mylyn.internal.builds.core.operations.RunBuildOperation;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Plan</b></em>'.
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBuildPlan()
 * @model kind="class" superTypes=
 *        "org.eclipse.mylyn.internal.builds.core.IBuildPlan org.eclipse.mylyn.internal.builds.core.IBuildPlanWorkingCopy"
 * @generated
 */
public class BuildPlan extends EObjectImpl implements EObject, IBuildPlan, IBuildPlanWorkingCopy {
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
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan#getUrl <em>Url</em>}' attribute.
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
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__URL, oldUrl, url));
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
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan#getName <em>Name</em>}' attribute.
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
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__NAME, oldName, name));
	}

	/**
	 * Returns the value of the '<em><b>Server</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Server</em>' container reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Server</em>' reference.
	 * @see #setServer(IBuildServer)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan_Server()
	 * @model type="org.eclipse.mylyn.internal.builds.core.IBuildServer" required="true"
	 * @generated
	 */
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
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan#getServer <em>Server</em>}'
	 * reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Server</em>' reference.
	 * @see #getServer()
	 * @generated
	 */
	public void setServer(IBuildServer newServer) {
		IBuildServer oldServer = server;
		server = newServer;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__SERVER, oldServer, server));
	}

	/**
	 * Returns the value of the '<em><b>Children</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.builds.core.IBuildPlan}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getParent
	 * <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Children</em>' reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Children</em>' reference list.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan_Children()
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getParent
	 * @model type="org.eclipse.mylyn.internal.builds.core.IBuildPlan" opposite="parent"
	 * @generated
	 */
	public EList<IBuildPlan> getChildren() {
		if (children == null) {
			children = new EObjectWithInverseResolvingEList<IBuildPlan>(IBuildPlan.class, this,
					BuildPackage.BUILD_PLAN__CHILDREN, BuildPackage.IBUILD_PLAN__PARENT);
		}
		return children;
	}

	/**
	 * Returns the value of the '<em><b>Parent</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getChildren
	 * <em>Children</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Parent</em>' reference.
	 * @see #setParent(IBuildPlan)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan_Parent()
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getChildren
	 * @model type="org.eclipse.mylyn.internal.builds.core.IBuildPlan" opposite="children"
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
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan#getParent <em>Parent</em>}'
	 * reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Parent</em>' reference.
	 * @see #getParent()
	 * @generated
	 */
	public void setParent(IBuildPlan newParent) {
		if (newParent != parent) {
			NotificationChain msgs = null;
			if (parent != null)
				msgs = ((InternalEObject) parent).eInverseRemove(this, BuildPackage.IBUILD_PLAN__CHILDREN,
						IBuildPlan.class, msgs);
			if (newParent != null)
				msgs = ((InternalEObject) newParent).eInverseAdd(this, BuildPackage.IBUILD_PLAN__CHILDREN,
						IBuildPlan.class, msgs);
			msgs = basicSetParent(newParent, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__PARENT, newParent, newParent));
	}

	/**
	 * Returns the value of the '<em><b>Health</b></em>' attribute.
	 * The default value is <code>"-1"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Health</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Health</em>' attribute.
	 * @see #setHealth(int)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan_Health()
	 * @model default="-1"
	 * @generated
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan#getHealth <em>Health</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Health</em>' attribute.
	 * @see #getHealth()
	 * @generated
	 */
	public void setHealth(int newHealth) {
		int oldHealth = health;
		health = newHealth;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__HEALTH, oldHealth, health));
	}

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan_Id()
	 * @model required="true"
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__ID, oldId, id));
	}

	/**
	 * Returns the value of the '<em><b>Info</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Info</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Info</em>' attribute.
	 * @see #setInfo(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan_Info()
	 * @model
	 * @generated
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan#getInfo <em>Info</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Info</em>' attribute.
	 * @see #getInfo()
	 * @generated
	 */
	public void setInfo(String newInfo) {
		String oldInfo = info;
		info = newInfo;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__INFO, oldInfo, info));
	}

	/**
	 * Returns the value of the '<em><b>Selected</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Selected</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Selected</em>' attribute.
	 * @see #setSelected(boolean)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan_Selected()
	 * @model default="false" required="true"
	 * @generated
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan#isSelected <em>Selected</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Selected</em>' attribute.
	 * @see #isSelected()
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
	 * Returns the value of the '<em><b>Summary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Summary</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Summary</em>' attribute.
	 * @see #setSummary(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan_Summary()
	 * @model
	 * @generated
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan#getSummary <em>Summary</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Summary</em>' attribute.
	 * @see #getSummary()
	 * @generated
	 */
	public void setSummary(String newSummary) {
		String oldSummary = summary;
		summary = newSummary;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__SUMMARY, oldSummary, summary));
	}

	/**
	 * Returns the value of the '<em><b>State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>State</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>State</em>' attribute.
	 * @see #setState(BuildState)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan_State()
	 * @model dataType="org.eclipse.mylyn.internal.builds.core.BuildState"
	 * @generated
	 */
	public BuildState getState() {
		return state;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan#getState <em>State</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>State</em>' attribute.
	 * @see #getState()
	 * @generated
	 */
	public void setState(BuildState newState) {
		BuildState oldState = state;
		state = newState;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__STATE, oldState, state));
	}

	/**
	 * Returns the value of the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Status</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Status</em>' attribute.
	 * @see #setStatus(BuildStatus)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan_Status()
	 * @model dataType="org.eclipse.mylyn.internal.builds.core.BuildStatus"
	 * @generated
	 */
	public BuildStatus getStatus() {
		return status;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan#getStatus <em>Status</em>}'
	 * attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Status</em>' attribute.
	 * @see #getStatus()
	 * @generated
	 */
	public void setStatus(BuildStatus newStatus) {
		BuildStatus oldStatus = status;
		status = newStatus;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__STATUS, oldStatus, status));
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
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan_Description()
	 * @model
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan#getDescription
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
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_PLAN__DESCRIPTION, oldDescription,
					description));
	}

	/**
	 * Returns the value of the '<em><b>Last Build</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Last Build</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Last Build</em>' reference.
	 * @see #setLastBuild(IBuild)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan_LastBuild()
	 * @model type="org.eclipse.mylyn.internal.builds.core.IBuild"
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
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BuildPlan#getLastBuild <em>Last Build</em>}'
	 * reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Last Build</em>' reference.
	 * @see #getLastBuild()
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
	 * Returns the value of the '<em><b>Parameter Definitions</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.internal.builds.core.IParameterDefinition}.
	 * It is bidirectional and its opposite is '
	 * {@link org.eclipse.mylyn.internal.builds.core.IParameterDefinition#getContainingBuildPlan
	 * <em>Containing Build Plan</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Definitions</em>' containment reference list isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Parameter Definitions</em>' containment reference list.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBuildPlan_ParameterDefinitions()
	 * @see org.eclipse.mylyn.internal.builds.core.IParameterDefinition#getContainingBuildPlan
	 * @model opposite="containingBuildPlan" containment="true"
	 * @generated
	 */
	public EList<IParameterDefinition> getParameterDefinitions() {
		if (parameterDefinitions == null) {
			parameterDefinitions = new EObjectContainmentWithInverseEList<IParameterDefinition>(
					IParameterDefinition.class, this, BuildPackage.BUILD_PLAN__PARAMETER_DEFINITIONS,
					BuildPackage.IPARAMETER_DEFINITION__CONTAINING_BUILD_PLAN);
		}
		return parameterDefinitions;
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
				msgs = ((InternalEObject) parent).eInverseRemove(this, BuildPackage.IBUILD_PLAN__CHILDREN,
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
		case BuildPackage.BUILD_PLAN__URL:
			return getUrl();
		case BuildPackage.BUILD_PLAN__NAME:
			return getName();
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
		case BuildPackage.BUILD_PLAN__URL:
			setUrl((String) newValue);
			return;
		case BuildPackage.BUILD_PLAN__NAME:
			setName((String) newValue);
			return;
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
		case BuildPackage.BUILD_PLAN__URL:
			setUrl(URL_EDEFAULT);
			return;
		case BuildPackage.BUILD_PLAN__NAME:
			setName(NAME_EDEFAULT);
			return;
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
		case BuildPackage.BUILD_PLAN__URL:
			return URL_EDEFAULT == null ? url != null : !URL_EDEFAULT.equals(url);
		case BuildPackage.BUILD_PLAN__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
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
		result.append(" (url: ");
		result.append(url);
		result.append(", name: ");
		result.append(name);
		result.append(", health: ");
		result.append(health);
		result.append(", id: ");
		result.append(id);
		result.append(", info: ");
		result.append(info);
		result.append(", selected: ");
		result.append(selected);
		result.append(", summary: ");
		result.append(summary);
		result.append(", state: ");
		result.append(state);
		result.append(", status: ");
		result.append(status);
		result.append(", description: ");
		result.append(description);
		result.append(')');
		return result.toString();
	}

	public BuildPlan createWorkingCopy() {
		EcoreUtil.Copier copier = new EcoreUtil.Copier();
		BuildPlan newPlan = (BuildPlan) copier.copy(this);
		copier.copyReferences();
		return newPlan;
	}

	public void run(IOperationMonitor monitor) throws CoreException {
		new RunBuildOperation(new RunBuildRequest(this)).doRun(monitor);
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
				// do nothing
			}
		};
		copier.copy(source);

		// TODO The above and the below is not a real merge!
		getParameterDefinitions().clear();
		getParameterDefinitions().addAll(EcoreUtil.copyAll(source.getParameterDefinitions()));
	}

	public String getLabel() {
		return getName();
	}

} // BuildPlan
