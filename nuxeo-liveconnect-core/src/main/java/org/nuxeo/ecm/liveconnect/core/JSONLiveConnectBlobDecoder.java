/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Gabriel Barata <gbarata@nuxeo.com>
 *
 */
package org.nuxeo.ecm.liveconnect.core;

import java.io.IOException;

import org.codehaus.jackson.node.ObjectNode;
import org.nuxeo.ecm.automation.core.util.JSONBlobDecoder;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.blob.BlobManager;
import org.nuxeo.ecm.core.blob.BlobProvider;
import org.nuxeo.runtime.api.Framework;

/**
 * @since 8.4
 */
public class JSONLiveConnectBlobDecoder implements JSONBlobDecoder {

    @Override
    public Blob getBlobFromJSON(ObjectNode jsonObject) {

        if (!(jsonObject.has("providerId") && jsonObject.has("user") && jsonObject.has("fileId"))) {
            return null;
        }

        String providerId = jsonObject.get("providerId").getTextValue();
        BlobProvider provider = Framework.getService(BlobManager.class).getBlobProvider(providerId);
        if (!(provider instanceof LiveConnectBlobProvider)) {
            return null;
        }
        try {
            return ((LiveConnectBlobProvider) provider).toBlob(new LiveConnectFileInfo(
                    jsonObject.get("user").getTextValue(), jsonObject.get("fileId").getTextValue()));
        } catch (IOException e) {
            throw new NuxeoException(e);
        }

    }

}
