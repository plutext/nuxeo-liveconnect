/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *      Andre Justo
 */
package org.nuxeo.ecm.liveconnect.dropbox;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonObjectParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.oauth2.providers.AbstractOAuth2UserEmailProvider;
import org.nuxeo.ecm.platform.oauth2.tokens.NuxeoOAuth2Token;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 7.3
 */
public class DropboxOAuth2ServiceProvider extends AbstractOAuth2UserEmailProvider {

    protected static final Log log = LogFactory.getLog(DropboxOAuth2ServiceProvider.class);

    private static final String ACCOUNT_INFO_URL = "https://api.dropbox.com/1/account/info";

    private static final HttpRequestFactory requestFactory =
        HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                request.setParser(new JsonObjectParser(JSON_FACTORY));
            }
        });

    protected String getUserEmail(String accessToken) throws IOException {
        GenericUrl url = new GenericUrl(ACCOUNT_INFO_URL);
        url.set("access_token", accessToken);

        HttpResponse response = requestFactory.buildGetRequest(url).execute();
        GenericJson json = response.parseAs(GenericJson.class);
        return json.get("email").toString();
    }

    public String getServiceUser(String username) {
        Map<String, Serializable> filter = new HashMap<>();
        filter.put("serviceName", serviceName);
        filter.put(NuxeoOAuth2Token.KEY_NUXEO_LOGIN, username);
        List<DocumentModel> entries = getCredentialDataStore().query(filter);
        if (entries == null || entries.size() == 0) {
            return null;
        }
        if (entries.size() > 1) {
            log.error("Found multiple " + serviceName + " accounts for " + username);
        }
        return (String) entries.get(0).getProperty(NuxeoOAuth2Token.SCHEMA, NuxeoOAuth2Token.KEY_SERVICE_LOGIN);
    }

    protected HttpRequestFactory getRequestFactory() {
        return requestFactory;
    }
}
