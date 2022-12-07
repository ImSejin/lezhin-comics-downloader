/*
 * Copyright 2020 Sejin Im
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

package io.github.imsejin.dl.lezhin.exception;

/**
 * @since 2.7.0
 */
public class LoginException extends LezhinComicsDownloaderException {

    public LoginException(String errorCode) {
        super("Failed to login: %s", convertErrorCode(errorCode));
    }

    public LoginException(Throwable cause, String format, Object... args) {
        super(cause, format, args);
    }

    /**
     * Converts error code to error message.
     *
     * <p> The following script is in login page. First, {@code __LZ_ERROR_CODE__} is {@code ''}.
     * If you failed to login, it is assigned to error code and {@code __LZ_ERROR__} is assigned to error message.
     *
     * <pre>{@code
     *     <script>
     *         __LZ_ERROR_CODE__ = '1101';
     *
     *         __LZ_ERROR__ = {
     *             '1101': '정확한 정보 입력 후, 다시 시도해주세요.',
     *             '1104': '정확한 정보 입력 후, 다시 시도해주세요.',
     *             '1006': '로그인에 문제가 발생했습니다. help@lezhin.com으로 문의해주세요.',
     *             '1400': '페이스북 로그인에 실패하였습니다.',
     *             '1401': '이메일로 가입된 계정입니다. 이메일 주소와 비밀번호를 입력해 주세요.',
     *             '1402': '페이스북으로 연결된 계정입니다. 페이스북 로그인을 이용해 주세요.',
     *             '1403': '로그인 할 수 없습니다.',
     *             '1404': '이메일 계정이 연결되어 있지 않습니다.',
     *             '1405': '페이스북 연결 정보가 없습니다.',
     *             '1406': '페이스북 사용자와 일치하지 않습니다.',
     *             '1407': '페이스북 이메일 공유를 승인해주셔야 합니다.',
     *             '1408': '다른 계정과 연결되어 있습니다.',
     *             '1409': '페이스북 프로필에서 이메일 주소를 추가해주세요.',
     *             '1410': '네이버 아이디로 로그인 할 수 없습니다.',
     *             '1411': '네이버 아이디로 로그인 할 수 없습니다.',
     *             '1412': '네이버 아이디로 연결된 계정입니다. 네이버 로그인을 이용해 주세요.',
     *             '1413': '이메일 또는 페이스북으로 가입된 계정입니다. 다시 로그인을 시도해 주세요.',
     *             '1414': '이메일 또는 네이버로 가입된 계정입니다. 다시 로그인을 시도해 주세요.',
     *             '1415': '간편 로그인 연결은 하나만 가능합니다.',
     *             '1416': '트위터 계정으로 로그인 할 수 없습니다.',
     *             '1417': '트위터 계정으로 로그인 할 수 없습니다.',
     *             '1418': '트위터로 연결된 계정입니다. 트위터 로그인을 이용해 주세요.',
     *             '1419': '이메일 또는 트위터로 가입된 계정입니다. 다시 로그인을 시도해 주세요.',
     *             '1420': '간편 로그인에 실패하였습니다.',
     *             '1421': '야후 계정으로 로그인 할 수 없습니다.',
     *             '1422': '야후 계정으로 로그인 할 수 없습니다.',
     *             '1423': '야후로 연결된 계정입니다. 야후 로그인을 이용해 주세요.',
     *             '1424': '이메일 또는 야후로 가입된 계정입니다. 다시 로그인을 시도해 주세요.',
     *             '1425': '트위터 프로필에 이메일 주소를 추가해주세요.',
     *             '1431': '구글 계정으로 로그인 할 수 없습니다.',
     *             '1432': '구글로 연결된 계정입니다. 구글 로그인을 이용해 주세요.',
     *             '1434': 'Apple로 로그인 할 수 없습니다.',
     *             '1436': 'Apple ID로 연결된 계정입니다. Apple 로그인을 이용해 주세요.'
     *         }['1101'];
     *     </script>
     * }</pre>
     *
     * @param errorCode error code
     * @return error message
     */
    private static String convertErrorCode(String errorCode) {
        switch (errorCode) {
            case "1101":
                return "non-existent username";
            case "1104":
                return "invalid password";
            default:
                return "unrecognized error";
        }
    }

}
