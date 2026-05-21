package no.ntnu.idatx2003.millions.model;

/**
 * Enum representing a player's status in the game.
 * Status is determined by trading activity and net worth.
 */
public enum PlayerStatus {
    /**
     * Novice: Starting status for all players.
     */
    NOVICE,

    /**
     * Investor: Achieved by trading ≥10 weeks and net worth ≥ 1.2x starting money.
     */
    INVESTOR,

    /**
     * Speculator: Achieved by trading ≥20 weeks and net worth ≥ 2x starting money.
     */
    SPECULATOR;

    @Override
    public String toString() {
        return switch (this) {
            case NOVICE -> "NOVICE";
            case INVESTOR -> "INVESTOR";
            case SPECULATOR -> "SPECULATOR";
        };
    }
}

