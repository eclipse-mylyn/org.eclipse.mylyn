/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.opf.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.mylyn.docs.epub.opf.Guide;
import org.eclipse.mylyn.docs.epub.opf.Manifest;
import org.eclipse.mylyn.docs.epub.opf.Metadata;
import org.eclipse.mylyn.docs.epub.opf.OPFPackage;
import org.eclipse.mylyn.docs.epub.opf.Spine;
import org.eclipse.mylyn.docs.epub.opf.Tours;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Package</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl#getMetadata <em>Metadata</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl#getManifest <em>Manifest</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl#getSpine <em>Spine</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl#getGuide <em>Guide</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl#getTours <em>Tours</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl#getUniqueIdentifier <em>Unique Identifier</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl#isGenerateCoverHTML <em>Generate Cover HTML</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl#isGenerateTableOfContents <em>Generate Table Of Contents</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl#isIncludeReferencedResources <em>Include Referenced Resources</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl#getPrefix <em>Prefix</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl#getLang <em>Lang</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl#getDir <em>Dir</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl#getId <em>Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PackageImpl extends EObjectImpl implements org.eclipse.mylyn.docs.epub.opf.Package {
	/**
	 * The cached value of the '{@link #getMetadata() <em>Metadata</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMetadata()
	 * @generated
	 * @ordered
	 */
	protected Metadata metadata;

	/**
	 * The cached value of the '{@link #getManifest() <em>Manifest</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getManifest()
	 * @generated
	 * @ordered
	 */
	protected Manifest manifest;

	/**
	 * The cached value of the '{@link #getSpine() <em>Spine</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSpine()
	 * @generated
	 * @ordered
	 */
	protected Spine spine;

	/**
	 * The cached value of the '{@link #getGuide() <em>Guide</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGuide()
	 * @generated
	 * @ordered
	 */
	protected Guide guide;

	/**
	 * The cached value of the '{@link #getTours() <em>Tours</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTours()
	 * @generated
	 * @ordered
	 */
	protected Tours tours;

	/**
	 * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected static final String VERSION_EDEFAULT = "2.0"; //$NON-NLS-1$

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
	 * The default value of the '{@link #getUniqueIdentifier() <em>Unique Identifier</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUniqueIdentifier()
	 * @generated
	 * @ordered
	 */
	protected static final String UNIQUE_IDENTIFIER_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUniqueIdentifier() <em>Unique Identifier</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUniqueIdentifier()
	 * @generated
	 * @ordered
	 */
	protected String uniqueIdentifier = UNIQUE_IDENTIFIER_EDEFAULT;

	/**
	 * The default value of the '{@link #isGenerateCoverHTML() <em>Generate Cover HTML</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isGenerateCoverHTML()
	 * @generated
	 * @ordered
	 */
	protected static final boolean GENERATE_COVER_HTML_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isGenerateCoverHTML() <em>Generate Cover HTML</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isGenerateCoverHTML()
	 * @generated
	 * @ordered
	 */
	protected boolean generateCoverHTML = GENERATE_COVER_HTML_EDEFAULT;

	/**
	 * The default value of the '{@link #isGenerateTableOfContents() <em>Generate Table Of Contents</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isGenerateTableOfContents()
	 * @generated
	 * @ordered
	 */
	protected static final boolean GENERATE_TABLE_OF_CONTENTS_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isGenerateTableOfContents() <em>Generate Table Of Contents</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isGenerateTableOfContents()
	 * @generated
	 * @ordered
	 */
	protected boolean generateTableOfContents = GENERATE_TABLE_OF_CONTENTS_EDEFAULT;

	/**
	 * The default value of the '{@link #isIncludeReferencedResources() <em>Include Referenced Resources</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIncludeReferencedResources()
	 * @generated
	 * @ordered
	 */
	protected static final boolean INCLUDE_REFERENCED_RESOURCES_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIncludeReferencedResources() <em>Include Referenced Resources</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIncludeReferencedResources()
	 * @generated
	 * @ordered
	 */
	protected boolean includeReferencedResources = INCLUDE_REFERENCED_RESOURCES_EDEFAULT;

	/**
	 * The default value of the '{@link #getPrefix() <em>Prefix</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @see #getPrefix()
	 * @generated
	 * @ordered
	 */
	protected static final String PREFIX_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPrefix() <em>Prefix</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @see #getPrefix()
	 * @generated
	 * @ordered
	 */
	protected String prefix = PREFIX_EDEFAULT;

	/**
	 * The default value of the '{@link #getLang() <em>Lang</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @see #getLang()
	 * @generated
	 * @ordered
	 */
	protected static final String LANG_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLang() <em>Lang</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @see #getLang()
	 * @generated
	 * @ordered
	 */
	protected String lang = LANG_EDEFAULT;

	/**
	 * The default value of the '{@link #getDir() <em>Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @see #getDir()
	 * @generated
	 * @ordered
	 */
	protected static final String DIR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDir() <em>Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @see #getDir()
	 * @generated
	 * @ordered
	 */
	protected String dir = DIR_EDEFAULT;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc --> 
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PackageImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OPFPackage.Literals.PACKAGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Metadata getMetadata() {
		return metadata;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetMetadata(Metadata newMetadata, NotificationChain msgs) {
		Metadata oldMetadata = metadata;
		metadata = newMetadata;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__METADATA, oldMetadata, newMetadata);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMetadata(Metadata newMetadata) {
		if (newMetadata != metadata) {
			NotificationChain msgs = null;
			if (metadata != null)
				msgs = ((InternalEObject)metadata).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OPFPackage.PACKAGE__METADATA, null, msgs);
			if (newMetadata != null)
				msgs = ((InternalEObject)newMetadata).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OPFPackage.PACKAGE__METADATA, null, msgs);
			msgs = basicSetMetadata(newMetadata, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__METADATA, newMetadata, newMetadata));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Manifest getManifest() {
		return manifest;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetManifest(Manifest newManifest, NotificationChain msgs) {
		Manifest oldManifest = manifest;
		manifest = newManifest;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__MANIFEST, oldManifest, newManifest);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setManifest(Manifest newManifest) {
		if (newManifest != manifest) {
			NotificationChain msgs = null;
			if (manifest != null)
				msgs = ((InternalEObject)manifest).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OPFPackage.PACKAGE__MANIFEST, null, msgs);
			if (newManifest != null)
				msgs = ((InternalEObject)newManifest).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OPFPackage.PACKAGE__MANIFEST, null, msgs);
			msgs = basicSetManifest(newManifest, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__MANIFEST, newManifest, newManifest));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Spine getSpine() {
		return spine;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSpine(Spine newSpine, NotificationChain msgs) {
		Spine oldSpine = spine;
		spine = newSpine;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__SPINE, oldSpine, newSpine);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSpine(Spine newSpine) {
		if (newSpine != spine) {
			NotificationChain msgs = null;
			if (spine != null)
				msgs = ((InternalEObject)spine).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OPFPackage.PACKAGE__SPINE, null, msgs);
			if (newSpine != null)
				msgs = ((InternalEObject)newSpine).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OPFPackage.PACKAGE__SPINE, null, msgs);
			msgs = basicSetSpine(newSpine, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__SPINE, newSpine, newSpine));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Guide getGuide() {
		return guide;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetGuide(Guide newGuide, NotificationChain msgs) {
		Guide oldGuide = guide;
		guide = newGuide;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__GUIDE, oldGuide, newGuide);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGuide(Guide newGuide) {
		if (newGuide != guide) {
			NotificationChain msgs = null;
			if (guide != null)
				msgs = ((InternalEObject)guide).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OPFPackage.PACKAGE__GUIDE, null, msgs);
			if (newGuide != null)
				msgs = ((InternalEObject)newGuide).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OPFPackage.PACKAGE__GUIDE, null, msgs);
			msgs = basicSetGuide(newGuide, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__GUIDE, newGuide, newGuide));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Tours getTours() {
		if (tours != null && tours.eIsProxy()) {
			InternalEObject oldTours = (InternalEObject)tours;
			tours = (Tours)eResolveProxy(oldTours);
			if (tours != oldTours) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, OPFPackage.PACKAGE__TOURS, oldTours, tours));
			}
		}
		return tours;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Tours basicGetTours() {
		return tours;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTours(Tours newTours) {
		Tours oldTours = tours;
		tours = newTours;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__TOURS, oldTours, tours));
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
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__VERSION, oldVersion, version, !oldVersionESet));
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
			eNotify(new ENotificationImpl(this, Notification.UNSET, OPFPackage.PACKAGE__VERSION, oldVersion, VERSION_EDEFAULT, oldVersionESet));
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
	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUniqueIdentifier(String newUniqueIdentifier) {
		String oldUniqueIdentifier = uniqueIdentifier;
		uniqueIdentifier = newUniqueIdentifier;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__UNIQUE_IDENTIFIER, oldUniqueIdentifier, uniqueIdentifier));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isGenerateCoverHTML() {
		return generateCoverHTML;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGenerateCoverHTML(boolean newGenerateCoverHTML) {
		boolean oldGenerateCoverHTML = generateCoverHTML;
		generateCoverHTML = newGenerateCoverHTML;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__GENERATE_COVER_HTML, oldGenerateCoverHTML, generateCoverHTML));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isGenerateTableOfContents() {
		return generateTableOfContents;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGenerateTableOfContents(boolean newGenerateTableOfContents) {
		boolean oldGenerateTableOfContents = generateTableOfContents;
		generateTableOfContents = newGenerateTableOfContents;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__GENERATE_TABLE_OF_CONTENTS, oldGenerateTableOfContents, generateTableOfContents));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isIncludeReferencedResources() {
		return includeReferencedResources;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIncludeReferencedResources(boolean newIncludeReferencedResources) {
		boolean oldIncludeReferencedResources = includeReferencedResources;
		includeReferencedResources = newIncludeReferencedResources;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__INCLUDE_REFERENCED_RESOURCES, oldIncludeReferencedResources, includeReferencedResources));
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPrefix(String newPrefix) {
		String oldPrefix = prefix;
		prefix = newPrefix;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__PREFIX, oldPrefix, prefix));
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLang(String newLang) {
		String oldLang = lang;
		lang = newLang;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__LANG, oldLang, lang));
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDir() {
		return dir;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDir(String newDir) {
		String oldDir = dir;
		dir = newDir;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__DIR, oldDir, dir));
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 3.0 
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.PACKAGE__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case OPFPackage.PACKAGE__METADATA:
				return basicSetMetadata(null, msgs);
			case OPFPackage.PACKAGE__MANIFEST:
				return basicSetManifest(null, msgs);
			case OPFPackage.PACKAGE__SPINE:
				return basicSetSpine(null, msgs);
			case OPFPackage.PACKAGE__GUIDE:
				return basicSetGuide(null, msgs);
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
			case OPFPackage.PACKAGE__METADATA:
				return getMetadata();
			case OPFPackage.PACKAGE__MANIFEST:
				return getManifest();
			case OPFPackage.PACKAGE__SPINE:
				return getSpine();
			case OPFPackage.PACKAGE__GUIDE:
				return getGuide();
			case OPFPackage.PACKAGE__TOURS:
				if (resolve) return getTours();
				return basicGetTours();
			case OPFPackage.PACKAGE__VERSION:
				return getVersion();
			case OPFPackage.PACKAGE__UNIQUE_IDENTIFIER:
				return getUniqueIdentifier();
			case OPFPackage.PACKAGE__GENERATE_COVER_HTML:
				return isGenerateCoverHTML();
			case OPFPackage.PACKAGE__GENERATE_TABLE_OF_CONTENTS:
				return isGenerateTableOfContents();
			case OPFPackage.PACKAGE__INCLUDE_REFERENCED_RESOURCES:
				return isIncludeReferencedResources();
			case OPFPackage.PACKAGE__PREFIX:
				return getPrefix();
			case OPFPackage.PACKAGE__LANG:
				return getLang();
			case OPFPackage.PACKAGE__DIR:
				return getDir();
			case OPFPackage.PACKAGE__ID:
				return getId();
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
			case OPFPackage.PACKAGE__METADATA:
				setMetadata((Metadata)newValue);
				return;
			case OPFPackage.PACKAGE__MANIFEST:
				setManifest((Manifest)newValue);
				return;
			case OPFPackage.PACKAGE__SPINE:
				setSpine((Spine)newValue);
				return;
			case OPFPackage.PACKAGE__GUIDE:
				setGuide((Guide)newValue);
				return;
			case OPFPackage.PACKAGE__TOURS:
				setTours((Tours)newValue);
				return;
			case OPFPackage.PACKAGE__VERSION:
				setVersion((String)newValue);
				return;
			case OPFPackage.PACKAGE__UNIQUE_IDENTIFIER:
				setUniqueIdentifier((String)newValue);
				return;
			case OPFPackage.PACKAGE__GENERATE_COVER_HTML:
				setGenerateCoverHTML((Boolean)newValue);
				return;
			case OPFPackage.PACKAGE__GENERATE_TABLE_OF_CONTENTS:
				setGenerateTableOfContents((Boolean)newValue);
				return;
			case OPFPackage.PACKAGE__INCLUDE_REFERENCED_RESOURCES:
				setIncludeReferencedResources((Boolean)newValue);
				return;
			case OPFPackage.PACKAGE__PREFIX:
				setPrefix((String)newValue);
				return;
			case OPFPackage.PACKAGE__LANG:
				setLang((String)newValue);
				return;
			case OPFPackage.PACKAGE__DIR:
				setDir((String)newValue);
				return;
			case OPFPackage.PACKAGE__ID:
				setId((String)newValue);
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
			case OPFPackage.PACKAGE__METADATA:
				setMetadata((Metadata)null);
				return;
			case OPFPackage.PACKAGE__MANIFEST:
				setManifest((Manifest)null);
				return;
			case OPFPackage.PACKAGE__SPINE:
				setSpine((Spine)null);
				return;
			case OPFPackage.PACKAGE__GUIDE:
				setGuide((Guide)null);
				return;
			case OPFPackage.PACKAGE__TOURS:
				setTours((Tours)null);
				return;
			case OPFPackage.PACKAGE__VERSION:
				unsetVersion();
				return;
			case OPFPackage.PACKAGE__UNIQUE_IDENTIFIER:
				setUniqueIdentifier(UNIQUE_IDENTIFIER_EDEFAULT);
				return;
			case OPFPackage.PACKAGE__GENERATE_COVER_HTML:
				setGenerateCoverHTML(GENERATE_COVER_HTML_EDEFAULT);
				return;
			case OPFPackage.PACKAGE__GENERATE_TABLE_OF_CONTENTS:
				setGenerateTableOfContents(GENERATE_TABLE_OF_CONTENTS_EDEFAULT);
				return;
			case OPFPackage.PACKAGE__INCLUDE_REFERENCED_RESOURCES:
				setIncludeReferencedResources(INCLUDE_REFERENCED_RESOURCES_EDEFAULT);
				return;
			case OPFPackage.PACKAGE__PREFIX:
				setPrefix(PREFIX_EDEFAULT);
				return;
			case OPFPackage.PACKAGE__LANG:
				setLang(LANG_EDEFAULT);
				return;
			case OPFPackage.PACKAGE__DIR:
				setDir(DIR_EDEFAULT);
				return;
			case OPFPackage.PACKAGE__ID:
				setId(ID_EDEFAULT);
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
			case OPFPackage.PACKAGE__METADATA:
				return metadata != null;
			case OPFPackage.PACKAGE__MANIFEST:
				return manifest != null;
			case OPFPackage.PACKAGE__SPINE:
				return spine != null;
			case OPFPackage.PACKAGE__GUIDE:
				return guide != null;
			case OPFPackage.PACKAGE__TOURS:
				return tours != null;
			case OPFPackage.PACKAGE__VERSION:
				return isSetVersion();
			case OPFPackage.PACKAGE__UNIQUE_IDENTIFIER:
				return UNIQUE_IDENTIFIER_EDEFAULT == null ? uniqueIdentifier != null : !UNIQUE_IDENTIFIER_EDEFAULT.equals(uniqueIdentifier);
			case OPFPackage.PACKAGE__GENERATE_COVER_HTML:
				return generateCoverHTML != GENERATE_COVER_HTML_EDEFAULT;
			case OPFPackage.PACKAGE__GENERATE_TABLE_OF_CONTENTS:
				return generateTableOfContents != GENERATE_TABLE_OF_CONTENTS_EDEFAULT;
			case OPFPackage.PACKAGE__INCLUDE_REFERENCED_RESOURCES:
				return includeReferencedResources != INCLUDE_REFERENCED_RESOURCES_EDEFAULT;
			case OPFPackage.PACKAGE__PREFIX:
				return PREFIX_EDEFAULT == null ? prefix != null : !PREFIX_EDEFAULT.equals(prefix);
			case OPFPackage.PACKAGE__LANG:
				return LANG_EDEFAULT == null ? lang != null : !LANG_EDEFAULT.equals(lang);
			case OPFPackage.PACKAGE__DIR:
				return DIR_EDEFAULT == null ? dir != null : !DIR_EDEFAULT.equals(dir);
			case OPFPackage.PACKAGE__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
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
		result.append(" (version: "); //$NON-NLS-1$
		if (versionESet) result.append(version); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", uniqueIdentifier: "); //$NON-NLS-1$
		result.append(uniqueIdentifier);
		result.append(", generateCoverHTML: "); //$NON-NLS-1$
		result.append(generateCoverHTML);
		result.append(", generateTableOfContents: "); //$NON-NLS-1$
		result.append(generateTableOfContents);
		result.append(", includeReferencedResources: "); //$NON-NLS-1$
		result.append(includeReferencedResources);
		result.append(", prefix: "); //$NON-NLS-1$
		result.append(prefix);
		result.append(", lang: "); //$NON-NLS-1$
		result.append(lang);
		result.append(", dir: "); //$NON-NLS-1$
		result.append(dir);
		result.append(", id: "); //$NON-NLS-1$
		result.append(id);
		result.append(')');
		return result.toString();
	}

} //PackageImpl
