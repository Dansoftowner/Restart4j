package com.restart4j;

import org.junit.jupiter.api.Test;
import oshi.PlatformEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandLineExecutorTest {

    @Test
    void testMacOsExecutor() {
        String given = "/usr/bin/java --module-path lib --module Test/com.test.Launcher 'Argument1 Argument2'";

        CommandLineExecutor executor = CommandLineExecutor.getForPlatform(PlatformEnum.MACOS);

        String expected = "/usr/bin/java\00--module-path\00lib\00--module\00Test/com.test.Launcher\00Argument1 Argument2";
        assertEquals(expected, executor.formatCommandLine(given));
    }
}
