package io.github.imsejin.dl.lezhin.util

import spock.lang.Specification
import spock.lang.Subject

@Subject(FileNameUtils)
class FileNameUtilsSpec extends Specification {

    def "Replaces forbidden characters"() {
        when:
        def actual = FileNameUtils.replaceForbiddenCharacters(fileName)

        then:
        actual == expected

        where:
        fileName                | expected
        ""                      | ""
        "alpha.log"             | "alpha.log"
        " beta "                | " beta "
        "<gamma>"               | "＜gamma＞"
        "delta:2"               | "delta：2"
        'He said "It\'s mine".' | "He said ＂It's mine＂．"
        "1/4"                   | "1／4"
        "red\\blue"             | "red＼blue"
        "black|white"           | "black｜white"
        "Why so serious?"       | "Why so serious？"
        "* is called asterisk." | "＊ is called asterisk．"
        "It is..."              | "It is…"
        "...Wait, what!?"       | "...Wait, what!？"
    }

    def "Sanitizes file name"() {
        when:
        def actual = FileNameUtils.sanitize(fileName)

        then:
        actual == expected

        where:
        fileName        | expected
        ""              | ""
        "\t\n\r\b"      | ""
        " alpha . conf" | "alpha.conf"
        "A\u0000B"      | "AB"
        "X\u001fY"      | "XY"
        "const"         | "const"
        "Con.log"       | "_Con.log"
        "PRNT.dat"      | "PRNT.dat"
        "PRN.dat"       | "_PRN.dat"
        "aux2"          | "aux2"
        " aux "         | "_aux"
        "<NUL>"         | "<NUL>"
        "NUL"           | "_NUL"
        "Com10.ini"     | "Com10.ini"
        "Com9.ini"      | "_Com9.ini"
        "lPT-1.yml"     | "lPT-1.yml"
        "lPT0.yml"      | "_lPT0.yml"
    }

}
