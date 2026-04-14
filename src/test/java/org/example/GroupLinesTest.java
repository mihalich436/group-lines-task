package org.example;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

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

    @Nested
    @DisplayName("Тесты построения DSU")
    class BuildDsuTests {
        @Test
        @DisplayName("DSU с группировкой")
        void testBuildDsuWithGrouping() {
            List<String> lines = new ArrayList<>();
            lines.add("1;\"2\";3;4");
            lines.add("1;5;6;7");
            lines.add("8;\"2\";");
            GroupLines.DSU dsu = GroupLines.buildDSU(lines);
            assertEquals(dsu.find(0), dsu.find(1));
            assertEquals(dsu.find(0), dsu.find(2));
        }
        @Test
        @DisplayName("DSU без группировки")
        void testBuildDsuNoGrouping() {
            List<String> lines = new ArrayList<>();
            lines.add("1;2;3;4");
            lines.add("4;3;2;1");
            lines.add("5;6;7;8");
            GroupLines.DSU dsu = GroupLines.buildDSU(lines);
            assertNotEquals(dsu.find(0), dsu.find(1));
            assertNotEquals(dsu.find(0), dsu.find(2));
            assertNotEquals(dsu.find(1), dsu.find(2));
        }
        @Test
        @DisplayName("DSU с пустыми значениями")
        void testBuildDsuWithEmptyValues() {
            List<String> lines = new ArrayList<>();
            lines.add("1;;3;4");
            lines.add("4;;2;\"\"");
            lines.add("5;6;7;\"\"");
            GroupLines.DSU dsu = GroupLines.buildDSU(lines);
            assertNotEquals(dsu.find(0), dsu.find(1));
            assertNotEquals(dsu.find(0), dsu.find(2));
            assertNotEquals(dsu.find(1), dsu.find(2));
        }
        @Test
        @DisplayName("DSU")
        void testBuildDSU() {
            List<String> lines = new ArrayList<>();
            lines.add("1;2;3;4");
            lines.add("1;5;6;7");
            lines.add("8;;");
            lines.add("4;3;2;1");
            lines.add("\"\";;");
            lines.add("\"\";9;10");
            GroupLines.DSU dsu = GroupLines.buildDSU(lines);
            assertEquals(dsu.find(0), dsu.find(1));
            assertNotEquals(dsu.find(0), dsu.find(2));
            assertNotEquals(dsu.find(0), dsu.find(3));
            assertNotEquals(dsu.find(2), dsu.find(4));
            assertNotEquals(dsu.find(4), dsu.find(5));
        }
    }
}