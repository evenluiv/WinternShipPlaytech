package winternship.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Host {
    private long hostBalance;

    private List<HostOperation> hostOperations = new ArrayList<>();

    public void setHostBalance(long hostBalance) {
        this.hostBalance += hostBalance;
    }

    public void addHostOperation(HostOperation operation){
        hostOperations.add(operation);
    }
}
