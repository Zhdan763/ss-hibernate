package sorokin.java.course.console;

import org.springframework.stereotype.Component;
import sorokin.java.course.operations.ConsoleOperationType;
import sorokin.java.course.operations.OperationCommand;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OperationsConsoleListener {

    private final Map<ConsoleOperationType, OperationCommand> commandMap;
    private final ConsoleInput consoleInput;
    private boolean running;

    public OperationsConsoleListener(
            List<OperationCommand> operationCommandList,
            ConsoleInput consoleInput
    ) {
        this.commandMap = operationCommandList.stream()
                .collect(Collectors.toMap(OperationCommand::getOperationType, it -> it));
        this.consoleInput = consoleInput;
        this.running = true;
    }

    public void runBank() {
        init();
        process();
    }

    private void process() {
        while (running) {
            var nextOperation = consoleInput.readOperationType();
            processNextCommand(nextOperation);
            if (nextOperation == ConsoleOperationType.EXIT) {
                running = false;
            }
        }
    }

    private void processNextCommand(ConsoleOperationType operationType) {
        try {
            OperationCommand command = commandMap.get(operationType);
            if (command == null) {
                throw new IllegalStateException("No command handler for " + operationType);
            }
            command.execute();
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            String message = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
            System.out.println("Error: " + message);
        }
    }

    private void init() {
        System.out.println("MiniBank started. Type EXIT to stop.");
        consoleInput.printAvailableCommands();
    }

}
