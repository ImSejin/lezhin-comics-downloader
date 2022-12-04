package io.github.imsejin.dl.lezhin.process.framework

import io.github.imsejin.dl.lezhin.process.impl.AccessTokenProcessor
import io.github.imsejin.dl.lezhin.process.impl.ConfigurationFileProcessor
import io.github.imsejin.dl.lezhin.process.impl.ContentDirectoryProcessor
import io.github.imsejin.dl.lezhin.process.impl.ContentInformationProcessor
import io.github.imsejin.dl.lezhin.process.impl.EpisodeAuthorityProcessor
import io.github.imsejin.dl.lezhin.process.impl.LocaleSelectionProcessor
import io.github.imsejin.dl.lezhin.process.impl.LoginProcessor
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
        def types = [ConfigurationFileProcessor, LoginProcessor, AccessTokenProcessor, LocaleSelectionProcessor,
                     ContentInformationProcessor, ContentDirectoryProcessor, EpisodeAuthorityProcessor]

        when:
        def creator = new ProcessorCreator(beans as Object[])
        def processors = creator.create(types)

        then:
        processors.collect { it.class } == types
    }

}
