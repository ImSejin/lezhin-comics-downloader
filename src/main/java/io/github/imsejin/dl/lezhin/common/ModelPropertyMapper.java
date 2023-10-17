/*
 * Copyright 2023 Sejin Im
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

package io.github.imsejin.dl.lezhin.common;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import io.github.imsejin.dl.lezhin.api.auth.model.Authority;
import io.github.imsejin.dl.lezhin.api.auth.model.ServiceRequest;
import io.github.imsejin.dl.lezhin.api.auth.service.AuthorityService.AuthData;
import io.github.imsejin.dl.lezhin.attribute.impl.Content;
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Episode;
import io.github.imsejin.dl.lezhin.attribute.impl.HttpHosts;

@Mapper
public interface ModelPropertyMapper {

    ModelPropertyMapper INSTANCE = Mappers.getMapper(ModelPropertyMapper.class);

    Authority toAuthority(AuthData source);

    @Mapping(target = "contentId", source = "content.id")
    @Mapping(target = "episodeId", source = "episode.id")
    @Mapping(target = "q", ignore = true)
    @Mapping(target = "firstCheckType", constant = "'P'")
    ServiceRequest toServiceRequest(Content content, Episode episode, boolean purchased);

    @Mapping(target = "api", source = "config.apiUrl")
    @Mapping(target = "cdn", source = "config.cdnUrl")
    @Mapping(target = "contentsCdn", source = "config.contentsCdnUrl")
    @Mapping(target = "reco", source = "config.recoUrl")
    @Mapping(target = "pay", source = "config.payUrl")
    @Mapping(target = "panther", source = "config.pantherUrl")
    HttpHosts toHttpHosts(Map<String, String> config);

    @Mapping(target = "version", source = "props.version")
    @Mapping(target = "chromedriver.url.downloadList", source = "props.chromedriver/url/download-list")
    ApplicationProperties toApplicationProperties(Map<String, String> props);

}
