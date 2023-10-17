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

package io.github.imsejin.dl.lezhin.browser

import spock.lang.Specification
import spock.lang.Subject

@Subject(ChromeVersion)
class ChromeVersionSpec extends Specification {

    def "Instantiates by version string"() {
        when:
        def chromeVersion = ChromeVersion.from(versionString)

        then:
        chromeVersion.value == expected

        where:
        versionString                                                                                  | expected
        "ChromeDriver 114.0.5735.90 (386bc09e8f4f2e025eddae123f36f626-refs/branch-heads/5735@{#1052})" | "114.0.5735.90"
        "ProductVersion=115.0.5790.170\nFileVersion=115.0.5790.3"                                      | "115.0.5790.170"
        "Google Chrome 116.0.5845.97-1 a"                                                              | "116.0.5845.97"
        "Google Chrome 116.0.5845.110"                                                                 | "116.0.5845.110"
    }

    def "Checks if compatible version with other"() {
        given:
        def version = ChromeVersion.from(base)
        def otherVersion = ChromeVersion.from(other)

        when:
        def actual = version.isCompatibleWith(otherVersion) && otherVersion.isCompatibleWith(version)

        then:
        actual == expected

        where:
        base            | other            || expected
        "70.0.3538.97"  | "70.0.2409.16"   || true
        "70.0.3538.97"  | "70.0.3538.97"   || true
        "70.0.3538.97"  | "70.0.4831.105"  || true
        "70.0.3538.97"  | "69.0.3538.97"   || false
        "70.0.3538.97"  | "71.0.3538.97"   || false
        "110.0.5481.77" | "110.0.2759.143" || true
        "110.0.5481.77" | "110.0.5481.77"  || true
        "110.0.5481.77" | "110.0.8740.58"  || true
        "110.0.5481.77" | "106.0.5249.61"  || false
        "110.0.5481.77" | "114.0.5735.90"  || false
    }

}
