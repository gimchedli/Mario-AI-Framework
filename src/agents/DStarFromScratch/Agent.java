package agents.DStarFromScratch;

import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;
import engine.helper.MarioActions;

/**
 * @author DStarFromScratch
 */
public class Agent implements MarioAgent {
    private boolean[] action;
    //private NewDStarLite tree;
    private DStarLiteScratch tree;

    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
        this.action = new boolean[MarioActions.numberOfActions()];
        //this.tree = new NewDStarLite();
        this.tree = new DStarLiteScratch(model);
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
        
        action = this.tree.runMarioRun(model);
        //action[MarioActions.SPEED.getValue()] = action[MarioActions.JUMP.getValue()] = model.mayMarioJump() || !model.isMarioOnGround();
        //System.out.println(action[MarioActions.JUMP.getValue()]);
        
        tree.init(model.getMarioFloatPos()[0], model.getMarioFloatPos()[1], model.getCurrentGoal(),  model.getMarioFloatPos()[1]);
        tree.computeShortestPath(model);

        

       

        //tree.replan(model);
        /*
        for (Node i : tree.getPath())
		{
			System.out.println("x: " + i.x + " y: " + i.y);
		}
        */
        //System.out.println(tree.getPath());

        return action;
    }

    @Override
    public String getAgentName() {
        return "DStarFromScratch";
    }

}

