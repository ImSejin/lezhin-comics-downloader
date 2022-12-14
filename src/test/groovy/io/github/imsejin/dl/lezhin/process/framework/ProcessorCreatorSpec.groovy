package io.github.imsejin.dl.lezhin.process.framework

import io.github.imsejin.dl.lezhin.process.impl.AccessTokenProcessor
import io.github.imsejin.dl.lezhin.process.impl.ConfigurationFileProcessor
import io.github.imsejin.dl.lezhin.process.impl.ContentInformationProcessor
import io.github.imsejin.dl.lezhin.process.impl.DirectoryCreationProcessor
import io.github.imsejin.dl.lezhin.process.impl.DownloadProcessor
import io.github.imsejin.dl.lezhin.process.impl.HttpHostsProcessor
import io.github.imsejin.dl.lezhin.process.impl.LocaleSelectionProcessor
import io.github.imsejin.dl.lezhin.process.impl.LoginProcessor
import io.github.imsejin.dl.lezhin.process.impl.PurchasedEpisodesProcessor
import spock.lang.Specification

import java.nio.file.Path

class ProcessorCreatorSpec extends Specification {

    def "Returns empty processors"() {
        given:
        def creator = new ProcessorCreator()

        when:
        def processors = creator.create([])

        then:
        processors == []
    }

    def "Creates processors"() {
        given:
        def beans = [Path].collect { Mock(it) }
        def types = [ConfigurationFileProcessor, LoginProcessor, HttpHostsProcessor, AccessTokenProcessor,
                     LocaleSelectionProcessor, ContentInformationProcessor, PurchasedEpisodesProcessor,
                     DirectoryCreationProcessor, DownloadProcessor]

        when:
        def creator = new ProcessorCreator(beans as Object[])
        def processors = creator.create(types)

        then:
        processors*.class == types
    }

}
