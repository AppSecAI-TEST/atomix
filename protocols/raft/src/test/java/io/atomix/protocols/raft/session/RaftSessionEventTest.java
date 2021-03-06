/*
 * Copyright 2017-present Open Networking Laboratory
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
package io.atomix.protocols.raft.session;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Raft session event test.
 */
public class RaftSessionEventTest {
  @Test
  public void testRaftSessionEvent() throws Exception {
    RaftSession session = mock(RaftSession.class);
    long timestamp = System.currentTimeMillis();
    RaftSessionEvent event = new RaftSessionEvent(RaftSessionEvent.Type.OPEN, session, timestamp);
    assertEquals(RaftSessionEvent.Type.OPEN, event.type());
    assertEquals(session, event.subject());
    assertEquals(timestamp, event.time());
  }
}
