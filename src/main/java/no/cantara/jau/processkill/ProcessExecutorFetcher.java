package no.cantara.jau.processkill;

public class ProcessExecutorFetcher {

    public ProcessExecutor getProcessExecutorBasedOnOs() {
        if (isWindows()) {
            return new WindowsProcessExecutor();
        } else {
            return new UnixProcessExecutor();
        }
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
}
