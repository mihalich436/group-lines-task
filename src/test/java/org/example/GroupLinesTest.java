package org.example;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class GroupLinesTest {
    @Nested
    @DisplayName("Тесты валидации строк")
    class ValidationTests {

        @Test
        @DisplayName("Корректная строка без кавычек")
        void testValidLineWithoutQuotes() {
            assertTrue(GroupLines.isValidLine("111;123;222"));
            assertTrue(GroupLines.isValidLine("200;123;100;10001;abc"));
            assertTrue(GroupLines.isValidLine("300;;100"));
            assertTrue(GroupLines.isValidLine(";"));
        }

        @Test
        @DisplayName("Корректная строка с кавычками вокруг значений")
        void testValidLineWithQuotes() {
            assertTrue(GroupLines.isValidLine("\"111\";\"123\";\"222\""));
            assertTrue(GroupLines.isValidLine("\"200\";\"123\";\"100\""));
            assertTrue(GroupLines.isValidLine("\"300\";\"\";\"100\""));
            assertTrue(GroupLines.isValidLine("200;\"\""));
        }

        @Test
        @DisplayName("Пустые значения в кавычках")
        void testEmptyValuesInQuotes() {
            assertTrue(GroupLines.isValidLine("\"\";\"\";\"\""));
            assertTrue(GroupLines.isValidLine(";\"\";"));
            assertTrue(GroupLines.isValidLine("\"\""));
        }

        @Test
        @DisplayName("Некорректные строки с лишними кавычками внутри значения")
        void testInvalidLineWithInternalQuotes() {
            assertFalse(GroupLines.isValidLine("\"8383\"200000741652251\""));
            assertFalse(GroupLines.isValidLine("\"79855053897\"83100000580443402\";\"200000133000191\""));
        }

        @Test
        @DisplayName("Строки с непарными кавычками")
        void testUnmatchedQuotes() {
            assertFalse(GroupLines.isValidLine("\""));
            assertFalse(GroupLines.isValidLine("\"111;123;222"));
            assertFalse(GroupLines.isValidLine("111;\"123;222"));
            assertFalse(GroupLines.isValidLine("111;123;222\""));
            assertFalse(GroupLines.isValidLine("\"111;123\";222"));
        }

        @Test
        @DisplayName("Строка с пробелами")
        void testLineWithSpaces() {
            assertTrue(GroupLines.isValidLine("111; 123; 222"));
            assertTrue(GroupLines.isValidLine(" ; ; "));
        }

        @Test
        @DisplayName("Строка со специальными символами")
        void testLineWithSpecialChars() {
            assertTrue(GroupLines.isValidLine("test@email.com;123-456;value with spaces"));
        }
    }
}