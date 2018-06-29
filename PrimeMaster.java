import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

import java.util.ArrayList;


public class PrimeMaster extends UntypedActor{
    private Router ActiveRouter;
    private int WorkersNum;
    private int ResultsNum = 0;

    private ArrayList<Routee> routees = new ArrayList<Routee>();
    public PrimeMaster( int numwork ){
    	WorkersNum = numwork;
    	for (int i = 0; i < WorkersNum; i++) {
    	      ActorRef r = getContext().actorOf(Props.create(PrimeWorker.class));
    	      getContext().watch(r);
    	      routees.add(new ActorRefRoutee(r));
    	}
        ActiveRouter = new Router(new RoundRobinRoutingLogic(), routees);
    }
	public void Receiveing(Object message) throws Exception {
		// TODO Auto-generated method stub
		if(message instanceof int[]){
			int messages[] = (int[]) message;
			int start = messages[0];
			int end = messages[1];
			
			int numberOfNumbers = end - start;
			int segmentLength = numberOfNumbers / WorkersNum;
			
			for(int i = 0; i < WorkersNum; i++){
				int startNumber = start + (i* segmentLength);
				int endNumber = startNumber + segmentLength - 1;
				if( i == WorkersNum -1)
					endNumber = end;
				int send[] = { startNumber, endNumber};
				ActiveRouter.route(send, getSelf());
			}
		}
		else if( message instanceof ArrayList){
			ArrayList<Integer> result = (ArrayList<Integer>) message;
			for(int n : result)
				System.out.print(n + ", ");
			System.out.println();
			if( ++ResultsNum >= WorkersNum){
				getContext().stop(getSelf());
				getContext().system().shutdown();
			}
		}
		else
			unhandled(message);
	}
}