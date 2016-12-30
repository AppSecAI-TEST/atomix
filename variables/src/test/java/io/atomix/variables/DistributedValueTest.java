/*
 * Copyright 2015 the original author or authors.
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
package io.atomix.variables;

import io.atomix.testing.AbstractCopycatTest;
import org.testng.annotations.Test;

/**
 * Distributed atomic value test.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@Test
@SuppressWarnings("unchecked")
public class DistributedValueTest extends AbstractCopycatTest<DistributedValue> {

  @Override
  protected Class<? super DistributedValue> type() {
    return DistributedValue.class;
  }

  /**
   * Tests a set of atomic operations.
   */
  public void testAtomicSetGet() throws Throwable {
    createServers(3);
    DistributedValue<String> atomic = createResource();
    atomic.set("Hello world!").thenRun(() -> {
      atomic.get().thenAccept(value -> {
        threadAssertEquals(value, "Hello world!");
        resume();
      });
    });
    await(10000);
  }

}
