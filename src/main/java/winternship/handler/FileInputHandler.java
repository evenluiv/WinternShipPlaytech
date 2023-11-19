package winternship.handler;

import winternship.domain.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class FileInputHandler {

    private static final String playerDataFile = "/player_data.txt";
    private static final String matchDataFile = "/match_data.txt";

    public static List<Match> readMatchData() {

        List<Match> matches = new ArrayList<>();

        InputStream inputStream = FileInputHandler.class.getResourceAsStream(matchDataFile);
        if (inputStream == null) {
            throw new IllegalArgumentException("Match data not found");
        }
        try {
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter(",");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(",");

                if (data.length > 0) {
                    UUID matchID = UUID.fromString(data[0]);
                    double rateA = Double.parseDouble(data[1]);
                    double rateB = Double.parseDouble(data[2]);
                    ResultType result = ResultType.valueOf(data[3]);
                    matches.add( new Match(matchID, rateA, rateB, result));
                }

            }

            scanner.close();

        } catch (Exception e) {
            throw new RuntimeException("Failed to read match data");
        }

        return matches;
    }

    public static List<Operation> readPlayerData() {

        List<Operation> operations = new ArrayList<>();
        InputStream inputStream = FileInputHandler.class.getResourceAsStream(playerDataFile);

        if (inputStream == null) {
            throw new IllegalArgumentException("Player data not found");
        }

        try {
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter(",");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(",");

                if (data.length > 0) {
                    UUID playerID = UUID.fromString(data[0]);
                    OperationType operationType = OperationType.valueOf(data[1]);
                    UUID matchID = !data[2].isEmpty() ? UUID.fromString(data[2]) : null;
                    int amount = Integer.parseInt(data[3]);

                    BettingSideType bettingSide = null;
                    if (data.length == 5) {
                        bettingSide = !data[4].isEmpty() ? BettingSideType.valueOf(data[4]): null;
                    }

                    operations.add(new Operation(playerID, operationType, matchID, amount, bettingSide));
                }

            }

            scanner.close();

        } catch (IllegalStateException e) {
            throw new RuntimeException("Failed to read player data");
        }

        return operations;
    }


    public static void writeResults(Host host, List<Player> legitPlayers, List<Operation> illegalOperations){
        try {
            File file = new File("result.txt");

            if (file.exists()){
                if (file.delete()){
                    System.out.println("result file deleted");
                } else {
                    System.out.println("did not find result file");
                }
            }

            FileWriter fileWriter = new FileWriter("result.txt", true);

            for (Player player : legitPlayers) {
                fileWriter.write("%s %d %s%n".formatted(player.getPlayerID(), player.getAccountBalance(), player.getWinRate()));
            }
            fileWriter.write("\n");

            for (Operation operation : illegalOperations) {
                fileWriter.write("%s %s %s %d %s%n".formatted(operation.getPlayerID(),
                        operation.getOperationType(),
                        operation.getMatchID(),
                        operation.getAmount(),
                        operation.getBettingSide()));
            }
            fileWriter.write("\n");

            fileWriter.write("%s".formatted(host.getHostBalance()));
            fileWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
