/*
 *  Copyright 2012 Peter Karich info@jetsli.de
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
package de.jetsli.compare.misc;

import de.jetsli.graph.storage.Graph;
import de.jetsli.graph.util.EdgeIterator;
import de.jetsli.graph.util.GraphUtility;
import org.junit.Test;
import static de.jetsli.graph.util.GraphUtility.*;
import static org.junit.Assert.*;

/**
 * just copied on 29.6.2012 from graphhopper core to avoid dependency or moving it to sources
 */
public abstract class AbstractGraphTester {

    protected abstract Graph createGraph(int size);

    @Test
    public void testSimpleGet() {
        Graph g = createGraph(10);
        g.setNode(0, 10, 20);
        assertEquals(0, GraphUtility.count(g.getEdges(0)));
        assertEquals(10, g.getLatitude(0), 1e-5);

        g.edge(0, 1, 10, true);
        assertEquals(1, GraphUtility.count(g.getEdges(0)));
        assertEquals(1, GraphUtility.count(g.getEdges(1)));
    }

    @Test public void testCreateLocation() {
        Graph graph = createGraph(4);
        graph.edge(3, 1, 50, true);
        graph.edge(1, 2, 100, true);

        assertEquals(2, count(graph.getOutgoing(1)));
    }

    @Test public void testEdges() {
        Graph graph = createGraph(5);

        graph.edge(2, 1, 12, true);
        assertEquals(1, count(graph.getOutgoing(2)));

        graph.edge(2, 3, 12, true);
        assertEquals(2, count(graph.getOutgoing(2)));

        graph.edge(2, 3, 12, true);
        assertEquals(2, count(graph.getOutgoing(2)));

        graph.edge(3, 2, 12, true);
        assertEquals(2, count(graph.getOutgoing(2)));

//        assertEquals(0, count(graph.getOutgoing(a1.id())));        
        assertEquals(1, count(graph.getOutgoing(1)));
        assertEquals(2, count(graph.getOutgoing(2)));
        assertEquals(1, count(graph.getOutgoing(3)));
    }

    // assume the following behaviour which allows the graph to stored bidirections more efficient
    @Test public void testOverwriteWillResultInSymetricUpdateOfEdgeWeight() {
        Graph g = createGraph(3);

        g.edge(1, 2, 12, true);
        EdgeIterator iter = g.getOutgoing(2);
        iter.next();
        assertEquals(12, iter.distance(), 1e-7);
        iter = g.getOutgoing(1);
        iter.next();
        assertEquals(12, iter.distance(), 1e-7);
        g.edge(1, 2, 11, false);
        iter = g.getOutgoing(2);
        iter.next();
        assertEquals(1, iter.node());
        assertEquals(11, iter.distance(), 1e-7);
        iter = g.getOutgoing(1);
        iter.next();
        assertEquals(2, iter.node());
        assertEquals(11, iter.distance(), 1e-7);
        g.edge(1, 2, 13, true);
        iter = g.getOutgoing(2);
        iter.next();
        assertEquals(1, iter.node());
        assertEquals(13, iter.distance(), 1e-7);
        iter = g.getOutgoing(1);
        iter.next();
        assertEquals(2, iter.node());
        assertEquals(13, iter.distance(), 1e-7);
    }

    @Test
    public void testGetLocations() {
        Graph g = createGraph(11);
        g.setNode(0, 12, 23);
        g.setNode(1, 22, 23);
        assertEquals(2, g.getNodes());

        g.edge(0, 1, 10, true);
        assertEquals(2, g.getNodes());

        g.edge(0, 2, 10, true);
        assertEquals(3, g.getNodes());

        g = createGraph(11);
        assertEquals(0, g.getNodes());
    }

    @Test
    public void testAddLocation() {
        Graph g = createGraph(11);
        g.setNode(0, 12, 23);
        g.setNode(1, 38.33f, 235.3f);
        g.setNode(2, 6, 2339);
        g.setNode(3, 78, 89);
        g.setNode(4, 2, 1);
        g.setNode(5, 7, 5);
        g.edge(0, 1, 12, true);
        g.edge(0, 2, 212, true);
        g.edge(0, 3, 212, true);
        g.edge(0, 4, 212, true);
        g.edge(0, 5, 212, true);

        assertEquals(12f, g.getLatitude(0), 1e-9);
        assertEquals(23f, g.getLongitude(0), 1e-9);

        assertEquals(38.33f, g.getLatitude(1), 1e-9);
        assertEquals(235.3f, g.getLongitude(1), 1e-9);

        assertEquals(6, g.getLatitude(2), 1e-9);
        assertEquals(2339, g.getLongitude(2), 1e-9);

        assertEquals(78, g.getLatitude(3), 1e-9);
        assertEquals(89, g.getLongitude(3), 1e-9);

        assertEquals(1, count(g.getOutgoing(1)));
        assertEquals(5, count(g.getOutgoing(0)));
        try {
            assertEquals(0, count(g.getOutgoing(6)));
            // for now return empty iterator
            // assertFalse(true);
        } catch (Exception ex) {
        }
    }
    // not implemented in neo4j etc graphs
//    @Test public void testUnidirectional() {
//        Graph g = createGraph(14);
//
//        g.edge(1, 2, 12, false);
//        g.edge(1, 11, 12, false);
//        g.edge(11, 1, 12, false);
//        g.edge(1, 12, 12, false);
//        g.edge(3, 2, 112, false);
//        Iterator<? extends DistEntry> i = g.getOutgoing(2).iterator();
//        assertFalse(i.hasNext());
//
//        i = g.getOutgoing(3).iterator();
//        assertEquals(2, i.next().node);
//        assertFalse(i.hasNext());
//
//        i = g.getOutgoing(1).iterator();
//        assertEquals(2, i.next().node);
//        assertEquals(11, i.next().node);
//        assertEquals(12, i.next().node);
//        assertFalse(i.hasNext());
//    }
//
//    @Test public void testUpdateUnidirectional() {
//        Graph g = createGraph(4);
//
//        g.edge(1, 2, 12, false);
//        g.edge(3, 2, 112, false);
//        Iterator<? extends DistEntry> i = g.getOutgoing(2).iterator();
//        assertFalse(i.hasNext());
//        i = g.getOutgoing(3).iterator();
//        assertEquals(2, i.next().node);
//        assertFalse(i.hasNext());
//
//        g.edge(2, 3, 112, false);
//        i = g.getOutgoing(2).iterator();
//        assertTrue(i.hasNext());
//        assertEquals(3, i.next().node);
//        i = g.getOutgoing(3).iterator();
//        assertEquals(2, i.next().node);
//        assertFalse(i.hasNext());
//    }
//
//    @Test
//    public void testClone() {
//        Graph g = createGraph(11);
//        g.edge(1, 2, 10, true);
//        g.addLocation(12, 23);
//        Graph clone = g.clone();
//        assertEquals(g.getLocations(), clone.getLocations());
//        assertEquals(count(g.getOutgoing(1)), count(clone.getOutgoing(1)));
//    }
//
//    @Test public void testDirectional() {
//        Graph g = createGraph(11);
//        g.edge(1, 2, 12, true);
//        g.edge(2, 3, 12, false);
//        g.edge(3, 4, 12, false);
//        g.edge(3, 5, 12, true);
//        g.edge(6, 3, 12, false);
//
//        assertEquals(1, count(g.getEdges(1)));
//        assertEquals(1, count(g.getIncoming(1)));
//        assertEquals(1, count(g.getOutgoing(1)));
//
//        assertEquals(2, count(g.getEdges(2)));
//        assertEquals(1, count(g.getIncoming(2)));
//        assertEquals(2, count(g.getOutgoing(2)));
//
//        assertEquals(4, count(g.getEdges(3)));
//        assertEquals(3, count(g.getIncoming(3)));
//        assertEquals(2, count(g.getOutgoing(3)));
//
//        assertEquals(1, count(g.getEdges(4)));
//        assertEquals(1, count(g.getIncoming(4)));
//        assertEquals(0, count(g.getOutgoing(4)));
//
//        assertEquals(1, count(g.getEdges(5)));
//        assertEquals(1, count(g.getIncoming(5)));
//        assertEquals(1, count(g.getOutgoing(5)));
//    }
//
//    @Test public void testDozendEdges() {
//        Graph g = createGraph(11);
//        g.edge(1, 2, 12, true);
//        assertEquals(1, count(g.getEdges(1)));
//
//        g.edge(1, 3, 13, false);
//        assertEquals(2, count(g.getEdges(1)));
//
//        g.edge(1, 4, 14, false);
//        assertEquals(3, count(g.getEdges(1)));
//
//        g.edge(1, 5, 15, false);
//        assertEquals(4, count(g.getEdges(1)));
//
//        g.edge(1, 6, 16, false);
//        assertEquals(5, count(g.getEdges(1)));
//
//        g.edge(1, 7, 16, false);
//        assertEquals(6, count(g.getEdges(1)));
//
//        g.edge(1, 8, 16, false);
//        assertEquals(7, count(g.getEdges(1)));
//
//        g.edge(1, 9, 16, false);
//        assertEquals(8, count(g.getEdges(1)));
//        assertEquals(8, count(g.getOutgoing(1)));
//        assertEquals(1, count(g.getIncoming(1)));
//        assertEquals(1, count(g.getIncoming(2)));
//    }
//
//    @Test
//    public void testCheckFirstNode() {
//        Graph g = createGraph(2);
//        assertEquals(0, count(g.getEdges(1)));
//        g.edge(0, 1, 12, true);
//        assertEquals(1, count(g.getEdges(1)));
//    }
//
//    @Test
//    public void testDeleteNode() {
//        Graph g = createGraph(11);
//        assertEquals(0, g.addLocation(12, 23));
//        assertEquals(1, g.addLocation(38.33f, 235.3f));
//        assertEquals(2, g.addLocation(3, 3));
//        assertEquals(3, g.addLocation(78, 89));
//        assertEquals(4, g.addLocation(2, 1));
//        assertEquals(5, g.addLocation(2.5f, 1));
//
//        g.edge(0, 1, 10, true);
//        g.edge(0, 3, 20, false);
//        g.edge(3, 5, 20, true);
//        g.edge(1, 5, 20, false);
//
//        g.markDeleted(0);
//        g.markDeleted(2);
//        // no deletion happend
//        assertEquals(6, g.getLocations());
//
//        // now actually perform deletion
//        g.optimize();
//        assertEquals(4, g.getLocations());
//        assertEquals(38.33f, g.getLatitude(0), 1e-4);
//        assertEquals(78, g.getLatitude(1), 1e-4);
//        assertTrue(MyIteratorable.contains(g.getEdges(0), 3));
//        assertFalse(MyIteratorable.contains(g.getEdges(0), 1));
//    }
}
