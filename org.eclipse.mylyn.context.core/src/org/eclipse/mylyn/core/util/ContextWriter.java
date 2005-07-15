/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/


package org.eclipse.mylar.core.util;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * TODO: remove the explicit string references
 * 
 * @author Mik Kersten
 */
public class ContextWriter {	
	
	private DocumentBuilderFactory dbf = null;
	private Document doc = null;
	private Element root = null;
	private OutputStream outputStream = null;
	private Result result = null;
	
	public ContextWriter() {
		try {
			dbf = DocumentBuilderFactory.newInstance();
			doc = dbf.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			MylarPlugin.log(e, "could not create xml writer");
		}
	}
	
	public void writeContextToStream(MylarContext t) throws IOException{
		if (outputStream == null) {
			IOException ioe = new IOException("OutputStream not set");
			throw ioe;
		}
		
		clearDocument();
		root = doc.createElement("InteractionHistory");
		root.setAttribute("Version", "1");
		root.setAttribute("Id", t.getId());

		for (InteractionEvent ie : t.getInteractionHistory()) {
			writeInteractionEvent(ie);
		}
		doc.appendChild(root);
		writeDOMtoStream(doc);
		return;
	}

    private void writeDOMtoStream(Document document) {
        Source source = new DOMSource(document);
        result = new StreamResult(outputStream);
        Transformer xformer = null;
        try {
            xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (TransformerException e1) {
            e1.printStackTrace();
        }
    }
    
    private void writeInteractionEvent(InteractionEvent e) {
    	Element node = doc.createElement("InteractionEvent");
    	String f = "yyyy-MM-dd HH:mm:ss.S z";
    	SimpleDateFormat format = new SimpleDateFormat(f, Locale.ENGLISH);
    	node.setAttribute("Kind", e.getKind().toString());
    	node.setAttribute("StartDate", format.format(e.getDate()));
    	node.setAttribute("EndDate", format.format(e.getEndDate()));
    	node.setAttribute("OriginId", checkStringFormat(e.getOriginId()));
    	node.setAttribute("StructureKind", checkStringFormat(e.getStructureKind()));
		node.setAttribute("StructureHandle", checkStringFormat(e.getStructureHandle()));
		node.setAttribute("Navigation", checkStringFormat(e.getNavigation()));
		node.setAttribute("Delta", checkStringFormat(e.getDelta()));
		node.setAttribute("Interest", "" + e.getInterestContribution());
		root.appendChild(node);
    }
    
    public void writeEventToStream(InteractionEvent e) throws IOException{
    	if (outputStream == null) {
			IOException ioe = new IOException("OutputStream not set");
			throw ioe;
		}
    	
    	clearDocument();
    	root = doc.createElement("InteractionEvent");
    	String f = "yyyy-MM-dd HH:mm:ss.S z";
    	SimpleDateFormat format = new SimpleDateFormat(f, Locale.ENGLISH);
    	root.setAttribute("Kind", e.getKind().toString());
    	root.setAttribute("StartDate", format.format(e.getDate()));
    	root.setAttribute("EndDate", format.format(e.getEndDate()));
    	root.setAttribute("OriginId", checkStringFormat(e.getOriginId()));
    	root.setAttribute("StructureKind", checkStringFormat(e.getStructureKind()));
    	root.setAttribute("StructureHandle", checkStringFormat(e.getStructureHandle()));
    	root.setAttribute("Navigation", checkStringFormat(e.getNavigation()));
    	root.setAttribute("Delta", checkStringFormat(e.getDelta()));
    	root.setAttribute("Interest", "" + e.getInterestContribution());
    	writeDOMtoStream(doc);
    }
    
    private String checkStringFormat(String s) {
    	if (s == null) return "";
    	StringBuffer res = new StringBuffer(s.length() + 20);
		for (int i = 0; i < s.length(); ++i)
			appendEscapedChar(res, s.charAt(i));
		return res.toString();
    }
    
    private void appendEscapedChar(StringBuffer buffer, char c) {
		String replacement = getReplacement(c);
		if (replacement != null) {
			buffer.append('&');
			buffer.append(replacement);
			buffer.append(';');
		} else {
			buffer.append(c);
		}
	}	
    
    private  String getReplacement(char c) {
		switch (c) {
			case '<' :
				return "lt"; //$NON-NLS-1$
			case '>' :
				return "gt"; //$NON-NLS-1$
			case '"' :
				return "quot"; //$NON-NLS-1$
			case '\'' :
				return "apos"; //$NON-NLS-1$
			case '&' :
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
    
    private void clearDocument() {
    	try {
    		this.doc = dbf.newDocumentBuilder().newDocument();
    	} catch(ParserConfigurationException e) {
    		MylarPlugin.log(e, "could not clear document");
    	}
    	
    }

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
}
