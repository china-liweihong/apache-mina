/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.mina.codec.delimited;

import java.nio.ByteBuffer;

import org.apache.mina.codec.ProtocolDecoder;
import org.apache.mina.codec.ProtocolDecoderException;
import org.apache.mina.codec.StatelessProtocolDecoder;
import org.apache.mina.codec.StatelessProtocolEncoder;
import org.apache.mina.codec.delimited.ints.RawInt32Transcoder;
import org.apache.mina.codec.delimited.ints.VarIntTranscoder;

/**
 * Abstract class providing both encoding and decoding methods between a given type and ByteBuffers.
 * 
 * <p>
 * Transcoder is stateless class providing encoding and decoding facilities.
 * Additionally this abstract requires two methods which allows to determine the size of a given message and 
 * to write it directly to a previously allocated ByteBuffer.
 * </p>
 *
 * @param <TYPE> the type of the messages which will be encoded in ByteBuffers and decoded from ByteBuffers.
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 * 
 */
public abstract class Transcoder<INPUT, OUTPUT> implements StatelessProtocolDecoder<ByteBuffer, INPUT>,
        StatelessProtocolEncoder<OUTPUT, ByteBuffer> {
    /**
     * Being stateless, this method is left empty
     * @see ProtocolDecoder#createDecoderState()
     */
    @Override
    final public Void createDecoderState() {
        // stateless !
        return null;
    }

    /**
     * Being stateless, this method is left empty
     * @see ProtocolDecoder#createDecoderState()
     */
    @Override
    final public Void createEncoderState() {
        // stateless !
        return null;
    }

    /**
     * Decodes a message from a {@link ByteBuffer}
     * 
     * <p>
     * When a truncated input is given to this method it <b>may</b> return null. Not all transcoder will be
     * able to detect this issue and report it that way. Thanks to prefixing of messages, transcoder will
     * only receive appropriately sized ByteBuffers.
     * </p>
     * 
     * <p>
     * n.b. The transcoders used for the prefixing (i.e. {@link RawInt32Transcoder} and {@link VarIntTranscoder}) <b>have</b> to detect truncated ByteBuffers. 
     * </p>
     * 
     * @param input data to be decoded as a TYPE message
     * @return the decoded message on success, null otherwise
     * 
     * @throws ProtocolDecoderException
     */
    abstract public INPUT decode(ByteBuffer input) throws ProtocolDecoderException;

    /**
     * Decodes a message from a {@link ByteBuffer}
     * <p>
     * The actual decoding needs to be implemented in the abstract method {@link Transcoder#decode(ByteBuffer)}
     * </p>
     */
    @Override
    final public INPUT decode(ByteBuffer input, Void context) throws ProtocolDecoderException {
        return decode(input);
    }

    /**
     * Encodes a message to a {@link ByteBuffer}
     * 
     * @param message a message to be encoded
     * @return the buffer containing {@link ByteBuffer} representation of the message
     */
    public ByteBuffer encode(OUTPUT message) {
        ByteBuffer buffer = ByteBuffer.allocate(getEncodedSize(message));
        writeTo(message, buffer);
        buffer.position(0);
        return buffer;
    }

    /**
     * Encodes a message to a {@link ByteBuffer}
     * <p>
     * The actual encoding needs to be implemented in the abstract method {@link Transcoder#encode(Object)}
     * </p>
     */

    @Override
    final public ByteBuffer encode(OUTPUT message, Void context) {
        return encode(message);
    }

    /**
     * Being stateless, this method is left empty
     * 
     * @see ProtocolDecoder#finishDecode(Object)
     */
    @Override
    final public void finishDecode(Void context) {
        // stateless !
    }

    /**
     * 
     * Computes the size of the serialized form of a message in bytes.
     * 
     * @param message a message to be encoded 
     * @return the size of the serialized form of the message
     */
    abstract public int getEncodedSize(OUTPUT message);

    /**
     * Writes a message on a {@link ByteBuffer}.
     * 
     * <p>
     * Nb. The buffer is expected to have at least a sufficient capacity to handle the serialized form 
     * of the message. 
     * </p>
     * 
     * @param message a message to be encoded
     * @param buffer a target {@link ByteBuffer}
     */
    abstract public void writeTo(OUTPUT message, ByteBuffer buffer);

}