package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

/**
 * Instance of event used to signal HanSolo and C3PO to launch attack.
 */
public class AttackEvent implements Event<Boolean> {
    private final Attack attack;

    /**
     * Constructs an AttackEvent
     * @param attack - the parameters for the attack that needed to be held.
     */
    public AttackEvent(Attack attack){
        this.attack = attack;

    }

    /**
     * Get the attack parameters.
     * @return attack
     */
    public Attack getAttack() {
        return attack;
    }
}
