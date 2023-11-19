/*******************************************************************************
 * Copyright (c) 2011-2014 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse  License v1.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.core;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil.FeatureEList;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.mylyn.docs.epub.dc.Creator;
import org.eclipse.mylyn.docs.epub.dc.Date;
import org.eclipse.mylyn.docs.epub.dc.Identifier;
import org.eclipse.mylyn.docs.epub.dc.Language;
import org.eclipse.mylyn.docs.epub.dc.Rights;
import org.eclipse.mylyn.docs.epub.dc.Subject;
import org.eclipse.mylyn.docs.epub.dc.Title;
import org.eclipse.mylyn.docs.epub.opf.Item;

/**
 * Simplified representation of an {@link Publication} - designed for common use cases and to make it easier to edit
 * instances in user interfaces etc.
 *
 * @author Torkild U. Resheim
 * @see Publication
 * @since 2.0 TODO: add tests for this type
 */
public class PublicationProxy {

	class ValueComparator implements Comparator<String> {

		Map<String, String> base;

		public ValueComparator(Map<String, String> base) {
			this.base = base;
		}

		public int compare(String a, String b) {
			String key_a = a;
			String key_b = b;

			return key_a.compareTo(key_b);
		}
	}

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private final static String ID = "epub-id"; //$NON-NLS-1$

	private static final String STYLING_ID = "styling"; //$NON-NLS-1$

	private static final EStructuralFeature TEXT = XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_Text();

	private final Publication publication;

	private final File markupFile;

	TreeMap<String, String> sorted_locales;

	public PublicationProxy() {
		publication = Publication.getVersion2Instance();
		markupFile = null;
	}

	public PublicationProxy(Publication publication, File markupFile) {
		this.publication = publication;
		this.markupFile = markupFile;
		String id = publication.getPackage().getUniqueIdentifier();
		if (id == null || id.trim().length() == 0) {
			publication.getPackage().setUniqueIdentifier(ID);
			publication.addIdentifier(ID, "UUID", UUID.randomUUID().toString()); //$NON-NLS-1$
		}
		// Clear everything except the metadata
		publication.getPackage().getManifest().getItems().clear();
		publication.getPackage().getGuide().getGuideItems().clear();
		publication.getPackage().getSpine().getSpineItems().clear();

		sorted_locales = new TreeMap<String, String>();
		String[] iso639s = Locale.getISOLanguages();
		for (String iso639 : iso639s) {
			Locale locale = new Locale(iso639);
			sorted_locales.put(locale.getDisplayLanguage(), locale.getLanguage());
		}
	}

	/**
	 * Returns the path to the file used as a cover image if specified, otherwise an empty string is returned.
	 *
	 * @return the cover image file
	 */
	public String getCover() {
		Item item = publication.getItemById(Publication.COVER_IMAGE_ID);
		if (item == null) {
			return EMPTY_STRING;
		} else {
			return item.getFile();
		}
	}

	/**
	 * Returns the name of the first instance of an {@link Creator} in the publication. If a value has not been assigned
	 * an empty string will be returned.
	 *
	 * @return the creator name
	 */
	@SuppressWarnings("rawtypes")
	public String getCreator() {
		EList<Creator> creators = publication.getPackage().getMetadata().getCreators();
		if (creators.size() > 0) {
			FeatureMap fm = creators.get(0).getMixed();
			Object o = fm.get(TEXT, false);
			if (o instanceof FeatureEList) {
				if (((FeatureEList) o).size() > 0) {
					return ((FeatureEList) o).get(0).toString();
				}
			}
		}
		return EMPTY_STRING;
	}

	@SuppressWarnings("rawtypes")
	public String getIdentifier() {
		EList<Identifier> identifiers = publication.getPackage().getMetadata().getIdentifiers();
		if (identifiers.size() > 0) {
			FeatureMap fm = identifiers.get(0).getMixed();
			Object o = fm.get(TEXT, false);
			if (o instanceof FeatureEList) {
				if (((FeatureEList) o).size() > 0) {
					return ((FeatureEList) o).get(0).toString();
				}
			}
		}
		return EMPTY_STRING;
	}

	@SuppressWarnings("rawtypes")
	public String getLanguage() {
		EList<Language> languages = publication.getPackage().getMetadata().getLanguages();
		if (languages.size() > 0) {
			FeatureMap fm = languages.get(0).getMixed();
			Object o = fm.get(TEXT, false);
			if (o instanceof FeatureEList) {
				if (((FeatureEList) o).size() > 0) {
					String iso639 = ((FeatureEList) o).get(0).toString();
					Locale l = new Locale(iso639);
					return l.getDisplayLanguage();
				}
			}
		}
		return EMPTY_STRING;
	}

	/**
	 * Returns a sorted map of locales that can be used to specify the {@link Language} of the publication.
	 *
	 * @return map of locales
	 */
	public Map<String, String> getLocales() {
		return sorted_locales;
	}

	public File getMarkupFile() {
		return markupFile;
	}

	public Publication getOPSPublication() {
		return publication;
	}

	@SuppressWarnings("rawtypes")
	public String getPublicationDate() {
		EList<Date> dates = publication.getPackage().getMetadata().getDates();
		if (dates.size() > 0) {
			for (Date date : dates) {
				if (date.getEvent().equals("publication")) { //$NON-NLS-1$
					FeatureMap fm = date.getMixed();
					Object o = fm.get(TEXT, false);
					if (o instanceof FeatureEList) {
						if (((FeatureEList) o).size() > 0) {
							return ((FeatureEList) o).get(0).toString();
						}
					}
				}
			}
		}
		return EMPTY_STRING;
	}

	@SuppressWarnings("rawtypes")
	public String getRights() {
		EList<Rights> rights = publication.getPackage().getMetadata().getRights();
		if (rights.size() > 0) {
			FeatureMap fm = rights.get(0).getMixed();
			Object o = fm.get(TEXT, false);
			if (o instanceof FeatureEList) {
				if (((FeatureEList) o).size() > 0) {
					return ((FeatureEList) o).get(0).toString();
				}
			}
		}
		return EMPTY_STRING;
	}

	public String getScheme() {
		Identifier ident = publication.getPackage().getMetadata().getIdentifiers().get(0);
		return ident.getScheme();
	}

	public String getStyleSheet() {
		List<Item> stylesheets = publication.getItemsByMIMEType(Publication.MIMETYPE_CSS);
		if (stylesheets.isEmpty()) {
			return EMPTY_STRING;
		} else {
			return stylesheets.get(0).getHref();
		}
	}

	@SuppressWarnings("rawtypes")
	public String getSubject() {
		EList<Subject> subjects = publication.getPackage().getMetadata().getSubjects();
		if (subjects.size() > 0) {
			FeatureMap fm = subjects.get(0).getMixed();
			Object o = fm.get(TEXT, false);
			if (o instanceof FeatureEList) {
				if (((FeatureEList) o).size() > 0) {
					return ((FeatureEList) o).get(0).toString();
				}
			}
		}
		return EMPTY_STRING;
	}

	@SuppressWarnings("rawtypes")
	public String getTitle() {
		EList<Title> titles = publication.getPackage().getMetadata().getTitles();
		if (titles.size() > 0) {
			FeatureMap fm = titles.get(0).getMixed();
			Object o = fm.get(TEXT, false);
			if (o instanceof FeatureEList) {
				if (((FeatureEList) o).size() > 0) {
					return ((FeatureEList) o).get(0).toString();
				}
			}
		}
		return EMPTY_STRING;
	}

	public void setCover(String cover) {
		if (cover.length() > 0) {
			publication.setCover(new File(cover), "Cover page"); //$NON-NLS-1$
		}
	}

	public void setCreator(String creator) {
		publication.getPackage().getMetadata().getCreators().clear();
		publication.addCreator(creator);
	}

	public void setIdentifier(String identifier) {
		String scheme = getScheme();
		publication.getPackage().getMetadata().getIdentifiers().clear();
		publication.addIdentifier(ID, scheme, identifier);
		publication.getPackage().setUniqueIdentifier(ID);
	}

	/**
	 * Sets the language of the publication. If it already contains one or more language specifications, they will be
	 * removed.
	 *
	 * @param language
	 *            the RFC-3066 format of the language code
	 */
	public void setLanguage(String language) {
		publication.getPackage().getMetadata().getLanguages().clear();
		publication.addLanguage(sorted_locales.get(language));
	}

	/**
	 * Sets the publication date. Must be represented in the form YYYY[-MM[-DD]]: a required 4-digit year, an optional
	 * 2-digit month, and if the month is given, an optional 2-digit day of month.
	 *
	 * @param date
	 *            the date in ISO-8601 format
	 */
	public void setPublicationDate(String date) {
		publication.getPackage().getMetadata().getDates().clear();
		publication.addDate(null, date, "publication"); //$NON-NLS-1$
	}

	public void setRights(String rights) {
		publication.getPackage().getMetadata().getRights().clear();
		publication.addRights(rights);
	}

	public void setScheme(String schemeName) {
		String identifier = getIdentifier();
		publication.getPackage().getMetadata().getIdentifiers().clear();
		publication.getPackage().setUniqueIdentifier(ID);
		publication.addIdentifier(ID, schemeName, identifier);
	}

	public void setStyleSheet(String css) {
		if (css.length() > 0) {
			publication.getPackage().getManifest().getItems().remove(publication.getItemById(STYLING_ID));
			publication.addItem(STYLING_ID, null, new File(css), null, null, false, false, true);
		}
	}

	public void setSubject(String subject) {
		publication.getPackage().getMetadata().getSubjects().clear();
		publication.addSubject(subject);
	}

	public void setTitle(String title) {
		publication.getPackage().getMetadata().getTitles().clear();
		publication.addTitle(title);
	}
}
