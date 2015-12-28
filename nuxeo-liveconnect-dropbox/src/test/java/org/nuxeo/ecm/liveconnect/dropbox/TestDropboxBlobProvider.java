/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Andre Justo
 */
package org.nuxeo.ecm.liveconnect.dropbox;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.blob.BlobManager;
import org.nuxeo.ecm.core.blob.BlobProvider;
import org.nuxeo.ecm.core.blob.BlobManager.BlobInfo;
import org.nuxeo.ecm.core.blob.SimpleManagedBlob;
import org.nuxeo.runtime.test.runner.RuntimeHarness;

import javax.inject.Inject;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestDropboxBlobProvider extends DropboxTestCase {

    // same as in test XML contrib
    private static final String PREFIX = "dropbox";

    @Inject
    protected RuntimeHarness harness;

    @Inject
    protected BlobManager blobManager;

    @Test
    public void testSupportsUserUpdate() throws Exception {
        BlobProvider blobProvider = blobManager.getBlobProvider(PREFIX);
        assertTrue(blobProvider.supportsUserUpdate());
    }

    @Test
    public void testReadBlobStreamUploaded() throws Exception {
        BlobInfo blobInfo = new BlobInfo();
        blobInfo.key = PREFIX + ":" + USERID + ":" + FILEID_JPEG;
        blobInfo.filename = FILEID_JPEG;
        Blob blob = new SimpleManagedBlob(blobInfo);
        try (InputStream is = blobManager.getStream(blob)) {
            assertNotNull(is);
            byte[] bytes = IOUtils.toByteArray(is);
            assertEquals(SIZE, bytes.length);
        }
    }

    @Test
    public void testReadBlobStreamNative() throws Exception {
        BlobInfo blobInfo = new BlobInfo();
        blobInfo.key = PREFIX + ":" + USERID + ":" + FILEID_DOC;
        blobInfo.filename = FILEID_DOC;
        Blob blob = new SimpleManagedBlob(blobInfo);
        try (InputStream is = blobManager.getStream(blob)) {
            assertNull(is);
        }
    }

    @Test
    public void testGetBlob() throws Exception {
        String fileInfo = String.format("%s:%s", PREFIX, FILENAME_PDF);
        Blob blob = ((DropboxBlobProvider) blobManager.getBlobProvider(PREFIX)).getBlob(fileInfo);
        assertEquals(SIZE, blob.getLength());
        assertEquals(FILENAME_PDF, blob.getFilename());
    }
 }
