package winternship.domain;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Player {

    @NonNull
    private UUID playerID;
    private long accountBalance;
    private BigDecimal winRate;
    private int gamesPlayed;
    private int gamesWon;
    private boolean isLegit = true;

    private List<Operation> operations = new ArrayList<>();
    private final List<Operation> illegalOperations = new ArrayList<>();

    public void setAccountBalance(long accountBalance) {
        this.accountBalance += accountBalance;
    }

    public void setGamesPlayed(int gamesPlayed){
        this.gamesPlayed += gamesPlayed;
    }

    public void setWinRate(int gamesWon, int gamesPlayed) {
        double result = ((double) gamesWon / gamesPlayed);
        BigDecimal bigDecimalWinRate = new BigDecimal(result);
        winRate = bigDecimalWinRate.setScale(2, RoundingMode.HALF_UP);
    }

    public void addOperation(Operation operation){
        operations.add(operation);
    }

    public void addIllegalOperation(Operation operation){
        illegalOperations.add(operation);
    }

}
