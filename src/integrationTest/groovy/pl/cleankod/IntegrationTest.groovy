package pl.cleankod

import org.apache.http.HttpResponse

import java.nio.charset.StandardCharsets


class IntegrationTest extends BaseApplicationSpecification {

    def "should return an account by number with different currency for real"() {
        given:
        def accountNumberValue = "65 1090 1665 0000 0001 0373 7343"
        def accountNumberUrlEncoded = URLEncoder.encode(accountNumberValue, StandardCharsets.UTF_8)
        def currency = "EUR"

        when:
        HttpResponse response = getResponse("/accounts/number=${accountNumberUrlEncoded}?currency=${currency}")

        then:
        response.getStatusLine().statusCode in 200..300
    }
}
