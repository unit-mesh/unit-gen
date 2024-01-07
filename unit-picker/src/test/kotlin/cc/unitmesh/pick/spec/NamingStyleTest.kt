package cc.unitmesh.pick.spec;

import  org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NamingStyleTest {

    @Test
    fun should_return_true_when_string_follows_snake_case_naming_style() {
        // given
        val name = "snake_case"

        // when
        val result = checkSnakeCase(name)

        // then
        assertTrue(result)
    }

    @Test
    fun should_return_false_when_string_does_not_contain_underscore() {
        // given
        val name = "camelCase"

        // when
        val result = checkSnakeCase(name)

        // then
        assertFalse(result)
    }

    @Test
    fun should_return_false_when_underscore_is_the_first_character() {
        // given
        val name = "_snake_case"

        // when
        val result = checkSnakeCase(name)

        // then
        assertFalse(result)
    }

    @Test
    fun should_return_false_when_underscore_is_the_last_character() {
        // given
        val name = "snake_case_"

        // when
        val result = checkSnakeCase(name)

        // then
        assertFalse(result)
    }

    @Test
    fun should_return_false_when_underscore_is_consecutive() {
        // given
        val name = "snake__case"

        // when
        val result = checkSnakeCase(name)

        // then
        assertFalse(result)
    }

    @Test
    fun should_return_false_when_underscore_is_followed_by_a_number() {
        // given
        val name = "snake_123_case"

        // when
        val result = checkSnakeCase(name)

        // then
        assertFalse(result)
    }

    @Test
    fun should_return_false_when_underscore_is_followed_by_a_capital_letter() {
        // given
        val name = "snake_Case"

        // when
        val result = checkSnakeCase(name)

        // then
        assertFalse(result)
    }
}
