package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.Attack;

public class AttackEvent implements Event<Boolean> {
    private Attack attack;
    private int attackNumber;

    public AttackEvent(Attack attack, int attackNumber){
        this.attack = attack;
        this.attackNumber = attackNumber;
    }

    public Attack getAttack() {
        return attack;
    }
    public int getAttackNumber(){ return attackNumber;}
}
