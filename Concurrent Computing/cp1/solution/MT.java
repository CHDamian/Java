/** @file
 * TransactionManager class
 *
 * @author Damian Chańko <dc394127@students.mimuw.edu.pl>
 * @copyright Uniwersytet Warszawski
 * @date 17.12.2020
 */


package cp1.solution;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import cp1.base.ResourceId;
import cp1.base.ResourceOperation;
import cp1.base.ResourceOperationException;
import cp1.base.TransactionManager;
import cp1.base.UnknownResourceIdException;
import cp1.base.ActiveTransactionAborted;
import cp1.base.NoActiveTransactionException;
import cp1.base.AnotherTransactionActiveException;
import cp1.base.LocalTimeProvider;
import cp1.base.Resource;

public class MT implements TransactionManager {

    // Map of resources and operations made in single transaction
    private ConcurrentHashMap< ResourceId, Pair<Resource, Stack<ResourceOperation>> > resources;

    // Map of mutexes for resources, that lock threads while waiting for access
    private ConcurrentHashMap< ResourceId, Semaphore> mutexes;

    // Map of active transactions (Pair contains abort flag and list of operated resources)
    private ConcurrentHashMap< Thread, Pair<Boolean, ArrayList<ResourceId>> > activeTrans;

    // Map contains starting time of every transaction
    private ConcurrentHashMap< Thread, Long> timer;


    // Graph of cycles
    private ResourceGraph graph;

    // For setting start time for transactions
    private LocalTimeProvider timeProvider;


    // Constructor
    public MT(Collection<Resource> resources,
            LocalTimeProvider timeProvider)
    {
        this.timeProvider = timeProvider;
        this.graph = new ResourceGraph(resources);
        this.resources = new ConcurrentHashMap< ResourceId, Pair<Resource, Stack<ResourceOperation>> >();
        this.mutexes = new ConcurrentHashMap< ResourceId, Semaphore>();
        this.activeTrans = new ConcurrentHashMap< Thread, Pair<Boolean, ArrayList<ResourceId>> >();
        this.timer = new ConcurrentHashMap< Thread, Long>();


        // Creating database
        Iterator<Resource> it = resources.iterator();
        while(it.hasNext())
        {
            Resource res = it.next();
            this.resources.put(res.getId(),
                    new Pair<Resource, Stack<ResourceOperation>>(res, new Stack<ResourceOperation>()));
            this.mutexes.put(res.getId(), new Semaphore(1,true));
        }
    }

    // Starts transaction
    public void startTransaction() throws AnotherTransactionActiveException
    {
        Thread t = Thread.currentThread();
        if (activeTrans.containsKey(t)) // If another active
        {
            throw new AnotherTransactionActiveException();
        }

        // Set info into database
        timer.put(t, timeProvider.getTime());
        activeTrans.put(t, new Pair<Boolean, ArrayList<ResourceId>>
                (false, new ArrayList<ResourceId>()));
    }


    // Executes given operation
    private void executer(
            ResourceId rid,
            ResourceOperation operation) throws
            ResourceOperationException
    {
            operation.execute(resources.get(rid).getKey());
            resources.get(rid).getValue().push(operation);
    }


    // Abort one transaction preventing cycles in graph
    private void abortTrans(ArrayList<Thread> list) throws  InterruptedException
    {
        Iterator<Thread> it = list.iterator(); // List of candidates
        Thread toAbort = it.next(); // Candidate
        Long time = timer.get(toAbort); // His time

        while (it.hasNext()) // Compares with another candidates
        {
            Thread candicate = it.next();
            Long candTime = timer.get(candicate);
            if(time < candTime) // By time
            {
                toAbort = candicate;
                time = candTime;
            }
            else if(time == candTime && toAbort.getId() < candicate.getId()) // By id
            {
                toAbort = candicate;
                time = candTime;
            }
        }

        activeTrans.get(toAbort).setKey(true); // Set abort flag
        graph.clearEdges(activeTrans.get(toAbort).getValue(), false); // Delete all edges in graph
        toAbort.interrupt();
    }

    // Main function for execute operation
    public void operateOnResourceInCurrentTransaction(
            ResourceId rid,
            ResourceOperation operation) throws
            NoActiveTransactionException,
            UnknownResourceIdException,
            ActiveTransactionAborted,
            ResourceOperationException,
            InterruptedException
    {
        Thread t = Thread.currentThread();

        // Check if everything ok
        if(!isTransactionActive()) {
            throw new NoActiveTransactionException();
        }

        if(!resources.containsKey(rid)) {
            throw new UnknownResourceIdException(rid);
        }

        if(isTransactionAborted()) {
            throw new ActiveTransactionAborted();
        }


        if(activeTrans.get(t).getValue().contains(rid)) { // If thread has this resource
            executer(rid, operation);
        }
        else if(mutexes.get(rid).tryAcquire()) { // If thread can take it now
            activeTrans.get(t).getValue().add(rid);
            executer(rid, operation);
        }
        else {
            ArrayList<Thread> list = graph.findCycles( //search for cycles in graph
                    activeTrans.get(t).getValue(), rid);
            if(!list.isEmpty()) { // If cycle found
                abortTrans(list);
            }
            else graph.unlockGraph(); // else unlock graph for someone else
            mutexes.get(rid).acquire(); // Wait for resource
            graph.clearEdges(activeTrans.get(t).getValue(), true); // We do not need those edges anymore
            activeTrans.get(t).getValue().add(rid); // Add to his resources
            executer(rid, operation); //execute safely
        }

    }

    // Commit changes
    public void commitCurrentTransaction() throws
            NoActiveTransactionException,
            ActiveTransactionAborted
    {
        // Check if everything ok
        if(!isTransactionActive())
        {
            throw new NoActiveTransactionException();
        }

        if(isTransactionAborted())
        {
            throw new ActiveTransactionAborted();
        }

        // Clean before giving everything back
        Thread t = Thread.currentThread();
        Iterator<ResourceId> it = activeTrans.get(t).getValue().iterator();

        while (it.hasNext())
        {
            ResourceId id = it.next();
            resources.get(id).getValue().clear();
            mutexes.get(id).release();
        }

        // End transaction
        timer.remove(t);
        activeTrans.remove(t);

        return;
    }

    // Return resource to state before transaction
    private void cleanResource(ResourceId ID)
    {
        Resource R = resources.get(ID).getKey();
        Stack<ResourceOperation> Operations =  resources.get(ID).getValue();

        while (!Operations.empty())
        {
            ResourceOperation Op = Operations.pop();
            Op.undo(R);
        }

        return;
    }

    public void rollbackCurrentTransaction()
    {
        // Check if everything ok
        Thread t = Thread.currentThread();
        if(!activeTrans.containsKey(t)) return;

        // Clean before giving everything back
        Iterator<ResourceId> it = activeTrans.get(t).getValue().iterator();
        while (it.hasNext())
        {
            ResourceId id = it.next();
            cleanResource(id);
            resources.get(id).getValue().clear();
            mutexes.get(id).release();
        }

        // End transaction
        timer.remove(t);
        activeTrans.remove(t);

        return;
    }

    public boolean isTransactionActive()
    {
        Thread t = Thread.currentThread();
        return activeTrans.containsKey(t);
    }

    public boolean isTransactionAborted()
    {
        Thread t = Thread.currentThread();
        if(!activeTrans.containsKey(t)) return false;
        return activeTrans.get(t).getKey();
    }

}