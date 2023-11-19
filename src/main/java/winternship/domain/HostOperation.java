package winternship.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class HostOperation {

    private UUID playerID;
    private int amount;
}
