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

import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.InteractionEvent;
import org.eclipse.mylar.core.model.internal.Taskscape;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class TaskscapeXmlWriter {	
	
	private DocumentBuilderFactory dbf = null;
	private Document doc = null;
	private Element root = null;
	private OutputStream outputStream = null;
	private Result result = null;
	
	public TaskscapeXmlWriter() {
		try {
			dbf = DocumentBuilderFactory.newInstance();
			doc = dbf.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			MylarPlugin.log(this.getClass().getName(), e);
		}
	}
	
	public void writeTaskscapeToStream(Taskscape t) throws IOException{
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

    /**
	 * Writes the provided XML document out to the specified output stream.
	 * 
	 * doc - the document to be written outputStream - the stream to which the
	 * document is to be written
	 */
    private void writeDOMtoStream(Document document) {
        // Prepare the DOM document for writing
        // DOMSource - Acts as a holder for a transformation Source tree in the 
        // form of a Document Object Model (DOM) tree
        Source source = new DOMSource(document);

        // StreamResult - Acts as an holder for a XML transformation result
        // Prepare the output stream
        result = new StreamResult(outputStream);
        
        // An instance of this class can be obtained with the 
        // TransformerFactory.newTransformer  method. This instance may 
        // then be used to process XML from a variety of sources and write 
        // the transformation output to a variety of sinks
        Transformer xformer = null;
        try {
            xformer = TransformerFactory.newInstance().newTransformer();
            //Transform the XML Source to a Result
            //
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
		// Encode special XML characters into the equivalent character references.
		// The first five are defined by default for all XML documents.
		// The next three (#xD, #xA, #x9) are encoded to avoid them
		// being converted to spaces on deserialization
		// (fixes bug 93720)
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
    		MylarPlugin.log(this.getClass().getName(), e);
    	}
    	
    }

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
}
