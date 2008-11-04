/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * Populates a <link>ProductConfiguration</link> data structure.
 * 
 * @author Rob Elves
 */
public class SaxConfigurationContentHandler extends DefaultHandler {

	private static final String ELEMENT_RESOLUTION = "resolution";

	private static final String ELEMENT_STATUS_OPEN = "status_open";

	private static final String ELEMENT_TARGET_MILESTONE = "target_milestone";

	private static final String ELEMENT_TARGET_MILESTONES = "target_milestones";

	private static final String ELEMENT_INSTALL_VERSION = "install_version";

	private static final String ATTRIBUTE_RDF_ABOUT = "rdf:about";

	private static final String ATTRIBUTE_RESOURCE = "resource";

	private static final String ELEMENT_VERSION = "version";

	private static final String ELEMENT_VERSIONS = "versions";

	private static final String ELEMENT_COMPONENT = "component";

	private static final String ELEMENT_COMPONENTS = "components";

	private static final String ELEMENT_NAME = "name";

	private static final String ELEMENT_PRODUCTS = "products";

	private static final String ELEMENT_DESCRIPTION = "description";

	private static final String ELEMENT_FIELDS = "fields";

	private static final String ELEMENT_FIELD = "field";

	private static final String ELEMENT_FLAG_TYPES = "flag_types";

	private static final String ELEMENT_FLAG_TYPE = "flag_type";

	private static final String ELEMENT_SEVERITY = "severity";

	private static final String ELEMENT_PRIORITY = "priority";

	private static final String ELEMENT_KEYWORD = "keyword";

	private static final String ELEMENT_OP_SYS = "op_sys";

	private static final String ELEMENT_PLATFORM = "platform";

	private static final String ELEMENT_LI = "li";

	private static final String ELEMENT_STATUS = "status";

	private static final String ELEMENT_TYPE = "type";

	private static final String ELEMENT_TYPE_DESC = "type_desc";

	private static final String ELEMENT_ENTER_BUG = "enter_bug";

	private static final String ELEMENT_REQUESTABLE = "requestable";

	private static final String ELEMENT_SPECIFICALLY_REQUESTABLE = "specifically_requestable";

	private static final String ELEMENT_MULTIPLICABLE = "multiplicable";

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

	private int state = EXPECTING_ROOT;

	private String currentProduct = "";

	private String currentName = "";

	private String currentDescription = "";

	private String currentType;

	private String currentRequestable;

	private String currentSpecifically_requestable;

	private String currentMultiplicable;

	private String currentTypeDesc = "";

	private String currentEnterBug = "";

	private StringBuffer characters = new StringBuffer();

	private String about;

	private final RepositoryConfiguration configuration = new RepositoryConfiguration();

	private final Map<String, List<String>> components = new HashMap<String, List<String>>();

	private final Map<String, List<String>> versions = new HashMap<String, List<String>>();

	private final Map<String, List<String>> milestones = new HashMap<String, List<String>>();

	private final Map<String, String> componentNames = new HashMap<String, String>();

	private final Map<String, String> versionNames = new HashMap<String, String>();

	private final Map<String, String> milestoneNames = new HashMap<String, String>();

	private final Map<String, List<String>> customOption = new HashMap<String, List<String>>();

	private String currentCustomOptionName = "";

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
		} else if (localName.equals(ELEMENT_TARGET_MILESTONES)) {
			state = state | IN_TARGET_MILESTONES;
		} else if (localName.equals(ELEMENT_TARGET_MILESTONE)) {
			state = state | IN_TARGET_MILESTONE;
			parseResource(attributes);
		} else if (localName.equals(ELEMENT_STATUS_OPEN)) {
			state = state | IN_STATUS_OPEN;
		} else if (localName.equals(ELEMENT_RESOLUTION)) {
			state = state | IN_RESOLUTION;
		} else if (localName.equals(ELEMENT_KEYWORD)) {
			state = state | IN_KEYWORD;
		} else if (localName.equals(ELEMENT_FIELDS)) {
			state = state | IN_FIELDS;
		} else if (localName.equals(ELEMENT_FIELD)) {
			state = state | IN_FIELD;
			parseResource(attributes);
			currentName = "";
			currentDescription = "";
			currentType = "";
			currentTypeDesc = "";
			currentEnterBug = "";
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
				configuration.addStatus(characters.toString());
			} else if (state == (IN_STATUS_OPEN)) {
				configuration.addOpenStatusValue(characters.toString());
			} else if (state == (IN_STATUS_CLOSED)) {
				// TODO: Add closed status values to configuration
			} else if (state == (IN_RESOLUTION)) {
				configuration.addResolution(characters.toString());
			} else if (state == (IN_KEYWORD)) {
				configuration.addKeyword(characters.toString());
			} else if (state == (IN_PLATFORM)) {
				configuration.addPlatform(characters.toString());
			} else if (state == (IN_OP_SYS)) {
				configuration.addOS(characters.toString());
			} else if (state == (IN_PRIORITY)) {
				configuration.addPriority(characters.toString());
			} else if (state == (IN_SEVERITY)) {
				configuration.addSeverity(characters.toString());
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
				if (about != null && !componentNames.containsValue(about)) {
					if (characters.length() > 0) {
						componentNames.put(about, characters.toString());
					}
				}
			} else if (state == (IN_TARGET_MILESTONES | IN_LI | IN_TARGET_MILESTONE)) {
				// MILESTONE NAME
				if (about != null && !milestoneNames.containsValue(about)) {
					if (characters.length() > 0) {
						milestoneNames.put(about, characters.toString());
					}
				}
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
		} else if (localName.equals(ELEMENT_TARGET_MILESTONE)) {
			state = state & ~IN_TARGET_MILESTONE;
		} else if (localName.equals(ELEMENT_TARGET_MILESTONES)) {
			state = state & ~IN_TARGET_MILESTONES;
		} else if (localName.equals(ELEMENT_STATUS_OPEN)) {
			state = state & ~IN_STATUS_OPEN;
		} else if (localName.equals(ELEMENT_RESOLUTION)) {
			state = state & ~IN_RESOLUTION;
		} else if (localName.equals(ELEMENT_KEYWORD)) {
			state = state & ~IN_KEYWORD;
		} else if (localName.equals(ELEMENT_FIELDS)) {
			state = state & ~IN_FIELDS;
		} else if (localName.equals(ELEMENT_FIELD)) {
			if (currentName.startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
				BugzillaCustomField newField = new BugzillaCustomField(currentDescription, currentName, currentType,
						currentTypeDesc, currentEnterBug);
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
		} else if (localName.equals(ELEMENT_TYPE_DESC)) {
			currentTypeDesc = characters.toString();
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
			BugzillaFlag newFlag = new BugzillaFlag(currentName, currentDescription, currentType, currentRequestable,
					currentSpecifically_requestable, currentMultiplicable);
			configuration.addFlag(newFlag);
			state = state & ~IN_FLAG_TYPE;
		} else if (localName.startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
			state = state & ~IN_CUSTOM_OPTION;
			currentCustomOptionName = "";
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
		case IN_COMPONENTS | IN_LI | IN_COMPONENT:
			if (attributes != null) {
				about = attributes.getValue(ATTRIBUTE_RDF_ABOUT);
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
		case IN_FLAG_TYPE | IN_LI | IN_FLAG_TYPE:
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
					configuration.addComponent(product, realName);
				}
			}
		}

		for (String product : versions.keySet()) {
			List<String> versionURIs = versions.get(product);
			for (String uri : versionURIs) {
				String realName = versionNames.get(uri);
				if (realName != null) {
					configuration.addVersion(product, realName);
				}
			}

		}

		for (String product : milestones.keySet()) {
			List<String> milestoneURIs = milestones.get(product);
			for (String uri : milestoneURIs) {
				String realName = milestoneNames.get(uri);
				if (realName != null) {
					configuration.addTargetMilestone(product, realName);
				}
			}

		}
		super.endDocument();
	}
}
