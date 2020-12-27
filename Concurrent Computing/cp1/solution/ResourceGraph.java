/** @file
 * Resource graph for finding cycles
 *
 * @author Damian Chańko <dc394127@students.mimuw.edu.pl>
 * @copyright Uniwersytet Warszawski
 * @date 17.12.2020
 */


package cp1.solution;

import cp1.base.Resource;
import cp1.base.ResourceId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class ResourceGraph {

    // Map of nodes and where edges are going to (if key == value there are no edges from)
    private ConcurrentHashMap<ResourceId,ResourceId> nodes;

    // Map of visited nodes for DFS
    private ConcurrentHashMap<ResourceId,Boolean> odw;

    // Map of resource holders
    private ConcurrentHashMap<ResourceId,Thread> users;

    // For collecting DFS result
    private ArrayList<Thread> collector;

    // For gaining access to graph
    private Semaphore mutex;


    // Constructor
    public ResourceGraph(Collection<Resource> resources)
    {
        this.nodes = new ConcurrentHashMap<ResourceId,ResourceId>();
        this.odw = new ConcurrentHashMap<ResourceId,Boolean>();
        this.users = new ConcurrentHashMap<ResourceId,Thread>();
        this.collector = new ArrayList<Thread>();
        this.mutex = new Semaphore(1,true);

        Iterator<Resource> it = resources.iterator();
        while (it.hasNext()) // Make nodes
        {
            ResourceId id = it.next().getId();
            nodes.put(id, id);
            odw.put(id, false);
        }
    }

    // Set visited to false
    private void clearOdw()
    {
        Iterator<ResourceId> it = odw.keySet().iterator();
        while (it.hasNext())
        {
            odw.put(it.next(), false);
        }
        return;
    }

    // Recirsive DFS ends when he finds cycle or confirms there are none
    private Boolean DFS(ResourceId ID)
    {
        odw.put(ID, true);
        ResourceId next = nodes.get(ID);

        if(ID == next) return false;

        if(odw.get(next))
        {
            collector.add(users.get(next));
            return true;
        }
        else return DFS(next);
    }

    // Try to lock graph for himself
    public void lockGraph() throws InterruptedException
    {
        mutex.acquire();
    }

    // Unlocks graph
    public void unlockGraph() throws InterruptedException
    {
        mutex.release();
    }

    // Find cycles in graph preventing deadlock
    public ArrayList<Thread> findCycles(ArrayList<ResourceId> resources, ResourceId blocked)
            throws InterruptedException
    {
        lockGraph(); // Lock
        clearOdw(); // All not visited
        collector.clear(); // New out

        Iterator<ResourceId> it = resources.iterator();
        while (it.hasNext()) // Make new edges
        {
            ResourceId id = it.next();
            nodes.put(id, blocked);
            odw.put(id, true);
            users.put(id, Thread.currentThread());
        }

        if(DFS(blocked))collector.add(users.get(blocked)); // Find cycle

        ArrayList<Thread> result = new ArrayList<Thread>(collector); //Result of this function
        return result; // No unlock, cause thread is still operating on graph data
    }

    // Removes edges that are going from all nodes from list, flag asks if lock mutex at the start
    public void clearEdges(ArrayList<ResourceId> resources, boolean czyLockowac)
            throws InterruptedException
    {
        if(czyLockowac)lockGraph();

        Iterator<ResourceId> it = resources.iterator();
        while (it.hasNext())
        {
            ResourceId ID = it.next();
            nodes.put(ID, ID);
            users.remove(ID);
        }

        unlockGraph();
        return;
    }

}
