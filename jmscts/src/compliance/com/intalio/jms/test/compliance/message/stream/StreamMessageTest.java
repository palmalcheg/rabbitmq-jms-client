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
 * $Id: StreamMessageTest.java,v 1.4 2003/05/04 14:12:38 tanderson Exp $
 */
package org.exolab.jmscts.test.message.stream;

import java.util.Arrays;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageEOFException;
import javax.jms.MessageFormatException;
import javax.jms.StreamMessage;

import junit.framework.Test;

import org.exolab.jmscts.core.AbstractMessageTestCase;
import org.exolab.jmscts.core.ClassHelper;
import org.exolab.jmscts.core.JMSTestRunner;
import org.exolab.jmscts.core.MessagePopulator;
import org.exolab.jmscts.core.MessageTypes;
import org.exolab.jmscts.core.TestContext;
import org.exolab.jmscts.core.TestCreator;
import org.exolab.jmscts.test.message.util.Conversions;
import org.exolab.jmscts.test.message.util.MessageValues;


/**
 * This class tests the StreamMessage message type
 * <p>
 * It covers the following requirements:
 * <ul>
 *   <li>message.stream.conversion</li>
 *   <li>message.stream.null</li>
 *   <li>message.stream.read</li>
 *   <li>message.stream.method.writeBytes(1)</li>
 *   <li>message.stream.method.writeBytes(2)</li>
 *   <li>message.stream.method.writeObject</li>
 *   <li>message.stream.method.readBytes</li>
 * </ul>
 *
 * @author <a href="mailto:tma@netspace.net.au">Tim Anderson</a>
 * @version $Revision: 1.4 $
 * @see javax.jms.StreamMessage
 * @see AbstractMessageTestCase
 * @see org.exolab.jmscts.core.MessageTestRunner
 */
public class StreamMessageTest extends AbstractMessageTestCase 
    implements MessageValues {

    /**
     * Requirements covered by this test case
     */
    private static final String[][] REQUIREMENTS = {
        {"testReadFailure", "message.stream.read"},
        {"testConversion", "message.stream.conversion"},
        {"testStringConversion", "message.stream.conversion"},
        {"testInvalidNumericConversion", "message.stream.conversion"},
        {"testNull", "message.stream.null"},
        {"testWriteObject", "message.stream.method.writeObject"},
        {"testInvalidObject", "message.stream.method.writeObject"},
        {"testReadWriteBytes", "message.stream.method.writeBytes(1)",
         "message.stream.method.writeBytes(2)"},
        {"testReadBytesReset1", "message.stream.method.readBytes"},
        {"testReadBytesReset2", "message.stream.method.readBytes"},
        {"testReadBytesClearBody1", "message.stream.method.readBytes"},
        {"testReadBytesClearBody2", "message.stream.method.readBytes"},
        {"testWriteBytes", "message.stream.method.writeBytes(1)",
         "message.stream.method.writeBytes(2)"},
        {"testFullReadBytes", "message.stream.method.readBytes"},
        {"testIncrementalReadBytes", "message.stream.method.readBytes"},
        {"testPartialReadBytes", "message.stream.method.readBytes"}};

    /**
     * Values to test conversions against. String conversions are handled
     * separately, by the {@link #STRING_CONVERSION_VALUES} attribute
     */
     private static final Object[][] CONVERSION_VALUES = {
        BOOLEANS, BYTES, SHORTS, CHARS, INTS, LONGS, FLOATS, DOUBLES, 
        BYTE_ARRAYS};

    /**
     * Float values to test string conversions against. String conversions 
     * don't have to support NaN, or +-Infinity, hence the reason they are not 
     * included here
     */
    private static final Float[] FLOAT_CONVERSION_VALUES = {
        new Float(Float.MIN_VALUE), new Float(Float.MAX_VALUE)};

    /**
     * Double values to test string conversions against. String conversions 
     * don't have to support NaN, or +-Infinity, hence the reason they are not 
     * included here
     */
    private static final Double[] DOUBLE_CONVERSION_VALUES = {
        new Double(Double.MIN_VALUE), new Double(Double.MAX_VALUE)};

    /**
     * Values to test string conversions against
     */
    final Object[][] STRING_CONVERSION_VALUES = {
        BOOLEANS, BYTES, SHORTS, INTS, LONGS, FLOAT_CONVERSION_VALUES, 
        DOUBLE_CONVERSION_VALUES, STRINGS};

    /**
     * Create an instance of this class for a specific test case
     * 
     * @param name the name of test case
     */
    public StreamMessageTest(String name) {
        super(name, MessageTypes.STREAM, REQUIREMENTS);
    }

    /**
     * The main line used to execute this test
     */
    public static void main(String[] args) {
        JMSTestRunner test = new JMSTestRunner(suite(), args);
        junit.textui.TestRunner.run(test);
    }

    /**
     * Sets up the test suite
     *
     * @return an instance of this class that may be run by 
     * {@link JMSTestRunner}
     */
    public static Test suite() {
        return TestCreator.createMessageTest(StreamMessageTest.class);
    }

    /**
     * Get the message populator. This implementation always returns null
     *
     * @return null
     */
    public MessagePopulator getMessagePopulator() {
        return null;
    }

    /**
     * Test that if a read method throws MessageFormatException 
     * the current position of the read pointer is not 
     * incremented, and that a subsequent read is capable of recovering from 
     * the exception by re-reading the data as a different type.
     * This covers requirements:
     * <ul>
     *   <li>message.stream.read</li>
     * </ul>
     *
     * @throws Exception for any error
     */
    public void testReadFailure() throws Exception {
        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();

        for (int i = 0; i < ALL_VALUES.length; ++i) {
            for (int j = 0; j < ALL_VALUES[i].length; ++j) {
                Object value = ALL_VALUES[i][j];
                write(message, value);
                message.reset();
                Class type = value.getClass();
                Class[] invalid = Conversions.getInvalidConversions(type);
                for (int k = 0; k < invalid.length; ++k) {
                    try {
                        read(message, invalid[k]);
                        fail("Expected MessageFormatException to be thrown " +
                             "when reading type=" + 
                             ClassHelper.getPrimitiveName(invalid[k]) +
                             " for type=" + 
                             ClassHelper.getPrimitiveName(type));
                    } catch (MessageFormatException ignore) {
                    } catch (Exception exception) {
                        fail("Expected MessageFormatException to be thrown " +
                             "when reading type=" + 
                             ClassHelper.getPrimitiveName(invalid[k]) +
                             " for type=" + ClassHelper.getPrimitiveName(type)
                             + ", but got exception=" + 
                             exception.getClass().getName() + ", message=" +
                             exception.getMessage());
                    }
                }
                if (value instanceof byte[]) {
                    byte[] v = (byte[]) value;
                    byte[] buffer = new byte[v.length];
                    assertEquals(v.length, message.readBytes(buffer));
                    assertTrue(Arrays.equals(v, buffer));
                    assertEquals(-1, message.readBytes(buffer));
                } else {
                    Object result = read(message, type);
                    assertEquals(value, result);
                }
                message.clearBody();
            }
        }
    }

    /**
     * Test valid conversions for all types except String (this is handled by
     * {@link #testStringConversion}).
     * This covers requirements:
     * <ul>
     *   <li>message.stream.conversion</li>
     * </ul>
     *
     * @throws Exception for any error
     */
    public void testConversion() throws Exception {
        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();
        
        for (int i = 0; i < CONVERSION_VALUES.length; ++i) {
            for (int j = 0; j < CONVERSION_VALUES[i].length; ++j) {
                Object value = CONVERSION_VALUES[i][j];
                write(message, value);
                Class type = value.getClass();
                Class[] valid = Conversions.getValidConversions(type);
                for (int k = 0; k < valid.length; ++k) {
                    message.reset();
                    Object result = read(message, valid[k]);
                    Object converted = Conversions.convert(value, valid[k]);
                    if (converted instanceof byte[]) {
                        // byte arrays cannot be converted
                        if (!Arrays.equals((byte[]) result, 
                                           (byte[]) converted)) {
                            fail("Read byte array different to that written");
                        }
                    } else {
                        assertEquals(
                            "Conversion of type=" + 
                            ClassHelper.getPrimitiveName(type) + " to type=" +
                            ClassHelper.getPrimitiveName(valid[k]) +
                            " failed.", converted, result);
                    }
                }
                message.clearBody();
            }
        }
    }

    /**
     * Test valid string conversions. This covers requirements:
     * <ul>
     *   <li>message.stream.conversion</li>
     * </ul>
     *
     * @throws Exception for any error
     */
    public void testStringConversion() throws Exception {
        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();
        
        for (int i = 0; i < STRING_CONVERSION_VALUES.length; ++i) {
            for (int j = 0; j < STRING_CONVERSION_VALUES[i].length; ++j) {
                Object value = STRING_CONVERSION_VALUES[i][j];
                write(message, value.toString());
                Class type = value.getClass();
                message.reset();
                Object result = read(message, type);
                if (!value.equals(result)) {
                    fail("Conversion of type=String to type=" +
                         ClassHelper.getPrimitiveName(type) +
                         " failed. Expected value=" + value + 
                         ", but got value=" + result);
                }
                message.clearBody();
            }
        }
    }

    /**
     * Test invalid string to numeric conversions. This covers requirements:
     * <ul>
     *   <li>message.stream.conversion</li>
     * </ul>
     *
     * @throws Exception for any error
     */
    public void testInvalidNumericConversion() throws Exception {
        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();
        Class numerics[] = {Byte.class, Short.class, Integer.class, Long.class,
                            Float.class, Double.class};
        String[] invalidNos = {"a", "0x00", "NaN", "-Infinity", "+Infinity"};
        for (int i = 0; i < invalidNos.length; ++i) {
            String value = invalidNos[i];
            message.writeString(value);
            message.reset();
            for (int j = 0; j < numerics.length; ++j) {
                try {
                    read(message, numerics[j]);
                    fail("Expected NumberFormatException to be thrown when " +
                         "reading value=" + value + " as type=" +
                         ClassHelper.getPrimitiveName(numerics[j]));
                } catch (NumberFormatException ignore) {
                } catch (Exception exception) {
                    fail("Expected NumberFormatException to be thrown when " +
                         "reading value=" + value + " as type=" +
                         ClassHelper.getPrimitiveName(numerics[j]) + 
                         ", but got exception=" + 
                         exception.getClass().getName() + ", message=" +
                         exception.getMessage());
                }
            }
            String result = message.readString();
            assertEquals(value, result);
            message.clearBody();
        }
    }

    /**
     * Test null conversions.
     * <ul>
     *   <li>message.stream.null</li>
     * </ul>
     *
     * @throws Exception for any error
     */
    public void testNull() throws Exception {
        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();

        try {
            message.writeObject(null);
        } catch (Exception exception) {
            fail("Failed to write a null value to StreamMessage, exception=" +
                 exception.getClass().getName() + ", message=" + 
                 exception.getMessage());
        }
        message.reset();
        Object result = readNull(message, Boolean.class, null);
        assertEquals(Boolean.FALSE, result);
        message.reset();

        result = readNull(message, String.class, null);
        assertEquals(null, result);
        message.reset();

        byte[] tmp = new byte[0];
        assertEquals(-1, message.readBytes(tmp));
        message.reset();

        // no need to call reset() for the following - the message should
        // do it.
        readNull(message, Byte.class, NumberFormatException.class);
        readNull(message, Short.class, NumberFormatException.class);
        readNull(message, Character.class, NullPointerException.class);
        readNull(message, Integer.class, NumberFormatException.class);
        readNull(message, Long.class, NumberFormatException.class);
        readNull(message, Float.class, NullPointerException.class);
        readNull(message, Double.class, NullPointerException.class);
    }

    /**
     * Test that writeObject() can handle all supported types
     * This covers requirements:
     * <ul>
     *   <li>message.bytes.method.writeObject</li>
     * </ul>
     *
     * @throws Exception for any error
     */
    public void testWriteObject() throws Exception {
        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();

        for (int i = 0; i < ALL_VALUES.length; ++i) {
            for (int j = 0; j < ALL_VALUES[i].length; ++j) {
                message.writeObject(ALL_VALUES[i][j]);
            }
        }
        message.reset();
        for (int i = 0; i < ALL_VALUES.length; ++i) {
            for (int j = 0; j < ALL_VALUES[i].length; ++j) {
                Object value = message.readObject();
                if (ALL_VALUES[i][j] instanceof byte[]) {
                    byte[] source = (byte[]) ALL_VALUES[i][j];
                    byte[] result = (byte[]) value;
                    assertTrue(Arrays.equals(source, result));
                } else {
                    assertEquals(ALL_VALUES[i][j], value);
                }
            }
        }
    }

    /**
     * Test an invalid object being written using the writeObject() method
     * This covers requirements:
     * <ul>
     *   <li>message.stream.method.writeObject</li>
     * </ul>
     *
     * @throws Exception for any error
     */
    public void testInvalidObject() throws Exception {
        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();

        try {
            message.writeObject(new java.math.BigDecimal(0.0));
            fail("StreamMessage.writeObject() should only support " + 
                 "objectified primitives");
        } catch (MessageFormatException ignore) {
        }
    }

    /**
     * Test that readBytes returns that written. This covers requirements:
     * <ul>
     *   <li>message.stream.method.writeBytes(1)</li>
     *   <li>message.stream.method.writeBytes(2)</li>
     * </ul>
     */
    public void testReadWriteBytes() throws Exception {
        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();

        byte[] bytes = populateByteArray(256, 0);
        message.writeBytes(bytes);
        message.reset();
        byte[] buffer1 = new byte[bytes.length];
        message.readBytes(buffer1);
        if (!Arrays.equals(bytes, buffer1)) {
            fail("Read byte array differs to that written");
        }
        int count = message.readBytes(buffer1);
        if (count != -1) {
            fail("Expected readBytes to return count=-1 to indicate end of " +
                 "array field, but returned count=" + count);
        
        }

        message.clearBody();
        message.writeBytes(bytes, 1, bytes.length - 2);           
        message.reset();
        byte[] expected  = populateByteArray(bytes.length - 2, 1);
        byte[] buffer2 = new byte[expected.length];
        message.readBytes(buffer2);
        if (!Arrays.equals(expected, buffer2)) {
            fail("Read byte array differs to that written");
        }
        count = message.readBytes(buffer2);
        if (count != -1) {
            fail("Expected readBytes to return count=-1 to indicate end of " +
                 "array field, but returned count=" + count);
        
        }
    }

    /**
     * Test that invoking readBytes() followed by reset() followed by 
     * readBytes() returns the expected result, when the 
     * first readBytes() call has not completed reading the array field. This
     * verifies that reset() correctly resets the state of the message.
     * This covers requirements:
     * <ul>
     *   <li>message.stream.method.readBytes</li>
     * </ul>
     */
    public void testReadBytesReset1() throws Exception {
        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();

        byte[] bytes = populateByteArray(256, 0);
        message.writeBytes(bytes);
        message.reset();
        byte[] buffer = new byte[bytes.length];
        message.readBytes(buffer);
        if (!Arrays.equals(bytes, buffer)) {
            fail("Read byte array differs to that written");
        }

        message.reset();
        // NOTE: readBytes() had not completed reading the array field

        message.readBytes(buffer);
        if (!Arrays.equals(bytes, buffer)) {
            fail("Read byte array differs to that written");
        }
    }

    /**
     * Test that invoking readBytes() followed by reset() followed by 
     * readObject() returns the expected result, when the readBytes() call has
     * not completed reading the array field. This verifies that reset() 
     * correctly resets the state of the message. 
     * This covers requirements:
     * <ul>
     *   <li>message.stream.method.readBytes</li>
     * </ul>
     */
    public void testReadBytesReset2() throws Exception {
        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();

        byte[] bytes = populateByteArray(256, 0);
        message.writeBytes(bytes);
        message.reset();
        byte[] buffer = new byte[bytes.length];
        message.readBytes(buffer);
        if (!Arrays.equals(bytes, buffer)) {
            fail("Read byte array differs to that written");
        }

        message.reset();
        // NOTE: readBytes() had not completed reading the array field

        byte[] result = (byte[]) message.readObject();
        if (!Arrays.equals(bytes, buffer)) {
            fail("Read byte array differs to that written");
        }
    }

    /**
     * Test that invoking readBytes() followed by clearBody() followed by 
     * readBytes() returns the expected result, when the 
     * first readBytes() call has not completed reading the array field. This 
     * verifies that clearBody() correctly clears the state of the message. 
     * This covers requirements:
     * <ul>
     *   <li>message.stream.method.readBytes</li>
     * </ul>
     */
    public void testReadBytesClearBody1() throws Exception {
        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();

        byte[] bytes = populateByteArray(256, 0);
        message.writeBytes(bytes);
        message.reset();
        byte[] buffer = new byte[bytes.length];
        message.readBytes(buffer);
        if (!Arrays.equals(bytes, buffer)) {
            fail("Read byte array differs to that written");
        }

        message.clearBody(); 
        // NOTE: readBytes() had not completed reading the array field
        
        bytes = populateByteArray(256, 1);
        message.writeBytes(bytes);
        message.reset();
        message.readBytes(buffer);
        if (!Arrays.equals(bytes, buffer)) {
            fail("Read byte array differs to that written");
        }
    }

    /**
     * Test that invoking readBytes() followed by clearBody() followed by 
     * readObject() returns the expected result, when the readBytes() call has
     * not completed reading the array field. This verifies that clearBody() 
     * correctly clears the state of the message. 
     * This covers requirements:
     * <ul>
     *   <li>message.stream.method.readBytes</li>
     * </ul>
     */
    public void testReadBytesClearBody2() throws Exception {
        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();

        byte[] bytes = populateByteArray(256, 0);
        message.writeBytes(bytes);
        message.reset();
        byte[] buffer = new byte[bytes.length];
        message.readBytes(buffer);
        if (!Arrays.equals(bytes, buffer)) {
            fail("Read byte array differs to that written");
        }

        message.clearBody(); 
        // NOTE: readBytes() had not completed reading the array field
        
        bytes = populateByteArray(256, 1);
        message.writeBytes(bytes);
        message.reset();
        byte[] result = (byte[]) message.readObject();
        if (!Arrays.equals(bytes, result)) {
            fail("Read byte array differs to that written");
        }
    }

    /**
     * Test that invoking writeBytes does not modify the source array
     * <ul>
     *   <li>message.stream.method.writeBytes(1)</li>
     *   <li>message.stream.method.writeBytes(2)</li>
     * </ul>
     */
    public void testWriteBytes() throws Exception {
        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();

        byte[] bytes = populateByteArray(256, 0);
        byte[] copy = populateByteArray(bytes.length, 0);
        message.writeBytes(bytes);
        message.reset();
        byte[] buffer = new byte[bytes.length];
        message.readBytes(buffer);
        if (!Arrays.equals(bytes, copy)) {
            fail("writeBytes(byte[]) modified the source byte array");
        }

        message.clearBody();
        message.writeBytes(bytes, 0, bytes.length);
        message.reset();
        message.readBytes(buffer);
        if (!Arrays.equals(bytes, copy)) {
            fail("writeBytes(byte[], int, int) modified the source byte " +
                 "array");
        }
    }

    /**
     * Test that readBytes can read an entire byte array, and returns -1
     * on the subsequent call.
     * <ul>
     *   <li>message.stream.method.readBytes</li>
     * </ul>
     */
    public void testFullReadBytes() throws Exception {
        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();

        byte[] bytes = populateByteArray(256, 0);
        message.writeBytes(bytes);
        message.reset();

        byte[] buffer = new byte[bytes.length];
        int count = message.readBytes(buffer);
        if (count != buffer.length) {
            fail("Expected readBytes to return count=" + buffer.length + 
                 ", but returned count=" + count);
        }
        if (!Arrays.equals(bytes, buffer)) {
            fail("Read byte array differs to that written");
        }
        count = message.readBytes(buffer);
        if (count != -1) {
            fail("Expected readBytes to return count=-1 to indicate end of " +
                 "array field, but returned count=" + count);
        
        }
    }

    /**
     * Test that readBytes can read be invoked incrementally.
     * <ul>
     *   <li>message.stream.method.readBytes</li>
     * </ul>
     */
    public void testIncrementalReadBytes() throws Exception {
        final int chunkSize = 64;      
        final int remainder = (chunkSize / 2);
        final int size = 7 * chunkSize + remainder;
        // the size of the byte array. The result will be read back 
        // incrementally, with the last read only filling a portion of the 
        // buffer
        final int chunks = 8; // the number of chunks to read. The last chunk
                              // is only half full

        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();

        byte[] bytes = populateByteArray(size, 0);
        message.writeBytes(bytes);
        message.reset();

        byte[][] buffers = new byte[chunks][chunkSize];
        int total = 0;
        int count = 0;
        for (int i = 0; i < chunks; ++i) {
            count = message.readBytes(buffers[i]);
            total += count;
            int expected = (i < chunks - 1) ? chunkSize : remainder;
            if (count != expected) {
                fail("Expected readBytes to return count=" + expected +
                     ", but returned count=" + count);
            }
        }

        // concatenate the buffers together, and check that the read array
        // matches that written
        byte[] read = new byte[size];
        for (int i = 0; i < chunks; ++i) {
            int length = (i < chunks - 1) ? chunkSize : remainder;
            System.arraycopy(buffers[i], 0, read, i * chunkSize, length);
        }
        if (!Arrays.equals(bytes, read)) {
            fail("Byte array read differs to that written");
        }

        // verify that readBytes signals end of field
        count = message.readBytes(buffers[0]);
        if (count != -1) {
            fail("Expected readBytes to return count=-1 to indicate end of " +
                 "array field, but returned count=" + count);
        
        }
    }

    /**
     * Test that invoking any read method when a partial byte array has been
     * read throws MessageFormatException. This covers requirements
     * <ul>
     *   <li>message.stream.method.readBytes</li>
     * </ul>
     */
    public void testPartialReadBytes() throws Exception {
        TestContext context = getContext();
        StreamMessage message = (StreamMessage) context.getMessage();

        byte[] bytes = populateByteArray(256, 0);
        message.writeBytes(bytes);
        message.reset();
        byte[] buffer = new byte[bytes.length];
        // read the entire byte array, but don't invoke a second time to 
        // complete the read
        int size = message.readBytes(buffer);
        if (size != buffer.length) {
            fail("Expected readBytes() to return count=" + buffer.length +
                 ", but returned count=" + size);
        }
        if (!Arrays.equals(bytes, buffer)) {
            fail("Byte array read differs to that written");
        }
        
        Class[] types = {Boolean.class, Byte.class, Short.class, 
                         Character.class, Integer.class, Long.class, 
                         Float.class, Double.class, String.class, 
                         Object.class};
        for (int i = 0; i < types.length; ++i) {
            try {
                read(message, types[i]);
                fail("Expected MessageFormatException to be thrown when " +
                     "reading type=" + ClassHelper.getPrimitiveName(types[i]) +
                     " after an incomplete read of a byte array");
            } catch (MessageFormatException ignore) {
            } catch (Exception exception) {
                fail("Expected MessageFormatException to be thrown when " +
                     "reading type=" + ClassHelper.getPrimitiveName(types[i]) +
                     " after an incomplete read of a byte array, but got " +
                     "exception=" + exception.getClass().getName() + 
                     ", message=" + exception.getMessage());
            }
        }
        assertEquals(-1, message.readBytes(buffer));
    }

    private void write(StreamMessage message, Object value) throws Exception {
        if (value instanceof Boolean) {
            message.writeBoolean(((Boolean) value).booleanValue());
        } else if (value instanceof Byte) {
            message.writeByte(((Byte) value).byteValue());
        } else if (value instanceof Short) {
            message.writeShort(((Short) value).shortValue());
        } else if (value instanceof Character) {
            message.writeChar(((Character) value).charValue());
        } else if (value instanceof Integer) {
            message.writeInt(((Integer) value).intValue());
        } else if (value instanceof Long) {
            message.writeLong(((Long) value).longValue());
        } else if (value instanceof Float) {
            message.writeFloat(((Float) value).floatValue());
        } else if (value instanceof Double) {
            message.writeDouble(((Double) value).doubleValue());
        } else if (value instanceof String) {
            message.writeString((String) value);
        } else if (value instanceof byte[]) {
            message.writeBytes((byte[]) value);
        } else {
            // let the message deal with the exception
            message.writeObject(value);
        }
    }

    private Object read(StreamMessage message, Class type) throws Exception {
        Object result = null;
        if (type.equals(Boolean.class)) {
            result = new Boolean(message.readBoolean());
        } else if (type.equals(Byte.class)) {
            result = new Byte(message.readByte());
        } else if (type.equals(Short.class)) {
            result = new Short(message.readShort());
        } else if (type.equals(Character.class)) {
            result = new Character(message.readChar());
        } else if (type.equals(Integer.class)) {
            result = new Integer(message.readInt());
        } else if (type.equals(Long.class)) {
            result = new Long(message.readLong());
        } else if (type.equals(Float.class)) {
            result = new Float(message.readFloat());
        } else if (type.equals(Double.class)) {
            result = new Double(message.readDouble());
        } else if (type.equals(String.class)) {
            result = message.readString(); 
        } else if (type.equals(byte[].class)) {
            byte[] bytes = null;
            byte[] buffer = new byte[256];
            int length = 0;
            while ((length = message.readBytes(buffer)) != -1) {
                // not particularly efficient, but for our purposes it doesn't
                // matter
                int size = (bytes != null) ? bytes.length : 0;
                size += length;
                if (bytes == null) {
                    bytes = new byte[size];
                    System.arraycopy(buffer, 0, bytes, 0, size);
                } else {
                    byte[] tmp = new byte[size];
                    System.arraycopy(bytes, 0, tmp, 0, bytes.length);
                    System.arraycopy(buffer, 0, tmp, bytes.length, length);
                    System.arraycopy(tmp, 0, bytes, 0, size);
                    bytes = tmp;
                }
            }
            result = bytes;
        } else if (type.equals(Object.class)) {
            result = message.readObject();
        }
        return result;
    }

    private Object readNull(StreamMessage message, Class type, 
                            Class exceptionType) throws Exception {
        Object result = null;
        try {
            result = read(message, type);
            if (exceptionType != null) {
                fail("Expected exception, type=" + exceptionType.getName() + 
                     " to be thrown when reading null as type=" + 
                     ClassHelper.getPrimitiveName(type));
            }
        } catch (Exception exception) {
            if (exceptionType == null) {
                fail("Did not expect exception to be thrown when reading " +
                     "null as type=" + ClassHelper.getPrimitiveName(type) + 
                     " but got exception="  + exception.getClass().getName() + 
                     ", message=" + exception.getMessage());
            } else if (!exceptionType.isAssignableFrom(exception.getClass())) {
                fail("Expected exception, type=" + exceptionType.getName() + 
                     " to be thrown when reading null as type=" + 
                     ClassHelper.getPrimitiveName(type) + ", but got " +
                     "exception=" + exception.getClass().getName() + 
                     ", message=" + exception.getMessage());
            }
        }
        return result;
    }

    /**
     * Helper to return a byte array of the specified length, populated with
     * an incrementing sequence of values
     *
     * @param length the length of the array
     * @param start the number to start the sequence at
     * @return a new byte array
     */
    private byte[] populateByteArray(int length, int start) {
        byte[] result = new byte[length];
        byte j = (byte) start;
        for (int i = 0; i < length; ++i, ++j) {
            result[i] = j;
        }
        return result;
    }

} //-- StreamMessageTest
