package fi.hsl.common.redis;

import org.junit.jupiter.api.Test;

import java.util.List;

import static fi.hsl.common.lang.Checks.checkNotEmpty;
import static fi.hsl.common.lang.Checks.checkRequired;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChecksTest {

    @Test
    void shouldReturnValueWhenNotNull() {
        // given
        var value = "hello";

        // when
        var result = checkRequired("testParam", value);

        // then
        assertThat(result).isEqualTo("hello");
    }

    @Test
    void shouldThrowWhenValueIsNull() {
        // given
        String value = null;

        // when / then
        assertThatThrownBy(() -> checkRequired("myParam", value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("myParam is required");
    }

    @Test
    void shouldReturnCollectionWhenNotEmpty() {
        // given
        var collection = List.of("a", "b");

        // when
        var result = checkNotEmpty("param", collection);

        // then
        assertThat(result).containsExactly("a", "b");
    }

    @Test
    void shouldThrowWhenCollectionIsNull() {
        // given
        List<String> collection = null;

        // when / then
        assertThatThrownBy(() -> checkNotEmpty("param", collection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("param must not be null");
    }

    @Test
    void shouldThrowWhenCollectionIsEmpty() {
        // given
        var collection = List.of();

        // when / then
        assertThatThrownBy(() -> checkNotEmpty("param", collection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("param must not be empty");
    }

    @Test
    void shouldReturnStringWhenNotBlank() {
        // given
        var value = "hello";

        // when
        var result = checkNotEmpty("param", value);

        // then
        assertThat(result).isEqualTo("hello");
    }

    @Test
    void shouldThrowWhenStringIsNull() {
        // given
        String value = null;

        // when / then
        assertThatThrownBy(() -> checkNotEmpty("param", value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("param must not be blank");
    }

    @Test
    void shouldThrowWhenStringIsBlank() {
        // given
        var value = "   ";

        // when / then
        assertThatThrownBy(() -> checkNotEmpty("param", value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("param must not be blank");
    }
}
