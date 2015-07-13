/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.opf.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.mylyn.docs.epub.dc.Contributor;
import org.eclipse.mylyn.docs.epub.dc.Coverage;
import org.eclipse.mylyn.docs.epub.dc.Creator;
import org.eclipse.mylyn.docs.epub.dc.Date;
import org.eclipse.mylyn.docs.epub.dc.Description;
import org.eclipse.mylyn.docs.epub.dc.Format;
import org.eclipse.mylyn.docs.epub.dc.Identifier;
import org.eclipse.mylyn.docs.epub.dc.Language;
import org.eclipse.mylyn.docs.epub.dc.Publisher;
import org.eclipse.mylyn.docs.epub.dc.Relation;
import org.eclipse.mylyn.docs.epub.dc.Rights;
import org.eclipse.mylyn.docs.epub.dc.Source;
import org.eclipse.mylyn.docs.epub.dc.Subject;
import org.eclipse.mylyn.docs.epub.dc.Title;
import org.eclipse.mylyn.docs.epub.dc.Type;

import org.eclipse.mylyn.docs.epub.opf.Meta;
import org.eclipse.mylyn.docs.epub.opf.Metadata;
import org.eclipse.mylyn.docs.epub.opf.OPFPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Metadata</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getTitles <em>Titles</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getCreators <em>Creators</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getSubjects <em>Subjects</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getDescriptions <em>Descriptions</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getPublishers <em>Publishers</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getContributors <em>Contributors</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getDates <em>Dates</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getTypes <em>Types</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getFormats <em>Formats</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getIdentifiers <em>Identifiers</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getSources <em>Sources</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getLanguages <em>Languages</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getRelations <em>Relations</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getCoverages <em>Coverages</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getRights <em>Rights</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl#getMetas <em>Metas</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MetadataImpl extends EObjectImpl implements Metadata {
	/**
	 * The cached value of the '{@link #getTitles() <em>Titles</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTitles()
	 * @generated
	 * @ordered
	 */
	protected EList<Title> titles;

	/**
	 * The cached value of the '{@link #getCreators() <em>Creators</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreators()
	 * @generated
	 * @ordered
	 */
	protected EList<Creator> creators;

	/**
	 * The cached value of the '{@link #getSubjects() <em>Subjects</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubjects()
	 * @generated
	 * @ordered
	 */
	protected EList<Subject> subjects;

	/**
	 * The cached value of the '{@link #getDescriptions() <em>Descriptions</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescriptions()
	 * @generated
	 * @ordered
	 */
	protected EList<Description> descriptions;

	/**
	 * The cached value of the '{@link #getPublishers() <em>Publishers</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPublishers()
	 * @generated
	 * @ordered
	 */
	protected EList<Publisher> publishers;

	/**
	 * The cached value of the '{@link #getContributors() <em>Contributors</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContributors()
	 * @generated
	 * @ordered
	 */
	protected EList<Contributor> contributors;

	/**
	 * The cached value of the '{@link #getDates() <em>Dates</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDates()
	 * @generated
	 * @ordered
	 */
	protected EList<Date> dates;

	/**
	 * The cached value of the '{@link #getTypes() <em>Types</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypes()
	 * @generated
	 * @ordered
	 */
	protected EList<Type> types;

	/**
	 * The cached value of the '{@link #getFormats() <em>Formats</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFormats()
	 * @generated
	 * @ordered
	 */
	protected EList<Format> formats;

	/**
	 * The cached value of the '{@link #getIdentifiers() <em>Identifiers</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIdentifiers()
	 * @generated
	 * @ordered
	 */
	protected EList<Identifier> identifiers;

	/**
	 * The cached value of the '{@link #getSources() <em>Sources</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSources()
	 * @generated
	 * @ordered
	 */
	protected EList<Source> sources;

	/**
	 * The cached value of the '{@link #getLanguages() <em>Languages</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLanguages()
	 * @generated
	 * @ordered
	 */
	protected EList<Language> languages;

	/**
	 * The cached value of the '{@link #getRelations() <em>Relations</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRelations()
	 * @generated
	 * @ordered
	 */
	protected EList<Relation> relations;

	/**
	 * The cached value of the '{@link #getCoverages() <em>Coverages</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCoverages()
	 * @generated
	 * @ordered
	 */
	protected EList<Coverage> coverages;

	/**
	 * The cached value of the '{@link #getRights() <em>Rights</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRights()
	 * @generated
	 * @ordered
	 */
	protected EList<Rights> rights;

	/**
	 * The cached value of the '{@link #getMetas() <em>Metas</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMetas()
	 * @generated
	 * @ordered
	 */
	protected EList<Meta> metas;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MetadataImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OPFPackage.Literals.METADATA;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Title> getTitles() {
		if (titles == null) {
			titles = new EObjectContainmentEList<Title>(Title.class, this, OPFPackage.METADATA__TITLES);
		}
		return titles;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Creator> getCreators() {
		if (creators == null) {
			creators = new EObjectContainmentEList<Creator>(Creator.class, this, OPFPackage.METADATA__CREATORS);
		}
		return creators;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Subject> getSubjects() {
		if (subjects == null) {
			subjects = new EObjectContainmentEList<Subject>(Subject.class, this, OPFPackage.METADATA__SUBJECTS);
		}
		return subjects;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Description> getDescriptions() {
		if (descriptions == null) {
			descriptions = new EObjectContainmentEList<Description>(Description.class, this, OPFPackage.METADATA__DESCRIPTIONS);
		}
		return descriptions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Publisher> getPublishers() {
		if (publishers == null) {
			publishers = new EObjectContainmentEList<Publisher>(Publisher.class, this, OPFPackage.METADATA__PUBLISHERS);
		}
		return publishers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Contributor> getContributors() {
		if (contributors == null) {
			contributors = new EObjectContainmentEList<Contributor>(Contributor.class, this, OPFPackage.METADATA__CONTRIBUTORS);
		}
		return contributors;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Date> getDates() {
		if (dates == null) {
			dates = new EObjectContainmentEList<Date>(Date.class, this, OPFPackage.METADATA__DATES);
		}
		return dates;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Type> getTypes() {
		if (types == null) {
			types = new EObjectContainmentEList<Type>(Type.class, this, OPFPackage.METADATA__TYPES);
		}
		return types;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Format> getFormats() {
		if (formats == null) {
			formats = new EObjectContainmentEList<Format>(Format.class, this, OPFPackage.METADATA__FORMATS);
		}
		return formats;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Identifier> getIdentifiers() {
		if (identifiers == null) {
			identifiers = new EObjectContainmentEList<Identifier>(Identifier.class, this, OPFPackage.METADATA__IDENTIFIERS);
		}
		return identifiers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Source> getSources() {
		if (sources == null) {
			sources = new EObjectContainmentEList<Source>(Source.class, this, OPFPackage.METADATA__SOURCES);
		}
		return sources;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Language> getLanguages() {
		if (languages == null) {
			languages = new EObjectContainmentEList<Language>(Language.class, this, OPFPackage.METADATA__LANGUAGES);
		}
		return languages;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Relation> getRelations() {
		if (relations == null) {
			relations = new EObjectContainmentEList<Relation>(Relation.class, this, OPFPackage.METADATA__RELATIONS);
		}
		return relations;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Coverage> getCoverages() {
		if (coverages == null) {
			coverages = new EObjectContainmentEList<Coverage>(Coverage.class, this, OPFPackage.METADATA__COVERAGES);
		}
		return coverages;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Rights> getRights() {
		if (rights == null) {
			rights = new EObjectContainmentEList<Rights>(Rights.class, this, OPFPackage.METADATA__RIGHTS);
		}
		return rights;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Meta> getMetas() {
		if (metas == null) {
			metas = new EObjectContainmentEList<Meta>(Meta.class, this, OPFPackage.METADATA__METAS);
		}
		return metas;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case OPFPackage.METADATA__TITLES:
				return ((InternalEList<?>)getTitles()).basicRemove(otherEnd, msgs);
			case OPFPackage.METADATA__CREATORS:
				return ((InternalEList<?>)getCreators()).basicRemove(otherEnd, msgs);
			case OPFPackage.METADATA__SUBJECTS:
				return ((InternalEList<?>)getSubjects()).basicRemove(otherEnd, msgs);
			case OPFPackage.METADATA__DESCRIPTIONS:
				return ((InternalEList<?>)getDescriptions()).basicRemove(otherEnd, msgs);
			case OPFPackage.METADATA__PUBLISHERS:
				return ((InternalEList<?>)getPublishers()).basicRemove(otherEnd, msgs);
			case OPFPackage.METADATA__CONTRIBUTORS:
				return ((InternalEList<?>)getContributors()).basicRemove(otherEnd, msgs);
			case OPFPackage.METADATA__DATES:
				return ((InternalEList<?>)getDates()).basicRemove(otherEnd, msgs);
			case OPFPackage.METADATA__TYPES:
				return ((InternalEList<?>)getTypes()).basicRemove(otherEnd, msgs);
			case OPFPackage.METADATA__FORMATS:
				return ((InternalEList<?>)getFormats()).basicRemove(otherEnd, msgs);
			case OPFPackage.METADATA__IDENTIFIERS:
				return ((InternalEList<?>)getIdentifiers()).basicRemove(otherEnd, msgs);
			case OPFPackage.METADATA__SOURCES:
				return ((InternalEList<?>)getSources()).basicRemove(otherEnd, msgs);
			case OPFPackage.METADATA__LANGUAGES:
				return ((InternalEList<?>)getLanguages()).basicRemove(otherEnd, msgs);
			case OPFPackage.METADATA__RELATIONS:
				return ((InternalEList<?>)getRelations()).basicRemove(otherEnd, msgs);
			case OPFPackage.METADATA__COVERAGES:
				return ((InternalEList<?>)getCoverages()).basicRemove(otherEnd, msgs);
			case OPFPackage.METADATA__RIGHTS:
				return ((InternalEList<?>)getRights()).basicRemove(otherEnd, msgs);
			case OPFPackage.METADATA__METAS:
				return ((InternalEList<?>)getMetas()).basicRemove(otherEnd, msgs);
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
			case OPFPackage.METADATA__TITLES:
				return getTitles();
			case OPFPackage.METADATA__CREATORS:
				return getCreators();
			case OPFPackage.METADATA__SUBJECTS:
				return getSubjects();
			case OPFPackage.METADATA__DESCRIPTIONS:
				return getDescriptions();
			case OPFPackage.METADATA__PUBLISHERS:
				return getPublishers();
			case OPFPackage.METADATA__CONTRIBUTORS:
				return getContributors();
			case OPFPackage.METADATA__DATES:
				return getDates();
			case OPFPackage.METADATA__TYPES:
				return getTypes();
			case OPFPackage.METADATA__FORMATS:
				return getFormats();
			case OPFPackage.METADATA__IDENTIFIERS:
				return getIdentifiers();
			case OPFPackage.METADATA__SOURCES:
				return getSources();
			case OPFPackage.METADATA__LANGUAGES:
				return getLanguages();
			case OPFPackage.METADATA__RELATIONS:
				return getRelations();
			case OPFPackage.METADATA__COVERAGES:
				return getCoverages();
			case OPFPackage.METADATA__RIGHTS:
				return getRights();
			case OPFPackage.METADATA__METAS:
				return getMetas();
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
			case OPFPackage.METADATA__TITLES:
				getTitles().clear();
				getTitles().addAll((Collection<? extends Title>)newValue);
				return;
			case OPFPackage.METADATA__CREATORS:
				getCreators().clear();
				getCreators().addAll((Collection<? extends Creator>)newValue);
				return;
			case OPFPackage.METADATA__SUBJECTS:
				getSubjects().clear();
				getSubjects().addAll((Collection<? extends Subject>)newValue);
				return;
			case OPFPackage.METADATA__DESCRIPTIONS:
				getDescriptions().clear();
				getDescriptions().addAll((Collection<? extends Description>)newValue);
				return;
			case OPFPackage.METADATA__PUBLISHERS:
				getPublishers().clear();
				getPublishers().addAll((Collection<? extends Publisher>)newValue);
				return;
			case OPFPackage.METADATA__CONTRIBUTORS:
				getContributors().clear();
				getContributors().addAll((Collection<? extends Contributor>)newValue);
				return;
			case OPFPackage.METADATA__DATES:
				getDates().clear();
				getDates().addAll((Collection<? extends Date>)newValue);
				return;
			case OPFPackage.METADATA__TYPES:
				getTypes().clear();
				getTypes().addAll((Collection<? extends Type>)newValue);
				return;
			case OPFPackage.METADATA__FORMATS:
				getFormats().clear();
				getFormats().addAll((Collection<? extends Format>)newValue);
				return;
			case OPFPackage.METADATA__IDENTIFIERS:
				getIdentifiers().clear();
				getIdentifiers().addAll((Collection<? extends Identifier>)newValue);
				return;
			case OPFPackage.METADATA__SOURCES:
				getSources().clear();
				getSources().addAll((Collection<? extends Source>)newValue);
				return;
			case OPFPackage.METADATA__LANGUAGES:
				getLanguages().clear();
				getLanguages().addAll((Collection<? extends Language>)newValue);
				return;
			case OPFPackage.METADATA__RELATIONS:
				getRelations().clear();
				getRelations().addAll((Collection<? extends Relation>)newValue);
				return;
			case OPFPackage.METADATA__COVERAGES:
				getCoverages().clear();
				getCoverages().addAll((Collection<? extends Coverage>)newValue);
				return;
			case OPFPackage.METADATA__RIGHTS:
				getRights().clear();
				getRights().addAll((Collection<? extends Rights>)newValue);
				return;
			case OPFPackage.METADATA__METAS:
				getMetas().clear();
				getMetas().addAll((Collection<? extends Meta>)newValue);
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
			case OPFPackage.METADATA__TITLES:
				getTitles().clear();
				return;
			case OPFPackage.METADATA__CREATORS:
				getCreators().clear();
				return;
			case OPFPackage.METADATA__SUBJECTS:
				getSubjects().clear();
				return;
			case OPFPackage.METADATA__DESCRIPTIONS:
				getDescriptions().clear();
				return;
			case OPFPackage.METADATA__PUBLISHERS:
				getPublishers().clear();
				return;
			case OPFPackage.METADATA__CONTRIBUTORS:
				getContributors().clear();
				return;
			case OPFPackage.METADATA__DATES:
				getDates().clear();
				return;
			case OPFPackage.METADATA__TYPES:
				getTypes().clear();
				return;
			case OPFPackage.METADATA__FORMATS:
				getFormats().clear();
				return;
			case OPFPackage.METADATA__IDENTIFIERS:
				getIdentifiers().clear();
				return;
			case OPFPackage.METADATA__SOURCES:
				getSources().clear();
				return;
			case OPFPackage.METADATA__LANGUAGES:
				getLanguages().clear();
				return;
			case OPFPackage.METADATA__RELATIONS:
				getRelations().clear();
				return;
			case OPFPackage.METADATA__COVERAGES:
				getCoverages().clear();
				return;
			case OPFPackage.METADATA__RIGHTS:
				getRights().clear();
				return;
			case OPFPackage.METADATA__METAS:
				getMetas().clear();
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
			case OPFPackage.METADATA__TITLES:
				return titles != null && !titles.isEmpty();
			case OPFPackage.METADATA__CREATORS:
				return creators != null && !creators.isEmpty();
			case OPFPackage.METADATA__SUBJECTS:
				return subjects != null && !subjects.isEmpty();
			case OPFPackage.METADATA__DESCRIPTIONS:
				return descriptions != null && !descriptions.isEmpty();
			case OPFPackage.METADATA__PUBLISHERS:
				return publishers != null && !publishers.isEmpty();
			case OPFPackage.METADATA__CONTRIBUTORS:
				return contributors != null && !contributors.isEmpty();
			case OPFPackage.METADATA__DATES:
				return dates != null && !dates.isEmpty();
			case OPFPackage.METADATA__TYPES:
				return types != null && !types.isEmpty();
			case OPFPackage.METADATA__FORMATS:
				return formats != null && !formats.isEmpty();
			case OPFPackage.METADATA__IDENTIFIERS:
				return identifiers != null && !identifiers.isEmpty();
			case OPFPackage.METADATA__SOURCES:
				return sources != null && !sources.isEmpty();
			case OPFPackage.METADATA__LANGUAGES:
				return languages != null && !languages.isEmpty();
			case OPFPackage.METADATA__RELATIONS:
				return relations != null && !relations.isEmpty();
			case OPFPackage.METADATA__COVERAGES:
				return coverages != null && !coverages.isEmpty();
			case OPFPackage.METADATA__RIGHTS:
				return rights != null && !rights.isEmpty();
			case OPFPackage.METADATA__METAS:
				return metas != null && !metas.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //MetadataImpl
