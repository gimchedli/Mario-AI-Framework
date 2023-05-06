package agents.DStarFromScratch;

import java.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

//import javax.swing.text.html.HTMLDocument.Iterator;

import engine.helper.MarioActions;


import engine.core.MarioForwardModel;

public class DStarLiteScratch {
    private List<Node> path = new ArrayList<Node>();
    private double C1;
    private Node start = new Node();
    private Node goal = new Node();
    private Node last = new Node();
    private float k_m;
    private int maxSteps;
    private PriorityQueue<Node> openList  = new PriorityQueue<Node>();
    private MarioForwardModel mario = null;
    private List<Node> posList;

	private ArrayList<boolean[]> currentActionPlan;

    private HashMap<Node, Float>		openHash = new HashMap<Node, Float>();
    public HashMap<Node, PointInfo>	pointHash = new HashMap<Node, PointInfo>();


    //Constants
	private double M_SQRT2 = Math.sqrt(2.0);

    //Default constructor
	public DStarLiteScratch(MarioForwardModel model)
	{
		this.mario = model;
		maxSteps	= 80000;
		C1			= 1;
	}


    /*
	 * Initialise Method
	 * @params start and goal coordinates
	 */
	public void init(float sX, float sY, float gX, float gY)
	{
		pointHash.clear();
		path.clear();
		openHash.clear();
		while(!openList.isEmpty()) openList.poll();

		k_m = 0;

		start.x = sX;
		start.y = sY;
		goal.x  = gX;
		goal.y  = gY;

		PointInfo tmp = new PointInfo();
		tmp.g   = 0;
		tmp.rhs = 0;
		tmp.cost = C1;

		pointHash.put(goal, tmp);

		tmp = new PointInfo();
		tmp.g = tmp.rhs = heuristic(start, goal);
		tmp.cost = C1;
		pointHash.put(start, tmp);
		start = calculateKey(start);

		last = start;

	}


    private double heuristic(Node a, Node b){
		double temp;
		double min = Math.abs(a.x - b.x);
		double max = Math.abs(a.y - b.y);
		if (min > max)
		{
			temp = min;
			min = max;
			max = temp;
		}
		return ((M_SQRT2-1.0)*min + max);
    }



    /*
	 * CalculateKey(Node u)
	 * As per [S. Koenig, 2002]
	 */
	private Node calculateKey(Node u)
	{
		double val = Math.min(getRHS(u), getG(u));

		u.k.setFirst (val + heuristic(u, start) + k_m);
		u.k.setSecond(val);

		return u;
	}

    /*
	 * Checks if a point is in the hash table, if not it adds it in.
	 */
	private void makeNewPoint(Node u)
	{
		if (pointHash.get(u) != null) return;
		PointInfo tmp = new PointInfo();
		tmp.g = tmp.rhs = heuristic(u, goal);
		tmp.cost = C1;
		pointHash.put(u, tmp);
	}


    /*
	 * Sets the G value for node u
	 */
	private void setG(Node u, double g)
	{
		makeNewPoint(u);
		pointHash.get(u).g = g;
	}

	/*
	 * Sets the rhs (right hand side) value for node u
	 */
	private void setRHS(Node u, double rhs)
	{
		makeNewPoint(u);
		pointHash.get(u).rhs = rhs;
	}


	/*
	 * Returns the rhs value for node u.
	 */
	private double getRHS(Node u)
	{
		if (u == goal) return 0;

		//if the poitHash doesn't contain the node u
		if (pointHash.get(u) == null)
			return heuristic(u, goal);

		return pointHash.get(u).rhs;
	}


	/*
	 * Returns the g value for the node u.
	 */
	private double getG(Node u)
	{
		//if the PointHash doesn't contain the node u
		if (pointHash.get(u) == null)
			return heuristic(u, goal);
		return pointHash.get(u).g;
	}

    public float[] min(Node s){
        float[] temp = {10000, 10000};

        for(int i = 0; i < posList.size(); i++) {
            if (posList.get(i).x < temp[0]) {
                temp[0] = posList.get(i).x;              
            } else if(posList.get(i).x == temp[0] && posList.get(i).y < temp[1]) {
                temp = posList.get(i).getPos();
            }
        }

        return temp;

    }


	/**
	 * Create action list for mario
	 */

	public static boolean[] createAction(boolean left, boolean right, boolean down,
	boolean jump, boolean speed) {
		boolean[] action = new boolean[5];
		action[MarioActions.DOWN.getValue()] = down;
        action[MarioActions.JUMP.getValue()] = jump;
        action[MarioActions.LEFT.getValue()] = left;
        action[MarioActions.RIGHT.getValue()] = right;
        action[MarioActions.SPEED.getValue()] = speed;
		return action;
	}



    /*
	 * Returns a list of successor Nodes for Node u, since this is an
	 * 8-way graph this list contains all of a Points neighbours. Unless
	 * the Point is occupied, in which case it has no successors.
	 */
	private LinkedList<Node> getSucc(Node u)
	{
		LinkedList<Node> s = new LinkedList<Node>();
		Node tempNode;

		if (occupied(u)) return s;

		ArrayList<boolean[]> possibleActions = u.setPossibleActions(u);
		if (u.isLeafNode()) {
            possibleActions.clear();
        }

		//this.mario = u.sceneSnapshot.clone();
		u.sceneSnapshot = this.mario.clone();

		for (int i = 0; i < 5; i++) {
			boolean[] action = createAction(false, false, false, false, false);
			action[i] = true;
			System.out.println(u.sceneSnapshot.getMarioFloatPos()[0] + " " + u.sceneSnapshot.getMarioFloatPos()[1]);
			for (int j = 0; j<1; j++) {
				u.sceneSnapshot.advance(action);
			}
			System.out.println(u.sceneSnapshot.getMarioFloatPos()[0] + " " + u.sceneSnapshot.getMarioFloatPos()[1]);
			tempNode = new Node(u.sceneSnapshot.getMarioFloatPos()[0], u.sceneSnapshot.getMarioFloatPos()[1],
			new Pair<>(-1.0, -1.0));
			tempNode.setPosAction(action);
			s.addFirst(tempNode);
		}
		return s;
	}

    /*
	 * Returns a list of all the predecessor Nodes for Node u. Since
	 * this is for an 8-way connected graph, the list contains all the
	 * neighbours for Node u. Occupied neighbours are not added to the list
	 * 
	 * TODO: change this to mario actions
	 */
	private LinkedList<Node> getPred(Node u)
	{
		LinkedList<Node> s = new LinkedList<Node>();
		Node tempNode;

		//this.mario = u.sceneSnapshot.clone();
		u.sceneSnapshot = this.mario.clone();

		for (int i = 0; i < 5; i++) {
			boolean[] action = createAction(false, false, false, false, false);
			action[i] = true;
			this.mario.advance(action);
			tempNode = new Node(this.mario.getMarioFloatPos()[0], this.mario.getMarioFloatPos()[1],
			new Pair<>(-1.0, -1.0));
			tempNode.setPosAction(action);
			if (!occupied(tempNode)) s.addFirst(tempNode);
		}
		

		return s;
	}


    /*
	 * Returns the key hash code for the Node u, this is used to compare
	 * a Node that has been updated
	 */
	private float keyHashCode(Node u)
	{
		return (float)(u.k.first() + 1193*u.k.second());
	}

	/*
	 * Returns true if the Point is occupied (non-traversable), false
	 * otherwise. Non-traversable are marked with a cost < 0
     * TODO: Enduce with mario code
	 */
	private boolean occupied(Node u)
	{
		//if the PointHash does not contain the Node u
		if (pointHash.get(u) == null)
			return false;
		return (pointHash.get(u).cost < 0);
	}


    /*
	 * Euclidean cost between Node a and Node b
	 */
	private double trueDist(Node a, Node b)
	{
		float x = a.x-b.x;
		float y = a.y-b.y;
		return Math.sqrt(x*x + y*y);
	}

	/*
	 * Returns the cost of moving from Node a to Node b. This could be
	 * either the cost of moving off Node a or onto Node b, we went with the
	 * former. This is also the 8-way cost.
	 */
	private double cost(Node a, Node b)
	{
		float xd = Math.abs(a.x-b.x);
		float yd = Math.abs(a.y-b.y);
		double scale = 1;

		if (xd+yd > 1) scale = M_SQRT2;

		if (pointHash.containsKey(a)==false) return scale; 
		return scale*pointHash.get(a).cost;
	}


	/*
	 * As per [S. Koenig, 2002]
	 */
	private void updateVertex(Node u)
	{
		LinkedList<Node> s = new LinkedList<Node>();

		if (u.neq(goal)) {
			s = getSucc(u);
			double tmp = Double.POSITIVE_INFINITY;
			double tmp2;

			for (Node i : s) {
				tmp2 = getG(i) + cost(u,i);
				if (tmp2 < tmp) tmp = tmp2;
			}
			if (!close(getRHS(u),tmp)) setRHS(u,tmp);
		}

		if (!close(getG(u),getRHS(u))) insert(u);
	}

    public List<Node> getPath()
	{
		return path;
	}




    private void replan() {
        path.clear();
		int res = computeShortestPath();
		
		if (res < 0)
		{
			System.out.println("No Path to Goal");
			//return false;
		}
		
		LinkedList<Node> n = new LinkedList<Node>();
		Node cur = start;
		
		if (getG(start) == Double.POSITIVE_INFINITY)
		{
			System.out.println("No Path to Goal");
			//return false;
		}
		
		while (cur.neq(goal))
		{		
			path.add(cur);
			n = new LinkedList<Node>();
			n = getSucc(cur);
			
			if (n.isEmpty())
			{
				System.out.println("No Path to Goal");
				//return false;
			}
			
			double cmin = Double.POSITIVE_INFINITY;
			double tmin = 0;   
			Node smin = new Node();

			for (Node i : n)
			{
				double val  = cost(cur, i);
				double val2 = trueDist(i, goal) + trueDist(start, i);
				val += getG(i);

				if (close(val,cmin)) {
					if (tmin > val2) {
						tmin = val2;
						cmin = val;
						smin = i;
					}
				} else if (val < cmin) {
					tmin = val2;
					cmin = val;
					smin = i;
				}
			}
			n.clear();
			cur = new Node(smin);
			//cur = smin;			
		}	
		path.add(goal);
		//System.out.println(check++);
		//return true;
    }

    /*
	 * As per [S. Koenig,2002] except for two main modifications:
	 * 1. We stop planning after a number of steps, 'maxsteps' we do this
	 *    because this algorithm can plan forever if the start is surrounded  by obstacles
	 * 2. We lazily remove Nodes from the open list so we never have to iterate through it.
	 */
	private int computeShortestPath()
	{
		LinkedList<Node> s = new LinkedList<Node>();

		if (openList.isEmpty()) return 1;

		int k=0;
		while ((!openList.isEmpty()) &&
			   (openList.peek().lt(start = calculateKey(start))) ||
			   (getRHS(start) != getG(start))) {

			if (k++ > maxSteps) {
				System.out.println("At maxsteps");
				return -1;
			}

			Node u;

			boolean test = (getRHS(start) != getG(start));

			//lazy remove
			while(true) {
				if (openList.isEmpty()) return 1;
				u = openList.poll();

				if (!isValid(u)) continue;
				if (!(u.lt(start)) && (!test)) return 2;
				break;
			}

			openHash.remove(u);

			Node k_old = new Node(u);

			if (k_old.lt(calculateKey(u))) { //u is out of date
				insert(u);
			} else if (getG(u) > getRHS(u)) { //needs update (got better)
				setG(u,getRHS(u));
				s = getPred(u);
				for (Node i : s) {
					updateVertex(i);
				}
			} else {						 // g <= rhs, Node has got worse
				setG(u, Double.POSITIVE_INFINITY);
				s = getPred(u);

				for (Node i : s) {
					updateVertex(i);
				}
				updateVertex(u);
			}
		} //while
		return 0;
	}

    /*
	 * Update the position of the agent/robot.
	 * This does not force a replan.
	 */
	public void updateStart(float x, float y)
	{
		start.x = x;
		start.y = y;

		k_m += heuristic(last, start);

		start = calculateKey(start);
		last = start;

	}

	/*
	 * This is somewhat of a hack, to change the position of the goal we
	 * first save all of the non-empty nodes on the map, clear the map, move the
	 * goal and add re-add all of the non-empty Points. Since most of these Points
	 * are not between the start and goal this does not seem to hurt performance
	 * too much. Also, it frees up a good deal of memory we are probably not
	 * going to use.
	 */
	public void updateGoal(float x, float y)
	{
		List<Pair<ipoint2, Double> > toAdd = new ArrayList<Pair<ipoint2, Double> >();
		Pair<ipoint2, Double> tempPoint;

		for (Map.Entry<Node, PointInfo> entry : pointHash.entrySet()) {
			if (!close(entry.getValue().cost, C1)) {
				tempPoint = new Pair(
							new ipoint2(entry.getKey().x,entry.getKey().y),
							entry.getValue().cost);
				toAdd.add(tempPoint);
			}
		}

		pointHash.clear();
		openHash.clear();

		while(!openList.isEmpty())
			openList.poll();

		k_m = 0;

		goal.x = x;
		goal.y = y;

		PointInfo tmp = new PointInfo();
		tmp.g = tmp.rhs = 0;
		tmp.cost = C1;

		pointHash.put(goal, tmp);

		tmp = new PointInfo();
		tmp.g = tmp.rhs = heuristic(start, goal);
		tmp.cost = C1;
		pointHash.put(start, tmp);
		start = calculateKey(start);

		last = start;

		Iterator<Pair<ipoint2, Double> > iterator = toAdd.iterator();
		while(iterator.hasNext()) {
			tempPoint = iterator.next();
			updatePoint(tempPoint.first().x, tempPoint.first().y, tempPoint.second());
		}


	}


	/*
	 * Returns true if Node u is on the open list or not by checking if
	 * it is in the hash table.
	 */
	private boolean isValid(Node u)
	{
		if (openHash.get(u) == null) return false;
		if (!close(keyHashCode(u),openHash.get(u))) return false;
		return true;
	}

    /*
	 * updatePoint as per [S. Koenig, 2002]
	 */
	public void updatePoint(float x, float y, double val)
	{
		Node u = new Node();
		u.x = x;
		u.y = y;

		if ((u.eq(start)) || (u.eq(goal))) return;

		makeNewPoint(u);
		pointHash.get(u).cost = val;
		updateVertex(u);
	}

	/*
	 * Inserts Node u into openList and openHash
	 */
	private void insert(Node u)
	{
		//iterator cur
		float csum;

		u = calculateKey(u);
		//cur = openHash.find(u);
		csum = keyHashCode(u);

		// return if Point is already in list. TODO: this should be
		// uncommented except it introduces a bug, I suspect that there is a
		// bug somewhere else and having duplicates in the openList queue
		// hides the problem...
		//if ((cur != openHash.end()) && (close(csum,cur->second))) return;

		openHash.put(u, csum);
		openList.add(u);
	}

    /*
	 * Returns true if x and y are within 10E-5, false otherwise
	 */
	private boolean close(double x, double y)
	{
		if (x == Double.POSITIVE_INFINITY && y == Double.POSITIVE_INFINITY) return true;
		return (Math.abs(x-y) < 0.00001);
	}


	/*
	public float simulatePos() {
        this.sceneSnapshot = parentPos.sceneSnapshot.clone();
        for (int i = 0; i < repetitions; i++) {
            this.sceneSnapshot.advance(action);
        }
        int marioDamage = Helper.getMarioDamage(this.sceneSnapshot, this.parentPos.sceneSnapshot);
        remainingTime =
                calcRemainingTime(this.sceneSnapshot.getMarioFloatPos()[0], this.sceneSnapshot.getMarioFloatVelocity()[0]) +
                        marioDamage * (1000000 - 100 * distanceFromOrigin);
        if (isInVisitedList)
            remainingTime += Helper.visitedListPenalty;
        hasBeenHurt = marioDamage != 0;

        return remainingTime;
    }
	*/


	

	public boolean[] runMarioRun(DStarLiteScratch tree){
		boolean[] runPath = new boolean[5];
		  //Create pathfinder
		  //DStarLite pf = new DStarLite();
		  //set start and goal nodes

		/*
		tree.init(tree.mario.getMarioFloatPos()[0], tree.mario.getMarioFloatPos()[1], 
			tree.mario.getCurrentGoal(), tree.mario.getMarioFloatPos()[1]);
		
		tree.replan();
		*/
		runPath = createAction(false, true, false, false, false);
		MarioForwardModel temp = tree.mario.clone();
		System.out.println(temp.getMarioFloatPos()[0] + " " + temp.getMarioFloatPos()[1] 
		+ " " + temp.getCurrentGoal() + " " +  temp.getMarioFloatPos()[1]);
		
		return runPath;
	}

}


class PointInfo implements java.io.Serializable
{
	public double g=0;
	public double rhs=0;
	public double cost=0;
}

class ipoint2
{
	public float x=0;
	public float y=0;

	//default constructor
	public ipoint2()
	{

	}

	//overloaded constructor
	public ipoint2(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
}