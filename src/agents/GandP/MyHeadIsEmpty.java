package agents.GandP;

import java.util.List;
import java.util.PriorityQueue;



public class MyHeadIsEmpty {
    private List<SearchNode> posSuccNode;
    private double k_m;
    private PriorityQueue<SearchNode> priorityQueue;
    private SearchNode s_goal;
    private SearchNode s_start;
    //rhs and g

    public void updateVertex(SearchNode u) {
        if (u != s_goal){
            // rhs(u) = 
        }

        if (priorityQueue.contains(u)) {
            priorityQueue.remove(u);
        }

        if (u!=null) {
            priorityQueue.add(u);           
        }
     }


     private void computeShortestPath() {

     }


    public static void main(String[] args) {
        
    }


    
}
