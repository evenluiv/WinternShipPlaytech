package winternship.handler;

import winternship.domain.*;

import java.util.*;

public class DataHandler {

    private static Host host = new Host();
    private final List<Operation> operations;
    private final List<Match> matches;
    private static final Map<UUID, Player> players = new HashMap<>();

    public DataHandler() {
        this.operations = FileInputHandler.readPlayerData();
        this.matches = FileInputHandler.readMatchData();
    }

    public void Run() {
        createPlayers();
        handleOperations();
        generateResults();
    }

    public void createPlayers() {
        for (Operation operation : operations) {
            if (players.get(operation.getPlayerID()) == null) {
                players.put(operation.getPlayerID(), new Player(operation.getPlayerID()));
            }
        }
    }

    private void handleOperations() {
        for (Operation operation : operations) {
            switch (operation.getOperationType()) {
                case BET -> bet(operation);
                case DEPOSIT -> deposit(operation);
                case WITHDRAW -> withdraw(operation);
            }
        }
    }

    private void deposit(Operation operation) {
        Player player = players.get(operation.getPlayerID());

        if (player != null) {
            player.setAccountBalance(operation.getAmount());
            player.addOperation(operation);
        } else {
            System.out.println("Player with ID '" + operation.getPlayerID() + "' not found");
        }
    }

    private void withdraw(Operation operation) {
        Player player = players.get(operation.getPlayerID());

        if (player != null) {
            if (operation.getAmount() <= player.getAccountBalance()) {
                player.setAccountBalance(-operation.getAmount());
                player.addOperation(operation);
            } else {
                player.addIllegalOperation(operation);
                rollbackPlayerOperations(player.getPlayerID());
            }
        } else {
            System.out.println("Player with ID '" + operation.getPlayerID() + "' not found");
        }
    }

    private void bet(Operation operation) {
        Player player = players.get(operation.getPlayerID());

        int betAmount = operation.getAmount();

        if (betAmount <= player.getAccountBalance()) {
            player.setAccountBalance(-betAmount);
            player.setGamesPlayed(1);
            player.addOperation(operation);

            Optional<Match> findMatch = matches.stream()
                    .filter(x -> x.getMatchID().equals(operation.getMatchID()))
                    .findFirst();

            if (findMatch.isPresent()) {
                Match match = findMatch.get();

                ResultType matchResult = match.getResult();

                double rate = 0;
                if (matchResult.equals(ResultType.A)) {
                    rate = match.getRateA();
                } else if (matchResult.equals(ResultType.B)) {
                    rate = match.getRateB();
                }

                int addToPlayerAccount;

                if (Objects.equals(operation.getBettingSide().toString(), matchResult.toString())) {
                    player.setGamesWon(1);

                    if (rate < 1) {
                        addToPlayerAccount = (int) (betAmount + (rate * betAmount));
                    } else {
                        addToPlayerAccount = (int) (rate * betAmount);
                    }

                    player.setAccountBalance(addToPlayerAccount);

                    host.addHostOperation(new HostOperation(player.getPlayerID(),-addToPlayerAccount + betAmount));
                    host.setHostBalance(-addToPlayerAccount + betAmount);

                } else if (matchResult.equals(ResultType.DRAW)) {
                    player.setAccountBalance(betAmount);
                } else {
                    host.addHostOperation(new HostOperation(player.getPlayerID(), betAmount));
                    host.setHostBalance(betAmount);
                }
            }

            player.setWinRate(player.getGamesWon(), player.getGamesPlayed());
        } else {
            player.addIllegalOperation(operation);
            rollbackPlayerOperations(player.getPlayerID());
        }
    }

    private static void rollbackPlayerOperations(UUID playerID){
        Player player = players.get(playerID);
        player.setLegit(false);

        List<HostOperation> hostOperations = host.getHostOperations();
        for (HostOperation hostOperation : hostOperations) {
            if (Objects.equals(playerID, hostOperation.getPlayerID())) {
                host.setHostBalance(-(hostOperation.getAmount()));
            }
        }
    }

    public void generateResults() {
        List<Player> legitPlayers = players.values()
                .stream()
                .filter(x -> x.isLegit())
                .toList();

        List<Operation> illegalOperations = players.values()
                .stream()
                .filter(x -> !x.isLegit())
                .flatMap(player -> player.getIllegalOperations().stream())
                .toList();

        FileInputHandler.writeResults(host, legitPlayers, illegalOperations);
    }
}
