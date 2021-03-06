/*
 * Copyright 2015-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.storage.buffer;

import org.junit.AfterClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Mapped buffer test.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class UnsafeMappedBufferTest extends BufferTest {
  @AfterClass
  public static void afterTest() {
    FileTesting.cleanFiles();
  }
  
  @Override
  protected Buffer createBuffer(int capacity) {
    return UnsafeMappedBuffer.allocate(FileTesting.createFile(), capacity);
  }

  @Override
  protected Buffer createBuffer(int capacity, int maxCapacity) {
    return UnsafeMappedBuffer.allocate(FileTesting.createFile(), capacity, maxCapacity);
  }

  /**
   * Rests reopening a file that has been closed.
   */
  @Test
  public void testPersist() {
    File file = FileTesting.createFile();
    try (UnsafeMappedBuffer buffer = UnsafeMappedBuffer.allocate(file, 16)) {
      buffer.writeLong(10).writeLong(11).flip();
      assertEquals(buffer.readLong(), 10);
      assertEquals(buffer.readLong(), 11);
    }
    try (UnsafeMappedBuffer buffer = UnsafeMappedBuffer.allocate(file, 16)) {
      assertEquals(buffer.readLong(), 10);
      assertEquals(buffer.readLong(), 11);
    }
  }

  /**
   * Tests deleting a file.
   */
  @Test
  public void testDelete() {
    File file = FileTesting.createFile();
    UnsafeMappedBuffer buffer = UnsafeMappedBuffer.allocate(file, 16);
    buffer.writeLong(10).writeLong(11).flip();
    assertEquals(buffer.readLong(), 10);
    assertEquals(buffer.readLong(), 11);
    assertTrue(Files.exists(file.toPath()));
    buffer.delete();
    assertFalse(Files.exists(file.toPath()));
  }

}
