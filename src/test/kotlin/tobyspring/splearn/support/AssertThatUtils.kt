package tobyspring.splearn.support

import org.assertj.core.api.AssertProvider
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.json.JsonPathValueAssert

class AssertThatUtils {
    companion object {

        fun notNull(): (AssertProvider<JsonPathValueAssert>) -> Unit = {
            assertThat(it).isNotNull
        }

        fun equalsTo(expected: Any?): (AssertProvider<JsonPathValueAssert>) -> Unit = {
            assertThat(it).isEqualTo(expected)
        }
    }
}