package learningtest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StringTest {

    @Test
    @DisplayName("','를 기준으로 split한다.")
    void split() {
        /* given */
        String sampleText = "1,2";

        /* when */
        String[] tokens = sampleText.split(",");

        /* then */
        assertThat(tokens).containsExactly("1", "2");
    }

    @Test
    @DisplayName("','가 없는 문자열을 split한 경우 그대로 나온다.")
    void split2() {
        /* given */
        String sampleText = "1";

        /* when */
        String[] tokens = sampleText.split(",");

        /* then */
        assertThat(tokens).containsExactly("1");
    }

    @Test
    @DisplayName("substring()으로 괄호 안의 문자열을 추출한다.")
    void substring() {
        /* given */
        String sampleText = "(1,2)";

        /* when */
        String extractedText = sampleText.substring(1, sampleText.length() - 1);

        /* then */
        assertThat(extractedText).isEqualTo("1,2");
    }

    @Test
    @DisplayName("잘못된 index로 String을 자르려고 한 경우 IndexOutOfBoundsException")
    void stringIndexOutOfBoundsException() {
        /* when */ /* then */
        assertThatThrownBy(() -> "".substring(0, 1))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }
}