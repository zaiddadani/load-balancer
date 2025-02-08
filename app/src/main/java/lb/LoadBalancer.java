package lb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

import lb.strategies.RoundRobin;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "lb", mixinStandardHelpOptions = true, version = "lb 1.0", description = "This challenge is to build your own application layer load balancer")
public class LoadBalancer implements Callable<Result> {

    public static void main(String[] args) {
        var compress = new LoadBalancer();
        var cmd = new CommandLine(compress);
        var exitCode = cmd.execute(args);
        cmd.getExecutionResult();
        System.exit(exitCode);
    }

    @Option(names = "-p", description = "-b specifies byte positions")
    int port = 8080;
    @Option(names = "-blist", description = "-blist specifies byte positions")
    String bePools;

    @Option(names = "-b", arity = "0..", description = "-b specifies byte positions")
    boolean isBackend = false;

    @Override
    public Result call() throws Exception {
        if (this.isBackend) {
            new SimpleBackend(this.port);
        } else {
            if (bePools == null || bePools.isEmpty()) {
                bePools = "http://localhost:9000";
            }
            var bePoolArray = bePools.split(",");
            var backendServers = new ArrayList<String>(Arrays.asList(bePoolArray));
            var lbStrategy = new RoundRobin(backendServers);
            lbStrategy.startHealthChecks();
            new SimpleLoadBalancer(this.port, lbStrategy);
        }
        return new Result();
    }
}