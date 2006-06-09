/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.mylar.internal.core.MylarContextExternalizer;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.core.util.XmlStringConverter;
import org.eclipse.mylar.internal.monitor.HtmlStreamTokenizer.Token;
import org.eclipse.mylar.provisional.core.IInteractionEventListener;
import org.eclipse.mylar.provisional.core.InteractionEvent;
import org.eclipse.mylar.provisional.core.InteractionEvent.Kind;

/**
 * @author Mik Kersten
 * @author Ken Sueda (XML serialization)
 * 
 * TODO: use buffered output stream for better performance?
 */
public class InteractionEventLogger implements IInteractionEventListener {

	private File outputFile;

	private static FileOutputStream outputStream;

	private boolean started = false;

	private int eventAccumulartor = 0;

	private List<InteractionEvent> queue = new ArrayList<InteractionEvent>();
	
	private HandleObfuscator handleObfuscator = new HandleObfuscator();

	public InteractionEventLogger(File outputFile) {
		this.outputFile = outputFile;
	}

	public void interactionObserved(InteractionEvent event) {
		if (handleObfuscator.isObfuscationEnabled()) {
			String obfuscatedHandle = handleObfuscator.obfuscateHandle(event.getStructureKind(), event.getStructureHandle());
			event = new InteractionEvent(event.getKind(), event.getStructureKind(), obfuscatedHandle, event.getOriginId(), event.getNavigation(), event.getDelta(), event.getInterestContribution());
		}
		System.err.println("> " + event);
		try {
			if (started) {
				String xml = interactionEventToXml(event);
				outputStream.write(xml.getBytes());
			} else {
				queue.add(event);
			}
			eventAccumulartor++;
		} catch (NullPointerException e) {
			MylarStatusHandler.log(e, "could not log interaction event");
		} catch (Throwable t) {
			MylarStatusHandler.log(t, "could not log interaction event");
		}
	}

	public void startObserving() {
		synchronized(this) {
			if (started) {
				return;
			} else {
				started = true;
			}
		}
		try {
			if (!outputFile.exists())
				outputFile.createNewFile();
			outputStream = new FileOutputStream(outputFile, true);

			for (InteractionEvent queuedEvent : queue)
				interactionObserved(queuedEvent);
			queue.clear();
		} catch (FileNotFoundException e) {
			MylarStatusHandler.log(e, "could not resolve file");
		} catch (Throwable t) {
			MylarStatusHandler.log(t, "could not create new file");
		}
	}

	public void stopObserving() {
		try {
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}
			started = false;
			if (MylarMonitorPlugin.getDefault() != null)
				MylarMonitorPlugin.getDefault().incrementObservedEvents(eventAccumulartor);
			eventAccumulartor = 0;
		} catch (IOException e) {
			MylarStatusHandler.fail(e, "could not close interaction event stream", false);
		}
	}

	public File moveOutputFile(String newPath) {
		stopObserving();
		File newFile = new File(newPath);
		try {
			if (outputFile.exists() && !newFile.exists()) {
				outputFile.renameTo(newFile);
			} else if (!newFile.exists()) {
				newFile.createNewFile();
				outputFile.delete();
			} else {
				outputFile.delete();
			}
			this.outputFile = newFile;
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Could not set logger output file", true);
		}
		startObserving();
		return newFile;
	}

	/**
	 * @return true if successfully cleared
	 */
	public synchronized void clearInteractionHistory() throws IOException {
		stopObserving();
		outputStream = new FileOutputStream(outputFile, false);
		outputStream.flush();
		outputStream.close();
		outputFile.delete();
		outputFile.createNewFile();
		startObserving();
	}

	public File getOutputFile() {
		return outputFile;
	}

	public List<InteractionEvent> getHistoryFromFile(File file) {
		List<InteractionEvent> events = new ArrayList<InteractionEvent>();
		try {
			// The file may be a zip file...
			if (file.getName().endsWith(".zip")) {
				ZipFile zip = new ZipFile(file);
				if (zip.entries().hasMoreElements()) {
					ZipEntry entry = zip.entries().nextElement();
					getHistoryFromStream(zip.getInputStream(entry), events);
				}
			} else {
				InputStream reader = new FileInputStream(file);
				getHistoryFromStream(reader, events);
				reader.close();
			}

		} catch (Exception e) {
			MylarStatusHandler.log("could not read interaction history", this);
			e.printStackTrace();
		}
		return events;
	}

	/**
	 * @param events
	 * @param tag
	 * @param endl
	 * @param buf
	 */
	private void getHistoryFromStream(InputStream reader, List<InteractionEvent> events) throws IOException {
		String xml;
		int index;
		String buf = "";
		String tag = "</" + MylarContextExternalizer.ELMNT_INTERACTION_HISTORY_OLD + ">";
		String endl = "\r\n";
		byte[] buffer = new byte[1000];
		int bytesRead = 0;
		while ((bytesRead = reader.read(buffer)) != -1) {
			buf = buf + new String(buffer, 0, bytesRead);
			while ((index = buf.indexOf(tag)) != -1) {
				index += tag.length();
				xml = buf.substring(0, index);
				InteractionEvent event = readEvent(xml);
				if (event != null)
					events.add(event);

				if (index + endl.length() > buf.length()) {
					buf = "";
				} else {
					buf = buf.substring(index + endl.length(), buf.length());
				}
			}
			buffer = new byte[1000];
		}
	}

	private static final String OPEN = "<";

	private static final String CLOSE = ">";

	private static final String SLASH = "/";

	private static final String ENDL = "\n";

	private static final String TAB = "\t";

	public String interactionEventToXml(InteractionEvent e) {
		StringBuffer res = new StringBuffer();
		String tag = "interactionEvent";
		String f = "yyyy-MM-dd HH:mm:ss.S z";
		SimpleDateFormat format = new SimpleDateFormat(f, Locale.ENGLISH);
		res.append(OPEN + tag + CLOSE + ENDL);
		res.append(TAB + OPEN + "kind" + CLOSE + e.getKind().toString() + OPEN + SLASH + "kind" + CLOSE + ENDL);
		res.append(TAB + OPEN + "date" + CLOSE + format.format(e.getDate()) + OPEN + SLASH + "date" + CLOSE + ENDL);
		res.append(TAB + OPEN + "endDate" + CLOSE + format.format(e.getEndDate()) + OPEN + SLASH + "endDate" + CLOSE
				+ ENDL);
		res.append(TAB + OPEN + "originId" + CLOSE + XmlStringConverter.convertToXmlString(e.getOriginId()) + OPEN
				+ SLASH + "originId" + CLOSE + ENDL);
		res.append(TAB + OPEN + "structureKind" + CLOSE + XmlStringConverter.convertToXmlString(e.getStructureKind())
				+ OPEN + SLASH + "structureKind" + CLOSE + ENDL);
		res.append(TAB + OPEN + "structureHandle" + CLOSE
				+ XmlStringConverter.convertToXmlString(e.getStructureHandle()) + OPEN + SLASH + "structureHandle"
				+ CLOSE + ENDL);
		res.append(TAB + OPEN + "navigation" + CLOSE + XmlStringConverter.convertToXmlString(e.getNavigation()) + OPEN
				+ SLASH + "navigation" + CLOSE + ENDL);
		res.append(TAB + OPEN + "delta" + CLOSE + XmlStringConverter.convertToXmlString(e.getDelta()) + OPEN + SLASH
				+ "delta" + CLOSE + ENDL);
		res.append(TAB + OPEN + "interestContribution" + CLOSE + "" + e.getInterestContribution() + OPEN + SLASH
				+ "interestContribution" + CLOSE + ENDL);
		res.append(OPEN + SLASH + tag + CLOSE + ENDL);
		return res.toString();
	}

	public InteractionEvent readEvent(String xml) {
		Reader reader = new StringReader(xml);
		HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(reader, null);
		String kind = "";
		String startDate = "";
		String endDate = "";
		String originId = "";
		String structureKind = "";
		String structureHandle = "";
		String navigation = "";
		String delta = "";
		String interest = "";
		try {
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
				if (token.getValue().toString().equals("<kind>")) {
					token = tokenizer.nextToken();
					if (!token.getValue().toString().equals("</kind>")) {
						kind = token.getValue().toString().toLowerCase();
						token = tokenizer.nextToken();
					}
				} else if (token.getValue().toString().equals("<date>")) {
					token = tokenizer.nextToken();
					while (!token.getValue().toString().equals("</date>")) {
						startDate += token.getValue().toString() + " ";
						token = tokenizer.nextToken();
					}
					startDate.trim();
				} else if (token.getValue().toString().equals("<endDate>")) {
					token = tokenizer.nextToken();
					while (!token.getValue().toString().equals("</endDate>")) {
						endDate += token.getValue().toString() + " ";
						token = tokenizer.nextToken();
					}
					endDate.trim();
				} else if (token.getValue().toString().equals("<originId>")) {
					token = tokenizer.nextToken();
					originId = XmlStringConverter.convertXmlToString(token.getValue().toString());
					token = tokenizer.nextToken();
				} else if (token.getValue().toString().equals("<structureKind>")) {
					token = tokenizer.nextToken();
					while (!token.getValue().toString().equals("</structureKind>")) {
						if (structureKind.equals("")) {
							structureKind += token.getValue().toString();
						} else {
							structureKind += " " + token.getValue().toString();
						}
						token = tokenizer.nextToken();
					}
					structureKind = XmlStringConverter.convertXmlToString(structureKind);
				} else if (token.getValue().toString().equals("<structureHandle>")) {
					token = tokenizer.nextToken();
					while (!token.getValue().toString().equals("</structureHandle>")) {
						if (structureHandle.equals("")) {
							structureHandle += token.getValue().toString();
						} else {
							structureHandle += " " + token.getValue().toString();
						}
						token = tokenizer.nextToken();
					}
					structureHandle = XmlStringConverter.convertXmlToString(structureHandle);
				} else if (token.getValue().toString().equals("<navigation>")) {
					token = tokenizer.nextToken();
					while (!token.getValue().toString().equals("</navigation>")) {
						if (navigation.equals("")) {
							navigation += token.getValue().toString();
						} else {
							navigation += " " + token.getValue().toString();
						}
						token = tokenizer.nextToken();
					}
					navigation = XmlStringConverter.convertXmlToString(navigation);
					navigation.trim();
				} else if (token.getValue().toString().equals("<delta>")) {
					token = tokenizer.nextToken();
					while (!token.getValue().toString().equals("</delta>")) {
						if (delta.equals("")) {
							delta += token.getValue().toString();
						} else {
							delta += " " + token.getValue().toString();
						}
						token = tokenizer.nextToken();
					}
					delta = XmlStringConverter.convertXmlToString(delta);
					delta.trim();
				} else if (token.getValue().toString().equals("<interestContribution>")) {
					token = tokenizer.nextToken();
					interest = XmlStringConverter.convertXmlToString(token.getValue().toString());
					token = tokenizer.nextToken();
				}
			}
			String formatString = "yyyy-MM-dd HH:mm:ss.S z";
			SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);
			float interestFloatVal = 0;
			try {
				interestFloatVal = Float.parseFloat(interest);
			} catch (NumberFormatException nfe) {
				// ignore for empty interest values
			}
			InteractionEvent event = new InteractionEvent(Kind.fromString(kind), structureKind, structureHandle, originId,
					navigation, delta, interestFloatVal, format.parse(startDate), format.parse(endDate));
			return event;

		} catch (ParseException e) {
			System.err.println("readevent: " + xml);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("readevent: " + xml);
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("readevent: " + xml);
			e.printStackTrace();
		}

		return null;
	}
}
