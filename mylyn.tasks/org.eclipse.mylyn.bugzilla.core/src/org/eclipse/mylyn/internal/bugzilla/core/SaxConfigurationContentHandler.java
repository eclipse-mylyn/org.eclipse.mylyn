/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Quick config rdf parser.
 * 
 * <pre>
 * config.cgi?ctype=rdf
 * </pre>
 * 
 * Populates a <link>RepositoryConfiguration</link> data structure.
 * 
 * @author Rob Elves
 */
public class SaxConfigurationContentHandler extends DefaultHandler {

	private static final String ELEMENT_RESOLUTION = "resolution"; //$NON-NLS-1$

	private static final String ELEMENT_STATUS_OPEN = "status_open"; //$NON-NLS-1$

	private static final String ELEMENT_STATUS_CLOSED = "status_closed"; //$NON-NLS-1$

	private static final String ELEMENT_TARGET_MILESTONE = "target_milestone"; //$NON-NLS-1$

	private static final String ELEMENT_TARGET_MILESTONES = "target_milestones"; //$NON-NLS-1$

	private static final String ELEMENT_INSTALL_VERSION = "install_version"; //$NON-NLS-1$

	private static final String ELEMENT_DB_ENCODING = "db_encoding"; //$NON-NLS-1$

	private static final String ATTRIBUTE_RDF_ABOUT = "rdf:about"; //$NON-NLS-1$

	private static final String ATTRIBUTE_RESOURCE = "resource"; //$NON-NLS-1$

	private static final String ELEMENT_VERSION = "version"; //$NON-NLS-1$

	private static final String ELEMENT_VERSIONS = "versions"; //$NON-NLS-1$

	private static final String ELEMENT_COMPONENT = "component"; //$NON-NLS-1$

	private static final String ELEMENT_COMPONENTS = "components"; //$NON-NLS-1$

	private static final String ELEMENT_NAME = "name"; //$NON-NLS-1$

	private static final String ELEMENT_PRODUCTS = "products"; //$NON-NLS-1$

	private static final String ELEMENT_DESCRIPTION = "description"; //$NON-NLS-1$

	private static final String ELEMENT_FIELDS = "fields"; //$NON-NLS-1$

	private static final String ELEMENT_FIELD = "field"; //$NON-NLS-1$

	private static final String ELEMENT_FLAG_TYPES = "flag_types"; //$NON-NLS-1$

	private static final String ELEMENT_FLAG_TYPE = "flag_type"; //$NON-NLS-1$

	private static final String ELEMENT_SEVERITY = "severity"; //$NON-NLS-1$

	private static final String ELEMENT_PRIORITY = "priority"; //$NON-NLS-1$

	private static final String ELEMENT_KEYWORD = "keyword"; //$NON-NLS-1$

	private static final String ELEMENT_KEYWORDS = "keywords"; //$NON-NLS-1$

	private static final String ELEMENT_OP_SYS = "op_sys"; //$NON-NLS-1$

	private static final String ELEMENT_PLATFORM = "platform"; //$NON-NLS-1$

	private static final String ELEMENT_LI = "li"; //$NON-NLS-1$

	private static final String ELEMENT_STATUS = "status"; //$NON-NLS-1$

	private static final String ELEMENT_TYPE = "type"; //$NON-NLS-1$

	private static final String ELEMENT_ENTER_BUG = "enter_bug"; //$NON-NLS-1$

	private static final String ELEMENT_REQUESTABLE = "requestable"; //$NON-NLS-1$

	private static final String ELEMENT_SPECIFICALLY_REQUESTABLE = "specifically_requestable"; //$NON-NLS-1$

	private static final String ELEMENT_ID = "id"; //$NON-NLS-1$

	private static final String ELEMENT_MULTIPLICABLE = "multiplicable"; //$NON-NLS-1$

	private static final String ELEMENT_ALLOWS_UNCONFIRMED = "allows_unconfirmed"; //$NON-NLS-1$

	private static final int EXPECTING_ROOT = 0;

	private static final int IN_INSTALL_VERSION = 1 << 1;

	private static final int IN_STATUS = 1 << 2;

	private static final int IN_PLATFORM = 1 << 3;

	private static final int IN_OP_SYS = 1 << 4;

	private static final int IN_PRIORITY = 1 << 5;

	private static final int IN_SEVERITY = 1 << 6;

	private static final int IN_PRODUCTS = 1 << 7;

	private static final int IN_COMPONENTS = 1 << 8;

	private static final int IN_VERSIONS = 1 << 9;

	private static final int IN_LI = 1 << 10;

	private static final int IN_LI_LI = 1 << 11;

	private static final int IN_NAME = 1 << 12;

	private static final int IN_COMPONENT = 1 << 13;

	private static final int IN_VERSION = 1 << 14;

	private static final int IN_TARGET_MILESTONES = 1 << 15;

	private static final int IN_TARGET_MILESTONE = 1 << 16;

	private static final int IN_STATUS_OPEN = 1 << 17;

	private static final int IN_RESOLUTION = 1 << 18;

	private static final int IN_KEYWORD = 1 << 19;

	private static final int IN_STATUS_CLOSED = 1 << 20;

	private static final int IN_FIELDS = 1 << 21;

	private static final int IN_FIELD = 1 << 22;

	private static final int IN_CUSTOM_OPTION = 1 << 23;

	private static final int IN_FLAG_TYPES = 1 << 24;

	private static final int IN_FLAG_TYPE = 1 << 25;

	private static final int IN_DB_ENCODING = 1 << 26;

	private static final int IN_KEYWORDS = 1 << 27;

	private int state = EXPECTING_ROOT;

	private String currentProduct = ""; //$NON-NLS-1$

	private String currentName = ""; //$NON-NLS-1$

	private String currentDescription = ""; //$NON-NLS-1$

	private String currentType;

	private String currentRequestable;

	private String currentSpecifically_requestable;

	private String currentMultiplicable;

	private int currentId;

	private String currentEnterBug = ""; //$NON-NLS-1$

	private StringBuffer characters = new StringBuffer();

	private String about;

	private final RepositoryConfiguration configuration;

	private final Map<String, List<String>> components = new HashMap<String, List<String>>();

	private final Map<String, List<String>> versions = new HashMap<String, List<String>>();

	private final Map<String, List<String>> milestones = new HashMap<String, List<String>>();

	private final Map<String, String> componentNames = new HashMap<String, String>();

	private final Map<String, String> versionNames = new HashMap<String, String>();

	private final Map<String, String> milestoneNames = new HashMap<String, String>();

	private final Map<String, List<String>> customOption = new HashMap<String, List<String>>();

	private final Map<String, Map<String, List<String>>> flagsInComponent = new HashMap<String, Map<String, List<String>>>();

	private final Map<String, Integer> flagIds = new HashMap<String, Integer>();

	private String currentComponent = ""; //$NON-NLS-1$

	private String currentCustomOptionName = ""; //$NON-NLS-1$

	public SaxConfigurationContentHandler() {
		configuration = new RepositoryConfiguration();
	}

	public RepositoryConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		characters.append(ch, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		characters = new StringBuffer();
		if (localName.equals(ELEMENT_STATUS)) {
			state = state | IN_STATUS;
		} else if (localName.equals(ELEMENT_LI) && ((state & IN_LI) == IN_LI)) {
			state = state | IN_LI_LI;
			parseResource(attributes);
		} else if (localName.equals(ELEMENT_LI) && ((state & IN_LI) != IN_LI)) {
			state = state | IN_LI;
		} else if (localName.equals(ELEMENT_PLATFORM)) {
			state = state | IN_PLATFORM;
		} else if (localName.equals(ELEMENT_OP_SYS)) {
			state = state | IN_OP_SYS;
		} else if (localName.equals(ELEMENT_PRIORITY)) {
			state = state | IN_PRIORITY;
		} else if (localName.equals(ELEMENT_SEVERITY)) {
			state = state | IN_SEVERITY;
		} else if (localName.equals(ELEMENT_PRODUCTS)) {
			state = state | IN_PRODUCTS;
		} else if (localName.equals(ELEMENT_OP_SYS)) {
			state = state | IN_OP_SYS;
		} else if (localName.equals(ELEMENT_NAME)) {
			state = state | IN_NAME;
		} else if (localName.equals(ELEMENT_COMPONENTS)) {
			state = state | IN_COMPONENTS;
		} else if (localName.equals(ELEMENT_COMPONENT)) {
			state = state | IN_COMPONENT;
			parseResource(attributes);
		} else if (localName.equals(ELEMENT_VERSIONS)) {
			state = state | IN_VERSIONS;
		} else if (localName.equals(ELEMENT_VERSION)) {
			state = state | IN_VERSION;
			parseResource(attributes);
		} else if (localName.equals(ELEMENT_INSTALL_VERSION)) {
			state = state | IN_INSTALL_VERSION;
		} else if (localName.equals(ELEMENT_DB_ENCODING)) {
			state = state | IN_DB_ENCODING;
		} else if (localName.equals(ELEMENT_TARGET_MILESTONES)) {
			state = state | IN_TARGET_MILESTONES;
		} else if (localName.equals(ELEMENT_TARGET_MILESTONE)) {
			state = state | IN_TARGET_MILESTONE;
			parseResource(attributes);
		} else if (localName.equals(ELEMENT_STATUS_OPEN)) {
			state = state | IN_STATUS_OPEN;
		} else if (localName.equals(ELEMENT_STATUS_CLOSED)) {
			state = state | IN_STATUS_CLOSED;
		} else if (localName.equals(ELEMENT_RESOLUTION)) {
			state = state | IN_RESOLUTION;
		} else if (localName.equals(ELEMENT_KEYWORD)) {
			state = state | IN_KEYWORD;
		} else if (localName.equals(ELEMENT_KEYWORDS)) {
			state = state | IN_KEYWORDS;
		} else if (localName.equals(ELEMENT_FIELDS)) {
			state = state | IN_FIELDS;
		} else if (localName.equals(ELEMENT_FIELD)) {
			state = state | IN_FIELD;
			parseResource(attributes);
			currentName = ""; //$NON-NLS-1$
			currentDescription = ""; //$NON-NLS-1$
			currentType = ""; //$NON-NLS-1$
			currentEnterBug = ""; //$NON-NLS-1$
			currentId = -1;
		} else if (localName.equals(ELEMENT_FLAG_TYPES)) {
			state = state | IN_FLAG_TYPES;
		} else if (localName.equals(ELEMENT_FLAG_TYPE)) {
			state = state | IN_FLAG_TYPE;
			parseResource(attributes);
		} else if (localName.startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
			state = state | IN_CUSTOM_OPTION;
			currentCustomOptionName = localName;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		// KEEP: && ((state & IN_LI) == IN_LI)

		if (localName.equals(ELEMENT_STATUS)) {
			state = state & ~IN_STATUS;
		} else if (localName.equals(ELEMENT_LI) && ((state & IN_LI_LI) == IN_LI_LI)) {
			state = state & ~IN_LI_LI;
		} else if (localName.equals(ELEMENT_LI) && ((state & IN_LI_LI) != IN_LI_LI)) {
			state = state & ~IN_LI;
			if (characters.length() == 0) {
				return;
			}
			if (state == (IN_STATUS)) {
				configuration.addItem(BugzillaAttribute.BUG_STATUS, characters.toString());
			} else if (state == (IN_STATUS_OPEN)) {
				configuration.addOpenStatusValue(characters.toString());
			} else if (state == (IN_STATUS_CLOSED)) {
				configuration.addClosedStatusValue(characters.toString());
			} else if (state == (IN_RESOLUTION)) {
				configuration.addItem(BugzillaAttribute.RESOLUTION, characters.toString());
			} else if (state == (IN_KEYWORD)) {
				configuration.addItem(BugzillaAttribute.KEYWORDS, characters.toString());
			} else if (state == (IN_PLATFORM)) {
				configuration.addItem(BugzillaAttribute.REP_PLATFORM, characters.toString());
			} else if (state == (IN_OP_SYS)) {
				configuration.addItem(BugzillaAttribute.OP_SYS, characters.toString());
			} else if (state == (IN_PRIORITY)) {
				configuration.addItem(BugzillaAttribute.PRIORITY, characters.toString());
			} else if (state == (IN_SEVERITY)) {
				configuration.addItem(BugzillaAttribute.BUG_SEVERITY, characters.toString());
			} else if (state == (IN_CUSTOM_OPTION)) {
				// Option for CutstomFields
				if (currentCustomOptionName != null) {
					if (characters.length() > 0) {
						List<String> customOptionList = customOption.get(currentCustomOptionName);
						if (customOptionList == null) {
							customOptionList = new ArrayList<String>();
							customOption.put(currentCustomOptionName, customOptionList);
						}
						customOptionList.add(characters.toString());
					}
				}
			}
		} else if (localName.equals(ELEMENT_PLATFORM)) {
			state = state & ~IN_PLATFORM;
		} else if (localName.equals(ELEMENT_OP_SYS)) {
			state = state & ~IN_OP_SYS;
		} else if (localName.equals(ELEMENT_PRIORITY)) {
			state = state & ~IN_PRIORITY;
		} else if (localName.equals(ELEMENT_SEVERITY)) {
			state = state & ~IN_SEVERITY;
		} else if (localName.equals(ELEMENT_PRODUCTS)) {
			state = state & ~IN_PRODUCTS;
		} else if (localName.equals(ELEMENT_OP_SYS)) {
			state = state & ~IN_OP_SYS;
		} else if (localName.equals(ELEMENT_NAME)) {
			state = state & ~IN_NAME;
			if (state == (IN_PRODUCTS | IN_LI)) {
				// PRODUCT NAME
				currentProduct = characters.toString();
				configuration.addProduct(currentProduct);
			} else if (state == (IN_VERSIONS | IN_LI | IN_VERSION)) {
				// VERSION NAME
				if (about != null && !versionNames.containsValue(about)) {
					if (characters.length() > 0) {
						versionNames.put(about, characters.toString());
					}
				}
			} else if (state == (IN_COMPONENTS | IN_LI | IN_COMPONENT)) {
				// COMPONENT NAME
				currentComponent = characters.toString();
				if (about != null && !componentNames.containsValue(about)) {
					if (currentComponent.length() > 0) {
						componentNames.put(about, currentComponent);
					}
				}
			} else if (state == (IN_TARGET_MILESTONES | IN_LI | IN_TARGET_MILESTONE)) {
				// MILESTONE NAME
				if (about != null && !milestoneNames.containsValue(about)) {
					if (characters.length() > 0) {
						milestoneNames.put(about, characters.toString());
					}
				}
			} else if (state == (IN_KEYWORDS | IN_LI | IN_KEYWORD)) {
				// KEYWORD NAME
				currentName = characters.toString();
			} else if (state == (IN_FIELDS | IN_LI | IN_FIELD) || state == (IN_FLAG_TYPES | IN_LI | IN_FLAG_TYPE)) {
				// FIELDS NAME
				currentName = characters.toString();
			}
		} else if (localName.equals(ELEMENT_COMPONENTS)) {
			state = state & ~IN_COMPONENTS;
		} else if (localName.equals(ELEMENT_COMPONENT)) {
			state = state & ~IN_COMPONENT;
		} else if (localName.equals(ELEMENT_VERSION)) {
			state = state & ~IN_VERSION;
		} else if (localName.equals(ELEMENT_VERSIONS)) {
			state = state & ~IN_VERSIONS;
		} else if (localName.equals(ELEMENT_INSTALL_VERSION)) {
			state = state & ~IN_INSTALL_VERSION;
			configuration.setInstallVersion(characters.toString());
		} else if (localName.equals(ELEMENT_DB_ENCODING)) {
			state = state & ~IN_DB_ENCODING;
			String charsetString = characters.toString().trim();
			if (charsetString.length() > 0) {
				try {
					Charset.forName(charsetString);
					configuration.setEncoding(charsetString);
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN,
							"Unrecognized encoding in configuration: " + charsetString)); //$NON-NLS-1$
				}
			}
		} else if (localName.equals(ELEMENT_TARGET_MILESTONE)) {
			state = state & ~IN_TARGET_MILESTONE;
		} else if (localName.equals(ELEMENT_TARGET_MILESTONES)) {
			state = state & ~IN_TARGET_MILESTONES;
		} else if (localName.equals(ELEMENT_STATUS_OPEN)) {
			state = state & ~IN_STATUS_OPEN;
		} else if (localName.equals(ELEMENT_STATUS_CLOSED)) {
			state = state & ~IN_STATUS_CLOSED;
		} else if (localName.equals(ELEMENT_RESOLUTION)) {
			state = state & ~IN_RESOLUTION;
		} else if (localName.equals(ELEMENT_KEYWORD)) {
			if (state == (IN_KEYWORDS | IN_LI | IN_KEYWORD)) {
				configuration.addItem(BugzillaAttribute.KEYWORDS, currentName);
			}
			state = state & ~IN_KEYWORD;
		} else if (localName.equals(ELEMENT_KEYWORDS)) {
			state = state & ~IN_KEYWORDS;
		} else if (localName.equals(ELEMENT_FIELDS)) {
			state = state & ~IN_FIELDS;
		} else if (localName.equals(ELEMENT_FIELD)) {
			if (currentName.startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
				BugzillaCustomField newField = new BugzillaCustomField(currentDescription, currentName, currentType,
						currentEnterBug);
				List<String> customOptionList = customOption.get(currentName);
				if (customOptionList != null && !customOptionList.isEmpty()) {
					newField.setOptions(customOptionList);
				}
				configuration.addCustomField(newField);
			}
			state = state & ~IN_FIELD;
		} else if (localName.equals(ELEMENT_DESCRIPTION)) {
			currentDescription = characters.toString();
		} else if (localName.equals(ELEMENT_TYPE)) {
			currentType = characters.toString();
		} else if (localName.equals(ELEMENT_ID)) {
			currentId = Integer.parseInt(characters.toString());
		} else if (localName.equals(ELEMENT_ENTER_BUG)) {
			currentEnterBug = characters.toString();
		} else if (localName.equals(ELEMENT_REQUESTABLE)) {
			currentRequestable = characters.toString();
		} else if (localName.equals(ELEMENT_SPECIFICALLY_REQUESTABLE)) {
			currentSpecifically_requestable = characters.toString();
		} else if (localName.equals(ELEMENT_MULTIPLICABLE)) {
			currentMultiplicable = characters.toString();
		} else if (localName.equals(ELEMENT_FLAG_TYPES)) {
			state = state & ~IN_FLAG_TYPES;
		} else if (localName.equals(ELEMENT_FLAG_TYPE)) {
			if (currentId != -1) {
				if (about != null && !flagIds.containsValue(currentId)) {
					flagIds.put(about, currentId);
				}
				BugzillaFlag newFlag = new BugzillaFlag(currentName, currentDescription, currentType,
						currentRequestable, currentSpecifically_requestable, currentMultiplicable, currentId);
				configuration.addFlag(newFlag);
			}
			state = state & ~IN_FLAG_TYPE;
		} else if (localName.startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
			state = state & ~IN_CUSTOM_OPTION;
			currentCustomOptionName = ""; //$NON-NLS-1$
		} else if (localName.equals(ELEMENT_ALLOWS_UNCONFIRMED)) {
			String value = characters.toString();
			Boolean boolValue = value.equals("0") ? false : true; //$NON-NLS-1$
			configuration.addUnconfirmedAllowed(currentProduct, boolValue);
		}
	}

	private void parseResource(Attributes attributes) {

		switch (state) {
		case IN_PRODUCTS | IN_LI | IN_COMPONENTS | IN_LI_LI:
			if (attributes != null) {
				String compURI = attributes.getValue(ATTRIBUTE_RESOURCE);
				if (compURI != null && currentProduct.length() > 0) {
					List<String> compURIs = components.get(currentProduct);
					if (compURIs == null) {
						compURIs = new ArrayList<String>();
						components.put(currentProduct, compURIs);
					}
					compURIs.add(compURI);
				}
			}
			break;
		case IN_PRODUCTS | IN_LI | IN_VERSIONS | IN_LI_LI:
			if (attributes != null) {
				String resourceURI = attributes.getValue(ATTRIBUTE_RESOURCE);
				if (resourceURI != null && currentProduct.length() > 0) {
					List<String> versionUris = versions.get(currentProduct);
					if (versionUris == null) {
						versionUris = new ArrayList<String>();
						versions.put(currentProduct, versionUris);
					}
					versionUris.add(resourceURI);
				}
			}
			break;
		case IN_PRODUCTS | IN_LI | IN_TARGET_MILESTONES | IN_LI_LI:
			if (attributes != null) {
				String resourceURI = attributes.getValue(ATTRIBUTE_RESOURCE);
				if (resourceURI != null) {
					List<String> milestoneUris = milestones.get(currentProduct);
					if (milestoneUris == null) {
						milestoneUris = new ArrayList<String>();
						milestones.put(currentProduct, milestoneUris);
					}
					milestoneUris.add(resourceURI);
				}
			}
			break;
		case IN_COMPONENTS | IN_LI | IN_COMPONENT | IN_FLAG_TYPES | IN_LI_LI:
			if (attributes != null) {
				String compURI = attributes.getValue(ATTRIBUTE_RESOURCE);
				if (compURI != null && currentComponent.length() > 0 && currentProduct.length() > 0
						&& compURI.length() > 0) {

					Map<String, List<String>> flagComponentList = flagsInComponent.get(currentProduct);
					if (flagComponentList == null) {
						flagComponentList = new HashMap<String, List<String>>();
						flagsInComponent.put(currentProduct, flagComponentList);
					}
					List<String> flagsForComponent = flagComponentList.get(currentComponent);
					if (flagsForComponent == null) {
						flagsForComponent = new ArrayList<String>();
						flagComponentList.put(currentComponent, flagsForComponent);
					}
					flagsForComponent.add(compURI.replace("flags.cgi?id=", "flag.cgi?id=")); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			break;
		case IN_COMPONENTS | IN_LI | IN_COMPONENT:
			if (attributes != null) {
				about = attributes.getValue(ATTRIBUTE_RDF_ABOUT);
				int idx = about.indexOf("&product="); //$NON-NLS-1$
				if (idx != -1) {
					currentProduct = about.substring(idx + 9);
					currentProduct = currentProduct.replaceAll("%20", " "); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			break;
		case IN_VERSIONS | IN_LI | IN_VERSION:
			if (attributes != null) {
				about = attributes.getValue(ATTRIBUTE_RDF_ABOUT);
			}
			break;

		case IN_TARGET_MILESTONES | IN_LI | IN_TARGET_MILESTONE:
			if (attributes != null) {
				about = attributes.getValue(ATTRIBUTE_RDF_ABOUT);
			}
			break;
		case IN_FIELDS | IN_LI | IN_FIELD:
			if (attributes != null) {
				about = attributes.getValue(ATTRIBUTE_RDF_ABOUT);
			}
			break;
		case IN_FLAG_TYPES | IN_LI | IN_FLAG_TYPE:
			if (attributes != null) {
				about = attributes.getValue(ATTRIBUTE_RDF_ABOUT);
			}
			break;

		}
	}

	@Override
	public void endDocument() throws SAXException {

		for (String product : components.keySet()) {
			List<String> componentURIs = components.get(product);
			for (String uri : componentURIs) {
				String realName = componentNames.get(uri);
				if (realName != null) {
					configuration.addItem2ProductConfiguration(BugzillaAttribute.COMPONENT, product, realName);
				}
			}
		}

		for (String product : versions.keySet()) {
			List<String> versionURIs = versions.get(product);
			for (String uri : versionURIs) {
				String realName = versionNames.get(uri);
				if (realName != null) {
					configuration.addItem2ProductConfiguration(BugzillaAttribute.VERSION, product, realName);
				}
			}

		}

		for (String product : milestones.keySet()) {
			List<String> milestoneURIs = milestones.get(product);
			for (String uri : milestoneURIs) {
				String realName = milestoneNames.get(uri);
				if (realName != null) {
					configuration.addItem2ProductConfiguration(BugzillaAttribute.TARGET_MILESTONE, product, realName);
				}
			}

		}

		for (String flagProduct : flagsInComponent.keySet()) {
			Map<String, List<String>> flagComponentUsage = flagsInComponent.get(flagProduct);
			for (String flagusageList : flagComponentUsage.keySet()) {
				List<String> flagList = flagComponentUsage.get(flagusageList);
				for (String flagAbout : flagList) {
					Integer flagId = flagIds.get(flagAbout);
					BugzillaFlag flag = configuration.getFlagWithId(flagId);
					flag.addUsed(flagProduct, flagusageList);
				}
			}
		}

		super.endDocument();
	}
}
