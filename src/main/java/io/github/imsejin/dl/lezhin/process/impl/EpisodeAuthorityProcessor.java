/*
 * Copyright 2022 Sejin Im
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

package io.github.imsejin.dl.lezhin.process.impl;

import io.github.imsejin.dl.lezhin.annotation.ProcessSpecification;
import io.github.imsejin.dl.lezhin.api.auth.model.Authority;
import io.github.imsejin.dl.lezhin.api.auth.model.ServiceRequest;
import io.github.imsejin.dl.lezhin.api.auth.service.AuthorityService;
import io.github.imsejin.dl.lezhin.exception.LezhinComicsDownloaderException;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;

/**
 * @since 3.0.0
 */
@ProcessSpecification(dependsOn = AccessTokenProcessor.class)
public class EpisodeAuthorityProcessor implements Processor {

    @Override
    public Authority process(ProcessContext context) throws LezhinComicsDownloaderException {
        AuthorityService service = new AuthorityService(context.getLanguage().getValue(),
                context.getAccessToken().getValue());

        ServiceRequest request = new ServiceRequest();
        request.setContentId(0L);
        request.setEpisodeId(0L);

        return service.getAuthForViewEpisode(request);
    }

}
