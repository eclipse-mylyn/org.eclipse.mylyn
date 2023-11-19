/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.mylyn.docs.epub.ncx.BookStruct;
import org.eclipse.mylyn.docs.epub.ncx.DefaultState;
import org.eclipse.mylyn.docs.epub.ncx.NCXPackage;
import org.eclipse.mylyn.docs.epub.ncx.OverrideType;
import org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Smil Custom Test</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.SmilCustomTestImpl#getBookStruct <em>Book Struct</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.SmilCustomTestImpl#getDefaultState <em>Default State</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.SmilCustomTestImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.SmilCustomTestImpl#getOverride <em>Override</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SmilCustomTestImpl extends EObjectImpl implements SmilCustomTest {
	/**
	 * The default value of the '{@link #getBookStruct() <em>Book Struct</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBookStruct()
	 * @generated
	 * @ordered
	 */
	protected static final BookStruct BOOK_STRUCT_EDEFAULT = BookStruct.PAGENUMBER;

	/**
	 * The cached value of the '{@link #getBookStruct() <em>Book Struct</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBookStruct()
	 * @generated
	 * @ordered
	 */
	protected BookStruct bookStruct = BOOK_STRUCT_EDEFAULT;

	/**
	 * This is true if the Book Struct attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean bookStructESet;

	/**
	 * The default value of the '{@link #getDefaultState() <em>Default State</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefaultState()
	 * @generated
	 * @ordered
	 */
	protected static final DefaultState DEFAULT_STATE_EDEFAULT = DefaultState.FALSE;

	/**
	 * The cached value of the '{@link #getDefaultState() <em>Default State</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefaultState()
	 * @generated
	 * @ordered
	 */
	protected DefaultState defaultState = DEFAULT_STATE_EDEFAULT;

	/**
	 * This is true if the Default State attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean defaultStateESet;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getOverride() <em>Override</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOverride()
	 * @generated
	 * @ordered
	 */
	protected static final OverrideType OVERRIDE_EDEFAULT = OverrideType.HIDDEN;

	/**
	 * The cached value of the '{@link #getOverride() <em>Override</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOverride()
	 * @generated
	 * @ordered
	 */
	protected OverrideType override = OVERRIDE_EDEFAULT;

	/**
	 * This is true if the Override attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean overrideESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SmilCustomTestImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return NCXPackage.Literals.SMIL_CUSTOM_TEST;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BookStruct getBookStruct() {
		return bookStruct;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBookStruct(BookStruct newBookStruct) {
		BookStruct oldBookStruct = bookStruct;
		bookStruct = newBookStruct == null ? BOOK_STRUCT_EDEFAULT : newBookStruct;
		boolean oldBookStructESet = bookStructESet;
		bookStructESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.SMIL_CUSTOM_TEST__BOOK_STRUCT, oldBookStruct, bookStruct, !oldBookStructESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetBookStruct() {
		BookStruct oldBookStruct = bookStruct;
		boolean oldBookStructESet = bookStructESet;
		bookStruct = BOOK_STRUCT_EDEFAULT;
		bookStructESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, NCXPackage.SMIL_CUSTOM_TEST__BOOK_STRUCT, oldBookStruct, BOOK_STRUCT_EDEFAULT, oldBookStructESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetBookStruct() {
		return bookStructESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DefaultState getDefaultState() {
		return defaultState;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDefaultState(DefaultState newDefaultState) {
		DefaultState oldDefaultState = defaultState;
		defaultState = newDefaultState == null ? DEFAULT_STATE_EDEFAULT : newDefaultState;
		boolean oldDefaultStateESet = defaultStateESet;
		defaultStateESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.SMIL_CUSTOM_TEST__DEFAULT_STATE, oldDefaultState, defaultState, !oldDefaultStateESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetDefaultState() {
		DefaultState oldDefaultState = defaultState;
		boolean oldDefaultStateESet = defaultStateESet;
		defaultState = DEFAULT_STATE_EDEFAULT;
		defaultStateESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, NCXPackage.SMIL_CUSTOM_TEST__DEFAULT_STATE, oldDefaultState, DEFAULT_STATE_EDEFAULT, oldDefaultStateESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetDefaultState() {
		return defaultStateESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.SMIL_CUSTOM_TEST__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OverrideType getOverride() {
		return override;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOverride(OverrideType newOverride) {
		OverrideType oldOverride = override;
		override = newOverride == null ? OVERRIDE_EDEFAULT : newOverride;
		boolean oldOverrideESet = overrideESet;
		overrideESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.SMIL_CUSTOM_TEST__OVERRIDE, oldOverride, override, !oldOverrideESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetOverride() {
		OverrideType oldOverride = override;
		boolean oldOverrideESet = overrideESet;
		override = OVERRIDE_EDEFAULT;
		overrideESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, NCXPackage.SMIL_CUSTOM_TEST__OVERRIDE, oldOverride, OVERRIDE_EDEFAULT, oldOverrideESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetOverride() {
		return overrideESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case NCXPackage.SMIL_CUSTOM_TEST__BOOK_STRUCT:
				return getBookStruct();
			case NCXPackage.SMIL_CUSTOM_TEST__DEFAULT_STATE:
				return getDefaultState();
			case NCXPackage.SMIL_CUSTOM_TEST__ID:
				return getId();
			case NCXPackage.SMIL_CUSTOM_TEST__OVERRIDE:
				return getOverride();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case NCXPackage.SMIL_CUSTOM_TEST__BOOK_STRUCT:
				setBookStruct((BookStruct)newValue);
				return;
			case NCXPackage.SMIL_CUSTOM_TEST__DEFAULT_STATE:
				setDefaultState((DefaultState)newValue);
				return;
			case NCXPackage.SMIL_CUSTOM_TEST__ID:
				setId((String)newValue);
				return;
			case NCXPackage.SMIL_CUSTOM_TEST__OVERRIDE:
				setOverride((OverrideType)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case NCXPackage.SMIL_CUSTOM_TEST__BOOK_STRUCT:
				unsetBookStruct();
				return;
			case NCXPackage.SMIL_CUSTOM_TEST__DEFAULT_STATE:
				unsetDefaultState();
				return;
			case NCXPackage.SMIL_CUSTOM_TEST__ID:
				setId(ID_EDEFAULT);
				return;
			case NCXPackage.SMIL_CUSTOM_TEST__OVERRIDE:
				unsetOverride();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case NCXPackage.SMIL_CUSTOM_TEST__BOOK_STRUCT:
				return isSetBookStruct();
			case NCXPackage.SMIL_CUSTOM_TEST__DEFAULT_STATE:
				return isSetDefaultState();
			case NCXPackage.SMIL_CUSTOM_TEST__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case NCXPackage.SMIL_CUSTOM_TEST__OVERRIDE:
				return isSetOverride();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (bookStruct: ");
		if (bookStructESet) result.append(bookStruct); else result.append("<unset>");
		result.append(", defaultState: ");
		if (defaultStateESet) result.append(defaultState); else result.append("<unset>");
		result.append(", id: ");
		result.append(id);
		result.append(", override: ");
		if (overrideESet) result.append(override); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //SmilCustomTestImpl
