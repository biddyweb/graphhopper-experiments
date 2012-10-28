/*
 *  Copyright 2012 Peter Karich
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.compare.neo4j;

import com.graphhopper.routing.util.CarStreetType;
import com.graphhopper.util.DistanceCalc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Peter Karich, info@jetsli.de
 */
public class Neo4JStorageTest {

    Neo4JStorage storage;

    @Before
    public void setup() throws Exception {
        storage = new Neo4JStorage();
        storage.createNew();
    }

    @After
    public void shutdown() throws Exception {
        storage.close();
    }

    @Test
    public void testAddNode() {
        storage.addNode(0, 13, 21);
        storage.addNode(1, 13.1f, 21);
        storage.addEdge(0, 1, CarStreetType.flagsDefault(true), new DistanceCalc());
        
//        List<DistEntry> list = storage.getOutgoing(0);
//        assertEquals(1, list.size());
//        assertEquals(11.11953, list.get(0).distance, 1e-5);
//        assertEquals(1, list.get(0).loc);
    }
}
