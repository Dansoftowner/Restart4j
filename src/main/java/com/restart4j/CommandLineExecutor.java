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
               put(PlatformEnum.LINUX, LinuxExecutor::new);
               put(PlatformEnum.MACOS, MacOSExecutor::new);
            }};

    abstract void exec(String commandLine);

    String formatCommandLine(String commandLine) {
        return commandLine;
    }

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
            commandLine = formatCommandLine(commandLine);
            try {
                Runtime.getRuntime().exec(commandLine);
            } catch (IOException e) {
                throw new RestartException(String.format("Couldn't execute the starter command with the OS: %s", commandLine), e);
            }
        }
    }

    private static final class LinuxExecutor extends CommandLineExecutor {
        @Override
        void exec(String commandLine) {
            String[] cmdArray = formatCommandLine(commandLine).split(NUL_CHAR);
            try {
                Runtime.getRuntime().exec(cmdArray);
            } catch (IOException e) {
                throw new RestartException(String.format("Couldn't execute the starter command with the OS: %s", Arrays.toString(cmdArray)), e);
            }
        }
    }

    private static final class MacOSExecutor extends CommandLineExecutor {
        @Override
        void exec(String commandLine) {
            String[] cmdArray = formatCommandLine(commandLine).split(NUL_CHAR);
            try {
                Runtime.getRuntime().exec(cmdArray);
            } catch (IOException e) {
                throw new RestartException(String.format("Couldn't execute the starter command with the OS: %s", Arrays.toString(cmdArray)), e);
            }
        }

        /*@Override
        String formatCommandLine(String commandLine) {
            List<Character> quotes = Arrays.asList('"', '\'');
            boolean inQuote = false;
            StringBuilder formattedCommand = new StringBuilder();
            for (char ch : commandLine.toCharArray()) {
                if (quotes.contains(ch)) {
                    inQuote = !inQuote;
                    continue;
                }
                if (inQuote) {
                    formattedCommand.append(ch);
                } else {
                    if (ch == SPACE) {
                        formattedCommand.append(NUL_CHAR);
                    } else {
                        formattedCommand.append(ch);
                    }
                }
            }
            return formattedCommand.toString();
        }*/
    }

}
