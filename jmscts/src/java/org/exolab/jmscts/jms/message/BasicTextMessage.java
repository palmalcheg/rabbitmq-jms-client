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
 *    please contact tma@netspace.net.au.
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
 * Copyright 2003-2004 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: BasicTextMessage.java,v 1.2 2004/02/02 03:49:55 tanderson Exp $
 */

package org.exolab.jmscts.jms.message;

import javax.jms.JMSException;
import javax.jms.MessageNotWriteableException;
import javax.jms.TextMessage;


/**
 * This class provides a basic implementation of the javax.jms.ObjectMessage
 * interface.
 *
 * @version     $Revision: 1.2 $ $Date: 2004/02/02 03:49:55 $
 * @author      <a href="mailto:tma@netspace.net.au">Tim Anderson</a>
 * @see         javax.jms.TextMessage
 */
public class BasicTextMessage extends BasicMessage implements TextMessage {

    /**
     * The message body
     */
    private String _text;


    /**
     * Construct a new <code>BasicTextMessage</code>
     */
    public BasicTextMessage() {
    }

    /**
     * Set the text of this message
     *
     * @param text the text to set
     * @throws MessageNotWriteableException if message in read-only mode
     */
    @Override
    public void setText(String text) throws MessageNotWriteableException {
        checkWrite();
        _text = text;
    }

    /**
     * Get the string containing this message's data. The default value is
     * null.
     *
     * @return the String containing the message's data
     */
    @Override
    public String getText() {
        return _text;
    }

    /**
     * Clear out the message body. Clearing a message's body does not clear
     * its header values or property entries.
     * If this message body was read-only, calling this method leaves the
     * message body is in the same state as an empty body in a newly created
     * message
     *
     * @throws JMSException if the body can't be cleared
     */
    @Override
    public void clearBody() throws JMSException {
        super.clearBody();
        _text = null;
    }

}
