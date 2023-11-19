package winternship.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Operation {

    private UUID playerID;
    private OperationType operationType;
    private UUID matchID;
    private int amount;
    private BettingSideType bettingSide;
}
