/*******************************************************************************
 * Copyright (c) 2011,2012 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse  License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.docs.epub.ui;

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
import org.eclipse.mylyn.docs.epub.core.OPSPublication;
import org.eclipse.mylyn.docs.epub.dc.Creator;
import org.eclipse.mylyn.docs.epub.dc.Date;
import org.eclipse.mylyn.docs.epub.dc.Identifier;
import org.eclipse.mylyn.docs.epub.dc.Language;
import org.eclipse.mylyn.docs.epub.dc.Rights;
import org.eclipse.mylyn.docs.epub.dc.Subject;
import org.eclipse.mylyn.docs.epub.dc.Title;
import org.eclipse.mylyn.docs.epub.opf.Item;

/**
 * Simplified representation of an EPUB revision 2.0 instance.
 * 
 * @author Torkild U. Resheim
 */
class EPUB2Bean {

	private static final String STYLING_ID = "styling"; //$NON-NLS-1$

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private static final EStructuralFeature TEXT = XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_Text();

	private final OPSPublication epub;

	private final File markupFile;

	public File getMarkupFile() {
		return markupFile;
	}

	public EPUB2Bean() {
		epub = OPSPublication.getVersion2Instance();
		markupFile = null;
	}

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

	TreeMap<String, String> sorted_locales;

	public EPUB2Bean(OPSPublication epub, File markupFile, File epubFile, File workingFolder) {
		this.epub = epub;
		this.markupFile = markupFile;
		String id = epub.getOpfPackage().getUniqueIdentifier();
		if (id == null || id.trim().length() == 0) {
			epub.getOpfPackage().setUniqueIdentifier(ID);
			epub.addIdentifier(ID, "UUID", UUID.randomUUID().toString()); //$NON-NLS-1$
		}
		// Clear everything except the metadata
		epub.getOpfPackage().getManifest().getItems().clear();
		epub.getOpfPackage().getGuide().getGuideItems().clear();
		epub.getOpfPackage().getSpine().getSpineItems().clear();

		sorted_locales = new TreeMap<String, String>();
		String[] iso639s = Locale.getISOLanguages();
		for (String iso639 : iso639s) {
			Locale locale = new Locale(iso639);
			sorted_locales.put(locale.getDisplayLanguage(), locale.getLanguage());
		}
	}

	public String getCover() {
		Item item = epub.getItemById(OPSPublication.COVER_IMAGE_ID);
		if (item == null) {
			return EMPTY_STRING;
		} else {
			return item.getFile();
		}
	}

	@SuppressWarnings("rawtypes")
	public String getCreator() {
		EList<Creator> creators = epub.getOpfPackage().getMetadata().getCreators();
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

	public OPSPublication getEPUB() {
		return epub;
	}

	@SuppressWarnings("rawtypes")
	public String getIdentifier() {
		EList<Identifier> identifiers = epub.getOpfPackage().getMetadata().getIdentifiers();
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
		EList<Language> languages = epub.getOpfPackage().getMetadata().getLanguages();
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

	public void setLanguage(String language) {
		epub.getOpfPackage().getMetadata().getLanguages().clear();
		epub.addLanguage(null, sorted_locales.get(language));
	}

	@SuppressWarnings("rawtypes")
	public String getRights() {
		EList<Rights> rights = epub.getOpfPackage().getMetadata().getRights();
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

	public String getStyleSheet() {
		List<Item> stylesheets = epub.getItemsByMIMEType(OPSPublication.MIMETYPE_CSS);
		if (stylesheets.isEmpty()) {
			return EMPTY_STRING;
		} else {
			return stylesheets.get(0).getHref();
		}
	}

	@SuppressWarnings("rawtypes")
	public String getSubject() {
		EList<Subject> subjects = epub.getOpfPackage().getMetadata().getSubjects();
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
		EList<Title> titles = epub.getOpfPackage().getMetadata().getTitles();
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
		epub.setCover(new File(cover), Messages.EPUB2Bean_0);
	}

	public void setCreator(String creator) {
		epub.getOpfPackage().getMetadata().getCreators().clear();
		epub.addCreator(null, null, creator, null, null);
	}

	private final static String ID = "epub-id"; //$NON-NLS-1$

	public void setIdentifier(String identifier) {
		String scheme = getScheme();
		epub.getOpfPackage().getMetadata().getIdentifiers().clear();
		epub.addIdentifier(ID, scheme, identifier);
		epub.getOpfPackage().setUniqueIdentifier(ID);
	}

	public void setScheme(String schemeName) {
		String identifier = getIdentifier();
		epub.getOpfPackage().getMetadata().getIdentifiers().clear();
		epub.getOpfPackage().setUniqueIdentifier(ID);
		epub.addIdentifier(ID, schemeName, identifier);
	}

	public String getScheme() {
		Identifier ident = epub.getOpfPackage().getMetadata().getIdentifiers().get(0);
		return ident.getScheme();
	}

	public void setRights(String rights) {
		epub.getOpfPackage().getMetadata().getRights().clear();
		epub.addRights(null, null, rights);
	}

	public void setStyleSheet(String css) {
		epub.getOpfPackage().getManifest().getItems().remove(epub.getItemsByMIMEType(OPSPublication.MIMETYPE_CSS));
		epub.addItem(STYLING_ID, null, new File(css), null, null, false, false, true);
	}

	public void setSubject(String subject) {
		epub.getOpfPackage().getMetadata().getSubjects().clear();
		epub.addSubject(null, null, subject);
	}

	public void setTitle(String title) {
		epub.getOpfPackage().getMetadata().getTitles().clear();
		epub.addTitle(null, null, title);
	}

	public void setPublicationDate(String date) {
		epub.getOpfPackage().getMetadata().getDates().clear();
		epub.addDate(null, date, "publication"); //$NON-NLS-1$
	}

	@SuppressWarnings("rawtypes")
	public String getPublicationDate() {
		EList<Date> dates = epub.getOpfPackage().getMetadata().getDates();
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

	public Map<String, String> getLocales() {
		return sorted_locales;
	}
}
