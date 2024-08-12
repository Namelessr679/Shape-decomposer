/**
 * Name: Robel Solomon Gizaw
 * Date: 4/30/2024
 * CSC 202
 * Project 3-DecomposableShape.java
 * 
 * This class stores a list x/y coordinates that, when connected end 
 * to end form a shape. The least important nodes can sequentially
 * be removed from the shape and restored to the list of x/y
 * coordinates. 
 * 
 * Citations of Assistance (who and what OR declare no assistance):
 * I recieved help from Oshan on structuring my code so that it would fit style standards
 * I recieved help from Professor Mueller in figuring out the problem with the method to find lowest importance
 * 
 * 
 */


import java.util.Scanner;
import java.awt.Polygon;


public class DecomposableShape {

    private int numPointsInitial;
    private int numPointsCurrent;
    private PointNode front;
    private StackADT<PointNode> removed;

    /**
     * Constructs a DecomposableShape from a sequence of points read from an input scanner.
     * Each point contributes to a circular doubly linked list of PointNodes.
     * 
     * @param input Scanner from which the point data is read.
     */
    public DecomposableShape(Scanner input){
        initializeVariables();
        PointNode finalNode = null;

        if(input.hasNextLine()){
            PointNode newNode = createNewNode(input);
            this.front = newNode;
            finalNode = newNode;
            this.numPointsCurrent++;
            this.numPointsInitial++;
            
        }

        while(input.hasNextLine()){
            PointNode newNode = createNewNode(input);
            finalNode = addNewNodes(newNode, finalNode);
            
        }

        finalNode.next = this.front;
        this.front.prev = finalNode;
        
        calculateNewImportance();
    }

    /**
     * Initializes the class fields to their default values.
     */
    private void initializeVariables(){
        this.numPointsInitial = 0;
        this.numPointsCurrent = 0;
        this.front = null;
        this.removed = new ArrayStack<>();

    }
    /**
     * Creates a new PointNode from a line of text read from the input.
     *
     * @param input Scanner from which the line is read.
     * @return A new PointNode initialized with coordinates parsed from the input.
     */
    private PointNode createNewNode(Scanner input){
        String line = input.nextLine();
        String[] point = line.split(",");
        int x = Integer.parseInt(point[0]);
        int y = Integer.parseInt(point[1]);
        PointNode newNode = new PointNode(x, y);

        return newNode;
    }

    /**
     * Adds a newly created PointNode to the circular doubly linked list of points.
     *
     * @param newNode The new PointNode to be added to the list.
     * @param finalNode The last PointNode in the list before adding the new node.
     * @return The newly added node, now serving as the last node in the list.
     */
    private PointNode addNewNodes(PointNode newNode, PointNode finalNode){

        finalNode.next = newNode;
        newNode.prev = finalNode;
        finalNode = newNode;

        this.numPointsInitial++;
        this.numPointsCurrent++;

        return finalNode;
    }

    /**
     * Calculates the importance of each node in the circular doubly linked list.
     * The importance metric is used to determine which nodes to remove or keep during operations.
     */
    private void calculateNewImportance(){
        PointNode current = front;
            
        do{
            current.calculateImportance();
            current = current.next;
        }
        while(current != front);
    }

    /**
     * Converts the shape into a polygon based on the point nodes currently in the list.
     * 
     * @return A Polygon object representing the current shape.
     */
    public Polygon toPolygon(){
        int[] xpoints = new int[this.numPointsInitial];
        int[] ypoints = new int[this.numPointsInitial];

        PointNode current = front;
        
        for(int i = 0;i < this.numPointsCurrent; i++){
            xpoints[i] = current.x;
            ypoints[i] = current.y;

            current = current.next;
            
        }

        Polygon shape = new Polygon(xpoints, ypoints, this.numPointsCurrent);
        return shape;
    }


    /**
     * Finds the node with the least importance in the list.
     *
     * @return The node with the least importance.
     */
    private PointNode findLeastImportance(){
        PointNode least = this.front;
        PointNode current = this.front;

        do {
            if(current.importance < least.importance){
                least = current;
            }
            current = current.next;
        } while (current != this.front);
        

        return least;
    
    }

    /**
     * Removes a node from the doubly linked list and updates adjacent nodes' importance.
     *
     * @param leastImportantNode The node to be removed.
     */    
    private void removeNode(PointNode leastImportantNode){ 
        if(leastImportantNode == front) {
            this.front = this.front.next;
        }

        leastImportantNode.prev.next = leastImportantNode.next;
        leastImportantNode.next.prev = leastImportantNode.prev;
        leastImportantNode.prev.calculateImportance();
        leastImportantNode.next.calculateImportance();
        this.numPointsCurrent--;

        
        removed.push(leastImportantNode);
    }

    /**
     * Re-adds a previously removed node to the doubly linked list and updates adjacent nodes' importance.
     */

    private void addNode(){

        PointNode newNode = removed.pop();
        newNode.prev.next = newNode;
        newNode.next.prev = newNode; 
        newNode.next.calculateImportance();
        newNode.prev.calculateImportance();
        this.numPointsCurrent++;

        
    }

    /**
     * Adjusts the size of the decomposable shape to a target percentage of its initial size,
     * either by removing least important nodes or by re-adding previously removed nodes.
     *
     * @param target The percentage of the initial number of points that the current size should reach.
     */
    
    public void setToSize(int target) {
        int targetPoints = (int) Math.floor((target / 100.0) * this.numPointsInitial);
        if(this.numPointsCurrent > targetPoints){
            while (this.numPointsCurrent > targetPoints) {
                PointNode leastImportant = findLeastImportance();
                removeNode(leastImportant);
            }
        }else if(this.numPointsCurrent < targetPoints){
            while (this.numPointsCurrent < targetPoints) {
                addNode();
            }
        }
    }
    
    /**
 * Returns a string representation of the circular doubly linked list of points,
 * showing each point's details in a formatted string.
 * 
 * @return A formatted string representing each point in the shape.
 */
    public String toString() {    
        PointNode current = front;
        String finalString = String.format("%s", current.toString());
        while (current.next != front) {
            current = current.next;
            finalString += String.format("\n%s", current.toString());
        }
        return finalString;
    }
    


    private static class PointNode{
        private int x;
        private int y;
        private double importance;
        private PointNode prev;
        private PointNode next;

        /**
     * Constructs a PointNode with specified coordinates.
     * The importance is initialized to 0.0, and links are initially null.
     * 
     * @param x The x-coordinate of this point.
     * @param y The y-coordinate of this point.
     */
        public PointNode(int x, int y){
            this.x = x;
            this.y = y;
            this.importance = 0.0;
            this.prev = null;
            this.next = null;
        }
        /**
     * Calculates the Euclidean distance between two points.
     * 
     * @param point1 The first point.
     * @param point2 The second point.
     * @return The distance between the two points.
     */

        private double distanceBetween(PointNode point1, PointNode point2){
            return Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2));
        }

        /**
     * Calculates and updates the importance of this point based on its position relative
     * to its neighboring points in the list. The importance is defined as the sum of the distances
     * from this point to its previous and next points minus the distance directly between those neighbors.
     */
        public void calculateImportance(){
            double lp = distanceBetween(prev, this);
            double pr = distanceBetween(this, next);
            double lr = distanceBetween(prev, next);

            importance = lp + pr - lr;

        }
        /**
     * Returns a string representation of the point, including its coordinates and importance.
     * 
     * @return A formatted string that includes x, y coordinates, and importance of the point.
     */

        public String toString(){
            return String.format("x = %d, y = %d, importance = " + importance, x, y);
        }


    }


}

