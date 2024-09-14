package cell.dto;

import position.interfaces.IPosition;

import java.util.Set;

public record CellDetails(CellBasicDetails basicDetails,
                          int currentVersion,
                          Set<IPosition> observers,
                          Set<IPosition> observables) {

    @Override
    public String toString()    {
        return String.format(
                """
                    Cell: {
                       position: %s,
                       originalValue: %s,
                       effectiveValue: %s,
                       version: %s,
                       observers: %s,
                       observables: %s
                    }""",
                basicDetails.position(),
                basicDetails.originalValue(),
                basicDetails.effectiveValue(),
                currentVersion,
                observers,
                observables
        );
    }

}
