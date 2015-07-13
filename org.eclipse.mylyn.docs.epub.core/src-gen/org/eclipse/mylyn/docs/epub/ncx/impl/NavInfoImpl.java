/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.mylyn.docs.epub.ncx.Audio;
import org.eclipse.mylyn.docs.epub.ncx.DirType;
import org.eclipse.mylyn.docs.epub.ncx.Img;
import org.eclipse.mylyn.docs.epub.ncx.NCXPackage;
import org.eclipse.mylyn.docs.epub.ncx.NavInfo;
import org.eclipse.mylyn.docs.epub.ncx.Text;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Nav Info</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavInfoImpl#getText <em>Text</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavInfoImpl#getAudio <em>Audio</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavInfoImpl#getImg <em>Img</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavInfoImpl#getDir <em>Dir</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavInfoImpl#getLang <em>Lang</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NavInfoImpl extends EObjectImpl implements NavInfo {
	/**
	 * The cached value of the '{@link #getText() <em>Text</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getText()
	 * @generated
	 * @ordered
	 */
	protected Text text;

	/**
	 * The cached value of the '{@link #getAudio() <em>Audio</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAudio()
	 * @generated
	 * @ordered
	 */
	protected Audio audio;

	/**
	 * The cached value of the '{@link #getImg() <em>Img</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImg()
	 * @generated
	 * @ordered
	 */
	protected Img img;

	/**
	 * The default value of the '{@link #getDir() <em>Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDir()
	 * @generated
	 * @ordered
	 */
	protected static final DirType DIR_EDEFAULT = DirType.LTR;

	/**
	 * The cached value of the '{@link #getDir() <em>Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDir()
	 * @generated
	 * @ordered
	 */
	protected DirType dir = DIR_EDEFAULT;

	/**
	 * This is true if the Dir attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean dirESet;

	/**
	 * The default value of the '{@link #getLang() <em>Lang</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLang()
	 * @generated
	 * @ordered
	 */
	protected static final String LANG_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLang() <em>Lang</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLang()
	 * @generated
	 * @ordered
	 */
	protected String lang = LANG_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NavInfoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return NCXPackage.Literals.NAV_INFO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Text getText() {
		return text;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetText(Text newText, NotificationChain msgs) {
		Text oldText = text;
		text = newText;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, NCXPackage.NAV_INFO__TEXT, oldText, newText);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setText(Text newText) {
		if (newText != text) {
			NotificationChain msgs = null;
			if (text != null)
				msgs = ((InternalEObject)text).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NAV_INFO__TEXT, null, msgs);
			if (newText != null)
				msgs = ((InternalEObject)newText).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NAV_INFO__TEXT, null, msgs);
			msgs = basicSetText(newText, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NAV_INFO__TEXT, newText, newText));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Audio getAudio() {
		return audio;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetAudio(Audio newAudio, NotificationChain msgs) {
		Audio oldAudio = audio;
		audio = newAudio;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, NCXPackage.NAV_INFO__AUDIO, oldAudio, newAudio);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAudio(Audio newAudio) {
		if (newAudio != audio) {
			NotificationChain msgs = null;
			if (audio != null)
				msgs = ((InternalEObject)audio).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NAV_INFO__AUDIO, null, msgs);
			if (newAudio != null)
				msgs = ((InternalEObject)newAudio).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NAV_INFO__AUDIO, null, msgs);
			msgs = basicSetAudio(newAudio, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NAV_INFO__AUDIO, newAudio, newAudio));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Img getImg() {
		return img;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetImg(Img newImg, NotificationChain msgs) {
		Img oldImg = img;
		img = newImg;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, NCXPackage.NAV_INFO__IMG, oldImg, newImg);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setImg(Img newImg) {
		if (newImg != img) {
			NotificationChain msgs = null;
			if (img != null)
				msgs = ((InternalEObject)img).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NAV_INFO__IMG, null, msgs);
			if (newImg != null)
				msgs = ((InternalEObject)newImg).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NAV_INFO__IMG, null, msgs);
			msgs = basicSetImg(newImg, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NAV_INFO__IMG, newImg, newImg));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DirType getDir() {
		return dir;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDir(DirType newDir) {
		DirType oldDir = dir;
		dir = newDir == null ? DIR_EDEFAULT : newDir;
		boolean oldDirESet = dirESet;
		dirESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NAV_INFO__DIR, oldDir, dir, !oldDirESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetDir() {
		DirType oldDir = dir;
		boolean oldDirESet = dirESet;
		dir = DIR_EDEFAULT;
		dirESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, NCXPackage.NAV_INFO__DIR, oldDir, DIR_EDEFAULT, oldDirESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetDir() {
		return dirESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLang(String newLang) {
		String oldLang = lang;
		lang = newLang;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NAV_INFO__LANG, oldLang, lang));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case NCXPackage.NAV_INFO__TEXT:
				return basicSetText(null, msgs);
			case NCXPackage.NAV_INFO__AUDIO:
				return basicSetAudio(null, msgs);
			case NCXPackage.NAV_INFO__IMG:
				return basicSetImg(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case NCXPackage.NAV_INFO__TEXT:
				return getText();
			case NCXPackage.NAV_INFO__AUDIO:
				return getAudio();
			case NCXPackage.NAV_INFO__IMG:
				return getImg();
			case NCXPackage.NAV_INFO__DIR:
				return getDir();
			case NCXPackage.NAV_INFO__LANG:
				return getLang();
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
			case NCXPackage.NAV_INFO__TEXT:
				setText((Text)newValue);
				return;
			case NCXPackage.NAV_INFO__AUDIO:
				setAudio((Audio)newValue);
				return;
			case NCXPackage.NAV_INFO__IMG:
				setImg((Img)newValue);
				return;
			case NCXPackage.NAV_INFO__DIR:
				setDir((DirType)newValue);
				return;
			case NCXPackage.NAV_INFO__LANG:
				setLang((String)newValue);
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
			case NCXPackage.NAV_INFO__TEXT:
				setText((Text)null);
				return;
			case NCXPackage.NAV_INFO__AUDIO:
				setAudio((Audio)null);
				return;
			case NCXPackage.NAV_INFO__IMG:
				setImg((Img)null);
				return;
			case NCXPackage.NAV_INFO__DIR:
				unsetDir();
				return;
			case NCXPackage.NAV_INFO__LANG:
				setLang(LANG_EDEFAULT);
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
			case NCXPackage.NAV_INFO__TEXT:
				return text != null;
			case NCXPackage.NAV_INFO__AUDIO:
				return audio != null;
			case NCXPackage.NAV_INFO__IMG:
				return img != null;
			case NCXPackage.NAV_INFO__DIR:
				return isSetDir();
			case NCXPackage.NAV_INFO__LANG:
				return LANG_EDEFAULT == null ? lang != null : !LANG_EDEFAULT.equals(lang);
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
		result.append(" (dir: ");
		if (dirESet) result.append(dir); else result.append("<unset>");
		result.append(", lang: ");
		result.append(lang);
		result.append(')');
		return result.toString();
	}

} //NavInfoImpl
