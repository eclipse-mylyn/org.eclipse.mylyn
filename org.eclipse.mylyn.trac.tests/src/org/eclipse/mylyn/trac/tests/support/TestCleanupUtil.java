package org.eclipse.mylyn.trac.tests.support;

import org.eclipse.mylyn.context.tests.support.MylarTestUtils.PrivilegeLevel;
import org.eclipse.mylyn.internal.trac.core.TracException;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracAttachment;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.trac.tests.AbstractTracClientTest;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.TestData;

/**
 * Utility that cleans up artifacts created by the Trac test suite. This class
 * should be run periodically to speed up execution of (attachment) tests.
 * 
 * @author Steffen Pingel
 */
public class TestCleanupUtil extends AbstractTracClientTest {

	private TestData data;

	public TestCleanupUtil() {
		super(Version.XML_RPC, PrivilegeLevel.ADMIN);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		data = TestFixture.init010();
	}

	public void testCleanup010() throws Exception {
		connect010();
		cleanup();
	}

	public void testCleanup011() throws Exception {
		connect011();
		cleanup();
	}

	private void cleanup() throws TracException {
		TracTicket ticket = repository.getTicket(data.attachmentTicketId);
		TracAttachment[] attachments = ticket.getAttachments();
		// skips the first attachment
		for (int i = 1; i < attachments.length; i++) {
			repository.deleteAttachment(data.attachmentTicketId, attachments[i].getFilename());
		}
	}

}
