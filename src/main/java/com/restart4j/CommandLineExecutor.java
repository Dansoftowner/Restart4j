package com.restart4j;

import oshi.PlatformEnum;
import oshi.SystemInfo;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class CommandLineExecutor {

    private static final String NUL_CHAR = "\00";
    private static final char SPACE = '\u0020';

    private static final Map<PlatformEnum, Supplier<CommandLineExecutor>> executors =
            new HashMap<PlatformEnum, Supplier<CommandLineExecutor>>() {{
               put(PlatformEnum.LINUX, SplitExecutor::new);
               put(PlatformEnum.MACOS, SplitExecutor::new);
            }};

    abstract void exec(String commandLine);

    static CommandLineExecutor getDefaultExecutor() {
        return getForPlatform(SystemInfo.getCurrentPlatform());
    }

    static CommandLineExecutor getForPlatform(PlatformEnum platform) {
        Supplier<CommandLineExecutor> preferred = executors.get(platform);
        return (preferred == null) ? new SimpleExecutor() : preferred.get();
    }

    private static class SimpleExecutor extends CommandLineExecutor {
        @Override
        void exec(String commandLine) {
            try {
                Runtime.getRuntime().exec(commandLine);
            } catch (IOException e) {
                throw new RestartException(String.format("Couldn't execute the starter command with the OS: %s", commandLine), e);
            }
        }
    }

    private static final class SplitExecutor extends CommandLineExecutor {
        @Override
        void exec(String commandLine) {
            String[] cmdArray = commandLine.split(NUL_CHAR);
            try {
                Runtime.getRuntime().exec(cmdArray);
            } catch (IOException e) {
                throw new RestartException(String.format("Couldn't execute the starter command with the OS: %s", Arrays.toString(cmdArray)), e);
            }
        }
    }
}
