/*
 * Copyright 2014 the original author or authors.
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
package net.kuujo.copycat.cluster.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import net.kuujo.copycat.cluster.Cluster;
import net.kuujo.copycat.cluster.ClusterConfig;
import net.kuujo.copycat.cluster.Member;
import net.kuujo.copycat.event.MembershipChangeEvent;
import net.kuujo.copycat.state.impl.RaftStateContext;

/**
 * Default cluster implementation.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class RaftCluster implements Cluster, Observer {
  private final RaftStateContext context;
  private final ClusterConfig userConfig;
  private final ClusterConfig internalConfig;
  private final String localMember;
  private final Map<String, Member> members = new HashMap<>();

  public RaftCluster(ClusterConfig userConfig, ClusterConfig internalConfig, RaftStateContext context) {
    this.context = context;
    this.userConfig = userConfig;
    internalConfig.setLocalMember(userConfig.getLocalMember());
    internalConfig.setRemoteMembers(userConfig.getRemoteMembers());
    this.internalConfig = internalConfig;
    this.internalConfig.addObserver(this);
    this.localMember = internalConfig.getLocalMember();
    clusterChanged(this.internalConfig);
  }

  @Override
  public void update(Observable o, Object arg) {
    clusterChanged((ClusterConfig) o);
  }

  /**
   * Called when the cluster configuration has changed.
   */
  private void clusterChanged(ClusterConfig config) {
    for (String member : config.getMembers()) {
      if (!members.containsKey(member)) {
        members.put(member, new RaftMember(member, context));
      }
    }
    Iterator<Map.Entry<String, Member>> iterator = members.entrySet().iterator();
    while (iterator.hasNext()) {
      if (!config.getMembers().contains(iterator.next().getKey())) {
        iterator.remove();
      }
    }

    context.events().membershipChange().run(new MembershipChangeEvent(new HashSet<>(config.getMembers())));
  }

  @Override
  public ClusterConfig config() {
    return userConfig;
  }

  @Override
  public Member localMember() {
    return members.get(localMember);
  }

  @Override
  public Set<Member> members() {
    return new HashSet<Member>(members.values());
  }

  @Override
  public Member member(String uri) {
    return members.get(uri);
  }

  @Override
  public int hashCode() {
    int hashCode = 23;
    hashCode = 37 * hashCode + members.hashCode();
    return hashCode;
  }

}