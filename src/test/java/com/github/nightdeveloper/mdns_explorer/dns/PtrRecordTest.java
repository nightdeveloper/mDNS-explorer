/*
 * The MIT License
 *
 * Copyright (c) 2015-2018 Todd Kulesza <todd@dropline.net>
 *
 * This file is part of Hola.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.nightdeveloper.mdns_explorer.dns;

import org.junit.Test;

import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertTrue;

public class PtrRecordTest {
    @Test
    public void testParser() throws UnknownHostException {
        Record record = buildRecord();

        assertTrue("name = Zelda._http._tcp.local.: " + record.getName(), record.getName().equals("Zelda._http._tcp.local."));
        assertTrue("type = PTR", record instanceof PtrRecord);
        assertTrue("ttl = 2600", record.getTTL() == 2600);
        PtrRecord ptrRecord = (PtrRecord) record;
        assertTrue("user visible name = Zelda", ptrRecord.getUserVisibleName().equals("Zelda"));
    }

    @Test
    public void testParserWithEmptyName() throws UnknownHostException {
        Record record = buildRecordWithEmptyName();

        assertTrue("name = Zelda._http._tcp.local.: " + record.getName(), record.getName().equals("Zelda._http._tcp.local."));
        assertTrue("type = PTR", record instanceof PtrRecord);
        assertTrue("ttl = 2600", record.getTTL() == 2600);
        PtrRecord ptrRecord = (PtrRecord) record;
        assertTrue("user visible name = Untitled: " + ptrRecord.getUserVisibleName(), ptrRecord.getUserVisibleName().equals(PtrRecord.UNTITLED_NAME));
    }

    @Test
    public void testParserWithSingleName() throws UnknownHostException {
        Record record = buildRecordWithSingleName();

        assertTrue("name = Zelda._http._tcp.local.: " + record.getName(), record.getName().equals("Zelda._http._tcp.local."));
        assertTrue("type = PTR", record instanceof PtrRecord);
        assertTrue("ttl = 2600", record.getTTL() == 2600);
        PtrRecord ptrRecord = (PtrRecord) record;
        assertTrue("user visible name = Zelda", ptrRecord.getUserVisibleName().equals("Zelda"));
    }

    @Test
    public void testToStringForExceptions() {
        Record record = buildRecord();
        String string = record.toString();
        assertTrue("toString() is not null", string != null);
        assertTrue("toString() is not empty", string.length() > 0);
    }

    private Record buildRecord() {
        ByteBuffer buffer = buildBuffer();
        buffer.putShort((short) 2);
        buffer.putShort((short) 0xC000);
        buffer.limit(buffer.position());
        buffer.rewind();
        return Record.fromBuffer(buffer);
    }

    private Record buildRecordWithSingleName() {
        ByteBuffer buffer = buildBuffer();
        String name = "Zelda";
        buffer.putShort((short) (name.length() + 1));
        RecordTest.addNameToBuffer(name, buffer);
        buffer.limit(buffer.position());
        buffer.rewind();
        return Record.fromBuffer(buffer);
    }

    private Record buildRecordWithEmptyName() {
        ByteBuffer buffer = buildBuffer();
        buffer.putShort((short) 0);
        buffer.limit(buffer.position());
        buffer.rewind();
        return Record.fromBuffer(buffer);
    }

    private ByteBuffer buildBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(500);
        RecordTest.addNameToBuffer("Zelda._http._tcp.local.", buffer);
        buffer.putShort((short) Record.Type.PTR.asUnsignedShort());
        buffer.putShort((short) Record.Class.IN.asUnsignedShort());
        buffer.putInt(2600);
        return buffer;
    }
}
