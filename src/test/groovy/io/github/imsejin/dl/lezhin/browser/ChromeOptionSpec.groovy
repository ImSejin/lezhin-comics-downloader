package io.github.imsejin.dl.lezhin.browser

import spock.lang.Specification
import spock.lang.Subject

@Subject(ChromeOption)
class ChromeOptionSpec extends Specification {

    def "Checks if argument string is valid"() {
        given:
        def arguments = ChromeOption.arguments

        expect:
        arguments.unique(false) == arguments
        arguments.every { it.matches('^--[a-z]+(-[a-z]+)*(=.+)?$') }
    }

}
