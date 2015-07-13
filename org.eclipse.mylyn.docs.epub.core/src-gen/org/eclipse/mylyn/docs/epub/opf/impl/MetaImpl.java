/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.opf.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.mylyn.docs.epub.opf.Meta;
import org.eclipse.mylyn.docs.epub.opf.OPFPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Meta</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetaImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetaImpl#getContent <em>Content</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetaImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetaImpl#getProperty <em>Property</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetaImpl#getRefines <em>Refines</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetaImpl#getScheme <em>Scheme</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetaImpl#getDir <em>Dir</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MetaImpl extends EObjectImpl implements Meta {
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
	 * The default value of the '{@link #getContent() <em>Content</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getContent()
	 * @generated
	 * @ordered
	 */
	protected static final String CONTENT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getContent() <em>Content</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getContent()
	 * @generated
	 * @ordered
	 */
	protected String content = CONTENT_EDEFAULT;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getProperty() <em>Property</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @see #getProperty()
	 * @generated
	 * @ordered
	 */
	protected static final String PROPERTY_EDEFAULT = ""; //$NON-NLS-1$

	/**
	 * The cached value of the '{@link #getProperty() <em>Property</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @see #getProperty()
	 * @generated
	 * @ordered
	 */
	protected String property = PROPERTY_EDEFAULT;

	/**
	 * The default value of the '{@link #getRefines() <em>Refines</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @see #getRefines()
	 * @generated
	 * @ordered
	 */
	protected static final String REFINES_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRefines() <em>Refines</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @see #getRefines()
	 * @generated
	 * @ordered
	 */
	protected String refines = REFINES_EDEFAULT;

	/**
	 * The default value of the '{@link #getScheme() <em>Scheme</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @see #getScheme()
	 * @generated
	 * @ordered
	 */
	protected static final String SCHEME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getScheme() <em>Scheme</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @see #getScheme()
	 * @generated
	 * @ordered
	 */
	protected String scheme = SCHEME_EDEFAULT;

	/**
	 * The default value of the '{@link #getDir() <em>Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @see #getDir()
	 * @generated
	 * @ordered
	 */
	protected static final String DIR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDir() <em>Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @see #getDir()
	 * @generated
	 * @ordered
	 */
	protected String dir = DIR_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public MetaImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OPFPackage.Literals.META;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.META__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getContent() {
		return content;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setContent(String newContent) {
		String oldContent = content;
		content = newContent;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.META__CONTENT, oldContent, content));
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.META__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public void setProperty(String newProperty) {
		String oldProperty = property;
		property = newProperty;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.META__PROPERTY, oldProperty, property));
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public String getRefines() {
		return refines;
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public void setRefines(String newRefines) {
		String oldRefines = refines;
		refines = newRefines;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.META__REFINES, oldRefines, refines));
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public String getScheme() {
		return scheme;
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public void setScheme(String newScheme) {
		String oldScheme = scheme;
		scheme = newScheme;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.META__SCHEME, oldScheme, scheme));
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public String getDir() {
		return dir;
	}

	/**
	 * <!-- begin-user-doc -->
	 * 
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public void setDir(String newDir) {
		String oldDir = dir;
		dir = newDir;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.META__DIR, oldDir, dir));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case OPFPackage.META__NAME:
				return getName();
			case OPFPackage.META__CONTENT:
				return getContent();
			case OPFPackage.META__ID:
				return getId();
			case OPFPackage.META__PROPERTY:
				return getProperty();
			case OPFPackage.META__REFINES:
				return getRefines();
			case OPFPackage.META__SCHEME:
				return getScheme();
			case OPFPackage.META__DIR:
				return getDir();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case OPFPackage.META__NAME:
				setName((String)newValue);
				return;
			case OPFPackage.META__CONTENT:
				setContent((String)newValue);
				return;
			case OPFPackage.META__ID:
				setId((String)newValue);
				return;
			case OPFPackage.META__PROPERTY:
				setProperty((String)newValue);
				return;
			case OPFPackage.META__REFINES:
				setRefines((String)newValue);
				return;
			case OPFPackage.META__SCHEME:
				setScheme((String)newValue);
				return;
			case OPFPackage.META__DIR:
				setDir((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case OPFPackage.META__NAME:
				setName(NAME_EDEFAULT);
				return;
			case OPFPackage.META__CONTENT:
				setContent(CONTENT_EDEFAULT);
				return;
			case OPFPackage.META__ID:
				setId(ID_EDEFAULT);
				return;
			case OPFPackage.META__PROPERTY:
				setProperty(PROPERTY_EDEFAULT);
				return;
			case OPFPackage.META__REFINES:
				setRefines(REFINES_EDEFAULT);
				return;
			case OPFPackage.META__SCHEME:
				setScheme(SCHEME_EDEFAULT);
				return;
			case OPFPackage.META__DIR:
				setDir(DIR_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case OPFPackage.META__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case OPFPackage.META__CONTENT:
				return CONTENT_EDEFAULT == null ? content != null : !CONTENT_EDEFAULT.equals(content);
			case OPFPackage.META__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case OPFPackage.META__PROPERTY:
				return PROPERTY_EDEFAULT == null ? property != null : !PROPERTY_EDEFAULT.equals(property);
			case OPFPackage.META__REFINES:
				return REFINES_EDEFAULT == null ? refines != null : !REFINES_EDEFAULT.equals(refines);
			case OPFPackage.META__SCHEME:
				return SCHEME_EDEFAULT == null ? scheme != null : !SCHEME_EDEFAULT.equals(scheme);
			case OPFPackage.META__DIR:
				return DIR_EDEFAULT == null ? dir != null : !DIR_EDEFAULT.equals(dir);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: "); //$NON-NLS-1$
		result.append(name);
		result.append(", content: "); //$NON-NLS-1$
		result.append(content);
		result.append(", id: "); //$NON-NLS-1$
		result.append(id);
		result.append(", property: "); //$NON-NLS-1$
		result.append(property);
		result.append(", refines: "); //$NON-NLS-1$
		result.append(refines);
		result.append(", scheme: "); //$NON-NLS-1$
		result.append(scheme);
		result.append(", dir: "); //$NON-NLS-1$
		result.append(dir);
		result.append(')');
		return result.toString();
	}

} //MetaImpl
