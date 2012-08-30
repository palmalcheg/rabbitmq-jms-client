/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Exoffice Technologies.  For written permission,
 *    please contact jima@intalio.com.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Exoffice Technologies. Exolab is a registered
 *    trademark of Exoffice Technologies.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY EXOFFICE TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * EXOFFICE TECHNOLOGIES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2001, 2003 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: RollbackTest.java,v 1.7 2003/05/04 14:12:39 tanderson Exp $
 */
package org.exolab.jmscts.test.session.transacted;

import java.util.Iterator;
import java.util.List;

import javax.jms.IllegalStateException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.Category;

import junit.framework.Test;

import org.exolab.jmscts.core.AbstractSendReceiveTestCase;
import org.exolab.jmscts.core.AckTypes;
import org.exolab.jmscts.core.ConnectionHelper;
import org.exolab.jmscts.core.JMSTestRunner;
import org.exolab.jmscts.core.MessageReceiver;
import org.exolab.jmscts.core.MessageSender;
import org.exolab.jmscts.core.MessageTypes;
import org.exolab.jmscts.core.MessagingBehaviour;
import org.exolab.jmscts.core.ReceiptType;
import org.exolab.jmscts.core.TestContext;
import org.exolab.jmscts.core.TestCreator;
import org.exolab.jmscts.test.session.SessionSendReceiveTestCase;


/**
 * This class tests session rollback functionality.
 * It covers requirements:
 * <ul>
 *   <li>session.rollback</li>
 *   <li>session.close</li>
 * </ul>
 *
 * @author <a href="tma@netspace.net.au">Tim Anderson</a>
 * @version $Revision: 1.7 $
 * @see SessionSendReceiveTestCase
 */
public class RollbackTest extends SessionSendReceiveTestCase {

    /**
     * Requirements covered by this test case
     */
    private static final String[][] REQUIREMENTS = {
        {"testRollback", "session.rollback"},
        {"testClose", "session.close"}};

    /**
     * The destinations to create prior to running the test
     */
    private static final String[] DESTINATIONS = {"rollback1", "rollback2", 
                                                  "rollback3"};

    /**
     * The logger
     */
    private static final Category _log = 
        Category.getInstance(RollbackTest.class.getName());

    /**
     * Construct an instance of this class for a specific test case.
     * The test will be run against a transacted session type, all
     * message delivery types, and using MapMessage messages
     *
     * @param name the name of test case
     */
    public RollbackTest(String name) {
        super(name, MessageTypes.MAP, REQUIREMENTS);
    }

    /**
     * The main line used to execute this test
     */
    public static void main(String[] args) {
        JMSTestRunner test = new JMSTestRunner(suite(), args);
        junit.textui.TestRunner.run(test);
    }

    /**
     * Sets up the test suite.
     */
    public static Test suite() {
        AckTypes types = AckTypes.TRANSACTIONAL;
        return TestCreator.createSendReceiveTest(RollbackTest.class, types);
    }

    /**
     * Returns the list of destination names used by this test case. These
     * are used to pre-administer destinations prior to running the test case.
     *
     * @return the list of destinations used by this test case
     */
    public String[] getDestinations() {
        return DESTINATIONS;
    }

    /**
     * Tests session rollback. This covers requirements:
     * <ul>
     *   <li>session.rollback</li>
     * </ul>
     *
     * @throws Exception for any error
     */
    public void testRollback() throws Exception {
        final int count = 10;  // send count messages to each destination

        TestContext context = getContext();
        Session session = context.getSession();
        Message message = context.getMessage();
        MessagingBehaviour behaviour = context.getMessagingBehaviour();
        boolean isBrowser = false;
        if (behaviour.getReceiptType() == ReceiptType.BROWSER) {
            isBrowser = true;
        }

        // create the senders and receivers
        MessageSender[] senders = createSenders();
        MessageReceiver[] receivers = createReceivers();


        try {
            // send the messages
            send(senders, message, count);

            // roll back the session. All sent messages should be discarded
            session.rollback();

            // verify that the receivers cannot receive any messages
            receive(receivers, 0);

            // send the messages and commit the session. The receivers should 
            // be able to receive all messages
            send(senders, message, count);
            session.commit();

            for (int i = 0; i < receivers.length; ++i) {
                List messages = receive(receivers[i], count);
                if (!isBrowser) {
                    // verify that the redelivered flag isn't set.
                    // The state of the flag is not defined for QueueBrowsers
                    checkRedelivered(messages, false);
                }
            }

            // rollback the session, and verify that the receivers can 
            // re-receive all messages
            session.rollback();

            for (int i = 0; i < receivers.length; ++i) {
                List messages = receive(receivers[i], count);
                if (!isBrowser) {
                    // verify that the redelivered flag is set
                    // The state of the flag is not defined for QueueBrowsers
                    checkRedelivered(messages, true);
                }
            }
        } finally {
            closeSenders(senders);
            closeReceivers(receivers); 
        }
    }

    /**
     * Tests that calling {@link Session#rollback} for a closed session throws 
     * IllegalStateException. This covers requirements:
     * <ul>
     *   <li>session.close</li>
     * </ul>
     *
     * @throws Exception for any error
     */
    public void testClose() throws Exception {
        TestContext context = getContext();
        Session session = ConnectionHelper.createSession(
            context, AckTypes.TRANSACTED);
        session.close();
        try {
            session.rollback();
            String msg = "Session.rollback() for a closed session should " +
                " throw " + IllegalStateException.class.getName() + 
                ". Test context=" + context;
            _log.debug(msg);
            fail(msg);
        } catch (IllegalStateException ignore) {
        } catch (Exception exception) {
            String msg = "Session.rollback() for a closed session should " +
                " throw " + IllegalStateException.class.getName() + 
                ". Thrown exception=" + exception.getClass().getName() + 
                ". Test context=" + context;
            _log.debug(msg, exception);
            fail(msg);
        }
    }

    /**
     * Helper to compare the JMSRedelivered property of a list of messages
     * against that expected
     *
     * @param messages the list of messages to check
     * @param redelivered if true, expect JMSRedelivered to be true
     * @throws Exception if JMSRedivered is not equal to redelivered
     */
    private void checkRedelivered(List messages, boolean redelivered) 
        throws Exception {

        TestContext context = getContext();
        Iterator iter = messages.iterator();
        while (iter.hasNext()) {
            Message received = (Message) iter.next();
            if (received.getJMSRedelivered() && !redelivered) {
                String msg = "The JMSRedelivered property has been set for " +
                    "a message that has not been re-delivered. " +
                    "Test context=" + context;
                _log.debug(msg);
                fail(msg);
            } else if (!received.getJMSRedelivered() && redelivered) {
                String msg = "The JMSRedelivered property must be set for a " +
                    "message that has been re-delivered. Test context=" + 
                    context;
                _log.debug(msg);
                fail(msg);
            }
        }
    }
            
} //-- RollbackTest
