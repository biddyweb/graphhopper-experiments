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

import de.jetsli.graph.storage.DefaultStorage;
import de.jetsli.graph.util.Helper;
import java.io.File;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Peter Karich, info@jetsli.de
 */
public class LuceneStorage extends DefaultStorage {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private IndexWriter writer;

    public LuceneStorage(int expectedNodes) {
        super(expectedNodes);
    }

    public boolean init(boolean forceCreate) {
        try {
            File file = new File("osm.lucene.test");
            if (forceCreate)
                Helper.deleteDir(file);

            // germany.osm => 3.6 GB on disc for nodes only, 1.5 GB memory usage at the end of the nodes
            Directory dir = FSDirectory.open(file);
            IndexWriterConfig cfg = new IndexWriterConfig(Version.LUCENE_35, new KeywordAnalyzer());
            LogByteSizeMergePolicy mp = new LogByteSizeMergePolicy();
            mp.setMaxMergeMB(3000);
            cfg.setRAMBufferSizeMB(128);
            cfg.setTermIndexInterval(512);
            cfg.setMergePolicy(mp);

            // specify different formats for id fields etc
            // -> this breaks 16 of our tests!? Lucene Bug?
//            cfg.setCodec(new Lucene40Codec() {
//
//                @Override public PostingsFormat getPostingsFormatForField(String field) {
//                    return new Pulsing40PostingsFormat();
//                }
//            });

            // cfg.setMaxThreadStates(8);
            boolean create = !IndexReader.indexExists(dir);
            cfg.setOpenMode(create ? IndexWriterConfig.OpenMode.CREATE : IndexWriterConfig.OpenMode.APPEND);
            writer = new IndexWriter(dir, cfg);
            return true;
        } catch (Exception ex) {
            logger.error("cannot init lucene storage", ex);
            return false;
        }
    }

//    @Override public boolean addNode(int osmId, double lat, double lon) {
//        try {
//            Document doc = new Document();
//            doc.add(new NumericField("_id", Field.Store.NO, true).setIntValue(osmId));
//            doc.add(new NumericField("lat", Field.Store.YES, false).setDoubleValue(lat));
//            doc.add(new NumericField("lon", Field.Store.YES, false).setDoubleValue(lon));
//            writer.addDocument(doc);
//            return true;
//        } catch (Exception ex) {
//            logger.error("Problem while adding node to lucene", ex);
//            return false;
//        }
//    }
    @Override
    public void close() {
        Helper.close(writer);
    }

    @Override public void flush() {
        super.flush();
        try {
            writer.commit();
        } catch (Exception ex) {
            logger.error("cannot commit lucene", ex);
        }
    }

    @Override
    public boolean loadExisting() {
        return init(false);
    }

    @Override
    public void createNew() {
        init(true);
    }
}
