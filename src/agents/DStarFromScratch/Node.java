package agents.DStarFromScratch;

import java.util.ArrayList;

import engine.core.MarioForwardModel;
import engine.core.MarioWorld;
import engine.helper.GameStatus;


public class Node {
    //private float rhs;
    //private float g;

    public float x;
    public float y;
	public MarioWorld world;
    public MarioForwardModel sceneSnapshot = null;
    public Pair<Double, Double> k = new Pair(0.0, 0.0);
	private boolean[] posAction;
    //private MarioForwardModel marioForwardModel;

    public Node (){
        //x = this.marioForwardModel.getMarioFloatPos()[0];
        //y = this.marioForwardModel.getMarioFloatPos()[1];
    }

    //Overloaded constructor
	public Node(float x, float y, Pair<Double,Double> k)
	{
		this.x = x;
		this.y = y;
    }



    //Overloaded constructor
	public Node(Node other)
	{
		this.x = other.x;
		this.y = other.y;
		this.k = other.k;
	}


	public void setPosAction(boolean left, boolean right, boolean down,
	boolean jump, boolean speed){
		this.posAction = new boolean[]{left, right, down, jump, speed};
	}

	public boolean[] getPosAction(){
		return this.posAction;
	}



    public float[] getPos(){
        float[] temp = {x, y};
        return temp;
    }

	public boolean isLeafNode() {
        if (this.sceneSnapshot == null) {
            return false;
        }
        return this.sceneSnapshot.getGameStatus() != GameStatus.RUNNING;
    }


    //Equals
	public boolean eq(final Node s2)
	{
		return ((this.x == s2.x) && (this.y == s2.y));
	}

	//Not Equals
	public boolean neq(final Node s2)
	{
		return ((this.x != s2.x) || (this.y != s2.y));
	}

	//Greater than
	public boolean gt(final Node s2)
	{
		if (k.first()-0.00001 > s2.k.first()) return true;
		else if (k.first() < s2.k.first()-0.00001) return false;
		return k.second() > s2.k.second();
	}

	//Less than or equal to
	public boolean lte(final Node s2)
	{
		if (k.first() < s2.k.first()) return true;
		else if (k.first() > s2.k.first()) return false;
		return k.second() < s2.k.second() + 0.00001;
	}

	//Less than
	public boolean lt(final Node s2)
	{
		if (k.first() + 0.000001 < s2.k.first()) return true;
		else if (k.first() - 0.000001 > s2.k.first()) return false;
		return k.second() < s2.k.second();
	}

	//CompareTo Method. This is necessary when this class is used in a priority queue
	public int compareTo(Object that)
	{
		//This is a modified version of the gt method
		Node other = (Node)that;
		if (k.first()-0.00001 > other.k.first()) return 1;
		else if (k.first() < other.k.first()-0.00001) return -1;
		if (k.second() > other.k.second()) return 1;
		else if (k.second() < other.k.second()) return -1;
		return 0;
	}

	public void setPosAction(boolean[] action) {
		this.posAction = action;
	}

	public ArrayList<boolean[]> setPossibleActions(Node u) {
		return null;
	}


	


    /*
    public float getRHS() {
        return this.rhs;
    }

    public void setRHS(float value) {
        this.rhs = value;
    }

    public float getG() {
        return this.g;
    }

    public void setG(float value) {
        this.g = value;
    }*/
}



