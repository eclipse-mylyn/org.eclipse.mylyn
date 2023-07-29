/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     IBM Corporation - initial API and implementation
 *     Manuel Doninger - fixes for bug 360365
 *******************************************************************************/
package org.eclipse.mylyn.commons.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.commons.core.ICommonsCoreConstants;
import org.eclipse.mylyn.internal.commons.core.Messages;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This class represents the default implementation of the <code>XMLMemento</code> interface.
 * <p>
 * This class is not intended to be extended by clients.
 * </p>
 * 
 * @see XmlMemento
 * @author Manuel Doninger
 * @since 3.7
 */
public final class XmlMemento {

	private final Document factory;

	private final Element element;

	private static final String TAG_ID = "XmlMemento.internal.id"; //$NON-NLS-1$

	/**
	 * Creates a <code>Document</code> from the <code>Reader</code> and returns a memento on the first
	 * <code>Element</code> for reading the document.
	 * <p>
	 * Same as calling createReadRoot(reader, null)
	 * </p>
	 * 
	 * @param reader
	 *            the <code>Reader</code> used to create the memento's document
	 * @return a memento on the first <code>Element</code> for reading the document
	 * @throws InvocationTargetException
	 *             if IO problems, invalid format, or no element.
	 */
	public static XmlMemento createReadRoot(Reader reader) throws InvocationTargetException {
		return createReadRoot(reader, null);
	}

	/**
	 * Creates a <code>Document</code> from the <code>Reader</code> and returns a memento on the first
	 * <code>Element</code> for reading the document.
	 * 
	 * @param reader
	 *            the <code>Reader</code> used to create the memento's document
	 * @param baseDir
	 *            the directory used to resolve relative file names in the XML document. This directory must exist and
	 *            include the trailing separator. The directory format, including the separators, must be valid for the
	 *            platform. Can be <code>null</code> if not needed.
	 * @return a memento on the first <code>Element</code> for reading the document
	 * @throws InvocationTargetException
	 *             if IO problems, invalid format, or no element.
	 */
	public static XmlMemento createReadRoot(Reader reader, String baseDir) throws InvocationTargetException {
		String errorMessage = null;
		Exception exception = null;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			InputSource source = new InputSource(reader);
			if (baseDir != null) {
				source.setSystemId(baseDir);
			}

			parser.setErrorHandler(new ErrorHandler() {
				/**
				 * @throws SAXException
				 */
				public void warning(SAXParseException exception) throws SAXException {
					// ignore
				}

				/**
				 * @throws SAXException
				 */
				public void error(SAXParseException exception) throws SAXException {
					// ignore
				}

				public void fatalError(SAXParseException exception) throws SAXException {
					throw exception;
				}
			});

			Document document = parser.parse(source);
			NodeList list = document.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if (node instanceof Element) {
					return new XmlMemento(document, (Element) node);
				}
			}
		} catch (ParserConfigurationException e) {
			exception = e;
			errorMessage = Messages.XMLMemento_parserConfigError;
		} catch (IOException e) {
			exception = e;
			errorMessage = Messages.XMLMemento_ioError;
		} catch (SAXException e) {
			exception = e;
			errorMessage = Messages.XMLMemento_formatError;
		}

		String problemText = null;
		if (exception != null) {
			problemText = exception.getMessage();
		}
		if (problemText == null || problemText.length() == 0) {
			problemText = errorMessage != null ? errorMessage : Messages.XMLMemento_noElement;
		}
		throw new InvocationTargetException(exception, errorMessage);
	}

	/**
	 * Returns a root memento for writing a document.
	 * 
	 * @param type
	 *            the element node type to create on the document
	 * @return the root memento for writing a document
	 * @throws DOMException
	 */
	public static XmlMemento createWriteRoot(String type) throws DOMException {
		Document document;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element element = document.createElement(type);
			document.appendChild(element);
			return new XmlMemento(document, element);
		} catch (ParserConfigurationException e) {
//            throw new Error(e);
			throw new Error(e.getMessage());
		}
	}

	/**
	 * Creates a memento for the specified document and element.
	 * <p>
	 * Clients should use <code>createReadRoot</code> and <code>createWriteRoot</code> to create the initial memento on
	 * a document.
	 * </p>
	 * 
	 * @param document
	 *            the document for the memento
	 * @param element
	 *            the element node for the memento
	 */
	public XmlMemento(Document document, Element element) {
		super();
		this.factory = document;
		this.element = element;
	}

	/**
	 * Creates a new child of this memento with the given type.
	 * <p>
	 * The <code>getChild</code> and <code>getChildren</code> methods are used to retrieve children of a given type.
	 * </p>
	 * 
	 * @param type
	 *            the type
	 * @return a new child memento
	 * @see #getChild
	 * @see #getChildren
	 * @throws DOMException
	 *             if the child cannot be created
	 */
	public XmlMemento createChild(String type) throws DOMException {
		Element child = factory.createElement(type);
		element.appendChild(child);
		return new XmlMemento(factory, child);
	}

	/**
	 * Creates a new child of this memento with the given type and id. The id is stored in the child memento (using a
	 * special reserved key, <code>TAG_ID</code>) and can be retrieved using <code>getId</code>.
	 * <p>
	 * The <code>getChild</code> and <code>getChildren</code> methods are used to retrieve children of a given type.
	 * </p>
	 * 
	 * @param type
	 *            the type
	 * @param id
	 *            the child id
	 * @return a new child memento with the given type and id
	 * @see #getID
	 * @throws DOMException
	 *             if the child cannot be created
	 */
	public XmlMemento createChild(String type, String id) throws DOMException {
		Element child = factory.createElement(type);
		child.setAttribute(TAG_ID, id == null ? "" : id); //$NON-NLS-1$
		element.appendChild(child);
		return new XmlMemento(factory, child);
	}

	/**
	 * Create a copy of the child node and append it to this node.
	 * 
	 * @param child
	 * @return An IMenento for the new child node.
	 * @throws DOMException
	 *             if the child cannot be created
	 */
	public XmlMemento copyChild(XmlMemento child) throws DOMException {
		Element childElement = child.element;
		Element newElement = (Element) factory.importNode(childElement, true);
		element.appendChild(newElement);
		return new XmlMemento(factory, newElement);
	}

	/* (non-Javadoc)
	 * Method declared in XMLMemento.
	 */
	public XmlMemento getChild(String type) {

		// Get the nodes.
		NodeList nodes = element.getChildNodes();
		int size = nodes.getLength();
		if (size == 0) {
			return null;
		}

		// Find the first node which is a child of this node.
		for (int nX = 0; nX < size; nX++) {
			Node node = nodes.item(nX);
			if (node instanceof Element) {
				Element element = (Element) node;
				if (element.getNodeName().equals(type)) {
					return new XmlMemento(factory, element);
				}
			}
		}

		// A child was not found.
		return null;
	}

	/* (non-Javadoc)
	 * Method declared in XMLMemento.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public XmlMemento[] getChildren(String type) {

		// Get the nodes.
		NodeList nodes = element.getChildNodes();
		int size = nodes.getLength();
		if (size == 0) {
			return new XmlMemento[0];
		}

		// Extract each node with given type.
		ArrayList list = new ArrayList(size);
		for (int nX = 0; nX < size; nX++) {
			Node node = nodes.item(nX);
			if (node instanceof Element) {
				Element element = (Element) node;
				if (element.getNodeName().equals(type)) {
					list.add(element);
				}
			}
		}

		// Create a memento for each node.
		size = list.size();
		XmlMemento[] results = new XmlMemento[size];
		for (int x = 0; x < size; x++) {
			results[x] = new XmlMemento(factory, (Element) list.get(x));
		}
		return results;
	}

	/* (non-Javadoc)
	 * Method declared in XMLMemento.
	 */
	public Float getFloat(String key) {
		Attr attr = element.getAttributeNode(key);
		if (attr == null) {
			return null;
		}
		String strValue = attr.getValue();
		try {
			return Float.valueOf(strValue);
		} catch (NumberFormatException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ICommonsCoreConstants.ID_PLUGIN,
					"Memento problem - Invalid float for key: " //$NON-NLS-1$
							+ key + " value: " + strValue, //$NON-NLS-1$
					e));
			return null;
		}
	}

	/**
	 * @since 3.4
	 */
	public String getType() {
		return element.getNodeName();
	}

	/* (non-Javadoc)
	 * Method declared in XMLMemento.
	 */
	public String getID() {
		return element.getAttribute(TAG_ID);
	}

	/* (non-Javadoc)
	 * Method declared in XMLMemento.
	 */
	public Integer getInteger(String key) {
		Attr attr = element.getAttributeNode(key);
		if (attr == null) {
			return null;
		}
		String strValue = attr.getValue();
		try {
			return Integer.valueOf(strValue);
		} catch (NumberFormatException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ICommonsCoreConstants.ID_PLUGIN,
					"Memento problem - invalid integer for key: " + key //$NON-NLS-1$
							+ " value: " + strValue, //$NON-NLS-1$
					e));
			return null;
		}
	}

	/* (non-Javadoc)
	 * Method declared in XMLMemento.
	 */
	public String getString(String key) {
		Attr attr = element.getAttributeNode(key);
		if (attr == null) {
			return null;
		}
		return attr.getValue();
	}

	/**
	 * @since 3.4
	 */
	public Boolean getBoolean(String key) {
		Attr attr = element.getAttributeNode(key);
		if (attr == null) {
			return null;
		}
		return Boolean.valueOf(attr.getValue());
	}

	/**
	 * Returns the data of the Text node of the memento. Each memento is allowed only one Text node.
	 * 
	 * @return the data of the Text node of the memento, or <code>null</code> if the memento has no Text node.
	 * @since 2.0
	 * @throws DOMException
	 *             if the text node is too big
	 */
	public String getTextData() throws DOMException {
		Text textNode = getTextNode();
		if (textNode != null) {
			return textNode.getData();
		}
		return null;
	}

	/**
	 * @since 3.4
	 */
	public String[] getAttributeKeys() {
		NamedNodeMap map = element.getAttributes();
		int size = map.getLength();
		String[] attributes = new String[size];
		for (int i = 0; i < size; i++) {
			Node node = map.item(i);
			attributes[i] = node.getNodeName();
		}
		return attributes;
	}

	/**
	 * Returns the Text node of the memento. Each memento is allowed only one Text node.
	 * 
	 * @return the Text node of the memento, or <code>null</code> if the memento has no Text node.
	 */
	private Text getTextNode() {
		// Get the nodes.
		NodeList nodes = element.getChildNodes();
		int size = nodes.getLength();
		if (size == 0) {
			return null;
		}
		for (int nX = 0; nX < size; nX++) {
			Node node = nodes.item(nX);
			if (node instanceof Text) {
				return (Text) node;
			}
		}
		// a Text node was not found
		return null;
	}

	/**
	 * Places the element's attributes into the document.
	 * 
	 * @param copyText
	 *            true if the first text node should be copied
	 * @throws DOMException
	 *             if the attributes or children cannot be copied to this node.
	 */
	private void putElement(Element element, boolean copyText) throws DOMException {
		NamedNodeMap nodeMap = element.getAttributes();
		int size = nodeMap.getLength();
		for (int i = 0; i < size; i++) {
			Attr attr = (Attr) nodeMap.item(i);
			putString(attr.getName(), attr.getValue());
		}

		NodeList nodes = element.getChildNodes();
		size = nodes.getLength();
		// Copy first text node (fixes bug 113659).
		// Note that text data will be added as the first child (see putTextData)
		boolean needToCopyText = copyText;
		for (int i = 0; i < size; i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				XmlMemento child = createChild(node.getNodeName());
				child.putElement((Element) node, true);
			} else if (node instanceof Text && needToCopyText) {
				putTextData(((Text) node).getData());
				needToCopyText = false;
			}
		}
	}

	/**
	 * Sets the value of the given key to the given floating point number.
	 * 
	 * @param key
	 *            the key
	 * @param f
	 *            the value
	 * @throws DOMException
	 *             if the attribute cannot be set
	 */
	public void putFloat(String key, float f) throws DOMException {
		element.setAttribute(key, String.valueOf(f));
	}

	/**
	 * Sets the value of the given key to the given integer.
	 * 
	 * @param key
	 *            the key
	 * @param n
	 *            the value
	 * @throws DOMException
	 *             if the attribute cannot be set
	 */
	public void putInteger(String key, int n) throws DOMException {
		element.setAttribute(key, String.valueOf(n));
	}

	/**
	 * Copy the attributes and children from <code>memento</code> to the receiver.
	 * 
	 * @param memento
	 *            the XMLMemento to be copied.
	 * @throws DOMException
	 *             if the attributes or children cannot be copied to this node.
	 */
	public void putMemento(XmlMemento memento) throws DOMException {
		// Do not copy the element's top level text node (this would overwrite the existing text).
		// Text nodes of children are copied.
		putElement(memento.element, false);
	}

	/**
	 * Sets the value of the given key to the given string.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @throws DOMException
	 *             if the attribute cannot be set
	 */
	public void putString(String key, String value) throws DOMException {
		if (value == null) {
			return;
		}
		element.setAttribute(key, value);
	}

	/**
	 * Sets the value of the given key to the given boolean value.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @since 3.4
	 * @throws DOMException
	 *             if the attribute cannot be set
	 */
	public void putBoolean(String key, boolean value) throws DOMException {
		element.setAttribute(key, value ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Sets the memento's Text node to contain the given data. Creates the Text node if none exists. If a Text node does
	 * exist, it's current contents are replaced. Each memento is allowed only one text node.
	 * 
	 * @param data
	 *            the data to be placed on the Text node
	 * @since 2.0
	 * @throws DOMException
	 *             if the text node cannot be created under this node.
	 */
	public void putTextData(String data) throws DOMException {
		Text textNode = getTextNode();
		if (textNode == null) {
			textNode = factory.createTextNode(data);
			// Always add the text node as the first child (fixes bug 93718) 
			element.insertBefore(textNode, element.getFirstChild());
		} else {
			textNode.setData(data);
		}
	}

	/**
	 * Saves this memento's document current values to the specified writer.
	 * 
	 * @param writer
	 *            the writer used to save the memento's document
	 * @throws IOException
	 *             if there is a problem serializing the document to the stream.
	 */
	public void save(Writer writer) throws IOException {
		DOMWriter out = new DOMWriter(writer);
		try {
			out.print(element);
		} finally {
			out.close();
		}
	}

	/**
	 * A simple XML writer. Using this instead of the javax.xml.transform classes allows compilation against JCL
	 * Foundation (bug 80053).
	 */
	private static final class DOMWriter extends PrintWriter {

		/* constants */
		private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; //$NON-NLS-1$

		/**
		 * Creates a new DOM writer on the given output writer.
		 * 
		 * @param output
		 *            the output writer
		 */
		public DOMWriter(Writer output) {
			super(output);
			println(XML_VERSION);
		}

		/**
		 * Prints the given element.
		 * 
		 * @param element
		 *            the element to print
		 */
		public void print(Element element) {
			// Ensure extra whitespace is not emitted next to a Text node,
			// as that will result in a situation where the restored text data is not the
			// same as the saved text data.
			boolean hasChildren = element.hasChildNodes();
			startTag(element, hasChildren);
			if (hasChildren) {
				boolean prevWasText = false;
				NodeList children = element.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node node = children.item(i);
					if (node instanceof Element) {
						if (!prevWasText) {
							println();
						}
						print((Element) children.item(i));
						prevWasText = false;
					} else if (node instanceof Text) {
						print(getEscaped(node.getNodeValue()));
						prevWasText = true;
					}
				}
				if (!prevWasText) {
					println();
				}
				endTag(element);
			}
		}

		private void startTag(Element element, boolean hasChildren) {
			StringBuffer sb = new StringBuffer();
			sb.append("<"); //$NON-NLS-1$
			sb.append(element.getTagName());
			NamedNodeMap attributes = element.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Attr attribute = (Attr) attributes.item(i);
				sb.append(" "); //$NON-NLS-1$
				sb.append(attribute.getName());
				sb.append("=\""); //$NON-NLS-1$
				sb.append(getEscaped(String.valueOf(attribute.getValue())));
				sb.append("\""); //$NON-NLS-1$
			}
			sb.append(hasChildren ? ">" : "/>"); //$NON-NLS-1$ //$NON-NLS-2$
			print(sb.toString());
		}

		private void endTag(Element element) {
			StringBuffer sb = new StringBuffer();
			sb.append("</"); //$NON-NLS-1$
			sb.append(element.getNodeName());
			sb.append(">"); //$NON-NLS-1$
			print(sb.toString());
		}

		private static void appendEscapedChar(StringBuffer buffer, char c) {
			String replacement = getReplacement(c);
			if (replacement != null) {
				buffer.append('&');
				buffer.append(replacement);
				buffer.append(';');
			} else if (c == 9 || c == 10 || c == 13 || c >= 32) {
				buffer.append(c);
			}
		}

		private static String getEscaped(String s) {
			StringBuffer result = new StringBuffer(s.length() + 10);
			for (int i = 0; i < s.length(); ++i) {
				appendEscapedChar(result, s.charAt(i));
			}
			return result.toString();
		}

		private static String getReplacement(char c) {
			// Encode special XML characters into the equivalent character references.
			// The first five are defined by default for all XML documents.
			// The next three (#xD, #xA, #x9) are encoded to avoid them
			// being converted to spaces on deserialization
			// (fixes bug 93720)
			switch (c) {
			case '<':
				return "lt"; //$NON-NLS-1$
			case '>':
				return "gt"; //$NON-NLS-1$
			case '"':
				return "quot"; //$NON-NLS-1$
			case '\'':
				return "apos"; //$NON-NLS-1$
			case '&':
				return "amp"; //$NON-NLS-1$
			case '\r':
				return "#x0D"; //$NON-NLS-1$
			case '\n':
				return "#x0A"; //$NON-NLS-1$
			case '\u0009':
				return "#x09"; //$NON-NLS-1$
			}
			return null;
		}
	}

}
