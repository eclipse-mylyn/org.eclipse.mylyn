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
import org.eclipse.mylyn.docs.epub.opf.Item;
import org.eclipse.mylyn.docs.epub.opf.OPFPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Item</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl#getHref <em>Href</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl#getMedia_type <em>Media type</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl#getFallback <em>Fallback</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl#getFallback_style <em>Fallback style</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl#getRequired_namespace <em>Required namespace</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl#getRequired_modules <em>Required modules</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl#getFile <em>File</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl#isNoToc <em>No Toc</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl#isGenerated <em>Generated</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl#getSourcePath <em>Source Path</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl#getMedia_overlay <em>Media overlay</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ItemImpl extends EObjectImpl implements Item {
	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getHref() <em>Href</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see #getHref()
	 * @generated
	 * @ordered
	 */
	protected static final String HREF_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getHref() <em>Href</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see #getHref()
	 * @generated
	 * @ordered
	 */
	protected String href = HREF_EDEFAULT;

	/**
	 * The default value of the '{@link #getMedia_type() <em>Media type</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getMedia_type()
	 * @generated
	 * @ordered
	 */
	protected static final String MEDIA_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMedia_type() <em>Media type</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getMedia_type()
	 * @generated
	 * @ordered
	 */
	protected String media_type = MEDIA_TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getFallback() <em>Fallback</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getFallback()
	 * @generated
	 * @ordered
	 */
	protected static final String FALLBACK_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFallback() <em>Fallback</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getFallback()
	 * @generated
	 * @ordered
	 */
	protected String fallback = FALLBACK_EDEFAULT;

	/**
	 * The default value of the '{@link #getFallback_style() <em>Fallback style</em>}' attribute.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #getFallback_style()
	 * @generated
	 * @ordered
	 */
	protected static final String FALLBACK_STYLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFallback_style() <em>Fallback style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFallback_style()
	 * @generated
	 * @ordered
	 */
	protected String fallback_style = FALLBACK_STYLE_EDEFAULT;

	/**
	 * The default value of the '{@link #getRequired_namespace() <em>Required namespace</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getRequired_namespace()
	 * @generated
	 * @ordered
	 */
	protected static final String REQUIRED_NAMESPACE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRequired_namespace() <em>Required namespace</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getRequired_namespace()
	 * @generated
	 * @ordered
	 */
	protected String required_namespace = REQUIRED_NAMESPACE_EDEFAULT;

	/**
	 * The default value of the '{@link #getRequired_modules() <em>Required modules</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getRequired_modules()
	 * @generated
	 * @ordered
	 */
	protected static final String REQUIRED_MODULES_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRequired_modules() <em>Required modules</em>}' attribute.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see #getRequired_modules()
	 * @generated
	 * @ordered
	 */
	protected String required_modules = REQUIRED_MODULES_EDEFAULT;

	/**
	 * The default value of the '{@link #getFile() <em>File</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see #getFile()
	 * @generated
	 * @ordered
	 */
	protected static final String FILE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFile() <em>File</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see #getFile()
	 * @generated
	 * @ordered
	 */
	protected String file = FILE_EDEFAULT;

	/**
	 * The default value of the '{@link #isNoToc() <em>No Toc</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #isNoToc()
	 * @generated
	 * @ordered
	 */
	protected static final boolean NO_TOC_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isNoToc() <em>No Toc</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see #isNoToc()
	 * @generated
	 * @ordered
	 */
	protected boolean noToc = NO_TOC_EDEFAULT;

	/**
	 * The default value of the '{@link #getTitle() <em>Title</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected static final String TITLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTitle() <em>Title</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected String title = TITLE_EDEFAULT;

	/**
	 * The default value of the '{@link #isGenerated() <em>Generated</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #isGenerated()
	 * @generated
	 * @ordered
	 */
	protected static final boolean GENERATED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isGenerated() <em>Generated</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #isGenerated()
	 * @generated
	 * @ordered
	 */
	protected boolean generated = GENERATED_EDEFAULT;

	/**
	 * The default value of the '{@link #getSourcePath() <em>Source Path</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getSourcePath()
	 * @generated
	 * @ordered
	 */
	protected static final String SOURCE_PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSourcePath() <em>Source Path</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getSourcePath()
	 * @generated
	 * @ordered
	 */
	protected String sourcePath = SOURCE_PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #getProperties() <em>Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @see #getProperties()
	 * @generated
	 * @ordered
	 */
	protected static final String PROPERTIES_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProperties() <em>Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @see #getProperties()
	 * @generated
	 * @ordered
	 */
	protected String properties = PROPERTIES_EDEFAULT;

	/**
	 * The default value of the '{@link #getMedia_overlay() <em>Media overlay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @see #getMedia_overlay()
	 * @generated
	 * @ordered
	 */
	protected static final String MEDIA_OVERLAY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMedia_overlay() <em>Media overlay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @see #getMedia_overlay()
	 * @generated
	 * @ordered
	 */
	protected String media_overlay = MEDIA_OVERLAY_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public ItemImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OPFPackage.Literals.ITEM;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.ITEM__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getHref() {
		return href;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setHref(String newHref) {
		String oldHref = href;
		href = newHref;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.ITEM__HREF, oldHref, href));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getMedia_type() {
		return media_type;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setMedia_type(String newMedia_type) {
		String oldMedia_type = media_type;
		media_type = newMedia_type;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.ITEM__MEDIA_TYPE, oldMedia_type, media_type));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getFallback() {
		return fallback;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setFallback(String newFallback) {
		String oldFallback = fallback;
		fallback = newFallback;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.ITEM__FALLBACK, oldFallback, fallback));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getFallback_style() {
		return fallback_style;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setFallback_style(String newFallback_style) {
		String oldFallback_style = fallback_style;
		fallback_style = newFallback_style;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.ITEM__FALLBACK_STYLE, oldFallback_style, fallback_style));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getRequired_namespace() {
		return required_namespace;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setRequired_namespace(String newRequired_namespace) {
		String oldRequired_namespace = required_namespace;
		required_namespace = newRequired_namespace;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.ITEM__REQUIRED_NAMESPACE, oldRequired_namespace, required_namespace));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getRequired_modules() {
		return required_modules;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setRequired_modules(String newRequired_modules) {
		String oldRequired_modules = required_modules;
		required_modules = newRequired_modules;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.ITEM__REQUIRED_MODULES, oldRequired_modules, required_modules));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getFile() {
		return file;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setFile(String newFile) {
		String oldFile = file;
		file = newFile;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.ITEM__FILE, oldFile, file));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isNoToc() {
		return noToc;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setNoToc(boolean newNoToc) {
		boolean oldNoToc = noToc;
		noToc = newNoToc;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.ITEM__NO_TOC, oldNoToc, noToc));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setTitle(String newTitle) {
		String oldTitle = title;
		title = newTitle;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.ITEM__TITLE, oldTitle, title));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isGenerated() {
		return generated;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setGenerated(boolean newGenerated) {
		boolean oldGenerated = generated;
		generated = newGenerated;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.ITEM__GENERATED, oldGenerated, generated));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getSourcePath() {
		return sourcePath;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setSourcePath(String newSourcePath) {
		String oldSourcePath = sourcePath;
		sourcePath = newSourcePath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.ITEM__SOURCE_PATH, oldSourcePath, sourcePath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * 
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public String getProperties() {
		return properties;
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public void setProperties(String newProperties) {
		String oldProperties = properties;
		properties = newProperties;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.ITEM__PROPERTIES, oldProperties, properties));
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public String getMedia_overlay() {
		return media_overlay;
	}

	/**
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @generated
	 */
	public void setMedia_overlay(String newMedia_overlay) {
		String oldMedia_overlay = media_overlay;
		media_overlay = newMedia_overlay;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OPFPackage.ITEM__MEDIA_OVERLAY, oldMedia_overlay, media_overlay));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case OPFPackage.ITEM__ID:
				return getId();
			case OPFPackage.ITEM__HREF:
				return getHref();
			case OPFPackage.ITEM__MEDIA_TYPE:
				return getMedia_type();
			case OPFPackage.ITEM__FALLBACK:
				return getFallback();
			case OPFPackage.ITEM__FALLBACK_STYLE:
				return getFallback_style();
			case OPFPackage.ITEM__REQUIRED_NAMESPACE:
				return getRequired_namespace();
			case OPFPackage.ITEM__REQUIRED_MODULES:
				return getRequired_modules();
			case OPFPackage.ITEM__FILE:
				return getFile();
			case OPFPackage.ITEM__NO_TOC:
				return isNoToc();
			case OPFPackage.ITEM__TITLE:
				return getTitle();
			case OPFPackage.ITEM__GENERATED:
				return isGenerated();
			case OPFPackage.ITEM__SOURCE_PATH:
				return getSourcePath();
			case OPFPackage.ITEM__PROPERTIES:
				return getProperties();
			case OPFPackage.ITEM__MEDIA_OVERLAY:
				return getMedia_overlay();
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
			case OPFPackage.ITEM__ID:
				setId((String)newValue);
				return;
			case OPFPackage.ITEM__HREF:
				setHref((String)newValue);
				return;
			case OPFPackage.ITEM__MEDIA_TYPE:
				setMedia_type((String)newValue);
				return;
			case OPFPackage.ITEM__FALLBACK:
				setFallback((String)newValue);
				return;
			case OPFPackage.ITEM__FALLBACK_STYLE:
				setFallback_style((String)newValue);
				return;
			case OPFPackage.ITEM__REQUIRED_NAMESPACE:
				setRequired_namespace((String)newValue);
				return;
			case OPFPackage.ITEM__REQUIRED_MODULES:
				setRequired_modules((String)newValue);
				return;
			case OPFPackage.ITEM__FILE:
				setFile((String)newValue);
				return;
			case OPFPackage.ITEM__NO_TOC:
				setNoToc((Boolean)newValue);
				return;
			case OPFPackage.ITEM__TITLE:
				setTitle((String)newValue);
				return;
			case OPFPackage.ITEM__GENERATED:
				setGenerated((Boolean)newValue);
				return;
			case OPFPackage.ITEM__SOURCE_PATH:
				setSourcePath((String)newValue);
				return;
			case OPFPackage.ITEM__PROPERTIES:
				setProperties((String)newValue);
				return;
			case OPFPackage.ITEM__MEDIA_OVERLAY:
				setMedia_overlay((String)newValue);
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
			case OPFPackage.ITEM__ID:
				setId(ID_EDEFAULT);
				return;
			case OPFPackage.ITEM__HREF:
				setHref(HREF_EDEFAULT);
				return;
			case OPFPackage.ITEM__MEDIA_TYPE:
				setMedia_type(MEDIA_TYPE_EDEFAULT);
				return;
			case OPFPackage.ITEM__FALLBACK:
				setFallback(FALLBACK_EDEFAULT);
				return;
			case OPFPackage.ITEM__FALLBACK_STYLE:
				setFallback_style(FALLBACK_STYLE_EDEFAULT);
				return;
			case OPFPackage.ITEM__REQUIRED_NAMESPACE:
				setRequired_namespace(REQUIRED_NAMESPACE_EDEFAULT);
				return;
			case OPFPackage.ITEM__REQUIRED_MODULES:
				setRequired_modules(REQUIRED_MODULES_EDEFAULT);
				return;
			case OPFPackage.ITEM__FILE:
				setFile(FILE_EDEFAULT);
				return;
			case OPFPackage.ITEM__NO_TOC:
				setNoToc(NO_TOC_EDEFAULT);
				return;
			case OPFPackage.ITEM__TITLE:
				setTitle(TITLE_EDEFAULT);
				return;
			case OPFPackage.ITEM__GENERATED:
				setGenerated(GENERATED_EDEFAULT);
				return;
			case OPFPackage.ITEM__SOURCE_PATH:
				setSourcePath(SOURCE_PATH_EDEFAULT);
				return;
			case OPFPackage.ITEM__PROPERTIES:
				setProperties(PROPERTIES_EDEFAULT);
				return;
			case OPFPackage.ITEM__MEDIA_OVERLAY:
				setMedia_overlay(MEDIA_OVERLAY_EDEFAULT);
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
			case OPFPackage.ITEM__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case OPFPackage.ITEM__HREF:
				return HREF_EDEFAULT == null ? href != null : !HREF_EDEFAULT.equals(href);
			case OPFPackage.ITEM__MEDIA_TYPE:
				return MEDIA_TYPE_EDEFAULT == null ? media_type != null : !MEDIA_TYPE_EDEFAULT.equals(media_type);
			case OPFPackage.ITEM__FALLBACK:
				return FALLBACK_EDEFAULT == null ? fallback != null : !FALLBACK_EDEFAULT.equals(fallback);
			case OPFPackage.ITEM__FALLBACK_STYLE:
				return FALLBACK_STYLE_EDEFAULT == null ? fallback_style != null : !FALLBACK_STYLE_EDEFAULT.equals(fallback_style);
			case OPFPackage.ITEM__REQUIRED_NAMESPACE:
				return REQUIRED_NAMESPACE_EDEFAULT == null ? required_namespace != null : !REQUIRED_NAMESPACE_EDEFAULT.equals(required_namespace);
			case OPFPackage.ITEM__REQUIRED_MODULES:
				return REQUIRED_MODULES_EDEFAULT == null ? required_modules != null : !REQUIRED_MODULES_EDEFAULT.equals(required_modules);
			case OPFPackage.ITEM__FILE:
				return FILE_EDEFAULT == null ? file != null : !FILE_EDEFAULT.equals(file);
			case OPFPackage.ITEM__NO_TOC:
				return noToc != NO_TOC_EDEFAULT;
			case OPFPackage.ITEM__TITLE:
				return TITLE_EDEFAULT == null ? title != null : !TITLE_EDEFAULT.equals(title);
			case OPFPackage.ITEM__GENERATED:
				return generated != GENERATED_EDEFAULT;
			case OPFPackage.ITEM__SOURCE_PATH:
				return SOURCE_PATH_EDEFAULT == null ? sourcePath != null : !SOURCE_PATH_EDEFAULT.equals(sourcePath);
			case OPFPackage.ITEM__PROPERTIES:
				return PROPERTIES_EDEFAULT == null ? properties != null : !PROPERTIES_EDEFAULT.equals(properties);
			case OPFPackage.ITEM__MEDIA_OVERLAY:
				return MEDIA_OVERLAY_EDEFAULT == null ? media_overlay != null : !MEDIA_OVERLAY_EDEFAULT.equals(media_overlay);
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
		result.append(" (id: "); //$NON-NLS-1$
		result.append(id);
		result.append(", href: "); //$NON-NLS-1$
		result.append(href);
		result.append(", media_type: "); //$NON-NLS-1$
		result.append(media_type);
		result.append(", fallback: "); //$NON-NLS-1$
		result.append(fallback);
		result.append(", fallback_style: "); //$NON-NLS-1$
		result.append(fallback_style);
		result.append(", required_namespace: "); //$NON-NLS-1$
		result.append(required_namespace);
		result.append(", required_modules: "); //$NON-NLS-1$
		result.append(required_modules);
		result.append(", file: "); //$NON-NLS-1$
		result.append(file);
		result.append(", noToc: "); //$NON-NLS-1$
		result.append(noToc);
		result.append(", title: "); //$NON-NLS-1$
		result.append(title);
		result.append(", generated: "); //$NON-NLS-1$
		result.append(generated);
		result.append(", sourcePath: "); //$NON-NLS-1$
		result.append(sourcePath);
		result.append(", properties: "); //$NON-NLS-1$
		result.append(properties);
		result.append(", media_overlay: "); //$NON-NLS-1$
		result.append(media_overlay);
		result.append(')');
		return result.toString();
	}

} //ItemImpl
