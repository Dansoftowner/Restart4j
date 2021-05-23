package com.restart4j;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * For restarting the application.
 * An {@link ApplicationRestart} can be created through builder api.
 *
 * @author Daniel Gyorffy
 */
public final class ApplicationRestart {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationRestart.class);

    private static final Runnable EMPTY_RUNNABLE = () -> {
    };
    private static final Function<String, String> EMPTY_FUNCTION = it -> it;

    private static final String NUL_CHAR = "\00";
    private static final char SPACE = '\u0020';

    private final Runnable beforeNewProcessCreated;
    private final Runnable beforeCurrentProcessTerminated;
    private final Runnable terminationPolicy;

    private final Supplier<CommandLineExecutor> executorFactory;
    private final Function<String, String> cmdModifier;

    private ApplicationRestart(Runnable beforeNewProcessCreated,
                               Runnable beforeCurrentProcessTerminated,
                               Runnable terminationPolicy,
                               Supplier<CommandLineExecutor> executorFactory,
                               Function<String, String> cmdModifier) {
        this.beforeNewProcessCreated = beforeNewProcessCreated;
        this.beforeCurrentProcessTerminated = beforeCurrentProcessTerminated;
        this.terminationPolicy = terminationPolicy;
        this.executorFactory = executorFactory;
        this.cmdModifier = cmdModifier;
    }

    private ApplicationRestart(Builder builder) {
        this(
                builder.beforeNewProcessCreated,
                builder.beforeCurrentProcessTerminated,
                builder.terminationPolicy,
                builder.executorFactory,
                builder.cmdModifier
        );
    }

    public void restartApp() throws RestartException {
        Optional<OSProcess> appProcess = getAppProcess();
        OSProcess raw = appProcess.orElseThrow(() -> new RestartException("Couldn't identify the process by PID"));
        String commandLine = getCommandLine(raw);
        buildNewProcessCreation(commandLine);
        Optional.ofNullable(beforeCurrentProcessTerminated).orElse(EMPTY_RUNNABLE).run();
        Optional.ofNullable(terminationPolicy).orElse(() -> System.exit(0)).run();
    }

    private void buildNewProcessCreation(String commandLine) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Optional.ofNullable(this.beforeNewProcessCreated).orElse(EMPTY_RUNNABLE).run();
            exec(commandLine);
        }));
    }

    private void exec(String commandLine) throws RestartException {
        Optional.ofNullable(executorFactory)
                .orElse(CommandLineExecutor::getDefaultExecutor)
                .get()
                .exec(commandLine);
    }

    private Optional<OSProcess> getAppProcess() {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        int currentPID = operatingSystem.getProcessId();
        return Optional.ofNullable(operatingSystem.getProcess(currentPID));
    }

    private String getCommandLine(@NotNull OSProcess osProcess) {
        String commandLine = osProcess.getCommandLine();
        if (commandLine == null || commandLine.isEmpty())
            throw new RestartException("Couldn't retrieve command-line (it's empty or null)");
        return Optional.ofNullable(this.cmdModifier)
                .orElse(EMPTY_FUNCTION)
                .apply(commandLine);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Runnable beforeNewProcessCreated;
        private Runnable beforeCurrentProcessTerminated;
        private Runnable terminationPolicy;

        private Supplier<CommandLineExecutor> executorFactory;
        private Function<String, String> cmdModifier;

        private Builder() {
        }

        public Builder beforeNewProcessCreated(Runnable beforeNewProcessCreated) {
            this.beforeNewProcessCreated = beforeNewProcessCreated;
            return this;
        }

        public Builder beforeCurrentProcessTerminated(Runnable beforeCurrentProcessTerminated) {
            this.beforeCurrentProcessTerminated = beforeCurrentProcessTerminated;
            return this;
        }

        public Builder terminationPolicy(Runnable terminationPolicy) {
            this.terminationPolicy = terminationPolicy;
            return this;
        }

        public Builder modifyCmd(Function<String, String> cmdModifier) {
            this.cmdModifier = cmdModifier;
            return this;
        }

        public Builder executorFactory(Supplier<CommandLineExecutor> executorFactory) {
            this.executorFactory = executorFactory;
            return this;
        }

        public ApplicationRestart build() {
            return new ApplicationRestart(this);
        }
    }
}
