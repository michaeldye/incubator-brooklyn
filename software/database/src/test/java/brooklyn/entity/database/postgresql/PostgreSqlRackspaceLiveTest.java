/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package brooklyn.entity.database.postgresql;

import java.util.Arrays;

import org.testng.annotations.Test;

import brooklyn.entity.database.DatastoreMixins.DatastoreCommon;
import brooklyn.entity.database.VogellaExampleAccess;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.location.basic.PortRanges;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.location.jclouds.JcloudsLocation;
import brooklyn.util.net.Protocol;
import brooklyn.util.ssh.IptablesCommands;
import brooklyn.util.ssh.IptablesCommands.Chain;
import brooklyn.util.ssh.IptablesCommands.Policy;

import com.google.common.collect.ImmutableList;

/**
 * The PostgreSqlRackspaceLiveTest installs Postgresql on various operating systems like Ubuntu, CentOS, Red Hat etc. To
 * make sure that PostgreSql works like expected on these Operating Systems.
 */
public class PostgreSqlRackspaceLiveTest extends PostgreSqlIntegrationTest {
    @Test(groups = "Live")
    public void test_Debian_6() throws Exception {
        test("Debian 6");
    }

    @Test(groups = "Live")
    public void test_Ubuntu_10_0() throws Exception {
        test("Ubuntu 10.0");
    }

    @Test(groups = "Live")
    public void test_Ubuntu_11_0() throws Exception {
        test("Ubuntu 11.0");
    }

    @Test(groups = "Live")
    public void test_Ubuntu_12_0() throws Exception {
        test("Ubuntu 12.0");
    }

    @Test(groups = "Live")
    public void test_CentOS_6_0() throws Exception {
        test("CentOS 6.0");
    }

    @Test(groups = "Live")
    public void test_CentOS_5_6() throws Exception {
        test("CentOS 5.6");
    }

    @Test(groups = "Live")
    public void test_Fedora_17() throws Exception {
        test("Fedora 17");
    }

    @Test(groups = "Live")
    public void test_Red_Hat_Enterprise_Linux_6() throws Exception {
        test("Red Hat Enterprise Linux 6");
    }

    @Test(groups = "Live")
    public void test_localhost() throws Exception {
        super.test_localhost();
    }
    
    public void test(String osRegex) throws Exception {
        PostgreSqlNode psql = tapp.createAndManageChild(EntitySpec.create(PostgreSqlNode.class)
                .configure(DatastoreCommon.CREATION_SCRIPT_CONTENTS, CREATION_SCRIPT)
                .configure(PostgreSqlNode.POSTGRESQL_PORT, PortRanges.fromInteger(5432))
                .configure(PostgreSqlNode.SHARED_MEMORY, "32MB"));

        brooklynProperties.put("brooklyn.location.jclouds.rackspace-cloudservers-uk.imageNameRegex", osRegex);
        brooklynProperties.remove("brooklyn.location.jclouds.rackspace-cloudservers-uk.image-id");
        brooklynProperties.remove("brooklyn.location.jclouds.rackspace-cloudservers-uk.imageId");
        brooklynProperties.put("brooklyn.location.jclouds.rackspace-cloudservers-uk.inboundPorts", Arrays.asList(22, 5432));
        JcloudsLocation jcloudsLocation = (JcloudsLocation) managementContext.getLocationRegistry().resolve("jclouds:rackspace-cloudservers-uk");

        tapp.start(ImmutableList.of(jcloudsLocation));

        SshMachineLocation l = (SshMachineLocation) psql.getLocations().iterator().next();
        l.execCommands("add iptables rule", ImmutableList.of(IptablesCommands.insertIptablesRule(Chain.INPUT, Protocol.TCP, 5432, Policy.ACCEPT)));

        String url = psql.getAttribute(DatastoreCommon.DATASTORE_URL);
        new VogellaExampleAccess("org.postgresql.Driver", url).readModifyAndRevertDataBase();
    }
}
