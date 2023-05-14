package agents.DStarFromScratch;

import java.util.ArrayList;


import engine.core.MarioForwardModel;
import engine.helper.GameStatus;

public class MarioNode {
    public MarioForwardModel mario = null;

    public int timeElapsed = 0;
    public float remainingTimeEstimated = 0;
    public float remainingTime = 0;

    public MarioNode succMarioNode;
    public MarioNode predMarioNode;

    public int distanceFromOrigin = 0;
    public boolean hasBeenHurt = false;
    public boolean isInVisitedList = false;

    boolean[] action;
    int repetitions = 1;

    public MarioNode(boolean[] action, int repetitions, MarioNode parent) {
        this.predMarioNode = parent;
        if (parent != null) {
            this.remainingTimeEstimated = parent.estimateRemainingTimeChild(action, repetitions);
            this.distanceFromOrigin = parent.distanceFromOrigin + 1;
        }
        this.action = action;
        this.repetitions = repetitions;
        if (parent != null) {
            timeElapsed = parent.timeElapsed + repetitions;
        }else {
            timeElapsed = 0;
        }
    }


    /**
     * this calculates possible remaining time after movemeont
     * @param marioX // mario position
     * @param marioXA // mario acceleration
     * @return
     */
    public float calcRemainingTime(float marioX, float marioXA) {
        return (100000 - (maxForwardMovement(marioXA, 1000) + marioX)) / Helper.maxMarioSpeed - 1000;
    }


    /**
     * get remaining time
     * @return
     */
    public float getRemainingTime() {
        if (remainingTime > 0) {
            return remainingTime;
        }else {
            return remainingTimeEstimated;
        }
    }

    public void initializeRoot(MarioForwardModel model) {
        if (this.predMarioNode == null) {
            this.mario = model.clone();
            this.remainingTimeEstimated = calcRemainingTime(model.getMarioFloatPos()[0], 0);
        }
    }

    

    public float estimateRemainingTimeChild(boolean[] action, int repetitions) {
        float[] childbehaviorDistanceAndSpeed = Helper.estimateMaximumForwardMovement(
            this.mario.getMarioFloatVelocity()[0], action, repetitions);
        return calcRemainingTime(this.mario.getMarioFloatPos()[0] + childbehaviorDistanceAndSpeed [0], childbehaviorDistanceAndSpeed [1]);
    }

    public float simulatePos() {
        this.mario = predMarioNode.mario.clone();
        for (int i = 0; i < repetitions; i++) {
            this.mario.advance(action);
        }
        int marioDamage = Helper.getMarioDamage(this.mario, this.predMarioNode.mario);
        remainingTime = 
                calcRemainingTime(this.mario.getMarioFloatPos()[0], this.mario.getMarioFloatVelocity()[0]) +
                    marioDamage * (1000000 - 100 * distanceFromOrigin);
        return remainingTime;
    }


    public ArrayList<MarioNode> generateSucc() {
        ArrayList<MarioNode> list = new ArrayList<MarioNode>();
        ArrayList<boolean[]> possibleActions = Helper.createPossibleActions(this);
        if (this.isLeafNode()){
            possibleActions.clear();
        }
        for (boolean[] action: possibleActions) {
            list.add(new MarioNode(action, repetitions, this));
        }
        return list;
    }


    public boolean isLeafNode() {
        if (this.mario == null) {
            return false;
        }
        return this.mario.getGameStatus() != GameStatus.RUNNING;
    }

    private float maxForwardMovement(float initialSpeed, int ticks) {
        float y = ticks;
        float s0 = initialSpeed;
        return (float) (99.17355373 * Math.pow(0.89, y + 1) - 9.090909091 * s0 * Math.pow(0.89, y + 1) + 10.90909091 * y
        - 88.26446282 + 9.090909091 * s0);
    }
}
