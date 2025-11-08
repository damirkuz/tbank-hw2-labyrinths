package academy.maze.validation;

import static org.junit.jupiter.api.Assertions.*;

import academy.maze.dto.Point;
import org.junit.jupiter.api.Test;

class InputValidatorTest {

    @Test
    void testParsePointValid() {
        Point p = InputValidator.parsePoint("5,10");
        assertNotNull(p);
        assertEquals(5, p.x());
        assertEquals(10, p.y());
    }

    @Test
    void testParsePointWithSpaces() {
        Point p = InputValidator.parsePoint("  3 , 4  ");
        assertNotNull(p);
        assertEquals(3, p.x());
        assertEquals(4, p.y());
    }

    @Test
    void testParsePointZero() {
        Point p = InputValidator.parsePoint("0,0");
        assertNotNull(p);
        assertEquals(0, p.x());
        assertEquals(0, p.y());
    }

    @Test
    void testParsePointNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> InputValidator.parsePoint("-1,5"));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.parsePoint("5,-1"));
    }

    @Test
    void testParsePointInvalidFormatThrows() {
        assertThrows(IllegalArgumentException.class, () -> InputValidator.parsePoint("5"));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.parsePoint("5,10,15"));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.parsePoint("a,b"));
    }

    @Test
    void testParsePointEmptyThrows() {
        assertThrows(IllegalArgumentException.class, () -> InputValidator.parsePoint(""));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.parsePoint(null));
    }

    @Test
    void testValidateDimensionsValid() {
        // Не должно выбросить исключение
        assertDoesNotThrow(() -> InputValidator.validateDimensions(1, 1));
        assertDoesNotThrow(() -> InputValidator.validateDimensions(10, 10));
        assertDoesNotThrow(() -> InputValidator.validateDimensions(100, 100));
    }

    @Test
    void testValidateDimensionsZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateDimensions(0, 5));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateDimensions(5, 0));
    }

    @Test
    void testValidateDimensionsNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateDimensions(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateDimensions(5, -1));
    }

    @Test
    void testValidateDimensionsTooLargeThrows() {
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateDimensions(1001, 500));
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateDimensions(500, 1001));
    }

    @Test
    void testValidateDimensionsMaxValid() {
        // Максимальное допустимое значение
        assertDoesNotThrow(() -> InputValidator.validateDimensions(1000, 1000));
    }
}
