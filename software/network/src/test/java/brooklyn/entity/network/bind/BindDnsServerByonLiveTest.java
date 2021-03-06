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
package brooklyn.entity.network.bind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import brooklyn.entity.BrooklynAppLiveTestSupport;
import brooklyn.location.Location;
import brooklyn.util.text.Strings;

public class BindDnsServerByonLiveTest extends BrooklynAppLiveTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(BindDnsServerByonLiveTest.class);

    @Test(groups = "Live")
    @Parameters({"locationSpec"})
    public void testDns(String locationSpec) throws Exception {
        if (Strings.isBlank(locationSpec)) {
            LOG.info("{} got no spec, skipping test", this);
        } else {
            Location testLocation = mgmt.getLocationRegistry().resolve(locationSpec);
            BindDnsServerLiveTest.testBindStartsAndUpdates(app, testLocation);
        }
    }
}
