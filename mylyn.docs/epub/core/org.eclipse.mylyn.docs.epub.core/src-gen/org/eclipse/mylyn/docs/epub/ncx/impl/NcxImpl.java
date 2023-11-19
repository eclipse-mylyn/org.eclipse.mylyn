/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.mylyn.docs.epub.ncx.DirType;
import org.eclipse.mylyn.docs.epub.ncx.DocAuthor;
import org.eclipse.mylyn.docs.epub.ncx.DocTitle;
import org.eclipse.mylyn.docs.epub.ncx.Head;
import org.eclipse.mylyn.docs.epub.ncx.NCXPackage;
import org.eclipse.mylyn.docs.epub.ncx.NavList;
import org.eclipse.mylyn.docs.epub.ncx.NavMap;
import org.eclipse.mylyn.docs.epub.ncx.Ncx;
import org.eclipse.mylyn.docs.epub.ncx.PageList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Ncx</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NcxImpl#getHead <em>Head</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NcxImpl#getDocTitle <em>Doc Title</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NcxImpl#getDocAuthors <em>Doc Authors</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NcxImpl#getNavMap <em>Nav Map</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NcxImpl#getPageList <em>Page List</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NcxImpl#getNavLists <em>Nav Lists</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NcxImpl#getDir <em>Dir</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NcxImpl#getLang <em>Lang</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.impl.NcxImpl#getVersion <em>Version</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NcxImpl extends EObjectImpl implements Ncx {
	/**
	 * The cached value of the '{@link #getHead() <em>Head</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHead()
	 * @generated
	 * @ordered
	 */
	protected Head head;

	/**
	 * The cached value of the '{@link #getDocTitle() <em>Doc Title</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDocTitle()
	 * @generated
	 * @ordered
	 */
	protected DocTitle docTitle;

	/**
	 * The cached value of the '{@link #getDocAuthors() <em>Doc Authors</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDocAuthors()
	 * @generated
	 * @ordered
	 */
	protected EList<DocAuthor> docAuthors;

	/**
	 * The cached value of the '{@link #getNavMap() <em>Nav Map</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNavMap()
	 * @generated
	 * @ordered
	 */
	protected NavMap navMap;

	/**
	 * The cached value of the '{@link #getPageList() <em>Page List</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPageList()
	 * @generated
	 * @ordered
	 */
	protected PageList pageList;

	/**
	 * The cached value of the '{@link #getNavLists() <em>Nav Lists</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNavLists()
	 * @generated
	 * @ordered
	 */
	protected EList<NavList> navLists;

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
	 * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected static final String VERSION_EDEFAULT = "2005-1";

	/**
	 * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected String version = VERSION_EDEFAULT;

	/**
	 * This is true if the Version attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean versionESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NcxImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return NCXPackage.Literals.NCX;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Head getHead() {
		return head;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetHead(Head newHead, NotificationChain msgs) {
		Head oldHead = head;
		head = newHead;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, NCXPackage.NCX__HEAD, oldHead, newHead);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setHead(Head newHead) {
		if (newHead != head) {
			NotificationChain msgs = null;
			if (head != null)
				msgs = ((InternalEObject)head).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NCX__HEAD, null, msgs);
			if (newHead != null)
				msgs = ((InternalEObject)newHead).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NCX__HEAD, null, msgs);
			msgs = basicSetHead(newHead, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NCX__HEAD, newHead, newHead));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DocTitle getDocTitle() {
		return docTitle;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDocTitle(DocTitle newDocTitle, NotificationChain msgs) {
		DocTitle oldDocTitle = docTitle;
		docTitle = newDocTitle;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, NCXPackage.NCX__DOC_TITLE, oldDocTitle, newDocTitle);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDocTitle(DocTitle newDocTitle) {
		if (newDocTitle != docTitle) {
			NotificationChain msgs = null;
			if (docTitle != null)
				msgs = ((InternalEObject)docTitle).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NCX__DOC_TITLE, null, msgs);
			if (newDocTitle != null)
				msgs = ((InternalEObject)newDocTitle).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NCX__DOC_TITLE, null, msgs);
			msgs = basicSetDocTitle(newDocTitle, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NCX__DOC_TITLE, newDocTitle, newDocTitle));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<DocAuthor> getDocAuthors() {
		if (docAuthors == null) {
			docAuthors = new EObjectContainmentEList<DocAuthor>(DocAuthor.class, this, NCXPackage.NCX__DOC_AUTHORS);
		}
		return docAuthors;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NavMap getNavMap() {
		return navMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetNavMap(NavMap newNavMap, NotificationChain msgs) {
		NavMap oldNavMap = navMap;
		navMap = newNavMap;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, NCXPackage.NCX__NAV_MAP, oldNavMap, newNavMap);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNavMap(NavMap newNavMap) {
		if (newNavMap != navMap) {
			NotificationChain msgs = null;
			if (navMap != null)
				msgs = ((InternalEObject)navMap).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NCX__NAV_MAP, null, msgs);
			if (newNavMap != null)
				msgs = ((InternalEObject)newNavMap).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NCX__NAV_MAP, null, msgs);
			msgs = basicSetNavMap(newNavMap, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NCX__NAV_MAP, newNavMap, newNavMap));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PageList getPageList() {
		return pageList;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPageList(PageList newPageList, NotificationChain msgs) {
		PageList oldPageList = pageList;
		pageList = newPageList;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, NCXPackage.NCX__PAGE_LIST, oldPageList, newPageList);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPageList(PageList newPageList) {
		if (newPageList != pageList) {
			NotificationChain msgs = null;
			if (pageList != null)
				msgs = ((InternalEObject)pageList).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NCX__PAGE_LIST, null, msgs);
			if (newPageList != null)
				msgs = ((InternalEObject)newPageList).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - NCXPackage.NCX__PAGE_LIST, null, msgs);
			msgs = basicSetPageList(newPageList, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NCX__PAGE_LIST, newPageList, newPageList));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<NavList> getNavLists() {
		if (navLists == null) {
			navLists = new EObjectContainmentEList<NavList>(NavList.class, this, NCXPackage.NCX__NAV_LISTS);
		}
		return navLists;
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
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NCX__DIR, oldDir, dir, !oldDirESet));
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
			eNotify(new ENotificationImpl(this, Notification.UNSET, NCXPackage.NCX__DIR, oldDir, DIR_EDEFAULT, oldDirESet));
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
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NCX__LANG, oldLang, lang));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVersion(String newVersion) {
		String oldVersion = version;
		version = newVersion;
		boolean oldVersionESet = versionESet;
		versionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NCXPackage.NCX__VERSION, oldVersion, version, !oldVersionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetVersion() {
		String oldVersion = version;
		boolean oldVersionESet = versionESet;
		version = VERSION_EDEFAULT;
		versionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, NCXPackage.NCX__VERSION, oldVersion, VERSION_EDEFAULT, oldVersionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetVersion() {
		return versionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case NCXPackage.NCX__HEAD:
				return basicSetHead(null, msgs);
			case NCXPackage.NCX__DOC_TITLE:
				return basicSetDocTitle(null, msgs);
			case NCXPackage.NCX__DOC_AUTHORS:
				return ((InternalEList<?>)getDocAuthors()).basicRemove(otherEnd, msgs);
			case NCXPackage.NCX__NAV_MAP:
				return basicSetNavMap(null, msgs);
			case NCXPackage.NCX__PAGE_LIST:
				return basicSetPageList(null, msgs);
			case NCXPackage.NCX__NAV_LISTS:
				return ((InternalEList<?>)getNavLists()).basicRemove(otherEnd, msgs);
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
			case NCXPackage.NCX__HEAD:
				return getHead();
			case NCXPackage.NCX__DOC_TITLE:
				return getDocTitle();
			case NCXPackage.NCX__DOC_AUTHORS:
				return getDocAuthors();
			case NCXPackage.NCX__NAV_MAP:
				return getNavMap();
			case NCXPackage.NCX__PAGE_LIST:
				return getPageList();
			case NCXPackage.NCX__NAV_LISTS:
				return getNavLists();
			case NCXPackage.NCX__DIR:
				return getDir();
			case NCXPackage.NCX__LANG:
				return getLang();
			case NCXPackage.NCX__VERSION:
				return getVersion();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case NCXPackage.NCX__HEAD:
				setHead((Head)newValue);
				return;
			case NCXPackage.NCX__DOC_TITLE:
				setDocTitle((DocTitle)newValue);
				return;
			case NCXPackage.NCX__DOC_AUTHORS:
				getDocAuthors().clear();
				getDocAuthors().addAll((Collection<? extends DocAuthor>)newValue);
				return;
			case NCXPackage.NCX__NAV_MAP:
				setNavMap((NavMap)newValue);
				return;
			case NCXPackage.NCX__PAGE_LIST:
				setPageList((PageList)newValue);
				return;
			case NCXPackage.NCX__NAV_LISTS:
				getNavLists().clear();
				getNavLists().addAll((Collection<? extends NavList>)newValue);
				return;
			case NCXPackage.NCX__DIR:
				setDir((DirType)newValue);
				return;
			case NCXPackage.NCX__LANG:
				setLang((String)newValue);
				return;
			case NCXPackage.NCX__VERSION:
				setVersion((String)newValue);
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
			case NCXPackage.NCX__HEAD:
				setHead((Head)null);
				return;
			case NCXPackage.NCX__DOC_TITLE:
				setDocTitle((DocTitle)null);
				return;
			case NCXPackage.NCX__DOC_AUTHORS:
				getDocAuthors().clear();
				return;
			case NCXPackage.NCX__NAV_MAP:
				setNavMap((NavMap)null);
				return;
			case NCXPackage.NCX__PAGE_LIST:
				setPageList((PageList)null);
				return;
			case NCXPackage.NCX__NAV_LISTS:
				getNavLists().clear();
				return;
			case NCXPackage.NCX__DIR:
				unsetDir();
				return;
			case NCXPackage.NCX__LANG:
				setLang(LANG_EDEFAULT);
				return;
			case NCXPackage.NCX__VERSION:
				unsetVersion();
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
			case NCXPackage.NCX__HEAD:
				return head != null;
			case NCXPackage.NCX__DOC_TITLE:
				return docTitle != null;
			case NCXPackage.NCX__DOC_AUTHORS:
				return docAuthors != null && !docAuthors.isEmpty();
			case NCXPackage.NCX__NAV_MAP:
				return navMap != null;
			case NCXPackage.NCX__PAGE_LIST:
				return pageList != null;
			case NCXPackage.NCX__NAV_LISTS:
				return navLists != null && !navLists.isEmpty();
			case NCXPackage.NCX__DIR:
				return isSetDir();
			case NCXPackage.NCX__LANG:
				return LANG_EDEFAULT == null ? lang != null : !LANG_EDEFAULT.equals(lang);
			case NCXPackage.NCX__VERSION:
				return isSetVersion();
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
		result.append(", version: ");
		if (versionESet) result.append(version); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //NcxImpl
