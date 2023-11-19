package winternship.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Match {
    private UUID matchID;
    private double rateA;
    private double rateB;
    private ResultType result;
}
