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

package io.github.imsejin.dl.lezhin.process.impl;

import io.github.imsejin.dl.lezhin.annotation.ProcessSpecification;
import io.github.imsejin.dl.lezhin.api.chromedriver.service.ChromeDriverDownloadService;
import io.github.imsejin.dl.lezhin.attribute.impl.ChromeInfo;
import io.github.imsejin.dl.lezhin.exception.LezhinComicsDownloaderException;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;

@ProcessSpecification(dependsOn = ChromeInfoResolutionProcessor.class)
public class ChromeDriverDownloadProcessor implements Processor {

    @Override
    public Object process(ProcessContext context) throws LezhinComicsDownloaderException {
        ChromeInfo chromeInfo = context.getChromeInfo();
        ChromeDriverDownloadService service = new ChromeDriverDownloadService();

        return null;
    }

    // -------------------------------------------------------------------------------------------------

}
