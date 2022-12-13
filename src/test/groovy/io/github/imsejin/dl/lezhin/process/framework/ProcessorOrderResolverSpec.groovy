package io.github.imsejin.dl.lezhin.process.framework

import io.github.imsejin.common.tool.ClassFinder
import io.github.imsejin.common.tool.ClassFinder.SearchPolicy
import io.github.imsejin.common.util.ClassUtils
import io.github.imsejin.dl.lezhin.process.Processor
import io.github.imsejin.dl.lezhin.process.impl.AccessTokenProcessor
import io.github.imsejin.dl.lezhin.process.impl.ConfigurationFileProcessor
import io.github.imsejin.dl.lezhin.process.impl.ContentInformationProcessor
import io.github.imsejin.dl.lezhin.process.impl.DirectoryCreationProcessor
import io.github.imsejin.dl.lezhin.process.impl.DownloadProcessor
import io.github.imsejin.dl.lezhin.process.impl.HttpHostsProcessor
import io.github.imsejin.dl.lezhin.process.impl.LocaleSelectionProcessor
import io.github.imsejin.dl.lezhin.process.impl.LoginProcessor
import spock.lang.Specification

class ProcessorOrderResolverSpec extends Specification {

    def "Resolves the order of process types"() {
        given:
        def processorTypes = ClassFinder.getAllSubtypes(Processor, SearchPolicy.CLASS)
                .findAll { !ClassUtils.isAbstractClass(it) && it.enclosingClass == null }

        when:
        def orderedTypes = ProcessorOrderResolver.resolve(processorTypes)

        then:
        processorTypes == orderedTypes as Set
        orderedTypes == [
                ConfigurationFileProcessor,
                LoginProcessor,
                HttpHostsProcessor,
                AccessTokenProcessor,
                LocaleSelectionProcessor,
                ContentInformationProcessor,
                DirectoryCreationProcessor,
                DownloadProcessor,
        ]
    }

}
