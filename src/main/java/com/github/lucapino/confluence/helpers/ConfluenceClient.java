/*
 * Copyright 2011 Tomasz Maciejewski
 * Copyright 2013 Luca Tagliani
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
 */
package com.github.lucapino.confluence.helpers;

import com.github.lucapino.confluence.model.PageDescriptor;
import com.github.lucapino.confluence.rest.client.api.ClientFactory;
import com.github.lucapino.confluence.rest.client.impl.ClientFactoryImpl;
import com.github.lucapino.confluence.rest.core.api.RequestService;
import com.github.lucapino.confluence.rest.core.api.domain.content.ContentResultsBean;
import com.github.lucapino.confluence.rest.core.api.misc.ContentStatus;
import com.github.lucapino.confluence.rest.core.api.misc.ContentType;
import com.github.lucapino.confluence.rest.core.impl.APIAuthConfig;
import com.github.lucapino.confluence.rest.core.impl.APIUriProvider;
import com.github.lucapino.confluence.rest.core.impl.HttpAuthRequestService;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConfluenceClient {

    private final HttpAuthRequestService requestService;
    private final ClientFactory factory;

    public ConfluenceClient(String username, String password, String url) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        APIAuthConfig conf = new APIAuthConfig(url, username, password);
        requestService = new HttpAuthRequestService();
        requestService.connect(new URI(conf.getBaseUrl()), conf.getUser(), conf.getPassword());

        APIUriProvider uriProvider = new APIUriProvider(new URI(conf.getBaseUrl() + "/confluence"));
        factory = new ClientFactoryImpl(executorService, requestService, uriProvider);
    }

    public String getPageId(PageDescriptor descriptor) {
        if (descriptor.isAbsolute()) {
            return descriptor.getId();
        } else if (descriptor.isRelative()) {
            try {
                ContentResultsBean contentResultsBean = factory.getContentClient().getContent(ContentType.PAGE, descriptor.getSpace(), descriptor.getTitle(), ContentStatus.CURRENT, null, null, 0, 0).get();
                return contentResultsBean.getSize() > 0 ? contentResultsBean.getResults().get(0).getId() : null;
            } catch (ExecutionException | InterruptedException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public ClientFactory getClientFactory() {
        return factory;
    }

    public RequestService getRequestService() {
        return requestService;
    }
}
