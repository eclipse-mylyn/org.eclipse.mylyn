/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.net.UrlEscapers;

public class AutoLinkSpan extends SourceSpan {

	@SuppressWarnings("nls")
	private static final Set<String> SCHEMES = Set.of("coap", "doi", "javascript", "aaa", "aaas", "about", "acap",
			"cap", "cid", "crid", "data", "dav", "dict", "dns", "file", "ftp", "geo", "go", "gopher", "h323", "http",
			"https", "iax", "icap", "im", "imap", "info", "ipp", "iris", "iris.beep", "iris.xpc", "iris.xpcs",
			"iris.lwz", "ldap", "mailto", "mid", "msrp", "msrps", "mtqp", "mupdate", "news", "nfs", "ni", "nih", "nntp",
			"opaquelocktoken", "pop", "pres", "rtsp", "service", "session", "shttp", "sieve", "sip", "sips", "sms",
			"snmp", "soap.beep", "soap.beeps", "tag", "tel", "telnet", "tftp", "thismessage", "tn3270", "tip", "tv",
			"urn", "vemmi", "ws", "wss", "xcon", "xcon-userid", "xmlrpc.beep", "xmlrpc.beeps", "xmpp", "z39.50r",
			"z39.50s", "adiumxtra", "afp", "afs", "aim", "apt", "attachment", "aw", "beshare", "bitcoin", "bolo",
			"callto", "chrome", "chrome-extension", "com-eventbrite-attendee", "content", "cvs", "dlna-playsingle",
			"dlna-playcontainer", "dtn", "dvb", "ed2k", "facetime", "feed", "finger", "fish", "gg", "git",
			"gizmoproject", "gtalk", "hcp", "icon", "ipn", "irc", "irc6", "ircs", "itms", "jar", "jms", "keyparc",
			"lastfm", "ldaps", "magnet", "maps", "market,message", "mms", "ms-help", "msnim", "mumble", "mvn", "notes",
			"oid", "palm", "paparazzi", "platform", "proxy", "psyc", "query", "res", "resource", "rmi", "rsync", "rtmp",
			"secondlife", "sftp", "sgn", "skype", "smb", "soldat", "spotify", "ssh", "steam", "svn", "teamspeak",
			"things", "udp", "unreal", "ut2004", "ventrilo", "view-source", "webcal", "wtai", "wyciwyg", "xfire", "xri",
			"ymsgr");

	private static final String EMAIL_DOMAIN_PART = "[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?"; //$NON-NLS-1$

	private static final String EMAIL_REGEX = "[a-z0-9.!#$%&'*+/=?^_`{|}~-]+@" + EMAIL_DOMAIN_PART + "(?:\\." //$NON-NLS-1$ //$NON-NLS-2$
			+ EMAIL_DOMAIN_PART + ")*"; //$NON-NLS-1$

	private final Pattern linkPattern = createLinkPattern();

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		char c = cursor.getChar();
		if (c == '<') {
			Matcher matcher = cursor.matcher(linkPattern);
			if (matcher.matches()) {
				String href = matcher.group(1);
				String link = href;
				String email = matcher.group(2);
				if (email != null) {
					link = "mailto:" + email; //$NON-NLS-1$
				}
				int endOffset = cursor.getOffset(matcher.end(3));
				int linkLength = endOffset - cursor.getOffset();
				return Optional.of(new Link(cursor.getLineAtOffset(), cursor.getOffset(), linkLength, escapeUri(link),
						null, List.<Inline> of(new Characters(cursor.getLineAtOffset(), cursor.getOffset() + 1,
								linkLength - 2, href))));
			}
		}
		return Optional.empty();
	}

	private String escapeUri(String link) {
		return UrlEscapers.urlFragmentEscaper().escape(link).replace("%23", "#").replace("%25", "%"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	private Pattern createLinkPattern() {
		String regex = ""; //$NON-NLS-1$
		for (String scheme : SCHEMES) {
			if (regex.isEmpty()) {
				regex += "<((?:(?:"; //$NON-NLS-1$
			} else {
				regex += "|"; //$NON-NLS-1$
			}
			regex += scheme.replace(".", "\\."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		regex += "):[^\\s>]+)|(" + EMAIL_REGEX + "))(>).*"; //$NON-NLS-1$ //$NON-NLS-2$
		return Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	}
}
