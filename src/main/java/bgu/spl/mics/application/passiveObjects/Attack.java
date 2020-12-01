package bgu.spl.mics.application.passiveObjects;

import java.util.List;


/**
 * Passive data-object representing an attack object.
 * You must not alter any of the given public methods of this class.
 * <p>
 * Do not add any additional members/method to this class (except for getters).
 */
public class Attack {
    final List<Integer> serials;
    final int duration;

    /**
     * Constructs an Attack.
     * {@param serialNumbers} - Specify which ewoks will be needed for the attack.
     * {@param duration} - Specify the duration for the attacker to sleep after the attack.
     */
    public Attack(List<Integer> serialNumbers, int duration) {
        this.serials = serialNumbers;
        this.duration = duration;
    }

    /**
     * Getter for the attack ewoks serial numbers list.
     * @return serials
     */
    public List<Integer> getSerials() {
        return serials;
    }

    /**
     * Getter for the duration for the attacker to sleep after the attack.
     * @return duration
     */
    public int getDuration() {
        return duration;
    }
}
